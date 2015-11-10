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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zjrc.isale.client.R;

public class AttenceListAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	private Handler handler;
	private List<AttenceItem> list;
	private Context context;

	public AttenceListAdapter(Context context, LayoutInflater inflater,
			Handler handler) {
		this.inflater = inflater;
		this.handler = handler;
		this.list = new ArrayList<AttenceItem>();
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
		AttenceItem item = list.get(position);
		View view = null;
		if (convertView != null) {
			view = convertView;
		} else {
			view = inflater.inflate(R.layout.attence_list_item, parent, false);
		}
		ItemViewHolder holder = (ItemViewHolder) view.getTag();
		if (holder == null) {
			holder = new ItemViewHolder();
			holder.ll_listitem = (LinearLayout) view
					.findViewById(R.id.ll_listitem);
//			holder.iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
			holder.tv_address = (TextView) view.findViewById(R.id.tv_address);
			holder.tv_time = (TextView) view.findViewById(R.id.tv_time);
			holder.tv_date = (TextView) view.findViewById(R.id.tv_date);
			holder.tv_type = (TextView) view.findViewById(R.id.tv_type);
			holder.tv_result = (TextView) view.findViewById(R.id.tv_result);
			holder.rl_button = (RelativeLayout) view
					.findViewById(R.id.rl_button);
			/*
			 * holder.btn_operate1 = (Button)
			 * view.findViewById(R.id.btn_operate1); holder.btn_operate2 =
			 * (Button) view.findViewById(R.id.btn_operate2);
			 * holder.btn_operate3 = (Button)
			 * view.findViewById(R.id.btn_operate3);
			 */
			view.setTag(holder);
		}
		if (item != null) {
			holder.tv_date.setText(item.getDate());
			holder.tv_time.setText(item.getTime());
			holder.tv_type.setText(item.getType());
			holder.tv_address.setText(item.getAddress());
			if (!item.getResult().equals("正常")) {
				holder.tv_result.setText(item.getResult());
			} else {
				holder.tv_result.setText("");
			}

			/*
			 * if (item.getChecked()){
			 * holder.btn_operate1.setText(R.string.travel_btn_detail);
			 * holder.btn_operate1.setTag(item);
			 * holder.btn_operate1.setOnClickListener(new View.OnClickListener()
			 * {
			 * 
			 * @Override public void onClick(View arg0) { WorkItem item =
			 * (WorkItem)arg0.getTag(); Bundle bundle = new Bundle();
			 * bundle.putString("operate", "viewtravel");
			 * bundle.putSerializable("item", item); Message msg =
			 * handler.obtainMessage(Constant.TRAVELLIST_BTN_CLICK);
			 * msg.setData(bundle); handler.sendMessage(msg); } });
			 * holder.btn_operate2.setVisibility(View.VISIBLE);
			 * holder.btn_operate2.setText(R.string.travel_btn_modify);
			 * holder.btn_operate2.setTag(item);
			 * holder.btn_operate2.setOnClickListener(new View.OnClickListener()
			 * {
			 * 
			 * @Override public void onClick(View arg0) { WorkItem item =
			 * (WorkItem)arg0.getTag(); Bundle bundle = new Bundle();
			 * bundle.putString("operate", "modifytravel");
			 * bundle.putSerializable("item", item); Message msg =
			 * handler.obtainMessage(Constant.TRAVELLIST_BTN_CLICK);
			 * msg.setData(bundle); handler.sendMessage(msg); } });
			 * holder.btn_operate3.setVisibility(View.VISIBLE);
			 * holder.btn_operate3.setText(R.string.travel_btn_delete);
			 * holder.btn_operate3.setTag(item);
			 * holder.btn_operate3.setOnClickListener(new View.OnClickListener()
			 * {
			 * 
			 * @Override public void onClick(View arg0) { WorkItem item =
			 * (WorkItem)arg0.getTag(); Bundle bundle = new Bundle();
			 * bundle.putString("operate", "deletetravel");
			 * bundle.putSerializable("item", item); Message msg =
			 * handler.obtainMessage(Constant.TRAVELLIST_BTN_CLICK);
			 * msg.setData(bundle); handler.sendMessage(msg); } });
			 * 
			 * holder.rl_button.setVisibility(View.VISIBLE); }else{
			 * holder.rl_button.setVisibility(View.GONE); }
			 */
		}
		return view;
	}

	public void addItem(String attenceid, String date, String time,
			String latitude, String longitude, String address, String result,
			String type) {
		AttenceItem item = new AttenceItem();
		item.setAddress(address);
		item.setAttenceid(attenceid);
		item.setDate(date);
		item.setTime(time);
		item.setLatitude(latitude);
		item.setLongitude(longitude);
		item.setResult(result);
		item.setType(type);
		item.setChecked(false);
		list.add(item);
	}

	public void clearItem() {
		list.clear();
	}

	public void selectItem(int position) {
		for (int i = 0; i < list.size(); i++) {
			AttenceItem item = list.get(i);
			if (i == position) {
				item.setChecked(true);
			} else {
				item.setChecked(false);
			}
		}
	}

	protected class ItemViewHolder {
		public LinearLayout ll_listitem;

		public ImageView iv_icon;
		public TextView tv_address;
		public TextView tv_type;
		public TextView tv_time;
		public TextView tv_date;
		public TextView tv_result;
		public RelativeLayout rl_button;

		/*
		 * public Button btn_operate1; public Button btn_operate2; public Button
		 * btn_operate3;
		 */
	}

	@SuppressWarnings("serial")
	public class AttenceItem implements Serializable {

		private String attenceid;

		private String date;

		private String time;

		public String getTime() {
			return time;
		}

		public void setTime(String time) {
			this.time = time;
		}

		private String latitude;

		private String longitude;

		private String address;

		private String result;

		private String type;

		private boolean checked;

		public String getAttenceid() {
			return attenceid;
		}

		public void setAttenceid(String attenceid) {
			this.attenceid = attenceid;
		}

		public String getDate() {
			return date;
		}

		public void setDate(String date) {
			this.date = date;
		}

		public String getLatitude() {
			return latitude;
		}

		public void setLatitude(String latitude) {
			this.latitude = latitude;
		}

		public String getLongitude() {
			return longitude;
		}

		public void setLongitude(String longitude) {
			this.longitude = longitude;
		}

		public String getAddress() {
			return address;
		}

		public void setAddress(String address) {
			this.address = address;
		}

		public String getResult() {
			return result;
		}

		public void setResult(String result) {
			this.result = result;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public boolean isChecked() {
			return checked;
		}

		public void setChecked(boolean checked) {
			this.checked = checked;
		}

	}
}
