package com.gnice.radar;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.ashokvarma.bottomnavigation.BadgeItem;
import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.baidu.location.LocationClient;
import com.baidu.mapapi.SDKInitializer;
import com.gnice.radar.database.DatabaseHelper;
import com.gnice.radar.database.DatabaseManager;
import com.gnice.radar.util.MySMSManager;
import com.gnice.radar.util.PersonItem;
import com.gnice.radar.util.Util;

import static com.gnice.radar.AppData.databaseManager;

public class MainActivity extends AppCompatActivity implements BottomNavigationBar.OnTabSelectedListener {
    //    private ArrayList<Fragment> fragments;
    private static final int BAIDU_READ_PHONE_STATE = 0x100;
    private static final int BAIDU_LOCATION_HARDWARE = 0x101;
    private static final int GNICE_SEND_SMS = 0x110;
    private static final int GNICE_READ_SMS = 0x111;

    private BottomNavigationBar bottomNavigationBar = null;

    private RadarFragment radarFragment = null;
    private FriendFragment friendFragment = null;
    private EnemyFragment enemyFragment = null;
    private SettingFragment settingFragment = null;

    private MySMSManager mySMSManager;

    private BadgeItem numberBadgeItem0;

    // 当前标签页
    private int currentabPos = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 状态栏沉浸
        // 5.0 以上
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//5.0及以上
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
            //4.4到5.0
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
            localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
        }

        // 初始化短信收发通讯模块
        mySMSManager = new MySMSManager(this);

        // 初始化数据库
        databaseManager = new DatabaseManager(getApplicationContext());

        // 初始化百度地图SDK
        SDKInitializer.initialize(getApplicationContext());

        // 初始化百度定位服务
        AppData.mLocationClient = new LocationClient(getApplicationContext());     //声明LocationClient类


        // 底部导航控件
        bottomNavigationBar = (BottomNavigationBar) findViewById(R.id.bottom_navigation_bar);
        //        bottomNavigationBar.setMode(BottomNavigationBar.MODE_FIXED);
        //        bottomNavigationBar.setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_STATIC);
        bottomNavigationBar.setMode(BottomNavigationBar.MODE_SHIFTING);
        bottomNavigationBar.setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_RIPPLE);

        //        bottomNavigationBar
        //                .setActiveColor(R.color.colorPrimary)
        //                .setInActiveColor("#FFFFFF")
        //                .setBarBackgroundColor("#ECECEC");

        // 红色圆点
        numberBadgeItem0 = new BadgeItem()
                .setBorderWidth(4)
                .setBackgroundColorResource(android.R.color.holo_red_light)
                .setText("99+")
                .setAnimationDuration(200);
        //                .setHideOnSelect(true);
        //                .setHideOnSelect(autoHide.isChecked());

        bottomNavigationBar.addItem(new BottomNavigationItem(R.mipmap.radar_normal, "Radar").setActiveColorResource(R.color.radar_green).setBadgeItem(numberBadgeItem0))
                .addItem(new BottomNavigationItem(R.mipmap.friend_normal, "Friends").setActiveColorResource(R.color.friend_blue))
                .addItem(new BottomNavigationItem(R.mipmap.enemy_normal, "Enemies").setActiveColorResource(R.color.enemy_red))
                .addItem(new BottomNavigationItem(R.mipmap.setting_normal, "Setting").setActiveColorResource(R.color.setting_orange))
                //                .addItem(new BottomNavigationItem(R.mipmap.ic_videogame_asset_white_24dp, "Games").setActiveColorResource(R.color.grey))
                .setFirstSelectedPosition(0)
                .initialise();

        //        fragments = getFragments();
        // initialize all fragments
        initFragments();
        bottomNavigationBar.setTabSelectedListener(this);
        setDefaultFragment();

        // 加载数据库中的数据
        loadData();

        // 测试数据
        //        test_data();


        // 检测系统有无打开位置开关
        LocationManager locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            // 未打开位置开关，可能导致定位失败或定位不准，提示用户或做相应处理
            Log.i("位置开关", "close");
        }

    }

    @TargetApi(Build.VERSION_CODES.M)
    private void requestPermission() {

        // android 6.0 以上 动态申请权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getApplicationContext().checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                // 申请一个（或多个）权限，并提供用于回调返回的获取码（用户定义)
                // 第一次请求权限时，用户如果拒绝，下一次请求shouldShowRequestPermissionRationale()返回true
                // 向用户解释为什么需要这个权限
                if (shouldShowRequestPermissionRationale(Manifest.permission.READ_PHONE_STATE)) {
                    new AlertDialog.Builder(this)
                            .setMessage("获取手机状态用于更好地定位服务")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //申请相机权限
                                    requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, BAIDU_READ_PHONE_STATE);
                                }
                            })
                            .show();
                } else {
                    requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, BAIDU_READ_PHONE_STATE);
                }

            }

            if (getApplicationContext().checkSelfPermission(Manifest.permission.LOCATION_HARDWARE) != PackageManager.PERMISSION_GRANTED) {
                // 申请一个（或多个）权限，并提供用于回调返回的获取码（用户定义)
                requestPermissions(new String[]{Manifest.permission.LOCATION_HARDWARE}, BAIDU_LOCATION_HARDWARE);
            }
            if (getApplicationContext().checkSelfPermission(Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.SEND_SMS}, GNICE_SEND_SMS);
            }
            if (getApplicationContext().checkSelfPermission(Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_SMS}, GNICE_SEND_SMS);
            }
        }
    }


    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            // requestCode即所声明的权限获取码，在checkSelfPermission时传入
            case BAIDU_READ_PHONE_STATE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 获取到权限，作相应处理（调用定位SDK应当确保相关权限均被授权，否则可能引起定位失败）
                    Log.i("permission", "PERMISSION_GRANTED Get");
                } else {
                    // 没有获取到权限，做特殊处理
                    //用户勾选了不再询问
                    //提示用户手动打开权限
                    if (!shouldShowRequestPermissionRationale(Manifest.permission.READ_PHONE_STATE)) {
                        Toast.makeText(getApplicationContext(), "获取手机状态权限已被禁止,无法定位,请手动打开", Toast.LENGTH_LONG).show();
                        Log.i("permission", "PERMISSION_GRANTED Reject");
                    }
                }
                break;
            case BAIDU_LOCATION_HARDWARE:
                // // TODO: 2016/10/18 权限申请处理
                break;
            case GNICE_SEND_SMS:
                break;
            case GNICE_READ_SMS:
                break;
            default:
                break;
        }
    }

    /**
     * 设置默认的  ???加入并隐藏其他???
     */

    private void setDefaultFragment() {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        //        transaction.replace(R.id.layFrame, RadarFragment.newInstance("Radar"));

        //        if (friendFragment != null) {
        //            transaction.add(R.id.layFrame, friendFragment);
        //        } else {
        //            friendFragment = new FriendFragment();
        //            transaction.replace(R.id.layFrame, friendFragment);
        //        }
        //        transaction.hide(friendFragment);
        //        if (enemyFragment != null) {
        //            transaction.add(R.id.layFrame, enemyFragment);
        //        } else {
        //            enemyFragment = new EnemyFragment();
        //            transaction.replace(R.id.layFrame, enemyFragment);
        //        }
        //        transaction.hide(enemyFragment);
        //        if (settingFragment != null) {
        //            transaction.add(R.id.layFrame, settingFragment);
        //        } else {
        //            settingFragment = new SettingFragment();
        //            transaction.replace(R.id.layFrame, settingFragment);
        //        }
        //        transaction.hide(settingFragment);


        if (radarFragment != null) {
            transaction.add(R.id.layFrame, radarFragment);
        } else {
            radarFragment = new RadarFragment();
            transaction.replace(R.id.layFrame, radarFragment);
        }

        //        transaction.replace(R.id.layFrame, fragments.get(0));
        transaction.commit();
        currentabPos = 0;
    }

    private void initFragments() {
        radarFragment = new RadarFragment();
        friendFragment = new FriendFragment();
        enemyFragment = new EnemyFragment();
        settingFragment = new SettingFragment();
    }

    //    private ArrayList<Fragment> getFragments() {
    //        ArrayList<Fragment> fragments = new ArrayList<>();
    //        fragments.add(RadarFragment.newInstance("Radar"));
    //        fragments.add(FriendFragment.newInstance("Friends"));
    //        fragments.add(EnemyFragment.newInstance("Enemies"));
    //        fragments.add(SettingFragment.newInstance("Setting"));
    ////        fragments.add(GameFragment.newInstance("Games"));
    //        return fragments;
    //    }

    @Override
    public void onTabSelected(int position) {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        //        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        if (currentabPos < position)
            transaction.setCustomAnimations(R.animator.fragment_slide_left_enter, R.animator.fragment_slide_left_exit);
        else
            transaction.setCustomAnimations(R.animator.fragment_slide_right_enter, R.animator.fragment_slide_right_exit);

        switch (position) {
            case 0:
                if (radarFragment == null) {
                    radarFragment = new RadarFragment();
                    transaction.add(R.id.layFrame, radarFragment, "radar");  // Tag

                } else if (radarFragment.isHidden()) {
                    transaction.show(radarFragment);
                } else {
                    transaction.add(R.id.layFrame, radarFragment, "radar");  // Tag
                }
                numberBadgeItem0.hide();
                break;
            case 1:
                if (friendFragment == null) {
                    friendFragment = new FriendFragment();
                    transaction.add(R.id.layFrame, friendFragment, "friend");  // Tag

                } else if (friendFragment.isHidden()) {
                    transaction.show(friendFragment);
                    friendFragment.refreshList();
                } else {
                    transaction.add(R.id.layFrame, friendFragment, "friend");  // Tag
                }
                break;
            case 2:
                if (enemyFragment == null) {
                    enemyFragment = new EnemyFragment();
                    transaction.add(R.id.layFrame, enemyFragment, "enemy");  // Tag

                } else if (enemyFragment.isHidden()) {
                    transaction.show(enemyFragment);
                    enemyFragment.refreshList();
                } else {
                    transaction.add(R.id.layFrame, enemyFragment, "enemy");  // Tag
                }
                break;
            case 3:
                if (settingFragment == null) {
                    settingFragment = new SettingFragment();
                    transaction.add(R.id.layFrame, settingFragment, "setting");  // Tag

                } else if (settingFragment.isHidden()) {
                    transaction.show(settingFragment);
                } else {
                    transaction.add(R.id.layFrame, settingFragment, "setting");  // Tag
                }
                break;
            default:
                break;
        }
        transaction.commitAllowingStateLoss();

        currentabPos = position;


        //        if (fragments != null) {
        //            if (position < fragments.size()) {
        //                FragmentManager fm = getFragmentManager();
        //                FragmentTransaction ft = fm.beginTransaction();
        //                Fragment fragment = fragments.get(position);
        //                if (fragment.isAdded()) {
        //                    ft.replace(R.id.layFrame, fragment);
        //                } else {
        //                    ft.add(R.id.layFrame, fragment);
        ////                    ft.replace(R.id.layFrame, fragment);
        //                }
        //                ft.commitAllowingStateLoss();
        //            }
        //        }

    }

    @Override
    public void onTabUnselected(int position) {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        //        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
        if (currentabPos > position)
            transaction.setCustomAnimations(R.animator.fragment_slide_left_enter, R.animator.fragment_slide_left_exit);
        else
            transaction.setCustomAnimations(R.animator.fragment_slide_right_enter, R.animator.fragment_slide_right_exit);

        switch (position) {
            case 0:
                if (radarFragment.isAdded()) {
                    transaction.hide(radarFragment);
                }
                break;
            case 1:
                if (friendFragment.isAdded()) {
                    transaction.hide(friendFragment);
                }
                break;
            case 2:
                if (enemyFragment.isAdded()) {
                    transaction.hide(enemyFragment);
                }
                break;
            case 3:
                if (settingFragment.isAdded()) {
                    transaction.hide(settingFragment);
                }
                break;
            default:
                break;
        }
        transaction.commitAllowingStateLoss();

        //        if (fragments != null) {
        //            if (position < fragments.size()) {
        //                FragmentManager fm = getFragmentManager();
        //                FragmentTransaction ft = fm.beginTransaction();
        //                Fragment fragment = fragments.get(position);
        ////                ft.remove(fragment);
        //                ft.hide(fragment);
        //                ft.commitAllowingStateLoss();
        //            }
        //        }
    }

    @Override
    public void onTabReselected(int position) {

    }


    @Override
    public void onDestroy() {
        databaseManager.close();
        mySMSManager.unRegister();
        super.onDestroy();
    }

    private void loadData() {
        Cursor cursor = AppData.databaseManager.getAType(PersonItem.MYSELF);
        if (cursor.getCount() != 0 && cursor.moveToFirst()) {
            do {
                AppData.myself.setPosition(cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.LATITUDE)),
                        cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.LONGITUDE)));
            } while (cursor.moveToNext());
        }

        cursor = databaseManager.getAType(PersonItem.FRIEND);
        if (!AppData.friendsList.isEmpty())
            AppData.friendsList.clear();
        if (cursor.getCount() != 0 && cursor.moveToFirst()) {
            do {
                PersonItem personItem = new PersonItem(
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.NAME)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.PHONENUM)),
                        PersonItem.FRIEND);
                personItem.setPosition(cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.LATITUDE)),
                        cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.LONGITUDE)));
                personItem.setDistance(cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.DISTANCE)));
                personItem.setLastUpdate(cursor.getString(cursor.getColumnIndex(DatabaseHelper.LASTUPDATE)));
                AppData.friendsList.add(personItem);
                AppData.dictionary.put(personItem.getPhoneNum(), personItem);
            } while (cursor.moveToNext());
        }

        cursor = databaseManager.getAType(PersonItem.ENEMY);
        if (!AppData.enemiesList.isEmpty())
            AppData.enemiesList.clear();
        if (cursor.getCount() != 0 && cursor.moveToFirst()) {
            do {
                PersonItem personItem = new PersonItem(
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.NAME)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.PHONENUM)),
                        PersonItem.ENEMY);
                personItem.setPosition(cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.LATITUDE)),
                        cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.LONGITUDE)));
                personItem.setDistance(cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.DISTANCE)));
                personItem.setLastUpdate(cursor.getString(cursor.getColumnIndex(DatabaseHelper.LASTUPDATE)));
                AppData.enemiesList.add(personItem);
                AppData.dictionary.put(personItem.getPhoneNum(), personItem);
            } while (cursor.moveToNext());
        }

    }


    private void test_data() {
        //        Log.i("test person", "data create");

        PersonItem p1 = new PersonItem("Gnice", "15820577562", PersonItem.FRIEND);
        AppData.friendsList.add(p1);

        //        PersonItem p2 = new PersonItem("tony", "15820577556", PersonItem.FRIEND);
        //        AppData.friendsList.add(p2);

        //        PersonItem p3 = new PersonItem("Jenny", "15820577560", PersonItem.FRIEND);
        //        AppData.friendsList.add(p3);

        PersonItem p4 = new PersonItem("Jack", "15820577563", PersonItem.FRIEND);
        AppData.friendsList.add(p4);

        PersonItem p5 = new PersonItem("Dish", "15820577564", PersonItem.FRIEND);
        AppData.friendsList.add(p5);

        PersonItem p6 = new PersonItem("Stephen", "15820577565", PersonItem.FRIEND);
        AppData.friendsList.add(p6);

        PersonItem p7 = new PersonItem("Job", "15820577562", PersonItem.FRIEND);
        AppData.friendsList.add(p7);

        //        PersonItem p8 = new PersonItem("Chan", "15820577568", PersonItem.FRIEND);
        //        AppData.friendsList.add(p8);

        PersonItem p9 = new PersonItem("Bush", "15820577567", PersonItem.FRIEND);
        AppData.friendsList.add(p9);

        //        PersonItem p10 = new PersonItem("Yoyo", "15820577569", PersonItem.FRIEND);
        //        AppData.friendsList.add(p10);

        AppData appData = (AppData) getApplication();
        Util.sortByName(AppData.friendsList);

    }

}
