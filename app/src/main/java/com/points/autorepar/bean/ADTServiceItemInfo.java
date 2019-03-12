package com.points.autorepar.bean;

/**
 * Created by points on 16/11/25.
 */


import android.os.Parcel;
import android.os.Parcelable;


public class ADTServiceItemInfo implements Parcelable {

    public  String id;
    public  String name;
    public  String price;
    public  String topTypeId;
    public  String workHourPay;
    public  String num;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(id);
        out.writeString(name);
        out.writeString(price);
        out.writeString(topTypeId);
        out.writeString(workHourPay);
        out.writeString(num);
    }

    public static final Creator<ADTServiceItemInfo> CREATOR = new Creator<ADTServiceItemInfo>() {
        public ADTServiceItemInfo createFromParcel(Parcel in) {
            return new ADTServiceItemInfo(in);
        }

        public ADTServiceItemInfo[] newArray(int size) {
            return new ADTServiceItemInfo[size];
        }
    };

    public ADTServiceItemInfo(Parcel in) {
        id = in.readString();
        name = in.readString();
        price = in.readString();
        topTypeId = in.readString();
        workHourPay = in.readString();
        num = in.readString();

    }





    public ADTServiceItemInfo()
    {

    }

}
