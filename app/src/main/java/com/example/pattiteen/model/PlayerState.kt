package com.example.pattiteen.model

import java.io.Serializable

data class PlayerState(
    var cardsState: CardsState = CardsState.BLIND,
    var potAmount: Int = 0
): Serializable

enum class CardsState {
    BLIND, SEEN, PACKED
}

data class PlayerAction(
    val type: PlayerActionType,
    val double: Boolean = false
)

enum class PlayerActionType {
    CHAAL, PACK
    // sideShow
}