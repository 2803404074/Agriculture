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

import com.bumptech.glide.Glide;
import com.github.ybq.android.spinkit.SpinKitView;
import com.tzl.agriculture.R;
import com.tzl.agriculture.util.JsonUtil;
import com.tzl.agriculture.util.SPUtils;
import com.tzl.agriculture.util.TextUtil;
import com.tzl.agriculture.util.ToastUtil;

import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import Utils.GsonObjectCallback;
import Utils.OkHttp3Utils;
import butterknife.BindView;
import butterknife.OnClick;
import camera.activity.CameraActivity;
import config.Mall;
import config.User;
import okhttp3.Call;

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

    @BindView(R.id.tv_load)
    TextView mTextViewLoad;

    private String path01;
    private String path02;

    private boolean isFont = true; //正面
    private String name;
    private String number;
    private String status;

    @BindView(R.id.spin_kit)
    SpinKitView mSpinKitView;

    @Override
    public void backFinish() {
        finish();
    }

    @Override
    public int setLayout() {
        return R.layout.activity_up_load_identify;
    }

    @Override
    public void initView() {
        setTitle("身份上传");
    }


    @OnClick({R.id.tv_load,R.id.tv_zm,R.id.iv_zm,R.id.tv_fm,R.id.iv_fm})
    public void onTextClick(View view) {
        if (view.getId() == R.id.tv_load) {
            upIdInfo();
        }else if(view.getId()==R.id.tv_zm||view.getId()==R.id.iv_zm){
            identityPositive();
        }else if(view.getId()==R.id.tv_fm||view.getId()==R.id.iv_fm){
            identityBack();
        }
    }

    @Override
    public void initData() {
        name = getIntent().getStringExtra("name");
        number = getIntent().getStringExtra("number");
         status = getIntent().getStringExtra("status");
        path01 = getIntent().getStringExtra("fontImage");
        path02 = getIntent().getStringExtra("reveserImage");
        if ("1".equals(status + "")) {
            mTextViewLoad.setVisibility(View.GONE);
        }
        if(!TextUtils.isEmpty(path01)){
            Glide.with(this.getApplicationContext()).asBitmap().load(path01).into(ivZm);
            tvZm.setVisibility(View.INVISIBLE);
        }  if(!TextUtils.isEmpty(path02)){
            Glide.with(this.getApplicationContext()).asBitmap().load(path02).into(ivFm);
            tvFm.setVisibility(View.INVISIBLE);
        }
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
                    loadUrl(new File(path));
                }
            }


        } else {//REQUEST_BACK
            if (data != null) {
                String path = data.getStringExtra("result");
                if (!TextUtils.isEmpty(path)) {
                    loadUrl(new File(path));
                }
            }
        }

    }

    //文件上传
    private void loadUrl(final File file) {
        Map<String, Object> map = new HashMap<>();
        map.put("file", file);
        map.put("type", "head-portrait");
        mSpinKitView.setVisibility(View.VISIBLE);
        OkHttp3Utils.getInstance(User.BASE).upLoadFile3(User.fileUpload, map, new GsonObjectCallback<String>(User.BASE) {
            @Override
            public void onUi(String result) {
                mSpinKitView.setVisibility(View.GONE);
                System.out.println("result = [" + result + "]");
                try {
                    JSONObject object = new JSONObject(result);
                    JSONObject dataObj = object.optJSONObject("data");
                    String url = dataObj.optString("url");
                    if (StringUtils.isEmpty(url)) return;
                    if (isFont) {
                        path01 = url;
                        ivZm.setImageBitmap(BitmapFactory.decodeFile(file.getAbsolutePath()));
                    } else {
                        path02 = url;
                        ivFm.setImageBitmap(BitmapFactory.decodeFile(file.getAbsolutePath()));
                    }
                } catch (JSONException e) {
                    //  e.printStackTrace();
                    ToastUtil.showShort(UpLoadIdentifyActivity.this, "上传文件失败");
                }
            }

            @Override
            public void onFailed(Call call, IOException e) {
                mSpinKitView.setVisibility(View.GONE);
                System.out.println("call = [" + call + "], e = [" + e + "]");
            }
        });
    }

    /**
     * 拍摄证件照片
     *
     * @param type 拍摄证件类型
     */
    private void takePhoto(int type) {
        if (ActivityCompat.checkSelfPermission(UpLoadIdentifyActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(UpLoadIdentifyActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 0x12);
            return;
        }
        CameraActivity.navToCamera(this, type, 0);
    }

    /**
     * 身份证正面
     */
    public void identityPositive() {
        if("1".equals(status+"")){
            ToastUtil.showShort(UpLoadIdentifyActivity.this,"您已通过实名认证");
            return;
        }
        isFont = true;
        takePhoto(CameraActivity.TYPE_ID_CARD_FRONT);
    }

    /**
     * 身份证反面
     */
    public void identityBack() {
        if("1".equals(status+"")){
            ToastUtil.showShort(UpLoadIdentifyActivity.this,"您已通过实名认证");
            return;
        }
        isFont = false;
        takePhoto(CameraActivity.TYPE_ID_CARD_BACK);
    }


    @Override
    protected void onResume() {
        /**
         * 设置为横屏
         */
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        super.onResume();
    }


    private void upIdInfo() {
        Map<String, String> map = new HashMap<>();
        map.put("identityName", name);
        map.put("identityNumber", number);
        map.put("frontalPic", path01);
        map.put("reversePic", path02);
        String str = JsonUtil.obj2String(map);
        String token = (String) SPUtils.instance(this, 1).getkey("token", "");
        OkHttp3Utils.getInstance(Mall.BASE).doPostJson2(User.uploadIdentiy, str, token, new GsonObjectCallback<String>(Mall.BASE) {

            @Override
            public void onUi(String result) {
                try {
                    JSONObject object = new JSONObject(result);
                    if (object.optInt("code") == 0) {
                        ToastUtil.showShort(UpLoadIdentifyActivity.this, "提交成功");
                        finish();
                    } else {
                        ToastUtil.showShort(UpLoadIdentifyActivity.this, TextUtil.checkStr2Str(object.optString("msg")));
                    }

                } catch (JSONException e) {
                    ToastUtil.showShort(UpLoadIdentifyActivity.this, "数据异常，请重试再试！");
                }
            }

            @Override
            public void onFailed(Call call, IOException e) {
                ToastUtil.showShort(UpLoadIdentifyActivity.this, "提交失败，请稍后重试！");
            }
        });
    }
}
