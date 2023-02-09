package com.points.autorepar.http.submodule.repair;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.points.autorepar.bean.ADTReapirItemInfo;
import com.points.autorepar.http.HttpManager;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class HttpManagerRepairUtil {
    public static  void upDateRepairItem(Context context,
                                         ADTReapirItemInfo newInfo,
                                         Response.Listener<JSONObject> succeedListener,
                                         Response.ErrorListener errorListener){
        Map map = new HashMap();
        map.put("id", newInfo.idfromnode);
        map.put("type", newInfo.type);
        map.put("num", newInfo.num);
        map.put("price", newInfo.price);
        HttpManager.getInstance(context).post("/repair/updateOneItem", map, succeedListener, errorListener);
    }
}
