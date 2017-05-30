//
// Copyright (c) Microsoft. All rights reserved.
// Licensed under the MIT license.
//
// Microsoft Cognitive Services (formerly Project Oxford): https://www.microsoft.com/cognitive-services
//
// Microsoft Cognitive Services (formerly Project Oxford) GitHub:
// https://github.com/Microsoft/Cognitive-Face-Android
//
// Copyright (c) Microsoft Corporation
// All rights reserved.
//
// MIT License:
// Permission is hereby granted, free of charge, to any person obtaining
// a copy of this software and associated documentation files (the
// "Software"), to deal in the Software without restriction, including
// without limitation the rights to use, copy, modify, merge, publish,
// distribute, sublicense, and/or sell copies of the Software, and to
// permit persons to whom the Software is furnished to do so, subject to
// the following conditions:
//
// The above copyright notice and this permission notice shall be
// included in all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED ""AS IS"", WITHOUT WARRANTY OF ANY KIND,
// EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
// MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
// NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
// LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
// OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
// WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
//
package uet.kltn.hoavt_58.crazyexface.helpers;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.microsoft.projectoxford.face.contract.Face;
import com.microsoft.projectoxford.face.contract.FaceLandmarks;
import com.microsoft.projectoxford.face.contract.FaceRectangle;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

/**
 * Defined several functions to load, draw, save, resize, and rotate images.
 */
public class ImageHelper {

    private static final String TAG = ImageHelper.class.getSimpleName();

    // The maximum side length of the image to detect, to keep the size of image less than 4MB.
    // Resize the image if its side length is larger than the maximum.
    private static final int IMAGE_MAX_SIDE_LENGTH = 1280;

    // Ratio to scale a detected face rectangle, the face rectangle scaled up looks more natural.
    private static final double FACE_RECT_SCALE_RATIO = 1;

    // Decode image from imageUri, and resize according to the expectedMaxImageSideLength
    // If expectedMaxImageSideLength is
    //     (1) less than or equal to 0,
    //     (2) more than the actual max size length of the bitmap
    //     then return the original bitmap
    // Else, return the scaled bitmap
    public static Bitmap loadSizeLimitedBitmapFromUri(
            Uri imageUri,
            ContentResolver contentResolver) {
        try {
            // Load the image into InputStream.
            InputStream imageInputStream = contentResolver.openInputStream(imageUri);

            // For saving memory, only decode the image meta and get the side length.
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            Rect outPadding = new Rect();
            BitmapFactory.decodeStream(imageInputStream, outPadding, options);

            // Calculate shrink rate when loading the image into memory.
            int maxSideLength =
                    options.outWidth > options.outHeight ? options.outWidth : options.outHeight;
            options.inSampleSize = 1;
            options.inSampleSize = calculateSampleSize(maxSideLength, IMAGE_MAX_SIDE_LENGTH);
            options.inJustDecodeBounds = false;
            if (imageInputStream != null) {
                imageInputStream.close();
            }

            // Load the bitmap and resize it to the expected size length
            imageInputStream = contentResolver.openInputStream(imageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(imageInputStream, outPadding, options);
            maxSideLength = bitmap.getWidth() > bitmap.getHeight()
                    ? bitmap.getWidth() : bitmap.getHeight();
            double ratio = IMAGE_MAX_SIDE_LENGTH / (double) maxSideLength;
            if (ratio < 1) {
                bitmap = Bitmap.createScaledBitmap(
                        bitmap,
                        (int) (bitmap.getWidth() * ratio),
                        (int) (bitmap.getHeight() * ratio),
                        false);
            }

            return rotateBitmap(bitmap, getImageRotationAngle(imageUri, contentResolver));
        } catch (Exception e) {
            return null;
        }
    }

    public static Bitmap loadSizeLimitedBitmapFromAssets(Context context,
                                                         String pathAssets) {
        try {
            AssetManager am = context.getAssets();
            InputStream imageInputStream = am.open(pathAssets);
            // Load the image into InputStream.
//            InputStream imageInputStream = context.openInputStream(imageUri);

            // For saving memory, only decode the image meta and get the side length.
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            Rect outPadding = new Rect();
            BitmapFactory.decodeStream(imageInputStream, outPadding, options);

            // Calculate shrink rate when loading the image into memory.
            int maxSideLength =
                    options.outWidth > options.outHeight ? options.outWidth : options.outHeight;
            options.inSampleSize = 1;
            options.inSampleSize = calculateSampleSize(maxSideLength, IMAGE_MAX_SIDE_LENGTH);
            options.inJustDecodeBounds = false;
            if (imageInputStream != null) {
                imageInputStream.close();
            }

            // Load the bitmap and resize it to the expected size length
//            imageInputStream = contentResolver.openInputStream(imageUri);
            imageInputStream = am.open(pathAssets);
            Bitmap bitmap = BitmapFactory.decodeStream(imageInputStream, outPadding, options);
            maxSideLength = bitmap.getWidth() > bitmap.getHeight()
                    ? bitmap.getWidth() : bitmap.getHeight();
            double ratio = IMAGE_MAX_SIDE_LENGTH / (double) maxSideLength;
            if (ratio < 1) {
                bitmap = Bitmap.createScaledBitmap(
                        bitmap,
                        (int) (bitmap.getWidth() * ratio),
                        (int) (bitmap.getHeight() * ratio),
                        false);
            }

            return bitmap;
        } catch (Exception e) {
            return null;
        }
    }

    // Draw detected face rectangles in the original image. And return the image drawn.
    // If drawLandmarks is set to be true, draw the five main landmarks of each face.
    public static Bitmap drawFaceRectanglesOnBitmap(
            Bitmap originalBitmap, Face[] faces, boolean drawLandmarks) {
        Bitmap bitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(bitmap);

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.GREEN);
        int stokeWidth = Math.max(originalBitmap.getWidth(), originalBitmap.getHeight()) / 100;
        if (stokeWidth == 0) {
            stokeWidth = 1;
        }
        paint.setStrokeWidth(stokeWidth);

        if (faces != null) {
            for (Face face : faces) {
                FaceRectangle faceRectangle =
                        calculateFaceRectangle(bitmap, face.faceRectangle, FACE_RECT_SCALE_RATIO);

                canvas.drawRect(
                        faceRectangle.left,
                        faceRectangle.top,
                        faceRectangle.left + faceRectangle.width,
                        faceRectangle.top + faceRectangle.height,
                        paint);

                if (drawLandmarks) {
                    int radius = face.faceRectangle.width / 30;
                    if (radius == 0) {
                        radius = 1;
                    }
                    paint.setStyle(Paint.Style.FILL);
                    paint.setStrokeWidth(radius);

                    canvas.drawCircle(
                            (float) face.faceLandmarks.pupilLeft.x,
                            (float) face.faceLandmarks.pupilLeft.y,
                            radius,
                            paint);

                    canvas.drawCircle(
                            (float) face.faceLandmarks.pupilRight.x,
                            (float) face.faceLandmarks.pupilRight.y,
                            radius,
                            paint);

                    canvas.drawCircle(
                            (float) face.faceLandmarks.noseTip.x,
                            (float) face.faceLandmarks.noseTip.y,
                            radius,
                            paint);

                    canvas.drawCircle(
                            (float) face.faceLandmarks.mouthLeft.x,
                            (float) face.faceLandmarks.mouthLeft.y,
                            radius,
                            paint);

                    canvas.drawCircle(
                            (float) face.faceLandmarks.mouthRight.x,
                            (float) face.faceLandmarks.mouthRight.y,
                            radius,
                            paint);

                    canvas.drawCircle(
                            (float) face.faceLandmarks.eyebrowLeftOuter.x,
                            (float) face.faceLandmarks.eyebrowLeftOuter.y,
                            radius,
                            paint);

                    canvas.drawCircle(
                            (float) face.faceLandmarks.eyeLeftOuter.x,
                            (float) face.faceLandmarks.eyeLeftOuter.y,
                            radius,
                            paint);

                    canvas.drawCircle(
                            (float) face.faceLandmarks.eyeLeftTop.x,
                            (float) face.faceLandmarks.eyeLeftTop.y,
                            radius,
                            paint);

                    canvas.drawCircle(
                            (float) face.faceLandmarks.eyeLeftBottom.x,
                            (float) face.faceLandmarks.eyeLeftBottom.y,
                            radius,
                            paint);

                    canvas.drawCircle(
                            (float) face.faceLandmarks.eyeLeftInner.x,
                            (float) face.faceLandmarks.eyeLeftInner.y,
                            radius,
                            paint);

                    canvas.drawCircle(
                            (float) face.faceLandmarks.eyebrowLeftInner.x,
                            (float) face.faceLandmarks.eyebrowLeftInner.y,
                            radius,
                            paint);

                    canvas.drawCircle(
                            (float) face.faceLandmarks.noseRootLeft.x,
                            (float) face.faceLandmarks.noseRootLeft.y,
                            radius,
                            paint);

                    canvas.drawCircle(
                            (float) face.faceLandmarks.noseRootRight.x,
                            (float) face.faceLandmarks.noseRootRight.y,
                            radius,
                            paint);

                    canvas.drawCircle(
                            (float) face.faceLandmarks.noseLeftAlarTop.x,
                            (float) face.faceLandmarks.noseLeftAlarTop.y,
                            radius,
                            paint);

                    canvas.drawCircle(
                            (float) face.faceLandmarks.noseRightAlarTop.x,
                            (float) face.faceLandmarks.noseRightAlarTop.y,
                            radius,
                            paint);

                    canvas.drawCircle(
                            (float) face.faceLandmarks.noseLeftAlarOutTip.x,
                            (float) face.faceLandmarks.noseLeftAlarOutTip.y,
                            radius,
                            paint);

                    canvas.drawCircle(
                            (float) face.faceLandmarks.noseLeftAlarOutTip.x,
                            (float) face.faceLandmarks.noseLeftAlarOutTip.y,
                            radius,
                            paint);

                    paint.setStyle(Paint.Style.STROKE);
                    paint.setStrokeWidth(stokeWidth);
                }
            }
        }

        return bitmap;
    }

    // Highlight the selected face thumbnail in face list.
    public static Bitmap highlightSelectedFaceThumbnail(Bitmap originalBitmap) {
        Bitmap bitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.parseColor("#3399FF"));
        int stokeWidth = Math.max(originalBitmap.getWidth(), originalBitmap.getHeight()) / 10;
        if (stokeWidth == 0) {
            stokeWidth = 1;
        }
        bitmap.getWidth();
        paint.setStrokeWidth(stokeWidth);
        canvas.drawRect(
                0,
                0,
                bitmap.getWidth(),
                bitmap.getHeight(),
                paint);

        return bitmap;
    }

    // Crop the face thumbnail out from the original image.
    // For better view for human, face rectangles are resized to the rate faceRectEnlargeRatio.
    public static Bitmap generateFaceThumbnail(
            Bitmap originalBitmap,
            FaceRectangle faceRectangle) throws IOException {
        FaceRectangle faceRect =
                calculateFaceRectangle(originalBitmap, faceRectangle, FACE_RECT_SCALE_RATIO);

        return Bitmap.createBitmap(
                originalBitmap, faceRect.left, faceRect.top, faceRect.width, faceRect.height);
    }

    // Return the number of times for the image to shrink when loading it into memory.
    // The SampleSize can only be a final value based on powers of 2.
    private static int calculateSampleSize(int maxSideLength, int expectedMaxImageSideLength) {
        int inSampleSize = 1;

        while (maxSideLength > 2 * expectedMaxImageSideLength) {
            maxSideLength /= 2;
            inSampleSize *= 2;
        }

        return inSampleSize;
    }

    // Get the rotation angle of the image taken.
    private static int getImageRotationAngle(
            Uri imageUri, ContentResolver contentResolver) throws IOException {
        int angle = 0;
        Cursor cursor = contentResolver.query(imageUri,
                new String[]{MediaStore.Images.ImageColumns.ORIENTATION}, null, null, null);
        if (cursor != null) {
            if (cursor.getCount() == 1) {
                cursor.moveToFirst();
                angle = cursor.getInt(0);
            }
            cursor.close();
        } else {
            ExifInterface exif = new ExifInterface(imageUri.getPath());
            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    angle = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    angle = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    angle = 90;
                    break;
                default:
                    break;
            }
        }
        return angle;
    }

    // Rotate the original bitmap according to the given orientation angle
    private static Bitmap rotateBitmap(Bitmap bitmap, int angle) {
        // If the rotate angle is 0, then return the original image, else return the rotated image
        if (angle != 0) {
            Matrix matrix = new Matrix();
            matrix.postRotate(angle);
            return Bitmap.createBitmap(
                    bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        } else {
            return bitmap;
        }
    }

    // Resize face rectangle, for better view for human
    // To make the rectangle larger, faceRectEnlargeRatio should be larger than 1, recommend 1.3
    private static FaceRectangle calculateFaceRectangle(
            Bitmap bitmap, FaceRectangle faceRectangle, double faceRectEnlargeRatio) {
        // Get the resized side length of the face rectangle
        double sideLength = faceRectangle.width * faceRectEnlargeRatio;
        sideLength = Math.min(sideLength, bitmap.getWidth());
        sideLength = Math.min(sideLength, bitmap.getHeight());

        // Make the left edge to left more.
        double left = faceRectangle.left
                - faceRectangle.width * (faceRectEnlargeRatio - 1.0) * 0.5;
        left = Math.max(left, 0.0);
        left = Math.min(left, bitmap.getWidth() - sideLength);

        // Make the top edge to top more.
        double top = faceRectangle.top
                - faceRectangle.height * (faceRectEnlargeRatio - 1.0) * 0.5;
        top = Math.max(top, 0.0);
        top = Math.min(top, bitmap.getHeight() - sideLength);

        // Shift the top edge to top more, for better view for human
        double shiftTop = faceRectEnlargeRatio - 1.0;
        shiftTop = Math.max(shiftTop, 0.0);
        shiftTop = Math.min(shiftTop, 1.0);
        top -= 0.15 * shiftTop * faceRectangle.height;
        top = Math.max(top, 0.0);

        // Set the result.
        FaceRectangle result = new FaceRectangle();
        result.left = (int) left;
        result.top = (int) top;
        result.width = (int) sideLength;
        result.height = (int) sideLength;
        return result;
    }

    public static Bitmap erodesFaceFromThumb(Bitmap faceThumbnail, Face face, float angle) {

        Path borderPath = new Path();

        Flog.d(TAG, "move: x=" + ((float) face.faceLandmarks.eyebrowLeftOuter.x - face.faceRectangle.left)
                + "_y=" + ((float) face.faceLandmarks.eyebrowLeftOuter.y - face.faceRectangle.top));
        int i = 0;
        if (i == 0) {

            float p1x = (float) face.faceLandmarks.eyebrowLeftOuter.x - face.faceRectangle.left;
            float p1y = (float) face.faceLandmarks.eyebrowLeftOuter.y - face.faceRectangle.top;
            borderPath.moveTo(p1x, p1y);
            float p2x = (float) face.faceLandmarks.eyebrowRightOuter.x - face.faceRectangle.left;
            float p2y = (float) face.faceLandmarks.eyebrowRightOuter.y - face.faceRectangle.top;
            borderPath.lineTo(p2x, p2y);
            float p3x = (float) face.faceLandmarks.mouthRight.x - face.faceRectangle.left;
            float p3y = (float) face.faceLandmarks.underLipBottom.y - face.faceRectangle.top;
            borderPath.lineTo(p3x, p3y);
            float p4x = (float) face.faceLandmarks.mouthLeft.x - face.faceRectangle.left;
            float p4y = (float) face.faceLandmarks.underLipBottom.y - face.faceRectangle.top;
            borderPath.lineTo(p4x, p4y);
            borderPath.close();

        } else if (i == 1) {

            FaceLandmarks marks = face.faceLandmarks;

            borderPath.moveTo((float) marks.eyeLeftOuter.x - face.faceRectangle.left,
                    (float) marks.eyeLeftOuter.y - face.faceRectangle.top);
            float controlEyeLeftX = (float) (marks.eyebrowLeftInner.x + marks.eyebrowLeftOuter.x) / 2;
            float controlEyeLeftY = (float) (marks.eyebrowLeftInner.y + marks.eyebrowLeftOuter.y) / 2;
            borderPath.quadTo(controlEyeLeftX - face.faceRectangle.left,
                    controlEyeLeftY - face.faceRectangle.top,
                    (float) marks.eyeLeftInner.x - face.faceRectangle.left,
                    (float) marks.eyeLeftInner.y - face.faceRectangle.top);
            borderPath.quadTo((float) (marks.pupilLeft.x - face.faceRectangle.left),
                    (float) (marks.pupilLeft.y * 2 - controlEyeLeftY - face.faceRectangle.top),
                    (float) marks.eyeLeftOuter.x - face.faceRectangle.left,
                    (float) marks.eyeLeftOuter.y - face.faceRectangle.top);
            Paint paint = new Paint();
            paint.setColor(Color.YELLOW);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(10);

        } else if (i == 2) {

            FaceLandmarks marks = face.faceLandmarks;
            float p1x = (float) marks.eyeLeftOuter.x - face.faceRectangle.left;
            float p1y = (float) marks.eyeLeftTop.y - face.faceRectangle.top;
            borderPath.moveTo(p1x - 10, p1y - 10);
            float p2x = (float) marks.eyeLeftInner.x - face.faceRectangle.left;
            float p2y = (float) marks.eyeLeftTop.y - face.faceRectangle.top;
            borderPath.lineTo(p2x + 10, p2y - 10);
            float p3x = (float) marks.eyeLeftInner.x - face.faceRectangle.left;
            float p3y = (float) marks.eyeLeftBottom.y - face.faceRectangle.top;
            borderPath.lineTo(p3x + 10, p3y + 10);
            float p4x = (float) marks.eyeLeftOuter.x - face.faceRectangle.left;
            float p4y = (float) marks.eyeLeftBottom.y - face.faceRectangle.top;
            borderPath.lineTo(p4x - 10, p4y + 10);
            borderPath.close();
        } else if (i == 3) {
            FaceRectangle rect = face.faceRectangle;
            float left = rect.left - face.faceRectangle.left;
            float top = rect.top - face.faceRectangle.top;
            float right = left + rect.width;
            float bottom = top + rect.height;
            borderPath.addRect(left, top, right, bottom, Path.Direction.CCW);
        }
        return rotatePath(face, borderPath, faceThumbnail, angle);
    }

    public static float angleSkewFace(FaceLandmarks landmarks) {
        return (float) MathUtils.angleSkew(landmarks.mouthLeft.x, landmarks.mouthLeft.y,
                landmarks.mouthRight.x, landmarks.mouthRight.y);
    }

    // Convert transparentColor to be transparent in a Bitmap.
    public static Bitmap makeTransparent(Bitmap bit) {
        int width = bit.getWidth();
        int height = bit.getHeight();
        Bitmap myBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        int[] allpixels = new int[height * width];
//        int [] allpixels = new int [ myBitmap.getHeight()*myBitmap.getWidth()];
//        bit.getPixels(allpixels, 0, myBitmap.getWidth(), 0, 0, myBitmap.getWidth(),myBitmap.getHeight());
//        myBitmap.setPixels(allpixels, 0, width, 0, 0, width, height);

//        for (int i = 0; i < height * width; i++) {
//            allpixels[i] = Color.alpha(Color.TRANSPARENT);
//        }
        Arrays.fill(allpixels, Color.alpha(Color.TRANSPARENT));

        myBitmap.setPixels(allpixels, 0, width, 0, 0, width, height);
        return myBitmap;
    }

    public static Bitmap rotatePath(Face face, Path path, Bitmap thumb, float angle) {
        Bitmap bitmap = thumb.copy(Bitmap.Config.ARGB_8888, true);
        bitmap = makeTransparent(bitmap);
        Canvas canvas = new Canvas(bitmap);
        Matrix matrix = new Matrix();

        matrix.setRotate(angle, (float) face.faceLandmarks.eyebrowLeftOuter.x - face.faceRectangle.left,
                (float) face.faceLandmarks.eyebrowLeftOuter.y - face.faceRectangle.top);

        path.transform(matrix);
        boolean doClip = true;
        if (doClip) {
            try {
                canvas.clipPath(path);
            } catch (UnsupportedOperationException e) {
                Log.e(TAG, "clipPath() not supported");
                doClip = false;
            }
        }


//        canvas.drawBitmap(thumb, 0, 0, null);
        Paint paint = new Paint();
//        paint.setAlpha(105);
        canvas.drawBitmap(thumb, matrix, paint);


//        Path borderPath = new Path();
//
//        FaceLandmarks marks = face.faceLandmarks;
//
//        borderPath.moveTo((float) marks.eyeLeftOuter.x - face.faceRectangle.left,
//                (float) marks.eyeLeftOuter.y - face.faceRectangle.top);
//        float controlEyeLeftX = (float) (marks.eyebrowLeftInner.x + marks.eyebrowLeftOuter.x) / 2;
//        float controlEyeLeftY = (float) (marks.eyebrowLeftInner.y + marks.eyebrowLeftOuter.y) / 2;
//        borderPath.quadTo(controlEyeLeftX - face.faceRectangle.left,
//                controlEyeLeftY - face.faceRectangle.top,
//                (float) marks.eyeLeftInner.x - face.faceRectangle.left,
//                (float) marks.eyeLeftInner.y - face.faceRectangle.top);
//        borderPath.quadTo((float) (marks.pupilLeft.x - face.faceRectangle.left),
//                (float) (marks.pupilLeft.y * 2 - controlEyeLeftY - face.faceRectangle.top),
//                (float) marks.eyeLeftOuter.x - face.faceRectangle.left,
//                (float) marks.eyeLeftOuter.y - face.faceRectangle.top);
//        Paint paint = new Paint();
//        paint.setColor(Color.YELLOW);
//        paint.setStyle(Paint.Style.STROKE);
//        paint.setStrokeWidth(10);
//        canvas.drawPath(borderPath, paint);
//        canvas.drawCircle((float) marks.eyeLeftOuter.x - face.faceRectangle.left,
//                (float) marks.eyeLeftOuter.y - face.faceRectangle.top, 20, paint);

        return bitmap;
    }

    public static Bitmap changeColorBitmap(Activity context, Bitmap bitmap) {
        Bitmap resultBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        Paint paint = new Paint();
        ColorFilter filter = new PorterDuffColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
        paint.setColorFilter(filter);

        Canvas canvas = new Canvas(resultBitmap);
        canvas.drawBitmap(resultBitmap, 0, 0, paint);
        return resultBitmap;
    }
}
