package com.tzl.agriculture.model;

import java.util.List;

/**
 * 商品详情
 */
public class GoodsDetailsMo {
    private Goods goods;//商品基本信息
    private String goodsServicesStr;//服务名称展示
    private List<CommentMo> goodsCommentList;
    private List<GoodsAttributes> goodsAttributes; //商品参数信息
    private List<GoodsServices> goodsServices; //商品服务信息
    private List<Specifications> goodsSpecs;//商品规格
    private List<ProductMo> goodsSpecifications;//产品

    public GoodsDetailsMo() {
    }

    public List<CommentMo> getGoodsCommentList() {
        return goodsCommentList;
    }

    public void setGoodsCommentList(List<CommentMo> goodsCommentList) {
        this.goodsCommentList = goodsCommentList;
    }

    public String getGoodsServicesStr() {
        return goodsServicesStr;
    }

    public void setGoodsServicesStr(String goodsServicesStr) {
        this.goodsServicesStr = goodsServicesStr;
    }

    public List<Specifications> getGoodsSpecs() {
        return goodsSpecs;
    }

    public void setGoodsSpecs(List<Specifications> goodsSpecs) {
        this.goodsSpecs = goodsSpecs;
    }

    public List<ProductMo> getGoodsSpecifications() {
        return goodsSpecifications;
    }

    public void setGoodsSpecifications(List<ProductMo> goodsSpecifications) {
        this.goodsSpecifications = goodsSpecifications;
    }

    public Goods getGoods() {
        return goods;
    }

    public void setGoods(Goods goods) {
        this.goods = goods;
    }

    public List<GoodsAttributes> getGoodsAttributes() {
        return goodsAttributes;
    }

    public void setGoodsAttributes(List<GoodsAttributes> goodsAttributes) {
        this.goodsAttributes = goodsAttributes;
    }

    public List<GoodsServices> getGoodsServices() {
        return goodsServices;
    }

    public void setGoodsServices(List<GoodsServices> goodsServices) {
        this.goodsServices = goodsServices;
    }

    //商品基本信息
    public static class Goods{
        private String goodsShareUrl;//分享跳转连接
        private int number;//库存
        private String salesVolume;//销量
        private List<String>goodsLabelList;//标签
        private String shopTypeId;
        private String goodsName;
        private String goodsId;
        private String isEquity;
        private int isSpike;// 0限时购
        private List<String> gallerys;
        private String spikeEndTime;//限时购结束时间
        private String shipAddress;//发货地址
        private String detail;//详情
        private String price;//价钱
        private String originalPrice;//原价
        private String alreadyCollect;//是否已经收藏
        private String shopId;//店铺id
        private String name;//店铺名称
        private String logoIcon;//店铺logo
        private int purchaseLimit;//限购，0无线，1限购一件
        private String freeShipping;//邮费

        public String getSalesVolume() {
            return salesVolume;
        }

        public List<String> getGoodsLabelList() {
            return goodsLabelList;
        }

        public int getPurchaseLimit() {
            return purchaseLimit;
        }

        public String getFreeShipping() {
            return freeShipping;
        }

        public int getNumber() {
            return number;
        }

        public void setNumber(int number) {
            this.number = number;
        }

        public String getGoodsShareUrl() {
            return goodsShareUrl;
        }

        public void setGoodsShareUrl(String goodsShareUrl) {
            this.goodsShareUrl = goodsShareUrl;
        }

        public String getOriginalPrice() {
            return originalPrice;
        }

        public void setOriginalPrice(String originalPrice) {
            this.originalPrice = originalPrice;
        }

        public String getAlreadyCollect() {
            return alreadyCollect;
        }

        public void setAlreadyCollect(String alreadyCollect) {
            this.alreadyCollect = alreadyCollect;
        }

        public String getGoodsId() {
            return goodsId;
        }

        public void setGoodsId(String goodsId) {
            this.goodsId = goodsId;
        }

        public String getShopTypeId() {
            return shopTypeId;
        }

        public void setShopTypeId(String shopTypeId) {
            this.shopTypeId = shopTypeId;
        }

        public String getGoodsName() {
            return goodsName;
        }

        public void setGoodsName(String goodsName) {
            this.goodsName = goodsName;
        }

        public String getIsEquity() {
            return isEquity;
        }

        public void setIsEquity(String isEquity) {
            this.isEquity = isEquity;
        }

        public int getIsSpike() {
            return isSpike;
        }

        public void setIsSpike(int isSpike) {
            this.isSpike = isSpike;
        }

        public List<String> getGallerys() {
            return gallerys;
        }

        public void setGallerys(List<String> gallerys) {
            this.gallerys = gallerys;
        }

        public String getSpikeEndTime() {
            return spikeEndTime;
        }

        public void setSpikeEndTime(String spikeEndTime) {
            this.spikeEndTime = spikeEndTime;
        }

        public String getShipAddress() {
            return shipAddress;
        }

        public void setShipAddress(String shipAddress) {
            this.shipAddress = shipAddress;
        }

        public String getDetail() {
            return detail;
        }

        public void setDetail(String detail) {
            this.detail = detail;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getShopId() {
            return shopId;
        }

        public void setShopId(String shopId) {
            this.shopId = shopId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getLogoIcon() {
            return logoIcon;
        }

        public void setLogoIcon(String logoIcon) {
            this.logoIcon = logoIcon;
        }
    }

    //参数数组
    public static class GoodsAttributes{
        //（产地：越南）
        private String attribute;//名称   产地
        private String value;//值     越南

        public String getAttribute() {
            return attribute;
        }

        public void setAttribute(String attribute) {
            this.attribute = attribute;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    //服务保障数组
    public static class GoodsServices{
        private String goodServiceName;//服务名称
        private String goodServiceDesc;//服务描述

        public String getGoodServiceName() {
            return goodServiceName;
        }

        public void setGoodServiceName(String goodServiceName) {
            this.goodServiceName = goodServiceName;
        }

        public String getGoodServiceDesc() {
            return goodServiceDesc;
        }

        public void setGoodServiceDesc(String goodServiceDesc) {
            this.goodServiceDesc = goodServiceDesc;
        }
    }
}
