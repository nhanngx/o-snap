package com.nhanngx.osnap.controllers;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Camera;
import android.media.MediaScannerConnection;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Surface;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;
import static com.nhanngx.osnap.ui.layouts.CameraPreviewLayout.TAG;

/**
 * Created by nhann on 18-Feb-17.
 * Singleton class to manage the active camera as well as all system cameras for use within app.
 */

public class CameraController {
    private static final String SHARED_PREF_LAST_USED_CAMERA = "osnap_last_used_camera";

    private int mCameraId;
    private Camera mCamera;
    private Activity mActivity;
    private boolean mHasMultipleCamera = false;
    private static CameraController mCameraController;

    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

            File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
            if (pictureFile == null){
                Log.d(TAG, "Error creating media file, check storage permissions: ");
                return;
            }

            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);
                fos.close();
                String filePathToBeScanned[] = {pictureFile.getCanonicalPath()};
                MediaScannerConnection.scanFile(mActivity.getApplicationContext(), filePathToBeScanned, null, null);
            } catch (FileNotFoundException e) {
                Log.d(TAG, "File not found: " + e.getMessage());
            } catch (IOException e) {
                Log.d(TAG, "Error accessing file: " + e.getMessage());
            }
        }
    };

    // Get a singleton
    public static CameraController getInstance(@Nullable Activity activity) {
        if (mCameraController == null) {
            mCameraController = new CameraController(activity);
        }
        return mCameraController;
    }

    private CameraController(@NonNull Activity activity) {
        mActivity = activity;

        int storedCameraId = activity.getPreferences(Context.MODE_PRIVATE).getInt(SHARED_PREF_LAST_USED_CAMERA, -1);
        if (storedCameraId != -1) {
            mCameraId = storedCameraId;
        } else {
            // No pref stored, attempt to start the app with default camera
            try {
                int numberOfCameras = Camera.getNumberOfCameras();
                if (numberOfCameras >= 2) {
                    mHasMultipleCamera = true;
                }
                Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
                for (mCameraId = 0; mCameraId < numberOfCameras; mCameraId++) {
                    Camera.getCameraInfo(mCameraId, cameraInfo);
                    if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                        break;
                    }
                }

                if (mCameraId == numberOfCameras) {
                    // if there is no backfacing camera, fetch the first camera found
                    mCameraId = 0;
                }
            } catch (Exception e){
                // Camera is not available (in use or does not exist)
            }
        }

        // Store camera ID into pref
        activity.getPreferences(Context.MODE_PRIVATE).edit().putInt(SHARED_PREF_LAST_USED_CAMERA, mCameraId).apply();
    }

    // Provided by Android Dev Guide @ https://developer.android.com/reference/android/hardware/Camera.html#setDisplayOrientation(int)
    public void setCameraDisplayOrientation(android.hardware.Camera camera) {
        android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(mCameraId, info);
        int rotation = mActivity.getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }

    public Camera getCurrentCamera() {
         mCamera = Camera.open(mCameraId);
        return mCamera;
    }

    // Advancing to the next camera, at the same time returning the next one in line
    public Camera getNextCamera() {
        mCameraId++;
        if (mCameraId == Camera.getNumberOfCameras()) {
            // if reaching last camera, wraps back
            mCameraId = 0;
        }
        mCamera = Camera.open(mCameraId);
        mActivity.getPreferences(Context.MODE_PRIVATE).edit().putInt(SHARED_PREF_LAST_USED_CAMERA, mCameraId).apply();
        return mCamera;
    }

    // Capture and process photo to make circular before saving it to device storage.
    public void capturePhoto() {
        // TODO: Populate first param to set up capture animation
        mCamera.takePicture(null, null, mPicture);
    }

    /** Create a File for saving an image or video */
    private static File getOutputMediaFile(int type){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "O-snap");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("O-snap", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".jpg");
        } else if(type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_"+ timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }
}
