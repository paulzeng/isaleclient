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

public class SuggestionListAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	private List<SuggestionItem> list;
	private Context context;

	public SuggestionListAdapter(Context context, LayoutInflater inflater,
			Handler handler) {
		this.inflater = inflater;
		this.list = new ArrayList<SuggestionItem>();
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
		SuggestionItem item = list.get(position);
		View view = null;
		if (convertView != null) {
			view = convertView;
		} else {
			view = inflater.inflate(R.layout.suggestion_list_item, parent,
					false);
		}
		ItemViewHolder holder = (ItemViewHolder) view.getTag();
		if (holder == null) {
			holder = new ItemViewHolder();
			holder.tv_date = (TextView) view
					.findViewById(R.id.tv_suggestion_date);
			holder.tv_title = (TextView) view.findViewById(R.id.tv_title);
			holder.tv_state = (TextView) view
					.findViewById(R.id.tv_suggestion_type);
			view.setTag(holder);
		}
		if (item != null) {
			holder.tv_date.setText(item.getSuggestiondate());
			holder.tv_title.setText(item.getSuggestiontitle());
			String state = item.getSuggestionstate();
			if (state.equalsIgnoreCase("0")) {
				holder.tv_state.setText("待处理");
				holder.tv_state.setVisibility(View.VISIBLE);
			} else {
				holder.tv_state.setVisibility(View.GONE);
			}

		}
		return view;
	}

	public void addItem(String suggestionid, String suggestiontitle,
			String suggstiontype, String suggestiondate,
			String suggestionstate, String suggestionresult) {
		SuggestionItem item = new SuggestionItem();
		item.setSuggestionid(suggestionid);
		item.setSuggestiontitle(suggestiontitle);
		item.setSuggstiontype(suggstiontype);
		item.setSuggestiondate(suggestiondate);
		item.setSuggestionstate(suggestionstate);
		item.setSuggestionresult(suggestionresult);
		item.setChecked(false);
		list.add(item);
	}

	public void clearItem() {
		list.clear();
	}

	public void selectItem(int position) {
		for (int i = 0; i < list.size(); i++) {
			SuggestionItem item = list.get(i);
			if (i == position) {
				item.setChecked(true);
			} else {
				item.setChecked(false);
			}
		}
	}

	protected class ItemViewHolder {
		public TextView tv_title;
		public TextView tv_date;
		public TextView tv_state;
	}

	@SuppressWarnings("serial")
	public class SuggestionItem implements Serializable {
		private String suggestionid;
		private String suggestiontitle;
		private String suggstiontype;
		private String suggestiondate;
		private String suggestionstate;
		private String suggestionresult;
		private boolean checked;

		public String getSuggestionid() {
			return suggestionid;
		}

		public void setSuggestionid(String suggestionid) {
			this.suggestionid = suggestionid;
		}

		public String getSuggestiontitle() {
			return suggestiontitle;
		}

		public void setSuggestiontitle(String suggestiontitle) {
			this.suggestiontitle = suggestiontitle;
		}

		public String getSuggstiontype() {
			return suggstiontype;
		}

		public void setSuggstiontype(String suggstiontype) {
			this.suggstiontype = suggstiontype;
		}

		public String getSuggestiondate() {
			return suggestiondate;
		}

		public void setSuggestiondate(String suggestiondate) {
			this.suggestiondate = suggestiondate;
		}

		public String getSuggestionstate() {
			return suggestionstate;
		}

		public void setSuggestionstate(String suggestionstate) {
			this.suggestionstate = suggestionstate;
		}

		public String getSuggestionresult() {
			return suggestionresult;
		}

		public void setSuggestionresult(String suggestionresult) {
			this.suggestionresult = suggestionresult;
		}

		public boolean getChecked() {
			return checked;
		}

		public void setChecked(boolean checked) {
			this.checked = checked;
		}

	}
}
