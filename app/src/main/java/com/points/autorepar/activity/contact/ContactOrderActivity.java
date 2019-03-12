package com.points.autorepar.activity.contact;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.points.autorepar.R;
import com.points.autorepar.activity.BaseActivity;
import com.points.autorepar.adapter.ContactOrderListAdapter;
import com.points.autorepar.bean.Contact;
import com.points.autorepar.bean.ContactOrderInfo;
import com.points.autorepar.http.HttpManager;
import com.points.autorepar.lib.cjj.MaterialRefreshLayout;
import com.points.autorepar.lib.cjj.MaterialRefreshListener;
import com.points.autorepar.lib.wheelview.WheelView;
import com.points.autorepar.sql.DBService;
import com.points.autorepar.utils.DateUtil;
import com.points.autorepar.utils.LoggerUtil;
import com.points.autorepar.utils.LoginUserUtil;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ContactOrderActivity extends BaseActivity  {
    private final  String  TAG = "ContactOrderActivity";

    private Button mBackBtn;
    private Button mConfirmBtn;
    private TextView mTitle;

    private ListView mListView;

    ContactOrderActivity m_this;
    ContactOrderListAdapter    m_adapter;
    private ArrayList<ContactOrderInfo>  m_arrData;
    private MaterialRefreshLayout materialRefreshLayout;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_info_edit);
        m_this = this;

        RelativeLayout naviLayout = (RelativeLayout) findViewById(R.id.contact_adduser_navi);
        mTitle = (TextView) naviLayout.findViewById(R.id.common_navi_title);
        mTitle.setText("客户预约维修保养");


        materialRefreshLayout = (MaterialRefreshLayout) findViewById(R.id.refresh_contactorder);
        materialRefreshLayout.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(final MaterialRefreshLayout materialRefreshLayout) {

                reloadDataAndRefreshView();

            }

            @Override
            public void onfinish() {

                Log.e("Contact", "onfinish");

            }

            @Override
            public void onRefreshLoadMore(final MaterialRefreshLayout materialRefreshLayout) {


            }
        });

        mListView = (ListView) findViewById(R.id.contact_info_listView);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

               final  ContactOrderInfo _info =  m_arrData.get(position);
                String[] arr = getResources().getStringArray(_info.state.equals("1") ?R.array.contact_order_list_click1 : R.array.contact_order_list_click);

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
                        .setTitle("请选择操作")
                        .setView(outerView)
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                Log.e(TAG, "OK" + wv.getSeletedIndex()+which);
                                if(_info.state.equals("1")){
                                    if(wv.getSeletedIndex() == 0){
                                        delOrderInfo(_info);
                                    }else {
                                        Contact _con = DBService.queryContact(_info.openid);
                                        Intent intent = new  Intent(m_this,ContactInfoEditActivity.class);
                                        intent.putExtra(String.valueOf(R.string.key_contact_edit_para), _con);
                                        startActivity(intent);
                                    }

                                }else{
                                    if(wv.getSeletedIndex() == 0){
                                        updateOrderInfo(_info);
                                    }else if(wv.getSeletedIndex() == 1){
                                        delOrderInfo(_info);
                                    }else {

                                        Contact _con = DBService.queryContact(_info.openid);
                                        Intent intent = new  Intent(m_this,ContactInfoEditActivity.class);
                                        intent.putExtra(String.valueOf(R.string.key_contact_edit_para), _con);
                                        startActivity(intent);

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

        mBackBtn = (Button) naviLayout.findViewById(R.id.common_navi_back);
        mBackBtn.setVisibility(View.VISIBLE);
        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mConfirmBtn = (Button) naviLayout.findViewById(R.id.common_navi_add);
        mConfirmBtn.setText("保存");
        mConfirmBtn.setVisibility(View.INVISIBLE);
        mConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }



    /**umeng统计
     *
     */

    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        reloadDataAndRefreshView();
    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    /*
    * 下拉或上拉相关函数
    */
    public   void  reloadDataAndRefreshView() {

        showWaitView();




        Map map = new HashMap();
        map.put("owner", LoginUserUtil.getTel(this).toString());
        showWaitView();
        HttpManager.getInstance(this).getCustomerOrdersList("/contact/getOrderRepairList", map,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        m_arrData = new ArrayList<>();
                        stopWaitingView();
                        JSONArray _arr = response.optJSONArray("ret");
                        Log.e(TAG, LoggerUtil.jsonFromObject(response));
                        if (response.optInt("code") == 1) {
                            for (int i = 0; i < _arr.length(); i++) {
                                ContactOrderInfo con = new ContactOrderInfo();
                                JSONObject obj =  _arr.optJSONObject(i);
                                con.openid =obj.optString("openid");
                                con.time = obj.optString("time");
                                con.info = obj.optString("info");
                                con.state = obj.optString("state");
                                con.confirmtome = obj.optString("confirmtime");
                                con.owner = obj.optString("owner");
                                con.inserttime = obj.optString("inserttime");
                                con.idfromserver = obj.optString("_id");
                                m_arrData.add(con);
                            }
                            m_adapter = new ContactOrderListAdapter(m_this, m_arrData);
                            mListView.setAdapter(m_adapter);
                        }

                        materialRefreshLayout.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                materialRefreshLayout.finishRefresh();
                                materialRefreshLayout.finishRefreshLoadMore();
                            }
                        }, 100);
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        stopWaitingView();
                        Toast.makeText(m_this, "添加失败", Toast.LENGTH_SHORT).show();


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

    private void updateOrderInfo(ContactOrderInfo info){

        Map map = new HashMap();
        map.put("shopname", LoginUserUtil.getShopName(this));
        map.put("openid", info.openid);
        map.put("id", info.idfromserver);
        map.put("confirmtime", DateUtil.timeFrom(new Date()));
        map.put("ordertime", info.time);
        map.put("orderinfo", info.info);
        map.put("state", "1");

        showWaitView();
        HttpManager.getInstance(this).getCustomerOrdersList("/contact/updateOrderRepair", map,new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        stopWaitingView();
                        if (response.optInt("code") == 1) {
                            Toast.makeText(m_this, "操作成功", Toast.LENGTH_SHORT).show();
                            reloadDataAndRefreshView();
                        } else {
                            Toast.makeText(m_this, response.optString("msg"), Toast.LENGTH_SHORT).show();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, error.getMessage(), error);

                        stopWaitingView();
                        Toast.makeText(m_this, "操作失败", Toast.LENGTH_SHORT).show();
                    }
                }
        );


    }

    private void delOrderInfo(ContactOrderInfo info){

        Map map = new HashMap();
        map.put("id", info.idfromserver);
        showWaitView();
        HttpManager.getInstance(this).getCustomerOrdersList("/contact/delOrderRepair", map,new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        stopWaitingView();
                        if (response.optInt("code") == 1) {
                            Toast.makeText(m_this, "删除成功", Toast.LENGTH_SHORT).show();
                            reloadDataAndRefreshView();
                        } else {
                            Toast.makeText(m_this, response.optString("msg"), Toast.LENGTH_SHORT).show();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, error.getMessage(), error);

                        stopWaitingView();
                        Toast.makeText(m_this, "删除失败", Toast.LENGTH_SHORT).show();
                    }
                }
        );


    }

}
