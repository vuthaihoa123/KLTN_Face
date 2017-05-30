package com.pic.libphotocollage.core.model;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import com.pic.libphotocollage.core.CollageView;

/**
 * Created by vutha on 1/9/2017.
 */

public class IconStickerModel extends BaseStickerModel {

    private float initScale = 1f;
    private Paint mIconStickerPaint;

    public IconStickerModel(CollageView collageView) {
        super(collageView);
        init();
    }

    private void init() {
        mItemType = ItemType.STICKER_ICON;
        mDisplayMetrics = mContext.getResources().getDisplayMetrics();
        MAX_SCALE = 1.2f;
        dst_delete = new Rect();
        dst_resize = new Rect();
        dst_flipV = new Rect();
        dst_top = new Rect();
        initLocalPaint();
        mIconStickerPaint = new Paint(Paint.FILTER_BITMAP_FLAG);
    }

    @Override
    public void draw(Canvas canvas) {
        if (canvas == null || isDeleted) return;
        if (mBitmap != null) {

//            float[] arrayOfFloat = new float[9];
//            matrix.getValues(arrayOfFloat);
//            float f1 = 0.0F * arrayOfFloat[0] + 0.0F * arrayOfFloat[1] + arrayOfFloat[2];
//            float f2 = 0.0F * arrayOfFloat[3] + 0.0F * arrayOfFloat[4] + arrayOfFloat[5];
//            float f3 = arrayOfFloat[0] * this.mBitmap.getWidth() + 0.0F * arrayOfFloat[1] + arrayOfFloat[2];
//            float f4 = arrayOfFloat[3] * this.mBitmap.getWidth() + 0.0F * arrayOfFloat[4] + arrayOfFloat[5];
//            float f5 = 0.0F * arrayOfFloat[0] + arrayOfFloat[1] * this.mBitmap.getHeight() + arrayOfFloat[2];
//            float f6 = 0.0F * arrayOfFloat[3] + arrayOfFloat[4] * this.mBitmap.getHeight() + arrayOfFloat[5];
//            float f7 = arrayOfFloat[0] * this.mBitmap.getWidth() + arrayOfFloat[1] * this.mBitmap.getHeight() + arrayOfFloat[2];
//            float f8 = arrayOfFloat[3] * this.mBitmap.getWidth() + arrayOfFloat[4] * this.mBitmap.getHeight() + arrayOfFloat[5];

//            canvas.save();
            canvas.drawBitmap(mBitmap, matrix, mIconStickerPaint);

//            //删除在右上角
//            dst_delete.left = (int) (f3 - deleteBitmapWidth / 2);
//            dst_delete.right = (int) (f3 + deleteBitmapWidth / 2);
//            dst_delete.top = (int) (f4 - deleteBitmapHeight / 2);
//            dst_delete.bottom = (int) (f4 + deleteBitmapHeight / 2);
//            //拉伸等操作在右下角
//            dst_resize.left = (int) (f7 - resizeBitmapWidth / 2);
//            dst_resize.right = (int) (f7 + resizeBitmapWidth / 2);
//            dst_resize.top = (int) (f8 - resizeBitmapHeight / 2);
//            dst_resize.bottom = (int) (f8 + resizeBitmapHeight / 2);
//            //垂直镜像在左上角
//            dst_top.left = (int) (f1 - flipVBitmapWidth / 2);
//            dst_top.right = (int) (f1 + flipVBitmapWidth / 2);
//            dst_top.top = (int) (f2 - flipVBitmapHeight / 2);
//            dst_top.bottom = (int) (f2 + flipVBitmapHeight / 2);
//            //水平镜像在左下角
//            dst_flipV.left = (int) (f5 - topBitmapWidth / 2);
//            dst_flipV.right = (int) (f5 + topBitmapWidth / 2);
//            dst_flipV.top = (int) (f6 - topBitmapHeight / 2);
//            dst_flipV.bottom = (int) (f6 + topBitmapHeight / 2);
            if (isInEdit) {
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


                //删除在右上角
                dst_delete.left = (int) (f3 - deleteBitmapWidth / 2);
                dst_delete.right = (int) (f3 + deleteBitmapWidth / 2);
                dst_delete.top = (int) (f4 - deleteBitmapHeight / 2);
                dst_delete.bottom = (int) (f4 + deleteBitmapHeight / 2);
                //拉伸等操作在右下角
                dst_resize.left = (int) (f7 - resizeBitmapWidth / 2);
                dst_resize.right = (int) (f7 + resizeBitmapWidth / 2);
                dst_resize.top = (int) (f8 - resizeBitmapHeight / 2);
                dst_resize.bottom = (int) (f8 + resizeBitmapHeight / 2);
                //垂直镜像在左上角
                dst_top.left = (int) (f1 - flipVBitmapWidth / 2);
                dst_top.right = (int) (f1 + flipVBitmapWidth / 2);
                dst_top.top = (int) (f2 - flipVBitmapHeight / 2);
                dst_top.bottom = (int) (f2 + flipVBitmapHeight / 2);
                //水平镜像在左下角
                dst_flipV.left = (int) (f5 - topBitmapWidth / 2);
                dst_flipV.right = (int) (f5 + topBitmapWidth / 2);
                dst_flipV.top = (int) (f6 - topBitmapHeight / 2);
                dst_flipV.bottom = (int) (f6 + topBitmapHeight / 2);

                canvas.drawLine(f1, f2, f3, f4, mLinePaint);
                canvas.drawLine(f3, f4, f7, f8, mLinePaint);
                canvas.drawLine(f5, f6, f7, f8, mLinePaint);
                canvas.drawLine(f5, f6, f1, f2, mLinePaint);

                canvas.drawBitmap(deleteBitmap, null, dst_delete, null);
                canvas.drawBitmap(resizeBitmap, null, dst_resize, null);
                canvas.drawBitmap(flipVBitmap, null, dst_flipV, null);
//                canvas.drawBitmap(topBitmap, null, dst_top, null);
            }

//            canvas.restore();
        }
    }

    @Override
    public void release() {
        Bitmap[] list = new Bitmap[]{deleteBitmap, flipVBitmap, topBitmap, resizeBitmap, mBitmap,};
        for (Bitmap item : list) {
            if (item != null) item.recycle();
        }
    }

    @Override
    public void setBitmap(Bitmap bitmap) {
        matrix.reset();
        mBitmap = bitmap;
        setDiagonalLength();
        initScaleBounds();
        allocateBitmaps();
        int w = mBitmap.getWidth();
        int h = mBitmap.getHeight();
        originalWidth = w;

        //normalize icon size for fit the screen
        initScale = (float) Math.sqrt(((mCollageView.getWidth() * mCollageView.getHeight()) / 25.0f) / (w * h));

        matrix.postScale(initScale, initScale, w / 2f, h / 2f);
        matrix.postTranslate(mCollageView.getWidth() / 4f - w / 4f, (mCollageView.getWidth()) / 2f - h / 2f);
    }

    public void setStickerPaint(int opacity) {
        mIconStickerPaint.setAlpha(opacity);
    }

    public int getOpacity() {
        return mIconStickerPaint.getAlpha();
    }
}
