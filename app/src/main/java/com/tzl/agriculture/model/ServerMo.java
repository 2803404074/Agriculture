package com.tzl.agriculture.model;

/**
 * 个人中心-功能与服务数据模型
 */
public class ServerMo {
    private String linkUrl;
    private String title;
    private int resources;

    public ServerMo(String title, int resources) {
        this.title = title;
        this.resources = resources;
    }

    public String getLinkUrl() {
        return linkUrl;
    }

    public void setLinkUrl(String linkUrl) {
        this.linkUrl = linkUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getResources() {
        return resources;
    }

    public void setResources(int resources) {
        this.resources = resources;
    }
}
