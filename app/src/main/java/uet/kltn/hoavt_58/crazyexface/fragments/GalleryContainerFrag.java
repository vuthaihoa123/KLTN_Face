package uet.kltn.hoavt_58.crazyexface.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uet.kltn.hoavt_58.crazyexface.R;
import uet.kltn.hoavt_58.crazyexface.adapters.GalleryAdapter;
import uet.kltn.hoavt_58.crazyexface.helpers.Flog;
import uet.kltn.hoavt_58.crazyexface.helpers.Utils;
import uet.kltn.hoavt_58.crazyexface.interfaces.OnGalleryListener;
import uet.kltn.hoavt_58.crazyexface.models.PhotoModel;

/**
 * Created by Adm on 8/10/2016.
 */
public class GalleryContainerFrag extends Fragment {
    public static final int NUMBER_GRID_CNT = 4;
    private static final String TAG = GalleryContainerFrag.class.getSimpleName();
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 0x69;
    public static List<PhotoModel> allPhotoList = null;
    public static Map<String, List<PhotoModel>> photoMap = null;
    public static List<String> sortedFolderList = null;
    private RecyclerView recyclerView = null;
    private GalleryAdapter adapter = null;
    private List<PhotoModel> photoList = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.content_gallery_view, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

                // MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            initList();
            initRecycleView();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    initList();
                    initRecycleView();
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private void initList() {
        photoList = new ArrayList<>();

        if (allPhotoList == null) {
            allPhotoList = new ArrayList<>();
        }

        if (photoMap == null) {
            photoMap = new HashMap<>();
        }

        if (sortedFolderList == null) {
            sortedFolderList = new ArrayList<>();
        }

        photoList.addAll(allPhotoList);

        if (allPhotoList.size() <= 0 || allPhotoList.isEmpty()) {
            new AsyncTask<Void, Void, List<PhotoModel>>() {
                @Override
                protected List<PhotoModel> doInBackground(Void... params) {

                    List<PhotoModel> list = getAllShownImagesPath(getActivity());

                    Collections.sort(sortedFolderList, new Comparator<String>() {
                        @Override
                        public int compare(String lhs, String rhs) {
                            return lhs.compareTo(rhs);
                        }
                    });

                    allPhotoList.addAll(list);

                    return list;
                }

                @Override
                protected void onPostExecute(List<PhotoModel> photoModels) {
                    super.onPostExecute(photoModels);
                    Flog.d(TAG, "map size: " + photoMap.size());


                    if (getActivity() instanceof OnGalleryListener) {
                        ((OnGalleryListener) getActivity()).onGalleryLoaded(sortedFolderList);
                    }

                    photoList.clear();
                    photoList.addAll(photoModels);
                    adapter.notifyDataSetChanged();
                }
            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    private void initRecycleView() {
        adapter = new GalleryAdapter(getContext(), photoList).setOnGalleryListener((OnGalleryListener) getActivity());
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), NUMBER_GRID_CNT);
        recyclerView = (RecyclerView) getView().findViewById(R.id.recycle_view);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(adapter);
    }

    private ArrayList<PhotoModel> getAllShownImagesPath(Activity activity) {
        Uri uri;
        Cursor cursor;
        int column_index_data, column_index_folder_name;
        ArrayList<PhotoModel> listOfAllImages = new ArrayList<PhotoModel>();
        String absolutePathOfImage = null;
        uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {MediaStore.MediaColumns.DATA,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME, MediaStore.Images.Media.DATE_MODIFIED};

        cursor = activity.getContentResolver().query(uri, projection, null,
                null, MediaStore.Images.Media.DATE_MODIFIED + " DESC");
        if (cursor == null)
            return listOfAllImages;

        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        column_index_folder_name = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
        while (cursor.moveToNext()) {
            PhotoModel model = new PhotoModel();
            absolutePathOfImage = cursor.getString(column_index_data);

            model.folderName = cursor.getString(column_index_folder_name);
            model.imgPath = absolutePathOfImage;

            File tmpFile = new File(absolutePathOfImage);

            // khanh fix crash from developer console, check name != null before use
            if (tmpFile.exists() && tmpFile.getName() != null
                    && Utils.getExtension(tmpFile.getName()) != null
                    && !Utils.getExtension(tmpFile.getName()).equalsIgnoreCase("GIF")) {

            } else {
                continue;
            }

//            if (!tmpFile.exists() || SettingsUtil.getExtension(tmpFile.getName()).equalsIgnoreCase("GIF"))
//                continue;


            List<PhotoModel> tmpList = photoMap.get(model.folderName);
            if (tmpList == null) {
                tmpList = new ArrayList<>();
                tmpList.add(model);
                photoMap.put(model.folderName, tmpList);

                sortedFolderList.add(model.folderName);

//                FLog.d(TAG, "folder name: " + model.folderName);
            } else {
                tmpList.add(model);
            }

            listOfAllImages.add(model);
        }
        if (cursor != null)
            cursor.close();
        return listOfAllImages;
    }

    public void reloadRecycleView() {
        photoList.clear();
        photoList.addAll(allPhotoList);
        adapter.notifyDataSetChanged();
    }

    public void reloadRecycleView(String key) {
        photoList.clear();
        photoList.addAll(photoMap.get(key));
        adapter.notifyDataSetChanged();
    }
}
