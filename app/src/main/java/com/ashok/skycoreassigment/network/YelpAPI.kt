package com.ashok.skycoreassigment.network

import com.ashok.skycoreassigment.model.YelpResponseModel
import retrofit2.Response
import retrofit2.http.*

interface YelpAPI {


    @GET("v3/businesses/search")
    suspend fun getNearbyRestaurents(
                                     @Query("term") term: String,
                                     @Query("latitude") lat: String,
                                     @Query("longitude")lon: String,
                                     @Query("limit")limit: Int,
                                     @Query("offset")offset: Int,
                                     @Query("sort_by")sort_by: String,
                                     @Query("radius")radius: Int)
    :Response<YelpResponseModel>


    @GET("v3/businesses/search")
    suspend fun getNearbyRestaurentsUsingAddress(
                                     @Query("location") location: String,
                                     @Query("term") term: String,
                                     @Query("limit")limit: Int,
                                     @Query("offset")offset: Int,
                                     @Query("sort_by")sort_by: String,
                                     @Query("radius")radius: Int)
            :Response<YelpResponseModel>


}