package com.zjrc.isale.client.ui.adapter;

/**
 * 项目名称：销售管家客户端
 * 版本号：V1.00
 * 创建者: 高林荣
 * 功能描述：我的订单列表适配器
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

public class BackorderListAdapter extends BaseAdapter {
	
	private LayoutInflater inflater;
	
	private List<BackorderItem> list;
	
	public BackorderListAdapter(LayoutInflater inflater){
		super();
		this.inflater = inflater;
		this.list = new ArrayList<BackorderItem>();
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
		BackorderItem item = list.get(position);
		View view=null;
		if (convertView != null) {
			view = convertView;
		} else {
			view = inflater.inflate(R.layout.backorder_list_item, parent, false);
		}
		ItemViewHolder holder = (ItemViewHolder) view.getTag();
		if (holder == null) {
			holder = new ItemViewHolder();
			holder.tv_terminalname = (TextView) view.findViewById(R.id.tv_terminalname);
			holder.tv_date = (TextView) view.findViewById(R.id.tv_date);
			holder.tv_totalprice = (TextView) view.findViewById(R.id.tv_totalprice);
			holder.tv_state = (TextView) view.findViewById(R.id.tv_state);		
			view.setTag(holder);
		}
        if (item != null) {
            holder.tv_terminalname.setText(item.getTerminalname());
            holder.tv_date.setText(item.getDate());
            holder.tv_totalprice.setText("退货金额：" + item.getTotalprice() + " 元");
            if (item.getSignstate().equalsIgnoreCase("0")) {//未审批
                holder.tv_state.setText("待处理");
            } else {
                if (item.getSignresult().equalsIgnoreCase("0")) {
                    holder.tv_state.setText("不同意退货");
                } else {
                    holder.tv_state.setText("同意退货");
                }
            }
        }
        return view;
    }
	
	public void addItem(String backorderid,String backorderno,String terminalname,String totalprice, String date, String signstate,String signresult){
		BackorderItem item = new BackorderItem();
		item.setBackorderid(backorderid);
		item.setBackorderno(backorderno);
		item.setTerminalname(terminalname);
		item.setTotalprice(totalprice);
		item.setDate(date);
		item.setSignstate(signstate);
		item.setSignresult(signresult);
		list.add(item);
	}
	
	public void clearItem(){
		list.clear();
	}	
	
	protected class ItemViewHolder {
		public TextView tv_terminalname;
		public TextView tv_date;
		public TextView tv_totalprice;
		public TextView tv_state;
	}	
	
	@SuppressWarnings("serial")
	public class BackorderItem implements Serializable {
		private String backorderid;
		private String backorderno;
		private String terminalname;
		private String totalprice;
		private String date;
		private String signstate;
		private String signresult;

		public String getBackorderid() {
			return backorderid;
		}
		public void setBackorderid(String backorderid) {
			this.backorderid = backorderid;
		}
		public String getBackorderno() {
			return backorderno;
		}
		public void setBackorderno(String backorderno) {
			this.backorderno = backorderno;
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
		public String getSignstate() {
			return signstate;
		}
		public void setSignstate(String signstate) {
			this.signstate = signstate;
		}
		public String getSignresult() {
			return signresult;
		}
		public void setSignresult(String signresult) {
			this.signresult = signresult;
		}	
	}
}
