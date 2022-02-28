package com.example.pattiteen.connect.server

import android.os.Bundle
import android.os.Message
import com.example.pattiteen.model.PlayerInfo
import com.example.pattiteen.util.Constants
import com.example.pattiteen.util.Logr
import java.io.IOException
import java.io.ObjectOutputStream
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
                    val serverSender = ServerSenderThread(ObjectOutputStream(socket.getOutputStream())).apply {
                        start()
                        addMessage("pattiTeen")
                    }
                    socketUserMap[socket] = serverSender

                    if (socketUserMap.size == playerCount) {
                        allPlayersJoined = true
                        serverHandler.handleMessage(Message().apply { data = Bundle().apply {
                            putInt(Constants.ACTION_KEY, Constants.GAME_START)
                        }})
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    companion object {
        const val SocketServerPORT = 8080
        const val SERVER_THREAD_SLEEP_MILLIS = 50L
        var socketUserMap: HashMap<Socket, ServerSenderThread> = HashMap()
        var serverStarted = false
        var serverSocket: ServerSocket? = null
        var allPlayersJoined = false
    }
}

