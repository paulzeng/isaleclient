package com.zjrc.isale.client.ui.adapter;

/**
 * 项目名称：销售管家客户端
 * 版本号：V1.00
 * 创建者: 高林荣
 * 功能描述：我的促销列表适配器
 */

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zjrc.isale.client.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ContendproductListAdapter extends BaseAdapter {

    private LayoutInflater inflater;

    private List<ContendproductItem> list;

    public ContendproductListAdapter(LayoutInflater inflater) {
        super();
        this.inflater = inflater;
        this.list = new ArrayList<ContendproductItem>();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        if (position < list.size()) {
            return list.get(position);
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        return -1;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ContendproductItem item = list.get(position);
        View view;
        if (convertView != null) {
            view = convertView;
        } else {
            view = inflater.inflate(R.layout.contendproduct_list_item, parent, false);
        }
        ItemViewHolder holder = (ItemViewHolder) view.getTag();
        if (holder == null) {
            holder = new ItemViewHolder();
            holder.tv_terminalname = (TextView) view.findViewById(R.id.tv_terminalname);
            holder.tv_product = (TextView) view.findViewById(R.id.tv_product);
            holder.tv_price = (TextView) view.findViewById(R.id.tv_price);
            holder.tv_date = (TextView) view.findViewById(R.id.tv_date);
            view.setTag(holder);
        }
        if (item != null) {
            holder.tv_terminalname.setText(item.getTerminalname());
            holder.tv_product.setText(item.getProductname());
            holder.tv_price.setText("￥" + item.getPrice() + " 元");
            holder.tv_date.setText(item.getDate());
        }
        return view;
    }

    public void addItem(String productid, String terminalname, String productname, String brand, String price, String date) {
        ContendproductItem item = new ContendproductItem();
        item.setProductid(productid);
        item.setTerminalname(terminalname);
        item.setProductname(productname);
        item.setBrand(brand);
        item.setPrice(price);
        item.setDate(date);
        list.add(item);
    }

    public void clearItem() {
        list.clear();
    }

    protected class ItemViewHolder {
        public TextView tv_terminalname;
        public TextView tv_product;
        public TextView tv_price;
        public TextView tv_date;
    }

    @SuppressWarnings("serial")
    public class ContendproductItem implements Serializable {
        //竞品ID
        private String productid;
        //促销网点
        private String terminalname;
        //竞品名称
        private String productname;
        //竞品品牌
        private String brand;
        //竞品价格
        private String price;
        //上报日期
        private String date;

        public String getProductid() {
            return productid;
        }

        public void setProductid(String productid) {
            this.productid = productid;
        }

        public String getTerminalname() {
            return terminalname;
        }

        public void setTerminalname(String terminalname) {
            this.terminalname = terminalname;
        }

        public String getProductname() {
            return productname;
        }

        public void setProductname(String productname) {
            this.productname = productname;
        }

        public String getBrand() {
            return brand;
        }

        public void setBrand(String brand) {
            this.brand = brand;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }
    }
}