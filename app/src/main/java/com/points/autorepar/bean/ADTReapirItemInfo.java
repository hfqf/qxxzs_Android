package com.points.autorepar.bean;

/**
 * Created by points on 2017/4/24.
 */

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ADTReapirItemInfo implements Parcelable {


    public  String repId;
    public  String contactId;
    public  String price;
    public  String num;
    public  String type;
    public  String idfromnode;
    public  int    currentPrice;
    public String itemtype;
    public String repairer;
    public String state;

    public String workhourpay;
    public String service;
    public String goodsId;
    public String serviceId;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(repId);
        out.writeString(contactId);
        out.writeString(price);
        out.writeString(num);
        out.writeString(type);
        out.writeString(idfromnode);
        out.writeInt(currentPrice);
        out.writeString(itemtype);
        out.writeString(repairer);
        out.writeString(state);
        out.writeString(workhourpay);
        out.writeString(service);
        out.writeString(goodsId);
        out.writeString(serviceId);
    }

    public static final Parcelable.Creator<ADTReapirItemInfo> CREATOR = new Parcelable.Creator<ADTReapirItemInfo>() {
        public ADTReapirItemInfo createFromParcel(Parcel in) {
            return new ADTReapirItemInfo(in);
        }

        public ADTReapirItemInfo[] newArray(int size) {
            return new ADTReapirItemInfo[size];
        }
    };

    public ADTReapirItemInfo(Parcel in) {
        repId = in.readString();
        contactId = in.readString();
        price = in.readString();
        num = in.readString();
        type = in.readString();
        idfromnode = in.readString();
        currentPrice = in.readInt();
        itemtype= in.readString();
        repairer = in.readString();
        state = in.readString();
        workhourpay =in.readString();
        service = in.readString();
        goodsId = in.readString();
        serviceId = in.readString();
    }

    public ADTReapirItemInfo( ) {

    }



    public static   ADTReapirItemInfo fromWithJsonObj(JSONObject json) {
        ADTReapirItemInfo item = new ADTReapirItemInfo();
        item.repId = json.optString("repid");
        item.contactId = json.optString("contactid");

        item.price = json.optString("price");
        if("".equalsIgnoreCase(item.price))
        {
            item.price ="0";
        }
        item.num = json.optString("num");
        if("".equalsIgnoreCase(item.num))
        {
            item.num ="0";
        }
        item.type = json.optString("type");
        item.idfromnode = json.optString("_id");
        item.itemtype =  json.optString("itemtype");
        boolean isNumber = true;
        item.repairer = json.optString("repairer");
        item.state = json.optString("state");
        item.workhourpay = json.optString("workhourpay");
        if("".equalsIgnoreCase(item.workhourpay))
        {
            item.workhourpay ="0";
        }
        item.service = json.optString("service");
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(item.price);
        if( !isNum.matches() ){
            isNumber = false;
        }
        if(isNumber){
            item.currentPrice = (int)((Float.parseFloat(item.price)+Float.parseFloat(item.workhourpay))*Float.parseFloat(item.num));
        }else {
            item.currentPrice = 0;
        }

        JSONObject goods = json.optJSONObject("goods");
        if(goods == null){
            item.goodsId = json.optString("goods");
        }else{
            item.goodsId = goods.optString("_id");
        }
        item.service = json.optString("service");
        return item;
    }

}
