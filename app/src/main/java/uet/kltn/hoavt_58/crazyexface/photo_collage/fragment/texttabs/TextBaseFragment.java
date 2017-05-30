package uet.kltn.hoavt_58.crazyexface.photo_collage.fragment.texttabs;

import android.support.v4.app.Fragment;

/**
 * Created by hoavt on 15/08/2016.
 */
public class TextBaseFragment extends Fragment {

    protected OnTextOptionCallback mOnKeyboardSoftListener = null;

    public interface OnTextOptionCallback {
        public void onKeyBoardSoftDisplayed();

        public void onFontDisplayed();

        public void onSettingDisplayed();

        public void onKeyBoardShown(int height);
    }
}
