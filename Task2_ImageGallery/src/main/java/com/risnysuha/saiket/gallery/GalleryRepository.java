package com.risnysuha.saiket.gallery;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/** The 12 images stored in res/drawable-nodpi. All were drawn for this project. */
public final class GalleryRepository {

    private static final List<GalleryItem> ITEMS = Collections.unmodifiableList(Arrays.asList(
            new GalleryItem(R.drawable.scene_01, "Sunrise Hills", "Warm light over rolling peach hills."),
            new GalleryItem(R.drawable.scene_02, "Ocean Dusk", "The sun sinks into a calm blue sea."),
            new GalleryItem(R.drawable.scene_03, "Misty Peaks", "Layer after layer of soft blue mountains."),
            new GalleryItem(R.drawable.scene_04, "Desert Glow", "Golden dunes in the late afternoon."),
            new GalleryItem(R.drawable.scene_05, "Aurora Night", "Northern lights over a pine forest."),
            new GalleryItem(R.drawable.scene_06, "Tea Country", "Green tea rows in the hill country."),
            new GalleryItem(R.drawable.scene_07, "Coral Bay", "Palm trees on a quiet turquoise beach."),
            new GalleryItem(R.drawable.scene_08, "Lavender Field", "Purple rows that reach the horizon."),
            new GalleryItem(R.drawable.scene_09, "Golden Hour Lake", "A still lake catching the evening sun."),
            new GalleryItem(R.drawable.scene_10, "Rainforest", "Deep green canopy under a mint sky."),
            new GalleryItem(R.drawable.scene_11, "Snow Ridge", "A bright white summit on a clear day."),
            new GalleryItem(R.drawable.scene_12, "Lagoon Night", "Moonlight on a calm lagoon.")
    ));

    private GalleryRepository() {
    }

    public static List<GalleryItem> items() {
        return ITEMS;
    }
}
