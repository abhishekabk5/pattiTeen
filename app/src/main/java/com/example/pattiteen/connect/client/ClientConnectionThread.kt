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
                    var senderThread: ClientSenderThread? = null
                    if (server.isConnected) {
                        serverStarted = true
                        ClientListenerThread(server, clientHandler).start()
                        val playerInfo = PlayerInfo(userName)
                        senderThread = ClientSenderThread(server, ObjectOutputStream(server.getOutputStream())).apply {
                            start()
                            addMessage(playerInfo)
                        }
                    }
                    socket = server to senderThread
                }
            } catch (e: UnknownHostException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    companion object {
        const val CLIENT_THREAD_SLEEP_MILLIS = 100L
        var socket: Pair<Socket, ClientSenderThread?>? = null
        var serverStarted = false
    }
}

