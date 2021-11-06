package com.larsaars.alarmclock.app.activity;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import android.content.pm.PackageManager;
import android.os.Bundle;

import com.larsaars.alarmclock.R;
import com.larsaars.alarmclock.ui.etc.RootActivity;

public class RequestPermissionActivity extends RootActivity {

    private static final int REQUEST_PERMISSION_CODE = 689;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_permission);

        // get from intent which permission should be requested
        String permissionToRequest = getIntent().getStringExtra("permission");
        // and request it
        ActivityCompat.requestPermissions(this, new String[]{permissionToRequest}, REQUEST_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION_CODE) { // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // set that the result was ok and finish
                setResult(RESULT_OK);
            } else {
                // set that result was bad
                setResult(RESULT_CANCELED);
            }

            finish();

            return;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}