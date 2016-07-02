package com.jxa.baidumap;

import android.content.Context;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;

public class MainActivity extends AppCompatActivity {
    MapView mMapView = null;
    private BaiduMap baiduMap;
    private MapStatusUpdate factory;
    private LocationClient mLocationClient;
    private MyLocationListener mLocationListener;
    private boolean isFirstLoc = true;//是否首次定位
    private Context context;
    private double mLatitude;
    private double mLongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        //注意该方法要再setContentView方法之前实现
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        this.context = this;
        initView();
        // 初始化定位
        initLocation();

    }

    private void initLocation() {
        //定位初始化
        mLocationClient = new LocationClient(this);
        mLocationListener = new MyLocationListener();
        mLocationClient.registerLocationListener(mLocationListener);
        LocationClientOption option = new LocationClientOption();
        option.setIsNeedAddress(true);
        option.setOpenGps(true);//打开GPS
        option.setCoorType("bd09ll");//设置坐标类型
        option.setScanSpan(1000);
        mLocationClient.setLocOption(option);
        //mLocationClient.start();
    }

    private void initView() {
        //获取地图控件引用
        mMapView = (MapView) findViewById(R.id.bmapView);
        baiduMap = mMapView.getMap();
        factory = MapStatusUpdateFactory.zoomTo(15.0f);
        baiduMap.setMapStatus(factory);
    }

    @Override
    protected void onResume() {
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        baiduMap.setMyLocationEnabled(true);
        if (!mLocationClient.isStarted()) {
            mLocationClient.start();
        }
    }

    @Override
    protected void onPause() {
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        // 退出时销毁定位
        mLocationClient.stop();
        // 关闭定位图层
        baiduMap.setMyLocationEnabled(false);
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
        mMapView = null;
        super.onDestroy();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.id_map_common:
                baiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
                break;
            case R.id.id_map_site:
                baiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
                break;
            case R.id.id_map_traffic:
                if (baiduMap.isTrafficEnabled()) {
                    baiduMap.setTrafficEnabled(false);
                    item.setTitle("实时交通(off)");
                } else {
                    baiduMap.setTrafficEnabled(true);
                    item.setTitle("实时交通(on)");
                }
                break;
            case R.id.id_map_hot:
                baiduMap.setBaiduHeatMapEnabled(true);
                break;
            case R.id.id_map_location:
                location();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //定位到我原来的位置
    private void location() {
        LatLng latLng = new LatLng(mLatitude, mLongitude);
        MapStatus.Builder builder = new MapStatus.Builder();
        builder.target(latLng).zoom(18.0f);
        baiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
    }

    // 定位SDK监听函数
    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            // map view 销毁后不在处理新接收的位置
            if (bdLocation == null || mMapView == null) {
                return;
            }
            MyLocationData data = new MyLocationData.Builder()//
                    .accuracy(bdLocation.getRadius())//精度
                    .direction(100).latitude(bdLocation.getLatitude())// 此处设置获取到的方向信息，顺时针0-360
                    .longitude(bdLocation.getLongitude()).build();
            mLatitude = bdLocation.getLatitude();
            mLongitude = bdLocation.getLongitude();
            if (isFirstLoc) {
                isFirstLoc = false;
                LatLng latLng = new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude());
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(latLng).zoom(18.0f);
                baiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
               /* MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newLatLng(latLng);
                baiduMap.animateMapStatus(mapStatusUpdate);*/
                Toast.makeText(context, bdLocation.getAddrStr(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
