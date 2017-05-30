package uet.kltn.hoavt_58.crazyexface.tasks;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.contract.Face;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import uet.kltn.hoavt_58.crazyexface.R;
import uet.kltn.hoavt_58.crazyexface.activities.DetectionActivity;
import uet.kltn.hoavt_58.crazyexface.helpers.Flog;
import uet.kltn.hoavt_58.crazyexface.helpers.ImageHelper;
import uet.kltn.hoavt_58.crazyexface.helpers.SampleApp;
import uet.kltn.hoavt_58.crazyexface.helpers.Statistic;
import uet.kltn.hoavt_58.crazyexface.models.MicrosoftFace;

/**
 * Created by vutha on 4/14/2017.
 */

public class DecodeTemplates extends AsyncTask<Integer, String, MicrosoftFace> {
    private static final String TAG = DecodeTemplates.class.getSimpleName();
    private final Context mContext;
    private boolean mSucceed = true;
    // Progress dialog popped up when communicating with server.
    private ProgressDialog mProgressDialog;

    public DecodeTemplates(Context context) {
        mContext = context;
    }

    @Override
    protected MicrosoftFace doInBackground(Integer... params) {

        // Get an instance of face service client to detect faces in image.
        FaceServiceClient faceServiceClient = SampleApp.getFaceServiceClient();

        publishProgress("Loading...");

//        Statistic.arrFaces.clear();
//        for (int i = 0; i < params[0]; i++) {
        Bitmap bitmap = ImageHelper.loadSizeLimitedBitmapFromAssets(mContext, "templates/ic_china_" + params[0] + ".jpg");
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(output.toByteArray());
        Face face = null;
        try {
            face = faceServiceClient.detect(
                    inputStream,  /* Input stream of image to detect */
                    false,       /* Whether to return face ID */
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
                    })[0];
        } catch (Exception e) {
            e.printStackTrace();
//            continue;
        }
//        Statistic.arrFaces.add(new MicrosoftFace(face, bitmap));
//        }
        return new MicrosoftFace(face, bitmap);
    }

    @Override
    protected void onPreExecute() {
        mProgressDialog = ProgressDialog.show(mContext,
                null, mContext.getString(R.string.progress_dialog_title), true);
        mProgressDialog.setCancelable(true);
    }

    @Override
    protected void onProgressUpdate(String... progress) {
        mProgressDialog.setMessage(progress[0]);
    }

    @Override
    protected void onPostExecute(MicrosoftFace result) {

        if (result == null) return;

        if (mProgressDialog.isShowing())
            mProgressDialog.dismiss();

        if (listener!=null)
            listener.onLoadFrameDone(result);
//        Intent mainIntent = new Intent(mContext, DetectionActivity.class);
//        mContext.startActivity(mainIntent);
//        ((Activity) mContext).finish();

//            ((ImageView)findViewById(R.id.test)).setImageBitmap(ImageHelper.drawFaceRectanglesOnBitmap(
//                    Statistic.arrFaces.get(0).getOriginBmp(), new Face[]{Statistic.arrFaces.get(0).getFace()}, true));
//            ((ImageView)findViewById(R.id.preview)).setImageBitmap(Statistic.arrFaces.get(0).getFaceEroded());
    }

    public interface OnLoadFramesListener{
        void onLoadFrameDone(MicrosoftFace face);
    }

    private OnLoadFramesListener listener;

    public DecodeTemplates setListener(OnLoadFramesListener listener) {
        this.listener = listener;
        return this;
    }
}
