package com.pic.libphotocollage.core.collection;

import android.graphics.Canvas;
import android.os.Handler;
import android.view.MotionEvent;

import com.pic.libphotocollage.core.model.BaseModel;
import com.pic.libphotocollage.core.model.BaseStickerModel;
import com.pic.libphotocollage.core.model.IconStickerModel;
import com.pic.libphotocollage.core.util.Flog;

import java.util.ArrayList;

/**
 * Created by thuck on 9/19/2016.
 */
public class ListAdaptiveItem extends ArrayList<BaseStickerModel> {
    private static final java.lang.String TAG = ListAdaptiveItem.class.getSimpleName();
    private int mCurrentItemIndex = -1;

    public void onDraw(Canvas canvas) {
        // draw stickers
        for (BaseModel item : this) {
            if (item != null) {
                item.draw(canvas);
            }
        }
    }

    @Override
    public boolean remove(Object object) {
        return super.remove(object);
    }

    public BaseStickerModel getCurrentItem() {
        int index = mCurrentItemIndex;
        if (index < 0 || index >= this.size()) {
            Flog.i("itemsticker current is null at getCurrentSticker");
            return null;
        }
        return this.get(index);
    }

    public void setCurrentItem(int index) {
        mCurrentItemIndex = index;
//        if (index < 0 || index >= this.size()) {
//            Flog.i("itemsticker current is null");
//            return;
//        }

        for (int i = 0; i < this.size(); i++) {
            BaseStickerModel item = this.get(i);
            if (i != index && item != null) {
                item.setInEdit(false);
//                return;
            } else if (i == index && item != null) {
                item.setInEdit(true);
//                return;
            }
        }
    }

    public boolean isInEdit() {
        for (BaseStickerModel item : this) {
            if (item != null && item.isInEdit()) return true;
        }
        return false;
    }

    public boolean setCurrentItem(MotionEvent event) {
        boolean result = false;

        for (int i = this.size() - 1; i >= 0; i--) {
            final BaseStickerModel item = this.get(i);
            if (item != null && item.isInBitmap(event) && !result) {
                mCurrentItemIndex = i;
                item.setInEdit(true);
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        item.getInteractListener().onItemClicked(item, true);
                    }
                });
                result = true;
            } else if (item != null) {
                item.setInEdit(false);
            }
        }
        if (!result)
            mCurrentItemIndex = -1;
        return result;
    }

    public boolean isOutSideItem() {
        BaseModel baseItem = getCurrentItem();
        if (baseItem != null) {
            return ((IconStickerModel) baseItem).isOutSide();
        }
        return false;
    }

    public boolean onTouchEvent(MotionEvent event) {
        BaseModel baseItem = getCurrentItem();
        if (baseItem != null) {
            if (baseItem.onTouchEvent(event)) {
                return true;
            }
        } else {
            Flog.d(TAG, "ItemSticker == null");
        }
        return false;
    }

    public boolean onTouchEvent1(MotionEvent event) {
        for (BaseModel model : this) {
            if (model != null && model.onTouchEvent(event))
                return true;
        }
        ;
//        BaseModel baseItem = getCurrentItem();
//        if (baseItem != null) {
//            if (baseItem.onTouchEvent(event)) {
//                return true;
//            }
//        } else {
//            Flog.d(TAG, "ItemSticker == null");
//        }
        return false;
    }

    public boolean notTouchAll() {
        for (BaseStickerModel item : this) {
            if (item != null && item.isInEdit())
                return false;
        }
        return true;
    }

    public boolean isDeleteAll() {
        for (BaseStickerModel item : this) {
            if (item != null && !item.isItemDeleted())
                return false;
        }
        return true;
    }

    public void setNotTouchAll() {
        for (BaseStickerModel item : this) {
            if (item != null) {
                item.setInEdit(false);
            }
        }
        setCurrentItem(-1);
    }

    public void invalidate() {
        for (BaseStickerModel item : this) {
            if (item != null) {
                item.invalidateRatio();
            }
        }
    }

    public int getCurrentItemIndex() {
        return mCurrentItemIndex;
    }
}
