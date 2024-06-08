package com.dicoding.tamantic.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProductModel(
    val uid: String,
    val name: String,
    val desc : String,
    val owner: String,
    val imageUrl: String,
    var status: String,
    var quantity: Int,
    val price: Int,
    val total: Int
) : Parcelable {
    constructor() :
            this(
                "",
                "",
                "",
                "",
                "",
                "",
                0,
                0,
                0,
            )
}