package com.points.autorepar.activity.contact;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.baidu.ocr.sdk.OCR;
import com.baidu.ocr.sdk.OnResultListener;
import com.baidu.ocr.sdk.exception.OCRError;
import com.baidu.ocr.sdk.model.OcrRequestParams;
import com.baidu.ocr.sdk.model.OcrResponseResult;
import com.bumptech.glide.Glide;
import com.points.autorepar.MainApplication;
import com.points.autorepar.activity.WebActivity;
import com.points.autorepar.activity.repair.RepairHistoryListActivity;
import com.points.autorepar.activity.workroom.WorkRoomEditActivity;
import com.points.autorepar.adapter.ContactInfoAdapter;
import com.points.autorepar.bean.ADTReapirItemInfo;
import com.points.autorepar.bean.RepairHistory;
import com.points.autorepar.bean.UpdateCarcodeEvent;
import com.points.autorepar.http.HttpManager;
import com.points.autorepar.activity.BaseActivity;
import com.points.autorepar.lib.ocr.ui.camera.FileUtil;
import com.points.autorepar.sql.DBService;
import com.points.autorepar.bean.Contact;
import com.points.autorepar.R;
import com.points.autorepar.utils.LoggerUtil;
import com.points.autorepar.utils.LoginUserUtil;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import de.greenrobot.event.EventBus;

public class ContactInfoEditActivity extends BaseActivity{
    private final  String  TAG = "ContactInfoEditActivity";
    private Button mBackBtn;
    private Button mConfirmBtn;
    private Button mdeleteBtn;
    private Button mSeeAllBtn;
    private Button mdeleteContactBtn;
    private Button mAddNewRepBtn;
    private Button mCloseTipBtn;
    private Button mOpenCardBtn;
    private Button mChargeCardBtn;
    private Button mHistoryCardBtn;
    private  String    m_headUrl;
    private TextView mTitle;
    private ListView mListView;
    private ContactInfoAdapter m_adapter;
    private View     mFooterView;
    private View     mHeaderView;
    ContactInfoEditActivity m_this;
    private  Contact m_currentContact;
    private ImageButton m_headBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_info_edit);
        EventBus.getDefault().register(this);
        m_this = this;
        m_currentContact = getIntent().getParcelableExtra(String.valueOf(R.string.key_contact_edit_para));

        RelativeLayout naviLayout =  (RelativeLayout)findViewById(R.id.contact_adduser_navi);
        mTitle      = (TextView)naviLayout.findViewById(R.id.common_navi_title);
        mTitle.setText("详情(可编辑)");

        mListView = (ListView) findViewById(R.id.contact_info_listView);

        mBackBtn    = (Button)naviLayout.findViewById(R.id.common_navi_back);
        mBackBtn.setVisibility(View.VISIBLE);
        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mConfirmBtn = (Button)naviLayout.findViewById(R.id.common_navi_add);
        mConfirmBtn.setText("保存");
        mConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateBtnClicked();
            }
        });

        if(MainApplication.getInstance().getUserType(ContactInfoEditActivity.this) == 2) {
            mConfirmBtn.setVisibility(View.VISIBLE);
        }else{
            if("1".equalsIgnoreCase(MainApplication.getInstance().getSPValue(ContactInfoEditActivity.this,"iscaneditcontact"))) {
                mConfirmBtn.setVisibility(View.VISIBLE);
            }else {
                mConfirmBtn.setVisibility(View.INVISIBLE);
            }
        }

        LayoutInflater layoutInflater =
                (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        if(m_currentContact.getisVip().equals("1")){
            mFooterView = layoutInflater.inflate(R.layout.contact_info_footerview2, null);
        }else {
            mFooterView = layoutInflater.inflate(R.layout.contact_info_footerview, null);
        }

        mHeaderView = layoutInflater.inflate(R.layout.contact_info_headerview, null);
        mListView.addHeaderView(mHeaderView);

        mListView.addFooterView(mFooterView);
        m_adapter = new ContactInfoAdapter(this,m_currentContact);
        m_adapter.m_isAddNew = false;
        mListView.setAdapter(m_adapter);

        m_headUrl = MainApplication.consts(m_this).BOS_SERVER+m_currentContact.getHeadurl();
        m_headBtn = (ImageButton) mHeaderView.findViewById(R.id.contact_edit_headurl);
        Glide.with(m_this).load(m_headUrl).into(m_headBtn);

        m_headBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSelectPicToUpload(1, new speUploadListener() {
                    @Override
                    public void uploadPictureSucceed(final String newHeadUrl) {
                        final String _newUrl =  newHeadUrl;
                        Log.e(TAG,newHeadUrl);
                        m_currentContact.setHeadurl(newHeadUrl);
                        Map map = new HashMap();
                        map.put("headurl", newHeadUrl);
                        map.put("id", m_adapter.m_contact.getIdfromnode());
                        HttpManager.getInstance(getApplicationContext()).updateHeadUrl("/contact/updateHeadUrl", map, new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject jsonObject) {
                                        if(jsonObject.optInt("code") == 1){
                                            Log.e(TAG,"/onResponse:"+jsonObject.toString());
                                            final    String url = MainApplication.consts(m_this).BOS_SERVER+newHeadUrl;
                                            DBService.updateContact(m_currentContact);
                                            Glide.get(m_this).clearMemory();
                                            Glide.with(m_this).load(url).into(m_headBtn);
                                        }
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError volleyError) {

                                    }
                                });
                    }
                });
            }
        });

        mdeleteBtn = (Button)mFooterView.findViewById(R.id.contact_edit_deleteBtn);
        mdeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteContactAndAllRepair();
            }
        });
        mSeeAllBtn = (Button)mFooterView.findViewById(R.id.contact_edit_queryBtn);
        mSeeAllBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seeAllRepair();
            }
        });

        if(MainApplication.getInstance().getUserType(ContactInfoEditActivity.this) == 2) {
            mSeeAllBtn.setVisibility(View.VISIBLE);
        }else{
            if("1".equalsIgnoreCase(MainApplication.getInstance().getSPValue(ContactInfoEditActivity.this,"iscanseecontactrepairs"))) {
                mSeeAllBtn.setVisibility(View.VISIBLE);
            }else {
                mSeeAllBtn.setVisibility(View.GONE);
            }
        }

        if(MainApplication.getInstance().getUserType(ContactInfoEditActivity.this) == 2) {
            mdeleteBtn.setVisibility(View.VISIBLE);
        }else{
            mdeleteBtn.setVisibility(View.GONE);
        }

        mdeleteContactBtn = (Button)mFooterView.findViewById(R.id.contact_edit_deletecontact);
        if(MainApplication.getInstance().getUserType(ContactInfoEditActivity.this) == 2) {
            mdeleteContactBtn.setVisibility(View.VISIBLE);
        }else{
            if("1".equalsIgnoreCase(MainApplication.getInstance().getSPValue(ContactInfoEditActivity.this,"iscandelcontact"))) {
                mdeleteContactBtn.setVisibility(View.VISIBLE);
            }else {
                mdeleteContactBtn.setVisibility(View.GONE);
            }
        }

        mdeleteContactBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder normalDialog =
                        new AlertDialog.Builder(m_this);
                normalDialog.setTitle("删除此客户,不可恢复!");
                normalDialog.setMessage("确认删除?");
                normalDialog.setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteServerAndLocalContact(m_currentContact);
                            }
                        });
                normalDialog.setNegativeButton("关闭",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                normalDialog.show();
            }
        });

        mCloseTipBtn = (Button)mFooterView.findViewById(R.id.contact_edit_closetip);
        mCloseTipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mAddNewRepBtn = (Button)mFooterView.findViewById(R.id.contact_add_new_repair);
        if(MainApplication.getInstance().getUserType(ContactInfoEditActivity.this) == 2) {
            mAddNewRepBtn.setVisibility(View.VISIBLE);
        }else{
            if("1".equalsIgnoreCase(MainApplication.getInstance().getSPValue(ContactInfoEditActivity.this,"iscandelcontact"))) {
                mAddNewRepBtn.setVisibility(View.VISIBLE);
            }else {
                mAddNewRepBtn.setVisibility(View.GONE);
            }
        }

        if(m_currentContact.getisVip().equals("1")){
            mChargeCardBtn = (Button)mFooterView.findViewById(R.id.contact_add_charge_card);
            mChargeCardBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String server = "http://store.autorepairehelper.cn";
                    String url = server+"/vip-client/list?shop="+LoginUserUtil.getTel(ContactInfoEditActivity.this)+"&key="+m_currentContact.getTel();
                    WebActivity.actionStart(ContactInfoEditActivity.this, url,"充值/套餐");
                }
            });

            mHistoryCardBtn = (Button)mFooterView.findViewById(R.id.contact_add_card_history);
            mHistoryCardBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String server = "http://store.autorepairehelper.cn";
                    String url = server+"/vip-client/cardHistory?shop="+LoginUserUtil.getTel(ContactInfoEditActivity.this)+"&key="+m_currentContact.getTel()+"&cardId="+m_currentContact.getIdfromnode();
                    WebActivity.actionStart(ContactInfoEditActivity.this, url,"充值/套餐");
                }
            });
        }else {
            mOpenCardBtn = (Button)mFooterView.findViewById(R.id.contact_add_open_card);
            mOpenCardBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String server = "http://store.autorepairehelper.cn";
                    String url = server+"/vip-client/add?shop="+LoginUserUtil.getTel(ContactInfoEditActivity.this)+"&key="+m_currentContact.getTel();
                    WebActivity.actionStart(ContactInfoEditActivity.this, url,"开卡");
                }
            });
        }

        mAddNewRepBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final RepairHistory rep =  new RepairHistory();
                rep.isAddedNewRepair = 1;
                rep.addition = "";
                rep.repairType = "略";
                rep.circle = "";
                rep.totalKm = "";
                rep.isClose = "0";
                rep.isreaded = "0";
                rep.carCode = m_currentContact.getCarCode();
                rep.contactid =m_currentContact.getIdfromnode();
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
                cv.put("owner", LoginUserUtil.getTel(m_this));
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
                            startActivity(intent);
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
            }
        });
    }

    private  void deleteServerAndLocalContact(final Contact _delCon){
        showWaitView();
        Map map = new HashMap();
        map.put("id", _delCon.getIdfromnode());
        map.put("owner", _delCon.getOwner());
        HttpManager.getInstance(m_this).deleteOneContact("/contact/del3", map, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                stopWaitingView();
                final Contact con = _delCon;
                Boolean isSucceed= jsonObject.optString("code").equals("1");
                if(isSucceed){
                    if(DBService.deleteContact(con)){
                        deleteAllReapirs();
                    }else {
                        Toast.makeText(m_this, "删除失败", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(m_this, "删除失败", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                stopWaitingView();
                Toast.makeText(m_this, "删除失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateBtnClicked(){
        showWaitView();
        Map map = new HashMap();
        map.put("name", m_adapter.m_contact.getName());
        map.put("carcode", m_adapter.m_contact.getCarCode());
        map.put("tel", m_adapter.m_contact.getTel());
        map.put("cartype", m_adapter.m_contact.getCarType());
        map.put("owner", m_adapter.m_contact.getOwner());
        map.put("id", m_adapter.m_contact.getIdfromnode());
        map.put("vin", m_adapter.m_contact.getVin().length() == 0?"":m_adapter.m_contact.getVin());
        map.put("carregistertime", m_adapter.m_contact.getCarregistertime().length() == 0 ?"":m_adapter.m_contact.getCarregistertime());
        map.put("headurl", m_adapter.m_contact.getHeadurl().length() == 0?"":m_adapter.m_contact.getHeadurl());
        map.put("isbindweixin", m_adapter.m_contact.getIsbindweixin());
        map.put("weixinopenid", m_adapter.m_contact.getWeixinopenid().length() == 0 ?"":m_adapter.m_contact.getWeixinopenid());
        map.put("safecompany", m_adapter.m_contact.getSafecompany().length() == 0 ?"":m_adapter.m_contact.getSafecompany());
        map.put("safenexttime", m_adapter.m_contact.getSafenexttime().length() == 0 ?"":m_adapter.m_contact.getSafenexttime());
        map.put("yearchecknexttime", m_adapter.m_contact.getYearchecknexttime().length() == 0 ?"":m_adapter.m_contact.getYearchecknexttime());
        map.put("tqTime1", m_adapter.m_contact.getTqTime1().length() == 0 ?"":m_adapter.m_contact.getTqTime1());
        map.put("tqTime2", m_adapter.m_contact.getTqTime2().length() == 0 ?"":m_adapter.m_contact.getTqTime2());
        map.put("key",m_adapter.m_contact.getCar_key()== null?"":m_adapter.m_contact.getCar_key());
        map.put("safecompany3", m_adapter.m_contact.getSafecompany3().length() == 0 ?"":m_adapter.m_contact.getSafecompany3());
        map.put("safenexttime3", m_adapter.m_contact.getSafenexttime3().length() == 0 ?"":m_adapter.m_contact.getSafenexttime3());
        map.put("tqTime3", m_adapter.m_contact.getTqTime3().length() == 0 ?"":m_adapter.m_contact.getTqTime3());

        HttpManager.getInstance(this).updateContact("/contact/update4",
                map,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        stopWaitingView();
                        Log.e(TAG, LoggerUtil.jsonFromObject(response));
                        if(response.optInt("code") == 1){
                            DBService.updateContact(m_adapter.m_contact);
                            Toast.makeText(getApplicationContext(),"修改成功",Toast.LENGTH_SHORT).show();
                            finishActivity(1);
                        }else {
                            Toast.makeText(getApplicationContext(),"修改失败",Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        stopWaitingView();
                        Toast.makeText(getApplicationContext(),"修改失败",Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * 私有方法
     */
    //删除当前用户以及对应的维修记录
    private  void deleteContactAndAllRepair( ){
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(this);
        normalDialog.setTitle("删除所有维修记录,不可恢复!");
        normalDialog.setMessage("确认删除?");
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        deleteAllReapirs();

                    }
                });
        normalDialog.setNegativeButton("关闭",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        normalDialog.show();
    }

    private ArrayList<RepairHistory> getArrayRepair(JSONObject ret){
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

                JSONArray arrPicsObj = obj.optJSONArray("carinfopics");
                ArrayList<String> arrPics = new ArrayList();
                if(arrPicsObj != null){
                    for(int j=0;j<arrPicsObj.length();j++){
                        String url = arrPicsObj.optString(j);
                        arrPics.add(url);
                    }
                }
                repFromServer.arrCarInfoPics = arrPics;

                repFromServer.totalPrice = String.valueOf(totalPrice);
                Contact con = DBService.queryContact(repFromServer.carCode);
                if(con != null){
                    arrRep.add(repFromServer);
                }
            }
        }
        return arrRep;
    }

    //查看当前用户所有维修记录
    private  void seeAllRepair( ){
        showWaitView();
        Map map = new HashMap();
        map.put("owner", m_currentContact.getOwner());
        map.put("contactid", m_currentContact.getIdfromnode());
        map.put("carcode", m_currentContact.getCarCode());

        HttpManager.getInstance(m_this).queryContactAllRepair("/repair/queryOneAll3", map, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                stopWaitingView();
                Log.e(TAG,"/repair/queryAllTiped"+jsonObject.toString());
                if(jsonObject.optInt("code") == 1){
                    ArrayList<RepairHistory> arr = getArrayRepair(jsonObject);
                    Intent intent = new  Intent(m_this,RepairHistoryListActivity.class);
                    intent.putExtra(String.valueOf(R.string.key_parcel_allhistory), arr);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(m_this,"查看失败",Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                stopWaitingView();
                Toast.makeText(m_this,"查看失败",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private  void  deleteAllReapirs(){
        showWaitView();
        Map map = new HashMap();
        map.put("owner", LoginUserUtil.getTel(m_this));
        map.put("contactid", m_currentContact.getIdfromnode());
        map.put("carcode", m_currentContact.getCarCode());
        HttpManager.getInstance(m_this).delAllRepair("/repair/delAll3", map, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                stopWaitingView();
                Boolean isSucceed= jsonObject.optString("code").equals("1");
                if(isSucceed){
                        Toast.makeText(m_this, "删除成功", Toast.LENGTH_SHORT).show();
                        finish();
                }else {
                    Toast.makeText(m_this, "删除失败", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                stopWaitingView();
                Toast.makeText(m_this, "删除失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onActivityResult(int requestCode
            , int resultCode, Intent intent) {
        getTakePhoto().onActivityResult(requestCode, resultCode, intent);
        if (requestCode == REQUEST_CODE_VEHICLE_LICENSE && resultCode == Activity.RESULT_OK) {
            String filePath = FileUtil.getSaveFile(ContactInfoEditActivity.this).getAbsolutePath();
            OcrRequestParams param = new OcrRequestParams();
            param.setImageFile(new File(filePath));
            param.putParam("detect_direction", true);
            OCR.getInstance(ContactInfoEditActivity.this).recognizeVehicleLicense(param, new OnResultListener<OcrResponseResult>() {
                @Override
                public void onResult(OcrResponseResult result) {
                    // 调用成功，返回OcrResponseResult对象
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
                                if("号牌号码".equalsIgnoreCase(key))
                                {
                                    plate = words;
                                }else if("注册日期".equalsIgnoreCase(key))
                                {
                                    StringBuffer stringBuffer = new StringBuffer(words);
                                    stringBuffer.insert(6,"-");
                                    stringBuffer.insert(4,"-");
                                    RegTime = stringBuffer.toString();
                                }else if("所有人".equalsIgnoreCase(key))
                                {
                                    userName = words;
                                }else if("车辆识别代号".equalsIgnoreCase(key))
                                {
                                    vinNO = words;
                                }else if("品牌型号".equalsIgnoreCase(key))
                                {
                                    carType = words;
                                }
                            }
                            m_currentContact.setVin(vinNO);
                            m_currentContact.setCarregistertime(RegTime);
                            m_currentContact.setName(userName);
                            m_currentContact.setCarCode(plate);
                            m_adapter.m_contact = m_currentContact;
                            m_adapter.notifyDataSetChanged();
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                @Override
                public void onError(OCRError error) {

                }
            });
        }
    }

    public void onEventMainThread(UpdateCarcodeEvent event) {
        String data = event.getCode();
        startSelectLicensePlatePicToUpload(3, new speUploadLicensePlateListener() {
            @Override
            public void onUploadLicensePlatePicSucceed(String licensePlate) {
                m_adapter.m_contact.setCarCode(licensePlate);
                m_adapter.notifyDataSetChanged();
            }
        });
    }

}
