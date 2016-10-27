package com.gnice.radar;

import android.app.Application;

import com.baidu.location.LocationClient;
import com.gnice.radar.database.DatabaseManager;
import com.gnice.radar.util.PersonItem;

import java.util.ArrayList;
import java.util.HashMap;

//import cn.bmob.newim.BmobIM;

// XX单例模式XX因为继承了Application所以构造函数不能私有  不能实现单例模式
public class AppData extends Application {
    public static ArrayList<PersonItem> friendsList = null;
    public static ArrayList<PersonItem> enemiesList = null;
    // 进行电话号码到人的索引  用于检查电话号码是否重复 以及收到位置短信后进行对应人的位置更新
    public static HashMap<String, PersonItem> dictionary = null;
    public static PersonItem myself;
    // 保存一个 地图服务器
    public static LocationClient mLocationClient;
    // 保存一个数据库管理
    public static DatabaseManager databaseManager;
    private static AppData instance;

    // 使得在任何地方都能够返回Appdata的实例
    public static AppData getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // 初始化IM
        // NewIM初始化 在初始化的时候，最好做下判断：只有主进程运行的时候才开始初始化，避免资源浪费。
        //        if (getApplicationInfo().packageName.equals(Util.getMyProcessName())) {
        //            BmobIM.init(this);
        //            //注册消息接收器
        //            BmobIM.registerDefaultMessageHandler(new MessageHandler());
        //        }


        instance = this;
        friendsList = new ArrayList<>();
        enemiesList = new ArrayList<>();
        dictionary = new HashMap<>();
        myself = new PersonItem("MYSELF", "88888888888", PersonItem.MYSELF);
        myself.setPosition(39.963175, 116.400244);
        myself.setType(PersonItem.MYSELF);

        //        Log.i("data", "" + myself.getLatitude() + " " + myself.getLongitude());
    }

    //    @Override
    //    public void onTerminate() {
    //
    //    }


}
