package uet.kltn.hoavt_58.crazyexface.helpers;

import java.util.ArrayList;

import uet.kltn.hoavt_58.crazyexface.R;
import uet.kltn.hoavt_58.crazyexface.models.MicrosoftFace;

/**
 * Created by hoavt_58 on 4/9/17.
 */

public class Statistic {

    public static final int CNT_ITEMS = 6;
    public static ArrayList<MicrosoftFace> arrFaces = new ArrayList<>();
    public static ArrayList<MicrosoftFace> arrFacesBest = new ArrayList<>();

    public static int arrIdFrames[] = new int[]{
            R.drawable.ic_china_1, R.drawable.ic_china_2,
            R.drawable.ic_china_3, R.drawable.ic_china_4,
            R.drawable.ic_china_5, R.drawable.ic_china_6,
//            R.drawable.ic_china_7, R.drawable.ic_china_8,
//            R.drawable.ic_china_9, R.drawable.ic_china_10
    };

    public static String arrDesFrames[] = new String[]{
            "Dieu Thuyen", "Tay Thi",
            "Duong Qua", "Hong That Cong",
            "Kim Long Bat Vuong", "Quach Tinh",
//            "Yi Zhi Mei", "Truong Vo Ky"
    };
}
