package com.points.autorepar.bean;

/**
 * Created by points on 16/11/25.
 */


import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;


public class ServiceManageInfo implements Parcelable {

    public  String id;
    public  String name;
    public  String owner;
    public  String iscommon;
    public  String isautocreated;
    public ArrayList<ServiceManageSubInfo> subtypelist;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(id);
        out.writeString(name);
        out.writeString(owner);
        out.writeString(iscommon);
        out.writeString(isautocreated);
        out.writeList(subtypelist);

    }

    public static final Creator<ServiceManageInfo> CREATOR = new Creator<ServiceManageInfo>() {
        public ServiceManageInfo createFromParcel(Parcel in) {
            return new ServiceManageInfo(in);
        }
        public ServiceManageInfo[] newArray(int size) {
            return new ServiceManageInfo[size];
        }
    };

    public ServiceManageInfo(Parcel in) {
        id = in.readString();
        name = in.readString();
        owner = in.readString();
        iscommon = in.readString();
        isautocreated = in.readString();
        subtypelist = in.readArrayList(ServiceManageSubInfo.class.getClassLoader());
    }

    public ServiceManageInfo()
    {

    }

}
