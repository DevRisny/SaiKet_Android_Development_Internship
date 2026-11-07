package com.risnysuha.saiket.timer;

import android.graphics.Color;
import android.view.View;

import androidx.activity.ComponentActivity;
import androidx.activity.EdgeToEdge;
import androidx.activity.SystemBarStyle;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

/**
 * Draws the app edge to edge (the brand header goes behind the status bar)
 * and pads the screen so nothing hides under the system bars or the keyboard.
 */
public final class EdgeInsets {

    private EdgeInsets() {
    }

    /** Call before setContentView: white status-bar icons, dark navigation-bar icons. */
    public static void enable(ComponentActivity activity) {
        EdgeToEdge.enable(activity,
                SystemBarStyle.dark(Color.TRANSPARENT),
                SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT));
    }

    /** Header gets the status-bar height on top; root gets side, bottom and keyboard space. */
    public static void apply(View root, View header) {
        final int headerTop = header.getPaddingTop();
        final int rootLeft = root.getPaddingLeft();
        final int rootRight = root.getPaddingRight();
        final int rootBottom = root.getPaddingBottom();
        ViewCompat.setOnApplyWindowInsetsListener(root, (view, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars()
                    | WindowInsetsCompat.Type.displayCutout());
            Insets ime = insets.getInsets(WindowInsetsCompat.Type.ime());
            header.setPadding(header.getPaddingLeft(), headerTop + bars.top,
                    header.getPaddingRight(), header.getPaddingBottom());
            view.setPadding(rootLeft + bars.left, view.getPaddingTop(),
                    rootRight + bars.right, rootBottom + Math.max(bars.bottom, ime.bottom));
            return WindowInsetsCompat.CONSUMED;
        });
    }
}
