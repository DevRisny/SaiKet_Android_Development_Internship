package com.risnysuha.saiket.gallery;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.shape.ShapeAppearanceModel;

import java.util.ArrayList;
import java.util.List;

/** Shows the selected picture large, with previous / next buttons. */
public class ImageViewerActivity extends AppCompatActivity {

    public static final String EXTRA_POSITION = "com.risnysuha.saiket.gallery.POSITION";
    private static final String KEY_POSITION = "position";

    private GalleryNavigator navigator;
    private ImageView image;
    private TextView title;
    private TextView caption;
    private TextView counter;
    private HorizontalScrollView thumbsScroll;
    private final List<ShapeableImageView> thumbs = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeInsets.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewer);
        EdgeInsets.apply(findViewById(R.id.root), findViewById(R.id.header));

        int start = savedInstanceState != null
                ? savedInstanceState.getInt(KEY_POSITION)
                : getIntent().getIntExtra(EXTRA_POSITION, 0);
        navigator = new GalleryNavigator(GalleryRepository.items().size(), start);

        image = findViewById(R.id.viewer_image);
        title = findViewById(R.id.viewer_title);
        caption = findViewById(R.id.viewer_caption);
        counter = findViewById(R.id.viewer_counter);
        thumbsScroll = findViewById(R.id.thumbs_scroll);
        buildThumbnails(findViewById(R.id.thumbs_row));

        MaterialButton back = findViewById(R.id.back_button);
        MaterialButton previous = findViewById(R.id.previous_button);
        MaterialButton next = findViewById(R.id.next_button);
        back.setOnClickListener(v -> finish());
        previous.setOnClickListener(v -> {
            navigator.previous();
            show(true, -1);
        });
        next.setOnClickListener(v -> {
            navigator.next();
            show(true, 1);
        });

        show(false, 0);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_POSITION, navigator.getPosition());
    }

    private void show(boolean animate, int direction) {
        GalleryItem item = GalleryRepository.items().get(navigator.getPosition());
        image.setImageResource(item.getImageRes());
        image.setContentDescription(item.getTitle());
        title.setText(item.getTitle());
        caption.setText(item.getCaption());
        counter.setText(navigator.label());
        highlightThumbnail();
        if (animate) {
            image.setAlpha(0.2f);
            image.setTranslationX(60f * direction);
            image.animate().alpha(1f).translationX(0f).setDuration(240).start();
        }
    }

    /** A strip of small pictures under the main one; tap any to jump to it. */
    private void buildThumbnails(LinearLayout row) {
        int size = dp(64);
        int pad = dp(3);
        List<GalleryItem> items = GalleryRepository.items();
        for (int i = 0; i < items.size(); i++) {
            ShapeableImageView thumb = new ShapeableImageView(this);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(size, size);
            if (i < items.size() - 1) {
                lp.setMarginEnd(dp(10));
            }
            thumb.setLayoutParams(lp);
            thumb.setScaleType(ImageView.ScaleType.CENTER_CROP);
            thumb.setShapeAppearanceModel(ShapeAppearanceModel.builder().setAllCornerSizes(dp(16)).build());
            thumb.setPadding(pad, pad, pad, pad);
            thumb.setStrokeWidth(dp(3));
            thumb.setImageResource(items.get(i).getImageRes());
            thumb.setContentDescription(items.get(i).getTitle());
            final int index = i;
            thumb.setOnClickListener(v -> {
                int direction = Integer.compare(index, navigator.getPosition());
                navigator.jumpTo(index);
                show(true, direction);
            });
            row.addView(thumb);
            thumbs.add(thumb);
        }
    }

    private void highlightThumbnail() {
        int selected = navigator.getPosition();
        int blue = ContextCompat.getColor(this, R.color.brand_blue);
        for (int i = 0; i < thumbs.size(); i++) {
            ShapeableImageView thumb = thumbs.get(i);
            boolean on = i == selected;
            thumb.setStrokeColor(ColorStateList.valueOf(on ? blue : Color.TRANSPARENT));
            thumb.setAlpha(on ? 1f : 0.6f);
        }
        if (selected < thumbs.size()) {
            ShapeableImageView current = thumbs.get(selected);
            thumbsScroll.post(() -> thumbsScroll.smoothScrollTo(
                    current.getLeft() - (thumbsScroll.getWidth() - current.getWidth()) / 2, 0));
        }
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }
}
