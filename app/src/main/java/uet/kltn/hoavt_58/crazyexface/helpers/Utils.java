package uet.kltn.hoavt_58.crazyexface.helpers;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by hoavt_58 on 4/9/17.
 */

public class Utils {

    private static final String TAG = Utils.class.getSimpleName();

    public static boolean isNetworkEnabled(Context context) {
        try {
            ConnectivityManager nInfo = (ConnectivityManager) context.getSystemService(
                    Context.CONNECTIVITY_SERVICE);
            nInfo.getActiveNetworkInfo().isConnectedOrConnecting();

            Flog.d(TAG, "Net avail:" + nInfo.getActiveNetworkInfo().isConnectedOrConnecting());

            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(
                    Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnectedOrConnecting()) {

                Flog.d(TAG, "Network available:true");
                return true;
            } else {

                Flog.d(TAG, "Network available:false");
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    public static String getExtension(String fileName) {
        String extension = null;

        int i = fileName.lastIndexOf('.');
        int p = Math.max(fileName.lastIndexOf('/'), fileName.lastIndexOf('\\'));

        if (i > p) {
            extension = fileName.substring(i + 1);
        }
        return extension;
    }

    public static int dpToPx(Context context, int dp) {
        int px = Math.round(dp * getPixelScaleFactor(context));
        return px;
    }

    private static float getPixelScaleFactor(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    public static void saveImageToGallery(Context context, Bitmap bitmap) {

        FileOutputStream outStream = null;

        try {
            File sdCard = Environment.getExternalStorageDirectory();
            File dir = new File(sdCard.getAbsolutePath() + "/FaceSwap");
            dir.mkdirs();

            String fileName = String.format("face_swap_%d.jpg", System.currentTimeMillis());
            File outFile = new File(dir, fileName);

            outStream = new FileOutputStream(outFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outStream);

            outStream.close();
            String imageResultPath = outFile.getAbsolutePath();


            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            mediaScanIntent.setData(Uri.fromFile(outFile));
            context.sendBroadcast(mediaScanIntent);
            Toast.makeText(context, "Save successfully to " + imageResultPath, Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            //Toast.makeText(this, "something went wrong please try again", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            //Toast.makeText(this,"something went wrong please try again",Toast.LENGTH_SHORT).show();
        }

    }
}
