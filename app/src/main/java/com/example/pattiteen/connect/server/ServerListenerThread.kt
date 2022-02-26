package com.example.pattiteen.connect.server

import android.os.Bundle
import android.os.Message
import com.example.pattiteen.model.GameState
import com.example.pattiteen.model.PlayerInfo
import com.example.pattiteen.model.TurnActionDto
import com.example.pattiteen.util.Constants
import com.example.pattiteen.util.Logr
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.net.Socket

class ServerListenerThread(
    private val hostThreadSocket: Socket,
    private val serverHandler: ServerHandler
) : Thread() {
    override fun run() {
        try {
            val clientStream = ObjectInputStream(hostThreadSocket.getInputStream())
            while (true) {
//                if (clientStream.available() == 0) {
//                    sleep(THREAD_SLEEP_MILLIS)
//                    continue
//                }
                val gameObject = clientStream.readObject()
                Logr.i("C: $gameObject")
                val data = Bundle()
                when (gameObject) {
                    is PlayerInfo -> {
                        data.putParcelable(Constants.KEY_PLAYER_INFO, gameObject)
                        data.putInt(Constants.ACTION_KEY, Constants.PLAYER_LIST_UPDATE)
                        ServerConnectionThread.socketUserMap[hostThreadSocket] =
                            ObjectOutputStream(hostThreadSocket.getOutputStream()) to gameObject
                    }
                    is GameState -> data.putParcelable(Constants.KEY_GAME_STATE, gameObject)
                    is TurnActionDto -> data.putParcelable(Constants.KEY_PLAYER_ACTION, gameObject)
                }
                serverHandler.sendMessage(Message().apply { this.data = data })
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        }
    }

    companion object {
        private const val THREAD_SLEEP_MILLIS = 50L
    }
}