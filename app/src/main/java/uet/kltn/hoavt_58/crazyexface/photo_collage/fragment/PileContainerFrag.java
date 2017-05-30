package uet.kltn.hoavt_58.crazyexface.photo_collage.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.viewpagerindicator.CirclePageIndicator;

import uet.kltn.hoavt_58.crazyexface.R;
import uet.kltn.hoavt_58.crazyexface.photo_collage.adapter.GridAdapter;
import uet.kltn.hoavt_58.crazyexface.photo_collage.statistic.Statistic;

/**
 * Created by Adm on 8/3/2016.
 */
public class PileContainerFrag extends BaseFragment {
    private ViewPager viewPager = null;
    private CirclePageIndicator circlePageIndicator = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.content_view_pager, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViewPager();
    }

    private void initViewPager() {
        viewPager = (ViewPager) getView().findViewById(R.id.view_pager);
        circlePageIndicator = (CirclePageIndicator) getView().findViewById(R.id.circle_page_indicator);

        viewPager.setAdapter(new GridAdapter(getChildFragmentManager(), getActivity(), Statistic.PILE_LAYOUT_CODE));
        circlePageIndicator.setViewPager(viewPager);
    }
}
