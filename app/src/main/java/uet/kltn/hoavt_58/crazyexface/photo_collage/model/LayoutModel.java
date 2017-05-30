package uet.kltn.hoavt_58.crazyexface.photo_collage.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Adm on 8/4/2016.
 */
public class LayoutModel implements Parcelable {
    public static final Creator<LayoutModel> CREATOR = new Creator<LayoutModel>() {
        @Override
        public LayoutModel createFromParcel(Parcel in) {
            return new LayoutModel(in);
        }

        @Override
        public LayoutModel[] newArray(int size) {
            return new LayoutModel[size];
        }
    };
    public String name;
    public boolean isGrid = true;
    public int imgCount = 2;

    public LayoutModel() {

    }

    protected LayoutModel(Parcel in) {
        name = in.readString();
        isGrid = in.readByte() != 0;
        imgCount = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeByte((byte) (isGrid ? 1 : 0));
        dest.writeInt(imgCount);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        sb.append("Name: " + name);
        sb.append(", ");
        sb.append("isGrid: " + isGrid);
        sb.append(", ");
        sb.append("imgCount: " + imgCount);
        sb.append("]");
        return sb.toString();
    }


    public LayoutModel clone() {
        LayoutModel model = new LayoutModel();
        model.name = name;
        model.imgCount = imgCount;
        model.isGrid = isGrid;
        return model;
    }
}
