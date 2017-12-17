package com.liam.bing_wallpaper.entity;

import java.util.List;

/**
 * Bing api entity.
 * Created by Liam on 2017/12/16.
 */

public class BingImageList {
    /**
     * Bing images.
     */
    private List<BingImage> images;

    public List<BingImage> getImages() {
        return images;
    }

    public void setImages(List<BingImage> images) {
        this.images = images;
    }
}
