package com.points.autorepar.activity.Store;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.points.autorepar.MainApplication;
import com.points.autorepar.R;
import com.points.autorepar.activity.BaseActivity;
import com.points.autorepar.activity.PoiSearchDemo;
import com.points.autorepar.bean.PurchaseRejectedInfo;
import com.points.autorepar.common.Consts;
import com.points.autorepar.http.HttpManager;
import com.points.autorepar.lib.wheelview.WheelView;
import com.points.autorepar.utils.ImgAdapter;
import com.points.autorepar.utils.LoginUserUtil;
import com.points.autorepar.utils.MyGridView;
import com.points.autorepar.zxing.activity.CaptureActivity;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.zyao89.view.zloading.ZLoadingDialog;
import com.zyao89.view.zloading.Z_TYPE;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import me.nereo.multi_image_selector.MultiImageSelectorActivity;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;


public class AddPurchaseActivity extends BaseActivity {

    private static final String key_sp  = "points";

    private  final  String  TAG = "EditUserInfoActivity";
    private Button mBackBtn;
    private Button mAddBtn;

    private TextView mTitle;

    private int upPic =0;
    private int curUpPic =0;

    private String province ;
    private String city ;
    private String lon;
    private String lat;
    private String  subid,sortname;
    private PurchaseRejectedInfo info;

    private EditText register_shopNameInput;
    private EditText shopAD;
    private EditText mShopNameText;

    private EditText name,saleprice,curprice,code,count,jgcount,mark;

    private TextView sort;
    private final int REQUEST_IMAGE = 2;
    private final int REQUEST_SORT = 1001;
    private final int REQUEST_CODE = 1002;
    private Context context = this;
    private final int maxNum = 30;
    private Button btn_selectphoto,addimg,scancode;
    private ArrayList<String> mSelectPath;
    private ArrayList<String> mSelectPath1;
    private List<Bitmap> bitmapList = new ArrayList<>();
    private List<Bitmap> bitmapList1 = new ArrayList<>();
    private GridView gv_show;
    private MyGridView gv_show1;
    private ImgAdapter imgAdapter,imgAdapter1;
    private TextView serviceInput,timeInput1,timeInput2,addressInput;
    private int pictype ;
    //是否选择的标记
    private boolean flag = true;
    private int timeInputType = 1;
    private List<String> finalSelectPaths = new ArrayList<>();
    private List<String> finalSelectPaths1 = new ArrayList<>();

    private String  shopservice= "";
    private String logoPath ="";

    private ImageView dwimg;

    //选择日期Dialog
    private DatePickerDialog datePickerDialog;
    //选择时间Dialog
    private TimePickerDialog timePickerDialog;

    private Calendar calendar;

    private ZLoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_purchase);


        pictype = 1;
        loadingDialog = new ZLoadingDialog(AddPurchaseActivity.this);
        loadingDialog.setLoadingBuilder(Z_TYPE.CIRCLE)
                .setLoadingColor(Color.WHITE)
                .setHintText("上传数据中...")
//                                .setHintTextSize(16) // 设置字体大小
                .setHintTextColor(Color.GRAY)  // 设置字体颜色
//                                .setDurationTime(0.5) // 设置动画时间百分比
                .setDialogBackgroundColor(Color.parseColor("#cc111111")); // 设置背景色



        mTitle      = (TextView)findViewById(R.id.common_navi_title);
        mTitle.setText("配件详情");

        finalSelectPaths1.clear();
        register_shopNameInput = (EditText)findViewById(R.id.register_shopNameInput);
        shopAD = (EditText)findViewById(R.id.shopAD);
        mBackBtn    = (Button)findViewById(R.id.common_navi_back);
        mBackBtn.setVisibility(View.VISIBLE);
        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        scancode = (Button)findViewById(R.id.scancode);
        scancode.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(AddPurchaseActivity.this, CaptureActivity.class);
                        intent.putExtra("type","1");
                        startActivityForResult(intent,REQUEST_CODE);
                    }
        });

        calendar = Calendar.getInstance();

        mAddBtn = (Button)findViewById(R.id.common_navi_add);
        mAddBtn.setText("入库");

        mAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                final PurchaseRejectedInfo info = (PurchaseRejectedInfo)mDataList.get(position);

                Intent intent = new Intent(AddPurchaseActivity.this,InPurchaseActivity.class);
                intent.putExtra("PurchaseRejectedInfo",info);
                startActivity(intent);
            }
        });
        ImageView common_navi_control = (ImageView) findViewById(R.id.common_navi_control);
        common_navi_control.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String[] arr = {"保存编辑","删除"};
                View outerView = LayoutInflater.from(AddPurchaseActivity.this).inflate(R.layout.wheel_view, null);
                final WheelView wv = (WheelView) outerView.findViewById(R.id.wheel_view_wv);
                wv.setItems(Arrays.asList(arr));
                wv.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
                    @Override
                    public void onSelected(int selectedIndex, String item) {

                    }
                });

                new android.app.AlertDialog.Builder(AddPurchaseActivity.this)
                        .setTitle("请选择操作")
                        .setView(outerView)
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {


                                if(wv.getSeletedIndex() == 0){
//
                                    addInfo();
                                }else if(wv.getSeletedIndex() == 1){
                                    delInfo();
                                }


                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();
            }
        });


        initView();
        mSelectPath = new ArrayList<String> ();
        logoPath = "";

        info = getIntent().getParcelableExtra("PurchaseRejectedInfo");
        if(info == null)
        {
            mAddBtn.setVisibility(View.INVISIBLE);
        }
        String type = getIntent().getStringExtra("type");


        if(info != null)
        {
            if("3".equalsIgnoreCase(type))
            {
                code.setText(info.good_barcode);
                getCodeInfo(info.good_barcode);
            }else {

                logoPath = info.good_headurl;
                name.setText(info.good_name);
                sort.setText(info.categorytoptypename);
                saleprice.setText(info.saleprice);
                curprice.setText(info.price);
                code.setText(info.good_barcode);
                count.setText(info.unit);
                jgcount.setText(info.minnum);
                mark.setText(info.remark);
                if (logoPath.length() > 0) {
                    String url = Consts.BOS_SERVER + logoPath + ".png";
                    mSelectPath.add(url);

                    finalSelectPaths.add(logoPath);
                    imgAdapter = new ImgAdapter(context, mSelectPath, false);
                    gv_show.setAdapter(imgAdapter);
                    //接口回调
                    imgAdapter.setOnItemClickLis(new ImgAdapter.onItemClickLis() {
                        @Override
                        public void onItemClick(ImageView img_marke, int position) {

                            bitmapList.clear();
                            pictype = 1;

                            selectGallery1(1);
                        }
                    });
                } else {
//            return _headurl+".png";
                }
            }

        }

    }

    private void initView() {

        name= ((EditText) findViewById(R.id.name));
        sort= ((TextView) findViewById(R.id.sort));
        sort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(AddPurchaseActivity.this,SelectServiceListActivity.class);
                intent.putExtra("PurchaseRejectedInfo",info);
                startActivityForResult(intent,REQUEST_SORT);
            }
        });
        saleprice= ((EditText) findViewById(R.id.saleprice));
        curprice= ((EditText) findViewById(R.id.curprice));
        code= ((EditText) findViewById(R.id.code));
        count= ((EditText) findViewById(R.id.count));
        jgcount= ((EditText) findViewById(R.id.jgcount));
        mark= ((EditText) findViewById(R.id.mark));


        //选择照片
        btn_selectphoto = ((Button) findViewById(R.id.addlogo));
        btn_selectphoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bitmapList.clear();
                pictype = 1;

                selectGallery1(1);
            }
        });


        gv_show = ((GridView) findViewById(R.id.gv_show));



    }



    /**
     * 发送短信验证码
     */
    private void sendCode(){

//        if(mNameText.getText().toString().length() == 0){
//            Toast.makeText(getApplicationContext(),"用户名不能为空",Toast.LENGTH_SHORT).show();;
//            return;
//        }


        if(mShopNameText.getText().toString().length() == 0){
            Toast.makeText(getApplicationContext(),"门店名称不能为空",Toast.LENGTH_SHORT).show();;
            return;
        }
        commit();

    }

    /**
     * 注册
     */
    private void commit(){
        showWaitView();
        MainApplication mainApplication = (MainApplication) getApplication();
        Map map = new HashMap();
//        map.put("username", mNameText.getText().toString());
        map.put("tel", LoginUserUtil.getTel(this));
//        map.put("address", mAddressText.getText().toString());
        map.put("shopname", mShopNameText.getText().toString());
        HttpManager.getInstance(this)
                .updateName("/users/updateUserInfo", map, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {

                        stopWaitingView();
                        Log.e(TAG,jsonObject.toString());

                        String ret = jsonObject.optString("code");
                        if(ret == "1"){

                            Toast.makeText(getApplicationContext(),jsonObject.optString("msg"),Toast.LENGTH_SHORT).show();

                            String name =  "";
                            String key6 = getApplicationContext().getResources().getString(R.string.key_loginer_name);
                            SharedPreferences sp6 = getSharedPreferences(key_sp, Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor6= sp6.edit();
                            editor6.putString(key6, name);
                            editor6.commit();

                            String address = "";
                            String key7 = getApplicationContext().getResources().getString(R.string.key_loginer_address);
                            SharedPreferences sp7 = getSharedPreferences(key_sp, Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor7= sp7.edit();
                            editor7.putString(key7, address);
                            editor7.commit();

                            String shopname =  mShopNameText.getText().toString();
                            String key8 = getApplicationContext().getResources().getString(R.string.key_loginer_shopname);
                            SharedPreferences sp8 = getSharedPreferences(key_sp, Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor8= sp8.edit();
                            editor8.putString(key8, shopname);
                            editor8.commit();

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

    private void selectGallery1(int num) {
        Intent intent = new Intent(AddPurchaseActivity.this, MultiImageSelectorActivity.class);
        // 是否显示拍摄图片,显示
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, true);
        // 最大可选择图片数量 30张
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, 1);
        // 选择模式,选取多张
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_MULTI);
        // 默认选择
//        if (mSelectPath != null && mSelectPath.size() > 0) {
//            intent.putExtra(MultiImageSelectorActivity.EXTRA_DEFAULT_SELECTED_LIST, mSelectPath);
//        }
        startActivityForResult(intent, REQUEST_IMAGE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == RESULT_OK) {


                    mSelectPath = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                    for (String p : mSelectPath) {
                        //加入最后选择的集合
                        finalSelectPaths.add(p);
                        uploadFileOne(p);
                    }
                    imgAdapter = new ImgAdapter(context, mSelectPath,false);
                    gv_show.setAdapter(imgAdapter);
                    //接口回调
                    imgAdapter.setOnItemClickLis(new ImgAdapter.onItemClickLis() {
                        @Override
                        public void onItemClick(ImageView img_marke, int position) {

//                            img_marke.setVisibility(View.INVISIBLE);
                            mSelectPath.remove(position);
                            finalSelectPaths.remove(position);
                            //遍历
                            for (int i = 0; i < finalSelectPaths.size(); i++) {
                                Log.e("最后的路径", finalSelectPaths.get(i));
                            }
//                            bitmapList.remove(position);
                            logoPath = "";
                            imgAdapter.setData(finalSelectPaths);
                            imgAdapter.notifyDataSetChanged();
                        }
                    });

            }
        }else if(requestCode == REQUEST_SORT)
        {
            if(resultCode == RESULT_OK)
            {
                String str = data.toString();

                Bundle bundle = data.getExtras();
                subid = bundle.getString("subid");
                sortname= bundle.getString("name");
                sort.setText(sortname);

            }
        }else if(requestCode == REQUEST_CODE)
        {
            if(resultCode == RESULT_OK)
            {
                String str = data.toString();

                Bundle bundle = data.getExtras();
                String resultid = bundle.getString("resultid");

                code.setText(resultid);
                getCodeInfo(resultid);

            }
        }
    }

    private void showDialogTwo() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_date, null);
        final DatePicker startTime = (DatePicker) view.findViewById(R.id.st);
        final DatePicker endTime = (DatePicker) view.findViewById(R.id.et);
        startTime.updateDate(startTime.getYear(), startTime.getMonth(), 01);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("选择时间");
        builder.setView(view);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int month = startTime.getMonth() + 1;
                String st = "" + startTime.getYear() + month + startTime.getDayOfMonth();
                int month1 = endTime.getMonth() + 1;
                String et = "" + endTime.getYear() + month1 + endTime.getDayOfMonth();
            }
        });
        builder.setNegativeButton("取消", null);
        AlertDialog dialog = builder.create();
        dialog.show();
        //自动弹出键盘问题解决
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }


    private void showTime() {
        timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                String strhourOfDay = hourOfDay>9?hourOfDay+"":"0"+hourOfDay;
                String strminute = minute>9?minute+"":"0"+minute;
                if(timeInputType == 1)
                {
                    timeInput1.setText(strhourOfDay+":"+strminute);
                }else{
                    timeInput2.setText(strhourOfDay+":"+strminute);
                }
            }
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
        timePickerDialog.show();
        timePickerDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    private void uploadFileOne(final String file) {
        Luban.with(AddPurchaseActivity.this)
                .load(file)
                .setCompressListener(new OnCompressListener() {
                    @Override
                    public void onStart() {
//                            Toast.makeText(WorkRoomEditActivity.this, "I'm start", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess(File file) {
                        Log.i("path", file.getAbsolutePath());
                        boolean ret = file.exists();
                        String  mCurrentPhotoPath = file.getAbsolutePath();
                        loadingDialog.show();

//                        String str[] = fileName.split("/");
                        Map map = new HashMap();
                        String filename = LoginUserUtil.getTel(AddPurchaseActivity.this) + "_" + System.currentTimeMillis();
                        map.put("fileName", filename);
//                        boolean ret = file.exists();
                        HttpManager.getInstance(AddPurchaseActivity.this).startNormalFilePost("/file/picUpload", filename, new File(mCurrentPhotoPath), map, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject jsonObject) {
                                loadingDialog.dismiss();
                                String strUrl = jsonObject.optString("url");
                                int ret = jsonObject.optInt("code");
                                if (ret != 1) {
                                    return;
                                }else{
                                    logoPath = strUrl;

                                }

                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError volleyError) {
                                loadingDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "上传图片失败", Toast.LENGTH_SHORT).show();
                                int i = 0;
                            }
                        });
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                }).launch();


    }

    private void uploadFileSome(final String fileName, final String file) {

        Luban.with(AddPurchaseActivity.this)
                .load(file)
                .setCompressListener(new OnCompressListener() {
                    @Override
                    public void onStart() {
//                            Toast.makeText(WorkRoomEditActivity.this, "I'm start", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess(File file) {
                        Log.i("path", file.getAbsolutePath());
                        boolean ret = file.exists();
                       String  mCurrentPhotoPath = file.getAbsolutePath();
                        Map map = new HashMap();
                        String filename = LoginUserUtil.getTel(AddPurchaseActivity.this) + "_" + System.currentTimeMillis();
                        map.put("fileName", filename);

                        HttpManager.getInstance(AddPurchaseActivity.this).startNormalFilePost("/file/picUpload", filename, new File(mCurrentPhotoPath), map, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject jsonObject) {
                                curUpPic++;
                                if(curUpPic ==upPic) {
                                    loadingDialog.dismiss();
                                }
                                String strUrl = jsonObject.optString("url");
                                int ret = jsonObject.optInt("code");
                                if (ret != 1) {
                                    return;
                                }else{
                                    //123
                                    finalSelectPaths1.add(strUrl);

                                }

                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError volleyError) {
                                loadingDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "上传图片失败", Toast.LENGTH_SHORT).show();
                                int i = 0;
                            }
                        });
                    }

                    @Override
                    public void onError(Throwable e) {
                        loadingDialog.dismiss();
                    }
                }).launch();



    }

    private void addInfo() {

        if(name.getText().toString().length() == 0){
            Toast.makeText(AddPurchaseActivity.this,"名称不能为空",Toast.LENGTH_SHORT).show();
            return;
        }

        if(sort.getText().toString().length() == 0){
            Toast.makeText(AddPurchaseActivity.this,"分类不能为空",Toast.LENGTH_SHORT).show();
            return;
        }



        loadingDialog.show();


        Map map = new HashMap();
        map.put("owner", LoginUserUtil.getTel(this));
        map.put("name", name.getText().toString());
        map.put("unit", count.getText().toString());
        map.put("minnum", jgcount.getText().toString());
        map.put("remark", mark.getText().toString());

        map.put("barcode", code.getText().toString());
//        if(mSelectPath.size()>0)
//        {
            map.put("picurl", logoPath);
//        }else {
//            map.put("picurl", "");
//        }
        map.put("costprice", curprice.getText().toString());

        map.put("saleprice", saleprice.getText().toString());

        if(info !=null)
        {
            map.put("id", info.id);
            map.put("category",info.categoryid);
            map.put("subtype", info.categoryid);
        }else{
            map.put("id", "");
            map.put("category",subid);
            map.put("subtype", subid);
        }
        map.put("isactive", "1");
        map.put("applycartype", "");
        map.put("brand", "");
        map.put("goodsencode", "");




        map.put("producteraddress", "");
        map.put("productertype", "");








        HttpManager.getInstance(AddPurchaseActivity.this)
                .updateName(info ==null?"/warehousegoods/add":"/warehousegoods/update", map, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {

                        loadingDialog.dismiss();
                        Log.e(TAG,jsonObject.toString());

                        String ret = jsonObject.optString("code");
                        if("1".equalsIgnoreCase(ret)){

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

                        loadingDialog.dismiss();
                        Toast.makeText(getApplicationContext(),"修改失败",Toast.LENGTH_SHORT).show();

                    }
                });
    }

    public void delInfo()
    {

        loadingDialog.show();


        Map map = new HashMap();

        if(info !=null)
        {
            map.put("id", info.id);
        }else{
            map.put("id", "");
        }








        HttpManager.getInstance(AddPurchaseActivity.this)
                .updateName("/warehousegoods/del", map, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {

                        loadingDialog.dismiss();
                        Log.e(TAG,jsonObject.toString());

                        String ret = jsonObject.optString("code");
                        if("1".equalsIgnoreCase(ret)){

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

                        loadingDialog.dismiss();
                        Toast.makeText(getApplicationContext(),"修改失败",Toast.LENGTH_SHORT).show();

                    }
                });
    }


    public void getCodeInfo(String code)
    {

//        loadingDialog.show();

//        code = "6925282257048";

        String url="http://jisutxmcx.market.alicloudapi.com/barcode2/query?barcode="+code;
        //创建OKHttp对象
        OkHttpClient okHttpCient=new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization","APPCODE 68174e9d480d4351a9e0a962d049851f")
                .get()
                .build();

        //创建一个call对象
        Call call=okHttpCient.newCall(request);
        //将请求添加到调度中
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
//                loadingDialog.dismiss();
            }

            @Override
            public void onResponse(com.squareup.okhttp.Response response) throws IOException {
//                loadingDialog.dismiss();
            }



        });

    }



}