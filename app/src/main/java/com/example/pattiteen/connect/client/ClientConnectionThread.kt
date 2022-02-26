package com.example.pattiteen.connect.client

import android.os.Handler
import com.example.pattiteen.connect.server.ServerConnectionThread
import com.example.pattiteen.model.PlayerInfo
import java.io.IOException
import java.io.ObjectOutputStream
import java.net.Socket
import java.net.UnknownHostException


class ClientConnectionThread(
    private val userName: String,
    private val serverAddress: String?,
    private val clientHandler: Handler
) : Thread() {
    private val dstPort = ServerConnectionThread.SocketServerPORT
    override fun run() {
        if (socket == null) {
            try {
                if (serverAddress != null) {
                    val server = Socket(serverAddress, dstPort)
                    val outputStream = ObjectOutputStream(server.getOutputStream())
                    if (server.isConnected) {
                        serverStarted = true
                        ClientListenerThread(server, clientHandler).start()
                        val playerInfo = PlayerInfo(userName)
                        ClientSenderThread(server, outputStream, playerInfo).start()
                    }
                    socket = server to outputStream
                }
            } catch (e: UnknownHostException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    companion object {
        var socket: Pair<Socket, ObjectOutputStream>? = null
        var serverStarted = false
    }
}

