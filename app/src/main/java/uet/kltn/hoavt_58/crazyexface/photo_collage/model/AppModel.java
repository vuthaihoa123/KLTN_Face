package uet.kltn.hoavt_58.crazyexface.photo_collage.model;

/**
 * Created by hoavt on 12/10/2016.
 */
public class AppModel {
    public String id;
    private String title;
    private int priority;

    public AppModel() {
    }

    public AppModel(String id, String title, int priority) {
        this.id = id;
        this.title = title;
        this.priority = priority;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}
