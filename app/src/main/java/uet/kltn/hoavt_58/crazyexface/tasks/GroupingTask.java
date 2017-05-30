package uet.kltn.hoavt_58.crazyexface.tasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.contract.GroupResult;

import java.util.ArrayList;
import java.util.UUID;

import uet.kltn.hoavt_58.crazyexface.R;
import uet.kltn.hoavt_58.crazyexface.helpers.Flog;
import uet.kltn.hoavt_58.crazyexface.helpers.SampleApp;
import uet.kltn.hoavt_58.crazyexface.interfaces.OnGroupingListener;

import static android.content.ContentValues.TAG;

/**
 * Created by vutha on 4/14/2017.
 */

// Background task for faceObj grouping.
public class GroupingTask extends AsyncTask<UUID, String, GroupResult> {

    public Context mContext;
    // Progress dialog popped up when communicating with server.
    public ProgressDialog mProgressDialog;
    private ArrayList<Bitmap> mListThumbs = new ArrayList<>();

    public GroupingTask(Context context) {
        mContext = context;
    }

    @Override
    protected GroupResult doInBackground(UUID... params) {
        FaceServiceClient faceServiceClient = SampleApp.getFaceServiceClient();
        Flog.d(TAG, "Request: Grouping " + params.length + " faceObj(s)");
        try{
            publishProgress("Grouping...");

            // Start grouping, params are faceObj IDs.
            return faceServiceClient.group(params);
        }  catch (Exception e) {
            Flog.d(TAG, e.getMessage());
            publishProgress(e.getMessage());
            return null;
        }
    }

    @Override
    protected void onPreExecute() {
        mProgressDialog = ProgressDialog.show(mContext, null, mContext.getString(R.string.grouping), true);
        mProgressDialog.setCancelable(false);
    }

    @Override
    protected void onPostExecute(GroupResult result) {
        if (mProgressDialog!=null&&mProgressDialog.isShowing())
            mProgressDialog.dismiss();

        if (result != null) {
            Flog.d(TAG, "Response: Success. Grouped into " + result.groups.size() + " faceObj group(s).");
        }

        if (listener!=null) {
            listener.onGroupingDone(result, mListThumbs);
        }
    }

    public GroupingTask setGroupingListener(OnGroupingListener listener) {
        this.listener = listener;
        return this;
    }

    private OnGroupingListener listener;

    public void setListThumbs(ArrayList<Bitmap> listThumbs) {
        mListThumbs = listThumbs;
    }
}
