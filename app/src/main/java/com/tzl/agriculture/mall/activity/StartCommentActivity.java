package com.tzl.agriculture.mall.activity;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.ybq.android.spinkit.SpinKitView;
import com.rey.material.app.BottomSheetDialog;
import com.tzl.agriculture.R;
import com.tzl.agriculture.fragment.personal.activity.set.SetBaseActivity;
import com.tzl.agriculture.util.CameraUtil;
import com.tzl.agriculture.util.JsonUtil;
import com.tzl.agriculture.util.SPUtils;
import com.tzl.agriculture.util.TextUtil;
import com.tzl.agriculture.util.ToastUtil;
import com.tzl.agriculture.util.Uri2StringPathUtil;
import com.tzl.agriculture.view.BaseAdapter;
import com.tzl.agriculture.view.BaseRecyclerHolder;

import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Utils.GsonObjectCallback;
import Utils.OkHttp3Utils;
import butterknife.BindView;
import config.Mall;
import config.User;
import okhttp3.Call;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class StartCommentActivity extends SetBaseActivity implements View.OnClickListener {

    @BindView(R.id.et_mess)
    EditText editText;

    @BindView(R.id.tv_send)
    TextView tvSend;

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    private List<String> mPath = new ArrayList<>();

    private BaseAdapter adapter;
    private String path;
    private int type;   //1 增加 2 修改  3删除
    private int position;

    @BindView(R.id.spin_kit)
    SpinKitView mSpinKitView;

    @Override
    public void backFinish() {
        finish();
    }

    @Override
    public int setLayout() {
        return R.layout.activity_start_comment;
    }

    @Override
    public void initView() {
        setTitle("发布评论");
        setAndroidNativeLightStatusBar(true);
        setStatusColor(R.color.colorW);
        myRequetPermission();
        isCheckPerimiss();
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

    //相册
    private boolean myRequetPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            return false;
        }
        return true;
    }


    @Override
    public void initData() {
        tvSend.setOnClickListener(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mPath.add("");
        adapter = new BaseAdapter<String>(this,
                mRecyclerView, mPath, R.layout.item_start_comment) {
            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder, int position, String o) {
                if (TextUtils.isEmpty(o)) {
                    holder.getView(R.id.text_add).setVisibility(View.VISIBLE);
                    holder.getView(R.id.image_path).setVisibility(View.GONE);
                    holder.getView(R.id.item_close).setVisibility(View.GONE);
                } else {
                    holder.getView(R.id.text_add).setVisibility(View.GONE);
                    holder.getView(R.id.image_path).setVisibility(View.VISIBLE);
                    holder.getView(R.id.item_close).setVisibility(View.VISIBLE);
                    holder.setImageByUrl(R.id.image_path, o);
                }
                //增加图片
                holder.getView(R.id.text_add).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View viewArp) {
                        StartCommentActivity.this.position = position;
                        StartCommentActivity.this.type = 1;
                        showBottomDialog();
                    }
                });
                //修改
                holder.getView(R.id.image_path).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View viewArp) {
                        StartCommentActivity.this.position = position;
                        StartCommentActivity.this.type = 2;
                        showBottomDialog();
                    }
                });
                //删除
                holder.getView(R.id.item_close).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View viewArp) {
                        StartCommentActivity.this.position = position;
                        StartCommentActivity.this.type = 3;
                        mPath.remove(position);
                        if (mPath.size() < 3 && !mPath.contains("")) {
                            mPath.add("");
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        };

        mRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_send:
                if (StringUtils.isEmpty(editText.getText().toString())) {
                    ToastUtil.showShort(this, "请填写您的评论内容");
                    return;
                }
                sendMess();
                break;
            default:
                break;
        }
    }

    private void sendMess() {
        List<String> m=new ArrayList<>();
        if (mPath.size() > 0) {
            for(int i=0;i<mPath.size();i++){
                if(!"".equals(mPath.get(i))){
                    m.add("\""+mPath.get(i)+"\"");
                }
            }
        }
        System.out.println(m.toString());
        Map<String, String> map = new HashMap<>();
        map.put("content", editText.getText().toString());
        map.put("imgUrl", m.toString());
        map.put("orderId", getIntent().getStringExtra("orderId"));
        String str = JsonUtil.obj2String(map);
        String token = (String) SPUtils.instance(this, 1).getkey("token", "");
        System.out.println(str);
        OkHttp3Utils.getInstance(Mall.BASE).doPostJson2(Mall.commentSave, str, token, new GsonObjectCallback<String>(Mall.BASE) {
            @Override
            public void onUi(String result) {
                try {
                    JSONObject object = new JSONObject(result);
                    if (object.optInt("code") == 0) {
                        ToastUtil.showShort(StartCommentActivity.this, "提交成功");
                        finish();
                    } else {
                        ToastUtil.showShort(StartCommentActivity.this, TextUtil.checkStr2Str(object.optString("msg")));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(Call call, IOException e) {

            }

            @Override
            public void onFailure(Call call, IOException e) {
                super.onFailure(call, e);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showShort(StartCommentActivity.this, "网络连接异常");
                    }
                });
            }
        });

    }


    //头像上传选择
    private BottomSheetDialog dialog;

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
                if (!myRequetPermission()) {
                    return;
                }
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

    private static final int REQ_1 = 1;//打开相机
    private static final int REQ_2 = 2;//打开相册
    private static final int REQ_4 = 4;//相册后启动裁剪程序
    private static final String IMAGE_FILE_LOCATION = "file:///" + Environment.getExternalStorageDirectory().getPath() + "/temp.jpg";
    private Uri imageUris = Uri.parse(IMAGE_FILE_LOCATION);
    private Uri imageUri;
    public static File tempFile;

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
                    imageUri = data.getData();
                    startPhotoZoom(data.getData());
                }
                break;
            }
            case REQ_4: {
                if (resultCode == RESULT_OK) {
                    path = Uri2StringPathUtil.getRealPathFromURI(this, imageUri);
                    loadUrl(new File(path));
                }
                break;
            }
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }


    //权限回调
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] != PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])) {
                ActivityCompat.requestPermissions(this, permissions, 2);
            } else {
                //跳去打开权限
                Intent intent = new Intent();
                intent.setAction(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.parse("package:" + this.getPackageName()));
                startActivity(intent);
            }
        }
    }

    //上传文件
    private void loadUrl(final File file) {
        mSpinKitView.setVisibility(View.VISIBLE);
        Map<String, Object> map = new HashMap<>();
        map.put("file", file);
        map.put("type", "head-portrait");
        OkHttp3Utils.getInstance(User.BASE).upLoadFile3(User.fileUpload, map, new GsonObjectCallback<String>(User.BASE) {
            @Override
            public void onUi(String result) {
                mSpinKitView.setVisibility(View.GONE);
                try {
                    JSONObject object = new JSONObject(result);
                    JSONObject dataObj = object.optJSONObject("data");
                    String url = dataObj.optString("url");
                    if (StringUtils.isEmpty(url)) return;
                    if (type == 1) {
                        mPath.remove(mPath.size() - 1);
                        mPath.add(url);
                        if (mPath.size() < 3) {
                            mPath.add("");
                        }
                    } else if (type == 2) {
                        mPath.set(position, url);
                    }
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(Call call, IOException e) {
                mSpinKitView.setVisibility(View.GONE);
                ToastUtil.showShort(StartCommentActivity.this, "异常数据");
            }
        });
    }

}
