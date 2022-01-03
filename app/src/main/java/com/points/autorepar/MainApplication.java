package com.points.autorepar;

import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.StrictMode;
import android.util.Log;

import SpeDBHelper.SpeSqlteHelper;
import cn.jpush.android.api.JPushInterface;


import com.alibaba.android.arouter.launcher.ARouter;
import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.points.autorepar.activity.MainTabbarActivity;
import com.points.autorepar.common.Consts;
import com.points.autorepar.dbutil.HFSmartDBUpdateManager;
import com.points.autorepar.sql.DBService;
import com.points.autorepar.utils.ExampleUtil;
import com.tencent.smtt.sdk.QbSdk;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;

/**
 * Created by points on 16/11/30.
 */
public class MainApplication extends Application {

    private static final String key_sp  = "points";


    private MainApplication m_this;
    public static MainTabbarActivity m_mainActivity;
    private  String TAG = "Application";
    private static Consts     consts;
    private DBService m_db;
    private String RepID;
    public  static int userType ; //1技工，3店长
    private static MainApplication mInstance;
    public static synchronized MainApplication getInstance() {
        if(mInstance == null)
        {
            mInstance = new MainApplication();
        }
        return mInstance;
    }
    @Override
    public void onCreate() {
        consts = new Consts();
        // 程序创建的时候执行
        Log.d(TAG, "onCreate");
        super.onCreate();
        m_this = this;
        HFSmartDBUpdateManager.getInstance(this);
        initAllSdks();
        SpeSqlteHelper.init();
        SDKInitializer.initialize(this);
        SDKInitializer.setCoordType(CoordType.BD09LL);
        JPushInterface.setDebugMode(consts.isDev()); 	// 设置开启日志,发布时请关闭日志
        JPushInterface.init(this);     		// 初始化 JPush

        initSql();
        registerMessageReceiver();
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();
        //友盟分享
        PlatformConfig.setWeixin("wxa42d0599cb05e642", "38dc4064cec86568247f68f6500a5e4a");
        PlatformConfig.setQQZone("1105896878", "KnZBOHak1zXW6ElA");
        UMShareAPI.get(this);

        QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {

            @Override
            public void onViewInitFinished(boolean arg0) {
                // TODO Auto-generated method stub
                //x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。
                Log.d("app", " onViewInitFinished is " + arg0);
            }

            @Override
            public void onCoreInitFinished() {
                // TODO Auto-generated method stub
            }
        };
        QbSdk.initX5Environment(getApplicationContext(),  cb);

    }
    @Override
    public void onTerminate() {
        // 程序终止的时候执行
        Log.d(TAG, "onTerminate");
        super.onTerminate();
    }
    @Override
    public void onLowMemory() {
        // 低内存的时候执行
        Log.d(TAG, "onLowMemory");
        super.onLowMemory();
    }
    @Override
    public void onTrimMemory(int level) {
        // 程序在内存清理的时候执行
        Log.d(TAG, "onTrimMemory");
        super.onTrimMemory(level);
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        Log.d(TAG, "onConfigurationChanged");
        super.onConfigurationChanged(newConfig);
    }


    public static Consts consts (){
        return consts;
    }

    public  static Consts consts(Context context) {
        Activity hander = (Activity)context;
        MainApplication mainApplication = (MainApplication) hander.getApplication();
        return mainApplication.consts();
    }


    private void initSql() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    m_db = DBService.getInstance(m_this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

        // 只有调用了DatabaseHelper的getWritableDatabase()方法或者getReadableDatabase()方法之后，才会创建或打开一个连接


//        SQLiteDatabase	| getReadableDatabase() | Create and/or open a database.
//        SQLiteDatabase	| getWritableDatabase() | Create and/or open a database that will be used for reading and writing.
    }




    /**
     * JPush
     * for receive customer msg from jpush server
     */
    private MessageReceiver mMessageReceiver;
    public static final String MESSAGE_RECEIVED_ACTION = "com.points.autorepair.MESSAGE_RECEIVED_ACTION";
    public static final String KEY_TITLE = "title";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_EXTRAS = "extras";

    public void registerMessageReceiver() {
        mMessageReceiver = new MessageReceiver();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction(MESSAGE_RECEIVED_ACTION);
        registerReceiver(mMessageReceiver, filter);
    }

    public class MessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (MESSAGE_RECEIVED_ACTION.equals(intent.getAction())) {
                String messge = intent.getStringExtra(KEY_MESSAGE);
                String extras = intent.getStringExtra(KEY_EXTRAS);
                StringBuilder showMsg = new StringBuilder();
                showMsg.append(KEY_MESSAGE + " : " + messge + "\n");
                if (!ExampleUtil.isEmpty(extras)) {
                    showMsg.append(KEY_EXTRAS + " : " + extras + "\n");
                }
                setCostomMsg(showMsg.toString());
            }
        }
    }

    private void setCostomMsg(String msg){
        if (null != msg) {
            Log.e(TAG,msg);
        }
    }


    public static void setEndDeta(Context m_context,String EndDate)
    {


        SharedPreferences sp = m_context.getSharedPreferences(key_sp, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("EndDate", EndDate);
        editor.commit();
    }



    public String getIsVip(Context m_context) {
        SharedPreferences sp = m_context.getSharedPreferences(key_sp, Context.MODE_PRIVATE);
        if (sp == null) {
            return "";
        }

        String pushID = sp.getString("IsVip","");
        return pushID;
    }

    public static void setIsVip(Context m_context,String IsVip)
    {


        SharedPreferences sp = m_context.getSharedPreferences(key_sp, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("IsVip", IsVip);
        editor.commit();
    }



    public String getEndDate(Context m_context) {
        SharedPreferences sp = m_context.getSharedPreferences(key_sp, Context.MODE_PRIVATE);
        if (sp == null) {
            return "";
        }

        String pushID = sp.getString("EndDate","");
        return pushID;
    }

    public static void setCreatePushID(Context m_context,String setCreatePushID)
    {
        SharedPreferences sp = m_context.getSharedPreferences(key_sp, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("CreatePushID", setCreatePushID);
        editor.commit();
    }



    public String getCreatePushID(Context m_context) {
        SharedPreferences sp = m_context.getSharedPreferences(key_sp, Context.MODE_PRIVATE);
        if (sp == null) {
            return "";
        }

        String pushID = sp.getString("CreatePushID","");
        return pushID;
    }




    public static void setUserType(Context m_context,int type)
    {


        SharedPreferences sp = m_context.getSharedPreferences(key_sp, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("userType", type);
        editor.commit();
    }



    public int getUserType(Context m_context) {
        SharedPreferences sp = m_context.getSharedPreferences(key_sp, Context.MODE_PRIVATE);
        if (sp == null) {
            return 2;
        }

        int num = sp.getInt("userType", 2);
        return num;
    }


    public String getSPValue(Context m_context,String key) {
        SharedPreferences sp = m_context.getSharedPreferences(key_sp, Context.MODE_PRIVATE);
        if (sp == null) {
            return "";
        }

        String str = sp.getString(key,"");
        return str;
    }


    public int getRoomType(Context m_context) {
        SharedPreferences sp = m_context.getSharedPreferences(key_sp, Context.MODE_PRIVATE);
        if (sp == null) {
            return 2;
        }

        int num = sp.getInt("RoomType", 2);
        return num;
    }


    public static void setRoomType(Context m_context,int type)
    {


        SharedPreferences sp = m_context.getSharedPreferences(key_sp, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("RoomType", type);
        editor.commit();
    }





    public String getRepID()
    {
        return RepID;
    }

    public void setRepID(String id)
    {
        this.RepID = id;
    }

    public static void setisneeddirectaddcartype(Context m_context,String type)
    {


        SharedPreferences sp = m_context.getSharedPreferences(key_sp, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("isneeddirectaddcartype", type);
        editor.commit();
    }



    public static String getisneeddirectaddcartype(Context m_context) {
        SharedPreferences sp = m_context.getSharedPreferences(key_sp, Context.MODE_PRIVATE);
        if (sp == null) {
            return "1";
        }

        String num = sp.getString("isneeddirectaddcartype","1");
        return num;
    }


    public static String getShopAD(Context m_context) {
        SharedPreferences sp = m_context.getSharedPreferences(key_sp, Context.MODE_PRIVATE);
        if (sp == null) {
            return "";
        }

        String pushID = sp.getString("ShopAD","");
        return pushID;
    }

    public static void setShopAD(Context m_context,String ShopAD)
    {

        SharedPreferences sp = m_context.getSharedPreferences(key_sp, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("ShopAD", ShopAD);
        editor.commit();
    }

    public static String getworkstarttime(Context m_context) {
        SharedPreferences sp = m_context.getSharedPreferences(key_sp, Context.MODE_PRIVATE);
        if (sp == null) {
            return "";
        }

        String pushID = sp.getString("workstarttime","");
        return pushID;
    }

    public static void setworkstarttime(Context m_context,String workstarttime)
    {


        SharedPreferences sp = m_context.getSharedPreferences(key_sp, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("workstarttime", workstarttime);
        editor.commit();
    }

    public static String getworkendtime(Context m_context) {
        SharedPreferences sp = m_context.getSharedPreferences(key_sp, Context.MODE_PRIVATE);
        if (sp == null) {
            return "";
        }

        String pushID = sp.getString("workendtime","");
        return pushID;
    }

    public static void setworkendtime(Context m_context,String workendtime)
    {


        SharedPreferences sp = m_context.getSharedPreferences(key_sp, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("workendtime", workendtime);
        editor.commit();
    }

    public static String getpics(Context m_context) {
        SharedPreferences sp = m_context.getSharedPreferences(key_sp, Context.MODE_PRIVATE);
        if (sp == null) {
            return "";
        }

        String pushID = sp.getString("pics","");
        return pushID;
    }

    public static void setpics(Context m_context,String pics)
    {


        SharedPreferences sp = m_context.getSharedPreferences(key_sp, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("pics", pics);
        editor.commit();
    }

    public static String getprovicename(Context m_context) {
        SharedPreferences sp = m_context.getSharedPreferences(key_sp, Context.MODE_PRIVATE);
        if (sp == null) {
            return "";
        }

        String pushID = sp.getString("provicename","");
        return pushID;
    }

    public static void setprovicename(Context m_context,String provicename)
    {


        SharedPreferences sp = m_context.getSharedPreferences(key_sp, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("provicename", provicename);
        editor.commit();
    }

    public static String getcityname(Context m_context) {
        SharedPreferences sp = m_context.getSharedPreferences(key_sp, Context.MODE_PRIVATE);
        if (sp == null) {
            return "";
        }

        String pushID = sp.getString("cityname","");
        return pushID;
    }

    public static void setcityname(Context m_context,String cityname)
    {


        SharedPreferences sp = m_context.getSharedPreferences(key_sp, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("cityname", cityname);
        editor.commit();
    }


    public static String getlocation(Context m_context) {
        SharedPreferences sp = m_context.getSharedPreferences(key_sp, Context.MODE_PRIVATE);
        if (sp == null) {
            return "";
        }

        String pushID = sp.getString("location","");
        return pushID;
    }

    public static void setlocation(Context m_context,String location)
    {


        SharedPreferences sp = m_context.getSharedPreferences(key_sp, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("location", location);
        editor.commit();
    }

    public static String getshopserviceID(Context m_context) {
        SharedPreferences sp = m_context.getSharedPreferences(key_sp, Context.MODE_PRIVATE);
        if (sp == null) {
            return "";
        }

        String pushID = sp.getString("shopserviceID","");
        return pushID;
    }

    public static void setshopserviceID(Context m_context,String shopserviceID)
    {


        SharedPreferences sp = m_context.getSharedPreferences(key_sp, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("shopserviceID", shopserviceID);
        editor.commit();
    }

    public static String getshopserviceName(Context m_context) {
        SharedPreferences sp = m_context.getSharedPreferences(key_sp, Context.MODE_PRIVATE);
        if (sp == null) {
            return "";
        }

        String pushID = sp.getString("shopserviceName","");
        return pushID;
    }

    public static void setshopserviceName(Context m_context,String shopserviceName)
    {
        SharedPreferences sp = m_context.getSharedPreferences(key_sp, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("shopserviceName", shopserviceName);
        editor.commit();
    }

    /**
     *初始化ARouter
     */
    private void initAllSdks(){
//        if(consts().isDev()){
            ARouter.openLog();     // 打印日志
            ARouter.openDebug();
//        }
        ARouter.init(this);
    }
}
