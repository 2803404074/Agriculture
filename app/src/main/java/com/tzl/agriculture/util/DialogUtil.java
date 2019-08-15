package com.tzl.agriculture.util;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tzl.agriculture.R;
import com.tzl.agriculture.fragment.personal.login.activity.LoginActivity;
import com.tzl.agriculture.main.MainActivity;
import com.tzl.agriculture.model.AppVersion;
import com.tzl.agriculture.view.BaseAdapter;
import com.tzl.agriculture.view.BaseRecyclerHolder;

import java.io.File;
import java.io.IOException;

import Utils.DownloadUtil;
import Utils.OkHttp3Utils;
import config.App;

import static androidx.core.content.PermissionChecker.PERMISSION_GRANTED;

public class DialogUtil {

    private static DialogUtil dialogUtil;

    private AlertDialog dialog;

    private Context context;

    public Context getContext() {
        return context;
    }

    public DialogUtil(Context context) {
        this.context = context;
    }

    public static DialogUtil init(Context context) {
        if (dialogUtil == null) {
            dialogUtil = new DialogUtil(context);
        }
        return dialogUtil;
    }

    /**
     * @param layout 该布局需要有一个iv_close id，用于通用关闭
     */
    public void show(int layout) {
        View view = LayoutInflater.from(context).inflate(layout, null, false);
        view.findViewById(R.id.iv_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog = new AlertDialog.Builder(context, R.style.TransparentDialog).setView(view).create();
        dialog.show();
        //此处设置位置窗体大小，我这里设置为了手机屏幕宽度的3/4
        dialog.getWindow().setLayout((ScreenUtils.getScreenWidth(context) / 4 * 3), LinearLayout.LayoutParams.WRAP_CONTENT);
    }


    public void showVersion(int layout, boolean must, AppVersion appVersion) {
        View view = LayoutInflater.from(context).inflate(layout, null, false);
        view.findViewById(R.id.iv_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        //版本
        TextView tvVersionCode = view.findViewById(R.id.tv_versionCode);
        tvVersionCode.setText("发现新版本\tV" + appVersion.getVersionInfo());

        //更新内容
        RecyclerView recyclerView = view.findViewById(R.id.recy);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        BaseAdapter adapter = new BaseAdapter<String>(context, recyclerView, appVersion.getRemarkList(), R.layout.item_text) {
            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder, String o) {
                TextView tvTips = holder.getView(R.id.tv_tips);
                tvTips.setText(o);
                tvTips.setTextColor(context.getResources().getColor(R.color.colorGri2));
            }
        };
        recyclerView.setAdapter(adapter);

        //暂不更新
        TextView tvNoUpdate = view.findViewById(R.id.tv_noUpdate);
        tvNoUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                des();
            }
        });
        if (must) {//是否必须
            tvNoUpdate.setVisibility(View.GONE);
        } else {
            tvNoUpdate.setVisibility(View.VISIBLE);
        }

        //立即更新
        TextView tvStartUpdate = view.findViewById(R.id.tv_startUpdate);

        tvStartUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                ProgressDialog pd = new ProgressDialog(context);
                pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                pd.setMessage("正在下载更新");
                pd.show();
                pd.setCancelable(false);
                pd.setCanceledOnTouchOutside(false);
                DownloadUtil.get().download(appVersion.getDownUrl(), context.getCacheDir().getAbsolutePath(), "qwxc.apk", new DownloadUtil.OnDownloadListener() {
                    @Override
                    public void onDownloadSuccess(File file) {
                        if (pd != null && pd.isShowing()) {
                            pd.dismiss();
                        }
                        //下载完成进行相关逻辑操作
                        Intent intent = new Intent();
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setAction(Intent.ACTION_VIEW);

                        if (Build.VERSION.SDK_INT >= 24) { // Android7.0及以上版本 Log.d("-->最新apk下载完毕","Android N及以上版本");
                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                            Uri contentUri = FileProvider.getUriForFile(context, "com.tzl.agriculture" + ".fileprovider", file);
                            //参数二:应用包名+".fileProvider"(和步骤二中的Manifest文件中的provider节点下的authorities对应)
                            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");

                        } else {
                            // Android7.0以下版本 Log.d("-->最新apk下载完毕","Android N以下版本");
                            try {
                                Runtime.getRuntime().exec("chmod 777 " + file.getCanonicalPath());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            intent.setDataAndType(Uri.fromFile(file),"application/vnd.android.package-archive");
                        }

                        context.startActivity(intent);
                    }

                    @Override
                    public void onDownloading(int progress) {
                        pd.setProgress(progress);
                    }

                    @Override
                    public void onDownloadFailed(Exception e) {
                        //下载异常进行相关提示操作
                        Log.e("qwxc", e.getMessage());
                    }
                });

            }
        });

        dialog = new AlertDialog.Builder(context, R.style.TransparentDialog).setView(view).create();
        dialog.setCancelable(false);
        dialog.show();
        //此处设置位置窗体大小，我这里设置为了手机屏幕宽度的3/4
        dialog.getWindow().setLayout((ScreenUtils.getScreenWidth(context) / 4 * 3), LinearLayout.LayoutParams.WRAP_CONTENT);
    }


    /**
     * 普通弹窗，不做逻辑
     */
    public void showTips() {

        dialog = new AlertDialog.Builder(context).setIcon(R.mipmap.application).setTitle("趣乡服务")
                .setMessage("敬请期待").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).create();
        dialog.show();
    }

    /**
     * 逻辑提示，点击控制
     * @param index 0 登陆超时
     */
    public void showTipsForControl(int index) {
        dialog = new AlertDialog.Builder(context).setIcon(R.mipmap.application).setTitle("趣乡服务")
                .setMessage("敬请期待").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (index == 0){
                            dialogInterface.dismiss();
                            Intent intent = new Intent(getContext(), LoginActivity.class);
                            context.startActivity(intent);
                            MainActivity.instance.finish();
                        }
                    }
                }).create();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }


    public void showTipsForControl(String title) {
        dialog = new AlertDialog.Builder(context).setIcon(R.mipmap.application).setTitle("趣乡服务")
                .setMessage(title).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            Intent intent = new Intent(getContext(), LoginActivity.class);
                            context.startActivity(intent);
                            MainActivity.instance.finish();
                    }
                }).create();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }


    public void des() {
        if (dialogUtil != null) {
            dialogUtil = null;
        }
        if (dialog != null) {
            dialog.cancel();
            dialog = null;
        }
    }
}
