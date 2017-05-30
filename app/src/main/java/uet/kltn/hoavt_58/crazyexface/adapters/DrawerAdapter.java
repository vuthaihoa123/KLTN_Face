package uet.kltn.hoavt_58.crazyexface.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.File;
import java.util.List;

import uet.kltn.hoavt_58.crazyexface.R;
import uet.kltn.hoavt_58.crazyexface.models.DrawerItem;

public class DrawerAdapter extends RecyclerView.Adapter<DrawerAdapter.DrawerViewHolder> {
    private List<DrawerItem> drawerMenuList;
    private OnNavigationItemSelectedListener listener;

    public DrawerAdapter(List<DrawerItem> drawerMenuList) {
        this.drawerMenuList = drawerMenuList;
    }

    public void setOnItemSelectedListener(OnNavigationItemSelectedListener listener) {
        this.listener = listener;
    }

    @Override
    public DrawerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.drawer_item_layout, parent, false);
        return new DrawerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DrawerViewHolder holder, final int position) {
        holder.title.setText(drawerMenuList.get(position).getTitle());
        String iconPath = drawerMenuList.get(position).iconPath;
        if (iconPath != null) {
            Glide.with(holder.itemView.getContext()).load(new File(iconPath))
                    .error(R.drawable.ic_no_image)
                    .centerCrop()
                    .dontAnimate()
                    .into(holder.icon);
        } else {
            holder.icon.setImageResource(R.mipmap.ic_launcher);
        }
        holder.number.setText("(" + drawerMenuList.get(position).total + ")");
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onNavigationItemSelected(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return drawerMenuList.size();
    }

    public interface OnNavigationItemSelectedListener {
        void onNavigationItemSelected(int position);
    }

    class DrawerViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView icon;
        TextView number;

        public DrawerViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            icon = (ImageView) itemView.findViewById(R.id.icon);
            number = (TextView) itemView.findViewById(R.id.number);
        }
    }
}