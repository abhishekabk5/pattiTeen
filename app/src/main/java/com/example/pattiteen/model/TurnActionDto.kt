package com.example.pattiteen.model

import java.io.Serializable

data class TurnActionDto(
    val state: PlayerState,
    val action: PlayerAction,
): Serializable