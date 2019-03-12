package com.points.autorepar.activity.Store;

import android.content.Intent;
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
import com.points.autorepar.bean.PurchaseRejectedInfo;
import com.points.autorepar.http.HttpManager;
import com.points.autorepar.utils.ImgAdapter;
import com.points.autorepar.utils.LoginUserUtil;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import me.nereo.multi_image_selector.MultiImageSelectorActivity;

/**
 *desc  添加客户页面
 *class ContactAddNewActivity.java
 *author Points
 *email hfqf123@126.com
 *created 16/11/30 上午9:53
 */
public class InPurchaseActivity extends BaseActivity  {
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
    private TextView  applyname;
    private String applyname_str,applyname_id;
    private EditText  applyprice,applynum;
    private int REQUEST_APPLY = 1002;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_in_purchaser);
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
        title.setText("入库");

        applyname = (TextView)findViewById(R.id.applyname) ;
        applyname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InPurchaseActivity.this,SelectSuppyCompanyListActivity.class);
                intent.putExtra("PurchaseRejectedInfo",info);
                startActivityForResult(intent,REQUEST_APPLY);
            }
        });
        applyprice = (EditText)findViewById(R.id.applyprice);
        applynum = (EditText)findViewById(R.id.applynum);



        info = getIntent().getParcelableExtra("PurchaseRejectedInfo");
        if(info != null)
        {
            final String url = MainApplication.consts(InPurchaseActivity.this).BOS_SERVER+info.good_headurl+".png";
            final BaseActivity activity = InPurchaseActivity.this;


            TextView name = (TextView)findViewById(R.id.name) ;
            name.setText("商品名称："+info.good_name);


            TextView price = (TextView)findViewById(R.id.price) ;
            String good_barcode = "";
            if(info.good_barcode.length() == 0)
            {
                good_barcode = "暂无";
            }else{
                good_barcode = info.good_barcode;
            }
            price.setText("条形码："+good_barcode);

//            TextView num = (TextView)findViewById(R.id.num) ;
//            num.setText("x"+info.num);

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

        if (applyname.getText().length() == 0) {
            Toast.makeText(getApplicationContext(), "供应商不能为空", Toast.LENGTH_SHORT).show();
            return;
        }




        Map map = new HashMap();

        /**
         * 为了兼顾之前错误逻辑,判断之前的系统价格为0，之前的数量不为0时，直接用当前价格作为系统价格，数量正常相加
         */

        int newNum=0;
        int newPrice=0;
        if(Integer.parseInt(info.num)!=0 &&Integer.parseInt(info.price)==0){
            newNum = Integer.parseInt(info.num)+Integer.parseInt(applynum.getText().toString());
            newPrice = Integer.parseInt(applyprice.getText().toString());
        }else {
             newNum = Integer.parseInt(info.num)+Integer.parseInt(applynum.getText().toString());

             newPrice =  (Integer.parseInt(info.num)*Integer.parseInt(info.price)+Integer.parseInt(applynum.getText().toString())*Integer.parseInt(applyprice.getText().toString()))/newNum;
        }

        map.put("id",info.id);
        map.put("num",newNum);
        map.put("systemprice",String.valueOf(newPrice));
        map.put("purchasenum","0");


        String url = "/warehousegoods/savebuyed";


        showWaitView();
        HttpManager.getInstance(this).addContact(url,
                        map,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {

                                stopWaitingView();
                                if(response.optInt("code") == 1 )
                                {
//                                    Toast.makeText(getApplicationContext(),"操作成功",Toast.LENGTH_SHORT).show();
//                                    finish();
                                    submitBtnClicked2();
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

    private void   submitBtnClicked2(){

        Map map = new HashMap();
        map.put("dealer",LoginUserUtil.getUserId(InPurchaseActivity.this));
        map.put("goods",info.id);
        map.put("owner", LoginUserUtil.getTel(InPurchaseActivity.this));
        map.put("num",applynum.getText().toString());
        map.put("remark","");
        map.put("type","1");

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



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
         if(requestCode == REQUEST_APPLY)
        {
            if(resultCode == RESULT_OK)
            {
                String str = data.toString();

                Bundle bundle = data.getExtras();
                applyname_id = bundle.getString("id");
                applyname_str= bundle.getString("name");
                applyname.setText(applyname_str);

            }
        }
    }


}
