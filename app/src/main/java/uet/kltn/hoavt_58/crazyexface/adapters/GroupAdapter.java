package uet.kltn.hoavt_58.crazyexface.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.microsoft.projectoxford.face.contract.GroupResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import uet.kltn.hoavt_58.crazyexface.R;
import uet.kltn.hoavt_58.crazyexface.ui.EmbeddedGridView;

/**
 * Created by vutha on 4/14/2017.
 */

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.MyViewHolder> {

    private Context mContext;

    // The faceObj groups.
    private List<List<UUID>> faceGroups = new ArrayList<>();
    private ArrayList<Bitmap> listThumbs = new ArrayList<>();

    public GroupAdapter(Context context, GroupResult result, ArrayList<Bitmap> listThumbs) {
        mContext = context;

        faceGroups = new ArrayList<>();
        if (result != null) {
            for (UUID[] group : result.groups) {
                faceGroups.add(Arrays.asList(group));
            }
            faceGroups.add(result.messyGroup);
        }

        this.listThumbs = listThumbs;
    }

    public GroupAdapter(Context context) {
        mContext = context;
    }

    @Override
    public GroupAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mItemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_grouping, parent, false);
        return new GroupAdapter.MyViewHolder(mItemView);
    }

    @Override
    public void onBindViewHolder(final GroupAdapter.MyViewHolder holder, final int position) {
        if (faceGroups.isEmpty()|| position>=faceGroups.size()) return;
        String faceGroupName = "Group " + position + ": " + faceGroups.get(position).size() + " faceObj(s)";
        if (position == faceGroups.size() - 1) {
            faceGroupName = "Messy Group: " + faceGroups.get(position).size() + " faceObj(s)";
        }
        holder.groupName.setText(faceGroupName);

        FacesAdapter facesAdapter = new FacesAdapter(mContext, faceGroups.get(position), listThumbs);
        holder.gridViewFaces.setAdapter(facesAdapter);
    }

    @Override
    public int getItemCount() {
        return listThumbs.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public EmbeddedGridView gridViewFaces;
        public TextView groupName;

        public MyViewHolder(View view) {
            super(view);
            gridViewFaces = (EmbeddedGridView) view.findViewById(R.id.faces);
            groupName = (TextView) view.findViewById(R.id.face_group_name);
        }
    }

    // The adapter of the GridView which contains the thumbnails of the detected faces.
    private class FacesAdapter extends BaseAdapter {
        // The detected faces.
        private List<UUID> faces;
        private Context mContext;
        private ArrayList<Bitmap> mListThumbs = new ArrayList<>();

        FacesAdapter(Context context, List<UUID> result, ArrayList<Bitmap> listThumbs) {
            mContext = context;
            faces = new ArrayList<>();
            faces.addAll(result);
            mListThumbs = listThumbs;
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
                LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = layoutInflater.inflate(R.layout.item_face, parent, false);
            }
            convertView.setId(position);

            // Show the faceObj thumbnail.
            ((ImageView) convertView.findViewById(R.id.image_face)).setImageBitmap(
                    mListThumbs.get(position));

            return convertView;
        }
    }

    public void setFaceGroups(List<List<UUID>> faceGroups) {
        this.faceGroups = faceGroups;
    }

    public void setListThumbs(ArrayList<Bitmap> listThumbs) {
        this.listThumbs = listThumbs;
    }
}

