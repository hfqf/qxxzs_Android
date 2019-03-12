package com.points.autorepar.activity;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.points.autorepar.MainApplication;
import com.points.autorepar.R;
import com.points.autorepar.common.Consts;
import com.points.autorepar.http.HttpManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import cn.jpush.android.api.JPushInterface;


public class RegisterActivity extends BaseActivity {

    private  final  String  TAG = "RegisterActivity";
    private Button mBackBtn;
    private Button mAddBtn;
    private Button getCodeBtn;
    private TextView mTitle;

    private EditText mNameText;
    private EditText mTelText;
    private EditText mCodeText;
    private EditText mPwdText;
    private EditText mConfirmText;

    private EditText mAddress;
    private EditText mShopName;
    private EditText mChannel;
    private String   mCity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mTitle      = (TextView)findViewById(R.id.common_navi_title);
        mTitle.setText("注册");

        mBackBtn    = (Button)findViewById(R.id.common_navi_back);
        mBackBtn.setVisibility(View.VISIBLE);
        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mAddBtn = (Button)findViewById(R.id.common_navi_add);
        mAddBtn.setText("提交");
        mAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendCode();
            }
        });
        getCodeBtn = (Button)findViewById(R.id.register_codeButton);
        getCodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getVirifyBtnClicked();
            }
        });

        mNameText = (EditText)findViewById(R.id.register_nameInut);
        mTelText = (EditText)findViewById(R.id.register_telInput);
        mCodeText = (EditText)findViewById(R.id.register_codeInput);
        mPwdText = (EditText)findViewById(R.id.register_pwdInut);
        mConfirmText = (EditText)findViewById(R.id.register_confirmInput);

        mAddress = (EditText)findViewById(R.id.register_address);
        mShopName = (EditText)findViewById(R.id.register_shopname);
        mChannel = (EditText)findViewById(R.id.register_channelInput);
    }

    private void getVirifyBtnClicked(){

        if(mTelText.getText().toString().length() != 11){
            Toast.makeText(getApplicationContext(),"请输入11位手机号",Toast.LENGTH_SHORT).show();
            return;
        }
        MainApplication mainApplication = (MainApplication) getApplication();
        Map map = new HashMap();
        map.put("tel", mTelText.getText().toString());
        showWaitView();
        HttpManager.getInstance(this)
                .addNewUser("/sms/sendSmsCode", map, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {

                        stopWaitingView();
                        if(jsonObject.optInt("code") == 1){
                            Toast.makeText(getApplicationContext(),"获取验证码成功",Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(getApplicationContext(),jsonObject.optString("msg"),Toast.LENGTH_SHORT).show();
                        }



                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                        stopWaitingView();
                        Toast.makeText(getApplicationContext(),"获取失败",Toast.LENGTH_SHORT).show();

                    }
                });

    }

    /**
     * 发送短信验证码
     */
    private void sendCode(){

        if(mNameText.getText().length() == 0){
            Toast.makeText(getApplicationContext(),"用户名不能为空",Toast.LENGTH_SHORT).show();;
            return;
        }

        if(mTelText.getText().length() !=11){
            Toast.makeText(getApplicationContext(),"手机号码不足11位",Toast.LENGTH_SHORT).show();;
            return;
        }

        if(mCodeText.getText().length() !=6){
            Toast.makeText(getApplicationContext(),"验证码不足11位",Toast.LENGTH_SHORT).show();;
            return;
        }


        if(mPwdText.getText().length() == 0){
            Toast.makeText(getApplicationContext(),"密码不能为空",Toast.LENGTH_SHORT).show();;
            return;
        }

        if(mConfirmText.getText().length() == 0){
            Toast.makeText(getApplicationContext(),"确认密码不能为空",Toast.LENGTH_SHORT).show();;
            return;
        }

        if(!mPwdText.getText().toString().equals(mConfirmText.getText().toString())){
            Toast.makeText(getApplicationContext(),"两次密码不一致",Toast.LENGTH_SHORT).show();;
            return;
        }

        startRegister();
    }

    /**
     * 注册
     */
   private void startRegister( ){
        MainApplication mainApplication = (MainApplication) getApplication();
        Map map = new HashMap();
        map.put("username", mNameText.getText().toString());
        map.put("pwd", mPwdText.getText().toString());
        map.put("tel", mTelText.getText().toString());
        map.put("code", mCodeText.getText().toString());
        map.put("viplevel", mainApplication.consts().VIP_LEVEL);
        map.put("udid", JPushInterface.getUdid(getApplicationContext()));
        map.put("ostype", mainApplication.consts().OS_TYPE);
        map.put("version", Consts.getLocalVersionName(RegisterActivity.this));

       map.put("city", "");
       map.put("downchannel", mChannel.getText().toString().length() == 0 ?"":mChannel.getText().toString());
       map.put("address", mAddress.getText().toString().length() == 0 ?"":mAddress.getText().toString());
       map.put("shopname", mShopName.getText().toString().length() == 0 ?"":mShopName.getText().toString());

       showWaitView();
        HttpManager.getInstance(this)
                .addNewUser("/register/addNewUser4", map, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {

                        stopWaitingView();
                        Log.e(TAG,jsonObject.toString());

                        try {
                            String ret = jsonObject.get("code").toString();
                            if(ret == "1"){

                                Toast.makeText(getApplicationContext(),jsonObject.get("msg").toString(),Toast.LENGTH_SHORT).show();

                                new Handler().postDelayed(new Runnable(){
                                    public void run() {
                                        finish();
                                    }
                                }, 1000);

                            }else {
                                Toast.makeText(getApplicationContext(),jsonObject.get("msg").toString(),Toast.LENGTH_SHORT).show();
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                        stopWaitingView();
                        Toast.makeText(getApplicationContext(),"注册失败",Toast.LENGTH_SHORT).show();

                    }
                });
    }
}