package com.tzl.agriculture.model;

/**
 * 物流信息
 */
public class WlMo {
    private String timeFormat;//时间
    private String remark;//描述
    private boolean isFist;

    public boolean isFist() {
        return isFist;
    }

    public void setFist(boolean fist) {
        isFist = fist;
    }

    public String getTimeFormat() {
        return timeFormat;
    }

    public void setTimeFormat(String timeFormat) {
        this.timeFormat = timeFormat;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
