package com.points.autorepar.activity.workroom;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
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
import com.points.autorepar.R;
import com.points.autorepar.activity.BaseActivity;
import com.points.autorepar.activity.contact.SelectContactActivity;
import com.points.autorepar.adapter.MyUnFinishedServiceAdapter;
import com.points.autorepar.adapter.RepairHistoryAdapter;
import com.points.autorepar.bean.ADTReapirItemInfo;
import com.points.autorepar.bean.Contact;
import com.points.autorepar.bean.RepairHistory;
import com.points.autorepar.bean.WorkRoomPicBackEvent;
import com.points.autorepar.fragment.WorkRoomFragment;
import com.points.autorepar.http.HttpManager;
import com.points.autorepar.lib.BluetoothPrinter.BluetoothController;
import com.points.autorepar.lib.BluetoothPrinter.BtService;
import com.points.autorepar.lib.BluetoothPrinter.SearchBluetoothActivity;
import com.points.autorepar.lib.BluetoothPrinter.base.AppInfo;
import com.points.autorepar.lib.BluetoothPrinter.bt.BtInterface;
import com.points.autorepar.lib.BluetoothPrinter.bt.BtUtil;
import com.points.autorepar.lib.BluetoothPrinter.print.PrintUtil;
import com.points.autorepar.lib.BluetoothPrinter.util.ToastUtil;
import com.points.autorepar.lib.cjj.MaterialRefreshLayout;
import com.points.autorepar.lib.cjj.MaterialRefreshListener;
import com.points.autorepar.sql.DBService;
import com.points.autorepar.utils.DateUtil;
import com.points.autorepar.utils.LoginUserUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;

/**
 * Created by points on 2018/6/30.
 */


public class EmployeeWorkRoomListActivity extends BaseActivity  implements BtInterface {

    private EmployeeWorkRoomListActivity m_this;
    public BluetoothAdapter mAdapter;
    @Override
    public void btStartDiscovery(Intent intent) {

    }

    @Override
    public void btFinishDiscovery(Intent intent) {

    }

    @Override
    public void btStatusChanged(Intent intent) {

        BluetoothController.initWorkEmploy(EmployeeWorkRoomListActivity.this);
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
        BtUtil.registerBluetoothReceiver(mBtReceiver, this);
        BluetoothController.initWorkEmploy(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        BtUtil.unregisterBluetoothReceiver(mBtReceiver, this);
        EventBus.getDefault().unregister(this);
    }


    public interface OnWorkRoomFragmentInteractionListener {


    }

    private WorkRoomFragment.OnWorkRoomFragmentInteractionListener mListener;

    private final  String   TAG = "WorkRoomFragment";
    //下拉上拉刷新控件
    private MaterialRefreshLayout materialRefreshLayout;
    private  Button    backBtn;
    private  Button    addBtn;
    private TextView   title;
    private ListView   m_list;
    private  int       m_page;
    private  boolean   m_isRefresh;


    private Button    m_btn0,m_btn1;
    private  View     m_indexIcon;
    private  int    m_index;
    private  ArrayList<RepairHistory>            m_arrData;
    private EditText mClearEditText;
    private Contact m_searchContact;
    private int m_type;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        m_index = 0;
        m_page = 0;
        m_this = this;
        m_isRefresh = true;

        if (!EventBus.getDefault().isRegistered(this)) {

            EventBus.getDefault().register(this);

        }

        m_type = getIntent().getIntExtra("type",1);
        setContentView(R.layout.fragment_workroom_employee);

            m_list =  (ListView) findViewById(R.id.id_workroomlist);
            m_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


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

            title = (TextView)findViewById(R.id.common_navi_title);
            title.setText("工单");
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

        Button common_navi_add  = (Button )findViewById(R.id.common_navi_add);
        common_navi_add.setVisibility(View.INVISIBLE);

//            mClearEditText = (EditText) findViewById(R.id.workroom_filter_edit);
//            mClearEditText.setClickable(true);
//            mClearEditText.setFocusableInTouchMode(false);
//            mClearEditText.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                    Intent intent = new Intent(m_this,SelectContactActivity.class);
//                    Bundle bundle = new Bundle();
//                    bundle.putString("flag","0");
//                    intent.putExtras(bundle);
//                    startActivityForResult(intent,1);
//                }
//            });


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
                m_isRefresh = true;
                reloadDataAndRefreshView();
            }
        });
        reloadDataAndRefreshView();
    }

    private void rereshIndexLocation( ){


        Display display = m_this.getWindow().getWindowManager().getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        display.getMetrics(dm);
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) m_indexIcon.getLayoutParams();
        lp.leftMargin = m_index * (dm.widthPixels /2);
        lp.width = (dm.widthPixels /2);;//这里动态设置光标的长度，以便适配不同的分辨率
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
     * 下拉或上拉相关函数
     */
    public   void  reloadDataAndRefreshView(){
        final BaseActivity base =  (BaseActivity) m_this;
        base.showWaitView();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// HH:mm:ss
//获取当前时间
        Date date = new Date(System.currentTimeMillis());
        String time = simpleDateFormat.format(date);

        Map map = new HashMap();
        map.put("id", LoginUserUtil.getUserId(EmployeeWorkRoomListActivity.this));

        map.put("state",m_index );
        map.put("pagesize", m_searchContact == null ?"20" : "1000");
        map.put("insertTime",time);
        if(m_searchContact != null){
            map.put("contactid",m_searchContact.getIdfromnode());
            map.put("carcode",m_searchContact.getCarCode());
        }
        final int _m_index = m_index;

        String url = "/repairitem/queryDispatchedItems";

        HttpManager.getInstance(m_this)
                .queryAllTipedRepair(url, map, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        Log.e(TAG,"/repair/queryAllWithState"+jsonObject.toString());

                        m_searchContact = null;
                        base.stopWaitingView();
                        if(jsonObject.optInt("code") == 1){


                            final JSONArray ary = jsonObject.optJSONArray("ret");
                            Handler mainHandler = new Handler(Looper.getMainLooper());
                            mainHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    MyUnFinishedServiceAdapter m_adapter = new MyUnFinishedServiceAdapter(EmployeeWorkRoomListActivity.this,ary ,_m_index==0?false:true);
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

    public void onEventMainThread(WorkRoomPicBackEvent event) {
        reloadDataAndRefreshView();
    }

}
