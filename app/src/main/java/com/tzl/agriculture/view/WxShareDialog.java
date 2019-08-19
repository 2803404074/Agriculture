package com.tzl.agriculture.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.core.content.ContextCompat;

import com.tzl.agriculture.R;
import com.tzl.agriculture.util.ShareUtils;

import cn.sharesdk.framework.PlatformActionListener;

public class WxShareDialog extends Dialog implements View.OnClickListener {
    private Context mContext;
    private String title,text,imageUrl,linkUrl;
    private PlatformActionListener lister;

    public WxShareDialog(Context context) {
        super(context);
        this.mContext=context;
        View view= LayoutInflater.from(context).inflate(R.layout.wx_share_dialog,null,false);
        setContentView(view);
        view.findViewById(R.id.wx_c).setOnClickListener(this);
        view.findViewById(R.id.wx_s).setOnClickListener(this);
        Window window = getWindow();
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.height=WindowManager.LayoutParams.WRAP_CONTENT;
        attributes.width=WindowManager.LayoutParams.MATCH_PARENT;
        attributes.gravity= Gravity.BOTTOM;
        window.setAttributes(attributes);
        window.setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(mContext,R.color.colorWhite)));
    }

    @Override
    public void onClick(View viewArp) {
        if(viewArp.getId()==R.id.wx_s){
            ShareUtils.getInstance(mContext).startShareWX(mContext,title,text,imageUrl,linkUrl,lister);
        }else{
            ShareUtils.getInstance(mContext).startShareWXcomtent(mContext,title,text,imageUrl,linkUrl,lister);
        }
        dismiss();
    }


    public void showShareDialog(String title,String text,String imageUrl,String linkUrl,PlatformActionListener lister){
        this.title=title;
        this.text=text;
        this.imageUrl=imageUrl;
        this.linkUrl=linkUrl;
        this.lister=lister;
        show();
    }
}
