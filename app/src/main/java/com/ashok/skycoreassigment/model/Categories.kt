package com.ashok.skycoreassigment.model

import com.google.gson.annotations.SerializedName



data class Categories (

	@SerializedName("alias") val alias : String,
	@SerializedName("title") val title : String
)