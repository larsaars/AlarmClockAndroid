package com.larsaars.alarmclock.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.widget.Toast;

import com.larsaars.alarmclock.R;

public class RequestPermissionActivity extends AppCompatActivity {

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