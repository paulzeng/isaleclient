package com.zjrc.isale.client.ui.widgets;

import com.zjrc.isale.client.R;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * 项目名称：销售管家客户端
 * 版本号：V2.00
 * 创建者: 陈浩
 * 功能描述：弹出选择列表界面
 */
public class CustomPopupDialog extends Dialog {
	private Context context;
	
	private String title;
	private String[] items;
	private OnItemClickListener listener;
	
	public CustomPopupDialog(Context context, String title, String[] items, OnItemClickListener listener) {
		super(context);
		
		this.context = context;
		this.title = title;
		this.items = items;
		this.listener = listener;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.v2_dialog_list);
		getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setCanceledOnTouchOutside(true);

		((TextView) findViewById(R.id.tv_title)).setText(title);
		
		ListView listview = (ListView) findViewById(R.id.lv_items);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, R.layout.v2_dialog_list_item, items);
		listview.setAdapter(adapter);
		if (listener != null) {
			listview.setOnItemClickListener(listener);
		}
	}
}
