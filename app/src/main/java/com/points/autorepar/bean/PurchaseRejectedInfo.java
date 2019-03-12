package com.points.autorepar.bean;

/**
 * Created by points on 2017/4/24.
 */

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

public class PurchaseRejectedInfo  implements Parcelable {


    public  String good_headurl;
    public  String good_name;
    public  String good_barcode;
    public  String price;
    public  String num;
    public  String id;


    public  String saleprice;
    public  String minnum;
    public  String unit;

    public  String categoryid;
    public  String categorytoptypeid;
    public  String categorytoptypename;
    public  String remark;



    public PurchaseRejectedInfo( ) {

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(good_headurl);
        out.writeString(good_name);
        out.writeString(good_barcode);
        out.writeString(price);
        out.writeString(num);
        out.writeString(id);

        out.writeString(saleprice);
        out.writeString(minnum);
        out.writeString(unit);

        out.writeString(categoryid);
        out.writeString(categorytoptypeid);
        out.writeString(categorytoptypename);
        out.writeString(remark);




    }

    public static final Creator<PurchaseRejectedInfo> CREATOR = new Creator<PurchaseRejectedInfo>() {
        public PurchaseRejectedInfo createFromParcel(Parcel in) {
            return new PurchaseRejectedInfo(in);
        }

        public PurchaseRejectedInfo[] newArray(int size) {
            return new PurchaseRejectedInfo[size];
        }
    };

    public PurchaseRejectedInfo(Parcel in) {
        good_headurl = in.readString();
        good_name = in.readString();
        good_barcode = in.readString();
        price = in.readString();
        num = in.readString();
        id = in.readString();


        saleprice = in.readString();
        minnum = in.readString();
        unit = in.readString();

        categoryid = in.readString();
        categorytoptypeid = in.readString();
        categorytoptypename = in.readString();
        remark = in.readString();
    }


    public static PurchaseRejectedInfo fromWithJsonObj(JSONObject json) {
        PurchaseRejectedInfo item = new PurchaseRejectedInfo();

        item.num = json.optString("num");
        item.id = json.optString("_id");
        item.good_headurl = json.optString("picurl");
        item.good_name = json.optString("name");
        item.good_barcode = json.optString("barcode");
        item.price = json.optString("systemprice");

        item.saleprice = json.optString("saleprice");
        item.minnum = json.optString("minnum");
        item.unit = json.optString("unit");

        item.remark = json.optString("remark");

        JSONObject category= json.optJSONObject("category");

        if(category !=null) {
            item.categoryid = category.optString("_id");
            item.categorytoptypeid = category.optString("toptypeid");
            item.categorytoptypename = category.optString("name");
        }

        return item;
    }

}
