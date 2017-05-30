package uet.kltn.hoavt_58.crazyexface.photo_collage.adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pic.libphotocollage.core.util.Flog;

import uet.kltn.hoavt_58.crazyexface.R;

/**
 * Created by vutha on 10/19/2016.
 */
public class StickerEditorAdapter extends RecyclerView.Adapter<StickerEditorAdapter.ViewHolder> {

    private int[] MENU_TITLES_RES = new int[]{R.string.sticker_opacity,
            R.string.sticker_rotate_5, R.string.sticker_mirror_5,
            R.string.sticker_up, R.string.sticker_down,
            R.string.sticker_left, R.string.sticker_right,
            R.string.zoom_in, R.string.zoom_out};
    private int[] MENU_ICON_RES = new int[]{R.drawable.ic_opacity,
            R.drawable.ic_rotate_right_5, R.drawable.ic_rotate_left_5,
            R.drawable.ic_up, R.drawable.ic_down,
            R.drawable.ic_left, R.drawable.ic_right,
            R.drawable.ic_zoom_in, R.drawable.ic_zoom_out};

    private Context mContext = null;

    private OnStickerEditorListener listener = null;
    private int mCurIndex = -1;


    public StickerEditorAdapter(Context context) {
        mContext = context;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_footer_menu, null, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.menuTitle.setText(MENU_TITLES_RES[position]);

        holder.menuImg.setImageResource(MENU_ICON_RES[position]);

        holder.btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onStickerEditor(holder.btnMenu, position, mCurIndex);
                } else {
                    Flog.i(StickerEditorAdapter.class.getSimpleName() + " listenner null");
                }
            }
        });

    }

    public StickerEditorAdapter setOnStickerEditorListener(OnStickerEditorListener listener) {
        this.listener = listener;
        return this;
    }

    @Override
    public int getItemCount() {
        return MENU_TITLES_RES.length;
    }

    public void setCurIndex(int curIndex) {
        mCurIndex = curIndex;
    }

    public interface OnStickerEditorListener {
        public void onStickerEditor(View view, int position, int curIndex);
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

