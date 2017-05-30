package uet.kltn.hoavt_58.crazyexface.photo_collage.adapter.gallery;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.pic.libphotocollage.core.util.SystemUtils;

import java.util.ArrayList;
import java.util.List;

import uet.kltn.hoavt_58.crazyexface.R;
import uet.kltn.hoavt_58.crazyexface.photo_collage.DisplayPhotoActivity;
import uet.kltn.hoavt_58.crazyexface.photo_collage.MyStudioActivity;
import uet.kltn.hoavt_58.crazyexface.photo_collage.model.SavedPhotoModel;
import uet.kltn.hoavt_58.crazyexface.photo_collage.statistic.Statistic;
import uet.kltn.hoavt_58.crazyexface.photo_collage.ui.flip3d.DisplayNextView;
import uet.kltn.hoavt_58.crazyexface.photo_collage.ui.flip3d.Flip3dAnimation;

/**
 * Created by hoavt on 22/11/2016.
 */
public class MyStudioStorageAdapter extends RecyclerView.Adapter<MyStudioStorageAdapter.ViewHolder> {

    private static final String TAG = MyStudioStorageAdapter.class.getSimpleName();
    private Context mContext = null;
    private List<SavedPhotoModel> mSavedImageList = new ArrayList<>();
    private ArrayList<String> mPathList = new ArrayList<>();
    private boolean mIsLongClicked = false;
    private OnApplyRotation mFlippedListener;

    public MyStudioStorageAdapter(Context context, List<SavedPhotoModel> listImageMystudio) {
        mContext = context;
        mSavedImageList = listImageMystudio;
        initPathList();
    }

    public void setSavedImageList(List<SavedPhotoModel> savedImageList) {
        mSavedImageList = savedImageList;
        initPathList();
    }

    private void initPathList() {
        mPathList.clear();
        for (SavedPhotoModel model : mSavedImageList) {
            mPathList.add(model.getData());
        }
    }

    @Override
    public MyStudioStorageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.my_studio_storage_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyStudioStorageAdapter.ViewHolder holder, final int position) {
        final SavedPhotoModel savedPhotoModel = mSavedImageList.get(position);
        String filePath = savedPhotoModel.getData();
        long date = savedPhotoModel.getDate();

        Glide.with(mContext).load(filePath)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .centerCrop()
                .dontAnimate()
                .into(holder.mIvIcon1);

        if (savedPhotoModel.isFirstImage()) {
            holder.mIvIcon2.setVisibility(View.GONE);
            holder.mIvIcon1.setVisibility(View.VISIBLE);
            holder.mIvIcon1.requestFocus();
        } else {
            holder.mIvIcon1.setVisibility(View.GONE);
            holder.mIvIcon2.setVisibility(View.VISIBLE);
            holder.mIvIcon2.requestFocus();
        }

        holder.mIvIcon1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                toggleTextColor(holder, savedPhotoModel.isFirstImage());
                applyRotation(savedPhotoModel, holder.mIvIcon1, holder.mIvIcon2, holder.itemView);
//                applyRotation(savedPhotoModel, holder);
            }
        });

        holder.mIvIcon2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleTextColor(holder, savedPhotoModel.isFirstImage());
                applyRotation(savedPhotoModel, holder.mIvIcon1, holder.mIvIcon2, holder.itemView);
            }
        });

        holder.mTvTime.setText(SystemUtils.getDateFormat(date));
        holder.mTvSize.setText((savedPhotoModel.getSize() / Statistic.VALUE_1KB) + Statistic.UNIT_KB);
        holder.mTvName.setText(savedPhotoModel.getTitle());

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mIsLongClicked = true;
                toggleTextColor(holder, savedPhotoModel.isFirstImage());
                applyRotation(savedPhotoModel, holder.mIvIcon1, holder.mIvIcon2, holder.itemView);
                return mIsLongClicked;
            }
        });

        if (savedPhotoModel.isFirstImage()) {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorSecondary));
            holder.mTvTime.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimaryDark));
            holder.mTvSize.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimaryDark));
            holder.mTvName.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimaryDark));
        } else {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
            holder.mTvTime.setTextColor(ContextCompat.getColor(mContext, R.color.white));
            holder.mTvSize.setTextColor(ContextCompat.getColor(mContext, R.color.white));
            holder.mTvName.setTextColor(ContextCompat.getColor(mContext, R.color.white));
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, DisplayPhotoActivity.class);
                intent.putExtra(Statistic.EXTRA_CURRENT_POSITION_STUDIO, position);
                intent.putExtra(Statistic.EXTRA_PATH_PHOTO_LIST, mPathList);
                ((MyStudioActivity)mContext).startActivityForResult(intent, Statistic.REQUEST_CODE_DISPLAY_PHOTO_ACTIVITY);
            }
        });
    }

    private void toggleTextColor(ViewHolder holder, boolean toogle) {
        if (!toogle) {
            holder.mTvTime.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimaryDark));
            holder.mTvSize.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimaryDark));
            holder.mTvName.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimaryDark));
        }else {
            holder.mTvTime.setTextColor(ContextCompat.getColor(mContext, R.color.white));
            holder.mTvSize.setTextColor(ContextCompat.getColor(mContext, R.color.white));
            holder.mTvName.setTextColor(ContextCompat.getColor(mContext, R.color.white));
        }
    }

    private void applyRotation(SavedPhotoModel savedPhotoModel, ImageView imageView1, ImageView imageView2, View itemView) {
        if (savedPhotoModel.isFirstImage()) {
            applyRotation(savedPhotoModel, imageView1, imageView2, 0, 90);
            itemView.setBackgroundColor(mContext.getResources().getColor(R.color.colorPrimary));
        } else {
            applyRotation(savedPhotoModel, imageView1, imageView2, 0, -90);
            itemView.setBackgroundColor(mContext.getResources().getColor(R.color.colorSecondary));
        }
        savedPhotoModel.setIsFirstImage(!savedPhotoModel.isFirstImage());
        if (mFlippedListener != null)
            mFlippedListener.onFliped();
    }

    private void applyRotation(SavedPhotoModel savedPhotoModel, ViewHolder holder) {
        if (savedPhotoModel.isFirstImage()) {
            applyRotation(savedPhotoModel, holder.mIvIcon1, holder.mIvIcon2, 0, 90);
            holder.itemView.setBackgroundColor(mContext.getResources().getColor(R.color.colorPrimary));
            holder.mTvTime.setTextColor(ContextCompat.getColor(mContext, R.color.white));
            holder.mTvSize.setTextColor(ContextCompat.getColor(mContext, R.color.white));
            holder.mTvName.setTextColor(ContextCompat.getColor(mContext, R.color.white));
        } else {
            applyRotation(savedPhotoModel, holder.mIvIcon1, holder.mIvIcon2, 0, -90);
            holder.itemView.setBackgroundColor(mContext.getResources().getColor(R.color.colorSecondary));
            holder.mTvTime.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimaryDark));
            holder.mTvSize.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimaryDark));
            holder.mTvName.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimaryDark));
        }
        savedPhotoModel.setIsFirstImage(!savedPhotoModel.isFirstImage());
        if (mFlippedListener != null)
            mFlippedListener.onFliped();
    }

    public void refreshFlipped() {
        for (SavedPhotoModel model : mSavedImageList) {
            model.setIsFirstImage(true);
        }
        notifyDataSetChanged();
    }

    public MyStudioStorageAdapter setFlippedListener(OnApplyRotation flippedListener) {
        mFlippedListener = flippedListener;
        return this;
    }

    private void applyRotation(SavedPhotoModel savedPhotoModel, ImageView image1, ImageView image2, float start, float end) {
        // Find the center of image
        final float centerX = image1.getWidth() / 2.0f;
        final float centerY = image1.getHeight() / 2.0f;

        // Create a new 3D rotation with the supplied parameter
        // The animation mInteractListener is used to trigger the next animation
        final Flip3dAnimation rotation =
                new Flip3dAnimation(start, end, centerX, centerY);
        rotation.setDuration(200);
        rotation.setFillAfter(true);
        rotation.setInterpolator(new AccelerateInterpolator());
        rotation.setAnimationListener(new DisplayNextView(savedPhotoModel.isFirstImage(), image1, image2));

        if (savedPhotoModel.isFirstImage()) {
            image1.startAnimation(rotation);
        } else {
            image2.startAnimation(rotation);
        }
    }

    public int getItemSelected() {
        int count = 0;
        for (int i = 0; i < mSavedImageList.size(); i++) {
            if (!mSavedImageList.get(i).isFirstImage()) {
                count++;
            }
        }
        return count;
    }

    @Override
    public int getItemCount() {
        return mSavedImageList.size();
    }

    public interface OnApplyRotation {
        public void onFliped();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView mIvIcon1, mIvIcon2;
        private LinearLayoutCompat mIvDelete, mIvShare;
        private TextView mTvName, mTvTime, mTvSize;
        private CheckBox mCbSelect;

        public ViewHolder(View itemView) {
            super(itemView);
            mIvIcon1 = (ImageView) itemView.findViewById(R.id.iv_icon1);
            mIvIcon2 = (ImageView) itemView.findViewById(R.id.iv_icon2);
            mTvName = (TextView) itemView.findViewById(R.id.tv_name);
            mTvTime = (TextView) itemView.findViewById(R.id.tv_time);
            mTvSize = (TextView) itemView.findViewById(R.id.tv_size);
        }
    }
}
