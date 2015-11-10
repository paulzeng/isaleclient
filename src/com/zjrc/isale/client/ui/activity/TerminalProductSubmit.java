package com.zjrc.isale.client.ui.activity;

/**
 * 项目名称：销售管家客户端
 * 版本号：V2.00
 * 创建者: 高林荣
 * 修改者: 陈浩
 * 功能描述：进店品项上报界面
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.zjrc.isale.client.R;
import com.zjrc.isale.client.application.ISaleApplication;
import com.zjrc.isale.client.bean.CommonUtils;
import com.zjrc.isale.client.bean.Constant;
import com.zjrc.isale.client.ui.adapter.TerminalProductListAdapter;
import com.zjrc.isale.client.util.xml.XmlNode;
import com.zjrc.isale.client.util.xml.XmlValueUtil;
import com.zjrc.isale.client.zxing.activity.CaptureActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TerminalProductSubmit extends BaseActivity implements View.OnClickListener {
    private Button btn_titlebar_back;
    private TextView tv_titlebar_title;
    private Button btn_titlebar_barcode;

    public RelativeLayout rl_container;
    private TextView tv_terminal;
    private ListView lv_product;
    private Button btn_addproduct;
    private Button btn_ok;

    //所选择的销售网点ID号
    private String terminalid;

    private String terminalcode;

    private String terminalname;

    private TerminalProductListAdapter terminalproductlistadapter;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // 刷新主容器，否则会有残影，原因未知
            rl_container.invalidate();
        }
    };

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.terminalproduct_submit);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar_small);

        btn_titlebar_back = (Button) findViewById(R.id.btn_titlebar_back);
        btn_titlebar_back.setOnClickListener(this);

        tv_titlebar_title = (TextView) findViewById(R.id.tv_titlebar_title);
        tv_titlebar_title.setText(R.string.terminalproduct_submit);

        btn_titlebar_barcode = (Button) findViewById(R.id.btn_titlebar_barcode);
        btn_titlebar_barcode.setVisibility(View.VISIBLE);
        btn_titlebar_barcode.setOnClickListener(this);

        rl_container = (RelativeLayout) findViewById(R.id.rl_container);

        tv_terminal = (TextView) findViewById(R.id.tv_terminal);
        tv_terminal.setOnClickListener(this);

        lv_product = (ListView) findViewById(R.id.lv_product);
        terminalproductlistadapter = new TerminalProductListAdapter(getLayoutInflater(), mHandler);
        lv_product.setAdapter(terminalproductlistadapter);

        btn_addproduct = (Button) findViewById(R.id.btn_addproduct);
        btn_addproduct.setOnClickListener(this);

        btn_ok = (Button) findViewById(R.id.btn_ok);
        btn_ok.setOnClickListener(this);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            terminalid = bundle.getString("terminalid");
            terminalname = bundle.getString("terminalname");
            tv_terminal.setText(terminalname);
            if (!TextUtils.isEmpty(terminalid)) {
                getTerminalProduct();
            }
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
    			Intent openCameraIntent = new Intent(TerminalProductSubmit.this, CaptureActivity.class);
    			startActivityForResult(openCameraIntent, Constant.BARCODE_SCAN);
    			break;
    		case R.id.tv_terminal:
    			Intent intent = new Intent(TerminalProductSubmit.this, TerminalSelectActivity.class);
    			intent.setClass(TerminalProductSubmit.this, TerminalSelectActivity.class);
    			startActivityForResult(intent, Constant.RESULT_TEMINAL_SELECT);
    			break;
    		case R.id.btn_addproduct:
    			if (TextUtils.isEmpty(terminalid) || TextUtils.isEmpty(terminalname)) {
    				showAlertDialog("提示", "请选择销售网点!", new View.OnClickListener() {
    					@Override
    					public void onClick(View arg0) {
    						alertDialog.cancel();
    					}
    				}, "确定", null, null);
    			} else {
    				Intent addintent = new Intent();
    				Bundle bundle = new Bundle();
    				bundle.putString("productid", "");
    				bundle.putString("brandid", "");
    				bundle.putString("categoryid", "");
    				addintent.putExtras(bundle);
    				addintent.setClass(TerminalProductSubmit.this, ProductSelectActivity.class);
    				startActivityForResult(addintent, Constant.RESULT_PRODUCT_SELECT);
    			}
    			break;
    		case R.id.btn_ok:
    			if (validityInput()) {
    				submitTerminalProduct();
    			}
    			break;
    		}
    	}
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constant.BARCODE_SCAN) {
            if (resultCode == Activity.RESULT_OK) {
                Bundle bundle = data.getExtras();
                String scanResult = bundle.getString("result");
                if (scanResult != null) {
                    terminalcode = scanResult;
                    terminalproductlistadapter.clearItem();
                    terminalproductlistadapter.notifyDataSetChanged();
                    sendQueryTerminalDetail(terminalcode);
                }
            }
        } else if (requestCode == Constant.RESULT_TEMINAL_SELECT) {
            if (resultCode == Activity.RESULT_OK) {
                Bundle bundle = data.getExtras();
                if (bundle != null) {
                    terminalid = bundle.getString("terminalid");
                    terminalname = bundle.getString("terminalname");
                    tv_terminal.setText(terminalname);
                    terminalproductlistadapter.clearItem();
                    terminalproductlistadapter.notifyDataSetChanged();
                    getTerminalProduct();
                }
            }
        } else if (requestCode == Constant.RESULT_PRODUCT_SELECT) {
            if (resultCode == Activity.RESULT_OK) {
                Bundle bundle = data.getExtras();
                if (terminalproductlistadapter.existItem(bundle.getString("productid"))) {
                    Toast.makeText(this, "进店品项中已存在该产品!", Toast.LENGTH_SHORT).show();
                } else {
                    terminalproductlistadapter.addItem(
                            bundle.getString("productid"),
                            bundle.getString("productname"),
                            bundle.getString("brandname"),
                            bundle.getString("categoryname"),
                            bundle.getString("norm"),
                            bundle.getString("price"),
                            new SimpleDateFormat("yyyy-MM-dd").format(new Date())
                    );
                    terminalproductlistadapter.notifyDataSetChanged();
                }
            }
        }
    }

    /**
     * //	 * 校验数据有效性
     * //	 * @return
     * //
     */
    private boolean validityInput() {
        if ("".equalsIgnoreCase(terminalid)) {
            showAlertDialog("提示", "请选择上报进店品项的客户!", new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    alertDialog.cancel();
                }
            }, "确定", null, null);
            tv_terminal.requestFocus();
            return false;
        }
        if (terminalproductlistadapter.getList().size() == 0) {
            showAlertDialog("提示", "进店品项不能为空，请添加!", new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    alertDialog.cancel();
                }
            }, "确定", null, null);
            lv_product.requestFocus();
            return false;
        }
        return true;
    }

    private void sendQueryTerminalDetail(String code) {
        ISaleApplication application = (ISaleApplication) getApplication();
        if (application != null && application.getConfig() != null && application.getConfig().getUserid() != null) {
            Map<String, String> params = new HashMap<String, String>();
            params.put("terminalid", "");
            params.put("companyid", application.getConfig().getCompanyid());
            params.put("terminalcode", code);
            request("terminal!detail?code=" + Constant.TERMINAL_DETAIL, params, FLAG_SHOW_PROGRESS | FLAG_SHOW_ERROR | FLAG_CANCEL);
        }
    }

    /**
     * 发送查询销售网点的进店品项请求
     */
    private void getTerminalProduct() {
        ISaleApplication application = (ISaleApplication)getApplication();
        if (application != null) {
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("userid",XmlValueUtil.encodeString(application.getConfig().getUserid()));
            params.put("terminalid",XmlValueUtil.encodeString(terminalid));
            params.put("productcode","");
            params.put("productbrand","");
            params.put("productcategory","");
            params.put("productname","");
            request("terminalproduct!list?code=" + Constant.TERMINALPRODUCT_QUERY, params, FLAG_SHOW_PROGRESS | FLAG_SHOW_ERROR | FLAG_CANCEL);
        }
    }

    private void submitTerminalProduct() {
        ISaleApplication application = (ISaleApplication) getApplication();
        if (application != null && application.getConfig() != null && application.getConfig().getUserid() != null) {
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("userid",  XmlValueUtil.encodeString(application.getConfig().getUserid()));
            params.put("terminalid",  XmlValueUtil.encodeString(terminalid));
            StringBuilder productid  = new StringBuilder();
            StringBuilder intodate  = new StringBuilder();
            List<TerminalProductListAdapter.ProductItem> items = terminalproductlistadapter.getList();
            for (int i =0;i<items.size();i++) {
                productid.append(XmlValueUtil.encodeString(items.get(i).getProductid()));
                intodate.append(XmlValueUtil.encodeString(items.get(i).getProductintodate()));
                if(i!=items.size()-1){
                    productid.append(",");
                    intodate.append(",");
                }
            }
            params.put("productid",  productid.toString());
            params.put("intodate",  intodate.toString());
            request("terminalproduct!submit?code=" + Constant.TERMINALPRODUCT_SUBMIT, params, FLAG_SHOW_PROGRESS | FLAG_SHOW_ERROR | FLAG_CANCEL);
        }

    }

    @Override
    public void onRecvData(XmlNode response) {
    }
    
    @Override
    public void onRecvData(JsonObject response) {
        if (response != null) {
            String code = response.get("code").getAsString();
            if (Constant.TERMINALPRODUCT_SUBMIT.equals(code)) {
                Toast.makeText(this, "进店品项上报成功!", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            } else if(Constant.TERMINALPRODUCT_QUERY.equals(code)){
                JsonArray terminalproducts = response.getAsJsonArray("body");
                if (terminalproducts != null) {
                    for (JsonElement productElem : terminalproducts) {
                        JsonObject productObj = (JsonObject) productElem;
                        terminalproductlistadapter.addItem(
                                productObj.get("id").getAsString(),
                                productObj.get("name").getAsString(),
                                productObj.get("brandname").getAsString(),
                                productObj.get("categoryname").getAsString(),
                                productObj.get("norm").getAsString(),
                                productObj.get("price").getAsString(),
                                productObj.get("intodate").getAsString());
                    }
                    terminalproductlistadapter.notifyDataSetChanged();
                }
            } else if (Constant.TERMINAL_DETAIL.equals(code)) {//网点详情
            	JsonObject backorder = response.getAsJsonObject("body");
                if(backorder != null) {
                    terminalid = backorder.get("id").getAsString();
    				terminalname = backorder.get("name").getAsString();
    				tv_terminal.setText(terminalname);
                }
            }
        }
    }
}

//	//窗体标题
//	private Button btn_titlebar_back;
//	private TextView tv_titlebar_title;
//	private Button btn_titlebar_search;
//
//	//提交按钮
//	private Button btn_ok;
//
//	//取消按钮
//	private Button btn_cancel;
//
//	//所选择的销售网点ID号
//	private String terminalid;
//
//	private String terminalcode;
//
//	//所选择的销售网点名称
//	private String terminalname;
//
//	//销售网点显示控件
//	private EditText et_terminal;
//
//	//销售网点选择按钮
//	private Button btn_terminalselect;
//
//	//销售网点进店品项显示列表
//	private ListView lv_product;
//
//	//添加进店品项按钮
//	private Button btn_addproduct;
//
//	//删除进店品项按钮
//	private Button btn_deleteproduct;
//
//	//销售网点进店品项列表Adapter
//	private TerminalProductListAdapter terminalproductlistadapter;
//
//	/**
//	 * 窗体初始化
//	 */
//	@Override
//	public void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
//		setContentView(R.layout.terminalproduct_submit);
//		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.titlebarsearch_small);
//		btn_titlebar_back = (Button) findViewById(R.id.btn_titlebar_back);
//		tv_titlebar_title = (TextView) findViewById(R.id.tv_titlebar_title);
//		btn_titlebar_search = (Button) findViewById(R.id.btn_titlebar_search);
//
//		btn_titlebar_back.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View arg0) {
//				finish();
//			}
//		});
//		btn_titlebar_search.setBackgroundResource(R.drawable.titlebar_2barcode);
//		btn_titlebar_search.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View arg0) {
//				terminalid = "";
//				terminalcode = "";
//				terminalname = "";
//				et_terminal.setText("");
//				Intent openCameraIntent = new Intent(TerminalProductSubmit.this, CaptureActivity.class);
//				startActivityForResult(openCameraIntent, Constant.BARCODE_SCAN);
//			}
//		});
//		tv_titlebar_title.setText(R.string.terminalproduct_submit);
//
//		et_terminal = (EditText)findViewById(R.id.et_terminal);
//
//		Intent intent = getIntent();
//		Bundle bundle = intent.getExtras();
//		terminalid = "";
//		terminalcode = "";
//		terminalname = "";
//		if (bundle!=null){
//			terminalid = bundle.getString("terminalid");
//			terminalname = bundle.getString("terminalname");
//		}
//
//		//判断是否有销售网点ID
//		if (!terminalid.equalsIgnoreCase("") && !terminalname.equalsIgnoreCase("")){
//			et_terminal.setText(terminalname);
//			getTerminalProduct(terminalid);
//		}
//
//		btn_terminalselect = (Button)findViewById(R.id.btn_terminalselect);
//
//		btn_terminalselect.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View arg0) {
//				Intent intent = new Intent();
//				Bundle bundle = new Bundle();
//				bundle.putString("terminalid", terminalid);
//				intent.putExtras(bundle);
//				intent.setClass(TerminalProductSubmit.this, TerminalSelectActivity.class);
//				startActivityForResult(intent,Constant.RESULT_TEMINAL_SELECT);
//			}
//		});
//
//		lv_product = (ListView)findViewById(R.id.lv_product);
//		lv_product.setVerticalScrollBarEnabled(true);
//		lv_product.setHorizontalScrollBarEnabled(true);
//
//		terminalproductlistadapter = new TerminalProductListAdapter(getLayoutInflater());
//		lv_product.setAdapter(terminalproductlistadapter);
//		lv_product.setOnItemClickListener(new OnItemClickListener(){
//
//			@Override
//			public void onItemClick(AdapterView<?> arg0, View arg1,	int arg2, long arg3) {
//				ProductItem item = (ProductItem) terminalproductlistadapter.getItem(arg2);
//				if (item != null) {
//					item.setChecked(!item.getChecked());
//					terminalproductlistadapter.notifyDataSetChanged();
//				}
//
//			}
//		});
//
//		btn_addproduct = (Button)findViewById(R.id.btn_addproduct);
//
//		btn_addproduct.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View arg0) {
//				if (terminalid.equalsIgnoreCase("") || terminalname.equalsIgnoreCase("")){
//					Builder b = new AlertDialog.Builder(TerminalProductSubmit.this).setTitle("提示").setMessage("请选择销售网点!");
//					b.setPositiveButton("确定", new DialogInterface.OnClickListener() {
//						public void onClick(DialogInterface dialog, int whichButton) {
//							dialog.cancel();
//						}
//					}).show();
//				}else{
//					Intent intent = new Intent();
//					Bundle bundle = new Bundle();
//					bundle.putString("productid", "");
//					bundle.putString("brandid", "");
//					bundle.putString("categoryid", "");
//					intent.putExtras(bundle);
//					intent.setClass(TerminalProductSubmit.this, ProductSelectActivity.class);
//					startActivityForResult(intent,Constant.RESULT_PRODUCT_SELECT);
//				}
//			}
//		});
//
//		btn_deleteproduct = (Button)findViewById(R.id.btn_deleteproduct);
//
//		btn_deleteproduct.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View arg0) {
//				List<ProductItem> items = terminalproductlistadapter.getList();
//				int selectcount = 0;
//				for (ProductItem item:items){
//					if (item.getChecked()){
//						selectcount++;
//					}
//				}
//				if (selectcount>0){
//					List<ProductItem> productitems =  new ArrayList<ProductItem>();
//					for (ProductItem item:items){
//						productitems.add(item);
//					}
//					terminalproductlistadapter.getList();
//					for (ProductItem item:items){
//						if (item.getChecked()){
//							productitems.remove(item);
//						}
//					}
//					terminalproductlistadapter.setList(productitems);
//					terminalproductlistadapter.notifyDataSetChanged();
//				}else{
//					Builder b = new AlertDialog.Builder(TerminalProductSubmit.this).setTitle("提示").setMessage("请选择要删除的进店品项!");
//					b.setPositiveButton("确定", new DialogInterface.OnClickListener() {
//						public void onClick(DialogInterface dialog, int whichButton) {
//							dialog.cancel();
//						}
//					}).show();
//				}
//			}
//		});
//
//		btn_ok = (Button)findViewById(R.id.btn_ok);
//		btn_ok.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View arg0) {
//				if (validityInput()){
//					submitTerminalProduct();
//				}
//			}
//		});
//		btn_cancel = (Button)findViewById(R.id.btn_cancel);
//		btn_cancel.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View arg0) {
//				finish();
//			}
//		});
//		tv_titlebar_title.requestFocus();
//	}
//
//	@Override
//	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		if(requestCode == Constant.BARCODE_SCAN){
//			if (resultCode == Activity.RESULT_OK){
//				Bundle bundle = data.getExtras();
//				String scanResult = bundle.getString("result");
//				if (scanResult != null) {
//					terminalcode = scanResult;
//					terminalproductlistadapter.clearItem();
//					terminalproductlistadapter.notifyDataSetChanged();
//					sendQueryTerminalDetail(terminalcode);
//				}
//			}
//		}else if(requestCode == Constant.RESULT_TEMINAL_SELECT){
//			if (resultCode == Activity.RESULT_OK){
//				Bundle bundle = data.getExtras();
//				if (bundle!=null){
//					terminalid = bundle.getString("terminalid");
//					terminalname = bundle.getString("terminalname");
//					et_terminal.setText(terminalname);
//					terminalproductlistadapter.clearItem();
//					terminalproductlistadapter.notifyDataSetChanged();
//					getTerminalProduct(terminalid);
//				}
//			}
//		}else if(requestCode == Constant.RESULT_PRODUCT_SELECT){
//			if (resultCode == Activity.RESULT_OK){
//				Bundle bundle = data.getExtras();
//				if (terminalproductlistadapter.existItem(bundle.getString("productid"))){
//					Toast.makeText(this, "进店品项中已存在该产品!", Toast.LENGTH_SHORT).show();
//				}else{
//					terminalproductlistadapter.addItem(bundle.getString("productid"),bundle.getString("productname"),bundle.getString("brandname"),
//							                           bundle.getString("categoryname"),bundle.getString("norm"),bundle.getString("price"),
//							                           new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
//					terminalproductlistadapter.notifyDataSetChanged();
//				}
//			}
//		}
//		super.onActivityResult(requestCode, resultCode, data);
//	}
//
//	private void sendQueryTerminalDetail(String code){
//		ISaleApplication application = (ISaleApplication)getApplication();
//		if (application!=null && application.getConfig()!=null && application.getConfig().getUserid()!=null){
//			String srequest = "<?xml version=\"1.0\" encoding=\"utf-8\"?>";
//			srequest += "<root>";
//			srequest += "<functionno>"+Constant.TERMINAL_DETAIL+"</functionno>";
//			srequest += "<terminalid></terminalid>";
//			srequest += "<companyid>"+application.getConfig().getCompanyid()+"</companyid>";
//			srequest += "<terminalcode>"+XmlValueUtil.encodeString(code)+"</terminalcode>";
//			srequest += "</root>";
//			XmlNode requestxml = XmlParser.parserXML(srequest,"UTF-8");
//			sendRequest(requestxml);
//		}
//	}
//
//	/**
//	 * 发送查询销售网点的进店品项请求
//	 * @param terminalid 销售网点ID
//	 */
//	private void getTerminalProduct(String terminalid){
//		ISaleApplication application = (ISaleApplication)getApplication();
//		if (application!=null && application.getConfig()!=null && application.getConfig().getUserid()!=null){
//			String srequest = "<?xml version=\"1.0\" encoding=\"utf-8\"?>";
//			srequest += "<root>";
//			srequest += "<functionno>"+Constant.TERMINALPRODUCT_QUERY+"</functionno>";
//			srequest += "<userid>"+XmlValueUtil.encodeString(application.getConfig().getUserid())+"</userid>";
//			srequest += "<terminalid>"+XmlValueUtil.encodeString(terminalid)+"</terminalid>";
//			srequest += "<productcode></productcode>";
//			srequest += "</root>";
//			XmlNode requestxml = XmlParser.parserXML(srequest,"UTF-8");
//			sendRequest(requestxml);
//		}
//	}
//
//	/**
//	 * 校验数据有效性
//	 * @return
//	 */
//	private boolean validityInput() {
//		if ("".equalsIgnoreCase(terminalid)) {
//			Builder b = new AlertDialog.Builder(this).setTitle("提示").setMessage("请选择上报进店品项的网点!");
//			b.setPositiveButton("确定", new DialogInterface.OnClickListener() {
//				public void onClick(DialogInterface dialog, int whichButton) {
//					dialog.cancel();
//				}
//			}).show();
//			et_terminal.requestFocus();
//			return false;
//		}
//		if (terminalproductlistadapter.getList().size()==0){
//			Builder b = new AlertDialog.Builder(this).setTitle("提示").setMessage("进店品项不能为空，请添加!");
//			b.setPositiveButton("确定", new DialogInterface.OnClickListener() {
//				public void onClick(DialogInterface dialog, int whichButton) {
//					dialog.cancel();
//				}
//			}).show();
//			lv_product.requestFocus();
//			return false;
//		}
//		return true;
//	}
//
//	private void submitTerminalProduct(){
//		ISaleApplication application = (ISaleApplication)getApplication();
//		if (application!=null && application.getConfig()!=null && application.getConfig().getUserid()!=null){
//			String srequest = "<?xml version=\"1.0\" encoding=\"utf-8\"?>";
//			srequest += "<root>";
//			srequest += "<functionno>"+Constant.TERMINALPRODUCT_SUBMIT+"</functionno>";
//			srequest += "<userid>"+XmlValueUtil.encodeString(application.getConfig().getUserid())+"</userid>";
//			srequest += "<terminalid>"+XmlValueUtil.encodeString(terminalid)+"</terminalid>";
//			srequest += "<records>";
//			List<ProductItem> items = terminalproductlistadapter.getList();
//			for (ProductItem item:items){
//				srequest += "<record>";
//				srequest += "<productid>"+XmlValueUtil.encodeString(item.getProductid())+"</productid>";
//				srequest += "<intodate>"+XmlValueUtil.encodeString(item.getProductintodate())+"</intodate>";
//				srequest += "</record>";
//			}
//			srequest += "</records>";
//			srequest += "</root>";
//			XmlNode requestxml = XmlParser.parserXML(srequest,"UTF-8");
//			sendRequest(requestxml);
//		}
//	}
//
//	/**
//	 * Socket接收到数据
//	 */
//	@Override
//	public void onRecvData(XmlNode response) {
//		if (response!=null){
//			String functionno = response.getText("root.functionno");
//			if (functionno!=null){
//				if (functionno.equalsIgnoreCase(Constant.TERMINALPRODUCT_QUERY)){//销售网点进店品项查询
//					XmlNode noderecords = response.getChildNode("records");
//					if (noderecords != null) {
//						ArrayList<XmlNode> records = noderecords.getChildNodeSet("record");
//						if (records != null) {
//							for (XmlNode record : records) {
//								terminalproductlistadapter.addItem(record.getChildNodeText("id"), record.getChildNodeText("name"), record.getChildNodeText("brandname"), record.getChildNodeText("categoryname"),
//																   record.getChildNodeText("norm"), record.getChildNodeText("price"), record.getChildNodeText("intodate"));
//							}
//							terminalproductlistadapter.notifyDataSetChanged();
//						}
//					}
//				}else if (functionno.equalsIgnoreCase(Constant.TERMINALPRODUCT_SUBMIT)){//销售网点进店品项上报
//					Toast.makeText(this, "进店品项上报成功!", Toast.LENGTH_SHORT).show();
//					finish();
//				}else if (functionno.equalsIgnoreCase(Constant.TERMINAL_DETAIL)){//网点详情
//					terminalid =  response.getChildNodeText("id");
//					terminalname = response.getChildNodeText("name");
//					et_terminal.setText(terminalname);
//					getTerminalProduct(terminalid);
//				}
//			}
//		}
//	}
