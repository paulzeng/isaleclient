package com.zjrc.isale.client.bean;

/**
 * 项目名:政企产品演示品台
 * 功能描述：
 * 创建者:贺彬
 * 创建时间: 2015-04-14
 * 版本：V1.0.0
 */
public class SaleObj {
    private String detailid;
    private String saleprice;
    private String productid;
    private String salenum;

    public String getDetailid() {
        return detailid;
    }

    public void setDetailid(String detailid) {
        this.detailid = detailid;
    }

    public String getSaletotalprice() {
        return saletotalprice;
    }

    public void setSaletotalprice(String saletotalprice) {
        this.saletotalprice = saletotalprice;
    }

    public String getSalenum() {
        return salenum;
    }

    public void setSalenum(String salenum) {
        this.salenum = salenum;
    }

    public String getProductid() {
        return productid;
    }

    public void setProductid(String productid) {
        this.productid = productid;
    }

    public String getSaleprice() {
        return saleprice;
    }

    public void setSaleprice(String saleprice) {
        this.saleprice = saleprice;
    }

    private String saletotalprice;
}
