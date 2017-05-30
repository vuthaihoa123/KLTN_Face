package uet.kltn.hoavt_58.crazyexface.photo_collage.fragment.menu;

import java.util.ArrayList;

import uet.kltn.hoavt_58.crazyexface.photo_collage.adapter.listener.OnItemMenuListener;
import uet.kltn.hoavt_58.crazyexface.photo_collage.fragment.CollageMenuDetailFrag;
import uet.kltn.hoavt_58.crazyexface.photo_collage.model.FrameModel;
import uet.kltn.hoavt_58.crazyexface.photo_collage.model.LayoutModel;
import uet.kltn.hoavt_58.crazyexface.photo_collage.model.StickerModel;
import uet.kltn.hoavt_58.crazyexface.photo_collage.statistic.Statistic;
import uet.kltn.hoavt_58.crazyexface.photo_collage.utils.FLog;

/**
 * Created by Adm on 8/4/2016.
 */
public class MenuLayoutFrag extends CollageMenuDetailFrag {
    @Override
    public void initList() {
        mainList = new ArrayList<>();
        subList = new ArrayList<>();


        FLog.d("current model: " + model);

        mainList.addAll(Statistic.createMainLayoutList());
        subList.addAll(Statistic.createSubLayoutList(model));

    }

    @Override
    public void onItemMenuSelectListener(int position) {
        FLog.d("scrollToPosition: " + position);
        mmenuRecycleView.scrollToPosition(position);
    }

    @Override
    public void onItemMenuClickListener(LayoutModel model) {
        this.model = model.clone();
        FLog.d("onItemMenuClickListener: " + model);
        subList.clear();
        subList.addAll(Statistic.createSubLayoutList(model));
        subAdapter.notifyDataSetChanged();
        mAdapter.notifyDataSetChanged();

        if (getActivity() instanceof OnItemMenuListener) {
            ((OnItemMenuListener) getActivity()).onItemMenuClickListener(model);
        }


    }

    @Override
    public void onLayoutChangeListener(LayoutModel model) {
        if (getActivity() instanceof OnItemMenuListener) {
            ((OnItemMenuListener) getActivity()).onLayoutChangeListener(model);
        }
    }


    public void onItemStickerMainMenuSelectListener(StickerModel model) {

    }

    public void onItemStickerSubMainMenuSelectListener(StickerModel model) {

    }

    @Override
    public void onItemFrameMainMenuSelectListener(FrameModel model) {

    }

    @Override
    public void onItemFrameSubMainMenuSelectListener(FrameModel model) {

    }
}
