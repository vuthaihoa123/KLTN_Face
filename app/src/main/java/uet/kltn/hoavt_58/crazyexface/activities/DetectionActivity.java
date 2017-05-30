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
package uet.kltn.hoavt_58.crazyexface.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.microsoft.projectoxford.face.contract.Face;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import uet.kltn.hoavt_58.crazyexface.R;
import uet.kltn.hoavt_58.crazyexface.adapters.FaceAdapter;
import uet.kltn.hoavt_58.crazyexface.helpers.Flog;
import uet.kltn.hoavt_58.crazyexface.helpers.ImageHelper;
import uet.kltn.hoavt_58.crazyexface.models.FaceItem;
import uet.kltn.hoavt_58.crazyexface.models.MicrosoftFace;
import uet.kltn.hoavt_58.crazyexface.tasks.DetectionAsync;

public class DetectionActivity extends AppCompatActivity implements FaceAdapter.OnItemClickLitener, DetectionAsync.OnDetectionListener {
    private static final java.lang.String TAG = DetectionActivity.class.getSimpleName();
    // Flag to indicate which task is to be performed.
    private static final int REQUEST_SELECT_IMAGE = 0;
    public static MicrosoftFace faceDetected;
    private FaceAdapter faceListAdapter;
    private int clickedPosition;
    // The URI of the image selected to detect.
    private Uri mImageUri;
    // The image selected to detect.
    private Bitmap mBitmap;
    private ArrayList<FaceItem> mFacesList = new ArrayList<>();
    private Face[] faces;

    // When the activity is created, set all the member variables to initial state.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detection);


//        mProgressDialog.setTitle(getString(R.string.progress_dialog_title));

        // Disable button "detect" as the image to detect is not selected.
        setDetectButtonEnabledStatus(false);
        setPickFrameButtonEnabledStatus(false);
    }

    // Save the activity state when it's going to stop.
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable("ImageUri", mImageUri);
    }

    // Recover the saved state when the activity is recreated.
    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        mImageUri = savedInstanceState.getParcelable("ImageUri");
        if (mImageUri != null) {
            mBitmap = ImageHelper.loadSizeLimitedBitmapFromUri(
                    mImageUri, getContentResolver());
        }
    }

    // Called when image selection is done.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_SELECT_IMAGE:
                if (resultCode == RESULT_OK) {
                    // If image is selected successfully, set the image URI and bitmap.
                    mImageUri = data.getData();
                    mBitmap = ImageHelper.loadSizeLimitedBitmapFromUri(
                            mImageUri, getContentResolver());
                    if (mBitmap != null) {
                        // Show the image on screen.
                        ImageView imageView = (ImageView) findViewById(R.id.image);
                        imageView.setImageBitmap(mBitmap);

                        // Add detection log.
                        Flog.d(TAG, "Image: " + mImageUri + " resized to " + mBitmap.getWidth()
                                + "x" + mBitmap.getHeight());
                    }

                    // Clear the detection result.
//                    FaceListAdapter faceListAdapter = new FaceListAdapter(null);
                    FaceAdapter faceListAdapter = new FaceAdapter(this, null, null);
                    RecyclerView listView = (RecyclerView) findViewById(R.id.list_detected_faces);
                    listView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
                    listView.setAdapter(faceListAdapter);

                    // Clear the information panel.
                    setInfo("");

                    // Enable button "detect" as the image is selected and not detected.
                    setDetectButtonEnabledStatus(true);
                    setPickFrameButtonEnabledStatus(false);
                }
                break;
            default:
                break;
        }
    }

    // Called when the "Select Image" button is clicked.
    public void selectImage(View view) {
        Intent intent = new Intent(this, SelectImageActivity.class);
        startActivityForResult(intent, REQUEST_SELECT_IMAGE);
    }

    // Called when the "Detect" button is clicked.
    public void detect(View view) {
        // Put the image into an input stream for detection.
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(output.toByteArray());

        // Start a background task to detect faces in the image.
        new DetectionAsync(this, 0).setListener(this).execute(inputStream);
//        new Detection().execute(inputStream);

        // Prevent button click during detecting.
        setAllButtonsEnabledStatus(false);
    }

    // Show the result on screen when detection is done.
    private void setUiAfterDetection(Face[] result, boolean succeed) {
//        // Detection is done, hide the progress dialog.
//        mProgressDialog.dismiss();

        // Enable all the buttons.
        setAllButtonsEnabledStatus(true);

        // Disable button "detect" as the image has already been detected.
        setDetectButtonEnabledStatus(false);
        setPickFrameButtonEnabledStatus(false);

        if (succeed) {
            // The information about the detection result.
            String detectionResult;
            if (result != null) {
                faces = result;

                detectionResult = result.length + " face"
                        + (result.length != 1 ? "s" : "") + " detected";

                // Show the detected faces on original image.
                ImageView imageView = (ImageView) findViewById(R.id.image);
                imageView.setImageBitmap(ImageHelper.drawFaceRectanglesOnBitmap(
                        mBitmap, result, true));

                // Set the adapter of the ListView which contains the details of the detected faces.
//                faceListAdapter = new FaceListAdapter(result);
                mFacesList = initFaceLists(result);

                faceListAdapter = new FaceAdapter(this, mFacesList, mBitmap).setListener(this);
                RecyclerView listView = (RecyclerView) findViewById(R.id.list_detected_faces);
                listView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
                listView.setAdapter(faceListAdapter);
            } else {
                detectionResult = "0 face detected";
                setPickFrameButtonEnabledStatus(false);
            }
            setInfo(detectionResult);
        }

        mImageUri = null;
//        mBitmap = null;
    }

    private ArrayList<FaceItem> initFaceLists(Face[] faces) {
        ArrayList<FaceItem> arrayList = new ArrayList<>();
        for (int i = 0; i < faces.length; i++) {
            arrayList.add(new FaceItem(i, faces[i]));
        }
        return arrayList;
    }

    // Set whether the buttons are enabled.
    private void setDetectButtonEnabledStatus(boolean isEnabled) {
        Button detectButton = (Button) findViewById(R.id.detect);
        detectButton.setEnabled(isEnabled);
    }

    // Set whether the buttons are enabled.
    private void setPickFrameButtonEnabledStatus(boolean isEnabled) {
        Button detectButton = (Button) findViewById(R.id.pick_frame);
        detectButton.setEnabled(isEnabled);
    }

    // Set whether the buttons are enabled.
    private void setAllButtonsEnabledStatus(boolean isEnabled) {
        Button selectImageButton = (Button) findViewById(R.id.select_image);
        selectImageButton.setEnabled(isEnabled);

        Button detectButton = (Button) findViewById(R.id.detect);
        detectButton.setEnabled(isEnabled);

        Button ViewLogButton = (Button) findViewById(R.id.pick_frame);
        ViewLogButton.setEnabled(isEnabled);
    }

    // Set the information panel on screen.
    private void setInfo(String info) {
        TextView textView = (TextView) findViewById(R.id.info);
        textView.setText(info);
    }

    // Called when the "Back" button is clicked.
    public void actionBack(View view) {
        finish();
    }

    // Called when the "Pick frame" button is clicked.
    public void pickFrame(View view) {
        Flog.d(TAG, "pos=" + (clickedPosition));
        Flog.d(TAG, "bmp=" + faceListAdapter.getThumbAt(clickedPosition));
        faceDetected = new MicrosoftFace(faces[clickedPosition], mBitmap);
        Intent intent = new Intent(this, FrameTemplateActivity.class);
        startActivity(intent);
    }

    @Override
    public void onItemClicked(int pos) {
        setPickFrameButtonEnabledStatus(true);
        clickedPosition = pos;
        for (int i = 0; i < mFacesList.size(); i++) {
            mFacesList.get(i).setSelected(i == pos);
        }
        faceListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDetectDone(int id, Face[] faces, boolean succeed, Bitmap bitmap) {
        // Show the result on screen when detection is done.
        setUiAfterDetection(faces, succeed);
    }
}
