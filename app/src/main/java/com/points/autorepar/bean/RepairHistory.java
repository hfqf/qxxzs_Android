package com.points.autorepar.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.points.autorepar.common.Consts;

import java.util.ArrayList;

/**
 * Created by points on 16/11/25.
 */

public class RepairHistory implements Parcelable {

    public  int    ID;
    public  String carCode;
    public  String totalKm;
    public  String repairTime;
    public  String repairType;
    public  String addition;
    public  String tipCircle;
    public  String isClose;
    public  String circle;
    public  String isreaded;
    public  String owner;
    public  String idfromnode;
    public  String inserttime;
    public  String totalPrice;

    public  int    isAddedNewRepair ;
    public ArrayList<ADTReapirItemInfo> arrRepairItems;
    public  int  m_isAddNewRep ;

    public String payType;
    public String  ownnum;


    public  String  state;
    public  String  wantedcompletedtime;
    public  String  customremark;
    public  String  iswatiinginshop;
    public  String  entershoptime;
    public  String  contactid;
    public String itemtype;


    public  String pics;
    public String saleMoney;
    public String oilvolume;
    public String nexttipkm;
    public ArrayList<String> arrCarInfoPics;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(ID);

        out.writeString(carCode);
        out.writeString(totalKm);
        out.writeString(repairTime);
        out.writeString(repairType);
        out.writeString(addition);
        out.writeString(tipCircle);
        out.writeString(isClose);
        out.writeString(circle);



        out.writeString(isreaded);
        out.writeString(owner);
        out.writeString(idfromnode);
        out.writeString(inserttime);
        out.writeString(totalPrice);
        out.writeInt(isAddedNewRepair);
        out.writeList(arrRepairItems);
        out.writeInt(m_isAddNewRep);
        out.writeString(payType);
        out.writeString(ownnum);
        out.writeString(state);
        out.writeString(wantedcompletedtime);
        out.writeString(customremark);
        out.writeString(iswatiinginshop);
        out.writeString(entershoptime);
        out.writeString(contactid);
        out.writeString(itemtype);
        out.writeString(pics);

        out.writeString(saleMoney);
        out.writeString(oilvolume);
        out.writeString(nexttipkm);
        out.writeList(arrCarInfoPics);

    }

    public static final Parcelable.Creator<RepairHistory> CREATOR = new Parcelable.Creator<RepairHistory>() {
        public RepairHistory createFromParcel(Parcel in) {
            return new RepairHistory(in);
        }

        public RepairHistory[] newArray(int size) {
            return new RepairHistory[size];
        }
    };

    public RepairHistory(Parcel in) {
        ID = in.readInt();
        carCode = in.readString();
        totalKm = in.readString();
        repairTime = in.readString();
        repairType = in.readString();
        addition = in.readString();
        tipCircle = in.readString();
        isClose = in.readString();
        circle = in.readString();

        isreaded = in.readString();
        owner = in.readString();
        idfromnode = in.readString();
        inserttime = in.readString();
        totalPrice = in.readString();
        isAddedNewRepair = in.readInt();
        try {
            arrRepairItems = in.readArrayList(ADTReapirItemInfo.class.getClassLoader());
        }catch (Exception e){
            e.printStackTrace();
            arrRepairItems = null;
        }
        m_isAddNewRep = in.readInt();
        payType = in.readString();
        ownnum = in.readString();
        state = in.readString();
        wantedcompletedtime = in.readString();
        customremark = in.readString();
        iswatiinginshop = in.readString();
        entershoptime = in.readString();
        contactid = in.readString();
        itemtype= in.readString();
        pics = in.readString();
        saleMoney = in.readString();
        oilvolume = in.readString();
        nexttipkm = in.readString();
        try {
            arrCarInfoPics = in.readArrayList(String.class.getClassLoader());
        }catch (Exception e){
            e.printStackTrace();
            arrCarInfoPics = null;
        }
    }


    public  RepairHistory()
    {

    }

    public ArrayList<String> getArrCarInfoPics() {
        if(arrCarInfoPics.size()>0){
            return arrCarInfoPics;
        }else if(pics.contains(",")){
            String[] parts = pics.split(",");
            for(int i=0;i<parts.length;i++){
                String url = parts[i];
                if(url.contains("png")){
                    arrCarInfoPics.add(Consts.HTTP_URL+"/file/pic/"+url);
                }else {
                    arrCarInfoPics.add(Consts.HTTP_URL+"/file/pic/"+url+".png");
                }
            }
            return arrCarInfoPics;
        }
        return arrCarInfoPics;
    }

    public String getArrCarInfoPicsString() {
        String pics = "";
        for(int i=0;i<getArrCarInfoPics().size();i++){
            String url = getArrCarInfoPics().get(i);
            pics+=url;
            if(i!=getArrCarInfoPics().size()-1){
                pics+=",";
            }
        }
        return pics;
    }
}
