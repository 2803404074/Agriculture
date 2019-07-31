package com.tzl.agriculture.model;

import com.tzl.agriculture.baseresult.BaseResponse;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 乡愁---文章详细类
 */
public class XiangcMo extends BaseResponse {

    private String typeId;
    private String typeName;
    private String typeDesc;
    private List<Article> articleInfoList;

    public String getTypeId() {
        return typeId;
    }

    public String getTypeName() {
        return typeName;
    }

    public String getTypeDesc() {
        return typeDesc;
    }

    public List<Article> getArticleInfoList() {
        return articleInfoList;
    }

    //文章列表
    public static class Article {
        private String articleId;
        private String typeId;
        private String coverImgurl;
        private String coverImgurl2;
        private String coverImgurl3;
        private String title;
        private String content;
        private String isGood;
        private String isCollect;
        private String isComment;
        private String collectNum;
        private String goodNum;
        private String userNickname;
        private String userId;
        private String commentNum;
        private String forwardNum;
        private String browseNum;
        private int coverImgurlSize;//图片
        private String createTime;
        private String headUrl;//作者头像
        private ArticleLocation articleLocation;

        private String alreadyGood;
        private String alreadyCollect;
        private String alreadyForward;

        public String getCreateTime() {
            return createTime;
        }

        public String getAlreadyGood() {
            return alreadyGood;
        }

        public String getHeadUrl() {
            return headUrl;
        }

        public String getAlreadyCollect() {
            return alreadyCollect;
        }

        public String getAlreadyForward() {
            return alreadyForward;
        }

        public String getCoverImgurl2() {
            return coverImgurl2;
        }

        public String getCoverImgurl3() {
            return coverImgurl3;
        }

        public String getUserNickname() {
            return userNickname;
        }

        public String getUserId() {
            return userId;
        }

        public int getCoverImgurlSize() {
            return coverImgurlSize;
        }

        public String getArticleId() {
            return articleId;
        }

        public String getTypeId() {
            return typeId;
        }

        public String getCoverImgurl() {
            return coverImgurl;
        }

        public String getTitle() {
            return title;
        }

        public String getContent() {
            return content;
        }

        public String getIsGood() {
            return isGood;
        }

        public String getIsCollect() {
            return isCollect;
        }

        public String getIsComment() {
            return isComment;
        }

        public String getCollectNum() {
            return collectNum;
        }

        public String getGoodNum() {
            return goodNum;
        }

        public String getCommentNum() {
            return commentNum;
        }

        public String getForwardNum() {
            return forwardNum;
        }

        public String getBrowseNum() {
            return browseNum;
        }

        public ArticleLocation getArticleLocation() {
            return articleLocation;
        }
    }
}
