package com.points.autorepar.bean;

/**
 * Created by points on 16/11/25.
 */


import android.os.Parcel;
import android.os.Parcelable;



public class Contact implements Parcelable {

    private  String carCode;
    private  String tel;
    private  String name;
    private  String carType;
    private  String owner;
    private  String idfromnode;
    private  String inserttime;
    private  String isbindweixin;
    private  String weixinopenid;
    private  String vin;
    private  String carregistertime;
    private  String headurl;

    private  String safecompany;
    private  String safenexttime;
    private  String yearchecknexttime;
    private  String tqTime1;
    private  String tqTime2;
    private  String car_key;
    private  String  isVip;
    private  String carId;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(carCode);
        out.writeString(tel);
        out.writeString(name);
        out.writeString(carType);
        out.writeString(owner);
        out.writeString(idfromnode);
        out.writeString(inserttime);
        out.writeString(isbindweixin);
        out.writeString(weixinopenid);
        out.writeString(vin);
        out.writeString(carregistertime);
        out.writeString(headurl);

        out.writeString(safecompany);
        out.writeString(safenexttime);
        out.writeString(yearchecknexttime);
        out.writeString(tqTime1);
        out.writeString(tqTime2);
        out.writeString(car_key);

        out.writeString(isVip);
        out.writeString(carId);

    }

    public static final Parcelable.Creator<Contact> CREATOR = new Parcelable.Creator<Contact>() {
        public Contact createFromParcel(Parcel in) {
            return new Contact(in);
        }

        public Contact[] newArray(int size) {
            return new Contact[size];
        }
    };

    public Contact(Parcel in) {
        carCode = in.readString();
        tel = in.readString();
        name = in.readString();
        carType = in.readString();
        owner = in.readString();
        idfromnode = in.readString();

        inserttime = in.readString();
        isbindweixin = in.readString();
        weixinopenid = in.readString();
        vin = in.readString();
        carregistertime = in.readString();
        headurl = in.readString();

        safecompany = in.readString();
        safenexttime = in.readString();
        yearchecknexttime = in.readString();
        tqTime1 = in.readString();
        tqTime2 = in.readString();
        car_key = in.readString();

        isVip = in.readString();
        carId = in.readString();
    }





    public  Contact()
    {

    }

    public  void setCarCode(String _carCode)
    {
        this.carCode = _carCode;
    }

    public  void setName(String _name)
    {
        this.name = _name;
    }

    public  void setTel(String _tel)
    {
        this.tel = _tel;
    }

    public  void setCarType(String _carType)
    {
        this.carType = _carType;
    }

    public  void setOwner(String _owner)
    {
        this.owner = _owner;
    }

    public  void setIdfromnode(String _idfromnode)
    {
        this.idfromnode = _idfromnode;
    }

    public  void setInserttime(String _inserttime)
    {
        this.inserttime = _inserttime;
    }
    public  void setIsbindweixin(String _isbindweixin)
    {
        this.isbindweixin = _isbindweixin;
    }
    public  void setWeixinopenid(String openid)
    {
        this.weixinopenid = openid;
    }
    public  void setVin(String _vin)
    {
        this.vin = _vin;
    }
    public  void setCarregistertime(String _time)
    {
        this.carregistertime = _time;
    }
    public  void setHeadurl(String _url)
    {
        this.headurl = _url;
    }
    public  void setSafecompany(String _safecompany)
    {
        this.safecompany = _safecompany;
    }
    public  void setYearchecknexttime(String _p)
    {
        this.yearchecknexttime = _p;
    }
    public  void setSafenexttime(String _p)
    {
        this.safenexttime = _p;
    }
    public  void setTqTime1(String _P)
    {
        this.tqTime1 = _P;
    }
    public  void setTqTime2(String _p)
    {
        this.tqTime2 = _p;
    }
    public  void setCar_key(String _p) {this.car_key = _p;}
    public  void setIsVip(String _p) {this.isVip = _p;}
    public  void setCarId(String _p) {this.carId = _p;}

    public String getName()
    {
        return name;
    }

    public String getCarType()
    {
        return carType;
    }

    public String getCarCode()
    {
        return carCode;
    }

    public String getTel()
    {
        return tel;
    }

    public String getOwner()
    {
        return owner;
    }

    public String getIdfromnode()
    {
        return idfromnode;
    }

    public String getInserttime()
    {
        return inserttime;
    }
    public String getIsbindweixin()
    {
        return isbindweixin;
    }
    public String getWeixinopenid()
    {
        return weixinopenid;
    }
    public String getVin()
    {
        return vin;
    }
    public String getCarregistertime()
    {
        return carregistertime;
    }
    public String getHeadurl()
    {
        if(headurl.contains(".png")){
            return headurl == null ? "":headurl;
        }else {
            return headurl == null ? "":headurl+".png";
        }
    }

    public String getSafecompany()
    {
        return safecompany;
    }
    public String getYearchecknexttime()
    {
        return yearchecknexttime;
    }
    public String getSafenexttime()
    {
        return safenexttime;
    }
    public String getTqTime1()
    {
        return tqTime1;
    }
    public String getTqTime2()
    {
        return tqTime2;
    }
    public String getCar_key() {return car_key;}
    public String getisVip() {return isVip;}
    public String getCar_id() {return carId;}

}
