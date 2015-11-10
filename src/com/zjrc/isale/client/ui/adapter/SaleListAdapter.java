package com.zjrc.isale.client.ui.adapter;

/**
 * 项目名称：销售管家客户端
 * 版本号：V2.00
 * 创建者: 高林荣
 * 修改者: 陈浩
 * 功能描述：我的销量列表适配器
 */

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.zjrc.isale.client.R;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SaleListAdapter extends BaseAdapter {
	
	private LayoutInflater inflater;
	
	private List<SaleItem> list;
	
	public SaleListAdapter(LayoutInflater inflater){
		super();
		this.inflater = inflater;
		this.list = new ArrayList<SaleItem>();
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
		SaleItem item = list.get(position);
		View view=null;
		if (convertView != null) {
			view = convertView;
		} else {
			view = inflater.inflate(R.layout.sale_list_item, parent, false);
		}
		ItemViewHolder holder = (ItemViewHolder) view.getTag();
		if (holder == null) {
			holder = new ItemViewHolder();
			holder.tv_terminalname = (TextView) view.findViewById(R.id.tv_terminalname);
			holder.tv_date = (TextView) view.findViewById(R.id.tv_date);
			holder.tv_totalprice = (TextView) view.findViewById(R.id.tv_totalprice);	
			view.setTag(holder);
		}	
		if (item!=null){
			holder.tv_terminalname.setText(item.getTerminalname());
			holder.tv_date.setText(item.getDate());
			holder.tv_totalprice.setText("销售金额：" + item.getTotalprice() + " 元");
		}
		return view;
	}
	
	public void addItem(String saleid,String terminalname,String totalprice, String date){
		SaleItem item = new SaleItem();
		item.setSaleid(saleid);
		item.setSaleno("");
		item.setTerminalname(terminalname);
		item.setTotalprice(totalprice);
		item.setDate(date);
		list.add(item);
	}
	
	public void clearItem(){
		list.clear();
	}	
	
	protected class ItemViewHolder {
		public TextView tv_terminalname;
		public TextView tv_date;
		public TextView tv_totalprice;
	}	
	
	@SuppressWarnings("serial")
	public class SaleItem implements Serializable {
		private String saleid;
		private String saleno;
		private String terminalname;
		private String totalprice;
		private String date;
		
		public String getSaleid() {
			return saleid;
		}
		public void setSaleid(String saleid) {
			this.saleid = saleid;
		}
		public String getSaleno() {
			return saleno;
		}
		public void setSaleno(String saleno) {
			this.saleno = saleno;
		}
		public String getTerminalname() {
			return terminalname;
		}
		public void setTerminalname(String terminalname) {
			this.terminalname = terminalname;
		}
		public String getTotalprice() {
			return totalprice;
		}
		public void setTotalprice(String totalprice) {
			this.totalprice = totalprice;
		}
		public String getDate() {
			return date;
		}
		public void setDate(String date) {
			this.date = date;
		}	
	}
}
