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

@RuntimeVBP
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

        mBtn1 = getTest();
    }


    public <T> T getTest(){
        return null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        VBP.unbind(this);
    }

    @OnClick({R.id.button, R.id.button2})
    public void doOnClick(View view) {
        System.out.println("!11"+((Button)view).getText().toString());
    }

    @NeedsPermission({Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO})
    public void showCamera() {
        //do something
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
