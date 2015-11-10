package com.zjrc.isale.client.ui.adapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zjrc.isale.client.R;
import com.zjrc.isale.client.bean.Constant;

public class WorkListAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	private Handler handler;
	private List<WorkItem> list;
	private Context context;

	public WorkListAdapter(Context context, LayoutInflater inflater,
			Handler handler) {
		this.inflater = inflater;
		this.handler = handler;
		this.list = new ArrayList<WorkItem>();
		this.context = context;
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
		WorkItem item = list.get(position);
		View view = null;
		if (convertView != null) {
			view = convertView;
		} else {
			view = inflater.inflate(R.layout.work_list_item, parent, false);
		}
		ItemViewHolder holder = (ItemViewHolder) view.getTag();
		if (holder == null) {
			holder = new ItemViewHolder();
			holder.tv_date = (TextView) view
					.findViewById(R.id.tv_workreport_date);
			holder.tv_title = (TextView) view.findViewById(R.id.tv_title);
			holder.tv_type = (TextView) view
					.findViewById(R.id.tv_workreport_type);

			view.setTag(holder);
		}
		if (item != null) {
			holder.tv_date.setText(item.getBegindate());
			holder.tv_title.setText(item.getTitle());
			holder.tv_type.setText(item.getType());
		}
		return view;
	}

	public void addItem(String id, String begindate, String enddate,
			String content, String planid, String remark, String submitdate,
			String title, String type) {
		WorkItem item = new WorkItem();
		item.setId(id);
		item.setBegindate(begindate);
		item.setEnddate(enddate);
		item.setContent(content);
		item.setPlanid(planid);
		item.setRemark(remark);
		item.setSubmitdate(submitdate);
		item.setTitle(title);
		item.setType(type);
		item.setChecked(false);
		list.add(item);
	}

	public void clearItem() {
		list.clear();
	}

	public void selectItem(int position) {
		for (int i = 0; i < list.size(); i++) {
			WorkItem item = list.get(i);
			if (i == position) {
				item.setChecked(true);
			} else {
				item.setChecked(false);
			}
		}
	}

	protected class ItemViewHolder {
		public ImageView iv_icon;
		public TextView tv_title;
		public TextView tv_date;
		public TextView tv_type;
	}

	@SuppressWarnings("serial")
	public class WorkItem implements Serializable {

		private String id;

		private String title;

		private String type;

		private String content;

		private String submitdate;

		private String begindate;

		private String enddate;

		private String planid;

		private String remark;

		private boolean checked;

		public boolean getChecked() {
			return checked;
		}

		public void setChecked(boolean checked) {
			this.checked = checked;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getContent() {
			return content;
		}

		public void setContent(String content) {
			this.content = content;
		}

		public String getSubmitdate() {
			return submitdate;
		}

		public void setSubmitdate(String submitdate) {
			this.submitdate = submitdate;
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

		public String getPlanid() {
			return planid;
		}

		public void setPlanid(String planid) {
			this.planid = planid;
		}

		public String getRemark() {
			return remark;
		}

		public void setRemark(String remark) {
			this.remark = remark;
		}

	}
}
