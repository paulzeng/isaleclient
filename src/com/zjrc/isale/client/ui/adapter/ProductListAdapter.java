package com.zjrc.isale.client.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zjrc.isale.client.R;

import java.util.ArrayList;
import java.util.List;

public class ProductListAdapter extends BaseAdapter {
    private LayoutInflater inflater;

    private List<ProductItem> list;

    public ProductListAdapter(LayoutInflater inflater) {
        super();
        this.inflater = inflater;
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
            view = inflater.inflate(R.layout.productlist_item, parent, false);
        }
        ItemViewHolder holder = (ItemViewHolder) view.getTag();
        if (holder == null) {
            holder = new ItemViewHolder();
            holder.ll_item = (LinearLayout) view.findViewById(R.id.ll_item);
            holder.tv_productname = (TextView) view.findViewById(R.id.tv_productname);
            holder.tv_productpriceordate = (TextView) view.findViewById(R.id.tv_productpriceordate);
            holder.tv_productbrand = (TextView) view.findViewById(R.id.tv_productbrand);
            holder.tv_productnumorprice = (TextView) view.findViewById(R.id.tv_productnumorprice);
            view.setTag(holder);
        }
        if (item != null) {
            int bottom = holder.ll_item.getPaddingBottom();
            int top = holder.ll_item.getPaddingTop();
            int right = holder.ll_item.getPaddingRight();
            int left = holder.ll_item.getPaddingLeft();
            if (position % 2 == 1) {    // 偶数项
                if (position == getCount() - 1) {   // 最后一项
                    holder.ll_item.setBackgroundResource(R.drawable.v2_list_item_even_lastchild);
                } else {
                    holder.ll_item.setBackgroundResource(R.drawable.v2_list_item_even);
                }
            } else {    // 奇数项
                if (position == 0) {    // 第一项
                    if (position == getCount() - 1) {   // 只有一项
                        holder.ll_item.setBackgroundResource(R.drawable.v2_list_item_selector);
                    } else {
                        holder.ll_item.setBackgroundResource(R.drawable.v2_list_item_odd_firstchild);
                    }
                } else if (position == getCount() - 1) { // 最后一项
                    holder.ll_item.setBackgroundResource(R.drawable.v2_list_item_odd_lastchild);
                } else {
                    holder.ll_item.setBackgroundResource(R.drawable.v2_list_item_odd);
                }
            }
            holder.ll_item.setPadding(left, top, right, bottom);
            holder.tv_productname.setText(item.getProductname());
            holder.tv_productpriceordate.setText(item.getProductpriceordate());
            holder.tv_productbrand.setText("品牌：" + (item.getProductbrand() != null ? item.getProductbrand() : "无"));
            holder.tv_productnumorprice.setText(item.getProductnumorprice());
        }
        return view;
    }

    public void addItem(String productid, String productname, String productpriceordate, String productbrand, String productnumorprice) {
        ProductItem item = new ProductItem();
        item.setProductid(productid);
        item.setProductname(productname);
        item.setProductpriceordate(productpriceordate);
        item.setProductbrand(productbrand);
        item.setProductnumorprice(productnumorprice);
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
        public LinearLayout ll_item;
        public TextView tv_productname;
        public TextView tv_productpriceordate;
        public TextView tv_productbrand;
        public TextView tv_productnumorprice;
    }

    public class ProductItem {
        private String productid;
        private String productname;
        private String productpriceordate;
        private String productbrand;
        private String productnumorprice;

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

        public String getProductpriceordate() {
            return productpriceordate;
        }

        public void setProductpriceordate(String productpriceordate) {
            this.productpriceordate = productpriceordate;
        }

        public String getProductbrand() {
            return productbrand;
        }

        public void setProductbrand(String productbrand) {
            this.productbrand = productbrand;
        }

        public String getProductnumorprice() {
            return productnumorprice;
        }

        public void setProductnumorprice(String productnumorprice) {
            this.productnumorprice = productnumorprice;
        }
    }
}
