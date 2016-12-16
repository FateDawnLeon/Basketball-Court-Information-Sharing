package com.example.tkft1.bfe2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Toast;

import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Objects;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.TextureMapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchOption;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.poi.PoiSortType;
import com.baidu.mapapi.search.share.OnGetShareUrlResultListener;
import com.baidu.mapapi.search.share.ShareUrlResult;
import com.baidu.mapapi.search.share.ShareUrlSearch;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MyLocationData;


public class TabHostTest extends Activity implements  View.OnClickListener {

    static private String username;
    static private String password;

    private TabHost th;
    private LayoutInflater i;

    private TabSpec tab01;
    private TabSpec tab02;
    private TabSpec tab03;
    private Intent intent;
    private boolean isclick = false;
    private Login login ;

    private CourtList court = new CourtList();

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

    //1127
    private static StringBuilder sb;
    private ArrayList<BasketballCourtClass> courtlist;

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
        sb = new StringBuilder();
        switch(v.getId())
        {
            case R.id.login:
                EditText editText = (EditText) findViewById(R.id.username);
                username = editText.getText().toString();
                editText = (EditText)findViewById(R.id.password);
                password = editText.getText().toString();
                function.print("aaaaaaaaaaaaaaaaaaaaaaaaaaaa");
                if (!username.isEmpty() && !password.isEmpty()) {
                    login = new Login(th,i,tab03);
                    function.print("bbbbbbbbbbbbbbbbbbbbbbbbb");
                    String[] s = new String[2];
                    s[0] = username;
                    s[1] = password;
                    function.print(s[0]+"   "+s[1]);
                    login.execute(s);
                    function.print("cccccccccccccccccccccccccccccc");
                }
                break;

            case R.id.register:
                intent = new Intent();
                intent.setClass(TabHostTest.this, tab03_my_register.class);
                startActivity(intent);
                break;

            case R.id.courtlist_btn:
//                type = 0;
//                page = 1;
//                citySearchBtn.setEnabled(true);
//                boundSearchBtn.setEnabled(true);
//                nextDataBtn.setEnabled(true);
//                bdMap.clear();
//                citySearch(page);

                th.setCurrentTab(0);
                if(!isclick)
                {
                    i.inflate(R.layout.tab02_vicinity_list, th.getTabContentView());
                    isclick = true;
                }
                court.getCourt(courtlist);
                SimpleAdapter adapter = new SimpleAdapter(this, court.getlist(), R.layout.tab02_vicinity_list_vlist,
                        new String[]{"court_name","court_distance","court_position"},
                        new int[]{R.id.court_name, R.id.court_distance, R.id.court_position});
                ListView lt = (ListView)findViewById(R.id.court_list);
                lt.setAdapter(adapter);

                lt.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        intent = new Intent();
                        intent.putExtra("com.example.tkft1.bfe2.information",position);
                        intent.setClass(TabHostTest.this, tab02_court_detail.class);
                        startActivity(intent);
                    }
                });
                tab02.setContent(R.id.vicinity_list);
                th.setCurrentTab(1);
                break;

            case R.id.court_bnt:
                th.setCurrentTab(0);
                tab02.setContent(R.id.vicinity);
                th.setCurrentTab(1);
                break;

            case R.id.my_information:
                intent = new Intent();
                ArrayList<String> informaion = new ArrayList<>();
                informaion.add(username);
                informaion.add(password);
                intent.putExtra("com.example.tkft1.bfe2.information",informaion);
                intent.setClass(TabHostTest.this, tab03_my_informaion.class);
                startActivity(intent);
                break;

            case R.id.my_news:
                intent = new Intent();
                intent.setClass(TabHostTest.this, tab03_my_news.class);
                startActivity(intent);
                break;

            case R.id.refresh:
                NewsList news = new NewsList(th,i,tab01);
                news.setFinishListener(new NewsList.DataFinishListener() {
                    @Override
                    public void dataFinishSuccessfully(Boolean result) {
                        if(result)
                            adapter();
                        th.setCurrentTab(0);
                    }
                });
                news.execute();
                break;

            case R.id.my_friends:
                intent = new Intent();
                intent.setClass(TabHostTest.this, tab03_my_friends.class);
                startActivity(intent);
                break;

            case R.id.news_sharing_btn:
                intent = new Intent();
                ArrayList<String> information = new ArrayList<>();
                information.add(username);
                information.add(courtlist.get(0).Address);
                intent.putExtra("com.example.tkft1.bfe2.information",information);
                intent.setClass(TabHostTest.this, tab03_news_sharing.class);
                startActivity(intent);
                break;

            case R.id.city_search_btn:
                type = 0;
                page = 1;
                citySearchBtn.setEnabled(true);
                boundSearchBtn.setEnabled(true);
                nextDataBtn.setEnabled(true);
                bdMap.clear();
                citySearch(page);
                break;

            case R.id.bound_search_btn:
                type = 1;
                page = 1;
                citySearchBtn.setEnabled(true);
                boundSearchBtn.setEnabled(true);
                nextDataBtn.setEnabled(true);
                bdMap.clear();
                boundSearch(page);
                break;

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
                    default:
                        break;
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


        NewsList news = new NewsList(th,i,tab01);
        news.setFinishListener(new NewsList.DataFinishListener() {
            @Override
            public void dataFinishSuccessfully(Boolean result) {
               if(result)
                   adapter();
                th.setCurrentTab(1);
            }
        });
        news.execute();

//        SimpleAdapter adapter = new SimpleAdapter(this, news.getlist(), R.layout.tab01_news_vlist,
//                new String[]{"user","position","introduction"},
//                new int[]{R.id.user, R.id.position, R.id.introduction});
//        ListView lt = (ListView)findViewById(R.id.newslist);
//        lt.setAdapter(adapter);
//        lt.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                intent = new Intent();
//                intent.putExtra("com.example.tkft1.bfe2.information",position);
//                intent.setClass(TabHostTest.this, tab01_new_detail.class);
//                startActivity(intent);
//            }
//        });
//        th.setCurrentTab(1);
    }

    private void adapter() {
        NewsList news = new NewsList();
        SimpleAdapter adapter = new SimpleAdapter(this, news.getlist(), R.layout.tab01_news_vlist,
                new String[]{"user","position","introduction"},
                new int[]{R.id.user, R.id.position, R.id.introduction});
        ListView lt = (ListView)findViewById(R.id.newslist);
        lt.setAdapter(adapter);
        lt.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                intent = new Intent();
                intent.putExtra("com.example.tkft1.bfe2.information",position);
                intent.setClass(TabHostTest.this, tab01_new_detail.class);
                startActivity(intent);
            }
        });
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
//        System.out.println("999999999999999999999999999999999999999999"+courtlist);
//        System.out.println("1234"+courtlist.get(0).CourtName);
        editCityEt = (EditText) findViewById(R.id.city);
        editSearchKeyEt = (EditText) findViewById(R.id.searchkey);

        citySearchBtn = (Button) findViewById(R.id.city_search_btn);
        boundSearchBtn = (Button) findViewById(R.id.bound_search_btn);
        nextDataBtn = (Button) findViewById(R.id.next_data_btn);
        nextDataBtn.setEnabled(false);
        citySearchBtn.setOnClickListener(this);
        boundSearchBtn.setOnClickListener(this);
        nextDataBtn.setOnClickListener(this);

        editSearchKeyEt.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                citySearchBtn.setEnabled(true);
                boundSearchBtn.setEnabled(true);
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

    OnGetPoiSearchResultListener poiSearchListener = new OnGetPoiSearchResultListener() {
        @Override
        public void onGetPoiResult(final PoiResult poiResult) {
            System.out.println("1");
            if (poiResult == null
                    || poiResult.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {// 没有找到检索结果
                Toast.makeText(TabHostTest.this, "未找到结果",
                        Toast.LENGTH_LONG).show();
                return;
            }
            System.out.println("2");
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
            System.out.println("3");
            sb.append("共搜索到").append(poiResult.getTotalPoiNum()).append("个POI/n");

            // 遍历当前页返回的POI（默认只返回10个）
            courtlist = new ArrayList<BasketballCourtClass>();
            System.out.println("4");
            new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    try{
                        DbHelper.getConnection();
                        for (PoiInfo poiInfo : poiResult.getAllPoi()) {
                            BasketballCourtClass newcourt = new BasketballCourtClass();
                            newcourt.CourtName = poiInfo.name;
                            newcourt.Location = poiInfo.location;
                            newcourt.City = poiInfo.city;
                            newcourt.Address = poiInfo.address;

                            double d = GetShortDistance(longitude, latitude, poiInfo.location.longitude, poiInfo.location.latitude);;
                            DecimalFormat df = new DecimalFormat("0.00");
                            String DIStance = String.valueOf(df.format(d));
                            newcourt.Distance = DIStance+"m";
                            courtlist.add(newcourt);
                            sb.append("名称：").append(poiInfo.name).append("\n");

                            String sql = "insert into basketballcourt(Address,City,Name,Latitude,Longtitude) values("
                                    +  "'"  +  URLEncoder.encode(newcourt.Address,"UTF-8")    +  "',"
                                    +  "'"  +  URLEncoder.encode(newcourt.City,"UTF-8")       +  "',"
                                    +  "'"  +  URLEncoder.encode(newcourt.CourtName,"UTF-8")  +  "',"
                                    +          newcourt.Location.latitude  + ","
                                    +          newcourt.Location.longitude + ");";
                            function.print(sql);
                            DbHelper.excuteUpdate(sql);
                        }
                        DbHelper.close();
                    } catch (Exception e) {
                        Log.d("123", "run: "+e);
                    }

                }
            }).start();
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
        System.out.println("!!!!!");
        poiSearch.searchInCity(citySearchOption);
        System.out.println("!!!!!@@@@");
    }

    /**
     * 范围检索
     */
    private void boundSearch(int page) {
        initLocation();

        PoiNearbySearchOption nearbySearchOption = new PoiNearbySearchOption();
        LatLng latlng = new LatLng(latitude, longitude);
        nearbySearchOption.sortType(PoiSortType.distance_from_near_to_far);
        nearbySearchOption.location(latlng);
        nearbySearchOption.keyword(editSearchKeyEt.getText().toString());
        nearbySearchOption.radius(5000);// 检索半径，单位是米
        nearbySearchOption.pageNum(page);
        poiSearch.searchNearby(nearbySearchOption);// 发起附近检索请求
    }

    public double GetShortDistance(double lon1, double lat1, double lon2, double lat2){
        double ew1, ns1, ew2, ns2;
        double dx, dy, dew;
        double distance;
        double DEF_PI = 3.14159265359; // PI
        double DEF_2PI= 6.28318530712; // 2*PI
        double DEF_PI180= 0.01745329252; // PI/180.0
        double DEF_R =6370693.5; // radius of earth

        ew1 = lon1 * DEF_PI180;
        ns1 = lat1 * DEF_PI180;
        ew2 = lon2 * DEF_PI180;
        ns2 = lat2 * DEF_PI180;
        dew = ew1 - ew2;
        if (dew > DEF_PI)
            dew = DEF_2PI - dew;
        else if (dew < -DEF_PI)
            dew = DEF_2PI + dew;
        dx = DEF_R * Math.cos(ns1) * dew;
        dy = DEF_R * (ns1 - ns2);
        distance = Math.sqrt(dx * dx + dy * dy);
        return distance;
    }

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
        public void onReceiveLocation(BDLocation location) {
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

            MyLocationData data = new MyLocationData.Builder()//
                    .accuracy(location.getRadius())//
                    .latitude(latitude)//
                    .longitude(longitude)//
                    .build();
            bdMap.setMyLocationData(data);
            if (isFirstIn) {
                LatLng latLng = new LatLng(latitude, longitude);

                System.out.println("1111111111111111111333333333333333333333333555555555555555555" + latLng.latitude + "###########22222222" + latLng.longitude);
                MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLng);
                bdMap.animateMapStatus(msu);
                isFirstIn = false;
            }
        }
    }
}