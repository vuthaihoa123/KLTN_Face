package uet.kltn.hoavt_58.crazyexface.photo_collage.adapter.listener;

import android.support.annotation.ColorInt;

/**
 * Created by Adm on 8/3/2016.
 */
public interface OnMenuBackgroundListener {

    public void onMenuColorBackgroundClickListener(@ColorInt int color);

    public void onMenuBackgroundClickListener(String bgName, boolean isPattern);
}
