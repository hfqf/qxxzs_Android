package com.points.autorepar.activity.Store;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.jph.takephoto.model.InvokeParam;
import com.jph.takephoto.model.TContextWrap;
import com.jph.takephoto.permission.PermissionManager;
import com.points.autorepar.R;
import com.points.autorepar.activity.BaseActivity;
import com.points.autorepar.bean.ADTReapirItemInfo;
import com.points.autorepar.bean.ServiceManageSubInfo;
import com.points.autorepar.http.HttpManager;
import com.points.autorepar.utils.LoginUserUtil;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 *desc  添加客户页面
 *class ContactAddNewActivity.java
 *author Points
 *email hfqf123@126.com
 *created 16/11/30 上午9:53
 */
public class AddServiceSubManageActivity extends BaseActivity  {
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


    private String  toptypeid;
    private String id;
    private Button     mAddBtn;
    private Button     mBackBtn;
    private ArrayList<ServiceManageSubInfo> subtypelist;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        subtypelist = new ArrayList<ServiceManageSubInfo>();
        setContentView(R.layout.activity_add_servicesubmanage);
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
        title.setText("子类");


        id = "";
        applycompany_et = (EditText)this.findViewById(R.id.applycompany);


        type =  getIntent().getIntExtra("type",1);


        toptypeid = getIntent().getStringExtra("toptypeid");
        subtypelist =  (ArrayList<ServiceManageSubInfo>)getIntent().getExtras().getSerializable("subtypelist");
        if(type ==2 )
        {
            String name = (String)getIntent().getStringExtra("name");
            id = (String)getIntent().getStringExtra("id");


            if(name == null)
            {
                return;
            }
            applycompany_et.setText(name);


            common_navi_add.setText("修改");
        }else{
            common_navi_add.setText("添加");
        }



    }

    ///提交添加
    private void   submitBtnClicked(){

        if (applycompany_et.getText().length() == 0) {
            Toast.makeText(getApplicationContext(), "名称不能为空", Toast.LENGTH_SHORT).show();
            return;
        }




        Map map = new HashMap();

        map.put("toptypeid", toptypeid);
        map.put("name", applycompany_et.getText().toString());

        map.put("owner", LoginUserUtil.getTel(AddServiceSubManageActivity.this));

        String url = "";

        if(type == 1)
        {
            url = "/warehousegoodssubtype/add2";
        }else{
            url = "/warehousegoodssubtype/update";
            map.put("id", id);
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
