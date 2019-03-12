package com.points.autorepar.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.dyhdyh.widget.loading.dialog.LoadingDialog;
import com.points.autorepar.R;
import com.points.autorepar.activity.contact.ContactInfoEditActivity;
import com.points.autorepar.activity.repair.RemindActivity;
import com.points.autorepar.activity.workroom.WorkRoomEditActivity;
import com.points.autorepar.bean.ADTReapirItemInfo;
import com.points.autorepar.bean.Contact;
import com.points.autorepar.bean.ContactOrderInfo;
import com.points.autorepar.bean.RemindInfo;
import com.points.autorepar.bean.RepairHistory;
import com.points.autorepar.http.HttpManager;
import com.points.autorepar.lib.wheelview.WheelView;
import com.points.autorepar.sql.DBService;
import com.points.autorepar.utils.DateUtil;
import com.points.autorepar.utils.LoginUserUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by necer on 2017/6/7.
 */

public class Reminddapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private RemindActivity m_activity;
//    private
    private ArrayList<RemindInfo> m_data1;
    private ArrayList<RemindInfo> m_data2;
    private ArrayList<RemindInfo> m_data3;

    private int  m_data1_showType;
    private int  m_data2_showType;
    private int  m_data3_showType;
    public Reminddapter(Context context,RemindActivity activity) {
        this.context = context;
        this.m_activity = activity;
        m_data1_showType =0;
        m_data2_showType =0;
        m_data3_showType =0;
    }



    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == 0)
        {
            return new MyViewTopHolder(LayoutInflater.from(context).inflate(R.layout.activity_remind_itemtop, parent, false));
        }else  {
            return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.activity_remind_item, parent, false));

        }
    }
//
//    @Override
//    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
//
//    }


    public void setData1(ArrayList<RemindInfo> data)
    {
        this.m_data1 = data;
    }
    public void setData2(ArrayList<RemindInfo> data)
    {
        this.m_data2 = data;
    }

    public void setData3(ArrayList<RemindInfo> data)
    {
        this.m_data3 = data;
    }

    @Override
    public int getItemViewType(int position) {
        int listnum = 0;
        if(this.m_data1==null)
        {
            listnum =0;
        }else{
            if(m_data1_showType == 0) {
                listnum = this.m_data1.size();
            }else{
                listnum =0;
            }
        }
        int listnum2 = 0;
        if(this.m_data2==null)
        {
            listnum2 =0;
        }else{

            if(m_data2_showType == 0) {
                listnum2 = this.m_data2.size();
            }else{
                listnum2 =0;
            }
        }

        int listnum3 = 0;
        if(this.m_data3==null)
        {
            listnum3 =0;
        }else{

            if(m_data3_showType == 0) {
                listnum3 = this.m_data3.size();
            }else{
                listnum3 =0;
            }
        }

        if(position == 0 || position ==( listnum+1) || position == (listnum+listnum2+2))
        {
            return 0;
        }else if(position<=listnum+1)
        {
            return 1;
        }else if(position <= (listnum+listnum2+2))
        {
            return 2;
        }else if(position <= (listnum+listnum2+listnum3+3))
        {
            return 3;
        }else{
            return 0;
        }


    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder hd, final int position) {
        int listnum = 0;
        int listnum_tmp = 0;
        int listnum_tmp2 = 0;
        int listnum_tmp3 = 0;
        if(this.m_data1==null)
        {
            listnum =0;
        }else{
            if(m_data1_showType == 0) {
                listnum = this.m_data1.size();
            }else{
                listnum =0;
            }
            listnum_tmp = this.m_data1.size();
        }
        int listnum2 = 0;
        if(this.m_data2==null)
        {
            listnum2 =0;
        }else{

            if(m_data2_showType == 0) {
                listnum2 = this.m_data2.size();
            }else{
                listnum2 =0;
            }
            listnum_tmp2 = this.m_data2.size();
        }

        int listnum3 = 0;
        if(this.m_data3==null)
        {
            listnum3 =0;
        }else{

            if(m_data3_showType == 0) {
                listnum3 = this.m_data3.size();
            }else{
                listnum3 =0;
            }
            listnum_tmp3 = this.m_data3.size();
        }


        if(position == 0 || position ==( listnum+1) || position == (listnum+listnum2+2))
        {
            MyViewTopHolder holder = (MyViewTopHolder) hd;
            if(position== 0) {
                holder.title.setText( "未处理(" + listnum_tmp + ")");
                holder.lin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(m_data1_showType == 1)
                        {
                            m_data1_showType = 0;
                        }else{
                            m_data1_showType = 1;
                        }
                        notifyDataSetChanged();

                    }
                });
            }else if(position== listnum+1){

                holder.title.setText( "已处理(" + listnum_tmp2 + ")");
                holder.lin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(m_data2_showType == 1)
                        {
                            m_data2_showType = 0;
                        }else{
                            m_data2_showType = 1;
                        }
                        notifyDataSetChanged();

                    }
                });
            }else{

                holder.title.setText( "已开单(" + listnum_tmp3 + ")");
                holder.lin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(m_data3_showType == 1)
                        {
                            m_data3_showType = 0;
                        }else{
                            m_data3_showType = 1;
                        }
                        notifyDataSetChanged();

                    }
                });
            }
        }else {
            MyViewHolder holder = (MyViewHolder) hd;
            final RemindInfo info;
            if(position<=listnum)
            {
                info = m_data1.get(position-1);
            }else if(position<=(listnum+listnum2+2)){
                info = m_data2.get(position-listnum-2);
            }else{
                info = m_data3.get(position-listnum-listnum2-3);
            }

//            final RemindInfo _info = info;
            final ContactOrderInfo contact = new ContactOrderInfo();
            contact.confirmtome = info.confirmtime;
            contact.idfromserver = info._id;
            contact.info = info.info;
            contact.inserttime = info.inserttime;
            contact.openid=info.openid;
            contact.owner = info.owner;
            contact.state = info.state;
            contact.time = info.time;
            contact.carcode = info.carcode;
            final  ContactOrderInfo _contact = contact;

            holder.remind_item_ln.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String[] arr;

                    if("0".equalsIgnoreCase(info.state)) {
                        arr = m_activity.getResources().getStringArray(R.array.contact_order_click1);
                    }else if ("1".equalsIgnoreCase(info.state)) {
                        arr = m_activity.getResources().getStringArray(R.array.contact_order_click2);
                    }else{
                        arr = m_activity.getResources().getStringArray(R.array.contact_order_click3);
                    }

                    View outerView = LayoutInflater.from(m_activity).inflate(R.layout.wheel_view, null);
                    final WheelView wv = (WheelView) outerView.findViewById(R.id.wheel_view_wv);
                    wv.setItems(Arrays.asList(arr));
                    wv.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
                        @Override
                        public void onSelected(int selectedIndex, String item) {

                        }
                    });

                    new AlertDialog.Builder(m_activity)
                            .setTitle("请选择操作")
                            .setView(outerView)
                            .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {


                                        if(wv.getSeletedIndex() == 0){
//                                            Toast.makeText(context,"info.state:"+info.state,Toast.LENGTH_SHORT).show();
                                            if("0".equalsIgnoreCase(info.state)) {
                                                updateOrderInfo(_contact, "1");
                                            }else if("1".equalsIgnoreCase(info.state)){
                                                updateOneRepair(info);
                                            }else{
                                                showRepair(info);
                                            }
                                        }else if(wv.getSeletedIndex() == 1){
                                            delOrderInfo(_contact);
                                        }


                                }
                            })
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .show();

                }
            });


        if("1".equalsIgnoreCase(info.state))
        {
//            holder.img2.setVisibility(View.VISIBLE);
            holder.status.setVisibility(View.VISIBLE);
            holder.time2.setVisibility(View.VISIBLE);
            holder.time2.setText(info.confirmtime);
            holder.status3.setVisibility(View.GONE);
            holder.time3.setVisibility(View.GONE);
        }else if("0".equalsIgnoreCase(info.state)){
//            holder.img2.setVisibility(View.GONE);
            holder.status2.setVisibility(View.GONE);
            holder.time2.setVisibility(View.GONE);
            holder.status3.setVisibility(View.GONE);
            holder.time3.setVisibility(View.GONE);
        }else {
//            holder.img2.setVisibility(View.GONE);

            holder.time2.setVisibility(View.VISIBLE);
            holder.status2.setVisibility(View.VISIBLE);
            holder.status3.setVisibility(View.VISIBLE);
            holder.time3.setVisibility(View.VISIBLE);
            holder.time2.setText(info.confirmtime);
            holder.time3.setText(info.starttime);
        }
        holder.time1.setText(info.inserttime);
            holder.info.setText(info.info);

//        Contact con = DBService.queryContact(info.openid);

//        if(con !=null) {
            holder.name.setText(info.carcode);


            final ImageView imgHead = holder.img3;
            m_activity.imageLoader.get(info.headurl, new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {
                    imgHead.setImageBitmap(imageContainer.getBitmap());
                }

                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    imgHead.setImageResource(R.drawable.appicon);
                }
            }, 1000, 1000);
//        }
        }



    }

    @Override
    public int getItemCount() {
        int count =0;
        int listnum = 0;
        if(this.m_data1==null)
        {
            listnum =0;
        }else{
            if(m_data1_showType == 0) {
                listnum = this.m_data1.size();
            }else{
                listnum =0;
            }
        }
        int listnum2 = 0;
        if(this.m_data2==null)
        {
            listnum2 =0;
        }else{

            if(m_data2_showType == 0) {
                listnum2 = this.m_data2.size();
            }else{
                listnum2 =0;
            }
        }

        int listnum3 = 0;
        if(this.m_data3==null)
        {
            listnum3 =0;
        }else{

            if(m_data3_showType == 0) {
                listnum3 = this.m_data3.size();
            }else{
                listnum3 =0;
            }
        }


        count = listnum+listnum2+listnum3+3;

        return count;
    }
    class MyViewTopHolder extends RecyclerView.ViewHolder{
        TextView title;
        LinearLayout lin;
        public MyViewTopHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.name);
            lin = (LinearLayout) itemView.findViewById(R.id.lin);
        }
    }
    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView time1;
        TextView time2;
        TextView time3;
//        ImageView img1;
//        ImageView img2;
        TextView status;
        TextView status2;
        TextView status3;
        TextView name;
        TextView info;
        ImageView img3;
        LinearLayout remind_item_ln;
        public MyViewHolder(View itemView) {
            super(itemView);
            time1 = (TextView) itemView.findViewById(R.id.time1);
            time2 = (TextView) itemView.findViewById(R.id.time2);
            time3 = (TextView) itemView.findViewById(R.id.time3);
            name = (TextView) itemView.findViewById(R.id.name);
            info = (TextView) itemView.findViewById(R.id.info);
            img3 = (ImageView)itemView.findViewById(R.id.img3);
//            img1 = (ImageView)itemView.findViewById(R.id.img1);
//            img2 = (ImageView)itemView.findViewById(R.id.img2);
            status = (TextView)itemView.findViewById(R.id.status);
            status2 = (TextView)itemView.findViewById(R.id.status2);
            status3 = (TextView)itemView.findViewById(R.id.status3);
            remind_item_ln = (LinearLayout)itemView.findViewById(R.id.remind_item_ln);
        }
    }

    private void delOrderInfo(ContactOrderInfo info){

        Map map = new HashMap();
        map.put("id", info.idfromserver);
        showWaitView();
        HttpManager.getInstance(m_activity).getCustomerOrdersList("/contact/delOrderRepair2", map,new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        stopWaitingView();
                        if (response.optInt("code") == 1) {
                            Toast.makeText(m_activity, "删除成功", Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(m_activity, response.optString("msg"), Toast.LENGTH_SHORT).show();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        stopWaitingView();
                        Toast.makeText(m_activity, "删除失败", Toast.LENGTH_SHORT).show();
                    }
                }
        );


    }

    private void updateOrderInfo(ContactOrderInfo info,String state){

        Map map = new HashMap();
        map.put("name",info.name);
        map.put("address",LoginUserUtil.getAddress(m_activity));
        map.put("car",info.carcode);
        map.put("tel",info.tel);
        map.put("owner",LoginUserUtil.getTel(context));
        map.put("shopname", LoginUserUtil.getShopName(m_activity));
        map.put("openid", info.openid);
        map.put("id", info.idfromserver);
        map.put("confirmtime", DateUtil.timeFrom(new Date()));
        map.put("ordertime", info.time);
        map.put("orderinfo", info.info);
        map.put("state", state);

        showWaitView();
        HttpManager.getInstance(m_activity).getCustomerOrdersList("/contact/updateOrderRepair2", map,new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        stopWaitingView();
                        if (response.optInt("code") == 1) {
                            Toast.makeText(m_activity, "操作成功", Toast.LENGTH_SHORT).show();
                            m_activity.refreshData();

                        } else {
                            Toast.makeText(m_activity, response.optString("msg"), Toast.LENGTH_SHORT).show();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        Log.e(TAG, error.getMessage(), error);

                        stopWaitingView();
                        Toast.makeText(m_activity, "操作失败", Toast.LENGTH_SHORT).show();
                    }
                }
        );


    }


    private void updateOneRepair(RemindInfo info)
    {
        final RepairHistory rep =  new RepairHistory();
        rep.isAddedNewRepair = 1;
        rep.addition = "";
        rep.repairType = "";
        rep.circle = "";
        rep.totalKm = "";
        rep.isClose = "0";
        rep.isreaded = "0";
        rep.carCode = info.carcode;
        rep.contactid ="";
        rep.iswatiinginshop = "0";
        rep.customremark = "";
        rep.wantedcompletedtime = "";
        rep.entershoptime = "";
        rep.repairTime = "";




        Contact con = DBService.queryContact(info.carcode);
        if(con == null)
        {
            return;
        }

        JSONArray arrItmes = new JSONArray();
        Map cv = new HashMap();
        cv.put("carerid", info.carerid);
        cv.put("carid", info.carid);
        cv.put("shopid", info.shopid);
        cv.put("orderid", info._id);
        cv.put("contactid", con.getIdfromnode());


        cv.put("carcode", rep.carCode);
        cv.put("totalkm", rep.totalKm);
        cv.put("repairetime",rep.repairTime);
        cv.put("repairtype", rep.repairType);
        cv.put("addition", rep.addition);
        cv.put("tipcircle", rep.tipCircle);
        cv.put("circle", rep.circle);
        cv.put("isclose", rep.isClose) ;
        cv.put("isreaded", rep.isClose);
        cv.put("owner", LoginUserUtil.getTel(context));
        cv.put("id", "");
        cv.put("items", arrItmes);
//        cv.put("contactid", rep.contactid);
        cv.put("iswatiinginshop", rep.iswatiinginshop);
        cv.put("customremark", rep.customremark);
        cv.put("wantedcompletedtime", rep.wantedcompletedtime);
        cv.put("entershoptime", rep.entershoptime);

        showWaitView();
        HttpManager.getInstance(context).updateOneRepair("/repair/add5", cv, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {

                stopWaitingView();
                if(jsonObject.optInt("code") == 1){
                    Toast.makeText(context,"开始接单",Toast.LENGTH_SHORT).show();
                    rep.idfromnode = jsonObject.optJSONObject("ret").optString("_id");
                    rep.state = jsonObject.optJSONObject("ret").optString("state");
                    rep.owner = jsonObject.optJSONObject("ret").optString("owner");
                    ArrayList<ADTReapirItemInfo> arrItems = new ArrayList();
                    rep.arrRepairItems = arrItems;
                    Intent intent = new Intent(context,WorkRoomEditActivity.class);
                    intent.putExtra(String.valueOf(R.string.key_repair_edit_para), rep);
                    m_activity.startActivityForResult(intent, 1);

                    m_activity.refreshData();
                }else {
                    Toast.makeText(context,"开单失败",Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

                stopWaitingView();
                Toast.makeText(context,"开单失败",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showRepair(RemindInfo info)
    {
        Map map = new HashMap();
        map.put("orderid",info._id);

        showWaitView();
        HttpManager.getInstance(m_activity).getCustomerOrdersList("/repair/queryOneByOrderID", map,new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        stopWaitingView();
                        if (response.optInt("code") == 1) {
                            RepairHistory repFromServer = new RepairHistory();
                            JSONObject obj = response.optJSONObject("ret");

                            if(obj == null)
                            {
                                Toast.makeText(context,"数据异常",Toast.LENGTH_SHORT).show();
                                return ;
                            }
                            repFromServer.addition =obj.optString("addition").replace(" ", "");
                            repFromServer.carCode =obj.optString("carcode").replace(" ", "");
                            repFromServer.circle =obj.optString("circle");
                            repFromServer.isreaded = obj.optString("isreaded");
                            repFromServer.isClose = obj.optString("isclose");
                            repFromServer.owner =obj.optString("owner");
                            repFromServer.repairTime =obj.optString("repairetime");
                            repFromServer.repairType =obj.optString("repairtype");
                            repFromServer.tipCircle =obj.optString("tipcircle");
                            repFromServer.totalKm =obj.optString("totalkm");
                            repFromServer.idfromnode =obj.optString("_id");
                            repFromServer.inserttime =obj.optString("inserttime");
                            repFromServer.pics = obj.optString("pics");

                            repFromServer.state =obj.optString("state");
                            repFromServer.customremark =obj.optString("customremark");
                            repFromServer.wantedcompletedtime =obj.optString("wantedcompletedtime");
                            repFromServer.iswatiinginshop =obj.optString("iswatiinginshop");
                            repFromServer.entershoptime =obj.optString("entershoptime");
                            repFromServer.contactid =obj.optString("contactid");

                            repFromServer.payType =obj.optString("payType");

                            repFromServer.ownnum =obj.optString("ownnum");
                            repFromServer.saleMoney = obj.optString("saleMoney");

                            if(repFromServer.entershoptime.length()==0){
                                repFromServer.entershoptime =   repFromServer.inserttime;
                            }

                            JSONArray items = obj.optJSONArray("items");
                            ArrayList<ADTReapirItemInfo> arrItems = new ArrayList();
                            int totalPrice = 0;
                            if(items != null){
                                for(int j=0;j<items.length();j++){
                                    JSONObject itemObj = items.optJSONObject(j);
                                    ADTReapirItemInfo item = ADTReapirItemInfo.fromWithJsonObj(itemObj);
                                    totalPrice+=item.currentPrice;

                                    arrItems.add(item);
                                }
                            }

                            repFromServer.arrRepairItems = arrItems;
                            repFromServer.totalPrice = String.valueOf(totalPrice);


                            Intent intent = new Intent(context,WorkRoomEditActivity.class);
                            intent.putExtra(String.valueOf(R.string.key_repair_edit_para), repFromServer);
                            m_activity.startActivityForResult(intent, 1);
                            m_activity.refreshData();

                        } else {
                            Toast.makeText(m_activity, response.optString("msg"), Toast.LENGTH_SHORT).show();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        Log.e(TAG, error.getMessage(), error);

                        stopWaitingView();
                        Toast.makeText(m_activity, "操作失败", Toast.LENGTH_SHORT).show();
                    }
                }
        );

    }



    public void showWaitView() {

        LoadingDialog.make(m_activity).show();

    }

    public void stopWaitingView() {
        LoadingDialog.cancel();
    }
}




