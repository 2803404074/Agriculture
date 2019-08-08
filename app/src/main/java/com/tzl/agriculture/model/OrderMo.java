package com.tzl.agriculture.model;

import com.tzl.agriculture.fragment.vip.model.CouponMo;

import java.io.Serializable;
import java.util.List;

/**
 * 订单页
 */
public class OrderMo implements Serializable {
    private String orderId;
    private String shopId;
    private String shopName;
    private String invoice;
    private String distribution;
    private String shopOrderAmount;//店铺总价
    private int orderStatus;
    private List<GoodsThis> goodsListBo;

    public OrderMo() {
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public int getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(int orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getInvoice() {
        return invoice;
    }

    public void setInvoice(String invoice) {
        this.invoice = invoice;
    }

    public String getDistribution() {
        return distribution;
    }

    public void setDistribution(String distribution) {
        this.distribution = distribution;
    }


    public String getShopOrderAmount() {
        return shopOrderAmount;
    }

    public void setShopOrderAmount(String shopOrderAmount) {
        this.shopOrderAmount = shopOrderAmount;
    }

    public List<GoodsThis> getGoodsListBo() {
        return goodsListBo;
    }

    public void setGoodsListBo(List<GoodsThis> goodsListBo) {
        this.goodsListBo = goodsListBo;
    }

    //店铺商品信息
    public static class GoodsThis{
        private String orderId;
        private String goodsId;
        private String goodsPrice;
        private String picUrl;
        private String goodsName;
        private String goodsNum;
        private String goodsSpecs;
        private String specsId;

        public GoodsThis() {
        }

        public String getOrderId() {
            return orderId;
        }

        public void setOrderId(String orderId) {
            this.orderId = orderId;
        }

        public String getGoodsId() {
            return goodsId;
        }

        public void setGoodsId(String goodsId) {
            this.goodsId = goodsId;
        }

        public String getGoodsPrice() {
            return goodsPrice;
        }

        public void setGoodsPrice(String goodsPrice) {
            this.goodsPrice = goodsPrice;
        }

        public String getPicUrl() {
            return picUrl;
        }

        public void setPicUrl(String picUrl) {
            this.picUrl = picUrl;
        }

        public String getGoodsName() {
            return goodsName;
        }

        public void setGoodsName(String goodsName) {
            this.goodsName = goodsName;
        }

        public String getGoodsNum() {
            return goodsNum;
        }

        public void setGoodsNum(String goodsNum) {
            this.goodsNum = goodsNum;
        }

        public String getGoodsSpecs() {
            return goodsSpecs;
        }

        public void setGoodsSpecs(String goodsSpecs) {
            this.goodsSpecs = goodsSpecs;
        }

        public String getSpecsId() {
            return specsId;
        }

        public void setSpecsId(String specsId) {
            this.specsId = specsId;
        }
    }
}
