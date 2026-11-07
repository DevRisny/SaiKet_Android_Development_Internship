package com.risnysuha.saiket.gallery;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Task 2 · Simple Image Gallery.
 * A GridView shows the pictures from res/drawable through an adapter, and an
 * OnItemClickListener opens the tapped picture in a full viewer.
 */
public class MainActivity extends AppCompatActivity {

    private Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeInsets.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EdgeInsets.apply(findViewById(R.id.root), findViewById(R.id.header));

        TextView count = findViewById(R.id.gallery_count);
        count.setText(getString(R.string.count_format, GalleryRepository.items().size()));

        GridView grid = findViewById(R.id.gallery_grid);
        grid.setAdapter(new ImageAdapter(this, GalleryRepository.items()));

        // Step 3: react when the user selects an image.
        grid.setOnItemClickListener((parent, view, position, id) -> {
            GalleryItem item = GalleryRepository.items().get(position);
            showToast(getString(R.string.opening_format, item.getTitle()));
            Intent intent = new Intent(this, ImageViewerActivity.class)
                    .putExtra(ImageViewerActivity.EXTRA_POSITION, position);
            // A smooth zoom from the tapped cell.
            ActivityOptions options = ActivityOptions.makeScaleUpAnimation(
                    view, 0, 0, view.getWidth(), view.getHeight());
            startActivity(intent, options.toBundle());
        });
    }

    private void showToast(String message) {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();
    }
}
