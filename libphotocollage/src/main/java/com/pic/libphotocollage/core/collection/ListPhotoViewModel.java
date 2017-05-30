package com.pic.libphotocollage.core.collection;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.Shader;
import android.support.annotation.ColorInt;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MotionEvent;

import com.pic.libphotocollage.core.R;
import com.pic.libphotocollage.core.model.BaseModel;
import com.pic.libphotocollage.core.model.PhotoViewModel;
import com.pic.libphotocollage.core.util.Flog;
import com.pic.libphotocollage.core.util.ImageUtils;

import java.util.ArrayList;

/**
 * Created by vutha on 1/10/2017.
 */

public class ListPhotoViewModel extends ArrayList<PhotoViewModel> {

    private static final String TAG = ListPhotoViewModel.class.getSimpleName();
    private int mCurPhotoIndex = -1;
    private int mDraggedPhotoIndex = -1;
    private boolean mIsSavePhoto;
    private float mChangedRatio = 1.0f;
    private int mBgColor = -1;

    private boolean mIsDrawGrid = true;
    private Context mContext;
    private Bitmap mPileFrameBmp;
    private Bitmap mBgBitmap = null;
    private Bitmap mFrameBmp;
    private Rect mBackgroundRect;
    private Path mCanvasPath;
    private ArrayList<Path> mGridClipPaths;
    private ArrayList<Path> mPileClipPaths;
    private Path mBgPath;
    private int[] mDrawOrders;
    private Paint mPileFramePaint;
    private Paint mBackgroundPaint;
    private Paint mFramePaint;
    private int mFrameWidth = 20;

    public ListPhotoViewModel(Context context) {
        mContext = context;
        initPaint();
        setBgCanvas();
        if (mBgColor == -1)
            mBgColor = ContextCompat.getColor(mContext, R.color.colorSecondary);
    }

    private void setBgCanvas() {
        mBgPath = new Path();
    }

    private void initPaint() {
        mPileFramePaint = new Paint(Paint.FILTER_BITMAP_FLAG);
        mPileFramePaint.setAntiAlias(true);
        mPileFramePaint.setFlags(Paint.ANTI_ALIAS_FLAG);

        mBackgroundPaint = new Paint(Paint.FILTER_BITMAP_FLAG);
        mFramePaint = new Paint(Paint.FILTER_BITMAP_FLAG);
    }

    public int[] getDrawOrders() {
        return mDrawOrders;
    }

    public void setDrawOrders(int[] drawOrders) {
        mDrawOrders = drawOrders;
    }

    public int getTouchedIndex(MotionEvent event) {
        if (event == null)
            return -1;

        int index = getTouchedIndex(event.getX(), event.getY());
        return index;
    }

    public int getTouchedIndex(float x, float y) {
        int index = -1;

        for (int i = this.size() - 1; i >= 0; i--) {
            if (this.get(i).getPathRegion().contains((int) x, (int) y)) {
                return i;
            }
        }
        return index;
    }

    public PhotoViewModel getCurPhotoItem() {
        if (mCurPhotoIndex < 0 || mCurPhotoIndex >= this.size()) return null;
        return this.get(mCurPhotoIndex);
    }

    public int getCurPhotoIndex() {
        return mCurPhotoIndex;
    }

    public void setCurPhotoIndex(int curPhotoIndex) {
        mCurPhotoIndex = curPhotoIndex;
        if (mCurPhotoIndex == -1) {
            setNotTouchAll();
        } else {
            setCurPhotoTouched();
        }
    }

    private void setNotTouchAll() {
        for (int i = 0; i < this.size(); i++) {
            get(i).setIsSelected(false);
        }
    }

    private void setCurPhotoTouched() {
        for (int i = 0; i < this.size(); i++) {
            if (i == mCurPhotoIndex)
                get(i).setIsSelected(true);
            else
                get(i).setIsSelected(false);
        }
    }

    public boolean isIndexInList(int index) {
        if (index < 0 || index >= this.size()) {
            return false;
        }
        return true;
    }

    public void setPileFrameBmp(Bitmap pileFrameImg) {
        mPileFrameBmp = ImageUtils.recycleBitmap(mPileFrameBmp);
        mPileFrameBmp = pileFrameImg;
    }

    public void scaleFitPileFrame() {
        mPileFrameBmp = Bitmap.createScaledBitmap(mPileFrameBmp,
                (mBackgroundRect.width() <= 0) ? 1 : mBackgroundRect.width(),
                (mBackgroundRect.height() <= 0) ? 1 : mBackgroundRect.height(),
                true);
    }

    public void release() {
        mBgPath = null;
        mBackgroundRect = null;
        mCanvasPath = null;
        if (mPileClipPaths != null) {
            mPileClipPaths.clear();
            mPileClipPaths = null;
        }
        if (mGridClipPaths != null) {
            mGridClipPaths.clear();
            mGridClipPaths = null;
        }
        mPileFramePaint = null;
        mPileFrameBmp = ImageUtils.recycleBitmap(mPileFrameBmp);
        mBgBitmap = ImageUtils.recycleBitmap(mBgBitmap);
        mFrameBmp = ImageUtils.recycleBitmap(mFrameBmp);
        for (PhotoViewModel item : this) {
            item.release();
        }
        this.clear();
    }

    private void drawPileFrame(Canvas canvas) {
        if (mPileFrameBmp == null || mPileFrameBmp.isRecycled()) {
            return;
        }
        try {
            canvas.clipRect(mBackgroundRect, Region.Op.REPLACE);
        } catch (UnsupportedOperationException e) {
            Log.e(TAG, "clipPath() not supported");
        }
        if (mIsSavePhoto) {
            canvas.drawBitmap(mPileFrameBmp, new Rect(0, 0, mPileFrameBmp.getWidth(),
                    mPileFrameBmp.getHeight()), mBackgroundRect, mPileFramePaint);
        } else {
            // anti-alias
            canvas.drawBitmap(mPileFrameBmp, new Rect(0, 0, mPileFrameBmp.getWidth(),
                    mPileFrameBmp.getHeight()), mBackgroundRect, null);
        }
    }

    private void setShaderPaint(Paint paint, Bitmap bitmap) {
        Shader shader = new BitmapShader(bitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        paint.setShader(shader);
    }

    private void drawFrame(Canvas canvas, Bitmap frameBmp) {

        if (frameBmp == null || frameBmp.isRecycled()) {
            Flog.d("frameBmp == null");
            return;
        }

//        if (mIsSavePhoto) {
//            if ((frameBmp.getWidth() * mChangedRatio) > 0 && (frameBmp.getHeight() * mChangedRatio) > 0)
//                frameBmp = Bitmap.createScaledBitmap(frameBmp,
//                        (int) (frameBmp.getWidth() * mChangedRatio),
//                        (int) (frameBmp.getHeight() * mChangedRatio),
//                        false);
//            else {
//                frameBmp = Bitmap.createScaledBitmap(frameBmp, 1, 1, false);
//            }
//        }

        drawFrameLine(canvas, 0, 0, mFrameWidth, mBackgroundRect.height(), mFramePaint);
        drawFrameLine(canvas, 0, 0, mBackgroundRect.width(), mFrameWidth, mFramePaint);
        drawFrameLine(canvas, mBackgroundRect.width()- mFrameWidth, 0, mBackgroundRect.width(), mBackgroundRect.height(), mFramePaint);
        drawFrameLine(canvas, 0, mBackgroundRect.height()- mFrameWidth, mBackgroundRect.width(), mBackgroundRect.height(), mFramePaint);

//        if(true)return;
//
//        int patternImgWidth = frameBmp.getWidth();
//        int patternImgHeight = frameBmp.getHeight();
//        Bitmap topFrame = cutFrame(frameBmp, 0, patternImgHeight / 6, patternImgWidth, patternImgHeight);
//        Bitmap leftFrame = cutFrame(frameBmp, patternImgWidth / 6, 0, patternImgWidth, patternImgHeight);
//        Bitmap bottomFrame = cutFrame(frameBmp, 0, 0, patternImgWidth, patternImgHeight * 5 / 6);
//        Bitmap rightFrame = cutFrame(frameBmp, 0, 0, patternImgWidth * 5 / 6, patternImgHeight);
//
//        int widthMax = mBackgroundRect.width() / patternImgWidth;
//        int heightMax = mBackgroundRect.height() / patternImgHeight;
//
//        int paddingStart = mBackgroundRect.left;
//        int paddingTop = mBackgroundRect.top;
//
//        if (mBackgroundRect.width() > patternImgWidth
//                && mBackgroundRect.height() > patternImgHeight) {
//
//            int fitBottom = (mBackgroundRect.height() % bottomFrame.getHeight());
//            int fitRight = (mBackgroundRect.width() % rightFrame.getWidth());
//
//            // Smartphone:
//            for (int i = 0; i < widthMax; i++) {
//                for (int j = 0; j < heightMax; j++) {
//                    if (i == 0) {   // left frame
//                        canvas.drawBitmap(leftFrame
//                                , paddingStart + leftFrame.getWidth() * i
//                                , paddingTop + leftFrame.getHeight() * j
//                                , null);
//                    }
//                    if (j == 0) {   // top frame
//                        canvas.drawBitmap(topFrame
//                                , paddingStart + topFrame.getWidth() * i
//                                , paddingTop + topFrame.getHeight() * j
//                                , null);
//                    }
//                    if ((i == widthMax - 1)) {     // right frame
//                        canvas.drawBitmap(rightFrame
//                                , paddingStart + rightFrame.getWidth() * i + fitRight
//                                , paddingTop + rightFrame.getHeight() * j
//                                , null);
//                    }
//                    if ((j == heightMax - 1)) {   // bottom frame
//                        canvas.drawBitmap(bottomFrame
//                                , paddingStart + bottomFrame.getWidth() * i
//                                , paddingTop + bottomFrame.getHeight() * j + fitBottom
//                                , null);
//                    }
//                }
//            }
//            if (fitBottom > 0) {
//                // draw addition to left and right:
//                canvas.drawBitmap(leftFrame
//                        , paddingStart
//                        , paddingTop + leftFrame.getHeight() * (heightMax - 1) + fitBottom
//                        , null);
//                canvas.drawBitmap(rightFrame
//                        , paddingStart + rightFrame.getWidth() * (widthMax - 1) + fitRight
//                        , paddingTop + rightFrame.getHeight() * (heightMax - 1) + fitBottom
//                        , null);
//            }
//            if (fitRight > 0) {
//                // draw addition to top and bottom:
//                canvas.drawBitmap(topFrame
//                        , paddingStart + topFrame.getWidth() * (widthMax - 1) + fitRight
//                        , paddingTop
//                        , null);
//                canvas.drawBitmap(bottomFrame
//                        , paddingStart + bottomFrame.getWidth() * (widthMax - 1) + fitRight
//                        , paddingTop + bottomFrame.getHeight() * (heightMax - 1) + fitBottom
//                        , null);
//            }
//
//            // recycle bitmaps:
//            topFrame = ImageUtils.recycleBitmap(topFrame);
//            leftFrame = ImageUtils.recycleBitmap(leftFrame);
//            rightFrame = ImageUtils.recycleBitmap(rightFrame);
//            bottomFrame = ImageUtils.recycleBitmap(bottomFrame);
//        } else if (mBackgroundRect.width() == patternImgWidth
//                && mBackgroundRect.height() == patternImgHeight) {
//            canvas.drawBitmap(frameBmp, paddingStart, paddingTop, null);
//        } else {
//            Bitmap temp = Bitmap.createScaledBitmap(frameBmp,
//                    (mBackgroundRect.width() <= 0) ? 1 : mBackgroundRect.width(),
//                    (mBackgroundRect.height() <= 0) ? 1 : mBackgroundRect.height(),
//                    true);
//            canvas.drawBitmap(temp, paddingStart4we, paddingTop, null);
//            temp.recycle();
//            temp = null;
//        }
    }

    private void drawFrameLine(Canvas canvas, int x, int y, int w, int h, Paint paint) {
        try {
            canvas.clipRect(x, y, w, h);
            canvas.drawPaint(paint);
            canvas.clipRect(mBackgroundRect, Region.Op.REPLACE);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "clipPath() not supported");
        }

    }

    public void setBackgroundPaint() {
        setShaderPaint(mBackgroundPaint, mBgBitmap);
    }

    public void upBackgroundPaint() {
        if (mBgBitmap == null)
            return;
        Bitmap backgroundBmp = Bitmap.createScaledBitmap(mBgBitmap,
                (int) (mBgBitmap.getWidth() * mChangedRatio),
                (int) (mBgBitmap.getHeight() * mChangedRatio),
                false);

        setShaderPaint(mBackgroundPaint, backgroundBmp);
//        backgroundBmp = ImageUtils.recycleBitmap(backgroundBmp);
    }

    public void upFramePaint() {
        if (mFrameBmp == null)
            return;

        Bitmap frameBmp = null;
        if ((mFrameBmp.getWidth() * mChangedRatio) > 0 && (mFrameBmp.getHeight() * mChangedRatio) > 0)
            frameBmp = Bitmap.createScaledBitmap(mFrameBmp,
                    (int) (mFrameBmp.getWidth() * mChangedRatio),
                    (int) (mFrameBmp.getHeight() * mChangedRatio),
                    false);
        else {
            frameBmp = Bitmap.createScaledBitmap(frameBmp, 1, 1, false);
        }

        setShaderPaint(mFramePaint, frameBmp);
//        frameBmp = ImageUtils.recycleBitmap(frameBmp);
        mFrameWidth *= mChangedRatio;
    }

    private Bitmap cutFrame(Bitmap originImag, int top, int left, int right, int bottom) {
        Bitmap bmOverlay = Bitmap.createBitmap(originImag.getWidth(), originImag.getHeight(), Bitmap.Config.ARGB_4444);

        Paint p = new Paint();
        p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        Canvas c = new Canvas(bmOverlay);
        c.drawBitmap(originImag, 0, 0, null);
        c.drawRect(top, left, right, bottom, p);
        return bmOverlay;
    }

    private void drawBackground(Canvas canvas, Bitmap bgBitmap, @ColorInt int color) {
//        canvas.clipRect(mBackgroundRect, Region.Op.REPLACE);

        if (bgBitmap != null) {
            drawBackgroundPattern(canvas, bgBitmap);
        } else {
            canvas.drawColor(color);
        }
    }

    public void setIsSavePhoto(boolean isSavePhoto) {
        mIsSavePhoto = isSavePhoto;
    }

    private void drawBackgroundPattern(Canvas canvas, Bitmap bgImag) {
        if (bgImag == null || bgImag.isRecycled()) {
            return;
        }

//        if (mIsSavePhoto) {
//            bgImag = Bitmap.createScaledBitmap(bgImag,
//                    (int) (bgImag.getWidth() * mChangedRatio),
//                    (int) (bgImag.getHeight() * mChangedRatio),
//                    false);
//        }

        canvas.drawPaint(mBackgroundPaint);
//        if (true)
//            return;
//
//        if (bgImag == null || (int) bgImag.getWidth() == 0 || (int) bgImag.getHeight() == 0)
//            return;
//
//        int patternImgWidth = (int) (bgImag.getWidth());
//        int patternImgHeight = (int) (bgImag.getHeight());
//
//        int widthMax = mBackgroundRect.width() / patternImgWidth;
//        int heightMax = mBackgroundRect.height() / patternImgHeight;
//
//        int paddingStart = mBackgroundRect.left;
//        int paddingTop = mBackgroundRect.top;
//
//        if (mBackgroundRect.width() > patternImgWidth
//                && mBackgroundRect.height() > patternImgHeight) {
//
//            if (mBackgroundRect.width() % patternImgWidth > 0) {
//                widthMax++;
//            }
//
//            if (mBackgroundRect.height() % patternImgHeight > 0) {
//                heightMax++;
//            }
//
//            // drawGrid looping image
//            for (int i = 0; i < widthMax; i++) {
//                for (int j = 0; j < heightMax; j++) {
//                    canvas.drawBitmap(bgImag
//                            , paddingStart + patternImgWidth * i
//                            , paddingTop + patternImgHeight * j
//                            , null);
//                }
//            }
//
//        } else if (mBackgroundRect.width() == patternImgWidth
//                && mBackgroundRect.height() == patternImgHeight) {
//            canvas.drawBitmap(bgImag, paddingStart, paddingTop, null);
//
//        } else {
//            Bitmap temp = Bitmap.createScaledBitmap(bgImag, mBackgroundRect.width(),
//                    mBackgroundRect.height(), true);
//            canvas.drawBitmap(temp, paddingStart, paddingTop, null);
//            temp.recycle();
//            temp = null;
//        }
    }

    public void setRoundnessRects(float roundNess) {
        for (PhotoViewModel itemPhotoView : this) {
            itemPhotoView.setRoundSize(roundNess);
        }
    }

    public void setBackgroundRect(Rect backgroundRect) {
        mBackgroundRect = backgroundRect;
        mBgPath.addRect(new RectF(mBackgroundRect), Path.Direction.CW);
    }

    public boolean checkDraggingCollision(MotionEvent event, int curPhotoIndex) {
        for (int i = this.size() - 1; i >= 0; i--) {
            if (i != curPhotoIndex) {
                PhotoViewModel itemPhotoView = this.get(i);
                if (itemPhotoView.getPathRegion().contains((int) event.getX(), (int) event.getY())) {
                    setDraggedPhotoIndex(i);
                    return true;
                }
            }
        }
        return false;
    }

    public int getDraggedPhotoIndex() {
        return mDraggedPhotoIndex;
    }

    public void setDraggedPhotoIndex(int draggedPhotoIndex) {
        mDraggedPhotoIndex = draggedPhotoIndex;
    }

    public void swapTwoPhotoItems(int curPhotoIndex, int draggedPhotoIndex) {
        if (curPhotoIndex < 0 || curPhotoIndex >= this.size())
            curPhotoIndex = 0;

        PhotoViewModel curItem = this.get(curPhotoIndex);
        PhotoViewModel draggedItem = this.get(draggedPhotoIndex);
        /* Drag and drop between 2 object that have contains image*/
        curItem.swapOtherPhoto(draggedItem);
        /* Drag and drop between 2 object that one of each have not contains image*/
        boolean tempBool = curItem.isContainsContent();
        curItem.containsContent(draggedItem.isContainsContent());
        draggedItem.containsContent(tempBool);

        setCurPhotoIndex(draggedPhotoIndex);
    }

    public int getMaxPhotoPixelIndex() {
        int index = -1;
        int maxSize = 0;

        for (int i = 0; i < this.size(); i++) {
            if (maxSize < this.get(i).getOrgPixelSize()) {
                index = i;
                maxSize = this.get(i).getOrgPixelSize();
            }
        }

        return index;
    }


    public void setFrameBmp(Bitmap bmpFrame) {
        mFrameBmp = ImageUtils.recycleBitmap(mFrameBmp);
        mFrameBmp = bmpFrame;
    }

    public void setBgBitmap(Bitmap bgBitmap) {
        mBgBitmap = ImageUtils.recycleBitmap(mBgBitmap);
        mBgBitmap = bgBitmap;
    }

    public void setBgColor(int mBgColor) {
        this.mBgColor = mBgColor;
    }

    public void setIsDrawGrid(boolean isGrid) {
        mIsDrawGrid = isGrid;
    }

    public PhotoViewModel getItemNoImage() {
        for (int i = 0; i < this.size(); i++) {
            PhotoViewModel itemPhotoView = this.get(i);
            if (!itemPhotoView.isContainsContent())
                return itemPhotoView;
        }
        return null;
    }

    public int[] getNoContentIndex() {
        //  count number of items that no content:
        int len = 0;
        for (int i = 0; i < this.size(); i++) {
            PhotoViewModel itemPhotoView = this.get(i);
            if (!itemPhotoView.isContainsContent())
                len++;
        }
        // init array of integer that save index of items that no content:
        int ans[] = new int[len];
        if (len <= 0) {
            return ans;
        }
        int index = 0;
        for (int i = 0; i < this.size(); i++) {
            PhotoViewModel itemPhotoView = this.get(i);
            if (!itemPhotoView.isContainsContent()) {
                ans[index] = i;
//                Flog.d(Flog.CTAG, "no image: " + i);
                index++;
            }
        }
        return ans;
    }

    public void setChangedRatio(float changedRatio) {
        mChangedRatio = changedRatio;
    }

    public void setPileDrawHelper() {
        if (mBackgroundRect == null)
            return;
        mCanvasPath = new Path();
        mCanvasPath.addRect(new RectF(mBackgroundRect), Path.Direction.CW);
    }

    public void onDraw(Canvas canvas) {
        // draw background:
        drawBackground(canvas, mBgBitmap, mBgColor);

        // draw photos:
        for (int i = 0; i < mDrawOrders.length; i++) {
            BaseModel item = this.get(mDrawOrders[i]);
            if (item != null) {
                /**
                 * Clip 2 times:
                 * First: Clip rect entire layout parent
                 * Second: Clip path for each photo
                 * */
                canvas.clipRect(mBackgroundRect, Region.Op.REPLACE);
                item.draw(canvas);
            } else {
                Flog.d(TAG, "item is null");
            }
        }

        // draw Frame of Pile style:
        if (!mIsDrawGrid) {
            drawPileFrame(canvas);
        }

        // draw Frame:
        canvas.clipRect(mBackgroundRect, Region.Op.REPLACE);
        drawFrame(canvas, mFrameBmp);
    }

    public boolean onTouchEvent(MotionEvent event) {
        PhotoViewModel baseItem = getCurPhotoItem();
        if (baseItem != null) {
            if (baseItem.onTouchEvent(event)) {
                return true;
            }
        } else {
            Flog.i("PhotoViewModel == null");
        }
        return false;
    }

    public void setFramePaint() {
        setShaderPaint(mFramePaint, mFrameBmp);
    }
}
