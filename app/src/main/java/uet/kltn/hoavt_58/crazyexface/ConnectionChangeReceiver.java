package uet.kltn.hoavt_58.crazyexface;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by hoavt_58 on 4/9/17.
 */

public class ConnectionChangeReceiver extends BroadcastReceiver {

    private MainActivity mMainActivity;

    public ConnectionChangeReceiver(MainActivity activity) {
        super();
        mMainActivity = activity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
//        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
//        NetworkInfo mobNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
//        if (activeNetInfo != null) {
//            Toast.makeText(context, "Active Network Type : " + activeNetInfo.getTypeName(), Toast.LENGTH_SHORT).show();
//        }
//        if (mobNetInfo != null) {
//            Toast.makeText(context, "Mobile Network Type : " + mobNetInfo.getTypeName(), Toast.LENGTH_SHORT).show();
//        }
        mMainActivity.checkNetworkstate();
    }
}
