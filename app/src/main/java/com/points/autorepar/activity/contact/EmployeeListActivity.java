package com.points.autorepar.activity.contact;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.points.autorepar.R;
import com.points.autorepar.activity.BaseActivity;
import com.points.autorepar.bean.Contact;
import com.points.autorepar.bean.ContactOrderInfo;
import com.points.autorepar.bean.EmployeeInfo;
import com.points.autorepar.http.HttpManager;
import com.points.autorepar.lib.cjj.MaterialRefreshLayout;
import com.points.autorepar.lib.cjj.MaterialRefreshListener;
import com.points.autorepar.lib.sortlistview.CharacterParser;
import com.points.autorepar.lib.sortlistview.ClearEditText;
import com.points.autorepar.lib.sortlistview.EmployeeSortAdapter;
import com.points.autorepar.lib.sortlistview.PinyinComparator;
import com.points.autorepar.lib.sortlistview.SideBar;
import com.points.autorepar.lib.sortlistview.SortAdapter;
import com.points.autorepar.lib.sortlistview.SortModel;
import com.points.autorepar.sql.DBService;
import com.points.autorepar.utils.LoggerUtil;
import com.points.autorepar.utils.LoginUserUtil;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmployeeListActivity extends BaseActivity {
    
    //回调接口
    public interface OnContactFragmentListener {
        //TODO 可自定义回调函数
        public void onContactFramentReloadData();

        public void onSelectedContact(Contact contact);
    }


    private EmployeeListActivity m_this;
    private static final String TAG = "InAndOutRecordsActivity";
    //下拉上拉刷新控件
    private MaterialRefreshLayout materialRefreshLayout;

    private ListView sortListView;
    private SideBar slideBar;
    private TextView dialog;
    private EmployeeSortAdapter adapter;
    private ClearEditText mClearEditText;
    private CharacterParser characterParser;
    private List<EmployeeInfo> SourceDateList;
    private PinyinComparator pinyinComparator;

    private boolean          m_isSelectContact;
    private Button addBtn;
    private Button           backBtn;
    List<SortModel>          filterDateList;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreateView");
        m_this = this;
        setContentView(R.layout.activity_employee);
        characterParser = CharacterParser.getInstance();
        pinyinComparator = new PinyinComparator();
        slideBar = (SideBar)findViewById(R.id.sidrbar);
        dialog = (TextView)findViewById(R.id.dialog);
        slideBar.setTextView(dialog);



        TextView common_navi_title = (TextView)findViewById(R.id.common_navi_title);
        common_navi_title.setText("员工");

        backBtn =(Button) findViewById(R.id.common_navi_back);
        backBtn.setVisibility(View.VISIBLE);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        addBtn = (Button)findViewById(R.id.common_navi_add);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("Contact", "onActivityCreated");
                Intent intent = new Intent(m_this,EmployeeAddNewActivity.class);

                intent.putExtra("type",1);
                startActivity(intent);
            }
        });

        if(m_isSelectContact){
            showQueryViewStyles();
        }

        sortListView = (ListView) findViewById(R.id.id_contact_list);
        sortListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                EmployeeInfo info =  SourceDateList.get(position);
                Intent intent = new Intent(m_this,EmployeeAddNewActivity.class);
                intent.putExtra("type",2);
                    intent.putExtra("data",info);
                    startActivityForResult(intent, 1001);

            }
        });

        sortListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                final int _index = i;
                final AlertDialog.Builder normalDialog =
                        new AlertDialog.Builder(m_this);
                normalDialog.setTitle("删除此员工,不可恢复!");
                normalDialog.setMessage("确认删除?");
                normalDialog.setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                Map map = new HashMap();
                                EmployeeInfo info =  SourceDateList.get(_index);
                                map.put("id",info.id);
                                showWaitView();
                                HttpManager.getInstance(EmployeeListActivity.this).addContact("/employee/del",
                                        map,
                                        new Response.Listener<JSONObject>() {
                                            @Override
                                            public void onResponse(JSONObject response) {

                                                stopWaitingView();
                                                if(response.optInt("code") == 1){
                                                    Toast.makeText(getApplicationContext(),"删除成功",Toast.LENGTH_LONG).show();
                                                    reloadDataAndRefreshView();
                                                }else {
                                                    Toast.makeText(getApplicationContext(),response.optString("msg"),Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        },
                                        new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {


                                            }
                                        });

                            }
                        });
                normalDialog.setNegativeButton("关闭",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //...To-do
                            }
                        });
                // 显示
                normalDialog.show();





                return false;
            }
        });




        materialRefreshLayout = (MaterialRefreshLayout) m_this.findViewById(R.id.refresh_contact2);
        materialRefreshLayout.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(final MaterialRefreshLayout materialRefreshLayout) {

                reloadDataAndRefreshView();

            }

            @Override
            public void onfinish() {

                Log.e("Contact", "onfinish");

            }

            @Override
            public void onRefreshLoadMore(final MaterialRefreshLayout materialRefreshLayout) {

                reloadDataAndRefreshView();

            }
        });
        adapter = new EmployeeSortAdapter(EmployeeListActivity.this, SourceDateList);
        sortListView.setAdapter(adapter);

        reloadDataAndRefreshView();



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 1){
            reloadDataAndRefreshView();
        }
    }

    public void showQueryViewStyles(){
        addBtn.setText("取消");
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m_this.finish();
            }
        });

    }

    /**
     * @param
     * @return
     */
    private List<SortModel> filledData( ){
        ArrayList<Contact> arr = DBService.queryAllContact();
        List<SortModel> mSortList = new ArrayList<SortModel>();

        for(int i=0; i<arr.size(); i++){
            SortModel sortModel = new SortModel();
            Contact con =  arr.get(i);
            sortModel.setName(con.getName());
            sortModel.contact = con;
            String pinyin = characterParser.getSelling(con.getName());
            String sortString = "";
            if(pinyin.length() > 0){
                sortString = pinyin.substring(0, 1).toUpperCase();
            }


            if(sortString.matches("[A-Z]")){
                sortModel.setSortLetters(sortString.toUpperCase());
            }else{
                sortModel.setSortLetters("#");
            }

            mSortList.add(sortModel);
        }
        return mSortList;

    }
    public void onResume() {
        super.onResume();
        reloadDataAndRefreshView();
    }

    //private function

    /*
     * 下拉或上拉相关函数
     */
    public   void  reloadDataAndRefreshView() {


        Map map = new HashMap();
        map.put("creatertel", LoginUserUtil.getTel(m_this).toString());
        HttpManager.getInstance(m_this).getCustomerOrdersList("/employee/query", map,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        JSONArray _arr = response.optJSONArray("ret");

                        SourceDateList = new ArrayList<EmployeeInfo>();

                        int _count = 0;
                        if (response.optInt("code") == 1) {
                            for (int i = 0; i < _arr.length(); i++) {
                                EmployeeInfo con = new EmployeeInfo();
                                JSONObject obj = _arr.optJSONObject(i);
                                con.username = obj.optString("username");
                                con.pwd = obj.optString("pwd");
                                con.headurl = obj.optString("headurl");
                                con.sex = obj.optString("sex");

                                con.age = obj.optString("age");
                                con.desc = obj.optString("desc");
                                con.tel = obj.optString("tel");
                                con.registertime = obj.optString("registertime");

                                con.roletype = obj.optString("roletype");
                                con.creatertel = obj.optString("creatertel");
                                con.state = obj.optString("state");
                                con.basepay = obj.optString("basepay");
                                con.workhourpay = obj.optString("workhourpay");

                                con.meritpay = obj.optString("meritpay");
                                con.id = obj.optString("_id");
                                con.iscanaddnewcontact = obj.optString("iscanaddnewcontact");
                                con.iscandelcontact = obj.optString("iscandelcontact");
                                con.iscaneditcontact = obj.optString("iscaneditcontact");

                                con.iscankaidan = obj.optString("iscankaidan");
                                con.iscanseecontactrepairs = obj.optString("iscanseecontactrepairs");
                                con.isneeddispatch = obj.optString("isneeddispatch");

                                SourceDateList.add(con);

                            }
                            adapter.setDate(SourceDateList);
                            adapter.notifyDataSetChanged();
                        }
                        materialRefreshLayout.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                materialRefreshLayout.finishRefresh();
                                materialRefreshLayout.finishRefreshLoadMore();
                            }
                        }, 100);
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        materialRefreshLayout.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                materialRefreshLayout.finishRefresh();
                                materialRefreshLayout.finishRefreshLoadMore();
                            }
                        }, 100);
                    }
                });

    }



}
