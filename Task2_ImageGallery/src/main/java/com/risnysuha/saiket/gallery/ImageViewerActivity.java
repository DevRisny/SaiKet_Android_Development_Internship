package com.risnysuha.saiket.gallery;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

/** Shows the selected picture large, with previous / next buttons. */
public class ImageViewerActivity extends AppCompatActivity {

    public static final String EXTRA_POSITION = "com.risnysuha.saiket.gallery.POSITION";
    private static final String KEY_POSITION = "position";

    private GalleryNavigator navigator;
    private ImageView image;
    private TextView title;
    private TextView caption;
    private TextView counter;

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
        if (animate) {
            image.setAlpha(0.2f);
            image.setTranslationX(60f * direction);
            image.animate().alpha(1f).translationX(0f).setDuration(240).start();
        }
    }
}
