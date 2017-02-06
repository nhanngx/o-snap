package com.nhanngx.osnap.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;

import com.nhanngx.osnap.R;

/**
 * Created by nhanngx on 04-Feb-17.
 */

public class SplashPermissionActivity extends Activity {
    private Activity mActivity;
    private int mCurrentRequestedPermission = 0;

    // Required permission constants
    static final int PERMISSION_REQUIRED_CAMERA = 0;
    static final int PERMISSION_REQUIRED_STORAGE = 1;

    // Splash delay in ms
    private static final int SPLASH_TIME = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mActivity = this;
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Check for permission
        if (ifRequiredPermissionsMissing()) {
            if (checkShouldShowRationale()) {
                // Change splash screen to display rationale here.
                // Camera permission is required for the app, so just exit otherwise.
                doShowRationaleSplash();
            } else {
                doRequestPermissionRoutine();
            }
        } else {
            // Perform normal app starting routine.
            doStartCameraActivityRoutine();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        if (mCurrentRequestedPermission == 2) {
            mCurrentRequestedPermission = 0;
            if (ifRequiredPermissionsMissing()) {
                doShowRationaleSplash();
            } else {
                doStartCameraActivityRoutine();
            }
        } else {
            doRequestPermissionRoutine();
        }
        // TODO: Handle optional permissions here case-by-case.
    }

    private void doShowRationaleSplash() {
        setContentView(R.layout.activity_splash_request_camera);
        Button requestPermissionBtn = (Button) findViewById(R.id.btn_request_permission_camera);
        requestPermissionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doRequestPermissionRoutine();
            }
        });
    }

    private void doStartCameraActivityRoutine() {
        // Check for main camera view finished loading

        // here.
        
        // TODO: might wanna display a thank you message once the user allowed all permissions?

        // Small delay for splash
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                // Change splash screen to display rationale here.
                // Camera permission is required for the app, so just exit otherwise.

                Intent cameraIntent = new Intent(getApplicationContext(), FragmentNavigationController.class);
                startActivity(cameraIntent);
                SplashPermissionActivity.this.finish();
            }
        }, SPLASH_TIME);
    }

    private boolean ifRequiredPermissionsMissing() {
        return (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                || (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED);
    }

    private boolean checkShouldShowRationale() {
        return (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA))
                || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    private void doRequestPermissionRoutine() {
        switch (mCurrentRequestedPermission) {
            case 0:
                mCurrentRequestedPermission++;
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.CAMERA},
                            PERMISSION_REQUIRED_CAMERA);
                } else {
                    doRequestPermissionRoutine();
                }
                break;
            case 1:
                mCurrentRequestedPermission++;
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            PERMISSION_REQUIRED_STORAGE);
                    return;
                } else {
                    doRequestPermissionRoutine();
                }
                break;
            case 2:
                break;
        }
    }
}
