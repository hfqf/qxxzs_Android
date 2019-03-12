package com.points.autorepar.activity.contact;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
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

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.points.autorepar.MainApplication;
import com.points.autorepar.R;
import com.points.autorepar.activity.BaseActivity;
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
import com.points.autorepar.sql.DBService;
import com.points.autorepar.utils.JSONOejectUtil;
import com.points.autorepar.utils.LoginUserUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContactListActivity extends BaseActivity {
    
    //回调接口
    public interface OnContactFragmentListener {
        //TODO 可自定义回调函数
        public void onContactFramentReloadData();

        public void onSelectedContact(Contact contact);
    }


    private ContactListActivity m_this;
    private static final String TAG = "InAndOutRecordsActivity";
    //下拉上拉刷新控件
    private MaterialRefreshLayout materialRefreshLayout;

    private ListView sortListView;
    private SideBar slideBar;
    private TextView dialog;
    private SortAdapter adapter;
    private ClearEditText mClearEditText;
    private CharacterParser characterParser;
    private List<SortModel> SourceDateList;
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
        setContentView(R.layout.fragment_contact);
        characterParser = CharacterParser.getInstance();
        pinyinComparator = new PinyinComparator();
        slideBar = (SideBar)findViewById(R.id.sidrbar);
        dialog = (TextView)findViewById(R.id.dialog);
        slideBar.setTextView(dialog);

        slideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {

            @Override
            public void onTouchingLetterChanged(String s) {
                int position = adapter.getPositionForSection(s.charAt(0));
                if(position != -1){
                    sortListView.setSelection(position);
                }
            }
        });


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
                Intent intent = new Intent(m_this,ContactAddNewActivity.class);
                startActivity(intent);
            }
        });

        if(MainApplication.getInstance().getUserType(ContactListActivity.this) == 2
                )
        {

            addBtn.setVisibility(View.VISIBLE);
        }else{
            if("1".equalsIgnoreCase(MainApplication.getInstance().getSPValue(ContactListActivity.this,"iscanaddnewcontact")))
            {
                addBtn.setVisibility(View.VISIBLE);
            }else {

                addBtn.setVisibility(View.INVISIBLE);
            }
        }

        if(m_isSelectContact){
            showQueryViewStyles();
        }

        sortListView = (ListView) findViewById(R.id.id_contact_list);
        sortListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SortModel data = null;
                if(mClearEditText.getText().toString().length() == 0){
                    data = (SortModel) SourceDateList.get(position);
                }else {
                    data = (SortModel) filterDateList.get(position);
                }
                if(m_isSelectContact){
                    Intent intent = new Intent();
                    intent.putExtra("contact",data.contact);
                    startActivityForResult(intent, 1001);
                }else {
                    mClearEditText.setText("");
                    Intent intent = new  Intent(m_this,ContactInfoEditActivity.class);
                    intent.putExtra(String.valueOf(R.string.key_contact_edit_para), data.contact);
                    startActivityForResult(intent,1);
                }
                Log.e(TAG,"onItemClick");
            }
        });


        mClearEditText = (ClearEditText)findViewById(R.id.filter_edit);

        mClearEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterData(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

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

    /**
     * @param filterStr
     */
    private void filterData(String filterStr){
        filterDateList = new ArrayList<SortModel>();

        if(TextUtils.isEmpty(filterStr)){
            filterDateList = SourceDateList;
        }else{
            filterDateList.clear();
            for(SortModel sortModel : SourceDateList){
                Contact carcode = sortModel.contact;
                String name = sortModel.getName();
                if(name.indexOf(filterStr.toString()) != -1||carcode.getTel().indexOf(filterStr.toString()) != -1  ||carcode.getCarCode().indexOf(filterStr.toString()) != -1  || characterParser.getSelling(name).startsWith(filterStr.toString())){
                    filterDateList.add(sortModel);
                }
            }
        }
        if(filterDateList !=null){
            Collections.sort(filterDateList, pinyinComparator);

            adapter.updateListView(filterDateList);
        }

    }



    //private function

    /*
     * 下拉或上拉相关函数
     */
    public   void  reloadDataAndRefreshView() {


        Handler mainHandler = new Handler(Looper.getMainLooper());
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                SourceDateList = filledData();
                Collections.sort(SourceDateList, pinyinComparator);
                adapter = new SortAdapter(m_this, SourceDateList);
                sortListView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                materialRefreshLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        materialRefreshLayout.finishRefresh();
                        materialRefreshLayout.finishRefreshLoadMore();
                        if(mClearEditText.getText().toString().length()==0) {
                            queryContactInfo();

                        }

                    }
                }, 500);

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
            }
        });



    }

    public   void  RefreshView() {


        Handler mainHandler = new Handler(Looper.getMainLooper());
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                SourceDateList = filledData();
                Collections.sort(SourceDateList, pinyinComparator);
                adapter = new SortAdapter(m_this, SourceDateList);
                sortListView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                materialRefreshLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        materialRefreshLayout.finishRefresh();
                        materialRefreshLayout.finishRefreshLoadMore();


                    }
                }, 500);

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
            }
        });



    }




    void queryContactInfo()
    {
        Map map = new HashMap();
        map.put("owner", LoginUserUtil.getTel(ContactListActivity.this));



        String url = "";

        url = "/contact/getTotalCount";

        HttpManager.getInstance(ContactListActivity.this)
                .queryAllTipedRepair(url, map, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {


                        if(jsonObject.optInt("code") == 1){
                            int ret = Integer.parseInt(jsonObject.optString("ret"));
                            ArrayList ary =  DBService.queryAllContact();
                            if(ret != ary.size()) {
                                getAllContactsFromServer();
                            }

                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                    }
                });
    }

    private  void  getAllContactsFromServer(){
        if(DBService.deleteAllContact()) {
            Map queryAllMap = new HashMap();
            queryAllMap.put("owner",   LoginUserUtil.getTel(ContactListActivity.this));

            HttpManager.getInstance(ContactListActivity.this).queryAllContacts("/contact/queryAll", queryAllMap, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {


                    if (jsonObject.optInt("code") == 1) {

                        JSONArray arr = jsonObject.optJSONArray("ret");
                        if (arr.length() > 0) {
                            DBService.deleteAllContact();
                            SQLiteDatabase db  = DBService.getDB();
                            for (int i = 0; i < arr.length(); i++) {
                                Contact conFromServer = new Contact();
                                try {
                                    JSONObject obj = (JSONObject) arr.get(i);
                                    conFromServer.setCarCode(obj.optString("carcode").replace(" ", ""));
                                    conFromServer.setOwner(obj.optString("owner"));
                                    conFromServer.setCarType(obj.optString("cartype"));
                                    conFromServer.setName(obj.optString("name"));
                                    conFromServer.setTel(obj.optString("tel"));
                                    conFromServer.setIdfromnode(obj.optString("_id"));

                                    conFromServer.setInserttime(JSONOejectUtil.optString(obj,"inserttime"));
                                    conFromServer.setIsbindweixin(JSONOejectUtil.optString(obj,"isbindweixin"));
                                    conFromServer.setWeixinopenid(JSONOejectUtil.optString(obj,"weixinopenid"));
                                    conFromServer.setVin(JSONOejectUtil.optString(obj,"vin"));
                                    conFromServer.setCarregistertime(JSONOejectUtil.optString(obj,"carregistertime"));
                                    conFromServer.setHeadurl(JSONOejectUtil.optString(obj,"headurl"));


                                    conFromServer.setSafecompany(JSONOejectUtil.optString(obj,"safecompany"));
                                    conFromServer.setSafenexttime(JSONOejectUtil.optString(obj,"safenexttime"));
                                    conFromServer.setYearchecknexttime(JSONOejectUtil.optString(obj,"yearchecknexttime"));
                                    conFromServer.setTqTime1(JSONOejectUtil.optString(obj,"tqTime1"));
                                    conFromServer.setTqTime2(JSONOejectUtil.optString(obj,"tqTime2"));
                                    conFromServer.setCar_key(JSONOejectUtil.optString(obj,"carId"));

                                    conFromServer.setIsVip(JSONOejectUtil.optString(obj,"isVip"));
                                    conFromServer.setCarId(JSONOejectUtil.optString(obj,"Car_key"));

                                    DBService.addNewContact(conFromServer,db);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
//                                DBService.closeDB(db);

                                RefreshView();

                            }
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
    }
}
