package com.tzl.agriculture.model;

import java.util.List;

public class AppVersion {
    private String versionInfo;
    private String title;
    private String downUrl;
    private String createTime;
    private List<String> remarkList;

    public List<String> getRemarkList() {
        return remarkList;
    }

    public String getVersionInfo() {
        return versionInfo;
    }

    public String getTitle() {
        return title;
    }

    public String getDownUrl() {
        return downUrl;
    }

    public String getCreateTime() {
        return createTime;
    }
}
