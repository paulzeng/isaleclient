package com.zjrc.isale.client.ui.activity;

/**
 * 项目名称：销售管家客户端
 * 版本号：V1.00
 * 创建者: 高林荣
 * 修改者:刘磊
 * 功能描述：库存上报/查看界面
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.zjrc.isale.client.R;
import com.zjrc.isale.client.application.ISaleApplication;
import com.zjrc.isale.client.bean.CommonUtils;
import com.zjrc.isale.client.bean.Constant;
import com.zjrc.isale.client.bean.StockObj;
import com.zjrc.isale.client.ui.adapter.ProductListAdapter2;
import com.zjrc.isale.client.ui.widgets.CustomDatePicker;
import com.zjrc.isale.client.util.xml.XmlNode;
import com.zjrc.isale.client.util.xml.XmlValueUtil;
import com.zjrc.isale.client.zxing.activity.CaptureActivity;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StockSubmitActivity extends BaseActivity implements View.OnClickListener, View.OnTouchListener {
	private Button btn_titlebar_back;
    private TextView tv_titlebar_title;
    private Button btn_titlebar_barcode;

    private TextView tv_stockterminal;
    private TextView tv_stockdate;
    private ListView lv_product;
    private Button btn_addproduct;
    private TextView tv_totalnum;
    private TextView tv_totalprice;
    private Button btn_ok;

    private String operate;

    //所选择的销售网点ID号
    private String terminalid;
    private String terminalname;

    private ProductListAdapter2 productlistadapter;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            StockSubmitActivity.this.updateSummary();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.stock_submit);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar_small);

        btn_titlebar_back = (Button) findViewById(R.id.btn_titlebar_back);
        btn_titlebar_back.setOnClickListener(this);

        tv_titlebar_title = (TextView) findViewById(R.id.tv_titlebar_title);
        tv_titlebar_title.setText(R.string.stock_submit);

        btn_titlebar_barcode = (Button) findViewById(R.id.btn_titlebar_barcode);
        btn_titlebar_barcode.setVisibility(View.VISIBLE);
        btn_titlebar_barcode.setOnClickListener(this);

        tv_stockterminal = (TextView) findViewById(R.id.tv_stockterminal);
        tv_stockterminal.setOnClickListener(this);

        tv_stockdate = (TextView) findViewById(R.id.tv_stockdate);
        tv_stockdate.setOnTouchListener(this);

        lv_product = (ListView) findViewById(R.id.lv_product);
        productlistadapter = new ProductListAdapter2(getLayoutInflater(), mHandler);
        lv_product.setAdapter(productlistadapter);

        btn_addproduct = (Button) findViewById(R.id.btn_addproduct);
        btn_addproduct.setOnClickListener(this);

        tv_totalnum = (TextView) findViewById(R.id.tv_totalnum);

        tv_totalprice = (TextView) findViewById(R.id.tv_totalprice);

        btn_ok = (Button) findViewById(R.id.btn_ok);
        btn_ok.setOnClickListener(this);

        Bundle bundle = getIntent().getExtras();
        operate = bundle != null ? bundle.getString("operate") : "insert";
        if (operate.equalsIgnoreCase("insert")) {
        } else if (operate.equalsIgnoreCase("modify")) {
        }
    }

    @Override
    public void onClick(View view) {
    	if (CommonUtils.isNotFastDoubleClick()) {
    		switch (view.getId()) {
    		case R.id.btn_titlebar_back:
    			finish();
    			break;
    		case R.id.btn_titlebar_barcode:
    			Intent openCameraIntent = new Intent(StockSubmitActivity.this, CaptureActivity.class);
    			startActivityForResult(openCameraIntent, Constant.BARCODE_SCAN);
    			break;
    			
    		case R.id.tv_stockterminal:
    			Intent intent = new Intent();
    			Bundle bundle = new Bundle();
    			bundle.putString("terminalid", terminalid);
    			intent.putExtras(bundle);
    			intent.setClass(StockSubmitActivity.this, TerminalSelectActivity.class);
    			startActivityForResult(intent, Constant.RESULT_TEMINAL_SELECT);
    			break;
    		case R.id.btn_addproduct:
    			if (TextUtils.isEmpty(terminalid) || TextUtils.isEmpty(terminalname)) {
    				showAlertDialog("提示", "请选择客户!", new View.OnClickListener() {
    					@Override
    					public void onClick(View arg0) {
    						alertDialog.cancel();
    					}
    				}, "确定", null, null);
    			} else {
    				Intent intent1 = new Intent();
    				Bundle bundle1 = new Bundle();
    				bundle1.putString("type", "stock");
    				bundle1.putString("terminalid", terminalid);
    				intent1.putExtras(bundle1);
    				intent1.setClass(StockSubmitActivity.this, ProductItemActivity.class);
    				startActivityForResult(intent1, Constant.RESULT_PRODUCTITEM_REFRESH);
    			}
    			break;
    		case R.id.btn_ok:
    			if (validityInput()) {
    				sendSubmit();
    			}
    			break;
    		}
    	}
    }
    CustomDatePicker datePicker;
    @Override
    public boolean onTouch(final View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
             	datePicker = new CustomDatePicker(StockSubmitActivity.this,
						"请输入入库时间", new View.OnClickListener() {

							@Override
							public void onClick(View arg0) {
								((TextView) v).setText(datePicker.getYear() + "-"
										+ datePicker.getMonth() + "-"
										+ datePicker.getDay());
								datePicker.dismiss();

							}
						}, new View.OnClickListener() {

							@Override
							public void onClick(View arg0) {
								datePicker.dismiss();
							}
						},((TextView) v).getText().toString());
				datePicker.show();
                break;
            default:
                break;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if (requestCode == Constant.BARCODE_SCAN) {
            if (resultCode == Activity.RESULT_OK) {
                Bundle bundle = data.getExtras();
                String scanResult = bundle.getString("result");
                if (scanResult != null) {
                    sendQueryTerminalDetail(scanResult);
                }
            }
        } else if (requestCode == Constant.RESULT_TEMINAL_SELECT) {
            if (resultCode == Activity.RESULT_OK) {
                Bundle bundle = data.getExtras();
                terminalid = bundle.getString("terminalid");
                terminalname = bundle.getString("terminalname");
                tv_stockterminal.setText(terminalname);
            }
        } else if (requestCode == Constant.RESULT_PRODUCTITEM_REFRESH) {
            if (resultCode == Activity.RESULT_OK) {
                Bundle extras = data.getExtras();
                ArrayList<Bundle> products = (ArrayList<Bundle>) extras.getSerializable("products");
                if (products != null && products.size() > 0) {
                    for (Bundle bundle : products) {
                        String productid = bundle.getString("productid");
                        String productname = bundle.getString("productname");
                        String brandname = bundle.getString("brandname");
                        String categoryname = bundle.getString("categoryname");
                        String productnorm = bundle.getString("productnorm");
                        String productprice = bundle.getString("productprice");
                        String productnum = bundle.getString("productnum");
                        String producttotalprice = bundle.getString("producttotalprice");
                        productlistadapter.addItem(productid, productname, brandname, categoryname, productnorm, productprice, productnum, producttotalprice);
                    }
                    productlistadapter.notifyDataSetChanged();
                    updateSummary();
                }
            }
        }
    }
    
    private void sendQueryTerminalDetail(String code){
		ISaleApplication application = (ISaleApplication)getApplication();
		if (application!=null && application.getConfig()!=null && application.getConfig().getUserid()!=null){
			Map<String, String> params = new HashMap<String, String>();
            params.put("terminalid", "");
            params.put("companyid", application.getConfig().getCompanyid());
            params.put("terminalcode", code);
            request("terminal!detail?code=" + Constant.TERMINAL_DETAIL, params, FLAG_SHOW_PROGRESS | FLAG_SHOW_ERROR | FLAG_CANCEL);
		}
	}

    private boolean validityInput() {
        if (TextUtils.isEmpty(terminalid)) {
        	showAlertDialog("提示", "请选择上报库存的网点!", new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					alertDialog.cancel();
				}
			}, "确定", null, null);
            tv_stockterminal.requestFocus();
            return false;
        } else if (TextUtils.isEmpty(tv_stockdate.getText())) {
           	showAlertDialog("提示", "请选择盘点日期!", new View.OnClickListener() {
        				@Override
        				public void onClick(View arg0) {
        					alertDialog.cancel();
        				}
        			}, "确定", null, null);
            tv_stockdate.requestFocus();
            return false;
        } else if (productlistadapter.getList().size() == 0) {
           	showAlertDialog("提示", "请添加产品!", new View.OnClickListener() {
        				@Override
        				public void onClick(View arg0) {
        					alertDialog.cancel();
        				}
        			}, "确定", null, null);
            lv_product.requestFocus();
            return false;
        }

        String saletotalprice = tv_totalprice.getText().toString().replaceAll("[^\\d\\.]*", "");
        if (Double.valueOf(saletotalprice) > 999999999) {
        	showAlertDialog("提示", "最大订货金额不能超过999999999!", new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					alertDialog.cancel();
				}
			}, "确定", null, null);
            tv_totalprice.requestFocus();
            return false;
        }
        return true;
    }
    
    private void sendSubmit() {
        ISaleApplication application = (ISaleApplication) getApplication();
        if (application != null) {
            HashMap<String, Object> params = new HashMap<String, Object>();
            params.put("userid", XmlValueUtil.encodeString(application.getConfig()
                    .getUserid()));
            params.put("operate",XmlValueUtil.encodeString(operate));
            params.put("terminalid",XmlValueUtil.encodeString(terminalid));
            params.put("date", XmlValueUtil.encodeString(tv_stockdate.getText().toString()) );
            params.put("totalprice",XmlValueUtil.encodeString(tv_totalprice.getText().toString().replaceAll("[^\\d\\.]*", "")));
            params.put("stockid", "");
            List<ProductListAdapter2.ProductItem> items = productlistadapter.getList();
            ArrayList<StockObj>StockObjArrayList = new ArrayList<StockObj>();
            if(items!=null){
                for (ProductListAdapter2.ProductItem item : items) {
                    StockObj saleObj = new StockObj();
                    saleObj.setDetailid("");
                    saleObj.setProductid(XmlValueUtil.encodeString(item.getProductid()));
                    saleObj.setStocknum(XmlValueUtil.encodeString(item.getProductnum()));
                    saleObj.setStockprice(XmlValueUtil.encodeString(item.getProductprice()));
                    saleObj.setStocktotalprice(XmlValueUtil.encodeString(item.getProducttotalprice()));
                    StockObjArrayList.add(saleObj);
                }
            }
            params.put("records", StockObjArrayList);
            request("corpstock!submit?code=" + Constant.STOCK_SUBMIT, params, FLAG_SHOW_PROGRESS | FLAG_SHOW_ERROR | FLAG_CANCEL);
        }
    }

    @Override
    public void onRecvData(XmlNode response) {
       
    }
    
    private void updateSummary() {
        int itotalenum = 0;
        double dtotalprice = 0;
        List<ProductListAdapter2.ProductItem> items = productlistadapter.getList();
        for (ProductListAdapter2.ProductItem item : items) {
            int iproductnum = 0;
            try {
                iproductnum = Integer.parseInt(item.getProductnum());
            } catch (Exception e) {
            }
            itotalenum += iproductnum;

            double dproducttotalprice = 0;
            try {
                dproducttotalprice = Double.parseDouble(item.getProducttotalprice());
            } catch (Exception e) {
            }
            dtotalprice += dproducttotalprice;
        }
        tv_totalnum.setText("共" + itotalenum + "件");

        BigDecimal bd = new BigDecimal(dtotalprice);
        DecimalFormat df = new DecimalFormat("#0.00");
        tv_totalprice.setText("￥" + df.format(bd));
    }
    @Override
    public void onRecvData(JsonObject response) {
    	if (response != null) {
            String code = response.get("code").getAsString();
            if (Constant.TERMINAL_DETAIL.equals(code)) {//网点详情
            	JsonObject backorder = response.getAsJsonObject("body");
                if(backorder != null) {
                    terminalid = backorder.get("id").getAsString();
    				terminalname = backorder.get("name").getAsString();
    				tv_stockterminal.setText(terminalname);
                }
            }else  if (Constant.STOCK_SUBMIT.equals(code)) {
                Toast.makeText(this,"库存上报成功!", Toast.LENGTH_SHORT).show();
                setResult(Activity.RESULT_OK);
                finish();
            }
        }
    }
}
