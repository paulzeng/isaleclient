package com.zjrc.isale.client.bean;

/**
 * 项目名:政企产品演示品台
 * 功能描述：
 * 创建者:贺彬
 * 创建时间: 2015-04-14
 * 版本：V1.0.0
 */
public class OrderObj {
    private String detailid;
    private String orderprice;
    private String productid;
    private String ordernum;

    public String getDetailid() {
        return detailid;
    }

    public void setDetailid(String detailid) {
        this.detailid = detailid;
    }

    public String getOrderprice() {
        return orderprice;
    }

    public void setOrderprice(String orderprice) {
        this.orderprice = orderprice;
    }

    public String getProductid() {
        return productid;
    }

    public void setProductid(String productid) {
        this.productid = productid;
    }

    public String getOrdernum() {
        return ordernum;
    }

    public void setOrdernum(String ordernum) {
        this.ordernum = ordernum;
    }

    public String getOrdertotalprice() {
        return ordertotalprice;
    }

    public void setOrdertotalprice(String ordertotalprice) {
        this.ordertotalprice = ordertotalprice;
    }

    private String ordertotalprice;
}
