package uet.kltn.hoavt_58.crazyexface.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.contract.Face;
import com.microsoft.projectoxford.face.rest.ClientException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import uet.kltn.hoavt_58.crazyexface.MainActivity;
import uet.kltn.hoavt_58.crazyexface.R;
import uet.kltn.hoavt_58.crazyexface.helpers.Flog;
import uet.kltn.hoavt_58.crazyexface.helpers.ImageHelper;
import uet.kltn.hoavt_58.crazyexface.helpers.SampleApp;
import uet.kltn.hoavt_58.crazyexface.helpers.Statistic;
import uet.kltn.hoavt_58.crazyexface.models.MicrosoftFace;
import uet.kltn.hoavt_58.crazyexface.tasks.DecodeTemplates;

/**
 * Created by hoavt_58 on 4/10/17.
 */

public class SplashActivity extends AppCompatActivity {

    private static final java.lang.String TAG = SplashActivity.class.getSimpleName();

    /**
     * Called when the activity is first created.
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

//        new DecodeTemplates(this).execute(Statistic.CNT_ITEMS);
    }
}
