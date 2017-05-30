package uet.kltn.hoavt_58.crazyexface.tasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.contract.Face;

import java.io.InputStream;

import uet.kltn.hoavt_58.crazyexface.R;
import uet.kltn.hoavt_58.crazyexface.helpers.SampleApp;

/**
 * Created by hoavt_58 on 4/13/17.
 */

// Background task of face detection.
public class DetectionAsync extends AsyncTask<InputStream, String, Face[]> {
    private static final String TAG = DetectionAsync.class.getSimpleName();
    private int mId = -1;
    private boolean mSucceed = true;

    // Progress dialog popped up when communicating with server.
    private ProgressDialog mProgressDialog;
    private Context mContext;
    private OnDetectionListener listener;
    private Bitmap mBitmap;

    public DetectionAsync(Context context, int id) {
        mContext = context;
        mId = id;
    }

    @Override
    protected Face[] doInBackground(InputStream... params) {
        // Get an instance of face service client to detect faces in image.
        FaceServiceClient faceServiceClient = SampleApp.getFaceServiceClient();
        try {
            publishProgress("Detecting...");

            // Start detection.
            return faceServiceClient.detect(
                    params[0],  /* Input stream of image to detect */
                    true,       /* Whether to return face ID */
                    true,       /* Whether to return face landmarks */
                        /* Which face attributes to analyze, currently we support:
                           age,gender,headPose,smile,facialHair */
                    new FaceServiceClient.FaceAttributeType[]{
                            FaceServiceClient.FaceAttributeType.Age,
                            FaceServiceClient.FaceAttributeType.Gender,
                            FaceServiceClient.FaceAttributeType.Smile,
                            FaceServiceClient.FaceAttributeType.Glasses,
                            FaceServiceClient.FaceAttributeType.FacialHair,
                            FaceServiceClient.FaceAttributeType.Emotion,
                            FaceServiceClient.FaceAttributeType.HeadPose
                    });
        } catch (Exception e) {
            mSucceed = false;
            publishProgress(e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPreExecute() {
        mProgressDialog = ProgressDialog.show(mContext, null, mContext.getString(R.string.progress_dialog_title), true);
        mProgressDialog.setCancelable(false);
//            mProgressDialog.show();
//        Flog.d(TAG, "Request: Detecting in image " + mImageUri);
    }

    @Override
    protected void onProgressUpdate(String... progress) {
        mProgressDialog.setMessage(progress[0]);
//        setInfo(progress[0]);
    }

    @Override
    protected void onPostExecute(Face[] result) {
        if (mProgressDialog != null && mProgressDialog.isShowing())
            mProgressDialog.dismiss();
//        if (mSucceed) {
//            Flog.d(TAG, "Response: Success. Detected " + (result == null ? 0 : result.length)
//                    + " face(s) in " + mImageUri);
//        }

        if (listener != null) {
            listener.onDetectDone(mId, result, mSucceed, mBitmap);
        }
    }

    public DetectionAsync setListener(OnDetectionListener listener) {
        this.listener = listener;
        return this;
    }

    public void setOriginBitmap(Bitmap bitmap) {
        mBitmap = bitmap;
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }

    public interface OnDetectionListener {
        void onDetectDone(int id, Face[] faces, boolean succeed, Bitmap bitmap);
    }
}
