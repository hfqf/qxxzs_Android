package com.points.autorepar.bean;

/**
 * Created by points on 16/11/25.
 */


import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;


public class ADTServiceInfo implements Parcelable {

    public  String id;
    public  String name;
    public  String count;
    public ArrayList<ADTServiceItemInfo> arrSubTypes;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(id);
        out.writeString(name);
        out.writeString(count);
        out.writeList(arrSubTypes);
    }

    public static final Creator<ADTServiceInfo> CREATOR = new Creator<ADTServiceInfo>() {
        public ADTServiceInfo createFromParcel(Parcel in) {
            return new ADTServiceInfo(in);
        }
        public ADTServiceInfo[] newArray(int size) {
            return new ADTServiceInfo[size];
        }
    };

    public ADTServiceInfo(Parcel in) {
        id = in.readString();
        name = in.readString();
        count = in.readString();
        arrSubTypes = in.readArrayList(ADTServiceItemInfo.class.getClassLoader());
    }

    public ADTServiceInfo()
    {

    }

}
