package uet.kltn.hoavt_58.crazyexface.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import uet.kltn.hoavt_58.crazyexface.R;
import uet.kltn.hoavt_58.crazyexface.models.ServiceItem;

/**
 * Created by hoavt_58 on 4/9/17.
 */

public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.MyViewHolder> {

    private ArrayList<ServiceItem> arrayList = new ArrayList<>();
    private OnItemClickLitener listener;

    public ServiceAdapter(ArrayList<ServiceItem> arrayList) {
        this.arrayList = arrayList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_service, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        ServiceItem item = this.arrayList.get(position);
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

    public ServiceAdapter setListener(OnItemClickLitener listener) {
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
            ivThumb = (ImageView) view.findViewById(R.id.iv_thumb);
            tvDescribe = (TextView) view.findViewById(R.id.tv_describe);
        }
    }
}
