package com.tzl.agriculture.fragment.personal.activity.set;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tzl.agriculture.R;
import com.tzl.agriculture.mall.activity.OrderActivity;
import com.tzl.agriculture.model.AddressMo;
import com.tzl.agriculture.util.JsonUtil;
import com.tzl.agriculture.util.SPUtils;
import com.tzl.agriculture.util.ScreenUtils;
import com.tzl.agriculture.util.TextUtil;
import com.tzl.agriculture.util.ToastUtil;
import com.tzl.agriculture.view.BaseAdapter;
import com.tzl.agriculture.view.BaseRecyclerHolder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import Utils.GsonObjectCallback;
import Utils.OkHttp3Utils;
import butterknife.BindView;
import config.Article;
import config.User;
import okhttp3.Call;

public class AddressActivity extends SetBaseActivity {

    @BindView(R.id.recy)
    RecyclerView recyclerView;

    @BindView(R.id.tv_add_address)
    TextView tvAdd;

    private BaseAdapter adapter;

    private List<AddressMo> mData = new ArrayList<>();



    @Override
    public int setLayout() {
        return R.layout.activity_address;
    }

    @Override
    public void initView() {
        setTitle("收货地址管理");
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        adapter = new BaseAdapter<AddressMo>(this, recyclerView, mData, R.layout.item_address) {
            @Override
            public void convert(Context mContext, BaseRecyclerHolder holder, int position,AddressMo o) {
                if (o.getIsDefault() == 1) {
                    holder.setText(R.id.tv_Mr, "默认");
                }
                holder.setText(R.id.tv_name, o.getReceiptName());
                holder.setText(R.id.tv_phone, o.getReceiptPhone());
                String address = o.getProvinceStr() + o.getCityStr() + o.getAreaStr() + o.getStreetStr() + o.getAddress();
                holder.setText(R.id.tv_address, address);
                holder.getView(R.id.iv_edit).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showAlert(o);
                    }
                });
            }
        };
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new BaseAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                AddressMo addressMo = (AddressMo) adapter.getData().get(position);
                Intent intent = new Intent(AddressActivity.this, AddressSetActivity.class);
                intent.putExtra("mess", JsonUtil.obj2String(addressMo));
                startActivityForResult(intent, 100);
            }
        });

        tvAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddressActivity.this, AddressSetActivity.class);
                startActivityForResult(intent, 100);
            }
        });
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (getIntent().getIntExtra("type", 0) == 1) {
            Intent intent = new Intent(this, OrderActivity.class);
            setResultForOrderActivity(intent);
            setResult(RESULT_OK, intent);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void backFinish() {
        if (getIntent().getIntExtra("type", 0) == 1) {
            Intent intent = new Intent(this, OrderActivity.class);
            setResultForOrderActivity(intent);
            setResult(RESULT_OK, intent);
            finish();
        }else {
            finish();
        }
    }


    /**
     * 操作弹窗
     *
     * @param o
     */
    private void showAlert(AddressMo o) {
        View view = LayoutInflater.from(this).inflate(R.layout.alert_address_edit, null, false);
        final AlertDialog dialog = new AlertDialog.Builder(this).setView(view).create();

        TextView title = view.findViewById(R.id.tv_title);
        TextView tvDefault = view.findViewById(R.id.tv_default);
        TextView delete = view.findViewById(R.id.tv_delete);

        title.setText("您将进行");

        tvDefault.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                setDefault(o);
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mData.remove(o);
                adapter.updateData(mData);
                dialog.dismiss();
                addressDelete(o.getHarvestId());
            }
        });

        dialog.show();
        //此处设置位置窗体大小，我这里设置为了手机屏幕宽度的3/4  注意一定要在show方法调用后再写设置窗口大小的代码，否则不起效果会
        dialog.getWindow().setLayout((ScreenUtils.getScreenWidth(this) / 8 * 7), LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    /**
     * 设置默认地址
     */
    private void setDefault(AddressMo addressMo){
        OkHttp3Utils.getInstance(User.BASE).doPostJsonForObj(User.addDefault+addressMo.getHarvestId(), "", getToken(),
                new GsonObjectCallback<String>(User.BASE) {
            @Override
            public void onUi(String result) {
                try {
                    JSONObject object = new JSONObject(result);
                    if (object.optInt("code") == 0){
                        initData();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(Call call, IOException e) {

            }
        });
    }













    /**
     * 删除地址
     */
    private void addressDelete(String harvestId) {
        OkHttp3Utils.getInstance(Article.BASE).doPostJson2(User.deleteAddress + harvestId, "", getToken(), new GsonObjectCallback<String>(Article.BASE) {
            @Override
            public void onUi(String result) {
                try {
                    JSONObject object = new JSONObject(result);
                    if (object.optInt("code") == 0) {
                        ToastUtil.showShort(AddressActivity.this, "删除成功");
                    } else {
                        ToastUtil.showShort(AddressActivity.this, TextUtil.checkStr2Str(object.optString("网络繁忙")));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailed(Call call, IOException e) {

            }
        });
    }

    /**
     * 初始化地址列表
     */
    @Override
    public void initData() {
        String token = (String) SPUtils.instance(this, 1).getkey("token", "");
        OkHttp3Utils.getInstance(Article.BASE).doPostJson2(User.addressList, "", token, new GsonObjectCallback<String>(Article.BASE) {
            @Override
            public void onUi(String result) {
                try {
                    JSONObject object = new JSONObject(result);
                    if (object.optInt("code") == 0) {
                        JSONObject dataObj = object.optJSONObject("data");
                        String str = dataObj.optString("records");
                        mData = JsonUtil.string2Obj(str, List.class, AddressMo.class);
                        if (mData.size() == 1){
                            mData.get(0).setIsDefault(1);
                        }
                        adapter.updateData(mData);
                    } else {
                        ToastUtil.showShort(AddressActivity.this, TextUtil.checkStr2Str(object.optString("msg")));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(Call call, IOException e) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 100) {
                initData();
            }
        }
    }


    private void setResultForOrderActivity(Intent intent) {
        for (int i = 0; i < mData.size(); i++) {
            if (mData.get(i).getIsDefault() == 1) {
                intent.putExtra("addressId", mData.get(i).getHarvestId());
                intent.putExtra("name", mData.get(i).getReceiptName());
                intent.putExtra("phone", mData.get(i).getReceiptPhone());
                String str = TextUtil.checkStr2Str(mData.get(i).getProvinceStr())
                        + TextUtil.checkStr2Str(mData.get(i).getCityStr())
                        + TextUtil.checkStr2Str(mData.get(i).getStreetStr())
                        + TextUtil.checkStr2Str(mData.get(i).getAreaStr());
                intent.putExtra("address", str);
                break;
            }
        }
        setResult(RESULT_OK, intent);
    }
}
