package com.zjrc.isale.client.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.mapapi.map.PopupOverlay;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.zjrc.isale.client.R;

import java.util.ArrayList;

/**
 * 项目名称：销售管家客户端
 * 版本号：V2.00
 * 创建者: 高林荣
 * 修改者: 陈浩
 * 功能描述：客户位置界面
 */

public class BaiduMapActivity extends Activity implements OnClickListener {
    private static MapView mMapView = null;

    private TextView tv_titlebar_title;
    private Button btn_titlebar_back;

    private Button btn_locate;

    private String terminalname;

    private MapController mMapController = null;

    // 定位相关
    private LocationClient mLocClient;
    private MyLocationListenner myListener = new MyLocationListenner();
    private MyLocationOverlay myLocationOverlay = null;
    private LocationData locData = null;

    private boolean firstLoc = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.terminal_baidu);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar_small);

        btn_titlebar_back = (Button) findViewById(R.id.btn_titlebar_back);
        btn_titlebar_back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                finish();
            }
        });

        tv_titlebar_title = (TextView) findViewById(R.id.tv_titlebar_title);
        tv_titlebar_title.setText("位置");

        btn_locate = (Button) findViewById(R.id.btn_locate);
        btn_locate.setOnClickListener(this);

        mMapView = (MapView) findViewById(R.id.bmapView);
        mMapController = mMapView.getController();
        mMapController.setZoom(18);

        Intent intent = this.getIntent();
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                String longitude = bundle.getString("longitude");
                String latitude = bundle.getString("latitude");
                terminalname = bundle.getString("terminalname");
                if (!TextUtils.isEmpty(longitude) && !TextUtils.isEmpty(latitude)) {
                    int ilongitude = (int) (Double.parseDouble(longitude) * 1E6);
                    int ilatitude = (int) (Double.parseDouble(latitude) * 1E6);
                    GeoPoint point = new GeoPoint(ilatitude, ilongitude);

                    Drawable marker = getResources().getDrawable(R.drawable.v2_icon_location);
                    CustomItemizedOverlay customitemizedoverlay = new CustomItemizedOverlay(marker, this);
                    OverlayItem overlayitem = new OverlayItem(point, "位置", "客户名称");
                    customitemizedoverlay.addOverlay(overlayitem);

                    mMapView.getOverlays().add(customitemizedoverlay);
                    mMapController.setCenter(point);
                }
            }
        }

        mLocClient = new LocationClient(getApplicationContext());
        mLocClient.registerLocationListener(myListener);
        mLocClient.start();

        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);
//		 option.setAddrType("all");// 返回的定位结果包含地址信息
        option.setCoorType("bd09ll");// 返回的定位结果是百度经纬度,默认值gcj02
//		option.setScanSpan(1000);// 设置发起定位请求的间隔时间为4000ms
        option.disableCache(true);// 禁止启用缓存定位
//		 option.setPoiNumber(1); // 最多返回POI个数
//		 option.setPoiDistance(1000); // poi查询距离
//		 option.setPoiExtraInfo(true); // 是否需要POI的电话和地址等详细信息
        mLocClient.setLocOption(option);
        mLocClient.requestLocation();

        myLocationOverlay = new MyLocationOverlay(mMapView);
        locData = new LocationData();
        myLocationOverlay.setData(locData);
        mMapView.getOverlays().add(myLocationOverlay);
        mMapView.refresh();
    }

    @Override
    protected void onPause() {
        mMapView.onPause();
        myLocationOverlay.disableCompass();
        super.onPause();
    }

    @Override
    protected void onResume() {
        mMapView.onResume();
        myLocationOverlay.enableCompass();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        if (myListener != null) {
            mLocClient.unRegisterLocationListener(myListener);
            myListener = null;

            mLocClient.stop();
            mLocClient = null;
        }
        mMapView.destroy();
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mMapView.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onClick(View v) {
        mLocClient.requestLocation();
    }

    /**
     * 屏蔽返回按钮
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            setResult(Activity.RESULT_CANCELED);
            finish();
            return false;
        } else {
            return false;
        }
    }

    public class MyLocationListenner implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location == null)
                return;

            locData.latitude = location.getLatitude();
            locData.longitude = location.getLongitude();
            locData.accuracy = location.getRadius();
            locData.direction = location.getDerect();
            Log.d("loctest", String.format("before: lat: %f lon: %f", location.getLatitude(), location.getLongitude()));

            myLocationOverlay.setData(locData);
            mMapView.refresh();

            if (firstLoc) {
                firstLoc = false;
            } else {
                mMapController.animateTo(new GeoPoint((int) (locData.latitude * 1e6), (int) (locData.longitude * 1e6)), null);
            }
        }

        public void onReceivePoi(BDLocation poiLocation) {
            if (poiLocation == null) {
                return;
            }
        }
    }

    public class CustomItemizedOverlay extends ItemizedOverlay<OverlayItem> {
        private ArrayList<OverlayItem> overlayitems = new ArrayList<OverlayItem>();

        private Context context;
        private PopupOverlay pop;

        private Bitmap popBitmap;
        private GeoPoint popPoint;
        private int margin;

        private boolean isPopShowing = false;

        public CustomItemizedOverlay(Drawable marker, Context context) {
            super(marker);
            this.context = context;
            this.pop = new PopupOverlay(mMapView, null);
        }

        @Override
        protected OverlayItem createItem(int i) {
            return overlayitems.get(i);
        }

        @Override
        public int size() {
            return overlayitems.size();
        }

        @Override
        protected boolean onTap(int i) {
            // 多次显示/隐藏PopupOverlay，会造成app崩溃
            // 所以不允许此操作
            if (!isPopShowing) {
                if (popBitmap == null) {
                    final OverlayItem item = getItem(i);

                    View popview = LayoutInflater.from(context).inflate(R.layout.terminal_baidu_popup, null);
                    TextView tv_terminalname = (TextView) popview.findViewById(R.id.tv_terminalname);
                    tv_terminalname.setText(terminalname);

                    popBitmap = convertViewToBitmap(popview);
                    popPoint = item.getPoint();
                    margin = dpToPx(30 + 8);
                }

                if (pop != null) {
                    pop.showPopup(popBitmap, popPoint, margin);
                    isPopShowing = true;
                }
            }

            return true;
        }

//        @Override
//        public boolean onTap(GeoPoint geoPoint, MapView mapView) {
//            if (pop != null) {
//                pop.hidePop();
//            }
//
//            return false;
//        }

        public void addOverlay(OverlayItem overlayitem) {
            overlayitems.add(overlayitem);
            this.populate();
        }

        public Bitmap convertViewToBitmap(View view) {
            int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            view.measure(w, h);
            view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
            view.buildDrawingCache();

            return view.getDrawingCache();
        }

        public int dpToPx(int dp) {
            float densityDpi = getResources().getDisplayMetrics().densityDpi;
            return Math.round(dp * (densityDpi / 160f));
        }
    }
}
