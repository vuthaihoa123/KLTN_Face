package uet.kltn.hoavt_58.crazyexface.photo_collage.adapter.menu;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import uet.kltn.hoavt_58.crazyexface.R;
import uet.kltn.hoavt_58.crazyexface.photo_collage.adapter.listener.OnMenuBackgroundListener;

/**
 * Created by Adm on 8/9/2016.
 */
public class ColorBackgroundAdapter extends RecyclerView.Adapter<ColorBackgroundAdapter.ViewHolder> {

    private Context context = null;
    private int[] mList = null;

    private OnMenuBackgroundListener listener = null;

    public ColorBackgroundAdapter(Context context, int[] list) {
        this.context = context;
        this.mList = list;
    }

    public ColorBackgroundAdapter setOnMenuBackgroundListener(OnMenuBackgroundListener listener) {
        this.listener = listener;
        return this;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.menu_item_color_bg, null, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        if (position!=0) holder.btnColor.setBackgroundColor(mList[position]);
        else holder.btnColor.setBackgroundResource(R.drawable.ic_white_frame);

        holder.btnColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onMenuColorBackgroundClickListener(mList[position]);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View btnColor;

        public ViewHolder(View itemView) {
            super(itemView);
            btnColor = itemView.findViewById(R.id.btn_color);
        }
    }
}