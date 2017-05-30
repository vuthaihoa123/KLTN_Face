package uet.kltn.hoavt_58.crazyexface.photo_collage.statistic;

import com.pic.libphotocollage.core.util.BaseStatistic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import uet.kltn.hoavt_58.crazyexface.photo_collage.model.FrameModel;
import uet.kltn.hoavt_58.crazyexface.photo_collage.model.LayoutModel;
import uet.kltn.hoavt_58.crazyexface.photo_collage.model.StickerModel;
import uet.kltn.hoavt_58.crazyexface.photo_collage.utils.FLog;

/**
 * Created by Adm on 8/3/2016.
 */
public final class Statistic extends BaseStatistic {

    public static final int GRID_LAYOUT_CODE = 0x12; // code for grid
    public static final int PILE_LAYOUT_CODE = 0x15; // code for pile

    public static final int ITEM_ON_PAGE = 8;
    public static final int ITEM_SPACE = 10;
    public static final int ITEM_MENU_COLLAGE_SPACE = 18;

    /**
     * List menu code
     */
    public static final int MENU_COLLAGE_TYPE_LAYOUT = 0x20;
    public static final int MENU_COLLAGE_TYPE_TEXT = 0x21;
    public static final int MENU_COLLAGE_TYPE_STICKER = 0x22;
    public static final int MENU_COLLAGE_TYPE_OTHERS = 0x23;
    public static final int MENU_COLLAGE_TYPE_BG = 0x24;
    public static final int MENU_COLLAGE_TYPE_FRAME = 0x25;
    public static final int MENU_COLLAGE_TYPE_MARGIN = 0x26;
    public static final int MENU_COLLAGE_TYPE_RATIO = 0x27;
    public static final long GALLERY_SHARED_ITEM_INTERVAL = 300;
    public static final int MAX_SIZE = 100;
    public static final int MAX_PADDING_SIZE = 50;
    public static final String EXTRA_CURRENT_SELECTED_RECT = "EXTRA_CURRENT_SELECTED_RECT";
    public static final String EXTRAS_PATH_SAVED_PHOTO = "EXTRAS_PATH_SAVED_PHOTO";
    public static final String JPEG = ".jpg";
    public static final int MIN_OPACITY = 20;
    public static final String TAG_PHOTO_EDITOR_FRAG = "photo editor fragment";
    public static final int STICKERS_ADDED_ALLOWED = 15;
    public static final long MAX_WAIT_TERMINATION = 5000;
    private static final int BG_COUNT = 28;
    private static int STICKER_ITEMS = 10;
    private static final int STICKER_TYPES = 10;
    public static final int SAMPLE_SIZE_THUMNAILS = 2;
    public static final String EXTRA_RATIO_FRAG = "EXTRA_RATIO_FRAG";
    public static final float MAX_ROUND_UNIT = 0.1f;
    public static final int RESOLUTION_EXCELLENT = 2560;
    public static final int RESOLUTION_GOOD = 1920;
    public static final int RESOLUTION_AVERAGE = 1280;
    public static final int RESOLUTION_POOR = 960;
    public static final String MAKET ="market://details?id=" ;
    public static final String TAG_STICKER_EDITOR_FRAG = "TAG_STICKER_EDITOR_FRAG";
    public static final String TAG_STICKER_OPACITY_FRAG = "TAG_STICKER_OPACITY_FRAG";
    public static final int VALUE_1KB = 1024;
    public static final String IMAGE_TYPE = "image/*";
    public static final String UNIT_KB = " KB";
    public static final String EXTRA_PATH_PHOTO = "EXTRA_PATH_PHOTO";
    public static final String EXTRA_PATH_PHOTO_LIST = "EXTRA_PATH_PHOTO_LIST";
    public static final String EXTRA_CURRENT_POSITION_STUDIO = "EXTRA_CURRENT_POSITION_STUDIO";
    public static final int REQUEST_CODE_DISPLAY_PHOTO_ACTIVITY = 5;
    public static final int NUM_GRID_COLS = 7;
    public static final String EXTRA_CLOSE_OPTION = "EXTRA_CLOSE_OPTION";
    public static final String EXTRA_CURRENT_TAB = "EXTRA_CURRENT_TAB";

    public static List<LayoutModel> GRID_LAYOUT = null;
    public static List<LayoutModel> PILE_LAYOUT = null;

    public static List<String> BG_LIST = null;

    public static ArrayList<String> FRAME_LIST = null;
    public static final int MAX_ROUND_STEPS = 30, MAX_SPACING_STEPS = 30;

    static {
        GRID_LAYOUT = new ArrayList<>();
        PILE_LAYOUT = new ArrayList<>();

        BG_LIST = new ArrayList<>();
        FRAME_LIST = new ArrayList<>();

        BG_LIST.addAll(getBgList());
        FRAME_LIST.addAll(getFrameList());

        GRID_LAYOUT.addAll(getLayoutList("grid_02_", 14, 2));
        GRID_LAYOUT.addAll(getLayoutList("grid_03_", 37, 3));
        GRID_LAYOUT.addAll(getLayoutList("grid_04_", 26, 4));
        GRID_LAYOUT.addAll(getLayoutList("grid_05_", 19, 5));
        GRID_LAYOUT.addAll(getLayoutList("grid_06_", 12, 6));


        PILE_LAYOUT.addAll(getPileLayoutList("pile_a_", 6));
        PILE_LAYOUT.addAll(getPileLayoutList("pile_b_", 6));
        PILE_LAYOUT.addAll(getPileLayoutList("pile_c_", 6));
        PILE_LAYOUT.addAll(getPileLayoutList("pile_d_", 6));
    }

    public static void init() {

    }

    private static List<LayoutModel> getLayoutList(String prefix, int cnt, int imgCnt) {
        List<LayoutModel> list = new ArrayList<>();
        for (int i = 1; i <= cnt; i++) {
            LayoutModel model = new LayoutModel();
            model.isGrid = true;
            model.name = prefix + i;
            model.imgCount = imgCnt;


            list.add(model);
        }
        return list;
    }

    private static List<LayoutModel> getPileLayoutList(String prefix, int cnt) {
        List<LayoutModel> list = new ArrayList<>();
        for (int i = 2; i <= cnt; i++) {
            LayoutModel model = new LayoutModel();
            model.isGrid = false;
            model.name = prefix + i;
            model.imgCount = i;


            list.add(model);
        }
        return list;
    }

    public static int getTotalPageForGridLayout() {
        int size = GRID_LAYOUT.size();
        if (size % ITEM_ON_PAGE == 0) {
            return size / ITEM_ON_PAGE;
        }
        return size / ITEM_ON_PAGE + 1;
    }

    public static int getTotalPageForPileLayout() {
        int size = PILE_LAYOUT.size();
        if (size % ITEM_ON_PAGE == 0) {
            return size / ITEM_ON_PAGE;
        }
        return size / ITEM_ON_PAGE + 1;
    }

    public static List<LayoutModel> getGridListForPage(int index) {
        List<LayoutModel> list = new ArrayList<>();
        int size = index * ITEM_ON_PAGE + ITEM_ON_PAGE;
        int startIndex = index * ITEM_ON_PAGE;
        if (startIndex < GRID_LAYOUT.size()) {
            for (int i = startIndex; i < size; i++) {
                if (i >= GRID_LAYOUT.size()) {
                    break;
                }
                list.add(GRID_LAYOUT.get(i));
            }
        }


        return list;
    }

    public static List<LayoutModel> getPileListForPage(int index) {
        List<LayoutModel> list = new ArrayList<>();
        int size = index * ITEM_ON_PAGE + ITEM_ON_PAGE;
        int startIndex = index * ITEM_ON_PAGE;
        if (startIndex < PILE_LAYOUT.size()) {
            for (int i = startIndex; i < size; i++) {
                if (i >= PILE_LAYOUT.size()) {
                    break;
                }
                list.add(PILE_LAYOUT.get(i));
            }
        }


        return list;
    }

    public static List<LayoutModel> createMainLayoutList() {
        List<LayoutModel> list = new ArrayList<>();
        for (int i = 2; i <= 6; i++) {
            LayoutModel model = new LayoutModel();
            model.isGrid = true;
            model.name = "grid_0" + i + "_1";
            model.imgCount = i;
            list.add(model);
        }


        for (int i = 2; i <= 6; i++) {
            LayoutModel model = new LayoutModel();
            model.isGrid = false;
            model.name = "pile_a_" + i;
            model.imgCount = i;
            list.add(model);
        }

        return list;
    }

    public static List<LayoutModel> createSubLayoutList(LayoutModel givenModel) {
        List<LayoutModel> list = new ArrayList<>();


        if (givenModel.isGrid) {
            list.addAll(createSubGridLayoutList(givenModel.imgCount));
        } else {
            list.addAll(createSubPileLayoutList(givenModel.imgCount));
        }

        return list;
    }

    public static List<LayoutModel> createSubGridLayoutList(int imgCount) {
        List<LayoutModel> list = new ArrayList<>();

        switch (imgCount) {
            case 2:
                list.addAll(getLayoutList("grid_02_", 14, imgCount));
                break;
            case 3:
                list.addAll(getLayoutList("grid_03_", 37, imgCount));
                break;
            case 4:
                list.addAll(getLayoutList("grid_04_", 26, imgCount));
                break;
            case 5:
                list.addAll(getLayoutList("grid_05_", 19, imgCount));
                break;
            case 6:
                list.addAll(getLayoutList("grid_06_", 12, imgCount));
                break;
        }


        return list;
    }

    public static List<LayoutModel> createSubPileLayoutList(int imgCount) {
        List<LayoutModel> list = new ArrayList<>();

        list.addAll(createPileLayoutListByImgCnt(imgCount));


        return list;
    }

    private static List<LayoutModel> createPileLayoutListByImgCnt(int imgCnt) {
        List<LayoutModel> list = new ArrayList<>();
        for (char i = 'a'; i <= 'd'; i++) {
            String prefix = "" + ((char) i);
            LayoutModel model = new LayoutModel();
            model.isGrid = false;
            model.name = "pile_" + prefix + "_" + imgCnt;
            model.imgCount = imgCnt;


            FLog.d("LayoutModel: " + model);
            list.add(model);
        }

        return list;
    }

    private static List<String> getBgList() {
        List<String> list = new ArrayList<>();
        for (int i = 1; i <= BG_COUNT; ++i) {
            list.add("ic_bag_" + i + ".jpg");
        }

        return list;
    }

    private static List<String> getFrameList() {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < BG_COUNT; ++i) {
            list.add("ic_bag_" + i + ".jpg");
        }

        return list;
    }

    public static Collection<StickerModel> createMainStickerList() {
        List<StickerModel> list = new ArrayList<>();
        for (int i = 0; i < STICKER_TYPES; ++i) {
            StickerModel model = new StickerModel();
            model.isMain = true;
            model.index = i;
            model.name = "s1";
            list.add(model);
        }

        return list;

    }

    public static Collection<StickerModel> createSubStickerList(int index) {
        List<StickerModel> list = new ArrayList<>();
        if (index < 6) {
            STICKER_ITEMS = 10;
        } else {
            STICKER_ITEMS = 26;
        }
        for (int i = 1; i <= STICKER_ITEMS; ++i) {
            StickerModel model = new StickerModel();
            model.name = "s" + i;
            model.index = index;
            list.add(model);
        }

        return list;
    }

    public static Collection<FrameModel> createMainFrameList() {
        List<FrameModel> list = new ArrayList<>();
        for (int i = 0; i < 3; ++i) {
            FrameModel model = new FrameModel();
            model.isMain = true;
            model.index = i;
            model.name = "f1";
            list.add(model);
        }

        return list;

    }

    public static Collection<FrameModel> createSubFrameList(int index) {
        List<FrameModel> list = new ArrayList<>();
        for (int i = 1; i <= 5; ++i) {
            FrameModel model = new FrameModel();
            model.name = "f" + i;
            model.index = index;
            list.add(model);
        }

        return list;
    }

    public static String[] getResolutionList(int ratioCode) {
//        String[] resolutions;
//        switch (ratioCode) {
//            case Statistic.COLLAGE_RATIO_169:
//                resolutions = new String[]{"Excellent (2560x1440)", "Good (1920x1080)", "Average (1280x720)", "Fair (Base-on max resolution)", "Poor (960x540)"};
//                break;
//            case Statistic.COLLAGE_RATIO_916:
//                resolutions = new String[]{"Excellent (1440x2560)", "Good (1080x1920)", "Average (720x1280)", "Fair (Base-on max resolution)", "Poor (540x960)"};
//                break;
//            default:    // Statistic.COLLAGE_RATIO_11
//                resolutions = new String[]{"Excellent (2560x2560)", "Good (1920x1920)", "Average (1280x1280)", "Fair (Base-on max resolution)", "Poor (960x960)"};
//                break;
//        }
//        return resolutions;
        return new String[]{"Excellent", "Good", "Average", "Fair (Base-on max resolution)", "Poor"};
    }

    public static String LINK_GIFT_APP = "http://play.google.com/store/apps/details?id=";
    public static String GIFT_APP = "http://bsoftjsc.com/bs/gifts.txt";
}
