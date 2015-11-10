package com.zjrc.isale.client.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.zjrc.isale.client.R;
import com.zjrc.isale.client.application.ISaleApplication;
import com.zjrc.isale.client.bean.CommonUtils;
import com.zjrc.isale.client.bean.Constant;
import com.zjrc.isale.client.util.DecimalDigitLimitFilter;
import com.zjrc.isale.client.util.xml.XmlNode;
import com.zjrc.isale.client.zxing.activity.CaptureActivity;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class ProductItemActivity extends BaseActivity {

    private Button btn_titlebar_back;
    private TextView tv_titlebar_title;
    private Button btn_titlebar_barcode;

    private Button btn_ok;

    private Button btn_next;

    private TextView tv_product;

    private EditText et_brand;

    private EditText et_category;

    private EditText et_norm;

    private EditText et_price;

    private EditText et_num;

    private EditText et_totalprice;

    private String type;

    private String terminalid;

    private String productid;

    private String productcode;

    private ArrayList<Bundle> products = new ArrayList<Bundle>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.product_item);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar_small);

        btn_titlebar_back = (Button) findViewById(R.id.btn_titlebar_back);
        btn_titlebar_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                finish();
            }
        });

        tv_titlebar_title = (TextView) findViewById(R.id.tv_titlebar_title);

        btn_titlebar_barcode = (Button) findViewById(R.id.btn_titlebar_barcode);
        btn_titlebar_barcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
            	if (CommonUtils.isNotFastDoubleClick()) {
            		productcode = "";
            		productid = "";
            		tv_product.setText("");
            		et_brand.setText("");
            		et_category.setText("");
            		et_norm.setText("");
            		et_price.setText("");
            		Intent openCameraIntent = new Intent(ProductItemActivity.this, CaptureActivity.class);
            		startActivityForResult(openCameraIntent, Constant.BARCODE_SCAN);
            	}
            }
        });

        tv_product = (TextView) findViewById(R.id.tv_product);

        et_brand = (EditText) findViewById(R.id.et_brand);

        et_category = (EditText) findViewById(R.id.et_category);

        et_norm = (EditText) findViewById(R.id.et_norm);

        et_price = (EditText) findViewById(R.id.et_price);
        et_price.setFilters(new InputFilter[] {
                new DecimalDigitLimitFilter(10, 2)
        });

        et_num = (EditText) findViewById(R.id.et_num);

        et_totalprice = (EditText) findViewById(R.id.et_totalprice);

        btn_ok = (Button) findViewById(R.id.btn_ok);

        btn_next = (Button) findViewById(R.id.btn_next);

        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();

        type = bundle.getString("type");

        if (type.equalsIgnoreCase("order")) {//订货上报
            tv_titlebar_title.setText(R.string.productitem_order);
            et_num.setHint(R.string.productitem_ordernum);
            et_totalprice.setHint(R.string.productitem_ordertotalprice);
        } else if (type.equalsIgnoreCase("backorder")) {//退货上报
            tv_titlebar_title.setText(R.string.productitem_backorder);
            et_num.setHint(R.string.productitem_backordernum);
            et_totalprice.setHint(R.string.productitem_backordertotalprice);
        } else if (type.equalsIgnoreCase("sale")) {//销量上报
            tv_titlebar_title.setText(R.string.productitem_sale);
            et_num.setHint(R.string.productitem_salenum);
            et_totalprice.setHint(R.string.productitem_saletotalprice);
        } else if (type.equalsIgnoreCase("stock")) {//库存上报
            tv_titlebar_title.setText(R.string.productitem_stock);
            et_num.setHint(R.string.productitem_stocknum);
            et_totalprice.setHint(R.string.productitem_stocktotalprice);
        }

        productid = "";

        terminalid = bundle.getString("terminalid");

        tv_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
            	if (CommonUtils.isNotFastDoubleClick()) {
            		Intent intent = new Intent();
            		Bundle bundle = new Bundle();
            		bundle.putString("terminalid", terminalid);
            		intent.putExtras(bundle);
            		intent.setClass(ProductItemActivity.this, TerminalProductSelectActivity.class);
            		startActivityForResult(intent, Constant.RESULT_PRODUCT_SELECT);
            	}
            }
        });

        et_price.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if (et_num.getText().toString().length() > 0 && et_price.getText().toString().length() > 0) {
                    int num = 0;
                    try {
                        num = Integer.parseInt(et_num.getText().toString());
                    } catch (Exception e) {

                    }
                    double price = 0;
                    try {
                        price = Double.parseDouble(et_price.getText().toString());
                    } catch (Exception e) {
            			showAlertDialog("提示","请输入合法的价格!", new View.OnClickListener() {
            				@Override
            				public void onClick(View arg0) {
            					alertDialog.cancel();
            				}
            			}, "确定", null, null);
                        et_price.getText().clear();
                        et_price.requestFocus();
                        return;
                    }
                    double totalprice = price * num;
                    BigDecimal bd = new BigDecimal(totalprice);
                    DecimalFormat df = new DecimalFormat("#0.00");
                    et_totalprice.setText(df.format(bd));
                }
            }

        });

        et_num.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if (et_num.getText().toString().length() > 0 && et_price.getText().toString().length() > 0) {
                    int num = 0;
                    try {
                        num = Integer.parseInt(et_num.getText().toString());
                    } catch (Exception e) {
             			showAlertDialog("提示","请输入整数数字!", new View.OnClickListener() {
            				@Override
            				public void onClick(View arg0) {
            					alertDialog.cancel();
            				}
            			}, "确定", null, null);
                        et_num.getText().clear();
                        et_num.requestFocus();
                        return;
                    }
                    double price = 0;
                    try {
                        price = Double.parseDouble(et_price.getText().toString());
                    } catch (Exception e) {
                    }
                    double totalprice = price * num;
                    BigDecimal bd = new BigDecimal(totalprice);
                    DecimalFormat df = new DecimalFormat("#0.00");
                    et_totalprice.setText(df.format(bd));
                }
            }

        });

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
            	if (CommonUtils.isNotFastDoubleClick()) {
            		if (validityInput()) {
            			Bundle bundle = new Bundle();
            			bundle.putString("productid", productid);
            			bundle.putString("productname", tv_product.getText().toString());
            			bundle.putString("brandname", et_brand.getText().toString());
            			bundle.putString("categoryname", et_category.getText().toString());
            			bundle.putString("productnorm", et_norm.getText().toString());
            			bundle.putString("productprice", et_price.getText().toString());
            			bundle.putString("productnum", et_num.getText().toString());
            			bundle.putString("producttotalprice", et_totalprice.getText().toString());
            			products.add(bundle);
            		}
            		
            		if (products.size() > 0) {
            			Intent intent = new Intent();
            			Bundle extras = new Bundle();
            			extras.putSerializable("products", products);
            			intent.putExtras(extras);
            			setResult(Activity.RESULT_OK, intent);
            			finish();
            		}
            	}
            }
        });

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
            	if (CommonUtils.isNotFastDoubleClick()) {
            		if (validityInput()) {
            			Bundle bundle = new Bundle();
            			bundle.putString("productid", productid);
            			bundle.putString("productname", tv_product.getText().toString());
            			bundle.putString("brandname", et_brand.getText().toString());
            			bundle.putString("categoryname", et_category.getText().toString());
            			bundle.putString("productnorm", et_norm.getText().toString());
            			bundle.putString("productprice", et_price.getText().toString());
            			bundle.putString("productnum", et_num.getText().toString());
            			bundle.putString("producttotalprice", et_totalprice.getText().toString());
            			products.add(bundle);
            			
            			productid = "";
            			tv_product.setText("");
            			et_brand.setText("");
            			et_category.setText("");
            			et_norm.setText("");
            			et_price.setText("");
            			et_num.setText("");
            			et_totalprice.setText("");
            			
            			tv_product.requestFocus();
            		}
            	}
            }
        });

        tv_titlebar_title.requestFocus();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constant.BARCODE_SCAN) {
            if (resultCode == Activity.RESULT_OK) {
                Bundle bundle = data.getExtras();
                String scanResult = bundle.getString("result");
                if (scanResult != null) {
                    productcode = scanResult;
                    sendQueryProduct(productcode, "", "0");
                }
            }
        } else if (requestCode == Constant.RESULT_PRODUCT_SELECT) {
            if (resultCode == Activity.RESULT_OK) {
                Bundle bundle = data.getExtras();
                productid = bundle.getString("productid");
                tv_product.setText(bundle.getString("productname"));
                et_brand.setText(bundle.getString("brandname"));
                et_category.setText(bundle.getString("categoryname"));
                et_norm.setText(bundle.getString("norm"));
                et_price.setText(bundle.getString("price"));
            }
        }
    }

    /**
     * 根据条码查询产品信息
     *
     * @param productcode
     * @param orderid
     * @param order
     */
    public void sendQueryProduct(String productcode, String orderid, String order) {
        ISaleApplication application = (ISaleApplication) getApplication();
        if (application != null && application.getConfig() != null && application.getConfig().getCompanyid() != null) {
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("companyid", application.getConfig().getCompanyid());
            params.put("brandid", "");
            params.put("categoryid", "");
            params.put("productcode", productcode);
            params.put("productname", "");
            params.put("productid", orderid);
            params.put("order", order);
            request("product!list?code=" + Constant.PRODUCT_LIST, params, FLAG_SHOW_PROGRESS | FLAG_SHOW_ERROR | FLAG_CANCEL);
        }
    }

    private boolean validityInput() {
        String sProduct = tv_product.getText().toString();
        if ("".equalsIgnoreCase(sProduct)) {
        	showAlertDialog("提示","产品不能为空,请选择产品!", new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					alertDialog.cancel();
				}
			}, "确定", null, null);
            tv_product.requestFocus();
            return false;
        }
        String sNorm = et_norm.getText().toString();
        if ("".equalsIgnoreCase(sNorm)) {
            showAlertDialog("提示","产品规格不能为空,请输入产品规格!", new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					alertDialog.cancel();
				}
			}, "确定", null, null);
            et_norm.requestFocus();
            return false;
        }
        String sPrice = et_price.getText().toString();
        if ("".equalsIgnoreCase(sPrice)) {
            showAlertDialog("提示","产品单价不能为空,请输入产品单价!", new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					alertDialog.cancel();
				}
			}, "确定", null, null);
            et_price.requestFocus();
            return false;
        }
        String sNum = et_num.getText().toString();
        if ("".equalsIgnoreCase(sNum)) {
            String sMessage = "";
            if (type.equalsIgnoreCase("order")) {// 订货上报
                sMessage = "订货数量不能为空,请输入订货数量!";
            } else if (type.equalsIgnoreCase("backorder")) {// 退货上报
                sMessage = "退货数量不能为空,请输入退货数量!";
            } else if (type.equalsIgnoreCase("sale")) {// 销量上报
                sMessage = "销售数量不能为空,请输入销售数量!";
            } else if (type.equalsIgnoreCase("stock")) {// 库存上报
                sMessage = "库存数量不能为空,请输入库存数量!";
            }
            showAlertDialog("提示",sMessage, new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					alertDialog.cancel();
				}
			}, "确定", null, null);
            et_price.requestFocus();
            return false;
        } else if (Integer.parseInt(sNum) <= 0) {
            String sMessage = "";
            if (type.equalsIgnoreCase("order")) {// 订货上报
                sMessage = "订货数量必须大于0,请重新输入订货数量!";
            } else if (type.equalsIgnoreCase("backorder")) {// 退货上报
                sMessage = "退货数量必须大于0,请重新输入退货数量!";
            } else if (type.equalsIgnoreCase("sale")) {// 销量上报
                sMessage = "销售数量必须大于0,请重新输入销售数量!";
            } else if (type.equalsIgnoreCase("stock")) {// 库存上报
                sMessage = "库存数量必须大于0,请重新输入库存数量!";
            }
            showAlertDialog("提示",sMessage, new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    alertDialog.cancel();
                }
            }, "确定", null, null);
            et_price.requestFocus();
            return false;
        }
        String sTotalPrice = et_totalprice.getText().toString();
        if ("".equalsIgnoreCase(sTotalPrice)) {
            String sMessage = "";
            if (type.equalsIgnoreCase("order")) {// 订货上报
                sMessage = getString(R.string.productitem_ordertotalprice) + "不能为空,请输入"
                        + getString(R.string.productitem_ordertotalprice) + "!";
            } else if (type.equalsIgnoreCase("backorder")) {// 退货上报
                sMessage = getString(R.string.productitem_backordertotalprice)
                        + "不能为空,请输入" + getString(R.string.productitem_backordertotalprice)
                        + "!";
            } else if (type.equalsIgnoreCase("sale")) {// 销量上报
                sMessage = getString(R.string.productitem_saletotalprice) + "不能为空,请输入"
                        + getString(R.string.productitem_saletotalprice) + "!";
            } else if (type.equalsIgnoreCase("stock")) {// 库存上报
                sMessage = getString(R.string.productitem_stocktotalprice) + "不能为空,请输入"
                        + getString(R.string.productitem_stocktotalprice) + "!";
            }
            showAlertDialog("提示",sMessage, new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					alertDialog.cancel();
				}
			}, "确定", null, null);
            et_price.requestFocus();
            return false;
        }

        if (Double.valueOf(sTotalPrice) > 999999999) {
            String sMessage = "";
            if (type.equalsIgnoreCase("order")) {// 订货上报
                sMessage = "最大订货金额不能超过999999999,请重新输入订货数量!";
            } else if (type.equalsIgnoreCase("backorder")) {// 退货上报
                sMessage = "最大退货金额不能超过999999999,请重新输入退货数量!";
            } else if (type.equalsIgnoreCase("sale")) {// 销量上报
                sMessage = "最大销量金额不能超过999999999,请重新输入销售数量!";
            } else if (type.equalsIgnoreCase("stock")) {// 库存上报
                sMessage = "最大库存金额不能超过999999999,请重新输入库存数量!";
            }

            showAlertDialog("提示",sMessage, new View.OnClickListener() {
      				@Override
      				public void onClick(View arg0) {
      					alertDialog.cancel();
      				}
      			}, "确定", null, null);
            et_totalprice.requestFocus();
            return false;
        }
        return true;
    }

    @Override
    public void onRecvData(XmlNode response) {
    }

    @Override
    public void onRecvData(JsonObject response) {
        if (response != null) {
            String code = response.get("code").getAsString();
            if (Constant.PRODUCT_LIST.equals(code)) {
                JsonArray products = response.getAsJsonArray("body");
                if (products != null) {
                    JsonObject product = (JsonObject) products.get(0);
                    productid = product.get("id").getAsString();
                    tv_product.setText(product.get("name").getAsString());
                    et_brand.setText(product.get("brandname").getAsString());
                    et_category.setText(product.get("categoryname").getAsString());
                    et_norm.setText(product.get("norm").getAsString());
                    et_price.setText(product.get("price").getAsString());
                }
            }
        }
    }
}
