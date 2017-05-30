package com.pic.libphotocollage.core.layout.pile;

import android.graphics.Path;
import android.graphics.Rect;

import com.pic.libphotocollage.core.util.BaseStatistic;
import com.pic.libphotocollage.core.util.Flog;

import java.util.ArrayList;

/**
 * Created by Adm on 8/9/2016.
 */
public class PileStatistic {
    private static int mIdPileStyle = -1;

    public static float[][] getCollageLayoutByName(String nameLayout, int ratio) {
        Flog.i("PileStatistic ratio: " + ratio);
        if (nameLayout.contains("_a_")) {
            mIdPileStyle = 0;
            return PileLayoutA.getCollageLayoutByName(nameLayout, ratio);
        } else if (nameLayout.contains("_b_")) {
            mIdPileStyle = 1;
            return PileLayoutB.getCollageLayoutByName(nameLayout, ratio);
        } else if (nameLayout.contains("_c_")) {
            mIdPileStyle = 2;
            return PileLayoutC.getCollageLayoutByName(nameLayout, ratio);
        } else if (nameLayout.contains("_d_")) {
            mIdPileStyle = 3;
            return PileLayoutD.getCollageLayoutByName(nameLayout, ratio);
        }
        mIdPileStyle = -1;
        return PileLayoutA.getCollageLayoutByName(nameLayout, ratio);
    }

    private static float[][][] getPilePathByIdStyle(String nameLayout, int ratio) {
        if (nameLayout.contains("_a_")) {
            mIdPileStyle = 0;
        } else if (nameLayout.contains("_b_")) {
            mIdPileStyle = 1;
        } else if (nameLayout.contains("_c_")) {
            mIdPileStyle = 2;
        } else if (nameLayout.contains("_d_")) {
            mIdPileStyle = 3;
        }

        switch (mIdPileStyle) {
            case 0:
                return PileLayoutA.getPathDateByName(nameLayout, ratio);
            case 1:
                return PileLayoutB.getPathDateByName(nameLayout, ratio);
            case 2:
                return PileLayoutC.getPathDateByName(nameLayout, ratio);
            case 3:
                return PileLayoutD.getPathDateByName(nameLayout, ratio);
            default:
                return PileLayoutA.getPathDateByName(nameLayout, ratio);
        }
    }

    public static ArrayList<Path> getPilePaths(String nameLayout, int ratioCode, Rect canvasRoi) {
        float[][][] currentPreset = getPilePathByIdStyle(nameLayout, ratioCode);
        if (currentPreset == null)
            return null;
        ArrayList<Path> drawPaths = new ArrayList<Path>();

        int viewWidth = canvasRoi.width(), viewHeight = canvasRoi.height(), leftPadding = canvasRoi.left, topPadding = canvasRoi.top;
//        Flog.i("viewWidth=" + viewWidth + "_viewHeight=" + viewHeight + "_leftPadding=" + leftPadding + "_topPadding=" + topPadding);

        for (int i = 0; i < currentPreset.length; i++) {
            Path path = new Path();
//            Flog.i(i+"-------------------------------");
//            Flog.i("mCurrentPreset[i][0][0]=" + currentPreset[i][0][0] + "_mCurrentPreset[i][0][1]=" + currentPreset[i][0][1]);
            path.moveTo(currentPreset[i][0][0] * viewWidth + leftPadding, currentPreset[i][0][1]
                    * viewHeight + topPadding);
//            Flog.i("mCurrentPreset[i][1][0]=" + currentPreset[i][1][0] + "_mCurrentPreset[i][1][1]=" + currentPreset[i][1][1]);
            path.lineTo(currentPreset[i][1][0] * viewWidth + leftPadding, currentPreset[i][1][1]
                    * viewHeight + topPadding);
//            Flog.i("mCurrentPreset[i][2][0]=" + currentPreset[i][2][0] + "_mCurrentPreset[i][2][1]=" + currentPreset[i][2][1]);
            path.lineTo(currentPreset[i][2][0] * viewWidth + leftPadding, currentPreset[i][2][1]
                    * viewHeight + topPadding);
//            Flog.i("mCurrentPreset[i][3][0]=" + currentPreset[i][3][0] + "_mCurrentPreset[i][3][1]=" + currentPreset[i][3][1]);
            path.lineTo(currentPreset[i][3][0] * viewWidth + leftPadding, currentPreset[i][3][1]
                    * viewHeight + topPadding);
//            Flog.i("mCurrentPreset[i][0][0]=" + currentPreset[i][0][0] + "_mCurrentPreset[i][0][1]=" + currentPreset[i][0][1]);
            path.lineTo(currentPreset[i][0][0] * viewWidth + leftPadding, currentPreset[i][0][1]
                    * viewHeight + topPadding);
//            Flog.i("mCurrentPreset[i][1][0]=" + currentPreset[i][1][0] + "_mCurrentPreset[i][1][1]=" + currentPreset[i][1][1]);
            path.lineTo(currentPreset[i][1][0] * viewWidth + leftPadding, currentPreset[i][1][1]
                    * viewHeight + topPadding);

            drawPaths.add(path);
        }
        return drawPaths;
    }
}
