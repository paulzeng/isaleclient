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

public class VacationListAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	private Handler handler;
	private List<VacationItem> list;
	private Context context;

	public VacationListAdapter(Context context, LayoutInflater inflater,
			Handler handler) {
		this.inflater = inflater;
		this.handler = handler;
		this.list = new ArrayList<VacationItem>();
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
		VacationItem item = list.get(position);
		View view = null;
		if (convertView != null) {
			view = convertView;
		} else {
			view = inflater.inflate(R.layout.vacation_list_item, parent, false);
		}
		ItemViewHolder holder = (ItemViewHolder) view.getTag();
		if (holder == null) {
			holder = new ItemViewHolder();
			holder.tv_begindate = (TextView) view
					.findViewById(R.id.tv_begindate);
			holder.tv_enddate = (TextView) view.findViewById(R.id.tv_enddate);
			holder.tv_state = (TextView) view.findViewById(R.id.tv_state);
			holder.tv_address = (TextView) view.findViewById(R.id.tv_address);
			holder.tv_vacationdays = (TextView) view
					.findViewById(R.id.tv_vacationdays);
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

			holder.tv_begindate.setText(item.getBegindate());
			holder.tv_enddate.setText(item.getEnddate());
			String result = item.getResult();
			Log.i("info", "result:" + result);
			if (result.equals("同意")) {
				holder.tv_state.setVisibility(View.GONE);
			} else {
				holder.tv_state.setVisibility(View.VISIBLE);
			}
			holder.tv_state.setText(result);
			holder.tv_address.setText(item.getType());
			holder.tv_vacationdays.setText(item.getVacationdays().substring(0,
					item.getVacationdays().length() - 2)
					+ "天");

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

	public void addItem(String vacationid, String submitdate,
			String vacationdays, String begindate, String enddate,
			String signstate, String signresult, String type, String result) {
		VacationItem item = new VacationItem();
		item.setVacationid(vacationid);
		item.setBegindate(begindate);
		item.setEnddate(enddate);
		item.setSignresult(signresult);
		item.setSignstate(signstate);
		item.setSubmitdate(submitdate);
		item.setVacationdays(vacationdays);
		item.setType(type);
		item.setResult(result);
		item.setChecked(false);
		list.add(item);
	}

	public void clearItem() {
		list.clear();
	}

	public void selectItem(int position) {
		for (int i = 0; i < list.size(); i++) {
			VacationItem item = list.get(i);
			if (i == position) {
				item.setChecked(true);
			} else {
				item.setChecked(false);
			}
		}
	}

	protected class ItemViewHolder {

		public TextView tv_begindate;
		public TextView tv_enddate;
		public TextView tv_state;
		public TextView tv_address;
		public TextView tv_vacationdays;

		/*
		 * public Button btn_operate1; public Button btn_operate2; public Button
		 * btn_operate3;
		 */
	}

	@SuppressWarnings("serial")
	public class VacationItem implements Serializable {

		private String submitdate;

		private String vacationdays;

		private String begindate;

		private String enddate;

		private String signstate;

		private String signresult;

		private String vacationid;

		private String result;

		public String getResult() {
			return result;
		}

		public void setResult(String result) {
			this.result = result;
		}

		private String type;

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		private boolean checked;

		public boolean isChecked() {
			return checked;
		}

		public void setChecked(boolean checked) {
			this.checked = checked;
		}

		public String getSubmitdate() {
			return submitdate;
		}

		public void setSubmitdate(String submitdate) {
			this.submitdate = submitdate;
		}

		public String getVacationdays() {
			return vacationdays;
		}

		public void setVacationdays(String vacationdays) {
			this.vacationdays = vacationdays;
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

		public String getVacationid() {
			return vacationid;
		}

		public void setVacationid(String vacationid) {
			this.vacationid = vacationid;
		}

	}
}
