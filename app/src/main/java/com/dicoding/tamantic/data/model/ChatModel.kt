package com.dicoding.tamantic.data.model

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize

@Parcelize
data class Chat(
    val id: String,
    val fromId: String,
    val toId: String,
    val text: String,
    val timeStamp: Long
) : Parcelable {
    constructor() : this(
        "",
        "",
        "",
        "",
        -1
    )
}