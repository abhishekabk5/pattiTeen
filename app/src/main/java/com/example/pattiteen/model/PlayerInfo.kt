package com.example.pattiteen.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Parcelize
data class PlayerInfo(
    val username: String,
    var orderIndex: Int = -1
) : Serializable, Parcelable