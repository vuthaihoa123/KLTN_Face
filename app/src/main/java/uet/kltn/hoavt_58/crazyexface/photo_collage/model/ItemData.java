package uet.kltn.hoavt_58.crazyexface.photo_collage.model;

/**
 * Created by vutha on 3/1/2017.
 */

public class ItemData {
    private String title = null ;
    private String data = null ;
    private int size = 0;
    private int date = 0;

    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }

    private boolean check = false;

    public ItemData(){
        return;
    }

    public ItemData(String title,String data,int size,int date){
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

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }
}
