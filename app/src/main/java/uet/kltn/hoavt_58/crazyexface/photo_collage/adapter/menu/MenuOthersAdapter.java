package uet.kltn.hoavt_58.crazyexface.photo_collage.adapter.menu;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


import io.karim.MaterialTabs;
import uet.kltn.hoavt_58.crazyexface.R;
import uet.kltn.hoavt_58.crazyexface.photo_collage.fragment.othertabs.OtherBaseFrag;
import uet.kltn.hoavt_58.crazyexface.photo_collage.fragment.othertabs.OtherBgFrag;
import uet.kltn.hoavt_58.crazyexface.photo_collage.fragment.othertabs.OtherFrameFrag;
import uet.kltn.hoavt_58.crazyexface.photo_collage.fragment.othertabs.OtherMarginFrag;
import uet.kltn.hoavt_58.crazyexface.photo_collage.fragment.othertabs.OtherRatioFrag;

/**
 * Created by vutha on 2/17/2017.
 */

public class MenuOthersAdapter extends FragmentPagerAdapter implements MaterialTabs.CustomTabProvider {

    private final int[] ICONS = new int[]{R.drawable.ic_frame,
            R.drawable.ic_bg,
            R.drawable.ic_margin2,
            R.drawable.ic_ratio};
    private Context mContext;
    private OtherBaseFrag tab = null;
    private OtherBaseFrag.OnOtherOptionCallback listener = null;

    public MenuOthersAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        Log.d("MenuOptionOthersFrag", "xxx pos="+position);
        switch (position) {
            case OtherBaseFrag.TAB_MARGIN_INDEX:
                // margin
                tab = new OtherMarginFrag();
                break;
            case OtherBaseFrag.TAB_FRAME_INDEX:
                // frame
                tab = new OtherFrameFrag();
                break;
            case OtherBaseFrag.TAB_BG_INDEX:
                // background
                tab = new OtherBgFrag();
                break;
            default:
                // ratio
                tab = new OtherRatioFrag();
                break;
        }
        return tab;
    }

    @Override
    public int getCount() {
        return ICONS.length;
    }

    @Override
    public View getCustomTabView(ViewGroup parent, int position) {
        ImageView image = (ImageView) View.inflate(parent.getContext(), R.layout.image_tab, null);

        image.setImageResource(ICONS[position]);
        return image;
    }

    public OtherBaseFrag getTab() {
        return tab;
    }

    public void setListener(OtherBaseFrag.OnOtherOptionCallback listener) {
        this.listener = listener;
    }
}

