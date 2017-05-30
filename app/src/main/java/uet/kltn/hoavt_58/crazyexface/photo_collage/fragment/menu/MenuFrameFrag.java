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
 * Created by Adm on 8/9/2016.
 */
public class MenuFrameFrag extends CollageMenuDetailFrag {


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        frameModel = new FrameModel();
        frameModel.index = 0;
        mAdapter.setCurrentFrameModel(frameModel);
    }

    @Override
    public void initList() {
        mainList = new ArrayList<>();
        subList = new ArrayList<>();


        mainList.addAll(Statistic.createMainFrameList());
        subList.addAll(Statistic.createSubFrameList(0));
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

    public void onItemStickerMainMenuSelectListener(StickerModel model) {


    }

    public void onItemStickerSubMainMenuSelectListener(StickerModel model) {

    }

    public void onItemFrameMainMenuSelectListener(FrameModel model) {
        subList.clear();
        subList.addAll(Statistic.createSubFrameList(model.index));
        subAdapter.notifyDataSetChanged();
        mAdapter.notifyDataSetChanged();
    }

    public void onItemFrameSubMainMenuSelectListener(FrameModel model) {
        if (getActivity() instanceof OnItemMenuListener) {
            ((OnItemMenuListener) getActivity()).onItemFrameSubMainMenuSelectListener(model);
        }
    }
}