package uet.kltn.hoavt_58.crazyexface.photo_collage.adapter.gallery;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.io.File;
import java.util.List;

import uet.kltn.hoavt_58.crazyexface.R;
import uet.kltn.hoavt_58.crazyexface.photo_collage.adapter.listener.OnGalleryListener;
import uet.kltn.hoavt_58.crazyexface.photo_collage.fragment.gallery.GalleryContainerFrag;
import uet.kltn.hoavt_58.crazyexface.photo_collage.model.PhotoModel;

/**
 * Created by Adm on 8/10/2016.
 */
public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {

    private static final String TAG = GalleryAdapter.class.getSimpleName();
    private Context context = null;
    private List<PhotoModel> mList;

    private OnGalleryListener listener = null;
    private int itemSize;

    public GalleryAdapter(Context context, List<PhotoModel> list) {
        this.context = context;
        this.mList = list;
        init();
    }

    private void init() {
        itemSize = context.getResources().getDisplayMetrics().widthPixels / GalleryContainerFrag.NUMBER_GRID_CNT;
    }


    public GalleryAdapter setOnGalleryListener(OnGalleryListener listener) {
        this.listener = listener;
        return this;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.gallery_item_photo_list, null, false);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(itemSize, itemSize);
        view.setLayoutParams(params);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final PhotoModel photoModel = mList.get(position);
        if (photoModel==null)
            return;

//        ImageLoader imageLoader = ImageLoader.getInstance();
//        DisplayImageOptions options = new DisplayImageOptions.Builder()
////                .cacheInMemory(true)
////                .cacheOnDisc(false).resetViewBeforeLoading(true)
//                .showImageForEmptyUri(R.drawable.ic_no_image)
//                .showImageOnFail(R.drawable.ic_no_image)
//                .showImageOnLoading(R.drawable.ic_no_image)
//                .displayer(new FadeInBitmapDisplayer(200))
//                .build();
//
//        Log.d(TAG, "path="+photoModel.imgPath);
//        imageLoader.displayImage("file://"+photoModel.imgPath, holder.imgView, options);

        Glide.with(context).load(new File(photoModel.imgPath))
                .listener(new RequestListener<File, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, File model, Target<GlideDrawable> target, boolean isFirstResource) {
                        // todo log exception
                        photoModel.hasContent = false;
                        // important to return false so the error placeholder can be placed
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, File model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        photoModel.hasContent = true;
                        return false;
                    }
                })
                .error(R.drawable.ic_no_image)
//                .placeholder(R.drawable.ic_no_image)
                .centerCrop()
                .dontAnimate()
//                .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))   // refesh lastest version of image
//                .diskCacheStrategy(DiskCacheStrategy.NONE)
//                .skipMemoryCache(true)
                .into(holder.imgView);

//        if (!photoModel.hasContent()) {
//            Glide.with(context).load(R.drawable.ic_no_image).into(holder.imgView);
//        }
//        if (photoModel != null && new File(photoModel.imgPath).length() < 1024) {
//            Glide.with(context).load(R.drawable.ic_no_image).into(holder.imgView);
//        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    PhotoModel delegate = photoModel.copy();
                    if (delegate==null)
                        return;
//                    delegate.drawable = holder.imgView.getDrawable();
//                    photoModel.drawable.mutate();
                    Log.d(TAG, "path="+delegate.imgPath+"_check="+new File(delegate.imgPath).exists()+"_len="+new File(delegate.imgPath).length());
//                    if (delegate != null && new File(delegate.imgPath).length() >= 1024)
//                        listener.onPhotoPickListener(delegate, holder.itemView);
//                    else {
//                        showNoImageDialog();
////                        SystemUtils.show(context, SystemUtils.getString(context, R.string.inform_small_image));
//                    }
                    if (delegate.hasContent) listener.onPhotoPickListener(delegate, holder.itemView);
                     else showNoImageDialog();
                }
            }
        });
    }

    private void showNoImageDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AppCompatAlertDialogStyle);
        builder.setMessage(context.getString(R.string.inform_small_image));
        builder.setPositiveButton(context.getString(R.string.ok), null);
        builder.setCancelable(false);
        builder.show();
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgView;

        public ViewHolder(View itemView) {
            super(itemView);
            imgView = (ImageView) itemView.findViewById(R.id.img_picked);
        }
    }
}