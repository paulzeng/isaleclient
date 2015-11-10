package com.zjrc.isale.client.ui.activity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.zjrc.isale.client.R;
import com.zjrc.isale.client.bean.CommonUtils;
import com.zjrc.isale.client.bean.Constant;
import com.zjrc.isale.client.bean.TerminalType;
import com.zjrc.isale.client.database.DatabaseService;
import com.zjrc.isale.client.ui.adapter.TravelDestinationAdapter;
import com.zjrc.isale.client.util.xml.XmlNode;

import java.util.ArrayList;

/**
 * @项目名称：销售管家客户端
 * @版本号：V2.00
 * @创建者: 贺彬
 * @功能描述：差旅目的地选择
 */
public class TravelDestinationActivity extends BaseActivity {
	private Button btn_titlebar_back;
	private TextView tv_titlebar_title;
	private ListView lv_destinationlist;
	private TextView tv_destination;

	private ArrayList<TerminalType> listprovinces = new ArrayList<TerminalType>();//省直辖市列表
	private ArrayList<TerminalType> listcitys = new ArrayList<TerminalType>();//城市列表
	private ArrayList<TerminalType> listzones = new ArrayList<TerminalType>();//地区列表

	private TravelDestinationAdapter adapter;
	private TerminalType province;//省直辖市
	private TerminalType city;//市
	private TerminalType zone;//区
	private StringBuffer destination = new StringBuffer();

	private void initProvince() {
		listprovinces.clear();
		DatabaseService databaseservice = new DatabaseService(this);
		try {
			Cursor cursor = databaseservice.getProvinceDataCursor();
			if (cursor.getCount() > 0) {
				cursor.moveToFirst();
				int i = 0;
				while (i < cursor.getCount()) {
					// if (i == 0) {
					// initCity("" + cursor.getInt(0));
					// }
					listprovinces.add(new TerminalType(cursor.getString(0)
							.trim(), cursor.getString(1).trim()));
					cursor.moveToNext();
					i++;
				}
			} else {
				// initCity("-1");
				// initZone("-1");
			}
		} catch (Exception e) {
			Log.i("", "" + e.getMessage());
		} finally {
			databaseservice.close();
		}
	}

	private void initCity(String provinceid) {
		listcitys.clear();
		DatabaseService databaseservice = new DatabaseService(this);
		try {
			Cursor cursor = databaseservice.getCityDataCursor(provinceid);
			if (cursor.getCount() > 0) {
				cursor.moveToFirst();
				int i = 0;
				while (i < cursor.getCount()) {
					listcitys.add(new TerminalType(cursor.getString(0).trim(),
							cursor.getString(1).trim()));
					cursor.moveToNext();
					i++;
				}
			} else {
			}
		} catch (Exception e) {
			Log.i("", "" + e.getMessage());
		} finally {
			databaseservice.close();
		}
	}

	private void initZone(String cityid) {
		listzones.clear();
		DatabaseService databaseservice = new DatabaseService(this);
		try {
			Cursor cursor = databaseservice.getZoneDataCursor(cityid);
			if (cursor.getCount() > 0) {
				cursor.moveToFirst();
				int i = 0;
				while (i < cursor.getCount()) {
					listzones.add(new TerminalType(cursor.getString(0).trim(),
							cursor.getString(1).trim()));
					cursor.moveToNext();
					i++;
				}
			}
			// if (listzones.size() == 0) {
			// findViewById(R.id.ll_zone).setVisibility(View.GONE);
			// } else {
			// findViewById(R.id.ll_zone).setVisibility(View.VISIBLE);
			// zoneadapter.notifyDataSetChanged();
			// }
		} catch (Exception e) {
			Log.i("", "" + e.getMessage());
		} finally {
			databaseservice.close();
		}
	}

	@Override
	public void onRecvData(XmlNode response) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.travel_destination);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.titlebar_small);
		tv_titlebar_title = (TextView) findViewById(R.id.tv_titlebar_title);
		tv_destination = (TextView) findViewById(R.id.tv_destination);
		tv_titlebar_title.setText(R.string.travel_destination);
		btn_titlebar_back = (Button) findViewById(R.id.btn_titlebar_back);
		btn_titlebar_back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (CommonUtils.isNotFastDoubleClick()) {
					if (zone != null) {
						destination.delete(0, destination.length());
						destination.append(province.getName() + " "
								+ city.getName() + " ");
						tv_destination.setText(destination);
						zone = null;
						listzones = null;
						initCity(province.getId());
						adapter.reSetList(listzones);
						adapter.notifyDataSetChanged();
					} else {
						if (city != null) {
							destination.delete(0, destination.length());
							destination.append(province.getName() + " ");
							tv_destination.setText(destination);
							city = null;
							initProvince();
							initCity(province.getId());
							adapter.reSetList(listcitys);
							adapter.notifyDataSetChanged();
						} else {
							if (province != null) {
								destination.delete(0, destination.length());
								destination.append("");
								tv_destination.setText(destination);
								initProvince();
								adapter.reSetList(listprovinces);
								adapter.notifyDataSetChanged();
								province = null;
								tv_titlebar_title
								.setText(R.string.travel_destination);
							} else {
								finish();
							}
						}
					}
				}
			}
		});

		lv_destinationlist = (ListView) findViewById(R.id.lv_destinationlist);
		initProvince();
		adapter = new TravelDestinationAdapter(this, listprovinces);
		lv_destinationlist.setAdapter(adapter);
		lv_destinationlist
				.setOnItemClickListener(new ListView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int index, long arg3) {
						if (province == null) {
							tv_titlebar_title.setText("上一步");
							province = (TerminalType) adapter.getItem(index);
							destination = new StringBuffer();
							destination.append(province.getName() + " ");
							tv_destination.setText(destination);
							tv_destination.setVisibility(View.VISIBLE);
							initCity(province.getId());
							if (listcitys.size()>0) {
								adapter.reSetList(listcitys);
								adapter.notifyDataSetChanged();
							}else{
								city = new TerminalType("", "");
								zone = new TerminalType("", "");
								Intent intent = getIntent();
								Bundle bundle = new Bundle();
								bundle.putSerializable("province", province);
								bundle.putSerializable("city", city);
								bundle.putSerializable("zone", zone);
								intent.putExtras(bundle);
								setResult(
										Constant.RESULT_TRAVEL_DESTINATION,
										intent);
								finish();
							}
							
						} else {
							if (city == null) {
								city = (TerminalType) adapter.getItem(index);
								destination.append(city.getName() + " ");
								tv_destination.setText(destination);
								initZone(city.getId());
								if (listzones.size()>0) {
									adapter.reSetList(listzones);
									adapter.notifyDataSetChanged();
								}else{
									zone = new TerminalType("", "");
									Intent intent = getIntent();
									Bundle bundle = new Bundle();
									bundle.putSerializable("province", province);
									bundle.putSerializable("city", city);
									bundle.putSerializable("zone", zone);
									intent.putExtras(bundle);
									setResult(
											Constant.RESULT_TRAVEL_DESTINATION,
											intent);
									finish();
								}
								
								
								
							} else {
								if (zone == null) {
									zone = (TerminalType) adapter
											.getItem(index);
									destination.append(zone.getName() + " ");
									tv_destination.setText(destination);
									Intent intent = getIntent();
									Bundle bundle = new Bundle();
									bundle.putSerializable("province", province);
									bundle.putSerializable("city", city);
									bundle.putSerializable("zone", zone);
									intent.putExtras(bundle);
									setResult(
											Constant.RESULT_TRAVEL_DESTINATION,
											intent);
									finish();
								}
							}
						}
					}
				});
	}

	@Override
	public void onBackPressed() {
		if (zone != null) {
			destination.delete(0, destination.length());
			destination.append(province.getName() + " " + city.getName() + " ");
			tv_destination.setText(destination);
			zone = null;
			listzones = null;
			initCity(province.getId());
			adapter.reSetList(listzones);
			adapter.notifyDataSetChanged();
		} else {
			if (city != null) {
				destination.delete(0, destination.length());
				destination.append(province.getName() + " ");
				tv_destination.setText(destination);
				city = null;
				initProvince();
				initCity(province.getId());
				adapter.reSetList(listcitys);
				adapter.notifyDataSetChanged();
			} else {
				if (province != null) {
					destination.delete(0, destination.length());
					destination.append("");
					tv_destination.setText(destination);
					initProvince();
					adapter.reSetList(listprovinces);
					adapter.notifyDataSetChanged();
					province = null;
					tv_titlebar_title.setText(R.string.travel_destination);
				} else {
					finish();
				}
			}
		}
	}
    @Override
    public void onRecvData(JsonObject response) {

    }
}
