package uet.kltn.hoavt_58.crazyexface.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.contract.Face;
import com.microsoft.projectoxford.face.contract.GroupResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import uet.kltn.hoavt_58.crazyexface.MainActivity;
import uet.kltn.hoavt_58.crazyexface.R;
import uet.kltn.hoavt_58.crazyexface.adapters.GroupAdapter;
import uet.kltn.hoavt_58.crazyexface.helpers.ConstValue;
import uet.kltn.hoavt_58.crazyexface.helpers.Flog;
import uet.kltn.hoavt_58.crazyexface.helpers.ImageHelper;
import uet.kltn.hoavt_58.crazyexface.helpers.SampleApp;
import uet.kltn.hoavt_58.crazyexface.interfaces.OnGroupingListener;
import uet.kltn.hoavt_58.crazyexface.tasks.GroupingTask;
import uet.kltn.hoavt_58.crazyexface.ui.EmbeddedGridView;

/**
 * Created by vutha on 4/14/2017.
 */

public class GroupActivity extends AppCompatActivity implements OnGroupingListener {

    private static final String TAG = GroupActivity.class.getSimpleName();
    private ArrayList<UUID> faceIds = new ArrayList<>();
    private ArrayList<Bitmap> listThumbs = new ArrayList<>();
    Map<UUID, Bitmap> faceIdThumbnailMap;
    private RecyclerView recyclerView;
    private GroupAdapter groupAdapter;
    public static ArrayList<UUID> faceIdsSelected = new ArrayList<>();
    private int allPos = 0;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        if (getIntent()==null) return;
        faceIdsSelected.clear();

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle(getString(R.string.progress_dialog_title));


        faceIds = (ArrayList<UUID>) getIntent().getSerializableExtra(ConstValue.EXTRA_FACE_IDS);
//        listThumbs = getIntent().getParcelableArrayListExtra(ConstValue.EXTRA_LIST_THUMBS);
        listThumbs = MainActivity.mListThumbs;

//        faceIds.clear();
//        for (int i = 0; i < MainActivity.mFaces.size(); i++) {
//            faceIds.add(MainActivity.mFaces.get(i).faceId);
//
//
//            faceThumbnails.add(faceThumbnail);
//            faceIdThumbnailMap.put(face.faceId, faceThumbnail);
//        }
//        Flog.d(TAG, "size faceIds="+faceIds.size());

        new GroupingTask().execute(faceIds.toArray(new UUID[faceIds.size()]));
//        if (true) return;
//        Log.d(TAG, "faceIds.size() received="+ faceIds.size()+"_thumbs="+listThumbs.size());

//        recyclerView = (RecyclerView) findViewById(R.id.grouped_faces);
//        recyclerView.setLayoutManager(
//                new GridLayoutManager(this, 4));
//        groupAdapter = new GroupAdapter(this);
//        recyclerView.setAdapter(groupAdapter);

//        GroupingTask task = new GroupingTask(this).setGroupingListener(this);
//        task.setListThumbs(listThumbs);
//        task.execute(faceIds.toArray(new UUID[faceIds.size()]));
    }

    // Background task for face grouping.
    class GroupingTask extends AsyncTask<UUID, String, GroupResult> {
        @Override
        protected GroupResult doInBackground(UUID... params) {
            FaceServiceClient faceServiceClient = SampleApp.getFaceServiceClient();
            Flog.d(TAG, "Request: Grouping " + params.length + " face(s)");
            try{
                publishProgress("Grouping...");

                // Start grouping, params are face IDs.
                return faceServiceClient.group(params);
            }  catch (Exception e) {
                e.printStackTrace();
                publishProgress(e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPreExecute() {
            mProgressDialog.show();
        }

        @Override
        protected void onPostExecute(GroupResult result) {
            if (result != null) {
                Flog.d(TAG, "Response: Success. Grouped into " + result.groups.size() + " face group(s).");
            }
            mProgressDialog.dismiss();
            // Show the result of face grouping.
            ListView groupedFaces = (ListView) findViewById(R.id.grouped_faces);
            FaceGroupsAdapter faceGroupsAdapter = new FaceGroupsAdapter(result);
            groupedFaces.setAdapter(faceGroupsAdapter);
        }
    }

    // The adapter of the GridView which contains the thumbnails of the detected faces.
    private class FaceGroupsAdapter extends BaseAdapter {
        // The face groups.
        List<List<UUID> > faceGroups;

        FaceGroupsAdapter(GroupResult result) {
            faceGroups = new ArrayList<>();
            if (result != null) {
                for (UUID[] group: result.groups) {
                    faceGroups.add(Arrays.asList(group));
                }
                faceGroups.add(result.messyGroup);
            }
        }

        @Override
        public int getCount() {
            return faceGroups.size();
        }

        @Override
        public Object getItem(int position) {
            return faceGroups.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater layoutInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = layoutInflater.inflate(R.layout.item_face_group, parent, false);
            }
            if (position == faceGroups.size() - 1) {
                allPos = faceGroups.size() - 1;
                return convertView;
//                faceGroupName = "Messy Group: " + faceGroups.get(position).size() + " face(s)";
            }
            convertView.setId(position);

            String faceGroupName = "Please pick a face in Group " + position + ": " + faceGroups.get(position).size() + " face(s)";
            if (position == faceGroups.size() - 1) {

                faceGroupName = "Messy Group: " + faceGroups.get(position).size() + " face(s)";
            }
            ((TextView) convertView.findViewById(R.id.face_group_name)).setText(faceGroupName);

            FacesAdapter facesAdapter = new FacesAdapter(faceGroups.get(position), false);
            EmbeddedGridView gridView = (EmbeddedGridView) convertView.findViewById(R.id.faces);
            gridView.setAdapter(facesAdapter);

            return convertView;
        }
    }


    // The adapter of the GridView which contains the thumbnails of the detected faces.
    private class FacesAdapter extends BaseAdapter {
        // The detected faces.
        List<UUID> faces;
        boolean isSelected;

        FacesAdapter(List<UUID> result, boolean isSelected) {
            faces = new ArrayList<>();
            faces.addAll(result);
            this.isSelected = isSelected;
            faceIdsSelected.clear();
        }

        @Override
        public int getCount() {
            return faces.size();
        }

        @Override
        public Object getItem(int position) {
            return faces.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater layoutInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = layoutInflater.inflate(R.layout.item_face, parent, false);
            }
            convertView.setId(position);

            // Show the face thumbnail.
            ((ImageView)convertView.findViewById(R.id.image_face)).setImageBitmap(
                    MainActivity.faceIdThumbnailMap.get(faces.get(position)));
            ((ImageView)convertView.findViewById(R.id.image_face)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Toast.makeText(GroupActivity.this, "clicked at "+faces.get(position), Toast.LENGTH_SHORT).show();
                    if (faceIdsSelected.contains(faces.get(position))||isSelected) {
                        Toast.makeText(GroupActivity.this, "A face in this rows is selected", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    faceIdsSelected.add(faces.get(position));
                    isSelected = true;
                }
            });

            return convertView;
        }
    }

    // Progress dialog popped up when communicating with server.
    ProgressDialog mProgressDialog;

    @Override
    public void onGroupingDone(GroupResult result, ArrayList<Bitmap> listThumbs) {
        if (result != null) {
            Flog.d(TAG, "Grouping is done");
            Flog.d(TAG, "groups="+result.groups.size());
            for (int i = 0; i < result.groups.size(); i++) {
                Flog.d(TAG, "-----"+i+" has size="+result.groups.get(i).length);
                for (int j = 0; j < result.groups.get(i).length; j++) {
                    Flog.d(TAG, "   "+j+"="+result.groups.get(i)[j]);
                }
            }
            Flog.d(TAG, "messyGroups="+result.messyGroup.size());
            for (int i = 0; i < result.messyGroup.size(); i++) {
                Flog.d(TAG, "-----"+i+" has size="+result.messyGroup.get(i));
            }
            List<List<UUID>> faceGroups = new ArrayList<>();
            if (result != null) {
                for (UUID[] group : result.groups) {
                    faceGroups.add(Arrays.asList(group));
                }
                faceGroups.add(result.messyGroup);
            }
            groupAdapter.setFaceGroups(faceGroups);
            groupAdapter.setListThumbs(listThumbs);
            groupAdapter.notifyDataSetChanged();
        } else {
            Flog.d(TAG, "Grouping is fail");
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, MainActivity.class));
    }

    public void collageFaces(View view) {
        if (faceIdsSelected.size()==allPos)
            startActivity(new Intent(this, CollageFacesActivity.class));
        else
            Toast.makeText(this, "Please choose the additional face in row.", Toast.LENGTH_SHORT).show();
        if(true)return;
        Toast.makeText(this, "size="+faceIdsSelected.size(), Toast.LENGTH_SHORT).show();
        Flog.d(TAG, "path="+MainActivity.pathContext);
        for (int i = 0; i < faceIdsSelected.size(); i++) {
            Flog.d(TAG, i+"="+faceIdsSelected.get(i));
        }
    }
}
