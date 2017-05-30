package uet.kltn.hoavt_58.crazyexface.photo_collage.adapter.gallery;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.pic.libphotocollage.core.util.SystemUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import uet.kltn.hoavt_58.crazyexface.R;
import uet.kltn.hoavt_58.crazyexface.photo_collage.model.SavedPhotoModel;
import uet.kltn.hoavt_58.crazyexface.photo_collage.statistic.Statistic;
import uet.kltn.hoavt_58.crazyexface.photo_collage.utils.SettingsUtil;

/**
 * Created by Adm on 11/9/2016.
 */
public class MyStudioAdapter extends RecyclerView.Adapter<MyStudioAdapter.ViewHolder> {

    private Context mContext = null;
    private List<SavedPhotoModel> mSavedImageList = new ArrayList<>();
    private String mFilePath;
    private boolean mIsLongClicked = false;
    private OnShowItemListener mShowListener;
    private OnHiddenItemListener mHiddenListener;
    private boolean onBind;
    public MyStudioAdapter(Context context, List<SavedPhotoModel> listImageMystudio) {
        mContext = context;
        mSavedImageList = listImageMystudio;
    }

    public void setIsLongClicked(boolean isLongClicked) {
        this.mIsLongClicked = isLongClicked;
    }

    @Override
    public MyStudioAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.my_studio_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyStudioAdapter.ViewHolder holder, final int position) {
        final SavedPhotoModel savedPhotoModel = mSavedImageList.get(position);
        mFilePath = savedPhotoModel.getData();
        final File file = new File(mFilePath);
        long date = savedPhotoModel.getDate();

        Glide.with(mContext).load(mFilePath).dontAnimate().into(holder.mIvIcon);
        holder.mTvTime.setText(SystemUtils.getDateFormat(date));
        holder.mTvSize.setText((savedPhotoModel.getSize() / Statistic.VALUE_1KB) + Statistic.UNIT_KB);
        holder.mTvName.setText(savedPhotoModel.getTitle());
        holder.mTvName.setSelected(true);
        visibleCheckbox(mIsLongClicked, holder.mCbSelect, holder.mIvDelete, holder.mIvShare);

        holder.mIvShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SystemUtils.initSetAsOrShareViaLayout(mContext,
                        SystemUtils.makeShareViaIntent(Uri.fromFile(file)), true);
            }
        });
        holder.mIvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogConfirmDel(file, position);
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mIsLongClicked = true;
                holder.mCbSelect.setChecked(true);
                notifyDataSetChanged();
                if (mShowListener != null)
                    mShowListener.onShowMenuEvent();
                return mIsLongClicked;
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsLongClicked) {
                    // select checkbox:
                    holder.mCbSelect.setChecked(!holder.mCbSelect.isChecked());
                } else {
                    // view image:
                    SystemUtils.showFileGallery(mContext, file);
                }
            }
        });
        onBind = true;
        holder.mCbSelect.setChecked(savedPhotoModel.isCheck());
        onBind = false;
        holder.mCbSelect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    savedPhotoModel.setCheck(true);
                } else {
                    savedPhotoModel.setCheck(false);
                    if (noItemSelected()) {
                        mIsLongClicked = false;
                        if (mHiddenListener != null)
                            mHiddenListener.onHideMenuEvent();
                        if (!onBind) {
                            notifyDataSetChanged();
                        }
                    }
                }
            }
        });
    }

    public boolean noItemSelected() {
        for (SavedPhotoModel model : mSavedImageList) {
            if (model.isCheck()) {
                return false;
            }
        }
        return true;
    }

    public boolean allItemSelected() {
        for (SavedPhotoModel model : mSavedImageList) {
            if (!model.isCheck()) {
                return false;
            }
        }
        return true;
    }

    private void visibleCheckbox(boolean visible, View... view) {
        View checkBox = view[0];
        View ivDel = view[1];
        View ivShare = view[2];

        if (visible) {
            checkBox.setVisibility(View.VISIBLE);
            ivDel.setVisibility(View.GONE);
            ivShare.setVisibility(View.GONE);
        } else {
            checkBox.setVisibility(View.GONE);
            ivDel.setVisibility(View.VISIBLE);
            ivShare.setVisibility(View.VISIBLE);
        }
    }

    private void showDialogConfirmDel(final File file, final int position) {
        Log.d("2222", "1 showDialogConfirmDel: file="+file.getAbsolutePath()+"_pos="+position);
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.AppCompatAlertDialogStyle);
        builder.setTitle(mContext.getString(R.string.dialog_title));
        builder.setMessage(mContext.getString(R.string.dialog_message));

        String positiveText = mContext.getString(android.R.string.ok);
        builder.setPositiveButton(positiveText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // positive button logic
                        if (!file.exists()) {
                            return;
                        }
                        if (file.delete()) {
                            SettingsUtil.delImage(mContext.getContentResolver(), file.getAbsolutePath());
                            SystemUtils.show(mContext, SystemUtils.getString(mContext, R.string.delete_photo_success));
                        } else {
                            SystemUtils.show(mContext, SystemUtils.getString(mContext, R.string.delete_photo_failed));
                        }

                        // Scan MediaStore again:
                        try {

                            mContext.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE));

                            mSavedImageList.remove(position);
                            mIsLongClicked = false;
                            if (mHiddenListener != null)
                                mHiddenListener.onHideMenuEvent();
                            notifyDataSetChanged();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

        String negativeText = mContext.getString(android.R.string.cancel);
        builder.setNegativeButton(negativeText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // negative button logic
                        dialog.dismiss();
                    }
                });

        AlertDialog dialog = builder.create();
        // display dialog
        dialog.show();
    }

    @Override
    public int getItemCount() {
        return mSavedImageList.size();
    }

    public void setSelectAll(boolean visible) {
        for (SavedPhotoModel model : mSavedImageList) {
            model.setCheck(visible);
        }
        notifyDataSetChanged();
    }

    public void multiDelete() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.AppCompatAlertDialogStyle);
        builder.setTitle(mContext.getString(R.string.dialog_title));
        builder.setMessage(mContext.getString(R.string.dialog_message));
        String positiveText = mContext.getString(android.R.string.ok);
        builder.setPositiveButton(positiveText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // positive button logic
                        int countSuccess = 0;
                        int countFailure = 0;
                        for (int i = mSavedImageList.size() - 1; i >= 0; i--) {
                            SavedPhotoModel savedPhotoModel = mSavedImageList.get(i);
                            if (savedPhotoModel.isCheck()) {
                                File file = new File(savedPhotoModel.getData());
                                if (!file.exists()) {
                                    Log.d("hoa", "file not exits");
                                    continue;
                                }
                                if (file.delete()) {
                                    countSuccess++;
                                } else {
                                    countFailure++;
                                }

                                // Scan MediaStore again:
                                try {

                                    mContext.sendBroadcast(new Intent(
                                            Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
                                    mSavedImageList.remove(i);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        SystemUtils.show(mContext,
                                (countSuccess > 0) ?
                                        (mContext.getResources().getQuantityString(R.plurals.numOfDeletedPhotos, countSuccess, countSuccess)) :
                                        "" + ((countFailure > 0) ? (mContext.getResources().getQuantityString(R.plurals.numOfDeletedPhotos, countFailure))  : ""));
                        mIsLongClicked = false;
                        if (mHiddenListener != null)
                            mHiddenListener.onHideMenuEvent();
                        notifyDataSetChanged();
                    }
                });

        String negativeText = mContext.getString(android.R.string.cancel);
        builder.setNegativeButton(negativeText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // negative button logic
                        dialog.dismiss();
                    }
                });

        AlertDialog dialog = builder.create();
        // display dialog
        dialog.show();
    }

    public MyStudioAdapter setShowMenuListener(OnShowItemListener listener) {
        mShowListener = listener;
        return this;
    }

    public MyStudioAdapter setHideMenuListener(OnHiddenItemListener listener) {
        mHiddenListener = listener;
        return this;
    }

    public interface OnShowItemListener {
        void onShowMenuEvent();
    }

    public interface OnHiddenItemListener {
        void onHideMenuEvent();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView mIvIcon;
        private LinearLayoutCompat mIvDelete, mIvShare;
        private TextView mTvName, mTvTime, mTvSize;
        private CheckBox mCbSelect;

        public ViewHolder(View itemView) {
            super(itemView);
            mIvIcon = (ImageView) itemView.findViewById(R.id.iv_icon);
            mTvName = (TextView) itemView.findViewById(R.id.tv_name);
            mTvTime = (TextView) itemView.findViewById(R.id.tv_time);
            mTvSize = (TextView) itemView.findViewById(R.id.tv_size);
            mIvDelete = (LinearLayoutCompat) itemView.findViewById(R.id.iv_delete);
            mIvShare = (LinearLayoutCompat) itemView.findViewById(R.id.iv_share);
            mCbSelect = (CheckBox) itemView.findViewById(R.id.cb_select);
        }
    }
}
