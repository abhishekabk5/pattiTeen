package com.example.pattiteen.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Parcelize
data class TurnActionDto(
    val orderIndex: Int,
    val state: PlayerState,
    val action: PlayerAction,
): Serializable, Parcelable