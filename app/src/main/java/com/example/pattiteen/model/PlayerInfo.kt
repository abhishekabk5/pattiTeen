package com.example.pattiteen.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PlayerInfo(
    val username: String
) : Parcelable