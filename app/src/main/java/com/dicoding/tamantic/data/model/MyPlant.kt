package com.dicoding.tamantic.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MyPlant(
    val id: String,
    val image: String,
    val description: String,
    val matahari: String,
    val pupuk: String,
    val potong: String,
    val air: String
) : Parcelable {
    constructor() : this(
        "",
        "",
        "",
        "",
        "",
        "",
        ""
    )
}