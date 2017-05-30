package uet.kltn.hoavt_58.crazyexface.photo_collage.fragment.menu;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;


import io.karim.MaterialTabs;
import uet.kltn.hoavt_58.crazyexface.R;
import uet.kltn.hoavt_58.crazyexface.photo_collage.adapter.listener.OnCloseFragListener;
import uet.kltn.hoavt_58.crazyexface.photo_collage.adapter.menu.MenuOthersAdapter;
import uet.kltn.hoavt_58.crazyexface.photo_collage.fragment.BaseFragment;
import uet.kltn.hoavt_58.crazyexface.photo_collage.fragment.CollageContainerFrag;
import uet.kltn.hoavt_58.crazyexface.photo_collage.fragment.othertabs.OtherBaseFrag;
import uet.kltn.hoavt_58.crazyexface.photo_collage.statistic.Statistic;
import uet.kltn.hoavt_58.crazyexface.photo_collage.ui.WrappingViewPager;

/**
 * Created by Adm on 8/8/2016.
 */
public class MenuOptionOthersFrag extends BaseFragment implements MaterialTabs.OnTabSelectedListener {

    private static final String TAG = MenuOptionOthersFrag.class.getSimpleName();
    private WrappingViewPager viewPager = null;
    private MaterialTabs tabs = null;
    private MenuOthersAdapter mOthersAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.content_collage_menu_others_view, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initTabLayout();
    }

    private void initTabLayout() {
        viewPager = (WrappingViewPager) getView().findViewById(R.id.view_pager);
        viewPager.setPagingEnabled(false);
        tabs = (MaterialTabs) getView().findViewById(R.id.material_tabs);
        mOthersAdapter = new MenuOthersAdapter(getActivity(), getChildFragmentManager());
        viewPager.setAdapter(mOthersAdapter);
        tabs.setViewPager(viewPager);

//        /**
//         * Call once when start fragment: model != null, otherwise: model == null.
//         * */
//        if (model!=null && !model.isGrid) {
//            showDontSetMarginPileDialog();
//        }

        tabs.setOnTabSelectedListener(this);

        if (getArguments()!= null){
            if (getArguments().getBoolean(Statistic.EXTRA_CLOSE_OPTION)) {
                int tabIndex = getArguments().getInt(Statistic.EXTRA_CURRENT_TAB);
                Log.d("onCloseCallback", "run me="+tabIndex);
                viewPager.setIsHide(true);
            }
        }
    }

    private void showDontSetMarginPileDialog() {
        ((OnCloseFragListener)getActivity()).onCloseCallback(OtherBaseFrag.TAB_MARGIN_INDEX);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AppCompatAlertDialogStyle);
        builder.setMessage(getActivity().getString(R.string.donot_set_margin_for_pile));
        builder.setPositiveButton(getActivity().getString(R.string.ok), null);
        builder.setCancelable(false);
        builder.show();
    }

    @Override
    public void onTabSelected(int position) {
        Log.d("onCloseCallback", "x position="+position);
        if (position == OtherBaseFrag.TAB_MARGIN_INDEX) {            // Option margin
            if (CollageContainerFrag.IS_PILE_TYPE) {
                showDontSetMarginPileDialog();
                return;
            }
        } else if (position == OtherBaseFrag.TAB_FRAME_INDEX) {     // Option frame

        } else if (position == OtherBaseFrag.TAB_BG_INDEX){      // Option Background

        } else {                        // Option Aspect Ratio

        }

        if (viewPager!=null)    {
            viewPager.setIsHide(false);
            viewPager.requestLayout();
            Animation slideUp = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_up);
            viewPager.startAnimation(slideUp);
        }
    }

    public void setHeightPager(int height) {
        if (height < 0) return;
        ViewGroup.LayoutParams params = viewPager.getLayoutParams();
        params.height = height;
        viewPager.setLayoutParams(params);
        viewPager.requestLayout();
    }

    public int getCurTab() {
        return viewPager.getCurrentItem();
    }
}
