package com.points.autorepar.activity.serviceManager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.points.autorepar.R;
import com.points.autorepar.activity.BaseActivity;
import com.points.autorepar.activity.Store.ServiceManageActivity;
import com.points.autorepar.adapter.SelectServiceHomeExpandableAdapter;
import com.points.autorepar.adapter.ServiceHomeExpandableAdapter;
import com.points.autorepar.bean.ADTReapirItemInfo;
import com.points.autorepar.bean.ADTServiceInfo;
import com.points.autorepar.bean.ADTServiceItemInfo;
import com.points.autorepar.bean.RepairHistory;
import com.points.autorepar.bean.WorkRoomEvent;
import com.points.autorepar.http.HttpManager;
import com.points.autorepar.lib.wheelview.WheelView;
import com.points.autorepar.utils.LoginUserUtil;
import com.umeng.analytics.MobclickAgent;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;

public class SelectServiceHomeActivity extends BaseActivity {

    private  final  String  TAG = "ServiceHomeActivity";
    private SelectServiceHomeActivity m_this;
    ExpandableListView          m_expandableListView;

    private Button mBackBtn;
    private Button mAddBtn,common_navi_add1;
    private TextView mTitle;
    private String repid;
    private String contactid;
    ArrayList<ADTServiceInfo>  m_arr;
    ArrayList<ADTReapirItemInfo>  m_ItemInfo;
    private RepairHistory m_currentData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_service_home);

        mTitle      = (TextView)findViewById(R.id.common_navi_title);
        mTitle.setText("选择服务");
        m_currentData = getIntent().getParcelableExtra("data");

        LinearLayout topTab = (LinearLayout)findViewById(R.id.topTab);
        topTab.setVisibility(View.GONE);
//        m_ItemInfo = m_currentData.arrRepairItems;
        m_ItemInfo = new ArrayList<ADTReapirItemInfo>();
        repid  = getIntent().getStringExtra("repid");
        contactid = getIntent().getStringExtra("contactid");
        mBackBtn    = (Button)findViewById(R.id.common_navi_back);
        mBackBtn.setVisibility(View.VISIBLE);
        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        common_navi_add1 = (Button)findViewById(R.id.common_navi_add1);
        common_navi_add1.setText("新增");
        common_navi_add1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(m_this, ServiceHomeActivity.class);

                startActivity(intent);


            }
        });

        mAddBtn = (Button)findViewById(R.id.common_navi_add);
        mAddBtn.setText("确定");
        mAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(m_this, AddOrEditServiceCategoryActivity.class);
//                intent.putExtra("type","0");//新增
//                startActivityForResult(intent,1);
                deleteRepid();


            }
        });
        m_this = this;
        m_expandableListView = (ExpandableListView)findViewById(R.id.expand_list);

//        m_expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
//            public View getGroupView(intgroupPosition, booleanisExpanded, View convertView, ViewGroup parent)
//
//            @Override
//                                        int groupPosition, long id) {
//                // TODO Auto-generated method stub
//                return false;
//            }
//        });

        m_expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
             @Override
           public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {
//                Toast.makeText(getApplicationContext(), groupStrings[i], Toast.LENGTH_SHORT).show();
                                                             // 请务必返回 false，否则分组不会展
                 return false;
                                                         }
                                                     });



        m_expandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(final int i) {


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






    }

    public void onResume() {
        super.onResume();
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
                        SelectServiceHomeExpandableAdapter adapter = new SelectServiceHomeExpandableAdapter(m_this, m_arr);
                        m_expandableListView.setAdapter(adapter);
                        for (int i = 0; i < m_arr.size(); i++) {
                            m_expandableListView.expandGroup(i);
                            m_expandableListView.setGroupIndicator(null);
                        }
                    }else{
                        new android.app.AlertDialog.Builder(SelectServiceHomeActivity.this)
                                .setTitle("暂无添加服务，请到服务管理中添加?")
                                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent(SelectServiceHomeActivity.this, ServiceHomeActivity.class);
                                        startActivity(intent);
                                    }
                                })

                                .show();
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

                        item.num =  "0";
                        arrItems.add(item);
                    }
                }
                serviceInfo.arrSubTypes = arrItems;

                arrRep.add(serviceInfo);
            }
        }
        return arrRep;
    }

    private  void addRepairItems()  {

        Map map = new HashMap();
//        List<Map<String,String>> list = new ArrayList<Map<String,String>>();
        try{
            JSONArray str_dispatchtime = new JSONArray();
            JSONArray str_goods  = new JSONArray();
            JSONArray str_isdirectadd = new JSONArray();
            JSONArray str_repairer = new JSONArray();
            JSONArray str_state = new JSONArray();
            JSONArray str_isneeddispatch = new JSONArray();
            JSONArray str_itemtype = new JSONArray();
            JSONArray str_workhourpay= new JSONArray();
            JSONArray str_repid = new JSONArray();
            JSONArray str_num = new JSONArray();
            JSONArray str_price = new JSONArray();
            JSONArray str_contactid = new JSONArray();
            JSONArray str_service = new JSONArray();
            JSONArray str_type = new JSONArray();
            JSONArray str_name = new JSONArray();


            JSONArray list = new JSONArray();
            JSONObject selectmap = null;
            for(int i=0;i<m_currentData.arrRepairItems.size();i++)
            {
                ADTReapirItemInfo info = m_currentData.arrRepairItems.get(i);
                if("0".equalsIgnoreCase(info.itemtype))
                {
//                ADTReapirItemInfo info = new ADTReapirItemInfo();

                    str_dispatchtime.put("");
                    str_name.put("");
                    str_goods.put("");
                    str_isdirectadd.put("0");
                    str_repairer.put("");
                    str_state.put("0");
                    str_isneeddispatch.put("1");
                    str_itemtype.put("0");
                    str_workhourpay.put("0");
                    str_service.put(info.idfromnode);
                    str_repid.put(repid);

                    str_contactid.put(contactid);
                    str_price.put(info.price);
                    str_num.put(info.num);
                    str_type.put(info.type);
                    selectmap = new JSONObject();

                    selectmap.put("dispatchtime", str_dispatchtime);
                    selectmap.put("goods", str_service);
                    selectmap.put("isdirectadd", str_isdirectadd);
                    selectmap.put("repairer", str_repairer);
                    selectmap.put("state", str_state);
                    selectmap.put("isneeddispatch", str_isneeddispatch);
                    selectmap.put("itemtype", str_itemtype);

                    selectmap.put("workhourpay", str_workhourpay);
                    selectmap.put("repid", str_repid);
                    selectmap.put("num", str_num);
                    selectmap.put("price", str_price);
                    selectmap.put("contactid", str_contactid);
                    selectmap.put("service", str_service);
                    selectmap.put("type", str_type);
                    selectmap.put("name", str_type);

                    list.put(selectmap);
                }
            }

            for(int k =0;k<m_arr.size();k++) {
                for (int j = 0; j < m_arr.get(k).arrSubTypes.size(); j++) {

                    ADTServiceInfo serviceInfo = m_arr.get(k);
                    ADTServiceItemInfo itemInfo = serviceInfo.arrSubTypes.get(j);
                    if (!"0".equalsIgnoreCase(itemInfo.num)) {

                        ADTReapirItemInfo info = new ADTReapirItemInfo();

                        str_dispatchtime.put("");
                        str_name.put("");
                        str_goods.put("");
                        str_isdirectadd.put("0");
                        str_repairer.put("");
                        str_state.put("0");
                        str_isneeddispatch.put("1");
                        str_itemtype.put("1");
                        str_workhourpay.put(itemInfo.workHourPay);
                        str_service.put(itemInfo.id);
                        str_repid.put(repid);

                        str_contactid.put(contactid);
                        str_price.put(itemInfo.price);
                        str_num.put(itemInfo.num);
                        str_type.put(itemInfo.name);





                selectmap = new JSONObject();

                    selectmap.put("dispatchtime", str_dispatchtime);
                    selectmap.put("goods", str_goods);
                    selectmap.put("isdirectadd", str_isdirectadd);
                    selectmap.put("repairer", str_repairer);
                    selectmap.put("state", str_state);
                    selectmap.put("isneeddispatch", str_isneeddispatch);
                    selectmap.put("itemtype", str_itemtype);

                    selectmap.put("workhourpay", str_workhourpay);
                    selectmap.put("repid", str_repid);
                    selectmap.put("num", str_num);
                    selectmap.put("price", str_price);
                    selectmap.put("contactid", str_contactid);
                    selectmap.put("service", str_service);
                    selectmap.put("type", str_type);
                    selectmap.put("name", str_name);

                list.put(selectmap);
                    }
                }
            }



        String str = list.toString();

        map.put("items", list);

        }catch (Exception e )
        {
            e.printStackTrace();
        }
        String url = "";

        url = "/repairitem/additems2";





        HttpManager.getInstance(SelectServiceHomeActivity.this)
                .queryAllTipedRepair(url, map, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        Log.e(TAG,"/repairitem/additems2"+jsonObject.toString());


                        if(jsonObject.optInt("code") == 1){

                            ArrayList<ADTReapirItemInfo>  m_ItemInfotmp = new ArrayList<ADTReapirItemInfo>();
                            JSONArray items = jsonObject.optJSONArray("ret");

                            for(int i=0 ;i<m_ItemInfo.size();i++)
                            {
                                ADTReapirItemInfo info = m_ItemInfo.get(i);
                                if("0".equalsIgnoreCase(info.itemtype))
                                {
                                    m_ItemInfotmp.add(info);
                                }


                            }


                            if(items != null) {
                                for (int j = 0; j < items.length(); j++) {
                                    JSONObject itemObj = items.optJSONObject(j);
                                    ADTReapirItemInfo info = ADTReapirItemInfo.fromWithJsonObj(itemObj);
                                    m_ItemInfotmp.add(info);
                                }
                                m_currentData.arrRepairItems = m_ItemInfotmp;
                                EventBus.getDefault().post(
                                        new WorkRoomEvent(m_currentData));
                                finish();
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

    private  void deleteRepid(){
        Map map = new HashMap();
        map.put("repid", repid);


        String url = "";

        url = "/repairitem/delOneRepairAll";

        HttpManager.getInstance(SelectServiceHomeActivity.this)
                .queryAllTipedRepair(url, map, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        Log.e(TAG,"/repair/queryAllSafeTiped"+jsonObject.toString());

                        if(jsonObject.optInt("code") == 1){


                            addRepairItems();



                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                    }
                });

    }

}
