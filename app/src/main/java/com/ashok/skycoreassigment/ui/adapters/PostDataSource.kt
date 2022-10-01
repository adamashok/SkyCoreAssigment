package com.ashok.skycoreassigment.ui.adapters

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.ashok.skycoreassigment.model.Businesses
import com.ashok.skycoreassigment.model.YelpResponseModel
import com.ashok.skycoreassigment.network.YelpAPI

class PostDataSource (private val yelpAPI: YelpAPI,
                      val lat: String,
                      val lon: String,
                      val limit: Int,
                      val offset: Int,
                      val radius: Int,
                      val edtTextValue : String) : PagingSource<Int, Businesses>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Businesses> {

        try {

            val currentLoadingPageKey = params.key ?: 0


            if(edtTextValue != "")
            {

                val response  = yelpAPI.getNearbyRestaurentsUsingAddress(edtTextValue,"restaurents",limit,currentLoadingPageKey,"distance",radius)
                val responseBody : YelpResponseModel = response.body()!!

                val collectionsList = responseBody.businesses
                val prevKey = if (currentLoadingPageKey == 0) null else currentLoadingPageKey - 15

                return LoadResult.Page(
                    data = collectionsList,
                    prevKey = prevKey,
                    nextKey = currentLoadingPageKey.plus(15)
                )

            }
            else

            {
                val response  = yelpAPI.getNearbyRestaurents("restaurents",lat,lon,limit,currentLoadingPageKey,"distance",radius)
                val responseBody : YelpResponseModel = response.body()!!

                val collectionsList = responseBody.businesses
                val prevKey = if (currentLoadingPageKey == 0) null else currentLoadingPageKey - 15

                return LoadResult.Page(
                    data = collectionsList,
                    prevKey = prevKey,
                    nextKey = currentLoadingPageKey.plus(15)
                )

            }

        } catch (e: Exception) {
            return LoadResult.Error(e)
        }

    }

    override fun getRefreshKey(state: PagingState<Int, Businesses>): Int? {

        return state.anchorPosition?.let { anchorPosition ->
            val anchorPageIndex = state.pages.indexOf(state.closestPageToPosition(anchorPosition))
            state.pages.getOrNull(anchorPageIndex + 15)?.prevKey ?: state.pages.getOrNull(anchorPageIndex - 15)?.nextKey
        }
    }

}