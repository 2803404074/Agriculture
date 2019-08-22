package config;

public class ShopCart extends Base {

    //加入购物车 v
    public static final String addGoods = "/api/mall/goodsCart/addBuyCart";

    //清空购物车 -
    public static final String clearAllGoods = "/api/mall/goodsCart/clearBuyCart";

    //删除商品 v
    public static final String delGoods = "/api/mall/goodsCart/deleteBuyCart";

    //获取购物车 v
    public static final String getGoods = "/api/mall/goodsCart/getBuyCartList";

    //获取购物车数量 -
    public static final String getGoodsNum = "/api/mall/goodsCart/getBuyCartNumber";

    //更改商品数量 v
    public static final String updateGoodsNum = "/api/mall/goodsCart/updateBuyCart";
}
