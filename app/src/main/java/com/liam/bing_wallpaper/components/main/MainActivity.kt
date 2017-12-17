package com.liam.bing_wallpaper.components.main

import android.app.WallpaperManager
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.liam.bing_wallpaper.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.experimental.async
import org.jetbrains.anko.coroutines.experimental.bg

class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel = MainViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // https://www.bing.com/az/hprichbg/rb/SeychellesCCSS_EN-US10430664838_1366x768.jpg
        val deferredUrl = bg { viewModel.getImage() }
        async {
            val (url, copyRight) = deferredUrl.await()
            Glide.with(this@MainActivity)
                    .load("https://www.bing.com$url")
                    .listener(object : RequestListener<Drawable?> {
                        override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable?>?, isFirstResource: Boolean): Boolean {
                            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                        }

                        override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable?>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                            progressBar.visibility = View.GONE
                            return true
                        }
                    })
                    .into(photoView)
            copyright.text = copyRight
        }
    }

    fun setAsWallPaper() {
        var wallpaperManager = WallpaperManager.getInstance(applicationContext)
        wallpaperManager.setBitmap(null)
    }
}
