package com.points.autorepar.activity.serviceManager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.points.autorepar.R;
import com.points.autorepar.activity.BaseActivity;
import com.points.autorepar.activity.MainTabbarActivity;
import com.points.autorepar.adapter.ServiceHomeExpandableAdapter;
import com.points.autorepar.bean.ADTReapirItemInfo;
import com.points.autorepar.bean.ADTServiceInfo;
import com.points.autorepar.bean.ADTServiceItemInfo;
import com.points.autorepar.bean.Contact;
import com.points.autorepar.bean.RepairHistory;
import com.points.autorepar.http.HttpManager;
import com.points.autorepar.lib.wheelview.WheelView;
import com.points.autorepar.sql.DBService;
import com.points.autorepar.utils.JSONOejectUtil;
import com.points.autorepar.utils.LoggerUtil;
import com.points.autorepar.utils.LoginUserUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServiceHomeActivity extends BaseActivity {

    private  final  String  TAG = "ServiceHomeActivity";
    private  ServiceHomeActivity  m_this;
    ExpandableListView          m_expandableListView;

    private Button mBackBtn;
    private Button mAddBtn;
    private TextView mTitle;
    ArrayList<ADTServiceInfo>  m_arr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_home);

        mTitle      = (TextView)findViewById(R.id.common_navi_title);
        mTitle.setText("服务管理");

        mBackBtn    = (Button)findViewById(R.id.common_navi_back);
        mBackBtn.setVisibility(View.VISIBLE);
        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        LinearLayout topTab = (LinearLayout)findViewById(R.id.topTab);
        topTab.setVisibility(View.GONE);

        mAddBtn = (Button)findViewById(R.id.common_navi_add);
        mAddBtn.setText("新增大类");
        mAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(m_this, AddOrEditServiceCategoryActivity.class);
                intent.putExtra("type","0");//新增
                startActivityForResult(intent,1);
            }
        });
        m_this = this;
        m_expandableListView = (ExpandableListView)findViewById(R.id.expand_list);

        m_expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
                // TODO Auto-generated method stub
                return false;
            }
        });


        m_expandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(final int i) {

                String[] arr = getResources().getStringArray(R.array.service_home_group);
                View outerView = LayoutInflater.from(m_this).inflate(R.layout.wheel_view, null);
                final WheelView wv =  (WheelView)outerView.findViewById(R.id.wheel_view_wv);
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
                                if(wv.getSeletedIndex() == 0) {

                                    Intent intent = new Intent(m_this,AddOrEditSubServiceActivity.class);
                                    ADTServiceInfo _serviceInfo = m_arr.get(i);
                                    intent.putExtra("service",_serviceInfo);
                                    intent.putExtra("isAdd","1");
                                    startActivityForResult(intent,2);
                                }else if(wv.getSeletedIndex() == 1){
                                    Intent intent = new Intent(m_this, AddOrEditServiceCategoryActivity.class);
                                    ADTServiceInfo _serviceInfo = m_arr.get(i);
                                    intent.putExtra("type","1");//新增
                                    intent.putExtra("service",_serviceInfo);//新增
                                    startActivityForResult(intent,1);
                                }else if(wv.getSeletedIndex() == 2){

                                    final AlertDialog.Builder normalDialog =
                                            new AlertDialog.Builder(m_this);
                                    normalDialog.setTitle("删除此服务分类,不可恢复!");
                                    normalDialog.setMessage("确认删除?");
                                    normalDialog.setPositiveButton("确定",
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {

                                                    ADTServiceInfo _serviceInfo = m_arr.get(i);
                                                    delService(_serviceInfo);

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

        m_expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int i) {


            }
        });

        m_expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {


                final int group = i;
                final   ADTServiceInfo _serviceInfo = m_arr.get(group);
                final int child = i1;
                final ADTServiceItemInfo _item = _serviceInfo.arrSubTypes.get(child);

                String[] arr = getResources().getStringArray(R.array.service_home_cell);
                View outerView = LayoutInflater.from(m_this).inflate(R.layout.wheel_view, null);
                final WheelView wv =  (WheelView)outerView.findViewById(R.id.wheel_view_wv);
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
                                if(wv.getSeletedIndex() == 0) {

                                    Intent intent = new Intent(m_this,AddOrEditSubServiceActivity.class);
                                    intent.putExtra("service",_serviceInfo);
                                    intent.putExtra("item",_item);
                                    intent.putExtra("isAdd","2");
                                    startActivityForResult(intent,2);
                                }else if(wv.getSeletedIndex() == 1){

                                    final AlertDialog.Builder normalDialog =
                                            new AlertDialog.Builder(m_this);
                                    normalDialog.setTitle("删除此服务,不可恢复!");
                                    normalDialog.setMessage("确认删除?");
                                    normalDialog.setPositiveButton("确定",
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {

                                                    m_this.showWaitView();
                                                    Map map = new HashMap();
                                                    map.put("id",  _item.id);
                                                    HttpManager.getInstance(m_this).addNewRepair("/servicesubtype/del", map, new Response.Listener<JSONObject>() {
                                                        @Override
                                                        public void onResponse(JSONObject jsonObject) {

                                                            m_this.stopWaitingView();
                                                            if(jsonObject.optInt("code") == 1){
                                                                reloadData();
                                                                Toast.makeText(m_this,"删除成功",Toast.LENGTH_SHORT).show();
                                                            }
                                                            else {
                                                                Toast.makeText(m_this,"删除失败",Toast.LENGTH_SHORT).show();
                                                            }


                                                        }
                                                    }, new Response.ErrorListener() {
                                                        @Override
                                                        public void onErrorResponse(VolleyError volleyError) {
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




                                }else
                                {
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
                return false;
            }
        });




        reloadData();

    }

    private  void delService(ADTServiceInfo _info){

        Map map = new HashMap();
        map.put("id", _info.id);
        HttpManager.getInstance(this).getAllServiceTypePreviewList("/servicetoptype/del", map, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                if (jsonObject.optInt("code") == 1) {
                   reloadData();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {



            }
        });
    }

    private void reloadData(){

        Map map = new HashMap();
        map.put("owner", LoginUserUtil.getTel(this));

        HttpManager.getInstance(this).getAllServiceTypePreviewList("/servicetoptype/preview", map, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                if (jsonObject.optInt("code") == 1) {
                    JSONArray arr = jsonObject.optJSONArray("ret");
                    if (arr.length() > 0) {
                        m_arr = getArrayService(jsonObject);
                        ServiceHomeExpandableAdapter adapter = new ServiceHomeExpandableAdapter(m_this, m_arr);
                        m_expandableListView.setAdapter(adapter);
                        for (int i = 0; i < m_arr.size(); i++) {
                            m_expandableListView.expandGroup(i);
                            m_expandableListView.setGroupIndicator(null);
                        }
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {



            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1){
            reloadData();
        }else if(requestCode == 2){
            reloadData();
        }
    }


    private ArrayList<ADTServiceInfo> getArrayService(JSONObject ret){
        JSONArray arr = ret.optJSONArray("ret");
        ArrayList<ADTServiceInfo> arrRep =  new ArrayList();
        if (arr.length() > 0) {
            for (int i = 0; i < arr.length(); i++) {
                ADTServiceInfo serviceInfo = new ADTServiceInfo();
                JSONObject obj = arr.optJSONObject(i);
                serviceInfo.id =obj.optString("_id").replace(" ", "");
                serviceInfo.name =obj.optString("name").replace(" ", "");

                JSONArray items = obj.optJSONArray("subtype");
                ArrayList<ADTServiceItemInfo> arrItems = new ArrayList();
                if(items != null){
                    for(int j=0;j<items.length();j++){
                        JSONObject itemObj = items.optJSONObject(j);
                        ADTServiceItemInfo item = new ADTServiceItemInfo();
                        item.id =  itemObj.optString("_id");
                        item.name =  itemObj.optString("name");
                        item.price =  itemObj.optString("price");
                        item.topTypeId =  itemObj.optString("toptypeid");
                        item.workHourPay =  itemObj.optString("workhourpay");
                        arrItems.add(item);
                    }
                }
                serviceInfo.arrSubTypes = arrItems;

                arrRep.add(serviceInfo);
            }
        }
        return arrRep;
    }




}
