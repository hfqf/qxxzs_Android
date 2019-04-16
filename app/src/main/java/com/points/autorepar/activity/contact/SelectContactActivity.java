package com.points.autorepar.activity.contact;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.points.autorepar.R;
import com.points.autorepar.activity.repair.RepairInfoEditActivity;
import com.points.autorepar.activity.workroom.WorkRoomEditActivity;
import com.points.autorepar.bean.ADTReapirItemInfo;
import com.points.autorepar.bean.Contact;
import com.points.autorepar.bean.RepairHistory;
import com.points.autorepar.fragment.ContactFragment;
import com.points.autorepar.http.HttpManager;
import com.points.autorepar.activity.BaseActivity;
import com.points.autorepar.utils.LoginUserUtil;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class SelectContactActivity extends BaseActivity implements
        ContactFragment.OnContactFragmentListener{

    private  final  String TAG = "SelectContactActivity";
    private ContactFragment m_tab2;
    //几个滑动页面布局
    private LinearLayout  m_tab2Layout;

    private FragmentManager fragmentManager;
    private String           m_selectType;//0统计页选择联系人，1是老版本新建维修记录 2是开单页
    private  SelectContactActivity m_this;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_contact);
        m_this = this;
        Bundle bundle = this.getIntent().getExtras();
        m_selectType = bundle.getString("flag");

        fragmentManager = getFragmentManager();
        setTabSelection(0);
    }

    private void setTabSelection(int index)
    {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        m_tab2 = ContactFragment.newInstance("1");
        transaction.add(R.id.select_contact_bg,m_tab2);
        transaction.commit();
    }


    @Override
    public void onContactFramentReloadData() {


    }

    @Override
    public void  onSelectedContact(Contact contact){

        if(m_selectType.equals("1")){
            RepairHistory rep =  new RepairHistory();
            rep.addition = "";
            rep.repairType = "略";
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
            Intent intent = new Intent(this,RepairInfoEditActivity.class);
            intent.putExtra(String.valueOf(R.string.key_repair_edit_para), rep);
            startActivityForResult(intent, 0);

        }else if(m_selectType.equals("0")){
            Intent data = new Intent();
            data.putExtra("contact",contact);
            setResult(1, data);
            finish();
        }else {

           final RepairHistory rep =  new RepairHistory();
            rep.isAddedNewRepair = 1;
            rep.addition = "";
            rep.repairType = "略";
            rep.circle = "";
            rep.totalKm = "";
            rep.isClose = "0";
            rep.isreaded = "0";
            rep.carCode = contact.getCarCode();
            rep.contactid =contact.getIdfromnode();
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
            cv.put("owner", LoginUserUtil.getTel(this));
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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            finish();
        }else if(requestCode == 1){
            finish();;
        }
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


}
