package com.zjrc.isale.client.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.zjrc.isale.client.R;
import com.zjrc.isale.client.ui.widgets.menudrawer.Item;
import com.zjrc.isale.client.ui.widgets.menudrawer.User;

import java.util.List;

public class MenuAdapter extends BaseAdapter {

	public interface MenuListener {

		void onActiveViewChanged(View v);
	}

	private Context mContext;

	private List<Object> mItems;

	private MenuListener mListener;

	private int mActivePosition = -1;

	public MenuAdapter(Context context, List<Object> items) {
		mContext = context;
		mItems = items;
	}

	public void setListener(MenuListener listener) {
		mListener = listener;
	}

	public void setActivePosition(int activePosition) {
		mActivePosition = activePosition;
		for (int i = 0; i < mItems.size(); i++) {
			if (mItems.get(i) instanceof Item) {
				if (i == mActivePosition) {
					((Item) mItems.get(i)).selected = true;
				} else {
					((Item) mItems.get(i)).selected = false;
				}
			}
		}
		this.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return mItems.size();
	}

	@Override
	public Object getItem(int position) {
		return mItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		Object item = getItem(position);
		if (item instanceof User) {
			// if (v == null) {
			// v = LayoutInflater.from(mContext).inflate(
			// R.layout.v2_menu_row_item, parent, false);
			// }//android 2.x 版本会报空针
			v = LayoutInflater.from(mContext).inflate(
					R.layout.v2_menu_row_user, parent, false);
			TextView tv_name = (TextView) v.findViewById(R.id.tv_user_name);
			TextView tv_id = (TextView) v.findViewById(R.id.tv_user_id);
			tv_name.setText(((User) item).mName);
			tv_id.setText(((User) item).mId);
		} else {
			v = LayoutInflater.from(mContext).inflate(
					R.layout.v2_menu_row_item, parent, false);
//			if (v == null) {
//				v = LayoutInflater.from(mContext).inflate(
//						R.layout.v2_menu_row_item, parent, false);
//			}
			ImageView iv_image = (ImageView) v
					.findViewById(R.id.iv_menu_item_image);
			iv_image.setImageResource(((Item) item).mIconRes);
			TextView tv_text = (TextView) v
					.findViewById(R.id.tv_menu_item_text);
			tv_text.setText(((Item) item).mTitle);
			if (((Item) item).selected) {
				tv_text.setTextColor(mContext.getResources().getColor(
						R.color.v2_blue));
			} else {
				tv_text.setTextColor(mContext.getResources().getColor(
						R.color.v2_button));
			}
			TextView tv_text_alert = (TextView) v
					.findViewById(R.id.tv_menu_item_image_alert);
			if (((Item) item).mNum > 0) {
				tv_text_alert.setText(((Item) item).mNum > 99 ? "99+" : ""
						+ ((Item) item).mNum);
				tv_text_alert.setVisibility(View.VISIBLE);
			} else {
				tv_text_alert.setVisibility(View.GONE);
			}

		}
		if (position == mActivePosition) {
			mListener.onActiveViewChanged(v);
		}

		return v;
	}

}
