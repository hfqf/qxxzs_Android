package com.points.autorepar.activity.contact;

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
import android.widget.Toast;

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

import com.android.volley.Response;
import com.android.volley.VolleyError;

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

import com.points.autorepar.activity.CommonWebviewActivity;
import com.points.autorepar.activity.workroom.WorkRoomEditActivity;
import com.points.autorepar.bean.ADTReapirItemInfo;
import com.points.autorepar.bean.RepairHistory;
import com.points.autorepar.http.HttpManager;
import com.points.autorepar.activity.BaseActivity;
import com.points.autorepar.lib.ocr.ui.camera.CameraActivity;
import com.points.autorepar.lib.ocr.ui.camera.FileUtil;
import com.points.autorepar.lib.ocr.ui.camera.RecognizeService;
import com.points.autorepar.sql.DBService;
import com.points.autorepar.bean.Contact;
import com.points.autorepar.utils.DateUtil;
import com.points.autorepar.utils.LoggerUtil;
import com.points.autorepar.utils.LoginUserUtil;
import com.umeng.analytics.MobclickAgent;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

/**
 *desc  添加客户页面
 *class ContactAddNewActivity.java
 *author Points
 *email hfqf123@126.com
 *created 16/11/30 上午9:53
 */
public class ContactAddNewActivity extends BaseActivity  implements  DatePickerDialog.OnDateSetListener {
    private final  String TAG  = "ContactAddNewActivity";
    /*
     *私有属性
     *
     */
    private EditText   mName;
    private EditText   mCarCode;
    private EditText   mTel;
    private EditText mCarType;
    private int  inittype = 0;

    private Button     mAddBtn;
    private Button     mBackBtn;
    ContactAddNewActivity m_this;
    private String     plate;

    private ImageButton m_addHeadBtn;
    private EditText   m_vinEditText;
    private EditText   m_carRegisterTimeEditText;

    private EditText   m_nextYearCheckTimeEditText;
    private EditText   m_tqTime1EditText;
    private EditText   m_safeCompanyEditText;
    private EditText   m_nextSageTimeEditText;
    private EditText   m_tqTime2EditText;
    private ImageView  m_car_vImg,m_carz_vImg;
    private  String    m_headUrl;
    private int        m_selectTimeType;
    private String mCarType_str;
    private String mKey_str;
    private Button contact_cartype_show;

    private EditText   m_safecompany3EditText;
    private EditText   m_safenexttime3EditText;
    private EditText   m_tqTime3EditText;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        m_this = this;
        setContentView(R.layout.activity_contact_add_new);
        Window window = getWindow();
//设置透明状态栏,这样才能让 ContentView 向上
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

//需要设置这个 flag 才能调用 setStatusBarColor 来设置状态栏颜色
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        Button common_navi_back = (Button)findViewById(R.id.common_navi_back);
        common_navi_back.setVisibility(View.GONE);
        Button common_navi_add = (Button)findViewById(R.id.common_navi_add);
//        common_navi_add.setVisibility(View.GONE);

        mName = (EditText)this.findViewById(R.id.contact_add_name);
        mCarType_str = "";
        contact_cartype_show = (Button)this.findViewById(R.id.contact_cartype_show);
        contact_cartype_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if("".equalsIgnoreCase(mKey_str))
                {
                    Toast.makeText(ContactAddNewActivity.this,"请设置正确的车型",Toast.LENGTH_LONG).show();
                    return;
                }
                MainApplication MainApplication = (MainApplication) getApplication();
                String weburl = MainApplication.consts().HTTP_URL + "/noticeboard/cardetail?carId="+mKey_str;
//                String weburl ="file:///android_asset/carpickerIndex.html";
                Intent intent = new Intent(ContactAddNewActivity.this, CommonWebviewActivity.class);
                intent.putExtra("url",weburl);
                intent.putExtra("title","车型");
                startActivityForResult(intent,1);
            }
        });
        mCarCode = (EditText) this.findViewById(R.id.contact_add_carcode);
        mTel = (EditText) this.findViewById(R.id.contact_add_tel);
        mCarType = (EditText) this.findViewById(R.id.contact_add_cartype);
        mCarType.setFocusableInTouchMode(false);
        mCarType.addTextChangedListener(nextYearCheckTime);

        String isneeddirectaddcartype = MainApplication.getInstance().getisneeddirectaddcartype(ContactAddNewActivity.this);
        if("0".equalsIgnoreCase(isneeddirectaddcartype))
        {
            mCarType.setCursorVisible(false);
            mCarType.setFocusable(false);
            mCarType.setFocusableInTouchMode(false);
            mCarType.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    MainApplication MainApplication = (MainApplication) getApplication();
               String weburl = MainApplication.consts().HTTP_URL + "/noticeboard/carpick2";
//                    String weburl ="file:///android_asset/carpickerIndex.html";
                    Intent intent = new Intent(ContactAddNewActivity.this, CommonWebviewActivity.class);
                    intent.putExtra("url",weburl);
                    intent.putExtra("title","车型");
                    startActivityForResult(intent,1);
                }
            });
        }else{
            mCarType.setCursorVisible(true);
            mCarType.setFocusable(true);
            mCarType.setFocusableInTouchMode(true);

        }






        m_nextYearCheckTimeEditText = (EditText)findViewById(R.id.contact_add_nextchecktime);
        m_nextYearCheckTimeEditText.setFocusableInTouchMode(false);
        m_nextYearCheckTimeEditText.addTextChangedListener(nextYearCheckTime);
        m_nextYearCheckTimeEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m_selectTimeType = 1;
                selectDate();
            }
        });
        m_tqTime1EditText= (EditText)findViewById(R.id.contact_add_tqtime1);
        m_safeCompanyEditText= (EditText)findViewById(R.id.contact_add_safecompany);
        m_nextSageTimeEditText= (EditText)findViewById(R.id.contact_add_nextsafetime);
        m_nextSageTimeEditText.setFocusableInTouchMode(false);
        m_nextSageTimeEditText.addTextChangedListener(nextSafeTime);
        m_nextSageTimeEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m_selectTimeType = 2;
                selectDate();
            }
        });
        m_tqTime2EditText= (EditText)findViewById(R.id.contact_add_tqtime2);


        m_safecompany3EditText =  (EditText)findViewById(R.id.contact_add_safecompany3);
        m_tqTime3EditText =  (EditText)findViewById(R.id.contact_add_tqtime3);
        m_safenexttime3EditText =  (EditText)findViewById(R.id.contact_add_nextsafetime3);
        m_safenexttime3EditText.setFocusableInTouchMode(false);
        m_safenexttime3EditText.addTextChangedListener(nextSafeTime3);
        m_safenexttime3EditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m_selectTimeType = 3;
                selectDate();
            }
        });



        RelativeLayout naviLayout =  (RelativeLayout)this.findViewById(R.id.contact_adduser_navi);
        mAddBtn = (Button)naviLayout.findViewById(R.id.common_navi_add);
        mAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                submitBtnClicked();

            }
        });

        m_car_vImg = (ImageView) findViewById(R.id.car_v);
        m_car_vImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             startSelectLicensePlatePicToUpload(3, new speUploadLicensePlateListener() {
                 @Override
                 public void onUploadLicensePlatePicSucceed(String licensePlate) {
                     mCarCode.setText(licensePlate);
                 }
             });
            }
        });

        m_carz_vImg = (ImageView) findViewById(R.id.carz_v);
        m_carz_vImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSelectVinPicToUpload(2, new speUploadVinListener() {
                    @Override
                    public void onUploadVinPicSucceed(String vin) {
                        if(vin.length() == 17){
                            m_vinEditText.setText(vin);
                        }
                    }
                });

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
                   public void uploadContactSucceed(String newHeadUrl) {
                       m_headUrl = newHeadUrl;

                      final String url = MainApplication.consts(m_this).BOS_SERVER+m_headUrl;

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

                   }

                   @Override
                   public void uploadUserSucceed(String newHeadUrl) {

                   }
               });

            }
        });

        m_vinEditText = (EditText) findViewById(R.id.contact_add_vin);

        m_carRegisterTimeEditText = (EditText) findViewById(R.id.contact_add_carregistertime);
        m_carRegisterTimeEditText.setFocusableInTouchMode(false);
        m_carRegisterTimeEditText.addTextChangedListener(watcherRepairTime);
        m_carRegisterTimeEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m_selectTimeType = 0;
                selectDate();
            }
        });

        plate = getIntent().getStringExtra("plate");
        if(plate != null) {
            if(plate.length() > 0){
                mCarCode.setText(plate);
            }
        }

        inittype = getIntent().getIntExtra("inittype",0);

        String  vinNO = getIntent().getStringExtra("vinNO");
        if(vinNO != null) {
            if(vinNO.length() > 0){
                m_vinEditText.setText(vinNO);
            }
        }
        String  RegTime = getIntent().getStringExtra("RegTime");
        if(RegTime != null) {
            if(RegTime.length() > 0){
                m_carRegisterTimeEditText.setText(RegTime);
            }
        }

        String  struserName = getIntent().getStringExtra("userName");
        if(struserName != null) {
            if(struserName.length() > 0){
                mName.setText(struserName);
            }
        }

        String stCarType = getIntent().getStringExtra("mCarType");
        if(stCarType != null) {
            if(stCarType.length() > 0){
                mCarType.setText(stCarType);
            }
        }


    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {

        int month = monthOfYear + 1;
        String date = year + (month > 9 ? ("-"+month) : ("-0"+month)) + ( dayOfMonth > 9 ?  ("-"+dayOfMonth) : ("-0"+dayOfMonth));
        if(m_selectTimeType == 0){
            m_carRegisterTimeEditText.setText(date);
        }else if(m_selectTimeType == 1){
            m_nextYearCheckTimeEditText.setText(date);
        }else if(m_selectTimeType == 2){
            m_nextSageTimeEditText.setText(date);
        }else if(m_selectTimeType == 3){
            m_safenexttime3EditText.setText(date);
        }
        Log.e(TAG, date);
    }

    /**
     * 选择日期
     */
    public void selectDate() {
        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                m_this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );

        dpd.setVersion(DatePickerDialog.Version.VERSION_2);
        dpd.show(getFragmentManager(), "Datepickerdialog");
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
        if(resultCode != RESULT_CANCELED) {
            // 当requestCode、resultCode同时为0，也就是处理特定的结果
            getTakePhoto().onActivityResult(requestCode, resultCode, intent);
            if (requestCode == 1 && resultCode == 1) {
                Bundle data = intent.getExtras();
                mCarType_str = data.getString("mCarType_str");
                mCarType.setText(mCarType_str);
                mKey_str = data.getString("mKey_str");
            }
            if (requestCode == REQUEST_CODE_GENERAL && resultCode == Activity.RESULT_OK) {
                RecognizeService.recGeneral(ContactAddNewActivity.this, FileUtil.getSaveFile(getApplicationContext()).getAbsolutePath(),
                        new RecognizeService.ServiceListener() {
                            @Override
                            public void onResult(String result) {
                                infoPopText(result);
                            }
                        });
            }
            if (requestCode == REQUEST_CODE_GENERAL_BASIC && resultCode == Activity.RESULT_OK) {
                RecognizeService.recGeneralBasic(ContactAddNewActivity.this, FileUtil.getSaveFile(getApplicationContext()).getAbsolutePath(),
                        new RecognizeService.ServiceListener() {
                            @Override
                            public void onResult(String result) {
                                infoPopText(result);
                            }
                        });
            }
            if (requestCode == REQUEST_CODE_GENERAL_ENHANCED && resultCode == Activity.RESULT_OK) {
                RecognizeService.recGeneralEnhanced(ContactAddNewActivity.this, FileUtil.getSaveFile(getApplicationContext()).getAbsolutePath(),
                        new RecognizeService.ServiceListener() {
                            @Override
                            public void onResult(String result) {
                                infoPopText(result);
                            }
                        });
            }
            if (requestCode == REQUEST_CODE_GENERAL_WEBIMAGE && resultCode == Activity.RESULT_OK) {
                RecognizeService.recWebimage(ContactAddNewActivity.this, FileUtil.getSaveFile(getApplicationContext()).getAbsolutePath(),
                        new RecognizeService.ServiceListener() {
                            @Override
                            public void onResult(String result) {
                                infoPopText(result);
                            }
                        });
            }
            if (requestCode == REQUEST_CODE_BANKCARD && resultCode == Activity.RESULT_OK) {
                RecognizeService.recBankCard(ContactAddNewActivity.this, FileUtil.getSaveFile(getApplicationContext()).getAbsolutePath(),
                        new RecognizeService.ServiceListener() {
                            @Override
                            public void onResult(String result) {
                                infoPopText(result);
                            }
                        });
            }

            if (requestCode == REQUEST_CODE_GENERAL && resultCode == Activity.RESULT_OK) {
                RecognizeService.recGeneral(ContactAddNewActivity.this, FileUtil.getSaveFile(getApplicationContext()).getAbsolutePath(),
                        new RecognizeService.ServiceListener() {
                            @Override
                            public void onResult(String result) {
                                infoPopText(result);
                            }
                        });
            }

            if (requestCode == REQUEST_CODE_GENERAL && resultCode == Activity.RESULT_OK) {
                RecognizeService.recGeneral(ContactAddNewActivity.this, FileUtil.getSaveFile(getApplicationContext()).getAbsolutePath(),
                        new RecognizeService.ServiceListener() {
                            @Override
                            public void onResult(String result) {
                                infoPopText(result);
                            }
                        });
            }

            if (requestCode == REQUEST_CODE_LICENSE_PLATE && resultCode == Activity.RESULT_OK) {
                String filePath = FileUtil.getSaveFile(ContactAddNewActivity.this).getAbsolutePath();
                OcrRequestParams param = new OcrRequestParams();
                param.setImageFile(new File(filePath));
                OCR.getInstance(ContactAddNewActivity.this).recognizeLicensePlate(param, new OnResultListener<OcrResponseResult>() {
                    @Override
                    public void onResult(OcrResponseResult result) {
                        // 调用成功，返回OcrResponseResult对象
                        String str = result.getJsonRes();
                        try {
                            JSONObject obj = new JSONObject(str);

                            JSONObject mapJSON = obj.getJSONObject("words_result");
                            String words = mapJSON.getString("number");
                            mCarCode.setText(words);


//                        }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(OCRError error) {
                        // 调用失败，返回OCRError对象
                        Toast.makeText(ContactAddNewActivity.this, "调用失败", Toast.LENGTH_LONG).show();
                    }
                });
            }
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

    private TextWatcher nextSafeTime3 = new TextWatcher() {

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
            Toast.makeText(getApplicationContext(), "车主名不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        if (mTel.getText().length() != 11) {
            Toast.makeText(getApplicationContext(), "手机号不足11位", Toast.LENGTH_SHORT).show();
            return;
        }

        if (mCarCode.getText().length() < 6) {
            Toast.makeText(getApplicationContext(), "车牌号不能小于6位", Toast.LENGTH_SHORT).show();
            return;
        }

//        if (mCarType.getText().length() == 0) {
//            Toast.makeText(getApplicationContext(), "车型不能为空", Toast.LENGTH_SHORT).show();
//            return;
//        }

        Map map = new HashMap();
        map.put("name", mName.getText().toString());
        map.put("tel", mTel.getText().toString());
        map.put("carcode", mCarCode.getText().toString());
        map.put("cartype", mCarType.getText().toString());
        map.put("carId", mKey_str);
        map.put("owner", LoginUserUtil.getTel(this).toString());
        map.put("headurl",m_headUrl == null ?"":m_headUrl);
        map.put("vin", m_vinEditText.getText().toString());
        map.put("carregistertime", m_carRegisterTimeEditText.getText().toString());
        map.put("safecompany", m_safeCompanyEditText.getText().toString());
        map.put("safenexttime", m_nextSageTimeEditText.getText().toString());
        map.put("yearchecknexttime", m_nextYearCheckTimeEditText.getText().toString());
        map.put("tqTime1", m_tqTime1EditText.getText().toString());
        map.put("tqTime2", m_tqTime2EditText.getText().toString());

        map.put("safecompany3", m_safecompany3EditText.getText().toString());
        map.put("safenexttime3", m_safenexttime3EditText.getText().toString());
        map.put("tqTime3", m_tqTime3EditText.getText().toString());

        showWaitView();
        HttpManager.getInstance(this).addContact("/contact/add4",
                        map,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {

                                stopWaitingView();
                                Log.e(TAG, LoggerUtil.jsonFromObject(response));
                                SQLiteDatabase db  = DBService.getDB();
                                if(response.optInt("code") == 1){
                                    Contact con = new Contact();

                                    con.setCarCode( mCarCode.getText().toString());
                                    con.setOwner(LoginUserUtil.getTel(getApplicationContext()));
                                    con.setCarType(mCarType.getText().toString());
                                    con.setName(mName.getText().toString());
                                    con.setTel(mTel.getText().toString());
                                    con.setIdfromnode(response.optJSONObject("ret").optString("_id"));
                                    con.setHeadurl(response.optJSONObject("ret").optString("headurl"));
                                    con.setVin(response.optJSONObject("ret").optString("vin"));
                                    con.setInserttime(response.optJSONObject("ret").optString("inserttime"));
                                    con.setIsbindweixin(response.optJSONObject("ret").optString("isbindweixin"));
                                    con.setWeixinopenid(response.optJSONObject("ret").optString("weixinopenid"));
                                    con.setCarregistertime(response.optJSONObject("ret").optString("carregistertime"));

                                    con.setSafecompany(response.optJSONObject("ret").optString("safecompany"));
                                    con.setSafenexttime(response.optJSONObject("ret").optString("safenexttime"));
                                    con.setYearchecknexttime(response.optJSONObject("ret").optString("yearchecknexttime"));
                                    con.setTqTime1(response.optJSONObject("ret").optString("tqTime1"));
                                    con.setTqTime2(response.optJSONObject("ret").optString("tqTime2"));
                                    con.setIsVip(response.optJSONObject("ret").optString("isVip"));
                                    con.setSafecompany3(response.optJSONObject("ret").optString("safecompany3"));
                                    con.setSafenexttime3(response.optJSONObject("ret").optString("safenexttime3"));
                                    con.setTqTime3(response.optJSONObject("ret").optString("tqTime3"));
                                    con.setSafetiptime3(response.optJSONObject("ret").optString("safetiptime3"));

                                    DBService.addNewContact(con,db);
//                                    DBService.closeDB(db);

                                    if(inittype ==1)
                                    {
                                        final RepairHistory rep =  new RepairHistory();
                                        rep.isAddedNewRepair = 1;
                                        rep.addition = "";
                                        rep.repairType = "";
                                        rep.circle = "";
                                        rep.totalKm = "";
                                        rep.isClose = "0";
                                        rep.isreaded = "0";
                                        rep.carCode = con.getCarCode();
                                        rep.contactid =con.getIdfromnode();
                                        rep.iswatiinginshop = "0";
                                        rep.customremark = "";
                                        rep.wantedcompletedtime = "";
                                        rep.entershoptime = "";
                                        rep.repairTime = "";

                                        JSONArray arrItmes = new JSONArray();
                                        Map cv = new HashMap();
                                        cv.put("carcode", rep.carCode);
                                        cv.put("totalkm", rep.totalKm);
                                        cv.put("repairetime",rep.repairTime);
                                        cv.put("repairtype", rep.repairType);
                                        cv.put("addition", rep.addition);
                                        cv.put("tipcircle", rep.tipCircle);
                                        cv.put("circle", rep.circle);
                                        cv.put("isclose", rep.isClose) ;
                                        cv.put("isreaded", rep.isClose);
                                        cv.put("owner", LoginUserUtil.getTel(ContactAddNewActivity.this));
                                        cv.put("id", "");
                                        cv.put("items", arrItmes);
                                        cv.put("contactid", rep.contactid);
                                        cv.put("iswatiinginshop", rep.iswatiinginshop);
                                        cv.put("customremark", rep.customremark);
                                        cv.put("wantedcompletedtime", rep.wantedcompletedtime);
                                        cv.put("entershoptime", rep.entershoptime);

                                        showWaitView();
                                        HttpManager.getInstance(m_this).updateOneRepair("/repair/add4", cv, new Response.Listener<JSONObject>() {
                                            @Override
                                            public void onResponse(JSONObject jsonObject) {

                                                stopWaitingView();
                                                if(jsonObject.optInt("code") == 1){
                                                    Toast.makeText(m_this,"开始接单",Toast.LENGTH_SHORT).show();
                                                    rep.idfromnode = jsonObject.optJSONObject("ret").optString("_id");
                                                    rep.state = jsonObject.optJSONObject("ret").optString("state");
                                                    rep.owner = jsonObject.optJSONObject("ret").optString("owner");
                                                    ArrayList<ADTReapirItemInfo> arrItems = new ArrayList();
                                                    rep.arrRepairItems = arrItems;
                                                    Intent intent = new Intent(m_this,WorkRoomEditActivity.class);
                                                    intent.putExtra(String.valueOf(R.string.key_repair_edit_para), rep);
                                                    startActivityForResult(intent, 1);
                                                    finish();
                                                }else {
                                                    Toast.makeText(m_this,"开单失败",Toast.LENGTH_SHORT).show();
                                                }

                                            }
                                        }, new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError volleyError) {

                                                stopWaitingView();
                                                Toast.makeText(getApplicationContext(),"开单失败",Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }else {
                                        finish();
                                    }

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

    public void infoPopText(final String result) {
        Log.e(TAG,"解析完毕"+result);

        try {
            JSONObject obj = new JSONObject(result);

            int num = obj.optInt("words_result_num");
            if(num > 0){
                JSONArray arr = obj.optJSONArray("words_result");
                JSONObject ret = arr.getJSONObject(0);
                String plate = ret.optString("words");
                mCarCode.setText(plate);
//                Intent intent = new Intent(getBaseContext(),ContactAddNewActivity.class);
//                intent.putExtra("plate",plate);
//                startActivity(intent);
            }
            else {
                Toast.makeText(this, "扫描失败,请调整角度或方向或距离再试一次", Toast.LENGTH_SHORT).show();
            }



        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "扫描失败,请调整角度或方向或距离再试一次", Toast.LENGTH_SHORT).show();

        }



    }
}
