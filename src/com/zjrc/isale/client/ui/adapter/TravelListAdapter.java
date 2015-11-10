package com.zjrc.isale.client.ui.adapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zjrc.isale.client.R;

public class TravelListAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	private Handler handler;
	private List<TravelItem> list;
	private Context context;

	public TravelListAdapter(Context context, LayoutInflater inflater,
			Handler handler) {
		this.inflater = inflater;
		this.handler = handler;
		this.list = new ArrayList<TravelItem>();
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
		TravelItem item = list.get(position);
		View view = null;
		if (convertView != null) {
			view = convertView;
		} else {
			view = inflater.inflate(R.layout.travel_list_item, parent, false);
		}
		ItemViewHolder holder = (ItemViewHolder) view.getTag();
		if (holder == null) {
			holder = new ItemViewHolder();
			holder.tv_begindate = (TextView) view
					.findViewById(R.id.tv_begindate);
			holder.tv_enddate = (TextView) view.findViewById(R.id.tv_enddate);
			holder.tv_state = (TextView) view.findViewById(R.id.tv_state);
			holder.tv_city = (TextView) view.findViewById(R.id.tv_address);
			view.setTag(holder);
		}
		if (item != null) {
			if (null != item.getActualbegindate()
					&& !item.getActualbegindate().equalsIgnoreCase("")) {
				holder.tv_begindate.setText(item.getActualbegindate());
			} else {
				holder.tv_begindate.setText(item.getBegindate());
			}
			if (null != item.getActualenddate()
					&& !item.getActualenddate().equalsIgnoreCase("")) {
				holder.tv_enddate.setText(item.getActualenddate());
			} else {
				holder.tv_enddate.setText(item.getEnddate());
			}
			String state = item.getState();
			if (state != null) {
				if (state.equalsIgnoreCase("0")) { // (0为未审批，1为审批通过，2为审批未通过，3为已开始,4已结束)
					holder.tv_state.setText("未审批");
				} else if (state.equalsIgnoreCase("1")) {
					holder.tv_state.setText("未开始");
				} else if (state.equalsIgnoreCase("2")) {
					holder.tv_state.setText("不同意");
				} else if (state.equalsIgnoreCase("3")) {
					holder.tv_state.setText("已开始");
				} else if (state.equalsIgnoreCase("4")) {
					holder.tv_state.setText("已到达");
				} else if (state.equalsIgnoreCase("5")) {
					holder.tv_state.setText("已离开");
				} else if (state.equalsIgnoreCase("6")) {
					holder.tv_state.setText("已结束");
				}
			}
			holder.tv_city.setText((item.getProvince()!=null?item.getProvince():"") + (item.getCity()!=null?item.getCity():"")
					+ (item.getZone()!=null?item.getZone():""));

		}
		return view;
	}

	public void addItem(String id, String province, String city, String zone,
			String begindate, String enddate, String state,
			String arrivetraceid, String leavetraceid, String actualbegindate,
			String actualenddate) {
		TravelItem item = new TravelItem();
		item.setId(id);
		item.setProvince(province);
		item.setCity(city);
		item.setZone(zone);
		item.setBegindate(begindate);
		item.setEnddate(enddate);
		item.setState(state);
		item.setArrivetraceid(arrivetraceid);
		item.setLeavetraceid(leavetraceid);
		item.setChecked(false);
		item.setActualbegindate(actualbegindate);
		item.setActualenddate(actualenddate);
		list.add(item);
	}

	public void clearItem() {
		list.clear();
	}

	public void selectItem(int position) {
		for (int i = 0; i < list.size(); i++) {
			TravelItem item = list.get(i);
			if (i == position) {
				item.setChecked(true);
			} else {
				item.setChecked(false);
			}
		}
	}

	protected class ItemViewHolder {

		public TextView tv_city;
		public TextView tv_begindate;
		public TextView tv_enddate;
		public TextView tv_state;

	}

	@SuppressWarnings("serial")
	public class TravelItem implements Serializable {

		private String id;

		private String province;

		private String city;

		private String zone;

		private String begindate;

		private String enddate;

		private String actualbegindate;

		public String getActualbegindate() {
			return actualbegindate;
		}

		public void setActualbegindate(String actualbegindate) {
			this.actualbegindate = actualbegindate;
		}

		private String actualenddate;

		public String getActualenddate() {
			return actualenddate;
		}

		public void setActualenddate(String actualenddate) {
			this.actualenddate = actualenddate;
		}

		private String state;

		private String arrivetraceid;

		private String leavetraceid;

		private boolean checked;

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getCity() {
			return city;
		}

		public void setCity(String city) {
			this.city = city;
		}

		public String getProvince() {
			return province;
		}

		public void setProvince(String province) {
			this.province = province;
		}

		public String getZone() {
			return zone;
		}

		public void setZone(String zone) {
			this.zone = zone;
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

		public String getState() {
			return state;
		}

		public void setState(String state) {
			this.state = state;
		}

		public String getArrivetraceid() {
			return arrivetraceid;
		}

		public void setArrivetraceid(String arrivetraceid) {
			this.arrivetraceid = arrivetraceid;
		}

		public String getLeavetraceid() {
			return leavetraceid;
		}

		public void setLeavetraceid(String leavetraceid) {
			this.leavetraceid = leavetraceid;
		}

		public boolean getChecked() {
			return checked;
		}

		public void setChecked(boolean checked) {
			this.checked = checked;
		}

	}
}
