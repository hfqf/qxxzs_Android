package com.points.autorepar.activity;

import android.Manifest;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;

import android.app.FragmentTransaction;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;


import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.baidu.ocr.sdk.OCR;
import com.baidu.ocr.sdk.OnResultListener;
import com.baidu.ocr.sdk.exception.OCRError;
import com.baidu.ocr.sdk.model.AccessToken;

import com.points.autorepar.MainApplication;
import com.points.autorepar.R;
import com.points.autorepar.activity.Store.AddPurchaseActivity;
import com.points.autorepar.activity.contact.ContactAddNewActivity;
import com.points.autorepar.activity.contact.SelectContactActivity;
import com.points.autorepar.bean.Contact;
import com.points.autorepar.bean.RepairHistory;
import com.points.autorepar.fragment.ContactFragment;
import com.points.autorepar.fragment.HomemenuFragment;
import com.points.autorepar.fragment.QueryFragment;
import com.points.autorepar.fragment.RepairFragment;
import com.points.autorepar.fragment.SettingFragment;
import android.net.Uri;
import android.widget.TextView;
import android.widget.Toast;

import com.points.autorepar.fragment.WorkRoomFragment;
import com.points.autorepar.http.HttpManager;
import com.points.autorepar.lib.BluetoothPrinter.PrinterActivity;
import com.points.autorepar.lib.BluetoothPrinter.util.ToastUtil;
import  com.points.autorepar.lib.ocr.ui.camera.CameraActivity;
import  com.points.autorepar.lib.ocr.ui.camera.FileUtil;
import com.points.autorepar.sql.DBService;
import com.points.autorepar.utils.LoginUserUtil;
import com.points.autorepar.zxing.activity.CaptureActivity;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMWeb;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import com.points.autorepar.fragment.MyUnFinishedRepairFragment;


public class MainTabbarActivity extends BaseActivity implements
        OnClickListener,
        ContactFragment.OnContactFragmentListener,
        QueryFragment.OnFragmentInteractionListener,
        RepairFragment.OnFragmentInteractionListener,
        SettingFragment.OnFragmentInteractionListener,
        PublishWindow.OnPublishWindowListener ,
        WorkRoomFragment.OnWorkRoomFragmentInteractionListener,
        MyUnFinishedRepairFragment.MyUnFinishedRepairFragmentInteractionListener{

    private  final  String TAG = "MainTabbarActivity";

    private HomemenuFragment m_tab0;
    private MyUnFinishedRepairFragment m_tab0_1;
    private SettingFragment  m_tab1;


    //几个滑动页面布局
    private LinearLayout  m_tab0Layout;
    private LinearLayout  m_tab1Layout;

    /*
     *fragment管理
     */
    private FragmentManager fragmentManager;


    ImageButton imgbtn_add;
    PublishWindow publishWindow;
    private  MainTabbarActivity m_this;

    private  int m_lastIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        m_this = this;

        MainTabbarActivity hander = (MainTabbarActivity)this;
        MainApplication mainApplication = (MainApplication) hander.getApplication();
        mainApplication.m_mainActivity = hander;

        setContentView(R.layout.activity_main_tabbar);
        addMuneViews();
        initViews();
        fragmentManager = getFragmentManager();
        setTabSelection(0);
        initAccessTokenWithAkSk();
        m_lastIndex = 0;

        checkPermission();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        getTakePhoto().onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    private void initAccessTokenWithAkSk() {
        OCR.getInstance(MainTabbarActivity.this).initAccessTokenWithAkSk(new OnResultListener<AccessToken>() {
            @Override

            public void onResult(AccessToken result) {
                String token = result.getAccessToken();
                Log.e(TAG,"initAccessTokenWithAkSk 成功"+token);
//                 OCR.getInstance(MainTabbarActivity.this).initWithToken(getApplicationContext(), token);

            }

            @Override
            public void onError(OCRError error) {
                error.printStackTrace();
                Log.e(TAG,"initAccessTokenWithAkSk 失败"+error.getMessage());
            }
        }, getApplicationContext(), "FfLAN2FH6eA0FqRszE2g6Wl7", "7nVfrr8KK5OiGkQs4W6xvgr2LHogFLyd");
    }


    /**
     * 增加底部的快捷功能页面
     */

    private  void addMuneViews(){
        imgbtn_add = (ImageButton)findViewById(R.id.main_btn_add);
        if(LoginUserUtil.isEmployeeLogined(MainTabbarActivity.this)) {
            if (MainApplication.getInstance().getUserType(MainTabbarActivity.this) == 2){
                imgbtn_add.setVisibility(View.INVISIBLE);
            }

            if (MainApplication.getInstance().getUserType(MainTabbarActivity.this) == 1){
                imgbtn_add.setVisibility(LoginUserUtil.isCanAddNewRepair(MainTabbarActivity.this)?View.VISIBLE:View.INVISIBLE);
            }
        }

        imgbtn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                publishWindow = new PublishWindow(MainTabbarActivity.this);
                publishWindow.showAtLocation(getWindow().getDecorView(),
                        Gravity.BOTTOM, 0,0);
            }
        });
    }

    @Override
    public void  onPublishWindowSelectedIndex(int ViewId){

        Log.e(TAG,"onPublishWindowSelectedIndex"+ViewId);
        switch (ViewId){
            case 0:{
                startSelectLicensePlatePicToUpload(3, new speUploadLicensePlateListener() {
                    @Override
                    public void onUploadLicensePlatePicSucceed(String licensePlate) {
                        licensePlate = licensePlate.replace("|","1");
                        checkNewCarcode(licensePlate);
                    }
                });
                break;
            }
            case 1:{
                Intent intent = new Intent(this, CameraActivity.class);
                intent.putExtra(CameraActivity.KEY_OUTPUT_FILE_PATH,
                        FileUtil.getSaveFile(getApplication()).getAbsolutePath());
                intent.putExtra(CameraActivity.KEY_CONTENT_TYPE,
                        CameraActivity.CONTENT_TYPE_GENERAL);
                startActivityForResult(intent, REQUEST_CODE_VEHICLE_LICENSE);
                break;
            }
            case 2:{
                Intent intent=new Intent(this, CaptureActivity.class);
                startActivityForResult(intent,REQUEST_CODE);
                break;
            }
            default:
            {
                break;
            }
        }
    }

    /*
     * private functions
     */

    private void initViews(){


        m_tab0Layout = (LinearLayout) findViewById(R.id.id_tab_bottom_workroom);
        m_tab1Layout = (LinearLayout) findViewById(R.id.id_tab_bottom_setting);

        m_tab0Layout.setOnClickListener(this);
        m_tab1Layout.setOnClickListener(this);
    }


    private void resetBtn()
    {
        Resources resource = (Resources) getBaseContext().getResources();
        ColorStateList csl = (ColorStateList) resource.getColorStateList(R.color.colorTabbarTitleNormal);
        ((TextView) m_tab0Layout.findViewById(R.id.tabbar_text_0)).setTextColor(csl);
        ((TextView) m_tab1Layout.findViewById(R.id.tabbar_text_1)).setTextColor(csl);
    }


    private void hideFragments(FragmentTransaction transaction)
    {

        if(MainApplication.getInstance().getUserType(MainTabbarActivity.this) ==2 || MainApplication.getInstance().getUserType(MainTabbarActivity.this) ==3) {
            if (m_tab0 != null) {
                transaction.hide(m_tab0);
            }

        }else{
            if (m_tab0_1 != null) {
                transaction.hide(m_tab0_1);
            }

        }
        if (m_tab1 != null) {
            transaction.hide(m_tab1);
        }

    }

    private void setTabSelection(int index)
    {
        m_lastIndex = index;
        //初始化界面
        resetBtn();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        hideFragments(transaction);

        switch (index)
        {
            case 0:
            {
                Resources resource = (Resources) getBaseContext().getResources();
                ColorStateList csl = (ColorStateList) resource.getColorStateList(R.color.material_blue);
                ColorStateList cs2 = (ColorStateList) resource.getColorStateList(R.color.colorCommon);
                ((TextView) m_tab0Layout.findViewById(R.id.tabbar_text_0)).setTextColor(csl);
                ((TextView) m_tab1Layout.findViewById(R.id.tabbar_text_1)).setTextColor(cs2);

                Drawable img0_on =  getBaseContext().getResources().getDrawable(R.drawable.home_gzt_on);
                Drawable img1_un =  getBaseContext().getResources().getDrawable(R.drawable.home_set_un);
                ((ImageButton) m_tab0Layout.findViewById(R.id.btn_tab_bottom_icon_0)).setBackground(img0_on);
                ((ImageButton) m_tab1Layout.findViewById(R.id.btn_tab_bottom_icon_1)).setBackground(img1_un);
                boolean isEmployee = LoginUserUtil.isEmployeeLogined(m_this);
                if(!isEmployee ||
                        (isEmployee && MainApplication.getInstance().getUserType(m_this) !=1) ) {
                    if(m_tab0 == null)
                    {

                            m_tab0 = new HomemenuFragment();
                            m_tab0.SetDelegate(this);
                            m_tab0.setListener(new HomemenuFragment.HomemenuFragmentListener() {
                                @Override
                                public void onHomemenuFragmentSelectedIndex(int index) {


                                }
                            });


                        transaction.add(R.id.id_content,m_tab0);
                    }
                    else
                    {
                        transaction.show(m_tab0);
                    }
                }else{

                    if(m_tab0_1 == null)
                    {

                        m_tab0_1 = new MyUnFinishedRepairFragment();
                        m_tab0_1.SetDelegate(this);

                        m_tab0_1.setListener(new MyUnFinishedRepairFragment.MyUnFinishedRepairFragmentInteractionListener() {
                            @Override
                            public void onMyUnFinishedRepairFragmentSelectedIndex(int index) {


                            }
                        });

                        transaction.add(R.id.id_content,m_tab0_1);
                    }
                    else
                    {
                        transaction.show(m_tab0_1);
                    }

                }
                break;
            }

            case 1:
            {
                Resources resource = (Resources) getBaseContext().getResources();
                ColorStateList csl = (ColorStateList) resource.getColorStateList(R.color.material_blue);
                ColorStateList cs2 = (ColorStateList) resource.getColorStateList(R.color.colorCommon);
                ((TextView) m_tab0Layout.findViewById(R.id.tabbar_text_0)).setTextColor(cs2);
                ((TextView) m_tab1Layout.findViewById(R.id.tabbar_text_1)).setTextColor(csl);

                Drawable img0_un =  getBaseContext().getResources().getDrawable(R.drawable.home_gzt_un);
                Drawable img1_on =  getBaseContext().getResources().getDrawable(R.drawable.home_set_on);
                ((ImageButton) m_tab0Layout.findViewById(R.id.btn_tab_bottom_icon_0)).setBackground(img0_un);
                ((ImageButton) m_tab1Layout.findViewById(R.id.btn_tab_bottom_icon_1)).setBackground(img1_on);
                if(m_tab1 == null)
                {
                    m_tab1 = new SettingFragment();
                    transaction.add(R.id.id_content,m_tab1);
                    m_tab1.SetDelegate(this);
                }
                else
                {
                    transaction.show(m_tab1);
                }
                break;
            }

            default:
                break;
        }
        transaction.commit();

        imgbtn_add.bringToFront();

    }

    @Override
    public void onClick(View v)
    {
       switch (v.getId())
       {
           case R.id.id_tab_bottom_workroom:
           {
               setTabSelection(0);
               break;
           }
           case R.id.id_tab_bottom_setting:
           {
               setTabSelection(1);
               break;
           }
           default:
               break;

       }
        imgbtn_add.bringToFront();

    }

    /*
     *各个fragmenet回调处理
     */

    //OnContactFragmentListener
    @Override
    public void onContactFramentReloadData() {

    }

    @Override
    public void onSelectedContact(Contact contact){

    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void  onLogout(){

        new android.app.AlertDialog.Builder(this)
                .setTitle("确认退出应用?")
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
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

    @Override
    public void onShareXZS(){
        UMWeb web = new UMWeb("http://sj.qq.com/myapp/detail.htm?apkName=com.points.autorepar");
        web.setDescription("让汽修管理更加轻松");
        web.setTitle("汽修小助手安卓");
        new ShareAction(MainTabbarActivity.this)
                .withText("汽修小助手安卓")
                .setDisplayList(SHARE_MEDIA.QQ, SHARE_MEDIA.WEIXIN)
                .withMedia(web)
                .setCallback(umShareListener)
                .open();
    }

    @Override
    public void onStartSelectContact(){


    }

    //QueryFragment.OnFragmentInteractionListener
    @Override
    public void onQueryFragmentSharePrint(String url){

        Toast.makeText(MainTabbarActivity.this, "分享出去的就是统计数据的网址,用电脑打开即可打印数据", Toast.LENGTH_SHORT).show();


        UMWeb web = new UMWeb(url);
        web.setTitle("维修记录统计");
        web.setDescription("电脑打开地址即可打印");
        new ShareAction(MainTabbarActivity.this)
                .withText("统计数据打印与分享")
                .withSubject("汽修小助手")
                .withFollow("汽修小助手")
                .setDisplayList(SHARE_MEDIA.QQ, SHARE_MEDIA.WEIXIN)
                .withMedia(web)
                .setCallback(umShareListener)
                .open();
    }






    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

    private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onStart(SHARE_MEDIA platform) {
            //分享开始的回调
        }
        @Override
        public void onResult(SHARE_MEDIA platform) {
            Log.d("plat","platform"+platform);
            Toast.makeText(MainTabbarActivity.this, platform + " 分享成功啦", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            Toast.makeText(MainTabbarActivity.this,platform + " 分享失败啦", Toast.LENGTH_SHORT).show();
            Log.e(TAG,"throw:");
            if(t!=null){
                Log.e(TAG,"throw:"+t.getMessage());
            }
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            Toast.makeText(MainTabbarActivity.this,platform + " 分享取消了", Toast.LENGTH_SHORT).show();
        }
    };


    /**umeng统计
     *
     */

    public void onResume() {
        super.onResume();
        Log.e(TAG,"onResume");
        setTabSelection(m_lastIndex);
        MobclickAgent.onResume(this);
    }
    public void onPause() {
        super.onPause();
        Log.e(TAG,"onPause");
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG,"onDestroy");
        OCR.getInstance(this).release();
    }

    /**
     * 设置页的上传图片
     * 不错的图片选择控件
     * https://github.com/crazycodeboy/TakePhoto#%E8%A3%81%E5%89%AA%E5%9B%BE%E7%89%87
     */

    public void startChangeHeadImage(){

        startSelectPicToUpload(0, new speUploadListener() {
            @Override
            public void uploadContactSucceed(String newHeadUrl) {

            }

            @Override
            public void uploadUserSucceed(String newHeadUrl) {

                final String  fileName = newHeadUrl;
                Map map = new HashMap();
                map.put("headurl", fileName);
                map.put("tel", LoginUserUtil.getTel(getApplicationContext()));
                HttpManager.getInstance(getApplicationContext()).updateHeadUrl("/users/update", map, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        if(jsonObject.optInt("code") == 1){

                            Log.e(TAG,"/onResponse:"+jsonObject.toString());
                            mQueue.getCache().clear();
                            mQueue.getCache().remove(LoginUserUtil.gethHeadUrl(m_this));


                            String headUrl =  fileName;
                            String key6 = getApplicationContext().getResources().getString(R.string.key_loginer_headurl);
                            SharedPreferences sp6 = getSharedPreferences("points", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor6= sp6.edit();
                            editor6.putString(key6, headUrl);
                            editor6.commit();

                            String url = MainApplication.consts(m_this).BOS_SERVER+fileName;
                            Log.e(TAG,"/users/update:"+url);
                            m_tab1.refreshHead(url);
                        }
                        else {
//                                Toast.makeText(getActivity(), "更新失败", Toast.LENGTH_SHORT).show();
                        }


                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
//                            m_parentActivity.dismissDialog();
//                            Toast.makeText(getActivity(), "更新失败", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

    }

    @Override
    public boolean onKeyDown(int keyCode,KeyEvent event){
        if(keyCode== KeyEvent.KEYCODE_BACK) {
            onLogout();
            return true;//不执行父类点击事件
        }
        return super.onKeyDown(keyCode, event);//继续执行父类其他点击事件
    }


    @Override
    public void onMyUnFinishedRepairFragmentSelectedIndex(int index) {

    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(MainTabbarActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // 进入这儿表示没有权限
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainTabbarActivity.this, Manifest.permission.CAMERA)) {
                Toast.makeText(MainTabbarActivity.this, "请打开应用的相机权限,否则使用某些功能会导致程序异常", Toast.LENGTH_LONG).show();
            } else {
                ActivityCompat.requestPermissions(MainTabbarActivity.this, new String[]{Manifest.permission.CAMERA}, 100);
            }
        }
    }


}
