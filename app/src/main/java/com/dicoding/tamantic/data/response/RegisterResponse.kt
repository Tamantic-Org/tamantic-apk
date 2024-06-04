package com.dicoding.tamantic.data.response

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

@Parcelize
data class RegisterResponse(

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("email")
	val email: String? = null,

	@field:SerializedName("isOwner")
	val isOwner: Boolean,

	@field:SerializedName("phone")
	val phone: String? = null,

	@field:SerializedName("password")
	val password: String? = null,

	@field:SerializedName("updatedDate")
	val updatedDate: String? = null,

	@field:SerializedName("registerDate")
	val registerDate: String? = null,

	@field:SerializedName("userImage")
	val imageUrl: String? = null

) : Parcelable
