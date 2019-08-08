package config;

/**
 * 软文服务
 */
public class Article extends Base{

    //首页
    public static String home = "/api/article/home";
    //获取乡愁软文列表
    public static String getArticles = "/api/article/mobile-api/getArticles";

    //文章点赞、收藏、转发   0=点赞，1=收藏，2=转发
    public static  String toOperatArticle = "/api/article/mobile-api/toOperatArticle";

    //获取用户点赞、收藏、转发列表
    public static String getOptionArticleList = "/api/article/mobile-api/getOptionArticleList";

    //获取用户点赞、收藏、转发的总数
    public static String getOptionTotalNum = "/api/article/mobile-api/getOptionTotalNum";

    //文章浏览量
    public static String addBrowseNum = "/api/article/mobile-api/addBrowseNum";

    //获取文章信息
    public static String getArticle = "/api/article/mobile-api/getArticle";

    //广告轮播图
    public static String banner = "/api/article/advertise-api/getAdvertise";


}
