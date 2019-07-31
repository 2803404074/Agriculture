package com.tzl.agriculture.model;

import java.io.Serializable;

/**
 * 商品列表模型
 */
public class GoodsMo implements Serializable {
    private String goodsId;
    private String originalPrice;
    private String goodsName;
    private String goodsDesc;
    private String categoryId;
    private String picUrl;
    private String spikeStartTime;
    private String spikeEndTime;
    private String shopName;
    private String categoryName;
    private String price;

    public GoodsMo() {
    }

    public String getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(String originalPrice) {
        this.originalPrice = originalPrice;
    }

    public String getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(String goodsId) {
        this.goodsId = goodsId;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public String getGoodsDesc() {
        return goodsDesc;
    }

    public void setGoodsDesc(String goodsDesc) {
        this.goodsDesc = goodsDesc;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getSpikeStartTime() {
        return spikeStartTime;
    }

    public void setSpikeStartTime(String spikeStartTime) {
        this.spikeStartTime = spikeStartTime;
    }

    public String getSpikeEndTime() {
        return spikeEndTime;
    }

    public void setSpikeEndTime(String spikeEndTime) {
        this.spikeEndTime = spikeEndTime;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
