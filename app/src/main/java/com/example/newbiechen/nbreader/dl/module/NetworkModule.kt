package com.example.newbiechen.nbreader.dl.module

import com.example.newbiechen.nbreader.data.remote.api.BookApi
import com.example.newbiechen.nbreader.uilts.Constants
import com.facebook.stetho.okhttp3.StethoInterceptor
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
class NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
        .addNetworkInterceptor(StethoInterceptor()) // 从 Stetho 拦截 OkHttp 数据
        .connectTimeout(15L, TimeUnit.SECONDS)
        .writeTimeout(15L, TimeUnit.SECONDS)
        .readTimeout(30L, TimeUnit.SECONDS)
        .build()

    @Provides
    @Singleton
    fun provideBookApi(client: OkHttpClient): BookApi = Retrofit.Builder()
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .baseUrl(Constants.API_BASE_URL)
        .build()
        .create(BookApi::class.java)
}