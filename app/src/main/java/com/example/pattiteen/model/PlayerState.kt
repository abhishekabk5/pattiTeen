package com.example.pattiteen.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Parcelize
data class PlayerState(
    var cardsState: CardsState = CardsState.BLIND,
    var potAmount: Int = 0
): Serializable, Parcelable

enum class CardsState {
    BLIND, SEEN, PACKED
}

@Parcelize
data class PlayerAction(
    val type: PlayerActionType,
    val double: Boolean = false
): Serializable, Parcelable

enum class PlayerActionType {
    CHAAL, PACK
    // sideShow
}