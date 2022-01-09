package com.example.pattiteen.connect.client

import android.os.Bundle
import android.os.Handler
import android.os.Message
import com.example.pattiteen.util.Utils
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
                (serverObject as? String)?.let { Utils.showToast("Server: $it") }
//                if (serverObject is String) {
//                    data.putSerializable(Constants.DATA_KEY, serverObject)
//                    data.putInt(Constants.ACTION_KEY, Constants.UPDATE_GAME_NAME)
//                }
//                if (serverObject is Game) {
//                    data.putSerializable(Constants.DATA_KEY, serverObject as Game)
//                }
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