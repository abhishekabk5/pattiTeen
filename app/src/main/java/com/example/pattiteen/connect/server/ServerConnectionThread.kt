package com.example.pattiteen.connect.server

import com.example.pattiteen.util.Logr
import java.io.IOException
import java.net.ServerSocket
import java.net.Socket


class ServerConnectionThread(
    private val playerCount: Int,
    private val serverHandler: ServerHandler
) : Thread() {
    override fun run() {
        if (serverSocket != null) return
        try {
            serverSocket = ServerSocket(SocketServerPORT)
            serverStarted = true
            Logr.i("Server started")
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

