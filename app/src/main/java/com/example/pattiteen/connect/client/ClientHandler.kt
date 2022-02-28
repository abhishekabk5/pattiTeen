package com.example.pattiteen.connect.client

import android.os.Handler
import android.os.Looper
import android.os.Message
import com.example.pattiteen.connect.EventHandler

class ClientHandler(
    private val eventHandler: EventHandler
) : Handler(Looper.getMainLooper()) {
    override fun handleMessage(msg: Message) {
        super.handleMessage(msg)
        val messageData = msg.data
        eventHandler.handleMessage(messageData)
    }

    fun sendToServer(gameObject: Any): Boolean {
        ClientConnectionThread.socket?.second?.let {
            it.addMessage(gameObject)
            return true
        }
        return false
    }
}