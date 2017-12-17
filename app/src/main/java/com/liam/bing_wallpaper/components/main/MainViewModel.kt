package com.liam.bing_wallpaper.components.main

import android.support.annotation.WorkerThread
import com.liam.bing_wallpaper.entity.BingImageList
import com.liam.bing_wallpaper.repo.BingRepository
import retrofit2.Call

/**
 * ViewModel for MainActivity.
 * Created by Liam on 2017/12/16.
 */
class MainViewModel {
    private val bingRepo: BingRepository = BingRepository()
    private var imagesCall: Call<BingImageList>? = null

    @WorkerThread
    fun getImage(): Pair<String, String> {
        var bingImage = bingRepo.getImages(0, 1)
                .execute()
                .body()!!
                .images[0]
        return Pair(bingImage.url, bingImage.copyright)
    }

    fun onClear() {
        imagesCall?.cancel()
    }
}
