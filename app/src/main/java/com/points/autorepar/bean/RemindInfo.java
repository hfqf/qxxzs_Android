package com.points.autorepar.bean;

/**
 * Created by points on 2017/4/24.
 */

import org.json.JSONObject;

public class RemindInfo {


    public  String state;
    public  String confirmtime;
    public  String owner;

    public  String inserttime;
    public  String date;
    public  String _id;
    public  String openid;
    public  String time;
    public  String info;
    public  String carcode;
    public  String headurl;

    public  String starttime;
    public  String tel;
    public  String name;

    public  String carid;
    public  String carerid;

    public  String  shopid;
    public RemindInfo( ) {

    }



    public static RemindInfo fromWithJsonObj(JSONObject json) {
        RemindInfo item = new RemindInfo();
        item.state = json.optString("state");
        item._id = json.optString("_id");
        item.confirmtime = json.optString("confirmtime");

        item.owner = json.optString("owner");
        item.date = json.optString("date");
        item.openid = json.optString("openid");
        item.inserttime = json.optString("inserttime");
        item.time = json.optString("time");
        item.info = json.optString("info");

        item.starttime = json.optString("starttime");

        JSONObject car = json.optJSONObject("car");
        if(car !=null) {
            item.carcode = car.optString("carcode");
            item.carid = car.optString("_id");
        }else{
            item.carcode = "";
            item.carid ="";
        }

        JSONObject carer = json.optJSONObject("carer");
        if(carer !=null) {
            item.headurl = carer.optString("headurl");
            item.tel = carer.optString("tel");
            item.name = carer.optString("name");
            item.carerid = carer.optString("_id");
        }else{
            item.headurl ="";
            item.tel ="";
            item.name ="";
            item.carerid ="";
        }

        JSONObject shop = json.optJSONObject("shop");
        if(carer !=null) {
            item.shopid = shop.optString("_id");

        }else{
            item.shopid ="";

        }
        return item;
    }

}
