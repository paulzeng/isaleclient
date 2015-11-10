package com.zjrc.isale.client.bean;

/**
 * 项目名:政企产品演示品台
 * 功能描述：
 * 创建者:贺彬
 * 创建时间: 2015-04-14
 * 版本：V1.0.0
 */
public class StockObj {
    private String detailid;
    private String stockprice;
    private String productid;
    private String stocknum;

    public String getDetailid() {
        return detailid;
    }

    public void setDetailid(String detailid) {
        this.detailid = detailid;
    }

    public String getStockprice() {
        return stockprice;
    }

    public void setStockprice(String stockprice) {
        this.stockprice = stockprice;
    }

    public String getProductid() {
        return productid;
    }

    public void setProductid(String productid) {
        this.productid = productid;
    }

    public String getStocknum() {
        return stocknum;
    }

    public void setStocknum(String stocknum) {
        this.stocknum = stocknum;
    }

    public String getStocktotalprice() {
        return stocktotalprice;
    }

    public void setStocktotalprice(String stocktotalprice) {
        this.stocktotalprice = stocktotalprice;
    }

    private String stocktotalprice;
}
