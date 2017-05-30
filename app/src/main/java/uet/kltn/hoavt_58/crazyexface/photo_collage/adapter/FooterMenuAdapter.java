package uet.kltn.hoavt_58.crazyexface.photo_collage.adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import uet.kltn.hoavt_58.crazyexface.R;
import uet.kltn.hoavt_58.crazyexface.photo_collage.adapter.listener.OnCollageMenuListener;
import uet.kltn.hoavt_58.crazyexface.photo_collage.statistic.Statistic;

/**
 * Created by Adm on 8/3/2016.
 */
public class FooterMenuAdapter extends RecyclerView.Adapter<FooterMenuAdapter.ViewHolder> {

    private int[] MENU_TITLES_RES;
    private int[] MENU_ICON_RES;

    private Context mContext = null;

    private int menuType = Statistic.MENU_COLLAGE_TYPE_LAYOUT;
    private OnCollageMenuListener listener = null;

    public FooterMenuAdapter(Context context, int[] titles, int[] icons) {
        mContext = context;
        MENU_TITLES_RES = titles;
        MENU_ICON_RES = icons;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_footer_menu, null, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        holder.menuTitle.setText(MENU_TITLES_RES[position]);
        holder.menuImg.setImageResource(MENU_ICON_RES[position]);

        holder.btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onCollageMenuClickListener(menuType + position);
                }
            }
        });

    }

    public FooterMenuAdapter setOnCollageMenuListener(OnCollageMenuListener listener) {
        this.listener = listener;
        return this;
    }

    @Override
    public int getItemCount() {
        return MENU_TITLES_RES.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView menuImg;
        TextView menuTitle;
        LinearLayoutCompat btnMenu;

        public ViewHolder(View itemView) {
            super(itemView);
            menuImg = (ImageView) itemView.findViewById(R.id.menu_img);
            menuTitle = (TextView) itemView.findViewById(R.id.menu_title);

            btnMenu = (LinearLayoutCompat) itemView.findViewById(R.id.btn_menu);
        }
    }
}
