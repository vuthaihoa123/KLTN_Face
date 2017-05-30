package uet.kltn.hoavt_58.crazyexface.helpers;

import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.widget.TextView;

/**
 * Created by hoavt_58 on 4/9/17.
 */

public class AnimationUtils {
    public static void startAnimation(TextView textView) {
        Drawable[] drawables = textView.getCompoundDrawables();
        for (Drawable drawable : drawables) {
            if (drawable != null && drawable instanceof Animatable) {
                ((Animatable) drawable).start();
            }
        }
    }
}
