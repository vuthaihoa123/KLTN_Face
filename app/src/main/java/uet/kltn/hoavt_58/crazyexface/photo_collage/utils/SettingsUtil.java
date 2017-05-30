package uet.kltn.hoavt_58.crazyexface.photo_collage.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.io.File;
import java.util.ArrayList;

import uet.kltn.hoavt_58.crazyexface.R;
import uet.kltn.hoavt_58.crazyexface.photo_collage.model.SavedPhotoModel;
import uet.kltn.hoavt_58.crazyexface.photo_collage.statistic.Statistic;

/**
 * Created by Adm on 7/11/2016.
 */
public class SettingsUtil {

    public static final String MORE_APP_PCK = "com.aes.secretvideorecorder";
    public static ArrayList<SavedPhotoModel> mediaStores;

    public static String getExtension(String fileName) {
        String extension = null;

        int i = fileName.lastIndexOf('.');
        int p = Math.max(fileName.lastIndexOf('/'), fileName.lastIndexOf('\\'));

        if (i > p) {
            extension = fileName.substring(i + 1);
        }
        return extension;
    }

    public static void openAppOnStore(Context context, String pck) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("market://details?id=" + pck));
        context.startActivity(intent);
    }

    public static void rateApp(Context context) {
        openAppOnStore(context, context.getPackageName());
    }

    public static void likeFacebookPage(Context context) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/807274629334722"));
            context.startActivity(intent);
        } catch (Exception e) {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.facebook.com/clip.cold")));
        }
    }

    public static void tweetUs(Context context) {
        Intent intent = null;
        try {
            // get the Twitter app if possible
            context.getPackageManager().getPackageInfo("com.twitter.android", 0);
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?user_id=709214944496001024"));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        } catch (Exception e) {
            // no Twitter app, revert to browser
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/BSoft16"));
        }
        context.startActivity(intent);
    }

    public static void sendFeeback(Context context, String appName, String dstEmail) {
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", dstEmail, null));
        intent.putExtra(Intent.EXTRA_SUBJECT, appName + ": Feedback");

        context.startActivity(Intent.createChooser(intent, "Feedback"));


    }

    public static void shareApp(Context context, String appName) {

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT,
                appName +
                        ": https://play.google.com/store/apps/details?id=" + context.getPackageName());
        sendIntent.setType("text/plain");
        context.startActivity(Intent.createChooser(sendIntent, "Share"));

    }

    public static void setIconColor(Context context, Menu menu, int pos) {
        Drawable newIcon = menu.getItem(pos).getIcon();
        newIcon.mutate().setColorFilter(ContextCompat.getColor(context, R.color.colorPrimaryDark), PorterDuff.Mode.SRC_IN);
        menu.getItem(pos).setIcon(newIcon);
    }

    public static void initAdmob(Activity activity, int resId) {
        AdView ddView = (AdView) activity.findViewById(resId);
        AdRequest adRequest = new AdRequest.Builder().build();
        ddView.loadAd(adRequest);
    }

    public static Uri addToLibrary(ContentResolver contentResolver, Context mContext, String imagePath) {
        ContentValues values = new ContentValues(6);
        values.put(MediaStore.Images.Media.TITLE, new File(imagePath).getName());
        values.put(MediaStore.Images.Media.SIZE, (new File(imagePath).length()));
        values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.DESCRIPTION, Statistic.ALBUM);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/*");
        values.put(MediaStore.Images.Media.DATA, imagePath);
        Uri base = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        Uri newUri = null;
        try {
            newUri = contentResolver.insert(base, values);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mContext.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, newUri));
        return newUri;
    }

    public static void loadImage(ContentResolver contentResolver){
        mediaStores = new ArrayList<>();
        Cursor cursor = contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media.TITLE,MediaStore.Images.Media.DATA,MediaStore.Images.Media.SIZE
                        ,MediaStore.Images.Media.DATE_ADDED}
                , MediaStore.Images.Media.DESCRIPTION + " = '"+ Statistic.ALBUM+"' ", null, MediaStore.Images.Media.DATE_ADDED + " DESC");
        if(cursor == null) return;
        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            String title = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.TITLE));
            String data = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            int size = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media.SIZE));
            int date = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED));
            mediaStores.add(new SavedPhotoModel(title,data,size,date));
            cursor.moveToNext();
        }
        if (cursor != null)
            cursor.close();
    }

    public static void delImage(ContentResolver contentResolver, String filePath){
        int c = contentResolver.delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, MediaStore.Images.Media.DATA + "=?", new String[] {filePath});
        Log.d("1111", "del rows="+c);
    }

    public static void setColorStatusBar(Activity activity, int color) {
        Window window = activity.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(color);
        } else {
            // must first enable translucency in your Activity - either by using
            // or inheriting from one of the various *.TranslucentDecor themes
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // create our manager instance after the content view is set
            SystemBarTintManager tintManager = new SystemBarTintManager(activity);
            // enable status bar tint
            tintManager.setStatusBarTintEnabled(true);
            // enable navigation bar tint
            tintManager.setNavigationBarTintEnabled(true);
            // set a custom tint color for all system bars
            tintManager.setTintColor(color);
        }
    }
}
