package com.example.pattiteen.model

import java.io.Serializable

data class GameState(
    val username: String,
    val playerCount: Int
) : Serializable