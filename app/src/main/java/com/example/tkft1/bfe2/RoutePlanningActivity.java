package com.example.tkft1.bfe2;


import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.TextureMapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption.DrivingPolicy;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.IndoorRouteResult;
import com.baidu.mapapi.search.route.MassTransitRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRoutePlanOption;
import com.baidu.mapapi.search.route.TransitRoutePlanOption.TransitPolicy;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRoutePlanOption;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.baidu.mapapi.search.share.OnGetShareUrlResultListener;
import com.baidu.mapapi.search.share.ShareUrlResult;
import com.example.tkft1.bfe2.DrivingRouteOverlay;
import com.example.tkft1.bfe2.TransitRouteOverlay;
import com.example.tkft1.bfe2.WalkingRouteOverlay;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * 路线规划
 *
 * @author ys
 *
 */
public class RoutePlanningActivity extends Activity implements OnClickListener {

    private boolean isFirstIn = true;
    private double latitude;
    private double longitude;
    public LatLng latlng;

    private LocationClient mLocationClient;
    private MyLocationListener mLocationListener;
    private TextureMapView mapView;
    private BaiduMap bdMap;

    private EditText startEt;
    private EditText endEt;

    private String startPlace;// 开始地点
    private String endPlace;// 结束地点
    private String city;//所在城市

    private Button driveBtn;// 驾车
    private Button walkBtn;// 步行
    private Button transitBtn;// 换成 （公交）
    private Button nextLineBtn;

    private Spinner drivingSpinner, transitSpinner;

    private RoutePlanSearch routePlanSearch;// 路径规划搜索接口

    private int index = -1;
    private int totalLine = 0;// 记录某种搜索出的方案数量
    private int drivintResultIndex = 0;// 驾车路线方案index
    private int transitResultIndex = 0;// 换乘路线方案index

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_planning);

        Intent intent = getIntent();
        ArrayList<String> court_name_and_city = intent.getStringArrayListExtra("com.example.tkft1.bfe2.information");
        city = court_name_and_city.get(0);
        endPlace = court_name_and_city.get(1);
        TextView textView = (TextView)findViewById(R.id.end_et);
        textView.setText(endPlace);

        init();
        initLocation();
    }

    /**
     *
     */
    private void init() {
        mapView = (TextureMapView) findViewById(R.id.mapview);
        mapView.showZoomControls(false);
        bdMap = mapView.getMap();

        startEt = (EditText) findViewById(R.id.start_et);
        endEt = (EditText) findViewById(R.id.end_et);
        driveBtn = (Button) findViewById(R.id.drive_btn);
        transitBtn = (Button) findViewById(R.id.transit_btn);
        walkBtn = (Button) findViewById(R.id.walk_btn);
        nextLineBtn = (Button) findViewById(R.id.nextline_btn);
        nextLineBtn.setEnabled(false);
        driveBtn.setOnClickListener(this);
        transitBtn.setOnClickListener(this);
        walkBtn.setOnClickListener(this);
        nextLineBtn.setOnClickListener(this);

        drivingSpinner = (Spinner) findViewById(R.id.driving_spinner);
        String[] drivingItems = getResources().getStringArray(
                R.array.driving_spinner);
        ArrayAdapter<String> drivingAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, drivingItems);
        drivingSpinner.setAdapter(drivingAdapter);
        drivingSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                if (index == 0) {
                    drivintResultIndex = 0;
                    drivingSearch(drivintResultIndex);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        transitSpinner = (Spinner) findViewById(R.id.transit_spinner);
        String[] transitItems = getResources().getStringArray(
                R.array.transit_spinner);
        ArrayAdapter<String> transitAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, transitItems);
        transitSpinner.setAdapter(transitAdapter);
        transitSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                if (index == 1) {
                    transitResultIndex = 0;
                    transitSearch(transitResultIndex);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        routePlanSearch = RoutePlanSearch.newInstance();
        routePlanSearch
                .setOnGetRoutePlanResultListener(routePlanResultListener);
    }

    /**
     * 路线规划结果回调
     */
    OnGetRoutePlanResultListener routePlanResultListener = new OnGetRoutePlanResultListener() {

        /**
         * 步行路线结果回调
         */
        @Override
        public void onGetWalkingRouteResult(
                WalkingRouteResult walkingRouteResult) {
            bdMap.clear();
            if (walkingRouteResult == null
                    || walkingRouteResult.error != SearchResult.ERRORNO.NO_ERROR) {
                Toast.makeText(RoutePlanningActivity.this, "抱歉，未找到结果",
                        Toast.LENGTH_SHORT).show();
            }
            if (walkingRouteResult.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
                // TODO
                return;
            }
            if (walkingRouteResult.error == SearchResult.ERRORNO.NO_ERROR) {
                WalkingRouteOverlay walkingRouteOverlay = new WalkingRouteOverlay(
                        bdMap);
                walkingRouteOverlay.setData(walkingRouteResult.getRouteLines()
                        .get(drivintResultIndex));
                bdMap.setOnMarkerClickListener(walkingRouteOverlay);
                walkingRouteOverlay.addToMap();
                walkingRouteOverlay.zoomToSpan();
                totalLine = walkingRouteResult.getRouteLines().size();
                Toast.makeText(RoutePlanningActivity.this,
                        "共查询出" + totalLine + "条符合条件的线路",  Toast.LENGTH_LONG).show();
                if (totalLine > 1) {
                    nextLineBtn.setEnabled(true);
                }
            }
        }

        /**
         * 换成路线结果回调
         */
        @Override
        public void onGetTransitRouteResult(
                TransitRouteResult transitRouteResult) {
            bdMap.clear();
            if (transitRouteResult == null
                    || transitRouteResult.error != SearchResult.ERRORNO.NO_ERROR) {
                Toast.makeText(RoutePlanningActivity.this, "抱歉，未找到结果",
                        Toast.LENGTH_SHORT).show();
            }
            if (transitRouteResult.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
                // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
                // drivingRouteResult.getSuggestAddrInfo()
                return;
            }
            if (transitRouteResult.error == SearchResult.ERRORNO.NO_ERROR) {
                TransitRouteOverlay transitRouteOverlay = new TransitRouteOverlay(
                        bdMap);
                transitRouteOverlay.setData(transitRouteResult.getRouteLines()
                        .get(drivintResultIndex));// 设置一条驾车路线方案
                bdMap.setOnMarkerClickListener(transitRouteOverlay);
                transitRouteOverlay.addToMap();
                transitRouteOverlay.zoomToSpan();
                totalLine = transitRouteResult.getRouteLines().size();
                Toast.makeText(RoutePlanningActivity.this,
                        "共查询出" + totalLine + "条符合条件的线路", Toast.LENGTH_LONG).show();
                if (totalLine > 1) {
                    nextLineBtn.setEnabled(true);
                }
                // 通过getTaxiInfo()可以得到很多关于打车的信息
                Toast.makeText(
                        RoutePlanningActivity.this,
                        "该路线打车总路程"
                                + transitRouteResult.getTaxiInfo()
                                .getDistance(),  Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onGetMassTransitRouteResult(MassTransitRouteResult massTransitRouteResult) {

        }


        /**
         * 驾车路线结果回调 查询的结果可能包括多条驾车路线方案
         */
        @Override
        public void onGetDrivingRouteResult(
                DrivingRouteResult drivingRouteResult) {
            bdMap.clear();
            if (drivingRouteResult == null
                    || drivingRouteResult.error != SearchResult.ERRORNO.NO_ERROR) {
                Toast.makeText(RoutePlanningActivity.this, "抱歉，未找到结果",
                        Toast.LENGTH_SHORT).show();
            }
            if (drivingRouteResult.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
                // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
                // drivingRouteResult.getSuggestAddrInfo()
                return;
            }
            if (drivingRouteResult.error == SearchResult.ERRORNO.NO_ERROR) {
                DrivingRouteOverlay drivingRouteOverlay = new DrivingRouteOverlay(
                        bdMap);
                drivingRouteOverlay.setData(drivingRouteResult.getRouteLines()
                        .get(drivintResultIndex));// 设置一条驾车路线方案
                bdMap.setOnMarkerClickListener(drivingRouteOverlay);
                drivingRouteOverlay.addToMap();
                drivingRouteOverlay.zoomToSpan();
                totalLine = drivingRouteResult.getRouteLines().size();
                Toast.makeText(RoutePlanningActivity.this,
                        "共查询出" + totalLine + "条符合条件的线路",  Toast.LENGTH_LONG).show();
                if (totalLine > 1) {
                    nextLineBtn.setEnabled(true);
                }
                // 通过getTaxiInfo()可以得到很多关于打车的信息
//                Toast.makeText(
//                        RoutePlanningActivity.this,
//                        "该路线打车总路程"
//                                + drivingRouteResult.getTaxiInfo()
//                                .getDistance(), Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onGetIndoorRouteResult(IndoorRouteResult indoorRouteResult) {

        }

        @Override
        public void onGetBikingRouteResult(BikingRouteResult bikingRouteResult) {

        }
    };

    /**
     * 驾车线路查询
     */
    private void drivingSearch(int index) {
        DrivingRoutePlanOption drivingOption = new DrivingRoutePlanOption();
        drivingOption.currentCity(city);
        drivingOption.policy(DrivingPolicy.values()[drivingSpinner
                .getSelectedItemPosition()]);// 设置驾车路线策略
        //drivingOption.from(PlanNode.withCityNameAndPlaceName(city,"哈尔滨工业大学"));// 设置起点
        drivingOption.from(PlanNode.withLocation(new LatLng(latitude,longitude)));
        drivingOption.to(PlanNode.withCityNameAndPlaceName(city, endPlace));// 设置终点
        routePlanSearch.drivingSearch(drivingOption);// 发起驾车路线规划
    }

    /**
     * 换乘路线查询
     */
    private void transitSearch(int index) {
        TransitRoutePlanOption transitOption = new TransitRoutePlanOption();
        transitOption.city(city);// 设置换乘路线规划城市，起终点中的城市将会被忽略
        transitOption.from(PlanNode.withLocation(new LatLng(latitude,longitude)));
        transitOption.to(PlanNode.withCityNameAndPlaceName(city, endPlace));
        transitOption.policy(TransitPolicy.values()[transitSpinner
                .getSelectedItemPosition()]);// 设置换乘策略
        routePlanSearch.transitSearch(transitOption);
    }

    /**
     * 步行路线查询
     */
    private void walkSearch() {
        WalkingRoutePlanOption walkOption = new WalkingRoutePlanOption();
        walkOption.from(PlanNode.withLocation(new LatLng(latitude,longitude)));
        walkOption.to(PlanNode.withCityNameAndPlaceName(city, endPlace));
        routePlanSearch.walkingSearch(walkOption);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.drive_btn:// 驾车
                index = 0;
                drivintResultIndex = 0;
                startPlace = startEt.getText().toString();
                endPlace = endEt.getText().toString();
                driveBtn.setEnabled(false);
                transitBtn.setEnabled(true);
                walkBtn.setEnabled(true);
                nextLineBtn.setEnabled(false);
                drivingSearch(drivintResultIndex);
                break;
            case R.id.transit_btn:// 换乘
                index = 1;
                transitResultIndex = 0;
                startPlace = startEt.getText().toString();
                endPlace = endEt.getText().toString();
                transitBtn.setEnabled(false);
                driveBtn.setEnabled(true);
                walkBtn.setEnabled(true);
                nextLineBtn.setEnabled(false);
                transitSearch(transitResultIndex);
                break;
            case R.id.walk_btn:// 步行
                index = 2;
                startPlace = startEt.getText().toString();
                endPlace = endEt.getText().toString();
                walkBtn.setEnabled(false);
                driveBtn.setEnabled(true);
                transitBtn.setEnabled(true);
                nextLineBtn.setEnabled(false);
                walkSearch();
                break;
            case R.id.nextline_btn:// 下一条
                switch (index) {
                    case 0:
                        drivingSearch(++drivintResultIndex);
                        break;
                    case 1:
                        transitSearch(transitResultIndex);
                        break;
                    case 2:

                        break;
                }
                break;
        }
    }

    protected void onStart()
    {
        super.onStart();
        // 开启定位
        bdMap.setMyLocationEnabled(true);
        if (!mLocationClient.isStarted())
            mLocationClient.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        routePlanSearch.destroy();// 释放检索实例
        mapView.onDestroy();
    }

    @Override
    protected void onStop()
    {
        super.onStop();

        // 停止定位
        bdMap.setMyLocationEnabled(false);
        mLocationClient.stop();

    }

    private void initLocation()
    {
        System.out.println("))))))))))))))))))))))");
        mLocationClient = new LocationClient(this);
        System.out.println("*******************");
        mLocationListener = new MyLocationListener();
        System.out.println("(&&&&&&&&&&&&)");
        mLocationClient.registerLocationListener(mLocationListener);
        System.out.println("^^^^^^^^^^^^^^^^");
        LocationClientOption option = new LocationClientOption();
        option.setCoorType("bd09ll");
        option.setIsNeedAddress(true);
        option.setOpenGps(true);
        option.setScanSpan(1000);
        System.out.println("((((((((((((())))))))))))))))))))))))");
    }


    OnGetShareUrlResultListener shareUrlResultListener = new OnGetShareUrlResultListener() {

        @Override
        public void onGetPoiDetailShareUrlResult(ShareUrlResult arg0) {

        }

        @Override
        public void onGetLocationShareUrlResult(ShareUrlResult arg0) {

        }

        @Override
        public void onGetRouteShareUrlResult(ShareUrlResult shareUrlResult) {

        }
    };

    private class MyLocationListener implements BDLocationListener
    {
        @Override
        public void onReceiveLocation(BDLocation location) {
            System.out.println("(((((((((((((((((((((((((((((((((((((((((");
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            double x_pi = 3.14159265358979324 * 3000.0 / 180.0;
            double x = longitude, y = latitude;
            double z = Math.sqrt(x * x + y * y) + 0.00002 * Math.sin(y * x_pi);
            double theta = Math.atan2(y, x) + 0.000003 * Math.cos(x * x_pi);
            x = z * Math.cos(theta) + 0.0065;
            y = z * Math.sin(theta) + 0.006;
            longitude = x;
            latitude = y;
            //latlng.latitude = latitude;
            MyLocationData data = new MyLocationData.Builder()//
                    .accuracy(location.getRadius())//
                    .latitude(latitude)//
                    .longitude(longitude)//
                    .build();
            bdMap.setMyLocationData(data);
            if (isFirstIn) {
                LatLng latLng = new LatLng(latitude, longitude);

                System.out.println("(((((((((((((((((((((((((((((((((((((((((" + latLng.latitude + "###########22222222" + latLng.longitude);
                MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLng);
                bdMap.animateMapStatus(msu);
                isFirstIn = false;
            }
        }
    }

}
