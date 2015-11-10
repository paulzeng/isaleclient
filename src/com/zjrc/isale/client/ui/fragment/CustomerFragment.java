package com.zjrc.isale.client.ui.fragment;

import android.app.Activity;
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
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.zjrc.isale.client.R;
import com.zjrc.isale.client.application.ISaleApplication;
import com.zjrc.isale.client.bean.CommonUtils;
import com.zjrc.isale.client.bean.Constant;
import com.zjrc.isale.client.ui.activity.TerminalDetailActivity;
import com.zjrc.isale.client.ui.activity.TerminalSearchActivity;
import com.zjrc.isale.client.ui.activity.TerminalSubmitActivity;
import com.zjrc.isale.client.ui.adapter.TerminalListAdapter;
import com.zjrc.isale.client.ui.adapter.TerminalListAdapter.TerminalItem;
import com.zjrc.isale.client.ui.widgets.pulltorefreshview.PullToRefreshBase.OnRefreshListener;
import com.zjrc.isale.client.ui.widgets.pulltorefreshview.PullToRefreshListView;
import com.zjrc.isale.client.util.xml.XmlNode;

import java.util.HashMap;

/**
 * 项目名称：销售管家客户端
 * 版本号：V2.00
 * 创建者: 贺彬,陈浩
 * 功能描述：我的客户fragment
 */
public class CustomerFragment extends BaseFragment implements
		OnItemClickListener {
	private PullToRefreshListView lv_list;

	private ListView actualListView;
	private View listview_foreGround;
	private TextView tv_msg;
	private ImageView iv_icon;

	private TerminalListAdapter adapter;

	private String terminalid = "";
	private String terminalcode = "";
	private String terminalname = "";

    private String id = "";

	private View v;// 缓存页面

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (v == null) {
			v = inflater.inflate(R.layout.terminal_list, container, false);
			lv_list = (PullToRefreshListView) v.findViewById(R.id.lv_list);
			actualListView = lv_list.getRefreshableView();
			adapter = new TerminalListAdapter(
					CustomerFragment.this.getActivity(), actualListView);
			actualListView.setAdapter(adapter);
			actualListView.setOnItemClickListener(this);

			lv_list.setOnRefreshListener(new OnRefreshListener() {

				@Override
				public void onRefresh() {
					if (actualListView.getFirstVisiblePosition() == 0) {
						TerminalItem item = (TerminalItem) adapter.getItem(0);
						if (item != null) {
                            id = "";
							sendQueryTerminal(id, "0", false);
						}
					} else if (actualListView.getLastVisiblePosition() == adapter
							.getCount() + 1) {
						TerminalItem item = (TerminalItem) adapter
								.getItem(actualListView
										.getLastVisiblePosition() - 2);
						if (item != null) {
                            id = item.getTerminalid();
							sendQueryTerminal(id, "1", false);
						}
					}
				}
			});
			listview_foreGround = v.findViewById(R.id.ll_listview_foreground);
			tv_msg = (TextView) listview_foreGround.findViewById(R.id.tv_msg);
			iv_icon = (ImageView) listview_foreGround
					.findViewById(R.id.iv_icon);
		}

		ViewGroup parent = (ViewGroup) v.getParent();
		if (parent != null) {
			parent.removeView(v);// 先移除
		}

        sendQueryTerminal(id, "0", true);

		return v;
	}

	@Override
	public void onClick(View v) {
		if (CommonUtils.isNotFastDoubleClick()) {
			Intent intent;
			switch (v.getId()) {
			case R.id.btn_titlebar_add:
				intent = new Intent(getActivity(), TerminalSubmitActivity.class);
				intent.putExtra("operate", "insert");
				startActivityForResult(intent, 0);
				break;
			case R.id.btn_titlebar_filter:
				intent = new Intent(getActivity(), TerminalSearchActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("terminalid", terminalid);
				bundle.putString("terminalcode", terminalcode);
				bundle.putString("terminalname", terminalname);
				intent.putExtras(bundle);
				startActivityForResult(intent, 1);
				break;
			}
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			if (data != null) {
				Bundle bundle = data.getExtras();
				terminalid = bundle.getString("terminalid");
				terminalcode = bundle.getString("terminalcode");
				terminalname = bundle.getString("terminalname");
			}
            id = "";
			sendQueryTerminal(id, "0", true);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Intent intent = new Intent(getActivity(), TerminalDetailActivity.class);
		intent.putExtra("terminalid",
				((TerminalItem) adapter.getItem(position - 1)).getTerminalid());
		startActivityForResult(intent, 0);
	}

	public void sendQueryTerminal(String terminalid, String order,
			boolean needProgress) {
		ISaleApplication application = (ISaleApplication) CustomerFragment.this
				.getActivity().getApplication();
		if (application != null) {
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("userid", application.getConfig().getUserid());
            params.put("terminalcode", terminalcode);
            params.put("terminalname", terminalname);
            params.put("terminalid", terminalid);
            params.put("order", order);
            request("terminal!list?code=" + Constant.TERMINAL_LIST, params, needProgress ? FLAG_SHOW_PROGRESS | FLAG_SHOW_ERROR | FLAG_CANCEL : FLAG_DEFAULT);
		}
	}

	@Override
	public void onRecvData(XmlNode response) {
	}

    @Override
    public void onRecvData(JsonObject response) {
        if (response != null) {
            String code = response.get("code").getAsString();
            if (Constant.TERMINAL_LIST.equals(code)) {
                JsonArray body = response.getAsJsonArray("body");
                if (body != null) {
                    if (TextUtils.isEmpty(id)) {
                        adapter.clearItem();
                    }
                    for (JsonElement elem : body) {
                        JsonObject obj = (JsonObject) elem;
                        adapter.addItem(obj.get("id").getAsString(),
                                obj.get("name").getAsString(),
                                obj.get("latitude").getAsString(),
                                obj.get("longitude").getAsString(),
                                obj.get("address").getAsString(),
                                obj.get("openstate").getAsString(),
                                obj.get("contactman").getAsString(),
                                obj.get("contactphone").getAsString());
                    }
                    adapter.notifyDataSetChanged();
                }

                if (adapter.getCount() <= 0) {
                    listview_foreGround.setVisibility(View.VISIBLE);
                    lv_list.setVisibility(View.GONE);
                    tv_msg.setText("暂无客户信息~");
                } else {
                    listview_foreGround.setVisibility(View.GONE);
                    lv_list.setVisibility(View.VISIBLE);
                }
                lv_list.onRefreshComplete();
            }
        }
    }

    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void reSet_TitleBar_Main() {
		iv_title_add.setVisibility(View.VISIBLE);
		iv_title_selector.setVisibility(View.GONE);
		tv_titlebar_title.setText("我的客户");
		btn_titlebar_filter.setVisibility(View.VISIBLE);
	}

	@Override
	public void reSet_TitleBar_Right_Btn(boolean isMenuOpen) {
		if (isMenuOpen) {
			iv_title_add.setVisibility(View.VISIBLE);
			iv_title_add.setBackgroundResource(R.drawable.v2_title_close);
			btn_titlebar_filter.setVisibility(View.GONE);
		} else {
			iv_title_add.setVisibility(View.VISIBLE);
			iv_title_add.setBackgroundResource(R.drawable.v2_title_add);
			btn_titlebar_filter.setVisibility(View.VISIBLE);
		}
	}

    @Override
    public boolean onBackPressed() {
        if (!isEmpty(terminalid, terminalcode, terminalname)) {
            terminalid = "";
            terminalcode = "";
            terminalname = "";

            id = "";
            sendQueryTerminal(id, "0", true);

            Toast.makeText(getActivity(), getString(R.string.v2_clearfilter), Toast.LENGTH_SHORT).show();

            return true;
        }
        return false;
    }
}
