package com.tzl.agriculture.bean;

public class VipCommodity {


    private String memberGoodsId;
    private int month;
    private String monthName;
    private String picUrl;
    private String sort;
    private String createTime;
    private String delFlag;

    public VipCommodity() {
    }

    public String getMemberGoodsId() {
        return memberGoodsId;
    }

    public void setMemberGoodsId(String memberGoodsId) {
        this.memberGoodsId = memberGoodsId;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public String getMonthName() {
        return monthName;
    }

    public void setMonthName(String monthName) {
        this.monthName = monthName;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }

}
