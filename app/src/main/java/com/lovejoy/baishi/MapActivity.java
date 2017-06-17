package com.lovejoy.baishi;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Toast;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.*;
import com.amap.api.maps.AMap.OnInfoWindowClickListener;
import com.amap.api.maps.AMap.OnMarkerClickListener;
import com.amap.api.maps.model.*;
import com.lovejoy.baishi.R.drawable;
import com.lovejoy.baishi.R.id;
import com.lovejoy.baishi.R.layout;
import com.lovejoy.baishi.utils.PosService;
import com.lovejoy.baishi.utils.RestaurantPos;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MapActivity extends AppCompatActivity implements
        OnCheckedChangeListener, AMapLocationListener, LocationSource,
        OnInfoWindowClickListener, OnMarkerClickListener {
    private MapView mapView;
    private ArrayList<RestaurantPos> posList = new ArrayList<>();
    private final ArrayList<LatLng> latLngList = new ArrayList<LatLng>();
    private AMap aMap;
    private UiSettings uiSettings;
    private AMapLocationClient aMapLocationClient;//定位服务类。此类提供单次定位、持续定位、地理围栏、最后位置相关功能
    private LocationSource.OnLocationChangedListener listener;//定位参数设置
    private AMapLocationClientOption aMapLocationClientOption;

    public static final LatLng BEIJING = new LatLng(39.90403, 116.407525);// 北京市经纬度

    final public static int REQUEST_CODE_ASK_LOCATION = 124;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(layout.activity_map);


        //Toolbar--------------------------------------------------------------
        Toolbar bar = (Toolbar) this.findViewById(id.toolbar);
        bar.setTitle("地图");
        bar.setNavigationIcon(drawable.ic_arrow_back_white_24dp);
        this.setSupportActionBar(bar);
        this.getSupportActionBar().setDisplayShowHomeEnabled(false);
        bar.setNavigationOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        //MapView--------------------------------------------------------------
        this.mapView = (MapView) this.findViewById(id.map);
        this.mapView.onCreate(savedInstanceState);//必须写
        this.aMap = this.mapView.getMap();
        this.aMap.setMapType(AMap.MAP_TYPE_NORMAL);//设置地图类型
        this.uiSettings = this.aMap.getUiSettings();


        //设置点击事件监听器------------------------------------------------------------
        this.aMap.setOnInfoWindowClickListener(this);
        this.aMap.setOnMarkerClickListener(this);

        //获取定位权限---------------------------------------------------------------
        if (Build.VERSION.SDK_INT >= 23) {
            int checkStoragePermission = ContextCompat.checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION);
            if (checkStoragePermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MapActivity.this, new String[]{
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS}, REQUEST_CODE_ASK_LOCATION);
            }
        }


        new Thread() {
            @Override
            public void run() {
                try {
                    //从服务器添加餐馆位置-----------------------------------------------------------
                    MapActivity.this.posList = PosService.getRestaurantsPos();
                    if (MapActivity.this.posList.size() > 0) {
                        MapActivity.this.addMarkersToMap();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();


        //开启定位-----------------------------------------------------------------
        this.aMap.setLocationSource(this);// 设置定位监听
        this.uiSettings.setMyLocationButtonEnabled(true);//设置默认定位按钮是否显示
        this.aMap.setMyLocationEnabled(true);// 设置为true表示系统定位按钮显示并响应点击，false表示隐藏，默认是false
        this.aMap.moveCamera(CameraUpdateFactory.zoomTo(30));//显示缩放级别


        //显示指南针----------------------------------------------------------------
        this.uiSettings.setCompassEnabled(true);


        //使用绘制样式类MyLocationStyle，对定位图标进行自定义------------------------------------
        MyLocationStyle locationStyle = new MyLocationStyle();
        locationStyle.strokeColor(Color.BLUE);
        locationStyle.strokeWidth(4);
        this.aMap.setMyLocationStyle(locationStyle);


        //aMapLocationClient---------------------------------------------------
        this.aMapLocationClient = new AMapLocationClient(this.getApplicationContext());
        this.aMapLocationClient.setLocationListener(this);


        //aMapLocationClientOption---------------------------------------------
        //初始化定位参数
        this.aMapLocationClientOption = new AMapLocationClientOption();
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        this.aMapLocationClientOption.setLocationMode(AMapLocationMode.Battery_Saving);
        //设置是否返回地址信息（默认返回地址信息）
        this.aMapLocationClientOption.setNeedAddress(true);
        //设置是否只定位一次,默认为false
        this.aMapLocationClientOption.setOnceLocation(false);
        //设置是否强制刷新WIFI，默认为强制刷新
        this.aMapLocationClientOption.setWifiActiveScan(true);
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        this.aMapLocationClientOption.setMockEnable(false);
        //设置定位间隔,单位毫秒,默认为1000ms
        this.aMapLocationClientOption.setInterval(1000);
        //给定位客户端对象设置定位参数
        this.aMapLocationClient.setLocationOption(this.aMapLocationClientOption);
        //启动定位
        this.aMapLocationClient.startLocation();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE_ASK_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                } else {
                    // Permission Denied
                    Toast.makeText(MapActivity.this, "无法获得访问位置权限", Toast.LENGTH_SHORT)
                            .show();
                    finish();
                }
        }
    }

    /**
     * 定位回调监听，当定位完成后调用此方法
     *
     * @param aMapLocation
     */
    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (this.listener != null && aMapLocation != null) {
            this.listener.onLocationChanged(aMapLocation);// 显示系统小蓝点
            if (aMapLocation.getErrorCode() == 0) {
                //定位成功回调信息，设置相关消息
                aMapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
                aMapLocation.getLatitude();//获取经度
                aMapLocation.getLongitude();//获取纬度;
                aMapLocation.getAccuracy();//获取精度信息
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date(aMapLocation.getTime());
                df.format(date);//定位时间
                aMapLocation.getAddress();//地址，如果option中设置isNeedAddress为false，则没有此结果
                aMapLocation.getCountry();//国家信息
                aMapLocation.getProvince();//省信息
                aMapLocation.getCity();//城市信息
                aMapLocation.getDistrict();//城区信息
                aMapLocation.getRoad();//街道信息
                aMapLocation.getCityCode();//城市编码
                aMapLocation.getAdCode();//地区编码

            } else {
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                Log.e("LOVEJOY", "location Error, ErrCode:"
                        + aMapLocation.getErrorCode() + ", errInfo:"
                        + aMapLocation.getErrorInfo());
            }
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
    }

    @Override
    public void activate(LocationSource.OnLocationChangedListener onLocationChangedListener) {
        this.listener = onLocationChangedListener;
    }

    @Override
    public void deactivate() {
        this.listener = null;
    }

    /**
     * 在地图上添加marker
     */
    private void addMarkersToMap() {
        for (int i = 0; i < this.posList.size(); i++) {
            RestaurantPos pos = this.posList.get(i);
            LatLng latLng = new LatLng(pos.getPosY(), pos.getPosX());
            this.latLngList.add(latLng);
            MarkerOptions mo = new MarkerOptions()
                    .anchor(0.5f, 0.5f)
                    .position(latLng)
                    .title(pos.getName())
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                    .draggable(false)
                    .period(50)
                    .setFlat(true);
            Marker marker = this.aMap.addMarker(mo);
            String markerId = marker.getId();
            this.posList.get(i).setMarkerId(markerId);
        }
    }

    /**
     * 对marker标注点点击响应事件
     */
    @Override
    public boolean onMarkerClick(Marker marker) {
        marker.showInfoWindow();
        return true;
    }


    /**
     * 监听点击infowindow窗口事件回调
     */
    @Override
    public void onInfoWindowClick(Marker marker) {
        String markerId = marker.getId();
        RestaurantPos pos = null;
        for (int i = 0; i < this.posList.size(); i++) {
            if (this.posList.get(i).getMarkerId().equals(markerId)) {
                pos = this.posList.get(i);
                break;
            }
        }
        if (pos != null) {
            Intent intent = new Intent(this, DetailRestaurantActivity.class);
            intent.putExtra("MyItem>id", pos.getId());
            intent.putExtra("MyItem>name", pos.getName());
            intent.putExtra("MyItem>image", pos.getImageUrl());
            this.startActivity(intent);
        }
    }


    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        this.mapView.onResume();
    }


    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        this.mapView.onPause();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        this.mapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.mapView.onDestroy();
        //销毁定位客户端
        if (this.aMapLocationClient != null) {
            this.aMapLocationClient.onDestroy();
            this.aMapLocationClient = null;
            this.aMapLocationClientOption = null;
        }
    }
}
