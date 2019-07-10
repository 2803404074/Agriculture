package com.tzl.agriculture.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.tzl.agriculture.R;

import java.util.ArrayList;
import java.util.List;

public class ShareDialog {
    private Dialog mDialog;
    //取消分享
    private TextView txtCancle;
    private OnClickListener mOnCancleListener;
    //微信分享
    private LinearLayout mWeChatShare;
    private OnClickListener mOnWeChatShareListener;
    //微信朋友圈分享
    private LinearLayout mWeChatFriendShare;
    private OnClickListener mOnWeChatFriendShareListener;
    //QQ分享
    private LinearLayout mQQShare;
    private OnClickListener mOnQQShareListener;
    private Context mContext;
    private Display display;

    public ShareDialog(Context context) {
        mContext = context;
        //获取屏幕对象
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        display = windowManager.getDefaultDisplay();
    }

    //设置微信分享
    public ShareDialog setOnWeChatShare(OnClickListener weChatShareListener) {
        mOnWeChatShareListener = weChatShareListener;
        return this;
    }

    //设置分享朋友圈
    public ShareDialog setOnWeChatFriendShare(OnClickListener weChatFriendShareListener) {
        mOnWeChatFriendShareListener = weChatFriendShareListener;
        return this;
    }

    //QQ分享
    public ShareDialog setOnQQShare(OnClickListener qqShareListener) {
        mOnQQShareListener = qqShareListener;
        return this;
    }

    public ShareDialog setOnCancleListener(OnClickListener cancleListener) {
        mOnCancleListener = cancleListener;
        return this;
    }

    public void show() {
        mDialog.show();
    }

    public void dismiss() {
        mDialog.dismiss();
    }

    /**
     * 创建BaseDialog实例
     *
     * @return
     */
    public ShareDialog builder() {

        List<ShareItem> mData = new ArrayList<>();
        mData.add(new ShareItem("微信", R.mipmap.wechat));
        mData.add(new ShareItem("微信朋友圈", R.mipmap.wechat));
        mData.add(new ShareItem("QQ", R.mipmap.wechat));
        mData.add(new ShareItem("QQ空间", R.mipmap.wechat));
        mData.add(new ShareItem("新浪微博", R.mipmap.wechat));

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.share_dialog, null);
        //设置弹出框横向铺满整个屏幕
        view.setMinimumWidth(display.getWidth());
        mDialog = new Dialog(mContext, R.style.ActionSheetDialogStyle);
        //设置dialog弹出后会点击屏幕，dialog不消失；点击物理返回键dialog消失
        mDialog.setCanceledOnTouchOutside(true);
        Window dialogWindow = mDialog.getWindow();
        dialogWindow.setGravity(Gravity.CENTER | Gravity.BOTTOM);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.x = 0;
        lp.y = 0;
        dialogWindow.setAttributes(lp);
        //设置点击隐藏
        txtCancle = (TextView) view.findViewById(R.id.txtCancle);
        txtCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnCancleListener != null) {
                    mOnCancleListener.onClick(mDialog, Dialog.BUTTON_NEGATIVE);
                }
                dismiss();
            }
        });

        RecyclerView recyclerView = view.findViewById(R.id.rlv);
        LinearLayoutManager manager = new LinearLayoutManager(mContext);
        manager.setOrientation(RecyclerView.HORIZONTAL);
        recyclerView.setLayoutManager(manager);


        BaseAdapter adapter = new BaseAdapter<ShareItem>(mContext, recyclerView, mData, R.layout.share_item) {
            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder, ShareItem o) {
                holder.setText(R.id.tv_name, o.getName());
                holder.setImageResource(R.id.iv_img, o.getResources());
            }
        };

        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new BaseAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                switch (position) {
                    //微信分享
                    case 0:
                        if (mOnWeChatShareListener != null) {
                            mOnWeChatShareListener.onClick(mDialog, Dialog.BUTTON_POSITIVE);
                        }
                        dismiss();
                        break;
                    //朋友圈分享
                    case 1:
                        if (mOnWeChatFriendShareListener != null) {
                            mOnWeChatFriendShareListener.onClick(mDialog, Dialog.BUTTON_POSITIVE);
                        }
                        dismiss();
                        break;
                    //QQ分享
                    case 2:
                        if (mOnQQShareListener != null) {
                            mOnQQShareListener.onClick(mDialog, Dialog.BUTTON_POSITIVE);
                        }
                        break;
                    case 3:
                        break;
                    case 4:
                        break;
                }

            }
        });
        mDialog.setContentView(view);
        return this;
    }
    protected class ShareItem {
        private String name;
        private int resources;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getResources() {
            return resources;
        }

        public void setResources(int resources) {
            this.resources = resources;
        }

        public ShareItem(String name, int resources) {
            this.name = name;
            this.resources = resources;
        }
    }
}
