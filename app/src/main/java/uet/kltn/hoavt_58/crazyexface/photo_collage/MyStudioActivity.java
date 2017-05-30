package uet.kltn.hoavt_58.crazyexface.photo_collage;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.pic.libphotocollage.core.util.SystemUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import uet.kltn.hoavt_58.crazyexface.R;
import uet.kltn.hoavt_58.crazyexface.photo_collage.adapter.gallery.MyStudioStorageAdapter;
import uet.kltn.hoavt_58.crazyexface.photo_collage.model.SavePhotoModelComparator;
import uet.kltn.hoavt_58.crazyexface.photo_collage.model.SavedPhotoModel;
import uet.kltn.hoavt_58.crazyexface.photo_collage.statistic.Statistic;
import uet.kltn.hoavt_58.crazyexface.photo_collage.utils.SettingsUtil;

/**
 * Created by Adm on 11/9/2016.
 */
public class MyStudioActivity extends BaseActivity implements MyStudioStorageAdapter.OnApplyRotation {

    private static final String TAG = MyStudioActivity.class.getSimpleName();
    private static final long TIME_STAMPS = 1000;
    private RecyclerView recyclerView = null;
    private MyStudioStorageAdapter adapter = null;
    private List<SavedPhotoModel> mPhotoModelList = new ArrayList<>();
    private Menu mMenu;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_studio);

        SettingsUtil.setColorStatusBar(this, Color.LTGRAY);

        initToolbarMystudio(getString(R.string.my_studio));
        initRecyclerView();

        SettingsUtil.initAdmob(this, R.id.adView);
    }

    public void loadImage(ContentResolver contentResolver) {
        mPhotoModelList.clear();
        Cursor cursor = contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media.TITLE, MediaStore.Images.Media.DATA, MediaStore.Images.Media.SIZE
                        , MediaStore.Images.Media.DATE_ADDED}
                , MediaStore.Images.Media.DESCRIPTION + " = '" + Statistic.ALBUM + "' ", null, null);
        if (cursor == null) return;
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String title = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.TITLE));
            String data = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            int size = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media.SIZE));
            /* The dateTaken, without the zeros*/
            long date = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED)) * TIME_STAMPS;
            mPhotoModelList.add(new SavedPhotoModel(title, data, size, date));
            cursor.moveToNext();
        }
        if (cursor != null)
            cursor.close();
    }

    private void initRecyclerView() {

        loadImage(getContentResolver());

        Collections.sort(mPhotoModelList, new SavePhotoModelComparator());
        adapter = new MyStudioStorageAdapter(this, mPhotoModelList).setFlippedListener(this);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        if (mPhotoModelList.size() <= 0) {
            SystemUtils.show(MyStudioActivity.this, getString(R.string.noti_empty_studio));
        }
    }

    protected void initToolbarMystudio(String fragmentTitleRes) {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle(fragmentTitleRes);
        mToolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        mToolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
        mToolbar.setNavigationIcon(R.drawable.ic_back);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (adapter.getItemSelected() == 0) {
            super.onBackPressed();
            finish();
        } else {
            mToolbar.setNavigationIcon(R.drawable.ic_back);
            mToolbar.setTitle(getString(R.string.my_studio));
            mToolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
            toggleSelectedItems(false);
            mToolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
            adapter.refreshFlipped();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_studio, menu);
        mMenu = menu;
        mMenu.findItem(R.id.menu_gallery).setVisible(false);
        toggleSelectedItems(false);
        return super.onCreateOptionsMenu(menu);
    }

    public void toggleSelectedItems(boolean visible) {
        MenuItem itemDelete = mMenu.findItem(R.id.action_delete);
        MenuItem itemShare = mMenu.findItem(R.id.action_share);
        itemDelete.setVisible(visible);
        itemShare.setVisible(visible);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_delete:
                confirmDeletePhotos();
                return true;
            case R.id.action_share:
                ArrayList<Uri> uriShare = new ArrayList<>();
                for (SavedPhotoModel model : mPhotoModelList) {
                    if (!model.isFirstImage()) {
                        File file = new File(model.getData());
                        uriShare.add(Uri.fromFile(file));
                    }
                }
                startActivity(Intent.createChooser(SystemUtils.makeShareViaIntent(uriShare),
                        getString(com.pic.libphotocollage.core.R.string.share_via)));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void confirmDeletePhotos() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
        builder.setTitle(getString(R.string.dialog_title));
        builder.setMessage(getString(R.string.dialog_message));
        String positiveText = getString(android.R.string.ok);
        builder.setPositiveButton(positiveText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // positive button logic
                        int countSuccess = 0;
                        int countFailed = 0;
                        for (int i = mPhotoModelList.size() - 1; i >= 0; i--) {
                            SavedPhotoModel model = mPhotoModelList.get(i);
                            if (!model.isFirstImage()) {
                                File file = new File(model.getData());
                                if (file.exists() && file.delete()) {
                                    countSuccess++;
                                    SettingsUtil.delImage(getContentResolver(), file.getAbsolutePath());
                                    // Scan MediaStore again:
                                    try {
                                        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE));
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        continue;
                                    }
                                    mPhotoModelList.remove(i);
                                } else {
                                    countFailed++;
                                }
                            }
                        }
                        adapter.setSavedImageList(mPhotoModelList);
                        adapter.notifyDataSetChanged();
                        updateToolbar();
                        SystemUtils.show(MyStudioActivity.this,
                                (countSuccess != 0) ?
                                        (getResources().getQuantityString(R.plurals.numOfDeletedPhotos, countSuccess, countSuccess)) :
                                        "" + ((countFailed != 0) ? (countFailed + " " + getString(R.string.delete_photo_failed)) : ""));
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
    public void onFliped() {
        updateToolbar();
    }

    private void updateToolbar() {
        int selected = adapter.getItemSelected();
        if (selected == 0) {
            mToolbar.setTitle(getString(R.string.my_studio));
            mToolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
            toggleSelectedItems(false);
            mToolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
            mToolbar.setNavigationIcon(R.drawable.ic_back);
        } else {
            mToolbar.setTitle("" + selected);
            mToolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white));
            toggleSelectedItems(true);
            mToolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
            mToolbar.setNavigationIcon(R.drawable.ic_back_toogle);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == Statistic.REQUEST_CODE_DISPLAY_PHOTO_ACTIVITY) {
            int pos = data.getExtras().getInt(Statistic.EXTRA_CURRENT_POSITION_STUDIO, DisplayPhotoActivity.FIRST_POS_ITEM);
            mPhotoModelList.remove(pos);
            adapter.setSavedImageList(mPhotoModelList);
            adapter.notifyDataSetChanged();
            updateToolbar();
        }
    }
}
