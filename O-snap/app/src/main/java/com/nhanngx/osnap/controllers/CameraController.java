package com.nhanngx.osnap.controllers;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Camera;
import android.preference.PreferenceManager;
import android.view.Surface;

import java.util.ArrayList;

/**
 * Created by nhann on 18-Feb-17.
 * Singleton class to manage all system cameras for use within app.
 */

public class CameraController {
    private static final String SHARED_PREF_LAST_USED_CAMERA = "osnap_last_used_camera";

    int mCameraId;
    Activity mActivity;
    private static CameraController mCameraController;

    // Get a singleton
    public static CameraController getInstance(Activity activity) {
        if (mCameraController == null) {
            mCameraController = new CameraController(activity);
        }
        return mCameraController;
    }

    CameraController(Activity activity) {
        mActivity = activity;

        int storedCameraId = activity.getPreferences(Context.MODE_PRIVATE).getInt(SHARED_PREF_LAST_USED_CAMERA, -1);
        if (storedCameraId != -1) {
            mCameraId = storedCameraId;
        } else {
            // No pref stored, attempt to start the app with default camera
            try {
                int numberOfCameras = Camera.getNumberOfCameras();
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
        return Camera.open(mCameraId);
    }

    // Advancing to the next camera, at the same time returning the next one in line
    public Camera getNextCamera() {
        mCameraId++;
        if (mCameraId == Camera.getNumberOfCameras()) {
            // if reaching last camera, wraps back
            mCameraId = 0;
        }
        return Camera.open(mCameraId);
    }
}
