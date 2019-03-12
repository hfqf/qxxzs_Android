package com.points.autorepar.bean;

/**
 * Created by points on 16/11/25.
 */


import android.os.Parcel;
import android.os.Parcelable;


public class ContactOrderInfo implements Parcelable {

    public  String openid;
    public  String time;
    public  String state;
    public  String confirmtome;
    public  String owner;
    public  String inserttime;
    public  String info;
    public  String idfromserver;
    public  String carcode;
    public  String tel;
    public  String name;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(openid);
        out.writeString(time);
        out.writeString(state);
        out.writeString(confirmtome);
        out.writeString(owner);
        out.writeString(inserttime);
        out.writeString(info);
        out.writeString(idfromserver);

        out.writeString(carcode);
        out.writeString(tel);
        out.writeString(name);
    }

    public static final Creator<ContactOrderInfo> CREATOR = new Creator<ContactOrderInfo>() {
        public ContactOrderInfo createFromParcel(Parcel in) {
            return new ContactOrderInfo(in);
        }

        public ContactOrderInfo[] newArray(int size) {
            return new ContactOrderInfo[size];
        }
    };

    public ContactOrderInfo(Parcel in) {
        openid = in.readString();
        time = in.readString();
        state = in.readString();
        confirmtome = in.readString();
        owner = in.readString();
        inserttime = in.readString();
        info = in.readString();
        idfromserver = in.readString();
        carcode = in.readString();
        tel = in.readString();
        name = in.readString();
    }





    public ContactOrderInfo()
    {

    }

}
