package com.gnice.radar.util;


import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

public class Util {

    //    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constant.dateFormat, Locale.getDefault());
    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constant.dateFormat, Locale.CHINA);
    private static DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL);
    private static String YD = "^[1]{1}(([3]{1}[4-9]{1})|([5]{1}[012789]{1})|([8]{1}[12378]{1})|([4]{1}[7]{1}))[0-9]{8}$";
    private static String LT = "^[1]{1}(([3]{1}[0-2]{1})|([5]{1}[56]{1})|([8]{1}[56]{1}))[0-9]{8}$";
    private static String DX = "^[1]{1}(([3]{1}[3]{1})|([5]{1}[3]{1})|([8]{1}[09]{1}))[0-9]{8}$";

    /**
     * 获取当前运行的进程名
     *
     * @return
     */
    public static String getMyProcessName() {
        try {
            File file = new File("/proc/" + android.os.Process.myPid() + "/" + "cmdline");
            BufferedReader mBufferedReader = new BufferedReader(new FileReader(file));
            String processName = mBufferedReader.readLine().trim();
            mBufferedReader.close();
            return processName;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static int getAPPVersionCode(Activity activity) {
        PackageManager pm = activity.getPackageManager();//得到PackageManager对象
        int appVersion = -1;

        try {
            PackageInfo pi = pm.getPackageInfo(activity.getPackageName(), 0);//得到PackageInfo对象，封装了一些软件包的信息在里面
            appVersion = pi.versionCode;//获取清单文件中versionCode节点的值

            //            Log.d(TAG, "appVersion="+appVersion);
            //            setVersion(getString(R.string.app_version)+":"+String.valueOf(appVersion));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            //            Log.e(TAG, "getAppVersion:"+e.getCause());
        }
        return appVersion;
    }

    public static String getAPPVersionName(Activity activity) {
        PackageManager pm = activity.getPackageManager();//得到PackageManager对象
        String appVersion = "";

        try {
            PackageInfo pi = pm.getPackageInfo(activity.getPackageName(), 0);//得到PackageInfo对象，封装了一些软件包的信息在里面
            appVersion = pi.versionName;//获取清单文件中versionCode节点的值

            //            Log.d(TAG, "appVersion="+appVersion);
            //            setVersion(getString(R.string.app_version)+":"+String.valueOf(appVersion));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            //            Log.e(TAG, "getAppVersion:"+e.getCause());
        }
        return appVersion;
    }

    public static String getCurrentTimeStr() {
        return simpleDateFormat.format(Calendar.getInstance().getTime());
        //        return dateFormat.format(Calendar.getInstance().getTime());
    }

    public static void sortByName(ArrayList<PersonItem> list) {
        NameComparator ncp = new NameComparator();
        Collections.sort(list, ncp);
    }

    public static void sortByDistance(ArrayList<PersonItem> list) {
        DistanseComparator dcp = new DistanseComparator();
        Collections.sort(list, dcp);
    }

    public static boolean iselPhoneNnm(String number) {
        /*
        * 10. * 移动: 2G号段(GSM网络)有139,138,137,136,135,134,159,158,152,151,150, 11. *
        * 3G号段(TD-SCDMA网络)有157,182,183,188,187,181 147是移动TD上网卡专用号段. 联通: 12. *
        * 2G号段(GSM网络)有130,131,132,155,156 3G号段(WCDMA网络)有186,185 电信: 13. *
        * 2G号段(CDMA网络)有133,153 3G号段(CDMA网络)有189,180 14.
        */

        if (number.length() == 11 && (number.matches(YD) || number.matches(LT) || number.matches(DX))) {
            return true;
        } else {
            return false;
        }
    }

    // 自定义比较器
    private static class NameComparator implements Comparator {
        public int compare(Object obj0, Object obj1) {
            PersonItem p0 = (PersonItem) obj0;
            PersonItem p1 = (PersonItem) obj1;

            return p0.getName().compareTo(p1.getName());
        }
    }

    private static class DistanseComparator implements Comparator {
        public int compare(Object obj0, Object obj1) {
            PersonItem p0 = (PersonItem) obj0;
            PersonItem p1 = (PersonItem) obj1;

            return (int) (p0.getDistance() - p1.getDistance());
        }
    }

}
