package uet.kltn.hoavt_58.crazyexface.photo_collage.asynctask;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.pic.libphotocollage.core.util.Flog;

import java.io.InputStream;

/**
 * Created by vutha on 9/21/2016.
 */
public class BackgroundDecoder extends AsyncTask<InputStream, Void, Bitmap> {
    //    private ProgressDialog mProgressDialog = null;
    private Context mContext = null;
    private OnBackgroundLoaded mOnDecodedFinish = null;

    public BackgroundDecoder(Context context) {
        mContext = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
//        mProgressDialog = new ProgressDialog(mContext);
//        mProgressDialog.setMessage(mContext.getResources().getString(R.string.processing));
//        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//        mProgressDialog.setIndeterminate(true);
//        mProgressDialog.show();
    }

    @Override
    protected Bitmap doInBackground(InputStream... params) {
        return BitmapFactory.decodeStream(params[0]);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);

        if (bitmap == null) {
            Flog.i("bg bitmap == null");
            return;
        }

        if (mOnDecodedFinish != null) {
//            Flog.i("mIndex: " + mIndex);
            mOnDecodedFinish.onDecodedDone(bitmap);
        } else {
            Flog.i("mOnBackgroundLoaded is null");
        }

        if (!isCancelled()) {
            cancel(true);
        }
    }

    public BackgroundDecoder setOnBackgroundLoaded(OnBackgroundLoaded ondecodedFinish) {
        mOnDecodedFinish = ondecodedFinish;
        return this;
    }

    public interface OnBackgroundLoaded {
        public void onDecodedDone(Bitmap bitmapDecoded);
    }
}

