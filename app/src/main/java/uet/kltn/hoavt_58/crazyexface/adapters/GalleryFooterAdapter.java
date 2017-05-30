package uet.kltn.hoavt_58.crazyexface.adapters;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.BitmapImageViewTarget;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import uet.kltn.hoavt_58.crazyexface.R;
import uet.kltn.hoavt_58.crazyexface.activities.GalleryActivity;
import uet.kltn.hoavt_58.crazyexface.helpers.ConstValue;
import uet.kltn.hoavt_58.crazyexface.helpers.Flog;
import uet.kltn.hoavt_58.crazyexface.models.PhotoModel;

/**
 * Created by Adm on 8/10/2016.
 */
public class GalleryFooterAdapter extends RecyclerView.Adapter<GalleryFooterAdapter.ViewHolder> {

    private final String TAG = GalleryFooterAdapter.class.getSimpleName();
    private final int itemCount;
    private int curIndex = 0;
    private Drawable unselectedDrawable = new ColorDrawable(Color.GRAY);
    private Activity context = null;
    private List<PhotoModel> mList = new ArrayList<>();

    public GalleryFooterAdapter(Activity activity) {
//        Flog.d("GalleryFooterAdapter");
        this.context = activity;
        itemCount = activity.getIntent().getIntExtra(GalleryActivity.EXTRA_NUM_PICKED_FRAME_KEY, 5);
        curIndex = activity.getIntent().getIntExtra(ConstValue.EXTRA_CURRENT_SELECTED_RECT, 0);
//        ArrayList<PhotoModel> models = activity.getIntent().getParcelableArrayListExtra(GalleryActivity.EXTRA_PHOTO_MODEL_LIST);

//        Flog.d("curIndex = "+curIndex);
//        Flog.d("itemCount=" + itemCount + "_models=" + models.size());
//        Flog.d("isEmpty: "+models.isEmpty());
//        if (models == null) return;
//        if (models.isEmpty()) {
            for (int i = 0; i < itemCount; i++) {
                //create empty model
                mList.add(null);
            }
//        } else {
//            mList.addAll(models);
//        }

//        Flog.d("mList size: " + mList.size());
    }

    public int getCurIndex() {
        return curIndex;
    }

    public String[] getOutput() {
        Flog.d(TAG, "size="+mList.size());
        String[] output = new String[mList.size()];
        for (int i = 0; i < output.length; i++) {
            if (mList.get(i) == null) {
                output[i] = null;
            } else {
                output[i] = mList.get(i).imgPath;
                Flog.d(TAG, i+"="+output[i]);
            }
        }

        return output;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.gallery_item_photo_picked, null, false);
        return new ViewHolder(view);
    }

    public void pushPhoto(PhotoModel model) {
        if (mList.isEmpty()) return;
        mList.set(curIndex, model);
        notifyItemChanged(curIndex);

        if (isFull()) return;

        do {
            curIndex++;
            if (curIndex >= mList.size()) curIndex = 0;
        } while (mList.get(curIndex) != null);
    }

    private boolean isFull() {
        for (PhotoModel model : mList) {
            if (model == null) return false;
        }
        return true;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        if (mList.isEmpty()) return;
        if (mList.get(position) != null) {
            final ImageView imageView = holder.image;

//            Bitmap myBitmap = BitmapFactory.decodeFile(mList.get(position).imgPath);
//            imageView.setImageBitmap(myBitmap);

            Glide.with(context).load(new File(mList.get(position).imgPath))
                    .asBitmap()
//                    .override(100, 100)
                    .error(R.drawable.ic_no_image)
                    .centerCrop()
//                    .into(imageView);
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(new BitmapImageViewTarget(imageView) {
                        @Override
                        protected void setResource(Bitmap resource) {
                            RoundedBitmapDrawable circularBitmapDrawable =
                                    RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                            circularBitmapDrawable.setCornerRadius(10f);
                            imageView.setImageDrawable(circularBitmapDrawable);
                        }
                    });
        } else {
            holder.image.setImageResource(R.drawable.ic_gray);
        }

        if (curIndex == position) {
            holder.bg.setBackgroundResource(R.drawable.boder);
        } else {
            holder.bg.setBackgroundColor(Color.TRANSPARENT);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                curIndex = position;
                notifyDataSetChanged();
            }
        });
        holder.clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("eee", "click close at pos = " + position);
                if (mList.get(position) != null) {
                    mList.set(position, null);
                }
                curIndex = position;
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemCount;
    }

    public ArrayList<PhotoModel> getChoosedModelList() {
        Flog.d("size picked list: " + mList.size());
        return (ArrayList<PhotoModel>) mList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public FrameLayout bg;
        public ImageView image;
        public View clearBtn;

        public ViewHolder(View itemView) {
            super(itemView);
            bg = (FrameLayout) itemView.findViewById(R.id.fl_bg_preview);
            image = (ImageView) itemView.findViewById(R.id.img_picked);
            clearBtn = itemView.findViewById(R.id.clear_btn);
        }
    }
}