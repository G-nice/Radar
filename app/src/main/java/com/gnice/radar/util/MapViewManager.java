package com.gnice.radar.util;


import android.graphics.Typeface;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.map.Text;
import com.baidu.mapapi.map.TextOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.utils.DistanceUtil;
import com.gnice.radar.AppData;
import com.gnice.radar.R;
import com.gnice.radar.RadarFragment;

import java.util.ArrayList;
import java.util.List;

public class MapViewManager implements PersonItem.OnPersonChangeListener {

    //构建Marker图标
    private final BitmapDescriptor myBitmap = BitmapDescriptorFactory.fromResource(R.mipmap.mark_me);
    private final BitmapDescriptor friendBitmap = BitmapDescriptorFactory.fromResource(R.mipmap.mark_friend);
    private final BitmapDescriptor enemyBitmap = BitmapDescriptorFactory.fromResource(R.mipmap.mark_enemy);
    private MapView mapView = null;
    private BaiduMap baiduMap = null;
    private Marker myMarker;
    // 用于快速切换显示内容
    //    private ArrayList<OverlaySet> friendOverlaySetList;
    private ArrayList<PersonItem> mFriendsList, mEnemiesList;

    public MapViewManager(MapView mapView, ArrayList<PersonItem> friendsList, ArrayList<PersonItem> enemiesList) {
        this.mapView = mapView;
        baiduMap = mapView.getMap();  //获取地图控制器
        //        friendOverlaySetList = new ArrayList<>();

        this.mFriendsList = friendsList;
        this.mEnemiesList = enemiesList;


        hideLogo();

        //        以动画方式更新地图状态，动画耗时 300 ms
        //        baiduMap.animateMapStatus();

        // 获取自己位置  并初始化标志
        //定义Maker坐标点
        LatLng point = new LatLng(AppData.myself.getLatitude(), AppData.myself.getLongitude());

        // 设置地图显示范围  自我中心
        baiduMap.setMapStatusLimits(new LatLngBounds.Builder().include(point).include(point).build());
        //构建MarkerOption，用于在地图上添加Marker
        OverlayOptions option = new MarkerOptions()
                .position(point)
                .icon(myBitmap);
        //在地图上添加Marker，并显示
        myMarker = (Marker) baiduMap.addOverlay(option);
        //        LatLng point2 = new LatLng(38.963175, 115.400244);
        //        myMarker.setPosition(point2);

        // 初始化 对存储已有数据绘图  在RadarFragment启动之前接口还没有初始化 存储的数据不能回调自己绘图
        //        init(AppData.friendsList);

    }


    // 数据变化地图更新接口实现
    @Override
    public void OnInfoChangeListener(PersonItem personItem) {
        updatePersonInfo(personItem);
    }

    @Override
    public void onPositionChange(PersonItem personItem) {
        //        personItem.setDistance(DistanceUtil.getDistance(myMarker.getPosition(), new LatLng(personItem.getLatitude(), personItem.getLongitude())));
        updatePersonPositionDraw(personItem);
    }


    // 旧接口
    //    @Override
    //    public void onNameChange(PersonItem personItem) {
    //    }
    //    @Override
    //    public void onPhoneNumChange(PersonItem personItem) {
    //    }
    //    }
    //    @Override
    //    public void onDistanceChange(float distance) {

    @Override
    public void onCreate(PersonItem personItem) {
        drawPersonInfo(baiduMap, personItem);
    }

    @Override
    public void onRemove(OverlaySet overlaySet) {
        if (overlaySet != null) {
            overlaySet.getMarker().remove();
            overlaySet.getPolyline().remove();
            overlaySet.getTextInfo().remove();
            overlaySet.getTextdDistance().remove();
            //            friendOverlaySetList.remove(overlaySet);
        }
    }

    //    private void init(ArrayList<OverlaySet> friendOverlaySetList, ArrayList<PersonItem> personItemArrayList) {
    public void init() {
        //        for (PersonItem p : personItemArrayList) {
        for (PersonItem p : mFriendsList) {
            p.setDistance(DistanceUtil.getDistance(myMarker.getPosition(), new LatLng(p.getLatitude(), p.getLongitude())));
            //            Log.i("mapviewManager", "init" + p.getLatitude() + p.getLongitude());
            drawPersonInfo(baiduMap, p);
            //            drawPersonInfo(baiduMap, AppData.myself);
        }
        for (PersonItem p : mEnemiesList) {
            drawPersonInfo(baiduMap, p);
        }
    }

    // 刷新全部图标

    //    刷新给定经纬度图标

    //    画直线 连线  标注距离

    //    标注昵称

    // 设置地图显示范围  自我中心 显示所有  /或/  给定中心  适当大小用于当Marker选中时显示详情
    public void AdjustMapScope() {

    }

    // 由于自己位置更新引起的所有人重绘 单元
    //    private void updateMyPositionDrawPerstep(PersonItem personItem){}

    // 当自己位置变化的时候进行相关信息的更新  更新自己的Marker 相关线段以及上边标注的距离
    public void updateMyPositionDraw(double latitude, double longitude) {
        // 更新自己图标
        AppData.myself.setPosition(latitude, longitude);
        AppData.databaseManager.updateMyself(AppData.myself);

        LatLng newLatlng = new LatLng(latitude, longitude);
        myMarker.setPosition(newLatlng);

        // 更新相连的线段以及距离标注
        for (PersonItem personItem : AppData.friendsList) {
            OverlaySet overLaySet = personItem.getOverlaySet();
            LatLng latLngtmp = (overLaySet.getMarker().getPosition());
            ArrayList<LatLng> pontListtmp = new ArrayList<>();
            pontListtmp.add(myMarker.getPosition());
            pontListtmp.add(overLaySet.getMarker().getPosition());

            overLaySet.getPolyline().setPoints(pontListtmp);

            // // TODO: 2016/10/16 要进行距离标示的修改  重新计算距离
            personItem.setDistance(DistanceUtil.getDistance(overLaySet.getMarker().getPosition(), newLatlng));
            overLaySet.getTextdDistance().setText(personItem.getDistanceStr());
            overLaySet.getTextdDistance().setPosition(new LatLng((latLngtmp.latitude + latitude) / 2, (latLngtmp.longitude + longitude) / 2));
            overLaySet.getTextdDistance().setRotate((float) calcTextAngle(latitude, longitude, latLngtmp.latitude, latLngtmp.longitude));
        }

        // 和上面重复
        for (PersonItem personItem : AppData.enemiesList) {
            OverlaySet overLaySet = personItem.getOverlaySet();
            LatLng latLngtmp = (overLaySet.getMarker().getPosition());
            ArrayList<LatLng> pontListtmp = new ArrayList<>();
            pontListtmp.add(myMarker.getPosition());
            pontListtmp.add(overLaySet.getMarker().getPosition());

            overLaySet.getPolyline().setPoints(pontListtmp);

            // // TODO: 2016/10/16 要进行距离标示的修改  重新计算距离
            personItem.setDistance(DistanceUtil.getDistance(overLaySet.getMarker().getPosition(), newLatlng));
            overLaySet.getTextdDistance().setText(personItem.getDistanceStr());
            overLaySet.getTextdDistance().setPosition(new LatLng((latLngtmp.latitude + latitude) / 2, (latLngtmp.longitude + longitude) / 2));
            overLaySet.getTextdDistance().setRotate((float) calcTextAngle(latitude, longitude, latLngtmp.latitude, latLngtmp.longitude));
        }
    }

    public void updatePersonPositionDraw(PersonItem personItem) {
        OverlaySet overlaySet = personItem.getOverlaySet();
        LatLng latLngtmp = new LatLng(personItem.getLatitude(), personItem.getLongitude());
        overlaySet.getTextInfo().setPosition(latLngtmp);
        overlaySet.getMarker().setPosition(latLngtmp);
        ArrayList<LatLng> pointListtmp = new ArrayList<>();
        pointListtmp.add(myMarker.getPosition());
        pointListtmp.add(latLngtmp);
        overlaySet.getPolyline().setPoints(pointListtmp);

        overlaySet.getTextdDistance().setText(personItem.getDistanceStr());
        overlaySet.getTextdDistance().setPosition(new LatLng((myMarker.getPosition().latitude + latLngtmp.latitude) / 2, (myMarker.getPosition().longitude + latLngtmp.longitude) / 2));
        overlaySet.getTextdDistance().setRotate((float) calcTextAngle(myMarker.getPosition().latitude, myMarker.getPosition().longitude, latLngtmp.latitude, latLngtmp.longitude));
    }

    public void updatePersonInfo(PersonItem personItem) {
        OverlaySet overlaySet = personItem.getOverlaySet();
        overlaySet.getTextInfo().setText(personItem.getName() + "\n" + personItem.getPhoneNum());
    }

    public void drawPersonInfo(BaiduMap baiduMap, PersonItem p) {
        //        Log.i("drawPersonInfo", "" + p.getLatitude() + p.getLongitude());
        Marker marker = drawMarker(baiduMap, p.getLatitude(), p.getLongitude(), p.getType());
        p.getOverlaySet().setMarker(marker);

        Polyline polyline = drawLine(baiduMap, myMarker.getPosition().latitude, myMarker.getPosition().longitude, p.getLatitude(), p.getLongitude(), p.getType());
        p.getOverlaySet().setPolyline(polyline);

        Text personInfo = drawTextHorizontal(baiduMap, p.getLatitude(), p.getLongitude(), p.getName() + "\n" + p.getPhoneNum(), p.getType());
        p.getOverlaySet().setTextInfo(personInfo);

        // 角计算起点以自己的标志为起点
        double angle = calcTextAngle(myMarker.getPosition().latitude, myMarker.getPosition().longitude, p.getLatitude(), p.getLongitude());
        Text distance = drawText(baiduMap, (p.getLatitude() + myMarker.getPosition().latitude) / 2, (p.getLongitude() + myMarker.getPosition().longitude) / 2, p.getDistanceStr(), (float) angle, p.getType());
        p.getOverlaySet().setTextdDistance(distance);

        //        p.setOnPersonChangeListener(this);

        //        friendOverlaySetList.add(p.getOverlaySet());
    }

    // 画指标
    public Marker drawMarker(BaiduMap baiduMap, double latitude, double longitude, int type) {
        LatLng point = new LatLng(latitude, longitude);
        MarkerOptions option;
        if (type == PersonItem.FRIEND)
            option = new MarkerOptions()
                    .position(point)
                    .icon(friendBitmap);

        else
            option = new MarkerOptions()
                    .position(point)
                    .icon(enemyBitmap);
        //  .zIndex(0).period(10).alpha(0.5F);
        //在地图上添加Marker，并显示
        return (Marker) baiduMap.addOverlay(option);
        //            mMarker.setTitle(p.getName() + "\n" + p.getPhoneNum());
    }

    // 画线段
    public Polyline drawLine(BaiduMap baiduMap, double beginLatitude, double beginLongitude, double endLatitude, double endLongitude, int type) {
        // 构造折线点坐标
        List<LatLng> points = new ArrayList<>();
        points.add(new LatLng(beginLatitude, beginLongitude));
        points.add(new LatLng(endLatitude, endLongitude));

        //构建分段颜色索引数组
        List<Integer> color = new ArrayList<>();
        color.add(Constant.overLayColor(type));

        // 添加距离线
        OverlayOptions ooPolyline = new PolylineOptions()
                .width(7)
                .colorsValues(color)
                .points(points);

        return (Polyline) baiduMap.addOverlay(ooPolyline);
    }

    public double calcTextAngle(double beginLatitude, double beginLongitude, double endLatitude, double endLongitude) {
        // 计算距离字符旋转角度
        double angle;
        if (endLatitude == beginLatitude) {
            angle = 0;
        } else if (endLongitude == beginLongitude) {
            angle = 0.5F;
        } else {
            angle = Math.atan((beginLatitude - endLatitude) / (beginLongitude - endLongitude));
        }
        angle = angle * 180 / Math.PI;

        return angle;
    }

    public Text drawText(BaiduMap baiduMap, double latitude, double longitude, String content, float angle, int type) {
        //定义用户信息文字所显示的坐标点
        LatLng llText = new LatLng(latitude, longitude);

        //构建文字Option对象，用于在地图上添加文字
        OverlayOptions textOptionInfo = new TextOptions()
                .bgColor(0x7FFFFFFF)
                .fontSize(48)
                .fontColor(Constant.overLayColor(type))
                .text(content)
                .typeface(Typeface.MONOSPACE)
                .rotate(angle)
                .position(llText);

        return (Text) baiduMap.addOverlay(textOptionInfo);
    }

    public Text drawTextHorizontal(BaiduMap baiduMap, double latitude, double longitude, String content, int type) {
        return drawText(baiduMap, latitude, longitude, content, 0F, type);
    }

    public void hideshowDetails(int status) {
        if (status == RadarFragment.HIDE) {
            for (PersonItem p : mFriendsList) {
                //                p.getOverlaySet().getTextInfo().remove();
                p.getOverlaySet().getTextInfo().setVisible(false);
                p.getOverlaySet().getTextdDistance().setVisible(false);
            }
            // 和上面重复
            for (PersonItem p : mEnemiesList) {
                p.getOverlaySet().getTextInfo().setVisible(false);
                p.getOverlaySet().getTextdDistance().setVisible(false);
            }
        } else if (status == RadarFragment.SHOW) {
            for (PersonItem p : mFriendsList) {
                p.getOverlaySet().getTextInfo().setVisible(true);
                p.getOverlaySet().getTextdDistance().setVisible(true);
                //                Text personInfo = drawTextHorizontal(baiduMap, p.getLatitude(), p.getLongitude(), p.getName() + "\n" + p.getPhoneNum(), p.getType());
                //                p.getOverlaySet().setTextInfo(personInfo);
                //
                //                // 角计算起点以自己的标志为起点
                //                double angle = calcTextAngle(myMarker.getPosition().latitude, myMarker.getPosition().longitude, p.getLatitude(), p.getLongitude());
                //                Text distance = drawText(baiduMap, (p.getLatitude() + myMarker.getPosition().latitude) / 2, (p.getLongitude() + myMarker.getPosition().longitude) / 2, p.getDistanceStr(), (float) angle, p.getType());
                //                p.getOverlaySet().setTextdDistance(distance);
            }
            // 和上面重复
            for (PersonItem p : mEnemiesList) {
                p.getOverlaySet().getTextInfo().setVisible(true);
                p.getOverlaySet().getTextdDistance().setVisible(true);
                //                Text personInfo = drawTextHorizontal(baiduMap, p.getLatitude(), p.getLongitude(), p.getName() + "\n" + p.getPhoneNum(), p.getType());
                //                p.getOverlaySet().setTextInfo(personInfo);
                //
                //                // 角计算起点以自己的标志为起点
                //                double angle = calcTextAngle(myMarker.getPosition().latitude, myMarker.getPosition().longitude, p.getLatitude(), p.getLongitude());
                //                Text distance = drawText(baiduMap, (p.getLatitude() + myMarker.getPosition().latitude) / 2, (p.getLongitude() + myMarker.getPosition().longitude) / 2, p.getDistanceStr(), (float) angle, p.getType());
                //                p.getOverlaySet().setTextdDistance(distance);
            }
        }
    }


    private void hideLogo() {
        // 隐藏logo
        //        View child = mapView.getChildAt(1);
        //        if (child != null && (child instanceof ImageView || child instanceof ZoomControls)){
        //            child.setVisibility(View.INVISIBLE);
        //        }
        // 隐藏logo
        mapView.removeViewAt(1);
        //普通地图
        baiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
    }

}
