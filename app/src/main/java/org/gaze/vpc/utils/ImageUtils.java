package org.gaze.vpc.utils;


import android.content.Context;
import android.graphics.Bitmap;

import java.nio.ByteBuffer;

public class ImageUtils {
    public static int getYUVByteSize(final int width, final int height) {
        // The luminance plane requires 1 byte per pixel.
        final int ySize = width * height;

        // The UV plane works on 2x2 blocks, so dimensions with odd size must be rounded up.
        // Each 2x2 block takes 2 bytes to encode, one each for U and V.
        final int uvSize = ((width + 1) / 2) * ((height + 1) / 2) * 2;

        return ySize + uvSize;
    }

    public static byte[] bitmap2RGB(Bitmap bitmap) {
        int bytes = bitmap.getByteCount();
        ByteBuffer buffer = ByteBuffer.allocate(bytes);
        bitmap.copyPixelsToBuffer(buffer);
        byte[] rgba = buffer.array();
        byte[] pixels = new byte[(rgba.length / 4) * 3];
        int count = rgba.length / 4;
        for (int i = 0; i < count; i++) {
            pixels[i * 3] = rgba[i * 4];
            pixels[i * 3 + 1] = rgba[i * 4 + 1];
            pixels[i * 3 + 2] = rgba[i * 4 + 2];
        }
        return pixels;
    }

    public static byte[] bitmap2RGBA(Bitmap bitmap) {
        int bytes = bitmap.getByteCount();
        ByteBuffer buffer = ByteBuffer.allocate(bytes);
        bitmap.copyPixelsToBuffer(buffer);
        byte[] rgba = buffer.array();
        return rgba;
    }


    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
