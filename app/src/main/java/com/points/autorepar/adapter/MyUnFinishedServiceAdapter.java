package com.points.autorepar.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.google.gson.JsonArray;
import com.points.autorepar.MainApplication;
import com.points.autorepar.R;
import com.points.autorepar.activity.BaseActivity;
import com.points.autorepar.activity.LoginActivity;
import com.points.autorepar.activity.workroom.EmployeeWorkRoomListActivity;
import com.points.autorepar.bean.Contact;
import com.points.autorepar.bean.RepairHistory;
import com.points.autorepar.bean.WorkRoomPicBackEvent;
import com.points.autorepar.http.HttpManager;
import com.points.autorepar.sql.DBService;
import com.points.autorepar.utils.LoginUserUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;

/**
 * Created by points on 16/12/6.
 */


public class MyUnFinishedServiceAdapter extends BaseAdapter {

    private Context m_context;
    private LayoutInflater m_LInflater;
    private JSONArray m_arrData;
    private Boolean isFinished;
//    private MyUnFinishedServiceAdapterInterface m_listener;
    private MyUnFinishedServiceAdapter that;
    public MyUnFinishedServiceAdapter(Context context, JSONArray list,Boolean isFinished) {

        this.m_context   = context;
        this.m_LInflater = LayoutInflater.from(context);
        this.m_arrData   = list;
        this.isFinished = isFinished;
//        this.m_listener = listener;
        that = this;
    }

    public interface MyUnFinishedServiceAdapterInterface{
        void onConfirmed(JSONObject object);
    }

    @Override
    public int getCount(){
        return m_arrData != null ? m_arrData.length() : 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        ViewHolder holder = null ;

        final JSONObject object = (JSONObject)m_arrData.optJSONObject(position);
        if(convertView==null){
            convertView = this.m_LInflater.inflate(R.layout.my_unfinished_service_cell, null);
            holder = new ViewHolder();
            holder.mTV1 = ((TextView) convertView.findViewById(R.id.id_1));
            holder.mBtn = ((Button) convertView.findViewById(R.id.id_2));
            holder.mTV3 = ((TextView) convertView.findViewById(R.id.id_3));
            holder.mTV4 = ((TextView) convertView.findViewById(R.id.id_4));
            holder.mTV5 = ((TextView) convertView.findViewById(R.id.id_5));
            holder.mTV6 = ((TextView) convertView.findViewById(R.id.id_6));
            holder.mTV7 = ((TextView) convertView.findViewById(R.id.id_7));
            holder.mTV8 = ((TextView) convertView.findViewById(R.id.id_8));
            holder.mTV9 = ((TextView) convertView.findViewById(R.id.id_9));
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder)convertView.getTag();
        }

        Contact customer =  DBService.queryContact(object.optString("contactid"));
        String url = "";
        BaseActivity activity = (BaseActivity) this.m_context;
        if(customer != null){
            url = MainApplication.consts(this.m_context).BOS_SERVER+customer.getHeadurl();

        }


        final ViewHolder _holder = holder;
        holder.mTV1.setText("车牌号:"+(customer==null ?"": customer.getCarCode()));
        holder.mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                that.m_listener.onConfirmed(object);
                finishend(object.optString("_id"));

            }
        });
        holder.mTV3.setText("服务内容:"+object.optString("type"));

        holder.mTV5.setText("派单时间"+object.optString("dispatchtime"));
        if(this.isFinished){
            holder.mTV6.setText("完成时间"+object.optString("finishtime"));
            holder.mTV6.setVisibility(View.VISIBLE);
            holder.mBtn.setVisibility(View.GONE);

        }else{
            holder.mTV6.setVisibility(View.GONE);
            holder.mBtn.setVisibility(View.VISIBLE);
        }

        holder.mTV7.setText("工时费:¥"+object.optString("workhourpay"));
        holder.mTV8.setText("x"+object.optString("num"));
        holder.mTV9.setText("¥"+Integer.parseInt(object.optString("workhourpay"))*Integer.parseInt(object.optString("num")));

        return convertView;
    }
    @Override
    public long getItemId(int position){
        return position;
    }

    @Override
    public  Object getItem(int position){
        JSONObject con = (JSONObject) m_arrData.optJSONObject(position);
        return con;
    }


    private class ViewHolder {

        TextView mTV1;
        Button   mBtn;
        TextView mTV2;
        TextView mTV3;
        TextView mTV4;
        TextView mTV5;
        TextView mTV6;
        TextView mTV7;
        TextView mTV8;
        TextView mTV9;
    }

    public   void  finishend(String id){


        Map map = new HashMap();
        map.put("id",id);

        map.put("ostype","android" );
        String str = MainApplication.getInstance().getCreatePushID(m_context);
        map.put("pushid",  str);


        String url = "/repairitem/employecommit";

        HttpManager.getInstance(m_context)
                .queryAllTipedRepair(url, map, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {



                        if(jsonObject.optInt("code") == 1){


                            final JSONArray ary = jsonObject.optJSONArray("ret");
                            Handler mainHandler = new Handler(Looper.getMainLooper());
                            mainHandler.post(new Runnable() {
                                @Override
                                public void run() {

                                    EventBus.getDefault().post(new WorkRoomPicBackEvent(""));
//                                    MyUnFinishedServiceAdapter m_adapter = new MyUnFinishedServiceAdapter(EmployeeWorkRoomListActivity.this,ary ,_m_index==0?false:true);

                                }
                            });

                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                    }
                });

    }

}
