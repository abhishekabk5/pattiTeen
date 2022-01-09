package com.example.pattiteen.connect.server

import android.os.Bundle
import android.os.Message
import com.example.pattiteen.model.GameState
import com.example.pattiteen.model.PlayerInfo
import com.example.pattiteen.util.Constants
import com.example.pattiteen.util.Utils
import java.io.IOException
import java.io.ObjectInputStream
import java.net.Socket

class ServerListenerThread(
    private val hostThreadSocket: Socket,
    private val serverHandler: ServerHandler
) : Thread() {
    override fun run() {
        while (true) {
            try {
                val gameObject = ObjectInputStream(hostThreadSocket.getInputStream()).readObject()
                (gameObject as? String)?.let { Utils.showToast("C: $it") }
                val data = Bundle()
                if (gameObject is PlayerInfo) {
                    data.putSerializable(Constants.DATA_KEY, gameObject)
                    data.putInt(Constants.ACTION_KEY, Constants.PLAYER_LIST_UPDATE)
                    ServerConnectionThread.socketUserMap[hostThreadSocket] =
                        gameObject.username
                } else {
                    data.putSerializable(Constants.DATA_KEY, gameObject as? GameState)
                }
                serverHandler.sendMessage(Message().apply{ this.data = data })
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: ClassNotFoundException) {
                e.printStackTrace()
            }
        }
    }
}