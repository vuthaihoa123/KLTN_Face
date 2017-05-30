package uet.kltn.hoavt_58.crazyexface.models;

import android.graphics.Bitmap;

import com.microsoft.projectoxford.face.contract.Face;

/**
 * Created by hoavt_58 on 4/9/17.
 */

public class FaceItem {

    private int id;
    private Face faceObj;
    private Bitmap thumb;
    private boolean isSelected;

    public FaceItem(int id, Face face) {
        this.id = id;
        this.faceObj = face;
    }

    public Face getFaceObj() {
        return faceObj;
    }

    public void setThumb(Bitmap thumb) {
        this.thumb = thumb;
    }

    public Bitmap getThumb() {
        return thumb;
    }

    public int getId() {
        return id;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isSelected() {
        return isSelected;
    }
}
