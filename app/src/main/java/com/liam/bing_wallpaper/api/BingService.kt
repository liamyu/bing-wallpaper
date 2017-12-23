package com.liam.bing_wallpaper.api

import com.liam.bing_wallpaper.entity.BingImageList

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Bing api.
 * Created by Liam on 2017/12/16.
 */
interface BingService {
    /**
     * Bing Api.
     * http://www.bing.com/HPImageArchive.aspx?format=js&idx=0&n=1&mkt=en-US
     *
     * @param pageSize page size
     * @return Call
     */
    @GET("HPImageArchive.aspx?format=js&mkt=en-US")
    fun getImages(@Query("idx") pageIndex: Int, @Query("n") pageSize: Int): Call<BingImageList>
}
