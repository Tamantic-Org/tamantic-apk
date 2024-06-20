package com.dicoding.tamantic.data.response

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

@Parcelize
data class RecomendedResponse(

	@field:SerializedName("data")
	val data: List<DataItemRecomended?>? = null,

	@field:SerializedName("recommendation")
	val recommendation: List<RecommendationItem?>? = null
) : Parcelable

@Parcelize
data class DataItemRecomended(

	@field:SerializedName("owner")
	val owner: String? = null,

	@field:SerializedName("image")
	val image: String? = null,

	@field:SerializedName("sold")
	val sold: Int? = null,

	@field:SerializedName("postdate")
	val postdate: String? = null,

	@field:SerializedName("description")
	val description: String? = null,

	@field:SerializedName("alamat")
	val alamat: String? = null,

	@field:SerializedName("phone")
	val phone: Long? = null,

	@field:SerializedName("rate")
	val rate: Double? = null,

	@field:SerializedName("price")
	val price: Int? = null,

	@field:SerializedName("product_id")
	val productId: String? = null,

	@field:SerializedName("imageMarket")
	val imageMarket: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("categories")
	val categories: List<String?>? = null,

	@field:SerializedName("stock")
	val stock: Int? = null
) : Parcelable

@Parcelize
data class RecommendationItem(

	@field:SerializedName("owner")
	val owner: String? = null,

	@field:SerializedName("image")
	val image: String? = null,

	@field:SerializedName("sold")
	val sold: Int? = null,

	@field:SerializedName("postdate")
	val postdate: String? = null,

	@field:SerializedName("description")
	val description: String? = null,

	@field:SerializedName("alamat")
	val alamat: String? = null,

	@field:SerializedName("phone")
	val phone: Long? = null,

	@field:SerializedName("rate")
	val rate: Double? = null,

	@field:SerializedName("price")
	val price: Int? = null,

	@field:SerializedName("product_id")
	val productId: String? = null,

	@field:SerializedName("imageMarket")
	val imageMarket: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("categories")
	val categories: List<String?>? = null,

	@field:SerializedName("stock")
	val stock: Int? = null
) : Parcelable
