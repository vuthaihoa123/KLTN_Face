package com.pic.libphotocollage.core;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.AppCompatEditText;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.pic.libphotocollage.core.collection.ListAdaptiveItem;
import com.pic.libphotocollage.core.collection.ListPhotoViewModel;
import com.pic.libphotocollage.core.model.BaseModel;
import com.pic.libphotocollage.core.model.BaseStickerModel;
import com.pic.libphotocollage.core.model.DragPhotoModel;
import com.pic.libphotocollage.core.model.PhotoViewModel;
import com.pic.libphotocollage.core.model.TextStickerModel;
import com.pic.libphotocollage.core.util.Flog;
import com.pic.libphotocollage.core.util.SystemUtils;

import java.util.List;

/**
 * Created by hoavt on 20/07/2016.
 */
public class CollageView extends FrameLayout implements ViewTreeObserver.OnGlobalLayoutListener, DragPhotoModel.OnSwapPhotosListener {
    private static final String TAG = CollageView.class.getSimpleName();
    public static final int MIN_PIXEL_WIDTH_SAVED = 960;
    int[] oldSize = new int[2];
    private boolean mIsDragging = false;
    private int mSaveCanvasWidth;
    private int mSaveCanvasHeight;
    private float mRatioView = 1.0f;
    private boolean isKeyboardShown = false;
    private float lastTranslateY;
    private int keyboardHeight;
    private boolean isTranslated = false;
    private Context mContext;
    private ListAdaptiveItem mListItem = new ListAdaptiveItem();
    private GestureDetector mGestureDetector;
    private ListPhotoViewModel mListPhotos;
    private OnCollageViewListenner mOnCollageViewListenner = null;
    private TextWatcher curTextWatcher;
    private EditText virtualEditText;
    private Rect r = new Rect();
    private DragPhotoModel mDragPhotoModel;
    private boolean mIsDrawedSavePhoto;

    public CollageView(Context context) {
        super(context);
        if (!isInEditMode()) {
            initViews(context);
        }
    }

    public CollageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) {
            initViews(context);
        }
    }

    public CollageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (!isInEditMode()) {
            initViews(context);
        }
    }

    public boolean isCurrentItemType(BaseModel.ItemType type) {
        return getCurrentItem() != null && getCurrentItem().getItemType() == type;
    }

    private void initViews(Context context) {
        Log.d(TAG, "initViewsinitViews");


        mContext = context;
        initGesture();  // for long press

        virtualEditText = new AppCompatEditText(context);
        virtualEditText.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(1, 1);
        virtualEditText.setLayoutParams(params);

        virtualEditText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        addView(virtualEditText);

        virtualEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    dismissKeyboard();
                }
                return false;
            }
        });

        final Window mRootWindow = getActivity().getWindow();
        View mRootView = mRootWindow.getDecorView().findViewById(android.R.id.content);
        mRootView.getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    private Activity getActivity() {
        return (Activity) mContext;
    }

    private void initGesture() {
        GestureDetector.OnGestureListener gestureListener = new GestureDetector.SimpleOnGestureListener() {
            @Override
            public void onLongPress(MotionEvent event) {
                super.onLongPress(event);
                Flog.d(Flog.CTAG, "onLongPress onLongPress --");
                if (mListPhotos == null || mListItem == null)
                    return;
                int touchedIdx = mListPhotos.getTouchedIndex(event);
                if (touchedIdx != -1 && mListPhotos.get(touchedIdx).isSelected()
                        && (mListItem.isDeleteAll() || mListItem.notTouchAll())) {  // apply only for dragging photo-item
                    // prepare dragging for photo item
                    mIsDragging = true;

                    PhotoViewModel curPhotoView = mListPhotos.getCurPhotoItem();
                    if (curPhotoView == null)
                        return;
                    curPhotoView.setIsStop(mIsDragging);
                    if (mDragPhotoModel == null)
                        mDragPhotoModel = new DragPhotoModel(CollageView.this).setSwapPhotosListener(CollageView.this);
                    if (curPhotoView.getBitmap() != null) {
                        mDragPhotoModel.setBitmap(curPhotoView.getBitmap(), event.getX(0), event.getY(0));
                        invalidate();
                    }
                }
            }
        };

        mGestureDetector = new GestureDetector(mContext, gestureListener);
    }

    public ListPhotoViewModel getListPhotos() {
        return mListPhotos;
    }

    public void setListPhotos(ListPhotoViewModel listPhotos) {
        mListPhotos = listPhotos;
    }

    public void setBackgroundRect(int width, int height) {
        mListPhotos.setBackgroundRect(new Rect(0, 0, width, height));
    }

    public void setBackgroundRect(int left, int top, int width, int height) {
        mListPhotos.setBackgroundRect(new Rect(left, top, width, height));
    }

    public void setBackgroundRectSaved() {
        mListPhotos.setBackgroundRect(new Rect(0, 0, mSaveCanvasWidth, mSaveCanvasHeight));
    }


    @Override
    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);

        Flog.d(Flog.CTAG, "COLLAGE DRAW");

        if (mIsDrawedSavePhoto)
            return;

        canvas.save();  // push to stack storing context, not save shape

        if (mListPhotos != null)
            mListPhotos.onDraw(canvas);

        if (mListItem != null)
            mListItem.onDraw(canvas);

        if (mIsDragging) {
            mDragPhotoModel.draw(canvas);
        }

        canvas.restore();   // pop off stack
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event == null || mListItem == null || mListPhotos == null) return true;

        // onTouch of stickers is unique
        if (mListItem.onTouchEvent(event) && !mListItem.isDeleteAll()) {
            mIsDragging = false;
            invalidate();   // update onDraw Stickers
            return true;
        }

        // press holding photo:
        if (mGestureDetector.onTouchEvent(event)) {
            Flog.d(Flog.CTAG, "onTouchEvent onLongPress");
            mIsDragging = false; // khanh add old code
            invalidate();   // update onDrrraw Stickers
            return true;
        }

        /*
        * Use getActionMasked for MotionEvent.ACTION_POINTER_DOWN:
        * */
        int action = MotionEventCompat.getActionMasked(event);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                Flog.d(Flog.CTAG, "ACTION_DOWN collageview");
                if (event.getPointerCount() > 1 || mListItem == null || mListPhotos == null)
                    return true;
                updateViewIndex(event);

                if (onTouchPhotoView(event))
                    invalidate();
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                Flog.d(Flog.CTAG, "ACTION_POINTER_DOWN collageview");
                onTouchPhotoView(event);
                break;
            case MotionEvent.ACTION_MOVE:
                Flog.d(Flog.CTAG, "ACTION_MOVE collageview");
                onTouchPhotoView(event);
                onTouchDragPhoto(event);
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                Flog.d(Flog.CTAG, "ACTION_UP coolageview");
                onTouchPhotoView(event);
                onTouchDragPhoto(event);
                break;
        }

        return true;
    }

    private void onTouchDragPhoto(MotionEvent event) {
        // onTouch of dragging photo like alpha shadow
        if (mIsDragging) {
            mDragPhotoModel.onTouchEvent(event);
        }
    }

    private boolean onTouchPhotoView(MotionEvent event) {
        // onTouch of photos is unique
        if (!mListItem.isInEdit() && mListItem.getCurrentItem() == null && mListPhotos != null && mListPhotos.getCurPhotoItem() != null) {
            mListPhotos.onTouchEvent(event);
            if (mListPhotos.getCurPhotoItem().isOpenGallery())
                return true;
        }
        return false;
    }

    private void updateViewIndex(MotionEvent event) {
        // update position of current photo
        if (!mListItem.isInEdit() && mListItem.getCurrentItem() == null && mListPhotos != null) {
            mListPhotos.setCurPhotoIndex(mListPhotos.getTouchedIndex(event));
        }

        // update position of current sticker
        if (!mListItem.isDeleteAll()) {
            // update when touch to sticker
            if (mListItem.setCurrentItem(event)) {
                mListPhotos.setCurPhotoIndex(-1);
            }
        }
    }

    public void registerKeyboardEvent(TextWatcher textWatcher) {
        if (curTextWatcher != null) {
            virtualEditText.removeTextChangedListener(curTextWatcher);
        }
        curTextWatcher = textWatcher;
        virtualEditText.addTextChangedListener(textWatcher);
    }

    public void unregisterKeyboardEvent(TextWatcher textWatcher) {
        virtualEditText.removeTextChangedListener(textWatcher);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void requestKeyboard() {
        if (isKeyboardShown) return;
        if (!isCurrentBubbleText()) return;
        showRealEditText();

        // Check if no view has focus:
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        isKeyboardShown = true;
        registerKeyboardEvent((TextWatcher) mListItem.getCurrentItem());
        invalidate();
    }

    private boolean isCurrentBubbleText() {
        return mListItem.getCurrentItem() instanceof TextStickerModel;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void showRealEditText() {
        if (mListItem.getCurrentItem() == null) return;
        if (!isCurrentBubbleText()) return;
        ViewGroup.LayoutParams params = virtualEditText.getLayoutParams();
        params.width = 0;
        params.height = 0;
        virtualEditText.setLayoutParams(params);
        virtualEditText.setBackground(getResources().getDrawable(R.drawable.rect));
        virtualEditText.setTextColor(Color.WHITE);
        virtualEditText.setIncludeFontPadding(true);
        virtualEditText.setPadding(SystemUtils.getDpToPixel(getContext(), 5),
                0, SystemUtils.getDpToPixel(getContext(), 15), virtualEditText.getPaddingBottom());
        virtualEditText.removeTextChangedListener(curTextWatcher);
        virtualEditText.getEditableText().clear();

        virtualEditText.append(((TextStickerModel) mListItem.getCurrentItem()).getText());
        virtualEditText.addTextChangedListener(curTextWatcher);
        virtualEditText.invalidate();
        virtualEditText.requestFocus();
        isKeyboardShown = true;
    }

    public void hideRealEditText() {
        Log.d(TAG, "hideRealEditText");
        ViewGroup.LayoutParams params = virtualEditText.getLayoutParams();
        params.width = 0;
        params.height = 0;
        virtualEditText.setLayoutParams(params);
        virtualEditText.invalidate();
        isKeyboardShown = false;
    }

    public void translateBack() {
        if (true) return;
        if (isTranslated) {
//            Log.d("EE", "vao translate back");
            TranslateAnimation translateAnimation = new TranslateAnimation(0, 0, 0, lastTranslateY);
            translateAnimation.setDuration(250);
            translateAnimation.setFillAfter(true);
            final float oldY = getY();
            translateAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    post(new Runnable() {
                        @Override
                        public void run() {
                            clearAnimation();
                            isTranslated = false;
//                            Log.d("EE", "getX = " + getX() + " getY = " + getY());
                            setY(oldY + lastTranslateY);
                        }
                    });
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            startAnimation(translateAnimation);
        }
    }

    private void performAnimation() {
        if (true) return;
        if (isTranslated) return;
        TextStickerModel textView = (TextStickerModel) mListItem.getCurrentItem();
        final float[] pos = textView.getCurrentPosition();
        final TypedArray styledAttributes = getContext().getTheme().obtainStyledAttributes(
                new int[]{android.R.attr.actionBarSize});
        int actionBarSize = (int) styledAttributes.getDimension(0, 0);
        styledAttributes.recycle();
//        Log.d(TAG, "actionBarSize = " + actionBarSize);

        lastTranslateY = getMeasuredHeight() - pos[1];
        if (getMeasuredHeight() - pos[1] >= keyboardHeight) return;
        TranslateAnimation translateAnimation = new TranslateAnimation(0, 0, 0, -lastTranslateY);
        translateAnimation.setDuration(250);
        translateAnimation.setFillAfter(true);
        final float oldY = getY();
        translateAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                isTranslated = true;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                post(new Runnable() {
                    @Override
                    public void run() {
                        clearAnimation();
                        setY(oldY - lastTranslateY);
                    }
                });
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        startAnimation(translateAnimation);
    }

    public void unSelectAllComponent() {
        mListItem.setNotTouchAll();
        mListPhotos.setCurPhotoIndex(-1);
        invalidate();
    }

    public void dismissKeyboard() {
        Flog.d("dismissKeyboard", "dismissKeyboard");
        if (!isKeyboardShown) return;
//        Log.d(TAG, "dismiss keyboard");
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(virtualEditText.getWindowToken(), 0);
        hideRealEditText();
        if (curTextWatcher != null) {
            virtualEditText.removeTextChangedListener(curTextWatcher);
        }
        isKeyboardShown = false;
    }

    public void setRoundSize(float roundSize) {
        mListPhotos.setRoundnessRects(roundSize);
    }

    public void setSaveBitmapSize() {
        int maxSizeIndex = mListPhotos.getMaxPhotoPixelIndex();

        if (maxSizeIndex < 0)
            return;

//        Flog.i("width=" + width
//                + "_getOrgWidth=" + mListPhotoViews.get(maxSizeIndex).getOrgSrcWidth()
//                + "_getOrgHeight=" + mListPhotoViews.get(maxSizeIndex).getOrgSrcHeight());
        mSaveCanvasWidth = Math.max(MIN_PIXEL_WIDTH_SAVED,
                Math.max(mListPhotos.get(maxSizeIndex).getOrgSrcWidth(),
                        mListPhotos.get(maxSizeIndex).getOrgSrcHeight()));

        mSaveCanvasHeight = (int) (mSaveCanvasWidth * mRatioView);
//        Flog.d(TAG, "mSaveCanvasWidth=" + mSaveCanvasWidth + "_mSaveCanvasHeight=" + mSaveCanvasHeight);
    }

    public void setSaveBitmapSize(int saveCanvasWidth, int saveCanvasHeight) {
        mSaveCanvasWidth = saveCanvasWidth;
        mSaveCanvasHeight = saveCanvasHeight;
    }

    public int getSaveCanvasHeight() {
        return mSaveCanvasHeight;
    }

    public int getSaveCanvasWidth() {
        return mSaveCanvasWidth;
    }

    public void setRatioSavedView(float ratio) {
        mRatioView = ratio;
    }

    public int[] getOldSize() {
        return oldSize;
    }

    public void putOldSize() {
        oldSize[0] = getWidth();
        oldSize[1] = getHeight();
    }

    public void addItem(BaseStickerModel item) {
        mListItem.setNotTouchAll();
        mListItem.add(item);
        mListItem.setCurrentItem(mListItem.size() - 1);
    }

    public int getItemSize() {
        return mListItem.size();
    }

    public BaseStickerModel getCurrentItem() {
        return mListItem.getCurrentItem();
    }

    public ListAdaptiveItem getListItem() {
        return mListItem;
    }

    public void setListItem(ListAdaptiveItem listAdaptiveItem) {
        if (mListItem != null) releaseListItemView(mListItem);
        this.mListItem = listAdaptiveItem;
    }

    private void releaseListItemView(List<? extends BaseModel> list) {
        for (BaseModel item : list) {
            if (item != null) item.release();
        }
    }

    public void release() {
        if (mDragPhotoModel != null) {
            mDragPhotoModel.release();
            mDragPhotoModel = null;
        }
        releaseListItemView(mListItem);
//        releaseListItemView(mListPhotos);
        if (mListPhotos != null) {
            mListPhotos.release();
            mListPhotos = null;
        }
        // Release unused objects:
        mGestureDetector = null;
        mOnCollageViewListenner = null;
        curTextWatcher = null;
        virtualEditText = null;
        r = null;
    }

    @Override
    public void onGlobalLayout() {
        if (r == null)
            return;
        int oldBottom = r.bottom;
        View view = getActivity().getWindow().getDecorView();
        view.getWindowVisibleDisplayFrame(r);
        int height = oldBottom - r.bottom;
        if (height > SystemUtils.dpToPx(getActivity(), 150)) {
            keyboardHeight = height;
            performAnimation();
        }
    }

    public CollageView setOnSwapTwoObj(OnCollageViewListenner onSwapTwoObj) {
        mOnCollageViewListenner = onSwapTwoObj;
        return this;
    }

    @Override
    public void onSwapAction(MotionEvent event) {
        if (mOnCollageViewListenner == null)
            return;
        if (mListPhotos != null && mListPhotos.checkDraggingCollision(event, mListPhotos.getCurPhotoIndex())) {
            mOnCollageViewListenner.onSwapTwoObj(mListPhotos.getCurPhotoIndex(), mListPhotos.getDraggedPhotoIndex());
            mListPhotos.swapTwoPhotoItems(mListPhotos.getCurPhotoIndex(), mListPhotos.getDraggedPhotoIndex());
        }
        mIsDragging = false;
        invalidate();
    }

    public void setDrawed(boolean drawed) {
        mIsDrawedSavePhoto = drawed;
    }

    public interface OnCollageViewListenner {
        void onSwapTwoObj(int curIndex, int dragIndex);

        void onStickerUnselected();
    }
}