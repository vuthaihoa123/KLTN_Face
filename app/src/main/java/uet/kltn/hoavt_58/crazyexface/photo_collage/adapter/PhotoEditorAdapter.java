package uet.kltn.hoavt_58.crazyexface.photo_collage.adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pic.libphotocollage.core.util.Flog;

import uet.kltn.hoavt_58.crazyexface.R;

/**
 * Created by vutha on 9/14/2016.
 */
public class PhotoEditorAdapter extends RecyclerView.Adapter<PhotoEditorAdapter.ViewHolder> {

    private static final int[] MENU_TITLES_RES = new int[]{R.string.editor_replace,
            R.string.editor_rotate_90, R.string.editor_mirror_90, R.string.editor_correct_90, R.string.editor_delete,
            R.string.sticker_up, R.string.sticker_down,
            R.string.sticker_left, R.string.sticker_right,
            R.string.zoom_in, R.string.zoom_out};      // , R.string.editor_edit : Hòa comment

    private static final int[] MENU_ICON_RES = new int[]{R.drawable.ic_replate, R.drawable.ic_rotate_right,
            R.drawable.ic_rotate_left, R.drawable.ic_rotate_90, R.drawable.ic_delete,
            R.drawable.ic_up, R.drawable.ic_down,
            R.drawable.ic_left, R.drawable.ic_right,
            R.drawable.ic_zoom_in, R.drawable.ic_zoom_out};     // , R.drawable.ic_mode_edit_white_24px1:  Hòa comment

    private Context mContext = null;

    private OnPhotoEditorListener listener = null;
    private int mCurIndex = -1;

    public PhotoEditorAdapter(Context context) {
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

        Log.d("hoacode", "1");
        holder.btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("hoacode", "listener="+listener);
                if (listener != null) {
                    Log.d("hoacode", "mCurIndex="+mCurIndex);
                    listener.onPhotoEditor(holder.btnMenu, position, mCurIndex);
                } else {
                    Flog.i(PhotoEditorAdapter.class.getSimpleName() + " listenner null");
                }
            }
        });

    }

    public PhotoEditorAdapter setOnPhotoEditorListener(OnPhotoEditorListener listener) {
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

    public interface OnPhotoEditorListener {
        public void onPhotoEditor(View view, int position, int curIndex);
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

