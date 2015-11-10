package com.zjrc.isale.client.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.zjrc.isale.client.R;
import com.zjrc.isale.client.ui.activity.PlanPatrolSubmitActivity.PicItem;

import java.util.ArrayList;

public class PlanPicsAdapter extends BaseAdapter {

	private ArrayList<PicItem> pics;
	private Context context;

	public PlanPicsAdapter(Context context, ArrayList<PicItem> pics) {
		this.pics = pics;
		this.context = context;
	}

	@Override
	public int getCount() {
		return pics.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(final int index, View arg1, ViewGroup arg2) {
		View v;
		ViewHolder viewHolder;
		if (arg1 == null) {
			viewHolder = new ViewHolder();
			v = ((Activity) context).getLayoutInflater().inflate(
					R.layout.plan_pic_item, null);
			viewHolder.pic = (ImageView) v.findViewById(R.id.iv_plan_item_pic);
			viewHolder.delete_pic = (ImageView) v
					.findViewById(R.id.iv_plan_item_delete_pic);
			v.setTag(viewHolder);
		} else {
			v = arg1;
			viewHolder = (ViewHolder) v.getTag();
		}
		if (pics.get(index).type == 0) {
			viewHolder.pic.setImageResource(R.drawable.v2_add_pic);
			viewHolder.delete_pic.setVisibility(View.GONE);
		} else {
			viewHolder.delete_pic.setVisibility(View.VISIBLE);
			viewHolder.pic.setImageBitmap(BitmapFactory.decodeFile(pics
					.get(index).fileName));
			viewHolder.delete_pic
					.setOnClickListener(new ImageView.OnClickListener() {

						@Override
						public void onClick(View arg0) {
							if (pics.size() == 1) {
								pics.get(0).des = "";
								pics.get(0).fileName = "";
								pics.get(0).fileType = "";
								pics.get(0).id = "";
								pics.get(0).type = 0;
							} else if (pics.size() == 2) {
								pics.remove(index);
							}else if (pics.size() == 3){
								if (index==0) {
									PicItem item =new PicItem(pics.get(1).fileType, pics.get(1).des, pics.get(1).fileName, pics.get(1).id, pics.get(1).type);
									pics.get(1).fileType = pics.get(0).fileType;
									if (pics.get(pics.size()-1).type!=0) {
										pics.get(2).fileType =item.fileType;
									}
									pics.remove(0);
									Log.i("info", "0:"+pics.get(0).fileType);
									Log.i("info", "1:"+pics.get(1).fileType);
								}
								if (index==1) {
									PicItem item = pics.get(1);
									if (pics.get(pics.size()-1).type!=0) {
										pics.get(2).fileType =item.fileType;
									}
									pics.remove(1);
								}
                                if (index==2) {
                                    pics.remove(2);
                                }
								if (pics.get(pics.size()-1).type!=0) {
									PicItem  item = new PicItem("", "", "", "", 0);
									pics.add(item);
								}
								
							}
							notifyDataSetChanged();

						}
					});
		}
		return v;
	}

	class ViewHolder {
		ImageView pic;
		ImageView delete_pic;
	}

}
