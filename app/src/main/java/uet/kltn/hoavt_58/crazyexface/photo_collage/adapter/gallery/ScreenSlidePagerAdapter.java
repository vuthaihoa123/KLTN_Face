package uet.kltn.hoavt_58.crazyexface.photo_collage.adapter.gallery;

/**
 * Created by vutha on 10/4/2016.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;


import java.util.ArrayList;

import uet.kltn.hoavt_58.crazyexface.photo_collage.fragment.gallery.ScreenSlidePageFragment;
import uet.kltn.hoavt_58.crazyexface.photo_collage.statistic.Statistic;

/**
 * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
 * sequence.
 */
public class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
    /**
     * The number of pages (wizard steps) to show in this demo.
     */
    private int mNumOfPages = 0;
    private ArrayList<String> mArrPathRes;

    public ScreenSlidePagerAdapter(FragmentManager fm, ArrayList<String> arrayList) {
        super(fm);
        mArrPathRes = arrayList;
        mNumOfPages = arrayList.size();
    }

    @Override
    public Fragment getItem(int position) {
        String path = mArrPathRes.get(position);

        ScreenSlidePageFragment slidePageFrag = new ScreenSlidePageFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Statistic.EXTRA_PATH_PHOTO, path);
        slidePageFrag.setArguments(bundle);
        return slidePageFrag;
    }

    @Override
    public int getCount() {
        return mNumOfPages;
    }
}

