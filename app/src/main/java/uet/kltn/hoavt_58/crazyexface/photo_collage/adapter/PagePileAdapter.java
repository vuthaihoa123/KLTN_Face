package uet.kltn.hoavt_58.crazyexface.photo_collage.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import uet.kltn.hoavt_58.crazyexface.photo_collage.model.LayoutModel;
import uet.kltn.hoavt_58.crazyexface.photo_collage.statistic.Statistic;

/**
 * Created by Adm on 8/3/2016.
 */
public class PagePileAdapter extends PageGridAdapter {


    public PagePileAdapter(Context context, List<LayoutModel> mList) {
        super(context, mList);
    }

    @Override
    public void onBindViewHolder(PageGridAdapter.ViewHolder holder, final int position) {
        final LayoutModel model = mList.get(position);
        final String layoutName = model.name;
        holder.wrapperView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onItemLayoutClickListener(model, Statistic.PILE_LAYOUT_CODE);
                }
            }
        });


        InputStream inputStream = null;
        try {
            inputStream = mContext.getAssets().open("layouts/piles/" + layoutName + ".png");
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
}
