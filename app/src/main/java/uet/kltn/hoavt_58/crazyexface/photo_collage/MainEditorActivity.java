package uet.kltn.hoavt_58.crazyexface.photo_collage;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.pic.libphotocollage.core.CollageView;
import com.pic.libphotocollage.core.pref.LibPref;
import com.pic.libphotocollage.core.util.Flog;

import java.io.File;
import java.util.ArrayList;

import uet.kltn.hoavt_58.crazyexface.R;
import uet.kltn.hoavt_58.crazyexface.photo_collage.adapter.PhotoEditorAdapter;
import uet.kltn.hoavt_58.crazyexface.photo_collage.adapter.StickerEditorAdapter;
import uet.kltn.hoavt_58.crazyexface.photo_collage.adapter.listener.OnCloseFragListener;
import uet.kltn.hoavt_58.crazyexface.photo_collage.adapter.listener.OnCollageMenuListener;
import uet.kltn.hoavt_58.crazyexface.photo_collage.adapter.listener.OnItemMenuListener;
import uet.kltn.hoavt_58.crazyexface.photo_collage.adapter.listener.OnMenuBackgroundListener;
import uet.kltn.hoavt_58.crazyexface.photo_collage.adapter.listener.OnMenuFrameListener;
import uet.kltn.hoavt_58.crazyexface.photo_collage.adapter.listener.OnMenuMarginListener;
import uet.kltn.hoavt_58.crazyexface.photo_collage.adapter.listener.OnMenuRatioListener;
import uet.kltn.hoavt_58.crazyexface.photo_collage.adapter.listener.OnOpacityStickerListener;
import uet.kltn.hoavt_58.crazyexface.photo_collage.adapter.text.TextFontAdapter;
import uet.kltn.hoavt_58.crazyexface.photo_collage.fragment.CollageContainerFrag;
import uet.kltn.hoavt_58.crazyexface.photo_collage.fragment.FooterMenuFragment;
import uet.kltn.hoavt_58.crazyexface.photo_collage.fragment.editor.PhotoEditorFrag;
import uet.kltn.hoavt_58.crazyexface.photo_collage.fragment.editor.StickerEditorFrag;
import uet.kltn.hoavt_58.crazyexface.photo_collage.fragment.editor.StickerOpacityFrag;
import uet.kltn.hoavt_58.crazyexface.photo_collage.fragment.menu.MenuLayoutFrag;
import uet.kltn.hoavt_58.crazyexface.photo_collage.fragment.menu.MenuOptionOthersFrag;
import uet.kltn.hoavt_58.crazyexface.photo_collage.fragment.menu.MenuStickerFrag;
import uet.kltn.hoavt_58.crazyexface.photo_collage.fragment.menu.MenuTextFrag;
import uet.kltn.hoavt_58.crazyexface.photo_collage.fragment.texttabs.TextBaseFragment;
import uet.kltn.hoavt_58.crazyexface.photo_collage.fragment.texttabs.TextSettingFragment;
import uet.kltn.hoavt_58.crazyexface.photo_collage.model.FrameModel;
import uet.kltn.hoavt_58.crazyexface.photo_collage.model.LayoutModel;
import uet.kltn.hoavt_58.crazyexface.photo_collage.model.PhotoModel;
import uet.kltn.hoavt_58.crazyexface.photo_collage.model.StickerModel;
import uet.kltn.hoavt_58.crazyexface.photo_collage.statistic.Statistic;
import uet.kltn.hoavt_58.crazyexface.photo_collage.utils.FLog;
import uet.kltn.hoavt_58.crazyexface.photo_collage.utils.SettingsUtil;

/**
 * Created by Adm on 8/3/2016.
 */
public class MainEditorActivity extends BaseActivity implements OnCollageMenuListener, OnItemMenuListener, OnMenuBackgroundListener, OnCloseFragListener,
        OnMenuRatioListener, OnOpacityStickerListener, OnMenuMarginListener, TextBaseFragment.OnTextOptionCallback, TextFontAdapter.OnTextFontListener,
        TextSettingFragment.OnTextSettingListener, OnMenuFrameListener, PhotoEditorAdapter.OnPhotoEditorListener, StickerEditorAdapter.OnStickerEditorListener, View.OnClickListener {
    private static final String TAG = MainEditorActivity.class.getSimpleName();
    public static boolean RATIO_GRID_FLAG = false;
    private Toolbar mToolbar = null;
    //TODO: LayoutModel should put to Bundle of fragment
    private LayoutModel model;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_editor_home);

        SettingsUtil.setColorStatusBar(this, Color.LTGRAY);

        model = getIntent().getParcelableExtra("LAYOUT_MODEL");
        if (model == null)
            return;
        RATIO_GRID_FLAG = model.isGrid;

        initPrefs();
        initToolbar();
        initFragment();

        SettingsUtil.initAdmob(this, R.id.adView);
    }

    private void initPrefs() {
        LibPref.init(this);
    }

    private void initFragment() {

        // collage container
        getSupportFragmentManager().beginTransaction()
                .add(R.id.collage_view_container, new CollageContainerFrag().setLayoutModel(model)).commit();

        // menu footer
        FooterMenuFragment footerFrag = new FooterMenuFragment();
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_in_up, R.anim.no_anim)
                .add(R.id.menu_footer, footerFrag).commit();
    }

    private void initToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        // must place in this order
        setSupportActionBar(mToolbar);

        mToolbar.setNavigationIcon(R.drawable.ic_back);
        mToolbar.setTitle(R.string.collage);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                FLog.d("back clicked");
//                checkUnsavedPhoto();
//                MainEditorActivity.this.onBackPressed(); // khanh add this
                ((CollageView) findViewById(R.id.collage_view)).dismissKeyboard();
                MainEditorActivity.this.onBackPressed();
            }
        });
    }

    public void onLayoutReady() {
        View v = findViewById(R.id.collage_view_container);
        ViewGroup.LayoutParams params = v.getLayoutParams();
        params.height = findViewById(R.id.nonAdParent).getMeasuredHeight() - findViewById(R.id.menu_footer).getMeasuredHeight();
        v.setLayoutParams(params);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private void checkUnsavedPhoto() {
        new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle).setCancelable(false)
                .setMessage(R.string.dialog_exit_editing)
                .setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        releaseUnused();
                        ((CollageView) findViewById(R.id.collage_view)).dismissKeyboard();
                        finish();
                    }
                }).show();
    }

    @Override
    public void onBackPressed() {

        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.menu_footer);

        if ((fragment instanceof StickerEditorFrag || fragment instanceof MenuStickerFrag || fragment instanceof MenuTextFrag)) {
            ((CollageView) findViewById(R.id.collage_view)).unSelectAllComponent();
        }

        if (fragment instanceof FooterMenuFragment) {
            checkUnsavedPhoto();
        } else if (fragment instanceof PhotoEditorFrag || fragment instanceof StickerEditorFrag || fragment instanceof MenuOptionOthersFrag) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_up, R.anim.no_anim);
            FooterMenuFragment collageMenuFrag = new FooterMenuFragment();
            ft.replace(R.id.menu_footer, collageMenuFrag).commit();
        } else if (fragment instanceof StickerOpacityFrag) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.setCustomAnimations(R.anim.slide_in_up, R.anim.no_anim);
            StickerEditorFrag curFragment = new StickerEditorFrag().setCurIndex(CollageContainerFrag.sCurStickerIdx);
            ft.replace(R.id.menu_footer, curFragment)
                    .addToBackStack(Statistic.TAG_STICKER_EDITOR_FRAG).commit();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_random) {
//            Toast.makeText(MainEditorActivity.this, "random", Toast.LENGTH_SHORT).show();
            CollageContainerFrag frag = (CollageContainerFrag) getSupportFragmentManager().findFragmentById(R.id.collage_view_container);
            frag.shuffleImages();
            return true;
        } else if (id == R.id.action_save) {
//            Toast.makeText(MainEditorActivity.this, "save", Toast.LENGTH_SHORT).show();
            CollageContainerFrag frag = (CollageContainerFrag) getSupportFragmentManager().findFragmentById(R.id.collage_view_container);
            frag.showResolutionSaveDialog();
            return true;
        }
//        else if (id == R.id.action_studio) {
//            getSupportFragmentManager().beginTransaction().add(R.id.content_main, MyStudioActivity.newFragment()).commit();
//            startActivity(new Intent(this, MyStudioActivity.class));
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void finish() {
        super.finish();

    }

    @Override
    public void onCollageMenuClickListener(int menuType) {

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_up, R.anim.no_anim);
        if (menuType == Statistic.MENU_COLLAGE_TYPE_LAYOUT) {
            Flog.i("MENU_COLLAGE_TYPE_LAYOUT");
            ft
                    .replace(R.id.menu_footer, new MenuLayoutFrag().setLayoutModel(model))
                    .addToBackStack("menuType_" + menuType).commit();
        } else if (menuType == Statistic.MENU_COLLAGE_TYPE_TEXT) {
            CollageContainerFrag fragText = (CollageContainerFrag) getSupportFragmentManager()
                    .findFragmentById(R.id.collage_view_container);
            fragText.setCollageBubbleTexts(ft);
        } else if (menuType == Statistic.MENU_COLLAGE_TYPE_STICKER) {
            ft
                    .replace(R.id.menu_footer, new MenuStickerFrag().setLayoutModel(model).setMenuType(menuType))
                    .addToBackStack("menuType_" + menuType).commit();
        } else if (menuType == Statistic.MENU_COLLAGE_TYPE_OTHERS) {
            Flog.d(TAG, "MENU_COLLAGE_TYPE_OTHERS");
            ft
                    .replace(R.id.menu_footer, new MenuOptionOthersFrag().setLayoutModel(model).setMenuType(menuType))
                    .addToBackStack("menuType_" + menuType).commit();
        }
//        else if (menuType == Statistic.MENU_COLLAGE_TYPE_BG) {

//            FLog.d(TAG, "MENU_COLLAGE_TYPE_BG");
//
//            ft
//                    .replace(R.id.menu_footer, new MenuBackgroundFrag().setLayoutModel(model))
//                    .addToBackStack("menuType_" + menuType).commit();
//        } else if (menuType == Statistic.MENU_COLLAGE_TYPE_FRAME) {
//            ft
//                    .replace(R.id.menu_footer, new MenuFrameFragment().setLayoutModel(model))
//                    .addToBackStack("menuType_" + menuType).commit();
//        } else if (menuType == Statistic.MENU_COLLAGE_TYPE_MARGIN) {
//            if (CollageContainerFrag.IS_PILE_TYPE) {
//                SystemUtils.show(this, getString(R.string.donot_set_margin_for_pile));
//                return;
//            }
//            Flog.i("MENU_COLLAGE_TYPE_MARGIN");
//            ft
//                    .replace(R.id.menu_footer, new MenuMarginFrag().setLayoutModel(model).setMenuType(menuType))
//                    .addToBackStack("menuType_" + menuType).commit();
//        }
//    else if (menuType == Statistic.MENU_COLLAGE_TYPE_RATIO) {
//            MenuRatioFrag ratioFrag = new MenuRatioFrag();
//            Bundle bundle = new Bundle();
//            bundle.putBoolean(Statistic.EXTRA_RATIO_FRAG, RATIO_GRID_FLAG);
//            ratioFrag.setArguments(bundle);
//            ft
//                    .replace(R.id.menu_footer, ratioFrag.setLayoutModel(model))
//                    .addToBackStack("menuType_" + menuType).commit();
//}
        else {
            ft.addToBackStack("menuType_" + menuType).commit();
        }
    }

    @Override
    public void onItemMenuSelectListener(int position) {

    }

    @Override
    public void onItemMenuClickListener(LayoutModel model) {
        this.model = model.clone();
    }

    @Override
    public void onLayoutChangeListener(LayoutModel model) {
        FLog.d(TAG, "onLayoutChangeListener: " + model);
        RATIO_GRID_FLAG = model.isGrid;
        CollageContainerFrag frag = (CollageContainerFrag) getSupportFragmentManager().findFragmentById(R.id.collage_view_container);
        frag.setCollageLayout(model);
    }

    @Override
    public void onMenuBackgroundClickListener(String bgName, boolean isPattern) {
        // TODO need to remove in the future, just for testing only
        Log.d("hoacode", "22 onMenuBackgroundClickListener: " + bgName);
        CollageContainerFrag frag = (CollageContainerFrag) getSupportFragmentManager().findFragmentById(R.id.collage_view_container);
        if (!isPattern) {
            frag.setCollageViewBg(bgName);
        } else {
            frag.setPatternText(bgName);
        }
    }

    public void onMenuColorBackgroundClickListener(@ColorInt int color) {
        CollageContainerFrag frag = (CollageContainerFrag) getSupportFragmentManager().findFragmentById(R.id.collage_view_container);
        frag.setCollageViewBg(color);
    }

    public void onItemStickerMainMenuSelectListener(StickerModel model) {

    }

    public void onItemStickerSubMainMenuSelectListener(StickerModel model) {
        CollageContainerFrag frag = (CollageContainerFrag) getSupportFragmentManager().findFragmentById(R.id.collage_view_container);
        frag.setCollageSticker(model);
    }

    public void onItemFrameMainMenuSelectListener(FrameModel model) {
        Flog.i("onItemFrameMainMenuSelectListener");
    }

    public void onItemFrameSubMainMenuSelectListener(FrameModel model) {
        Flog.i("onItemFrameSubMainMenuSelectListener");
//        CollageContainerFrag frag = (CollageContainerFrag) getSupportFragmentManager().findFragmentById(R.id.collage_view_container);
//        frag.setCollageFrame(model);
    }

    public void onMenuRatioClickListener(float ratio) {
        Log.d(TAG, "onMenuRatioCLick = " + ratio);
        CollageContainerFrag frag = (CollageContainerFrag) getSupportFragmentManager().findFragmentById(R.id.collage_view_container);
        frag.setRatio(ratio);
    }

    @Override
    public void onMenuMarginChangeListener(int progress) {
        CollageContainerFrag frag = (CollageContainerFrag) getSupportFragmentManager().findFragmentById(R.id.collage_view_container);
        frag.setMargin(progress);
    }

    @Override
    public void onMenuRoundChangeListener(int progress) {
        CollageContainerFrag frag = (CollageContainerFrag) getSupportFragmentManager().findFragmentById(R.id.collage_view_container);
        frag.setRound(progress);
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onKeyBoardSoftDisplayed() {
        CollageContainerFrag frag = (CollageContainerFrag) getSupportFragmentManager().findFragmentById(R.id.collage_view_container);
        frag.setTextKeyboardFrag();
    }

    @Override
    public void onFontDisplayed() {
        CollageContainerFrag frag = (CollageContainerFrag) getSupportFragmentManager().findFragmentById(R.id.collage_view_container);
        frag.setTextFontFrag();
    }

    @Override
    public void onSettingDisplayed() {
        CollageContainerFrag frag = (CollageContainerFrag) getSupportFragmentManager().findFragmentById(R.id.collage_view_container);
        frag.setTextSettingsFrag();
    }

    @Override
    public void onKeyBoardShown(int height) {

    }

    @Override
    public void onFontChanged(Typeface typeface) {
        CollageContainerFrag frag = (CollageContainerFrag) getSupportFragmentManager().findFragmentById(R.id.collage_view_container);
        frag.setFontText(typeface);
    }

    @Override
    public void onColorClickListener(@ColorInt int color) {
        CollageContainerFrag frag = (CollageContainerFrag) getSupportFragmentManager().findFragmentById(R.id.collage_view_container);
        frag.setColorText(color);
    }

    public void onTextSizeChangeListener(int unit) {
        CollageContainerFrag frag = (CollageContainerFrag) getSupportFragmentManager().findFragmentById(R.id.collage_view_container);
        frag.setTextSize(unit);
    }

    public void onTextPaddingChangeListener(int unit) {
        CollageContainerFrag frag = (CollageContainerFrag) getSupportFragmentManager().findFragmentById(R.id.collage_view_container);
        frag.setTextPadding(unit);
    }

    @Override
    public void onTextZoomInChangeListener() {
        CollageContainerFrag frag = (CollageContainerFrag) getSupportFragmentManager().findFragmentById(R.id.collage_view_container);
        frag.setTextZoomIn();
    }

    @Override
    public void onTextZoomOutChangeListener() {
        CollageContainerFrag frag = (CollageContainerFrag) getSupportFragmentManager().findFragmentById(R.id.collage_view_container);
        frag.setTextZoomOut();
    }

    @Override
    public void onTextRotate5ChangeListener() {
        CollageContainerFrag frag = (CollageContainerFrag) getSupportFragmentManager().findFragmentById(R.id.collage_view_container);
        frag.setTextRotate5();
    }

    @Override
    public void onTextMirror5ChangeListener() {
        CollageContainerFrag frag = (CollageContainerFrag) getSupportFragmentManager().findFragmentById(R.id.collage_view_container);
        frag.setTextMirror5();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        Flog.d("-------------onActivityResult----------------------");
        CollageContainerFrag frag = (CollageContainerFrag) getSupportFragmentManager()
                .findFragmentById(R.id.collage_view_container);
        if (requestCode == CollageContainerFrag.REQUEST_CODE_CONTAINER_FRAG && resultCode == RESULT_OK) {
//            Flog.d("success");
//            String[] list = data.getStringArrayExtra(GalleryActivity.OUTPUT_PATH_KEY);
            ArrayList<PhotoModel> models = data.getParcelableArrayListExtra(GalleryActivity.EXTRA_PHOTO_MODEL_LIST);
//            Flog.d("Thread", "size: " + models.size());
            for (int i = 0; i < models.size(); i++) {
                Flog.d(TAG, "____" + models.get(i));
                PhotoModel photoModel = models.get(i);
                if (photoModel == null || photoModel.imgPath == null) {
                    continue;
                }
                File file = new File(photoModel.imgPath);
                if (!file.exists() || file.length() == 0) {
                    models.set(i, null);
                }
            }

//            if (models.size() < 1) {
//                return;
//            }
//            for (int i = 0; i < list.length; i++) {
//                Flog.i(i + ": " + list[i]);
//            }
            frag.setPhotoModelList(models);
        } else {
            Flog.d(TAG, "onActivityResult failed");
        }
        frag.handleBackGallery();
    }

    @Override
    public void onMenuFrameClickListener(String bgName) {
        CollageContainerFrag frag = (CollageContainerFrag) getSupportFragmentManager().findFragmentById(R.id.collage_view_container);
        frag.setCollageFrame(bgName);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Flog.d("Test", "activity onDestroy");
        releaseUnused();
    }

    /*
    * onLowMemory() is not a big help
    * because it gets called only at times where the overall system is running out of memory
    * and not in cases where your app is running out of its available heap space.
    * */
    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        Flog.d(TAG, "onTrimMemory=" + level);
//        if (level > TRIM_MEMORY_RUNNING_MODERATE) {
//            Toast.makeText(this, "Your device is not enough memory to load this photos", Toast.LENGTH_SHORT).show();
////            finish();
//            AlarmManager mgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//            mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, PendingIntent.getActivity(this.getBaseContext(), 0, new Intent(getIntent()),
//                    getIntent().getFlags()));
//            android.os.Process.killProcess(android.os.Process.myPid());
//        }
    }

    public void releaseUnused() {
        LibPref.getInstance().remove(LibPref.KEY_ROUND_SIZE);
        LibPref.getInstance().remove(LibPref.KEY_MARGIN_SIZE);
        LibPref.getInstance().remove(LibPref.KEY_OPACITY_SIZE);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onPhotoEditor(View view, int position, int curIndex) {
        CollageContainerFrag frag = (CollageContainerFrag) getSupportFragmentManager()
                .findFragmentById(R.id.collage_view_container);
        frag.setPhotoEditor(view, position, curIndex);
    }

    @Override
    public void onOpacityChangeListener(int progress) {
        Flog.i("onOpacityChangeListener: " + progress);
        CollageContainerFrag frag = (CollageContainerFrag) getSupportFragmentManager()
                .findFragmentById(R.id.collage_view_container);
        frag.setStickerOpacity(progress);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onStickerEditor(View view, int position, int curIndex) {
        CollageContainerFrag frag = (CollageContainerFrag) getSupportFragmentManager()
                .findFragmentById(R.id.collage_view_container);
        frag.setStickerEditor(view, position, curIndex);
    }

    @Override
    public void onClick(View view) {
//        int id = view.getId();
//        Log.d("hoacode", "-4");
//        CollageContainerFrag frag = (CollageContainerFrag) getSupportFragmentManager().findFragmentById(R.id.collage_view_container);
//        Log.d("dismissKeyboard", "-3");
//        switch (id) {
//            case R.id.btn_back:
//                ((CollageView) findViewById(R.id.collage_view)).dismissKeyboard();
//                MainEditorActivity.this.onBackPressed();
//                break;
//            case R.id.btn_shuffle_layout:
//                frag.shuffleImages();
//                break;
//            case R.id.btn_save:
//                CollageContainerFrag frag1 = (CollageContainerFrag) getSupportFragmentManager().findFragmentById(R.id.collage_view_container);
//                Log.d("hoacode", "-2");
//                frag.showResolutionSaveDialog();
//                break;
//            default:
//                break;
//        }
    }

    @Override
    public void onCloseCallback(int tabIndex) {
        Log.d("onCloseCallback", "111111 onCloseCallback");
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        MenuOptionOthersFrag othersFrag = new MenuOptionOthersFrag();
        Bundle bundle = new Bundle();
        bundle.putBoolean(Statistic.EXTRA_CLOSE_OPTION, true);
        bundle.putInt(Statistic.EXTRA_CURRENT_TAB, tabIndex);
        othersFrag.setArguments(bundle);
        ft.replace(R.id.menu_footer, othersFrag).commit();
    }
}
