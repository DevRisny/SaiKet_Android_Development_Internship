package com.risnysuha.saiket.gallery;

/** One picture in the gallery: the drawable id, a title and a short caption. */
public final class GalleryItem {

    private final int imageRes;
    private final String title;
    private final String caption;

    public GalleryItem(int imageRes, String title, String caption) {
        this.imageRes = imageRes;
        this.title = title;
        this.caption = caption;
    }

    public int getImageRes() {
        return imageRes;
    }

    public String getTitle() {
        return title;
    }

    public String getCaption() {
        return caption;
    }
}
