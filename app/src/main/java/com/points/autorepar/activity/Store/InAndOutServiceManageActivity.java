package com.points.autorepar.activity.Store;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.points.autorepar.R;
import com.points.autorepar.activity.BaseActivity;
import com.points.autorepar.activity.StoreActivity;
import com.points.autorepar.adapter.ServiceManageExpandableAdapter;
import com.points.autorepar.bean.ADTServiceInfo;
import com.points.autorepar.bean.ServiceManageInfo;
import com.points.autorepar.bean.ServiceManageSubInfo;
import com.points.autorepar.http.HttpManager;
import com.points.autorepar.lib.wheelview.WheelView;
import com.points.autorepar.utils.LoginUserUtil;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class InAndOutServiceManageActivity extends BaseActivity {

    private  final  String  TAG = "ServiceHomeActivity";
    private InAndOutServiceManageActivity m_this;
    ExpandableListView          m_expandableListView;

    private Button mBackBtn;
    private Button mAddBtn;
    private TextView mTitle;
    private int type;
    ArrayList<ServiceManageInfo>  m_arr;
    private Button    m_btn0,m_btn1;
    private  View     m_indexIcon;
    private  int    m_index;
    private  int  select_index,sub_select_index ;

    private EditText search_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_home);

        mTitle      = (TextView)findViewById(R.id.common_navi_title);
        mTitle.setText("采购/入库");

        mBackBtn    = (Button)findViewById(R.id.common_navi_back);
        mBackBtn.setVisibility(View.VISIBLE);
        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        search_text = (EditText)findViewById(R.id.search_text);

        search_text.setOnEditorActionListener(new TextView.OnEditorActionListener(){

            public boolean onEditorAction(TextView v,int acitionId,KeyEvent event){
                if (acitionId== EditorInfo.IME_ACTION_SEND ||(event!=null&&event.getKeyCode()== KeyEvent.KEYCODE_ENTER))
                {
                    reloadData();
                    return true;
                }
                return false;
            }


        });

        mAddBtn = (Button)findViewById(R.id.common_navi_add);
        mAddBtn.setText("分类设置");
        mAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(InAndOutServiceManageActivity.this, ServiceManageActivity.class);
                startActivity(intent1);
            }
        });
        m_this = this;
        select_index = 0;
        sub_select_index = 0;
        m_expandableListView = (ExpandableListView)findViewById(R.id.expand_list);

        m_expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
                // TODO Auto-generated method stub

                final ServiceManageInfo serviceInfo =  m_arr.get(groupPosition);

                return true;
            }
        });




        m_expandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(final int i) {

                Log.e("1","2");

            }
        });

        m_expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int i) {

                Log.e("1","3");
            }
        });

        m_expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {

               final ServiceManageInfo serviceInfo =  m_arr.get(i);

               final ServiceManageSubInfo subInfo=serviceInfo.subtypelist.get(i1);


                Intent intent = new Intent(m_this, InAndOutServiceSubListActivity.class);
                intent.putExtra("subid",subInfo.id);//新增
                intent.putExtra("name",subInfo.name);//新增

                startActivity(intent);
                return false;

            }
        });




        type = 1;
        m_indexIcon =  findViewById(R.id.workroom_index_icon);
        m_index = 0;
        rereshIndexLocation();
        m_btn0 =(Button) findViewById(R.id.workroom_fragment_btn0);
        m_btn0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m_index = 0;
                rereshIndexLocation();
                reloadData();
            }
        });
        m_btn1 =(Button) findViewById(R.id.workroom_fragment_btn1);
        m_btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m_index = 1;
                rereshIndexLocation();
                reloadData();
            }
        });



    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        reloadData();
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
        map.put("type",m_index);
        map.put("key",search_text.getText().toString()   );
        map.put("owner", LoginUserUtil.getTel(this));

        HttpManager.getInstance(this).getAllServiceTypePreviewList("/warehousegoodstoptype/preview2", map, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                if (jsonObject.optInt("code") == 1) {
                    JSONArray arr = jsonObject.optJSONArray("ret");
                    if (arr.length() > 0) {
                        m_arr = getArrayService(jsonObject);
                        ServiceManageExpandableAdapter adapter = new ServiceManageExpandableAdapter(m_this, m_arr);
                        m_expandableListView.setAdapter(adapter);
                        for (int i = 0; i < m_arr.size(); i++) {
                            m_expandableListView.expandGroup(i);
                            m_expandableListView.setGroupIndicator(null);
                        }
                    }else{
                        m_arr =  new  ArrayList<ServiceManageInfo>();
                        ServiceManageExpandableAdapter adapter = new ServiceManageExpandableAdapter(m_this, m_arr);
                        m_expandableListView.setAdapter(adapter);
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        });
    }

    private void delinfo(String id){

        Map map = new HashMap();
        map.put("id",id);

        HttpManager.getInstance(this).getAllServiceTypePreviewList("/warehousegoodstoptype/del", map, new Response.Listener<JSONObject>() {
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

    private void delsubinfo(String id){

        Map map = new HashMap();
        map.put("id",id);

        HttpManager.getInstance(this).getAllServiceTypePreviewList("/warehousegoodssubtype/del", map, new Response.Listener<JSONObject>() {
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if(requestCode == 1){
//            reloadData();
//        }else if(requestCode == 2){
//            reloadData();
//        }
        reloadData();
    }


    private ArrayList<ServiceManageInfo> getArrayService(JSONObject ret){
        JSONArray arr = ret.optJSONArray("ret");
        ArrayList<ServiceManageInfo> arrRep =  new ArrayList();
        if (arr.length() > 0) {
            for (int i = 0; i < arr.length(); i++) {
                ServiceManageInfo serviceInfo = new ServiceManageInfo();
                JSONObject obj = arr.optJSONObject(i);
                serviceInfo.id =obj.optString("_id").replace(" ", "");
                serviceInfo.name =obj.optString("name").replace(" ", "");
                serviceInfo.owner =obj.optString("owner").replace(" ", "");
                serviceInfo.iscommon =obj.optString("iscommon").replace(" ", "");
                serviceInfo.isautocreated =obj.optString("isautocreated").replace(" ", "");

                JSONArray items = obj.optJSONArray("subtype");
                ArrayList<ServiceManageSubInfo> arrItems = new ArrayList();
                if(items != null){
                    for(int j=0;j<items.length();j++){
                        JSONObject itemObj = items.optJSONObject(j);
                        ServiceManageSubInfo item = new ServiceManageSubInfo();
                        item.id =  itemObj.optString("_id");
                        item.name =  itemObj.optString("name");
                        item.toptypeid =  itemObj.optString("toptypeid");
                        item.isautocreated =  itemObj.optString("isautocreated");
                        arrItems.add(item);

                    }
                }
                serviceInfo.subtypelist = arrItems;

                arrRep.add(serviceInfo);
            }
        }
        return arrRep;
    }




}
