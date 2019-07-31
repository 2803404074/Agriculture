package com.tzl.agriculture.model;

import java.util.List;

/**
 * 轮播图
 */
public class BannerMo {
    private String adId;
    private String link;
    private int linkType;
    private String description;
    private String url;

    public int getLinkType() {
        return linkType;
    }

    public String getAdId() {
        return adId;
    }

    public String getLink() {
        return link;
    }

    public String getDescription() {
        return description;
    }

    public String getUrl() {
        return url;
    }
}
