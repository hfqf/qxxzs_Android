package com.points.autorepar.activity.workroom;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v13.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.points.autorepar.MainApplication;
import com.points.autorepar.R;
import com.points.autorepar.activity.PayOffActivity;
import com.points.autorepar.activity.contact.ContactInfoEditActivity;
import com.points.autorepar.activity.serviceManager.SelectServiceCategoryActivity;
import com.points.autorepar.adapter.WorkRoomEditInfoFragmentPagerAdapter;
import com.points.autorepar.bean.ADTReapirItemInfo;
import com.points.autorepar.bean.Contact;
import com.points.autorepar.bean.GoodsItemInfo;
import com.points.autorepar.bean.RefEvent;
import com.points.autorepar.bean.RepairHistory;
import com.points.autorepar.bean.WorkRoomEvent;
import com.points.autorepar.bean.WorkRoomPicBackEvent;
import com.points.autorepar.bean.WorkRoomPicEvent;
import com.points.autorepar.fragment.WorkRoomCarInfoFragment;
import com.points.autorepar.fragment.WorkRoomRepairItemsFragment;
import com.points.autorepar.http.HttpManager;
import com.points.autorepar.lib.wheelview.WheelView;
import com.points.autorepar.activity.BaseActivity;
import com.points.autorepar.sql.DBService;
import com.points.autorepar.utils.DateUtil;
import com.points.autorepar.utils.LoginUserUtil;


import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import de.greenrobot.event.EventBus;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

public class WorkRoomEditActivity extends BaseActivity  implements WorkRoomCarInfoFragment.OnWorkRoomCarInfoFragmentInteractionListener,WorkRoomRepairItemsFragment.OnWorkRoomRepairItemsFragmentInteractionListener{

    private static final String key_sp  = "points";
    private  final  String  TAG = "WorkRoomEditActivity";
    private Button mBackBtn;
    private Button mAddBtn,common_navi_bt;
    private TextView mTitle;

    private EditText mTelText;
    private EditText mPwdText;
    private EditText mConfirmText;

    private String isdirectadditem;

    private RepairHistory m_currentData;
    private WorkRoomEditActivity m_this;

    private ViewPager    m_viewPager;
    WorkRoomEditInfoFragmentPagerAdapter m_fragmentAdapter;
    private  Button    m_leftBtn;
    private  Button    m_rightBtn;
    private  View      m_indexIcon;
    private TextView   m_totalPrice;
    private  Button    m_commitBtn;
    private  Button    m_saveBtn;
    private LinearLayout m_bottom;

    private void rereshIndexLocation(int  index){


        Display display = getWindow().getWindowManager().getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        display.getMetrics(dm);
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) m_indexIcon.getLayoutParams();
        lp.leftMargin = index * (dm.widthPixels /2);
        lp.width = (dm.widthPixels /2);;//这里动态设置光标的长度，以便适配不同的分辨率
        m_indexIcon.setLayoutParams(lp);

    }

    @Override
    public  void  onResume(){
        super.onResume();
        Log.e("Contact", "onResume");
        m_fragmentAdapter.notifyDataSetChanged();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public void onEventMainThread(WorkRoomEvent event) {
        RepairHistory data = event.getMsg();
        m_currentData = data;
        updateRepair(m_currentData);
        refreshBottom();
    }


    /**
     * 只在增加时手动设置，删除时后台处理
     * 修改仓库里的数量
     * @param isOut
     */
    private void  updatestorenum(final boolean isOut){
        for(int i=0;i<m_currentData.arrRepairItems.size();i++){
            final ADTReapirItemInfo item = m_currentData.arrRepairItems.get(i);
            if(item != null){
                if(item.itemtype != null){
                    if(!item.itemtype.equals("0")){
                        return;
                    }
                }
            }

            Map map = new HashMap();
            map.put("num", item.num);
            map.put("id", item.goodsId);
            map.put("isout", isOut?"1":"0");
            String url = "/warehousegoods/updatestorenum";
            HttpManager.getInstance(WorkRoomEditActivity.this)
                    .queryAllTipedRepair(url, map, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject jsonObject) {
                            if(jsonObject.optInt("code") == 1){
                                addNewGoodsInOutRecoedeWith(item,isOut);
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {

                        }
                    });
        }
    }

    /**
     * 增加仓库操作记录
     * @param item
     * @param isOut
     */
    private void  addNewGoodsInOutRecoedeWith(ADTReapirItemInfo item,boolean isOut){
        for(int i=0;i<m_currentData.arrRepairItems.size();i++){
            Map map = new HashMap();
            map.put("num", item.num);
            map.put("owner", LoginUserUtil.getTel(WorkRoomEditActivity.this));
            map.put("dealer", LoginUserUtil.getUserId(WorkRoomEditActivity.this));
            map.put("type", isOut?"2":"3");
            map.put("remark", "");
            map.put("goods", item.goodsId);
            String url = "/warehousegoodsinoutrecord/add";
            HttpManager.getInstance(WorkRoomEditActivity.this)
                    .queryAllTipedRepair(url, map, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject jsonObject) {
                            if(jsonObject.optInt("code") == 1){

                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {

                        }
                    });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        m_this = this;
        m_currentData = getIntent().getParcelableExtra(String.valueOf(R.string.key_repair_edit_para));
        if(m_currentData.entershoptime != null) {
            if (m_currentData.entershoptime.length() == 0) {
                m_currentData.entershoptime = DateUtil.timeFrom(new Date());
                m_currentData.repairTime = m_currentData.entershoptime.substring(0, 10);
            } else {
                m_currentData.repairTime = m_currentData.entershoptime.substring(0, 10);
            }
        }else{
            m_currentData.repairTime = "";
        }
        if(m_currentData.wantedcompletedtime == null || "".equalsIgnoreCase(m_currentData.wantedcompletedtime)) {
            m_currentData.wantedcompletedtime = DateUtil.timeFrom(new Date());
        }
        if(m_currentData.circle == null || "".equalsIgnoreCase(m_currentData.circle)) {
            m_currentData.circle = "1";
        }

        String key_isdirectadditem=  getApplicationContext().getResources().getString(R.string.key_loginer_isdirectadditem);
        SharedPreferences sp = getSharedPreferences(key_sp, Context.MODE_PRIVATE);
        isdirectadditem = sp.getString(key_isdirectadditem, null);



        //TODO 为了兼容3.2之前的版本没有contactid导致的逻辑不对，每次进来将联系人的id取出，此时再保存就有contactid了。以后这个临时代码要拿掉
        Contact con = DBService.queryContact(m_currentData.carCode);
        if(con != null) {
            m_currentData.contactid = con.getIdfromnode();
        }

        setContentView(R.layout.activity_work_room_edit_info);
        m_indexIcon =  findViewById(R.id.workroom_index_icon);

        m_viewPager = (ViewPager) findViewById(R.id.work_room_viewpager);
        FragmentManager fm = getFragmentManager();
        m_fragmentAdapter = new WorkRoomEditInfoFragmentPagerAdapter(this,fm,m_currentData);
        m_viewPager.setAdapter(m_fragmentAdapter);
        m_viewPager.setCurrentItem(0);
        m_viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                m_viewPager.setCurrentItem(position);
                rereshIndexLocation(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        rereshIndexLocation(0);

        m_leftBtn = (Button) findViewById(R.id.workroom_edit_leftbtn);
        m_leftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m_viewPager.setCurrentItem(0);
                rereshIndexLocation(0);
            }
        });

        m_rightBtn = (Button) findViewById(R.id.workroom_edit_rightbtn);
        m_rightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m_viewPager.setCurrentItem(1);
                rereshIndexLocation(1);
            }
        });



        common_navi_bt = (Button)findViewById(R.id.common_navi_bt);
        common_navi_bt.setVisibility(View.VISIBLE);
        if("0".equalsIgnoreCase(isdirectadditem))
        {
            common_navi_bt.setText("开启手动录入");
        }else{
            common_navi_bt.setText("关闭手动录入");

        }


        common_navi_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if("0".equalsIgnoreCase(isdirectadditem))
                {
                    isdirectadditem ="1";
                }else{
                    isdirectadditem ="0";
                }
                updateUserAddItemSet(isdirectadditem);
                m_fragmentAdapter.notifyDataSetChanged();
            }
        });


        EventBus.getDefault().register(this);

        m_commitBtn = (Button) findViewById(R.id.workroom_edit_bottom_leftbtn);
        m_commitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                RepairHistory _rep =  m_fragmentAdapter.getCurrentRepariData();
                if(_rep == null){
                    Toast.makeText(m_this,"数据异常，请重新操作",Toast.LENGTH_SHORT).show();
                    return;
                }

                m_currentData =  _rep;


                if(m_currentData.entershoptime.length() == 0 ){
                    Toast.makeText(m_this,"进店时间未填",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(m_currentData.totalKm.length() == 0 ){
                    Toast.makeText(m_this,"进店里程未填",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(m_currentData.wantedcompletedtime.length() == 0){
                    Toast.makeText(m_this,"预计提车时间未填",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(m_currentData.repairType.length() == 0){
                    Toast.makeText(m_this,"维修内容未填",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(m_currentData.circle.length() == 0){
                    Toast.makeText(m_this,"提醒周期未填",Toast.LENGTH_SHORT).show();
                    return;
                }


                if(m_currentData.state.equals("0")){
                    m_currentData.state = "1";
                    commitRepair(m_currentData);
                }else if(m_currentData.state.equals("1")){

                    Intent intent = new  Intent(m_this,PayOffActivity.class);
                    intent.putExtra("data", m_currentData);
                    startActivityForResult(intent,1);
                }else if(m_currentData.state.equals("2")){

                }else if(m_currentData.state.equals("3")){
                    m_currentData.state = "0";
                    revertRepair(m_currentData);
                }else {

                }

            }
        });

        m_saveBtn =  (Button) findViewById(R.id.workroom_edit_bottom_rightbtn);
        m_saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                RepairHistory _rep =  m_fragmentAdapter.getCurrentRepariData();
                if(_rep == null){
                    Toast.makeText(m_this,"数据异常，请重新操作",Toast.LENGTH_SHORT).show();
                    return;
                }
                m_currentData =  _rep;

                if(m_currentData.entershoptime.length() == 0 ){
                    Toast.makeText(m_this,"进店时间未填",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(m_currentData.totalKm.length() == 0 ){
                    Toast.makeText(m_this,"进店里程未填",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(m_currentData.wantedcompletedtime.length() == 0){
                    Toast.makeText(m_this,"预计提车时间未填",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(m_currentData.repairType.length() == 0){
                    Toast.makeText(m_this,"维修内容未填",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(m_currentData.circle.length() == 0){
                    Toast.makeText(m_this,"提醒周期未填",Toast.LENGTH_SHORT).show();
                    return;
                }
                updateRepair(m_currentData);
            }
        });


        m_bottom = (LinearLayout) findViewById(R.id.workroom_bottom);


        if(m_currentData.state!= null) {

            if (m_currentData.state.equals("0")) {
                m_commitBtn.setText("提交结账");
                m_commitBtn.setBackgroundColor(getResources().getColor(R.color.material_light_blue));
            } else if (m_currentData.state.equals("1")) {
                m_commitBtn.setText("确认收款");
                m_commitBtn.setBackgroundColor(getResources().getColor(R.color.material_red));
            } else if (m_currentData.state.equals("2")) {
                m_bottom.setVisibility(View.GONE);
            } else if (m_currentData.state.equals("3")) {
                m_commitBtn.setText("恢复工单");
                m_commitBtn.setBackgroundColor(getResources().getColor(R.color.material_green));
            } else {

            }
        }


        mTitle      = (TextView)findViewById(R.id.common_navi_title);
        mTitle.setText("工单处理");

        mBackBtn    = (Button)findViewById(R.id.common_navi_back);
        mBackBtn.setVisibility(View.VISIBLE);
        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkIsNoneInput();
            }
        });

        mAddBtn = (Button)findViewById(R.id.common_navi_add);
        mAddBtn.setText("更多");

        if(MainApplication.getInstance().getUserType(WorkRoomEditActivity.this) == 2)
        {

            mAddBtn.setVisibility(View.VISIBLE);
        }else{
            mAddBtn.setVisibility(View.INVISIBLE);

        }
        mAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String[] arr = null;
                if(m_currentData.state.equals("0")){
                    arr = getResources().getStringArray(R.array.more_setting_0);
                }else if(m_currentData.state.equals("1")){
                    arr = getResources().getStringArray(R.array.more_setting_0);
                }else if(m_currentData.state.equals("2")){
                    if("0".equalsIgnoreCase(m_currentData.ownnum)) {
                        arr = getResources().getStringArray(R.array.more_setting_2);
                    }else{
                        arr = getResources().getStringArray(R.array.more_setting_5);
                    }
                }
                else{
                    arr = getResources().getStringArray(R.array.more_setting_3);
                }


                View outerView = LayoutInflater.from(m_this).inflate(R.layout.wheel_view, null);
                final WheelView wv = (WheelView) outerView.findViewById(R.id.wheel_view_wv);
                wv.setItems(Arrays.asList(arr));
                wv.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
                    @Override
                    public void onSelected(int selectedIndex, String item) {
                        Log.e(TAG, "[Dialog]selectedIndex: " + selectedIndex + ", item: " + item);
                    }
                });

                new AlertDialog.Builder(m_this)
                        .setTitle("选择操作")
                        .setView(outerView)
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {


                                Log.e(TAG, "OK" + wv.getSeletedIndex()+which);
                                if(wv.getSeletedIndex() == 0){
                                    if(m_currentData.state.equals("0")){
                                        cancelRepair(m_currentData);
                                    }else if(m_currentData.state.equals("1")){
                                        cancelRepair(m_currentData);
                                    }else if(m_currentData.state.equals("2")){
                                        deleteOneRepairServerAndLocalDB(m_currentData);
                                    }
                                    else {
                                        revertRepair(m_currentData);
                                    }
                                }else if(wv.getSeletedIndex() == 1){

                                    if(m_currentData.state.equals("2")){//关闭提醒
                                        m_currentData.isClose = "1";
                                        m_currentData.isreaded = "1";
                                        updateRepair(m_currentData);
                                    }else {
                                        deleteOneRepairServerAndLocalDB(m_currentData);
                                    }
                                }else if(wv.getSeletedIndex() == 2){
                                    if(m_currentData.state.equals("2"))
                                    {
                                        clearownemoney(m_currentData);
                                    }else {
                                        m_currentData.isClose = "0";
                                        m_currentData.isreaded = "0";
                                        updateRepair(m_currentData);
                                    }
                                }
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.e(TAG, "onCancel");
                            }
                        })
                        .show();


            }
        });

        m_totalPrice = (TextView) findViewById(R.id.workroom_totalprice_text);

        refreshBottom();

    }


    /**
     * 取消工单
     * @param rep
     */
    private void cancelRepair(RepairHistory rep){
        final RepairHistory _rep = rep;
        Map map = new HashMap();
        map.put("owner", LoginUserUtil.getTel(m_this));
        map.put("id", rep.idfromnode);
        showWaitView();
        HttpManager.getInstance(m_this).deleteOneContact("/repair/cancel", map, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                stopWaitingView();
                if(jsonObject.optInt("code") == 1){
                    backToHome();
                }
                else {
                    Toast.makeText(m_this,"取消失败",Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                stopWaitingView();
                Toast.makeText(m_this,"取消失败",Toast.LENGTH_SHORT).show();
            }
        });

    }

    /**
     * 恢复工单
     * @param rep
     */
    private void revertRepair(RepairHistory rep){
        final RepairHistory _rep = rep;
        Map map = new HashMap();
        map.put("owner", LoginUserUtil.getTel(m_this));
        map.put("id", rep.idfromnode);
        showWaitView();
        HttpManager.getInstance(m_this).deleteOneContact("/repair/revert", map, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                stopWaitingView();
                if(jsonObject.optInt("code") == 1){
                    backToHome();
                }
                else {
                    Toast.makeText(m_this,"取消失败",Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                stopWaitingView();
                Toast.makeText(m_this,"取消失败",Toast.LENGTH_SHORT).show();
            }
        });

    }

    /**
     * 修改工单状态
     * @param rep
     */
    private void updateRepairState(RepairHistory rep,String state){
        final RepairHistory _rep = rep;
        Map map = new HashMap();
        map.put("owner", LoginUserUtil.getTel(m_this));
        map.put("id", rep.idfromnode);
        map.put("state", state);
        showWaitView();
        HttpManager.getInstance(m_this).deleteOneContact("/repair/updateState", map, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                stopWaitingView();
                if(jsonObject.optInt("code") == 1){
                    backToHome();
                }
                else {
                    Toast.makeText(m_this,"修改失败",Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                stopWaitingView();
                Toast.makeText(m_this,"修改失败",Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 删除某个维修记录
     * @param rep
     */
    private void deleteOneRepairServerAndLocalDB(RepairHistory rep){
        final RepairHistory _rep = rep;
        Map map = new HashMap();
        map.put("owner", LoginUserUtil.getTel(m_this));
        map.put("id", rep.idfromnode);
        showWaitView();
        HttpManager.getInstance(m_this).deleteOneContact("/repair/del", map, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                stopWaitingView();
                if(jsonObject.optInt("code") == 1){
                    backToHome();
                }
                else {
                    Toast.makeText(m_this,"删除失败",Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                stopWaitingView();
                Toast.makeText(m_this,"删除失败",Toast.LENGTH_SHORT).show();
            }
        });

    }


    public void refreshDataAndBottomView(RepairHistory newRep){
        m_currentData = newRep;
        refreshBottom();
    }

    private  void refreshBottom(){
        if(m_currentData.arrRepairItems != null){
            int total = 0;
            for(int i=0;i<m_currentData.arrRepairItems.size();i++)
            {
                ADTReapirItemInfo info=m_currentData.arrRepairItems.get(i);
                if(info.workhourpay ==null || "".equalsIgnoreCase(info.workhourpay))
                {
                    if(!LoginUserUtil.isNumeric(info.price)){
                        Toast.makeText(m_this,info.type+"价格格式有误,请检查不要有空格等符号",Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if(!LoginUserUtil.isNumeric(info.num)){
                        Toast.makeText(m_this,info.type+"次数格式有误,请检查不要有空格等符号",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    total = total + Integer.parseInt(info.price)*Integer.parseInt(info.num);
                }else {

                    if(!LoginUserUtil.isNumeric(info.price)){
                        Toast.makeText(m_this,info.type+"价格格式有误,请检查不要有空格等符号",Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if(!LoginUserUtil.isNumeric(info.num)){
                        Toast.makeText(m_this,info.type+"次数格式有误,请检查不要有空格等符号",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    
                    total = total+(Integer.parseInt(info.price)+ Integer.parseInt(info.workhourpay))*Integer.parseInt(info.num);

                }
            }
            m_totalPrice.setText("总计 ¥"+total);
        }else {
            m_totalPrice.setText("总计 ¥0");
        }
    }


    public void commitRepair(RepairHistory m_data){

        JSONArray list = new JSONArray();
        JSONObject selectmap = null;
        for(int i=0;i<m_data.arrRepairItems.size();i++){
            ADTReapirItemInfo _item = m_data.arrRepairItems.get(i);
            list.put(_item.idfromnode);
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
            cv.put("owner", LoginUserUtil.getTel(m_this));
            cv.put("id", m_data.idfromnode);
            cv.put("inserttime", m_data.inserttime);
            cv.put("items", list);
            cv.put("state", m_data.state);
            cv.put("wantedcompletedtime", m_data.wantedcompletedtime);
            cv.put("customremark", m_data.customremark);
            cv.put("iswatiinginshop", m_data.iswatiinginshop);
            cv.put("entershoptime", m_data.entershoptime);
            cv.put("contactid", m_data.contactid);
            cv.put("pics",m_data.pics);
            cv.put("oilvolume",m_data.oilvolume);
            cv.put("nexttipkm",m_data.nexttipkm);
            showWaitView();
            HttpManager.getInstance(m_this).updateOneRepair("/repair/update5", cv, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {

                    stopWaitingView();
                    if(jsonObject.optInt("code") == 1){
                        Toast.makeText(m_this,"提交成功",Toast.LENGTH_SHORT).show();
                        if(Integer.parseInt(m_currentData.state) == 1){
                            updatestorenum(true);
                        }
                        backToHome();
                    }else {
                        Toast.makeText(m_this,"提交失败",Toast.LENGTH_SHORT).show();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {

                    stopWaitingView();
                    Toast.makeText(m_this,"提交失败",Toast.LENGTH_SHORT).show();
                }
            });

        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
}


    public void updateRepair(RepairHistory m_data){

        JSONArray list = new JSONArray();
        JSONObject selectmap = null;
        for(int i=0;i<m_data.arrRepairItems.size();i++){
            ADTReapirItemInfo _item = m_data.arrRepairItems.get(i);
            list.put(_item.idfromnode);
        }



        String str = list.toString();
        Map map = new HashMap();
        map.put("items", list);


        DateFormat fmt =new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = fmt.parse(m_data.repairTime.toString());
            Calendar   calendar   =   new GregorianCalendar();
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
            cv.put("owner", LoginUserUtil.getTel(m_this));
            cv.put("id", m_data.idfromnode);
            cv.put("inserttime", m_data.inserttime);
            cv.put("items", list);
            cv.put("state", m_data.state);
            cv.put("wantedcompletedtime", m_data.wantedcompletedtime);
            cv.put("customremark", m_data.customremark);
            cv.put("iswatiinginshop", m_data.iswatiinginshop);
            cv.put("entershoptime", m_data.entershoptime);
            cv.put("contactid", m_data.contactid);
            cv.put("pics",m_data.pics);
            cv.put("oilvolume",m_data.oilvolume);
            cv.put("nexttipkm",m_data.nexttipkm);
            showWaitView();
            HttpManager.getInstance(m_this).updateOneRepair("/repair/update5", cv, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {

                    stopWaitingView();
                    if(jsonObject.optInt("code") == 1){
                        Toast.makeText(m_this,"更新成功",Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(m_this,"更新失败",Toast.LENGTH_SHORT).show();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {

                    stopWaitingView();
                    Toast.makeText(m_this,"更新失败",Toast.LENGTH_SHORT).show();
                }
            });

        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }


    }



    @Override
    public boolean onKeyDown(int keyCode,KeyEvent event){
        if(keyCode== KeyEvent.KEYCODE_BACK){
            checkIsNoneInput();
            return true;//不执行父类点击事件
        }

        return super.onKeyDown(keyCode, event);//继续执行父类其他点击事件
    }

    /**
     * 检查此工单是否无任何修改就返回了，如果是则需要主动删除此工单
     */
    private void checkIsNoneInput(){

        if(m_currentData.entershoptime.length() == 0 ||
                m_currentData.totalKm.length() == 0 ||
                m_currentData.wantedcompletedtime.length() == 0 ||
                m_currentData.repairType.length() == 0 ||
                m_currentData.circle.length() == 0 ){

            String[] arr = getResources().getStringArray(R.array.repair_back_tip);

            View outerView = LayoutInflater.from(m_this).inflate(R.layout.wheel_view, null);
            final WheelView wv = (WheelView) outerView.findViewById(R.id.wheel_view_wv);
            wv.setItems(Arrays.asList(arr));
            wv.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
                @Override
                public void onSelected(int selectedIndex, String item) {
                    Log.e(TAG, "[Dialog]selectedIndex: " + selectedIndex + ", item: " + item);
                }
            });

            new AlertDialog.Builder(m_this)
                    .setTitle("当前工单未编辑完成,确认返回?")
                    .setView(outerView)
                    .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {


                            Log.e(TAG, "OK" + wv.getSeletedIndex()+which);
                            if(wv.getSeletedIndex() == 0){

                                if(m_currentData.isAddedNewRepair == 1){
                                    deleteOneRepairServerAndLocalDB(m_currentData);
                                }else {
                                    backToHome();
                                }
                            }else if(wv.getSeletedIndex() == 1){

                            }
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Log.e(TAG, "onCancel");
                        }
                    })
                    .show();

        }else {
            if(m_currentData.state.equals("2")){//已完成的工单不要更改
                backToHome();
            }else {
                autoUpdateAndFinish(m_currentData);
            }

        }

    }

    public void autoUpdateAndFinish(RepairHistory m_data){

        JSONArray list = new JSONArray();
        JSONObject selectmap = null;
        for(int i=0;i<m_data.arrRepairItems.size();i++){
            ADTReapirItemInfo _item = m_data.arrRepairItems.get(i);
            list.put(_item.idfromnode);
        }



        DateFormat fmt =new SimpleDateFormat("yyyy-MM-dd");
        try {

            Date date = fmt.parse(m_data.repairTime.toString());
            Calendar   calendar   =   new GregorianCalendar();
            calendar.setTime(date);
//            calendar.add(calendar.DATE, Integer.valueOf(m_data.circle.toString()));//把日期往后增加一天.整数往后推,负数往前移动
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
            cv.put("owner", LoginUserUtil.getTel(m_this));
            cv.put("id", m_data.idfromnode);
            cv.put("inserttime", m_data.inserttime);
            cv.put("items", list);
            cv.put("state", m_data.state);
            cv.put("wantedcompletedtime", m_data.wantedcompletedtime);
            cv.put("customremark", m_data.customremark);
            cv.put("iswatiinginshop", m_data.iswatiinginshop);
            cv.put("entershoptime", m_data.entershoptime);
            cv.put("contactid", m_data.contactid);
            cv.put("pics",m_data.pics);
            cv.put("oilvolume",m_data.oilvolume);
            cv.put("nexttipkm",m_data.nexttipkm);
            showWaitView();
            HttpManager.getInstance(m_this).updateOneRepair("/repair/update5", cv, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {

                    stopWaitingView();
                    if(jsonObject.optInt("code") == 1){
                        backToHome();
                    }else {
                        backToHome();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    stopWaitingView();
                    backToHome();
                }
            });

        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }


    private void backToHome(){
        m_this.setResult(1, getIntent());
        finish();
    }

    /**
     * WorkRoomCarInfoFragment.OnFragmentInteractionListener
     */

    @Override
    public void onOnWorkRoomCarInfoFragmentInteraction(Uri uri){

    }

    @Override
    public void  onWorkRoomRepairItemsFragmentInteraction(Uri uri){

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        getTakePhoto().onActivityResult(requestCode, resultCode, data);
        if(resultCode != RESULT_CANCELED){
            // 当requestCode、resultCode同时为0，也就是处理特定的结果
            if (requestCode == 2 ) {

            }else if(requestCode == 1)
            {
                finish();
            }
        }

    }

    public void clearownemoney(RepairHistory m_data){
        try {
            Map cv = new HashMap();
            cv.put("id", m_data.idfromnode);

            showWaitView();
            HttpManager.getInstance(m_this).updateOneRepair("/repair/clearownemoney", cv, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {

                    stopWaitingView();
                    if(jsonObject.optInt("code") == 1){
                        Toast.makeText(m_this,"更新成功",Toast.LENGTH_SHORT).show();
                        finish();
                    }else {
                        Toast.makeText(m_this,"更新失败",Toast.LENGTH_SHORT).show();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {

                    stopWaitingView();
                    Toast.makeText(m_this,"更新失败",Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }


    }

    public void updateUserAddItemSet(final String type)
    {
        Map cv = new HashMap();
        cv.put("isdirectadditem", type);
        cv.put("tel", LoginUserUtil.getTel(WorkRoomEditActivity.this));

        HttpManager.getInstance(WorkRoomEditActivity.this).updateOneRepair("/users/updateUserAddItemSet", cv, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {


                if(jsonObject.optInt("code") == 1){
                    String key_isdirectadditem=  WorkRoomEditActivity.this.getApplicationContext().getResources().getString(R.string.key_loginer_isdirectadditem);
                    SharedPreferences.Editor editor18 = WorkRoomEditActivity.this.getSharedPreferences(key_sp, Context.MODE_PRIVATE).edit();

                    editor18.putString(key_isdirectadditem, type);
                    editor18.commit();
                    if("0".equalsIgnoreCase(isdirectadditem))
                    {
                        common_navi_bt.setText("开启手动录入");
                    }else{
                        common_navi_bt.setText("关闭手动录入");

                    }
                    EventBus.getDefault().post(new RefEvent());
                }else {
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {


                Toast.makeText(WorkRoomEditActivity.this,"更新失败",Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onRequestPermissionsResult() {

    }
}