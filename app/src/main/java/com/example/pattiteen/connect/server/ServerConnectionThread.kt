package com.example.pattiteen.connect.server

import android.os.Handler
import android.os.Looper
import java.io.IOException
import java.net.ServerSocket
import java.net.Socket
import android.os.Bundle
import android.os.Message
import com.example.pattiteen.connect.EventHandler
import com.example.pattiteen.connect.server.ServerConnectionThread.Companion.socketUserMap
import com.example.pattiteen.model.GameState


class ServerConnectionThread(
    private val playerCount: Int,
    private val serverHandler: ServerHandler
) : Thread() {
    override fun run() {
        if (serverSocket != null) return
        try {
            serverSocket = ServerSocket(SocketServerPORT)
            serverStarted = true
            while (true) {
                val socket = serverSocket?.accept() ?: continue
                if (!allPlayersJoined) {
                    ServerListenerThread(socket, serverHandler).start()
                    ServerSenderThread(socket, "pattiTeen").start()
                    socketUserMap[socket] = null
                    if (socketUserMap.size == playerCount) {
                        allPlayersJoined = true
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    companion object {
        const val SocketServerPORT = 8080
        var socketUserMap: HashMap<Socket, String?> = HashMap()
        var serverStarted = false
        var serverSocket: ServerSocket? = null
        var allPlayersJoined = false
    }
}

class ServerHandler(
    private val eventHandler: EventHandler
): Handler(Looper.getMainLooper()) {
    override fun handleMessage(msg: Message) {
        super.handleMessage(msg)
        val messageData: Bundle = msg.getData()
        eventHandler.handleMessage(messageData)
//        val gameObject: Any? = messageData.getSerializable(Constants.DATA_KEY)
//        if (gameObject is PlayerInfo) {
//            val playerInfo: PlayerInfo? = gameObject as PlayerInfo?
//            PlayerListFragment.deviceList.add(playerInfo.username)
//            PlayerListFragment.mAdapter.notifyItemInserted(PlayerListFragment.deviceList.size() - 1)
//        }
//        if (gameObject is Game) {
//            if (GameFragment.gameObject != null) {
//                GameFragment.gameObject = gameObject as Game?
//                GameFragment.updatePlayerStatus()
//                GameFragment.updateTable()
//                sendToAll(gameObject)
//            } else {
//                PlayerListFragment.gameObject = gameObject as Game?
//            }
//        }
    }

    fun sendToAll(gameObject: Any) {
        for((socket, username) in ServerConnectionThread.socketUserMap) {
            if (username != (gameObject as? GameState)?.username) {
                ServerSenderThread(socket, gameObject).start()
            }
        }
    }
}