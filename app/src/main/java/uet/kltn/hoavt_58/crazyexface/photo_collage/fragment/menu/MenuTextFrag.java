package uet.kltn.hoavt_58.crazyexface.photo_collage.fragment.menu;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;

import com.pic.libphotocollage.core.CollageView;
import com.pic.libphotocollage.core.util.SystemUtils;

import io.karim.MaterialTabs;
import uet.kltn.hoavt_58.crazyexface.R;
import uet.kltn.hoavt_58.crazyexface.photo_collage.adapter.menu.MenuTextAdapter;
import uet.kltn.hoavt_58.crazyexface.photo_collage.fragment.BaseFragment;
import uet.kltn.hoavt_58.crazyexface.photo_collage.fragment.texttabs.TextBaseFragment;

/**
 * Created by Adm on 8/8/2016.
 */
public class MenuTextFrag extends BaseFragment implements MaterialTabs.OnTabSelectedListener, ViewTreeObserver.OnGlobalLayoutListener {

    public static final String IS_INITIAL_EXPAND_KEY = "EXPAND_KEY";
    public static final String TEXT_SIZE_KEY = "TEXT_SIZE";
    public static final String PADDING_TEXT_KEY = "PADDING_TEXT";
    private static final String TAG = MenuTextFrag.class.getSimpleName();
    private View mRootView;
    private ViewPager viewPager = null;
    private MaterialTabs tabs = null;
    private Rect r = new Rect();
    private int keyboardHeight = -1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.content_collage_menu_text_view, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initTabLayout();
    }

    private void initTabLayout() {
        viewPager = (ViewPager) getView().findViewById(R.id.view_pager);
        tabs = (MaterialTabs) getView().findViewById(R.id.material_tabs);
        viewPager.setAdapter(new MenuTextAdapter(getChildFragmentManager(), getArguments()));
        tabs.setViewPager(viewPager);
        tabs.setOnTabSelectedListener(this);
        tabs.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                handleSelectedTab(position);
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        Log.d(TAG, "getActivity() instanceof TextBaseFragment.OnTextOptionCallback = " + (getActivity() instanceof TextBaseFragment.OnTextOptionCallback));
        Log.d(TAG, "getArguments().getBoolean(IS_INITIAL_EXPAND_KEY, true) = " + getArguments().getBoolean(IS_INITIAL_EXPAND_KEY, true));
//        if (getActivity() instanceof TextBaseFragment.OnTextOptionCallback &&
//                getArguments() != null && getArguments().getBoolean(IS_INITIAL_EXPAND_KEY, true)) {
//            Log.d(TAG, "onKeyBoardSoftDisplayed");
//            ((TextBaseFragment.OnTextOptionCallback) getActivity()).onKeyBoardSoftDisplayed();
//        }

    }

    public void setHeightPager(int height) {
        Log.d(TAG, "setHeightPager = " + height);
        if (height == -1) return;
        ViewGroup.LayoutParams params = viewPager.getLayoutParams();
        params.height = height;
        viewPager.setLayoutParams(params);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume add");
        final Window mRootWindow = getActivity().getWindow();
        mRootView = mRootWindow.getDecorView().findViewById(android.R.id.content);
        mRootView.getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    public int getKeyboardHeight() {
        return keyboardHeight;
    }

    @Override
    public void onPause() {
        super.onPause();
//        Log.d(TAG, "onPause remove");
        mRootView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
    }

    public int getCurTab() {
        return viewPager.getCurrentItem();
    }

    @Override
    public void onTabSelected(int position) {

        handleSelectedTab(position);
    }

    private void handleSelectedTab(int position) {
        if (position == 0) {
            if (getActivity() instanceof TextBaseFragment.OnTextOptionCallback) {
                ((TextBaseFragment.OnTextOptionCallback) getActivity()).onKeyBoardSoftDisplayed();
            }
        } else if (position == 1) {
            if (getActivity() instanceof TextBaseFragment.OnTextOptionCallback) {
                ((TextBaseFragment.OnTextOptionCallback) getActivity()).onFontDisplayed();
            }
        } else {
            if (getActivity() instanceof TextBaseFragment.OnTextOptionCallback) {
                ((TextBaseFragment.OnTextOptionCallback) getActivity()).onSettingDisplayed();
            }
        }

        if (position != 0 && !getArguments().getBoolean(IS_INITIAL_EXPAND_KEY, true)) {
            DisplayMetrics displaymetrics = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
            int height = (int) (3.5f * displaymetrics.heightPixels / 10);
            setHeightPager(height);
        }

        setHeightPager(keyboardHeight);
    }

    @Override
    public void onGlobalLayout() {
        int oldBottom = r.bottom;
        View view = getActivity().getWindow().getDecorView();
        view.getWindowVisibleDisplayFrame(r);
        int height = oldBottom - r.bottom;
        if (height > SystemUtils.dpToPx(getActivity(), 150)) {
//            Log.d(TAG, "setHeight");
            keyboardHeight = height;
            setHeightPager(height);
            getArguments().putBoolean(IS_INITIAL_EXPAND_KEY, true);
        } else if (keyboardHeight != 0 && Math.abs(height) == keyboardHeight && getCurTab() == 0) {
            CollageView collageView = (CollageView) getActivity().findViewById(R.id.collage_view);
            collageView.hideRealEditText();
            setHeightPager(0);
        }
    }
}
