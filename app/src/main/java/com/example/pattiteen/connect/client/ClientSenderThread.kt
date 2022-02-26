package com.example.pattiteen.connect.client

import com.example.pattiteen.util.Logr
import java.io.IOException
import java.io.ObjectOutputStream
import java.net.Socket

class ClientSenderThread(
    private val hostThreadSocket: Socket,
    private val outputStream: ObjectOutputStream,
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
                    Logr.i("to S: $message")
                    outputStream.apply {
                        reset()
                        writeObject(message)
                        flush()
                    }
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