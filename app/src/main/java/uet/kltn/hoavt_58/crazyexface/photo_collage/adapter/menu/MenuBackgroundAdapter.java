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
import uet.kltn.hoavt_58.crazyexface.photo_collage.adapter.listener.OnMenuBackgroundListener;

/**
 * Created by Adm on 8/8/2016.
 */
public class MenuBackgroundAdapter extends RecyclerView.Adapter<MenuBackgroundAdapter.ViewHolder> {

    private Context mContext = null;
    private List<String> mList = null;

    private OnMenuBackgroundListener mListener = null;
    private boolean mIsPattern = false;

    public MenuBackgroundAdapter(Context context, List<String> list, boolean isPattern) {
        mContext = context;
        mList = list;
        mIsPattern = isPattern;
    }

    public MenuBackgroundAdapter setOnMenuBackgroundListener(OnMenuBackgroundListener listener) {
        this.mListener = listener;
        return this;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.menu_item_bg, null, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final String bgTopName = mList.get(2 * position);
        final String bgBottomName = mList.get(2 * position + 1);

        try {
//            holder.bgTop.setImageBitmap(BitmapFactory.decodeStream(mContext.getResources().getAssets().open("bg/"+bgTopName)));
//            holder.bgBottom.setImageBitmap(BitmapFactory.decodeStream(mContext.getResources().getAssets().open("bg/"+bgBottomName)));

            RoundedBitmapDrawable circularBitmapDrawable =
                    RoundedBitmapDrawableFactory.create(mContext.getResources(), BitmapFactory.decodeStream(mContext.getResources().getAssets().open("bg/"+bgTopName)));
            circularBitmapDrawable.setCornerRadius(40);
            holder.bgTop.setImageDrawable(circularBitmapDrawable);

            RoundedBitmapDrawable circularBitmapDrawable1 =
                    RoundedBitmapDrawableFactory.create(mContext.getResources(), BitmapFactory.decodeStream(mContext.getResources().getAssets().open("bg/"+bgBottomName)));
            circularBitmapDrawable1.setCornerRadius(40);
            holder.bgBottom.setImageDrawable(circularBitmapDrawable1);
        } catch (IOException e) {
            e.printStackTrace();
        }
//        Glide.with(mContext)
//                .load(Uri.parse("file:///android_asset/bg/" + bgTopName))
//                .skipMemoryCache(true)
//                .diskCacheStrategy(DiskCacheStrategy.NONE)
//                .centerCrop()
//                .into(holder.bgTop);
//
//        Glide.with(mContext)
//                .load(Uri.parse("file:///android_asset/bg/" + bgBottomName))
//                .skipMemoryCache(true)
//                .diskCacheStrategy(DiskCacheStrategy.NONE)
//                .centerCrop()
//                .into(holder.bgBottom);

        holder.bgTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onMenuBackgroundClickListener(bgTopName, mIsPattern);
                }
            }
        });


        holder.bgBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onMenuBackgroundClickListener(bgBottomName, mIsPattern);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size() / 2;
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
