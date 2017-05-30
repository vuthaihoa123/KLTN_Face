package uet.kltn.hoavt_58.crazyexface.photo_collage.adapter.menu;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


import java.io.IOException;
import java.util.List;

import uet.kltn.hoavt_58.crazyexface.R;
import uet.kltn.hoavt_58.crazyexface.photo_collage.adapter.listener.OnMenuFrameListener;

/**
 * Created by vutha on 8/31/2016.
 */
public class MenuFrameAdapter extends RecyclerView.Adapter<MenuFrameAdapter.ViewHolder> {

    private Context context = null;
    private List<String> mList = null;

    private OnMenuFrameListener listener = null;

    public MenuFrameAdapter(Context context, List<String> list) {
        this.context = context;
        this.mList = list;
    }

    public MenuFrameAdapter setOnMenuFrameListener(OnMenuFrameListener listener) {
        this.listener = listener;
        return this;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.menu_item_frame, null, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final String bgTopName = mList.get(2 * position);
        final String bgBottomName = mList.get(2 * position + 1);
//        Flog.i("bgTopName: " + bgTopName);

        try {
//            holder.bgTop.setImageBitmap(BitmapFactory.decodeStream(context.getResources().getAssets().open("bg/"+bgTopName)));
//            holder.bgBottom.setImageBitmap(BitmapFactory.decodeStream(context.getResources().getAssets().open("bg/"+bgBottomName)));

            RoundedBitmapDrawable circularBitmapDrawable =
                    RoundedBitmapDrawableFactory.create(context.getResources(), BitmapFactory.decodeStream(context.getResources().getAssets().open("bg/"+bgTopName)));
            circularBitmapDrawable.setCornerRadius(40);
            holder.bgTop.setImageDrawable(circularBitmapDrawable);

            RoundedBitmapDrawable circularBitmapDrawable1 =
                    RoundedBitmapDrawableFactory.create(context.getResources(), BitmapFactory.decodeStream(context.getResources().getAssets().open("bg/"+bgBottomName)));
            circularBitmapDrawable1.setCornerRadius(40);
            holder.bgBottom.setImageDrawable(circularBitmapDrawable1);
        } catch (IOException e) {
            e.printStackTrace();
        }

//        Glide.with(context)
//                .load(Uri.parse("file:///android_asset/bg/" + mList.get(position)))
//                .skipMemoryCache(true)
//                .diskCacheStrategy(DiskCacheStrategy.NONE)
//                .centerCrop()
//                .into(holder.bgIcon);
//
//        Glide.with(context)
//                .load(Uri.parse("file:///android_asset/bg/" + bgBottomName))
//                .skipMemoryCache(true)
//                .diskCacheStrategy(DiskCacheStrategy.NONE)
//                .centerCrop()
//                .into(holder.bgBottom);

        holder.bgTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onMenuFrameClickListener(bgTopName);
                }
            }
        });


        holder.bgBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onMenuFrameClickListener(bgBottomName);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size()/2;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView bgTop, bgBottom;

        public ViewHolder(View itemView) {
            super(itemView);
            bgTop = (ImageView) itemView.findViewById(R.id.bg_top);
            bgBottom = (ImageView) itemView.findViewById(R.id.bg_bottom);
        }
    }
}
