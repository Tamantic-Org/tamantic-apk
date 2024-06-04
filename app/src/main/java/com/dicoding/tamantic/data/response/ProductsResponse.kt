package com.dicoding.tamantic.data.response

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

@Parcelize
data class ProductsResponse(

	@field:SerializedName("data")
	val data: List<DataItem?>? = null

) : Parcelable

@Parcelize
data class DataItem(

	@field:SerializedName("owner")
	val owner: String? = null,

	@field:SerializedName("image")
	val image: String? = null,

	@field:SerializedName("sold")
	val sold: Int? = null,

	@field:SerializedName("phone")
	val phone: Long? = null,

	@field:SerializedName("rate")
	val rate: Double? = null,

	@field:SerializedName("imageMarket")
	val imageMarket: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("postdate")
	val postdate: String? = null,

	@field:SerializedName("description")
	val description: String? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("categories")
	val categories: List<String?>? = null,

	@field:SerializedName("alamat")
	val alamat: String? = null,

	@field:SerializedName("price")
	val price: Int? = null

) : Parcelable
