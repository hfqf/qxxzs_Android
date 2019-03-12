package com.points.autorepar.activity.Store;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.points.autorepar.MainApplication;
import com.points.autorepar.R;
import com.points.autorepar.activity.BaseActivity;
import com.points.autorepar.activity.contact.ContactAddNewActivity;
import com.points.autorepar.activity.contact.ContactInfoEditActivity;
import com.points.autorepar.adapter.CompanyListAdapter;
import com.points.autorepar.adapter.RepairHistoryAdapter;
import com.points.autorepar.bean.ADTReapirItemInfo;
import com.points.autorepar.bean.CompanyItemInfo;
import com.points.autorepar.bean.Contact;
import com.points.autorepar.http.HttpManager;
import com.points.autorepar.lib.cjj.MaterialRefreshLayout;
import com.points.autorepar.lib.cjj.MaterialRefreshListener;
import com.points.autorepar.lib.sortlistview.CharacterParser;
import com.points.autorepar.lib.sortlistview.ClearEditText;
import com.points.autorepar.lib.sortlistview.PinyinComparator;
import com.points.autorepar.lib.sortlistview.SideBar;
import com.points.autorepar.lib.sortlistview.SortAdapter;
import com.points.autorepar.lib.sortlistview.SortModel;
import com.points.autorepar.lib.wheelview.WheelView;
import com.points.autorepar.sql.DBService;
import com.points.autorepar.utils.JSONOejectUtil;
import com.points.autorepar.utils.LoginUserUtil;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SuppyCompanyListActivity extends BaseActivity {
    
    //回调接口
    public interface OnContactFragmentListener {
        //TODO 可自定义回调函数
        public void onContactFramentReloadData();

        public void onSelectedContact(Contact contact);
    }


    private SuppyCompanyListActivity m_this;
    private static final String TAG = "InAndOutRecordsActivity";
    //下拉上拉刷新控件
    private MaterialRefreshLayout materialRefreshLayout;

    private ListView sortListView;
//    private SideBar slideBar;
    private TextView dialog;
    private SortAdapter adapter;
    private ClearEditText mClearEditText;
//    private CharacterParser characterParser;
//    private List<SortModel> SourceDateList;
//    private PinyinComparator pinyinComparator;
//    private boolean          m_isSelectContact;
    private Button addBtn;
    private Button           backBtn;
    private ArrayList<CompanyItemInfo> mDataList;
//    List<SortModel>          filterDateList;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreateView");
        m_this = this;
        setContentView(R.layout.fragment_suppycompanylist);
        EditText m_searchText = (EditText) findViewById(R.id.search_text);
        m_searchText.setVisibility(View.GONE);
        mDataList = new ArrayList<CompanyItemInfo>();
        TextView common_navi_title = (TextView)findViewById(R.id.common_navi_title);
        common_navi_title.setText("供应商管理");

//
//        characterParser = CharacterParser.getInstance();
//        pinyinComparator = new PinyinComparator();
//        slideBar = (SideBar)findViewById(R.id.sidrbar);
        dialog = (TextView)findViewById(R.id.dialog);
//        slideBar.setTextView(dialog);

//        slideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {
//
//            @Override
//            public void onTouchingLetterChanged(String s) {
//                int position = adapter.getPositionForSection(s.charAt(0));
//                if(position != -1){
//                    sortListView.setSelection(position);
//                }
//            }
//        });


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
                Intent intent = new Intent(m_this,AddSuppyCompanyActivity.class);
                intent.putExtra("type",1);
                startActivity(intent);
            }
        });

        if(MainApplication.getInstance().getUserType(SuppyCompanyListActivity.this) == 2)
        {

            addBtn.setVisibility(View.VISIBLE);
        }else{
            if("1".equalsIgnoreCase(MainApplication.getInstance().getSPValue(SuppyCompanyListActivity.this,"iscanaddnewcontact")))
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

                final CompanyItemInfo info = (CompanyItemInfo)mDataList.get(position);

                String[] arr = {"查看详情","删除"};
                View outerView = LayoutInflater.from(SuppyCompanyListActivity.this).inflate(R.layout.wheel_view, null);
                final WheelView wv = (WheelView) outerView.findViewById(R.id.wheel_view_wv);
                wv.setItems(Arrays.asList(arr));
                wv.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
                    @Override
                    public void onSelected(int selectedIndex, String item) {

                    }
                });

                new AlertDialog.Builder(SuppyCompanyListActivity.this)
                        .setTitle("请选择操作")
                        .setView(outerView)
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {


                                if(wv.getSeletedIndex() == 0){
//
                                    Intent intent = new Intent(m_this,AddSuppyCompanyActivity.class);
                                    intent.putExtra("type",2);
                                    intent.putExtra("data",info);
                                    startActivity(intent);
                                }else if(wv.getSeletedIndex() == 1){
                                    delInfo(info.CompanyId);
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


        getAllCompanylist();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 1){
        }
    }





    private  void  getAllCompanylist(){
            Map queryAllMap = new HashMap();
            queryAllMap.put("owner",   LoginUserUtil.getTel(SuppyCompanyListActivity.this));

            HttpManager.getInstance(SuppyCompanyListActivity.this).queryAllContacts("/warehousesupplier/query", queryAllMap, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {


                    if (jsonObject.optInt("code") == 1) {

                        JSONArray arr = jsonObject.optJSONArray("ret");
                        if (arr.length() > 0) {

                            mDataList.clear();
                            for (int i = 0; i < arr.length(); i++) {
                                JSONObject obj = arr.optJSONObject(i);
                                ArrayList<ADTReapirItemInfo> arrItems = new ArrayList();
                                CompanyItemInfo info = CompanyItemInfo.fromWithJsonObj(obj);
                                mDataList.add(info);
                            }

                            final  ArrayList<CompanyItemInfo> list = mDataList;
                            Handler mainHandler = new Handler(Looper.getMainLooper());
                            mainHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    CompanyListAdapter m_adapter = new CompanyListAdapter(m_this, list);
                                    sortListView.setAdapter(m_adapter);
                                    m_adapter.notifyDataSetChanged();
//                                    materialRefreshLayout.postDelayed(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            materialRefreshLayout.finishRefresh();
//                                            materialRefreshLayout.finishRefreshLoadMore();
//                                        }
//                                    }, 100);
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

        HttpManager.getInstance(SuppyCompanyListActivity.this).queryAllContacts("/warehousesupplier/del", queryAllMap, new Response.Listener<JSONObject>() {
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
