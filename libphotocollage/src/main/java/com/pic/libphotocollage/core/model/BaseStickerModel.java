package com.pic.libphotocollage.core.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewGroup;

import com.pic.libphotocollage.core.CollageView;
import com.pic.libphotocollage.core.R;
import com.pic.libphotocollage.core.util.Flog;

/**
 * Created by vutha on 1/9/2017.
 */

public abstract class BaseStickerModel extends BaseModel {

    public static final float BITMAP_SCALE = 0.7f;
    public static final int MOVE_UP = 0;
    public static final int MOVE_DOWN = 1;
    public static final int MOVE_LEFT = 2;
    public static final int MOVE_RIGHT = 3;
    public static final int MOVE_THRESHOLD = 4;
    public static final float STEP_MOVE = 10f;
    public static final float STEP_ROTATE = 5f;
    public static final float STEP_SCALE_IN = 1.1f;
    public static final float STEP_SCALE_OUT = 0.9f;
    private static final java.lang.String TAG = BaseStickerModel.class.getSimpleName();
    public final float moveLimitDis = 10f;
    protected Bitmap deleteBitmap;
    protected Bitmap resizeBitmap;
    protected Bitmap flipVBitmap;
    protected Bitmap topBitmap;
    protected Rect dst_delete;
    protected Rect dst_resize;
    protected Rect dst_flipV;
    protected Rect dst_top;
    protected int deleteBitmapWidth;
    protected int deleteBitmapHeight;
    protected int resizeBitmapWidth;
    protected int resizeBitmapHeight;
    protected int flipVBitmapWidth;
    protected int flipVBitmapHeight;
    protected int topBitmapWidth;
    protected int topBitmapHeight;
    protected Paint mLinePaint;
    protected double halfDiagonalLength;
    protected float originalWidth;
    protected float MIN_SCALE = 0.6f;
    protected float MAX_SCALE = 1.5f;
    protected boolean isInEdit = false;
    protected boolean isInResize;
    protected float lastRotateDegree;
    protected float lastLength;
    protected boolean isHorizonMirror;
    protected boolean isMove;
    protected boolean isUp;
    protected boolean isDeleted;
    private boolean mNotTranslate = false;
    private boolean mNotScale = false;
    private boolean outSide = false;
    protected float scale = 1.0f;
    protected boolean mIsSetShader = false;

    protected BaseStickerModel(CollageView collageView) {
        super(collageView);
    }

    public void initLocalPaint() {
        mLinePaint = new Paint();
        mLinePaint.setFilterBitmap(true);
        mLinePaint.setColor(mContext.getResources().getColor(R.color.red_e73a3d));
        mLinePaint.setAntiAlias(true);
        mLinePaint.setDither(true);
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setStrokeWidth(2.0f);
    }

    public void allocateBitmaps() {
        if (topBitmap == null)
            topBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.icon_top_enable);
        if (deleteBitmap == null)
            deleteBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.icon_delete);
        if (flipVBitmap == null)
            flipVBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.icon_flip);
        if (resizeBitmap == null)
            resizeBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.icon_resize);

        deleteBitmapWidth = (int) (deleteBitmap.getWidth() * BITMAP_SCALE);
        deleteBitmapHeight = (int) (deleteBitmap.getHeight() * BITMAP_SCALE);

        resizeBitmapWidth = (int) (resizeBitmap.getWidth() * BITMAP_SCALE);
        resizeBitmapHeight = (int) (resizeBitmap.getHeight() * BITMAP_SCALE);

        flipVBitmapWidth = (int) (flipVBitmap.getWidth() * BITMAP_SCALE);
        flipVBitmapHeight = (int) (flipVBitmap.getHeight() * BITMAP_SCALE);

        topBitmapWidth = (int) (topBitmap.getWidth() * BITMAP_SCALE);
        topBitmapHeight = (int) (topBitmap.getHeight() * BITMAP_SCALE);
    }

    public void setDiagonalLength() {
        halfDiagonalLength = Math.hypot(mBitmap.getWidth(), mBitmap.getHeight()) / 2;
    }

    public void initScaleBounds() {
        float minWidth = mCollageView.getWidth() / 8;
        if (mBitmap.getWidth() < minWidth) {
            MIN_SCALE = 1f;
        } else {
            MIN_SCALE = 1.0f * minWidth / mBitmap.getWidth();
        }

        if (mBitmap.getWidth() > mCollageView.getWidth()) {
            MAX_SCALE = 1;
        } else {
            MAX_SCALE = 1.0f * mCollageView.getWidth() / mBitmap.getWidth();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d("sticker", "onTouchEvent: BaseStickerModel=" +(!isInEdit));
        if (!isInEdit || isDeleted) {
            return false;
        }
        int action = MotionEventCompat.getActionMasked(event);
        boolean handled = true;
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                Log.d("sticker", "ACTION_DOWN BaseStickerModel");
                if (isInButton(event, dst_delete)) {
                    isDeleted = true;
                    //dismiss because of press delete btn"); 4136:bvcv  4091:aaa
                    mCollageView.post(new Runnable() {
                        @Override
                        public void run() {
                            int index = mCollageView.getListItem().indexOf(BaseStickerModel.this);
                            mCollageView.getListItem().remove(index);
                            release();
                        }
                    });

                    if (mInteractListener != null) {
                        mInteractListener.onItemDeleted(this);
                    }
                    return false;
                } else if (isInResize(event)) {
                    isInResize = true;
                    lastRotateDegree = rotationToStartPoint(event);
                    updateMiddlePoint(event);
                    lastLength = diagonalLength(event);

                } else if (isInButton(event, dst_flipV)) {
                    //水平镜像
                    PointF localPointF = new PointF();
                    midDiagonalPoint(localPointF);
                    matrix.postScale(-1.0F, 1.0F, localPointF.x, localPointF.y);
                    isHorizonMirror = !isHorizonMirror;
                } else if (isInBitmap(event)) {
                    Flog.d(TAG, "isInBitmap");
                    isTranslating = true;
                    lastX = event.getX(0);
                    lastY = event.getY(0);
                    isMove = false;
                    isPointerDown = false;
                    return true;
                } else {
                    Flog.d(TAG, "outside");
                    handled = false;
                    outSide = isTranslating;
                    setInEdit(false);
                    //TODO: hide keyboard
                    //dismiss because of click outside textview
                    mCollageView.invalidate();
                    if (mInteractListener != null) {
                        mInteractListener.onItemUnselected(this);
                    }
                    return handled;
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                //TODO: hide keyboard
//                mCollageView.dismissKeyboard();

                if (spacing(event) > pointerLimitDis) {
                    oldDis = spacing(event);
                    isPointerDown = true;
                    updateMiddlePoint(event);
                } else {
                    isPointerDown = false;
                }
                isTranslating = false;
                isInResize = false;
                break;
            case MotionEvent.ACTION_MOVE:
                Log.d("sticker", "ACTION_MOVE BaseStickerModel");
                if (isPointerDown) {
                    float scale;
                    float disNew = spacing(event);
                    if (disNew == 0 || disNew < pointerLimitDis) {
                        scale = 1;
                    } else {
                        scale = disNew / oldDis;
                        //缩放缓慢
                        scale = (scale - 1) * pointerZoomCoeff + 1;
                    }
                    float scaleTemp = (scale * Math.abs(dst_flipV.left - dst_resize.left)) / originalWidth;
                    if (((scaleTemp <= MIN_SCALE)) && scale < 1 ||
                            (scaleTemp >= MAX_SCALE) && scale > 1) {
                        scale = 1;
                    } else {
                        lastLength = diagonalLength(event);
                    }
//                    if (mItemType==ItemType.STICKER_ICON)
                        matrix.postScale(scale, scale, mid.x, mid.y);

                } else if (isInResize) {
                    matrix.postRotate((rotationToStartPoint(event) - lastRotateDegree) * 2, mid.x, mid.y);
                    lastRotateDegree = rotationToStartPoint(event);

                    scale = diagonalLength(event) / lastLength;

                    if (((diagonalLength(event) / halfDiagonalLength <= MIN_SCALE)) && scale < 1 ||
                            (diagonalLength(event) / halfDiagonalLength >= MAX_SCALE) && scale > 1) {
                        scale = 1;
                        if (!isInResize(event)) {
                            isInResize = false;
                        }
                    } else {
                        lastLength = diagonalLength(event);
                    }

//                    if (mItemType==ItemType.STICKER_ICON)
                        matrix.postScale(scale, scale, mid.x, mid.y);

//                    invalidate();
                } else if (isTranslating) {
                    float x = event.getX(0);
                    float y = event.getY(0);
                    //判断手指抖动距离 加上isMove判断 只要移动过 都是true
                    if (!isMove && Math.abs(x - lastX) < moveLimitDis
                            && Math.abs(y - lastY) < moveLimitDis) {
                        isMove = false;
                    } else {
                        if (!checkOverBounds(event)) {
                            isMove = true;
                            matrix.postTranslate(x - lastX, y - lastY);
                            lastX = x;
                            lastY = y;
                        } else {
                            isMove = false;
                        }
                    }
                    if (mInteractListener != null && isMove) {
                        mInteractListener.onMovingItem(this);
                    }

                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
//                Flog.d(TAG, "ACTION_UP");
                if (mInteractListener != null && isTranslating && !isMove && isInEdit) {
//                    firstClick = false;
                    mInteractListener.onItemClicked(this, false);
                }
                if (mInteractListener != null && isMove) {
//                    mInteractListener.onStopMovingItem(this);
                }
                isInResize = false;
                isTranslating = false;
                isPointerDown = false;
                isUp = true;
                isMove = false;
                break;
        }
//        Flog.d(TAG, ".............handled="+handled);
        if (handled) {
            this.isInEdit = true;
        }

        return handled;
    }

    protected boolean isInButton(MotionEvent event, Rect rect) {
        int left = rect.left;
        int right = rect.right;
        int top = rect.top;
        int bottom = rect.bottom;
        return event.getX(0) >= left && event.getX(0) <= right && event.getY(0) >= top && event.getY(0) <= bottom;
    }

    protected boolean isInResize(MotionEvent event) {
        int left = -20 + this.dst_resize.left;
        int top = -20 + this.dst_resize.top;
        int right = 20 + this.dst_resize.right;
        int bottom = 20 + this.dst_resize.bottom;
        return event.getX(0) >= left && event.getX(0) <= right && event.getY(0) >= top && event.getY(0) <= bottom;
    }

    protected float diagonalLength(MotionEvent event) {
        float diagonalLength = (float) Math.hypot(event.getX(0) - mid.x, event.getY(0) - mid.y);
        return diagonalLength;
    }

    protected void midDiagonalPoint(PointF paramPointF) {
        float[] arrayOfFloat = new float[9];
        this.matrix.getValues(arrayOfFloat);
        float f1 = 0.0F * arrayOfFloat[0] + 0.0F * arrayOfFloat[1] + arrayOfFloat[2];
        float f2 = 0.0F * arrayOfFloat[3] + 0.0F * arrayOfFloat[4] + arrayOfFloat[5];
        float f3 = arrayOfFloat[0] * this.mBitmap.getWidth() + arrayOfFloat[1] * this.mBitmap.getHeight() + arrayOfFloat[2];
        float f4 = arrayOfFloat[3] * this.mBitmap.getWidth() + arrayOfFloat[4] * this.mBitmap.getHeight() + arrayOfFloat[5];
        float f5 = f1 + f3;
        float f6 = f2 + f4;
        paramPointF.set(f5 / 2.0F, f6 / 2.0F);
    }

    public boolean isInBitmap(MotionEvent event) {
        float[] arrayOfFloat1 = new float[9];
        this.matrix.getValues(arrayOfFloat1);
        //左上角
        float f1 = 0.0F * arrayOfFloat1[0] + 0.0F * arrayOfFloat1[1] + arrayOfFloat1[2];
        float f2 = 0.0F * arrayOfFloat1[3] + 0.0F * arrayOfFloat1[4] + arrayOfFloat1[5];
        //右上角
        float f3 = arrayOfFloat1[0] * this.mBitmap.getWidth() + 0.0F * arrayOfFloat1[1] + arrayOfFloat1[2];
        float f4 = arrayOfFloat1[3] * this.mBitmap.getWidth() + 0.0F * arrayOfFloat1[4] + arrayOfFloat1[5];
        //左下角
        float f5 = 0.0F * arrayOfFloat1[0] + arrayOfFloat1[1] * this.mBitmap.getHeight() + arrayOfFloat1[2];
        float f6 = 0.0F * arrayOfFloat1[3] + arrayOfFloat1[4] * this.mBitmap.getHeight() + arrayOfFloat1[5];
        //右下角
        float f7 = arrayOfFloat1[0] * this.mBitmap.getWidth() + arrayOfFloat1[1] * this.mBitmap.getHeight() + arrayOfFloat1[2];
        float f8 = arrayOfFloat1[3] * this.mBitmap.getWidth() + arrayOfFloat1[4] * this.mBitmap.getHeight() + arrayOfFloat1[5];

        float[] arrayOfFloat2 = new float[4];
        float[] arrayOfFloat3 = new float[4];
        //确定X方向的范围
        arrayOfFloat2[0] = f1;//左上的左
        arrayOfFloat2[1] = f3;//右上的右
        arrayOfFloat2[2] = f7;//右下的右
        arrayOfFloat2[3] = f5;//左下的左
        //确定Y方向的范围
        arrayOfFloat3[0] = f2;//左上的上
        arrayOfFloat3[1] = f4;//右上的上
        arrayOfFloat3[2] = f8;
        arrayOfFloat3[3] = f6;
        return pointInRect(arrayOfFloat2, arrayOfFloat3, event.getX(0), event.getY(0));
    }

    private boolean pointInRect(float[] xRange, float[] yRange, float x, float y) {
        //四条边的长度
        double a1 = Math.hypot(xRange[0] - xRange[1], yRange[0] - yRange[1]);
        double a2 = Math.hypot(xRange[1] - xRange[2], yRange[1] - yRange[2]);
        double a3 = Math.hypot(xRange[3] - xRange[2], yRange[3] - yRange[2]);
        double a4 = Math.hypot(xRange[0] - xRange[3], yRange[0] - yRange[3]);
        //待检测点到四个点的距离
        double b1 = Math.hypot(x - xRange[0], y - yRange[0]);
        double b2 = Math.hypot(x - xRange[1], y - yRange[1]);
        double b3 = Math.hypot(x - xRange[2], y - yRange[2]);
        double b4 = Math.hypot(x - xRange[3], y - yRange[3]);

        double u1 = (a1 + b1 + b2) / 2;
        double u2 = (a2 + b2 + b3) / 2;
        double u3 = (a3 + b3 + b4) / 2;
        double u4 = (a4 + b4 + b1) / 2;

        //矩形的面积
        double s = a1 * a2;
        double ss = Math.sqrt(u1 * (u1 - a1) * (u1 - b1) * (u1 - b2))
                + Math.sqrt(u2 * (u2 - a2) * (u2 - b2) * (u2 - b3))
                + Math.sqrt(u3 * (u3 - a3) * (u3 - b3) * (u3 - b4))
                + Math.sqrt(u4 * (u4 - a4) * (u4 - b4) * (u4 - b1));
        return Math.abs(s - ss) < 0.5;
    }

    protected boolean checkOverBounds(MotionEvent event) {
        float rx = event.getRawX();
        float ry = event.getRawY();
        int[] l = new int[2];
        mCollageView.getLocationOnScreen(l);
        int x = l[0];
        int y = l[1];
        int w = mCollageView.getWidth();
        int h = mCollageView.getHeight();

        if (rx < x || rx > x + w || ry < y || ry > y + h) {
            return true;
        }
        return false;
    }

    public boolean isInEdit() {
        return isInEdit;
    }

    public void setInEdit(boolean isInEdit) {
        this.isInEdit = isInEdit;
    }

    public Matrix getMatrix() {
        return matrix;
    }

    public void postMatrixRotated(float angel) {
        initMidPoint();
        matrix.postRotate(angel * 1.0f, mid.x, mid.y);
    }

    private void initMidPoint() {
        float[] arrayOfFloat = new float[9];
        matrix.getValues(arrayOfFloat);
        float f7 = arrayOfFloat[0] * this.mBitmap.getWidth() + arrayOfFloat[1] * this.mBitmap.getHeight() + arrayOfFloat[2];
        float f8 = arrayOfFloat[3] * this.mBitmap.getWidth() + arrayOfFloat[4] * this.mBitmap.getHeight() + arrayOfFloat[5];
        midPointToStartPoint(f7, f8);
    }

    protected void midPointToStartPoint(float f7, float f8) {

        float[] arrayOfFloat = new float[9];
        matrix.getValues(arrayOfFloat);
        float f1 = 0.0f * arrayOfFloat[0] + 0.0f * arrayOfFloat[1] + arrayOfFloat[2];
        float f2 = 0.0f * arrayOfFloat[3] + 0.0f * arrayOfFloat[4] + arrayOfFloat[5];
        float f3 = f1 + f7;
        float f4 = f2 + f8;
        mid.set(f3 / 2, f4 / 2);
    }

    public void postMatrixTranslated(int direction) {
        initMidPoint();
        if (checkOverBounds(direction, mid.x, mid.y)) {
            mNotTranslate = true;
            return;
        }
        mNotTranslate = false;

        switch (direction) {
            case MOVE_UP:
                matrix.postTranslate(0, -STEP_MOVE);
                break;
            case MOVE_DOWN:
                matrix.postTranslate(0, STEP_MOVE);
                break;
            case MOVE_LEFT:
                matrix.postTranslate(-STEP_MOVE, 0);
                break;
            case MOVE_RIGHT:
                matrix.postTranslate(STEP_MOVE, 0);
                break;
            default:
                break;
        }
    }

    protected boolean checkOverBounds(int direction, float dx, float dy) {
        float rx = dx;
        float ry = dy;
        int[] l = new int[2];
        mCollageView.getLocationOnScreen(l);
        int x = 0;
        int y = 0;
        int w = mCollageView.getWidth();
        int h = mCollageView.getHeight();

        switch (direction) {
            case MOVE_UP:
                if (ry < y)
                    return true;
                break;
            case MOVE_DOWN:
                if (ry > y + h)
                    return true;
                break;
            case MOVE_LEFT:
                if (rx < x)
                    return true;
                break;
            case MOVE_RIGHT:
                if (rx > x + w)
                    return true;
                break;
            default:
                break;
        }
        return false;
    }

    public boolean isNotTranslate() {
        return mNotTranslate;
    }

    public boolean isNotScale() {
        return mNotScale;
    }

    public void postMatrixScaled(float scale) {
        initMidPoint();

        float f7 = (dst_resize.right + dst_resize.left) / 2;
        float f8 = (dst_resize.top + dst_resize.bottom) / 2;
        if (((diagonalLength(f7, f8) /
                halfDiagonalLength <= MIN_SCALE)) && scale < 1 ||
                (diagonalLength(f7, f8) / halfDiagonalLength >= MAX_SCALE) && scale > 1) {
            mNotScale = true;
            return;
        }
        mNotScale = false;
        matrix.postScale(scale, scale, mid.x, mid.y);
    }

    protected float diagonalLength(float f7, float f8) {
        float diagonalLength = (float) Math.hypot(f7 - mid.x, f8 - mid.y);
        return diagonalLength;
    }

    public boolean isItemDeleted() {
        return isDeleted;
    }

    public void invalidateRatio() {
        int[] oldSize = mCollageView.getOldSize();
        ViewGroup.LayoutParams params = mCollageView.getLayoutParams();
        float ratioWidth = (float) params.width / oldSize[0];
        float ratioHeight = (float) params.height / oldSize[1];
        float[] values = new float[9];
        matrix.getValues(values);
        float X_TRANS = values[Matrix.MTRANS_X];
        float Y_TRANS = values[Matrix.MTRANS_Y];

        values[Matrix.MTRANS_X] *= ratioWidth;
        values[Matrix.MTRANS_Y] *= ratioHeight;
        matrix.setValues(values);
        X_TRANS = values[Matrix.MTRANS_X];
        Y_TRANS = values[Matrix.MTRANS_Y];
        mCollageView.invalidate();
    }

    public ItemType getItemType() {
        return mItemType;
    }

    public boolean isOutSide() {
        return outSide;
    }

    public void setOutSide(boolean outSide) {
        this.outSide = outSide;
    }
}
