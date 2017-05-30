package uet.kltn.hoavt_58.crazyexface.photo_collage.adapter.menu;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import io.karim.MaterialTabs;
import uet.kltn.hoavt_58.crazyexface.R;
import uet.kltn.hoavt_58.crazyexface.photo_collage.fragment.texttabs.TextBaseFragment;
import uet.kltn.hoavt_58.crazyexface.photo_collage.fragment.texttabs.TextFontFragment;
import uet.kltn.hoavt_58.crazyexface.photo_collage.fragment.texttabs.TextKeyboardFragment;
import uet.kltn.hoavt_58.crazyexface.photo_collage.fragment.texttabs.TextSettingFragment;

/**
 * Created by Adm on 8/16/2016.
 */
public class MenuTextAdapter extends FragmentPagerAdapter implements MaterialTabs.CustomTabProvider {

    private final int[] ICONS = new int[]{R.drawable.ic_keyboard_white_24px,
            R.drawable.ic_font_download_white_24px,
            R.drawable.ic_settings};
    private TextBaseFragment tab = null;
    private TextBaseFragment.OnTextOptionCallback listener = null;
    private Bundle mData;

    public MenuTextAdapter(FragmentManager fm, Bundle data) {
        super(fm);
        this.mData = data;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                tab = new TextKeyboardFragment();
                break;
            case 1:
                // fonts
                tab = new TextFontFragment();
                break;
            default:
                // color,size,...
                tab = new TextSettingFragment();
                tab.setArguments(mData);
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
}
