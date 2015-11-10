package com.zjrc.isale.client.ui.adapter;

/**
 * 项目名称：销售管家客户端
 * 版本号：V1.00
 * 创建者: 高林荣
 * 功能描述：我的促销列表适配器
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

public class PromotionListAdapter extends BaseAdapter {
	
	private LayoutInflater inflater;
	
	private List<PromotionItem> list;
	
	public PromotionListAdapter(LayoutInflater inflater){
		super();
		this.inflater = inflater;
		this.list = new ArrayList<PromotionItem>();
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
		PromotionItem item = list.get(position);
		View view=null;
		if (convertView != null) {
			view = convertView;
		} else {
			view = inflater.inflate(R.layout.promotion_list_item, parent, false);
		}
		ItemViewHolder holder = (ItemViewHolder) view.getTag();
		if (holder == null) {
			holder = new ItemViewHolder();
			holder.tv_terminalname = (TextView) view.findViewById(R.id.tv_terminalname);
			holder.tv_promotiondesc = (TextView) view.findViewById(R.id.tv_promotiondesc);
			holder.tv_begindate = (TextView) view.findViewById(R.id.tv_begindate);
			holder.tv_enddate = (TextView) view.findViewById(R.id.tv_enddate);		
			holder.tv_promotionprice = (TextView) view.findViewById(R.id.tv_promotionprice);		
			view.setTag(holder);
		}	
		if (item!=null){
			holder.tv_terminalname.setText(item.getTerminalname());
			holder.tv_promotiondesc.setText(item.getPromotiondesc());
			holder.tv_begindate.setText(item.getBegindate());
			holder.tv_enddate.setText(item.getEnddate());
			holder.tv_promotionprice.setText(item.getPromotionprice() + " 元");
		}
		return view;
	}
	
	public void addItem(String promotionid,String terminalname,String promotiondesc, String promotionbegindate, String promotionenddate, String promotionprice){
		PromotionItem item = new PromotionItem();
		item.setPromotionid(promotionid);
		item.setTerminalname(terminalname);
		item.setPromotiondesc(promotiondesc);
		item.setBegindate(promotionbegindate);
		item.setEnddate(promotionenddate);
		item.setPromotionprice(promotionprice);
		list.add(item);
	}
	
	public void clearItem(){
		list.clear();
	}	
	
	protected class ItemViewHolder {
		public TextView tv_terminalname;
		public TextView tv_promotiondesc;
		public TextView tv_begindate;
		public TextView tv_enddate;
		public TextView tv_promotionprice;
	}	
	
	@SuppressWarnings("serial")
	public class PromotionItem implements Serializable {
		//促销ID
		private String promotionid;
		//促销网点
		private String terminalname;
		//促销标题
		private String promotiondesc;
		//促销开始日期
		private String begindate;
		//促销结束日期
		private String enddate;
		//促销价格
		private String promotionprice;
		
		public String getPromotionid() {
			return promotionid;
		}
		public void setPromotionid(String promotionid) {
			this.promotionid = promotionid;
		}
		public String getTerminalname() {
			return terminalname;
		}
		public void setTerminalname(String terminalname) {
			this.terminalname = terminalname;
		}
		public String getPromotiondesc() {
			return promotiondesc;
		}
		public void setPromotiondesc(String promotiondesc) {
			this.promotiondesc = promotiondesc;
		}
		public String getBegindate() {
			return begindate;
		}
		public void setBegindate(String begindate) {
			this.begindate = begindate;
		}
		public String getEnddate() {
			return enddate;
		}
		public void setEnddate(String enddate) {
			this.enddate = enddate;
		}
		public String getPromotionprice() {
			return promotionprice;
		}
		public void setPromotionprice(String promotionprice) {
			this.promotionprice = promotionprice;
		}		
	}
}
