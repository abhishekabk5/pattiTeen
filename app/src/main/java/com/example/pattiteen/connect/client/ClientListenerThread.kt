package com.example.pattiteen.connect.client

import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import com.example.pattiteen.model.GameState
import com.example.pattiteen.model.PlayerInfo
import com.example.pattiteen.util.Constants
import com.example.pattiteen.util.Logr
import java.io.IOException
import java.io.ObjectInputStream
import java.io.OptionalDataException
import java.net.Socket

class ClientListenerThread (
    private val socket: Socket,
    private val clientHandler: Handler
) : Thread() {
    override fun run() {
        try {
            val objectStream = ObjectInputStream(socket.getInputStream())
            while (true) {
                Log.i("stream tag", "length: ${objectStream.available()}")
                if (objectStream.available() == 0) {
                    sleep(ClientConnectionThread.CLIENT_THREAD_SLEEP_MILLIS)
                    continue
                }
                val data = Bundle()
                var serverObject: Any?
                try {
                     serverObject = objectStream.readObject() as Any
                } catch(e: OptionalDataException) { continue }
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
}