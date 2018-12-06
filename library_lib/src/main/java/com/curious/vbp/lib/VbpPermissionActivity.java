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
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.Arrays;

@TargetApi(Build.VERSION_CODES.M)
public class VbpPermissionActivity extends Activity {

    public static final String EXTRA_PERMISSIONS = "permissions";
    public static final String EXTRA_RATIONALE_TITLE = "title";
    public static final String EXTRA_RATIONALE_MESSAGE = "message";
    public static final String EXTRA_NEVER_ASK_REASON = "reason";
    public static final String EXTRA_NEVER_ASK_TITLE = "never_ask_title";
    private static final int REQUEST_PERMISSION = 100;
    private static final int REQUEST_SETTINGS = 101;


    private String rationaleTitle;
    private String rationaleMessage;
    private String neverAskReason;
    private String neverAskTitle;
    public static VBP.PermissionListener permissionListener;
    private AlertDialog mDialog;

    private ArrayList<String> permissions;
    private ArrayList<String> rationalePermissions = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new FrameLayout(this));
        getWindow().setStatusBarColor(0);

        Intent intent = getIntent();
        permissions = intent.getStringArrayListExtra(EXTRA_PERMISSIONS);
        rationaleTitle = intent.getStringExtra(EXTRA_RATIONALE_TITLE);
        rationaleMessage = intent.getStringExtra(EXTRA_RATIONALE_MESSAGE);
        neverAskReason = intent.getStringExtra(EXTRA_NEVER_ASK_REASON);
        neverAskTitle = intent.getStringExtra(EXTRA_NEVER_ASK_TITLE);
        handleRationale();
    }

    private void handleRationale() {
        for (String permission : permissions) {
            if (shouldShowRequestPermissionRationale(permission)) {
                rationalePermissions.add(permission);
            }
        }
        if (!rationalePermissions.isEmpty() && !TextUtils.isEmpty(rationaleMessage)) {
            mDialog = new AlertDialog.Builder(this)
                    .setCancelable(false)
                    .setTitle(rationaleTitle)
                    .setMessage(rationaleMessage)
                    .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                        dialog.dismiss();
                        requestPermissions();
                    })
                    .create();
            mDialog.show();
            return;
        }
        requestPermissions();
    }

    private void goToSettings(ArrayList<String> neverAskList, ArrayList<String> deniedList) {
        //never ask permission
        ArrayList<String> tmp = new ArrayList<>();
        tmp.addAll(neverAskList);
        tmp.addAll(deniedList);

        mDialog = new AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle(!TextUtils.isEmpty(neverAskTitle) ?
                        neverAskTitle : getString(android.R.string.dialog_alert_title))
                .setMessage(!TextUtils.isEmpty(neverAskReason) ?
                        neverAskReason : getString(R.string.vbp_ask_reason, neverAskList))
                .setNegativeButton(android.R.string.cancel, (dialog, which) -> {
                    dialog.dismiss();
                    denied(tmp);
                })
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    dialog.dismiss();
                    Intent setIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.fromParts("package", getPackageName(), null));
                    startActivityForResult(setIntent, REQUEST_SETTINGS);
                })
                .create();
        mDialog.show();
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
            requestPermissions();
            //TODO save data, this page may finished
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        System.out.println(Arrays.toString(permissions) + "====]=[====" + Arrays.toString(grantResults));


        if (requestCode == REQUEST_PERMISSION) {
            int index = 0;
            ArrayList<String> deniedResult = new ArrayList<>();
            ArrayList<String> neverAskList = new ArrayList<>();
            ArrayList<String> justNeverAskList = new ArrayList<>();

            boolean allGrant = true;
            for (String permission : permissions) {
                if (grantResults[index] == PackageManager.PERMISSION_DENIED) {
                    allGrant = false;
                    /**
                     * shouldShowRequestPermissionRationale:
                     * first request return false;
                     * if reject but not select never ask return true;
                     * if select never ask, it return false;
                     */
                    boolean isTip = shouldShowRequestPermissionRationale(permission);
                    if (!isTip) {
                        neverAskList.add(permission);
                        if (rationalePermissions.contains(permission)) {
                            justNeverAskList.add(permission);
                        }
                        continue;
                    }
                    deniedResult.add(permission);
                }
                index++;
            }
            if (allGrant) {
                grant();
            } else {
                if (!deniedResult.isEmpty()) {
                    deniedResult.addAll(neverAskList);
                    denied(deniedResult);
                } else if (!justNeverAskList.isEmpty()) {
                    deniedResult.addAll(neverAskList);
                    denied(deniedResult);
                } else {
                    goToSettings(neverAskList, deniedResult);
                }
            }
        }
    }

    private void grant() {
        if (permissionListener != null) {
            permissionListener.onGrant();
        }
        finish();
    }

    private void denied(ArrayList<String> permissions) {
        if (permissionListener != null) {
            permissionListener.onDenied(permissions);
        }
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
        permissionListener = null;
    }
}
