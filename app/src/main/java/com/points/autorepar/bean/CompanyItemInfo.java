package com.points.autorepar.bean;

/**
 * Created by points on 2017/4/24.
 */

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CompanyItemInfo implements Parcelable {


    public  String CompanyId;
    public  String address;
    public  String managername;
    public  String owner;
    public  String remark;
    public  String suppliercompanyname;
    public String tel;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(CompanyId);
        out.writeString(address);
        out.writeString(managername);
        out.writeString(owner);
        out.writeString(remark);
        out.writeString(suppliercompanyname);
        out.writeString(tel);
    }

    public static final Creator<CompanyItemInfo> CREATOR = new Creator<CompanyItemInfo>() {
        public CompanyItemInfo createFromParcel(Parcel in) {
            return new CompanyItemInfo(in);
        }

        public CompanyItemInfo[] newArray(int size) {
            return new CompanyItemInfo[size];
        }
    };

    public CompanyItemInfo(Parcel in) {
        CompanyId = in.readString();
        address = in.readString();
        managername = in.readString();
        owner = in.readString();
        remark = in.readString();
        suppliercompanyname = in.readString();
        tel= in.readString();
    }

    public CompanyItemInfo( ) {

    }



    public static CompanyItemInfo fromWithJsonObj(JSONObject json) {
        CompanyItemInfo item = new CompanyItemInfo();
        item.CompanyId = json.optString("_id");
        item.address = json.optString("address");

        item.managername = json.optString("managername");

        item.owner = json.optString("owner");

        item.remark = json.optString("remark");
        item.suppliercompanyname = json.optString("suppliercompanyname");
        item.tel =  json.optString("tel");

        return item;
    }

}
