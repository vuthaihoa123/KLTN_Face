package uet.kltn.hoavt_58.crazyexface.photo_collage.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import uet.kltn.hoavt_58.crazyexface.R;


/**
 * Created by yue on 21/07/2016.
 */
public class LinearItemDecoration extends RecyclerView.ItemDecoration {
    private final Drawable mDivider;
    private int mSpace = 0;

    public LinearItemDecoration(Context context) {
        mDivider = context.getResources().getDrawable(R.drawable.line_divider);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                               RecyclerView.State state) {
        int position = parent.getChildLayoutPosition(view);
        if (position == 0 && outRect.top == 0) {
            outRect.top = mSpace;
        }
        outRect.bottom = mSpace;
        outRect.right = mSpace;
        outRect.left = mSpace;
    }
    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            int top = child.getBottom() + params.bottomMargin;
            int bottom = top + mDivider.getIntrinsicHeight();
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }
}
