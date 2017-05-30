package com.pic.libphotocollage.core.util.displaybmp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.pic.libphotocollage.core.util.ResizeImage;

/**
 * Created by vutha on 9/18/2016.
 */
public class BitmapDecoder {

    public static Bitmap decodeSampledBitmapFromFile(Context context, String path, int reqWidth, int reqHeight, int maxTextureSize) {
        return new ResizeImage(context).getBitmap(path, reqWidth);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight, int maxTextureSize) {
//        Flog.d("calculateInSampleSize");
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

//        Flog.d("glinfo: " + "Max texture size = " + EGL14Util.getMaxTextureSize());

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while (((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth)
                    || ((height / inSampleSize) >= maxTextureSize)
                    || ((width / inSampleSize) >= maxTextureSize)) {
//                Flog.d("c1: " + ((halfHeight / inSampleSize)));
//                Flog.d("c2: " + ((halfWidth / inSampleSize) ));
//                Flog.d("c3: " + reqHeight +"_"+ reqWidth);
                inSampleSize *= 2;
            }
        }

//        Flog.d("w: "+width + "_h: "+height + "_sample: "+inSampleSize);
        return inSampleSize;
    }
}
