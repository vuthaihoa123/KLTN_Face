package com.pic.libphotocollage.core.model;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.MotionEvent;

import com.pic.libphotocollage.core.CollageView;
import com.pic.libphotocollage.core.R;
import com.pic.libphotocollage.core.util.Flog;

/**
 * Created by vutha on 1/20/2017.
 */

public class DragPhotoModel extends BaseModel {

    private static final float SCALE_DOWN = 0.5f;
    private static final int DRAG_ALPHA_PAINT = 125;
    private float mMoveToCenterX, mMoveToCenterY;
    private CollageView mCollageView;
    private Paint mDragPaint;
    private OnSwapPhotosListener mSwapPhotosListener;
    private int mPhotoWidth, mPhotoHeight;

    public DragPhotoModel(CollageView collageView) {
        super(collageView);
        mCollageView = collageView;
        initPaint();
    }

    private void initPaint() {
        mDragPaint = new Paint();
        mDragPaint.setAlpha(DRAG_ALPHA_PAINT);
    }

    @Override
    public void draw(Canvas canvas) {
        if (canvas == null) return;
        if (mBitmap != null) {
//            canvas.save();
            canvas.drawBitmap(mBitmap, matrix, mDragPaint);
//            canvas.restore();
        }
    }

    @Override
    public void release() {
        mCollageView = null;
        mDragPaint = null;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int action = MotionEventCompat.getActionMasked(event);

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                float x = event.getX(0) - mMoveToCenterX;
                float y = event.getY(0) - mMoveToCenterY;
                matrix.postTranslate(x - lastX, y - lastY);
                lastX = x;
                lastY = y;
                mCollageView.invalidate();
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                mBitmap = null;
                Log.d("hoacode", "MotionEvent.ACTION_UP="+mSwapPhotosListener);
                if (mSwapPhotosListener != null) {
                    mSwapPhotosListener.onSwapAction(event);
                }
                break;
        }
        return true;
    }

    @Override
    public void setBitmap(Bitmap bitmap) {
        matrix.reset();
        mBitmap = bitmap;
    }

    public void setBitmap(Bitmap bitmap, float touchedX, float touchedY) {
        matrix.reset();
        mBitmap = bitmap;
        mPhotoWidth = mBitmap.getWidth();
        mPhotoHeight = mBitmap.getHeight();

        mMoveToCenterX = mPhotoWidth * SCALE_DOWN / 2;
        mMoveToCenterY = mPhotoHeight * SCALE_DOWN / 2;

        lastX = touchedX - mMoveToCenterX;
        lastY = touchedY - mMoveToCenterY;

        matrix.setScale(SCALE_DOWN, SCALE_DOWN);
        matrix.postTranslate(lastX, lastY);
    }

    public DragPhotoModel setSwapPhotosListener(OnSwapPhotosListener swapPhotosListener) {
        mSwapPhotosListener = swapPhotosListener;
        return this;
    }

    public interface OnSwapPhotosListener {
        void onSwapAction(MotionEvent event);
    }
}
