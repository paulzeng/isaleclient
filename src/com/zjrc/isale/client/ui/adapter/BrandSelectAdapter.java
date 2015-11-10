package com.zjrc.isale.client.ui.adapter;

/**
 * 项目名称：销售管家客户端
 * 版本号：V1.00
 * 创建者: 高林荣
 * 功能描述：网点选择适配器
 */

import java.util.ArrayList;
import java.util.List;

import com.zjrc.isale.client.R;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class BrandSelectAdapter extends BaseAdapter {
	
	private LayoutInflater inflater;
	
	private List<BrandItem> list;
	
	public BrandSelectAdapter(LayoutInflater inflater) {
		super();
		this.inflater = inflater;
		this.list = new ArrayList<BrandItem>();
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
		BrandItem item = list.get(position);
		View view=null;
		if (convertView != null) {
			view = convertView;
		} else {
			view = inflater.inflate(R.layout.brandselect_list_item, parent, false);
		}
		ItemViewHolder holder = (ItemViewHolder) view.getTag();
		if (holder == null) {
			holder = new ItemViewHolder();
			holder.tv_brandname = (TextView) view.findViewById(R.id.tv_brandname);
			view.setTag(holder);
		}	
		if (item!=null){
			holder.tv_brandname.setText(item.getBrandname());
		}
		return view;
	}
	
	public void addItem(String brandid,String brandname){
		BrandItem item = new BrandItem();
		item.setBrandid(brandid);
		item.setBrandname(brandname);	
		list.add(item);
	}
	
	public void clearItem(){
		list.clear();
	}
	
	public List<BrandItem> getList(){
		return list;
	}	
	
	protected class ItemViewHolder {	
		public TextView tv_brandname;
	}	
	
	public class BrandItem{
		private String brandid;
		private String brandname;

		public String getBrandid() {
			return brandid;
		}
		public void setBrandid(String brandid) {
			this.brandid = brandid;
		}
		public String getBrandname() {
			return brandname;
		}
		public void setBrandname(String brandname) {
			this.brandname = brandname;
		}
	}
}
