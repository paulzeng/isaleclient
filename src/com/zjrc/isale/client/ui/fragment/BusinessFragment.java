package com.zjrc.isale.client.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.zjrc.isale.client.R;
import com.zjrc.isale.client.ui.activity.BackorderListActivity;
import com.zjrc.isale.client.ui.activity.ContendProductListActivity;
import com.zjrc.isale.client.ui.activity.ContendPromotionListActivity;
import com.zjrc.isale.client.ui.activity.OrderListActivity;
import com.zjrc.isale.client.ui.activity.PromotionListActivity;
import com.zjrc.isale.client.ui.activity.SaleListActivity;
import com.zjrc.isale.client.ui.activity.StockListActivity;
import com.zjrc.isale.client.ui.activity.TerminalProductActivity;
import com.zjrc.isale.client.util.xml.XmlNode;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 项目名称：销售管家客户端
 * 版本号：V2.00
 * 创建者: 贺彬,陈浩
 * 功能描述：我的业务fragment
 */
public class BusinessFragment extends BaseFragment {
    private GridView gridview;
    private View v;// 页面缓存

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (v == null) {
            v = inflater.inflate(R.layout.main_gridview, container, false);
            gridview = (GridView) v.findViewById(R.id.gridview);
            ArrayList<HashMap<String, Object>> itemList = new ArrayList<HashMap<String, Object>>();
            // 进店品项
            HashMap<String, Object> mapProduct = new HashMap<String, Object>();
            mapProduct.put("ItemImage", R.drawable.datasubmit_terminalproduct);
            mapProduct.put("ItemText", getResources().getString(R.string.datasubmit_terminalproduct));
            itemList.add(mapProduct);
            // 销量信息
            HashMap<String, Object> mapSale = new HashMap<String, Object>();
            mapSale.put("ItemImage", R.drawable.datasubmit_sale);
            mapSale.put("ItemText", getResources().getString(R.string.datasubmit_sale));
            itemList.add(mapSale);
            // 订货信息
            HashMap<String, Object> mapOrder = new HashMap<String, Object>();
            mapOrder.put("ItemImage", R.drawable.datasubmit_order);
            mapOrder.put("ItemText", getResources().getString(R.string.datasubmit_order));
            itemList.add(mapOrder);
            // 库存信息
            HashMap<String, Object> mapStock = new HashMap<String, Object>();
            mapStock.put("ItemImage", R.drawable.datasubmit_stock);
            mapStock.put("ItemText", getResources().getString(R.string.datasubmit_stock));
            itemList.add(mapStock);
            // 退货信息
            HashMap<String, Object> mapBackOrder = new HashMap<String, Object>();
            mapBackOrder.put("ItemImage", R.drawable.datasubmit_backorder);
            mapBackOrder.put("ItemText", getResources().getString(R.string.datasubmit_backorder));
            itemList.add(mapBackOrder);
            // 销售活动
            HashMap<String, Object> mapPromotion = new HashMap<String, Object>();
            mapPromotion.put("ItemImage", R.drawable.datasubmit_promotion);
            mapPromotion.put("ItemText", getResources().getString(R.string.datasubmit_promotion));
            itemList.add(mapPromotion);
            // 竞品活动
            HashMap<String, Object> mapContendPromotion = new HashMap<String, Object>();
            mapContendPromotion.put("ItemImage", R.drawable.datasubmit_contendpromotion);
            mapContendPromotion.put("ItemText", getResources().getString(R.string.datasubmit_contendpromotion));
            itemList.add(mapContendPromotion);
            // 竞品信息
            HashMap<String, Object> mapContendProduct = new HashMap<String, Object>();
            mapContendProduct.put("ItemImage", R.drawable.datasubmit_contendproduct);
            mapContendProduct.put("ItemText", getResources().getString(R.string.datasubmit_contendproduct));
            itemList.add(mapContendProduct);

            MyGridViewAdapter adapter = new MyGridViewAdapter(itemList);
            // 添加Item到网格中
            gridview.setAdapter(adapter);
            gridview.setOnItemClickListener(gridviewonitemclick);
        }
        ViewGroup parent = (ViewGroup) v.getParent();
        if (parent != null) {
            parent.removeView(v);// 先移除
        }
        return v;
    }

    private OnItemClickListener gridviewonitemclick = new OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            switch (position) {
                case 0:// 进店品项
                    bundle.putString("terminalid", "");
                    bundle.putString("terminalname", "");
                    intent.putExtras(bundle);
                    intent.setClass(BusinessFragment.this.getActivity(), TerminalProductActivity.class);
                    startActivity(intent);
                    break;
                case 1:// 销量信息
                    intent.setClass(BusinessFragment.this.getActivity(), SaleListActivity.class);
                    startActivity(intent);
                    break;
                case 2:// 订货信息
                    intent.setClass(BusinessFragment.this.getActivity(), OrderListActivity.class);
                    startActivity(intent);
                    break;
                case 3:// 库存信息
                    intent.setClass(BusinessFragment.this.getActivity(), StockListActivity.class);
                    startActivity(intent);
                    break;
                case 4:// 退货信息
                    intent.setClass(BusinessFragment.this.getActivity(), BackorderListActivity.class);
                    startActivity(intent);
                    break;
                case 5:// 销售活动
                    intent.setClass(BusinessFragment.this.getActivity(), PromotionListActivity.class);
                    startActivity(intent);
                    break;
                case 6:// 竞品活动
                    bundle.putString("operate", "insert");
                    bundle.putString("contendpromotionid", "");
                    bundle.putString("terminalid", "");
                    bundle.putString("terminalname", "");
                    intent.putExtras(bundle);
                    intent.setClass(BusinessFragment.this.getActivity(), ContendPromotionListActivity.class);
                    startActivity(intent);
                    break;
                case 7:// 竞品信息
                    bundle.putString("operate", "insert");
                    bundle.putString("contendproductid", "");
                    bundle.putString("terminalid", "");
                    bundle.putString("terminalname", "");
                    intent.putExtras(bundle);
                    intent.setClass(BusinessFragment.this.getActivity(), ContendProductListActivity.class);
                    startActivity(intent);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onRecvData(XmlNode response) {
    }

    @Override
    public void onRecvData(JsonObject response) {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void reSet_TitleBar_Main() {
        iv_title_add.setVisibility(View.GONE);
        iv_title_selector.setVisibility(View.GONE);
        tv_titlebar_title.setText("我的业务");
        btn_titlebar_filter.setVisibility(View.GONE);
    }

    @Override
    public void reSet_TitleBar_Right_Btn(boolean isMenuOpen) {
        if (isMenuOpen) {
            iv_title_add.setVisibility(View.VISIBLE);
            iv_title_add.setBackgroundResource(R.drawable.v2_title_close);
        } else {
            iv_title_add.setVisibility(View.GONE);
        }
    }

    class MyGridViewAdapter extends BaseAdapter {
        private ArrayList<HashMap<String, Object>> list;

        public MyGridViewAdapter(ArrayList<HashMap<String, Object>> list) {
            this.list = list;
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
                view = getActivity().getLayoutInflater().inflate(R.layout.gridviewitem, parent, false);
            }
            int height = gridview.getHeight();
            if (height > 0) {
                ViewGroup.LayoutParams params = view.getLayoutParams();
                params.height = height / 4 - dpToPx(3) * 3 / 4;
            }

            ItemViewHolder holder = (ItemViewHolder) view.getTag();
            if (holder == null) {
                holder = new ItemViewHolder();
                holder.ItemText = (TextView) view.findViewById(R.id.ItemText);
                holder.ItemImage = (ImageView) view.findViewById(R.id.ItemImage);
                view.setTag(holder);
            }

            HashMap<String, Object> item = list.get(position);
            if (item != null) {
                holder.ItemText.setText((String) item.get("ItemText"));
                holder.ItemImage.setImageResource((Integer) item.get("ItemImage"));
            }

            return view;
        }

        public int dpToPx(int dp) {
            DisplayMetrics displayMetrics = getActivity().getResources().getDisplayMetrics();
            return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        }

        protected class ItemViewHolder {
            public TextView ItemText;
            public ImageView ItemImage;
        }
    }
}