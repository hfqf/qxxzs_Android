package com.points.autorepar.activity;

import android.app.Activity;
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
import com.points.autorepar.activity.workroom.WorkRoomEditActivity;
import com.points.autorepar.bean.WorkRoomPicBackEvent;
import com.points.autorepar.bean.WorkRoomPicEvent;
import com.points.autorepar.common.Consts;
import com.points.autorepar.http.HttpManager;
import com.points.autorepar.utils.BitmapUtils;
import com.points.autorepar.utils.ImgAdapter;
import com.points.autorepar.utils.LoginUserUtil;
import com.points.autorepar.utils.MyGridView;
import com.zyao89.view.zloading.ZLoadingDialog;
import com.zyao89.view.zloading.Z_TYPE;
import com.zyao89.view.zloading.star.StarBuilder;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;


public class EditUserInfoActivity extends BaseActivity {

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


    private EditText register_shopNameInput;
    private EditText shopAD;
    private EditText mShopNameText;

    private final int REQUEST_IMAGE = 2;
    private final int REQUEST_ADDR = 1;
    private Context context = this;
    private final int maxNum = 30;
    private Button btn_selectphoto,addimg;
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
        setContentView(R.layout.activity_edit_user_info);

        loadingDialog = new ZLoadingDialog(EditUserInfoActivity.this);
        loadingDialog.setLoadingBuilder(Z_TYPE.CIRCLE)
                .setLoadingColor(Color.WHITE)
                .setHintText("上传数据中...")
//                                .setHintTextSize(16) // 设置字体大小
                .setHintTextColor(Color.GRAY)  // 设置字体颜色
//                                .setDurationTime(0.5) // 设置动画时间百分比
                .setDialogBackgroundColor(Color.parseColor("#cc111111")); // 设置背景色



        mTitle      = (TextView)findViewById(R.id.common_navi_title);
        mTitle.setText("门店详情");

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

        calendar = Calendar.getInstance();
        mAddBtn = (Button)findViewById(R.id.common_navi_add);
        mAddBtn.setText("提交");
        mAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                updateShopInfo();
            }
        });

//            mNameText = (EditText)findViewById(R.id.register_telInput);
//            mNameText.setText(LoginUserUtil.getName(this));
//
//            mAddressText  = (EditText)findViewById(R.id.register_addressInput);
//            mAddressText.setText(LoginUserUtil.getAddress(this));
//
//            mShopNameText = (EditText)findViewById(R.id.register_shopNameInput);
//            mShopNameText.setText(LoginUserUtil.getShopName(this));
        province = "";
        initView();
    }

    private void initView() {
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

        addimg = ((Button) findViewById(R.id.addimg));
        addimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bitmapList1.clear();
                pictype = 2;
                selectGallery2(9);
            }
        });

        addressInput =   (TextView)findViewById(R.id.addressInput);
        addressInput .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(EditUserInfoActivity.this, PoiSearchDemo.class),REQUEST_ADDR);
            }
        });

        dwimg = (ImageView)findViewById(R.id.dwimg);
        dwimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(EditUserInfoActivity.this, PoiSearchDemo.class),REQUEST_ADDR);
            }
        });

        timeInput1 =   (TextView)findViewById(R.id.timeInput1);
        timeInput1 .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeInputType = 1;
                showTime();
            }
        });

        timeInput2 =   (TextView)findViewById(R.id.timeInput2);
        timeInput2 .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeInputType = 2;
                showTime();
            }
        });


        serviceInput = (TextView)findViewById(R.id.serviceInput);
        serviceInput .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingDialog.show();
                Map map = new HashMap();
                map.put("owner", LoginUserUtil.getTel(getApplicationContext()));
                HttpManager.getInstance(getApplicationContext()).updateHeadUrl("/baseData/getAllShopService", map, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        loadingDialog.dismiss();
                        if(jsonObject.optInt("code") == 1){
                            JSONArray _arr = jsonObject.optJSONArray("ret");
                            final String items[] =  new String[_arr.length()];
                            final String items_id[] =  new String[_arr.length()];
                            final boolean selected[] = new boolean[_arr.length()];
                            for (int i = 0; i < _arr.length(); i++) {
                                JSONObject obj = _arr.optJSONObject(i);
                                items[i] = obj.optString("name");
                                items_id[i] = obj.optString("_id");
                                selected[i] = false;
                            }
//                             = {true, false, true, false};
                            AlertDialog.Builder builder = new AlertDialog.Builder(EditUserInfoActivity.this,3);
                            builder.setTitle("服务范围（可多选）");
                            builder.setIcon(R.mipmap.ic_launcher);
                            builder.setMultiChoiceItems(items, selected,
                                    new DialogInterface.OnMultiChoiceClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which,
                                                            boolean isChecked) {
                                            selected[which] = isChecked;
//                                            Toast.makeText(EditUserInfoActivity.this,
//                                                    items[which] + isChecked, Toast.LENGTH_SHORT)
//                                                    .show();
                                        }
                                    });
                            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    String str = "";
                                    shopservice = "";
                                    boolean flag = true;
                                    for(int i=0;i<items.length;i++)
                                    {
                                        if(selected[i])
                                        {
                                          str = str+" "+items[i];
                                            String strid = items_id[i];
                                            if(flag)
                                            {
                                                flag = false;
                                                shopservice =strid;
                                            }else{
                                                shopservice = shopservice+","+strid;
                                            }
                                        }

                                    }
                                    serviceInput.setText(str);
//                                    Toast.makeText(EditUserInfoActivity.this, "确定", Toast.LENGTH_SHORT)
//                                            .show();
                                    // android会自动根据你选择的改变selected数组的值。
//                for (int i = 0; i < selected.length; i++) {
//                    Log.e("hongliang", "" + selected[i]);
//                }
                                }
                            });
                            builder.create().show();

                        }
                        else {
                        }


                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        loadingDialog.dismiss();
                    }
                });

            }
        });

        gv_show = ((GridView) findViewById(R.id.gv_show));
        gv_show1 = ((MyGridView ) findViewById(R.id.img_show1));



        String ad = MainApplication.getInstance().getShopAD(EditUserInfoActivity.this);

        String starttime = MainApplication.getInstance().getworkstarttime(EditUserInfoActivity.this);
        String endtime = MainApplication.getInstance().getworkendtime(EditUserInfoActivity.this);

        String shopname = LoginUserUtil.getShopName(EditUserInfoActivity.this);
        String adress = LoginUserUtil.getAddress(EditUserInfoActivity.this);

        shopservice = MainApplication.getInstance().getshopserviceID(EditUserInfoActivity.this);
        String serviceName = MainApplication.getInstance().getshopserviceName(EditUserInfoActivity.this);


        logoPath = MainApplication.getInstance().getshopserviceID(EditUserInfoActivity.this);

        register_shopNameInput.setText(shopname);
        shopAD.setText(ad);
        timeInput1.setText(starttime);
        timeInput2.setText(endtime);
        addressInput.setText(adress);
        serviceInput.setText(serviceName);


        String key = context.getResources().getString(R.string.key_loginer_headurl);
        SharedPreferences sp = context.getSharedPreferences(key_sp, Context.MODE_PRIVATE);
        logoPath = sp.getString(key, "");



        mSelectPath = new ArrayList<String>();
        finalSelectPaths = new ArrayList<String>();

//        String _headurl = ret.length() > 0 ? (mainApplication.consts().BOS_SERVER+ret):ret;
        if(logoPath.length()>0){
            String url = Consts.BOS_SERVER+logoPath+".png";
            mSelectPath.add(url);

            finalSelectPaths.add(logoPath);
            imgAdapter = new ImgAdapter(context, mSelectPath,true);
            gv_show.setAdapter(imgAdapter);
            //接口回调
            imgAdapter.setOnItemClickLis(new ImgAdapter.onItemClickLis() {
                @Override
                public void onItemClick(ImageView img_marke, int position) {

//                            img_marke.setVisibility(View.INVISIBLE);
                    finalSelectPaths.remove(position);
                    mSelectPath.remove(position);
                    //遍历
                    for (int i = 0; i < finalSelectPaths.size(); i++) {
                        Log.e("最后的路径", finalSelectPaths.get(i));
                    }
//                    bitmapList.remove(position);
                    imgAdapter.setData(finalSelectPaths);
                    logoPath = "";
                    imgAdapter.notifyDataSetChanged();
                }
            });
        }else {
//            return _headurl+".png";
        }
        String location = MainApplication.getInstance().getlocation(EditUserInfoActivity.this);

        if(location.length()>3)
        {
            location = location.substring(1,location.length()-1);
            String [] loc = location.split(",");
            if(loc.length>1)
            {
                lat = loc[1];
                lon = loc[0];
            }
        }

        province = MainApplication.getInstance().getprovicename(EditUserInfoActivity.this);
        city = MainApplication.getInstance().getcityname(EditUserInfoActivity.this);

        String pics = MainApplication.getInstance().getpics(EditUserInfoActivity.this);


        String [] piclist = pics.split(",");
        mSelectPath1 = new ArrayList<String>();
        finalSelectPaths1 = new ArrayList<String>();
        for(int i=0;i<piclist.length;i++)
        {
            if(piclist[i].length()>1)
            {
                String url = Consts.BOS_SERVER+piclist[i]+".png";
                mSelectPath1.add(url);

                finalSelectPaths1.add(piclist[i]);
                imgAdapter1 = new ImgAdapter(context, mSelectPath1,true);
                gv_show1.setAdapter(imgAdapter1);
                //接口回调
                imgAdapter1.setOnItemClickLis(new ImgAdapter.onItemClickLis() {
                    @Override
                    public void onItemClick(ImageView img_marke, int position) {

//                            img_marke.setVisibility(View.INVISIBLE);
                        finalSelectPaths1.remove(position);
                        mSelectPath1.remove(position);
                        //遍历
                        for (int i = 0; i < finalSelectPaths1.size(); i++) {
                            Log.e("最后的路径", finalSelectPaths1.get(i));
                        }
//                    bitmapList.remove(position);
                        imgAdapter1.setData(mSelectPath1);

                        imgAdapter1.notifyDataSetChanged();
                    }
                });
            }
        }



//        MainApplication.getInstance().setpics(LoginActivity.this,userInfoObject.optString("pics"));
//        MainApplication.getInstance().setprovicename(LoginActivity.this,userInfoObject.optString("provicename"));
//        MainApplication.getInstance().setcityname(LoginActivity.this,userInfoObject.optString("cityname"));
//
//
//        MainApplication.getInstance().setlocation(LoginActivity.this,userInfoObject.optString("location"));
//
//
//        String shopserviceID="";
//        String shopserviceName = "";
//
//
//        MainApplication.getInstance().setshopserviceID(LoginActivity.this,userInfoObject.optString("cityname"));
//        MainApplication.getInstance().setshopserviceName(LoginActivity.this,userInfoObject.optString("cityname"));


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
        Intent intent = new Intent(EditUserInfoActivity.this, MultiImageSelectorActivity.class);
        // 是否显示拍摄图片,显示
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, true);
        // 最大可选择图片数量 30张
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, num-mSelectPath.size());
        // 选择模式,选取多张
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_MULTI);
        // 默认选择
//        if (mSelectPath != null && mSelectPath.size() > 0) {
//            intent.putExtra(MultiImageSelectorActivity.EXTRA_DEFAULT_SELECTED_LIST, mSelectPath);
//        }
        startActivityForResult(intent, REQUEST_IMAGE);
    }


    private void selectGallery2(int num) {
        Intent intent = new Intent(EditUserInfoActivity.this, MultiImageSelectorActivity.class);
        // 是否显示拍摄图片,显示
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, true);
        // 最大可选择图片数量 30张
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, num-mSelectPath1.size());
        // 选择模式,选取多张
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_MULTI);
        // 默认选择
//        if (mSelectPath1 != null && mSelectPath1.size() > 0) {
//            intent.putExtra(MultiImageSelectorActivity.EXTRA_DEFAULT_SELECTED_LIST, mSelectPath1);
//        }
        startActivityForResult(intent, REQUEST_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == RESULT_OK) {

                if(pictype == 1) {
                    mSelectPath = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                    for (String p : mSelectPath) {
                        //加入最后选择的集合
                        finalSelectPaths.add(p);
                        uploadFileOne(p);
                    }
                    imgAdapter = new ImgAdapter(context, mSelectPath,true);
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
                }else{
                    ArrayList<String> tmplist = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                    loadingDialog.show();
                    upPic = tmplist.size();
                    curUpPic= 0;

                    for (String p : tmplist) {
                        //加入最后选择的集合
//                        finalSelectPaths1.add(p);
                        mSelectPath1.add(p);
                        uploadFileSome("pic",p);
                    }
                    imgAdapter1 = new ImgAdapter(context, mSelectPath1,true);
                    gv_show1.setAdapter(imgAdapter1);
                    //接口回调
                    imgAdapter1.setOnItemClickLis(new ImgAdapter.onItemClickLis() {
                        @Override
                        public void onItemClick(ImageView img_marke, int position) {

//                            img_marke.setVisibility(View.INVISIBLE);
                            finalSelectPaths1.remove(position);
                            mSelectPath1.remove(position);
                            //遍历
                            for (int i = 0; i < finalSelectPaths1.size(); i++) {
                                Log.e("最后的路径", finalSelectPaths1.get(i));
                            }
//                            bitmapList1.remove(position);
                            imgAdapter1.setData(mSelectPath1);
                            imgAdapter1.notifyDataSetChanged();
                        }
                    });
                }
            }
        }else if(requestCode == REQUEST_ADDR)
        {
            if(resultCode == RESULT_OK)
            {
                String str = data.toString();

                Bundle bundle = data.getExtras();
                 province = bundle.getString("province");
                 city = bundle.getString("city");
                 lon= bundle.getString("lon");
                 lat= bundle.getString("lat");
                String strAdd = bundle.getString("addr");
                addressInput.setText(strAdd);

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
        Luban.with(EditUserInfoActivity.this)
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
                        String filename = LoginUserUtil.getTel(EditUserInfoActivity.this) + "_" + System.currentTimeMillis();
                        map.put("fileName", filename);
//                        boolean ret = file.exists();
                        HttpManager.getInstance(EditUserInfoActivity.this).startNormalFilePost("/file/picUpload", filename, new File(mCurrentPhotoPath), map, new Response.Listener<JSONObject>() {
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

        Luban.with(EditUserInfoActivity.this)
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
                        String filename = LoginUserUtil.getTel(EditUserInfoActivity.this) + "_" + System.currentTimeMillis();
                        map.put("fileName", filename);

                        HttpManager.getInstance(EditUserInfoActivity.this).startNormalFilePost("/file/picUpload", filename, new File(mCurrentPhotoPath), map, new Response.Listener<JSONObject>() {
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

    private void updateShopInfo() {

        if(register_shopNameInput.getText().toString().length() == 0){
            Toast.makeText(EditUserInfoActivity.this,"名称不能为空",Toast.LENGTH_SHORT).show();
            return;
        }

        if(shopAD.getText().toString().length() == 0){
            Toast.makeText(EditUserInfoActivity.this,"宣传语不能为空",Toast.LENGTH_SHORT).show();
            return;
        }

        if(logoPath.length() == 0){
            Toast.makeText(EditUserInfoActivity.this,"logo不能为空",Toast.LENGTH_SHORT).show();
            return;
        }

        if(addressInput.getText().toString().length() == 0){
            Toast.makeText(EditUserInfoActivity.this,"地址不能为空",Toast.LENGTH_SHORT).show();
            return;
        }

        if(province.length()==0){
            Toast.makeText(EditUserInfoActivity.this,"地址不能为空",Toast.LENGTH_SHORT).show();
            return;
        }

        if(serviceInput.getText().toString().length() == 0){
            Toast.makeText(EditUserInfoActivity.this,"服务范围不能为空",Toast.LENGTH_SHORT).show();
            return;
        }

        if(timeInput1.getText().toString().length() == 0){
            Toast.makeText(EditUserInfoActivity.this,"营业开始时间不能为空",Toast.LENGTH_SHORT).show();
            return;
        }
        if(timeInput2.getText().toString().length() == 0){
            Toast.makeText(EditUserInfoActivity.this,"营业结束时间不能为空",Toast.LENGTH_SHORT).show();
            return;
        }

        loadingDialog.show();


        Map map = new HashMap();
        map.put("owner", LoginUserUtil.getTel(this));
        map.put("shopname", register_shopNameInput.getText().toString());
        map.put("headurl", logoPath);
        map.put("address", addressInput.getText().toString());
        map.put("advtip", shopAD.getText().toString());

        String pic = "";
        for(int i=0;i<finalSelectPaths1.size();i++)
        {
            String str = finalSelectPaths1.get(i);
            if(i==0)
            {
                pic = str;
            }else{
                pic = pic+","+str;
            }
        }
        final String pic_url = pic;
        map.put("pics", pic);

        String[] strS = shopservice.split(",");
        JSONArray items = new JSONArray();

        for(int i=0;i<strS.length;i++)
        {
            items.put(strS[i]);
        }

        final String shopserviceid = shopservice;
        map.put("shopservice", items);
        map.put("workstarttime", timeInput1.getText().toString());
        map.put("workendtime", timeInput2.getText().toString());

        if( province.length()>2)
        {
            province =  province.substring(0,2);
        }
        if( city.length()>2)
        {
            city =  city.substring(0,2);
        }

        map.put("provicename", province);
        map.put("cityname", city);
        map.put("lon", lon);
        map.put("lan", lat);



        HttpManager.getInstance(EditUserInfoActivity.this)
                .updateName("/users/updateShopInfo", map, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {

                        loadingDialog.dismiss();
                        Log.e(TAG,jsonObject.toString());

                        String ret = jsonObject.optString("code");
                        if("1".equalsIgnoreCase(ret)){

                            Toast.makeText(getApplicationContext(),jsonObject.optString("msg"),Toast.LENGTH_SHORT).show();

                            String name =  register_shopNameInput.getText().toString();
                            String key6 = getApplicationContext().getResources().getString(R.string.key_loginer_name);
                            SharedPreferences sp6 = getSharedPreferences(key_sp, Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor6= sp6.edit();
                            editor6.putString(key6, name);
                            editor6.commit();

                            String address =  addressInput.getText().toString();
                            String key7 = getApplicationContext().getResources().getString(R.string.key_loginer_address);
                            SharedPreferences sp7 = getSharedPreferences(key_sp, Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor7= sp7.edit();
                            editor7.putString(key7, address);
                            editor7.commit();

//                            String shopname =  mShopNameText.getText().toString();
//                            String key8 = getApplicationContext().getResources().getString(R.string.key_loginer_shopname);
//                            SharedPreferences sp8 = getSharedPreferences(key_sp, Context.MODE_PRIVATE);
//                            SharedPreferences.Editor editor8= sp8.edit();
//                            editor8.putString(key8, shopname);
//                            editor8.commit();


                            MainApplication.getInstance().setShopAD(EditUserInfoActivity.this,shopAD.getText().toString());
                            MainApplication.getInstance().setworkstarttime(EditUserInfoActivity.this,timeInput1.getText().toString());
                            MainApplication.getInstance().setworkendtime(EditUserInfoActivity.this,timeInput2.getText().toString());

                            MainApplication.getInstance().setpics(EditUserInfoActivity.this,pic_url);
                            MainApplication.getInstance().setprovicename(EditUserInfoActivity.this,province);
                            MainApplication.getInstance().setcityname(EditUserInfoActivity.this,city);


                            MainApplication.getInstance().setlocation(EditUserInfoActivity.this,"["+lon+","+lat+"]");

                            MainApplication.getInstance().setshopserviceID(EditUserInfoActivity.this,shopserviceid);
                            MainApplication.getInstance().setshopserviceName(EditUserInfoActivity.this,serviceInput.getText().toString());


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

}