package config;

public class App extends Base{

    //检查更新
    public static String checkNewVersion = "/api/app/appAbout-api/checkNewVersion";

    //关于app
    public static String getAppAbout = "/api/app/appAbout-api/getAppAbout";

    //分享app
    public static String shareApp = "/api/app/appAbout-api/shareApp";

    //会员-我的奖励
    public static String rewards = "/api/basics/member/option/my-rewards";

    //年卡权益接口
    public static String interests = "/api/basics/member/option/card-interests";

    //会员兑换商品列表
    public static String vipGoodsList = "/api/mall/goods-api/page/list/vip";

    //非会员商品展示列表
    public static String sNoVipGoodsList = "/api/mall/memberGoods-api/all/list";

    //月份商品列表
    public static String monthInfo = "/api/mall/memberGoods-api/info";


}
