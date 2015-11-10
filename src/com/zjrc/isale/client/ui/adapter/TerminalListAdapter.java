package com.zjrc.isale.client.ui.adapter;

/**
 * 项目名称：销售管家客户端
 * 版本号：V1.00
 * 创建者: 高林荣
 * 功能描述：我的网点列表适配器
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.zjrc.isale.client.R;
import com.zjrc.isale.client.ui.activity.TerminalSubmitActivity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TerminalListAdapter extends BaseAdapter {
	private Context context;
	private ListView listView;
	private LayoutInflater inflater;
	private List<TerminalItem> list;

	public TerminalListAdapter(Context context, ListView listView) {
		this.context = context;
		this.inflater = LayoutInflater.from(context);
		this.listView = listView;
		
		this.list = new ArrayList<TerminalItem>();
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
		TerminalItem item = list.get(position);
		View view = null;
		if (convertView != null) {
			view = convertView;
		} else {
			view = inflater.inflate(R.layout.terminal_list_item, parent, false);
		}
		ItemViewHolder holder = (ItemViewHolder) view.getTag();
		if (holder == null) {
			holder = new ItemViewHolder();
			holder.tv_terminalname = (TextView) view.findViewById(R.id.tv_terminalname);
			holder.tv_terminaladdress = (TextView) view.findViewById(R.id.tv_terminaladdress);
			holder.tv_terminalman = (TextView) view.findViewById(R.id.tv_terminalman);
			holder.tv_terminalphone = (TextView) view.findViewById(R.id.tv_terminalphone);
			view.setTag(holder);
		}
		if (item != null) {
			holder.tv_terminalname.setText(item.getTerminalname());
			holder.tv_terminalname.setOnTouchListener(new TextViewDrawableOnTouchListener());
			holder.tv_terminalname.setTag(item.getTerminalid() + "," + position);
			holder.tv_terminaladdress.setText(item.getTerminaladdress());
			if (TextUtils.isEmpty(item.getTerminalman())) {
				holder.tv_terminalman.setText("无联系人");
			} else {
				holder.tv_terminalman.setText(item.getTerminalman());
			}
			if (TextUtils.isEmpty(item.getTerminalphone())) {
				holder.tv_terminalphone.setText("无联系电话");
			} else {
				holder.tv_terminalphone.setText(item.getTerminalphone());
			}
		}
		return view;
	}

	public void addItem(String terminalid, String terminalname, String terminallatitude, String terminallongitude, String terminaladdress, String terminalopenstate, String terminalman, String terminalphone) {
		TerminalItem item = new TerminalItem();
		item.setTerminalid(terminalid);
		item.setTerminalname(terminalname);
		item.setTerminallatitude(terminallatitude);
		item.setTerminallongitude(terminallongitude);
		item.setTerminaladdress(terminaladdress);
		item.setTerminalopenstate(terminalopenstate);
		item.setTerminalman(terminalman);
		item.setTerminalphone(terminalphone);
		
		list.add(item);
	}

	public void clearItem() {
		list.clear();
	}

	protected class ItemViewHolder {
		public TextView tv_terminalname;
		public TextView tv_terminaladdress;
		public TextView tv_terminalman;
		public TextView tv_terminalphone;
	}

	@SuppressWarnings("serial")
	public class TerminalItem implements Serializable {
		private String terminalid;
		private String terminalname;
		private String terminallatitude;
		private String terminallongitude;
		private String terminaladdress;
		private String terminalopenstate;
		private String terminalman;
		private String terminalphone;

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

		public String getTerminallatitude() {
			return terminallatitude;
		}

		public void setTerminallatitude(String terminallatitude) {
			this.terminallatitude = terminallatitude;
		}

		public String getTerminallongitude() {
			return terminallongitude;
		}

		public void setTerminallongitude(String terminallongitude) {
			this.terminallongitude = terminallongitude;
		}

		public String getTerminaladdress() {
			return terminaladdress;
		}

		public void setTerminaladdress(String terminaladdress) {
			this.terminaladdress = terminaladdress;
		}

		public String getTerminalopenstate() {
			return terminalopenstate;
		}

		public void setTerminalopenstate(String terminalopenstate) {
			this.terminalopenstate = terminalopenstate;
		}

		public String getTerminalman() {
			return terminalman;
		}

		public void setTerminalman(String terminalman) {
			this.terminalman = terminalman;
		}

		public String getTerminalphone() {
			return terminalphone;
		}

		public void setTerminalphone(String terminalphone) {
			this.terminalphone = terminalphone;
		}
	}
	
	public class TextViewDrawableOnTouchListener implements OnTouchListener {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					return true;
				case MotionEvent.ACTION_UP:
					if(event.getRawX() >= (v.getRight() - ((TextView) v).getCompoundDrawables()[2].getBounds().width())) {
						// 响应右侧图标点击事件
						Intent intent = new Intent(context, TerminalSubmitActivity.class);
						intent.putExtra("operate", "modify");
						intent.putExtra("terminalid", ((String) v.getTag()).split(",")[0]);
						((Activity) context).startActivityForResult(intent, 0);
	                } else {
	                	// 响应自己的点击事件
	                	if (!v.performClick()) {
		                	// 响应ListView点击事件
		                    int position = Integer.parseInt(((String) v.getTag()).split(",")[1]) + 1;
		                    listView.performItemClick(listView.getChildAt(position - listView.getFirstVisiblePosition()), position, listView.getItemIdAtPosition(position));
	                	}
						
	                }
			}
			
			return false;
		}
	}
}
