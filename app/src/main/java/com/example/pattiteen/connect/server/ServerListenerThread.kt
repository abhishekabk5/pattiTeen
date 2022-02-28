package com.example.pattiteen.connect.server

import android.os.Bundle
import android.os.Message
import android.util.Log
import com.example.pattiteen.connect.server.ServerConnectionThread.Companion.SERVER_THREAD_SLEEP_MILLIS
import com.example.pattiteen.model.GameState
import com.example.pattiteen.model.PlayerInfo
import com.example.pattiteen.model.TurnActionDto
import com.example.pattiteen.util.Constants
import com.example.pattiteen.util.Logr
import java.io.IOException
import java.io.ObjectInputStream
import java.io.OptionalDataException
import java.net.Socket

class ServerListenerThread(
    private val hostThreadSocket: Socket,
    private val serverHandler: ServerHandler
) : Thread() {
    override fun run() {
        try {
            val objectStream = ObjectInputStream(hostThreadSocket.getInputStream())
            while (true) {
                Log.i("stream tag", "length: ${objectStream.available()}")
                if (objectStream.available() == 0) {
                    sleep(SERVER_THREAD_SLEEP_MILLIS)
                    continue
                }
                var gameObject: Any?
                try {
                    gameObject = objectStream.readObject()
                } catch (e: OptionalDataException) {
                    continue
                }
                Logr.i("C: $gameObject")
                val data = Bundle()
                when (gameObject) {
                    is PlayerInfo -> {
                        data.putParcelable(Constants.KEY_PLAYER_INFO, gameObject)
                        data.putInt(Constants.ACTION_KEY, Constants.PLAYER_LIST_UPDATE)
                        ServerConnectionThread.socketUserMap[hostThreadSocket]?.first
                            ?.let {
                                ServerConnectionThread.socketUserMap[hostThreadSocket] =
                                    it to gameObject
                            }
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
}