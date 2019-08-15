package com.tzl.agriculture.fragment.vip.model;

public class CatMo {
    private int userTiebankId;
    private String cnname;
    private String bankName;
    private String bankNum;
    private String bankLogo;
    private boolean isDefault;

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }

    public int getUserTiebankId() {
        return userTiebankId;
    }


    public String getCnname() {
        return cnname;
    }

    public String getBankName() {
        return bankName;
    }

    public String getBankNum() {
        return bankNum;
    }

    public String getBankLogo() {
        return bankLogo;
    }
}
