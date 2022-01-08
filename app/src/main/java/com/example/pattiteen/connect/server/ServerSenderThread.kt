package com.example.pattiteen.connect.server

import java.io.IOException
import java.io.ObjectOutputStream
import java.net.Socket

class ServerSenderThread(
    private val hostThreadSocket: Socket,
    private val message: Any
) : Thread() {

    override fun run() {
        try {
            ObjectOutputStream(hostThreadSocket.getOutputStream()).writeObject(message)
//            if (message is Game) {
//                PlayerListFragment.gameObject = message as Game
//            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}