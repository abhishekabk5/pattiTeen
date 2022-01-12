package com.example.pattiteen.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Parcelize
data class GameState(
    var chaal: Int = 5,
    var potTotal: Int = 0,
    val userOrderList: ArrayList<PlayerState>,
    var currPlayer: Int = 0
) : Serializable, Parcelable


// update types: PlayerListUpdate(init, listEdit), GameStateUpdate(chaal)
// client actions: ActionTurn(playerState, turnType)