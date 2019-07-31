package com.tzl.agriculture.fragment.personal.activity.set;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.tzl.agriculture.R;
import com.tzl.agriculture.model.UserInfo;

import org.apache.commons.lang.StringUtils;

import butterknife.BindView;
import camera.activity.CameraActivity;

/**
 * 身份证拍照上传页面
 */
public class UpLoadIdentifyActivity extends SetBaseActivity {

    @BindView(R.id.iv_zm)
    ImageView ivZm;
    @BindView(R.id.iv_fm)
    ImageView ivFm;
    @BindView(R.id.tv_zm)
    TextView tvZm;
    @BindView(R.id.tv_fm)
    TextView tvFm;

    private String path01;
    private String path02;

    @Override
    public int setLayout() {
        return R.layout.activity_up_load_identify;
    }

    @Override
    public void initView() {
        setTitle("身份上传");

        tvZm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                identityPositive();
            }
        });
        tvFm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                identityBack();
            }
        });

    }

    @Override
    public void initData() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == CameraActivity.REQUEST_CODE) {
            //获取文件路径，显示图片
            if (data != null) {
                String path = data.getStringExtra("result");
                if (!TextUtils.isEmpty(path)) {
                    path01 = path;
                    ivZm.setImageBitmap(BitmapFactory.decodeFile(path));
                }
            }



        }else {//REQUEST_BACK
            if (data != null) {
                String path = data.getStringExtra("result");
                if (!TextUtils.isEmpty(path)) {
                    path02 = path;
                    ivFm.setImageBitmap(BitmapFactory.decodeFile(path));
                }
            }
        }

    }

    /**
     * 拍摄证件照片
     *
     * @param type 拍摄证件类型
     */
    private void takePhoto(int type) {
        if (ActivityCompat.checkSelfPermission(UpLoadIdentifyActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(UpLoadIdentifyActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 0x12);
            return;
        }
        CameraActivity.navToCamera(this, type,0);
    }

    /**
     * 身份证正面
     */
    public void identityPositive() {
        takePhoto(CameraActivity.TYPE_ID_CARD_FRONT);
    }

    /**
     * 身份证反面
     */
    public void identityBack() {
        takePhoto(CameraActivity.TYPE_ID_CARD_BACK);
    }


    @Override
    protected void onResume() {
        /**
         * 设置为横屏
         */
        if(getRequestedOrientation()!= ActivityInfo.SCREEN_ORIENTATION_PORTRAIT){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        super.onResume();
    }
}
