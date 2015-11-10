package com.zjrc.isale.client.ui.adapter;

/**
 * 项目名称：销售管家客户端
 * 版本号：V1.00
 * 创建者: 高林荣
 * 功能描述：产品选择适配器
 */

import java.util.ArrayList;
import java.util.List;

import com.zjrc.isale.client.R;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ProductSelectAdapter extends BaseAdapter {
	
	private LayoutInflater inflater;
	
	private List<ProductItem> list;
	
	public ProductSelectAdapter(LayoutInflater inflater) {
		super();
		this.inflater = inflater;
		this.list = new ArrayList<ProductItem>();
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
		ProductItem item = list.get(position);
		View view;
		if (convertView != null) {
			view = convertView;
		} else {
			view = inflater.inflate(R.layout.productselect_list_item, parent, false);
		}
		ItemViewHolder holder = (ItemViewHolder) view.getTag();
		if (holder == null) {
			holder = new ItemViewHolder();
			holder.tv_productname = (TextView) view.findViewById(R.id.tv_productname);
			holder.tv_productbrandname = (TextView) view.findViewById(R.id.tv_productbrandname);
			holder.tv_productcategoryname = (TextView) view.findViewById(R.id.tv_productcategoryname);
			view.setTag(holder);
		}	
		if (item!=null){
			holder.tv_productname.setText(item.getProductname());
			holder.tv_productbrandname.setText(item.getProductbrandname());
			holder.tv_productcategoryname.setText(item.getProductcategoryname());
		}
		return view;
	}
	
	public void addItem(String productid,String productname, String productbrandid, String productbrandname, String productcategoryid, String productcategoryname,
			            String productnorm, String productprice){
		ProductItem item = new ProductItem();
		item.setProductid(productid);
		item.setProductname(productname);
		item.setProductbrandid(productbrandid);
		item.setProductbrandname(productbrandname);
		item.setProductcategoryid(productcategoryid);
		item.setProductcategoryname(productcategoryname);
		item.setProductnorm(productnorm);
		item.setProductprice(productprice);	
		list.add(item);
	}
	
	public void clearItem(){
		list.clear();
	}
	
	public List<ProductItem> getList(){
		return list;
	}	
	
	protected class ItemViewHolder {	
		public TextView tv_productname;
		public TextView tv_productbrandname;
		public TextView tv_productcategoryname;
	}	
	
	public class ProductItem{
		private String productid;
		private String productname;
		private String productbrandid;
		private String productbrandname;
		private String productcategoryid;
		private String productcategoryname;
		private String productnorm;
		private String productprice;

		public String getProductid() {
			return productid;
		}
		public void setProductid(String productid) {
			this.productid = productid;
		}
		public String getProductname() {
			return productname;
		}
		public void setProductname(String productname) {
			this.productname = productname;
		}
		public String getProductbrandid() {
			return productbrandid;
		}
		public void setProductbrandid(String productbrandid) {
			this.productbrandid = productbrandid;
		}
		public String getProductbrandname() {
			return productbrandname;
		}
		public void setProductbrandname(String productbrandname) {
			this.productbrandname = productbrandname;
		}
		public String getProductcategoryid() {
			return productcategoryid;
		}
		public void setProductcategoryid(String productcategoryid) {
			this.productcategoryid = productcategoryid;
		}
		public String getProductcategoryname() {
			return productcategoryname;
		}
		public void setProductcategoryname(String productcategoryname) {
			this.productcategoryname = productcategoryname;
		}
		public String getProductnorm() {
			return productnorm;
		}
		public void setProductnorm(String productnorm) {
			this.productnorm = productnorm;
		}
		public String getProductprice() {
			return productprice;
		}
		public void setProductprice(String productprice) {
			this.productprice = productprice;
		}
	}
}
