package com.salvi.TaskManagER.Support;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;

public final class RequirementImages {

    private static final String DIRECTORY = "requirement_photos";
    private static final String AUTHORITY_SUFFIX = ".fileprovider";

    private RequirementImages() {
    }

    public static String createCapturePath(Context context) throws IOException {
        File directory = new File(context.getFilesDir(), DIRECTORY);
        if (!directory.exists() && !directory.mkdirs()) {
            throw new IOException("Could not create photo directory");
        }

        return File.createTempFile("req_photo_", ".jpg", directory)
            .getAbsolutePath();
    }

    public static Uri getContentUri(Context context, String path) {
        return FileProvider.getUriForFile(
            context,
            context.getPackageName() + AUTHORITY_SUFFIX,
            new File(path)
        );
    }

    public static Bitmap decodePreview(String path) {
        if (path == null) return null;

        BitmapFactory.Options bounds = new BitmapFactory.Options();
        bounds.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, bounds);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = calculateSampleSize(bounds, 640, 640);
        return BitmapFactory.decodeFile(path, options);
    }

    private static int calculateSampleSize(
        BitmapFactory.Options bounds,
        int maxWidth,
        int maxHeight
    ) {
        int sampleSize = 1;
        while (bounds.outWidth / sampleSize > maxWidth
                || bounds.outHeight / sampleSize > maxHeight) {
            sampleSize *= 2;
        }
        return sampleSize;
    }

    public static void delete(String path) {
        if (path != null) {
            new File(path).delete();
        }
    }
}
