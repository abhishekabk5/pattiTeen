package com.example.pattiteen.model

import java.io.Serializable

data class GameState(
    var chaal: Int = 5,
    var potTotal: Int = 0,
    val userOrderList: ArrayList<PlayerState>,
    var currPlayer: Int = 0
) : Serializable


// update types: PlayerListUpdate(init, listEdit), GameStateUpdate(chaal)
// client actions: ActionTurn(playerState, turnType)