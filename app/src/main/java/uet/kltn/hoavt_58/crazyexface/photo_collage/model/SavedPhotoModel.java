package uet.kltn.hoavt_58.crazyexface.photo_collage.model;

/**
 * Created by Adm on 11/9/2016.
 */
public class SavedPhotoModel {

    private String title = null ;
    private String data = null ;
    private int size = 0;
    private long date = 0;
    private boolean isSelectCheckbox = false;
    private boolean mIsFirstImage = true;

    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }

    private boolean check = false;

    public SavedPhotoModel(){
        return;
    }

    public SavedPhotoModel(String title,String data,int size,long date){
        this.title = title ;
        this.data = data ;
        this.size = size ;
        this.date = date ;
    }
    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public boolean isSelectCheckbox() {
        return isSelectCheckbox;
    }

    public void setIsSelectCheckbox(boolean isSelectCheckbox) {
        this.isSelectCheckbox = isSelectCheckbox;
    }

    public boolean isFirstImage() {
        return mIsFirstImage;
    }

    public void setIsFirstImage(boolean isFirstImage) {
        this.mIsFirstImage = isFirstImage;
    }
}
