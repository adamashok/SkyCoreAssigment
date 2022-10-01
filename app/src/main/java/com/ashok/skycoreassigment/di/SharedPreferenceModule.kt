package com.ashok.skycoreassigment.di

import android.content.Context
import android.content.SharedPreferences
import com.ashok.skycoreassigment.utils.CommonUtils
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SharedPreferenceModule {

    @Provides
    @Singleton
    fun provideSharedPreference(@ApplicationContext context: Context ) : SharedPreferences {
        return context.getSharedPreferences(CommonUtils.GET_PREF_NAME, Context.MODE_PRIVATE)
    }

}