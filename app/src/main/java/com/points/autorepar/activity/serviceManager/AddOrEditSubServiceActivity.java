package com.points.autorepar.activity.serviceManager;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.points.autorepar.R;
import com.points.autorepar.activity.BaseActivity;
import com.points.autorepar.bean.ADTServiceInfo;
import com.points.autorepar.bean.ADTServiceItemInfo;
import com.points.autorepar.http.HttpManager;
import com.points.autorepar.lib.BluetoothPrinter.util.ToastUtil;
import com.points.autorepar.utils.LoginUserUtil;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddOrEditSubServiceActivity extends BaseActivity {


    private  final  String  TAG = "AddOrEditSubService";
    private  AddOrEditSubServiceActivity  m_this;

    private Button mBackBtn;
    private Button mAddBtn;
    private TextView mTitle;

    private ADTServiceInfo m_serviceInfo;
    private ADTServiceItemInfo m_serviceItemInfo;
    private boolean m_isAdd;
    private EditText m_typeInput;
    private EditText m_typeInput2;
    private EditText m_typeInput3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_sub_categoty_home);
        m_this = this;

        m_typeInput = (EditText) findViewById(R.id.input1);
        m_typeInput2 =  (EditText) findViewById(R.id.input2);
        m_typeInput3 =  (EditText) findViewById(R.id.input3);

        m_serviceInfo = getIntent().getParcelableExtra("service");
        m_serviceItemInfo = getIntent().getParcelableExtra("item");
        String isAdd = getIntent().getStringExtra("isAdd");
        if(isAdd.equals("1")){
            m_isAdd = true;
        }else {
            m_isAdd = false;
            m_typeInput.setText(m_serviceItemInfo.name);
            m_typeInput2.setText(m_serviceItemInfo.price);
            m_typeInput3.setText(m_serviceItemInfo.workHourPay);
        }

        mTitle      = (TextView) findViewById(R.id.common_navi_title);
        mTitle.setText(m_isAdd ? "新增服务" : "编辑");

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
                        ToastUtil.showToast(m_this,"名称不能为空");
                        return;
                    }

                    if( m_typeInput2.getText().toString().length() == 0){
                        ToastUtil.showToast(m_this,"价格不能为空");
                        return;
                    }

                    Map map = new HashMap();
                    map.put("owner", LoginUserUtil.getTel(m_this));
                    map.put("toptypeid", m_serviceInfo.id);
                    map.put("name", m_typeInput.getText().toString());
                    map.put("price", m_typeInput2.getText().toString());
                    map.put("workhourpay", m_typeInput3.getText().toString().length() == 0 ?"0":m_typeInput3.getText().toString());
                    HttpManager.getInstance(m_this).startNormalCommonPost("/servicesubtype/add2", map, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject jsonObject) {
                            Log.e(TAG,jsonObject.toString());
                            if (jsonObject.optInt("code") == 1) {

                                List<String> _ids = new ArrayList<>();
                                for(int i=0;i<m_serviceInfo.arrSubTypes.size();i++){
                                    ADTServiceItemInfo _item =  m_serviceInfo.arrSubTypes.get(i);
                                    _ids.add(_item.id);
                                }

                                _ids.add(jsonObject.optJSONObject("ret").optString("_id"));

                                Map map = new HashMap();
                                map.put("owner", LoginUserUtil.getTel(m_this));
                                map.put("id", m_serviceInfo.id);
                                map.put("subids",_ids);
                                HttpManager.getInstance(m_this).startNormalCommonPost("/servicetoptype/addRef", map, new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject jsonObject) {
                                        Log.e(TAG,jsonObject.toString());
                                        if (jsonObject.optInt("code") == 1) {


                                            ToastUtil.showToast(m_this,jsonObject.optString("msg"));
                                            finishActivity(2);
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
                                ToastUtil.showToast(m_this,jsonObject.optString("msg"));
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {



                        }
                    });
                }else {



                    ////////////

                    if( m_typeInput.getText().toString().length() == 0){
                        ToastUtil.showToast(m_this,"名称不能为空");
                        return;
                    }

                    if( m_typeInput2.getText().toString().length() == 0){
                        ToastUtil.showToast(m_this,"价格不能为空");
                        return;
                    }

                    Map map = new HashMap();

                    map.put("owner", LoginUserUtil.getTel(m_this));
                    map.put("id", m_serviceItemInfo.id);
                    map.put("name", m_typeInput.getText().toString());
                    map.put("price", m_typeInput2.getText().toString());
                    map.put("workhourpay", m_typeInput3.getText().toString().length() == 0 ?"0":m_typeInput3.getText().toString());
                    HttpManager.getInstance(m_this).startNormalCommonPost("/servicesubtype/update2", map, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject jsonObject) {
                            Log.e(TAG,jsonObject.toString());
                            if (jsonObject.optInt("code") == 1) {

                                ToastUtil.showToast(m_this,jsonObject.optString("msg"));
                                finishActivity(2);
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
