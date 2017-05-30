package uet.kltn.hoavt_58.crazyexface.models;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;

import com.microsoft.projectoxford.face.contract.Face;
import com.microsoft.projectoxford.face.contract.FaceLandmarks;
import com.microsoft.projectoxford.face.contract.FaceRectangle;

import java.io.IOException;

import uet.kltn.hoavt_58.crazyexface.helpers.ImageHelper;
import uet.kltn.hoavt_58.crazyexface.helpers.MathUtils;

/**
 * Created by hoavt_58 on 4/10/17.
 */

public class MicrosoftFace {

    private Face face;
    private Bitmap originBmp;
    private Bitmap thumbBmp;
    private float angleSkew;

    public MicrosoftFace(Face face, Bitmap origin) {
        this.face = face;
        this.originBmp = origin;
        try {
            this.thumbBmp = ImageHelper.generateFaceThumbnail(origin, face.faceRectangle);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ;
    }

    public FaceRectangle getRectangle() {
        return face.faceRectangle;
    }

    public FaceLandmarks getLandmarks() {
        return face.faceLandmarks;
    }

    public Bitmap getFaceEroded(float angleSkew) {
        return ImageHelper.erodesFaceFromThumb(thumbBmp, face, angleSkew);
    }

    public void balanceSkinColor (Face other) {

    }

    public void faceEraser() {
        Bitmap tmp = originBmp.copy(Bitmap.Config.ARGB_8888, true);
        FaceLandmarks marks = face.faceLandmarks;
        float p1x = (float) marks.eyebrowLeftOuter.x;
        float p1y = (float) marks.eyebrowLeftOuter.y;
        float p2x = (float) marks.eyebrowRightOuter.x;
        float p2y = (float) marks.eyebrowRightOuter.y;
        float p3x = (float) marks.eyebrowRightOuter.x;
        float p3y = (float) marks.underLipBottom.y;
        float p4x = (float) marks.eyebrowLeftOuter.x;
        float p4y = (float) marks.underLipBottom.y;
        double width = MathUtils.length(p1x, p1y, p2x, p2y);
        double height = MathUtils.length(p1x, p1y, p4x, p4y);
        int[] pixels = new int[(int)Math.floor(width*height)];
        int eraserColor = tmp.getPixel((int)p4x-1, (int)p4y+1);
        for (int y = (int)p1y; y<= (int)p4y; y++){
            for (int x = (int)p1x; x <= p2x; x++) {
                tmp.setPixel(x, y, eraserColor);
            }
        }
        originBmp = tmp.copy(Bitmap.Config.ARGB_8888, true);
//            pixels[x] = Color.BLUE;
//        myBitmap.setPixels(pixels, 0, myBitmap.getWidth(), 0, 0, myBitmap.getWidth(), myBitmap.getHeight());

    }

    public float calucalteFaceSkew() {
        return ImageHelper.angleSkewFace(this.face.faceLandmarks);
    }

    public Bitmap getThumbBmp() {
        return thumbBmp;
    }

    public Bitmap getOriginBmp() {
        return originBmp;
    }

    public Bitmap morphinOtherFace(MicrosoftFace other) {
//        this.faceEraser();
        Bitmap bitmap = this.originBmp.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(bitmap);
        Bitmap drawed = other.getFaceEroded(other.calucalteFaceSkew() - this.calucalteFaceSkew());
        Matrix scaleMatrix = new Matrix();
        float scaleX = ((float) (this.getLandmarks().eyebrowRightOuter.x - this.getLandmarks().eyebrowLeftOuter.x)) /
                ((float) (other.getLandmarks().eyebrowRightOuter.x - other.getLandmarks().eyebrowLeftOuter.x));
        float scaleY = ((float) (this.getLandmarks().underLipBottom.y - this.getLandmarks().eyebrowRightOuter.y)) /
                ((float) (other.getLandmarks().underLipBottom.y - other.getLandmarks().eyebrowRightOuter.y));
        float transX = (float) this.getLandmarks().eyebrowLeftOuter.x - (float) (other.getLandmarks().eyebrowLeftOuter.x - other.getRectangle().left);
        float transY = (float) this.getLandmarks().eyebrowLeftOuter.y - (float) (other.getLandmarks().eyebrowLeftOuter.y - other.getRectangle().top);
        scaleMatrix.setTranslate(transX, transY);
        scaleMatrix.postScale(scaleX, scaleY, (float) this.getLandmarks().eyebrowLeftOuter.x, (float) this.getLandmarks().eyebrowLeftOuter.y);
        canvas.drawBitmap(drawed, scaleMatrix, null);
        return bitmap;
    }

    public Face getFace() {
        return face;
    }
}
