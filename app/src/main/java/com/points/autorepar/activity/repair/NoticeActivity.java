package com.points.autorepar.activity.repair;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.necer.ncalendar.calendar.NCalendar;
import com.necer.ncalendar.listener.OnCalendarChangedListener;
import com.necer.ncalendar.utils.MyLog;
import com.points.autorepar.R;
import com.points.autorepar.activity.BaseActivity;
import com.points.autorepar.adapter.Noticedapter;
import com.points.autorepar.adapter.Reminddapter;
import com.points.autorepar.bean.ADTReapirItemInfo;
import com.points.autorepar.bean.Contact;
import com.points.autorepar.bean.ContactOrderInfo;
import com.points.autorepar.bean.RemindInfo;
import com.points.autorepar.bean.RepairHistory;
import com.points.autorepar.http.HttpManager;
import com.points.autorepar.sql.DBService;
import com.points.autorepar.utils.JSONOejectUtil;
import com.points.autorepar.utils.LoginUserUtil;

import org.joda.time.LocalDate;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NoticeActivity extends BaseActivity implements OnCalendarChangedListener {


    private NCalendar ncalendar;
    private RecyclerView recyclerView;
    private TextView tv_month;

    private TextView tv_date;

    private int type;

    private  String m_month;
    private Noticedapter aaAdapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getSupportActionBar().hide();
        setContentView(R.layout.activity_remind);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
            localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
        }
        Button mBackBtn    = (Button)findViewById(R.id.common_navi_back);
        mBackBtn.setVisibility(View.GONE);
        Button mAddBtn    = (Button)findViewById(R.id.common_navi_add);
        mAddBtn.setVisibility(View.GONE);
        mAddBtn.setText("全部");

        TextView mTitle      = (TextView)findViewById(R.id.common_navi_title);
        mTitle.setText("到期提醒");
        m_month ="";
        ncalendar = (NCalendar) findViewById(R.id.ncalendarrrr);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        tv_month = (TextView) findViewById(R.id.tv_month);
        tv_date = (TextView) findViewById(R.id.tv_date);

        type = 1;
     //   ncalendar.setDateInterval("2017-04-02","2018-01-01");



        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        aaAdapter = new Noticedapter(this,this);
        recyclerView.setAdapter(aaAdapter);
        ncalendar.setOnCalendarChangedListener(this);

//        ncalendar.post(new Runnable() {
//            @Override
//            public void run() {
//
//                List<String> list = new ArrayList<>();
//                list.add("2017-09-21");
//                list.add("2017-10-21");
//                list.add("2017-10-1");
//                list.add("2017-10-15");
//                list.add("2017-10-18");
//                list.add("2017-10-26");
//                list.add("2017-11-21");
//
//                ncalendar.setPoint(list);
//            }
//        });
    }


//    @Override
//    public void onCalendarChanged(LocalDate date) {
//        tv_month.setText(date.getMonthOfYear() + "月");
//        tv_date.setText(date.getYear() + "年" + date.getMonthOfYear() + "月" + date.getDayOfMonth() + "日");
//
//        MyLog.d("dateTime::" + date);
//    }


    public void setDate(View view) {
        ncalendar.setDate("2017-12-31");
    }

    public void toMonth(View view) {
        ncalendar.toMonth();
    }

    public void toWeek(View view) {
        ncalendar.toWeek();
    }

    public void toToday(View view) {
        ncalendar.toToday();
    }

    public void toNextPager(View view) {
        ncalendar.toNextPager();
    }

    public void toLastPager(View view) {
        ncalendar.toLastPager();
    }




    @Override
    public void onCalendarChanged(LocalDate date) {

        tv_month.setText(date.getMonthOfYear() + "月");
        tv_date.setText(date.getYear() + "年" + date.getMonthOfYear() + "月" + date.getDayOfMonth() + "日");

        MyLog.d("dateTime::" + date);
        String month_str = date.getMonthOfYear()+"";
        if(date.getMonthOfYear()<10)
        {
            month_str = "0"+month_str;
        }

        String day_str = date.getDayOfMonth()+"";
        if(date.getDayOfMonth()<10)
        {
            day_str = "0"+day_str;
        }

        String curdate = date.getYear()+"-"+month_str+"-"+day_str;
        String curmonth = date.getYear()+"-"+month_str;
        getData1(curdate);
        getData2(curdate);
        getData3(curdate);
        getData4(curdate);
        if(!m_month.equalsIgnoreCase(curmonth))
        {
            m_month = curmonth;
            getPointData(m_month);
        }
    }


    private void getPointData(String curDate){
        Map map = new HashMap();
        map.put("owner", LoginUserUtil.getTel(this));
        map.put("start", curDate+"-01");
        map.put("end", curDate+"-31");

        String url = "";

        url = "/repairstatistics/getAllDeadlinesMonthRedNum";



        HttpManager.getInstance(NoticeActivity.this)
                .queryCustomerOrders(url, map, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {


                        if(jsonObject.optInt("code") == 1){


//                            JSONObject ret = jsonObject.optJSONObject("ret");
                            JSONArray arr = jsonObject.optJSONArray("ret");
                            List<String> list = new ArrayList<>();
                            if (arr.length() > 0) {

                                for(int i=0;i<arr.length();i++)
                                {
                                    JSONObject obj = arr.optJSONObject(i);
                                    String date = obj.optString("day");
                                    String count = obj.optString("count");
                                    if(!"0".equalsIgnoreCase(count))
                                    {
                                        list.add(date);
                                    }
                                }
                                final List<String> listtmp = list;
                                ncalendar.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            ncalendar.setPoint(listtmp);
                                        }
                                    });
                            }

                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        int i = 0;
                    }
                });
    }

    private void getData1(String curDate){
        Map map = new HashMap();
        map.put("owner", LoginUserUtil.getTel(this));
        map.put("day", curDate);

        String url = "";
        url = "/repair/queryTipedRepaisOneDay";



        HttpManager.getInstance(NoticeActivity.this)
                .queryCustomerOrders(url, map, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {


                        if(jsonObject.optInt("code") == 1){

                            ArrayList<RepairHistory> arrRep = new ArrayList();
                            JSONArray arr = jsonObject.optJSONArray("ret");
                            if (arr.length() > 0) {


                                for (int i = 0; i < arr.length(); i++) {
                                    RepairHistory repFromServer = new RepairHistory();
                                    JSONObject obj = arr.optJSONObject(i);
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
                                    repFromServer.saleMoney = obj.optString("saleMoney");

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
                                    arrRep.add(repFromServer);
                                }

                            }
                            aaAdapter.setData1(arrRep);
                            aaAdapter.notifyDataSetChanged();

                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        int i = 0;
                    }
                });
    }

    private void getData2(String curDate){
        Map map = new HashMap();
        map.put("owner", LoginUserUtil.getTel(this));
        map.put("day", curDate);

        String url = "";
        url = "/contact/queryAllYearCheckTipedOneDay";



        HttpManager.getInstance(NoticeActivity.this)
                .queryCustomerOrders(url, map, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {


                        try {
                            if (jsonObject.optInt("code") == 1) {
                                ArrayList<Contact> arrRep = new ArrayList();
                                JSONArray arr = jsonObject.optJSONArray("ret");

                                if (arr.length() > 0) {


                                    for (int i = 0; i < arr.length(); i++) {
                                        Contact conFromServer = new Contact();
                                        JSONObject obj = (JSONObject) arr.get(i);
                                        conFromServer.setCarCode(obj.optString("carcode").replace(" ", ""));
                                        conFromServer.setOwner(obj.optString("owner"));
                                        conFromServer.setCarType(obj.optString("cartype"));
                                        conFromServer.setName(obj.optString("name"));
                                        conFromServer.setTel(obj.optString("tel"));
                                        conFromServer.setIdfromnode(obj.optString("_id"));

                                        conFromServer.setInserttime(JSONOejectUtil.optString(obj, "inserttime"));
                                        conFromServer.setIsbindweixin(JSONOejectUtil.optString(obj, "isbindweixin"));
                                        conFromServer.setWeixinopenid(JSONOejectUtil.optString(obj, "weixinopenid"));
                                        conFromServer.setVin(JSONOejectUtil.optString(obj, "vin"));
                                        conFromServer.setCarregistertime(JSONOejectUtil.optString(obj, "carregistertime"));
                                        conFromServer.setHeadurl(JSONOejectUtil.optString(obj, "headurl"));


                                        conFromServer.setSafecompany(JSONOejectUtil.optString(obj, "safecompany"));
                                        conFromServer.setSafenexttime(JSONOejectUtil.optString(obj, "safenexttime"));
                                        conFromServer.setYearchecknexttime(JSONOejectUtil.optString(obj, "yearchecknexttime"));
                                        conFromServer.setTqTime1(JSONOejectUtil.optString(obj, "tqTime1"));
                                        conFromServer.setTqTime2(JSONOejectUtil.optString(obj, "tqTime2"));
                                        conFromServer.setCar_key(JSONOejectUtil.optString(obj, "carId"));

                                        conFromServer.setIsVip(JSONOejectUtil.optString(obj, "IsVip"));
                                        conFromServer.setCarId(JSONOejectUtil.optString(obj, "Car_key"));

                                        conFromServer.setSafecompany3(JSONOejectUtil.optString(obj, "safecompany3"));
                                        conFromServer.setTqTime3(JSONOejectUtil.optString(obj, "tqTime3"));
                                        conFromServer.setSafenexttime3(JSONOejectUtil.optString(obj, "safenexttime3"));
                                        conFromServer.setSafetiptime3(JSONOejectUtil.optString(obj, "safetiptime3"));


                                        arrRep.add(conFromServer);
                                    }

                                }
                                aaAdapter.setData2(arrRep);
                                aaAdapter.notifyDataSetChanged();
                            }
                        }catch (Exception e)
                        {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        int i = 0;
                    }
                });
    }
    private void getData3(String curDate){
        Map map = new HashMap();
        map.put("owner", LoginUserUtil.getTel(this));
        map.put("day", curDate);

        String url = "";
        url = "/contact/queryAllSafeTipedOneDay";



        HttpManager.getInstance(NoticeActivity.this)
                .queryCustomerOrders(url, map, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {

                try {
                    if (jsonObject.optInt("code") == 1) {

                        ArrayList<Contact> arrRep = new ArrayList();
                        JSONArray arr = jsonObject.optJSONArray("ret");
                        if (arr.length() > 0) {


                            for (int i = 0; i < arr.length(); i++) {
                                Contact conFromServer = new Contact();
                                JSONObject obj = (JSONObject) arr.get(i);
                                conFromServer.setCarCode(obj.optString("carcode").replace(" ", ""));
                                conFromServer.setOwner(obj.optString("owner"));
                                conFromServer.setCarType(obj.optString("cartype"));
                                conFromServer.setName(obj.optString("name"));
                                conFromServer.setTel(obj.optString("tel"));
                                conFromServer.setIdfromnode(obj.optString("_id"));

                                conFromServer.setInserttime(JSONOejectUtil.optString(obj, "inserttime"));
                                conFromServer.setIsbindweixin(JSONOejectUtil.optString(obj, "isbindweixin"));
                                conFromServer.setWeixinopenid(JSONOejectUtil.optString(obj, "weixinopenid"));
                                conFromServer.setVin(JSONOejectUtil.optString(obj, "vin"));
                                conFromServer.setCarregistertime(JSONOejectUtil.optString(obj, "carregistertime"));
                                conFromServer.setHeadurl(JSONOejectUtil.optString(obj, "headurl"));


                                conFromServer.setSafecompany(JSONOejectUtil.optString(obj, "safecompany"));
                                conFromServer.setSafenexttime(JSONOejectUtil.optString(obj, "safenexttime"));
                                conFromServer.setYearchecknexttime(JSONOejectUtil.optString(obj, "yearchecknexttime"));
                                conFromServer.setTqTime1(JSONOejectUtil.optString(obj, "tqTime1"));
                                conFromServer.setTqTime2(JSONOejectUtil.optString(obj, "tqTime2"));
                                conFromServer.setCar_key(JSONOejectUtil.optString(obj, "carId"));

                                conFromServer.setIsVip(JSONOejectUtil.optString(obj, "IsVip"));
                                conFromServer.setCarId(JSONOejectUtil.optString(obj, "Car_key"));

                                conFromServer.setSafecompany3(JSONOejectUtil.optString(obj, "safecompany3"));
                                conFromServer.setTqTime3(JSONOejectUtil.optString(obj, "tqTime3"));
                                conFromServer.setSafenexttime3(JSONOejectUtil.optString(obj, "safenexttime3"));
                                conFromServer.setSafetiptime3(JSONOejectUtil.optString(obj, "safetiptime3"));
                                arrRep.add(conFromServer);
                            }

                        }
                        aaAdapter.setData3(arrRep);
                        aaAdapter.notifyDataSetChanged();
                    }
                }catch (Exception e)
                {
                    e.printStackTrace();
                }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        int i = 0;
                    }
                });
    }
    private void getData4(String curDate){
        Map map = new HashMap();
        map.put("owner", LoginUserUtil.getTel(this));
        map.put("day", curDate);

        String url = "";
        url = "/contact/queryAllJQSafeTipedOneDay";
        HttpManager.getInstance(NoticeActivity.this)
                .queryCustomerOrders(url, map, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {

                        try {
                            if (jsonObject.optInt("code") == 1) {

                                ArrayList<Contact> arrRep = new ArrayList();
                                JSONArray arr = jsonObject.optJSONArray("ret");
                                if (arr.length() > 0) {

                                    for (int i = 0; i < arr.length(); i++) {
                                        Contact conFromServer = new Contact();
                                        JSONObject obj = (JSONObject) arr.get(i);
                                        conFromServer.setCarCode(obj.optString("carcode").replace(" ", ""));
                                        conFromServer.setOwner(obj.optString("owner"));
                                        conFromServer.setCarType(obj.optString("cartype"));
                                        conFromServer.setName(obj.optString("name"));
                                        conFromServer.setTel(obj.optString("tel"));
                                        conFromServer.setIdfromnode(obj.optString("_id"));

                                        conFromServer.setInserttime(JSONOejectUtil.optString(obj, "inserttime"));
                                        conFromServer.setIsbindweixin(JSONOejectUtil.optString(obj, "isbindweixin"));
                                        conFromServer.setWeixinopenid(JSONOejectUtil.optString(obj, "weixinopenid"));
                                        conFromServer.setVin(JSONOejectUtil.optString(obj, "vin"));
                                        conFromServer.setCarregistertime(JSONOejectUtil.optString(obj, "carregistertime"));
                                        conFromServer.setHeadurl(JSONOejectUtil.optString(obj, "headurl"));


                                        conFromServer.setSafecompany(JSONOejectUtil.optString(obj, "safecompany"));
                                        conFromServer.setSafenexttime(JSONOejectUtil.optString(obj, "safenexttime"));
                                        conFromServer.setYearchecknexttime(JSONOejectUtil.optString(obj, "yearchecknexttime"));
                                        conFromServer.setTqTime1(JSONOejectUtil.optString(obj, "tqTime1"));
                                        conFromServer.setTqTime2(JSONOejectUtil.optString(obj, "tqTime2"));
                                        conFromServer.setCar_key(JSONOejectUtil.optString(obj, "carId"));

                                        conFromServer.setIsVip(JSONOejectUtil.optString(obj, "IsVip"));
                                        conFromServer.setCarId(JSONOejectUtil.optString(obj, "Car_key"));

                                        conFromServer.setSafecompany3(JSONOejectUtil.optString(obj, "safecompany3"));
                                        conFromServer.setTqTime3(JSONOejectUtil.optString(obj, "tqTime3"));
                                        conFromServer.setSafenexttime3(JSONOejectUtil.optString(obj, "safenexttime3"));
                                        conFromServer.setSafetiptime3(JSONOejectUtil.optString(obj, "safetiptime3"));
                                        arrRep.add(conFromServer);
                                    }
                                }
                                aaAdapter.setData4(arrRep);
                                aaAdapter.notifyDataSetChanged();
                            }
                        }catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        int i = 0;
                    }
                });
    }
}
