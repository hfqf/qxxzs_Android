package com.points.autorepar.activity.workroom;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bigkoo.pickerview.TimePickerView;
import com.points.autorepar.MainApplication;
import com.points.autorepar.R;
import com.points.autorepar.activity.BaseActivity;
import com.points.autorepar.activity.WebActivity;
import com.points.autorepar.activity.contact.SelectContactActivity;
import com.points.autorepar.adapter.RepairHistoryAdapter;
import com.points.autorepar.adapter.RepairHistoryTipedAdapter;
import com.points.autorepar.bean.ADTReapirItemInfo;
import com.points.autorepar.bean.Contact;
import com.points.autorepar.bean.RepairHistory;
import com.points.autorepar.common.Consts;
import com.points.autorepar.fragment.RepairFragment;
import com.points.autorepar.fragment.WorkRoomFragment;
import com.points.autorepar.http.HttpManager;
import com.points.autorepar.lib.BluetoothPrinter.BluetoothController;
import com.points.autorepar.lib.BluetoothPrinter.BtService;
import com.points.autorepar.lib.BluetoothPrinter.PrinterActivity;
import com.points.autorepar.lib.BluetoothPrinter.SearchBluetoothActivity;
import com.points.autorepar.lib.BluetoothPrinter.base.AppInfo;
import com.points.autorepar.lib.BluetoothPrinter.bt.BtInterface;
import com.points.autorepar.lib.BluetoothPrinter.bt.BtUtil;
import com.points.autorepar.lib.BluetoothPrinter.print.PrintUtil;
import com.points.autorepar.lib.BluetoothPrinter.util.ToastUtil;
import com.points.autorepar.lib.cjj.MaterialRefreshLayout;
import com.points.autorepar.lib.cjj.MaterialRefreshListener;
import com.points.autorepar.lib.wheelview.WheelView;
import com.points.autorepar.sql.DBService;
import com.points.autorepar.utils.DateUtil;
import com.points.autorepar.utils.LoginUserUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by points on 2018/6/30.
 */


public class WorkRoomListActivity extends BaseActivity  implements BtInterface {

    private WorkRoomListActivity m_this;
    public BluetoothAdapter mAdapter;
    @Override
    public void btStartDiscovery(Intent intent) {

    }

    @Override
    public void btFinishDiscovery(Intent intent) {

    }

    @Override
    public void btStatusChanged(Intent intent) {

        BluetoothController.initWork(WorkRoomListActivity.this);
    }

    @Override
    public void btFoundDevice(Intent intent) {

    }

    @Override
    public void btBondStatusChange(Intent intent) {

    }


    @Override
    public  void  onResume(){
        super.onResume();
        m_isRefresh = true;
        m_page = 0;
        reloadDataAndRefreshView();

    }


    @Override
    public void btPairingRequest(Intent intent) {

    }
    protected BroadcastReceiver mBtReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (null == intent) {
                return;
            }
            String action = intent.getAction();
            if (TextUtils.isEmpty(action)) {
                return;
            }
            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                btStartDiscovery(intent);
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                btFinishDiscovery(intent);
            } else if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                btStatusChanged(intent);
            } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                btFoundDevice(intent);
            } else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                btBondStatusChange(intent);
            } else if ("android.bluetooth.device.action.PAIRING_REQUEST".equals(action)) {
                btPairingRequest(intent);
            }
        }
    };


    @Override
    protected void onStart() {
        super.onStart();
        if(m_type == 2){
            BtUtil.registerBluetoothReceiver(mBtReceiver, this);
            BluetoothController.initWork(this);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(m_type == 2) {
            BtUtil.unregisterBluetoothReceiver(mBtReceiver, this);
        }
    }


    public interface OnWorkRoomFragmentInteractionListener {


    }

    private WorkRoomFragment.OnWorkRoomFragmentInteractionListener mListener;

    private final  String   TAG = "WorkRoomFragment";
    //????????????????????????
    private MaterialRefreshLayout materialRefreshLayout;
    private  Button    backBtn,common_navi_bt;
    private  Button    addBtn;
    private TextView   title;
    private ListView   m_list;
    private  int       m_page;
    private  boolean   m_isRefresh;


    private Button    m_btn0,m_btn1,m_btn2,m_btn3,m_btn4;
    private  View     m_indexIcon;
    private  int    m_index;
    private  ArrayList<RepairHistory>            m_arrData;
    private EditText mClearEditText;
    private Contact m_searchContact;
    private String isdirectadditem;
    private int m_type;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        m_index = 0;
        m_page = 0;
        m_this = this;
        m_isRefresh = true;

        m_type = getIntent().getIntExtra("type",1);
        setContentView(R.layout.fragment_workroom);

            m_list =  (ListView) findViewById(R.id.id_workroomlist);

        m_list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                RepairHistory rep =  m_arrData.get(i);
                delOneRep(rep);
                return false;
            }
        });
            m_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    final RepairHistory rep =  m_arrData.get(position);
                    rep.m_isAddNewRep = 0;
                    if(m_type ==1 ) {
                        Intent intent = new Intent(m_this, WorkRoomEditActivity.class);
                        intent.putExtra(String.valueOf(R.string.key_repair_edit_para), rep);
                        startActivityForResult(intent, 1);
                    }else{

                        final String[] arr = {"????????????","????????????"};
                        View outerView = LayoutInflater.from(m_this).inflate(R.layout.wheel_view, null);
                        final WheelView wv = (WheelView) outerView.findViewById(R.id.wheel_view_wv);
                        wv.setItems(Arrays.asList(arr));
                        wv.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
                            @Override
                            public void onSelected(int selectedIndex, String item) {

                                if(selectedIndex==0){

                                }

                            }
                        });
                        new AlertDialog.Builder(m_this)
                                .setTitle("???????????????")
                                .setView(outerView)
                                .setPositiveButton("??????", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        Log.e(TAG, "OK" + wv.getSeletedIndex()+which);
                                        if(wv.getSeletedIndex() == 0){

                                            String weburl = Consts.HTTP_URL+"/repair/printAllItems?repid="+rep.idfromnode;
                                            WebActivity.actionStart(WorkRoomListActivity.this, weburl,"");
                                        }else if(wv.getSeletedIndex() == 1){
                                            if(rep.arrRepairItems== null || rep.arrRepairItems.size()==0)
                                            {
                                                Toast.makeText(WorkRoomListActivity.this,"???????????????????????????????????????",Toast.LENGTH_LONG).show();
                                                return;
                                            }

                                            if (TextUtils.isEmpty(AppInfo.btAddress)){
                                                ToastUtil.showToast(WorkRoomListActivity.this,"???????????????...");
                                                startActivity(new Intent(WorkRoomListActivity.this,SearchBluetoothActivity.class));
                                            }else {

                                                new AlertDialog.Builder(m_this)
                                                        .setTitle("?????????????????????????")
                                                        .setPositiveButton("??????", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {


                                                                if ( mAdapter.getState()==BluetoothAdapter.STATE_OFF ){//??????????????????????????????
                                                                    mAdapter.enable();
                                                                    ToastUtil.showToast(WorkRoomListActivity.this,"????????????????????????...");
                                                                }else {
//                                                ToastUtil.showToast(WorkRoomListActivity.this,"????????????...");
                                                                    Intent intent = new Intent(getApplicationContext(), BtService.class);
                                                                    intent.putExtra(String.valueOf(R.string.key_repair_edit_para), rep);
                                                                    intent.setAction(PrintUtil.ACTION_PRINT_PAGE);
                                                                    startService(intent);
                                                                }
                                                            }
                                                        })
                                                        .setNegativeButton("??????", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                Log.e(TAG, "onCancel");
                                                            }
                                                        })
                                                        .show();
                                            }
                                        }

                                    }
                                })
                                .setNegativeButton("??????", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Log.e(TAG, "onCancel");
                                    }
                                })
                                .show();
                    }
                }
            });
            m_indexIcon =  findViewById(R.id.workroom_index_icon);

            rereshIndexLocation();

            backBtn = (Button) findViewById(R.id.common_navi_back);
            backBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                  finish();

                }
            });

             addBtn = (Button) findViewById(R.id.common_navi_add);
            if(LoginUserUtil.isEmployeeLogined(WorkRoomListActivity.this) &&
                MainApplication.getInstance().getUserType(WorkRoomListActivity.this) == 1){
                if(LoginUserUtil.isCanAddNewRepair(WorkRoomListActivity.this)) {
                    addBtn.setVisibility(View.VISIBLE);
                }else {
                    addBtn.setVisibility(View.INVISIBLE);
                }
            }

            addBtn.setText("??????");
            addBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Log.e(TAG, "m_addNewRepairHistoryBtn");
                    final List arr = DBService.queryAllContactName();
                    if(arr == null){
                        Toast.makeText(m_this, "????????????,???????????????", Toast.LENGTH_SHORT).show();
                        return;
                    }


                    Intent intent = new Intent(m_this,SelectContactActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("flag","2");
                    intent.putExtras(bundle);
                    startActivity(intent);

                }
            });
            title = (TextView)findViewById(R.id.common_navi_title);
            title.setText("??????");
            m_btn0 =(Button) findViewById(R.id.workroom_fragment_btn0);
            m_btn0.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    m_index = 0;
                    m_searchContact = null;
                    rereshIndexLocation();
                    m_isRefresh = true;
                    reloadDataAndRefreshView();
                }
            });
            m_btn1 =(Button) findViewById(R.id.workroom_fragment_btn1);
            m_btn1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    m_index = 1;
                    m_page = 0;
                    m_searchContact = null;
                    m_isRefresh = true;
                    rereshIndexLocation();
                    reloadDataAndRefreshView();
                }
            });
            m_btn2 =(Button)findViewById(R.id.workroom_fragment_btn2);
            m_btn2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    m_index = 2;
                    m_page = 0;
                    m_searchContact = null;
                    rereshIndexLocation();
                    m_isRefresh = true;
                    reloadDataAndRefreshView();
                }
            });
            m_btn3 =(Button)findViewById(R.id.workroom_fragment_btn3);
            m_btn3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    m_index = 3;
                    m_page = 0;
                    m_searchContact = null;
                    m_isRefresh = true;
                    rereshIndexLocation();
                    reloadDataAndRefreshView();
                }
            });

        m_btn4 =(Button)findViewById(R.id.workroom_fragment_btn4);
        m_btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m_index = 4;
                m_page = 0;
                m_searchContact = null;
                m_isRefresh = true;
                rereshIndexLocation();
                reloadDataAndRefreshView();
            }
        });

            mClearEditText = (EditText) findViewById(R.id.workroom_filter_edit);
            mClearEditText.setClickable(true);
            mClearEditText.setFocusableInTouchMode(false);
            mClearEditText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(m_this,SelectContactActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("flag","0");
                    intent.putExtras(bundle);
                    startActivityForResult(intent,1);
                }
            });


        materialRefreshLayout = (MaterialRefreshLayout) m_this.findViewById(R.id.refresh_workroom);
        materialRefreshLayout.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(final MaterialRefreshLayout materialRefreshLayout) {
                m_searchContact = null;
                m_isRefresh = true;
                m_page = 0;
                reloadDataAndRefreshView();
            }
            @Override
            public void onfinish() {
                Log.e("Contact", "onfinish");
            }

            @Override
            public void onRefreshLoadMore(final MaterialRefreshLayout materialRefreshLayout) {
                m_page++;
                m_isRefresh = false;
                reloadDataAndRefreshView();
            }
        });
        String key_isdirectadditem=  getApplicationContext().getResources().getString(R.string.key_loginer_isdirectadditem);
        SharedPreferences sp = getSharedPreferences("points", Context.MODE_PRIVATE);
        isdirectadditem = sp.getString(key_isdirectadditem, null);


        common_navi_bt = (Button)findViewById(R.id.common_navi_bt);
        common_navi_bt.setVisibility(View.INVISIBLE);
        if("0".equalsIgnoreCase(isdirectadditem))
        {
            common_navi_bt.setText("??????????????????");
        }else{
            common_navi_bt.setText("??????????????????");
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
            }
        });

        reloadDataAndRefreshView();
    }

    private void rereshIndexLocation( ){


        Display display = m_this.getWindow().getWindowManager().getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        display.getMetrics(dm);
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) m_indexIcon.getLayoutParams();
        lp.leftMargin = m_index * (dm.widthPixels /5);
        lp.width = (dm.widthPixels /5);;//??????????????????????????????????????????????????????????????????
        m_indexIcon.setLayoutParams(lp);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(data == null){

            }else {
                m_isRefresh = true;
                m_page = 0;
                Contact contact = data.getParcelableExtra("contact");
                m_searchContact = contact;
                reloadDataAndRefreshView();
            }

        }
    }



    /*
     * ???????????????????????????
     */
    public   void  reloadDataAndRefreshView(){

        if(m_searchContact == null){
            mClearEditText.setText("");
        }else {
            mClearEditText.setText(m_searchContact.getCarCode());
        }

        final BaseActivity base =  (BaseActivity) m_this;
        base.showWaitView();
        String time ="";
        if(m_arrData==null ||m_arrData.size()== 0 || m_isRefresh) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// HH:mm:ss
//??????????????????
            Date date = new Date(System.currentTimeMillis());
            time = simpleDateFormat.format(date);
        }else{
            RepairHistory history = m_arrData.get(m_arrData.size()-1);
            time = history.inserttime;
        }


        Map map = new HashMap();
        map.put("owner", LoginUserUtil.getTel(m_this));

        map.put("page", String.valueOf(m_page));
        map.put("pagesize", m_searchContact == null ?"20" : "1000");
        map.put("insertTime",time);
        if(m_searchContact != null){
            map.put("contactid",m_searchContact.getIdfromnode());
            map.put("carcode",m_searchContact.getCarCode());
        }

        String url = "";
        switch(m_index)
        {
            case 0:
                url = "/repair/queryAllWithState2";


//                url = "/repair/queryAllWithState";
                map.put("state", String.valueOf(m_index));
                break;
            case 1:
                url = "/repair/queryAllWithState2";
                map.put("state", String.valueOf(m_index));
                break;
            case 2:
                url = "/repair/queryAllWithState2";
                map.put("state", String.valueOf(m_index));
                break;
            case 3:
                url = "/repair/queryAllWithState2";
                map.put("state", String.valueOf(m_index));
                break;
            case 4:
                url = "/repair/queryAllWithStateAndOwned";
                map.put("state", "2");
                break;
            case 5:
            default:
                break;
        }
        MainApplication.getInstance().setRoomType(WorkRoomListActivity.this,m_index);
//        url = "/repair/queryAllWithState";
        HttpManager.getInstance(m_this)
                .queryAllTipedRepair(url, map, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        Log.e(TAG,"/repair/queryAllWithState"+jsonObject.toString());

                        m_searchContact = null;
                        base.stopWaitingView();
                        if(jsonObject.optInt("code") == 1){

                            m_arrData =  getArrayRepair(jsonObject);

                            Handler mainHandler = new Handler(Looper.getMainLooper());
                            mainHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    RepairHistoryAdapter m_adapter = new RepairHistoryAdapter(m_this, m_arrData,m_index);
                                    m_list.setAdapter(m_adapter);
                                    if(m_isRefresh){
                                        m_list.setSelection(0);
                                    }else {
                                        m_list.setSelection(m_list.getBottom()-1);
                                    }
                                    materialRefreshLayout.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            materialRefreshLayout.finishRefresh();
                                            materialRefreshLayout.finishRefreshLoadMore();
                                        }
                                    }, 100);
                                }
                            });

                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        m_searchContact = null;
                        base.stopWaitingView();
                    }
                });

    }

    private ArrayList<RepairHistory> getArrayRepair(JSONObject ret){
        JSONArray arr = ret.optJSONArray("ret");

        ArrayList<RepairHistory> arrRep = m_isRefresh ? new ArrayList() : m_arrData;
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
                repFromServer.oilvolume = obj.optString("oilvolume");
                repFromServer.nexttipkm = obj.optString("nexttipkm");

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

                JSONArray arrPicsObj = obj.optJSONArray("carinfopics");
                ArrayList<String> arrPics = new ArrayList();
                if(arrPicsObj != null){
                    for(int j=0;j<arrPicsObj.length();j++){
                        String url = arrPicsObj.optString(j);
                        arrPics.add(url);
                    }
                }
                repFromServer.arrCarInfoPics = arrPics;

                repFromServer.totalPrice = String.valueOf(totalPrice);

                Contact con = DBService.queryContact(repFromServer.carCode);
                if(con != null){
                    arrRep.add(repFromServer);
                }
            }
        }
        return arrRep;
    }


    /**
     * ????????????
     */
    private void selectDayTime( ) {
        //???????????????
        TimePickerView pvTime = new TimePickerView.Builder(m_this, new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {//??????????????????

                String time = DateUtil.timeFrom(date);

            }
        }).build();
        pvTime.setDate(Calendar.getInstance());//??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
        pvTime.show();

    }
    public void updateUserAddItemSet(final String type)
    {
        Map cv = new HashMap();
        cv.put("isdirectadditem", type);
        cv.put("tel", LoginUserUtil.getTel(WorkRoomListActivity.this));

        HttpManager.getInstance(WorkRoomListActivity.this).updateOneRepair("/users/updateUserAddItemSet", cv, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {


                if(jsonObject.optInt("code") == 1){
                    String key_isdirectadditem=  WorkRoomListActivity.this.getApplicationContext().getResources().getString(R.string.key_loginer_isdirectadditem);
                    SharedPreferences.Editor editor18 = WorkRoomListActivity.this.getSharedPreferences("points", Context.MODE_PRIVATE).edit();

                    editor18.putString(key_isdirectadditem, type);
                    editor18.commit();
                    if("0".equalsIgnoreCase(isdirectadditem))
                    {
                        common_navi_bt.setText("??????????????????");
                    }else{
                        common_navi_bt.setText("??????????????????");

                    }
                    Toast.makeText(WorkRoomListActivity.this,"????????????",Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(WorkRoomListActivity.this,"????????????",Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {


                Toast.makeText(WorkRoomListActivity.this,"????????????",Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void delOneRep(final RepairHistory rep){
        String[] arr = getResources().getStringArray(R.array.more_setting_1);

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
                .setTitle("????????????")
                .setView(outerView)
                .setPositiveButton("??????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                        Log.e(TAG, "OK" + wv.getSeletedIndex()+which);
                        if(wv.getSeletedIndex() == 0){
                            deleteOneRepairServerAndLocalDB(rep);
                        }else if(wv.getSeletedIndex() == 1){

                        }else if(wv.getSeletedIndex() == 2){

                        }
                    }
                })
                .setNegativeButton("??????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.e(TAG, "onCancel");
                    }
                })
                .show();
    }

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
                    reloadDataAndRefreshView();
                }
                else {
                    Toast.makeText(m_this,"????????????",Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                stopWaitingView();
                Toast.makeText(m_this,"????????????",Toast.LENGTH_SHORT).show();
            }
        });

    }
}
