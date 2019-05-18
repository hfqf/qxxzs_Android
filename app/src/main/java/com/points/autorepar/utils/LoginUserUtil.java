package com.points.autorepar.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import com.points.autorepar.MainApplication;
import com.points.autorepar.R;

/**
 * Created by points on 2017/2/8.
 */

public class LoginUserUtil {

    private static final String key_sp  = "points";
    /**
     * 返回当前登录的账号
     * @return
     */
    public static String getName(Context context){
        String key = context.getResources().getString(R.string.key_loginer_name);
        SharedPreferences sp = context.getSharedPreferences(key_sp, Context.MODE_PRIVATE);
        String ret = sp.getString(key, null);
        if(ret == null)
        {
            return  "汽修小助手";
        }
        return ret.length() == 0 ? "" : ret;
    }

    /**
     * 获取当前用户的手机号码
     * @return
     */
    public static String getTel(Context context){
        String key = context.getResources().getString(R.string.key_loginer_tel);
        SharedPreferences sp = context.getSharedPreferences(key_sp, Context.MODE_PRIVATE);
        String ret = sp.getString(key, null);
        if(ret == null)
        {
            return  "";
        }
        return ret.length() == 0 ? "" : ret;
    }

    /**
     * 获取当前用户的头像
     * @return
     */
    public static String gethHeadUrl(Context context){

        String key = context.getResources().getString(R.string.key_loginer_headurl);
        SharedPreferences sp = context.getSharedPreferences(key_sp, Context.MODE_PRIVATE);
        String ret = sp.getString(key, null);
        if(ret == null)
        {
            return  "";
        }
        Activity hander = (Activity)context;
        MainApplication mainApplication = (MainApplication) hander.getApplication();

        String _headurl = ret.length() > 0 ? (mainApplication.consts().BOS_SERVER+ret):ret;
        if(_headurl.contains(".png")){
            return _headurl;
        }else {
            return _headurl+".png";
        }
    }

    /**
     * 获取当前用户的密码
     * @return
     */
    public static String getPwd(Context context){
        String key = context.getResources().getString(R.string.key_loginer_pwd);
        SharedPreferences sp = context.getSharedPreferences(key_sp, Context.MODE_PRIVATE);
        String ret = sp.getString(key, null);
        if(ret == null)
        {
            return  "";
        }
        return ret.length() == 0 ? "" : ret;
    }

    /**
     * 获取当前用户的vip等级,暂时是0
     * @return
     */
    public static int getVipLevel(Context context){
        String key = context.getResources().getString(R.string.key_loginer_level);
        SharedPreferences sp = context.getSharedPreferences(key_sp, Context.MODE_PRIVATE);
        String ret = sp.getString(key, null);
        if(ret == null)
        {
            return  0;
        }
        return ret.length() == 0 ? 0 : Integer.valueOf(ret);
    }

    /**
     * 获取当前用户的门店地址
     * @return
     */
    public static String getAddress(Context context){
        String key = context.getResources().getString(R.string.key_loginer_address);
        SharedPreferences sp = context.getSharedPreferences(key_sp, Context.MODE_PRIVATE);
        String ret = sp.getString(key, null);
        if(ret == null || ret.length() == 0)
        {
            return  "暂无";
        }
        return ret;
    }


    /**
     * 获取当前用户的门店名称
     * @return
     */
    public static String getShopName(Context context){
        String key = context.getResources().getString(R.string.key_loginer_shopname);
        SharedPreferences sp = context.getSharedPreferences(key_sp, Context.MODE_PRIVATE);
        String ret = sp.getString(key, null);
        if(ret == null || ret.length() == 0)
        {
            return  "暂无";
        }
        return ret;
    }

    public static String getShopAd(Context context){
        String key = context.getResources().getString(R.string.key_loginer_shopname);
        SharedPreferences sp = context.getSharedPreferences(key_sp, Context.MODE_PRIVATE);
        String ret = sp.getString(key, null);
        if(ret == null || ret.length() == 0)
        {
            return  "暂无";
        }
        return ret;
    }

    public static String getShopTime(Context context){
        String key = context.getResources().getString(R.string.key_loginer_shopname);
        SharedPreferences sp = context.getSharedPreferences(key_sp, Context.MODE_PRIVATE);
        String ret = sp.getString(key, null);
        if(ret == null || ret.length() == 0)
        {
            return  "暂无";
        }
        return ret;
    }

    /**
     * 查看绑定的设备是否变化,如果变化肯定要上传本地数据,并下载所有之前数据
     * @return
     */
    public static Boolean isDeviceModifyed(Context context){
        String key = context.getResources().getString(R.string.key_loginer_device_modifed);
        SharedPreferences sp = context.getSharedPreferences(key_sp, Context.MODE_PRIVATE);
        String ret = sp.getString(key, null);
        if(ret == null)
        {
            return  false;
        }
        return ret.length() == 0 ? false : ret.equals("1") ;
    }

    /**
     * 判断当前用户登录是否需要同步联系人
     * @param context
     * @return
     */
    public static Boolean isContactAsynced(Context context){
        String key = context.getResources().getString(R.string.KEY_loginer_IS_CONTACT_AYSNED);
        SharedPreferences sp = context.getSharedPreferences(key_sp, Context.MODE_PRIVATE);
        String ret = sp.getString(key, null);
        if(ret == null)
        {
            return  false;
        }
        if(ret.length() == 0){
            return false;
        }else {
            if(ret.equals("1")){
                return true;
            }else {
                return  false;
            }
        }
    }


    /**
     * 判断当前用户登录是否需要同步维修记录
     * @param context
     * @return
     */
    public static Boolean isRepairAsynced(Context context){
        String key = context.getResources().getString(R.string.KEY_loginer_IS_REPAIR_AYSNED);
        SharedPreferences sp = context.getSharedPreferences(key_sp, Context.MODE_PRIVATE);
        String ret = sp.getString(key, null);
        if(ret == null)
        {
            return  false;
        }
        if(ret.length() == 0){
            return false;
        }else {
            if(ret.equals("1")){
                return true;
            }else {
                return  false;
            }
        }
    }

    /**
     * 当前设备是否是第一次使用
     * @param context
     * @return
     */
    public static  Boolean isFirstLogined(Context context){
        String key = context.getResources().getString(R.string.KEY_loginer_IS_FIRST_LOGIN);
        SharedPreferences sp = context.getSharedPreferences(key_sp, Context.MODE_PRIVATE);
        String ret = sp.getString(key, null);
        if(ret == null)
        {
            return  true;
        }
        return ret.length() == 0 ? true : ret.equals("1");
    }


    /**
     * 查询当天总收入
     * @param context
     * @return
     */
    public static  String  getTodayTotalInput(Context context){
        String key = context.getResources().getString(R.string.key_loginer_totalprice);
        SharedPreferences sp = context.getSharedPreferences(key_sp, Context.MODE_PRIVATE);
        String ret = sp.getString(key, null);
        if(ret == null)
        {
            return  "今天收入:0元";
        }
        return ret.length() == 0 ? "今天收入:0元" : "今天收入:"+ret+"元";
    }

    /**
     * 查询当天维修次数
     * @param context
     * @return
     */
    public static  String  getTodayTotalNum(Context context){
        String key = context.getResources().getString(R.string.key_loginer_totalnum);
        SharedPreferences sp = context.getSharedPreferences(key_sp, Context.MODE_PRIVATE);
        String ret = sp.getString(key, null);
        if(ret == null)
        {
            return  "今天维修:0次";
        }
        return ret.length() == 0 ? "今天维修:0次" :"今天维修:"+ret+"次";
    }

    /**
     * 获取登录用户的id
     * @param context
     * @return
     */
    public static  String  getUserId(Context context){
        String key = context.getResources().getString(R.string.key_loginer_id);
        SharedPreferences sp = context.getSharedPreferences(key_sp, Context.MODE_PRIVATE);
        String ret = sp.getString(key, null);
        if(ret == null)
        {
            return  "";
        }
        return ret;
    }

    public static  Boolean  isEmployeeLogined(Context context){
        String key = context.getResources().getString(R.string.key_loginer_isCreater);
        SharedPreferences sp = context.getSharedPreferences(key_sp, Context.MODE_PRIVATE);
        String ret = sp.getString(key, null);
        if(ret == null)
        {
            return  false;
        }
        return ret.length() == 0 ? false : ret.equals("0");
    }


    /**
     * 用户角色 0门店主 1技师 2仓库员 3店长
     * @param context
     * @return
     */
    public static  int  roleType(Context context){
        String key = context.getResources().getString(R.string.key_loginer_roletype);
        SharedPreferences sp = context.getSharedPreferences(key_sp, Context.MODE_PRIVATE);
        String ret = sp.getString(key, null);
        if(ret == null)
        {
            return  0;
        }
        return ret.length() == 0 ? 0 : Integer.valueOf(ret);
    }


    public static  Boolean  isCanAddNewRepair(Context context){
        if(!isEmployeeLogined(context)){
            return true;
        }else {
            int role = roleType(context);
            if(role == 0 || role == 3){
                return true;
            }else if(role == 2) {
                return false;
            }else {
                String key = context.getResources().getString(R.string.key_loginer_iscankaidan);
                SharedPreferences sp = context.getSharedPreferences(key_sp, Context.MODE_PRIVATE);
                String ret = sp.getString(key, null);
                if(ret == null)
                {
                    return  false;
                }
                return ret.length() == 0 ? false : ret.equals("1");
            }
        }
    }

    public static  Boolean  isCanAddContact(Context context){
        if(!isEmployeeLogined(context)){
            return true;
        }else {
            int role = roleType(context);
            if(role == 0 || role == 3){
                return true;
            }else if(role == 2) {
                return false;
            }else {
                String key = context.getResources().getString(R.string.key_loginer_iscanaddnewcontact);
                SharedPreferences sp = context.getSharedPreferences(key_sp, Context.MODE_PRIVATE);
                String ret = sp.getString(key, null);
                if(ret == null)
                {
                    return  false;
                }
                return ret.length() == 0 ? false : ret.equals("1");
            }
        }
    }


    public static  Boolean  isCanEditContact(Context context){
        if(!isEmployeeLogined(context)){
            return true;
        }else {
            int role = roleType(context);
            if(role == 0 || role == 3){
                return true;
            }else if(role == 2) {
                return false;
            }else {
                String key = context.getResources().getString(R.string.key_loginer_iscaneditcontact);
                SharedPreferences sp = context.getSharedPreferences(key_sp, Context.MODE_PRIVATE);
                String ret = sp.getString(key, null);
                if(ret == null)
                {
                    return  false;
                }
                return ret.length() == 0 ? false : ret.equals("1");
            }
        }
    }

    public static  Boolean  isCanDelContact(Context context){
        if(!isEmployeeLogined(context)){
            return true;
        }else {
            int role = roleType(context);
            if(role == 0 || role == 3){
                return true;
            }else if(role == 2) {
                return false;
            }else {
                String key = context.getResources().getString(R.string.key_loginer_iscandelcontact);
                SharedPreferences sp = context.getSharedPreferences(key_sp, Context.MODE_PRIVATE);
                String ret = sp.getString(key, null);
                if(ret == null)
                {
                    return  false;
                }
                return ret.length() == 0 ? false : ret.equals("1");
            }
        }
    }

    /**
     * 是否可以查看到客户的所有维修记录
     * @param context
     * @return
     */
    public static  Boolean  isCanSeeRepairs(Context context){
        if(!isEmployeeLogined(context)){
            return true;
        }else {
            int role = roleType(context);
            if(role == 0 || role == 3){
                return true;
            }else if(role == 2) {
                return false;
            }else {
                String key = context.getResources().getString(R.string.key_loginer_iscanseecontactrepairs);
                SharedPreferences sp = context.getSharedPreferences(key_sp, Context.MODE_PRIVATE);
                String ret = sp.getString(key, null);
                if(ret == null)
                {
                    return  false;
                }
                return ret.length() == 0 ? false : ret.equals("1");
            }
        }
    }


    public static String getEmployeeTel(Context context){
        String key = context.getResources().getString(R.string.key_employee_tel);
        SharedPreferences sp = context.getSharedPreferences(key_sp, Context.MODE_PRIVATE);
        String ret = sp.getString(key, null);
        if(ret == null)
        {
            return  "";
        }
        return ret.length() == 0 ? "" : ret;
    }

    public static String getEmployeePwd(Context context){
        String key = context.getResources().getString(R.string.key_employee_pwd);
        SharedPreferences sp = context.getSharedPreferences(key_sp, Context.MODE_PRIVATE);
        String ret = sp.getString(key, null);
        if(ret == null)
        {
            return  "";
        }
        return ret.length() == 0 ? "" : ret;
    }

    public static boolean isNumeric(String str) {
        for (int i = str.length(); --i >= 0; ) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}
