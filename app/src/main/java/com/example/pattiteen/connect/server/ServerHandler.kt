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
//        val gameObject: Any? = messageData.getSerializable(Constants.DATA_KEY)
//        if (gameObject is PlayerInfo) {
//            val playerInfo: PlayerInfo? = gameObject as PlayerInfo?
//            PlayerListFragment.deviceList.add(playerInfo.username)
//            PlayerListFragment.mAdapter.notifyItemInserted(PlayerListFragment.deviceList.size() - 1)
//        }
//        if (gameObject is Game) {
//            if (GameFragment.gameObject != null) {
//                GameFragment.gameObject = gameObject as Game?
//                GameFragment.updatePlayerStatus()
//                GameFragment.updateTable()
//                sendToAll(gameObject)
//            } else {
//                PlayerListFragment.gameObject = gameObject as Game?
//            }
//        }
    }

    fun sendToAll(gameObject: Any) {
        for(socket in ServerConnectionThread.socketUserMap.keys) {
            ServerSenderThread(socket, gameObject).start()
        }
    }
}