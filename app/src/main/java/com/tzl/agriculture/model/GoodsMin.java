package com.tzl.agriculture.model;

import java.io.Serializable;

/**
 * 收藏、浏览记录的商品
 */
public class GoodsMin implements Serializable {
    private String goodsId;
    private String goodsName;
    private String picUrl;
    private String collectionNum;

    public GoodsMin() {
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

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getCollectionNum() {
        return collectionNum;
    }

    public void setCollectionNum(String collectionNum) {
        this.collectionNum = collectionNum;
    }
}
