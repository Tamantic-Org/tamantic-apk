package com.dicoding.tamantic.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ScanModel(
    val id: String,
    val image: String,
    val result: String,
    val explanation: String,
    val confidenceScore: Float,
) : Parcelable {
    constructor() : this(
        "",
        "",
        "",
        "",
        0f
    )
}