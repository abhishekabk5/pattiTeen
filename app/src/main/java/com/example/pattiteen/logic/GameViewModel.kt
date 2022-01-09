package com.example.pattiteen.logic

import android.net.wifi.p2p.WifiP2pDevice
import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.pattiteen.connect.EventHandler
import com.example.pattiteen.connect.PeersUpdateListener
import com.example.pattiteen.connect.PlayerConnectManager
import com.example.pattiteen.connect.client.ClientHandler
import com.example.pattiteen.connect.server.ServerHandler
import com.example.pattiteen.model.*
import com.example.pattiteen.util.Utils


class GameViewModelFactory(private val repo: PlayerConnectManager): ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return GameViewModel(repo) as T
    }
}

class GameViewModel(
    private val manager: PlayerConnectManager
) : ViewModel() {

    private val serverHandler = ServerHandler(object: EventHandler {
        override fun handleMessage(message: Bundle) {

        }
    })

    private val clientHandler = ClientHandler(object: EventHandler {
        override fun handleMessage(message: Bundle) {

        }
    })

    val peersCount = MutableLiveData(0)
    private val peerUpdate = object: PeersUpdateListener {
        override fun onPeersUpdate(peers: List<WifiP2pDevice>) {
            peersCount.value = peers.size
        }
    }

    init {
        manager.init(serverHandler, clientHandler)
        manager.peersUpdateListener = peerUpdate
    }

    fun startGame() {
        manager.connectToPeers()
    }

    fun onPeerCountClick() {
        manager.checkForPeers()
    }

    private var game: GameState
    private var playerInfo = PlayerInfo(Utils.getUserName())
    private var state = PlayerState()

    init {
        game = GameState(userOrderList = arrayListOf(state))
    }

    val isMyTurn = MutableLiveData(true)
    val cardsSeen = MutableLiveData(false)
    val chaalAmount = MutableLiveData(0)
    val potAmount = MutableLiveData(0)

    private fun handleGameState(gameState: GameState) {
        chaalAmount.value = gameState.chaal
        potAmount.value = gameState.potTotal
        gameState.userOrderList.getOrNull(playerInfo.orderIndex)?.let { playerState ->
            state = playerState
            if (playerState.cardsState == CardsState.SEEN) cardsSeen.value = true
        }
        isMyTurn.value = playerInfo.orderIndex == gameState.currPlayer
    }

    fun onCardsSeen() {
        if (state.cardsState != CardsState.BLIND) return
        state.cardsState = CardsState.SEEN
        if (state.cardsState == CardsState.SEEN) cardsSeen.value = true
    }

    fun onChaalClick() {
        playTurn(PlayerActionType.CHAAL, false)
    }

    fun onDoubleClick() {
        playTurn(PlayerActionType.CHAAL, true)
    }

    fun onPackClick() {
        playTurn(PlayerActionType.PACK)
    }

    private fun playTurn(type: PlayerActionType, double: Boolean = false) {
        val action = PlayerAction(type, double)
//        manager.sendEvent(TurnActionDto(state, action))
    }
}