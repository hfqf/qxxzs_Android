package com.points.autorepar.dbutil;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.points.autorepar.bean.Contact;
import com.points.autorepar.dbutil.bean.HFLocalDBColumnSettingModel;
import com.points.autorepar.dbutil.bean.HFLocalDBSettingModel;
import com.points.autorepar.dbutil.bean.HFLocalDBTableSettingModel;
import com.points.autorepar.utils.LoggerUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by points on 16/11/25.
 */
public class HFSmartDBService extends SQLiteOpenHelper {
    private static final String TAG = "HFSmartDBService";
    private static HFSmartDBService instance = null;
    private HFLocalDBSettingModel dbSettingModel;

    public static synchronized HFSmartDBService getInstance(Context context, HFLocalDBSettingModel lastestVersion) {
        if (instance == null) {
            synchronized (HFSmartDBService.class) {
                if (instance == null) {
                    instance = new HFSmartDBService(context, lastestVersion);
                }
            }
        }
        return instance;
    }

    public HFSmartDBService(Context context, HFLocalDBSettingModel lastestVersion) {
        super(context, lastestVersion.dbName, null, Integer.parseInt(lastestVersion.dbVersion));
        this.dbSettingModel = lastestVersion;
        getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        for (int i = 0; i < dbSettingModel.dbTables.size(); i++) {
            HFLocalDBColumnSettingModel tableModel = dbSettingModel.dbTables.get(i);
            String sql = "create table if not exists " + tableModel.tableName + "(";
            for (int j = 0; j < tableModel.columns.size(); j++) {
                HFLocalDBTableSettingModel columnModel = tableModel.columns.get(j);
                if (j == tableModel.columns.size() - 1) {
                    sql += columnModel.name+" " + columnModel.action + ");";
                } else {
                    sql += columnModel.name+" " + columnModel.action + ",";
                }
            }
            db.execSQL(sql);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        updateSQL(db);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {

        Log.e(TAG, "onOpen");
    }

    //升级数据库
    private void updateSQL(SQLiteDatabase db) {
        for (int i = 0; i < dbSettingModel.arrWillExecSQL.size(); i++) {
            String sql = dbSettingModel.arrWillExecSQL.get(i);
            db.execSQL(sql);
        }
    }

    public static void closeDB(SQLiteDatabase db) {
        if (db != null)
            db.close();
    }
}

