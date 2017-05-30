package uet.kltn.hoavt_58.crazyexface.activities;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import uet.kltn.hoavt_58.crazyexface.R;
import uet.kltn.hoavt_58.crazyexface.adapters.PreviewFrameAdapter;
import uet.kltn.hoavt_58.crazyexface.helpers.ConstValue;
import uet.kltn.hoavt_58.crazyexface.helpers.Statistic;
import uet.kltn.hoavt_58.crazyexface.helpers.Utils;
import uet.kltn.hoavt_58.crazyexface.models.FrameItem;
import uet.kltn.hoavt_58.crazyexface.models.MicrosoftFace;
import uet.kltn.hoavt_58.crazyexface.tasks.DecodeTemplates;

/**
 * Created by hoavt_58 on 4/9/17.
 */

public class ExFaceActivity extends AppCompatActivity implements PreviewFrameAdapter.OnItemClickLitener, DecodeTemplates.OnLoadFramesListener {

    private int frameIndex;
    private ImageView ivExContainer;
    private ImageView ivFace;
    private MicrosoftFace micFace;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ex_face);

        if (getIntent() == null) return;
        frameIndex = getIntent().getIntExtra(ConstValue.EXTRA_FRAME_INDEX, -1);
        if (frameIndex == -1) return;
        ivExContainer = ((ImageView) findViewById(R.id.iv_ex_container));
        ivFace = ((CircleImageView) findViewById(R.id.iv_face));
        ivFace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivExContainer.setImageBitmap(micFace.morphinOtherFace(DetectionActivity.faceDetected));
//                ivExContainer.setImageBitmap(ImageHelper.drawFaceRectanglesOnBitmap(Statistic.arrFaces.get(frameIndex).getOriginBmp()
//                        , new Face[]{Statistic.arrFaces.get(frameIndex).getFace()}, true));
            }
        });

        DecodeTemplates async = new DecodeTemplates(this).setListener(this);
        async.execute(frameIndex);
    }

    private void initViews() {
//        ivExContainer.setImageResource(Statistic.arrIdFrames[frameIndex]);
//        ivExContainer.setImageBitmap(Statistic.arrFaces.get(frameIndex).getOriginBmp());
        ivExContainer.setImageResource(Statistic.arrIdFrames[frameIndex]);

        Bitmap thumb = DetectionActivity.faceDetected.getThumbBmp();
//        thumb = ImageHelper.changeColorBitmap(this, thumb);
        ivFace.setImageBitmap(thumb);


        PreviewFrameAdapter adapter = new PreviewFrameAdapter(initFrameTemplates()).setListener(this);
        RecyclerView rvServices = (RecyclerView) findViewById(R.id.rv_frame_templates);
        rvServices.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvServices.setAdapter(adapter);
    }

    private ArrayList<FrameItem> initFrameTemplates() {
        ArrayList<FrameItem> arrayList = new ArrayList<>();
        for (int i = 0; i < Statistic.arrIdFrames.length; i++) {
            arrayList.add(new FrameItem(Statistic.arrIdFrames[i], Statistic.arrDesFrames[i]));
        }
        return arrayList;
    }

    public void actionBack(View view) {
        finish();
    }

    public void actionSave(View view) {
        Bitmap bitmap = ((BitmapDrawable)((ImageView)findViewById(R.id.iv_ex_container)).getDrawable()).getBitmap();
        Utils.saveImageToGallery(this, bitmap);
    }

    @Override
    public void onItemClicked(int pos) {
        if (pos < 0 || pos >= Statistic.arrIdFrames.length) return;
        ivExContainer.setImageResource(Statistic.arrIdFrames[pos]);
        frameIndex = pos;
        DecodeTemplates async = new DecodeTemplates(this).setListener(this);
        async.execute(frameIndex);
    }

    @Override
    public void onLoadFrameDone(MicrosoftFace face) {
        micFace = face;
        initViews();
    }
}
