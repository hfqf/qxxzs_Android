package com.points.autorepar.common;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.points.autorepar.R;

import java.util.concurrent.RecursiveTask;

/**
 * Created by points on 16/11/30.
 */
public class Consts {

    private final static  int          dev_mode                    =  0;//  1是测试环境 0是生产环境s
//    public static  final   String       HTTP_URL                          = dev_mode == 1 ? "http://192.168.0.107:18080"  :  "http://www.autorepairehelper.cn";
    public  static final   String       HTTP_URL                   = dev_mode == 1 ? "http://192.168.0.101:18080"  :  "https://www.autorepairehelper.cn";
    public  final   Boolean      isDev()                          {return dev_mode == 1;}

    public  final   String       VIP_LEVEL                         = "0";//暂时都是0

    public  final   String       OS_TYPE                           = "android";

    public static final   String       BOS_SERVER                         = HTTP_URL+"/file/pic/";
    public  final   String       key_Parcel_repairHistory          = "key_Parcel_repairHistory";


    public static int getLocalVersion(Context ctx) {
        int localVersion = 0;
        try {
            PackageInfo packageInfo = ctx.getApplicationContext()
                    .getPackageManager()
                    .getPackageInfo(ctx.getPackageName(), 0);
            localVersion = packageInfo.versionCode;
//            LogUtil.d("TAG", "本软件的版本号。。" + localVersion);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return localVersion;
    }

    /**
     * 获取本地软件版本号名称
     */
    public static String getLocalVersionName(Context ctx) {
        String localVersion = "";
        try {
            PackageInfo packageInfo = ctx.getApplicationContext()
                    .getPackageManager()
                    .getPackageInfo(ctx.getPackageName(), 0);
            localVersion = packageInfo.versionName;
//            LogUtil.d("TAG", "本软件的版本号。。" + localVersion);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return localVersion;
    }
}
