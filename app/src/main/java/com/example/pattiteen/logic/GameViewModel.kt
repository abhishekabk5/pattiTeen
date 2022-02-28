package com.example.pattiteen.logic

import android.os.Bundle
import androidx.lifecycle.*
import com.example.pattiteen.connect.EventHandler
import com.example.pattiteen.connect.PlayerConnectManager
import com.example.pattiteen.connect.client.ClientHandler
import com.example.pattiteen.connect.server.ServerHandler
import com.example.pattiteen.model.*
import com.example.pattiteen.util.Constants
import com.example.pattiteen.util.Utils
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect

@Suppress("UNCHECKED_CAST")
class GameViewModelFactory(private val repo: PlayerConnectManager): ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return GameViewModel(repo) as T
    }
}

class GameViewModel(
    private val manager: PlayerConnectManager
) : ViewModel() {

    private var playerInfo = PlayerInfo(Utils.getUserName())

    private val serverHandler = ServerHandler(object: EventHandler {
        override fun handleMessage(message: Bundle) {
            when(message.getInt(Constants.ACTION_KEY, Constants.ACTION_INVALID)) {
                Constants.PLAYER_LIST_UPDATE ->
                    message.getParcelable<PlayerInfo>(Constants.KEY_PLAYER_INFO)
                        ?.let { handleNewPlayer(it) }
                Constants.GAME_START ->
                    message.getInt(Constants.KEY_PLAYER_COUNT, -1).takeIf { it != -1 }
                        ?.let { tryGameStart(it) }
            }
            message.getParcelable<GameState>(Constants.KEY_GAME_STATE)
                ?.let { handleGameState(it) }
            message.getParcelable<TurnActionDto>(Constants.KEY_PLAYER_ACTION)
                ?.let { handlePlayerAction(it) }
        }
    })

    private val clientHandler = ClientHandler(object: EventHandler {
        override fun handleMessage(message: Bundle) {
            message.getParcelable<GameState>(Constants.KEY_GAME_STATE)
                ?.let { handleGameState(it) }
            message.getParcelableArrayList<PlayerInfo>(Constants.KEY_PLAYER_LIST)
                ?.let { handlePlayerList(it) }
        }
    })

    init {
        manager.init(playerInfo, serverHandler, clientHandler)
    }

    val peersCount = liveData {
        while(true) {
            manager.peersList.collect { emit(it.size) }
            delay(500L)
        }
    }

    fun onPeerCountClick() {
        manager.checkForPeers()
    }

    fun startGame() {
        manager.connectToPeers()
    }

    private var playerCount = -1
    private fun tryGameStart(playerCount: Int) {
        this.playerCount = playerCount
        if (playerList.size == playerCount)
            startGameInternal()
    }

    private fun startGameInternal() {
        if (game.currPlayer != -1) return // game already started
        playerList.forEachIndexed { i, info -> info.orderIndex = i }
        game.currPlayer = 0
        game.userOrderList.addAll(playerList.map { PlayerState() })
        updateGameState(game)
    }

    private fun updateGameState(game: GameState) {
        serverHandler.sendToAll(game)
        handleGameState(game)
    }

    private fun handleNewPlayer(newPlayer: PlayerInfo) {
        playerList.add(newPlayer)
        serverHandler.sendToAll(playerList)
        tryGameStart(playerCount)
    }

    private fun handlePlayerList(list: List<PlayerInfo>) {
        playerList.apply { clear(); addAll(list) }
        playerInfo.orderIndex = list.indexOfFirst { it.id == playerInfo.id }
    }

    private fun handlePlayerAction(turnActionDto: TurnActionDto) {
        game.userOrderList.getOrNull(turnActionDto.orderIndex)?.apply {
            this.cardsState = turnActionDto.state.cardsState
            if (turnActionDto.action.type == PlayerActionType.PACK) return@apply
            if (turnActionDto.action.double) game.chaal *= 2
            this.potAmount += game.chaal
            game.potTotal += game.chaal
        }
        do {
            game.currPlayer++
            game.currPlayer = game.currPlayer % game.userOrderList.size
            if (game.currPlayer == turnActionDto.orderIndex) {
                // todo: end game
                break
            }
        } while(game.userOrderList.getOrNull(game.currPlayer)?.cardsState == CardsState.PACKED)
        updateGameState(game)
    }

    //                                ----- ^Game Handle Logic^ -----

    private var playerList: ArrayList<PlayerInfo>
    private var game: GameState
    private var state = PlayerState()

    init {
        playerList = arrayListOf(playerInfo)
        game = GameState()
    }

    //                              ------ \/View State Logic\/ ------

    val isMyTurn = MutableLiveData(true)
    val cardsSeen = MutableLiveData(false)
    val chaalAmount = MutableLiveData(0)
    val potAmount = MutableLiveData(0)

    private fun handleGameState(gameState: GameState) {
        game = gameState
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
        playTurn(PlayerActionType.CHAAL)
    }

    fun onDoubleClick() {
        playTurn(PlayerActionType.CHAAL, true)
    }

    fun onPackClick() {
        playTurn(PlayerActionType.PACK)
    }

    private fun playTurn(type: PlayerActionType, double: Boolean = false) {
        val action = TurnActionDto(playerInfo.orderIndex, state, PlayerAction(type, double))
        val handled = clientHandler.sendToServer(action)
        if (!handled) handlePlayerAction(action)
    }
}