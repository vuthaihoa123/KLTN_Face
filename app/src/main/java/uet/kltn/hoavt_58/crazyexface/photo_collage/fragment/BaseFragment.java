package uet.kltn.hoavt_58.crazyexface.photo_collage.fragment;

import android.support.v4.app.Fragment;

import uet.kltn.hoavt_58.crazyexface.photo_collage.model.FrameModel;
import uet.kltn.hoavt_58.crazyexface.photo_collage.model.LayoutModel;
import uet.kltn.hoavt_58.crazyexface.photo_collage.model.StickerModel;
import uet.kltn.hoavt_58.crazyexface.photo_collage.statistic.Statistic;

import static uet.kltn.hoavt_58.crazyexface.photo_collage.fragment.CollageContainerFrag.IS_PILE_TYPE;


/**
 * Created by Adm on 8/3/2016.
 */
public class BaseFragment extends Fragment {
    protected LayoutModel model;
    protected StickerModel stickerModel;
    protected FrameModel frameModel;
    protected int menuType = Statistic.MENU_COLLAGE_TYPE_LAYOUT;

    public BaseFragment setLayoutModel(LayoutModel model) {
        this.model = model;
        IS_PILE_TYPE = !this.model.isGrid;
        return this;
    }

    public BaseFragment setStickerModel(StickerModel model) {
        this.stickerModel = model;
        return this;
    }

    public BaseFragment setFrameModel(FrameModel model) {
        this.frameModel = model;

        return this;
    }


    public BaseFragment setMenuType(int menuType) {
        this.menuType = menuType;

        return this;
    }
}
