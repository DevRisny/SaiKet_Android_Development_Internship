package com.risnysuha.saiket.gallery;

/** Plain-Java helper for moving between images in the full-screen viewer. Wraps around at both ends. */
public final class GalleryNavigator {

    private final int count;
    private int position;

    public GalleryNavigator(int count, int startPosition) {
        if (count <= 0) {
            throw new IllegalArgumentException("The gallery is empty");
        }
        this.count = count;
        this.position = clamp(startPosition);
    }

    public int getPosition() {
        return position;
    }

    public int next() {
        position = (position + 1) % count;
        return position;
    }

    public int previous() {
        position = (position - 1 + count) % count;
        return position;
    }

    /** Jumps to a picture chosen in the thumbnail strip. */
    public int jumpTo(int index) {
        position = clamp(index);
        return position;
    }

    /** Human friendly counter, for example "3 / 12". */
    public String label() {
        return (position + 1) + " / " + count;
    }

    private int clamp(int value) {
        return Math.max(0, Math.min(count - 1, value));
    }
}
