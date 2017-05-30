package com.pic.libphotocollage.core.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;

import com.pic.libphotocollage.core.CollageView;
import com.pic.libphotocollage.core.R;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by vutha on 1/9/2017.
 */

public class TextStickerModel extends BaseStickerModel implements TextWatcher {

    public static final int MAX_FONT_SIZE = 64;
    public static final int MIN_FONT_SIZE = 14;
    public static final int M_DEFULT_SIZE = 22;
    private static final String defaultStr = "Enter text";
    private static final String TAG = TextStickerModel.class.getSimpleName();
    boolean isInit = false;
    private int fontColor;
    private Bitmap originBitmap;
    private String mStr = "";
    private float mFontSize = 16;
    private TextPaint mFontPaint;
    private Canvas canvasText;
    private float realTextHeight;
    private float mPaddingRect;
    private float leftMargin;
    private float topMargin;
    private int mPaddingText;
    private Bitmap mPatternBmp;
    private Paint mTextStickerPaint;

    public TextStickerModel(CollageView collageView, int fontColor) {
        super(collageView);
        this.fontColor = fontColor;
        init();
    }

    private void init() {
        mItemType = ItemType.STICKER_TEXT;
        mDisplayMetrics = mContext.getResources().getDisplayMetrics();
        dst_delete = new Rect();
        dst_resize = new Rect();
        dst_flipV = new Rect(); // unused
        dst_top = new Rect();   // unused
        initLocalPaint();

        mFontSize = mContext.getResources().getDimension(R.dimen.default_sticker_text_size);
        mFontPaint = new TextPaint(Paint.FILTER_BITMAP_FLAG);
//        mFontPaint.setFilterBitmap(true);
//        mFontPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, mFontSize, mDisplayMetrics));
//        Log.d("textsize", "size="+TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, mFontSize, mDisplayMetrics));
        mFontPaint.setTextSize((TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, mFontSize, mDisplayMetrics)));
        mFontPaint.setColor(fontColor);
        mFontPaint.setTextAlign(Paint.Align.CENTER);
        mFontPaint.setTextAlign(Paint.Align.LEFT);
        mFontPaint.setAntiAlias(true);
        mFontPaint.setFilterBitmap(true);
        mFontPaint.setDither(true);

        mTextStickerPaint = new TextPaint(Paint.FILTER_BITMAP_FLAG);
        mTextStickerPaint.setAntiAlias(true);
        mTextStickerPaint.setFilterBitmap(true);
        mTextStickerPaint.setDither(true);


        mCollageView.registerKeyboardEvent(this);
        allocateBitmaps();
        leftMargin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, mDisplayMetrics);
        topMargin = leftMargin;
        initSize();
        isInit = true;
    }

    private void initSize() {
        if (mBitmap != null) mBitmap.recycle();
        Paint.FontMetrics fm = mFontPaint.getFontMetrics();
        realTextHeight = Math.abs(fm.top - fm.bottom);
        String curText = TextUtils.isEmpty(this.getText()) ? defaultStr : this.getText();
        float measureTextWidth = mFontPaint.measureText(curText);
        measureTextWidth = Math.min(measureTextWidth, 14.0f * mCollageView.getWidth() / 16);
        int numLines = autoSplit(curText, mFontPaint, measureTextWidth).length;
        int height = (int) (numLines * realTextHeight + (numLines - 1) * (Math.abs(mFontPaint.getFontMetrics().leading) + mPaddingText) + 2 * topMargin);
        Bitmap bitmap = Bitmap.createBitmap((int) (measureTextWidth + leftMargin + deleteBitmapWidth), height, Bitmap.Config.ARGB_4444);
        setBitmap(bitmap);
    }

    private String[] autoSplit(String content, Paint p, float width) {
        int length = content.length();
        float textWidth = p.measureText(content);
        if (textWidth <= width) {
            return new String[]{content};
        }

        int start = 0, end = 1, i = 0;
        int lines = (int) Math.ceil(textWidth / width); //计算行数
        String[] lineTexts = new String[lines];
        while (start < length) {
            if (i >= lines)
                break;
            if (p.measureText(content, start, end) > width) { //文本宽度超出控件宽度时
                lineTexts[i++] = (String) content.subSequence(start, end);
                start = end;
            }
            if (start < end && end == length) { //不足一行的文本
                lineTexts[i] = (String) content.subSequence(start, end);
                break;
            }
            end += 1;
        }
        return lineTexts;
    }

    public String getText() {
        return mStr;
    }

    public void setText(String text) {
        mStr = text;
        initSize();
    }

    public void setTextColor(int color) {
        mFontPaint.setShader(null);
        mFontPaint.setColor(color);
        mIsSetShader = false;
    }

    public void setTextPattern(Context context, String bgName) {
//        if (mFontPaint.getShader()!=null)
//            mFontPaint.setShader(null);
        InputStream inputStream = null;
//        Bitmap mPatternBmp = null;
        try {
            inputStream = context.getResources().getAssets().open("bg/" + bgName);
            mPatternBmp = BitmapFactory.decodeStream(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Shader shader = new BitmapShader(mPatternBmp,
                Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
//        shader.setLocalMatrix(matrix);
        mFontPaint.setShader(shader);
        mIsSetShader = true;
    }

    public void setFontType(Typeface fontType) {
        mFontPaint.setTypeface(fontType);
        initSize();
    }

    public void setPaddingText(int padding) {
        mPaddingText = padding;
        initSize();
    }

    public float getTextSize() {
        return mFontSize;
    }

    public void setTextSize(int unit) {
        mFontSize = unit;
        mFontPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, mFontSize, mDisplayMetrics));
        initSize();
    }

    public void scaleText() {
//        mFontSize = value;
        mFontPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, mFontSize, mDisplayMetrics));
        if (mBitmap != null) mBitmap.recycle();
        Paint.FontMetrics fm = mFontPaint.getFontMetrics();
        realTextHeight = Math.abs(fm.top - fm.bottom);
        String curText = TextUtils.isEmpty(this.getText()) ? defaultStr : this.getText();
        float measureTextWidth = mFontPaint.measureText(curText);
        measureTextWidth = Math.min(measureTextWidth, 14.0f * mCollageView.getWidth() / 16);
        int numLines = autoSplit(curText, mFontPaint, measureTextWidth).length;
        int height = (int) (numLines * realTextHeight + (numLines - 1) * (Math.abs(mFontPaint.getFontMetrics().leading) + mPaddingText) + 2 * topMargin);
        Bitmap bitmap = Bitmap.createBitmap((int) (measureTextWidth + leftMargin + deleteBitmapWidth), height, Bitmap.Config.ARGB_4444);
        originBitmap = bitmap;
        mBitmap = originBitmap.copy(Bitmap.Config.ARGB_8888, true);
        canvasText = new Canvas(mBitmap);
//        canvasText.setBitmap(mBitmap);
        canvasText.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));

        setDiagonalLength();
        initScaleBounds();
        int w = mBitmap.getWidth();
        int h = mBitmap.getHeight();
        originalWidth = w;
    }

    public float getPadding() {
        return mPaddingText;
    }

    @Override
    public void draw(Canvas canvas) {
        if (mBitmap != null && !isDeleted) {

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

            //先往文字上绘图
//            mBitmap = originBitmap.copy(Bitmap.Config.ARGB_8888, true);
//            canvasText.setBitmap(mBitmap);
//            canvasText.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            /**
             * Touched photo is twinkling:
             * */
//            canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
//            float scalex = arrayOfFloat[Matrix.MSCALE_X];
//            float skewy = arrayOfFloat[Matrix.MSKEW_Y];
//            float skewx = arrayOfFloat[Matrix.MSKEW_X];
//            float rScale = (float) Math.sqrt(scalex * scalex + skewy * skewy);

//            if (isPointerDown || isInResize) {
//                if (mFontPaint.getShader() != null) {
//                    mFontPaint.getShader().setLocalMatrix(matrix);
//                    mIsSetShader = true;
//                } else {
//                    mIsSetShader = false;
//                }
//                mFontSize *= scale;
//                scaleText();
//            }

            mFontPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, mFontSize, mDisplayMetrics));
            String[] texts = autoSplit(TextUtils.isEmpty(mStr) ? defaultStr : mStr, mFontPaint,
                    mBitmap.getWidth() - leftMargin - deleteBitmapWidth);
            Paint.FontMetrics fm = mFontPaint.getFontMetrics();
            float top = Math.abs(fm.top) + topMargin;
            //基于底线开始画的
            for (String text : texts) {
                if (TextUtils.isEmpty(text)) {
                    continue;
                }
                canvasText.drawText(text, leftMargin, top, mFontPaint);
                top += realTextHeight + mFontPaint.getFontMetrics().leading + mPaddingText; //添加字体行间距
            }
//            canvas.save();

            canvas.drawBitmap(mBitmap, matrix, mTextStickerPaint);

//            //删除在右上角w
//            dst_delete.left = (int) (f3 + mPaddingRect - deleteBitmapWidth / 2);
//            dst_delete.right = (int) (f3 + mPaddingRect + deleteBitmapWidth / 2);
//            dst_delete.top = (int) (f4 - mPaddingRect - deleteBitmapHeight / 2);
//            dst_delete.bottom = (int) (f4 - mPaddingRect + deleteBitmapHeight / 2);
//            //拉伸等操作在右下角
//            dst_resize.left = (int) (f7 + mPaddingRect - resizeBitmapWidth / 2);
//            dst_resize.right = (int) (f7 + mPaddingRect + resizeBitmapWidth / 2);
//            dst_resize.top = (int) (f8 + mPaddingRect - resizeBitmapHeight / 2);
//            dst_resize.bottom = (int) (f8 + mPaddingRect + resizeBitmapHeight / 2);

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

                //删除在右上角w
                dst_delete.left = (int) (f3 + mPaddingRect - deleteBitmapWidth / 2);
                dst_delete.right = (int) (f3 + mPaddingRect + deleteBitmapWidth / 2);
                dst_delete.top = (int) (f4 - mPaddingRect - deleteBitmapHeight / 2);
                dst_delete.bottom = (int) (f4 - mPaddingRect + deleteBitmapHeight / 2);
                //拉伸等操作在右下角
                dst_resize.left = (int) (f7 + mPaddingRect - resizeBitmapWidth / 2);
                dst_resize.right = (int) (f7 + mPaddingRect + resizeBitmapWidth / 2);
                dst_resize.top = (int) (f8 + mPaddingRect - resizeBitmapHeight / 2);
                dst_resize.bottom = (int) (f8 + mPaddingRect + resizeBitmapHeight / 2);

                canvas.drawLine(f1 - mPaddingRect, f2 - mPaddingRect, f3 + mPaddingRect, f4 - mPaddingRect, mLinePaint);
                canvas.drawLine(f3 + mPaddingRect, f4 - mPaddingRect, f7 + mPaddingRect, f8 + mPaddingRect, mLinePaint);
                canvas.drawLine(f5 - mPaddingRect, f6 + mPaddingRect, f7 + mPaddingRect, f8 + mPaddingRect, mLinePaint);
                canvas.drawLine(f5 - mPaddingRect, f6 + mPaddingRect, f1 - mPaddingRect, f2 - mPaddingRect, mLinePaint);

                canvas.drawBitmap(deleteBitmap, null, dst_delete, null);
                canvas.drawBitmap(resizeBitmap, null, dst_resize, null);
            }

//            canvas.restore();
        }
    }

    @Override
    public void release() {
        Bitmap[] list = new Bitmap[]{mBitmap, deleteBitmap, flipVBitmap, topBitmap, resizeBitmap,
                originBitmap};
        for (Bitmap item : list) {
            if (item != null) item.recycle();
        }
    }

    public float[] getCurrentPosition() {
        float[] arrayOfFloat1 = new float[9];
        this.matrix.getValues(arrayOfFloat1);

        //左上角
        float f1 = 0.0F * arrayOfFloat1[0] + 0.0F * arrayOfFloat1[1] + arrayOfFloat1[2];
        float f6 = 0.0F * arrayOfFloat1[3] + arrayOfFloat1[4] * this.mBitmap.getHeight() + arrayOfFloat1[5];

        return new float[]{f1, f6};
    }

    @Override
    public void setBitmap(Bitmap bitmap) {
        originBitmap = bitmap;
        mBitmap = originBitmap.copy(Bitmap.Config.ARGB_8888, true);
        canvasText = new Canvas(mBitmap);
//        canvasText.setBitmap(mBitmap);
        canvasText.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));

        setDiagonalLength();
        initScaleBounds();
        int w = mBitmap.getWidth();
        int h = mBitmap.getHeight();
        originalWidth = w;
        if (!isInit) {
            matrix.postTranslate(5, (mCollageView.getWidth()) / 3 - h);
        } else {
//            matrix.setTranslate(mScreenWidth / 2 - w / 2, 0);
        }
        mCollageView.invalidate();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        Log.d(TAG, "onTextChanged=" + s.toString());
        setInEdit(true);
        setText(s.toString());
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }
}
