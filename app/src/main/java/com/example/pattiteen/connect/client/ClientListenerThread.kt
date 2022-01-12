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
            while (true) {
                val data = Bundle()
                val serverObject = ObjectInputStream(socket.getInputStream()).readObject() as Any
                (serverObject as? String)?.let { Logr.i("S: $it") }
                if (serverObject is ArrayList<*>) {
                    data.putParcelableArrayList(
                        Constants.KEY_PLAYER_LIST,
                        ArrayList<PlayerInfo>().apply {
                            serverObject.forEach { (it as? PlayerInfo)?.let { info -> add(info) } }
                        }
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

}