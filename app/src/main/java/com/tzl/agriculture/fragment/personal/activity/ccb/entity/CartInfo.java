package com.tzl.agriculture.fragment.personal.activity.ccb.entity;

import android.text.TextUtils;

import java.util.List;

/**
 *
 * 购物车
 */

public class CartInfo {

    private List<DataBean> shopList;

    public List<DataBean> getData() {
        return shopList;
    }

    public void setData(List<DataBean> data) {
        this.shopList = data;
    }

    public static class DataBean {
        private String shopId; //购物车id
        private String shopName; //店铺名称
        private List<ItemsBean> goodsList; //店铺商品列表
        private boolean ischeck=false;

        public boolean ischeck() {
            return ischeck;
        }

        public void setIscheck(boolean ischeck) {
            this.ischeck = ischeck;
        }

        public String getShop_id() {
            return shopId;
        }

        public void setShop_id(String shop_id) {
            this.shopId = shop_id;
        }

        public String getShop_name() {
            return shopName;
        }

        public void setShop_name(String shop_name) {
            this.shopName = shop_name;
        }

        public List<ItemsBean> getItems() {
            return goodsList;
        }

        public void setItems(List<ItemsBean> items) {
            this.goodsList = items;
        }

        public static class ItemsBean {
            private String goodsId; //商品id
            private String specifications;
            private String picUrl;//图片
            private String price;//价钱
            private String totalPrice;//总价格
            private String goodsName;//标题
            private String cartId;//购物车id
            private boolean ischeck=false;
            private String number="1";
            private String stock="1";

            public int getStock() {
                if(TextUtils.isEmpty(stock)){
                    return 1;
                }
                return Integer.valueOf(stock);
            }

            public void setStock(String stockArp) {
                stock = stockArp;
            }

            public int getCartId() {
                if(TextUtils.isEmpty(cartId)){
                    return 0;
                }
                return Integer.valueOf(cartId);
            }

            public void setCartId(String cartIdArp) {
                cartId = cartIdArp;
            }

            public String getTotalPrice() {
                return totalPrice;
            }

            public void setTotalPrice(String totalPriceArp) {
                totalPrice = totalPriceArp;
            }

            public int getNum() {
                if(TextUtils.isEmpty(number)){
                    return 1;
                }
                return Integer.valueOf(number);
            }

            public void setNum(String num) {
                this.number = num;
            }

            public boolean ischeck() {
                return ischeck;
            }

            public void setIscheck(boolean ischeck) {
                this.ischeck = ischeck;
            }

            public String getItemid() {
                return goodsId;
            }

            public void setItemid(String itemid) {
                this.goodsId = itemid;
            }

            public String getSpecifications() {
                return specifications;
            }

            public void setSpecifications(String quantity) {
                this.specifications = quantity;
            }

            public String getImage() {
                return picUrl;
            }

            public void setImage(String image) {
                this.picUrl = image;
            }

            public String getPrice() {
                return price;
            }

            public void setPrice(String price) {
                this.price = price;
            }

            public String getTitle() {
                return goodsName;
            }

            public void setTitle(String title) {
                this.goodsName = title;
            }
        }
    }
}
