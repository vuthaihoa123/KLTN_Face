package com.pic.libphotocollage.core.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.util.DisplayMetrics;
import android.view.MotionEvent;

import com.pic.libphotocollage.core.CollageView;

/**
 * Created by vutha on 1/9/2017.
 */

public abstract class BaseModel {

    protected CollageView mCollageView;
    protected Context mContext;
    protected Bitmap mBitmap;
    protected Matrix matrix = new Matrix();
    protected ItemType mItemType;
    protected OnItemInteractListener mInteractListener;
    protected DisplayMetrics mDisplayMetrics;
    protected int mScreenwidth, mScreenHeight;
    protected boolean isTranslating;
    protected float lastX, lastY;
    protected boolean isPointerDown;
    protected float oldDis;
    protected PointF mid = new PointF();
    protected float lastRotateDegree;
    protected float pointerLimitDis = 20f;
    protected float pointerZoomCoeff = 0.09f;

    protected BaseModel(CollageView collageView) {
        this.mCollageView = collageView;
        this.mContext = collageView.getContext();
    }

    public abstract void draw(Canvas canvas);

    public abstract void release();

    public abstract boolean onTouchEvent(MotionEvent event);

    public OnItemInteractListener getInteractListener() {
        return mInteractListener;
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }

    public abstract void setBitmap(Bitmap bmp);

    public void setOnInteractListener(OnItemInteractListener itemInteractListener) {
        this.mInteractListener = itemInteractListener;
    }

    /**
     * Determine the space between the first two fingers
     */
    protected float spacing(MotionEvent event) {
        if (event.getPointerCount() == 2) {
            float x = event.getX(0) - event.getX(1);
            float y = event.getY(0) - event.getY(1);
            return (float) Math.sqrt(x * x + y * y);
        } else {
            return 0;
        }
    }

    protected void updateMiddlePoint(MotionEvent event) {
        float[] arrayOfFloat = new float[9];
        matrix.getValues(arrayOfFloat);
        float f1 = 0.0f * arrayOfFloat[0] + 0.0f * arrayOfFloat[1] + arrayOfFloat[2];
        float f2 = 0.0f * arrayOfFloat[3] + 0.0f * arrayOfFloat[4] + arrayOfFloat[5];
        float f3 = f1 + event.getX(0);
        float f4 = f2 + event.getY(0);
        mid.set(f3 / 2, f4 / 2);
    }

    protected float rotationToStartPoint(MotionEvent event) {

        float[] arrayOfFloat = new float[9];
        matrix.getValues(arrayOfFloat);
        float x = 0.0f * arrayOfFloat[0] + 0.0f * arrayOfFloat[1] + arrayOfFloat[2];
        float y = 0.0f * arrayOfFloat[3] + 0.0f * arrayOfFloat[4] + arrayOfFloat[5];
        double arc = Math.atan2(event.getY(0) - y, event.getX(0) - x);

        return (float) Math.toDegrees(arc);
    }

    public Matrix getMatrix() {
        return matrix;
    }

    public void setMatrix(Matrix matrix) {
        this.matrix = matrix;
    }

    public enum ItemType {
        PHOTO, STICKER_TEXT, STICKER_ICON
    }

    public interface OnItemInteractListener {
        void onMovingItem(BaseModel item);

        void onStopMovingItem(BaseModel item);

        void onItemClicked(BaseModel item, boolean isFirstClick);

        void onItemUnselected(BaseModel item);

        void onItemDeleted(BaseModel item);
    }
}
