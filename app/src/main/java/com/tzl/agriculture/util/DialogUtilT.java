package com.tzl.agriculture.util;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tzl.agriculture.R;
import com.tzl.agriculture.model.AppVersion;
import com.tzl.agriculture.view.BaseAdapter;
import com.tzl.agriculture.view.BaseRecyclerHolder;

import java.io.File;
import java.io.IOException;

import Utils.DownloadUtil;

/**
 * 弹窗，多类型
 * @param <T>
 */
public abstract class DialogUtilT<T> extends DialogUtil{
    private AlertDialog dialog;

    public DialogUtilT(Context context) {
        super(context);
    }

    /**
     * @param layout 该布局需要有一个iv_close id，用于通用关闭
     */
    public void show2(int layout,T data) {
        View view = LayoutInflater.from(getContext()).inflate(layout, null, false);
        BaseRecyclerHolder holder = BaseRecyclerHolder.getRecyclerHolder(getContext(), view);
        convert(holder,data);
        view.findViewById(R.id.iv_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog = new AlertDialog.Builder(getContext(), R.style.TransparentDialog).setView(view).create();

        dialog.show();
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = (ScreenUtils.getScreenWidth(getContext()) / 4 * 3);
        params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(params);
        dialog.getWindow().setWindowAnimations(R.style.dialog_animation);
    }

    public void setDialogWidth(float w){
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = (int) (ScreenUtils.getScreenWidth(getContext()) *w);
        params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(params);
        dialog.getWindow().setWindowAnimations(R.style.dialog_animation);
    }


    /**
     *
     * @param layout
     * @param data
     * @param x   x * y
     * @param y
     */
    public void show2(int layout,T data,int x,int y) {
        View view = LayoutInflater.from(getContext()).inflate(layout, null, false);
        BaseRecyclerHolder holder = BaseRecyclerHolder.getRecyclerHolder(getContext(), view);
        convert(holder,data);
        view.findViewById(R.id.iv_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog = new AlertDialog.Builder(getContext(), R.style.TransparentDialog).setView(view).create();

        dialog.show();
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = (ScreenUtils.getScreenWidth(getContext()) / x * y);
        params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(params);
        dialog.getWindow().setWindowAnimations(R.style.dialog_animation);
    }

    public void des(){
        if (dialog != null){
            dialog.dismiss();
            dialog = null;
        }
    }


    public abstract void convert(BaseRecyclerHolder holder,T data);
}
