package com.points.autorepar.utils;

import android.content.Context;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ares on 6/18/16.
 */
public class DateUtil {
    public static String getDateFormatString(Date date) {
        String dateString = null;
        if (null != date) {
            SimpleDateFormat format=new SimpleDateFormat("yyyyMMdd_HHmmss");
            dateString = format.format(date);
        }

        return dateString;
    }

    public static String timeFrom(Date date) {
        String dateString = null;
        if (null != date) {
            SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            dateString = format.format(date);
        }

        return dateString;
    }

    public static String getYear( ) {
        Date date = new Date();
        String dateString = null;
        if (null != date) {
            SimpleDateFormat format=new SimpleDateFormat("yyyy-01-01");
            dateString = format.format(date);
        }

        return dateString;
    }

    public static String getToday( ) {
        Date date = new Date();
        String dateString = null;
        if (null != date) {
            SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
            dateString = format.format(date);
        }

        return dateString;
    }

    public static boolean isValidTimeSelect(String start,String end){
        SimpleDateFormat CurrentTime=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date beginTime=CurrentTime.parse(start);
            Date endTime=CurrentTime.parse(end);
            if((endTime.getTime() - beginTime.getTime())>0) {
                 return  true;
            }else{
                return  false;
            }

        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return  false;
        }
    }

    public static String getPicNameFormTime(Date date, Context Context) {
        String dateString = null;
        if (null != date) {
            SimpleDateFormat format=new SimpleDateFormat("yyyyMMddHHmmss");
            dateString = format.format(date);
        }

        return LoginUserUtil.getTel(Context)+"_"+dateString;
    }

    public static String timetamp2Normal(String dataFormat, long timeStamp) {
        if (timeStamp == 0) {
            return "";
        }
        String result = "";
        SimpleDateFormat format = new SimpleDateFormat(dataFormat);
        result = format.format(new Date(timeStamp));
        return result;
    }
}


