package uet.kltn.hoavt_58.crazyexface.photo_collage.adapter.text;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import java.util.List;

import uet.kltn.hoavt_58.crazyexface.R;
import uet.kltn.hoavt_58.crazyexface.photo_collage.model.FontModel;

/**
 * Created by hoavt on 16/08/2016.
 */
public class TextFontAdapter extends RecyclerView.Adapter<TextFontAdapter.ViewHolder> {

    private static final String TAG = TextFontAdapter.class.getSimpleName();
    private int numOfCustomFonts = 0;
    private Context context = null;
    private List<FontModel> mList;
    private Typeface mTypeFace;

    public TextFontAdapter(Context context, List<FontModel> list, int numOfCustomFonts) {
        this.context = context;
        this.mList = list;
        this.numOfCustomFonts = numOfCustomFonts;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_text_font, null, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final FontModel fontModel = mList.get(position);
        holder.fontPreview.setText(fontModel.fontPreview);
        if (position == 0) {
            mTypeFace = Typeface.DEFAULT;
        } else if (position < (mList.size() - numOfCustomFonts)) {
            // system font
            mTypeFace = Typeface.createFromFile(fontModel.fontPath);
        } else {
            // asset fonts
            mTypeFace = Typeface.createFromAsset(context.getAssets(), fontModel.fontPath);
        }
        holder.fontPreview.setTypeface(mTypeFace);


        holder.btnFont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (context instanceof OnTextFontListener) {
                    Typeface tmpTypeFace;
                    if (position == 0) {
                        tmpTypeFace = Typeface.DEFAULT;
                    } else if (position < (mList.size() - numOfCustomFonts)) {
                        // system font
                        tmpTypeFace = Typeface.createFromFile(fontModel.fontPath);
                    } else {
                        // asset fonts
                        tmpTypeFace = Typeface.createFromAsset(context.getAssets(), fontModel.fontPath);
                    }

                    ((OnTextFontListener) context).onFontChanged(tmpTypeFace);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public interface OnTextFontListener {
        public void onFontChanged(Typeface typeface);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView fontPreview;
        LinearLayoutCompat btnFont;

        public ViewHolder(View itemView) {
            super(itemView);
            btnFont = (LinearLayoutCompat) itemView.findViewById(R.id.btn_font);
            fontPreview = (TextView) itemView.findViewById(R.id.preview_font);
        }
    }
}