package com.zjrc.isale.client.ui.adapter;

/**
 * 项目名称：销售管家客户端
 * 版本号：V1.00
 * 创建者: 高林荣
 * 功能描述：通讯录适配器
 */

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.zjrc.isale.client.R;

public class ContactListAdapter extends BaseAdapter {
	private Context context;
	private LayoutInflater inflater;
	private List<ContactItem> list;

	public ContactListAdapter(Context context, LayoutInflater inflater) {
		this.context = context;
		this.inflater = inflater;
		this.list = new ArrayList<ContactItem>();
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
		ContactItem item = list.get(position);
		View view=null;
		if (convertView != null) {
			view = convertView;
		} else {
			view = inflater.inflate(R.layout.contact_list_item, parent, false);
		}
		ItemViewHolder holder = (ItemViewHolder) view.getTag();
		if (holder == null) {
			holder = new ItemViewHolder();
			holder.tv_contactname = (TextView) view.findViewById(R.id.tv_contactname);
			holder.tv_contactphoneno = (TextView) view.findViewById(R.id.tv_contactphoneno);
			holder.iv_message = (ImageView) view.findViewById(R.id.iv_message);
			holder.iv_call = (ImageView) view.findViewById(R.id.iv_call);
			holder.iv_ctd = (ImageView) view.findViewById(R.id.iv_ctd);
			view.setTag(holder);
		}	
		if (item != null){
			holder.tv_contactname.setText(item.getContactname());
			holder.tv_contactphoneno.setText(TextUtils.isEmpty(item.getContactphoneno()) ? "暂无联系方式" : item.getContactphoneno());
			holder.iv_message.setTag(item);
			holder.iv_message.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					ContactItem item = (ContactItem)arg0.getTag(); 
					if (!TextUtils.isEmpty(item.getContactphoneno())){
						Intent intent = new Intent(Intent.ACTION_SENDTO,Uri.fromParts("smsto", item.getContactphoneno(), null));
		                context.startActivity(intent);
					} else {
                        Toast.makeText(context, "联系方式不能为空！", Toast.LENGTH_SHORT).show();
                    }
				}
			});
			holder.iv_call.setTag(item);
			holder.iv_call.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					ContactItem item = (ContactItem)arg0.getTag();
					if (!TextUtils.isEmpty(item.getContactphoneno())){
						Intent intent = new Intent(Intent.ACTION_CALL,Uri.parse("tel://" + item.getContactphoneno()));
		                context.startActivity(intent);
					} else {
                        Toast.makeText(context, "联系方式不能为空！", Toast.LENGTH_SHORT).show();
                    }
				}
			});
			holder.iv_ctd.setTag(item);
			holder.iv_ctd.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
//					ContactItem item = (ContactItem)arg0.getTag();
//					if (!TextUtils.isEmpty(item.getContactphoneno())){
//						Intent intent = new Intent(Intent.ACTION_CALL,Uri.parse("tel://" + item.getContactphoneno()));
//		                context.startActivity(intent);
//					} else {
//                        Toast.makeText(context, "联系方式不能为空！", Toast.LENGTH_SHORT).show();
//                    }
				}
			});
		}
		return view;
	}

	public void addItem(String contactid, String contactname, String contactphoneno) {
		ContactItem item = new ContactItem();
		item.setContactid(contactid);
		item.setContactname(contactname);
		item.setContactphoneno(contactphoneno);
		list.add(item);
	}

	public void clearItem() {
		list.clear();
	}

	protected class ItemViewHolder {
		public TextView tv_contactname;
		public TextView tv_contactphoneno;
		public ImageView iv_message;
		public ImageView iv_call;
		public ImageView iv_ctd;
	}

	@SuppressWarnings("serial")
	public class ContactItem implements Serializable {

		private String contactid;

		private String contactname;

		private String contactphoneno;

		public String getContactid() {
			return contactid;
		}

		public void setContactid(String contactid) {
			this.contactid = contactid;
		}

		public String getContactname() {
			return contactname;
		}

		public void setContactname(String contactname) {
			this.contactname = contactname;
		}

		public String getContactphoneno() {
			return contactphoneno;
		}

		public void setContactphoneno(String contactphoneno) {
			this.contactphoneno = contactphoneno;
		}
	}
}
