package uet.kltn.hoavt_58.crazyexface.photo_collage.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.File;

import uet.kltn.hoavt_58.crazyexface.photo_collage.statistic.Statistic;

/**
 * Created by Adm on 11/9/2016.
 */
public class PhotoUtils {

    private static String normalizeFileName(String name) {
        int pos = name.lastIndexOf(".");
        if (pos != -1) {
            return name.substring(0, pos);
        }
        return name;
    }

    public static int addToLibrary(Context mContext, String imagePath) {
        ContentValues values = new ContentValues(9);


        values.put(MediaStore.Images.Media.TITLE, normalizeFileName(new File(imagePath).getName()));
        values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/*");
        values.put(MediaStore.Images.Media.DATA, imagePath);
        values.put(MediaStore.Images.Media.DESCRIPTION, Statistic.ALBUM);

        ContentResolver contentResolver = mContext.getContentResolver();
        Uri base = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        Uri newUri = contentResolver.insert(base, values);
        // Notifiy the media application on the device
        mContext.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, newUri));

        int returnCode = 0;
        try {
            assert newUri != null;
            returnCode = Integer.parseInt(newUri.getLastPathSegment());
        } catch (Exception e) {
            returnCode = -1;
        }
        return returnCode;
    }
}
