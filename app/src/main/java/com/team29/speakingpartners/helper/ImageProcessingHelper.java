package com.team29.speakingpartners.helper;

import android.graphics.Bitmap;
import android.graphics.Matrix;

public class ImageProcessingHelper {

    public static final String TAG = ImageProcessingHelper.class.getSimpleName();

    // Resize image
    public static Bitmap scaleDownBitmapImage(Bitmap realImageBitmap, int maxSize, boolean filter) {

        int realWidth = realImageBitmap.getWidth();
        int realHeight = realImageBitmap.getHeight();

        float ratio = Math.min(
                (float) maxSize / realWidth,
                (float) maxSize / realHeight
        );

        int width = Math.round(ratio * realWidth);
        int height = Math.round(ratio * realHeight);

        return Bitmap.createScaledBitmap(realImageBitmap, width, height, filter);
    }

    // Rotate image
    public static Matrix rotateImage(int angle, int pivotX, int pivotY) {
        Matrix matrix = new Matrix();
        // Need to set scale type <ImageView.ScaleType.MATRIX> to image view
        matrix.postRotate((float) angle, pivotX, pivotY);
        return matrix;
    }

}
