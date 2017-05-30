package uet.kltn.hoavt_58.crazyexface.models;

/**
 * Created by hoavt_58 on 4/9/17.
 */

public class ServiceItem {

    private int idResThumb;
    private String describe;

    public ServiceItem(int idResThumb, String describe) {
        this.idResThumb = idResThumb;
        this.describe = describe;
    }

    public int getIdResThumb() {
        return idResThumb;
    }

    public String getDescribe() {
        return describe;
    }
}
