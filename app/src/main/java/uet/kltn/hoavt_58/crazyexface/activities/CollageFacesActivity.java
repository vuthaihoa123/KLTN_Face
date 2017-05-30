package uet.kltn.hoavt_58.crazyexface.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.contract.Face;
import com.microsoft.projectoxford.face.rest.ClientException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import uet.kltn.hoavt_58.crazyexface.MainActivity;
import uet.kltn.hoavt_58.crazyexface.R;
import uet.kltn.hoavt_58.crazyexface.helpers.Flog;
import uet.kltn.hoavt_58.crazyexface.helpers.ImageHelper;
import uet.kltn.hoavt_58.crazyexface.helpers.SampleApp;
import uet.kltn.hoavt_58.crazyexface.helpers.Statistic;
import uet.kltn.hoavt_58.crazyexface.helpers.Utils;
import uet.kltn.hoavt_58.crazyexface.models.MicrosoftFace;

import static uet.kltn.hoavt_58.crazyexface.activities.GroupActivity.faceIdsSelected;

/**
 * Created by hoavt_58 on 4/16/17.
 */

public class CollageFacesActivity extends AppCompatActivity {
    private static final String TAG = CollageFacesActivity.class.getSimpleName();
    private ArrayList<Integer> mIndexIdentical = new ArrayList<>();
    private String imageResultPath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collage_faces);

//        Uri imageUri = Uri.fromFile(new File(MainActivity.pathContext));
//        Bitmap bitmap = ImageHelper.loadSizeLimitedBitmapFromUri(
//                imageUri, getContentResolver());
//        ((ImageView)findViewById(R.id.iv_collage_container)).setImageBitmap(bitmap);
        Flog.d(TAG, "path=" + MainActivity.pathContext);
        Flog.d(TAG, "size=" + faceIdsSelected.size());
        for (int i = 0; i < faceIdsSelected.size(); i++) {
            Flog.d(TAG, i + "=" + faceIdsSelected.get(i));
        }

        new DecodeContext(this).execute(MainActivity.pathContext);
    }

    class DecodeContext extends AsyncTask<String, String, Face[]> {
        private final Context mContext;
        private boolean mSucceed = true;
        // Progress dialog popped up when communicating with server.
        private ProgressDialog mProgressDialog;
        private Bitmap bitmap = null;

        public DecodeContext(Context context) {
            mContext = context;
        }

        @Override
        protected Face[] doInBackground(String... params) {

            // Get an instance of face service client to detect faces in image.
            FaceServiceClient faceServiceClient = SampleApp.getFaceServiceClient();

            publishProgress("Loading...");

            Uri imageUri = Uri.fromFile(new File(MainActivity.pathContext));
            Bitmap bitmap = ImageHelper.loadSizeLimitedBitmapFromUri(
                    imageUri, getContentResolver());
            this.bitmap = bitmap;
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);
            ByteArrayInputStream inputStream = new ByteArrayInputStream(output.toByteArray());
            Face[] faces = null;
            try {
                faces = faceServiceClient.detect(
                        inputStream,  /* Input stream of image to detect */
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
                e.printStackTrace();
            }
//            Statistic.arrFaces.add(new MicrosoftFace(faces, bitmap));
            Flog.d(TAG, "size faces=" + faces.length);
            mIndexIdentical.clear();
            for (int i = 0; i < faces.length; i++) {
                for (int j = 0; j < faceIdsSelected.size(); j++) {
                    boolean isIdentical = false;
                    try {
                        isIdentical = faceServiceClient.verify(faces[i].faceId, faceIdsSelected.get(j)).isIdentical;
                    } catch (Exception e) {
                        e.printStackTrace();
                        continue;
                    }
                    if (isIdentical) {
                        mIndexIdentical.add(j);
                        break;
                    }
                }
            }
            return faces;
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
        protected void onPostExecute(Face[] result) {

            if (result == null || result.length==0 || bitmap == null) return;

            if (mProgressDialog.isShowing())
                mProgressDialog.dismiss();

            Statistic.arrFacesBest.clear();
            for (int i = 0; i < result.length; i++) {
                Statistic.arrFacesBest.add(new MicrosoftFace(result[i], bitmap));
            }

            Bitmap bitmap = this.bitmap.copy(Bitmap.Config.ARGB_8888, true);
            Canvas canvas = new Canvas(bitmap);
//            Bitmap drawed = other.getFaceEroded(other.calucalteFaceSkew() - this.calucalteFaceSkew());
//            Matrix scaleMatrix = new Matrix();
//            float scaleX = ((float) (this.getLandmarks().eyebrowRightOuter.x - this.getLandmarks().eyebrowLeftOuter.x)) /
//                    ((float) (other.getLandmarks().eyebrowRightOuter.x - other.getLandmarks().eyebrowLeftOuter.x));
//            float scaleY = ((float) (this.getLandmarks().underLipBottom.y - this.getLandmarks().eyebrowRightOuter.y)) /
//                    ((float) (other.getLandmarks().underLipBottom.y - other.getLandmarks().eyebrowRightOuter.y));
//            float transX = (float) this.getLandmarks().eyebrowLeftOuter.x - (float) (other.getLandmarks().eyebrowLeftOuter.x - other.getRectangle().left);
//            float transY = (float) this.getLandmarks().eyebrowLeftOuter.y - (float) (other.getLandmarks().eyebrowLeftOuter.y - other.getRectangle().top);
//            scaleMatrix.setTranslate(transX, transY);
//            scaleMatrix.postScale(scaleX, scaleY, (float) this.getLandmarks().eyebrowLeftOuter.x, (float) this.getLandmarks().eyebrowLeftOuter.y);
//            canvas.drawBitmap(drawed, scaleMatrix, null);
            Flog.d(TAG, "faId size="+faceIdsSelected.size());
            for (int i = 0; i < faceIdsSelected.size(); i++) {

                Bitmap drawed = MainActivity.faceIdThumbnailMap.get(faceIdsSelected.get(mIndexIdentical.get(i))).copy(Bitmap.Config.ARGB_8888, true);
                Flog.d(TAG, i+"="+Statistic.arrFacesBest.size());
                canvas.drawBitmap(drawed, Statistic.arrFacesBest.get(i).getRectangle().left,
                        Statistic.arrFacesBest.get(i).getRectangle().top, null);
            }
            ((ImageView)findViewById(R.id.iv_collage_container)).setImageBitmap(bitmap);
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        faceIdsSelected.clear();
    }

    public void saveImage(View view) {
        Bitmap bitmap = ((BitmapDrawable)((ImageView)findViewById(R.id.iv_collage_container)).getDrawable()).getBitmap();
        Utils.saveImageToGallery(this, bitmap);
    }
}
