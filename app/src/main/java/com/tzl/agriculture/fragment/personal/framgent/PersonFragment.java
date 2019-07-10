package com.tzl.agriculture.fragment.personal.framgent;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.tzl.agriculture.R;
import com.tzl.agriculture.view.BaseFragment;

import butterknife.BindView;
import butterknife.ButterKnife;


public class PersonFragment extends BaseFragment {

    @BindView(R.id.rv_menu)
    RecyclerView recyclerView;

    @BindView(R.id.drawee_img)
    SimpleDraweeView headView;

    @BindView(R.id.tv_nickName)
    TextView tvNickName;

    @BindView(R.id.tv_scNum)
    TextView tvScNum;

    @BindView(R.id.tv_cartNum)
    TextView tvCartNum;

    @BindView(R.id.tv_seeNum)
    TextView tvSeeNum;

    @BindView(R.id.tv_dfk)
    TextView tvDfk;

    @BindView(R.id.tv_dsh)
    TextView tvDsh;

    @BindView(R.id.tv_dpj)
    TextView tvDpj;

    @BindView(R.id.tv_sh)
    TextView tvSh;

    @BindView(R.id.tv_myOrder)
    TextView tvMyOrder;

    public static PersonFragment getInstance(){
        PersonFragment homeFragment = new PersonFragment();
        return homeFragment;
    }
    @Override
    protected int initLayout() {
        return R.layout.fragment_my;
    }
    @Override
    protected void initView(View view) {
        ButterKnife.bind(view);
    }

    @Override
    protected void initData(Context mContext) {

    }
}
