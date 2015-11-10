package com.zjrc.isale.client.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.zjrc.isale.client.R;
import com.zjrc.isale.client.application.ISaleApplication;
import com.zjrc.isale.client.bean.Constant;
import com.zjrc.isale.client.ui.activity.ContactDetailActivity;
import com.zjrc.isale.client.ui.adapter.ContactListAdapter;
import com.zjrc.isale.client.ui.adapter.ContactListAdapter.ContactItem;
import com.zjrc.isale.client.ui.widgets.pulltorefreshview.PullToRefreshBase;
import com.zjrc.isale.client.ui.widgets.pulltorefreshview.PullToRefreshListView;
import com.zjrc.isale.client.util.xml.XmlNode;

import java.util.HashMap;
import java.util.Map;

/**
 * 项目名称：销售管家客户端
 * 版本号：V2.00
 * 创建者: 贺彬,陈浩
 * 功能描述：通讯录fragment
 */
public class RosterFragment extends BaseFragment implements OnEditorActionListener, AdapterView.OnItemClickListener, TextWatcher {
	private EditText et_search;
	private PullToRefreshListView lv_list;
    private ListView actualListView;
    private TextView tv_notmatch;

    private String id = "";

	private ContactListAdapter adapter;

	private View v;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (v == null) {
			v = inflater.inflate(R.layout.contact_list, container, false);
			super.onCreate(savedInstanceState);

			final FragmentActivity  activity = RosterFragment.this.getActivity();

			et_search = (EditText) v.findViewById(R.id.et_search);
			et_search.setOnEditorActionListener(this);
			et_search.addTextChangedListener(this);

			lv_list = (PullToRefreshListView) v.findViewById(R.id.lv_list);
			adapter = new ContactListAdapter(activity, inflater);
            actualListView = lv_list.getRefreshableView();
            actualListView.setAdapter(adapter);
            actualListView.setOnItemClickListener(this);

            lv_list.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    if (actualListView.getFirstVisiblePosition() == 0) {
                        ContactItem item = (ContactItem) adapter.getItem(0);
                        if (item != null) {
                            id = "";
                            request(id, "0", false);
                        }
                    } else if (actualListView.getLastVisiblePosition() == adapter.getCount() + 1) {
                        ContactItem item = (ContactItem) adapter.getItem(actualListView.getLastVisiblePosition() - 2);
                        if (item != null) {
                            id = item.getContactid();
                            request(id, "1", false);
                        }
                    }
                }
            });

            tv_notmatch = (TextView) v.findViewById(R.id.tv_notmatch);
		}

		ViewGroup parent = (ViewGroup) v.getParent();
		if (parent != null) {
			parent.removeView(v);// 先移除
		}

        request(id, "0", true);

		return v;
	}

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        ContactItem item = (ContactItem) adapter.getItem(arg2 - 1);
        if (item != null) {
            Intent intent = new Intent(getActivity(), ContactDetailActivity.class);
            intent.putExtra("contactid", item.getContactid());
            getActivity().startActivity(intent);
        }
    }

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		 switch(actionId){
	        case EditorInfo.IME_ACTION_SEARCH:
                id = "";
	        	request(id, "0", true);
		 }
		return true;
	}

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
    }

    @Override
    public void afterTextChanged(Editable editable) {
        id = "";
        request(id, "0", false);
    }

	public void request(String id, String order, boolean needProgress){
		ISaleApplication application = (ISaleApplication) RosterFragment.this.getActivity().getApplication();
		if (application!=null){
			Map<String, String> params = new HashMap<String, String>();
            params.put("companyid", application.getConfig().getCompanyid());
            params.put("userid", application.getConfig().getUserid());
            params.put("username", et_search.getText().toString());
            params.put("contactuserid", id);
            params.put("order", order);
            request("contact!list?code=" + Constant.CONTACT_LIST, params, FLAG_DEFAULT);
		}
	}

	@Override
	public void onRecvData(XmlNode response) {
	}

    @Override
    public void onRecvData(JsonObject response) {
    	if (response != null) {
            String code = response.get("code").getAsString();
            if (Constant.CONTACT_LIST.equals(code)) {//通讯录列表
            	JsonArray body = response.getAsJsonArray("body");
                if (body != null) {
                    if (TextUtils.isEmpty(id)) {
                        adapter.clearItem();
                    }
                    lv_list.setVisibility(View.VISIBLE);
                    tv_notmatch.setVisibility(View.GONE);
                    for (JsonElement elem : body) {
                        JsonObject obj = (JsonObject) elem;
                        
                        String mobile = obj.get("usermobile").getAsString();
                        if (TextUtils.isEmpty(mobile)) {
                            mobile = obj.get("userphone").getAsString();
                        }
						adapter.addItem(obj.get("userid").getAsString(),
								obj.get("username").getAsString(), mobile);
                    }
                    adapter.notifyDataSetChanged();
                } else if (adapter.getCount() == 0) {
                    lv_list.setVisibility(View.GONE);
                    tv_notmatch.setVisibility(View.VISIBLE);
                }
                lv_list.onRefreshComplete();
			}
		}
    }

    @Override
	public void reSet_TitleBar_Main() {
		iv_title_add.setVisibility(View.GONE);
		iv_title_selector.setVisibility(View.GONE);
		tv_titlebar_title.setText("通讯录");
        btn_titlebar_filter.setVisibility(View.GONE);
	}

	@Override
	public void reSet_TitleBar_Right_Btn(boolean isMenuOpen) {
        if(iv_title_add!=null) {
            if (isMenuOpen) {
                iv_title_add.setVisibility(View.VISIBLE);
                iv_title_add.setBackgroundResource(R.drawable.v2_title_close);
            } else {
                iv_title_add.setVisibility(View.GONE);
            }
        }
	}

    @Override
    public boolean onBackPressed() {
        if (!isEmpty(et_search.getText().toString())) {
            et_search.setText("");

            id = "";
            request(id, "0", true);

            Toast.makeText(getActivity(), getString(R.string.v2_clearfilter), Toast.LENGTH_SHORT).show();

            return true;
        }
        return false;
    }

}
