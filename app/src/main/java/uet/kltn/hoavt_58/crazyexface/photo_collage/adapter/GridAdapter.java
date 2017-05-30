package uet.kltn.hoavt_58.crazyexface.photo_collage.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import uet.kltn.hoavt_58.crazyexface.photo_collage.fragment.GridItemFrag;
import uet.kltn.hoavt_58.crazyexface.photo_collage.statistic.Statistic;

/**
 * Created by Adm on 8/3/2016.
 */
public class GridAdapter extends FragmentPagerAdapter {
    private static final String TAG = GridAdapter.class.getSimpleName();
    private Context mContext = null;
    private Fragment item = null;
    private int layoutCode = Statistic.GRID_LAYOUT_CODE;

    public GridAdapter(FragmentManager fm, Context context, int layoutCode) {
        super(fm);
        mContext = context;
        this.layoutCode = layoutCode;
    }

    @Override
    public Fragment getItem(int position) {
        item = new GridItemFrag();
        Bundle args = new Bundle();
        args.putInt("INDEX", position);
        args.putInt("LAYOUT_CODE", layoutCode);
        item.setArguments(args);
        return item;
    }

    @Override
    public int getCount() {
        if (layoutCode == Statistic.PILE_LAYOUT_CODE) {
            return Statistic.getTotalPageForPileLayout();
        }
        return Statistic.getTotalPageForGridLayout();
    }
}
