package uet.kltn.hoavt_58.crazyexface.photo_collage;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.pic.libphotocollage.core.util.SystemUtils;

import java.io.File;
import java.util.ArrayList;

import uet.kltn.hoavt_58.crazyexface.R;
import uet.kltn.hoavt_58.crazyexface.photo_collage.adapter.gallery.ScreenSlidePagerAdapter;
import uet.kltn.hoavt_58.crazyexface.photo_collage.statistic.Statistic;
import uet.kltn.hoavt_58.crazyexface.photo_collage.utils.SettingsUtil;

/**
 * Created by hoavt on 23/11/2016.
 */
public class DisplayPhotoActivity extends BaseActivity {

    private static final String TAG = DisplayPhotoActivity.class.getSimpleName();
    public static final int FIRST_POS_ITEM = 0;
    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    private ViewPager mPager;
    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private PagerAdapter mPagerAdapter;
    private ArrayList<String> mPathsList = new ArrayList<>();
    private int mCurPos = FIRST_POS_ITEM;
    private File mPhotoFile = null;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_photo);

        SettingsUtil.setColorStatusBar(this, Color.LTGRAY);

        if (getIntent() == null) {
            return;
        }

        getIntentValues();
        initToolbar(mPhotoFile.getName());
        initViewPager();

        SettingsUtil.initAdmob(this, R.id.adView);
    }

    private void getIntentValues() {
        mPathsList = (ArrayList<String>) getIntent().getSerializableExtra(Statistic.EXTRA_PATH_PHOTO_LIST);
        mCurPos = getIntent().getIntExtra(Statistic.EXTRA_CURRENT_POSITION_STUDIO, FIRST_POS_ITEM);
        mPhotoFile = new File(mPathsList.get(mCurPos));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_studio, menu);
        for (int i = 0; i < menu.size(); i++) {
            SettingsUtil.setIconColor(this, menu, i);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement

        // update current file selected:
        upCurPosition();

        switch (id) {
            case R.id.action_delete:
                confirmDeletePhoto();
                return true;
            case R.id.action_share:
                SystemUtils.initSetAsOrShareViaLayout(this,
                        SystemUtils.makeShareViaIntent(Uri.fromFile(mPhotoFile)), true);
                return true;
            case R.id.menu_gallery:
                SystemUtils.showFileGallery(this, mPhotoFile);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void upCurPosition() {
        this.mCurPos = mPager.getCurrentItem();
        mPhotoFile = new File(mPathsList.get(mCurPos));
        mToolbar.setTitle(mPhotoFile.getName());
    }

    private void confirmDeletePhoto() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
        builder.setTitle(getString(R.string.dialog_title));
        builder.setMessage(getString(R.string.dialog_message));
        String positiveText = getString(android.R.string.ok);
        builder.setPositiveButton(positiveText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // positive button logic
                        if (mPhotoFile.delete()) {
                            SettingsUtil.delImage(getContentResolver(), mPhotoFile.getAbsolutePath());
                            SystemUtils.show(DisplayPhotoActivity.this, SystemUtils.getString(DisplayPhotoActivity.this, R.string.delete_photo_success));
                        } else {
                            SystemUtils.show(DisplayPhotoActivity.this, SystemUtils.getString(DisplayPhotoActivity.this, R.string.delete_photo_failed));
                        }

                        // Scan MediaStore again:
                        try {

                            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        setResult(RESULT_OK, getIntent().putExtra(Statistic.EXTRA_CURRENT_POSITION_STUDIO, mCurPos));
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
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void initViewPager() {
        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager(), mPathsList);
        mPager.setAdapter(mPagerAdapter);
        mPager.setCurrentItem(mCurPos);
    }

    protected void initToolbar(String fragmentTitleRes) {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle(fragmentTitleRes);
        mToolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        mToolbar.setNavigationIcon(R.drawable.ic_back);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public void upToolbar() {
        upCurPosition();
    }
}
