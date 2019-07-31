package com.tzl.agriculture.model;

import java.io.Serializable;
import java.util.List;

public class CommentMo implements Serializable {
    private String headUrl;
    private String nickname;
    private String createTime;
    private String content;
    private List<String> imgList;
    private Goodsx goods;

    public CommentMo() {
    }

    public Goodsx getGoods() {
        return goods;
    }

    public void setGoods(Goodsx goods) {
        this.goods = goods;
    }

    public String getHeadUrl() {
        return headUrl;
    }

    public void setHeadUrl(String headUrl) {
        this.headUrl = headUrl;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<String> getImgList() {
        return imgList;
    }

    public void setImgList(List<String> imgList) {
        this.imgList = imgList;
    }

    public static class Goodsx implements Serializable{
        private String goodsId;
        private String goodsName;
        private String picUrl;
        private String finalAmount;

        public Goodsx() {
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

        public String getFinalAmount() {
            return finalAmount;
        }

        public void setFinalAmount(String finalAmount) {
            this.finalAmount = finalAmount;
        }
    }
}
