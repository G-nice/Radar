package com.gnice.radar;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.gnice.radar.util.MapViewManager;
import com.gnice.radar.util.PersonItem;

import java.util.List;
import java.util.Random;

import static com.gnice.radar.R.mipmap.hide;


public class RadarFragment extends Fragment implements Toolbar.OnMenuItemClickListener {
    public static final int HIDE = 0;
    public static final int SHOW = 1;
    private static final String STATE_SAVE_IS_HIDDEN = "STATE_SAVE_IS_HIDDEN";
    //    public LocationClient mLocationClient = null;
    public BDLocationListener myListener = new MyLocationListener();
    private MapView mapView;
    private Toolbar toolbar;
    private MapViewManager mapViewManager;
    private int hideshowStatus = SHOW;


    //    public static RadarFragment newInstance(String param1) {
    //        RadarFragment fragment = new RadarFragment();
    //        Bundle args = new Bundle();
    //        args.putString("agrs1", param1);
    //        fragment.setArguments(args);
    //        return fragment;
    //    }

    public RadarFragment() {
        super();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("Radar", "Radar onCreate");

        // 定位
        //        mLocationClient = new LocationClient(getActivity().getApplicationContext());     //声明LocationClient类
        AppData.mLocationClient.registerLocationListener(myListener);    //注册监听函数
        initLocation();

        // 解决 重新启动 fragment 重叠问题
        if (savedInstanceState != null) {
            boolean isSupportHidden = savedInstanceState.getBoolean(STATE_SAVE_IS_HIDDEN);

            FragmentTransaction ft = getFragmentManager().beginTransaction();
            if (isSupportHidden) {
                ft.hide(this);
            } else {
                ft.show(this);
            }
            ft.commit();
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // 解决 重新启动 fragment 重叠问题
        outState.putBoolean(STATE_SAVE_IS_HIDDEN, isHidden());
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        //注意该方法要再setContentView方法之前实现
        //        SDKInitializer.initialize(getActivity().getApplicationContext());
        View view = inflater.inflate(R.layout.radar_fragment, container, false);
        //        Bundle bundle = getArguments();
        //        String agrs1 = bundle.getString("agrs1");

        toolbar = (Toolbar) view.findViewById(R.id.toolbar_radar);
        toolbar.inflateMenu(R.menu.radar_toolbar_menu);
        toolbar.setOnMenuItemClickListener(this);


        //        MenuItem item = menu.findItem(R.id.share);
        //        item.setIcon(R.drawable.share);


        mapView = (MapView) view.findViewById(R.id.bmapView);
        mapViewManager = new MapViewManager(mapView, AppData.friendsList, AppData.enemiesList);
        mapViewManager.init();

        PersonItem.onPersonChangeListener = mapViewManager;

        return view;
    }


    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int menuItemId = item.getItemId();
        if (menuItemId == R.id.refresh) {
            //            Snackbar.make(getView(), "refresh finish.", Snackbar.LENGTH_INDEFINITE)
            //                    .setAction("I know", new View.OnClickListener() {
            //                        @Override
            //                        public void onClick(View v) {
            //                            AppData.mLocationClient.start();
            //                        }
            //                    })
            //                    .show();
            AppData.mLocationClient.start();
            //todo 向所有的人物发送位置请求信息

        } else if (menuItemId == R.id.location) {
            // 设置地图显示范围  自我中心
            LatLng point = new LatLng(AppData.myself.getLatitude(), AppData.myself.getLongitude());
            mapView.getMap().setMapStatusLimits(new LatLngBounds.Builder().include(point).include(point).build());
            //            AppData.mLocationClient.stop();

        } else if (menuItemId == R.id.switch_view) {

        } else if (menuItemId == R.id.show_hide_details) {
            if (hideshowStatus == SHOW) {
                item.setIcon(hide);
                mapViewManager.hideshowDetails(HIDE);
                hideshowStatus = HIDE;
            } else if (hideshowStatus == HIDE) {
                item.setIcon(R.mipmap.show);
                mapViewManager.hideshowDetails(SHOW);
                hideshowStatus = SHOW;
            }

        }
        return true;
    }

    // 配置定位SDK参数
    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        //        option.setLocationMode(LocationClientOption.LocationMode.Device_Sensors);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        //        option.setLocationMode(LocationClientOption.LocationMode.Battery_Saving);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        //        int span = 5000;
        //        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setScanSpan(0);
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤GPS仿真结果，默认需要
        AppData.mLocationClient.setLocOption(option);
    }

    @Override

    public void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mapView.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        AppData.mLocationClient.stop();
        mapView.onPause();
    }

    // 内部类  监听调用
    // 实现BDLocationListener接口
    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            //Receive Location
            StringBuffer sb = new StringBuffer(256);
            sb.append("time : ");
            sb.append(location.getTime());
            sb.append("\nerror code : ");
            sb.append(location.getLocType());
            sb.append("\nlatitude : ");
            sb.append(location.getLatitude());
            sb.append("\nlontitude : ");
            sb.append(location.getLongitude());
            sb.append("\nradius : ");
            sb.append(location.getRadius());
            if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
                sb.append("\nspeed : ");
                sb.append(location.getSpeed());// 单位：公里每小时
                sb.append("\nsatellite : ");
                sb.append(location.getSatelliteNumber());
                sb.append("\nheight : ");
                sb.append(location.getAltitude());// 单位：米
                sb.append("\ndirection : ");
                sb.append(location.getDirection());// 单位度
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());
                sb.append("\ndescribe : ");
                sb.append("gps定位成功");

            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());
                //运营商信息
                sb.append("\noperationers : ");
                sb.append(location.getOperators());
                sb.append("\ndescribe : ");
                sb.append("网络定位成功");
            } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
                sb.append("\ndescribe : ");
                sb.append("离线定位成功，离线定位结果也是有效的");
            } else if (location.getLocType() == BDLocation.TypeServerError) {
                sb.append("\ndescribe : ");
                sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
            } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                sb.append("\ndescribe : ");
                sb.append("网络不同导致定位失败，请检查网络是否通畅");
            } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                sb.append("\ndescribe : ");
                sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
            }
            sb.append("\nlocationdescribe : ");
            sb.append(location.getLocationDescribe());// 位置语义化信息
            List<Poi> list = location.getPoiList();// POI数据
            if (list != null) {
                sb.append("\npoilist size = : ");
                sb.append(list.size());
                for (Poi p : list) {
                    sb.append("\npoi= : ");
                    sb.append(p.getId() + " " + p.getName() + " " + p.getRank());
                }
            }
            //            Log.i("BaiduLocationApiDem", sb.toString());
            //            Toast.makeText(getContext(), sb.toString(), Toast.LENGTH_SHORT).show();

            // 模拟 获得地理位置新数据
            // test data create
            Random random = new Random();
            if (location.getLocType() == BDLocation.TypeServerError) {
                // test
                //                mapViewManager.updateMyPositionDraw(25, 112);
                mapViewManager.updateMyPositionDraw(AppData.myself.getLatitude() + random.nextDouble() * 1, AppData.myself.getLongitude() + random.nextDouble() * 1);
                Snackbar.make(getView(), "refresh ERROR. Please check your setting", Snackbar.LENGTH_SHORT)
                        .setAction("I know", null)
                        .show();

            } else {
                mapViewManager.updateMyPositionDraw(location.getLatitude(), location.getLongitude());
                // 数据库更新 --> 在函数updateMyPositionDraw里面
                Snackbar.make(getView(), "refresh finish.", Snackbar.LENGTH_INDEFINITE)
                        .setAction("I know", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                            }
                        })
                        .show();
            }
            AppData.databaseManager.updateMyself(AppData.myself);
            // 单次更新
            AppData.mLocationClient.stop();

            // test
            AppData.friendsList.get(random.nextInt(AppData.friendsList.size())).setPosition(AppData.myself.getLatitude() + random.nextDouble() * 1, AppData.myself.getLongitude() + random.nextDouble() * 1);

            Log.i("BaiduLocationApiDem", "position update");
        }
    }

}
