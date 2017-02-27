package com.nhanngx.osnap.ui.fragments;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.nhanngx.osnap.R;
import com.nhanngx.osnap.controllers.CameraController;
import com.nhanngx.osnap.ui.layouts.CameraPreviewLayout;

/**
 * Created by nhanngx on 29-Jan-17.
 */

public class CameraFragment extends Fragment {
    Activity mActivity;
    CameraPreviewLayout mCameraPreview;
    Camera mCamera;
    FrameLayout mPreviewFrameLayout;
    CameraController mCameraController;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (!getContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            // system detects no camera, display error toast and/or close app here.
        }

        // init member variables
        mActivity = getActivity();

        // init layout elements
        View rootView = inflater.inflate(R.layout.fragment_camera, container, false);
        View headerView = rootView.findViewById(R.id.camera_header);
        View footerView = rootView.findViewById(R.id.camera_footer);

        // Measuring and setting display params to create the square preview matching all screen sizes.
        // All display params are set to support ONLY portrait orientation.
        Display defaultDisplay = mActivity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        Point realSize = new Point();

        defaultDisplay.getSize(size);
        defaultDisplay.getRealSize(realSize);
        int buttonViewHeight = (size.y - size.x)/2;

        ViewGroup.LayoutParams tempLayoutParams = headerView.getLayoutParams();
        tempLayoutParams.height = buttonViewHeight;
        headerView.setLayoutParams(tempLayoutParams);
        tempLayoutParams = footerView.getLayoutParams();
        tempLayoutParams.height = buttonViewHeight;
        footerView.setPadding(0, 0, 0, (realSize.y - size.y));

        mCameraController = CameraController.getInstance(mActivity);
        mPreviewFrameLayout = (FrameLayout) rootView.findViewById(R.id.camera_preview);

        ImageButton captureButton = (ImageButton) rootView.findViewById(R.id.capture_button);
        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCameraController.capturePhoto();
            }
        });
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        // get camera object to create preview
        mCamera = mCameraController.getCurrentCamera();

        // Create our Preview view and set it as the content of our activity.
        mCameraPreview = new CameraPreviewLayout(getContext(), mCamera);
        mPreviewFrameLayout.removeAllViews();
        mPreviewFrameLayout.addView(mCameraPreview);
        mCameraController.setCameraDisplayOrientation(mCamera);
    }

    @Override
    public void onPause() {
        super.onPause();
        mCameraController.releaseCamera();
    }
}
