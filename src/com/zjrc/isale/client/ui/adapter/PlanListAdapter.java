package com.zjrc.isale.client.ui.adapter;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.zjrc.isale.client.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PlanListAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	private Handler handler;
	private List<PlanItem> list;
	private Context context;

	public PlanListAdapter(Context context, LayoutInflater inflater,
			Handler handler) {
		this.inflater = inflater;
		this.handler = handler;
		this.list = new ArrayList<PlanItem>();
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
		PlanItem item = list.get(position);
		View view = null;
		if (convertView != null) {
			view = convertView;
		} else {
			view = inflater.inflate(R.layout.plan_list_item, parent, false);
		}
		ItemViewHolder holder = (ItemViewHolder) view.getTag();
		if (holder == null) {
			holder = new ItemViewHolder();
			holder.tv_plandate = (TextView) view.findViewById(R.id.tv_time);
			holder.tv_planstate = (TextView) view.findViewById(R.id.tv_state);
			holder.tv_terminalname = (TextView) view
					.findViewById(R.id.tv_address);
			holder.iv_temporary = (ImageView) view
					.findViewById(R.id.iv_temporary);
			view.setTag(holder);
		}
		if (item != null) {
			holder.tv_plandate.setText("计划拜访时间：" + list.get(position).plandate);
			String signState = list.get(position).signstate;
			String signResult = list.get(position).signresult;
			String state = list.get(position).planstate;
			String plantype = list.get(position).plantype;
			if (plantype.equals("0")) {// 临时任务
				if (signState != null && state != null) {
					holder.iv_temporary.setVisibility(View.VISIBLE);
					if (state.equalsIgnoreCase("0")) {// 未执行
						holder.tv_planstate.setText("未执行");
					} else if (state.equalsIgnoreCase("1")) {// 已执行
						if (signState != null && state != null) {
							if (signState.equalsIgnoreCase("0")) {// 待审批
								holder.tv_planstate.setText("未审批");
							} else if (signState.equalsIgnoreCase("1")) {// 已审核
								holder.tv_planstate.setText("");
							}
						}
					} else if (state.equalsIgnoreCase("2")) {
						holder.tv_planstate.setText("已推迟");
					} else if (state.equalsIgnoreCase("3")) {
						holder.tv_planstate.setText("已取消");
					}
				}
			} else {
				holder.iv_temporary.setVisibility(View.GONE);
				if (signState != null && state != null) {
					if (signState.equalsIgnoreCase("0")) {// 待审批
						holder.tv_planstate.setText("未审批");
					} else if (signState.equalsIgnoreCase("1")) {// 已审核
						if (signResult.equalsIgnoreCase("1")) {// 审核通过
							if (state.equalsIgnoreCase("0")) {// 未执行
								holder.tv_planstate.setText("未执行");
							} else if (state.equalsIgnoreCase("1")) {// 已执行
								holder.tv_planstate.setText("");
							} else if (state.equalsIgnoreCase("2")) {
								holder.tv_planstate.setText("已推迟");
							} else if (state.equalsIgnoreCase("3")) {
								holder.tv_planstate.setText("已取消");
							}
						} else {// 审核未通过
							holder.tv_planstate.setText("不同意");
						}
					}
				}
			}
			holder.tv_terminalname.setText(list.get(position).terminalname);
		}
		return view;
	}

	public void addItem(String id, String terminalname, String plandate,
			String signstate, String signresult, String planstate,
			String patrolid, String plantype) {
		PlanItem item = new PlanItem();
		item.setId(id);
		item.setTerminalname(terminalname);
		item.setPlandate(plandate);
		item.setSignstate(signstate);
		item.setSignresult(signresult);
		item.setPlanstate(planstate);
		item.setPatrolid(patrolid);
		item.setChecked(false);
		item.setPlantype(plantype);
		list.add(item);
	}

	public void clearItem() {
		list.clear();
	}

	public void selectItem(int position) {
		for (int i = 0; i < list.size(); i++) {
			PlanItem item = list.get(i);
			if (i == position) {
				item.setChecked(true);
			} else {
				item.setChecked(false);
			}
		}
	}

	protected class ItemViewHolder {
		public TextView tv_terminalname;
		public TextView tv_planstate;
		public TextView tv_plandate;
		public ImageView iv_temporary;
	}

	@SuppressWarnings("serial")
	public class PlanItem implements Serializable {

		private String id;

		private String terminalname;

		private String plandate;

		private String signstate;

		private String signresult;

		private String planstate;

		private String patrolid;
		private String plantype;

		public String getPlantype() {
			return plantype;
		}

		public void setPlantype(String plantype) {
			this.plantype = plantype;
		}

		private boolean checked;

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getTerminalname() {
			return terminalname;
		}

		public void setTerminalname(String terminalname) {
			this.terminalname = terminalname;
		}

		public String getPlandate() {
			return plandate;
		}

		public void setPlandate(String plandate) {
			this.plandate = plandate;
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

		public String getPlanstate() {
			return planstate;
		}

		public void setPlanstate(String planstate) {
			this.planstate = planstate;
		}

		public String getPatrolid() {
			return patrolid;
		}

		public void setPatrolid(String patrolid) {
			this.patrolid = patrolid;
		}

		public boolean getChecked() {
			return checked;
		}

		public void setChecked(boolean checked) {
			this.checked = checked;
		}
	}
}
