package com.larsaars.alarmclock.app.activity;

import androidx.activity.result.ActivityResult;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.larsaars.alarmclock.R;
import com.larsaars.alarmclock.ui.etc.RootActivity;
import com.larsaars.alarmclock.ui.view.ToastMaker;
import com.larsaars.alarmclock.utils.Constants;
import com.larsaars.alarmclock.utils.Executable;
import com.larsaars.alarmclock.utils.activity.BetterActivityResult;

public class RequestPermissionActivity extends RootActivity {

    private static final int REQUEST_PERMISSION_CODE = 689;

    // function to check if permission has been granted, if not start intent
    // and request permission
    // in any case execute result when finished
    public static void checkPermission(RootActivity context, String permission, Runnable result) {
        // if permission has not been granted, start intent
        if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(context, RequestPermissionActivity.class);
            intent.putExtra(Constants.EXTRA_PERMISSION, permission);
            context.activityLauncher.launch(intent, resultIntent -> {
                        boolean resultCode = resultIntent.getResultCode() == RESULT_OK;
                        if (resultCode) {
                            result.run();
                        } else {
                            ToastMaker.make(context, R.string.missing_permission);
                        }
                    }
            );
        } else {
            result.run();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_permission);

        // get from intent which permission should be requested
        String permissionToRequest = getIntent().getStringExtra(Constants.EXTRA_PERMISSION);
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