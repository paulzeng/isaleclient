package com.zjrc.isale.client.ui.adapter;

/**
 * 项目名称：销售管家客户端
 * 版本号：V2.00
 * 创建者: 陈浩
 * 功能描述：进店品项列表适配器
 */

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zjrc.isale.client.R;

import java.util.ArrayList;
import java.util.List;

public class TerminalProductAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private List<TerminalProductItem> list;
    private String color = null;

    public TerminalProductAdapter(LayoutInflater inflater) {
        this.inflater = inflater;
        list = new ArrayList<TerminalProductItem>();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        if (position < list.size()) {
            return list.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return -1;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if (convertView != null) {
            view = convertView;
        } else {
            view = inflater.inflate(R.layout.terminalproduct_list_item, parent, false);
        }

        ItemViewHolder holder = (ItemViewHolder) view.getTag();
        if (holder == null) {
            holder = new ItemViewHolder();
            holder.tv_terminalname = (TextView) view.findViewById(R.id.tv_terminalname);
            holder.ll_product1 = (LinearLayout) view.findViewById(R.id.ll_product1);
            holder.tv_productname1 = (TextView) view.findViewById(R.id.tv_productname1);
            holder.tv_intoterminaldate1 = (TextView) view.findViewById(R.id.tv_intoterminaldate1);
            holder.ll_product2 = (LinearLayout) view.findViewById(R.id.ll_product2);
            holder.tv_productname2 = (TextView) view.findViewById(R.id.tv_productname2);
            holder.tv_intoterminaldate2 = (TextView) view.findViewById(R.id.tv_intoterminaldate2);
            holder.ll_product3 = (LinearLayout) view.findViewById(R.id.ll_product3);
            holder.tv_productname3 = (TextView) view.findViewById(R.id.tv_productname3);
            holder.tv_intoterminaldate3 = (TextView) view.findViewById(R.id.tv_intoterminaldate3);
            holder.tv_product_more = (TextView) view.findViewById(R.id.tv_product_more);
            holder.tv_product_total = (TextView) view.findViewById(R.id.tv_product_total);
            view.setTag(holder);
        }

        TerminalProductItem item = list.get(position);
        if (item != null) {
            holder.tv_terminalname.setText(item.getTerminalname());

            List<ProductItem> products = item.getProducts();
            switch (item.getProductsize()) {
                case 0:
                    break;
                case 1:
                    holder.ll_product1.setVisibility(View.VISIBLE);
                    holder.ll_product2.setVisibility(View.GONE);
                    holder.ll_product3.setVisibility(View.GONE);
                    holder.tv_product_more.setVisibility(View.GONE);

                    holder.tv_productname1.setText(products.get(0).getProductname());
                    holder.tv_intoterminaldate1.setText(products.get(0).getIntoterminaldate());
                    break;
                case 2:
                    holder.ll_product1.setVisibility(View.VISIBLE);
                    holder.ll_product2.setVisibility(View.VISIBLE);
                    holder.ll_product3.setVisibility(View.GONE);
                    holder.tv_product_more.setVisibility(View.GONE);

                    holder.tv_productname1.setText(products.get(0).getProductname());
                    holder.tv_intoterminaldate1.setText(products.get(0).getIntoterminaldate());
                    holder.tv_productname2.setText(products.get(1).getProductname());
                    holder.tv_intoterminaldate2.setText(products.get(1).getIntoterminaldate());
                    break;
                case 3:
                    holder.ll_product1.setVisibility(View.VISIBLE);
                    holder.ll_product2.setVisibility(View.VISIBLE);
                    holder.ll_product3.setVisibility(View.VISIBLE);
                    holder.tv_product_more.setVisibility(View.GONE);

                    holder.tv_productname1.setText(products.get(0).getProductname());
                    holder.tv_intoterminaldate1.setText(products.get(0).getIntoterminaldate());
                    holder.tv_productname2.setText(products.get(1).getProductname());
                    holder.tv_intoterminaldate2.setText(products.get(1).getIntoterminaldate());
                    holder.tv_productname3.setText(products.get(2).getProductname());
                    holder.tv_intoterminaldate3.setText(products.get(2).getIntoterminaldate());
                    break;
                default:
                    holder.ll_product1.setVisibility(View.VISIBLE);
                    holder.ll_product2.setVisibility(View.VISIBLE);
                    holder.ll_product3.setVisibility(View.VISIBLE);
                    holder.tv_product_more.setVisibility(View.VISIBLE);

                    holder.tv_productname1.setText(products.get(0).getProductname());
                    holder.tv_intoterminaldate1.setText(products.get(0).getIntoterminaldate());
                    holder.tv_productname2.setText(products.get(1).getProductname());
                    holder.tv_intoterminaldate2.setText(products.get(1).getIntoterminaldate());
                    holder.tv_productname3.setText(products.get(2).getProductname());
                    holder.tv_intoterminaldate3.setText(products.get(2).getIntoterminaldate());
            }

            if (color == null) {
                color = String.format("%X", view.getResources().getColor(R.color.v2_text_blue)).substring(2);
            }
            holder.tv_product_total.setText(Html.fromHtml(String.format("共 <b><font color=\"#%s\">%s</font></b> 项商品", color, item.getProductsize())), TextView.BufferType.SPANNABLE);
        }

        return view;
    }

    public void addItem(String terminalid, String terminalname, int productsize, List<ProductItem> products) {
        TerminalProductItem item = new TerminalProductItem();
        item.setTerminalid(terminalid);
        item.setTerminalname(terminalname);
        item.setProductsize(productsize);
        item.setProducts(products);

        list.add(item);
    }

    public ProductItem createProduct(String productid, String productname, String intoterminaldate) {
        ProductItem item = new ProductItem();
        item.setProductid(productid);
        item.setProductname(productname);
        item.setIntoterminaldate(intoterminaldate);

        return item;
    }

    public void clearItem() {
        list.clear();
    }

    protected class ItemViewHolder {
        public TextView tv_terminalname;
        public LinearLayout ll_product1;
        public TextView tv_productname1;
        public TextView tv_intoterminaldate1;
        public LinearLayout ll_product2;
        public TextView tv_productname2;
        public TextView tv_intoterminaldate2;
        public LinearLayout ll_product3;
        public TextView tv_productname3;
        public TextView tv_intoterminaldate3;
        public TextView tv_product_more;
        public TextView tv_product_total;
    }

    public class TerminalProductItem {
        private String terminalid;
        private String terminalname;
        private List<ProductItem> products;
        private int productsize;

        public String getTerminalid() {
            return terminalid;
        }

        public void setTerminalid(String terminalid) {
            this.terminalid = terminalid;
        }

        public String getTerminalname() {
            return terminalname;
        }

        public void setTerminalname(String terminalname) {
            this.terminalname = terminalname;
        }

        public int getProductsize() {
            return productsize;
        }

        public void setProductsize(int productsize) {
            this.productsize = productsize;
        }

        public List<ProductItem> getProducts() {
            return products;
        }

        public void setProducts(List<ProductItem> products) {
            this.products = products;
        }
    }

    public class ProductItem {
        private String productid;
        private String productname;
        private String intoterminaldate;

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

        public String getIntoterminaldate() {
            return intoterminaldate;
        }

        public void setIntoterminaldate(String intoterminaldate) {
            this.intoterminaldate = intoterminaldate;
        }
    }
}
