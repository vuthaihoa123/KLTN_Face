package uet.kltn.hoavt_58.crazyexface.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import uet.kltn.hoavt_58.crazyexface.R;
import uet.kltn.hoavt_58.crazyexface.models.FrameItem;
import uet.kltn.hoavt_58.crazyexface.models.FrameItem;

/**
 * Created by hoavt_58 on 4/9/17.
 */

public class FrameAdapter extends RecyclerView.Adapter<FrameAdapter.MyViewHolder> {

    private ArrayList<FrameItem> arrayList = new ArrayList<>();
    private OnItemClickLitener listener;

    public FrameAdapter(ArrayList<FrameItem> arrayList) {
        this.arrayList = arrayList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_frame, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        FrameItem item = this.arrayList.get(position);
        holder.ivThumb.setImageResource(item.getIdResThumb());
        holder.tvDescribe.setText(item.getDescribe());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onItemClicked(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public FrameAdapter setListener(OnItemClickLitener listener) {
        this.listener = listener;
        return this;
    }

    public interface OnItemClickLitener {
        public void onItemClicked(int pos);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView ivThumb;
        public TextView tvDescribe;

        public MyViewHolder(View view) {
            super(view);
            ivThumb = (CircleImageView) view.findViewById(R.id.iv_thumb);
            tvDescribe = (TextView) view.findViewById(R.id.tv_describe);
        }
    }
}
