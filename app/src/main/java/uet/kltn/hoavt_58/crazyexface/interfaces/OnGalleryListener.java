package uet.kltn.hoavt_58.crazyexface.interfaces;

import android.view.View;

import java.util.List;

import uet.kltn.hoavt_58.crazyexface.models.PhotoModel;

/**
 * Created by Adm on 8/10/2016.
 */
public interface OnGalleryListener {
    public void onPhotoPickListener(PhotoModel photoModel, View sourceView);

    public void onGalleryLoaded(List<String> sortedFolderList);
}
