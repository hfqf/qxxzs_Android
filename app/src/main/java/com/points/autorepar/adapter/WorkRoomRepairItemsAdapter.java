package com.points.autorepar.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.points.autorepar.MainApplication;
import com.points.autorepar.R;
import com.points.autorepar.activity.PayOffActivity;
import com.points.autorepar.activity.serviceManager.SelectServiceCategoryActivity;
import com.points.autorepar.activity.serviceManager.SelectServiceHomeActivity;
import com.points.autorepar.activity.workroom.WorkRoomEditActivity;
import com.points.autorepar.bean.ADTReapirItemInfo;
import com.points.autorepar.bean.RepairHistory;
import com.points.autorepar.bean.RepairerInfo;
import com.points.autorepar.http.HttpManager;
import com.points.autorepar.lib.wheelview.WheelView;
import com.points.autorepar.utils.LoginUserUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by points on 16/11/28.
 */
public class WorkRoomRepairItemsAdapter extends BaseAdapter {
    private Context         m_context;
    private LayoutInflater  m_LInflater;
    public RepairHistory    m_data;
    private  final  String TAG = "WorkRoomRepairItemsAdapter";

    private int selectedIndex = 0;

    private  WorkRoomEditActivity m_activity1;
    private LinearLayout  m_addView;

    public WorkRoomRepairItemsAdapter(Context context, RepairHistory rep) {
        this.m_activity1 = (WorkRoomEditActivity )context;
        this.m_context   = context;
        this.m_LInflater = LayoutInflater.from(context);
        this.m_data   = rep;
    }

    @Override
    public int getCount(){

        if(this.m_data.arrRepairItems == null)
            return  0;

        if(this.m_data.state!=null && "2".equalsIgnoreCase(this.m_data.state))
        {
            return this.m_data.arrRepairItems.size();
        }else{
            return this.m_data.arrRepairItems.size()+1;
        }
}

    @Override
    public   View getView(int position, View convertView, ViewGroup parent) {

        if(m_data.state.equals("2")){

            final   ADTReapirItemInfo info = m_data.arrRepairItems.get(position);
            final int   index =  position-1;
            ViewHolderWorkRoomPriceItemCell holder = null ;



            convertView = this.m_LInflater.inflate(R.layout.cell_workroom_item, null);
            holder = new ViewHolderWorkRoomPriceItemCell();
            holder.type =  ((TextView) convertView.findViewById(R.id.workroom_item_cell_type));
            holder.price = ((TextView) convertView.findViewById(R.id.workroom_item_cell_price));
            holder.num = ((TextView) convertView.findViewById(R.id.workroom_item_cell_num));
            holder.total = ((TextView) convertView.findViewById(R.id.workroom_item_cell_totalprice));


            holder.delBtn = ((ImageButton) convertView.findViewById(R.id.workroom_item_cell_del));
            if(m_data.state.equals("2")){
                holder.delBtn.setVisibility(View.INVISIBLE);
            }
            convertView.setTag(holder);

            boolean isNumber = true;
            Pattern pattern = Pattern.compile("[0-9]*");
            Matcher isNum = pattern.matcher(info.price);
            if( !isNum.matches() ){
                isNumber = false;
            }

            int info_price = 0;
            if(info.price !=null)
            {
                info_price = "".equalsIgnoreCase(info.price)?0:(Integer.parseInt(info.price));
            }
            int info_num =0;
            if(info.num!= null){
                info_num = "".equalsIgnoreCase(info.num)?0:(Integer.parseInt(info.num));
            }


            holder.type.setText(info.type);
            holder.total.setText("¥ "+Integer.parseInt(info.price)*Integer.parseInt(info.num));
            int info_workhourpay = 0;
            if(info.workhourpay !=null) {

                info_workhourpay = "".equalsIgnoreCase(info.workhourpay) ? 0 : (Integer.parseInt(info.workhourpay));

            }else{
                info.workhourpay = "0";
            }
            if(isNumber){

                holder.total.setText("¥ "+ (info_price+info_workhourpay)*info_num );
            }else {
                holder.total.setText("?");
            }

            holder.price.setText("¥ " + info.price + " 工时：￥" + info.workhourpay);
            holder.num.setText("x"+info_num);

            holder.delBtn.setClickable(true);
            holder.delBtn.setFocusableInTouchMode(false);
            holder.delBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final AlertDialog.Builder normalDialog =
                            new AlertDialog.Builder(m_activity1);
                    normalDialog.setTitle("删除此收费记录,不可恢复!");
                    normalDialog.setMessage("确认删除?");
                    normalDialog.setPositiveButton("确定",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    m_activity1.showWaitView();
                                    Map map = new HashMap();
                                    map.put("id", info.idfromnode);
                                    HttpManager.getInstance(m_activity1).addNewRepair("/repairitem/del", map, new Response.Listener<JSONObject>() {
                                        @Override
                                        public void onResponse(JSONObject jsonObject) {

                                            m_activity1.stopWaitingView();
                                            if(jsonObject.optInt("code") == 1){
                                                Toast.makeText(m_activity1,"删除成功",Toast.LENGTH_SHORT).show();

                                                m_data.arrRepairItems.remove(index);
                                                ArrayList<ADTReapirItemInfo> arrItems = m_data.arrRepairItems;
                                                int totalPrice = 0;
                                                for(int j=0;j<arrItems.size();j++){
                                                    ADTReapirItemInfo item = arrItems.get(j);
                                                    totalPrice+=item.currentPrice;
                                                }
                                                m_data.totalPrice = String.valueOf(totalPrice);
                                                notifyDataSetChanged();
                                                m_activity1.refreshDataAndBottomView(m_data);
                                            }
                                            else {
                                                Toast.makeText(m_activity1,"删除失败",Toast.LENGTH_SHORT).show();
                                            }


                                        }
                                    }, new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError volleyError) {

                                            m_activity1.stopWaitingView();
                                            Toast.makeText(m_activity1,"删除失败",Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                }
                            });
                    normalDialog.setNegativeButton("关闭",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //...To-do
                                }
                            });
                    // 显示
                    normalDialog.show();



                }
            });


        }else {
            if(position == 0){

                String key_isdirectadditem= m_activity1.getApplicationContext().getResources().getString(R.string.key_loginer_isdirectadditem);
                SharedPreferences sp = m_activity1.getSharedPreferences("points", Context.MODE_PRIVATE);
                String isdirectadditem = sp.getString(key_isdirectadditem, null);



                if("0".equalsIgnoreCase(isdirectadditem))
                {



                ViewHolderAddItem holder6 = null ;
                convertView = this.m_LInflater.inflate(R.layout.repair_info_item_add_new, null);
                holder6 = new ViewHolderAddItem();
//                holder6.tip = ((TextView) convertView.findViewById(R.id.repair_info_cell_tip));
//                holder6.value1 = ((EditText) convertView.findViewById(R.id.repair_info_cell_content1));
//                holder6.value2 = ((EditText) convertView.findViewById(R.id.repair_info_cell_content2));
//                holder6.value3 = ((EditText) convertView.findViewById(R.id.repair_info_cell_content3));
                holder6.addBtn1 = ((Button) convertView.findViewById(R.id.repair_info_cell_bt1));
                holder6.addBtn2 = ((Button) convertView.findViewById(R.id.repair_info_cell_bt2));
                convertView.setTag(holder6);
                holder6.addBtn1.setClickable(true);
                holder6.addBtn1.setFocusableInTouchMode(false);
                holder6.addBtn2.setClickable(true);
                holder6.addBtn2.setFocusableInTouchMode(false);

                final ViewHolderAddItem  _holder6 = holder6;
                holder6.addBtn1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(m_context, SelectServiceCategoryActivity.class);
                        intent.putExtra("repid",m_data.idfromnode);
                        intent.putExtra("contactid",m_data.contactid);
                        intent.putExtra("data",m_data);

                         Bundle selectednum = new Bundle();
                        for(int i=0;i<m_data.arrRepairItems.size();i++){
                            ADTReapirItemInfo item =  m_data.arrRepairItems.get(i);
                            if(Integer.parseInt(item.num) >0){
                                selectednum.putString(item.type,item.num);
                            }
                        }
                        intent.putExtra("selectednum",selectednum);
                        m_context.startActivity(intent);
                    }
                });
                holder6.addBtn2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(m_context, SelectServiceHomeActivity.class);
                        intent.putExtra("repid",m_data.idfromnode);
                        intent.putExtra("contactid",m_data.contactid);
                        intent.putExtra("data",m_data);
                        m_context.startActivity(intent);
                    }
                });

                }else{

                    ViewHolderAddItemOld holder6 = null ;
                    convertView = this.m_LInflater.inflate(R.layout.repair_info_item_add, null);
                    holder6 = new ViewHolderAddItemOld();
                    holder6.tip = ((TextView) convertView.findViewById(R.id.repair_info_cell_tip));
                    holder6.value1 = ((EditText) convertView.findViewById(R.id.repair_info_cell_content1));
                    holder6.value2 = ((EditText) convertView.findViewById(R.id.repair_info_cell_content2));
                    holder6.value3 = ((EditText) convertView.findViewById(R.id.repair_info_cell_content3));
                    holder6.addBtn = ((Button) convertView.findViewById(R.id.repair_info_cell_content4));
                    convertView.setTag(holder6);
                    holder6.addBtn.setClickable(true);
                    holder6.addBtn.setFocusableInTouchMode(false);

                    final ViewHolderAddItemOld  _holder6 = holder6;
                    holder6.addBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {



                            if(_holder6.value1.getText().toString().length() == 0){
                                Toast.makeText(m_context,"收费内容不能为空",Toast.LENGTH_SHORT).show();
                                return;
                            }

                            if(_holder6.value2.getText().toString().length() == 0){
                                Toast.makeText(m_context,"收费价格只能填写数字",Toast.LENGTH_SHORT).show();
                                return;
                            }
                            if(!LoginUserUtil.isNumeric(_holder6.value2.getText().toString())){
                                Toast.makeText(m_context,"收费价格只能填写数字",Toast.LENGTH_SHORT).show();
                                return;
                            }

                            if(_holder6.value3.getText().toString().length() == 0){
                                Toast.makeText(m_context,"收费次数只能填写数字",Toast.LENGTH_SHORT).show();
                                return;
                            }
                            if(!LoginUserUtil.isNumeric(_holder6.value3.getText().toString())){
                                Toast.makeText(m_context,"收费次数只能填写数字",Toast.LENGTH_SHORT).show();
                                return;
                            }

                            final  ADTReapirItemInfo       item = new ADTReapirItemInfo();
                            item.type =  _holder6.value1.getText().toString();
                            item.price = _holder6.value2.getText().toString();
                            item.num = _holder6.value3.getText().toString();
                            item.contactId = m_data.carCode;
                            item.currentPrice =  Integer.parseInt(item.price)*Integer.parseInt(item.num);

                            item.repId = m_data.idfromnode;

                            Map map = new HashMap();
                            map.put("repid", item.repId);
                            map.put("contactid", item.contactId);
                            map.put("price", item.price);
                            map.put("num", item.num);
                            map.put("type", item.type);
                            m_activity1.showWaitView();
                            HttpManager.getInstance(m_context).addNewRepair("/repairitem/add", map, new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject jsonObject) {

                                    m_activity1.stopWaitingView();
                                    item.idfromnode =  jsonObject.optJSONObject("ret").optString("_id");
                                    if(jsonObject.optInt("code") == 1){
                                        Toast.makeText(m_context,"添加收费明细成功",Toast.LENGTH_SHORT).show();



                                        m_data.arrRepairItems.add(item);
                                        ArrayList<ADTReapirItemInfo> arrItems = m_data.arrRepairItems;
                                        int totalPrice = 0;
                                        for(int j=0;j<arrItems.size();j++){
                                            ADTReapirItemInfo item = arrItems.get(j);
                                            totalPrice+=item.currentPrice;
                                        }
                                        m_data.totalPrice = String.valueOf(totalPrice);
                                        notifyDataSetChanged();

                                        updateRepair();

                                    }
                                    else {
                                        Toast.makeText(m_context,"添加收费明细失败",Toast.LENGTH_SHORT).show();
                                    }


                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError volleyError) {
                                    m_activity1.stopWaitingView();
                                    Toast.makeText(m_context,"添加收费明细失败",Toast.LENGTH_SHORT).show();
                                }
                            });


                        }
                    });


                }
            }else {
                final   ADTReapirItemInfo info = m_data.arrRepairItems.get(position-1);
                final int   index =  position-1;
                ViewHolderWorkRoomPriceItemCell holder = null ;



                convertView = this.m_LInflater.inflate(R.layout.cell_workroom_item, null);
                holder = new ViewHolderWorkRoomPriceItemCell();
                holder.type =  ((TextView) convertView.findViewById(R.id.workroom_item_cell_type));
                holder.price = ((TextView) convertView.findViewById(R.id.workroom_item_cell_price));
                holder.num = ((TextView) convertView.findViewById(R.id.workroom_item_cell_num));
                holder.total = ((TextView) convertView.findViewById(R.id.workroom_item_cell_totalprice));
                holder.workroom_item_cell_send = ((TextView) convertView.findViewById(R.id.workroom_item_cell_send));

                holder.delBtn = ((ImageButton) convertView.findViewById(R.id.workroom_item_cell_del));
                if(m_data.state.equals("2")){
                    holder.delBtn.setVisibility(View.INVISIBLE);
                }
                convertView.setTag(holder);

                holder.type.setText(info.type);

                boolean isNumber = true;
                Pattern pattern = Pattern.compile("[0-9]*");
                Matcher isNum = pattern.matcher(info.price);
                if( !isNum.matches() ){
                    isNumber = false;
                }

                String key_isneeddispatch =  m_context.getApplicationContext().getResources().getString(R.string.key_loginer_isneeddispatch);
                SharedPreferences sp = m_context.getSharedPreferences("points", Context.MODE_PRIVATE);
                String isneeddispatch = sp.getString(key_isneeddispatch, null);

               int roomType =  MainApplication.getInstance().getRoomType(m_context);

               if(roomType == 0) {
                   holder.workroom_item_cell_send.setVisibility(View.VISIBLE);
                   if ("1".equalsIgnoreCase(info.itemtype) && "1".equalsIgnoreCase(isneeddispatch)) {
                       if (info.repairer == null || "".equalsIgnoreCase(info.repairer)) {
                           holder.workroom_item_cell_send.setText("还未派单");
                           holder.workroom_item_cell_send.setTextColor(Color.RED);
                       } else {
                           holder.workroom_item_cell_send.setVisibility(View.VISIBLE);

                           if ("1".equalsIgnoreCase(info.state)) {
                               holder.workroom_item_cell_send.setText("服务已完成");
                               holder.workroom_item_cell_send.setTextColor(Color.GREEN);
                           } else {
                               holder.workroom_item_cell_send.setText("服务未完成");
                               holder.workroom_item_cell_send.setTextColor(Color.GRAY);
                           }
                       }
                   } else {
                       holder.workroom_item_cell_send.setVisibility(View.GONE);

                   }
               }else
               {
                   holder.workroom_item_cell_send.setVisibility(View.GONE);
               }

                holder.workroom_item_cell_send.setClickable(true);
                holder.workroom_item_cell_send.setFocusableInTouchMode(false);


                holder.workroom_item_cell_send.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        m_activity1.showWaitView();

                        Map map = new HashMap();

                        map.put("creatertel", LoginUserUtil.getTel(m_context));
                        HttpManager.getInstance(m_activity1).addNewRepair("/employee/query", map, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject jsonObject) {
                               final ADTReapirItemInfo adInfo = m_data.arrRepairItems.get(index);
                                m_activity1.stopWaitingView();
                                if(jsonObject.optInt("code") == 1){

                                    try {
                                        ArrayList<RepairerInfo> ary =new ArrayList<RepairerInfo>();
                                        ArrayList<String> arr =new ArrayList<String>();
                                        ArrayList<RepairerInfo> aryTemp1 = new ArrayList<RepairerInfo> ();
                                        JSONArray jsonArray = jsonObject.getJSONArray("ret");
                                        for(int i=0;i<jsonArray.length();i++)
                                        {
                                            RepairerInfo info = RepairerInfo.fromWithJsonObj(jsonArray.optJSONObject(i));

                                            if("1".equalsIgnoreCase(info.roletype) && "1".equalsIgnoreCase(info.state)) {
                                                aryTemp1.add(info);
                                                arr.add (info.username+"("+info.tel+")");
                                            }
                                        }
                                        final  ArrayList<RepairerInfo> aryTemp = aryTemp1;
                                        final  RepairHistory dataTemp = m_data;
//                                        String[] arr = getResources().getStringArray(R.array.service_home_cell);
                                        View outerView = LayoutInflater.from(m_context).inflate(R.layout.wheel_view, null);
                                        final WheelView wv =  (WheelView)outerView.findViewById(R.id.wheel_view_wv);
                                        wv.setItems(arr);
                                        wv.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
                                            @Override
                                            public void onSelected(int selectedIndex1, String item) {
//                                                Log.e(TAG, "[Dialog]selectedIndex: " + selectedIndex + ", item: " + item);
                                                selectedIndex = selectedIndex1;
                                            }
                                        });
                                        new AlertDialog.Builder(m_context)
                                                .setTitle("请选择派单师傅")
                                                .setView(outerView)
                                                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {


                                                       String pushid = MainApplication.getInstance().getCreatePushID(m_context);
                                                        RepairerInfo info = aryTemp.get(selectedIndex);
                                                        Map map = new HashMap();
                                                        map.put("id", adInfo.idfromnode);
                                                        map.put("pushid", pushid);
                                                        map.put("repairer", info.repairer);
                                                        map.put("ostype", "android");


                                                            HttpManager.getInstance(m_context).getAllServiceTypePreviewList("/repairitem/apply2employee", map, new Response.Listener<JSONObject>() {
                                                                @Override
                                                                public void onResponse(JSONObject jsonObject) {
                                                                    if (jsonObject.optInt("code") == 1) {
                                                                        setPDstatus(index);
                                                                        Toast.makeText(m_context,"派单成功",Toast.LENGTH_SHORT).show();

                                                                    }
                                                                }
                                                            }, new Response.ErrorListener() {
                                                                @Override
                                                                public void onErrorResponse(VolleyError volleyError) {



                                                                }
                                                            });

                                                    }
                                                })
                                                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {

                                                    }
                                                })
                                                .show();
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }


//                                    ArrayList<ADTReapirItemInfo> arrItems = m_data.arrRepairItems;
//                                    int totalPrice = 0;
//                                    for(int j=0;j<arrItems.size();j++){
//                                        ADTReapirItemInfo item = arrItems.get(j);
//                                        totalPrice+=item.currentPrice;
//                                    }
//                                    m_data.totalPrice = String.valueOf(totalPrice);
//                                    notifyDataSetChanged();
//                                    m_activity1.refreshDataAndBottomView(m_data);
                                }
                                else {
                                    Toast.makeText(m_activity1,"删除失败",Toast.LENGTH_SHORT).show();
                                }


                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError volleyError) {

                                m_activity1.stopWaitingView();
                                Toast.makeText(m_activity1,"删除失败",Toast.LENGTH_SHORT).show();
                            }
                        });




                    }
                });

                int info_price = 0;
                if(info.price !=null)
                {
                    info_price = "".equalsIgnoreCase(info.price)?0:(Integer.parseInt(info.price));
                }
                int info_num =0;
                if(info.num!= null){
                    info_num = "".equalsIgnoreCase(info.num)?0:(Integer.parseInt(info.num));
                }


                int info_workhourpay = 0;
                if(info.workhourpay !=null) {

                        info_workhourpay = "".equalsIgnoreCase(info.workhourpay) ? 0 : (Integer.parseInt(info.workhourpay));

                }else{
                    info.workhourpay = "0";
                }
                if(isNumber){

                    holder.total.setText("¥ "+ (info_price+info_workhourpay)*info_num );
                }else {
                    holder.total.setText("?");
                }

                    holder.price.setText("¥ " + info.price + " 工时：￥" + info.workhourpay);

                holder.num.setText("x"+info.num);

                holder.delBtn.setClickable(true);
                holder.delBtn.setFocusableInTouchMode(false);
                holder.delBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        final AlertDialog.Builder normalDialog =
                                new AlertDialog.Builder(m_activity1);
                        normalDialog.setTitle("删除此收费记录,不可恢复!");
                        normalDialog.setMessage("确认删除?");
                        normalDialog.setPositiveButton("确定",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        m_activity1.showWaitView();
                                        Map map = new HashMap();
                                        map.put("id", info.idfromnode);

                                        HttpManager.getInstance(m_activity1).addNewRepair("/repairitem/del", map, new Response.Listener<JSONObject>() {
                                            @Override
                                            public void onResponse(JSONObject jsonObject) {

                                                m_activity1.stopWaitingView();
                                                if(jsonObject.optInt("code") == 1){
                                                    Toast.makeText(m_activity1,"删除成功",Toast.LENGTH_SHORT).show();

                                                    m_data.arrRepairItems.remove(index);
                                                    ArrayList<ADTReapirItemInfo> arrItems = m_data.arrRepairItems;
                                                    int totalPrice = 0;
                                                    for(int j=0;j<arrItems.size();j++){
                                                        ADTReapirItemInfo item = arrItems.get(j);
                                                        totalPrice+=item.currentPrice;
                                                    }
                                                    m_data.totalPrice = String.valueOf(totalPrice);
                                                    notifyDataSetChanged();
                                                    m_activity1.refreshDataAndBottomView(m_data);
                                                }
                                                else {
                                                    Toast.makeText(m_activity1,"删除失败",Toast.LENGTH_SHORT).show();
                                                }


                                            }
                                        }, new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError volleyError) {

                                                m_activity1.stopWaitingView();
                                                Toast.makeText(m_activity1,"删除失败",Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                    }
                                });
                        normalDialog.setNegativeButton("关闭",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //...To-do
                                    }
                                });
                        // 显示
                        normalDialog.show();



                    }
                });

            }


        }
        return convertView;
    }
    @Override
    public long getItemId(int position){
        return position;
    }

    @Override
    public  Object getItem(int position){
        return this.m_data;
    }

    private class ViewHolderNormalInput {
        TextView tip;
        EditText value;
    }

    private class ViewHolderMutilInput {
        TextView tip;
        EditText value;
    }

    private class ViewHolderAddItem {
//        TextView tip;
//        EditText value1;
//        EditText value2;
//        EditText value3;
        Button   addBtn1;
        Button   addBtn2;
    }

    private class ViewHolderAddItemOld {
        TextView tip;
        EditText value1;
        EditText value2;
        EditText value3;
        Button   addBtn;
    }

    private class ViewHolderWorkRoomPriceItemCell {
        TextView  type;
        ImageButton delBtn;

        TextView workroom_item_cell_send;
        TextView  price;;
        TextView  num;
        TextView  total;
    }

    private class ViewHolderSwiter {
        TextView tip;
        CheckBox checkbox;
    }

    private TextWatcher watcherKm = new TextWatcher( ) {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // TODO Auto-generated method stub

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
            // TODO Auto-generated method stub
        }

        @Override
        public void afterTextChanged(Editable s) {
            m_data.totalKm = s.toString();
        }
    };

    private TextWatcher watcherRepairTime = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // TODO Auto-generated method stub

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
            // TODO Auto-generated method stub

        }

        @Override
        public void afterTextChanged(Editable s) {
            m_data.repairTime = s.toString();
        }
    };

    private TextWatcher watcherTipCircle = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // TODO Auto-generated method stub

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
            // TODO Auto-generated method stub

        }

        @Override
        public void afterTextChanged(Editable s) {
            m_data.tipCircle = s.toString();

        }
    };

    private TextWatcher watcherRepairType = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // TODO Auto-generated method stub

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
            // TODO Auto-generated method stub

        }

        @Override
        public void afterTextChanged(Editable s) {
            m_data.repairType = s.toString();

        }
    };

    private TextWatcher watcherAddtion = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // TODO Auto-generated method stub

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
            // TODO Auto-generated method stub

        }

        @Override
        public void afterTextChanged(Editable s) {
            m_data.addition = s.toString();

        }
    };



    public void updateRepair(){

        JSONArray list = new JSONArray();
        JSONObject selectmap = null;
        for(int i=0;i<m_data.arrRepairItems.size();i++){
            ADTReapirItemInfo _item = m_data.arrRepairItems.get(i);
//            selectmap = new JSONObject();
//            try {
//
//
//                selectmap.put("id", _item.idfromnode);
//                selectmap.put("num", _item.num);
//                selectmap.put("contactid", m_data.contactid);
//            }catch (Exception e )
//            {
//                e.printStackTrace();
//            }
            list.put(_item.idfromnode);
        }
        Map cv = new HashMap();
        cv.put("carcode", m_data.carCode);
        cv.put("totalkm", m_data.totalKm);
        cv.put("repairetime", m_data.repairTime);
        cv.put("repairtype", m_data.repairType);
        cv.put("addition", m_data.addition);
        cv.put("tipcircle", m_data.tipCircle);
        cv.put("circle", m_data.circle);
        cv.put("isclose", m_data.isClose) ;
        cv.put("isreaded", m_data.isClose);
        cv.put("owner", LoginUserUtil.getTel(m_activity1));
        cv.put("id", m_data.idfromnode);
        cv.put("inserttime", m_data.inserttime);
        cv.put("items", list);
        cv.put("state", m_data.state);
        cv.put("wantedcompletedtime", m_data.wantedcompletedtime);
        cv.put("customremark", m_data.customremark);
        cv.put("iswatiinginshop", m_data.iswatiinginshop);
        cv.put("entershoptime", m_data.entershoptime);
        cv.put("pics",m_data.pics);
        cv.put("oilvolume",m_data.oilvolume);
        cv.put("nexttipkm",m_data.nexttipkm);
        m_activity1.showWaitView();
        HttpManager.getInstance(m_context).updateOneRepair("/repair/update5", cv, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {

                m_activity1.stopWaitingView();
                if(jsonObject.optInt("code") == 1){

                    m_activity1.refreshDataAndBottomView(m_data);
                    Toast.makeText(m_context,"更新成功",Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(m_context,"更新失败",Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

                m_activity1.stopWaitingView();
                Toast.makeText(m_context,"更新失败",Toast.LENGTH_SHORT).show();
            }
        });
    }

//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if(requestCode == 1&&resultCode == 2) {
////Intent intent = getIntent();   // data 本身就是一个 Inten  所以不需要再new了 直接调用里面的方法就行了
//            String s = data.getStringExtra("AA");
//        }
//    }


    public void setPDstatus (int index){
        m_data.arrRepairItems.get(index).repairer ="1";
        notifyDataSetChanged();;
    }

}
