package com.tzl.agriculture.model;

import java.io.Serializable;
import java.util.List;

public class HomeMo implements Serializable {
    private int position;
    private int sort;//位置
    private List<BannerMo> advertiseList;
    private XiangcMo articleList;

    private List<XiangcMo> articleListXc;

    private List<LimitGoods> goodsTypeList;//限时购 模块数据

    private XchwGoods xchwModel;//乡村好物 模块数据

    public HomeMo() {
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public XchwGoods getXchwModel() {
        return xchwModel;
    }

    public void setXchwModel(XchwGoods xchwModel) {
        this.xchwModel = xchwModel;
    }

    public List<LimitGoods> getGoodsTypeList() {
        return goodsTypeList;
    }

    public void setGoodsTypeList(List<LimitGoods> goodsTypeList) {
        this.goodsTypeList = goodsTypeList;
    }

    public List<XiangcMo> getArticleListXc() {
        return articleListXc;
    }

    public void setArticleListXc(List<XiangcMo> articleListXc) {
        this.articleListXc = articleListXc;
    }

    public List<BannerMo> getAdvertiseList() {
        return advertiseList;
    }

    public void setAdvertiseList(List<BannerMo> advertiseList) {
        this.advertiseList = advertiseList;
    }

    public XiangcMo getArticleList() {
        return articleList;
    }

    public void setArticleList(XiangcMo articleList) {
        this.articleList = articleList;
    }

    public int getPosition() {
        return position;
    }
    public void setPosition(int position) {
        this.position = position;
    }


    /**
     * 限时购模块
     */
    public static class LimitGoods implements Serializable{
        private String typeName;//模块标题
        private String tag;//模块描述
        private List<GoodsList> goodsList;//商品列表

        private List<GoodsList> articleList;//去旅游模块(奇葩需求)

        public List<GoodsList> getArticleList() {
            return articleList;
        }

        public void setArticleList(List<GoodsList> articleList) {
            this.articleList = articleList;
        }

        public String getTypeName() {
            return typeName;
        }

        public void setTypeName(String typeName) {
            this.typeName = typeName;
        }

        public String getTag() {
            return tag;
        }

        public void setTag(String tag) {
            this.tag = tag;
        }

        public List<GoodsList> getGoodsList() {
            return goodsList;
        }

        public void setGoodsList(List<GoodsList> goodsList) {
            this.goodsList = goodsList;
        }

        public static class GoodsList implements Serializable{
            private String picUrl;
            private String goodsId;
            private String price;
            private String articleId;//去旅游的文章id

            public String getPrice() {
                return price;
            }

            public String getArticleId() {
                return articleId;
            }

            public void setArticleId(String articleId) {
                this.articleId = articleId;
            }

            public String getPicUrl() {
                return picUrl;
            }

            public void setPicUrl(String picUrl) {
                this.picUrl = picUrl;
            }

            public String getGoodsId() {
                return goodsId;
            }

            public void setGoodsId(String goodsId) {
                this.goodsId = goodsId;
            }
        }
    }


    /**
     * 乡村好物模块
     */

    public static class XchwGoods implements Serializable{
        private String modelName;
        private String modelTag;
        private List<LimitGoods> goodsTypeList;

        public String getModelName() {
            return modelName;
        }

        public void setModelName(String modelName) {
            this.modelName = modelName;
        }

        public String getModelTag() {
            return modelTag;
        }

        public void setModelTag(String modelTag) {
            this.modelTag = modelTag;
        }

        public List<LimitGoods> getGoodsTypeList() {
            return goodsTypeList;
        }

        public void setGoodsTypeList(List<LimitGoods> goodsTypeList) {
            this.goodsTypeList = goodsTypeList;
        }
    }
}
