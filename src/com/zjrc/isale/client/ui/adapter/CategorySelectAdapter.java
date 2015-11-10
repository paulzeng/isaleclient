package com.zjrc.isale.client.ui.adapter;

/**
 * 项目名称：销售管家客户端
 * 版本号：V1.00
 * 创建者: 高林荣
 * 功能描述：产品类别选择适配器
 */

import java.util.ArrayList;
import java.util.List;

import com.zjrc.isale.client.R;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CategorySelectAdapter extends BaseAdapter {
	
	private LayoutInflater inflater;
	
	private List<CategoryItem> list;
	
	public CategorySelectAdapter(LayoutInflater inflater) {
		super();
		this.inflater = inflater;
		this.list = new ArrayList<CategoryItem>();
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
		CategoryItem item = list.get(position);
		View view=null;
		if (convertView != null) {
			view = convertView;
		} else {
			view = inflater.inflate(R.layout.categoryselect_list_item, parent, false);
		}
		ItemViewHolder holder = (ItemViewHolder) view.getTag();
		if (holder == null) {
			holder = new ItemViewHolder();
			holder.tv_categoryname = (TextView) view.findViewById(R.id.tv_categoryname);
			view.setTag(holder);
		}	
		if (item!=null){
			holder.tv_categoryname.setText(item.getCategoryname());
		}
		return view;
	}
	
	public void addItem(String categoryid,String categoryname){
		CategoryItem item = new CategoryItem();
		item.setCategoryid(categoryid);
		item.setCategoryname(categoryname);		
		list.add(item);
	}
	
	public void clearItem(){
		list.clear();
	}
	
	public List<CategoryItem> getList(){
		return list;
	}	
	
	protected class ItemViewHolder {	
		public TextView tv_categoryname;
	}	
	
	public class CategoryItem{
		private String categoryid;
		private String categoryname;

		public String getCategoryid() {
			return categoryid;
		}
		public void setCategoryid(String categoryid) {
			this.categoryid = categoryid;
		}
		public String getCategoryname() {
			return categoryname;
		}
		public void setCategoryname(String categoryname) {
			this.categoryname = categoryname;
		}
	}
}
