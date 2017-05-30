package uet.kltn.hoavt_58.crazyexface.photo_collage.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.pic.libphotocollage.core.util.SystemUtils;
import com.pic.libphotocollage.core.util.displaybmp.BitmapDecoder;

import java.io.IOException;
import java.io.InputStream;

import uet.kltn.hoavt_58.crazyexface.R;
import uet.kltn.hoavt_58.crazyexface.photo_collage.model.LayoutModel;
import uet.kltn.hoavt_58.crazyexface.photo_collage.statistic.Statistic;

/**
 * Created by Adm on 8/8/2016.
 */
public class AssetsUtil {
    private static final String TAG = AssetsUtil.class.getSimpleName();

    public static Bitmap getBitmapFromAssets(Context context, String fileName) {
        try {

            InputStream inputStream = context.getAssets().open(fileName);

            return BitmapFactory.decodeStream(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static String getFramePileByModel(LayoutModel model, int ratio) {
        if (model.name.contains("_a_")) {
            if (ratio == Statistic.COLLAGE_RATIO_11) {
                return "frames/pile/ratio11/a/pile_a_" + model.imgCount + ".png";
            } else if (ratio == Statistic.COLLAGE_RATIO_32) {
                return "frames/pile/ratio32/a/pile_a_" + model.imgCount + ".png";
            } else if (ratio == Statistic.COLLAGE_RATIO_916) {
                return "frames/pile/ratio916/a/pile_a_" + model.imgCount + ".png";
            }
        } else if (model.name.contains("_b_")) {
            if (ratio == Statistic.COLLAGE_RATIO_11) {
                return "frames/pile/ratio11/b/pile_b_" + model.imgCount + ".png";
            } else if (ratio == Statistic.COLLAGE_RATIO_32) {
                return "frames/pile/ratio32/b/pile_b_" + model.imgCount + ".png";
            } else if (ratio == Statistic.COLLAGE_RATIO_916) {
                return "frames/pile/ratio916/b/pile_b_" + model.imgCount + ".png";
            }
        } else if (model.name.contains("_c_")) {
            if (ratio == Statistic.COLLAGE_RATIO_11) {
                return "frames/pile/ratio11/c/pile_c_" + model.imgCount + ".png";
            } else if (ratio == Statistic.COLLAGE_RATIO_32) {
                return "frames/pile/ratio32/c/pile_c_" + model.imgCount + ".png";
            } else if (ratio == Statistic.COLLAGE_RATIO_916) {
                return "frames/pile/ratio916/c/pile_c_" + model.imgCount + ".png";
            }
        } else if (model.name.contains("_d_")) {
            if (ratio == Statistic.COLLAGE_RATIO_11) {
                return "frames/pile/ratio11/d/pile_d_" + model.imgCount + ".png";
            } else if (ratio == Statistic.COLLAGE_RATIO_32) {
                return "frames/pile/ratio32/d/pile_d_" + model.imgCount + ".png";
            } else if (ratio == Statistic.COLLAGE_RATIO_916) {
                return "frames/pile/ratio916/d/pile_d_" + model.imgCount + ".png";
            }
        }
        return "frames/pile/ratio11/a/pile_a_" + model.imgCount + ".png";
    }


    public static Bitmap getBitmapFromAssetsForPile(Context context, LayoutModel layoutModel, int ratio,
                                                    int reqWidth, int reqHeight, int maxTextureSize) {
//        Log.d("memory", "reqWidth: "+reqWidth+"_reqHeight: "+reqHeight+"_maxTextureSize: "+maxTextureSize);
        try {

//            Flog.d(TAG, "getBitmapFromAssetsForPile ratio: " + ratio);

            String pileName = getFramePileByModel(layoutModel, ratio);

//            Flog.d("pileName: " + pileName);

            BitmapFactory.Options options = new BitmapFactory.Options();
            InputStream inputStream = context.getAssets().open(pileName);
            options.inJustDecodeBounds = true;
            options.inSampleSize = BitmapDecoder.calculateInSampleSize(options, reqWidth, reqHeight, maxTextureSize);
//            options.inSampleSize = ImageUtils.getSampleSize(options, ImageUtils.MAX_IMAGE_SIZE);

            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            options.inMutable = true;
            Log.d("memory", "inSampleSize: "+options.inSampleSize);
            options.inJustDecodeBounds = false;
            inputStream.reset();
            return BitmapFactory.decodeStream(inputStream, null, options);
        } catch (IOException e) {
            e.printStackTrace();
        }catch (OutOfMemoryError e){
            SystemUtils.show(context, context.getString(R.string.out_of_memory_error));
            ((Activity)context).finish();
        }

        return null;
    }
}
