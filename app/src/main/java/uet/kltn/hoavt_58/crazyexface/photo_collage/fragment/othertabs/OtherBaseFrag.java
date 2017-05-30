package uet.kltn.hoavt_58.crazyexface.photo_collage.fragment.othertabs;

import android.support.v4.app.Fragment;

/**
 * Created by hoavt on 17/02/2017.
 */
public class OtherBaseFrag extends Fragment {

    public static final int TAB_MARGIN_INDEX = 2;
    public static final int TAB_FRAME_INDEX = 0;
    public static final int TAB_BG_INDEX = 1;
    public static final int TAB_RATIO_INDEX = 3;

    public interface OnOtherOptionCallback {
        public void onCloseOption();
    }
}
