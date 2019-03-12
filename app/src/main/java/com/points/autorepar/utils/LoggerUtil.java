package com.points.autorepar.utils;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by points on 2017/3/9.
 */

public class LoggerUtil {
    /**
     * 获取对象的json字符串
     * @param obj
     * @return
     */
    public  static String jsonFromObject(Object obj){
        Gson gson2 = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE).create();
        String json = gson2.toJson(obj);
        return json;
    }
}
