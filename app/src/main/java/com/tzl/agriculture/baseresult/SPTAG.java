package com.tzl.agriculture.baseresult;

/**
 * 本地存储key值
 */
public class SPTAG {
    public static final String USER = "user";
    public static final String TOKEN = "token";
    public static final String LOGIN = "1";

    //激光推送的sequence 用来标识一次操作的唯一性(退出登录时根据此参数删除别名)
    public static final int SEQUENCE = 200;

    //激光推送的TAG 用来标识一次操作的唯一性(退出登录时根据此参数删除别名)
    public static final int JPUSHTAG = 300;


    /**
     * 推送弹窗点击控制
     *
     * 100 文章详情
     * 101 商品详情
     * 102 开通会员页面
     * 103 订单详情
     */

    public static final int articleType = 100;
    public static final int goodsType = 101;
    public static final int vipType = 102;
    public static final int orderType = 103;

}
