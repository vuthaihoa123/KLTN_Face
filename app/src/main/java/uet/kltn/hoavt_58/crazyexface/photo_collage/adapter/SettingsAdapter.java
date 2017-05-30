package uet.kltn.hoavt_58.crazyexface.photo_collage.adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.ArrayList;

import uet.kltn.hoavt_58.crazyexface.R;
import uet.kltn.hoavt_58.crazyexface.photo_collage.model.ItemSetting;

/**
 * Created by hoavt on 09/07/2016.
 */
public class SettingsAdapter extends RecyclerView.Adapter<SettingsAdapter.ViewHolder> {
    private ArrayList<ItemSetting> mItems = new ArrayList<>();
    private Context mContext;
    private OnClickSettings mOnClickSettings;

    // Provide a suitable constructor (depends on the kind of dataset)
    public SettingsAdapter(Context context, ArrayList<ItemSetting> myItems) {
        mContext = context;
        mItems = myItems;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public SettingsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
//                .inflate(R.layout.item_settings_layout, parent, false);
                .inflate(R.layout.item_settings_home_layout, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(SettingsAdapter.ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        ImageView ivIcon = holder.mIconSetting;
        TextView tvDes = holder.mTvDescribe;
        LinearLayoutCompat parentView = holder.mParentView;
        ItemSetting itemSetting = mItems.get(position);
        int idRes = itemSetting.getIdResImg();
        if (idRes != -1) ivIcon.setImageResource(idRes);
        String describe = itemSetting.getDescribe();
        if (describe != null) tvDes.setText(describe);
//        int idColor = itemSetting.getIdColor();
//        if (idColor != -1) parentView.setBackgroundColor(idColor);
        parentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnClickSettings != null)
                    mOnClickSettings.onClickSettings(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public SettingsAdapter setOnClickSettings(OnClickSettings onClickSettings) {
        mOnClickSettings = onClickSettings;
        return this;
    }

    public interface OnClickSettings {
        public void onClickSettings(int pos);
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        LinearLayoutCompat mParentView;
        ImageView mIconSetting;
        TextView mTvDescribe;

        public ViewHolder(View v) {
            super(v);
            initViews(v);
        }

        private void initViews(View view) {
            mParentView = (LinearLayoutCompat) view.findViewById(R.id.ln_settings_item);
            mIconSetting = (ImageView) view.findViewById(R.id.iv_icon_settings);
            mTvDescribe = (TextView) view.findViewById(R.id.tv_describe);
        }
    }
}

