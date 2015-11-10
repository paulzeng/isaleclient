package com.zjrc.isale.client.ui.adapter;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zjrc.isale.client.R;

import java.util.ArrayList;
import java.util.List;

public class ProductListAdapter2 extends BaseAdapter {
    private LayoutInflater inflater;
    private Handler mHandler;

    private List<ProductItem> list;

    public ProductListAdapter2(LayoutInflater inflater, Handler mHandler) {
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
        View view = null;
        if (convertView != null) {
            view = convertView;
        } else {
            view = inflater.inflate(R.layout.productlist_item2, parent, false);
        }
        ItemViewHolder holder = (ItemViewHolder) view.getTag();
        if (holder == null) {
            holder = new ItemViewHolder();
            holder.tv_productname = (TextView) view.findViewById(R.id.tv_productname);
            view.setTag(holder);
        }
        if (item != null) {
            holder.tv_productname.setText(item.getProductname() + "(￥" + item.getProductprice() + " × " + item.getProductnum() +  ")");
            holder.tv_productname.setTag(item);
            holder.tv_productname.setOnTouchListener(new TextViewDrawableOnTouchListener());
        }
        return view;
    }

    public void addItem(String productid, String productname, String brandname, String categoryname, String productnorm, String productprice, String productnum, String producttotalprice) {
        ProductItem item = new ProductItem();
        item.setProductid(productid);
        item.setProductname(productname);
        item.setBrandname(brandname);
        item.setCategoryname(categoryname);
        item.setProductnorm(productnorm);
        item.setProductprice(productprice);
        item.setProductnum(productnum);
        item.setProducttotalprice(producttotalprice);
        list.add(item);
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

    protected class ItemViewHolder {
        public TextView tv_productname;
    }

    public class ProductItem {
        private String productid;
        private String productname;
        private String brandname;
        private String categoryname;
        private String productnorm;
        private String productprice;
        private String productnum;
        private String producttotalprice;

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

        public String getBrandname() {
            return brandname;
        }

        public void setBrandname(String brandname) {
            this.brandname = brandname;
        }

        public String getCategoryname() {
            return categoryname;
        }

        public void setCategoryname(String categoryname) {
            this.categoryname = categoryname;
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

        public String getProductnum() {
            return productnum;
        }

        public void setProductnum(String productnum) {
            this.productnum = productnum;
        }

        public String getProducttotalprice() {
            return producttotalprice;
        }

        public void setProducttotalprice(String producttotalprice) {
            this.producttotalprice = producttotalprice;
        }
    }

    public class TextViewDrawableOnTouchListener implements View.OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    return true;
                case MotionEvent.ACTION_UP:
                    if (event.getRawX() <= (v.getLeft() + ((TextView) v).getCompoundDrawables()[0].getBounds().width())) {
                        // 响应左侧图标点击事件
                        removeItem((ProductItem) v.getTag());
                        notifyDataSetChanged();
                        mHandler.sendEmptyMessage(0);
                    }
            }

            return false;
        }
    }
}
