package com.liam.bing_wallpaper.repo

import com.liam.bing_wallpaper.api.BingService
import com.liam.bing_wallpaper.entity.BingImageList
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


/**
 * Bing repository.
 * Created by Liam on 2017/12/16.
 */
class BingRepository {
    private val bingService: BingService

    init {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BASIC
        val client = OkHttpClient.Builder()
                .addInterceptor(logging)
                .build()
        val retrofit = Retrofit.Builder()
                .client(client)
                .baseUrl("http://www.bing.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        bingService = retrofit.create(BingService::class.java)
    }

    fun getImages(pageIndex: Int, pageSize: Int): Call<BingImageList> {
        return bingService.getImages(pageIndex, pageSize)
    }
}
