package com.tzl.agriculture.fragment.personal.activity.set;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;

import com.facebook.drawee.view.SimpleDraweeView;
import com.rey.material.app.BottomSheetDialog;
import com.tzl.agriculture.R;
import com.tzl.agriculture.model.UserInfo;
import com.tzl.agriculture.util.BasisTimesUtils;
import com.tzl.agriculture.util.CameraUtil;
import com.tzl.agriculture.util.JsonUtil;
import com.tzl.agriculture.util.SPUtils;
import com.tzl.agriculture.util.TextUtil;
import com.tzl.agriculture.util.ToastUtil;
import com.tzl.agriculture.util.UserData;

import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import Utils.GsonObjectCallback;
import Utils.OkHttp3Utils;
import butterknife.BindView;
import config.User;
import okhttp3.Call;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

/**
 * 用户基本信息页面
 */
public class UserMessActivity extends SetBaseActivity implements View.OnClickListener {

    @BindView(R.id.ll_head)
    LinearLayout llHead;

    @BindView(R.id.ll_nick)
    LinearLayout llNick;

    @BindView(R.id.ll_sex)
    LinearLayout llSex;

    @BindView(R.id.drawee_img)
    SimpleDraweeView draweeView;

    @BindView(R.id.tv_userName)
    TextView tvUserName;

    @BindView(R.id.tv_nickName)
    TextView tvNickName;

    @BindView(R.id.tv_sex)
    TextView tvSex;

    //出生日期
    @BindView(R.id.tv_date)
    TextView tvDate;


    private UserInfo userInfo;

    private BottomSheetDialog dialog;

    @Override
    public void backFinish() {
        finish();
    }

    @Override
    public int setLayout() {
        return R.layout.activity_user_mess;
    }

    @Override
    public void initView() {
        String str = (String) SPUtils.instance(this, 1).getkey("user", "");
        setTitle("基本信息");
        llNick.setOnClickListener(this);
        llHead.setOnClickListener(this);


        userInfo = JsonUtil.string2Obj(str, UserInfo.class);

        if (null != userInfo) {
            tvNickName.setText(userInfo.getNickname());
            tvUserName.setText(userInfo.getUsername());
            draweeView.setImageURI(userInfo.getHeadUrl());
            tvSex.setText(userInfo.getSex() == 1 ? "男" : "女");
            tvDate.setText(TextUtil.checkStr2Str(userInfo.getBirthday()));
        }

        tvDate.setOnClickListener(this);
        llSex.setOnClickListener(this);
    }

    @Override
    public void initData() {
        myRequetPermission();
    }

    private void myRequetPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        } else {
            //Toast.makeText(this,"您已经申请了权限!",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_head:
                showBottomDialog();
                break;
            case R.id.ll_nick:
                Intent intent = new Intent(this, SetTextActivity.class);
                intent.putExtra("name", tvNickName.getText().toString());
                startActivityForResult(intent, 100);
                break;
            case R.id.ll_sex:
                showBottomDialogCs();
                break;
            case R.id.tv_date:
                showDatePickerDialog();
                break;
            default:
                break;
        }
    }

    //头像上传选择
    private void showBottomDialog() {
        dialog = new BottomSheetDialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.choos_cam_pho, null);
        view.findViewById(R.id.takePhoto).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isCheckPerimiss()) {
                    return;
                }
                openCamera();//拍照
                dialog.dismiss();
            }
        });
        view.findViewById(R.id.openPhotos).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAlbum();//相册
                dialog.dismiss();
            }
        });
        view.findViewById(R.id.iv_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.contentView(view)
                .inDuration(200)
                .outDuration(200)
                .cancelable(true)
                .show();
    }

    //请检擦权限
    private boolean isCheckPerimiss() {
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.CAMERA) != PermissionChecker.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.CAMERA},
                    3);

            return false;
        }
        return true;
    }

    private String sexStr = "";

    //性别选择
    private void showBottomDialogCs() {
        dialog = new BottomSheetDialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_sex, null);
        TextView tvNan = view.findViewById(R.id.tv_sex_nan);
        TextView tvNv = view.findViewById(R.id.tv_sex_nv);
        TextView tvBm = view.findViewById(R.id.tv_sex_bm);

        if (userInfo.getSex() == 1) {//男
            tvNan.setBackgroundResource(R.drawable.shape_side_white);
        } else if (userInfo.getSex() == 0) {//女
            tvNv.setBackgroundResource(R.drawable.shape_side_white);
        } else {
            tvBm.setBackgroundResource(R.drawable.shape_side_white);
        }

        tvNan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvNan.setBackgroundResource(R.drawable.shape_side_white);
                tvNv.setBackgroundResource(R.color.colorW);
                tvBm.setBackgroundResource(R.color.colorW);
                sexStr = "1";
            }
        });
        tvNv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvNv.setBackgroundResource(R.drawable.shape_side_white);
                tvBm.setBackgroundResource(R.color.colorW);
                tvNan.setBackgroundResource(R.color.colorW);
                sexStr = "0";
            }
        });
        tvBm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvBm.setBackgroundResource(R.drawable.shape_side_white);
                tvNv.setBackgroundResource(R.color.colorW);
                tvNan.setBackgroundResource(R.color.colorW);
                sexStr = "2";
            }
        });

        view.findViewById(R.id.tv_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                tvSex.setText(Integer.parseInt(sexStr) == 1 ? "男" : "女");
                UserData.instance(UserMessActivity.this).updateUserInfo(getToken(), 4, sexStr);
            }
        });
        dialog.contentView(view)
                .inDuration(200)
                .outDuration(200)
                .cancelable(true)
                .show();

    }

    //日期选择
    private void showDatePickerDialog() {
        BasisTimesUtils.showDatePickerDialog(this, true, "", 2019, 1, 1,
                new BasisTimesUtils.OnDatePickerListener() {

                    @Override
                    public void onConfirm(int year, int month, int dayOfMonth) {
                        UserData.instance(UserMessActivity.this).updateUserInfo(getToken(), 5, year + "-" + month + "-" + dayOfMonth);
                        tvDate.setText(year + "-" + month + "-" + dayOfMonth);
                    }

                    @Override
                    public void onCancel() {

                    }
                }).setDayGone();
    }


    private static final int REQ_1 = 1;//打开相机
    private static final int REQ_2 = 2;//打开相册
    private static final int REQ_4 = 4;//相册后启动裁剪程序
    private static final String IMAGE_FILE_LOCATION = "file:///" + Environment.getExternalStorageDirectory().getPath() + "/temp.jpg";
    private Uri imageUris = Uri.parse(IMAGE_FILE_LOCATION);
    private Uri imageUri;
    public static File tempFile;
    private Uri headUri;
    private Bitmap bitmapCamera;//截切后的bitmap

    //打开相册方法
    private void openAlbum() {
        try {
            Intent intent = new Intent(Intent.ACTION_PICK, null);
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
            startActivityForResult(intent, REQ_2);
        } catch (Throwable t) {
            ToastUtil.showShort(this, "该设备不支持相册功能");
        }

    }


    /**
     * 打开相机方法
     */
    private void openCamera() {
        //獲取系統版本
        int currentapiVersion = Build.VERSION.SDK_INT;
        // 激活相机
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // 判断存储卡是否可以用，可用进行存储
        if (CameraUtil.hasSdcard()) {
            SimpleDateFormat timeStampFormat = new SimpleDateFormat(
                    "yyyy_MM_dd_HH_mm_ss");
            String filename = timeStampFormat.format(new Date());
            tempFile = new File(Environment.getExternalStorageDirectory(),
                    filename + ".jpg");
            if (currentapiVersion < 24) {
                // 从文件中创建uri
                imageUri = Uri.fromFile(tempFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            } else {
                //兼容android7.0 使用共享文件的形式
                ContentValues contentValues = new ContentValues(1);
                contentValues.put(MediaStore.Images.Media.DATA, tempFile.getAbsolutePath());
                imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            }
        }
        startActivityForResult(intent, REQ_1);
    }

    //剪切、压缩后的方法
    public void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // 下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true");
        //该参数可以不设定用来规定裁剪区的宽高比
        intent.putExtra("aspectX", 2);
        intent.putExtra("aspectY", 2);
        //该参数设定为你的imageView的大小
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        intent.putExtra("scale", true);
        //是否返回bitmap对象
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUris);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());//输出图片的格式
        intent.putExtra("noFaceDetection", false); // 头像识别
        startActivityForResult(intent, REQ_4);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 100) {
            if (resultCode == 200) {
                if (data != null) {
                    tvNickName.setText(data.getStringExtra("name"));
                }
            }
        }

        switch (requestCode) {
            case REQ_1: {
                if (resultCode == RESULT_OK) {
                    Intent intent = new Intent("com.android.camera.action.CROP");
                    intent.setDataAndType(imageUri, "image/*");
                    intent.putExtra("scale", true);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    startPhotoZoom(imageUri);
                }
                break;
            }
            case REQ_2: {
                if (resultCode == RESULT_OK) {
                    startPhotoZoom(data.getData());
                }
                break;
            }
            case REQ_4: {
                if (resultCode == RESULT_OK) {
                    try {
                        bitmapCamera = BitmapFactory.decodeStream(getContentResolver()
                                .openInputStream(imageUris));
                        this.headUri = imageUris;

                        Uri uri = bitmap2uri(UserMessActivity.this, bitmapCamera);
                        draweeView.setImageURI(uri);

                        if (bitmapCamera != null) {
                            File file = null;   //图片地址
                            try {
                                file = new File(new URI(headUri.toString()));
                            } catch (URISyntaxException e) {
                                e.printStackTrace();
                            }
                            loadUrl(file);
                        }


                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            }
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    public Uri bitmap2uri(Context c, Bitmap b) {
        File path = new File(c.getCacheDir() + File.separator + System.currentTimeMillis() + ".jpg");
        try {
            OutputStream os = new FileOutputStream(path);
            b.compress(Bitmap.CompressFormat.JPEG, 100, os);
            os.close();
            return Uri.fromFile(path);
        } catch (Exception ignored) {
        }
        return null;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PERMISSION_GRANTED) {
                } else {
                    Toast.makeText(this, "申请失败", Toast.LENGTH_SHORT).show();
                }
                break;
            case 2:
                if (grantResults.length > 0 && grantResults[0] == PERMISSION_GRANTED) {
                } else {
                    Toast.makeText(this, "申请失败", Toast.LENGTH_SHORT).show();
                }
                break;
            case 3:
                if (grantResults.length > 0 && grantResults[0] != PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(
                            this,
                            Manifest.permission.CAMERA)) {
                        ActivityCompat.requestPermissions(
                                this,
                                new String[]{Manifest.permission.CAMERA},
                                3);
                    } else {
                        //跳去打开权限
                        Intent intent = new Intent();
                        intent.setAction(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.setData(Uri.parse("package:" + this.getPackageName()));
                        startActivity(intent);
                    }
                }
                break;
        }
    }


    private void loadUrl(final File file) {
        Map<String, Object> map = new HashMap<>();
        map.put("file", file);
        map.put("type", "head-portrait");
        OkHttp3Utils.getInstance(User.BASE).upLoadFile3(User.fileUpload, map, new GsonObjectCallback<String>(User.BASE) {
            @Override
            public void onUi(String result) {
                try {
                    JSONObject object = new JSONObject(result);
                    JSONObject dataObj = object.optJSONObject("data");
                    String url = dataObj.optString("url");
                    if (StringUtils.isEmpty(url)) return;
                    SPUtils.instance(UserMessActivity.this,1).put("user_head_url",url);
                    UserData.instance(UserMessActivity.this).updateUserInfo(getToken(), 1, url);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(Call call, IOException e) {

            }
        });
    }
}
