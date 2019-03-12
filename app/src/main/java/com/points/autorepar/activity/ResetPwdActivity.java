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
import com.points.autorepar.R;
import com.points.autorepar.MainApplication;
import com.points.autorepar.http.HttpManager;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ResetPwdActivity extends BaseActivity {

    private  final  String  TAG = "RegisterActivity";
    private Button mBackBtn;
    private Button mAddBtn;
    private TextView mTitle;

    private EditText mTelText;
    private EditText mPwdText;
    private EditText mConfirmText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resetpwd);

        mTitle      = (TextView)findViewById(R.id.common_navi_title);
        mTitle.setText("重置密码");

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

        mTelText = (EditText)findViewById(R.id.register_telInput);
        mPwdText = (EditText)findViewById(R.id.register_pwdInut);
        mConfirmText = (EditText)findViewById(R.id.register_confirmInput);
    }

    /**
     * 发送短信验证码
     */
    private void sendCode(){

        if(mTelText.getText().length() !=11){
            Toast.makeText(getApplicationContext(),"手机号码不足11位",Toast.LENGTH_SHORT).show();;
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
   private void startRegister(){
        MainApplication mainApplication = (MainApplication) getApplication();
        Map map = new HashMap();
        map.put("pwd", mPwdText.getText().toString());
        map.put("tel", mTelText.getText().toString());
        showWaitView();
        HttpManager.getInstance(this)
                .addNewUser("/users/regetpwd", map, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {

                        stopWaitingView();
                        Log.e(TAG,jsonObject.toString());

                            String ret = jsonObject.optString("code");
                            if(ret == "1"){

                                Toast.makeText(getApplicationContext(),jsonObject.optString("msg"),Toast.LENGTH_SHORT).show();

                                new Handler().postDelayed(new Runnable(){
                                    public void run() {
                                        finish();
                                    }
                                }, 1000);

                            }else {
                                Toast.makeText(getApplicationContext(),jsonObject.optString("msg"),Toast.LENGTH_SHORT).show();
                            }


                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                        stopWaitingView();
                        Toast.makeText(getApplicationContext(),"修改失败",Toast.LENGTH_SHORT).show();

                    }
                });
    }
}