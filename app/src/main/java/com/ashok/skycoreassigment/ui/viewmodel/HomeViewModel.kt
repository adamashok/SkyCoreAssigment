package com.ashok.skycoreassigment.ui.viewmodel

import androidx.lifecycle.*
import androidx.paging.*
import com.ashok.skycoreassigment.model.Businesses
import com.ashok.skycoreassigment.network.YelpAPI
import com.ashok.skycoreassigment.ui.adapters.PostDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val yelpAPI: YelpAPI)
    : ViewModel()
{

    private fun getData(lat: String,
                                   lon: String,
                                   limit: Int,
                                   offset: Int,
                                   radius: Int,
                                   edtTextValue : String): Flow<PagingData<Businesses>> {
        return Pager(PagingConfig(1)) {
            PostDataSource(yelpAPI,lat,lon,limit,offset,radius, edtTextValue)
        }.flow
    }

    fun getDataRestaurents(lat: String,
                           lon: String,
                           limit: Int,
                           offset: Int,
                           radius: Int,
                           edtTextValue : String): Flow<PagingData<Businesses>> = getData(lat,lon,limit,offset,radius, edtTextValue)
        .map {
                pagingData ->
            pagingData.map {
                it
            }
        }
        .cachedIn(viewModelScope)


}