package com.points.autorepar.activity.workroom;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.points.autorepar.R;
import com.points.autorepar.bean.RefEvent;
import com.points.autorepar.bean.RepairHistory;
import com.points.autorepar.http.HttpManager;
import com.points.autorepar.utils.LoginUserUtil;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import RxJava.ClickCallback;
import de.greenrobot.event.EventBus;

public class WorkRoomEditActivityViewModel {
    public static void updateCarInfoPics(Context context, RepairHistory newRep, ClickCallback callback){
        Map cv = new HashMap();
        cv.put("id", newRep.idfromnode);
        cv.put("carinfopics", newRep.getArrCarInfoPicsString());
        HttpManager.getInstance(context).updateOneRepair("/repair/updateCarInfoPics", cv, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                if(jsonObject.optInt("code") == 1){
                    Toast.makeText(context,"更新成功",Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(context,"更新失败",Toast.LENGTH_SHORT).show();
                }
                callback.handleClick();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(context,"更新失败",Toast.LENGTH_SHORT).show();
            }
        });
    }
}
