package uet.kltn.hoavt_58.crazyexface.activities;

import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

import uet.kltn.hoavt_58.crazyexface.MainActivity;
import uet.kltn.hoavt_58.crazyexface.R;
import uet.kltn.hoavt_58.crazyexface.adapters.FrameAdapter;
import uet.kltn.hoavt_58.crazyexface.adapters.ServiceAdapter;
import uet.kltn.hoavt_58.crazyexface.helpers.ConstValue;
import uet.kltn.hoavt_58.crazyexface.helpers.Flog;
import uet.kltn.hoavt_58.crazyexface.helpers.Statistic;
import uet.kltn.hoavt_58.crazyexface.models.FrameItem;
import uet.kltn.hoavt_58.crazyexface.models.ServiceItem;

/**
 * Created by hoavt_58 on 4/9/17.
 */

public class FrameTemplateActivity extends AppCompatActivity implements FrameAdapter.OnItemClickLitener {

    private static final java.lang.String TAG = FrameTemplateActivity.class.getSimpleName();
    private ArrayList<FrameItem> mFrameList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frame_template);
        initViews();
    }

    private void initViews() {
        mFrameList = initFrameTemplates();
        FrameAdapter adapter = new FrameAdapter(mFrameList).setListener(this);

        RecyclerView rvServices = (RecyclerView) findViewById(R.id.rv_frame_templates);
        rvServices.setLayoutManager(new GridLayoutManager(this, 2));
        rvServices.setAdapter(adapter);
    }

    private ArrayList<FrameItem> initFrameTemplates() {
        ArrayList<FrameItem> arrayList = new ArrayList<>();
        for (int i = 0; i < Statistic.arrIdFrames.length; i++) {
            arrayList.add(new FrameItem(Statistic.arrIdFrames[i], Statistic.arrDesFrames[i]));
        }
        return arrayList;
    }

    @Override
    public void onItemClicked(int pos) {
//        Toast.makeText(this, "click at "+pos, Toast.LENGTH_SHORT).show();
        Flog.d(TAG, "size=" + mFrameList.size());
        Intent intent = new Intent(FrameTemplateActivity.this, ExFaceActivity.class);
        intent.putExtra(ConstValue.EXTRA_FRAME_INDEX, pos);
        startActivity(intent);
    }

    public void actionBack(View view) {
        finish();
    }
}
