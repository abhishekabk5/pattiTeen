package com.example.pattiteen.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GameState(
    val username: String,
    val playerCount: Int
) : Parcelable