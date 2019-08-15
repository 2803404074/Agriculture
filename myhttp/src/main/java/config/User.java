package config;

/**
 * 用户服务
 */
public class User extends Base{
    //文件上传
    //public static String fileUpload = "/api/file/file/service/uploadFile";
    public static String fileUpload = "/api/file/file/service/uploadFileWithApi";

    //登陆
    public static String login = "/api/basics/access/app/token";

    //检测验证码
    public static String checkCode = "/api/basics/access/check/code";

    //发送验证码
    public static String sendSms = "/api/basics/access/send/sms";

    //检测用户是否存在(手机号)
    public static String checkPhone = "/api/basics/access/check/phone";

    //检测用户是否存在（微信OpenId）
    public static String checkOpenId = "/api/basics/access/check/";

    //用户注册
    public static String register = "/api/basics/access/app/register";

    //微信登陆
    public static String wechatLogin = "/api/basics/access/wx/token";

    //获取用户信息
    public static String getUserInfo = "/api/basics/user/current/info";

    //修改用户信息
    public static String saveInfo = "/api/basics/user/saveInfo";

    //获取用户信息2
    public static String getUserInfo2 = "/api/basics/user/current/info2";

    //获取用户绑定的银行卡列表
    public static String cartList = "/api/mall/tiebank-api/all/list";

    //所有银行卡列表
    public static String allCardList = "/api/mall/bank-api/all/list";

    //添加银行卡
    public static String addCard = "/api/mall/tiebank-api/save";

    //提现
    public static String postCash = "/api/mall/withdrawalInfo-api/save";

    //获取意见反馈类型列表
    public static String getType = "/api/basics/feedback/getType";

    //用户反馈
    public static String response = "/api/basics/feedback/save";

    //退出登陆
    public static String logout = "/api/basics/access/app/logout";

    //收货地址列表
    public static String addressList = "/api/basics/harvest/myPage";

    //删除地址
    public static String deleteAddress = "/api/basics/harvest/myDelete/";

    //新增/修改地址
    public static String saveAddress = "/api/basics/harvest/save";
}
