package com.dicoding.tamantic.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Alamat(
    val id: String,
    val address: String,
) : Parcelable {
    constructor() : this(
        "",
        "",
    )
}