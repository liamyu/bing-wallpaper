package com.liam.bing_wallpaper.components.main

import android.Manifest
import android.app.WallpaperManager
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.alexvasilkov.gestures.GestureController
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.snackbar.Snackbar
import com.liam.bing_wallpaper.R
import com.liam.bing_wallpaper.common.GlideApp
import com.liam.bing_wallpaper.common.getNavigationBarHeight
import com.liam.bing_wallpaper.common.saveTo
import com.liam.bing_wallpaper.common.toBitmap
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.experimental.launch
import org.jetbrains.anko.coroutines.experimental.bg
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {
    private val PERMISSIONS_REQUEST = 1
    private val viewModel: MainViewModel = MainViewModel()
    private var resDrawable: Drawable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        loadBingImage()
        setupImmersiveMode()
    }

    private fun setupImmersiveMode() {
        photoView.controller.settings.maxZoom = 8f
        photoView.controller.settings.doubleTapZoom = 2.8f
        photoView.controller.setOnGesturesListener(object : GestureController.SimpleOnGestureListener() {
            override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                toggleImmersiveMode()
                return super.onSingleTapConfirmed(e)
            }
        })

        window.decorView.setOnSystemUiVisibilityChangeListener {
            if (it == View.VISIBLE) {
                exitImmersiveMode()
            }
        }
    }

    /**Fetch bing images.*/
    private fun loadBingImage() {
        val deferredImage = bg { viewModel.getImage() }
        launch {
            val image = deferredImage.await()
            runOnUiThread {
                displayImage(image.url)
                copyright.text = image.copyright
            }
        }
    }

    /**Display image.*/
    private fun displayImage(url: String) {
        GlideApp.with(this)
                .load("http://www.bing.com$url".replace("1080", "1200"))
                .centerInside()
                .listener(object : RequestListener<Drawable?> {
                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable?>?, isFirstResource: Boolean): Boolean {
                        return true
                    }

                    override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable?>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                        progressBar.visibility = View.GONE
                        photoView.setImageDrawable(resource)
                        resDrawable = resource
                        return true
                    }
                })
                .into(photoView)
    }

    private fun setAsWallPaper() {
        if (resDrawable == null) {
            return
        }
        val wallpaperManager = WallpaperManager.getInstance(applicationContext)
        wallpaperManager.setBitmap(resDrawable!!.toBitmap())
        Toast.makeText(this, R.string.set_wallpaper_success, Toast.LENGTH_SHORT).show()
    }

    private fun toggleImmersiveMode() {
        var newUiOptions = window.decorView.systemUiVisibility

        if (newUiOptions and View.SYSTEM_UI_FLAG_LAYOUT_STABLE == 0) {
            // first time enter immersive mode
            newUiOptions = newUiOptions or
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_IMMERSIVE
        }

        // TODO: rotation
        if (newUiOptions and View.SYSTEM_UI_FLAG_FULLSCREEN == 0) {
            // enter immersive mode
            copyright.visibility = View.GONE
        } else {
            exitImmersiveMode()
        }

        // toggle status bar and navigation bar visibility
        newUiOptions = newUiOptions xor
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION xor
                View.SYSTEM_UI_FLAG_FULLSCREEN

        window.decorView.systemUiVisibility = newUiOptions
    }

    private fun exitImmersiveMode() {
        copyright.visibility = View.VISIBLE
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.action_apply -> {
                AlertDialog.Builder(this)
                        .setMessage(R.string.alert_msg)
                        .setPositiveButton(android.R.string.ok) { _, _ -> setAsWallPaper() }
                        .setNegativeButton(android.R.string.cancel, null)
                        .create()
                        .show()
                true
            }
            R.id.action_download -> {
                // 检查读取内部存储、电话权限
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) === PackageManager.PERMISSION_GRANTED) {
                    saveToSdCard()
                } else {
                    // Should we show an explanation?
                    if (ActivityCompat.shouldShowRequestPermissionRationale(
                                    this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        // sees the explanation, try again to request the permission.
                        Toast.makeText(this, R.string.request_storage_permission, Toast.LENGTH_LONG)
                    }
                    ActivityCompat.requestPermissions(
                            this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                            PERMISSIONS_REQUEST)
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun saveToSdCard() {
        if (resDrawable == null) {
            return
        }
        val dir = "Pictures/BingWall"
        val name = SimpleDateFormat("yyyyMMdd", Locale.US).format(Date()) + ".png"
        if (resDrawable!!.toBitmap().saveTo(this, dir, name)) {
            val msg = String.format(Locale.US, getString(R.string.save_success),
                    dir + File.separator + name)
            Snackbar.make(copyright, msg, Snackbar.LENGTH_LONG)
                    .apply {
                        view.layoutParams = (view.layoutParams as FrameLayout.LayoutParams)
                                .apply {
                                    setMargins(leftMargin, topMargin, rightMargin, getNavigationBarHeight())
                                }
                    }
                    .show()
        } else {
            Toast.makeText(this, R.string.save_failed, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSIONS_REQUEST) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                saveToSdCard()
            }
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            toggleImmersiveMode()
        }
    }
}
