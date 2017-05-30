package uet.kltn.hoavt_58.crazyexface.photo_collage.asynctask;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.pic.libphotocollage.core.util.SystemUtils;

import uet.kltn.hoavt_58.crazyexface.R;

/**
 * Created by vutha on 9/10/2016.
 */
public class SaveToFileAsync extends AsyncTask<Void, Void, Void> {
    private String mSavePath = "";
    private String mSaveName = "";
    private Bitmap mSaveBmp = null;
    private ProgressDialog mProgressDialog = null;
    private Context mContext = null;
    private OnSavedFinish mOnSavedFinish;

    public SaveToFileAsync(Context context, String savePath, String saveName, Bitmap saveBmp) {
        mContext = context;
        mSavePath = savePath;
        mSaveName = saveName;
        mSaveBmp = saveBmp;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mProgressDialog = new ProgressDialog(mContext, R.style.CircularProgress);
        mProgressDialog.setMessage(mContext.getResources().getString(R.string.saving));
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }

    @Override
    protected Void doInBackground(Void... params) {
        SystemUtils.saveToImage(mContext, mSavePath, mSaveName, mSaveBmp, Bitmap.CompressFormat.JPEG);
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
        if (mOnSavedFinish != null) {
            mSaveBmp.recycle();
            mSaveBmp = null;
            mOnSavedFinish.onSavedDone();
        }
    }

    public SaveToFileAsync setOnSavedFinish(OnSavedFinish onSavedFinish) {
        mOnSavedFinish = onSavedFinish;
        return this;
    }

    public interface OnSavedFinish {
        public void onSavedDone();
    }
}


