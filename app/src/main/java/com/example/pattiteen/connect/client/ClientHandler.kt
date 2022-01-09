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
//        val value = messageData!!.getInt(Constants.ACTION_KEY)
//        val clientObject: Any? = messageData!!.getSerializable(Constants.DATA_KEY)
//        if (value == Constants.UPDATE_GAME_NAME) {
//            val gameName = clientObject as String?
//            JoinGameFragment.gameName.setText(gameName)
//        }
//        if (clientObject is Game) {
//            if (GameFragment.gameObject != null) {
//                if ((clientObject as Game?).senderUsername.equals(java.lang.String.valueOf(Constants.NEW_GAME))) {
//                    ClientSenderThread.isActive = true
//                    //                    ((Game) clientObject).senderUsername = "";
//                }
//                GameFragment.gameObject = clientObject as Game?
//                GameFragment.updatePlayerStatus()
//                GameFragment.updateTable()
//                GameFragment.updateHand()
//            } else {
//                JoinGameFragment.gameobject = clientObject as Game?
//            }
//        }
    }

    companion object {
        fun sendToServer(gameObject: Any) {
            ClientConnectionThread.socket?.let {
                ClientSenderThread(it, gameObject).start()
            }
        }
    }
}