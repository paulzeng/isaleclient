package com.zjrc.isale.client.ui.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.zjrc.isale.client.R;
import com.zjrc.isale.client.application.ISaleApplication;
import com.zjrc.isale.client.bean.CommonUtils;
import com.zjrc.isale.client.bean.Constant;
import com.zjrc.isale.client.service.AuditNotifyService;
import com.zjrc.isale.client.ui.activity.AuditSubmitActivity;
import com.zjrc.isale.client.ui.fragment.AuditFragment.SelectorAdapter.SelectorItem;
import com.zjrc.isale.client.ui.fragment.AuditFragment.UnAuditListAdapter.UnAuditItem;
import com.zjrc.isale.client.util.xml.XmlNode;
import com.zjrc.isale.client.util.xml.XmlValueUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 项目名称：销售管家客户端
 * 版本号：V2.00
 * 创建者:贺彬,陈浩
 * 功能描述：我的审批fragment
 */

public class AuditFragment extends BaseFragment implements OnItemClickListener {
	private ListView lv_selector;
	private TextView tv_mask;

	private ListView lv_unaudit_list;
	private View listview_foreGround;

	private String selector = "未审批";

	private SelectorAdapter selectorAdapter;
	private UnAuditListAdapter unAuditListAdapter;

	private int requestType = 0;

	private View v;

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			Intent intent = (Intent) msg.obj;
			int total = intent.getIntExtra("total", 0);
			int vacation = intent.getIntExtra("vacation", 0);
			int travel = intent.getIntExtra("travel", 0);
			int plan = intent.getIntExtra("plan", 0);
			int notice = intent.getIntExtra("notice", 0);

			// 更新各审批任务数量
			((SelectorItem) selectorAdapter.getItem(0)).setNumber(total);
			((SelectorItem) selectorAdapter.getItem(1)).setNumber(plan);
			((SelectorItem) selectorAdapter.getItem(2)).setNumber(travel);
			((SelectorItem) selectorAdapter.getItem(3)).setNumber(notice);
			((SelectorItem) selectorAdapter.getItem(4)).setNumber(vacation);
			selectorAdapter.notifyDataSetChanged();
		}
	};

	// 获取系统未审批数量广播，通知UI更新
	private BroadcastReceiver auditRecivier = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Message msg = Message.obtain(mHandler, 0);
			msg.obj = intent;
			mHandler.sendMessage(msg);
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (v == null) {
			v = inflater.inflate(R.layout.v2_fragment_audit_list, container,
					false);

			Activity activity = AuditFragment.this.getActivity();

			tv_mask = (TextView) v.findViewById(R.id.tv_mask);
			tv_mask.setOnClickListener(this);

			lv_selector = (ListView) v.findViewById(R.id.lv_selector);
			selectorAdapter = new SelectorAdapter(activity);
			lv_selector.setAdapter(selectorAdapter);
			lv_selector.setOnItemClickListener(this);

			unAuditListAdapter = new UnAuditListAdapter(
					activity.getLayoutInflater());
			lv_unaudit_list = (ListView) v.findViewById(R.id.lv_unaudit_list);
			lv_unaudit_list.setAdapter(unAuditListAdapter);
			lv_unaudit_list.setOnItemClickListener(this);

			listview_foreGround = v.findViewById(R.id.ll_listview_foreground);
		
		}
		getActivity().registerReceiver(auditRecivier,
				new IntentFilter(AuditNotifyService.FILTER));
		
		ViewGroup parent = (ViewGroup) v.getParent();
		if (parent != null) {
			parent.removeView(v);// 先移除
		}

		// 通知服务重新获取未审批数量
//		getActivity().sendBroadcast(
//				new Intent(AuditNotifyService.REFETCH_FILTER));
		unAuditListAdapter.clear();
		request("", "0", requestType, true);

		return v;
	}

	@Override
	public void onDestroy() {
        if(auditRecivier!=null)
		    getActivity().unregisterReceiver(auditRecivier);
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		if (CommonUtils.isNotFastDoubleClick()) {
			if (v.getId() == R.id.tv_titlebar_selector || v.getId() == R.id.tv_mask) {
				toggleSelector();
				return;
			}
			super.onClick(v);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		switch (parent.getId()) {
		case R.id.lv_selector:
			if (position != selectorAdapter.getSelectedIndes()) {
				SelectorItem item = (SelectorItem) selectorAdapter
						.getItem(position);
				selector = item.getSelector();
				reSet_TitleBar_Main();

				selectorAdapter.selectItem(position);
				selectorAdapter.notifyDataSetChanged();

				unAuditListAdapter.clear();
				unAuditListAdapter.notifyDataSetChanged();

				requestType = position;
				request("", "0", requestType, true);
			}

			toggleSelector();

			break;
		case R.id.lv_unaudit_list:
			UnAuditItem item = (UnAuditItem) unAuditListAdapter
					.getItem(position);
			if (item != null) {
				Intent intent = new Intent(getActivity(),
						AuditSubmitActivity.class);
				intent.putExtra("type", item.getSelector());
				intent.putExtra("id", item.getId());
				startActivityForResult(intent, 0);
			}
			break;
		}
	}

	private void toggleSelector() {
		if (lv_selector.getVisibility() == View.GONE) {
			tv_mask.setVisibility(View.VISIBLE);
			lv_selector.setVisibility(View.VISIBLE);

			tv_mask.bringToFront();
			lv_selector.bringToFront();
		} else {
			tv_mask.setVisibility(View.GONE);
			lv_selector.setVisibility(View.GONE);

			lv_unaudit_list.bringToFront();
		}
	}

	private void request(String id, String order, int type, boolean needProgress) {
        ISaleApplication application = (ISaleApplication) AuditFragment.this.getActivity().getApplication();
        if (application != null) {
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("audituserid", XmlValueUtil.encodeString(application.getConfig().getUserid()));
            params.put("order",  XmlValueUtil.encodeString(order) );
            params.put("type", type+"");
            request("audit!unauditBadgesList?code=" + Constant.UNAUDIT_LIST, params, FLAG_SHOW_PROGRESS | FLAG_SHOW_ERROR | FLAG_CANCEL);
        }
    }

	@Override
	public void onRecvData(XmlNode response) {

	}

    @Override
    public void onRecvData(JsonObject response) {
        if (response != null) {
            String code = response.get("code").getAsString();
            if (Constant.UNAUDIT_LIST.equals(code)) {
                JsonArray jsonArray = response.getAsJsonArray("body");
                if (jsonArray != null) {
                    if (jsonArray != null && jsonArray.size() > 0) {
                        for (JsonElement record : jsonArray) {
                            JsonObject obj = (JsonObject) record;
                            int index = Integer.parseInt(obj.get("type").getAsString());
                            String type;
                            switch (index) {
                                case 1:
                                    type = "拜访申请";
                                    break;
                                case 2:
                                    type = "差旅申请";
                                    break;
                                case 3:
                                    type = "公告申请";
                                    break;
                                case 4:
                                    type = "休假申请";
                                    break;
                                default:
                                    type = "拜访申请";
                            }
                            unAuditListAdapter.addItem(obj.get("id").getAsString(), index,obj.get("subtitle").getAsString(),
                                    type,obj.get("username").getAsString(), "未审批",
                                    obj.get("createdate").getAsString()
                                            .substring(0, 10));
                        }
                    }
                }
                unAuditListAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Constant.RESULT_AUDITLIST_REFRESH) {
			unAuditListAdapter.clear();
			request("", "0", requestType, true);

			// 通知服务重新获取未审批数量
			getActivity().sendBroadcast(
					new Intent(AuditNotifyService.REFETCH_FILTER));
		}
	}

	@Override
	public void reSet_TitleBar_Main() {
		iv_title_add.setVisibility(View.GONE);
		iv_title_selector.setVisibility(View.VISIBLE);
		iv_title_selector.setText(selector);
		tv_titlebar_title.setText("我的审批");
		btn_titlebar_filter.setVisibility(View.GONE);
	}

	@Override
	public void reSet_TitleBar_Right_Btn(boolean isMenuOpen) {
		if (isMenuOpen) {
			iv_title_add.setVisibility(View.VISIBLE);
			iv_title_add.setBackgroundResource(R.drawable.v2_title_close);
			iv_title_selector.setVisibility(View.GONE);
            if (tv_mask != null) {
                tv_mask.setVisibility(View.GONE);
            }
            if (lv_selector != null) {
                lv_selector.setVisibility(View.GONE);
            }
		} else {
			iv_title_add.setVisibility(View.GONE);
			iv_title_selector.setVisibility(View.VISIBLE);
		}
	}

	class SelectorAdapter extends BaseAdapter {
		private LayoutInflater inflater;
		private List<SelectorItem> list = new ArrayList<SelectorItem>();

		private int selectedIndex = 0;

		public SelectorAdapter(Context context) {
			inflater = LayoutInflater.from(context);

			int[] unauditNumbers = ((ISaleApplication) getActivity()
					.getApplication()).getUnAuditNumbers();
			String[] selectors = context.getResources().getStringArray(
					R.array.audit_selector);
			for (int i = 0; i < selectors.length; i++) {
				list.add(new SelectorItem(selectors[i], unauditNumbers[i]));
			}
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			if (position < list.size()) {
				return list.get(position);
			}
			return null;
		}

		@Override
		public long getItemId(int paramInt) {
			return -1;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			SelectorItem item = list.get(position);
			View view = null;
			if (convertView != null) {
				view = convertView;
			} else {
				view = inflater
						.inflate(R.layout.v2_fragment_audit_selector_item,
								parent, false);
			}
			ItemViewHolder holder = (ItemViewHolder) view.getTag();
			if (holder == null) {
				holder = new ItemViewHolder();
				holder.iv_selected = (ImageView) view
						.findViewById(R.id.iv_selected);
				holder.tv_selector = (TextView) view
						.findViewById(R.id.tv_selector);
				holder.tv_badge = (TextView) view.findViewById(R.id.tv_badge);
				view.setTag(holder);
			}
			if (item != null) {
				holder.tv_selector.setText(item.getSelector());
				if (selectedIndex == position) {
					holder.iv_selected.setVisibility(View.VISIBLE);
					holder.tv_selector.setTextColor(view.getResources()
							.getColor(R.color.v2_text_blue));
				} else {
					holder.iv_selected.setVisibility(View.GONE);
					holder.tv_selector.setTextColor(view.getResources()
							.getColor(R.color.v2_text));
				}
				if (item.getNumber() > 0) {
					holder.tv_badge.setText(item.getNumber() > 99 ? "99+" : ""
							+ item.getNumber());
					holder.tv_badge.setVisibility(View.VISIBLE);
				} else {
					holder.tv_badge.setVisibility(View.GONE);
				}
			}
			return view;
		}

		public void selectItem(int position) {
			selectedIndex = position;
		}

		public int getSelectedIndes() {
			return selectedIndex;
		}

		protected class ItemViewHolder {
			public ImageView iv_selected;
			public TextView tv_selector;
			public TextView tv_badge;
		}

		protected class SelectorItem {
			private String selector;
			private int number;

			public SelectorItem(String selector, int number) {
				this.selector = selector;
				this.number = number;
			}

			public String getSelector() {
				return selector;
			}

			public void setSelector(String selector) {
				this.selector = selector;
			}

			public int getNumber() {
				return number;
			}

			public void setNumber(int number) {
				this.number = number;
			}
		}
	}

	class UnAuditListAdapter extends BaseAdapter {
		private LayoutInflater inflater;
		private List<UnAuditItem> list = new ArrayList<UnAuditItem>();

		public UnAuditListAdapter(LayoutInflater inflater) {
			this.inflater = inflater;
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			if (position < list.size()) {
				return list.get(position);
			}

			return null;
		}

		@Override
		public long getItemId(int paramInt) {
			return -1;
		}

		@Override
		public View getView(int position, View contentView, ViewGroup parent) {
			View view = null;
			if (contentView != null) {
				view = contentView;
			} else {
				view = inflater.inflate(R.layout.v2_fragment_audit_list_item,
						parent, false);
			}
			ItemViewHolder holder = (ItemViewHolder) view.getTag();
			if (holder == null) {
				holder = new ItemViewHolder();
				holder.iv_image = (ImageView) view.findViewById(R.id.iv_image);
				holder.tv_title = (TextView) view.findViewById(R.id.tv_title);
				holder.tv_type = (TextView) view.findViewById(R.id.tv_type);
				holder.tv_user = (TextView) view.findViewById(R.id.tv_user);
				holder.tv_state = (TextView) view.findViewById(R.id.tv_state);
				holder.tv_date = (TextView) view.findViewById(R.id.tv_date);

				view.setTag(holder);
			}

			UnAuditItem item = list.get(position);
			if (item != null) {
				switch (item.getSelector()) {
				case 1:
					holder.iv_image
							.setImageResource(R.drawable.v2_icon_audit_plan);
					break;
				case 2:
					holder.iv_image
							.setImageResource(R.drawable.v2_icon_audit_travel);
					break;
				case 3:
					holder.iv_image
							.setImageResource(R.drawable.v2_icon_audit_notice);
					break;
				case 4:
					holder.iv_image
							.setImageResource(R.drawable.v2_icon_audit_vacation);
					break;
				}
				holder.tv_title.setText(item.getTitle());
				holder.tv_type.setText(item.getType());
				holder.tv_user.setText("人员：" + item.getUser());
				holder.tv_state.setText(item.getState());
				holder.tv_date.setText(item.getDate());
			}

			return view;
		}

		public void addItem(String id, int selector, String title, String type,
				String user, String state, String date) {
			UnAuditItem item = new UnAuditItem();
			item.setId(id);
			item.setSelector(selector);
			item.setTitle(title);
			item.setType(type);
			item.setUser(user);
			item.setState(state);
			item.setDate(date);

			list.add(item);
		}

		public void clear() {
			list.clear();
		}

		protected class ItemViewHolder {
			private ImageView iv_image;
			private TextView tv_title;
			private TextView tv_type;
			private TextView tv_user;
			private TextView tv_state;
			private TextView tv_date;
		}

		protected class UnAuditItem implements Serializable {
			private static final long serialVersionUID = 5631104299357205535L;

			private String id;
			// 类型 1：拜访，2：差旅，3：公告，4：请假
			private int selector;
			private String title;
			private String type;
			private String user;
			private String state;
			private String date;

			public String getId() {
				return id;
			}

			public void setId(String id) {
				this.id = id;
			}

			public int getSelector() {
				return selector;
			}

			public void setSelector(int selector) {
				this.selector = selector;
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

			public String getUser() {
				return user;
			}

			public void setUser(String user) {
				this.user = user;
			}

			public String getState() {
				return state;
			}

			public void setState(String state) {
				this.state = state;
			}

			public String getDate() {
				return date;
			}

			public void setDate(String date) {
				this.date = date;
			}
		}
	}
}
