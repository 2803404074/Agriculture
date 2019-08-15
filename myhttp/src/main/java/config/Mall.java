package config;

/**
 * 商城服务
 */
public class Mall extends Base{

    //商品类型
    public static String typeTopList = "/api/mall/shopType-api/typeTopList";

    //限时购商品列表
    public static String xsgList = "/api/mall/goods-api/page/list/xsg";

    //发现好物商品列表
    public static String fxhwList = "/api/mall/goods-api/page/list/fxhw";

    //中国馆-查询省
    public static String getProvinceList = "/api/mall/area/getAddressList";

    //中国馆商品列表
    public static String zggList = "/api/mall/goods-api/zggList";

    //商品详情
    public static String goodsInfo = "/api/mall/goods-api/info";

    //获取订单详情
    public static String getConfirm = "/api/mall/order-api/getConfirm";

    //确认订单
    public static String saveConfirm = "/api/mall/order-api/saveConfirm";

    //提交订单
    public static String save = "/api/mall/order-api/save";

    //支付
    public static String paySave = "/api/mall/order-api/pay";

    //订单列表
    public static String orderList = "/api/mall/order-api/page/list";

    //搜索订单列表
    public static String orderSearch = "/api/mall/order-api/search/page/list";

    //订单详情
    public static String orderInfo ="/api/mall/order-api/info";

    //取消订单
    public static String cancelOrder = "/api/mall/order-api/cancelOrder";

    //确认收货
    public static String receivingOrder = "/api/mall/order-api/receivingOrder";

    //收藏商品
    public static String toOptionGoods ="/api/mall/goodsOption/toOptionGoods";

    //商品搜索
    public static String searchGoods = "/api/mall/goods-api/page/list/all";

    //会员权益商品
    public static String vipGoods = "/api/mall/goods-api/page/list/vip";

    //开通会员权益商品
    public static String openVipGoods = "/api/mall/goods-api/page/list/openVip";

    //优惠券列表
    public static String minePage = "/api/basics/cardInfo/minePage";

    //优惠券详细信息
    public static String couponDetails = "/api/basics/cardInfo/apply/return/";

    //添加商品评论
    public static String commentSave = "/api/mall/goodsComment/appSave";

    //商品评论列表
    public static String appCommentList = "/api/mall/goodsComment/appCommentList";

    //我的评论列表
    public static String myCommentList = "/api/mall/goodsComment/myCommentList";

    //我的收藏（商品、店铺）  和浏览列表
    public static String getGoodsOptionList = "/api/mall/goodsOption/getGoodsOptionList";

    //清空浏览列表
    public static String clearMyBrowse = "/api/mall/goodsOption/clearMyBrowse";

}
