package uet.kltn.hoavt_58.crazyexface.photo_collage.adapter.listener;


import uet.kltn.hoavt_58.crazyexface.photo_collage.model.FrameModel;
import uet.kltn.hoavt_58.crazyexface.photo_collage.model.LayoutModel;
import uet.kltn.hoavt_58.crazyexface.photo_collage.model.StickerModel;

/**
 * Created by Adm on 8/3/2016.
 */
public interface OnItemMenuListener {

    public void onItemMenuSelectListener(int position);

    public void onItemMenuClickListener(LayoutModel model);

    public void onLayoutChangeListener(LayoutModel model);


    // sticker
    public void onItemStickerMainMenuSelectListener(StickerModel model);

    public void onItemStickerSubMainMenuSelectListener(StickerModel model);

    // frames
    public void onItemFrameMainMenuSelectListener(FrameModel model);

    public void onItemFrameSubMainMenuSelectListener(FrameModel model);
}
