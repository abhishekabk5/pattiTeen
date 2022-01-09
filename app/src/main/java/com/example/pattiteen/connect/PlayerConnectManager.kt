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
import com.example.pattiteen.util.Utils

@SuppressLint("MissingPermission")
class PlayerConnectManager(
    private val manager: WifiP2pManager,
    private val channel: WifiP2pManager.Channel
) : BroadcastReceiver() {

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
                val networkInfo = intent
                    .getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO) as? NetworkInfo
                if (networkInfo?.isConnected == true) {
                    manager.requestConnectionInfo(channel, connectionListener)
                }
                Utils.showToast("Network Info: Connected - ${networkInfo?.isConnected} ")
            }
            WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION -> {
//                (activity.supportFragmentManager.findFragmentById(R.id.frag_list) as DeviceListFragment)
//                    .apply {
//                        updateThisDevice(
//                            intent.getParcelableExtra(
//                                WifiP2pManager.EXTRA_WIFI_P2P_DEVICE) as WifiP2pDevice
//                        )
//                    }
            }
        }
    }

    fun init(serverHandler: ServerHandler, clientHandler: ClientHandler) {
        this.serverHandler = serverHandler
        this.clientHandler = clientHandler
    }

    fun callDiscover() {
        manager.discoverPeers(channel, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                Utils.showToast("Discover Success")
            }

            override fun onFailure(reasonCode: Int) {
                Utils.showToast("Discover Failed: $reasonCode")
            }
        })
    }

    private val peers = mutableListOf<WifiP2pDevice>()
    var peersUpdateListener: PeersUpdateListener? = null
    private val peerListListener = WifiP2pManager.PeerListListener { peerList ->
        val refreshedPeers = peerList.deviceList
        if (refreshedPeers != peers) {
            peers.clear()
            peers.addAll(refreshedPeers)

            // If an AdapterView is backed by this data, notify it
            // of the change. For instance, if you have a ListView of
            // available peers, trigger an update.
            peersUpdateListener?.onPeersUpdate(peers)

            // Perform any other updates needed based on the new list of
            // peers connected to the Wi-Fi P2P network.
        }

        Utils.showToast("${peers.size} devices found")
    }

    private val connectionListener = WifiP2pManager.ConnectionInfoListener { info ->

        if (!info.groupFormed) return@ConnectionInfoListener

        // String from WifiP2pInfo struct
        val groupOwnerAddress = info.groupOwnerAddress?.hostAddress

        if (info.isGroupOwner) {
            ServerConnectionThread(peers.size, serverHandler).start()
        } else {
            Utils.showToast("Connecting to server...")
            ClientConnectionThread(Utils.getUserName(), groupOwnerAddress, clientHandler).start()
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
                    Utils.showToast("Connect to ${config.deviceAddress} success")
                }

                override fun onFailure(reasonCode: Int) {
                    Utils.showToast("Connect to ${config.deviceAddress} failed: $reasonCode")
                }
            })
        }
    }
}

interface PeersUpdateListener {
    fun onPeersUpdate(peers: List<WifiP2pDevice>)
}