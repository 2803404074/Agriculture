package com.tzl.agriculture.model;

import android.text.TextUtils;

public class IdentityModel {
    /**
     * {"identityName":null,"identityNumber":null,"frontalPic":null,"reversePic":null,"auditStatus":null}
     */

    private String identityName;
    private String identityNumber;
    private String frontalPic;
    private String reversePic;
    private String auditStatus;

    public String getIdentityName() {
        if (TextUtils.isEmpty(identityName)) {
            return "";
        }
        if (identityName.length() > 1) {
            return "*" + identityName.substring(1);
        }
        return identityName;
    }

    public void setIdentityName(String identityNameArp) {
        identityName = identityNameArp;
    }

    public String getIdentityNumber() {
        if (TextUtils.isEmpty(identityNumber)) {
            return "";
        }
        if (identityNumber.length() == 18) {
            return identityNumber.substring(0, 6) + "******" + identityNumber.substring(12);
        }
        if (identityNumber.length() == 18) {
            return identityNumber.substring(0, 6) + "********" + identityNumber.substring(14);
        }
        return identityNumber;
    }

    public String getRelIdentityNumber() {
        return identityNumber;
    }

    public String getRelIdentityName() {
        return identityName;
    }

    public void setIdentityNumber(String identityNumberArp) {
        identityNumber = identityNumberArp;
    }

    public String getFrontalPic() {
        return frontalPic;
    }

    public void setFrontalPic(String frontalPicArp) {
        frontalPic = frontalPicArp;
    }

    public String getReversePic() {
        return reversePic;
    }

    public void setReversePic(String reversePicArp) {
        reversePic = reversePicArp;
    }

    public String getAuditStatus() {
        return auditStatus;
    }

    public void setAuditStatus(String auditStatusArp) {
        auditStatus = auditStatusArp;
    }
}
