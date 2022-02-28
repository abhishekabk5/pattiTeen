package com.example.pattiteen.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.io.Serializable
import java.util.*

@Parcelize
data class PlayerInfo(
    val username: String,
    var orderIndex: Int = -1,
    val id: String = UUID.randomUUID().toString()
) : Serializable, Parcelable