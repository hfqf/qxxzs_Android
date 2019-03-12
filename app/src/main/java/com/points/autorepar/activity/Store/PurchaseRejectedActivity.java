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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.jph.takephoto.model.InvokeParam;
import com.jph.takephoto.model.TContextWrap;
import com.jph.takephoto.permission.PermissionManager;
import com.points.autorepar.MainApplication;
import com.points.autorepar.R;
import com.points.autorepar.activity.BaseActivity;
import com.points.autorepar.adapter.PurchaseRejectedAdapter;
import com.points.autorepar.bean.PurchaseRejectedInfo;
import com.points.autorepar.http.HttpManager;
import com.points.autorepar.utils.LoginUserUtil;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 *desc  添加客户页面
 *class ContactAddNewActivity.java
 *author Points
 *email hfqf123@126.com
 *created 16/11/30 上午9:53
 */
public class PurchaseRejectedActivity extends BaseActivity  {
    private final  String TAG  = "ContactAddNewActivity";
    /*
     *私有属性
     *
     */
    private EditText outnum;
    private int  type = 1;

    private PurchaseRejectedInfo info;

    private String id;
    private Button     mAddBtn;
    private Button     mBackBtn;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_purchaserejected);
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
        common_navi_add.setText("保存");
        common_navi_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitBtnClicked();
            }
        });

        TextView title = (TextView)findViewById(R.id.common_navi_title) ;
        title.setText("库存退货");

        outnum = (EditText)findViewById(R.id.outnum);


        info = getIntent().getParcelableExtra("PurchaseRejectedInfo");
        if(info != null)
        {
            final String url = MainApplication.consts(PurchaseRejectedActivity.this).BOS_SERVER+info.good_headurl+".png";
            final BaseActivity activity = PurchaseRejectedActivity.this;


            TextView name = (TextView)findViewById(R.id.name) ;
            name.setText("配件名："+info.good_name+"  编码："+info.good_barcode);

            TextView price = (TextView)findViewById(R.id.price) ;
            price.setText("￥" +info.price+"(系统价格)");

            TextView num = (TextView)findViewById(R.id.num) ;
            num.setText("x"+info.num);

           final ImageView m_head = (ImageView)findViewById(R.id.headurl);
            Handler mainHandler = new Handler(Looper.getMainLooper());
            mainHandler.post(new Runnable() {
                @Override
                public void run() {

                    activity.imageLoader.get(url, new ImageLoader.ImageListener() {
                        @Override
                        public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {
                            m_head.setImageBitmap(imageContainer.getBitmap());
                        }
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            m_head.setImageResource(R.drawable.appicon);
                        }
                    },200,200);
                }
            });
        }
//        id = "";
//        applycompany_et = (EditText)this.findViewById(R.id.applycompany);
//
//
//        type =  getIntent().getIntExtra("type",1);
//
//        if(type ==2 )
//        {
//            String name = (String)getIntent().getStringExtra("name");
//            id = (String)getIntent().getStringExtra("id");
//
//
//            if(name == null)
//            {
//                return;
//            }
//            applycompany_et.setText(name);
//
//            common_navi_add.setText("修改");
//        }else{
//            common_navi_add.setText("添加");
//        }


    }

    ///提交添加
    private void   submitBtnClicked(){

        if (outnum.getText().length() == 0) {
            Toast.makeText(getApplicationContext(), "数量不能为空", Toast.LENGTH_SHORT).show();
            return;
        }


        int i_curnum =Integer.valueOf(info.num);
       final int i_outnum =Integer.valueOf(outnum.getText().toString());

        if(i_outnum>i_curnum )
        {
            Toast.makeText(getApplicationContext(), "退货数量不能大于库存数量", Toast.LENGTH_SHORT).show();
            return;
        }
       final int num =i_curnum-i_outnum;

        Map map = new HashMap();
        map.put("num", num);

        map.put("id",info.id);

        String url = "/warehousegoods/reject";


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
                                    submitBtnClicked2(i_outnum);
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


    ///提交添加
    private void   submitBtnClicked2(int  num){






        Map map = new HashMap();
        map.put("num", num);

        map.put("goods",info.id);
        map.put("owner",LoginUserUtil.getTel(PurchaseRejectedActivity.this));
        map.put("type","4");
        map.put("remark","");
        map.put("dealer",LoginUserUtil.getUserId(PurchaseRejectedActivity.this));

        String url = "/warehousegoodsinoutrecord/add";


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
