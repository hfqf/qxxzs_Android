package com.points.autorepar.bean;

/**
 * Created by points on 2017/4/24.
 */

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GoodsItemInfo implements Parcelable {


    public  String picurl;
    public  String name;
    public  String subtype;
    public  String saleprice;
    public  String productertype;
    public  String num;
    public  String selectnum;
    public String id;
    public String barcode;
    public String workhourpay;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(picurl);
        out.writeString(name);
        out.writeString(subtype);
        out.writeString(saleprice);
        out.writeString(productertype);
        out.writeString(num);
        out.writeString(selectnum);
        out.writeString(id);
        out.writeString(barcode);
        out.writeString(workhourpay);
    }

    public static final Creator<GoodsItemInfo> CREATOR = new Creator<GoodsItemInfo>() {
        public GoodsItemInfo createFromParcel(Parcel in) {
            return new GoodsItemInfo(in);
        }

        public GoodsItemInfo[] newArray(int size) {
            return new GoodsItemInfo[size];
        }
    };

    public GoodsItemInfo(Parcel in) {
        picurl = in.readString();
        name = in.readString();
        subtype = in.readString();
        saleprice = in.readString();
        productertype = in.readString();
        num = in.readString();
        selectnum = in.readString();
        id = in.readString();
        barcode= in.readString();
        workhourpay = in.readString();
    }

    public GoodsItemInfo( ) {

    }



    public static GoodsItemInfo fromWithJsonObj(JSONObject json) {
        GoodsItemInfo item = new GoodsItemInfo();
        item.picurl = json.optString("picurl");
        item.name = json.optString("name");
        item.subtype = json.optString("subtype");
        item.saleprice = json.optString("saleprice");
        item.productertype = json.optString("productertype");
        item.num = json.optString("num");
        item.selectnum = "0";
        item.id =  json.optString("_id");;
        item.barcode = json.optString("barcode");
        item.workhourpay = json.optString("workhourpay");
        return item;
    }

}
