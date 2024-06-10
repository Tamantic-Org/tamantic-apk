package com.dicoding.tamantic.data.response

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

@Parcelize
data class ScanResponse(

	@field:SerializedName("result")
	val result: String? = null,

	@field:SerializedName("confidenceScore")
	val confidenceScore: Float? = null,

	@field:SerializedName("explanation")
	val explanation: String? = null
) : Parcelable
