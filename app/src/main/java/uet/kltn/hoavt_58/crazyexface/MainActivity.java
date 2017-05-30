package uet.kltn.hoavt_58.crazyexface;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.microsoft.projectoxford.face.contract.Face;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import uet.kltn.hoavt_58.crazyexface.activities.DetectionActivity;
import uet.kltn.hoavt_58.crazyexface.activities.GalleryActivity;
import uet.kltn.hoavt_58.crazyexface.activities.GroupActivity;
import uet.kltn.hoavt_58.crazyexface.activities.SplashActivity;
import uet.kltn.hoavt_58.crazyexface.adapters.ServiceAdapter;
import uet.kltn.hoavt_58.crazyexface.helpers.AnimationUtils;
import uet.kltn.hoavt_58.crazyexface.helpers.ConstValue;
import uet.kltn.hoavt_58.crazyexface.helpers.Flog;
import uet.kltn.hoavt_58.crazyexface.helpers.ImageHelper;
import uet.kltn.hoavt_58.crazyexface.helpers.Utils;
import uet.kltn.hoavt_58.crazyexface.models.ServiceItem;
import uet.kltn.hoavt_58.crazyexface.photo_collage.MenuActivity;
import uet.kltn.hoavt_58.crazyexface.tasks.DetectionAsync;

public class MainActivity extends AppCompatActivity implements ServiceAdapter.OnItemClickLitener, DetectionAsync.OnDetectionListener {

    private static final int PHOTO_COLLAGE_INDEX = 0;
    private static final int EX_FACE_INDEX = 1;
    private static final int GROUP_BEST_PHOTO_INDEX = 2;
    private static final int REQUEST_CODE_CONTAINER_FRAG = 0x0034;
    private static final String TAG = MainActivity.class.getSimpleName();
    private ConnectionChangeReceiver changeReceiver;
    private IntentFilter filter;
    private int cntThreadDones = 0;
    private int mAllThreads = 0;
    public static ArrayList<Bitmap> mListThumbs = new ArrayList<>();
    public static String pathContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initReceivers();
        initViews();
    }

    private void initReceivers() {
        changeReceiver = new ConnectionChangeReceiver(this);
        filter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(changeReceiver, filter);
    }

    private void initViews() {
        ServiceAdapter faceListAdapter = new ServiceAdapter(initAppServices()).setListener(this);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false);

        RecyclerView rvServices = (RecyclerView) findViewById(R.id.rv_services);
        rvServices.setLayoutManager(layoutManager);
        rvServices.setAdapter(faceListAdapter);
    }

    private ArrayList<ServiceItem> initAppServices() {
        int[] arrIdRes = new int[]{R.drawable.ic_photocollage, R.drawable.ic_ex_face, R.drawable.ic_group_best_photo};
        String[] arrDescribe = new String[]{"Photo Collage", "Ex-face", "Group the best"};
        ArrayList<ServiceItem> items = new ArrayList<>();
        for (int i = 0; i < arrIdRes.length; i++) {
            ServiceItem item = new ServiceItem(arrIdRes[i], arrDescribe[i]);
            items.add(item);
        }
        return items;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (changeReceiver != null && filter != null)
            registerReceiver(changeReceiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (changeReceiver != null)
            unregisterReceiver(changeReceiver);
    }

    public void checkNetworkstate() {
        if (Utils.isNetworkEnabled(this)) {
            (findViewById(R.id.tv_app_name_animate)).setVisibility(View.GONE);
            (findViewById(R.id.tv_app_name_no_animate)).setVisibility(View.VISIBLE);
        } else {
            (findViewById(R.id.tv_app_name_animate)).setVisibility(View.VISIBLE);
            (findViewById(R.id.tv_app_name_no_animate)).setVisibility(View.GONE);
            AnimationUtils.startAnimation((TextView) findViewById(R.id.tv_app_name_animate));
            Toast.makeText(this, this.getString(R.string.network_required), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onItemClicked(int pos) {
        switch (pos) {
            case PHOTO_COLLAGE_INDEX:
                startActivity(new Intent(this, MenuActivity.class));
                break;
            case EX_FACE_INDEX:
                if (!Utils.isNetworkEnabled(this)) {
                    showNetworkDialog();
                    return;
                }
                Intent intent = new Intent(this, DetectionActivity.class);
                startActivity(intent);
                break;
            case GROUP_BEST_PHOTO_INDEX:
                if (!Utils.isNetworkEnabled(this)) {
                    showNetworkDialog();
                    return;
                }
                Intent galleryIntent = new Intent(this, GalleryActivity.class);
                startActivityForResult(galleryIntent, REQUEST_CODE_CONTAINER_FRAG);
                break;
            default:
                break;
        }
    }

    public void showNetworkDialog() {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
        builder.setTitle("Network is required");
        builder.setMessage(this.getString(R.string.network_required));
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CONTAINER_FRAG && resultCode == RESULT_OK && data != null) {
            String[] paths = data.getStringArrayExtra(GalleryActivity.EXTRA_OUTPUT_PATH_KEY);
            pathContext = paths[0];
            Flog.d(TAG, "size="+paths.length);
            int cnt = 0;
            for (int i = 0; i < paths.length; i++) {
                if (paths[i]!=null)
                    cnt++;
            }
            cntThreadDones = 0;
            mAllThreads = cnt;

            faceIdThumbnailMap.clear();
            for (int i = 0; i < paths.length; i++) {
                Flog.d(TAG, i+"_"+paths[i]);
                if (paths[i]!=null) {
                    Uri imageUri = Uri.fromFile(new File(paths[i]));
                    Bitmap bitmap = ImageHelper.loadSizeLimitedBitmapFromUri(
                            imageUri, getContentResolver());
                    ByteArrayOutputStream output = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);
                    ByteArrayInputStream inputStream = new ByteArrayInputStream(output.toByteArray());
                    DetectionAsync async = new DetectionAsync(this, i).setListener(this);
                    async.setOriginBitmap(bitmap);
                    async.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, inputStream);
                }
            }
        }
    }

    private ArrayList<Face> mFaces = new ArrayList<>();

    public static Map<UUID, Bitmap> faceIdThumbnailMap=new HashMap<>();
    List<UUID> faceIds = new ArrayList<>();
    @Override
    public void onDetectDone(int id, Face[] faces, boolean succeed, Bitmap bitmap) {
        cntThreadDones++;
        Flog.d(TAG, "id"+"="+id+"_size faces:"+faces.length+"_succeed="+succeed);
        for (Face face: faces) {
            try {
//                    mListThumbs.add(ImageHelper.generateFaceThumbnail(
//                            bitmap, face.faceRectangle));
                faceIds.add(face.faceId);
                faceIdThumbnailMap.put(face.faceId, ImageHelper.generateFaceThumbnail(
                        bitmap, face.faceRectangle));
            } catch (IOException e) {
                e.printStackTrace();
                continue;
            }

        }
        if (cntThreadDones==mAllThreads) {
            // notify detected done -> start activity.
            Flog.d(TAG, "detected done. All faces="+mFaces.size()
                +"_thumbs size="+mListThumbs.size());


//            for (Face face: mFaces) {
//                try {
////                    mListThumbs.add(ImageHelper.generateFaceThumbnail(
////                            bitmap, face.faceRectangle));
//                    faceIds.add(face.faceId);
//                    faceIdThumbnailMap.put(face.faceId, ImageHelper.generateFaceThumbnail(
//                            bitmap, face.faceRectangle));
//                } catch (IOException e) {
//                    e.printStackTrace();
//                    continue;
//                }
//
//            }
            Flog.d(TAG, "faceIds.size()="+faceIds.size());
            if (faceIds.size() > 0) {
                Intent intent = new Intent(this, GroupActivity.class);
                intent.putExtra(ConstValue.EXTRA_FACE_IDS, (Serializable) faceIds);
//                intent.putParcelableArrayListExtra(ConstValue.EXTRA_LIST_THUMBS, mListThumbs);
                startActivity(intent);
                finish();
            } else {
                AlertDialog.Builder builder =
                        new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
                builder.setMessage(getString(R.string.no_face_to_group));
                builder.setPositiveButton(getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.show();
            }
        }
    }
}
