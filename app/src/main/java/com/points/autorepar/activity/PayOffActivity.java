package com.points.autorepar.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.allen.library.SuperTextView;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.points.autorepar.R;
import com.points.autorepar.bean.ADTReapirItemInfo;
import com.points.autorepar.bean.Contact;
import com.points.autorepar.bean.RepairHistory;
import com.points.autorepar.http.HttpManager;
import com.points.autorepar.sql.DBService;
import com.points.autorepar.utils.DateUtil;
import com.points.autorepar.utils.LoginUserUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by allen on 2016/10/19.
 */
public class PayOffActivity extends BaseActivity {

    private int m_type;
    private RepairHistory m_data;
    private SuperTextView tv1, tv2,tv3, pay_stv1, pay_stv2, pay_stv3,pay_stv4;

    private ImageView img1,img2,img3,img4;
    private EditText ed_discount,edit_nopaynum;
    private LinearLayout lin1,lin2,lin3,lin4;
    int i_discount,total,i_nopaynum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();

//设置透明状态栏,这样才能让 ContentView 向上
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

//需要设置这个 flag 才能调用 setStatusBarColor 来设置状态栏颜色
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        setContentView(R.layout.activity_payoff);
        m_data = getIntent().getParcelableExtra("data");

        initView();

        total = 0;
        for(int i=0;i<m_data.arrRepairItems.size();i++)
        {
            ADTReapirItemInfo info=m_data.arrRepairItems.get(i);
            if(info.workhourpay !=null)
            {
                total = total + info.currentPrice;
            }
        }


//        total = Integer.parseInt(m_data.totalPrice);

        i_discount = 0;
        i_nopaynum = 0;

        m_type = 1;
        tv1.setRightString(m_data.idfromnode+"");
        tv2.setRightString("￥"+total);


        int Val = total -i_discount-i_nopaynum;
        tv3.setRightString("￥"+Val);
    }

    private void initView() {
        Button common_navi_back = (Button)findViewById(R.id.common_navi_back);
        common_navi_back.setVisibility(View.GONE);
        Button common_navi_add = (Button)findViewById(R.id.common_navi_add);
        common_navi_add.setVisibility(View.GONE);
        TextView title = (TextView)findViewById(R.id.common_navi_title);
        title.setText("支付");
        tv1 = (SuperTextView) findViewById(R.id.tv1);
        tv2 = (SuperTextView) findViewById(R.id.tv2);
        tv3 = (SuperTextView) findViewById(R.id.tv3);

        img1 = (ImageView) findViewById(R.id.img_1);
        img2 = (ImageView) findViewById(R.id.img_2);
        img3 = (ImageView) findViewById(R.id.img_3);
        img4 = (ImageView) findViewById(R.id.img_4);
        setPayType(1);

        lin1 = (LinearLayout) findViewById(R.id.lin1);
        lin2 = (LinearLayout) findViewById(R.id.lin2);
        lin3 = (LinearLayout) findViewById(R.id.lin3);
        lin4 = (LinearLayout) findViewById(R.id.lin4);

        lin1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPayType(1);
                int Val = total -i_discount-i_nopaynum;
                tv3.setRightString("￥"+Val);
                ed_discount.setVisibility(View.VISIBLE);
                edit_nopaynum.setVisibility(View.VISIBLE);
            }
        });

        lin2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Contact con = DBService.queryContactCode(m_data.contactid);
                if (con != null) {
                    if(!"1".equalsIgnoreCase(con.getisVip()))
                    {
                        Toast.makeText(PayOffActivity.this, "该客户还未办理会员,无法使用会员卡支付", Toast.LENGTH_LONG).show();
                        return;
                    }
                }else{
                    Toast.makeText(PayOffActivity.this,"该客户不存在",Toast.LENGTH_LONG).show();
                    return;
                }
                ed_discount.setVisibility(View.INVISIBLE);
                edit_nopaynum.setVisibility(View.INVISIBLE);
                tv3.setRightString("￥0");
                setPayType(2);

            }
        });

        lin3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPayType(3);
                int Val = total -i_discount-i_nopaynum;
                tv3.setRightString("￥"+Val);
                ed_discount.setVisibility(View.VISIBLE);
                edit_nopaynum.setVisibility(View.VISIBLE);
            }
        });

        lin4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPayType(4);
                int Val = total -i_discount-i_nopaynum;
                tv3.setRightString("￥"+Val);
                ed_discount.setVisibility(View.VISIBLE);
                edit_nopaynum.setVisibility(View.VISIBLE);
            }
        });

        Button bt_pay = (Button) findViewById(R.id.bt_pay);
        bt_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(m_type != 2) {
                    commitRepair();
                }else{
                    commitVip();
                }
            }
        });



        ed_discount = (EditText) findViewById(R.id.ed_discount);
        ed_discount.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().length() == 0){
                    Toast.makeText(PayOffActivity.this,"请填写数字",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!LoginUserUtil.isNumeric(s.toString())){
                    Toast.makeText(PayOffActivity.this,"请填写数字",Toast.LENGTH_SHORT).show();
                    return;
                }
                int Val= Integer.parseInt(s.toString());
                i_discount = Val;
                Val = total -i_discount-i_nopaynum;
                tv3.setRightString("￥"+Val);

            }
        });
        edit_nopaynum =  (EditText) findViewById(R.id.edit_nopaynum);
        edit_nopaynum.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().length() == 0){
                    Toast.makeText(PayOffActivity.this,"请填写数字",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!LoginUserUtil.isNumeric(s.toString())){
                    Toast.makeText(PayOffActivity.this,"请填写数字",Toast.LENGTH_SHORT).show();
                    return;
                }

                int Val= Integer.parseInt(s.toString());
                i_nopaynum = Val;
                Val = total -i_discount-i_nopaynum;
                tv3.setRightString("￥"+Val);

            }
        });
    }

    void setPayType(int type)
    {
        img1.setVisibility(View.INVISIBLE);
        img2.setVisibility(View.INVISIBLE);
        img3.setVisibility(View.INVISIBLE);
        img4.setVisibility(View.INVISIBLE);

        m_type = type;
        switch (type)
        {
            case 1:
                img1.setVisibility(View.VISIBLE);
                break;
            case 2:
                img2.setVisibility(View.VISIBLE);
                break;
            case 3:
                img3.setVisibility(View.VISIBLE);
                break;
            case 4:
                img4.setVisibility(View.VISIBLE);
                break;
        }
    }

    public void commitVip(){

        JSONArray list = new JSONArray();
        JSONObject selectmap = null;
        int totalMoney = 0;
        for(int i=0;i<m_data.arrRepairItems.size();i++){
            ADTReapirItemInfo _item = m_data.arrRepairItems.get(i);
            if(_item != null){
                if(_item.itemtype != null){
                    if(_item.itemtype.equals("1")){
                        selectmap = new JSONObject();
                        try {
                            selectmap.put("id", _item.service);
                            selectmap.put("num", _item.num);
                            selectmap.put("contactId", m_data.contactid);
                        }catch (Exception e )
                        {
                            e.printStackTrace();
                        }
                        list.put(selectmap);
                    }else {
                        totalMoney+=_item.currentPrice;
                    }
                }
            }
        }

        DateFormat fmt =new SimpleDateFormat("yyyy-MM-dd");
        try {

            String realCommitTime =  DateUtil.timeFrom(new Date());
            m_data.wantedcompletedtime =  realCommitTime;
//            Date date = fmt.parse(m_data.repairTime.toString());
            Date date = fmt.parse(realCommitTime);
            Calendar calendar   =   new GregorianCalendar();
            calendar.setTime(date);
            calendar.add(calendar.DATE, Integer.valueOf(m_data.circle.toString()));//把日期往后增加一天.整数往后推,负数往前移动
            date = calendar.getTime();   //这个时间就是日期往后推一天的结果

            Contact con = DBService.queryContactCode(m_data.contactid);
            String finalTime = fmt.format(date);
            m_data.tipCircle = finalTime;

            Map cv = new HashMap();
            cv.put("cardId", con.getIdfromnode());
            cv.put("totalMoney", String.valueOf(totalMoney));
            cv.put("saleMoney", String.valueOf(i_discount));
            cv.put("owner", LoginUserUtil.getTel(PayOffActivity.this));
            cv.put("services", list);
            showWaitView();
            HttpManager.getInstance(PayOffActivity.this).updateOneRepair("/vipcard/payRepairByVipCard", cv, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {

                    stopWaitingView();
                    if(jsonObject.optInt("code") == 1){
                        Toast.makeText(PayOffActivity.this,"提交成功",Toast.LENGTH_SHORT).show();
                        commitRepair();
//                        finish();
                    }else {
                        Toast.makeText(PayOffActivity.this,jsonObject.optString("msg"),Toast.LENGTH_SHORT).show();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {

                    stopWaitingView();
                    Toast.makeText(PayOffActivity.this,"提交失败",Toast.LENGTH_SHORT).show();
                }
            });

        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }


    }


    public void commitRepair(){

        JSONArray list = new JSONArray();
        JSONObject selectmap = null;
        for(int i=0;i<m_data.arrRepairItems.size();i++){
            ADTReapirItemInfo _item = m_data.arrRepairItems.get(i);
            if(_item != null){
                if(_item.itemtype != null){
                    list.put(_item.idfromnode);
                }

            }

        }
        DateFormat fmt =new SimpleDateFormat("yyyy-MM-dd");
        try {

            String realCommitTime =  DateUtil.timeFrom(new Date());
            m_data.wantedcompletedtime =  realCommitTime;
            Date date = fmt.parse(realCommitTime);
            Calendar calendar   =   new GregorianCalendar();
            calendar.setTime(date);
            calendar.add(calendar.DATE, Integer.valueOf(m_data.circle.toString()));//把日期往后增加一天.整数往后推,负数往前移动
            date = calendar.getTime();   //这个时间就是日期往后推一天的结果

            String finalTime = fmt.format(date);
            m_data.tipCircle = finalTime;

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

            cv.put("saleMoney",i_discount+"");
            cv.put("ownnum",i_nopaynum+"");
            //,0线下(现金) 1会员卡支付 2微信 3支付宝
            cv.put("payType",m_type-1);
            cv.put("owner", LoginUserUtil.getTel(PayOffActivity.this));
            cv.put("id", m_data.idfromnode);
            cv.put("inserttime", m_data.inserttime);
            cv.put("items", list);
            cv.put("state", "2");
            cv.put("wantedcompletedtime", m_data.wantedcompletedtime);
            cv.put("customremark", m_data.customremark);
            cv.put("iswatiinginshop", m_data.iswatiinginshop);
            cv.put("entershoptime", m_data.entershoptime);
            cv.put("contactid", m_data.contactid);
            cv.put("pics",m_data.pics);
            cv.put("oilvolume",m_data.oilvolume);
            cv.put("nexttipkm",m_data.nexttipkm);
            showWaitView();
            HttpManager.getInstance(PayOffActivity.this).updateOneRepair("/repair/update5", cv, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {

                    stopWaitingView();
                    if(jsonObject.optInt("code") == 1){
                        Toast.makeText(PayOffActivity.this,"提交成功",Toast.LENGTH_SHORT).show();
                        setResult(1, null);
                        finish();
                    }else {
                        Toast.makeText(PayOffActivity.this,"提交失败",Toast.LENGTH_SHORT).show();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {

                    stopWaitingView();
                    Toast.makeText(PayOffActivity.this,"提交失败",Toast.LENGTH_SHORT).show();
                }
            });

        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }


    }


}
