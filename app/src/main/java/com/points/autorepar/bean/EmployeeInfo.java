package com.points.autorepar.bean;

/**
 * Created by points on 16/11/25.
 */


import android.os.Parcel;
import android.os.Parcelable;


public class EmployeeInfo implements Parcelable {




    public  String username;
    public  String pwd;
    public  String headurl;
    public  String sex;
    public  String age;
    public  String desc;
    public  String tel;
    public  String registertime;

    public  String roletype;
    public  String creatertel;
    public  String state;
    public  String basepay;
    public  String workhourpay;

    public  String meritpay;
    public  String id;
    public  String iscanaddnewcontact;
    public  String iscandelcontact;
    public  String iscaneditcontact;

    public  String iscankaidan;
    public  String iscanseecontactrepairs;
    public  String isneeddispatch;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(username);
        out.writeString(pwd);
        out.writeString(headurl);
        out.writeString(sex);
        out.writeString(age);
        out.writeString(desc);
        out.writeString(tel);
        out.writeString(registertime);

        out.writeString(roletype);
        out.writeString(creatertel);
        out.writeString(state);
        out.writeString(basepay);
        out.writeString(workhourpay);

        out.writeString(meritpay);
        out.writeString(id);
        out.writeString(iscanaddnewcontact);
        out.writeString(iscandelcontact);
        out.writeString(iscaneditcontact);

        out.writeString(iscankaidan);
        out.writeString(iscanseecontactrepairs);
        out.writeString(isneeddispatch);


    }

    public static final Creator<EmployeeInfo> CREATOR = new Creator<EmployeeInfo>() {
        public EmployeeInfo createFromParcel(Parcel in) {
            return new EmployeeInfo(in);
        }

        public EmployeeInfo[] newArray(int size) {
            return new EmployeeInfo[size];
        }
    };

    public EmployeeInfo(Parcel in) {
        username = in.readString();
        pwd = in.readString();
        headurl = in.readString();
        sex = in.readString();
        age = in.readString();
        desc = in.readString();
        tel = in.readString();
        registertime = in.readString();

        roletype = in.readString();
        creatertel = in.readString();
        state = in.readString();
        basepay = in.readString();
        workhourpay = in.readString();

        meritpay = in.readString();
        id = in.readString();
        iscanaddnewcontact = in.readString();
        iscandelcontact = in.readString();
        iscaneditcontact = in.readString();

        iscankaidan = in.readString();
        iscanseecontactrepairs = in.readString();
        isneeddispatch = in.readString();


    }





    public EmployeeInfo()
    {

    }

}
