package com.curious.vbp.lib;


import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;

import java.util.ArrayList;

@TargetApi(Build.VERSION_CODES.M)
public class VbpPermissionActivity extends Activity {

    public static final String EXTRA_PERMISSIONS = "permissions";
    public static final String EXTRA_RATIONALE_TITLE = "title";
    public static final String EXTRA_RATIONALE_MESSAGE = "message";
    public static final String EXTRA_NEVER_ASK_REASON = "reason";
    private static final int REQUEST_PERMISSION = 100;
    private static final int REQUEST_SETTINGS = 101;


    private ArrayList<String> permissions;
    private String rationaleTitle;
    private String rationaleMessage;
    private String neverAskReason;
    public static VBP.PermissionListener permissionListener;
    private AlertDialog mDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(0);
        Intent intent = getIntent();
        permissions = intent.getStringArrayListExtra(EXTRA_PERMISSIONS);
        rationaleTitle = intent.getStringExtra(EXTRA_RATIONALE_TITLE);
        rationaleMessage = intent.getStringExtra(EXTRA_RATIONALE_MESSAGE);
        neverAskReason = intent.getStringExtra(EXTRA_NEVER_ASK_REASON);

        handleRationale();
    }

    private void handleRationale() {
        for (String permission : permissions) {
            if (shouldShowRequestPermissionRationale(permission)) {
                if (!TextUtils.isEmpty(rationaleMessage)) {
                    mDialog = new AlertDialog.Builder(this)
                            .setCancelable(false)
                            .setTitle(rationaleTitle)
                            .setMessage(rationaleMessage)
                            .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                                dialog.dismiss();
                                handlePermission();
                            })
                            .create();
                    mDialog.show();
                    return;
                }
            }
        }
        handlePermission();
    }

    private void handlePermission() {
        for (String permission : permissions) {
            //never ask permission
            if (!shouldShowRequestPermissionRationale(permission)) {
                mDialog = new AlertDialog.Builder(this)
                        .setCancelable(false)
                        .setMessage(!TextUtils.isEmpty(neverAskReason) ?
                                neverAskReason : getString(R.string.vbp_ask_reason, permission))
                        .setNegativeButton(android.R.string.cancel, (dialog, which) -> {
                            dialog.dismiss();
                            denied(permissions);
                        })
                        .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                            dialog.dismiss();
                            Intent setIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                    Uri.fromParts("package", getPackageName(), null));
                            startActivityForResult(setIntent, REQUEST_SETTINGS);
                        })
                        .create();
                mDialog.show();
                break;
            }
            requestPermissions();
        }
    }


    private void requestPermissions() {
        String[] result = new String[permissions.size()];
        permissions.toArray(result);
        requestPermissions(result, REQUEST_PERMISSION);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SETTINGS) {
            handlePermission();
            //TODO save data, this page may finished
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION) {
            int index = 0;
            ArrayList<String> result = new ArrayList<>();
            boolean allGrant = true;
            for (String permission : permissions) {
                if (grantResults[index] == PackageManager.PERMISSION_DENIED) {
                    allGrant = false;
                    result.add(permission);
                }
                index++;
            }
            if (allGrant) {
                grant();
            } else {
                denied(result);
            }
        }
    }

    private void grant() {
        if (permissionListener != null) {
            permissionListener.onGrant();
        }
        permissionListener = null;
        finish();
    }

    private void denied(ArrayList<String> permissions) {
        if (permissionListener != null) {
            permissionListener.onDenied(permissions);
        }
        permissionListener = null;
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }
}
