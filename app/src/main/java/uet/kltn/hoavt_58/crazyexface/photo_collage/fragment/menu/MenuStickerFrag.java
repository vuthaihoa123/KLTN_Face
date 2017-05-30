package uet.kltn.hoavt_58.crazyexface.photo_collage.fragment.menu;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import java.util.ArrayList;

import uet.kltn.hoavt_58.crazyexface.photo_collage.adapter.listener.OnItemMenuListener;
import uet.kltn.hoavt_58.crazyexface.photo_collage.fragment.CollageMenuDetailFrag;
import uet.kltn.hoavt_58.crazyexface.photo_collage.model.FrameModel;
import uet.kltn.hoavt_58.crazyexface.photo_collage.model.LayoutModel;
import uet.kltn.hoavt_58.crazyexface.photo_collage.model.StickerModel;
import uet.kltn.hoavt_58.crazyexface.photo_collage.statistic.Statistic;

/**
 * Created by Adm on 8/4/2016.
 */
public class MenuStickerFrag extends CollageMenuDetailFrag {


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        stickerModel = new StickerModel();
        stickerModel.index = 0;
        mAdapter.setCurrentStickerModel(stickerModel);
    }

    @Override
    public void initList() {
        mainList = new ArrayList<>();
        subList = new ArrayList<>();

        mainList.addAll(Statistic.createMainStickerList());
        subList.addAll(Statistic.createSubStickerList(0));

    }

    @Override
    public void onItemMenuSelectListener(int position) {
    }

    @Override
    public void onItemMenuClickListener(LayoutModel model) {
    }

    @Override
    public void onLayoutChangeListener(LayoutModel model) {

    }

    @Override
    public void onItemStickerMainMenuSelectListener(StickerModel model) {

        subList.clear();
        subList.addAll(Statistic.createSubStickerList(model.index));
        // Scroll sub menu to first item :
        submenuRecycleView.scrollToPosition(0);
        subAdapter.notifyDataSetChanged();
        mAdapter.notifyDataSetChanged();

    }

    @Override
    public void onItemStickerSubMainMenuSelectListener(StickerModel model) {
        if (getActivity() instanceof OnItemMenuListener) {
            ((OnItemMenuListener) getActivity()).onItemStickerSubMainMenuSelectListener(model);
        }
    }

    public void onItemFrameMainMenuSelectListener(FrameModel model) {

    }

    public void onItemFrameSubMainMenuSelectListener(FrameModel model) {

    }
}
