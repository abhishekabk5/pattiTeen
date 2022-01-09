package com.example.pattiteen.model

import java.io.Serializable

data class PlayerInfo(
    val username: String,
    val orderIndex: Int = -1
) : Serializable