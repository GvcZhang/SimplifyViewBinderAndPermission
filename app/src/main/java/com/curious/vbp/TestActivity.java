package com.curious.vbp;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.curious.vbp.annotation.RuntimeVBP;
import com.curious.vbp.annotation.permission.NeedsPermission;
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
                        .withNeverAskReason("never reason")
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
                break;
            case R.id.button3:
                break;
        }
    }
}
