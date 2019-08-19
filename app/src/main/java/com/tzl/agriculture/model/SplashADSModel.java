package com.tzl.agriculture.model;

public class SplashADSModel {
    /**
     * {"advertise":{"adId":49,"adName":"启动图","description":"描述","url":"http://192.168.0.161:8769/api/file/file/service/downloadFile?bucketName=banner-advertise&fileName=e12c58bf2c5c2c96462cebd5628eec4f.jpg","link":"http://www.baidu.com","linkType":2}}}
     */
    private AdvertiseBean advertise;

    public AdvertiseBean getAdvertise() {
        return advertise;
    }

    public void setAdvertise(AdvertiseBean advertiseArp) {
        advertise = advertiseArp;
    }

    public static class AdvertiseBean{
        private String adId;
        private String adName;
        private String description;
        private String url;
        private String link; //0：商品id，1：文章id，2：外链连接
        private String linkType; //0：商品，1：文章，2：外链

        public String getAdId() {
            return adId;
        }

        public void setAdId(String adIdArp) {
            adId = adIdArp;
        }

        public String getAdName() {
            return adName;
        }

        public void setAdName(String adNameArp) {
            adName = adNameArp;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String descriptionArp) {
            description = descriptionArp;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String urlArp) {
            url = urlArp;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String linkArp) {
            link = linkArp;
        }

        public String getLinkType() {
            return linkType;
        }

        public void setLinkType(String linkTypeArp) {
            linkType = linkTypeArp;
        }
    }
}
