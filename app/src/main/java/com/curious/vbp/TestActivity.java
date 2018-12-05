package com.curious.vbp;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.curious.vbp.annotation.viewbinder.BindView;
import com.curious.vbp.annotation.viewbinder.OnClick;
import com.curious.vbp.lib.VBP;

import java.util.List;

public class TestActivity extends AppCompatActivity {

    @BindView(R.id.button)
    public Button mBtn1;
    @BindView(R.id.textView)
    public TextView mTV1;
    @BindView(R.id.button2)
    public Button mBtn2;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        VBP.bind(this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        VBP.unbind(this);
    }

    @OnClick({R.id.button, R.id.button2, R.id.button3})
    public void doOnClick(View view) {
        switch (view.getId()) {
            //camera permission
            case R.id.button:
                VBP.with(this)
                        .withPermission(Manifest.permission.CAMERA)
                        .withRationale("Tip", "We need some permissions to perform this function for you, please grant the needed permissions.")
                        .withNeverAskReason("Help", "Please click \"Settings\"-\"App Manager\"-\"Permission Manager\" to open it.")
                        .withListener(new VBP.PermissionListener() {
                            @Override
                            public void onGrant() {
                                System.out.println("grant===all");
                            }

                            @Override
                            public void onDenied(List<String> deniedPermissions) {
                                System.out.println("denied===" + deniedPermissions);
                            }
                        }).check();
                break;
            case R.id.button2:

                VBP.with(this)
                        .withPermission(Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .withRationale("Tip", "We need some permissions to perform this function for you, please grant the needed permissions.")
                        .withListener(new VBP.PermissionListener() {
                            @Override
                            public void onGrant() {
                                System.out.println("grant===all");
                            }

                            @Override
                            public void onDenied(List<String> deniedPermissions) {
                                System.out.println("denied===" + deniedPermissions);
                            }
                        }).check();

                break;
            case R.id.button3:
                Intent setIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.fromParts("package", getPackageName(), null));
                startActivityForResult(setIntent, 1111);
                break;
        }
    }
}
