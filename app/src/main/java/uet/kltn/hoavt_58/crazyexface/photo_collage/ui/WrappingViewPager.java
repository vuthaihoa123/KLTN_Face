package uet.kltn.hoavt_58.crazyexface.photo_collage.ui;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import uet.kltn.hoavt_58.crazyexface.photo_collage.fragment.othertabs.OtherBaseFrag;

/**
 * Created by vutha on 2/17/2017.
 */

public class WrappingViewPager extends ViewPager {

    private static final String TAG = WrappingViewPager.class.getSimpleName();
    private boolean mIsHide;

    public WrappingViewPager(Context context) {
        super(context);
    }

    public WrappingViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.d("onCloseCallback", "onMeasure isHide="+mIsHide);
        boolean wrapHeight = MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.AT_MOST;

        final View tab = getChildAt(0);
        int width = getMeasuredWidth();
        int tabHeight = tab.getMeasuredHeight();

        Log.d(TAG, "wrapHeight=" + wrapHeight);

        if (wrapHeight) {
//             Keep the current measured width.
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
        }

        Fragment fragment = ((OtherBaseFrag) getAdapter().instantiateItem(this, getCurrentItem()));
        int fragmentHeight = measureFragment(fragment.getView());
        if (mIsHide)
            fragmentHeight = 0;
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(fragmentHeight, MeasureSpec.AT_MOST);

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public int measureFragment(View view) {
        if (view == null)
            return 0;

        view.measure(0, 0);
        return view.getMeasuredHeight();
    }

    public void setIsHide(boolean isHide) {
        mIsHide = isHide;
    }

    private boolean enabled;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return enabled ? super.onTouchEvent(event) : false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return enabled ? super.onInterceptTouchEvent(event) : false;
    }

    public void setPagingEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isPagingEnabled() {
        return enabled;
    }
}
