package com.example.internadmin.fooddiary.Activities;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.example.internadmin.fooddiary.R;

/**
 * Class object which is used by an activity to make
 * permission request to allow the use of the device camera.
 */

class PermissionsDelegate {

    private static final int REQUEST_CODE = 10;
    private final Activity activity;

    PermissionsDelegate(Activity activity) {
        this.activity = activity;
    }

    boolean hasCameraPermission() {
        int permissionCheckResult = ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.CAMERA
        );
        return permissionCheckResult == PackageManager.PERMISSION_GRANTED;
    }

    void requestCameraPermission() {
        ActivityCompat.requestPermissions(
                activity,
                new String[]{Manifest.permission.CAMERA},
                REQUEST_CODE
        );
    }

    boolean resultGranted(int requestCode,
                          String[] permissions,
                          int[] grantResults) {

        if (requestCode != REQUEST_CODE) {
            return false;
        }

        if (grantResults.length < 1) {
            return false;
        }
        if (!(permissions[0].equals(Manifest.permission.CAMERA))) {
            return false;
        }

        View noPermissionView = activity.findViewById(R.id.no_permission);

        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            noPermissionView.setVisibility(View.GONE);
            return true;
        }

        requestCameraPermission();
        noPermissionView.setVisibility(View.VISIBLE);
        return false;
    }
}