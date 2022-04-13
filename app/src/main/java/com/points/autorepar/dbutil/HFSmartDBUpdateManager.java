package com.points.autorepar.dbutil;

import android.content.Context;
import android.os.Environment;

import com.google.gson.Gson;
import com.points.autorepar.dbutil.bean.HFLocalDBColumnSettingModel;
import com.points.autorepar.dbutil.bean.HFLocalDBSettingModel;

import java.io.File;

/**
 * 数据库升级管理类
 */
public class HFSmartDBUpdateManager {
    private final  String KEY_JsonName = "dbupdate.json";
    private final  String Path_JsonName = Environment.getExternalStorageDirectory() + "/HFSmartDBManager/json/"+KEY_JsonName;
    private Context context;


    //手机中保存的配置
    private HFLocalDBSettingModel appLocalDBSettingModel;
    //app中最新配置
    private HFLocalDBSettingModel appAssetsDBSettingModel;

    private static class SingletonClassInstance {
        private static final HFSmartDBUpdateManager instance = new HFSmartDBUpdateManager();
    }

    private HFSmartDBUpdateManager() {
        super();
    }


    public void setContext(Context context) {
        this.context = context;
        checkUpdate(context);
    }

    public static HFSmartDBUpdateManager getInstance(Context context) {
        HFSmartDBUpdateManager _instance =  SingletonClassInstance.instance;
        _instance.setContext(context);
        return _instance;
    }

    /**
     * 根据工程内的json文件和制定目录下的json文件进行对比，确认是否需要升级字段
     * 要求如下：
     * 1.多余的字段不直接删
     * 2.新增的字段排在最后
     * @return
     */
    private boolean checkUpdate(Context context){
        appAssetsDBSettingModel = getAppAssetsDBSetting();
        appLocalDBSettingModel = getAppLoclDBSetting();
        //本地无配置肯定需要更新数据库
        if(appLocalDBSettingModel==null){
            //升级数据库
            HFSmartDBService.getInstance(context,appAssetsDBSettingModel);
            //最新配置保存至本地
            saveDBSetting2Local();
            String ret = JsonUtil.readTextFile(Path_JsonName);
            if(ret!=null){

            }else{

            }
        }else{

        }
        return true;
    }

    private HFLocalDBSettingModel getAppAssetsDBSetting(){
        String json = JsonUtil.getJson(KEY_JsonName,context);
        Gson gson = new Gson();
        HFLocalDBSettingModel model = gson.fromJson(json,HFLocalDBSettingModel.class);
        return model;
    }

    /**
     * 查询手机中下数据库配置文件
     * @return HFLocalDBSettingModel
     */
    private HFLocalDBSettingModel getAppLoclDBSetting(){
        return null;
    }

    private void saveDBSetting2Local(){
        String json = JsonUtil.getJson(KEY_JsonName,context);
        JsonUtil.saveToSDCard(context,KEY_JsonName,json);
    }

}
