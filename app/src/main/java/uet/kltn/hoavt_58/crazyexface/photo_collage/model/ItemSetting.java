package uet.kltn.hoavt_58.crazyexface.photo_collage.model;

/**
 * Created by hoavt on 09/07/2016.
 */
public class ItemSetting {
    private int mIdResImg;
    private int mIdColor;
    private String mDescribe;


    public ItemSetting(int idResImg, int idColorBg, String describe) {
        mIdResImg = idResImg;
        mIdColor = idColorBg;
        mDescribe = describe;
    }

    public int getIdResImg() {
        return mIdResImg;
    }

    public String getDescribe() {
        return mDescribe;
    }

    public int getIdColor() {
        return mIdColor;
    }
}
