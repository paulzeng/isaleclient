package com.zjrc.isale.client.ui.adapter;

/**
 * 项目名称：销售管家客户端
 * 版本号：V1.00
 * 创建者: 高林荣
 * 功能描述：新闻公告列表适配器
 */

import java.util.ArrayList;
import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zjrc.isale.client.R;

public class NoticeAdapter extends BaseAdapter {
	
	private LayoutInflater inflater;
	
	private List<NoticeItem> list;
	
	public NoticeAdapter(LayoutInflater inflater) {
		super();
		this.inflater = inflater;
		this.list = new ArrayList<NoticeItem>();
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public NoticeItem getItem(int position) {
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
		NoticeItem item = list.get(position);
		View view=null;
		if (convertView != null) {
			view = convertView;
		} else {
			view = inflater.inflate(R.layout.notice_list_item, parent, false);
		}
		ItemViewHolder holder = (ItemViewHolder) view.getTag();
		if (holder == null) {
			holder = new ItemViewHolder();
			holder.tv_noticedate = (TextView) view.findViewById(R.id.tv_noticedate);
			holder.tv_noticetitle = (TextView) view.findViewById(R.id.tv_noticetitle);
			view.setTag(holder);
		}	
		if (item!=null){
			String curDate = item.getNoticedate().substring(0, 10);
			String preDate = null;
			
			if (position > 0) {
				NoticeItem preItem = list.get(position - 1);
				if (preItem != null) {
					preDate = preItem.getNoticedate().substring(0, 10);
				}
			}
			
			if (preDate == null || !preDate.equals(curDate)) {
				holder.tv_noticedate.setText(curDate);
				holder.tv_noticedate.setVisibility(View.VISIBLE);
			} else {
				holder.tv_noticedate.setVisibility(View.GONE);
			}
			
			holder.tv_noticetitle.setText(item.getNoticetitle());
			if (item.getNoticeread().equalsIgnoreCase("0")) {
				holder.tv_noticetitle.setTextColor(view.getResources().getColor(R.color.v2_text));
			} else {
				holder.tv_noticetitle.setTextColor(view.getResources().getColor(R.color.v2_text_light));
			}
		}
		return view;
	}
	
	public void addItem(String noticeid,String noticetitle,String noticedate,String noticeread, String noticeurl){
		NoticeItem item = new NoticeItem();
		item.setNoticeid(noticeid);
		item.setNoticetitle(noticetitle);
		item.setNoticedate(noticedate);
		item.setNoticeread(noticeread);
		item.setNoticeurl(noticeurl);
		list.add(item);
	}
	
	public void addItemAsFirst(String noticeid,String noticetitle,String noticedate,String noticeread, String noticeurl){
		NoticeItem item = new NoticeItem();
		item.setNoticeid(noticeid);
		item.setNoticetitle(noticetitle);
		item.setNoticedate(noticedate);
		item.setNoticeread(noticeread);
		item.setNoticeurl(noticeurl);
		list.add(0, item);
	}
	
	public void clearItem(){
		list.clear();
	}
	
	protected class ItemViewHolder {
		public TextView tv_noticedate;
		public TextView tv_noticetitle;
	}	
	
	public class NoticeItem{
		private String noticeid;
		private String noticetitle;
		private String noticedate;
		private String noticeread;
		private String noticeurl;
		private int indexindate;
		
		public void setNoticeid(String noticeid) {
			this.noticeid = noticeid;
		}
		
		public String getNoticeid() {
			return noticeid;
		}		
		
		public String getNoticetitle() {
			return noticetitle;
		}
		
		public void setNoticetitle(String noticetitle) {
			this.noticetitle = noticetitle;
		}
		
		public String getNoticedate() {
			return noticedate;
		}
		
		public void setNoticedate(String noticedate) {
			this.noticedate = noticedate;
		}
		
		public String getNoticeread() {
			return noticeread;
		}
		
		public void setNoticeread(String noticeread) {
			this.noticeread = noticeread;
		}

		public String getNoticeurl() {
			return noticeurl;
		}

		public void setNoticeurl(String noticeurl) {
			this.noticeurl = noticeurl;
		}

		public int getIndexindate() {
			return indexindate;
		}

		public void setIndexindate(int indexindate) {
			this.indexindate = indexindate;
		}
	}
}
