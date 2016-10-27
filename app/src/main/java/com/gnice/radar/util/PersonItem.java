package com.gnice.radar.util;


import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.gnice.radar.AppData;

import java.util.Random;

public class PersonItem {
    public static final int FRIEND = 0;
    public static final int ENEMY = 1;
    public static final int MYSELF = 2;

    // 为了能够进行 在person信息变化的时候自动更新绘图  将这个接口声明成类变量
    public static OnPersonChangeListener onPersonChangeListener = null;
    // 进行recyclerView中item距离的更新
    //    private OnPersonChangeUpdateListListener onPersonChangeUpdateListListener = null;

    private String name;
    private int type = FRIEND;
    private String phoneNum;
    private double longitude = 0;
    private double latitude = 0;
    private double distance = 0;  // 单位 米
    private String lastUpdate = "";

    private OverlaySet overlaySet;

    private int palette_color;


    public PersonItem(String name, String phoneNum, int type) {
        this.name = name.trim();
        this.phoneNum = phoneNum.trim();
        this.type = type;
        this.palette_color = Constant.palette[(int) (Long.parseLong(phoneNum) % Constant.palette.length)];

        this.overlaySet = new OverlaySet();

        // // TODO: 2016/10/19 解决初始添加一个人 立即发送位置请求信息
        // test data create
        Random random = new Random();
        if (AppData.myself != null) {
            latitude = AppData.myself.getLatitude() + random.nextDouble() * 1 - 0.5;
            longitude = AppData.myself.getLongitude() + random.nextDouble() * 1 - 0.5;
            distance = DistanceUtil.getDistance(new LatLng(latitude, longitude), new LatLng(AppData.myself.getLatitude(), AppData.myself.getLongitude()));
            //            Log.i("person", "construct" + AppData.myself.getLatitude() + AppData.myself.getLongitude());
        }

        // 在RadarFragment启动之前 接口都 == null  即对存储的数据初始化时候不会调用
        if (onPersonChangeListener != null)
            onPersonChangeListener.onCreate(this);
    }

    public void setOnPersonChangeListener(OnPersonChangeListener onPersonChangeListener) {
        //        this.onPersonChangeListener = onPersonChangeListener;
        onPersonChangeListener = onPersonChangeListener;
    }

    //    public interface OnPersonChangeUpdateListListener {
    //        void OnPersonChangeUpdateList(PersonItem personItem);
    //    }

    // 由于Java中没有析构函数  所以删除人物对象的时候要进行delete的显式调用进行地图的更新
    public void delete() {
        if (onPersonChangeListener != null)
            //            this.onPersonChangeListener.onRemove(this.overlaySet);
            // 在RadarFragment启动之前 接口都 == null  即对存储的数据初始化时候不会调用
            if (onPersonChangeListener != null)
                onPersonChangeListener.onRemove(this.overlaySet);
    }

    //    public void setOnPersonChangeListenerUpdateList(OnPersonChangeUpdateListListener onPersonChangeListenerUpdateList) {
    //        this.onPersonChangeUpdateListListener = onPersonChangeListenerUpdateList;
    //    }

    public OverlaySet getOverlaySet() {
        return this.overlaySet;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        // 在RadarFragment启动之前 接口都 == null  即对存储的数据初始化时候不会调用
        if (onPersonChangeListener != null)
            onPersonChangeListener.OnInfoChangeListener(this);
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
        this.palette_color = Constant.palette[(int) (Long.parseLong(phoneNum) % Constant.palette.length)];
        // 在RadarFragment启动之前 接口都 == null  即对存储的数据初始化时候不会调用
        if (onPersonChangeListener != null)
            onPersonChangeListener.OnInfoChangeListener(this);
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getDistance() {
        return distance;
    }

    //    计算据距离 由于需要知道中心位置 由MapViewManager进行计算并调用setDistance对本人的距离信息进行更新
    public void setDistance(double distance) {
        this.distance = distance;
        //        onPersonChangeListener.onDistanceChange(distance);
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getPaletteColor() {
        return this.palette_color;
    }

    public String getLastUpdate() {
        return this.lastUpdate;
    }

    public void setLastUpdate(String string) {
        this.lastUpdate = string;
    }

    public String getDistanceStr() {
        double num = 0;
        String unit;

        if (distance == 0) {
            return "∞ M";
        }

        if (distance < 1000) {
            num = distance;
            unit = "M";
        } else {
            num = distance / 1000;
            unit = "KM";
        }

        if (num < 10) {
            return String.format("%4.3f %s", num, unit);
        } else if (num < 100) {
            return String.format("%4.2f %s", num, unit);
        } else {
            return String.format("%4.1f %s", num, unit);
        }
    }

    // 包含改变 经度 维度 以及 计算 距离
    public void setPosition(double latitude, double longitude) {
        this.longitude = longitude;
        this.latitude = latitude;
        updateLastUpdate();
        if (this != AppData.myself) {
            // 缺少SDK initializer 会 调用不成功
            distance = DistanceUtil.getDistance(new LatLng(latitude, longitude), new LatLng(AppData.myself.getLatitude(), AppData.myself.getLongitude()));
        }

        // 计算据距离 由于需要知道中心位置 由MapViewManager进行计算并调用setDistance对本人的距离信息进行更新
        // 在RadarFragment启动之前 接口都 == null  即对存储的数据初始化时候不会调用
        if (this != AppData.myself && onPersonChangeListener != null)
            onPersonChangeListener.onPositionChange(this);
        //        if (this != AppData.myself && onPersonChangeUpdateListListener != null)
        //            onPersonChangeUpdateListListener.OnPersonChangeUpdateList(this);
    }

    // 当调用setposition的时候自动地调用
    public void updateLastUpdate() {
        this.lastUpdate = Util.getCurrentTimeStr();
    }

    // Person信息改变的进行地图数据更新 回调接口
    public interface OnPersonChangeListener {
        //        void onNameChange(PersonItem personItem);
        //        void onPhoneNumChange(PersonItem personItem);
        void OnInfoChangeListener(PersonItem personItem);

        // 包含改变经度 维度 和 距离
        void onPositionChange(PersonItem personItem);

        //        void onDistanceChange(float distance);

        void onCreate(PersonItem personItem);

        void onRemove(OverlaySet overlaySet);
    }


}
