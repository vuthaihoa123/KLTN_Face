package uet.kltn.hoavt_58.crazyexface.photo_collage;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.pic.libphotocollage.core.util.Flog;
import com.pic.libphotocollage.core.util.SystemUtils;

import java.io.File;

import uet.kltn.hoavt_58.crazyexface.R;
import uet.kltn.hoavt_58.crazyexface.photo_collage.statistic.Statistic;
import uet.kltn.hoavt_58.crazyexface.photo_collage.utils.SettingsUtil;

/**
 * Created by hoavt on 12/07/2016.
 */
public class SavePhotoActivity extends BaseActivity {
    public static final int ACTIVITY_SELECT_IMAGE = 1234;
    private static final String TAG = SavePhotoActivity.class.getSimpleName();
    private Toolbar mToolbar;
    private ImageView mIvSavedPhoto;
    private LinearLayoutCompat mIbSharePhoto;
    private LinearLayoutCompat mIbDelete;
    private File mFilePhoto;
    private View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.ib_share:
                    SystemUtils.initSetAsOrShareViaLayout(SavePhotoActivity.this,
                            SystemUtils.makeShareViaIntent(Uri.fromFile(mFilePhoto)), true);
                    break;
                case R.id.ib_del:
                    showDialogConfirmDel();
                    break;
                default:
                    SystemUtils.show(SavePhotoActivity.this, "Nothing");
                    break;
            }
        }
    };

    private void showDialogConfirmDel() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SavePhotoActivity.this, R.style.AppCompatAlertDialogStyle);
        builder.setTitle(getString(R.string.dialog_title));
        builder.setMessage(getString(R.string.dialog_message));

        String positiveText = getString(android.R.string.ok);
        builder.setPositiveButton(positiveText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // positive button logic
                        if (mFilePhoto.delete()) {
                            SettingsUtil.delImage(getContentResolver(), mFilePhoto.getAbsolutePath());
                            SystemUtils.show(SavePhotoActivity.this, SystemUtils.getString(SavePhotoActivity.this, R.string.delete_photo_success));
                        } else {
                            SystemUtils.show(SavePhotoActivity.this, SystemUtils.getString(SavePhotoActivity.this, R.string.delete_photo_failed));
                        }

                        // Scan MediaStore again:
                        try {

                            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        finish();
                    }
                });

        String negativeText = getString(android.R.string.cancel);
        builder.setNegativeButton(negativeText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // negative button logic
                        dialog.dismiss();
                    }
                });

        AlertDialog dialog = builder.create();
        // display dialog
        dialog.show();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivity_save_photo_layout);
        SettingsUtil.setColorStatusBar(this, Color.LTGRAY);

        init();
        mToolbar.setNavigationIcon(R.drawable.ic_back);
        setSupportActionBar(mToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(null);

        initViews();


        long timestamp = System.currentTimeMillis();
        if (timestamp % 2 == 0) {
            showRateDialog();
        }
    }

    private void init() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mIvSavedPhoto = (ImageView) findViewById(R.id.iv_photo_save);
        mIbSharePhoto = (LinearLayoutCompat) findViewById(R.id.ib_share);
        mIbDelete = (LinearLayoutCompat) findViewById(R.id.ib_del);
    }

    private void initViews() {
        if (getIntent() != null) {
            String path = getIntent().getStringExtra(Statistic.EXTRAS_PATH_SAVED_PHOTO);
            Flog.i("return intent: " + path);
            mFilePhoto = new File(path);
            Glide.with(this)
                    .load(mFilePhoto)
                    .fitCenter()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(mIvSavedPhoto);
        } else {
            Flog.i("Intent at SavePhotoActivity is null");
        }
        mIbSharePhoto.setOnClickListener(mClickListener);
        mIbDelete.setOnClickListener(mClickListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_save_layout, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int idItem = item.getItemId();
        switch (idItem) {
            case android.R.id.home:
                finish();
                break;
            case R.id.menu_gallery:
                SystemUtils.showFileGallery(this, mFilePhoto);
                break;
            case R.id.menu_studio:
//                getSupportFragmentManager().beginTransaction().add(R.id.content_main, MyStudioActivity.newFragment()).commit();
                startActivity(new Intent(this, MyStudioActivity.class));
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ACTIVITY_SELECT_IMAGE && resultCode == RESULT_OK) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String filePath = cursor.getString(columnIndex);
            cursor.close();
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(new File(filePath)), "image/*");
            startActivity(intent);
        }
    }


    private void buildAppRate() {
//        AppRate.with(this)
//                .setInstallDays(0) // default 10, 0 means install day.
//                .setLaunchTimes(1) // default 10
//                .setRemindInterval(99) // default 1
//                .setShowNeutralButton(true) // default true
//                .setDebug(false) // default false
//                .setOnClickButtonListener(new OnClickButtonListener() { // callback mInteractListener.
//                    @Override
//                    public void onClickButton(int which) {
//                        SavePhotoActivity.super.onBackPressed();
//                    }
//                })
//                .monitor();
    }

    private void showRateDialog() {
//        AppRate.with(this)
//                .setInstallDays(0) // default 10, 0 means install day.
//                .setLaunchTimes(1) // default 10
//                .setRemindInterval(99) // default 1
//                .setShowNeutralButton(true) // default true
//                .setDebug(false) // default false
//                .setOnClickButtonListener(new OnClickButtonListener() { // callback mInteractListener.
//                    @Override
//                    public void onClickButton(int which) {
////                        SavePhotoActivity.super.onBackPressed();
//                    }
//                })
//                .monitor();
//
//        if (AppRate.showRateDialogIfMeetsConditions(this)) {
//            //do nothing
//        }
    }


    // rate dialog
    @Override
    public void onBackPressed() {
//        buildAppRate();
//        if (AppRate.showRateDialogIfMeetsConditions(this)) {
//            //do nothing
//        } else {
//            super.onBackPressed();
//        }

    }
}
