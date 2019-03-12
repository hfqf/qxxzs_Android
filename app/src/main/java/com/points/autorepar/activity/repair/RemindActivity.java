package com.points.autorepar.activity.repair;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.necer.ncalendar.calendar.NCalendar;
import com.necer.ncalendar.listener.OnCalendarChangedListener;
import com.necer.ncalendar.utils.MyLog;
import com.points.autorepar.MainApplication;
import com.points.autorepar.R;
import com.points.autorepar.activity.BaseActivity;
import com.points.autorepar.activity.CommonWebviewActivity;
import com.points.autorepar.activity.contact.ContactAddNewActivity;
import com.points.autorepar.adapter.Reminddapter;
import com.points.autorepar.bean.GoodsItemInfo;
import com.points.autorepar.bean.RemindInfo;
import com.points.autorepar.http.HttpManager;
import com.points.autorepar.utils.LoginUserUtil;

import org.json.JSONArray;
import org.json.JSONObject;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.LocalDate;

public class RemindActivity extends BaseActivity implements OnCalendarChangedListener {


    private NCalendar ncalendar;
    private RecyclerView recyclerView;
    private TextView tv_month;
    private TextView tv_date;

    private  String m_month;
    private  Reminddapter aaAdapter;

    private String lastTime;


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

        mAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });

        lastTime = "";

        TextView mTitle      = (TextView)findViewById(R.id.common_navi_title);
        mTitle.setText("预约单");
        m_month ="";
        ncalendar = (NCalendar) findViewById(R.id.ncalendarrrr);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        tv_month = (TextView) findViewById(R.id.tv_month);
        tv_date = (TextView) findViewById(R.id.tv_date);


     //   ncalendar.setDateInterval("2017-04-02","2018-01-01");



        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        aaAdapter = new Reminddapter(this,this);
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
        getData(curdate);

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

        url = "/repairstatistics/getOrderRepairMonthRedNum";



        HttpManager.getInstance(RemindActivity.this)
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

    private void getData(String curDate){

        lastTime = curDate;
        Map map = new HashMap();
        map.put("owner", LoginUserUtil.getTel(this));
        map.put("day", curDate);

        String url = "";
        url = "/contact/getOrderRepairList3";



        HttpManager.getInstance(RemindActivity.this)
                .queryCustomerOrders(url, map, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {


                        if(jsonObject.optInt("code") == 1){


                            JSONArray arr = jsonObject.optJSONArray("ret");
                            if (arr.length() > 0) {

                                ArrayList<RemindInfo> arrRep1 = new ArrayList();
                                ArrayList<RemindInfo> arrRep2 = new ArrayList();
                                ArrayList<RemindInfo> arrRep3 = new ArrayList();
                                for (int i = 0; i < arr.length(); i++) {
                                    RemindInfo info = RemindInfo.fromWithJsonObj(arr.optJSONObject(i));
                                    if("1".equalsIgnoreCase(info.state)) {
                                        arrRep2.add(info);
                                    }else if("0".equalsIgnoreCase(info.state)){
                                        arrRep1.add(info);
                                    }else{
                                        arrRep3.add(info);
                                    }
                                }
                                aaAdapter.setData1(arrRep1);
                                aaAdapter.setData2(arrRep2);
                                aaAdapter.setData3(arrRep3);
                                aaAdapter.notifyDataSetChanged();
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

    public void refreshData()
    {
        getData(lastTime);
    }
}
