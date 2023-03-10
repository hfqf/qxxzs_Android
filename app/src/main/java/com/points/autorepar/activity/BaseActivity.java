package com.points.autorepar.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.alibaba.android.arouter.launcher.ARouter;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.baidu.ocr.sdk.OCR;
import com.baidu.ocr.sdk.OnResultListener;
import com.baidu.ocr.sdk.exception.OCRError;
import com.baidu.ocr.sdk.model.OcrRequestParams;
import com.baidu.ocr.sdk.model.OcrResponseResult;
import com.dyhdyh.widget.loading.dialog.LoadingDialog;
import com.jph.takephoto.app.TakePhoto;
import com.jph.takephoto.app.TakePhotoImpl;
import com.jph.takephoto.compress.CompressConfig;
import com.jph.takephoto.model.CropOptions;
import com.points.autorepar.R;
import com.points.autorepar.activity.Store.AddPurchaseActivity;
import com.points.autorepar.activity.contact.ContactAddNewActivity;
import com.points.autorepar.MainApplication;
import com.points.autorepar.activity.contact.ContactInfoEditActivity;
import com.points.autorepar.activity.repair.RepairHistoryListActivity;
import com.points.autorepar.activity.workroom.WorkRoomEditActivity;
import com.points.autorepar.bean.ADTReapirItemInfo;
import com.points.autorepar.bean.Contact;
import com.points.autorepar.bean.PurchaseRejectedInfo;
import com.points.autorepar.bean.RepairHistory;
import com.points.autorepar.bean.UpdateCarcodeEvent;
import com.points.autorepar.bean.WorkRoomEvent;
import com.points.autorepar.common.Consts;
import com.points.autorepar.http.HttpManager;
import com.points.autorepar.lib.ocr.ui.camera.FileUtil;
import com.points.autorepar.lib.ocr.ui.camera.RecognizeService;
import com.points.autorepar.lib.wheelview.WheelView;
import com.points.autorepar.platerecognizer.base.BitmapCache;
import com.points.autorepar.sql.DBService;
import com.points.autorepar.utils.DateUtil;
import com.points.autorepar.utils.LoginUserUtil;
import com.umeng.analytics.MobclickAgent;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.jph.takephoto.model.InvokeParam;
import com.jph.takephoto.model.TContextWrap;
import com.jph.takephoto.model.TResult;
import com.jph.takephoto.permission.InvokeListener;
import com.jph.takephoto.permission.PermissionManager;
import com.jph.takephoto.permission.PermissionManager.TPermissionType;
import com.jph.takephoto.permission.TakePhotoInvocationHandler;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import Utils.SpeDateHelper;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.ThreadMode;


public class BaseActivity extends Activity implements  TakePhoto.TakeResultListener,InvokeListener {

    public  RequestQueue mQueue;

    public BitmapCache lruImageCache;
    public ImageLoader imageLoader;
    private  int selectedIndex = 0;
    public Consts    mConsts;
    private MainApplication m_mainApplication;
    private   LinearLayout m_loadingView;

    public ViewGroup  m_container;
    private AVLoadingIndicatorView m_waitingView;
    private String      TAG = "BaseActivity";

    public TakePhoto m_takePhoto;

    private InvokeParam invokeParam;
    private speUploadListener m_listener;

    private speUploadVinListener m_vinlistener;
    private speUploadLicensePlateListener m_licensePlatelistener;

    public  int        m_uplaodType;//0??????????????????,1?????????????????????,2??????vin 3???????????????

    public static final int REQUEST_CODE_GENERAL = 105;
    public static final int REQUEST_CODE_GENERAL_BASIC = 106;
    public static final int REQUEST_CODE_GENERAL_ENHANCED = 107;
    public static final int REQUEST_CODE_GENERAL_WEBIMAGE = 108;
    public static final int REQUEST_CODE_BANKCARD = 110;
    public static final int REQUEST_CODE_VEHICLE_LICENSE = 120;
    public static final int REQUEST_CODE = 10001;
    public static final int REQUEST_CODE_DRIVING_LICENSE = 121;
    public static final int REQUEST_CODE_LICENSE_PLATE = 122;


    public interface speUploadListener{
        void uploadPictureSucceed(String url);
    }

    public interface speUploadVinListener{
        void onUploadVinPicSucceed(String vin);
    }

    /**
     * ?????????????????????
     */
    public interface speUploadLicensePlateListener{
        void onUploadLicensePlatePicSucceed(String licensePlate);
    }

    /**
     * post??????
     */
    public interface PostApiListener{
        void onUploadVinPicSucceed(String vin);

        void onGetRepairHistory(ArrayList<RepairHistory> list);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        getTakePhoto().onCreate(savedInstanceState);

        super.onCreate(savedInstanceState);
        ARouter.getInstance().inject(this);

        m_takePhoto= getTakePhoto();


        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            window.setStatusBarColor(getResources().getColor(R.color.white));
        }

        LayoutInflater inflater = getLayoutInflater();
        m_loadingView  = (LinearLayout) inflater.inflate(R.layout.loadingviewlayout, null);
        m_waitingView = (AVLoadingIndicatorView) m_loadingView.findViewById(R.id.waitingView);

        mQueue = Volley.newRequestQueue(this);

        lruImageCache = new  BitmapCache();

        imageLoader = new ImageLoader(mQueue,lruImageCache);

        m_mainApplication = (MainApplication) getApplication();
        mConsts = m_mainApplication.consts();
    }

    public Consts  getConsts(){
        return mConsts;
    }

    public void showWaitView() {

        LoadingDialog.make(this).show();

    }



    public void stopWaitingView() {
        LoadingDialog.cancel();
    }




    @Override
    public void finish() {
        super.finish();
    }


    /**
     * ????????????????????????
     * ???????????????????????????
     * https://github.com/crazycodeboy/TakePhoto#%E8%A3%81%E5%89%AA%E5%9B%BE%E7%89%87
     */

    public void startSelectPicToUpload(int uploadType,speUploadListener listener){

        this.m_uplaodType = uploadType;
        this.m_listener = listener;


        String[] arr = getResources().getStringArray(R.array.uploadpic);

        View outerView = LayoutInflater.from(this).inflate(R.layout.wheel_view, null);
        final WheelView wv = (WheelView) outerView.findViewById(R.id.wheel_view_wv);
        wv.setItems(Arrays.asList(arr));
        wv.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
            @Override
            public void onSelected(int selectedIndex, String item) {
                Log.e(TAG, "[Dialog]selectedIndex: " + selectedIndex + ", item: " + item);
            }
        });

        new android.app.AlertDialog.Builder(this)
                .setTitle("????????????")
                .setView(outerView)
                .setPositiveButton("??????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                        Log.e(TAG, "startSelectPicToUpload+OK" + wv.getSeletedItem());

                        int index = wv.getSeletedIndex();

                        File file=new File(Environment.getExternalStorageDirectory(), "/temp/"+System.currentTimeMillis() + ".jpg");
                        Log.e(TAG, "startSelectPicToUpload+1" + wv.getSeletedIndex());

                        if (!file.getParentFile().exists())file.getParentFile().mkdirs();
                        Log.e(TAG, "startSelectPicToUpload+2" + wv.getSeletedIndex());
                        Uri imageUri = Uri.fromFile(file);
                        Log.e(TAG, "startSelectPicToUpload+3" + wv.getSeletedIndex());
                        if(index == 1){
                            Log.e(TAG, "startSelectPicToUpload+4" + wv.getSeletedIndex());
                            Log.e(TAG, "startSelectPicToUpload+4" + m_takePhoto+imageUri);
                            m_takePhoto.onPickFromGallery();
                            Log.e(TAG, "startSelectPicToUpload+5" + wv.getSeletedIndex());
                        }else {
                            Log.e(TAG, "startSelectPicToUpload+6" + wv.getSeletedIndex());
                            m_takePhoto.onPickFromCapture(imageUri);
                            Log.e(TAG, "startSelectPicToUpload+7" + wv.getSeletedIndex());
                        }

                    }
                })
                .setNegativeButton("??????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.e(TAG, "onCancel");
                    }
                })
                .show();
    }

    public void startSelectVinPicToUpload(int uploadType,speUploadVinListener listener){

        this.m_uplaodType = uploadType;
        this.m_vinlistener = listener;

        String[] arr = getResources().getStringArray(R.array.uploadpic);

        View outerView = LayoutInflater.from(this).inflate(R.layout.wheel_view, null);
        final WheelView wv = (WheelView) outerView.findViewById(R.id.wheel_view_wv);
        wv.setItems(Arrays.asList(arr));
        wv.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
            @Override
            public void onSelected(int selectedIndex, String item) {
                Log.e(TAG, "[Dialog]selectedIndex: " + selectedIndex + ", item: " + item);
            }
        });

        new android.app.AlertDialog.Builder(this)
                .setTitle("????????????")
                .setView(outerView)
                .setPositiveButton("??????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                        Log.e(TAG, "startSelectPicToUpload+OK" + wv.getSeletedItem());

                        int index = wv.getSeletedIndex();

                        File file=new File(Environment.getExternalStorageDirectory(), "/temp/"+System.currentTimeMillis() + ".jpg");
                        Log.e(TAG, "startSelectPicToUpload+1" + wv.getSeletedIndex());

                        if (!file.getParentFile().exists())file.getParentFile().mkdirs();
                        Log.e(TAG, "startSelectPicToUpload+2" + wv.getSeletedIndex());
                        Uri imageUri = Uri.fromFile(file);
                        Log.e(TAG, "startSelectPicToUpload+3" + wv.getSeletedIndex());
                        if(index == 1){
                            Log.e(TAG, "startSelectPicToUpload+4" + wv.getSeletedIndex());
                            Log.e(TAG, "startSelectPicToUpload+4" + m_takePhoto+imageUri);
                            m_takePhoto.onPickFromGallery();
                            Log.e(TAG, "startSelectPicToUpload+5" + wv.getSeletedIndex());
                        }else {
                            Log.e(TAG, "startSelectPicToUpload+6" + wv.getSeletedIndex());
                            m_takePhoto.onPickFromCapture(imageUri);
                            Log.e(TAG, "startSelectPicToUpload+7" + wv.getSeletedIndex());
                        }

                    }
                })
                .setNegativeButton("??????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.e(TAG, "onCancel");
                    }
                })
                .show();
    }

    public void startSelectLicensePlatePicToUpload(int uploadType,speUploadLicensePlateListener listener){

        this.m_uplaodType = 3;
        this.m_licensePlatelistener = listener;

        String[] arr = getResources().getStringArray(R.array.uploadpic);

        View outerView = LayoutInflater.from(this).inflate(R.layout.wheel_view, null);
        final WheelView wv = (WheelView) outerView.findViewById(R.id.wheel_view_wv);
        wv.setItems(Arrays.asList(arr));
        wv.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
            @Override
            public void onSelected(int selectedIndex, String item) {
                Log.e(TAG, "[Dialog]selectedIndex: " + selectedIndex + ", item: " + item);
            }
        });

        new android.app.AlertDialog.Builder(this)
                .setTitle("????????????")
                .setView(outerView)
                .setPositiveButton("??????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                        Log.e(TAG, "startSelectPicToUpload+OK" + wv.getSeletedItem());

                        int index = wv.getSeletedIndex();

                        File file=new File(Environment.getExternalStorageDirectory(), "/temp/"+System.currentTimeMillis() + ".jpg");
                        Log.e(TAG, "startSelectPicToUpload+1" + wv.getSeletedIndex());

                        if (!file.getParentFile().exists())file.getParentFile().mkdirs();
                        Uri imageUri = Uri.fromFile(file);
                        if(index == 1){
                            m_takePhoto.onPickFromGallery();
                        }else {
                            m_takePhoto.onPickFromCapture(imageUri);
                        }
                    }
                })
                .setNegativeButton("??????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.e(TAG, "onCancel");
                    }
                })
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        TPermissionType type=PermissionManager.onRequestPermissionsResult(requestCode,permissions,grantResults);
        PermissionManager.handlePermissionsResult(this,type,invokeParam,this);
    }

    /**
     *  ??????TakePhoto??????
     * @return
     */
    public TakePhoto getTakePhoto(){
        if (m_takePhoto==null){
            m_takePhoto= (TakePhoto) TakePhotoInvocationHandler.of(this).bind(new TakePhotoImpl(this,this));
        }
        CompressConfig compressConfig=new CompressConfig.Builder().setMaxSize(800*800).setMaxPixel(7000).create();
        m_takePhoto.onEnableCompress(compressConfig,true);
        return m_takePhoto;
    }
    @Override
    public void takeSuccess(TResult result) {
        Log.i(TAG,"takeSuccess???" + result.getImage().getCompressPath());
        File file = new File(result.getImage().getCompressPath());
        if(this.m_uplaodType == 0 || this.m_uplaodType == 1){
            uploadFileToBOS(DateUtil.getPicNameFormTime(new Date(),this), file);
        }else if(this.m_uplaodType == 2){
            uploadVinFileToServer(DateUtil.getPicNameFormTime(new Date(),this), file);
        }else if(this.m_uplaodType == 3){
            uploadLicensePlateFileToServer(DateUtil.getPicNameFormTime(new Date(),this), file);
        }
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
    public TPermissionType invoke(InvokeParam invokeParam) {
        TPermissionType type=PermissionManager.checkPermission(TContextWrap.of(this),invokeParam.getMethod());
        if(TPermissionType.WAIT.equals(type)){
            this.invokeParam=invokeParam;
        }
        return type;
    }


    public CropOptions getCropOptions(){
        CropOptions cropOptions=new CropOptions.Builder().setAspectX(1).setAspectY(1).setWithOwnCrop(true).create();
        return  cropOptions;
    }


    public void uploadFileToBOS(final String fileName, final File file) {
        Map map = new HashMap();
        map.put("fileName", fileName);
        HttpManager.getInstance(this).startNormalFilePost("/file/picUpload", fileName,file, map, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
              final   String _file = fileName+".png";
                m_listener.uploadPictureSucceed(_file);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(getApplicationContext(), "??????????????????", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void uploadVinFileToServer(final String fileName, final File file) {
        Map map = new HashMap();
        map.put("fileName", fileName);
        HttpManager.getInstance(this).startNormalFilePost("/file/vinPicUpload", fileName,file, map, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                    final   String _vin = jsonObject.optString("ret");
                    m_vinlistener.onUploadVinPicSucceed(_vin);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(getApplicationContext(), "??????????????????", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void uploadLicensePlateFileToServer(final String fileName, final File file) {
        Map map = new HashMap();
        map.put("fileName", fileName);
        HttpManager.getInstance(this).startNormalFilePost("/file/licensePlatePicUpload", fileName,file, map, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                final   String _vin = jsonObject.optString("ret");
                m_licensePlatelistener.onUploadLicensePlatePicSucceed(_vin);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(getApplicationContext(), "??????????????????", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String  filePath = "";
        try {
            getTakePhoto().onActivityResult(requestCode, resultCode, data);
             filePath = FileUtil.getSaveFile(getApplicationContext()).getAbsolutePath();
        }catch (Exception e)
        {
            e.printStackTrace();
        }

        if (requestCode == REQUEST_CODE_GENERAL && resultCode == Activity.RESULT_OK) {
            RecognizeService.recGeneral(getApplicationContext(),FileUtil.getSaveFile(getApplicationContext()).getAbsolutePath(),
                    new RecognizeService.ServiceListener() {
                        @Override
                        public void onResult(String result) {
                            infoPopText(result);
                        }
                    });
        }
        if (requestCode == REQUEST_CODE_GENERAL_BASIC && resultCode == Activity.RESULT_OK) {
            RecognizeService.recGeneralBasic(getApplicationContext(),FileUtil.getSaveFile(getApplicationContext()).getAbsolutePath(),
                    new RecognizeService.ServiceListener() {
                        @Override
                        public void onResult(String result) {
                            infoPopText(result);
                        }
                    });
        }
        if (requestCode == REQUEST_CODE_GENERAL_ENHANCED && resultCode == Activity.RESULT_OK) {
            RecognizeService.recGeneralEnhanced(getApplicationContext(),FileUtil.getSaveFile(getApplicationContext()).getAbsolutePath(),
                    new RecognizeService.ServiceListener() {
                        @Override
                        public void onResult(String result) {
                            infoPopText(result);
                        }
                    });
        }
        if (requestCode == REQUEST_CODE_GENERAL_WEBIMAGE && resultCode == Activity.RESULT_OK) {
            RecognizeService.recWebimage(getApplicationContext(),FileUtil.getSaveFile(getApplicationContext()).getAbsolutePath(),
                    new RecognizeService.ServiceListener() {
                        @Override
                        public void onResult(String result) {
                            infoPopText(result);
                        }
                    });
        }
        if (requestCode == REQUEST_CODE_BANKCARD && resultCode == Activity.RESULT_OK) {
            RecognizeService.recBankCard(getApplicationContext(),FileUtil.getSaveFile(getApplicationContext()).getAbsolutePath(),
                    new RecognizeService.ServiceListener() {
                        @Override
                        public void onResult(String result) {
                            infoPopText(result);
                        }
                    });
        }

        if (requestCode == REQUEST_CODE_GENERAL && resultCode == Activity.RESULT_OK) {
            RecognizeService.recGeneral(getApplicationContext(),FileUtil.getSaveFile(getApplicationContext()).getAbsolutePath(),
                    new RecognizeService.ServiceListener() {
                        @Override
                        public void onResult(String result) {
                            infoPopText(result);
                        }
                    });
        }

        if (requestCode == REQUEST_CODE_GENERAL && resultCode == Activity.RESULT_OK) {
            RecognizeService.recGeneral(getApplicationContext(),FileUtil.getSaveFile(getApplicationContext()).getAbsolutePath(),
                    new RecognizeService.ServiceListener() {
                        @Override
                        public void onResult(String result) {
                            infoPopText(result);
                        }
                    });
        }

        if (requestCode == REQUEST_CODE_LICENSE_PLATE && resultCode == Activity.RESULT_OK) {
//            String filePath = FileUtil.getSaveFile(getApplicationContext()).getAbsolutePath();
            OcrRequestParams param = new OcrRequestParams();
            param.setImageFile(new File(filePath));
            OCR.getInstance(getApplicationContext()).recognizeLicensePlate(param, new OnResultListener<OcrResponseResult>() {
                @Override
                public void onResult(OcrResponseResult result) {
                    // ?????????????????????OcrResponseResult??????
                    String str = result.getJsonRes();
                    try {
                        JSONObject obj = new JSONObject(str);
                        JSONObject mapJSON = obj.getJSONObject("words_result");
                        String words = mapJSON.getString("number");
                        checkNewCarcode(words);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(OCRError error) {
                    // ?????????????????????OCRError??????
                    Toast.makeText(getApplicationContext(),"????????????",Toast.LENGTH_LONG).show();
                }
            });
        }
        if(requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK)
        {
            Toast.makeText(getApplicationContext(),"????????????",Toast.LENGTH_LONG).show();
            Bundle bundle = data.getExtras();
            String resultid = bundle.getString("resultid");
             PurchaseRejectedInfo info = new PurchaseRejectedInfo();

             info.good_barcode = resultid;
            Intent intent = new Intent(this,AddPurchaseActivity.class);
            intent.putExtra("PurchaseRejectedInfo",info);
            intent.putExtra("type","3");
            startActivity(intent);
        }
        if (requestCode == REQUEST_CODE_VEHICLE_LICENSE && resultCode == Activity.RESULT_OK) {
            OcrRequestParams param = new OcrRequestParams();
            param.setImageFile(new File(filePath));
            param.putParam("detect_direction", true);
            OCR.getInstance(getApplicationContext()).recognizeVehicleLicense(param, new OnResultListener<OcrResponseResult>() {
                @Override
                public void onResult(OcrResponseResult result) {
                    // ?????????????????????OcrResponseResult??????
//                    listener.onResult(result.getJsonRes());
                    String str = result.getJsonRes();
                    try {
                        JSONObject obj = new JSONObject(str);

                        String plate = "";
                        String RegTime= "";
                        String userName = "";
                        String vinNO = "";
                        String carType = "";
                        int num = obj.optInt("words_result_num");
                        if(num > 0) {
                            JSONObject mapJSON = obj.getJSONObject("words_result");
                            Iterator<String> iterator = mapJSON.keys();
                            while (iterator.hasNext()) {
                                String key = iterator.next();
                                JSONObject keyJSON = mapJSON.getJSONObject(key);
                                String words = keyJSON.getString("words");
                                if("????????????".equalsIgnoreCase(key))
                                {
                                    plate = words;
                                }else if("????????????".equalsIgnoreCase(key))
                                {
                                    StringBuffer stringBuffer = new StringBuffer(words);
                                    stringBuffer.insert(6,"-");
                                    stringBuffer.insert(4,"-");
                                    RegTime = stringBuffer.toString();

                                }else if("?????????".equalsIgnoreCase(key))
                                {
                                    userName = words;
                                }else if("??????????????????".equalsIgnoreCase(key))
                                {
                                    vinNO = words;
                                }else if("????????????".equalsIgnoreCase(key))
                                {
                                    carType = words;
                                }
                            }

                            final ArrayList arr = DBService.queryContactNameByCarcode(plate);
                            if(arr == null ||arr.size()==0){
                                Intent intent = new Intent(getBaseContext(), ContactAddNewActivity.class);
                                intent.putExtra("plate", plate);
                                intent.putExtra("RegTime", RegTime);
                                intent.putExtra("userName", userName);
                                intent.putExtra("vinNO", vinNO);
                                intent.putExtra("carType", carType);
                                startActivity(intent);
                                return;
                            }else {
                                if (arr.size() > 0) {
                                    //???????????????????????????????????????????????????????????????????????????????????????????????????
                                   final   Contact contact = (Contact) arr.get(0);
                                    havedRepairHistory(contact, new PostApiListener() {
                                        @Override
                                        public void onUploadVinPicSucceed(String vin) {

                                        }
                                        @Override
                                        public void onGetRepairHistory(ArrayList<RepairHistory> arr ) {
                                            if(arr!=null){
                                                if(arr.size()>0){
                                                    Intent intent = new  Intent(BaseActivity.this,RepairHistoryListActivity.class);
                                                    intent.putExtra(String.valueOf(R.string.key_parcel_allhistory), arr);
                                                    startActivity(intent);
                                                }else {
                                                    Intent intent = new  Intent(getBaseContext(),ContactInfoEditActivity.class);
                                                    intent.putExtra(String.valueOf(R.string.key_contact_edit_para), contact);
                                                    startActivityForResult(intent,1);
                                                }
                                            }else {
                                                Toast.makeText(getBaseContext(), "????????????,?????????????????????????????????????????????", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                } else {
                                    Contact contact = (Contact) arr.get(0);
                                    Intent intent = new  Intent(getBaseContext(),ContactInfoEditActivity.class);
                                    intent.putExtra(String.valueOf(R.string.key_contact_edit_para), contact);
                                    startActivityForResult(intent,1);
                                }
                            }
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                @Override
                public void onError(OCRError error) {
                    // ?????????????????????OCRError??????
                }
            });
        }

    }

    private void havedRepairHistory(Contact con, final PostApiListener listern){
        Map map = new HashMap();
        map.put("owner", con.getOwner());
        map.put("contactid", con.getIdfromnode());
        map.put("carcode", con.getCarCode());
        HttpManager.getInstance(this).queryContactAllRepair("/repair/queryOneAll3", map, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                if(jsonObject.optInt("code") == 1){
                    ArrayList<RepairHistory> arr = getArrayRepair2(jsonObject);
                    if(listern!=null){
                        listern.onGetRepairHistory(arr);
                    }else {

                    }
                }
                else {
                    listern.onGetRepairHistory(null);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                listern.onGetRepairHistory( null);
            }
        });

    }


    public void infoPopText(final String result) {
        Log.e(TAG,"????????????"+result);

        try {
            JSONObject obj = new JSONObject(result);

            JSONObject mapJSON = obj.getJSONObject("words_result");
            String words = mapJSON.getString("number");


                Intent intent = new Intent(getBaseContext(),ContactAddNewActivity.class);
                intent.putExtra("plate",words);
                startActivity(intent);




        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "????????????,?????????????????????????????????????????????", Toast.LENGTH_SHORT).show();

        }



    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        //??????????????????????????????????????????
//        ARouter.getInstance().destroy();
    }


    protected ArrayList<RepairHistory> getArrayRepair2(JSONObject ret){
        JSONArray arr = ret.optJSONArray("ret");

        ArrayList<RepairHistory> arrRep = new ArrayList();
        if (arr.length() > 0) {
            for (int i = 0; i < arr.length(); i++) {
                RepairHistory repFromServer = new RepairHistory();
                JSONObject obj = arr.optJSONObject(i);
                repFromServer.addition =obj.optString("addition").replace(" ", "");
                repFromServer.carCode =obj.optString("carcode").replace(" ", "");
                repFromServer.circle =obj.optString("circle");
                repFromServer.isreaded = obj.optString("isreaded");
                repFromServer.isClose = obj.optString("isclose");
                repFromServer.owner =obj.optString("owner");
                repFromServer.repairTime =obj.optString("repairetime");
                repFromServer.repairType =obj.optString("repairtype");
                repFromServer.tipCircle =obj.optString("tipcircle");
                repFromServer.totalKm =obj.optString("totalkm");
                repFromServer.idfromnode =obj.optString("_id");
                repFromServer.inserttime =obj.optString("inserttime");
                repFromServer.pics = obj.optString("pics");

                repFromServer.state =obj.optString("state");
                repFromServer.customremark =obj.optString("customremark");
                repFromServer.wantedcompletedtime =obj.optString("wantedcompletedtime");
                repFromServer.iswatiinginshop =obj.optString("iswatiinginshop");
                repFromServer.entershoptime =obj.optString("entershoptime");
                repFromServer.contactid =obj.optString("contactid");
                repFromServer.saleMoney = obj.optString("saleMoney");


                if(repFromServer.entershoptime.length()==0){
                    repFromServer.entershoptime =   repFromServer.inserttime;
                }

                JSONArray items = obj.optJSONArray("items");
                ArrayList<ADTReapirItemInfo> arrItems = new ArrayList();
                int totalPrice = 0;
                if(items != null){
                    for(int j=0;j<items.length();j++){
                        JSONObject itemObj = items.optJSONObject(j);
                        ADTReapirItemInfo item = ADTReapirItemInfo.fromWithJsonObj(itemObj);
                        totalPrice+=item.currentPrice;

                        arrItems.add(item);
                    }
                }
                repFromServer.arrRepairItems = arrItems;
                repFromServer.totalPrice = String.valueOf(totalPrice);

                Contact con = DBService.queryContact(repFromServer.carCode);
                if(con != null){
                    arrRep.add(repFromServer);
                }
            }
        }
        return arrRep;
    }

    /**
     * ??????
     * @param contact
     */
    protected void addNewRepair(Contact contact){
        final RepairHistory rep =  new RepairHistory();
        rep.addition = "";
        rep.repairType = "";
        rep.circle = "1";
        rep.totalKm = "";
        rep.isClose = "0";
        rep.isreaded = "0";
        rep.carCode = contact.getCarCode();
        rep.contactid =contact.getIdfromnode();
        rep.iswatiinginshop = "0";
        rep.customremark = "";
        rep.wantedcompletedtime = "";
        rep.entershoptime = "";

        ArrayList<ADTReapirItemInfo> arrItems = new ArrayList();
        rep.arrRepairItems = arrItems;
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
        cv.put("owner", LoginUserUtil.getTel(getBaseContext()));
        cv.put("id", "");
        cv.put("items", arrItmes);
        cv.put("contactid", rep.contactid);
        cv.put("iswatiinginshop", rep.iswatiinginshop);
        cv.put("customremark", rep.customremark);
        cv.put("wantedcompletedtime", rep.wantedcompletedtime);
        cv.put("entershoptime", rep.entershoptime);

        showWaitView();
        HttpManager.getInstance(getBaseContext()).updateOneRepair("/repair/add4", cv, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {

                stopWaitingView();
                if(jsonObject.optInt("code") == 1){
                    Toast.makeText(getBaseContext(),"????????????",Toast.LENGTH_SHORT).show();
                    rep.idfromnode = jsonObject.optJSONObject("ret").optString("_id");
                    rep.state = jsonObject.optJSONObject("ret").optString("state");
                    rep.owner = jsonObject.optJSONObject("ret").optString("owner");
                    ArrayList<ADTReapirItemInfo> arrItems = new ArrayList();
                    rep.arrRepairItems = arrItems;
                    Intent intent = new Intent(getBaseContext(),WorkRoomEditActivity.class);
                    intent.putExtra(String.valueOf(R.string.key_repair_edit_para), rep);
                    startActivityForResult(intent, 1);

                }else {
                    Toast.makeText(getBaseContext(),"????????????",Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                stopWaitingView();
                Toast.makeText(getApplicationContext(),"????????????",Toast.LENGTH_SHORT).show();
            }
        });
    }


    /**
     * ?????????????????????????????????????????????????????????
     * @param words
     */
    public void checkNewCarcode(String words){
        final ArrayList arr = DBService.queryContactNameByCarcode(words);
        if(arr == null ||arr.size()==0){
            Intent intent = new Intent(getBaseContext(),ContactAddNewActivity.class);
            intent.putExtra("plate",words);
            startActivity(intent);
            return;
        }else{
            if(arr.size() >0) {
                final  Contact contact = (Contact)arr.get(0);

                havedRepairHistory(contact, new PostApiListener() {
                    @Override
                    public void onUploadVinPicSucceed(String vin) {

                    }
                    @Override
                    public void onGetRepairHistory(ArrayList<RepairHistory> arr ) {
                        if(arr!=null){
                            if(arr.size()>0){
                                Intent intent = new  Intent(BaseActivity.this,RepairHistoryListActivity.class);
                                intent.putExtra(String.valueOf(R.string.key_parcel_allhistory), arr);
                                intent.putExtra(String.valueOf(R.string.key_contact_edit_para), contact);
                                startActivity(intent);
                            }else {
                                addNewRepair(contact);
                            }
                        }else {
                            Toast.makeText(getBaseContext(), "????????????,?????????????????????????????????????????????", Toast.LENGTH_SHORT).show();
                        }
                    }
                });



            }
        }
    }


    /**
     * ????????????
     */
    public void selectDate(DatePickerDialog.OnDateSetListener  listener) {
        SpeDateHelper.show(listener, getFragmentManager());
    }

}

