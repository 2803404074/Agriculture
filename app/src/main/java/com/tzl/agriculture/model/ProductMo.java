package com.tzl.agriculture.model;

/**
 * 产品
 */
public class ProductMo {
    private String id;
    private String goodsId;
    private String specsIds;//规格id
    private String specifications;
    private String price;
    private String number;//库存

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(String goodsId) {
        this.goodsId = goodsId;
    }

    public String getSpecsIds() {
        return specsIds;
    }

    public void setSpecsIds(String specsIds) {
        this.specsIds = specsIds;
    }

    public String getSpecifications() {
        return specifications;
    }

    public void setSpecifications(String specifications) {
        this.specifications = specifications;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
