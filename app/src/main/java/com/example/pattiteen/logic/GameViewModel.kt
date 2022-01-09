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
import com.example.pattiteen.model.CardsState


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

    private var state = CardsState()

    private var chaal = 2
    private var addedToPot = 0
    private var potTotal = 0

    val cardsSeen = MutableLiveData(false)
    val chaalAmount = MutableLiveData(0)
    val potAmount = MutableLiveData(0)

    fun onCardsSeen() {
        state.seen = true
        cardsSeen.value = state.seen
    }

    fun onChaalClick() {
        val chips = if (state.seen) chaal else chaal / 2
        playChips(chips)
    }

    fun onDoubleClick() {
        chaal *= 2
        val chips = if (state.seen) chaal else chaal / 2
        playChips(chips)
        chaalAmount.value = chaal
    }

    private fun playChips(chips: Int) {
        addedToPot += chips
        // send chaal signal
        potTotal += chips
        potAmount.value = potTotal
    }

    fun onPackClick() {
        // send pack signal
        state.packed = true
    }
}