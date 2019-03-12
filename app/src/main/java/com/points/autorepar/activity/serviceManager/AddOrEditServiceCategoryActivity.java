package com.points.autorepar.activity.serviceManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.points.autorepar.R;
import com.points.autorepar.activity.BaseActivity;
import com.points.autorepar.adapter.ServiceHomeExpandableAdapter;
import com.points.autorepar.bean.ADTServiceInfo;
import com.points.autorepar.bean.ADTServiceItemInfo;
import com.points.autorepar.http.HttpManager;
import com.points.autorepar.lib.BluetoothPrinter.util.ToastUtil;
import com.points.autorepar.utils.LoginUserUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddOrEditServiceCategoryActivity extends BaseActivity {

    private  final  String  TAG = "AddOrEditServiceCat";
    private  AddOrEditServiceCategoryActivity  m_this;

    private Button mBackBtn;
    private Button mAddBtn;
    private TextView mTitle;

    private ADTServiceInfo m_serviceInfo;
    private boolean m_isAdd;
    private EditText  m_typeInput;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_categoty_home);
        m_this = this;

        m_typeInput = (EditText) findViewById(R.id.contact_add_vin);
        m_serviceInfo = getIntent().getParcelableExtra("service");
        if(m_serviceInfo == null){
            m_isAdd = true;
        }else {
            m_isAdd = false;
            m_typeInput.setText(m_serviceInfo.name);
        }

        mTitle      = (TextView)findViewById(R.id.common_navi_title);
        mTitle.setText(m_isAdd ? "新增大类" : "编辑");

        mBackBtn    = (Button)findViewById(R.id.common_navi_back);
        mBackBtn.setVisibility(View.VISIBLE);
        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        mAddBtn = (Button)findViewById(R.id.common_navi_add);
        mAddBtn.setText("保存");
        mAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(m_isAdd){
                    if( m_typeInput.getText().toString().length() == 0){
                        ToastUtil.showToast(m_this,"类名不能为空");
                        return;
                    }
                    Map map = new HashMap();
                    map.put("owner", LoginUserUtil.getTel(m_this));
                    map.put("name", m_typeInput.getText().toString());
                    HttpManager.getInstance(m_this).startNormalCommonPost("/servicetoptype/add", map, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject jsonObject) {
                            Log.e(TAG,jsonObject.toString());
                            if (jsonObject.optInt("code") == 1) {
                                Log.e(TAG,"okok");
                                ToastUtil.showToast(m_this,jsonObject.optString("msg"));
                                finishActivity(1);
                                finish();
                            }else {
                                ToastUtil.showToast(m_this,jsonObject.optString("msg"));
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {



                        }
                    });
                }else {


                    if( m_typeInput.getText().toString().length() == 0){
                        ToastUtil.showToast(m_this,"类名不能为空");
                        return;
                    }
                    Map map = new HashMap();
                    map.put("owner", LoginUserUtil.getTel(m_this));
                    map.put("name", m_typeInput.getText().toString());
                    map.put("id", m_serviceInfo.id);
                    HttpManager.getInstance(m_this).startNormalCommonPost("/servicetoptype/update", map, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject jsonObject) {
                            Log.e(TAG,jsonObject.toString());
                            if (jsonObject.optInt("code") == 1) {
                                Log.e(TAG,"okok");
                                ToastUtil.showToast(m_this,jsonObject.optString("msg"));
                                finishActivity(1);
                                finish();
                            }else {
                                ToastUtil.showToast(m_this,jsonObject.optString("msg"));
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {



                        }
                    });


                }
            }
        });



    }





}
