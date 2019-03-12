package com.points.autorepar.bean;

/**
 * Created by points on 2017/4/24.
 */

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RepairerInfo  {


    public  String username;
    public  String pushid;
    public  String repairer;
    public String roletype;
    public String state;
    public String tel;



    public RepairerInfo( ) {

    }



    public static RepairerInfo fromWithJsonObj(JSONObject json) {
        RepairerInfo item = new RepairerInfo();
        item.username = json.optString("username");
        item.repairer = json.optString("_id");
        item.pushid = json.optString("pushid");
        item.roletype = json.optString("roletype");
        item.state = json.optString("state");
        item.tel = json.optString("tel");

        return item;
    }

}
