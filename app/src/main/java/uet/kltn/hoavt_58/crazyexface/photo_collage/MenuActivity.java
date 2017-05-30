package uet.kltn.hoavt_58.crazyexface.photo_collage;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.View;

import com.pic.libphotocollage.core.util.SystemUtils;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import uet.kltn.hoavt_58.crazyexface.R;
import uet.kltn.hoavt_58.crazyexface.photo_collage.adapter.OnItemLayoutListener;
import uet.kltn.hoavt_58.crazyexface.photo_collage.model.AppModel;
import uet.kltn.hoavt_58.crazyexface.photo_collage.model.LayoutModel;
import uet.kltn.hoavt_58.crazyexface.photo_collage.statistic.Statistic;
import uet.kltn.hoavt_58.crazyexface.photo_collage.utils.LocalizedApp;
import uet.kltn.hoavt_58.crazyexface.photo_collage.utils.SettingsUtil;

/**
 * Created by Adm on 8/3/2016.
 */
public class MenuActivity extends BaseActivity implements Runnable, OnItemLayoutListener {

    private static final String TAG = MenuActivity.class.getSimpleName();
    private LinearLayoutCompat mBtRateApp, mBtMyStudio, mBtMoreApps, mBtGift, mBtTemplate, mBtNavi;
    private View.OnClickListener mListennerBtnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.btn_nav_bar:
//                    Toast.makeText(MenuActivity.this, "NavigationBar", Toast.LENGTH_SHORT).show();
                    openSettingsMenu();
                    break;
                case R.id.btn_more_apps:
                case R.id.btn_gift_app:
//                    SystemUtils.openAppOnStore(MenuActivity.this, "com.bsoft.reversevideo");
                    ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                    if (cm.getActiveNetworkInfo() != null) {
                        gitfApp(MenuActivity.this);
                    } else {
                        startActivity(
                                new Intent(Intent.ACTION_VIEW,
                                        Uri.parse("https://play.google.com/store/apps/details?id=com.pic.photocollage")));
                    }

                    break;
                case R.id.btn_templates:
                    Random rd = new Random();
                    int[] codes = new int[]{Statistic.GRID_LAYOUT_CODE, Statistic.PILE_LAYOUT_CODE};

                    List<LayoutModel> list = null;
                    LayoutModel model = null;
                    int[] listRd = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24};
                    int idx = rd.nextInt(listRd.length) + 0;
                    int rdIndex = 0;
                    if (listRd[idx] % 2 == 0) {
                        rdIndex = 1;
                    } else {
                        rdIndex = 0;
                    }
                    int layoutCode = codes[rdIndex];
                    if (layoutCode == Statistic.GRID_LAYOUT_CODE) {
                        list = Statistic.GRID_LAYOUT;
                    } else {
                        list = Statistic.PILE_LAYOUT;
                    }

                    rdIndex = rd.nextInt(list.size()) + 0;
                    model = list.get(rdIndex);
                    Intent intent = new Intent(MenuActivity.this, MainEditorActivity.class);
                    intent.putExtra("LAYOUT_CODE", layoutCode);
                    intent.putExtra("LAYOUT_MODEL", model);
                    startActivity(intent);

                    break;
                case R.id.btn_rate_app:
                    SystemUtils.rateApp(MenuActivity.this);
                    break;
                case R.id.btn_my_studio:
                    showMyStudio();
                    break;
//                case R.id.btn_more_apps:
//                    openSettingsMenu();
//                    break;
            }
        }
    };

    public static boolean isPackageInstalled(String packageName, PackageManager packageManager) {
        try {
            packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    private void showMyStudio() {
        Intent intent = new Intent(MenuActivity.this, FunctionActivity.class);
        intent.putExtra(FunctionActivity.FRAGMENT_HOME, FunctionActivity.FOLDER);
        startActivity(intent);
    }

    private void gitfApp(final Context context) {
        try {
            new AsyncTask<Void, Void, Void>() {
                ProgressDialog progressDialog = new ProgressDialog(context, R.style.CircularProgress);

                @Override
                protected Void doInBackground(Void... params) {
                    URL url = null;
                    try {
                        url = new URL(Statistic.GIFT_APP);
                        URLConnection urlConnection = null;
                        try {
                            urlConnection = url.openConnection();
                            BufferedReader reader = null;
                            try {
                                reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                                String line = "";
                                List<AppModel> listApps = new ArrayList<>();
                                try {
                                    while ((line = reader.readLine()) != null) {
                                        try {
                                            JSONObject jsonObject = new JSONObject(line);
                                            AppModel app = new AppModel();
                                            app.setPriority(jsonObject.getInt("prio"));
                                            app.setTitle(jsonObject.getString("title"));
                                            app.setId(jsonObject.getString("id"));
                                            if (app.getId().equalsIgnoreCase(context.getPackageName()) ||
                                                    isPackageInstalled(app.getId(), context.getPackageManager())) {
                                                continue;
                                            }
                                            listApps.add(app);
                                        } catch (Exception ex) {
                                            ex.printStackTrace();
                                        }
                                    }

                                    Collections.sort(listApps, new Comparator<AppModel>() {
                                        @Override
                                        public int compare(AppModel lhs, AppModel rhs) {
                                            return (rhs.getPriority() - lhs.getPriority());
                                        }
                                    });

                                    if (!listApps.isEmpty()) {
                                        openApp(context, listApps.get(0).getId());
                                    } else {
                                        openApp(context, context.getPackageName());
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    progressDialog.setMessage(getResources().getString(R.string.please));
                    progressDialog.show();
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                    progressDialog.dismiss();
                }

                @Override
                protected void onProgressUpdate(Void... values) {
                    super.onProgressUpdate(values);
                }

            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openApp(Context context, String packageName) {
        Uri uri = Uri.parse(Statistic.MAKET + packageName);
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET
                | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            context.startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Statistic.LINK_GIFT_APP + packageName)));
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_home);
        SettingsUtil.setColorStatusBar(this, Color.parseColor("#7B00D4"));

        initViews();

//        getSupportFragmentManager().beginTransaction().add(R.id.layout_grid, new GridContainerFrag()).commit();
//        getSupportFragmentManager().beginTransaction().add(R.id.layout_pile, new PileContainerFrag()).commit();
    }

    private void initViews() {
//        mBtRateApp = (LinearLayoutCompat) findViewById(R.id.btn_rate_app);
//        mBtRateApp.setOnClickListener(mListennerBtnClick);
//        mBtMyStudio = (LinearLayoutCompat) findViewById(R.id.btn_my_studio);
//        mBtMyStudio.setOnClickListener(mListennerBtnClick);
//        mBtMoreApps = (LinearLayoutCompat) findViewById(R.id.btn_more_apps);
//        mBtMoreApps.setOnClickListener(mListennerBtnClick);
//        mBtSettings = (LinearLayoutCompat) findViewById(R.id.btn_settings);
//        mBtSettings.setOnClickListener(mListennerBtnClick);

        mBtNavi = (LinearLayoutCompat) findViewById(R.id.btn_nav_bar);
        mBtNavi.setOnClickListener(mListennerBtnClick);
        mBtGift = (LinearLayoutCompat) findViewById(R.id.btn_gift_app);
        mBtGift.setOnClickListener(mListennerBtnClick);
        mBtTemplate = (LinearLayoutCompat) findViewById(R.id.btn_templates);
        mBtTemplate.setOnClickListener(mListennerBtnClick);
        mBtMyStudio = (LinearLayoutCompat) findViewById(R.id.btn_my_studio);
        mBtMyStudio.setOnClickListener(mListennerBtnClick);
        mBtRateApp = (LinearLayoutCompat) findViewById(R.id.btn_rate_app);
        mBtRateApp.setOnClickListener(mListennerBtnClick);
        mBtMoreApps = (LinearLayoutCompat) findViewById(R.id.btn_more_apps);
        mBtMoreApps.setOnClickListener(mListennerBtnClick);
    }

    @Override
    public void run() {
    }

    @Override
    public void onItemLayoutClickListener(LayoutModel model, int layoutCode) {

        Intent intent = new Intent(this, MainEditorActivity.class);
        intent.putExtra("LAYOUT_CODE", layoutCode);
        intent.putExtra("LAYOUT_MODEL", model);

        startActivity(intent);
    }

    private void openSettingsMenu() {
        Intent intentSettings = new Intent(this, SettingsActivity.class);
        startActivity(intentSettings);
        /**
         * exit animation overide onFinish() of destination activity.
         * */
        overridePendingTransition(R.anim.left_to_right, 0);
    }

    @Override
    public void onBackPressed() {
//        showRateDialog();
//        if (AppRate.showRateDialogIfMeetsConditions(this)) {
//            //do nothing
//        } else {
            super.onBackPressed();
//            finishAffinity();
//        }
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
//                        MenuActivity.super.onBackPressed();
//                        finishAffinity();
//                    }
//                })
//                .monitor();
    }
}
