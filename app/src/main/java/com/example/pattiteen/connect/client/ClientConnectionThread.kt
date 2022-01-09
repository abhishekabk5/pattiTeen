package com.example.pattiteen.connect.client

import android.os.Handler
import com.example.pattiteen.connect.server.ServerConnectionThread
import com.example.pattiteen.model.PlayerInfo
import java.io.IOException
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
                    socket = Socket(serverAddress, dstPort).also {
                        if (it.isConnected) {
                            serverStarted = true
                            ClientListenerThread(it, clientHandler).start()
                            val playerInfo = PlayerInfo(userName)
                            ClientSenderThread(it, playerInfo).start()
                        }
                    }
                }
            } catch (e: UnknownHostException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    companion object {
        var socket: Socket? = null
        var serverStarted = false
    }
}

