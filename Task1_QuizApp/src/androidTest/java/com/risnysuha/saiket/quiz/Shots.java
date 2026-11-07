package com.risnysuha.saiket.quiz;

import android.os.ParcelFileDescriptor;

import androidx.test.platform.app.InstrumentationRegistry;

import java.io.InputStream;

/** Saves a real screenshot from the device for the README (pulled off the device by CI). */
final class Shots {

    private Shots() {
    }

    static void take(String name) {
        pause(700);
        shell("mkdir -p /sdcard/Pictures/saiket");
        shell("screencap -p /sdcard/Pictures/saiket/" + name + ".png");
    }

    static void pause(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static void shell(String command) {
        try {
            ParcelFileDescriptor pfd = InstrumentationRegistry.getInstrumentation().getUiAutomation()
                    .executeShellCommand(command);
            try (InputStream in = new ParcelFileDescriptor.AutoCloseInputStream(pfd)) {
                byte[] buffer = new byte[1024];
                while (in.read(buffer) != -1) {
                    // wait until the command finishes
                }
            }
        } catch (Exception ignored) {
            // A missing screenshot must never fail the functional checks.
        }
    }
}
