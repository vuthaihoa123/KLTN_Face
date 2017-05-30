package uet.kltn.hoavt_58.crazyexface.interfaces;

import android.graphics.Bitmap;

import com.microsoft.projectoxford.face.contract.GroupResult;

import java.util.ArrayList;

/**
 * Created by vutha on 4/14/2017.
 */

public interface OnGroupingListener {
    void onGroupingDone(GroupResult result, ArrayList<Bitmap> thumbs);
}
