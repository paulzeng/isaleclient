package com.zjrc.isale.client.ui.fragment;

import java.util.HashMap;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.zjrc.isale.client.R;
import com.zjrc.isale.client.application.ISaleApplication;
import com.zjrc.isale.client.bean.Constant;
import com.zjrc.isale.client.ui.activity.LoginActivity;
import com.zjrc.isale.client.ui.activity.NoticeDetailActivity;
import com.zjrc.isale.client.ui.adapter.NoticeAdapter;
import com.zjrc.isale.client.ui.adapter.NoticeAdapter.NoticeItem;
import com.zjrc.isale.client.ui.widgets.pulltorefreshview.PullToRefreshBase.OnRefreshListener;
import com.zjrc.isale.client.ui.widgets.pulltorefreshview.PullToRefreshListView;
import com.zjrc.isale.client.util.xml.XmlNode;
import com.zjrc.isale.client.util.xml.XmlParser;
import com.zjrc.isale.client.util.xml.XmlValueUtil;

/**
 * 项目名称：销售管家客户端
 * 版本号：V2.00
 * 创建者: 贺彬,陈浩
 * 功能描述：新闻公告fragment
 */
public class NoticeFragment extends BaseFragment implements OnItemClickListener {
	private PullToRefreshListView lv_list;

	private ListView actualListView;
	private View listview_foreGround;
	private TextView tv_msg;
	private ImageView iv_icon;
	private NoticeAdapter adapter;

	private int selectedindex;

	private boolean isclick = false;

    private String id = "";

	private View v;// 缓存界面

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (v == null) {
			v = inflater.inflate(R.layout.notice_list, container, false);

			lv_list = (PullToRefreshListView) v.findViewById(R.id.lv_list);
			adapter = new NoticeAdapter(inflater);
			actualListView = lv_list.getRefreshableView();
			actualListView.setAdapter(adapter);
			actualListView.setOnItemClickListener(this);
			lv_list.setOnRefreshListener(new OnRefreshListener() {

				@Override
				public void onRefresh() {
					if (actualListView.getFirstVisiblePosition() == 0) {
						NoticeItem item = (NoticeItem) adapter
								.getItem(actualListView
										.getFirstVisiblePosition());
						if (item != null) {
                            id = "";
							queryrequest(id, "0", false);
						}
					} else if (actualListView.getLastVisiblePosition() == adapter
							.getCount() + 1) {
						NoticeItem item = (NoticeItem) adapter
								.getItem(actualListView
										.getLastVisiblePosition() - 2);
						if (item != null) {
                            id = item.getNoticeid();
							queryrequest(id, "1", false);
						}
					}
				}
			});
			listview_foreGround = v.findViewById(R.id.ll_listview_foreground);
			tv_msg = (TextView) listview_foreGround.findViewById(R.id.tv_msg);
			iv_icon = (ImageView) listview_foreGround
					.findViewById(R.id.iv_icon);

			selectedindex = 0;
		}

		ViewGroup parent = (ViewGroup) v.getParent();
		if (parent != null) {
			parent.removeView(v);// 先移除
		}

		queryrequest(id, "0", true);

		return v;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		if (!isclick) {
			isclick = true;
			NoticeItem item = (NoticeItem) adapter.getItem(arg2 - 1);
			selectedindex = arg2 - 1;
			if (item != null) {
				if (item.getNoticeread().equalsIgnoreCase("0")) {// 未读则设置为已读
					readrequest(item.getNoticeid());
				} else {
					Intent it = new Intent();
					it.putExtra("url", item.getNoticeurl());
					it.setClass(NoticeFragment.this.getActivity(),
							NoticeDetailActivity.class);
					startActivity(it);
				}
			}
			isclick = false;
		}
	}

	/**
	 * 查询新闻公告列表
	 * 
	 * @param id
	 * @param order
	 */
	public void queryrequest(String id, String order, boolean needProgress) {
		ISaleApplication application = (ISaleApplication) NoticeFragment.this
				.getActivity().getApplication();
		if (application != null && application.getConfig() != null
				&& application.getConfig().getUserid() != null) {
			Map<String, String> params = new HashMap<String, String>();
            params.put("userid", application.getConfig().getUserid());
            params.put("companyid", application.getConfig().getCompanyid());
            params.put("title", "");
            params.put("corpusernoticeid", id);
            params.put("order", order);
            request("notice!list?code=" + Constant.NOTICE_LIST, params, FLAG_SHOW_PROGRESS | FLAG_SHOW_ERROR | FLAG_CANCEL);
		} else {
			Intent intent = new Intent();
			Bundle bundle = new Bundle();
			bundle.putBoolean("bstartactivity", false);
			intent.putExtras(bundle);
			intent.setClass(NoticeFragment.this.getActivity(), LoginActivity.class);
			startActivity(intent);
		}
	}

	/**
	 * 上报新闻已读
	 * 
	 * @param id
	 */
	public void readrequest(String id) {
		ISaleApplication application = (ISaleApplication) NoticeFragment.this
				.getActivity().getApplication();
		if (application != null && application.getConfig() != null
				&& application.getConfig().getUserid() != null) {
			Map<String, String> params = new HashMap<String, String>();
            params.put("userid", application.getConfig().getUserid());
            params.put("noticeid", id);
            request("notice!read?code=" + Constant.NOTICE_READ, params, FLAG_SHOW_PROGRESS | FLAG_SHOW_ERROR | FLAG_CANCEL);
		} else {
			Intent intent = new Intent();
			Bundle bundle = new Bundle();
			bundle.putBoolean("bstartactivity", false);
			intent.putExtras(bundle);
			intent.setClass(NoticeFragment.this.getActivity(),
					LoginActivity.class);
			startActivity(intent);
			this.getActivity().finish();
		}
	}

	/**
	 * 底层数据通知
	 */
	@Override
	public void onRecvData(XmlNode response) {
	}

    @Override
    public void onRecvData(JsonObject response) {
    	if (response != null) {
            String code = response.get("code").getAsString();
            if (Constant.NOTICE_LIST.equals(code)) {//公告列表
                JsonArray body = response.getAsJsonArray("body");
                if (body != null) {
                    if (TextUtils.isEmpty(id)) {
                        adapter.clearItem();
                    }
                    for (JsonElement elem : body) {
                        JsonObject obj = (JsonObject) elem;
                        String date = obj.get("date").getAsString();
                        NoticeItem item = adapter.getItem(0);
						if (item != null && date.compareTo(item.getNoticedate()) > 0) {
							adapter.addItemAsFirst(
									obj.get("id").getAsString(),
									obj.get("title").getAsString(),
									obj.get("date").getAsString(),
									obj.get("read").getAsString(),
									obj.get("detailurl").getAsString());
						} else {
							adapter.addItem(obj.get("id").getAsString(),
									obj.get("title").getAsString(),
									obj.get("date").getAsString(),
									obj.get("read").getAsString(),
									obj.get("detailurl").getAsString());
						}
                    }
                    adapter.notifyDataSetChanged();
                }

                if (adapter.getCount() <= 0) {
					listview_foreGround.setVisibility(View.VISIBLE);
					lv_list.setVisibility(View.GONE);
					tv_msg.setText("暂无公告信息~");
				} else {
					listview_foreGround.setVisibility(View.GONE);
					lv_list.setVisibility(View.VISIBLE);
				}
				lv_list.onRefreshComplete();
            } else if (Constant.NOTICE_READ.equals(code)) {//公告已读
            	NoticeItem item = (NoticeItem) adapter.getItem(selectedindex);
				if (item != null) {
					item.setNoticeread("1");
					adapter.notifyDataSetChanged();
					Intent it = new Intent();
					it.putExtra("url", item.getNoticeurl());
					it.setClass(NoticeFragment.this.getActivity(),
							NoticeDetailActivity.class);
					startActivity(it);
				}
            }
        }
    }

    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void reSet_TitleBar_Main() {
		iv_title_add.setVisibility(View.GONE);
		iv_title_selector.setVisibility(View.GONE);
		tv_titlebar_title.setText("新闻公告");
		btn_titlebar_filter.setVisibility(View.GONE);
	}

	@Override
	public void reSet_TitleBar_Right_Btn(boolean isMenuOpen) {
		if (isMenuOpen) {
			iv_title_add.setVisibility(View.VISIBLE);
			iv_title_add.setBackgroundResource(R.drawable.v2_title_close);
		} else {
			iv_title_add.setVisibility(View.GONE);
		}
	}
}
