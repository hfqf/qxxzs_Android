package com.points.autorepar.bean;

/**
 * Created by points on 16/11/25.
 */


import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;


public class ServiceManageSubInfo implements Parcelable {

    public  String id;
    public  String name;
    public  String toptypeid;
    public  String isautocreated;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(id);
        out.writeString(name);
        out.writeString(toptypeid);
        out.writeString(isautocreated);
    }

    public static final Creator<ServiceManageSubInfo> CREATOR = new Creator<ServiceManageSubInfo>() {
        public ServiceManageSubInfo createFromParcel(Parcel in) {
            return new ServiceManageSubInfo(in);
        }
        public ServiceManageSubInfo[] newArray(int size) {
            return new ServiceManageSubInfo[size];
        }
    };

    public ServiceManageSubInfo(Parcel in) {
        id = in.readString();
        name = in.readString();
        toptypeid = in.readString();
        toptypeid = in.readString();
    }

    public ServiceManageSubInfo()
    {

    }

}
