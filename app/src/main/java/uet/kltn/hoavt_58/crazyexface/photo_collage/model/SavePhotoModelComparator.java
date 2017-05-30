package uet.kltn.hoavt_58.crazyexface.photo_collage.model;

import java.util.Comparator;

/**
 * Created by hoavt on 24/11/2016.
 */
public class SavePhotoModelComparator implements Comparator<SavedPhotoModel> {
    @Override
    public int compare(SavedPhotoModel lhs, SavedPhotoModel rhs) {
        if (lhs.getDate() < rhs.getDate())
            return 1;
        else if (lhs.getDate() == rhs.getDate())
            return 0;
        else
            return -1;
    }
}
