package com.example.pattiteen.connect.server

import com.example.pattiteen.util.Logr
import java.io.IOException
import java.io.ObjectOutputStream

class ServerSenderThread(
    private val outputStream: ObjectOutputStream,
    private val message: Any
) : Thread() {

    override fun run() {
        try {
            Logr.i("to C: $message")
            outputStream.apply {
                reset()
                writeObject(message)
                flush()
            }
//            if (message is Game) {
//                PlayerListFragment.gameObject = message as Game
//            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}