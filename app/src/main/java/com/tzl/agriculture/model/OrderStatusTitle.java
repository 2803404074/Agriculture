package com.tzl.agriculture.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 全部 "" 待付款0 代发货1 2待评价
 */
public class OrderStatusTitle {
    private String name;
    private String id;

    public OrderStatusTitle(String name, String id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**

     * @return
     */
    public static List<OrderStatusTitle> getOrderStatus(){
        List<OrderStatusTitle> list = new ArrayList<>();
        list.add(new OrderStatusTitle("全部",""));
        list.add(new OrderStatusTitle("待付款","0"));
        list.add(new OrderStatusTitle("待发货","1"));
        list.add(new OrderStatusTitle("待收货","3"));
        list.add(new OrderStatusTitle("待评价","2"));
        return list;
    }
}
