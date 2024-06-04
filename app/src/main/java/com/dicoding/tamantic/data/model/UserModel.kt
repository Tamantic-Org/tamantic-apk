package com.dicoding.tamantic.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserModel(
    val uid: String,
    val name: String,
    val email: String,
    val imageUrl: String,
    val token: String,
    val isLogin: Boolean
) : Parcelable {
    constructor() :
            this(
                "",
                "",
                "",
                "",
                "",
                false
            )
}