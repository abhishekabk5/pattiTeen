package com.example.pattiteen.connect.client

import java.io.IOException
import java.io.ObjectOutputStream
import java.net.Socket

class ClientSenderThread(
    private val hostThreadSocket: Socket,
    private val message: Any
) : Thread() {
    override fun run() {
        if (hostThreadSocket.isConnected) {
            try {
                if (isActive) {
//                    if (message is Game && !Constants.isPlayerActive(
//                            MainFragment.username.getText().toString(), message as Game
//                        )
//                    ) {
//                        isActive = false
//                    }
                    ObjectOutputStream(hostThreadSocket.getOutputStream()).writeObject(message)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    companion object {
        var isActive = true
    }

}