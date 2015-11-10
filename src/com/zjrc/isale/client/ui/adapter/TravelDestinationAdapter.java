package com.zjrc.isale.client.ui.adapter;

import java.util.ArrayList;

import com.zjrc.isale.client.R;
import com.zjrc.isale.client.bean.TerminalType;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class TravelDestinationAdapter extends BaseAdapter {
	private Context context;
	private ArrayList<TerminalType> lists = new ArrayList<TerminalType>();

	public TravelDestinationAdapter(Context context,
			ArrayList<TerminalType> lists) {
		this.context = context;
		this.lists = lists;
	}

	public void reSetList(ArrayList<TerminalType> lists) {
		this.lists.clear();
		this.lists = lists;
	}

	@Override
	public int getCount() {
		return lists.size();
	}

	@Override
	public Object getItem(int arg0) {
		return lists.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int arg0, View view, ViewGroup arg2) {
		ViewHolder viewHolder;
		if (view == null) {
			view = ((Activity) context).getLayoutInflater().inflate(
					R.layout.travel_destination_item, null);
			viewHolder = new ViewHolder();
			viewHolder.name = (TextView) view.findViewById(R.id.tv_destination);
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}
		viewHolder.name.setText(lists.get(arg0).getName());
		return view;
	}

	class ViewHolder {
		TextView name;
	}

}
