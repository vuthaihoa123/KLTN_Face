package uet.kltn.hoavt_58.crazyexface.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.microsoft.projectoxford.face.contract.Emotion;
import com.microsoft.projectoxford.face.contract.FacialHair;
import com.microsoft.projectoxford.face.contract.HeadPose;

import java.io.IOException;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import uet.kltn.hoavt_58.crazyexface.R;
import uet.kltn.hoavt_58.crazyexface.helpers.Flog;
import uet.kltn.hoavt_58.crazyexface.helpers.ImageHelper;
import uet.kltn.hoavt_58.crazyexface.models.FaceItem;

/**
 * Created by hoavt_58 on 4/9/17.
 */

public class FaceAdapter extends RecyclerView.Adapter<FaceAdapter.MyViewHolder> {

    private static String TAG = FaceAdapter.class.getSimpleName();
    private ArrayList<FaceItem> arrayList = new ArrayList<>();
    private OnItemClickLitener listener;
    private Context context;

    public FaceAdapter(Context context, ArrayList<FaceItem> arrayList, Bitmap bitmap) {
        this.context = context;
        if (arrayList == null || arrayList.isEmpty() || bitmap == null)
            return;
        this.arrayList = arrayList;

        for (int i = 0; i < arrayList.size(); i++) {
            try {
                Bitmap thumbBmp = ImageHelper.generateFaceThumbnail(
                        bitmap, arrayList.get(i).getFaceObj().faceRectangle);
                arrayList.get(i).setThumb(thumbBmp);
                Flog.d(TAG, i+": w="+thumbBmp.getWidth()+"_y="+thumbBmp.getHeight());
            } catch (IOException e) {
                e.printStackTrace();
                continue;
            }
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_face_with_description, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        FaceItem item = this.arrayList.get(position);

        // Show the face thumbnail.
        holder.ivThumb.setImageBitmap(item.getThumb());

        // Show the face details.
//        DecimalFormat formatter = new DecimalFormat("#0.0");
        String face_description = String.format("Age: %s\nGender: %s\nSmile: %s\nGlasses: %s\nFacialHair: %s\nEmotion: %s\nHeadPose: %s",
                item.getFaceObj().faceAttributes.age,
                item.getFaceObj().faceAttributes.gender,
                item.getFaceObj().faceAttributes.smile,
                item.getFaceObj().faceAttributes.glasses,
                getFacialHair(item.getFaceObj().faceAttributes.facialHair),
                getEmotion(item.getFaceObj().faceAttributes.emotion),
                getHeadPose(item.getFaceObj().faceAttributes.headPose)
        );
        holder.tvDescribe.setText(face_description);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener!=null)
                    listener.onItemClicked(position);
            }
        });
        if (item.isSelected()) {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorSecondary));
        } else {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.button_disabled_background));
        }
    }

    private String getFacialHair(FacialHair facialHair) {
        return (facialHair.moustache + facialHair.beard + facialHair.sideburns > 0) ? "Yes" : "No";
    }

    private String getEmotion(Emotion emotion) {
        String emotionType = "";
        double emotionValue = 0.0;
        if (emotion.anger > emotionValue) {
            emotionValue = emotion.anger;
            emotionType = "Anger";
        }
        if (emotion.contempt > emotionValue) {
            emotionValue = emotion.contempt;
            emotionType = "Contempt";
        }
        if (emotion.disgust > emotionValue) {
            emotionValue = emotion.disgust;
            emotionType = "Disgust";
        }
        if (emotion.fear > emotionValue) {
            emotionValue = emotion.fear;
            emotionType = "Fear";
        }
        if (emotion.happiness > emotionValue) {
            emotionValue = emotion.happiness;
            emotionType = "Happiness";
        }
        if (emotion.neutral > emotionValue) {
            emotionValue = emotion.neutral;
            emotionType = "Neutral";
        }
        if (emotion.sadness > emotionValue) {
            emotionValue = emotion.sadness;
            emotionType = "Sadness";
        }
        if (emotion.surprise > emotionValue) {
            emotionValue = emotion.surprise;
            emotionType = "Surprise";
        }
        return String.format("%s: %f", emotionType, emotionValue);
    }

    private String getHeadPose(HeadPose headPose) {
        return String.format("Pitch: %s, Roll: %s, Yaw: %s", headPose.pitch, headPose.roll, headPose.yaw);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public FaceAdapter setListener(OnItemClickLitener listener) {
        this.listener = listener;
        return this;
    }

    public Bitmap getThumbAt(int pos) {
        return arrayList.get(pos).getThumb();
    }

    public interface OnItemClickLitener {
        public void onItemClicked(int pos);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public CircleImageView ivThumb;
        public TextView tvDescribe;

        public MyViewHolder(View view) {
            super(view);
            ivThumb = ((CircleImageView) view.findViewById(R.id.face_thumbnail));
            tvDescribe = ((TextView) view.findViewById(R.id.text_detected_face));
        }
    }
}
