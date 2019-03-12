package com.points.autorepar.utils;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

/**
 * Created by points on 2017/3/9.
 */

public class JSONOejectUtil {


    public static String optString(JSONObject json, String key)
    {
        // http://code.google.com/p/android/issues/detail?id=13830
        if (json.isNull(key))
            return " ";
        else
            return json.optString(key, null);
    }

}
