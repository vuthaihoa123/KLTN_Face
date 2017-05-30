package uet.kltn.hoavt_58.crazyexface.photo_collage.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.pic.libphotocollage.core.util.Flog;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import uet.kltn.hoavt_58.crazyexface.R;
import uet.kltn.hoavt_58.crazyexface.photo_collage.adapter.listener.OnItemMenuListener;
import uet.kltn.hoavt_58.crazyexface.photo_collage.model.FrameModel;
import uet.kltn.hoavt_58.crazyexface.photo_collage.model.LayoutModel;
import uet.kltn.hoavt_58.crazyexface.photo_collage.model.StickerModel;
import uet.kltn.hoavt_58.crazyexface.photo_collage.statistic.Statistic;

/**
 * Created by Adm on 8/4/2016.
 */
public class ItemMenuAdapter extends RecyclerView.Adapter<ItemMenuAdapter.ViewHolder> {
    private static final String TAG = ItemMenuAdapter.class.getSimpleName();
    private boolean isMainMenu = true;
    private Context context = null;
    private List<LayoutModel> mList = null;
    private LayoutModel currentLayoutModel = null;

    private StickerModel stickerModel = null;

    private OnItemMenuListener listener = null;
    private FrameModel currentFrameModel;

    public ItemMenuAdapter(Context context, boolean isMainMenu, List<LayoutModel> list) {
        this.context = context;
        this.isMainMenu = isMainMenu;
        this.mList = list;
    }

    public ItemMenuAdapter setCurrentLayoutModel(LayoutModel model) {
        this.currentLayoutModel = model;
        return this;
    }

    public ItemMenuAdapter setCurrentStickerModel(StickerModel model) {
        this.stickerModel = model;
        return this;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.menu_item_footer, null, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final LayoutModel model = mList.get(position);

        if (model instanceof StickerModel) {
            holder.btnMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null && isMainMenu) {
                        stickerModel = (StickerModel) model;
                        listener.onItemStickerMainMenuSelectListener(stickerModel);
                    } else if (listener != null) {
                        listener.onItemStickerSubMainMenuSelectListener((StickerModel) model);
                    }
                }
            });
        } else if (model instanceof FrameModel) {
            holder.btnMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null && isMainMenu) {
                        currentFrameModel = (FrameModel) model;
                        listener.onItemFrameMainMenuSelectListener(currentFrameModel);
                    } else if (listener != null) {
                        listener.onItemFrameSubMainMenuSelectListener((FrameModel) model);
                    }
                }
            });
        } else {
            holder.btnMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Flog.d(TAG, "isMainMenu="+isMainMenu);
//                    Flog.d(TAG, "1isGrid="+model.isGrid);
                    if (listener != null && isMainMenu) {
                        currentLayoutModel = model;
                        listener.onItemMenuClickListener(model);
                    } else if (listener != null) {
//                        Flog.d(TAG, "2isGrid="+model.isGrid);
                    }
                    listener.onLayoutChangeListener(model);
                }
            });
        }


        try {

            final String layoutName = model.name;
            InputStream inputStream = null;

            if (model instanceof StickerModel) {
                Flog.i("BaseStickerModel");
                inputStream = context.getAssets().open("stickers/" + ((StickerModel) model).index + "/" + layoutName + ".png");


                if (stickerModel != null && stickerModel.index == ((StickerModel) model).index) {
                    holder.rootView.setBackgroundResource(R.color.colorPrimary);
//                    if (mInteractListener != null) {
//                        mInteractListener.onItemStickerMainMenuSelectListener(stickerModel);
//                    }
                } else {
                    holder.rootView.setBackgroundResource(R.color.transparent);
                }

            } else if (model instanceof FrameModel) {
                Flog.i("FrameModel");
                inputStream = context.getAssets().open("frames/" + ((FrameModel) model).index + "/ratio_11/" + layoutName + ".png");


                if (currentFrameModel != null && currentFrameModel.index == ((FrameModel) model).index) {
                    holder.rootView.setBackgroundResource(R.color.colorPrimary);
                } else {
                    holder.rootView.setBackgroundResource(R.color.transparent);
                }
            } else {
                if (model.isGrid) {
                    inputStream = context.getAssets().open("layouts/grids/" + layoutName + ".png");
                } else {
                    inputStream = context.getAssets().open("layouts/piles/" + layoutName + ".png");
                }

                if (currentLayoutModel != null && currentLayoutModel.isGrid == model.isGrid && currentLayoutModel.imgCount == model.imgCount) {
                    holder.rootView.setBackgroundResource(R.color.colorPrimary);
                    if (listener != null) {
                        listener.onItemMenuSelectListener(position);
                    }
                } else {
                    holder.rootView.setBackgroundResource(R.color.transparent);
                }
            }

            // TODO:

            Bitmap bitmapDecoded = null;

            InputStream stream = inputStream;
            BitmapFactory.Options optsResolution = null;
            if (stream != null) {
                optsResolution = new BitmapFactory.Options();
//                optsResolution.inJustDecodeBounds = true;
                optsResolution.inPreferredConfig = Bitmap.Config.ARGB_8888;
//                optsResolution.inMutable = true;
//                int sampleSize = getSampleSize(optsResolution.outWidth
//                        * optsResolution.outHeight, SystemUtils.MAX_THUMBNAILS_SIZE);
                int sampleSize = Statistic.SAMPLE_SIZE_THUMNAILS;
                optsResolution.inSampleSize = sampleSize;
                optsResolution.inJustDecodeBounds = false;

                try {
                    bitmapDecoded = BitmapFactory.decodeStream(stream, null,
                            optsResolution);
//                    if (bitmapDecoded != null) {
//
//                        int sampleSizeDecoded = getSampleSize(bitmapDecoded.getWidth()
//                                * bitmapDecoded.getHeight(), SystemUtils.MAX_THUMBNAILS_SIZE);
//                        Flog.i("sampleSizeDecoded: " + sampleSizeDecoded);
//                        if (sampleSizeDecoded > 1) {
//                            Bitmap tmp = Bitmap.createScaledBitmap(bitmapDecoded,
//                                    bitmapDecoded.getWidth() / sampleSizeDecoded,
//                                    bitmapDecoded.getHeight() / sampleSizeDecoded, true);
//                            bitmapDecoded.recycle();
//                            bitmapDecoded = tmp;
//                        }
//                    } else {
//                        Flog.i("bitmapDecoded is null");
//                    }
                } catch (NullPointerException e) {
                    bitmapDecoded = null;
                    e.printStackTrace();
                } catch (OutOfMemoryError e) {
                    bitmapDecoded = null;
                    e.printStackTrace();
                }

                if (stream != null) {
                    try {
                        stream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                holder.menuImg.setImageBitmap(bitmapDecoded);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }


    public ItemMenuAdapter setOnItemMenuListener(OnItemMenuListener listener) {

        this.listener = listener;
        return this;
    }

    public void setCurrentFrameModel(FrameModel currentFrameModel) {
        this.currentFrameModel = currentFrameModel;
    }

    private int getSampleSize(int bitmapSize, int maxSize) {
        int ret = 1;
        if (bitmapSize > maxSize) {
            for (int i = 1; (bitmapSize / i) > maxSize; i *= 2) {
                ret = i;
            }
            ret++;
        }
        return ret;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayoutCompat btnMenu;
        ImageView menuImg;
        View rootView;

        public ViewHolder(View itemView) {
            super(itemView);
            rootView = itemView;
            btnMenu = (LinearLayoutCompat) itemView.findViewById(R.id.btn_menu);
            menuImg = (ImageView) itemView.findViewById(R.id.menu_img);
        }
    }
}
