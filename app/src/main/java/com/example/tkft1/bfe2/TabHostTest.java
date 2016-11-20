package com.example.tkft1.bfe2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.TextureMapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiBoundSearchOption;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchOption;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.share.OnGetShareUrlResultListener;
import com.baidu.mapapi.search.share.ShareUrlResult;
import com.baidu.mapapi.search.share.ShareUrlSearch;
import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MyLocationData;
import java.util.HashMap;

public class TabHostTest extends Activity implements  View.OnClickListener{
    static private boolean islogin = false;
    static private String username;
    static private String password;
    static private TabHost th;
    static private LayoutInflater i;
    private TabSpec tab01;
    private TabSpec tab02;
    private TabSpec tab03;


    private boolean isFirstIn = true;
    private LocationClient mLocationClient;
    private MyLocationListener mLocationListener;
    private TextureMapView mapView;
    private BaiduMap bdMap;
    private PoiSearch poiSearch;
    private ShareUrlSearch shareUrlSearch;
    private EditText editCityEt, editSearchKeyEt;

    // 城市检索，区域检索，周边检索，下一组数据 按钮
    private Button citySearchBtn, boundSearchBtn, nextDataBtn;

    // 记录检索类型
    private int type;
    // 记录页标
    private int page = 1;
    private int totalPage = 0;

    private double latitude;
    private double longitude;
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.tabhost);

        th = (TabHost)findViewById(R.id.home);
        th.setup();
        i = LayoutInflater.from(this);
        initTabHost();

        init();
        initLocation();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.login:
                EditText editText = (EditText) findViewById(R.id.username);
                username = editText.getText().toString();
                editText = (EditText)findViewById(R.id.password);
                password = editText.getText().toString();
                if (username.equals("godchen") && password.equals("123456")){

                    th.setCurrentTab(1);
                    i.inflate(R.layout.tab03_my, th.getTabContentView());
                    tab03.setContent(R.id.my);

                    Button information = (Button)findViewById(R.id.my_information);
                    Button news = (Button)findViewById(R.id.my_news);
                    Button friends = (Button)findViewById(R.id.my_friends);

                    information.setOnClickListener(this);
                    news.setOnClickListener(this);
                    friends.setOnClickListener(this);
                    th.setCurrentTab(2);

                    islogin = true;
                }
                else
                    islogin = false;
                break;

            case R.id.register:
                break;
            case R.id.my_information:
                Intent intent = new Intent();
                ArrayList<String> informaion = new ArrayList<String>();
                informaion.add(username);
                informaion.add(password);
                intent.putExtra("com.example.tkft1.bfe2.information",informaion);
                intent.setClass(TabHostTest.this, tab03_my_informaion.class);
                startActivity(intent);
                break;
            case R.id.my_news:

                break;
            case R.id.my_friends:

                break;
            case R.id.city_search_btn:
                type = 0;
                page = 1;
                citySearchBtn.setEnabled(true);
                boundSearchBtn.setEnabled(true);
                //nearbySearchBtn.setEnabled(true);
                nextDataBtn.setEnabled(true);
                bdMap.clear();
                citySearch(page);
                break;
            case R.id.bound_search_btn:
                type = 1;
                page = 1;
                citySearchBtn.setEnabled(true);
                boundSearchBtn.setEnabled(true);
                //nearbySearchBtn.setEnabled(true);
                nextDataBtn.setEnabled(true);
                bdMap.clear();
                boundSearch(page);
                break;
            /**
            case R.id.nearby_search_btn:
                type = 2;
                page = 1;
                citySearchBtn.setEnabled(true);
                boundSearchBtn.setEnabled(true);
                nearbySearchBtn.setEnabled(true);
                nextDataBtn.setEnabled(true);
                bdMap.clear();
                nearbySearch(page);
                break;
             **/
            case R.id.next_data_btn:
                switch (type) {
                    case 0:
                        if (++page <= totalPage) {
                            citySearch(page);
                        } else {
                            Toast.makeText(TabHostTest.this, "已经查到了最后一页~",
                                    Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case 1:
                        if (++page <= totalPage) {
                            boundSearch(page);
                        } else {
                            Toast.makeText(TabHostTest.this, "已经查到了最后一页~",
                                    Toast.LENGTH_SHORT).show();
                        }
                        break;
                    /**
                    case 2:
                        if (++page <= totalPage) {
                            nearbySearch(page);
                        } else {
                            Toast.makeText(TabHostTest.this, "已经查到了最后一页~",
                                    Toast.LENGTH_SHORT).show();
                        }
                        break;
                     **/
                }
                break;
            default:
                break;
        }
    }

    private void initTabHost(){
        i.inflate(R.layout.tab01_news, th.getTabContentView());
        i.inflate(R.layout.tab02_vicinity, th.getTabContentView());
        i.inflate(R.layout.tab03_my_login, th.getTabContentView());

        tab01 = th.newTabSpec("tab01_news").setIndicator("动态");
        tab02 = th.newTabSpec("tab02_vicinity").setIndicator("附近");
        tab03 = th.newTabSpec("tab03_my").setIndicator("我的");

        th.addTab(tab01.setContent(R.id.news));
        th.addTab(tab02.setContent(R.id.vicinity));
        th.addTab(tab03.setContent(R.id.my_login));

        SimpleAdapter adapter = new SimpleAdapter(this, getNews(), R.layout.tab01_news_vlist,
                new String[]{"user","position","introduction"},
                new int[]{R.id.user, R.id.position, R.id.introduction});
        ListView lt = (ListView)findViewById(R.id.newslist);
        lt.setAdapter(adapter);

        Button login = (Button)findViewById(R.id.login);
        Button register = (Button)findViewById(R.id.register);
        login.setOnClickListener(this);
        register.setOnClickListener(this);

        th.setCurrentTab(1);
    }

    private void initLocation()
    {
        mLocationClient = new LocationClient(this);
        mLocationListener = new MyLocationListener();
        mLocationClient.registerLocationListener(mLocationListener);
        LocationClientOption option = new LocationClientOption();
        option.setCoorType("bd09ll");
        option.setIsNeedAddress(true);
        option.setOpenGps(true);
        option.setScanSpan(1000);
    }
    private void init() {
        mapView = (TextureMapView) findViewById(R.id.mapview);
        mapView.showZoomControls(false);
        bdMap = mapView.getMap();
        // 实例化PoiSearch对象
        poiSearch = PoiSearch.newInstance();
        // 设置检索监听器
        poiSearch.setOnGetPoiSearchResultListener(poiSearchListener);
        editCityEt = (EditText) findViewById(R.id.city);
        editSearchKeyEt = (EditText) findViewById(R.id.searchkey);

        citySearchBtn = (Button) findViewById(R.id.city_search_btn);
        boundSearchBtn = (Button) findViewById(R.id.bound_search_btn);
        //nearbySearchBtn = (Button) findViewById(R.id.nearby_search_btn);
        nextDataBtn = (Button) findViewById(R.id.next_data_btn);
        nextDataBtn.setEnabled(false);
        citySearchBtn.setOnClickListener(this);
        boundSearchBtn.setOnClickListener(this);
        //nearbySearchBtn.setOnClickListener(this);
        nextDataBtn.setOnClickListener(this);

        editSearchKeyEt.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                citySearchBtn.setEnabled(true);
                boundSearchBtn.setEnabled(true);
                //nearbySearchBtn.setEnabled(true);
                nextDataBtn.setEnabled(false);
                page = 1;
                totalPage = 0;
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        shareUrlSearch = ShareUrlSearch.newInstance();

    }

    /**
     *
     */
    OnGetPoiSearchResultListener poiSearchListener = new OnGetPoiSearchResultListener() {
        @Override
        public void onGetPoiResult(PoiResult poiResult) {
            if (poiResult == null
                    || poiResult.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {// 没有找到检索结果
                Toast.makeText(TabHostTest.this, "未找到结果",
                        Toast.LENGTH_LONG).show();
                return;
            }

            if (poiResult.error == SearchResult.ERRORNO.NO_ERROR) {// 检索结果正常返回
                bdMap.clear();
                MyPoiOverlay poiOverlay = new MyPoiOverlay(bdMap);
                poiOverlay.setData(poiResult);// 设置POI数据
                bdMap.setOnMarkerClickListener(poiOverlay);
                poiOverlay.addToMap();// 将所有的overlay添加到地图上
                poiOverlay.zoomToSpan();
                //
                totalPage = poiResult.getTotalPageNum();// 获取总分页数
                Toast.makeText(
                        TabHostTest.this,
                        "总共查到" + poiResult.getTotalPoiNum() + "个兴趣点, 分为"
                                + totalPage + "页", Toast.LENGTH_SHORT).show();

            }
        }

        @Override
        public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {
            if (poiDetailResult.error != SearchResult.ERRORNO.NO_ERROR) {
                Toast.makeText(TabHostTest.this, "抱歉，未找到结果",
                        Toast.LENGTH_SHORT).show();
            } else {// 正常返回结果的时候，此处可以获得很多相关信息
                Toast.makeText(
                        TabHostTest.this,
                        poiDetailResult.getName() + ": "
                                + poiDetailResult.getAddress(),
                        Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

        }
    };

    /**
     * 短串检索监听器
     */
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

    class MyPoiOverlay extends PoiOverlay {

        public MyPoiOverlay(BaiduMap arg0) {
            super(arg0);
        }

        @Override
        public boolean onPoiClick(int arg0) {
            super.onPoiClick(arg0);
            PoiInfo poiInfo = getPoiResult().getAllPoi().get(arg0);
            poiSearch.searchPoiDetail(new PoiDetailSearchOption()
                    .poiUid(poiInfo.uid));
            return true;
        }

    }

    /**
     * 城市内搜索
     */
    private void citySearch(int page) {
        // 设置检索参数
        PoiCitySearchOption citySearchOption = new PoiCitySearchOption();
        citySearchOption.city(editCityEt.getText().toString());// 城市
        citySearchOption.keyword(editSearchKeyEt.getText().toString());// 关键字
        citySearchOption.pageCapacity(15);// 默认每页10条
        citySearchOption.pageNum(page);// 分页编号
        // 发起检索请求
        poiSearch.searchInCity(citySearchOption);
    }

    /**
     * 范围检索
     */
    private void boundSearch(int page) {
        initLocation();

        PoiBoundSearchOption boundSearchOption = new PoiBoundSearchOption();
        LatLng southwest = new LatLng(latitude - 0.01, longitude - 0.012);// 西南
        LatLng northeast = new LatLng(latitude + 0.01, longitude + 0.012);// 东北
        LatLngBounds bounds = new LatLngBounds.Builder().include(southwest)
                .include(northeast).build();// 得到一个地理范围对象
        boundSearchOption.bound(bounds);// 设置poi检索范围
        boundSearchOption.keyword(editSearchKeyEt.getText().toString());// 检索关键字
        boundSearchOption.pageNum(page);
        poiSearch.searchInBound(boundSearchOption);// 发起poi范围检索请求
    }

    /**
     * 附近检索
     */
    /**
    private void nearbySearch(int page) {
        initLocation();

        PoiNearbySearchOption nearbySearchOption = new PoiNearbySearchOption();
        nearbySearchOption.location(new LatLng(latitude, longitude));
        nearbySearchOption.keyword(editSearchKeyEt.getText().toString());
        nearbySearchOption.radius(1000);// 检索半径，单位是米
        nearbySearchOption.pageNum(page);
        poiSearch.searchNearby(nearbySearchOption);// 发起附近检索请求
    }
**/
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        // 开启定位
        bdMap.setMyLocationEnabled(true);
        if (!mLocationClient.isStarted())
            mLocationClient.start();
    }
    @Override
    protected void onStop()
    {
        super.onStop();

        // 停止定位
        bdMap.setMyLocationEnabled(false);
        mLocationClient.stop();

    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        poiSearch.destroy();// 释放poi检索对象
        mapView.onDestroy();
    }

    private class MyLocationListener implements BDLocationListener
    {
        @Override
        public void onReceiveLocation(BDLocation location)
        {
            MyLocationData data = new MyLocationData.Builder()//
                    .accuracy(location.getRadius())//
                    .latitude(location.getLatitude())//
                    .longitude(location.getLongitude())//
                    .build();
            bdMap.setMyLocationData(data);
            if (isFirstIn)
            {
                LatLng latLng = new LatLng(location.getLatitude(),
                        location.getLongitude());
                MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLng);
                bdMap.animateMapStatus(msu);
                isFirstIn = false;
            }
            latitude = location.getLatitude();
            longitude = location.getLongitude();

        }
    }

    private List<Map<String,Object>> getNews(){
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> map;
        for(int i=0; i<100; i++) {
            map = new HashMap<>();
            map.put("user", i+"");
            map.put("position", "#");
            map.put("introduction", "###");
            list.add(map);
        }

        /**
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        map.put("user", "godchen");
        map.put("position", "harbin");
        map.put("introduction", "好开心啊");
        list.add(map);

        map = new HashMap<>();
        map.put("user", "godliu");
        map.put("position", "harbin");
        map.put("introduction", "好开心啊");
        list.add(map);

        map = new HashMap<>();
        map.put("user", "godzhang");
        map.put("position", "harbin");
        map.put("introduction", "好开心啊");
        list.add(map);
**/
        return list;
    }
}
