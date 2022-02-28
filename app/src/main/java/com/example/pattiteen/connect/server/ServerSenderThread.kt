package com.example.pattiteen.connect.server

import com.example.pattiteen.util.Logr
import java.io.IOException
import java.io.ObjectOutputStream
import java.util.*
import java.util.concurrent.ArrayBlockingQueue

class ServerSenderThread(
    private val outputStream: ObjectOutputStream
) : Thread() {

    private val msgQ = ArrayBlockingQueue<Any>(1)
    fun addMessage(msg: Any) {
        msgQ.add(msg)
    }

    override fun run() {
        while(true) {
            try {
                if (msgQ.isEmpty()) {
                    sleep(ServerConnectionThread.SERVER_THREAD_SLEEP_MILLIS)
                    continue
                }
                val message = msgQ.poll()
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
}