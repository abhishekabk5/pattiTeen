package com.example.pattiteen.connect.server

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import com.example.pattiteen.connect.EventHandler

class ServerHandler(
    private val eventHandler: EventHandler
): Handler(Looper.getMainLooper()) {
    override fun handleMessage(msg: Message) {
        super.handleMessage(msg)
        val messageData: Bundle = msg.data
        eventHandler.handleMessage(messageData)
    }

    fun sendToAll(gameObject: Any) {
        for(socket in ServerConnectionThread.socketUserMap.values) {
            ServerSenderThread(socket.first, gameObject).start()
        }
    }
}