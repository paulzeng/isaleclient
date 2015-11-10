package com.zjrc.isale.client.ui.adapter;

/**
 * 项目名称：销售管家客户端
 * 版本号：V1.00
 * 创建者: 高林荣
 * 功能描述：竞品选择适配器
 */

import java.util.ArrayList;
import java.util.List;

import com.zjrc.isale.client.R;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ContendProductSelectAdapter extends BaseAdapter {
	
	private LayoutInflater inflater;
	
	private List<ContendproductItem> list;
	
	public ContendProductSelectAdapter(LayoutInflater inflater) {
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
		View view=null;
		if (convertView != null) {
			view = convertView;
		} else {
			view = inflater.inflate(R.layout.contentproductselect_list_item, parent, false);
		}
		ItemViewHolder holder = (ItemViewHolder) view.getTag();
		if (holder == null) {
			holder = new ItemViewHolder();
			holder.tv_contendproductname = (TextView) view.findViewById(R.id.tv_contendproductname);
			holder.tv_contendproductbrandname = (TextView) view.findViewById(R.id.tv_contendproductbrandname);
			view.setTag(holder);
		}	
		if (item!=null){
			holder.tv_contendproductname.setText(item.getContendproductname());
			holder.tv_contendproductbrandname.setText(item.getContendproductbrandname());
		}
		return view;
	}
	
	public void addItem(String contendproductid,String contendproductname, String terminalid, String terminalname, String contendproductbrandname, String contendproductnorm,
			            String contendproductprice){
		ContendproductItem item = new ContendproductItem();
		item.setContendproductid(contendproductid);
		item.setContendproductname(contendproductname);
		item.setTerminalid(terminalid);
		item.setTerminalname(terminalname);
		item.setContendproductbrandname(contendproductbrandname);
		item.setContendproductnorm(contendproductnorm);
		item.setContendproductprice(contendproductprice);	
		list.add(item);
	}
	
	public void clearItem(){
		list.clear();
	}
	
	public List<ContendproductItem> getList(){
		return list;
	}	
	
	protected class ItemViewHolder {	
		public TextView tv_contendproductname;
		public TextView tv_contendproductbrandname;
	}	
	
	public class ContendproductItem{

		private String contendproductid;
		private String contendproductname;		
		private String terminalid;
		private String terminalname;
		private String contendproductbrandname;
		private String contendproductnorm;
		private String contendproductprice;

		public String getContendproductid() {
			return contendproductid;
		}
		public void setContendproductid(String contendproductid) {
			this.contendproductid = contendproductid;
		}
		public String getContendproductname() {
			return contendproductname;
		}
		public void setContendproductname(String contendproductname) {
			this.contendproductname = contendproductname;
		}
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
		public String getContendproductbrandname() {
			return contendproductbrandname;
		}
		public void setContendproductbrandname(String contendproductbrandname) {
			this.contendproductbrandname = contendproductbrandname;
		}
		public String getContendproductnorm() {
			return contendproductnorm;
		}
		public void setContendproductnorm(String contendproductnorm) {
			this.contendproductnorm = contendproductnorm;
		}
		public String getContendproductprice() {
			return contendproductprice;
		}
		public void setContendproductprice(String contendproductprice) {
			this.contendproductprice = contendproductprice;
		}		
	}
}
