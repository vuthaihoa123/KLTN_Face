package uet.kltn.hoavt_58.crazyexface.helpers;

import android.graphics.PointF;

/**
 * Created by hoavt_58 on 4/9/17.
 */

public class MathUtils {
    public static double length(PointF leftPoint, PointF rightPoint) {
        return Math.hypot(leftPoint.x - rightPoint.x, leftPoint.y - rightPoint.y);
    }

    public static double length(double leftX, double leftY, double rightX, double rightY) {
        return Math.hypot(leftX - rightX, leftY - rightY);
    }

    /**
     * _ rightP
     * _| |
     * _|   |
     * leftP|_____| perpendicularP
     */
    public static double angleSkew(PointF leftPoint, PointF rightPoint) {
        PointF perpendicularPoint = new PointF(rightPoint.x, leftPoint.y);
        double lenHypotenuse = length(leftPoint, rightPoint);
        double lenPerpendicularSide = length(leftPoint, perpendicularPoint);
        if (leftPoint.y >= rightPoint.y) {
            return Math.toDegrees(Math.acos(lenPerpendicularSide / lenHypotenuse));
        } else {
            return -Math.toDegrees(Math.acos(lenPerpendicularSide / lenHypotenuse));
        }
    }

    public static double angleSkew(double leftX, double leftY, double rightX, double rightY) {
        double lenHypotenuse = length(leftX, leftY, rightX, rightY);
        double lenPerpendicularSide = length(leftX, leftY, rightX, leftY);
        if (leftY >= rightY) {
            return Math.toDegrees(Math.acos(lenPerpendicularSide / lenHypotenuse));
        } else {
            return -Math.toDegrees(Math.acos(lenPerpendicularSide / lenHypotenuse));
        }
    }
}
