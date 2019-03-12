package com.points.autorepar.bean;

/**
 * Created by points on 2017/4/24.
 */

import org.json.JSONObject;

public class InOutInfo {


    public  String good_headurl;
    public  String good_name;
    public  String good_barcode;
    public  String type;
    public  String num;
    public  String time;
    public  String dealer_username;
    public  String id;






    public InOutInfo( ) {

    }



    public static InOutInfo fromWithJsonObj(JSONObject json) {
        InOutInfo item = new InOutInfo();
        item.type = json.optString("type");
        item.num = json.optString("num");

        item.time = json.optString("time");
        item.id = json.optString("_id");

        JSONObject good= json.optJSONObject("goods");
        if(good!=null) {
            item.good_headurl = good.optString("picurl");
            item.good_name = good.optString("name");
            item.good_barcode = good.optString("barcode");
        }else{
            item.good_headurl = "";
            item.good_name = "";
            item.good_barcode = "";
        }

        JSONObject dealer= json.optJSONObject("dealer");
        if(dealer !=null) {
            item.dealer_username = dealer.optString("username");
        }else{
            item.dealer_username ="";
        }

        return item;
    }

}
