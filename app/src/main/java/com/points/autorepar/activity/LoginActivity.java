package com.points.autorepar.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
//import android.support.v4.app.FragmentManager;
import android.support.design.widget.BottomSheetDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;

import cn.jpush.android.api.JPushInterface;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.points.autorepar.MainApplication;
import com.points.autorepar.common.Consts;
import com.points.autorepar.http.HttpManager;
import com.points.autorepar.sql.DBService;
import com.points.autorepar.bean.Contact;
import com.points.autorepar.utils.JSONOejectUtil;
import com.points.autorepar.utils.LoginUserUtil;
import com.umeng.analytics.MobclickAgent;
import com.points.autorepar.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class LoginActivity extends BaseActivity {

    private  final  String TAG = "LoginActivity";



    public static boolean isForeground = false;

    private  static  Integer OVERLAY_PERMISSION_REQ_CODE = 1000;

    private ImageView m_head;
    private Button m_registerBtn;
    private Button m_loginBtn;
    private Button m_forgetBtn;
    private LoginActivity m_this;

    private EditText mNameText;
    private EditText mPwdText;
    private CheckBox mRoleCheckBox;
    private TextView mVersion;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        m_this = this;
        setContentView(R.layout.activity_login);
        RelativeLayout bg = (RelativeLayout)findViewById(R.id.activity_login);
        m_container =  bg;
        distributViews();
        checkUpdate();
    }


    /**
     * 给xml控件分配变量
     */
    private  void  distributViews(){

        m_registerBtn = (Button) findViewById(R.id.login_register);
        m_registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(m_this,RegisterActivity.class);
                startActivity(intent);

            }
        });


        m_loginBtn = (Button) findViewById(R.id.login_login);
        m_loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                BottomSheetDialog dialog = new BottomSheetDialog(m_this);
//                View view = LayoutInflater.from(LoginActivity.this).inflate(R.layout.design_bottom_sheet_dialog1, null);
//                dialog.setContentView(view);
//                dialog.show();
                startLogin();

            }
        });


        mRoleCheckBox = (CheckBox) findViewById(R.id.checkBox);
        mRoleCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    mRoleCheckBox.setText("员工登录");
                }else{
                    mRoleCheckBox.setText("店主登录");
                }
            }
        });

        m_forgetBtn = (Button)findViewById(R.id.login_forget);
        m_forgetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forgetBtnClicked();
            }
        });

        mNameText = (EditText) findViewById(R.id.login_nameInut);
        mNameText.setText(LoginUserUtil.isEmployeeLogined(m_this)? LoginUserUtil.getEmployeeTel(m_this):LoginUserUtil.getTel(m_this));
        mPwdText = (EditText) findViewById(R.id.login_pwdInput);
        mPwdText.setText(LoginUserUtil.getPwd(m_this));

        if(MainApplication.getInstance().getUserType(LoginActivity.this)==2){
            mRoleCheckBox.setChecked(false);
        }else{
            mRoleCheckBox.setChecked(true);
        }

        mVersion = (TextView) findViewById(R.id.login_version);
        mVersion.setText(String.valueOf(Consts.getLocalVersionName(this)));
    }

    /**umeng统计
     *
     */
    public void onResume() {
        isForeground = true;
        super.onResume();
        MobclickAgent.onResume(this);
    }

    private  void checkUpdate(){
        Map map = new HashMap();
        HttpManager.getInstance(m_this).checkupVersion("/update/android", map, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {

                Log.e(TAG,"checkupVersion"+jsonObject.toString());
                if(jsonObject.optString("code").equals("1")){

                    JSONObject ret = jsonObject.optJSONObject("ret");

                    int version = Consts.getLocalVersion(LoginActivity.this);

                    if(ret == null)
                    {
                        return;
                    }
                    int newVersion =  ret.optInt("lastest");

                    boolean isNeedShow = ret.optString("isneedshow").equals("1");
                    if(newVersion > version && isNeedShow){

                        boolean isForceUp = ret.optString("isforceup").equals("1");
                        final AlertDialog.Builder normalDialog =
                                new AlertDialog.Builder(m_this);
                        normalDialog.setTitle("版本升级");
                        normalDialog.setCancelable(false);
                        normalDialog.setMessage(ret.optString("newfeature"));
                        normalDialog.setPositiveButton("确定",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        String url =   "https://autorepairhelper.oss-cn-hangzhou.aliyuncs.com/app/AutoRepairHelper.apk";
                                        Intent intent = new Intent();
                                        intent.setAction("android.intent.action.VIEW");
                                        Uri content_url = Uri.parse(url);
                                        intent.setData(content_url);
                                        startActivity(intent);
                                        finish();

                                    }
                                });


                        if(!isForceUp){
                            normalDialog.setNegativeButton("关闭",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            //...To-do
                                            startLogin();
                                        }
                                    });
                        }

                        // 显示
                        normalDialog.show();

                    }else{
                        startLogin();
                    }



                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                startLogin();
            }
        });
    }

    public void onPause() {
        isForeground = false;
        super.onPause();
        MobclickAgent.onPause(this);
    }


    /**
     * 关闭键盘
     */
    private void hideKeyboard(){
        InputMethodManager imm =  (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        if(imm != null) {
            imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(),0);
        }
    }

    /**
     * 登录
     */
    private  void startLogin(){
        if(mNameText.getText().length() == 0){
            Toast.makeText(getApplicationContext(),"用户名不能为空",Toast.LENGTH_SHORT).show();
            return;
        }

        if(mPwdText.getText().length() == 0){
            Toast.makeText(getApplicationContext(),"密码不能为空",Toast.LENGTH_SHORT).show();;
            return;
        }

        hideKeyboard();

        showWaitView();
        MainApplication mainApplication = (MainApplication) getApplication();
        boolean isfirstlogin =  LoginUserUtil.isFirstLogined(this);

        Map map = new HashMap();
        map.put("username", mNameText.getText().toString());
        map.put("tel", mNameText.getText().toString());
        map.put("pwd", mPwdText.getText().toString());
        map.put("udid", JPushInterface.getUdid(getApplicationContext()));
        map.put("ostype", mainApplication.consts().OS_TYPE);
        map.put("pushid", JPushInterface.getRegistrationID(getApplicationContext()));
        map.put("version", Consts.getLocalVersionName(LoginActivity.this));
        map.put("isfirstlogin",isfirstlogin? "1":"0");

        if(mRoleCheckBox.isChecked()){
            HttpManager.getInstance(this).employeeLogin("/employee/login2", map, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    Log.e(TAG,"/employee/login2"+jsonObject.toString());
                    Boolean isSucceed= jsonObject.optString("code").equals("1");
                    if(isSucceed){
                        processReturnedData(jsonObject.optJSONObject("ret"));
                    }
                    else {
                        stopWaitingView();
                        try {
                            Toast.makeText(getApplicationContext(),jsonObject.getString("msg"),Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    Handler mainHandler = new Handler(Looper.getMainLooper());
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            stopWaitingView();
                            Toast.makeText(getApplicationContext(),"登录失败",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }else {
            HttpManager.getInstance(this).startLogin("/users/login4", map, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    Log.e(TAG,"/users/login4"+jsonObject.toString());
                    Boolean isSucceed= jsonObject.optString("code").equals("1");
                    if(isSucceed){
                        processReturnedData(jsonObject.optJSONObject("ret"));
                    }
                    else {
                        stopWaitingView();
                        try {
                            Toast.makeText(getApplicationContext(),jsonObject.getString("msg"),Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {

                    Handler mainHandler = new Handler(Looper.getMainLooper());
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            stopWaitingView();
                            Toast.makeText(getApplicationContext(),"登录失败",Toast.LENGTH_SHORT).show();

                        }
                    });

                }

            });
        }

    }


     /**
      *设置用户本地标识,处理返回的数据
      */
    private void processReturnedData(JSONObject userInfoObject) {
        String key1 = getApplicationContext().getResources().getString(R.string.key_loginer_name);
        String key2 = getApplicationContext().getResources().getString(R.string.key_loginer_tel);
        String key3 = getApplicationContext().getResources().getString(R.string.key_loginer_level);
        String key4 = getApplicationContext().getResources().getString(R.string.key_loginer_device_modifed);
        String key5 = getApplicationContext().getResources().getString(R.string.key_loginer_pwd);
        String key6 = getApplicationContext().getResources().getString(R.string.key_loginer_headurl);
        String key7 = getApplicationContext().getResources().getString(R.string.key_loginer_address);
        String key8 = getApplicationContext().getResources().getString(R.string.key_loginer_shopname);
        String key9 = getApplicationContext().getResources().getString(R.string.key_loginer_isCreater);
        String key10 = getApplicationContext().getResources().getString(R.string.key_loginer_id);

        String key_roletype = getApplicationContext().getResources().getString(R.string.key_loginer_roletype);

        String key_iscanaddnewcontact = getApplicationContext().getResources().getString(R.string.key_loginer_iscanaddnewcontact);
        String key_iscandelcontact = getApplicationContext().getResources().getString(R.string.key_loginer_iscandelcontact);
        String key_iscaneditcontact = getApplicationContext().getResources().getString(R.string.key_loginer_iscaneditcontact);
        String key_iscankaidan = getApplicationContext().getResources().getString(R.string.key_loginer_iscankaidan);
        String key_iscanseecontactrepairs = getApplicationContext().getResources().getString(R.string.key_loginer_iscanseecontactrepairs);

        String key_isdirectadditem = getApplicationContext().getResources().getString(R.string.key_loginer_isdirectadditem);
        String key_isneeddispatch = getApplicationContext().getResources().getString(R.string.key_loginer_isneeddispatch);

        String key_emplyeeTel = getApplicationContext().getResources().getString(R.string.key_employee_tel);
        String key_emplyeePwd = getApplicationContext().getResources().getString(R.string.key_employee_pwd);


        SharedPreferences sp = getSharedPreferences("points", Context.MODE_PRIVATE);


        String pwd = userInfoObject.optString("pwd");
        String tel = userInfoObject.optString("tel");
        String name = userInfoObject.optString("username");
        String headUrl = userInfoObject.optString("headurl");
        String _id = userInfoObject.optString("_id");
        String roletype = userInfoObject.optString("roletype");

        String isdirectadditem = "0";
        String isneeddispatch = "0";
        String address = "";
        String shopname = "";
        String viplevel = "";
        String devicemodifyed = "0";

        String iscanaddnewcontact = "0";
        String iscandelcontact = "0";
        String iscaneditcontact = "0";
        String iscankaidan = "0";
        String iscanseecontactrepairs = "0";

        String isneeddirectaddcartype ="1";

        String vip = userInfoObject.optString("isVip");
        MainApplication.getInstance().setIsVip(LoginActivity.this,vip);
        if ("1".equalsIgnoreCase(vip)) {
            MainApplication.getInstance().setEndDeta(LoginActivity.this, "会员到期时间:"+userInfoObject.optString("endTime"));
        }else{
            MainApplication.getInstance().setEndDeta(LoginActivity.this, "到期时间:"+userInfoObject.optString("endTime"));
        }

        SharedPreferences.Editor editor= sp.edit();

        if(mRoleCheckBox.isChecked()){//员工登录

            String employee_tel = mNameText.getText().toString();
            String employee_pwd = mPwdText.getText().toString();
            editor.putString(key_emplyeeTel, employee_tel);
            editor.putString(key_emplyeePwd, employee_pwd);

            JSONObject creater = userInfoObject.optJSONObject("creater");
            if(creater != null){
                isdirectadditem = creater.optString("isdirectadditem");
                tel = creater.optString("tel");
                iscanaddnewcontact = userInfoObject.optString("iscanaddnewcontact");
                iscandelcontact = userInfoObject.optString("iscandelcontact");
                iscaneditcontact = userInfoObject.optString("iscaneditcontact");
                iscanseecontactrepairs = userInfoObject.optString("iscanseecontactrepairs");
                iscankaidan = userInfoObject.optString("iscankaidan");
                isneeddispatch = userInfoObject.optString("isneeddispatch");
                isneeddirectaddcartype = creater.optString("isneeddirectaddcartype");
                address              = creater.optString("address");
                shopname              = creater.optString("shopname");
                roletype               = userInfoObject.optString("roletype");

                MainApplication.getInstance().setUserType(LoginActivity.this,Integer.parseInt(roletype));
                MainApplication.getInstance().setCreatePushID(LoginActivity.this,userInfoObject.optString("pushid"));
            }

        }else {


             viplevel             = userInfoObject.optString("viplevel");
             devicemodifyed       = userInfoObject.optString("devicemodifyed");
             address              = userInfoObject.optString("address");
             shopname              = userInfoObject.optString("shopname");
            isneeddirectaddcartype = userInfoObject.optString("isneeddirectaddcartype");
             isdirectadditem      = userInfoObject.optString("isdirectadditem");
             isneeddispatch        = userInfoObject.optString("isneeddispatch");
             if("3".equalsIgnoreCase(roletype))
             {
                 MainApplication.getInstance().setUserType(LoginActivity.this,3);
             }else {
                 MainApplication.getInstance().setUserType(LoginActivity.this, 2);
             }

            MainApplication.getInstance().setCreatePushID(LoginActivity.this,userInfoObject.optString("pushid"));
        }
        MainApplication.getInstance().setisneeddirectaddcartype(LoginActivity.this,isneeddirectaddcartype);


        editor.putString(key1, name);

        editor.putString(key2, tel);

        editor.putString(key3, viplevel);

        editor.putString(key4, devicemodifyed);

        editor.putString(key5, pwd);

        editor.putString(key6, headUrl);

        editor.putString(key7, address);

        editor.putString(key8, shopname);

        editor.putString(key9, mRoleCheckBox.isChecked()?"0":"1");

        editor.putString(key10, _id);

        editor.putString(key_roletype, roletype);

        editor.putString("iscanaddnewcontact", iscanaddnewcontact);
        editor.putString(key_iscanaddnewcontact, iscanaddnewcontact);

        editor.putString("iscandelcontact", iscandelcontact);
        editor.putString(key_iscandelcontact, iscandelcontact);

        editor.putString("iscaneditcontact", iscaneditcontact);
        editor.putString(key_iscaneditcontact, iscaneditcontact);

        editor.putString("iscankaidan", iscankaidan);
        editor.putString(key_iscankaidan, iscankaidan);

        editor.putString("iscanseecontactrepairs", iscanseecontactrepairs);
        editor.putString(key_iscanseecontactrepairs, iscanseecontactrepairs);

        editor.putString(key_isdirectadditem, isdirectadditem);
        editor.putString(key_isneeddispatch, isneeddispatch);

        editor.commit();

        MainApplication.getInstance().setShopAD(LoginActivity.this,userInfoObject.optString("advtip"));
        MainApplication.getInstance().setworkstarttime(LoginActivity.this,userInfoObject.optString("workstarttime"));
        MainApplication.getInstance().setworkendtime(LoginActivity.this,userInfoObject.optString("workendtime"));

        MainApplication.getInstance().setpics(LoginActivity.this,userInfoObject.optString("pics"));
        MainApplication.getInstance().setprovicename(LoginActivity.this,userInfoObject.optString("provicename"));
        MainApplication.getInstance().setcityname(LoginActivity.this,userInfoObject.optString("cityname"));


        MainApplication.getInstance().setlocation(LoginActivity.this,userInfoObject.optString("location"));

        JSONArray ary = userInfoObject.optJSONArray("shopservice");
        String shopserviceID="";
        String shopserviceName = "";
        if(ary!=null) {
            for (int i = 0; i < ary.length();i++)
            {
                JSONObject obj = ary.optJSONObject(i);
                String strid = obj.optString("_id");
                String strname = obj.optString("name");
                if(i==0)
                {
                    shopserviceID = strid;
                    shopserviceName = strname;
                }else{
                    shopserviceID =shopserviceID+","+ strid;
                    shopserviceName = shopserviceName+" "+strname;
                }
            }
        }

        MainApplication.getInstance().setshopserviceID(LoginActivity.this,shopserviceID);
        MainApplication.getInstance().setshopserviceName(LoginActivity.this,shopserviceName);

        checkAsync(userInfoObject);
    }

    /**
     * 检查此次登录是否需要同步数据
     */
    private void  checkAsync(JSONObject userInfoObject){
        DBService.deleteAllContact();
        getAllContactsFromServer(userInfoObject.optString("creatertel"));
    }

    /**
     *  清除本地联系人数据,同时同步服务器上的所有联系人数据
     */
     private  void  getAllContactsFromServer(String tel){
         if(DBService.deleteAllContact()) {
             Map queryAllMap = new HashMap();
             queryAllMap.put("owner",  tel.length() == 0 ? LoginUserUtil.getTel(m_this) : tel);

             HttpManager.getInstance(m_this).queryAllContacts("/contact/queryAll", queryAllMap, new Response.Listener<JSONObject>() {
                 @Override
                 public void onResponse(JSONObject jsonObject) {


                     if (jsonObject.optInt("code") == 1) {

                         JSONArray arr = jsonObject.optJSONArray("ret");
                         if (arr.length() > 0) {
                             for (int i = 0; i < arr.length(); i++) {
                                 Contact conFromServer = new Contact();
                                 SQLiteDatabase db  = DBService.getDB();
                                 try {
                                     JSONObject obj = (JSONObject) arr.get(i);
                                     conFromServer.setCarCode(obj.optString("carcode").replace(" ", ""));
                                     conFromServer.setOwner(obj.optString("owner"));
                                     String cartype = obj.optString("cartype");
                                     if(cartype == null || cartype.equalsIgnoreCase("null"))
                                     {
                                         cartype = "";
                                     }
                                     conFromServer.setCarType(cartype);
                                     conFromServer.setName(obj.optString("name"));
                                     conFromServer.setTel(obj.optString("tel"));
                                     conFromServer.setIdfromnode(obj.optString("_id"));

                                     conFromServer.setInserttime(JSONOejectUtil.optString(obj,"inserttime"));
                                     conFromServer.setIsbindweixin(JSONOejectUtil.optString(obj,"isbindweixin"));
                                     conFromServer.setWeixinopenid(JSONOejectUtil.optString(obj,"weixinopenid"));
                                     conFromServer.setVin(JSONOejectUtil.optString(obj,"vin"));
                                     conFromServer.setCarregistertime(JSONOejectUtil.optString(obj,"carregistertime"));
                                     conFromServer.setHeadurl(JSONOejectUtil.optString(obj,"headurl"));


                                     conFromServer.setSafecompany(JSONOejectUtil.optString(obj,"safecompany"));
                                     conFromServer.setSafenexttime(JSONOejectUtil.optString(obj,"safenexttime"));
                                     conFromServer.setYearchecknexttime(JSONOejectUtil.optString(obj,"yearchecknexttime"));
                                     conFromServer.setTqTime1(JSONOejectUtil.optString(obj,"tqTime1"));
                                     conFromServer.setTqTime2(JSONOejectUtil.optString(obj,"tqTime2"));
                                     conFromServer.setCar_key(JSONOejectUtil.optString(obj,"key"));
                                     conFromServer.setIsVip(JSONOejectUtil.optString(obj,"isVip"));
                                     conFromServer.setSafecompany3(JSONOejectUtil.optString(obj, "safecompany3"));
                                     conFromServer.setTqTime3(JSONOejectUtil.optString(obj, "tqTime3"));
                                     conFromServer.setSafenexttime3(JSONOejectUtil.optString(obj, "safenexttime3"));
                                     conFromServer.setSafetiptime3(JSONOejectUtil.optString(obj, "safetiptime3"));

                                     DBService.addNewContact(conFromServer,db);
                                 } catch (JSONException e) {
                                     e.printStackTrace();
                                 }

//                                  DBService.closeDB(db);
                             }
                         }

                         String key = getApplicationContext().getResources().getString(R.string.KEY_loginer_IS_FIRST_LOGIN);
                         SharedPreferences sp = getSharedPreferences("points", Context.MODE_PRIVATE);
                         SharedPreferences.Editor editor= sp.edit();
                         editor.putString(key, "0");
                         editor.commit();

                         stopWaitingView();

//                         HomeMenuActivity
                         Intent intent = new Intent(getBaseContext(),MainTabbarActivity.class);
                         startActivity(intent);
                     }


                 }
             }, new Response.ErrorListener() {
                 @Override
                 public void onErrorResponse(VolleyError volleyError) {
                     Log.e(TAG, "contact/queryAll:onErrorResponse " + volleyError);

                     stopWaitingView();

                     Toast.makeText(getApplicationContext(),"登录失败",Toast.LENGTH_SHORT).show();

                 }
             });
         }
     }


    private void forgetBtnClicked(){
        Intent intent = new Intent(getBaseContext(),ResetPwdActivity.class);
        startActivity(intent);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1000) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(this)) {

                }
            }
        }
    }

//    @Override
//    public boolean onKeyDown(int keyCode,KeyEvent event){
//        if(keyCode== KeyEvent.KEYCODE_BACK)
//            return true;//不执行父类点击事件
//        return super.onKeyDown(keyCode, event);//继续执行父类其他点击事件
//    }


}
