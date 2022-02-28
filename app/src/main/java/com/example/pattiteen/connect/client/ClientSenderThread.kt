package com.example.pattiteen.connect.client

import com.example.pattiteen.connect.server.ServerConnectionThread
import com.example.pattiteen.util.Logr
import java.io.IOException
import java.io.ObjectOutputStream
import java.net.Socket
import java.util.concurrent.ArrayBlockingQueue

class ClientSenderThread(
    private val hostThreadSocket: Socket,
    private val outputStream: ObjectOutputStream
) : Thread() {

    private val msgQ = ArrayBlockingQueue<Any>(1)
    fun addMessage(msg: Any) {
        msgQ.add(msg)
    }

    override fun run() {
        if (hostThreadSocket.isConnected) {
            while (true) {
                try {
                    if (msgQ.isEmpty()) {
                        sleep(ClientConnectionThread.CLIENT_THREAD_SLEEP_MILLIS)
                        continue
                    }
                    val message = msgQ.poll()
                    Logr.i("to S: $message")
                    outputStream.apply {
                        reset()
                        writeObject(message)
                        flush()
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }
}