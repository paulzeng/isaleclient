package com.zjrc.isale.client.ui.adapter;

/**
 * 项目名称：销售管家客户端
 * 版本号：V1.00
 * 创建者: 高林荣
 * 功能描述：销售网点进店品项列表显示Adapter
 */

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.zjrc.isale.client.R;

import java.util.ArrayList;
import java.util.List;

public class TerminalProductListAdapter extends BaseAdapter implements View.OnClickListener {
    private LayoutInflater inflater;
    private Handler mHandler;

    private List<ProductItem> list;

    public TerminalProductListAdapter(LayoutInflater inflater, Handler mHandler) {
        super();
        this.inflater = inflater;
        this.mHandler = mHandler;
        this.list = new ArrayList<ProductItem>();
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
        ProductItem item = list.get(position);
        View view;
        if (convertView != null) {
            view = convertView;
        } else {
            view = inflater.inflate(R.layout.terminalproductlist_item, parent, false);
        }
        ItemViewHolder holder = (ItemViewHolder) view.getTag();
        if (holder == null) {
            holder = new ItemViewHolder();
            holder.iv_deleteproduct = (ImageView) view.findViewById(R.id.iv_deleteproduct);
            holder.tv_productname = (TextView) view.findViewById(R.id.tv_productname);
            view.setTag(holder);
        }
        if (item != null) {
            holder.iv_deleteproduct.setTag(item);
            holder.iv_deleteproduct.setOnClickListener(this);
            holder.tv_productname.setText(item.getProductname());
        }
        return view;
    }

    public void addItem(String productid, String productname, String productbrandname, String productcategoryname, String productnorm, String productprice, String productintodate) {
        ProductItem item = new ProductItem();
        item.setProductid(productid);
        item.setProductname(productname);
        item.setProductbrandname(productbrandname);
        item.setProductcategoryname(productcategoryname);
        item.setProductnorm(productnorm);
        item.setProductprice(productprice);
        item.setProductintodate(productintodate);
        list.add(item);
    }

    public boolean existItem(String productid) {
        boolean bExist = false;
        for (ProductItem item : list) {
            if (item.getProductid().equalsIgnoreCase(productid)) {
                bExist = true;
                break;
            }
        }
        return bExist;
    }

    public void removeItem(ProductItem item) {
        if (list.contains(item)) {
            list.remove(item);
        }
    }

    public void clearItem() {
        list.clear();
    }

    public List<ProductItem> getList() {
        return this.list;
    }

    public void setList(List<ProductItem> list) {
        this.list = list;
    }

    @Override
    public void onClick(View view) {
        removeItem((ProductItem) view.getTag());
        notifyDataSetChanged();
        mHandler.sendEmptyMessage(0);
    }

    protected class ItemViewHolder {
        public ImageView iv_deleteproduct;
        public TextView tv_productname;
    }

    public class ProductItem {
        private String productid;
        private String productname;
        private String productbrandname;
        private String productcategoryname;
        private String productnorm;
        private String productprice;
        private String productintodate;

        public String getProductid() {
            return productid;
        }

        public void setProductid(String productid) {
            this.productid = productid;
        }

        public String getProductname() {
            return productname;
        }

        public void setProductname(String productname) {
            this.productname = productname;
        }

        public String getProductbrandname() {
            return productbrandname;
        }

        public void setProductbrandname(String productbrandname) {
            this.productbrandname = productbrandname;
        }

        public String getProductcategoryname() {
            return productcategoryname;
        }

        public void setProductcategoryname(String productcategoryname) {
            this.productcategoryname = productcategoryname;
        }

        public String getProductnorm() {
            return productnorm;
        }

        public void setProductnorm(String productnorm) {
            this.productnorm = productnorm;
        }

        public String getProductprice() {
            return productprice;
        }

        public void setProductprice(String productprice) {
            this.productprice = productprice;
        }

        public String getProductintodate() {
            return productintodate;
        }

        public void setProductintodate(String productintodate) {
            this.productintodate = productintodate;
        }
    }
}
