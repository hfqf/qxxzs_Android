package com.points.autorepar.activity.contact;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.bumptech.glide.Glide;
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
import com.points.autorepar.bean.Contact;
import com.points.autorepar.bean.EmployeeInfo;
import com.points.autorepar.http.HttpManager;
import com.points.autorepar.lib.wheelview.WheelView;
import com.points.autorepar.sql.DBService;
import com.points.autorepar.utils.DateUtil;
import com.points.autorepar.utils.LoggerUtil;
import com.points.autorepar.utils.LoginUserUtil;
import com.umeng.analytics.MobclickAgent;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.json.JSONObject;

import java.io.File;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 *desc  添加客户页面
 *class ContactAddNewActivity.java
 *author Points
 *email hfqf123@126.com
 *created 16/11/30 上午9:53
 */
public class EmployeeAddNewActivity extends BaseActivity   {
    private final  String TAG  = "ContactAddNewActivity";
    private String strid;
    /*
     *私有属性
     *
     */

    private Switch sw_1,sw_2,sw_3,sw_4,sw_5,sw_6;
    private LinearLayout ln_sw_1,ln_sw_2,ln_sw_3,ln_sw_4,ln_sw_5,ln_sw_6;
    private Boolean f_sw_1,f_sw_2,f_sw_3,f_sw_4,f_sw_5,f_sw_6;
    private EditText   mName;
    private EditText   mCarCode;
    private EditText   mTel;
    private TextView mCarType;
    private int iCarType;
    private Button     mAddBtn;
    private Button     mBackBtn;
    EmployeeAddNewActivity m_this;

    private ImageButton m_addHeadBtn;
    private EditText   m_vinEditText,contact_add_nextchecktime;
    private Button   m_carRegisterTimeEditText;

    private  String    m_headUrl;
    private String mCarType_str;
    private String mKey_str;
    private int type;
    @SuppressLint("WrongViewCast")
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        m_this = this;
        setContentView(R.layout.activity_employee_add_new);
        Window window = getWindow();
//设置透明状态栏,这样才能让 ContentView 向上
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

//需要设置这个 flag 才能调用 setStatusBarColor 来设置状态栏颜色
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        Button common_navi_back = (Button)findViewById(R.id.common_navi_back);
        common_navi_back.setVisibility(View.GONE);
        Button common_navi_add = (Button)findViewById(R.id.common_navi_add);
        common_navi_add.setVisibility(View.GONE);

        TextView common_navi_title = (TextView)findViewById(R.id.common_navi_title);
        common_navi_title.setText("员工");

        mName = (EditText)this.findViewById(R.id.contact_add_name);

        ln_sw_1 = (LinearLayout)this.findViewById(R.id.ln_sw_1);
        ln_sw_2 = (LinearLayout)this.findViewById(R.id.ln_sw_2);
        ln_sw_3 = (LinearLayout)this.findViewById(R.id.ln_sw_3);
        ln_sw_4 = (LinearLayout)this.findViewById(R.id.ln_sw_4);
        ln_sw_5 = (LinearLayout)this.findViewById(R.id.ln_sw_5);
        ln_sw_6 = (LinearLayout)this.findViewById(R.id.ln_sw_6);


        sw_1 = (Switch) this.findViewById(R.id.sw_1);
        sw_2 = (Switch) this.findViewById(R.id.sw_2);
        sw_3 = (Switch) this.findViewById(R.id.sw_3);
        sw_4 = (Switch) this.findViewById(R.id.sw_4);
        sw_5 = (Switch) this.findViewById(R.id.sw_5);
        sw_6 = (Switch) this.findViewById(R.id.sw_6);

        sw_1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sw1BtnClicked(1,isChecked);
            }
        });

        sw_2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                swBtnClicked(2,isChecked);
            }
        });
        sw_3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                swBtnClicked(3,isChecked);
            }
        });

        sw_4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                swBtnClicked(4,isChecked);
            }
        });
        sw_5.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                swBtnClicked(5,isChecked);
            }
        });
        sw_6.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                swBtnClicked(6,isChecked);
            }
        });

        ln_sw_2.setVisibility(View.GONE);
        ln_sw_3.setVisibility(View.GONE);
        ln_sw_4.setVisibility(View.GONE);
        ln_sw_5.setVisibility(View.GONE);
        ln_sw_6.setVisibility(View.GONE);

        f_sw_1 = true;
        f_sw_2 = false;
        f_sw_3= false;
        f_sw_4= false;
        f_sw_5= false;
        f_sw_6= false;

        mCarCode = (EditText) this.findViewById(R.id.contact_add_carcode);
        mTel = (EditText) this.findViewById(R.id.contact_add_tel);
        contact_add_nextchecktime = (EditText) this.findViewById(R.id.contact_add_nextchecktime);
        mCarType = (TextView) this.findViewById(R.id.contact_add_cartype);
        mCarType.setFocusableInTouchMode(false);
        mCarType.addTextChangedListener(nextYearCheckTime);
        mCarType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               final String[] arr = {"技师","仓库员","店长"};

                View outerView = LayoutInflater.from(m_this).inflate(R.layout.wheel_view, null);
                final WheelView wv = (WheelView) outerView.findViewById(R.id.wheel_view_wv);
                wv.setItems(Arrays.asList(arr));
                wv.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
                    @Override
                    public void onSelected(int selectedIndex, String item) {

//                        Log.e(TAG, "[Dialog]selectedIndex: " + selectedIndex + ", item: " + item);
                    }
                });

                new AlertDialog.Builder(m_this)
                        .setTitle("请选择操作")
                        .setView(outerView)
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                Log.e(TAG, "OK" + wv.getSeletedIndex()+which);
                                String name = arr[wv.getSeletedIndex()];

                                iCarType = wv.getSeletedIndex()+1;
                                mCarType.setText(name);
                                swShow(wv.getSeletedIndex());

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
        });

    //                   WebActivity.actionStart(ReportActivity.this, weburl,title);




        RelativeLayout naviLayout =  (RelativeLayout)this.findViewById(R.id.contact_adduser_navi);
        mAddBtn = (Button)naviLayout.findViewById(R.id.common_navi_add);
        mAddBtn.setVisibility(View.VISIBLE);
        mAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(type == 1) {
                    submitBtnClicked();
                }else{
                    saveBtnClicked();
                }

            }
        });

        mBackBtn = (Button)naviLayout.findViewById(R.id.common_navi_back);
        mBackBtn.setVisibility(View.VISIBLE);
        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        m_addHeadBtn = (ImageButton) findViewById(R.id.contact_add_headurl);



        m_addHeadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               startSelectPicToUpload(1, new speUploadListener() {
                   @Override
                   public void uploadPictureSucceed(String newHeadUrl) {
                       m_headUrl = newHeadUrl;
                       String url = MainApplication.consts(m_this).BOS_SERVER+m_headUrl+".png";
                       Glide.get(m_this).clearMemory();
                       Glide.with(m_this).load(url).into(m_addHeadBtn);
                   }
               });
            }
        });

        m_vinEditText = (EditText) findViewById(R.id.contact_add_vin);

        m_carRegisterTimeEditText = (Button) findViewById(R.id.contact_add_carregistertime);
        m_carRegisterTimeEditText.setFocusableInTouchMode(false);

        m_carRegisterTimeEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                m_selectTimeType = 0;
//                selectDate();
            }
        });

        type = getIntent().getIntExtra("type",1);
        if(type ==2)
        {
            mAddBtn.setText("保存");
            EmployeeInfo info =getIntent().getParcelableExtra("data");
            initInfo(info);
        }else{
            mAddBtn.setText("新建");
        }

    }

    public void initInfo(EmployeeInfo info) {
        mName.setText(info.username);
        mCarCode.setText(info.pwd);
        mTel.setText(info.tel);
        m_vinEditText.setText(info.basepay);
        contact_add_nextchecktime.setText(info.meritpay);
        iCarType = Integer.parseInt(info.roletype) ;
        swShow(iCarType);
        strid = info.id;
        if (iCarType == 1) {
            mCarType.setText("技师");
            if(info.iscankaidan !=null)
            sw_2.setChecked("1".equalsIgnoreCase(info.iscankaidan) ? true : false);
            if(info.iscanaddnewcontact !=null)
            sw_3.setChecked("1".equalsIgnoreCase(info.iscanaddnewcontact) ? true : false);
            if(info.iscaneditcontact !=null)
            sw_4.setChecked("1".equalsIgnoreCase(info.iscaneditcontact) ? true : false);
            if(info.iscandelcontact !=null)
            sw_5.setChecked("1".equalsIgnoreCase(info.iscandelcontact) ? true : false);
            if(info.iscanseecontactrepairs !=null)
            sw_6.setChecked("1".equalsIgnoreCase(info.iscanseecontactrepairs) ? true : false);
        }  else  if (iCarType == 2) {
            mCarType.setText("仓库员");
            sw_2.setChecked(false);
            sw_3.setChecked(false);
            sw_4.setChecked(false);
            sw_5.setChecked(false);
            sw_6.setChecked(false);
        }else  if (iCarType == 3) {
            mCarType.setText("店长");
        }


        if(info.state !=null)
        sw_1.setChecked("1".equalsIgnoreCase(info.state) ? true : false);
        if(info.headurl!= null && info.headurl.length()>2) {
            final String url = MainApplication.consts(m_this).BOS_SERVER + info.headurl + ".png";

            mQueue.getCache().remove(url);

            Handler mainHandler = new Handler(Looper.getMainLooper());
            mainHandler.post(new Runnable() {
                @Override
                public void run() {

                    imageLoader.get(url, new ImageLoader.ImageListener() {
                        @Override
                        public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {
                            m_addHeadBtn.setImageBitmap(imageContainer.getBitmap());
                        }

                        @Override
                        public void onErrorResponse(VolleyError volleyError) {

                        }
                    }, 1000, 1000);
                }
            });
        }
    }


    private TextWatcher watcherRepairTime = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // TODO Auto-generated method stub

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
            // TODO Auto-generated method stub

        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
    @Override
    public void onActivityResult(int requestCode
            , int resultCode, Intent intent)
    {
        getTakePhoto().onActivityResult(requestCode, resultCode, intent);
        // 当requestCode、resultCode同时为0，也就是处理特定的结果
        if (requestCode == 1 && resultCode == 1)
        {
            // 取出Intent里的Extras数据
            Bundle data = intent.getExtras();
            // 取出Bundle中的数据

            mCarType_str = data.getString("mCarType_str");
            mCarType.setText(mCarType_str);
            mKey_str = data.getString("mKey_str");
            // 修改city文本框的内容
//            city.setText(resultCity);
        }
    }

    private TextWatcher nextYearCheckTime = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // TODO Auto-generated method stub

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
            // TODO Auto-generated method stub

        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };


    private TextWatcher nextSafeTime = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // TODO Auto-generated method stub

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
            // TODO Auto-generated method stub

        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };



    ///提交添加
    private void   submitBtnClicked(){

        if (mName.getText().length() == 0) {
            Toast.makeText(getApplicationContext(), "员工姓名不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        Map map = new HashMap();
        map.put("username", mName.getText().toString());
        map.put("pwd", mCarCode.getText().toString()); //密码
        map.put("tel", mTel.getText().toString());
        map.put("roletype", String.valueOf(iCarType)); //员工角色
        map.put("creatertel", LoginUserUtil.getTel(this).toString());
        map.put("creater", LoginUserUtil.getUserId(this).toString());
        map.put("headurl",m_headUrl == null ?"":m_headUrl);

        map.put("basepay",m_vinEditText.getText().toString());

        map.put("meritpay", contact_add_nextchecktime.getText().toString());

        map.put("status",sw_1.isChecked()?"1":"0");
        map.put("iscankaidan", sw_2.isChecked()?"1":"0"); //开单
        map.put("iscanaddnewcontact", sw_3.isChecked()?"1":"0") ;//新增联系人
        map.put("iscaneditcontact", sw_4.isChecked()?"1":"0");//
        map.put("iscandelcontact", sw_5.isChecked()?"1":"0");
        map.put("iscanseecontactrepairs", sw_6.isChecked()?"1":"0");

        showWaitView();
        HttpManager.getInstance(this).addContact("/employee/add3",
                        map,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {

                                stopWaitingView();
                                Log.e(TAG, LoggerUtil.jsonFromObject(response));
                                if(response.optInt("code") == 1){
                                    Toast.makeText(getApplicationContext(),"新增成功",Toast.LENGTH_LONG).show();
                                    finish();
                                }else {
                                    Toast.makeText(getApplicationContext(),response.optString("msg"),Toast.LENGTH_SHORT).show();
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
                                        Toast.makeText(getApplicationContext(),"添加失败",Toast.LENGTH_SHORT).show();

                                    }
                                });

                            }
                        });

    }

    private void   saveBtnClicked(){

        if (mName.getText().length() == 0) {
            Toast.makeText(getApplicationContext(), "员工姓名不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        Map map = new HashMap();
        map.put("username", mName.getText().toString());
        map.put("pwd", mCarCode.getText().toString()); //密码
        map.put("tel", mTel.getText().toString());
        map.put("roletype", String.valueOf(iCarType)); //员工角色
        map.put("creatertel", LoginUserUtil.getTel(this).toString());
        map.put("creater", LoginUserUtil.getUserId(this).toString());
        map.put("headurl",m_headUrl == null ?"":m_headUrl);

        map.put("basepay",m_vinEditText.getText().toString());
        map.put("id",strid);
        map.put("meritpay", contact_add_nextchecktime.getText().toString());


        showWaitView();
        HttpManager.getInstance(this).addContact("/employee/update2",
                map,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        stopWaitingView();
                        Log.e(TAG, LoggerUtil.jsonFromObject(response));
                        if(response.optInt("code") == 1){
                            Toast.makeText(getApplicationContext(),"保存成功",Toast.LENGTH_LONG).show();
                            finish();
                        }else {
                            Toast.makeText(getApplicationContext(),response.optString("msg"),Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(getApplicationContext(),"保存失败",Toast.LENGTH_SHORT).show();

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
//        MobclickAgent.onResume(this);
    }
    public void onPause() {
        super.onPause();
//        MobclickAgent.onPause(this);
    }

    public void swShow( int num)
    {
        ln_sw_2.setVisibility(View.GONE);
        ln_sw_3.setVisibility(View.GONE);
        ln_sw_4.setVisibility(View.GONE);
        ln_sw_5.setVisibility(View.GONE);
        ln_sw_6.setVisibility(View.GONE);

        switch (num)
        {
            case 0:

                break;
            case 1:
                ln_sw_2.setVisibility(View.VISIBLE);
                ln_sw_3.setVisibility(View.VISIBLE);
                ln_sw_4.setVisibility(View.VISIBLE);
                ln_sw_5.setVisibility(View.VISIBLE);
                ln_sw_6.setVisibility(View.VISIBLE);
                break;
            case 2:
                break;
            case 3:
                ln_sw_2.setVisibility(View.VISIBLE);
                ln_sw_3.setVisibility(View.VISIBLE);
                ln_sw_4.setVisibility(View.VISIBLE);
                ln_sw_5.setVisibility(View.VISIBLE);
                ln_sw_6.setVisibility(View.VISIBLE);
                break;
        }

    }

    ///提交添加
    private void sw1BtnClicked(int type,Boolean flag){



        Map map = new HashMap();
        map.put("state",flag?"1":"0");
        map.put("id", strid) ;//新增联系人
        showWaitView();
        HttpManager.getInstance(this).addContact("/employee/updatestate",
                map,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        stopWaitingView();
                        Log.e(TAG, LoggerUtil.jsonFromObject(response));
                        if(response.optInt("code") == 1){
//                            Toast.makeText(getApplicationContext(),"新增成功",Toast.LENGTH_LONG).show();
//                            finish();
                        }else {
                            Toast.makeText(getApplicationContext(),response.optString("msg"),Toast.LENGTH_SHORT).show();
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
//                                Toast.makeText(getApplicationContext(),"添加失败",Toast.LENGTH_SHORT).show();

                            }
                        });

                    }
                });

    }


    ///提交添加
    private void swBtnClicked(int type,Boolean flag){



        Map map = new HashMap();
        map.put("state",flag?"1":"0");
        map.put("attibute",String.valueOf(type-1)); //开单
        map.put("id", strid) ;//新增联系人
        showWaitView();
        HttpManager.getInstance(this).addContact("/employee/updatePower",
                map,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        stopWaitingView();
                        Log.e(TAG, LoggerUtil.jsonFromObject(response));
                        if(response.optInt("code") == 1){
//                            Toast.makeText(getApplicationContext(),"新增成功",Toast.LENGTH_LONG).show();
//                            finish();
                        }else {
                            Toast.makeText(getApplicationContext(),response.optString("msg"),Toast.LENGTH_SHORT).show();
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
//                                Toast.makeText(getApplicationContext(),"添加失败",Toast.LENGTH_SHORT).show();

                            }
                        });

                    }
                });

    }

    public TakePhoto getTakePhoto(){
        Log.e(TAG,getLocalClassName()+"getTakePhoto:1" + m_takePhoto);

        if (m_takePhoto==null){
            m_takePhoto= (TakePhoto) TakePhotoInvocationHandler.of(this).bind(new TakePhotoImpl(this,this));
            Log.e(TAG,getLocalClassName()+"getTakePhoto:2" + m_takePhoto);
        }
        Log.e(TAG,getLocalClassName()+"getTakePhoto:3" + m_takePhoto);
        CompressConfig compressConfig=new CompressConfig.Builder().setMaxSize(200*200).setMaxPixel(800).create();
        m_takePhoto.onEnableCompress(compressConfig,true);
        return m_takePhoto;
    }
    @Override
    public void takeSuccess(TResult result) {
        Log.i(TAG,"takeSuccess：" + result.getImage().getCompressPath());
        File file = new File(result.getImage().getCompressPath());
        uploadFileToBOS(DateUtil.getPicNameFormTime(new Date(),this), file);
    }
    @Override
    public void takeFail(TResult result,String msg) {
        Log.i(TAG, "takeFail:" + msg);
    }
    @Override
    public void takeCancel() {
        Log.i(TAG, getResources().getString(com.points.autorepar.R.string.msg_operation_canceled));
    }

    @Override
    public PermissionManager.TPermissionType invoke(InvokeParam invokeParam) {
        PermissionManager.TPermissionType type= PermissionManager.checkPermission(TContextWrap.of(this),invokeParam.getMethod());
        if(PermissionManager.TPermissionType.WAIT.equals(type)){
//            this.invokeParam=invokeParam;
        }
        return type;
    }


    public CropOptions getCropOptions(){
        CropOptions cropOptions=new CropOptions.Builder().setAspectX(1).setAspectY(1).setWithOwnCrop(true).create();
        return  cropOptions;
    }

    @Override
    public void uploadFileToBOS(final String fileName, final File file) {

        Map map = new HashMap();
        map.put("fileName", fileName);
        HttpManager.getInstance(this).startNormalFilePost("/file/picUpload", fileName,file, map, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                final   String _file = fileName;
                if(m_uplaodType == 0){
//                    uploadUserSucceed(_file);
                    m_headUrl = fileName;
//                    m_currentContact.setHeadurl(m_headUrl);
                    final String url = MainApplication.consts(m_this).BOS_SERVER+m_headUrl+".png";

                    mQueue.getCache().remove(url);

                    Handler mainHandler = new Handler(Looper.getMainLooper());
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {

                            imageLoader.get(url, new ImageLoader.ImageListener() {
                                @Override
                                public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {
                                    m_addHeadBtn.setImageBitmap(imageContainer.getBitmap());
                                }
                                @Override
                                public void onErrorResponse(VolleyError volleyError) {

                                }
                            },1000,1000);
                        }
                    });
                }else if(m_uplaodType == 1){
                    m_headUrl = fileName;
//                    m_currentContact.setHeadurl(m_headUrl);
                    final String url = MainApplication.consts(m_this).BOS_SERVER+m_headUrl+".png";

                    mQueue.getCache().remove(url);

                    Handler mainHandler = new Handler(Looper.getMainLooper());
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {

                            imageLoader.get(url, new ImageLoader.ImageListener() {
                                @Override
                                public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {
                                    m_addHeadBtn.setImageBitmap(imageContainer.getBitmap());
                                }
                                @Override
                                public void onErrorResponse(VolleyError volleyError) {

                                }
                            },1000,1000);
                        }
                    });
//                    uploadContactSucceed(_file);
                }else {

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(getApplicationContext(), "上传图片失败", Toast.LENGTH_SHORT).show();
            }
        });

    }

}
