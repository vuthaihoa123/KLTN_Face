package com.pic.libphotocollage.core.model;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Region;
import android.os.Build;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.pic.libphotocollage.core.CollageView;
import com.pic.libphotocollage.core.R;
import com.pic.libphotocollage.core.util.Flog;
import com.pic.libphotocollage.core.util.ImageUtils;

/**
 * Created by vutha on 1/9/2017.
 */

public class PhotoViewModel extends BaseModel {

    public static final float ZOOM_IN_RATIO = 1.05f;
    public static final float ZOOM_OUT_RATIO = 0.95f;
    private static final String TAG = PhotoViewModel.class.getSimpleName();
    public RectF mBoundsRect;
    public boolean mIsStopMoving;
    protected float MIN_SCALE;
    protected float MAX_SCALE;
    private Paint mDrawLinePaint;
    private Paint mPhotoPaint;
    private Path mPathRoundRect;
    private PointF mLeftBottomPoint;
    private PointF mRightTopPoint;
    private PointF mLeftTopPoint;
    private PointF mRightBottomPoint;
    private Path mPath;
    private Region mPathRegion;
    private float mOringinWidth;
    private float mOringinHeight;
    private boolean mIsSelected;
    private boolean mContainsContent = false;
    private int mOrgSrcWidth;
    private int mOrgSrcHeight;
    private float mRoundSize;
    private int mPriority = -1;
    private OnScanGalleryListenner mOnScanGalleryListenner;
    private float mOldScale;
    private PointF mMidPoint = new PointF();
    private boolean mIsGrid;
    private boolean mNotZoom;
    private float mHoriCurDegree;

    public PhotoViewModel(CollageView collageView) {
        super(collageView);
        setCustomPaint();
        setRoundHelper();
        init();
    }

    @Override
    public void draw(Canvas canvas) {

        // drawGrid roundness
        if (mIsGrid) {
            drawRoundnessRect(canvas);
        }

        // Clip path of each layout for drawing photo:
        try {
            // fix black bug
            if (!isOpenGallery())
                mPhotoPaint.setColor(Color.GRAY);
            else
                mPhotoPaint.setColor(Color.WHITE);

//                canvas.drawPath(mPath, mPhotoPaint);  // draw rect bounds & anti-alias
//                canvas.clipPath(mPath, Region.Op.INTERSECT);
            /**
             * Fix: White overlay for pile layout on device samsung
             * */
            canvas.clipPath(mPath);
            canvas.drawPath(mPath, mPhotoPaint);
        } catch (UnsupportedOperationException e) {
            Log.e(TAG, "clipPath() not supported");
        }

        if (!mContainsContent) {
            return;
        }


        if (mBitmap != null) {
            float[] arrayOfFloat = new float[9];
            matrix.getValues(arrayOfFloat);
            float f1 = 0.0F * arrayOfFloat[0] + 0.0F * arrayOfFloat[1] + arrayOfFloat[2];
            float f2 = 0.0F * arrayOfFloat[3] + 0.0F * arrayOfFloat[4] + arrayOfFloat[5];
            float f3 = arrayOfFloat[0] * this.mBitmap.getWidth() + 0.0F * arrayOfFloat[1] + arrayOfFloat[2];
            float f4 = arrayOfFloat[3] * this.mBitmap.getWidth() + 0.0F * arrayOfFloat[4] + arrayOfFloat[5];
            float f5 = 0.0F * arrayOfFloat[0] + arrayOfFloat[1] * this.mBitmap.getHeight() + arrayOfFloat[2];
            float f6 = 0.0F * arrayOfFloat[3] + arrayOfFloat[4] * this.mBitmap.getHeight() + arrayOfFloat[5];
            float f7 = arrayOfFloat[0] * this.mBitmap.getWidth() + arrayOfFloat[1] * this.mBitmap.getHeight() + arrayOfFloat[2];
            float f8 = arrayOfFloat[3] * this.mBitmap.getWidth() + arrayOfFloat[4] * this.mBitmap.getHeight() + arrayOfFloat[5];

            canvas.drawBitmap(mBitmap, matrix, mPhotoPaint);

            // draw border of touched photo
            if (mIsSelected) {
                drawSelectedBounds(canvas, mPath);
            }
            //in the lower left corner
            mLeftBottomPoint.set(f5, f6);
            // in the lower right corner
            mRightBottomPoint.set(f7, f8);
            //in the upper right corner
            mRightTopPoint.set(f3, f4);
            //in the upper left corner
            mLeftTopPoint.set(f1, f2);
        }
    }

    @Override
    public void release() {
        mBitmap = ImageUtils.recycleBitmap(mBitmap);
        mDrawLinePaint = null;
        mPhotoPaint = null;
        mPathRoundRect = null;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = MotionEventCompat.getActionMasked(event);
        boolean handled = true;
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                Flog.d(Flog.CTAG, "photo ACTION_DOWN");
                if (mPathRegion != null
                        && mPathRegion.contains((int) event.getX(), (int) event.getY())) {
                    isTranslating = true;
                    lastX = event.getX(0);
                    lastY = event.getY(0);

                    scanOpenGallery();
                } else {
                    handled = false;
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                Flog.d(Flog.CTAG, "photo ACTION_POINTER_DOWN");
                if (spacing(event) > pointerLimitDis) {
                    oldDis = spacing(event);
                    isPointerDown = true;
                    upMidPoint(event);
                    lastRotateDegree = getRotateDegree(event);
                } else {
                    isPointerDown = false;
                }
                isTranslating = false;
                break;
            case MotionEvent.ACTION_MOVE:
                Flog.d(Flog.CTAG, "photo ACTION_MOVE");
                //Two-finger scaling
                if (isPointerDown && event.getPointerCount() >= 2) {
                    Flog.d(Flog.CTAG, "photo ACTION_MOVE 2 fingers===");
                    mNotZoom = false;
                    float scale;
                    float newDis = spacing(event);
                    // Rotating
                    matrix.postRotate((getRotateDegree(event) - lastRotateDegree), mMidPoint.x, mMidPoint.y);
                    lastRotateDegree = getRotateDegree(event);
                    // Scaling
                    if (newDis == 0 || newDis < pointerLimitDis) {
                        scale = 1;
                        mNotZoom = true;
                    } else {
                        scale = newDis / oldDis;
                        //The zoom is slow
                        scale = (scale - 1) * pointerZoomCoeff + 1;
                        if (mOldScale == scale)
                            return false;
                    }

                    scale = getLimitedScale(scale);
                    matrix.postScale(scale, scale, mMidPoint.x, mMidPoint.y);
                    mOldScale = scale;
                    upMidPoint(event);
                    mCollageView.invalidate();

                } else if (isTranslating && !mIsStopMoving) {
                    float x = event.getX(0);
                    float y = event.getY(0);
                    Flog.d(Flog.CTAG, "photo ACTION_MOVE isTranslating");
                    //TODO: The movement area judgment can not exceed the screen
                    matrix.postTranslate(x - lastX, y - lastY);
                    lastX = x;
                    lastY = y;
                    mCollageView.invalidate();
                } else {

                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                Flog.d(Flog.CTAG, "photo ACTION_UP");
                isTranslating = false;
                isPointerDown = false;
                mIsStopMoving = false;
                break;

        }
        return handled;
    }

    private float getLimitedScale(float scale) {
        // limit for scaling
        mNotZoom = false;
        float scaleTempHorizontal = (scale * spacing(mLeftBottomPoint, mRightBottomPoint));
        float scaleTempVertical = (scale * spacing(mLeftBottomPoint, mLeftTopPoint));
        if (scaleTempHorizontal > scaleTempVertical) {
            scaleTempHorizontal = scaleTempVertical;
        }
        if (mOringinWidth > mOringinHeight)
            mOringinWidth = mOringinHeight;

        scaleTempHorizontal /= mOringinWidth;

        if (((scaleTempHorizontal <= MIN_SCALE) && (scale < 1)) || ((scaleTempHorizontal >= MAX_SCALE) && (scale > 1))) {
            mNotZoom = true;
            scale = 1;
        }
        return scale;
    }

    /**
     * The location of the touch and the mMidPointpoint of the top left corner of the image
     *
     * @param event
     */
    private void upMidPoint(MotionEvent event) {
        float[] arrayOfFloat = new float[9];
        matrix.getValues(arrayOfFloat);
        float f1 = event.getX(0);
        float f2 = event.getY(0);
        float f3 = event.getX(1);
        float f4 = event.getY(1);
//        Log.d(TAG, "f1="+f1+"_f2="+f2+"_f3="+f3+"_f4="+f4);
        mMidPoint.set((f1 + f3) / 2, (f2 + f4) / 2);
    }

    public void scanOpenGallery() {
        if (mOnScanGalleryListenner == null)
            return;
        if (!isOpenGallery()) mOnScanGalleryListenner.onOpenGallery(mPriority);
        else mOnScanGalleryListenner.onShowEditor(mPriority);
    }

    @Override
    public void setBitmap(Bitmap bitmap) {
        matrix.reset();
        mBitmap = bitmap;
        if (mBitmap == null)
            return;
        initLimitedScale();
        int w = mBitmap.getWidth();
        int h = mBitmap.getHeight();
        mOringinWidth = w;
        mOringinHeight = h;
        mOrgSrcWidth = w;
        mOrgSrcHeight = h;
        mCollageView.invalidate();
    }

    public void setBitmap(Bitmap bitmap, float x, float y) {
        matrix.reset();
        mBitmap = bitmap;
        initLimitedScale();
        int w = mBitmap.getWidth();
        int h = mBitmap.getHeight();
        mOringinWidth = w;
        mOringinHeight = h;
        mOrgSrcWidth = w;
        mOrgSrcHeight = h;

        matrix.postTranslate(x, y);
    }

    private void initLimitedScale() {
        //When the width of the picture width is calculated according to the width of the zoom size
        // according to the size of the picture to change the smallest picture of the 1/4 maximum screen width
        if (mBitmap.getWidth() >= mBitmap.getHeight()) {
            float minWidth = mScreenwidth / 4;
            if (mBitmap.getWidth() < minWidth) {
                MIN_SCALE = 1f;
            } else {
                MIN_SCALE = 1.0f * minWidth / mBitmap.getWidth();
            }

            if (mBitmap.getWidth() > mScreenwidth) {
                MAX_SCALE = 1;
            } else {
                MAX_SCALE = 1.0f * mScreenwidth / mBitmap.getWidth();
            }
        } else {
            //When the picture is higher than the large, according to the high picture calculation
            float minHeight = mScreenHeight / 4;
            if (mBitmap.getHeight() < minHeight) {
                MIN_SCALE = 1f;
            } else {
                MIN_SCALE = 1.0f * minHeight / mBitmap.getHeight();
            }

            if (mBitmap.getHeight() > mScreenHeight) {
                MAX_SCALE = 1;
            } else {
                MAX_SCALE = 1.0f * mScreenHeight / mBitmap.getHeight();
            }
        }
        MAX_SCALE += 1; // increase MAX_SCALE to 1 units.
    }

    private void setCustomPaint() {
        // set Line Round paint
        mDrawLinePaint = new Paint();
        mDrawLinePaint.setColor(Color.rgb(0, 174, 207));    // light blue color
        mDrawLinePaint.setStrokeWidth((int) mContext.getResources()
                .getDimension(R.dimen.collage_select_line_width));
        mDrawLinePaint.setStyle(Paint.Style.STROKE);
        mDrawLinePaint.setAntiAlias(true);

        // set anti-alias when clip path for paint
        mPhotoPaint = new Paint();
        mPhotoPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
//        mPhotoPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DARKEN));
    }

    private void setRoundHelper() {
        /* Canvas.clipPath() support with hardware acceleration has been reintroduced since API 18: */
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2
                && Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            mCollageView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        mPathRoundRect = new Path();
    }

    private void init() {

        initFourVerticesPoint();
        mDisplayMetrics = mContext.getResources().getDisplayMetrics();
        mScreenwidth = mDisplayMetrics.widthPixels;
        mScreenHeight = mDisplayMetrics.heightPixels;
        pointerZoomCoeff = 0.05f;
    }

    private void initFourVerticesPoint() {
        mLeftBottomPoint = new PointF();
        mRightBottomPoint = new PointF();
        mLeftTopPoint = new PointF();
        mRightTopPoint = new PointF();
    }

    public void setRegionPath(Path path) {
        mPath = path;
        mBoundsRect = new RectF();
        mPath.computeBounds(mBoundsRect, true);
        mPathRegion = new Region();
        mPathRegion.setPath(mPath, new Region((int) mBoundsRect.left, (int) mBoundsRect.top, (int) mBoundsRect.right, (int) mBoundsRect.bottom));
    }

    public void swapOtherPhoto(PhotoViewModel other) {
        Bitmap bmpTmp = this.mBitmap;
        this.setBitmap(other.getBitmap());
        other.setBitmap(bmpTmp);

        this.fitPhotoToLayout();
        other.fitPhotoToLayout();
    }

    public boolean isContainsContent() {
        return mContainsContent;
    }

    public void containsContent(boolean con) {
        mContainsContent = con;
    }

    public int getOrgPixelSize() {
        return (mOrgSrcWidth * mOrgSrcHeight);
    }

    public boolean isOpenGallery() {
        return mContainsContent;
    }

    public void setIsSelected(boolean isSelected) {
        mIsSelected = isSelected;
    }

    private void drawRoundnessRect(Canvas canvas) {
        mPathRoundRect.reset();
        mPathRoundRect.addRoundRect(mBoundsRect, mRoundSize, mRoundSize, Path.Direction.CW);
        try {
            canvas.clipPath(mPathRoundRect, Region.Op.REPLACE);
        } catch (UnsupportedOperationException e) {
            Log.e(TAG, "clipPath() not supported");
        }
    }

    public void drawSelectedBounds(Canvas canvas, Path path) {
        canvas.drawPath(path, mDrawLinePaint);
        if (mIsGrid) {
            canvas.drawPath(mPathRoundRect, mDrawLinePaint);
            canvas.drawRoundRect(mBoundsRect, mRoundSize, mRoundSize, mDrawLinePaint);
        }
    }

    public int getPriority() {
        return mPriority;
    }

    public void setPriority(int pos) {
        mPriority = pos;
    }

    public int getOrgSrcWidth() {
        return mOrgSrcWidth;
    }

    public int getOrgSrcHeight() {
        return mOrgSrcHeight;
    }

    public float getRoundSize() {
        return mRoundSize;
    }

    public void setRoundSize(float roundSize) {
        mRoundSize = roundSize;
    }

    public boolean isSelected() {
        return mIsSelected;
    }

    /**
     * Use the twice times:
     * First time: when view photo from gallery.
     * Second time: when update layout.
     */
    public void fitPhotoToLayout() {

        if (mBoundsRect == null || mBitmap == null || matrix == null)
            return;

        float centerX = mBoundsRect.centerX();
        float centerY = mBoundsRect.centerY();
        float leftTranslateX = (centerX - mBitmap.getWidth() / 2);
        float topTranslateY = (centerY - mBitmap.getHeight() / 2);

//        matrix.setTranslate(leftTranslateX, topTranslateY);
        float scaleW = 1f, scaleH = 1f;
        if (mBoundsRect.width() > mBitmap.getWidth())
            scaleW = mBoundsRect.width() / mBitmap.getWidth();
        if (mBoundsRect.height() > mBitmap.getHeight())
            scaleH = mBoundsRect.height() / mBitmap.getHeight();
        if (scaleW < scaleH)
            scaleW = scaleH;
        /*
        * Translate to center of rect:
        * */
        matrix.setTranslate(leftTranslateX, topTranslateY);
        /*
        * Center crop photo to rect:
        * */
        matrix.postScale(scaleW, scaleW, centerX, centerY);
    }

    public float getRotateDegree(MotionEvent event) {
        if (event.getPointerCount() >= 2) {
            double arc = Math.atan2(event.getY(1) - event.getY(0), event.getX(1) - event.getX(0));
            return (float) Math.toDegrees(arc);
        }
        return 0;
    }

    public float getRotateDegree(PointF p1, PointF p2) {
        double arc = Math.atan2(p2.y - p1.y, p2.x - p1.x);
        return (float) Math.toDegrees(arc);
    }

    public PhotoViewModel setOnScanGalleryListenner(OnScanGalleryListenner onScanGalleryListenner) {
        mOnScanGalleryListenner = onScanGalleryListenner;
        return this;
    }

    public RectF getBoundsRect() {
        return mBoundsRect;
    }

    public Region getPathRegion() {
        return mPathRegion;
    }

    public void setIsGrid(boolean isGrid) {
        mIsGrid = isGrid;
        if (!mIsGrid) {
            matrix.reset();
        }
    }

    public Path getPath() {
        return mPath;
    }

    public PointF getCurMidPoint() {
        return new PointF((mLeftTopPoint.x + mRightBottomPoint.x) / 2, (mLeftTopPoint.y + mRightBottomPoint.y) / 2);
    }

    public void postRotate(int degree) {
        PointF mid = getCurMidPoint();
        matrix.postRotate(degree, mid.x, mid.y);
    }

    public void postTranslate(int x, int y) {
        matrix.postTranslate(x, y);
    }

    public boolean isNotZoom() {
        return mNotZoom;
    }

    public void postScale(float ratio) {
        ratio = getLimitedScale(ratio);
        PointF mid = getCurMidPoint();
        matrix.postScale(ratio, ratio, mid.x, mid.y);
    }

    public void setIsStop(boolean stop) {
        mIsStopMoving = stop;
    }

    protected float spacing(PointF begin, PointF end) {
        float x = begin.x - end.x;
        float y = begin.y - end.y;
        return (float) Math.sqrt(x * x + y * y);
    }

    public void correctHoriDirection() {

        PointF mid = getCurMidPoint();
        float angle;
        if (mHoriCurDegree < 0) {
            if (Math.abs(mHoriCurDegree) > 90) angle = -(180 - Math.abs(mHoriCurDegree));
            else angle = Math.abs(mHoriCurDegree);
        } else {
            if (mHoriCurDegree > 90) angle = (180 - mHoriCurDegree);
            else angle = -mHoriCurDegree;

        }
        matrix.postRotate(angle, mid.x, mid.y);
    }

    public void setHoriCurDegree() {
        boolean cmp = mBitmap.getWidth() > mBitmap.getHeight();
        float dis1 = spacing(mLeftTopPoint, mRightTopPoint);
        float dis2 = spacing(mLeftTopPoint, mLeftBottomPoint);
        if ((cmp && (dis1 > dis2)) || (!cmp && (dis1 < dis2))) {
            mHoriCurDegree = getRotateDegree(mLeftTopPoint, mRightTopPoint);
        } else {
            mHoriCurDegree = getRotateDegree(mLeftTopPoint, mLeftBottomPoint);
        }
    }

    public boolean isPerpendiculared() {
        float cmper = Math.abs(mHoriCurDegree);
        return (cmper == 90 || cmper == 0 || cmper == 180 || cmper == 360);
    }

    public interface OnScanGalleryListenner {
        void onOpenGallery(int index);

        void onShowEditor(int index);
    }
}
