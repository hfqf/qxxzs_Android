package com.points.autorepar.activity.Store;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.baidu.ocr.sdk.OCR;
import com.baidu.ocr.sdk.OnResultListener;
import com.baidu.ocr.sdk.exception.OCRError;
import com.baidu.ocr.sdk.model.OcrRequestParams;
import com.baidu.ocr.sdk.model.OcrResponseResult;
import com.jph.takephoto.app.TakePhoto;
import com.jph.takephoto.app.TakePhotoImpl;
import com.jph.takephoto.compress.CompressConfig;
import com.jph.takephoto.model.CropOptions;
import com.jph.takephoto.model.InvokeParam;
import com.jph.takephoto.model.TContextWrap;
import com.jph.takephoto.model.TResult;
import com.jph.takephoto.permission.PermissionManager;
import com.jph.takephoto.permission.TakePhotoInvocationHandler;
import com.points.autorepar.MainApplication;
import com.points.autorepar.R;
import com.points.autorepar.activity.BaseActivity;
import com.points.autorepar.activity.CommonWebviewActivity;
import com.points.autorepar.activity.workroom.WorkRoomEditActivity;
import com.points.autorepar.bean.ADTReapirItemInfo;
import com.points.autorepar.bean.CompanyItemInfo;
import com.points.autorepar.bean.Contact;
import com.points.autorepar.bean.RepairHistory;
import com.points.autorepar.http.HttpManager;
import com.points.autorepar.lib.ocr.ui.camera.CameraActivity;
import com.points.autorepar.lib.ocr.ui.camera.FileUtil;
import com.points.autorepar.lib.ocr.ui.camera.RecognizeService;
import com.points.autorepar.sql.DBService;
import com.points.autorepar.utils.DateUtil;
import com.points.autorepar.utils.LoggerUtil;
import com.points.autorepar.utils.LoginUserUtil;
import com.umeng.analytics.MobclickAgent;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *desc  添加客户页面
 *class ContactAddNewActivity.java
 *author Points
 *email hfqf123@126.com
 *created 16/11/30 上午9:53
 */
public class AddSuppyCompanyActivity extends BaseActivity  {
    private final  String TAG  = "ContactAddNewActivity";
    /*
     *私有属性
     *
     */
    private EditText   applycompany_et;
    private EditText   name_et;
    private EditText   tel_et;
    private EditText address_et;
    private EditText mark_et;
    private int  type = 1;


    private String id;
    private Button     mAddBtn;
    private Button     mBackBtn;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_suppycompany);
        Window window = getWindow();
//设置透明状态栏,这样才能让 ContentView 向上
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

//需要设置这个 flag 才能调用 setStatusBarColor 来设置状态栏颜色
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        Button common_navi_back = (Button)findViewById(R.id.common_navi_back);
        common_navi_back.setVisibility(View.VISIBLE);
        common_navi_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Button common_navi_add = (Button)findViewById(R.id.common_navi_add);
        common_navi_add.setVisibility(View.VISIBLE);
        common_navi_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitBtnClicked();
            }
        });

        TextView title = (TextView)findViewById(R.id.common_navi_title) ;
        title.setText("代理商信息");


        id = "";
        applycompany_et = (EditText)this.findViewById(R.id.applycompany);
        name_et = (EditText)this.findViewById(R.id.name);
        tel_et = (EditText)this.findViewById(R.id.tel);
        address_et = (EditText)this.findViewById(R.id.address);
        mark_et = (EditText)this.findViewById(R.id.mark);

        type =  getIntent().getIntExtra("type",1);

        if(type ==2 )
        {
            CompanyItemInfo info = (CompanyItemInfo)getIntent().getParcelableExtra("data");

            if(info == null)
            {
                return;
            }
            applycompany_et.setText(info.suppliercompanyname);
            name_et.setText(info.managername);
            tel_et.setText(info.tel);
            address_et.setText(info.address);
            mark_et.setText(info.remark);
            id = info.CompanyId;
            common_navi_add.setText("修改");
        }else{
            common_navi_add.setText("添加");
        }


    }

    ///提交添加
    private void   submitBtnClicked(){

        if (applycompany_et.getText().length() == 0) {
            Toast.makeText(getApplicationContext(), "代理商名不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        if (name_et.getText().length() ==0) {
            Toast.makeText(getApplicationContext(), "联系人不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        if (tel_et.getText().length() ==0) {
            Toast.makeText(getApplicationContext(), "手机号码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }


        Map map = new HashMap();
        map.put("suppliercompanyname", applycompany_et.getText().toString());
        map.put("managername", name_et.getText().toString());
        map.put("tel", tel_et.getText().toString());
        map.put("address", address_et.getText().toString());
        map.put("remark", mark_et.getText().toString());
        map.put("id", id);

        map.put("owner", LoginUserUtil.getTel(AddSuppyCompanyActivity.this));

        String url = "/warehousesupplier/add";

        if(type == 1)
        {
            url = "/warehousesupplier/add";
        }else{
            url = "/warehousesupplier/update";
        }

        showWaitView();
        HttpManager.getInstance(this).addContact(url,
                        map,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {

                                stopWaitingView();
                                if(response.optInt("code") == 1 )
                                {
                                    Toast.makeText(getApplicationContext(),"操作成功",Toast.LENGTH_SHORT).show();
                                    finish();
                                }else{
                                    Toast.makeText(getApplicationContext(),"操作失败",Toast.LENGTH_SHORT).show();
                                }

                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e(TAG, error.getMessage(), error);
                                Handler mainHandler = new Handler(Looper.getMainLooper());
                                mainHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        //已在主线程中，可以更新UI
                                        stopWaitingView();
                                        Toast.makeText(getApplicationContext(),"操作失败",Toast.LENGTH_SHORT).show();

                                    }
                                });

                            }
                        });

    }


    /**umeng统计
     *
     */

    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);

    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }


    @Override
    public PermissionManager.TPermissionType invoke(InvokeParam invokeParam) {
        PermissionManager.TPermissionType type= PermissionManager.checkPermission(TContextWrap.of(this),invokeParam.getMethod());
        if(PermissionManager.TPermissionType.WAIT.equals(type)){
//            this.invokeParam=invokeParam;
        }
        return type;
    }




}
