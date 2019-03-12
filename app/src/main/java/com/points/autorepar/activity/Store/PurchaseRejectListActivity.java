package com.points.autorepar.activity.Store;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.points.autorepar.MainApplication;
import com.points.autorepar.R;
import com.points.autorepar.activity.BaseActivity;
import com.points.autorepar.adapter.InOutAdapter;
import com.points.autorepar.adapter.PurchaseRejectedAdapter;
import com.points.autorepar.bean.CompanyItemInfo;
import com.points.autorepar.bean.Contact;
import com.points.autorepar.bean.PurchaseRejectedInfo;
import com.points.autorepar.http.HttpManager;
import com.points.autorepar.lib.cjj.MaterialRefreshLayout;
import com.points.autorepar.lib.sortlistview.ClearEditText;
import com.points.autorepar.lib.wheelview.WheelView;
import com.points.autorepar.utils.LoginUserUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class PurchaseRejectListActivity extends BaseActivity {

    public PurchaseRejectListActivity() {
    }

    //回调接口
    public interface OnContactFragmentListener {
        //TODO 可自定义回调函数
        public void onContactFramentReloadData();

        public void onSelectedContact(Contact contact);
    }


    private PurchaseRejectListActivity m_this;
    private static final String TAG = "InAndOutRecordsActivity";
    //下拉上拉刷新控件
    private MaterialRefreshLayout materialRefreshLayout;

    private ListView sortListView;
    //    private SideBar slideBar;
    private TextView dialog;
    private InOutAdapter adapter;
    private ClearEditText mClearEditText;
    private Button addBtn;
    private Button           backBtn;
    private ArrayList<PurchaseRejectedInfo> mDataList;
    private LinearLayout m_navi_btns_bg;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreateView");
        m_this = this;
        setContentView(R.layout.fragment_suppycompanylist);
        EditText m_searchText = (EditText) findViewById(R.id.search_text);
        m_searchText.setVisibility(View.GONE);
        mDataList = new ArrayList<PurchaseRejectedInfo>();
        TextView common_navi_title = (TextView)findViewById(R.id.common_navi_title);
        common_navi_title.setText("库存退货");

        dialog = (TextView)findViewById(R.id.dialog);
        backBtn =(Button) findViewById(R.id.common_navi_back);
        backBtn.setVisibility(View.VISIBLE);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        addBtn = (Button)findViewById(R.id.common_navi_add);
        addBtn.setVisibility(View.GONE);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("Contact", "onActivityCreated");
                Intent intent = new Intent(m_this,AddSuppyCompanyActivity.class);
                intent.putExtra("type",1);
                startActivity(intent);
            }
        });

        if(MainApplication.getInstance().getUserType(PurchaseRejectListActivity.this) == 2)
        {

            addBtn.setVisibility(View.VISIBLE);
        }else{
            if("1".equalsIgnoreCase(MainApplication.getInstance().getSPValue(PurchaseRejectListActivity.this,"iscanaddnewcontact")))
            {
                addBtn.setVisibility(View.VISIBLE);
            }else {

                addBtn.setVisibility(View.INVISIBLE);
            }
        }



        sortListView = (ListView) findViewById(R.id.id_contact_list);

        sortListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                final PurchaseRejectedInfo info = (PurchaseRejectedInfo)mDataList.get(position);

                Intent intent = new Intent(m_this,PurchaseRejectedActivity.class);
                intent.putExtra("PurchaseRejectedInfo",info);
                startActivity(intent);

            }
        });

        getAllCompanylist();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 1){
        }
    }





    private  void  getAllCompanylist(){
        Map queryAllMap = new HashMap();
        queryAllMap.put("owner",   LoginUserUtil.getTel(PurchaseRejectListActivity.this));

        HttpManager.getInstance(PurchaseRejectListActivity.this).queryAllContacts("/warehousegoods/query", queryAllMap, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {


                if (jsonObject.optInt("code") == 1) {

                    JSONArray arr = jsonObject.optJSONArray("ret");
                    if (arr.length() > 0) {

                        mDataList.clear();
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject obj = arr.optJSONObject(i);
                            ArrayList<PurchaseRejectedInfo> arrItems = new ArrayList();
                            PurchaseRejectedInfo info = PurchaseRejectedInfo.fromWithJsonObj(obj);
                            mDataList.add(info);
                        }

                        final  ArrayList<PurchaseRejectedInfo> list = mDataList;
                        Handler mainHandler = new Handler(Looper.getMainLooper());
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                PurchaseRejectedAdapter m_adapter = new PurchaseRejectedAdapter(m_this, list);
                                sortListView.setAdapter(m_adapter);
                                m_adapter.notifyDataSetChanged();
//
                            }
                        });
                    }




                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e(TAG, "contact/queryAll:onErrorResponse " + volleyError);

            }
        });
    }


    public void onResume() {
        super.onResume();
        getAllCompanylist();

    }
    private  void delInfo (String infoID){
        Map queryAllMap = new HashMap();
        queryAllMap.put("id",  infoID);

        HttpManager.getInstance(PurchaseRejectListActivity.this).queryAllContacts("/warehousegoods/query", queryAllMap, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {


                if (jsonObject.optInt("code") == 1) {


                    getAllCompanylist();


                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e(TAG, "contact/queryAll:onErrorResponse " + volleyError);

            }
        });
    }
}
