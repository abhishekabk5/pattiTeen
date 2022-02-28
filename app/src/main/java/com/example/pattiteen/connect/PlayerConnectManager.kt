package com.example.pattiteen.connect

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.NetworkInfo
import android.net.wifi.WpsInfo
import android.net.wifi.p2p.WifiP2pConfig
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pManager
import com.example.pattiteen.connect.client.ClientConnectionThread
import com.example.pattiteen.connect.client.ClientHandler
import com.example.pattiteen.connect.server.ServerConnectionThread
import com.example.pattiteen.connect.server.ServerHandler
import com.example.pattiteen.model.PlayerInfo
import com.example.pattiteen.util.Logr
import com.example.pattiteen.util.Utils
import kotlinx.coroutines.flow.flow

@SuppressLint("MissingPermission")
class PlayerConnectManager(
    private val manager: WifiP2pManager,
    private val channel: WifiP2pManager.Channel
) : BroadcastReceiver() {

    private lateinit var playerInfo: PlayerInfo

    private lateinit var serverHandler: ServerHandler
    private lateinit var clientHandler: ClientHandler

    override fun onReceive(context: Context, intent: Intent) {
        when(intent.action) {
            WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION -> {
                // Determine if Wifi P2P mode is enabled or not, alert
                // the Activity.
//                val state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1)
//                activity.isWifiP2pEnabled = state == WifiP2pManager.WIFI_P2P_STATE_ENABLED
            }
            WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION -> {
                manager.requestPeers(channel, peerListListener)
            }
            WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION -> {
                val networkInfo: NetworkInfo? = intent
                    .getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO)
                if (networkInfo?.isConnected == true) {
                    manager.requestConnectionInfo(channel, connectionListener)
                }
                Logr.i("Network Info: Connected - ${networkInfo?.isConnected} ")
            }
            WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION -> {
                val thisDevice: WifiP2pDevice? = intent
                    .getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE)
                thisDevice?.deviceName?.let { Utils.setUserName(it, false) }
            }
        }
    }

    fun init(playerInfo: PlayerInfo, serverHandler: ServerHandler, clientHandler: ClientHandler) {
        this.playerInfo = playerInfo
        this.serverHandler = serverHandler
        this.clientHandler = clientHandler
    }

    fun callDiscover() {
        manager.discoverPeers(channel, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                Logr.i("Discover Success")
            }

            override fun onFailure(reasonCode: Int) {
                Logr.i("Discover Failed: $reasonCode")
            }
        })
    }

    private val peers = mutableListOf<WifiP2pDevice>()
    val peersList = flow<List<WifiP2pDevice>> {
        emit(peers)
    }
    private val peerListListener = WifiP2pManager.PeerListListener { peerList ->
        val refreshedPeers = peerList.deviceList
        if (refreshedPeers != peers) {
            peers.clear()
            peers.addAll(refreshedPeers)

            // If an AdapterView is backed by this data, notify it
            // of the change. For instance, if you have a ListView of
            // available peers, trigger an update.

            // Perform any other updates needed based on the new list of
            // peers connected to the Wi-Fi P2P network.
        }

        Logr.i("${peers.size} devices found")
    }

    private val connectionListener = WifiP2pManager.ConnectionInfoListener { info ->

        if (!info.groupFormed) return@ConnectionInfoListener

        // String from WifiP2pInfo struct
        val groupOwnerAddress = info.groupOwnerAddress?.hostAddress

        if (info.isGroupOwner) {
            ServerConnectionThread(peers.size, serverHandler).start()
        } else {
            Logr.i("Connecting to server...")
            ClientConnectionThread(playerInfo, groupOwnerAddress, clientHandler).start()
        }
    }

    fun checkForPeers() {
        manager.requestPeers(channel, peerListListener)
    }

    fun connectToPeers() {
        val configs = peers.map {
            WifiP2pConfig().apply {
                deviceAddress = it.deviceAddress
                wps.setup = WpsInfo.PBC
            }
        }
        for (config in configs) {
            manager.connect(channel, config, object: WifiP2pManager.ActionListener {
                override fun onSuccess() {
                    Logr.i("Connect to ${config.deviceAddress} success")
                }

                override fun onFailure(reasonCode: Int) {
                    Logr.i("Connect to ${config.deviceAddress} failed: $reasonCode")
                }
            })
        }
    }
}