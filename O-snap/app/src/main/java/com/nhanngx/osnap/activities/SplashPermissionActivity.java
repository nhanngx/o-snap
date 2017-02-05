package com.nhanngx.osnap.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.nhanngx.osnap.R;

/**
 * Created by nhanngx on 04-Feb-17.
 */

public class SplashPermissionActivity extends Activity {
    private static final int PERMISSION_REQUEST_CAMERA = 0;

    private boolean shouldDisplayRequestSplash = false;

    // Splash delay in ms
    private static final int SPLASH_TIME = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Check for permission
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA)) {
                shouldDisplayRequestSplash = true;
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        PERMISSION_REQUEST_CAMERA);
            }
        }

        // Check for main camera view finished loading

        if (shouldDisplayRequestSplash) {
            // Change splash screen to display rationale here.
            // Camera permission is required for the app, so just exit otherwise.
            setContentView(R.layout.activity_splash_request_camera);

        } else {
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

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}
