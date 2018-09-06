package com.liam.bing_wallpaper.entity

/**
 * Bing image entity.
 * Created by Liam on 2017/12/16.
 */
data class BingImage(
        var startdate: String,
        var fullstartdate: String,
        var enddate: String,
        var url: String,
        var urlbase: String,
        var copyright: String,
        var copyrightlink: String,
        var quiz: String,
        var wp: Boolean,
        var hsh: String,
        var drk: Int,
        var top: Int,
        var bot: Int,
        var hs: List<String>
)