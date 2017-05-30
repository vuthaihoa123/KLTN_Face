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

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import uet.kltn.hoavt_58.crazyexface.R;
import uet.kltn.hoavt_58.crazyexface.photo_collage.model.LayoutModel;
import uet.kltn.hoavt_58.crazyexface.photo_collage.statistic.Statistic;

/**
 * Created by Adm on 8/3/2016.
 */
public class PageGridAdapter extends RecyclerView.Adapter<PageGridAdapter.ViewHolder> {
    private static final int PAGE_GRID_ITEM_COUNT = 8;
    private static final String TAG = PageGridAdapter.class.getSimpleName();
    protected Context mContext = null;
    protected List<LayoutModel> mList = null;

    protected OnItemLayoutListener listener = null;

    public PageGridAdapter(Context context, List<LayoutModel> mList) {
        mContext = context;
        this.mList = mList;
    }


    @Override
    public PageGridAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_grid_layout, null, false);
        PageGridAdapter.ViewHolder viewHolder = new PageGridAdapter.ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(PageGridAdapter.ViewHolder holder, final int position) {
        final LayoutModel model = mList.get(position);
        final String layoutName = model.name;
        holder.wrapperView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onItemLayoutClickListener(model, Statistic.GRID_LAYOUT_CODE);
                }
            }
        });

        InputStream inputStream = null;
        try {
            inputStream = mContext.getAssets().open("layouts/grids/" + layoutName + ".png");
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            // Calculate inSampleSize
            options.inSampleSize = Statistic.SAMPLE_SIZE_THUMNAILS;
            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            holder.imgLayout.setImageBitmap(BitmapFactory.decodeStream(inputStream, null, options));
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }


    public PageGridAdapter setOnItemLayoutListener(OnItemLayoutListener listener) {
        this.listener = listener;
        return this;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        public LinearLayoutCompat wrapperView;
        public ImageView imgLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            wrapperView = (LinearLayoutCompat) itemView.findViewById(R.id.btn_layout);
            imgLayout = (ImageView) itemView.findViewById(R.id.layout_img);
        }
    }
}
