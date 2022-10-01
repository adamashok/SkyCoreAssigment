
package com.ashok.skycoreassigment.di

import com.ashok.skycoreassigment.network.NetworkUtils
import com.ashok.skycoreassigment.network.YelpAPI
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {


    @Provides
    @Singleton
    fun provideHttpIntercetor() =  HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)


    @Provides
    @Singleton
    fun provideOkHttpClient(httpLoggingInterceptor: HttpLoggingInterceptor) =
        OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .addNetworkInterceptor{ chain ->
                val request = chain.request().newBuilder().addHeader("Authorization", "Bearer ${NetworkUtils.API_KEY}")
                    .build()

                chain.proceed(request)
            }.build()

    @Provides
    @Singleton
    fun provideRetrofitBuilder() = Retrofit.Builder()

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient,retrofitBuilder: Retrofit.Builder) =

        retrofitBuilder.baseUrl(NetworkUtils.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()

    @Provides
    @Singleton
    fun provideYelpAPI(retrofit: Retrofit): YelpAPI = retrofit.create(YelpAPI::class.java)

}