package com.points.autorepar.sql;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.points.autorepar.utils.LoggerUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.content.ContentValues;
import com.points.autorepar.bean.*;
import com.points.autorepar.utils.PinyinUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by points on 16/11/25.
 */
public class DBService extends SQLiteOpenHelper {

    private static final String dbName  = "autore.db";
    private static final String TAG     = "DBService";
    private static final int  dbVersion = 9;//3.3

    private static DBService instance  = null;

    /**
     * 单例获取该DBService
     *
     * @param context
     * @return
     */
    public static synchronized DBService getInstance(Context context)
    {
        if (instance == null)
        {
            synchronized (DBService.class)
            {
//                DatabaseContext dbContext = new DatabaseContext(context,"points");
                if (instance == null){
                    instance = new DBService(context);
                }

            }
        }
        return instance;
    }


    public DBService(Context context)
    {
        super(context,dbName,null,dbVersion);
    }



    /**
     * Called when the database is created for the first time. This is where the
     * creation of tables and the initial population of the tables should happen.
     *
     * @param db The database.
     */
    @Override
    public void onCreate(SQLiteDatabase db)
    {

        /**
         * 由于该方法只是再最开始的安装时会执行,根据dbversion直接创建,再配合onUpdate函数，可确保所有情形的数据库升级流程
         */
//        if(dbVersion == 1){//1.0
//
//            List<String> arrTable  = new ArrayList<String>(Arrays.asList("create table if not exists  contact (carcode TEXT,name TEXT,tel TEXT,cartype TEXT)",
//                    "CREATE TABLE IF NOT EXISTS repairHistory (ID INTEGER PRIMARY KEY AUTOINCREMENT,carcode TEXT,totalkm TEXT,repairtime TEXT,repairtype TEXT,addition TEXT,tipcircle TEXT,isclosetip TEXT,circle TEXT,isreaded TEXT)"));
//
//            for (int i=0;i<arrTable.size();i++)
//            {
//                String sql = arrTable.get(i);
//                db.execSQL(sql);
//            }
//
//        }else if(dbVersion == 2)//当是从2.2直接开始的新用户
//        {
//            List<String> arrTable  = new ArrayList<String>(Arrays.asList("create table if not exists  contact (carcode TEXT,name TEXT,tel TEXT,cartype TEXT)",
//                    "CREATE TABLE IF NOT EXISTS repairHistory (ID INTEGER PRIMARY KEY AUTOINCREMENT,carcode TEXT,totalkm TEXT,repairtime TEXT,repairtype TEXT,addition TEXT,tipcircle TEXT,isclosetip TEXT,circle TEXT,isreaded TEXT)"));
//
//            for (int i=0;i<arrTable.size();i++)
//            {
//                String sql = arrTable.get(i);
//                db.execSQL(sql);
//            }
//
//
//            List<String> arrAlertTable = new ArrayList<String>(Arrays.asList(
//                    "alter table contact add column owner TEXT",
//                    "alter table contact add column idfromnode TEXT",
//                    "alter table repairHistory add column owner TEXT",
//                    "alter table repairHistory add column idfromnode TEXT",
//                    "alter table repairHistory add column inserttime TEXT"));
//            for (int i = 0; i < arrAlertTable.size(); i++) {
//                String sql = arrAlertTable.get(i);
//                Log.e(TAG,"updateSQL:"+sql);
//                db.execSQL(sql);
//            }
//
//        }else if(dbVersion == 3)//当是从3.2直接开始的新用户
//        {
//            List<String> arrTable  = new ArrayList<String>(Arrays.asList("create table if not exists  contact (carcode TEXT,name TEXT,tel TEXT,cartype TEXT,owner TEXT,idfromnode TEXT)"
//                    ));
//
//            for (int i=0;i<arrTable.size();i++)
//            {
//                String sql = arrTable.get(i);
//                db.execSQL(sql);
//            }
//
//
//            List<String> arrAlertTable = new ArrayList<String>(Arrays.asList(
//                    "alter table contact add column inserttime TEXT",
//                    "alter table contact add column isbindweixin TEXT",
//                    "alter table contact add column weixinopenid TEXT",
//                    "alter table contact add column vin TEXT",
//                    "alter table contact add column carregistertime TEXT",
//                    "alter table contact add column headurl TEXT"));
//            for (int i = 0; i < arrAlertTable.size(); i++) {
//                String sql = arrAlertTable.get(i);
//                Log.e(TAG,"updateSQL:"+sql);
//                db.execSQL(sql);
//            }
//
//        }else if(dbVersion == 5)//当是从3.3直接开始的新用户
//        {
            List<String> arrTable  = new ArrayList<String>(Arrays.asList("create table if not exists  contact (carcode TEXT,name TEXT,tel TEXT,cartype TEXT,owner TEXT,idfromnode TEXT," +
                    " inserttime TEXT,isbindweixin TEXT,weixinopenid TEXT,vin TEXT,carregistertime TEXT,headurl TEXT," +
                    "safecompany TEXT,safenexttime TEXT,yearchecknexttime TEXT,tqTime1 TEXT,tqTime2 TEXT,key TEXT,isVip TEXT,carId TEXT)"
            ));

            for (int i=0;i<arrTable.size();i++)
            {
                String sql = arrTable.get(i);
                db.execSQL(sql);
            }
//        }
    }


    /**
     * Called when the database needs to be upgraded. The implementation
     * should use this method to drop tables, add tables, or do anything else it
     * needs to upgrade to the new schema version.
     * @param db The database.
     * @param oldVersion The old database version.
     * @param newVersion The new database version.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db,int oldVersion,int newVersion) {
        // 升级数据库
         updateSQL(db,oldVersion,newVersion);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {

        Log.e(TAG,"onOpen");
    }

    /**
     * 升级数据库表和字段
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    private  void  updateSQL(SQLiteDatabase db,int oldVersion,int newVersion) {

//        //2.2
//        if(newVersion == 2) {
//            List<String> arrTable = new ArrayList<String>(Arrays.asList(
//                    "alter table contact add column owner TEXT",
//                    "alter table contact add column idfromnode TEXT",
//                    "alter table repairHistory add column owner TEXT",
//                    "alter table repairHistory add column idfromnode TEXT",
//                    "alter table repairHistory add column inserttime TEXT"));
//            for (int i = 0; i < arrTable.size(); i++) {
//                String sql = arrTable.get(i);
//                Log.e(TAG,"updateSQL:"+sql);
//                db.execSQL(sql);
//            }
//        }else if(dbVersion == 3)//当是从3.2直接开始的新用户
//        {
//
//            List<String> arrAlertTable = new ArrayList<String>(Arrays.asList(
//                    "alter table contact add column inserttime TEXT",
//                    "alter table contact add column isbindweixin TEXT",
//                    "alter table contact add column weixinopenid TEXT",
//                    "alter table contact add column vin TEXT",
//                    "alter table contact add column carregistertime TEXT",
//                    "alter table contact add column headurl TEXT"));
//            for (int i = 0; i < arrAlertTable.size(); i++) {
//                String sql = arrAlertTable.get(i);
//                Log.e(TAG,"updateSQL:"+sql);
//                db.execSQL(sql);
//            }
//
//        }else if(dbVersion == 4)//当是从3.4直接开始的新用户
//        {
//            List<String> arrAlertTable = new ArrayList<String>(Arrays.asList(
//                    "alter table contact add column safecompany TEXT",
//                    "alter table contact add column safenexttime TEXT",
//                    "alter table contact add column yearchecknexttime TEXT",
//                    "alter table contact add column tqTime1 TEXT",
//                    "alter table contact add column tqTime2 TEXT"));
//            for (int i = 0; i < arrAlertTable.size(); i++) {
//                String sql = arrAlertTable.get(i);
//                Log.e(TAG,"updateSQL:"+sql);
//                db.execSQL(sql);
//            }
//        }

        String delsql = "DROP table contact";
        db.execSQL(delsql);


        List<String> arrTable  = new ArrayList<String>(Arrays.asList("create table if not exists  contact (carcode TEXT,name TEXT,tel TEXT,cartype TEXT,owner TEXT,idfromnode TEXT," +
                " inserttime TEXT,isbindweixin TEXT,weixinopenid TEXT,vin TEXT,carregistertime TEXT,headurl TEXT," +
                "safecompany TEXT,safenexttime TEXT,yearchecknexttime TEXT,tqTime1 TEXT,tqTime2 TEXT,key TEXT,isVip TEXT,carId TEXT)"
        ));

        for (int i=0;i<arrTable.size();i++)
        {
            String sql = arrTable.get(i);
            db.execSQL(sql);
        }

    }

    public static void closeDB(SQLiteDatabase db)
    {
        if(db != null)
        db.close();
    }
    public static SQLiteDatabase getDB()
    {
        SQLiteDatabase db = instance.getWritableDatabase();
        return db;
    }

    ///添加一个客户
    public static void addNewContact(Contact contact,SQLiteDatabase db){


        ContentValues cv = new ContentValues();
        cv.put("carcode", contact.getCarCode().length() == 0? "" :contact.getCarCode() );

        cv.put("name",contact.getName().length() == 0? "" :contact.getName() );

        cv.put("cartype", contact.getCarType().length() == 0? "" :contact.getCarType() );

        cv.put("owner",contact.getOwner().length() == 0? "" :contact.getOwner() );

        cv.put("idfromnode", contact.getIdfromnode().length() == 0? "" :contact.getIdfromnode() );

        cv.put("isbindweixin", contact.getIsbindweixin().length() == 0? "0" :contact.getIsbindweixin() );

        cv.put("weixinopenid",contact.getWeixinopenid().length() == 0? "" :contact.getWeixinopenid() );

        cv.put("vin",contact.getVin().length() == 0? "" :contact.getVin());

        cv.put("carregistertime", contact.getCarregistertime().length() == 0? "" :contact.getCarregistertime());

        cv.put("headurl", contact.getHeadurl().length() == 0? "" :contact.getHeadurl());

        cv.put("inserttime", contact.getInserttime().length() == 0? "" :contact.getInserttime());

        cv.put("tel", contact.getTel().length() == 0? "" :contact.getTel());


        cv.put("safecompany", contact.getSafecompany().length() == 0? "" :contact.getSafecompany());
        cv.put("safenexttime", contact.getSafenexttime().length() == 0? "" :contact.getSafenexttime());
        cv.put("yearchecknexttime", contact.getYearchecknexttime().length() == 0? "" :contact.getYearchecknexttime());
        cv.put("tqTime1", contact.getTqTime1().length() == 0? "" :contact.getTqTime1());
        cv.put("tqTime2", contact.getTqTime2().length() == 0? "" :contact.getTqTime2());
        cv.put("key", contact.getCar_key() == null? "" :contact.getCar_key());
        cv.put("isVip", contact.getisVip() == null? "" :contact.getisVip());
        cv.put("carId", contact.getCar_id() == null? "" :contact.getCar_id());

        db.insert("contact",null,cv);

    }

    //删除客户
    public  static  boolean deleteContact(Contact contact){
        Log.e(TAG,"deleteContact:"+LoggerUtil.jsonFromObject(contact));
        SQLiteDatabase db = instance.getWritableDatabase();
        db.delete("contact", "tel=?", new String[]{contact.getTel()});
        db.close();
        return true;
    }

    /**
     * 删除当前设备的所有联系人
     */
    public  static  boolean deleteAllContact( ){
        Log.e(TAG,"deleteAllContact");
        SQLiteDatabase db = instance.getWritableDatabase();
        db.delete("contact", null,null);
        db.close();
        return true;
    }

    //更新客户
    public  static  void updateContact(Contact contact){
        Log.e(TAG,"updateContact:"+LoggerUtil.jsonFromObject(contact));
        SQLiteDatabase db = instance.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("carcode", contact.getCarCode().length() == 0? "" :contact.getCarCode() );

        cv.put("name",contact.getName().length() == 0? "" :contact.getName() );

        cv.put("cartype", contact.getCarType().length() == 0? "" :contact.getCarType() );

        cv.put("owner",contact.getOwner().length() == 0? "" :contact.getOwner() );

        cv.put("idfromnode", contact.getIdfromnode().length() == 0? "" :contact.getIdfromnode() );

        cv.put("isbindweixin", contact.getIsbindweixin().length() == 0? "0" :contact.getIsbindweixin() );

        cv.put("weixinopenid",contact.getWeixinopenid().length() == 0? "" :contact.getWeixinopenid() );

        cv.put("vin",contact.getVin().length() == 0? "" :contact.getVin());

        cv.put("carregistertime", contact.getCarregistertime().length() == 0? "" :contact.getCarregistertime());

        cv.put("headurl", contact.getHeadurl().length() == 0? "" :contact.getHeadurl());

        cv.put("tel", contact.getTel().length() == 0? "" :contact.getTel());


        cv.put("safecompany", contact.getSafecompany().length() == 0? "" :contact.getSafecompany());
        cv.put("safenexttime", contact.getSafenexttime().length() == 0? "" :contact.getSafenexttime());
        cv.put("yearchecknexttime", contact.getYearchecknexttime().length() == 0? "" :contact.getYearchecknexttime());
        cv.put("tqTime1", contact.getTqTime1().length() == 0? "" :contact.getTqTime1());
        cv.put("tqTime2", contact.getTqTime2().length() == 0? "" :contact.getTqTime2());
        cv.put("key", contact.getCar_key() == null? "" :contact.getCar_key());
        cv.put("isVip", contact.getisVip() == null? "" :contact.getisVip());
        cv.put("carId", contact.getCar_id() == null? "" :contact.getCar_id());
        db.update("contact", cv, "idfromnode=?", new String[]{contact.getIdfromnode()});
        db.close();
    }

    //查询单个用户客户
    public  static Contact queryContact(String para){
        Log.e(TAG,"queryContact:"+para);
        SQLiteDatabase db = instance.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM contact WHERE carcode = ? or tel = ? or name = ? or idfromnode = ? or weixinopenid = ?", new String[]{para, para,para,para,para});

        while (c.moveToNext()) {
            Contact con = DBService.getContact(c);
            if(c.isLast())
            {
               return con;
            }
        }
        c.close();
        db.close();
        return null;
    }

    //查询单个用户客户
    public  static ArrayList queryContactNameByCarcode(String carcode){
        Log.e(TAG,"queryAllContactName");
        SQLiteDatabase db = instance.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM contact WHERE carcode = ?", new String[]{carcode});

        ArrayList<Contact> arr = new ArrayList<>();
        while (c.moveToNext()) {
            Contact con = DBService.getContact(c);
            arr.add(con);
        }
        c.close();
        db.close();
        return arr.size() > 0 ? arr : new  ArrayList();
    }

    //查询单个用户客户
    public  static Contact queryContactCode(String carcode,String idFromNode){
//        Log.e(TAG,"queryContact:"+para);
        SQLiteDatabase db = instance.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM contact WHERE carcode = ? or idFromNode = ? ", new String[]{carcode, idFromNode});

        while (c.moveToNext()) {
            Contact con = DBService.getContact(c);
            if(c.isLast())
            {
                return con;
            }
        }
        c.close();
        db.close();
        return null;
    }

    //查询单个用户客户
    public  static ArrayList queryAllContact(){
        Log.e(TAG,"queryAllContact");
        SQLiteDatabase db = instance.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM contact", null);
        ArrayList<Contact> arr = new ArrayList<>();
        while (c.moveToNext()) {
            Contact con = DBService.getContact(c);
            arr.add(con);
        }

        Gson gson2 = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE).create();
        String obj2 = gson2.toJson(arr);
        Log.e(TAG,"queryAllContact 获取完毕:"+obj2);
        c.close();
        db.close();
        return arr.size() > 0 ? arr : new  ArrayList();
    }

    private static Contact getContact(Cursor c){
            Contact con = new Contact();
            String carCode = c.getString(c.getColumnIndex("carcode"));
            String name = c.getString(c.getColumnIndex("name"));
            String tel = c.getString(c.getColumnIndex("tel"));
            String carType = c.getString(c.getColumnIndex("cartype"));
            String owner = c.getString(c.getColumnIndex("owner"));
            String idFromNode = c.getString(c.getColumnIndex("idfromnode"));
            String inserttime = c.getString(c.getColumnIndex("inserttime"));
            String isbindweixin = c.getString(c.getColumnIndex("isbindweixin"));
            String weixinopenid = c.getString(c.getColumnIndex("weixinopenid"));
            String vin = c.getString(c.getColumnIndex("vin"));
            String carregistertime = c.getString(c.getColumnIndex("carregistertime"));
            String headurl = c.getString(c.getColumnIndex("headurl"));


            String safecompany = c.getString(c.getColumnIndex("safecompany"));
            String safenexttime = c.getString(c.getColumnIndex("safenexttime"));
            String yearchecknexttime = c.getString(c.getColumnIndex("yearchecknexttime"));
            String tqTime1 = c.getString(c.getColumnIndex("tqTime1"));
            String tqTime2 = c.getString(c.getColumnIndex("tqTime2"));
            String key = c.getString(c.getColumnIndex("key"));



        String isVip = c.getString(c.getColumnIndex("isVip"));
        String carId = c.getString(c.getColumnIndex("carId"));

            con.setCarCode(carCode);
            con.setName(name);
            con.setTel(tel);
            con.setCarType(carType);
            con.setOwner(owner);
            con.setIdfromnode(idFromNode);
            con.setInserttime(inserttime);
            con.setIsbindweixin(isbindweixin);
            con.setWeixinopenid(weixinopenid);
            con.setVin(vin);
            con.setCarregistertime(carregistertime);
            con.setHeadurl(headurl);

            con.setSafecompany(safecompany);
            con.setSafenexttime(safenexttime);
            con.setYearchecknexttime(yearchecknexttime);
            con.setTqTime1(tqTime1);
            con.setTqTime2(tqTime2);

        con.setCar_key(key);
        con.setIsVip(isVip);
        con.setCarId(carId);
            return con;
    }

    private static JSONObject getContact2(Cursor c) {
        JSONObject con = new JSONObject();
        String carCode = c.getString(c.getColumnIndex("carcode"));
        String name = c.getString(c.getColumnIndex("name"));
        String tel = c.getString(c.getColumnIndex("tel"));
        String idFromNode = c.getString(c.getColumnIndex("idfromnode"));
        String vin = c.getString(c.getColumnIndex("vin"));
        String headurl = c.getString(c.getColumnIndex("headurl"));
        String cartype = c.getString(c.getColumnIndex("cartype"));
        try {
            con.putOpt("carcode", carCode);
            con.putOpt("name", name);
            con.putOpt("pinyin", PinyinUtil.formatAlpha(name));
            con.putOpt("tel", tel);
            con.putOpt("cartype", cartype);
            con.putOpt("id", idFromNode);
            con.putOpt("_id", idFromNode);
            con.putOpt("vin", vin);
            con.putOpt("avatar", headurl);
            return con;

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return con;
    }
    //查询单个用户客户
    public  static ArrayList queryAllContactName(){
        Log.e(TAG,"queryAllContactName");
        SQLiteDatabase db = instance.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM contact", null);
        ArrayList<String> arr = new ArrayList<>();
        while (c.moveToNext()) {
            String name = c.getString(c.getColumnIndex("name"));
            arr.add(name);
        }
        c.close();
        db.close();
        return arr.size() > 0 ? arr : new  ArrayList();
    }

}

