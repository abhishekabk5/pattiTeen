package com.example.pattiteen.connect.client

import android.os.Bundle
import android.os.Handler
import android.os.Message
import com.example.pattiteen.model.GameState
import com.example.pattiteen.model.PlayerInfo
import com.example.pattiteen.util.Constants
import com.example.pattiteen.util.Logr
import java.io.IOException
import java.io.ObjectInputStream
import java.net.Socket

class ClientListenerThread (
    private val socket: Socket,
    private val clientHandler: Handler
) : Thread() {
    override fun run() {
        try {
            val serverStream = ObjectInputStream(socket.getInputStream())
            while (true) {
//                if (serverStream.available() == 0) {
//                    sleep(THREAD_SLEEP_MILLIS)
//                    continue
//                }
                val data = Bundle()
                val serverObject = serverStream.readObject() as Any
                Logr.i("S: $serverObject")
                if (serverObject is ArrayList<*>) {
                    data.putParcelableArrayList(
                        Constants.KEY_PLAYER_LIST,
                        ArrayList(serverObject.mapNotNull { it as? PlayerInfo })
                    )
                } else if (serverObject is GameState) {
                    data.putParcelable(Constants.KEY_GAME_STATE, serverObject)
                }
                val msg = Message()
                msg.setData(data)
                clientHandler.sendMessage(msg)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        }
    }

    companion object {
        private const val THREAD_SLEEP_MILLIS = 100L
    }
}