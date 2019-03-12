package com.points.autorepar.activity.repair;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v13.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.points.autorepar.R;
import com.points.autorepar.adapter.RepairInfoAdapter;
import com.points.autorepar.bean.ADTReapirItemInfo;
import com.points.autorepar.http.HttpManager;
import com.points.autorepar.lib.wheelview.WheelView;
import com.points.autorepar.activity.BaseActivity;
import com.points.autorepar.bean.RepairHistory;
import com.points.autorepar.utils.LoginUserUtil;
import com.umeng.analytics.MobclickAgent;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

public class RepairInfoEditActivity extends BaseActivity implements TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {


    private RepairHistory mData;
    private final String TAG = "RepairInfoEditActivity";
    private EditText kmEditText;
    private EditText repairTimeEditText;
    private EditText tipCircleEditText;
    private EditText repairTypeEditText;
    private EditText addtionEditText;
    private CheckBox closeTipCheckbox;


    private EditText addItemTypeEditText;
    private EditText addItemPriceEditText;
    private EditText addItemNumEditText;
    private Button   addItemButton;

    protected Button mbackBtn;
    protected Button mAddBtn;
    protected Button mDeleteBtn;
    protected TextView m_title;
    RepairInfoEditActivity m_this;

    private ListView mListView;
    private RepairInfoAdapter  m_adapter;
    private View mFooterView;


    /**
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repair_info_edit);


        m_this = this;
        mData = getIntent().getParcelableExtra(String.valueOf(R.string.key_repair_edit_para));
        Log.e(TAG, "mData" + mData.toString());


        mAddBtn = (Button) findViewById(R.id.common_navi_add);
        mAddBtn.setText("保存");
        mAddBtn.setOnClickListener(new View.OnClickListener() {
            /**
             * @param v
             */
            @Override
            public void onClick(View v) {

                if (m_adapter.m_data.totalKm.length() == 0) {
                    Toast.makeText(getApplicationContext(), "公路数不能为空", Toast.LENGTH_SHORT).show();

                    return;
                }

                if (m_adapter.m_data.repairTime.length() == 0) {
                    Toast.makeText(getApplicationContext(), "维修日期不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (m_adapter.m_data.repairType.length() == 0) {
                    Toast.makeText(getApplicationContext(), "保养项目不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }

                DateFormat fmt =new SimpleDateFormat("yyyy-MM-dd");
                try {
                    Date date = fmt.parse(m_adapter.m_data.repairTime.toString());
                    Calendar   calendar   =   new GregorianCalendar();
                    calendar.setTime(date);
                    calendar.add(calendar.DATE, Integer.valueOf(m_adapter.m_data.circle.toString()));//把日期往后增加一天.整数往后推,负数往前移动
                    date = calendar.getTime();   //这个时间就是日期往后推一天的结果

                    String finalTime = fmt.format(date);
                    m_adapter.m_data.tipCircle = finalTime;

                    saveBtnClicked();
                } catch (ParseException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }


            }
        });

        mbackBtn = (Button) findViewById(R.id.common_navi_back);
        mbackBtn.setVisibility(View.VISIBLE);
        mbackBtn.setOnClickListener(new View.OnClickListener() {
            /**
             * @param v
             */
            @Override
            public void onClick(View v) {
                Intent data = new Intent();
                setResult(0, data);
                finish();
            }
        });

        m_title = (TextView) findViewById(R.id.common_navi_title);
        m_title.setText("编辑记录");

        mListView = (ListView) findViewById(R.id.repair_info_listView);
        m_adapter = new RepairInfoAdapter(this,mData);
        mListView.setAdapter(m_adapter);

        if(mData.m_isAddNewRep == 0){
            LayoutInflater layoutInflater =
                    (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            mFooterView = layoutInflater.inflate(R.layout.repair_info_footerview, null);
            mListView.addFooterView(mFooterView);
        }
    }


    public void deleteRepair(View view){


        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(m_this);
        normalDialog.setTitle("删除此记录,不可恢复!");
        normalDialog.setMessage("确认删除?");
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        deleteOneRepairServerAndLocalDB(m_adapter.m_data);

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

                    finish();
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

    public void changeCloseTip(){
        m_adapter.m_data.isClose =  m_adapter.m_data.isClose.equals("1") ? "0" : "1";
        m_adapter.m_data.isreaded =  m_adapter.m_data.isClose.equals("1") ? "0" : "1";
        m_adapter.notifyDataSetChanged();
        updateRepair();
    }

    /**
     * 可由当前页面的保存按钮触发，也可以由保存收费条目时触发(此时判断时是否是新记录，选择更新items，还是创建新记录)
     */
    private void saveBtnClicked(){
        if(mData.m_isAddNewRep == 1){
            addNewPepair();
        }else {
            updateRepair();
        }
    }

    public void addNewPepair(){

        JSONArray arrItmes = new JSONArray();
        for(int i=0;i<m_adapter.m_data.arrRepairItems.size();i++){
            ADTReapirItemInfo _item =  m_adapter.m_data.arrRepairItems.get(i);
            arrItmes.put(_item.idfromnode);
        }

        Map cv = new HashMap();
        cv.put("carcode", m_adapter.m_data.carCode);
        cv.put("totalkm", m_adapter.m_data.totalKm);
        cv.put("repairetime", m_adapter.m_data.repairTime);
        cv.put("repairtype", m_adapter.m_data.repairType);
        cv.put("addition", m_adapter.m_data.addition);
        cv.put("tipcircle", m_adapter.m_data.tipCircle);
        cv.put("circle", m_adapter.m_data.circle);
        cv.put("isclose", m_adapter.m_data.isClose) ;
        cv.put("isreaded", m_adapter.m_data.isClose);
        cv.put("owner", LoginUserUtil.getTel(this));
        cv.put("id", "");
        cv.put("items", arrItmes);

        showWaitView();
        HttpManager.getInstance(m_this).updateOneRepair("/repair/add4", cv, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {

                stopWaitingView();
                if(jsonObject.optInt("code") == 1){
                    Toast.makeText(m_this,"保存成功",Toast.LENGTH_SHORT).show();
                    finish();
                }else {
                    Toast.makeText(getApplicationContext(),"保存失败",Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

                stopWaitingView();
                Toast.makeText(getApplicationContext(),"保存失败",Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void updateRepair(){

        JSONArray arrItmes = new JSONArray();
        for(int i=0;i<m_adapter.m_data.arrRepairItems.size();i++){
            ADTReapirItemInfo _item =  m_adapter.m_data.arrRepairItems.get(i);
            arrItmes.put(_item.idfromnode);
        }
        Map cv = new HashMap();
        cv.put("carcode", m_adapter.m_data.carCode);
        cv.put("totalkm", m_adapter.m_data.totalKm);
        cv.put("repairetime", m_adapter.m_data.repairTime);
        cv.put("repairtype", m_adapter.m_data.repairType);
        cv.put("addition", m_adapter.m_data.addition);
        cv.put("tipcircle", m_adapter.m_data.tipCircle);
        cv.put("circle", m_adapter.m_data.circle);
        cv.put("isclose", m_adapter.m_data.isClose) ;
        cv.put("isreaded", m_adapter.m_data.isClose);
        cv.put("owner", m_adapter.m_data.owner);
        cv.put("id", m_adapter.m_data.idfromnode);
        cv.put("inserttime", m_adapter.m_data.inserttime);
        cv.put("items", arrItmes);


        showWaitView();
        HttpManager.getInstance(m_this).updateOneRepair("/repair/update3", cv, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {

                stopWaitingView();
                if(jsonObject.optInt("code") == 1){
                    Toast.makeText(getApplicationContext(),"更新成功",Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(getApplicationContext(),"更新失败",Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

                stopWaitingView();
                Toast.makeText(getApplicationContext(),"更新失败",Toast.LENGTH_SHORT).show();
            }
        });
    }

    /*
     *各种要实现的接口
     */
    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
        String time = "You picked the following time: " + hourOfDay + "h" + minute + "m" + second;
        Log.e(TAG, time);
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String date = year + "-" + (monthOfYear + 1) + ( dayOfMonth > 9 ?  ("-"+dayOfMonth) : ("-0"+dayOfMonth));
        m_adapter.m_data.repairTime = date;
        m_adapter.notifyDataSetChanged();
        Log.e(TAG, date);
    }

    /**
     * 选择日期
     */
    public void selectDate() {
        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );

        dpd.setVersion(DatePickerDialog.Version.VERSION_2);
        dpd.show(getFragmentManager(), "Datepickerdialog");
    }

    /**
     * 选择时间
     */
    private void selectDayTime() {
        Calendar now = Calendar.getInstance();
        TimePickerDialog tpd = TimePickerDialog.newInstance(this, now.get(Calendar.DATE), now.get(Calendar.MINUTE), true);
        tpd.setVersion(TimePickerDialog.Version.VERSION_2);
        tpd.show(getFragmentManager(), "TimePickerDialog");
    }


    public void selectTipCircle() {

        String[] arr = getResources().getStringArray(R.array.tip_circle);

        View outerView = LayoutInflater.from(this).inflate(R.layout.wheel_view, null);
        final WheelView wv = (WheelView) outerView.findViewById(R.id.wheel_view_wv);
        wv.setItems(Arrays.asList(arr));
        wv.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
            @Override
            public void onSelected(int selectedIndex, String item) {
                Log.e(TAG, "[Dialog]selectedIndex: " + selectedIndex + ", item: " + item);
            }
        });

        new AlertDialog.Builder(this)
                .setTitle("选择提醒周期(天)")
                .setView(outerView)
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                        Log.e(TAG, "OK" + wv.getSeletedItem());

                        m_adapter.m_data.circle = wv.getSeletedItem();
                        m_adapter.notifyDataSetChanged();
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

    public void addNewRepairItem(final ADTReapirItemInfo item){
        if(item.type.length() == 0){
            Toast.makeText(m_this,"收费内容不能为空",Toast.LENGTH_SHORT).show();
            return;
        }

        if(item.price.length() == 0){
            Toast.makeText(m_this,"收费价格不能为空",Toast.LENGTH_SHORT).show();
            return;
        }

        if(item.num.length() == 0){
            Toast.makeText(m_this,"收费次数或数量不能为空",Toast.LENGTH_SHORT).show();
            return;
        }
        item.repId = mData.idfromnode;
        item.contactId = mData.carCode;
        showWaitView();

        Map map = new HashMap();
        map.put("repid", item.repId);
        map.put("contactid", item.contactId);
        map.put("price", item.price);
        map.put("num", item.num);
        map.put("type", item.type);
        HttpManager.getInstance(this).addNewRepair("/repairitem/add", map, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {

                stopWaitingView();
                item.idfromnode =  jsonObject.optJSONObject("ret").optString("_id");
                if(jsonObject.optInt("code") == 1){
                    Toast.makeText(m_this,"添加收费明细成功",Toast.LENGTH_SHORT).show();
                    m_adapter.m_data.arrRepairItems.add(item);
                    m_adapter.notifyDataSetChanged();
                    if(m_adapter.m_data.m_isAddNewRep == 1){

                    } else {
                        updateRepair();
                    }
                }
                else {
                    Toast.makeText(m_this,"添加收费明细失败",Toast.LENGTH_SHORT).show();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

                stopWaitingView();
                Toast.makeText(m_this,"添加收费明细失败",Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void delRepairItem(final ADTReapirItemInfo item,final int index){

        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(m_this);
        normalDialog.setTitle("删除此收费记录,不可恢复!");
        normalDialog.setMessage("确认删除?");
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        showWaitView();
                        Map map = new HashMap();
                        map.put("id", item.idfromnode);
                        HttpManager.getInstance(m_this).addNewRepair("/repairitem/del", map, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject jsonObject) {

                                stopWaitingView();
                                if(jsonObject.optInt("code") == 1){
                                    Toast.makeText(m_this,"删除成功",Toast.LENGTH_SHORT).show();
                                    m_adapter.m_data.arrRepairItems.remove(index);
                                    m_adapter.notifyDataSetChanged();
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

    /**
     * umeng统计
     */

    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
