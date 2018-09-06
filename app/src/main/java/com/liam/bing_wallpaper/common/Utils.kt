package com.liam.bing_wallpaper.common

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Environment
import android.util.DisplayMetrics
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


/**
 * Utility file.
 * Created by Liam on 2017/12/23.
 */
fun Drawable.toBitmap(): Bitmap {
    if (this is BitmapDrawable) {
        return this.bitmap
    }

    var width = this.intrinsicWidth
    width = if (width > 0) width else 1
    var height = this.intrinsicHeight
    height = if (height > 0) height else 1

    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    this.setBounds(0, 0, canvas.width, canvas.height)
    this.draw(canvas)

    return bitmap
}

/**
 * Save to sd card.
 */
fun Bitmap.saveTo(ctx: Context, folderName: String, fileName: String): Boolean {
    if (Environment.getExternalStorageState() != Environment.MEDIA_MOUNTED) {
        return false
    }
    var out: FileOutputStream? = null
    try {
        val folderPath = Environment.getExternalStorageDirectory().toString() + File.separator + folderName
        val folder = File(folderPath)
        if (!folder.exists() && !folder.mkdirs()) {
            return false
        }
        val file = File(folderPath, fileName)
        if (!file.exists() && !file.createNewFile()) {
            return false
        }
        out = FileOutputStream(file)
        this.compress(Bitmap.CompressFormat.PNG, 100, out) // bmp is your Bitmap instance
        // PNG is a lossless format, the compression factor (100) is ignored

        ctx.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)))
    } catch (e: Exception) {
        e.printStackTrace()
        return false
    } finally {
        try {
            out?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
    return true
}


/**
 * 获取竖屏时虚拟导航栏高度.
 */
fun Activity.getNavigationBarHeight(): Int {
    val metrics = DisplayMetrics()
    windowManager.defaultDisplay.getMetrics(metrics)
    val usableHeight = metrics.heightPixels
    windowManager.defaultDisplay.getRealMetrics(metrics)
    val realHeight = metrics.heightPixels
    return if (realHeight > usableHeight) {
        realHeight - usableHeight
    } else {
        0
    }
}