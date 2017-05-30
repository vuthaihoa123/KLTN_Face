package uet.kltn.hoavt_58.crazyexface.photo_collage.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.pic.libphotocollage.core.CollageView;
import com.pic.libphotocollage.core.collection.ListAdaptiveItem;
import com.pic.libphotocollage.core.collection.ListPhotoViewModel;
import com.pic.libphotocollage.core.layout.grid.GridStatistic;
import com.pic.libphotocollage.core.layout.pile.PileStatistic;
import com.pic.libphotocollage.core.model.BaseModel;
import com.pic.libphotocollage.core.model.BaseStickerModel;
import com.pic.libphotocollage.core.model.IconStickerModel;
import com.pic.libphotocollage.core.model.PhotoViewModel;
import com.pic.libphotocollage.core.model.TextStickerModel;
import com.pic.libphotocollage.core.pref.LibPref;
import com.pic.libphotocollage.core.util.Flog;
import com.pic.libphotocollage.core.util.ImageUtils;
import com.pic.libphotocollage.core.util.SystemUtils;
import com.pic.libphotocollage.core.util.displaybmp.BitmapDecoder;
import com.pic.libphotocollage.core.util.displaybmp.EGL14Util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;

import uet.kltn.hoavt_58.crazyexface.R;
import uet.kltn.hoavt_58.crazyexface.photo_collage.GalleryActivity;
import uet.kltn.hoavt_58.crazyexface.photo_collage.SavePhotoActivity;
import uet.kltn.hoavt_58.crazyexface.photo_collage.SplashActivity;
import uet.kltn.hoavt_58.crazyexface.photo_collage.asynctask.SaveToFileAsync;
import uet.kltn.hoavt_58.crazyexface.photo_collage.fragment.editor.PhotoEditorFrag;
import uet.kltn.hoavt_58.crazyexface.photo_collage.fragment.editor.StickerEditorFrag;
import uet.kltn.hoavt_58.crazyexface.photo_collage.fragment.editor.StickerOpacityFrag;
import uet.kltn.hoavt_58.crazyexface.photo_collage.fragment.menu.MenuTextFrag;
import uet.kltn.hoavt_58.crazyexface.photo_collage.model.LayoutModel;
import uet.kltn.hoavt_58.crazyexface.photo_collage.model.PhotoModel;
import uet.kltn.hoavt_58.crazyexface.photo_collage.model.StickerModel;
import uet.kltn.hoavt_58.crazyexface.photo_collage.statistic.Statistic;
import uet.kltn.hoavt_58.crazyexface.photo_collage.utils.AssetsUtil;
import uet.kltn.hoavt_58.crazyexface.photo_collage.utils.FLog;

/**
 * Created by Adm on 8/3/2016.
 */
public class CollageContainerFrag extends BaseFragment implements
        SaveToFileAsync.OnSavedFinish,
        CollageView.OnCollageViewListenner,
        BaseModel.OnItemInteractListener, PhotoViewModel.OnScanGalleryListenner {
    public static final int REQUEST_CODE_CONTAINER_FRAG = 6;
    public static final int INIT_STEP = 3;
    private static final String TAG = CollageContainerFrag.class.getSimpleName();
    public static boolean IS_PILE_TYPE = false;
    public static int MAX_ROUND_STEPS = 30, MAX_SPACING_STEPS = 30;
    public static int sCurStickerIdx;
    private final float MIN_SPACING_WIDTH = 0.00f, MAX_SPACING_WIDTH = 0.04f,
            MIN_ROUND_WIDTH = 0.00f, MAX_ROUND_WIDTH = 0.20f;
    private final float SPACING_VALUE_PER_STEP = ((MAX_SPACING_WIDTH - MIN_SPACING_WIDTH) / MAX_SPACING_STEPS),
            ROUND_VALUE_PER_STEP = ((MAX_ROUND_WIDTH - MIN_ROUND_WIDTH) / MAX_ROUND_STEPS);
    public int mRatioCode = Statistic.COLLAGE_RATIO_11;
    private float mRatioView = 1.f;
    private CollageView mCollageView = null;
    private CollageView mSavedCollageView = null;
    private ListPhotoViewModel mListPhotoViews = null;
    private ListPhotoViewModel mListSavedPhotoViews = null;
    private ListPhotoViewModel mCachePhotoViews = null;
    private ArrayList<PhotoModel> mCachePhotoModels = null;
    private float mRoundSize = MIN_ROUND_WIDTH;
    private float mMarginVal = MAX_SPACING_WIDTH / 4;
    private RelativeLayout parentContainer = null;
    private int layoutWidth = 0, layoutHeight = 0;
    private int minLayoutScreen = 0;
    private ArrayList<PhotoModel> mListPhotoModels = new ArrayList<>();
    private String mSaveFileName = "";
    private ArrayList<Integer> mChangedIndex = new ArrayList<>();
    private int mMaxTextureSize = 0;
    private int numThreadsDone = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.content_collage_container_view, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (SystemUtils.isSamsungDevice()) {
//            Log.d(TAG, "isSamsungDevice");
            getView().setLayerType(View.LAYER_TYPE_SOFTWARE, null); // Fix : chom chom
        }

        if (model == null) {  // Hard fix for LENOVO 4.4.2 : Turn off screen -> model is equal to null.
            Intent intent = new Intent(getActivity(), SplashActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            return;
        }

        mMaxTextureSize = EGL14Util.getMaxTextureSize();
        initCollageView();
    }

    private void initCollageView() {

        parentContainer = (RelativeLayout) getView().findViewById(R.id.parent_container);
        mCollageView = (CollageView) getView().findViewById(R.id.collage_view);
        mCollageView.setOnSwapTwoObj(this);


        parentContainer.post(new Runnable() {
            @Override
            public void run() {
                layoutWidth = parentContainer.getWidth();
                layoutHeight = parentContainer.getHeight();
                int min = Math.min(layoutWidth, layoutHeight);
                layoutWidth = min;
                minLayoutScreen = min;
                layoutHeight = (int) (min * mRatioView);

                mListPhotoViews = new ListPhotoViewModel(getActivity());
                setOrderDrawer();
                mCachePhotoViews = new ListPhotoViewModel(getActivity());
                mCachePhotoModels = new ArrayList<PhotoModel>();

                initPhotos();

                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mCollageView.getLayoutParams();
                params.width = layoutWidth;
                params.height = layoutHeight;
                mCollageView.setLayoutParams(params);

                mCollageView.setListPhotos(mListPhotoViews);
                mCollageView.setBackgroundRect(0, 0, layoutWidth, layoutHeight);

                if (!model.isGrid) {
                    mListPhotoViews.scaleFitPileFrame();
                    mListPhotoViews.setPileDrawHelper();
                }

                initPhotoListModel();

                Log.d(TAG, "3323333");



            }
        });
        parentContainer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
//                Log.d(TAG, "touch outside");
                if (v.getId() == R.id.parent_container) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        Fragment fragment = getActivity().getSupportFragmentManager().findFragmentById(R.id.menu_footer);
                        mCollageView.dismissKeyboard();
                        mCollageView.unSelectAllComponent();
                        if (!(fragment instanceof FooterMenuFragment)) {
                            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction()
                                    .setCustomAnimations(R.anim.slide_in_up, R.anim.no_anim);
                            FooterMenuFragment collageMenuFrag = new FooterMenuFragment();
                            ft.replace(R.id.menu_footer, collageMenuFrag).commit();
                            return true;
                        }
                    }
                }
                return false;
            }
        });



    }

    private void setOrderDrawer() {
        mListPhotoViews.setDrawOrders(getDrawOrder(model.name, model.imgCount, mRatioView));
    }

    private int[] getDrawOrder(String pileStyleId, int srcNum, float viewRatio) {

        if (pileStyleId.contains("_c_") && viewRatio == 16.0f / 9.0f) {
            if (srcNum == 6)
                return new int[]{2, 0, 1, 5, 3, 4};
            else if (srcNum == 5)
                return new int[]{2, 0, 1, 3, 4};
        }

        if (pileStyleId.contains("_b_") && viewRatio == 16.0f / 9.0f) {
            if (srcNum == 2) {
                return new int[]{1, 0};
            }
            if (srcNum == 4)
                return new int[]{0, 1, 3, 2};
            if (srcNum == 5)
                return new int[]{0, 1, 3, 2, 4};
        }

        int[] order = new int[srcNum];
        for (int i = 0; i < srcNum; i++) {
            order[i] = i;
        }
        return order;
    }

    public void setCollageViewBg(String bgname) {
        try {
            InputStream inputStream = getResources().getAssets().open("bg/" + bgname);
            Bitmap bmp = BitmapFactory.decodeStream(inputStream);
            mListPhotoViews.setBgBitmap(bmp);
            mListPhotoViews.setBackgroundPaint();
        } catch (IOException e) {
            e.printStackTrace();
            mListPhotoViews.setBgBitmap(null);
        }

        mCollageView.invalidate();
    }

    public void setCollageViewBg(@ColorInt int color) {

        mListPhotoViews.setBgColor(color);
        mListPhotoViews.setBgBitmap(null);

        mCollageView.invalidate();
    }

    private void initPhotos() {
        mMarginVal = INIT_STEP;
        LibPref.getInstance().putInt(LibPref.KEY_MARGIN_SIZE, (int) mMarginVal);
        mMarginVal = mMarginVal * Statistic.MAX_ROUND_UNIT / Statistic.MAX_ROUND_STEPS;

        upLayout();
        Flog.d("PhotoViewModel", "layoutWidth=" + layoutWidth + "_layoutHeight=" + layoutHeight);
        ArrayList<Path> clipLayoutPaths = getLayoutPath(layoutWidth, layoutHeight);

        mListPhotoViews.clear();

        for (int i = 0; i < model.imgCount; i++) {
            // Item photo view
            PhotoViewModel photoModel = new PhotoViewModel(mCollageView).setOnScanGalleryListenner(this);
            photoModel.setRegionPath(clipLayoutPaths.get(i));
            photoModel.setPriority(i);
            photoModel.setIsGrid(model.isGrid);
            mListPhotoViews.add(photoModel);
        }
    }

    public void setRatio(float ratio) {
        Log.d(TAG, "setRatio=" + ratio);
        matchToRatioCode(ratio);
        float oldRatio = mRatioView;
        float newRatio = ratio;
        if (oldRatio == newRatio) {
            FLog.d(TAG, "oldRatio == newRatio " + ratio);
            return;
        }

        FLog.d(TAG, "setRatio: " + ratio);
        mCollageView.putOldSize();
        mRatioView = ratio;

        setCollageViewParams();

        setCollageLayout(model);

        mCollageView.setRatioSavedView(mRatioView);
        mCollageView.getListItem().invalidate();
    }

    private void setCollageViewParams() {
        layoutWidth = minLayoutScreen;
        layoutHeight = (int) (mRatioView * minLayoutScreen);
        int measureHeight = parentContainer.getHeight();
        layoutHeight = Math.min(layoutHeight, measureHeight);
        layoutWidth = (int) (layoutHeight / mRatioView);

        ViewGroup.LayoutParams params = mCollageView.getLayoutParams();
        params.width = layoutWidth;
        params.height = layoutHeight;

        mCollageView.setLayoutParams(params);

        if (model.isGrid) {
            mCollageView.setRoundSize(mRoundSize);
        }

        mCollageView.setBackgroundRect(0, 0, layoutWidth, layoutHeight);
    }

    private void matchToRatioCode(float ratio) {
        if (ratio == 1.0f) {
            mRatioCode = Statistic.COLLAGE_RATIO_11;
        } else if (ratio == 2.f / 3.f) {
            mRatioCode = Statistic.COLLAGE_RATIO_32;
        } else if (ratio == 3.f / 2.f) {
            mRatioCode = Statistic.COLLAGE_RATIO_23;
        } else if (ratio == 3.f / 4.f) {
            mRatioCode = Statistic.COLLAGE_RATIO_43;
        } else if (ratio == 4.f / 3.f) {
            mRatioCode = Statistic.COLLAGE_RATIO_34;
        } else if (ratio == 4.f / 5.f) {
            mRatioCode = Statistic.COLLAGE_RATIO_54;
        } else if (ratio == 5.f / 4.f) {
            mRatioCode = Statistic.COLLAGE_RATIO_45;
        } else if (ratio == 16.f / 9.f) {
            mRatioCode = Statistic.COLLAGE_RATIO_916;
        } else if (ratio == 9.f / 16.f) {
            mRatioCode = Statistic.COLLAGE_RATIO_169;
        } else {
            mRatioCode = Statistic.COLLAGE_RATIO_11;
        }
    }

    public void setCollageFrame(String frName) {
        try {
            InputStream inputStream = getResources().getAssets().open("bg/" + frName);
            Bitmap bmp = BitmapFactory.decodeStream(inputStream);
            Log.d("bitmapp", "1 w="+bmp.getWidth()+"_h="+bmp.getHeight());
            bmp = Bitmap.createScaledBitmap(bmp, bmp.getWidth()*3/4, bmp.getHeight()*3/4, false);
            Log.d("bitmapp", "2 w="+bmp.getWidth()+"_h="+bmp.getHeight());
            if (frName.equals("ic_bag_0.jpg"))  // No frame
                mListPhotoViews.setFrameBmp(null);
            else {
                mListPhotoViews.setFrameBmp(bmp);
                mListPhotoViews.setFramePaint();
            }
        } catch (IOException e) {
            e.printStackTrace();
            mListPhotoViews.setFrameBmp(null);
        }

        mCollageView.invalidate();
    }

    public void setRound(int progress) {
        if (!model.isGrid)
            return;

        float roundWidth = ROUND_VALUE_PER_STEP * progress + MIN_ROUND_WIDTH;
        int retVal = (int) ((roundWidth - MIN_ROUND_WIDTH) / ROUND_VALUE_PER_STEP);
        LibPref.getInstance().putInt(LibPref.KEY_ROUND_SIZE, retVal);
        mRoundSize = (roundWidth * layoutWidth);
        mCollageView.setRoundSize(mRoundSize);
        mCollageView.invalidate();
    }

    public void setMargin(int progress) {
        if (!model.isGrid)
            return;

        float spacingWidth = SPACING_VALUE_PER_STEP * progress + MIN_SPACING_WIDTH;
        int retVal = (int) ((spacingWidth - MIN_SPACING_WIDTH) / SPACING_VALUE_PER_STEP);
        LibPref.getInstance().putInt(LibPref.KEY_MARGIN_SIZE, retVal);
        mMarginVal = spacingWidth * layoutWidth;
        mMarginVal = mMarginVal * Statistic.MAX_ROUND_UNIT / Statistic.MAX_ROUND_STEPS;
        upLayout();
        ArrayList<Path> clipLayoutPaths = getLayoutPath(layoutWidth, layoutHeight);

        for (int i = 0; i < model.imgCount; i++) {
            // Item photo view
            PhotoViewModel photoModel = mListPhotoViews.get(i);
            photoModel.setRegionPath(clipLayoutPaths.get(i));
        }

        mCollageView.setRoundSize(mRoundSize);
        mCollageView.invalidate();
    }

    private boolean allowAddNextSticker() {
        if (mCollageView.getItemSize() >= Statistic.STICKERS_ADDED_ALLOWED) {
            AlertDialog.Builder builder =
                    new AlertDialog.Builder(getActivity(), R.style.AppCompatAlertDialogStyle);
            builder.setMessage(getActivity().getString(R.string.allow_stickers_added));
            builder.setPositiveButton(getActivity().getString(R.string.ok), null);
            builder.show();
            return false;
        }
//        SystemUtils.show(getActivity(),
//                SystemUtils.getString(getActivity(), R.string.limit_stickers_added));
        return true;
    }

    public void setCollageSticker(StickerModel model) {
        if (!allowAddNextSticker()) {
            return;
        }

        IconStickerModel item = new IconStickerModel(mCollageView);
        item.setOnInteractListener(this);
        String stickerName = "stickers/" + model.index + "/" + model.name + ".png";
        item.setBitmap(ImageUtils.loadBitmapFromAssets(getActivity(), stickerName));
        mCollageView.addItem(item);
        mCollageView.invalidate();
    }

    public void setCollageBubbleTexts(FragmentTransaction ft) {
        if (!allowAddNextSticker()) {
            return;
        }

//        Fragment menuTextFragment = new MenuTextFrag();
//        Bundle bundle = new Bundle();
//        menuTextFragment.setArguments(bundle);
//        ft
//                .replace(R.id.menu_footer, menuTextFragment)
//                .addToBackStack("menuType_" + menuType).commit();

        TextStickerModel item = new TextStickerModel(mCollageView, Color.BLUE);
        item.setOnInteractListener(this);
        mCollageView.addItem(item);
//        mCollageView.requestKeyboard();
//        mCollageView.invalidate();
//        mCollageView.showRealEditText();
//        Fragment menuTextFrag = getActivity().getSupportFragmentManager().findFragmentById(R.id.menu_footer);
//        if (menuTextFrag != null && menuTextFrag instanceof MenuTextFrag)
//            ((MenuTextFrag) menuTextFrag).setHeightPager(0);

        MenuTextFrag curFragment = new MenuTextFrag();
        Bundle bundle = new Bundle();
        bundle.putBoolean(MenuTextFrag.IS_INITIAL_EXPAND_KEY, true);
        bundle.putFloat(MenuTextFrag.TEXT_SIZE_KEY, item.getTextSize());
        bundle.putFloat(MenuTextFrag.PADDING_TEXT_KEY, item.getPadding());
        curFragment.setArguments(bundle);
        ft
                .replace(R.id.menu_footer, curFragment)
                .addToBackStack("menuType_" + menuType).commit();
    }

    public void setCollageLayout(LayoutModel model) {
        if (model == null) return; // khanh fix for temp, not sure
        this.model = model;

        IS_PILE_TYPE = !this.model.isGrid;

        upFooterGallery();
        upLayout();
        upImages();
        setOrderDrawer();

        mCollageView.invalidate();
    }

    private void initPhotoListModel() {
        for (int i = 0; i < model.imgCount; i++)
            mListPhotoModels.add(null);
    }

    private void upImages() {
        ArrayList<Path> clipLayoutPaths = getLayoutPath(layoutWidth, layoutHeight);

        int numOfPhotos = mListPhotoViews.size();
        int numOfRects = model.imgCount;
        if (numOfPhotos > numOfRects) {
            int diff = numOfPhotos - numOfRects;
            int[] noContentIdx = mListPhotoViews.getNoContentIndex();
            if (diff >= noContentIdx.length) {
                // del from reverse array avoiding confuse:
                for (int i = noContentIdx.length - 1; i >= 0; i--) {
                    mListPhotoViews.remove(noContentIdx[i]);
                }
                int odd = diff - noContentIdx.length;
                for (int i = 0; i < odd; i++) {
                    int lastIdx = mListPhotoViews.size() - 1;
                    mCachePhotoViews.add(mListPhotoViews.get(lastIdx));
                    mListPhotoViews.remove(lastIdx);
                }
            } else {
                // del from reverse array avoiding confuse:
                for (int i = noContentIdx.length - 1; i > (noContentIdx.length - 1 - diff); i--) {
                    mListPhotoViews.remove(noContentIdx[i]);
                }
            }
        } else if (numOfPhotos < numOfRects) {
            int diff = numOfRects - numOfPhotos;
            if (diff >= mCachePhotoViews.size()) {
                for (int i = mCachePhotoViews.size() - 1; i >= 0; i--) {
                    int priority = numOfPhotos + ((mCachePhotoViews.size() - 1) - i);
                    PhotoViewModel cachedItem = mCachePhotoViews.get(i);
                    PhotoViewModel photoModel = new PhotoViewModel(mCollageView).setOnScanGalleryListenner(this);
                    photoModel.setRegionPath(cachedItem.getPath());
                    photoModel.setBitmap(cachedItem.getBitmap());
                    photoModel.setPriority(priority);
                    photoModel.setIsGrid(model.isGrid);
                    photoModel.containsContent(true);
                    photoModel.setRoundSize(mRoundSize);
                    mListPhotoViews.add(photoModel);
                }

                for (int i = (numOfPhotos + mCachePhotoViews.size()); i < numOfRects; i++) {
                    PhotoViewModel photoModel = new PhotoViewModel(mCollageView).setOnScanGalleryListenner(this);
                    photoModel.setRegionPath(clipLayoutPaths.get(i));
                    photoModel.setPriority(i);
                    photoModel.setIsGrid(model.isGrid);
                    photoModel.setRoundSize(mRoundSize);
                    mListPhotoViews.add(photoModel);
                }
            } else {
                for (int i = mCachePhotoViews.size() - 1; i >= mCachePhotoViews.size() - diff; i--) {
                    int priority = numOfPhotos + ((mCachePhotoViews.size() - 1) - i);
                    PhotoViewModel cachedItem = mCachePhotoViews.get(i);
                    PhotoViewModel photoModel = new PhotoViewModel(mCollageView).setOnScanGalleryListenner(this);
                    photoModel.setRegionPath(cachedItem.getPath());
                    photoModel.setBitmap(cachedItem.getBitmap());
                    photoModel.setPriority(priority);
                    photoModel.setIsGrid(model.isGrid);
                    photoModel.containsContent(true);
                    photoModel.setRoundSize(mRoundSize);
                    mListPhotoViews.add(photoModel);
                }
            }
            mCachePhotoViews.clear();
        }

        // Update of item-photo
        for (int i = 0; i < model.imgCount; i++) {
            PhotoViewModel photoModel = mListPhotoViews.get(i);
            photoModel.setRegionPath(clipLayoutPaths.get(i));
            photoModel.setIsGrid(model.isGrid);
            photoModel.fitPhotoToLayout();
            photoModel.setPriority(i);
        }
    }

    private void upFooterGallery() {
        int numOfPhotos = mListPhotoModels.size();
        int numOfRects = model.imgCount;
        if (numOfPhotos > numOfRects) {
            int diff = numOfPhotos - numOfRects;
            int[] noContentIdx = getNoContentIndex();
            if (diff >= noContentIdx.length) {
                // del from reverse array avoiding confuse:
                for (int i = noContentIdx.length - 1; i >= 0; i--) {
                    mListPhotoModels.remove(noContentIdx[i]);
                }
                int odd = diff - noContentIdx.length;
                for (int i = 0; i < odd; i++) {
                    int lastIdx = mListPhotoModels.size() - 1;
                    mCachePhotoModels.add(mListPhotoModels.get(lastIdx));
                    mListPhotoModels.remove(lastIdx);
                }
            } else {
                // del from reverse array avoiding confuse:
                for (int i = noContentIdx.length - 1; i > (noContentIdx.length - 1 - diff); i--) {
                    mListPhotoModels.remove(noContentIdx[i]);
                }
            }
        } else if (numOfPhotos < numOfRects) {
            int diff = numOfRects - numOfPhotos;
            if (diff >= mCachePhotoModels.size()) {
                for (int i = mCachePhotoModels.size() - 1; i >= 0; i--) {
                    PhotoModel cachedItem = mCachePhotoModels.get(i);
                    mListPhotoModels.add(cachedItem);
                }
                for (int i = (numOfPhotos + mCachePhotoModels.size()); i < numOfRects; i++) {
                    mListPhotoModels.add(null);
                }
            } else {
                for (int i = mCachePhotoModels.size() - 1; i >= mCachePhotoModels.size() - diff; i--) {
//                    int priority = numOfPhotos + ((mCachePhotoModels.size() - 1) - i);
                    PhotoModel cachedItem = mCachePhotoModels.get(i);
                    mListPhotoModels.add(cachedItem);
                }
            }
            mCachePhotoModels.clear();
        }
    }

    public int[] getNoContentIndex() {
        //  count number of items that no content:
        int len = 0;
        for (int i = 0; i < mListPhotoModels.size(); i++) {
            PhotoModel item = mListPhotoModels.get(i);
            if (item == null)
                len++;
        }
        // init array of integer that save index of items that no content:
        int ans[] = new int[len];
        int index = 0;
        for (int i = 0; i < mListPhotoModels.size(); i++) {
            PhotoModel item = mListPhotoModels.get(i);
            if (item == null) {
                ans[index] = i;
                index++;
            }
        }
        return ans;
    }

    private void upLayout() {
        if (!model.isGrid) {
            /* Only apply aspect ratio 1:1 and 16:9 for Pile styles */
            if (mRatioCode != Statistic.COLLAGE_RATIO_11 && mRatioCode != Statistic.COLLAGE_RATIO_916) {
                mRatioCode = Statistic.COLLAGE_RATIO_11;
                mRatioView = 1.0f;
                setCollageViewParams();
            }

            mListPhotoViews.setPileDrawHelper();
            Bitmap bmpBackground = AssetsUtil.getBitmapFromAssetsForPile(getContext(),
                    model, mRatioCode, layoutWidth, layoutHeight, mMaxTextureSize);
            if (bmpBackground != null) {
                mListPhotoViews.setPileFrameBmp(bmpBackground);
            }
        }

        mListPhotoViews.setIsDrawGrid(model.isGrid);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mCollageView != null)
            mCollageView.dismissKeyboard();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void setTextKeyboardFrag() {
        Log.d(TAG, "setTextKeyboardFrag");
        if (mCollageView != null)
            mCollageView.requestKeyboard();
    }

    public void setTextFontFrag() {
        mCollageView.dismissKeyboard();
    }

    public void setTextSettingsFrag() {
        mCollageView.dismissKeyboard();
    }

    public void setFontText(Typeface plain) {

        if (!mCollageView.isCurrentItemType(BaseModel.ItemType.STICKER_TEXT)) return;
        TextStickerModel view = (TextStickerModel) mCollageView.getCurrentItem();
        view.setFontType(plain);
        mCollageView.invalidate();
    }

    public void setColorText(int color) {

        if (!mCollageView.isCurrentItemType(BaseModel.ItemType.STICKER_TEXT)) return;
        TextStickerModel view = (TextStickerModel) mCollageView.getCurrentItem();
        view.setTextColor(color);
        mCollageView.invalidate();
    }

    public void setTextSize(int unit) {

        if (!mCollageView.isCurrentItemType(BaseModel.ItemType.STICKER_TEXT)) return;
        TextStickerModel view = (TextStickerModel) mCollageView.getCurrentItem();
        view.setTextSize(unit);
        mCollageView.invalidate();
    }

    public void setTextPadding(int unit) {

        if (!mCollageView.isCurrentItemType(BaseModel.ItemType.STICKER_TEXT)) return;
        BaseModel item = mCollageView.getCurrentItem();
        if (item instanceof TextStickerModel) {
            TextStickerModel view = (TextStickerModel) item;
            view.setPaddingText(unit);
            mCollageView.invalidate();
        }
    }

    private void openGallery(int curIndex) {

        Intent galleryIntent = new Intent(getActivity(), GalleryActivity.class);
        galleryIntent.putExtra(Statistic.EXTRA_CURRENT_SELECTED_RECT, curIndex);
        galleryIntent.putExtra(GalleryActivity.EXTRA_NUM_PICKED_FRAME_KEY, model.imgCount);
        galleryIntent.putParcelableArrayListExtra(GalleryActivity.EXTRA_PHOTO_MODEL_LIST, mListPhotoModels);
        getActivity().startActivityForResult(galleryIntent, REQUEST_CODE_CONTAINER_FRAG);
    }

    public void setBitmapPhotoList() {
        if ((mListPhotoViews.size() != mListPhotoModels.size())
                || (mChangedIndex.size() > mListPhotoModels.size())) {
            Flog.d("not matched number of items");
            return;
        }
        loadBitmapMultiThreads();
    }

    private void loadBitmapMultiThreads() {
        final int poolSize = mChangedIndex.size();
        if (poolSize < 1)
            return;

        numThreadsDone = 0;
        for (int i = 0; i < mChangedIndex.size(); i++) {
            final int index = mChangedIndex.get(i);
            final PhotoViewModel singleModel = mListPhotoViews.get(index);
            final PhotoModel photoModel = mListPhotoModels.get(index);
            RectF rectF = singleModel.getBoundsRect();
            final int widthRect = (int) (rectF.right - rectF.left);
            final int heightRect = (int) (rectF.bottom - rectF.top);

            AsyncTask asyncTask = new AsyncTask<Object, Void, Void>() {
                Bitmap bitmap;
                int index = 0;
                ProgressDialog dialog;

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    dialog = new ProgressDialog(getActivity(), R.style.CircularProgress);
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.show();
                }

                @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                @Override
                protected Void doInBackground(Object... params) {

                    index = (int) params[0];
                    bitmap = doLongTask((String) params[4], (int) params[3], (int) params[1], (int) params[2]);
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);

                    if (dialog != null && dialog.isShowing())
                        dialog.dismiss();
                    dialog = null;

                    numThreadsDone++;
                    PhotoViewModel item = mListPhotoViews.get(index);
                    RectF rectF = item.getBoundsRect();
                    if (bitmap != null) {
                        item.setBitmap(bitmap, rectF.left, rectF.top);
                        item.fitPhotoToLayout();
                        item.containsContent(true);
                    } else {
                        item.containsContent(false);
                    }

                    if (numThreadsDone == poolSize) {
                        //notifyDone
                        Flog.d(Flog.CTAG, "numThreadsDone == poolSize");
                        mCollageView.invalidate();
                    }
                }

                private Bitmap doLongTask(String imgPath, int maxTextureSize, int widthRect, int heightRect) {
                    File file = new File(imgPath);
                    if (!file.exists()) return null;

                    Bitmap bitmap = BitmapDecoder.decodeSampledBitmapFromFile(getActivity(), imgPath,
                            widthRect, heightRect, maxTextureSize);
//                    Log.d("memory", numThreadsDone + " bitmap="+bitmap);
                    if (bitmap == null)
                        return null;
//                    bitmap = ImageUtils.correctRotatedBitmap(Uri.fromFile(file), bitmap);
                    return bitmap;
                }

            };
            asyncTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, index, widthRect,
                    heightRect, mMaxTextureSize, (photoModel != null) ? photoModel.imgPath : "");
        }


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mCollageView != null)
            mCollageView.release();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releaseUnused();
        if (mCollageView != null)
            mCollageView.dismissKeyboard();
    }

    public void setPhotoModelList(ArrayList<PhotoModel> models) {

        mChangedIndex.clear();
        if (mListPhotoModels.size() == models.size()) {
            for (int i = 0; i < models.size(); i++) {
                String path1 = "";
                if (mListPhotoModels.get(i) != null) path1 = mListPhotoModels.get(i).imgPath;
                String path2 = "";
                if (models.get(i) != null) path2 = models.get(i).imgPath;
                if (!path1.equals(path2)) mChangedIndex.add(i);
            }
            mListPhotoModels = models;

            setBitmapPhotoList();
        }
    }

    public void handleBackGallery() {
        if (mListPhotoViews != null && mListPhotoViews.getCurPhotoItem() != null) {
            mListPhotoViews.getCurPhotoItem().setIsSelected(false);
        }
    }

    public void shuffleImages() {

        ArrayList<LayoutModel> listModels;
        if (model.isGrid) {
            listModels = (ArrayList<LayoutModel>) Statistic.createSubGridLayoutList(model.imgCount);
        } else {
            listModels = (ArrayList<LayoutModel>) Statistic.createSubPileLayoutList(model.imgCount);
        }
        Random rd = new Random();
        int rdIndex = 0 + rd.nextInt(listModels.size());
        model = listModels.get(rdIndex);
        upLayout();
        upShuffleImgs();
        upBackground();
        setOrderDrawer();
    }

    private void upBackground() {

        Random rd = new Random();
        int type = 0 + rd.nextInt(2);
        if (type == 0) {
            // set color bg:
            int[] colorList = getActivity().getResources().getIntArray(R.array.color_picker);
            int rdIndex = 1 + rd.nextInt(colorList.length-1);
            setCollageViewBg(colorList[rdIndex]);
        } else {
            // set bitmap bg:
            ArrayList<String> bmpList = (ArrayList<String>) Statistic.BG_LIST;
            int rdIndex = 0 + rd.nextInt(bmpList.size());
            setCollageViewBg(bmpList.get(rdIndex));
        }
    }

    private void upShuffleImgs() {
        ArrayList<Path> clipLayoutPaths = getLayoutPath(layoutWidth, layoutHeight);
        for (int i = 0; i < model.imgCount; i++) {
            PhotoViewModel photoModel = mListPhotoViews.get(i);
            photoModel.setRegionPath(clipLayoutPaths.get(i));
            photoModel.setPriority(i);
            photoModel.fitPhotoToLayout();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mCollageView != null)
            mCollageView.dismissKeyboard();
    }

    private void releaseUnused() {
        if (mCollageView != null) {
            mCollageView.release();
        }
        if (mSavedCollageView != null)
            mSavedCollageView.release();
        if (mListPhotoViews != null)
            mListPhotoModels.clear();
        if (mCachePhotoViews != null)
            mCachePhotoViews.clear();
    }

    private void createSaveBitmap(int widthSave, int heightSave, boolean isDefaultSave) {

        Log.d("hoacode", "0");
        mListSavedPhotoViews = mListPhotoViews;

        mSavedCollageView = new CollageView(getActivity());
        mSavedCollageView.setRatioSavedView(mRatioView);

//        mListSavedPhotoViews.clear();
//        for (PhotoViewModel model:mListPhotoViews) {
//            mListSavedPhotoViews.add(model);
//        }
        mSavedCollageView.setListPhotos(mListSavedPhotoViews);
//        mCollageView.getListItem().clear();
//        mCollageView.setListPhotos(new ArrayList<>(mS));
//        Flog.d(TAG, "size 1: "+mSavedCollageView.getListItem().size());
//        Flog.d(TAG, "size 11: "+mCollageView.getListItem().size());
//        tempList.clear();
//        Flog.d(TAG, "size 2: "+mSavedCollageView.getListItem().size());
//        Flog.d(TAG, "size 22: "+mCollageView.getListItem().size());


        if (!isDefaultSave) {
            mSavedCollageView.setSaveBitmapSize(widthSave, heightSave);
        } else {
            mSavedCollageView.setSaveBitmapSize();
        }
        mSavedCollageView.setBackgroundRectSaved();
        mSavedCollageView.setRoundSize((mListSavedPhotoViews.get(0).getRoundSize()
                * mSavedCollageView.getSaveCanvasWidth() * 1.0f) / layoutWidth);

        mListSavedPhotoViews.setIsSavePhoto(true);

        mListSavedPhotoViews.setChangedRatio(mSavedCollageView.getSaveCanvasWidth() * 1.0f / layoutWidth);
        mListSavedPhotoViews.upBackgroundPaint();
        mListSavedPhotoViews.upFramePaint();

        initSavedPhotos(mSavedCollageView.getSaveCanvasWidth(), mSavedCollageView.getSaveCanvasHeight());
        initSavedItem(mSavedCollageView.getSaveCanvasWidth());
//        ListAdaptiveItem tempList = mCollageView.getListItem();
        mSavedCollageView.setListItem(mCollageView.getListItem());
//        mCollageView.setListItem(tempList);
//        Flog.d(TAG, "size 1: "+mSavedCollageView.getListItem().size());
//        Flog.d(TAG, "size 11: "+mCollageView.getListItem().size());
//        tempList.clear();
//        Flog.d(TAG, "size 2: "+mSavedCollageView.getListItem().size());
//        Flog.d(TAG, "size 22: "+mCollageView.getListItem().size());
//        Flog.d(TAG, "mSavedCollageView: getSaveCanvasWidth=" + mSavedCollageView.getSaveCanvasWidth()
//                + "_getSaveCanvasHeight=" + mSavedCollageView.getSaveCanvasHeight());

        Bitmap saveBitmap = Bitmap.createBitmap(
                (mSavedCollageView.getSaveCanvasWidth() <= 0) ? 1 : mSavedCollageView.getSaveCanvasWidth(),
                (mSavedCollageView.getSaveCanvasHeight() <= 0) ? 1 : mSavedCollageView.getSaveCanvasHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas saveCanvas = new Canvas(saveBitmap);
        mCollageView.getListItem().setNotTouchAll();
        Log.d("hoacode", "1");
        mSavedCollageView.draw(saveCanvas);
        Log.d("hoacode", "2");
        saveCanvas.save();
        Log.d("hoacode", "3");
        saveCanvas = null;
        String savePath = SystemUtils.SAVE_DIR;
        SystemUtils.checkOrMakeFileDirectory(savePath);
        Log.d("hoacode", "4");
        mCollageView.setDrawed(true);
        saveToFile(savePath, saveBitmap);
        Log.d("hoacode", "5");
    }

    private void initSavedItem(int saveCanvasWidth) {
        float ratio = (saveCanvasWidth * 1.0f) / mCollageView.getWidth();
        for (BaseStickerModel item : mCollageView.getListItem()) {
            Matrix matrixSticker = item.getMatrix();
            fitRatioMatrix(matrixSticker, ratio);
        }
    }

    private void fitRatioMatrix(Matrix matrix, float ratio) {
        float values[] = new float[9];
        matrix.getValues(values);
        // get old values of matrix stickers:
        float scale_X = values[0];
        float skew_X = values[1];
        float transform_X = values[2];
        float skew_Y = values[3];
        float scale_Y = values[4];
        float transform_Y = values[5];

        // set new values for matrix stickers:
        float newTransform_X = transform_X * ratio;
        float newTransform_Y = transform_Y * ratio;
        matrix.preScale(ratio, ratio);

        Matrix concatMatrix = new Matrix();
        concatMatrix.setTranslate(newTransform_X - transform_X, newTransform_Y - transform_Y);
        matrix.postConcat(concatMatrix);
    }

    private void saveToFile(String savePath, Bitmap saveBitmap) {
        mSaveFileName = SystemUtils.getSimpleDate() + Statistic.JPEG;
        SaveToFileAsync saveToFileAsync = new SaveToFileAsync(getActivity(), savePath, mSaveFileName, saveBitmap).setOnSavedFinish(this);
        saveToFileAsync.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void initSavedPhotos(int saveCanvasWidth, int saveCanvasHeight) {
        upLayout();

        ArrayList<Path> paths = getLayoutPath(saveCanvasWidth, saveCanvasHeight);

        float ratio = (saveCanvasWidth * 1.0f) / mCollageView.getWidth();

        for (int i = 0; i < mListPhotoViews.size(); i++) {
            PhotoViewModel photoModel = mListPhotoViews.get(i);
            photoModel.setRegionPath(paths.get(i));
            fitRatioMatrix(photoModel.getMatrix(), ratio);
//            mListSavedPhotoViews.add(photoModel);
        }

//        mListSavedPhotoViews.setCurPhotoIndex(-1);
        mListPhotoViews.setCurPhotoIndex(-1);
    }

    private ArrayList<Path> getLayoutPath(int width, int height) {
        Rect backgroundRect = new Rect(0, 0, width, height);
        ArrayList<Path> paths;
//        Flog.d(TAG, "model.isGrid=" + model.isGrid);
        if (model.isGrid) {
            paths = GridStatistic.getGridPointPaths(backgroundRect, mMarginVal, model.name, model.imgCount);
        } else {
//            Flog.d(TAG, "mRatioCode=" + mRatioCode);
//            Flog.d(TAG, "name=" + model.name);
//            Flog.d(TAG, "rect=" + backgroundRect);
            paths = PileStatistic.getPilePaths(model.name, mRatioCode, backgroundRect);
        }
        return paths;
    }

    public void showResolutionSaveDialog() {
        mCollageView.dismissKeyboard();
//        replaceAllFooterFrags();
        Log.d("hoacode", "-1");
        saveResolution(Statistic.RESOLUTION_GOOD);
//        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        builder.setTitle(SystemUtils.getString(getActivity(), R.string.save_resolution));
//        String[] types = Statistic.getResolutionList(mRatioCode);
//        builder.setItems(types, new DialogInterface.OnClickListener() {
//
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//
//                dialog.dismiss();
//                switch (which) {
//                    case 0:     // resolution 2560x
//                        saveResolution(Statistic.RESOLUTION_EXCELLENT);
//                        break;
//                    case 1:     // resolution 1920x
//                        saveResolution(Statistic.RESOLUTION_GOOD);
//                        break;
//                    case 2:     // resolution 1280x
//                        saveResolution(Statistic.RESOLUTION_AVERAGE);
//                        break;
//                    case 4:     // resolution 960x
//                        saveResolution(Statistic.RESOLUTION_POOR);
//                        break;
//                    default:     // resolution default
//                        createSaveBitmap(-1, -1, true);
//                        break;
//                }
//            }
//        });
//        String negativeText = getString(android.R.string.cancel);
//        builder.setPositiveButton(negativeText,
//                new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        // positive button logic
//                        dialog.dismiss();
//                    }
//                });
//
//        builder.show();
    }

    private void replaceAllFooterFrags() {
        Fragment fragment = getActivity().getSupportFragmentManager().findFragmentById(R.id.menu_footer);
        mCollageView.dismissKeyboard();
        if (!(fragment instanceof FooterMenuFragment)) {
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            FooterMenuFragment collageMenuFrag = new FooterMenuFragment();
            ft.replace(R.id.menu_footer, collageMenuFrag).commit();
        }
    }

    private void saveResolution(int resolution) {
        int widthSave, heightSave;
//        if (mRatioView >= 1) {
        heightSave = resolution;
        widthSave = (int) (heightSave / mRatioView);
//        }
//        else {
//            widthSave = resolution;
//            heightSave = (int) (widthSave / mRatioView);
//        }
//        Log.d(TAG, "mRatioView: " + mRatioView + "_widthSave: " + widthSave + "_heightSave: " + heightSave);
//        Log.d(TAG, "maxTextureSize = " + mMaxTextureSize);
        if (widthSave >= mMaxTextureSize || heightSave >= mMaxTextureSize) {
            if (mRatioView >= 1) {
                createSaveBitmap((int) (mMaxTextureSize / mRatioView), mMaxTextureSize, false);
            } else {
                createSaveBitmap(mMaxTextureSize, (int) (mMaxTextureSize * mRatioView), false);
            }
        } else {
//            if (mRatioView >= 1) {
            createSaveBitmap(widthSave, heightSave, false);
//            } else {
//                createSaveBitmap(widthSave, heightSave, false);
//            }
        }
    }

    @Override
    public void onSavedDone() {
        String fullNamePath = SystemUtils.SAVE_DIR + File.separator + mSaveFileName;
        Log.d(TAG, "1 fullNamePath="+fullNamePath);

//        SettingsUtil.addToLibrary(getActivity().getContentResolver(), getActivity(), fullNamePath);

        SystemUtils.show(getActivity(), SystemUtils.getString(getActivity(), R.string.saved_to_file) + " " + fullNamePath);

        // start save activity :
        Log.d("hoacode", "7");
        Intent saveIntent = new Intent(getActivity(), SavePhotoActivity.class);
        saveIntent.putExtra(Statistic.EXTRAS_PATH_SAVED_PHOTO, fullNamePath);
        getActivity().startActivity(saveIntent);
        Log.d("hoacode", "8");
        releaseUnused();
        Log.d("hoacode", "9");
        getActivity().finish();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void setStickerEditor(View view, int position, int curIndex) {
//        Log.d("Sticker", "position: "+position+"_curIndex: "+curIndex);
        ListAdaptiveItem listAdaptiveItem = mCollageView.getListItem();
        listAdaptiveItem.setCurrentItem(curIndex);
        BaseStickerModel baseItem = listAdaptiveItem.get(curIndex);
        if (baseItem == null)
            return;
        switch (position) {
            case 0: // opacity
                view.setBackground(ImageUtils.getBgClicked(getActivity()));
                replaceOpacityFrag(baseItem);
                break;
            case 1: // rotate 5 degree
                view.setBackground(ImageUtils.getBgClicked(getActivity()));
                baseItem.postMatrixRotated(BaseStickerModel.STEP_ROTATE);
                mCollageView.invalidate();
                break;
            case 2: // mirror 5 degree
                view.setBackground(ImageUtils.getBgClicked(getActivity()));
                baseItem.postMatrixRotated(-BaseStickerModel.STEP_ROTATE);
                mCollageView.invalidate();
                break;
            case 3: // up move
                baseItem.postMatrixTranslated(BaseStickerModel.MOVE_UP);
                setClickingEffect(view, !baseItem.isNotTranslate());
                break;
            case 4: // down move
                baseItem.postMatrixTranslated(BaseStickerModel.MOVE_DOWN);
                setClickingEffect(view, !baseItem.isNotTranslate());
                break;
            case 5: // left move
                baseItem.postMatrixTranslated(BaseStickerModel.MOVE_LEFT);
                setClickingEffect(view, !baseItem.isNotTranslate());
                break;
            case 6: // right move
                baseItem.postMatrixTranslated(BaseStickerModel.MOVE_RIGHT);
                setClickingEffect(view, !baseItem.isNotTranslate());
                break;
            case 7: // zoom in
                baseItem.postMatrixScaled(BaseStickerModel.STEP_SCALE_IN);
                setClickingEffect(view, !baseItem.isNotScale());
                break;
            default: // zoom out
                baseItem.postMatrixScaled(BaseStickerModel.STEP_SCALE_OUT);
                setClickingEffect(view, !baseItem.isNotScale());
                break;
        }
    }

    private void replaceOpacityFrag(BaseStickerModel item) {
        if (!(item instanceof IconStickerModel)) {
            return;
        }
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.slide_in_up, R.anim.no_anim);
        Bundle bundle = new Bundle();
        bundle.putInt(StickerOpacityFrag.OPACITY_KEY, ((IconStickerModel) item).getOpacity());
        Fragment curFragment = new StickerOpacityFrag();
        curFragment.setArguments(bundle);
        ft.replace(R.id.menu_footer, curFragment).addToBackStack(Statistic.TAG_STICKER_OPACITY_FRAG).commit();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void setPhotoEditor(View view, int position, int curIndex) {

        Log.d("hoacode", "curIndex="+curIndex);
        mListPhotoViews.setCurPhotoIndex(curIndex);
        PhotoViewModel photoModel = mListPhotoViews.get(curIndex);
        if (photoModel == null)
            return;

        switch (position) {
            case 0: // editor replace photo
                view.setBackground(ImageUtils.getBgClicked(getActivity()));
                openGallery(curIndex);
                break;
            case 1: // editor rotate 90 degree photo
                if (!photoModel.isContainsContent()) return;

                photoModel.postRotate(90);
                setClickingEffect(view, true);
                break;
            case 2: // editor mirror 90 degree photo
                if (!photoModel.isContainsContent()) return;
                view.setBackground(ImageUtils.getBgClicked(getActivity()));

                photoModel.postRotate(-90);
                break;
            case 3: // editor correct 90 degree photo
                photoModel.setHoriCurDegree();
                if (!photoModel.isContainsContent() || photoModel.isPerpendiculared()) {
                    view.setBackground(null);
                    return;
                }
                view.setBackground(ImageUtils.getBgClicked(getActivity()));
                photoModel.correctHoriDirection();
                break;
            case 4: // editor delete photo
                if (photoModel.isContainsContent()) {
//                    photoModel.getBitmap().eraseColor(Color.GRAY);
                    view.setBackground(ImageUtils.getBgClicked(getActivity()));
                    photoModel.containsContent(false);
                    mListPhotoModels.set(curIndex, null);
                    if (photoModelsIsEmpty()) {
                        FooterMenuFragment collageMenuFrag = new FooterMenuFragment();
                        getActivity().getSupportFragmentManager().beginTransaction()
                                .setCustomAnimations(R.anim.slide_in_up, R.anim.no_anim)
                                .add(R.id.menu_footer, collageMenuFrag).commit();
                    }
                } else {
                    view.setBackground(null);
                }
                break;
            case 5: // editor move up
                photoModel.postTranslate(0, -BaseStickerModel.MOVE_THRESHOLD);
                setClickingEffect(view, photoModel.isContainsContent());    //  && !photoModel.isNotMove()
                break;
            case 6: // editor move down
                photoModel.postTranslate(0, BaseStickerModel.MOVE_THRESHOLD);
                setClickingEffect(view, photoModel.isContainsContent());    //  && !photoModel.isNotMove()
                break;
            case 7: // editor move left
                photoModel.postTranslate(-BaseStickerModel.MOVE_THRESHOLD, 0);
                setClickingEffect(view, photoModel.isContainsContent());    //  && !photoModel.isNotMove()
                break;
            case 8: // editor move right
                photoModel.postTranslate(BaseStickerModel.MOVE_THRESHOLD, 0);
                setClickingEffect(view, photoModel.isContainsContent());    //   && !photoModel.isNotMove()
                break;
            case 9: // editor zoom in photo
                photoModel.postScale(photoModel.ZOOM_IN_RATIO);
                setClickingEffect(view, photoModel.isContainsContent() && !photoModel.isNotZoom());
                break;
            case 10: // editor zoom out photo
                photoModel.postScale(photoModel.ZOOM_OUT_RATIO);
                setClickingEffect(view, photoModel.isContainsContent() && !photoModel.isNotZoom());
                break;
            default: // editor edit photo
                Flog.d("edit"); // TODO:
                break;
        }
        mCollageView.invalidate();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void setClickingEffect(View view, boolean ok) {
        if (ok) {
            view.setBackground(ImageUtils.getBgClicked(getActivity()));
            mCollageView.invalidate();
        } else {
            view.setBackground(null);
        }
    }

    private boolean photoModelsIsEmpty() {
        for (int i = 0; i < mListPhotoModels.size(); i++) {
            if (mListPhotoModels.get(i) != null) {
                return false;
            }
        }
        return true;
    }

    public void setStickerOpacity(int progress) {
        if (mCollageView == null || !mCollageView.isCurrentItemType(BaseModel.ItemType.STICKER_ICON))
            return;

        int opacity = progress + Statistic.MIN_OPACITY;
        LibPref.getInstance().putInt(LibPref.KEY_OPACITY_SIZE, progress);
        IconStickerModel itemStickerView = (IconStickerModel) mCollageView.getCurrentItem();
        if (itemStickerView != null) {
            itemStickerView.setStickerPaint(opacity);
            mCollageView.invalidate();
        }
    }

    @Override
    public void onOpenGallery(int curPhotoIndex) {
        if (getFragmentManager().findFragmentById(R.id.menu_footer) instanceof MenuTextFrag) {
            mCollageView.dismissKeyboard();
            getFragmentManager().popBackStack("menuType_" + Statistic.MENU_COLLAGE_TYPE_TEXT
                    , FragmentManager.POP_BACK_STACK_INCLUSIVE);
        } else {
            openGallery(curPhotoIndex);
        }
    }

    @Override
    public void onShowEditor(int curPhotoIndex) {
//        Flog.d("onShowEditor");
        Fragment fragment = getActivity().getSupportFragmentManager().findFragmentById(R.id.menu_footer);
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        if (!(fragment instanceof PhotoEditorFrag)) {
            ft.setCustomAnimations(R.anim.slide_in_up, R.anim.no_anim);
        }
        ft.replace(R.id.menu_footer, new PhotoEditorFrag().setCurIndex(curPhotoIndex))
                .addToBackStack(Statistic.TAG_PHOTO_EDITOR_FRAG).commit();
    }

    @Override
    public void onSwapTwoObj(int curIndex, int dragIndex) {
        PhotoModel temp = mListPhotoModels.get(curIndex);
        mListPhotoModels.set(curIndex, mListPhotoModels.get(dragIndex));
        mListPhotoModels.set(dragIndex, temp);
        temp = null;
        /*Update footer photo-editor fragment*/
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.menu_footer, new PhotoEditorFrag().setCurIndex(dragIndex))
                .addToBackStack(Statistic.TAG_PHOTO_EDITOR_FRAG).commit();
    }

    @Override
    public void onStickerUnselected() {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                Fragment footerFragment = getFragmentManager().findFragmentById(R.id.menu_footer);
                Flog.d(TAG, "!(footerFragment instanceof FooterMenuFragment)=" + (!(footerFragment instanceof FooterMenuFragment)));
                if (!(footerFragment instanceof FooterMenuFragment)) {
//                    Log.d("opacity", "!(footerFragment instanceof FooterMenuFragment)");
                    FooterMenuFragment collageMenuFrag = new FooterMenuFragment();
                    getFragmentManager().beginTransaction()
                            .replace(R.id.menu_footer, collageMenuFrag).commit();
                }
                mCollageView.invalidate();
            }
        });
    }

    public void setTextZoomIn() {
        if (!mCollageView.isCurrentItemType(BaseModel.ItemType.STICKER_TEXT)) return;
        TextStickerModel view = (TextStickerModel) mCollageView.getCurrentItem();
        view.postMatrixScaled(BaseStickerModel.STEP_SCALE_IN);
        mCollageView.invalidate();
    }

    public void setTextZoomOut() {
        if (!mCollageView.isCurrentItemType(BaseModel.ItemType.STICKER_TEXT)) return;
        TextStickerModel view = (TextStickerModel) mCollageView.getCurrentItem();
        view.postMatrixScaled(BaseStickerModel.STEP_SCALE_OUT);
        mCollageView.invalidate();
    }

    public void setTextRotate5() {
        if (!mCollageView.isCurrentItemType(BaseModel.ItemType.STICKER_TEXT)) return;
        TextStickerModel view = (TextStickerModel) mCollageView.getCurrentItem();
        view.postMatrixRotated(BaseStickerModel.STEP_ROTATE);
        mCollageView.invalidate();
    }

    public void setTextMirror5() {
        if (!mCollageView.isCurrentItemType(BaseModel.ItemType.STICKER_TEXT)) return;
        TextStickerModel view = (TextStickerModel) mCollageView.getCurrentItem();
        view.postMatrixRotated(-BaseStickerModel.STEP_ROTATE);
        mCollageView.invalidate();
    }

    @Override
    public void onMovingItem(BaseModel item) {
        if (item instanceof TextStickerModel) {
            Fragment menuTextFrag = getActivity().getSupportFragmentManager().findFragmentById(R.id.menu_footer);
            Flog.d("dismissKeyboard", "dismissKeyboard");
            mCollageView.dismissKeyboard();
            if (menuTextFrag != null && menuTextFrag instanceof MenuTextFrag) {
//                Log.d(TAG, "onMovingBubbleTextView");
//                mCollageView.dismissKeyboard();
                if (((MenuTextFrag) menuTextFrag).getCurTab() == 0) {
//                    Log.d(TAG, "setHeigtPager to zero");
                    ((MenuTextFrag) menuTextFrag).setHeightPager(0);
                } else {
//                    Log.d(TAG, "just pop back stack");
                    getFragmentManager().popBackStack("menuType_" + Statistic.MENU_COLLAGE_TYPE_TEXT, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                }
            }
        }
    }

    @Override
    public void onStopMovingItem(BaseModel item) {

    }

    @Override
    public void onItemClicked(BaseModel item, boolean isFirstClick) {

        getFragmentManager().popBackStack(Statistic.TAG_PHOTO_EDITOR_FRAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        Fragment curFragment = getActivity().getSupportFragmentManager().findFragmentById(R.id.menu_footer);
        if (item instanceof TextStickerModel) {
            if (curFragment instanceof MenuTextFrag) {
                if (((MenuTextFrag) curFragment).getCurTab() == 0) {
                    mCollageView.requestKeyboard();
//                    Log.d(TAG, "height="+((MenuTextFrag) curFragment).getKeyboardHeight());
//                    ((MenuTextFrag) curFragment).onGlobalLayout();
                }
            } else {
                TextStickerModel bubbleTextView = (TextStickerModel) item;
                curFragment = new MenuTextFrag();
                Bundle bundle = new Bundle();
                bundle.putBoolean(MenuTextFrag.IS_INITIAL_EXPAND_KEY, !isFirstClick);
                bundle.putFloat(MenuTextFrag.TEXT_SIZE_KEY, bubbleTextView.getTextSize());
                bundle.putFloat(MenuTextFrag.PADDING_TEXT_KEY, bubbleTextView.getPadding());
                curFragment.setArguments(bundle);
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft
                        .replace(R.id.menu_footer, curFragment)
                        .addToBackStack("menuType_" + Statistic.MENU_COLLAGE_TYPE_TEXT).commit();
            }
        } else if (item instanceof IconStickerModel && isFirstClick && mCollageView.getListItem().isInEdit()) {
            Log.d(TAG, "onActionUpSticker");
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            ft.setCustomAnimations(R.anim.slide_in_up, R.anim.no_anim);
            curFragment = new StickerEditorFrag().setCurIndex(mCollageView.getListItem().getCurrentItemIndex());
            sCurStickerIdx = mCollageView.getListItem().getCurrentItemIndex();
            ft.replace(R.id.menu_footer, curFragment)
                    .addToBackStack(Statistic.TAG_STICKER_EDITOR_FRAG).commit();
        }
    }

    @Override
    public void onItemUnselected(BaseModel item) {
        Flog.d(TAG, "onItemUnselected");

        if (item instanceof TextStickerModel) {
            mCollageView.dismissKeyboard();
        }

//        new Handler().post(new Runnable() {
//            @Override
//            public void run() {
        Fragment footerFragment = getFragmentManager().findFragmentById(R.id.menu_footer);
        if (!(footerFragment instanceof FooterMenuFragment)) {
            FooterMenuFragment collageMenuFrag = new FooterMenuFragment();
            getFragmentManager().beginTransaction()
                    .replace(R.id.menu_footer, collageMenuFrag).commit();
        }
        mCollageView.invalidate();
//            }
//        });
    }

    @Override
    public void onItemDeleted(BaseModel item) {
        onItemUnselected(item);
    }

    public void setPatternText(String bgName) {
        if (!mCollageView.isCurrentItemType(BaseModel.ItemType.STICKER_TEXT)) return;
        TextStickerModel view = (TextStickerModel) mCollageView.getCurrentItem();
        view.setTextPattern(getActivity(), bgName);
        mCollageView.invalidate();
    }
}