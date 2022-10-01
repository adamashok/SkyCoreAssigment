package com.ashok.skycoreassigment.model
import com.google.gson.annotations.SerializedName

data class YelpResponseModel (

	@SerializedName("businesses") val businesses : ArrayList<Businesses>,
	@SerializedName("total") val total : Int,
	@SerializedName("region") val region : Region
)