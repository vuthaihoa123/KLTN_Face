package uet.kltn.hoavt_58.crazyexface.photo_collage;

import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.pic.libphotocollage.core.util.SystemUtils;

import java.util.ArrayList;

import uet.kltn.hoavt_58.crazyexface.R;
import uet.kltn.hoavt_58.crazyexface.photo_collage.adapter.SettingsAdapter;
import uet.kltn.hoavt_58.crazyexface.photo_collage.model.ItemSetting;
import uet.kltn.hoavt_58.crazyexface.photo_collage.utils.LocalizedApp;
import uet.kltn.hoavt_58.crazyexface.photo_collage.utils.SettingsUtil;

/**
 * Created by hoavt on 09/07/2016.
 */
public class SettingsActivity extends BaseActivity implements SettingsAdapter.OnClickSettings {
    private static final int NUMBER_OF_COLS = 3;
    private RecyclerView mRvSettingsMenu;
    private LinearLayoutCompat btBackHome;
    private RecyclerView.Adapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        boolean isTablet = getResources().getBoolean(R.bool.isTablet);
        if (isTablet) {
            setTheme(R.style.Theme_Transparent_Tablet);
        } else {
            setTheme(R.style.Theme_Transparent);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_home_layout);
        if (getSupportActionBar()!=null)
            getSupportActionBar().hide();
        init();
        initViews();

        initNewApp();
    }

    private void init() {
        mRvSettingsMenu = (RecyclerView) findViewById(R.id.rv_list_settings);
        btBackHome = (LinearLayoutCompat) findViewById(R.id.bt_back_home);
    }

    private void initNewApp() {
        try {
            new LocalizedApp(this)
                    .setOnNewAppLoadedListener(new LocalizedApp.OnNewAppLoaded() {
                        @Override
                        public void onNewAppLoaded(final LocalizedApp.AppModel appModel) {
                            try {


                                findViewById(R.id.new_app_btn).setVisibility(View.VISIBLE);
                                final TextView tvAppTitle = (TextView) findViewById(R.id.new_app_title);
                                final ImageView tvAppImage = (ImageView) findViewById(R.id.new_app_icon);
                                tvAppTitle.setText(appModel.app_name);
                                Glide.with(SettingsActivity.this).load(appModel.image_url).
                                        skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE)
                                        .into(tvAppImage);

                                findViewById(R.id.new_app_btn).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        LocalizedApp.openAppOnStore(SettingsActivity.this, appModel.package_name);
                                    }
                                });
                            }catch (Exception e){
                                e.printStackTrace();
                            }

                        }
                    })
                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    private void initViews() {
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRvSettingsMenu.setHasFixedSize(true);

        // use a gridview layout manager
//        mLayoutManager = new GridLayoutManager(this, NUMBER_OF_COLS);
        mRvSettingsMenu.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mAdapter = new SettingsAdapter(this, initSettingsMenu()).setOnClickSettings(this);
        mRvSettingsMenu.setAdapter(mAdapter);

        btBackHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.right_to_left);
    }

    private ArrayList<ItemSetting> initSettingsMenu() {
        ArrayList<ItemSetting> settings = new ArrayList<>();
        settings.add(new ItemSetting(R.drawable.ic_share,
                SystemUtils.getColor(this, R.color.transparent),
                SystemUtils.getString(this, R.string.share_app)));
        settings.add(new ItemSetting(R.drawable.ic_facebook,
                SystemUtils.getColor(this, R.color.transparent),
                SystemUtils.getString(this, R.string.facebook_page)));
        settings.add(new ItemSetting(R.drawable.ic_twitter,
                SystemUtils.getColor(this, R.color.transparent),
                SystemUtils.getString(this, R.string.twitter)));
        settings.add(new ItemSetting(R.drawable.ic_feedback,
                SystemUtils.getColor(this, R.color.transparent),
                SystemUtils.getString(this, R.string.send_feedback)));
        settings.add(new ItemSetting(R.drawable.ic_about,
                SystemUtils.getColor(this, R.color.transparent),
                SystemUtils.getString(this, R.string.about)));
//        settings.add(new ItemSetting(-1,
//                SystemUtils.getColor(this, R.color.transparent),
//                null));
        return settings;
    }

    @Override
    public void onClickSettings(int pos) {
        switch (pos) {
            case 0: // Button share app
//                Toaster.show(SettingsActivity.this, "Share app");
                SettingsUtil.shareApp(this, getResources().getString(R.string.app_name));
                break;
            case 1: // Button facebook
//                Toaster.show(SettingsActivity.this, "Facebook");
                SettingsUtil.likeFacebookPage(this);
                break;
            case 2: // Button twitter
//                Toaster.show(SettingsActivity.this, "Twitter");
                SettingsUtil.tweetUs(this);
                break;
            case 3: // Button send feedback
//                Toaster.show(SettingsActivity.this, "Feedback");
                SettingsUtil.sendFeeback(this, getResources().getString(R.string.app_name), getResources().getString(R.string.bsoft_email));
                break;
            case 4: // Button about
                showAboutDialog();
//                SystemUtils.show(SettingsActivity.this, "About");
                break;
            default:
//                SystemUtils.show(SettingsActivity.this, "Nothing");
                break;
        }
    }

    private void showAboutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this, R.style.AppCompatAlertDialogStyle);
        builder.setTitle(getString(R.string.about_title_dialog));
        PackageInfo pInfo = null;
        String versionName = "";
        int versionCode = 0;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            versionName = pInfo.versionName;
            versionCode = pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        builder.setMessage(SystemUtils.getString(this, R.string.versionName) + ": " + versionName + "\n"
                + SystemUtils.getString(this, R.string.versionCode) + ": " + versionCode);

        String positiveText = getString(android.R.string.ok);
        builder.setPositiveButton(positiveText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // positive button logic
                        dialog.dismiss();
                    }
                });

        AlertDialog dialog = builder.create();
        // display dialog
        dialog.show();
    }
}
