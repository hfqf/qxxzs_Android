package com.points.autorepar.activity.Store;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
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
import com.points.autorepar.bean.Contact;
import com.points.autorepar.bean.InOutInfo;
import com.points.autorepar.http.HttpManager;
import com.points.autorepar.lib.cjj.MaterialRefreshLayout;
import com.points.autorepar.lib.sortlistview.ClearEditText;
import com.points.autorepar.utils.LoginUserUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class InAndOutRecordsActivity extends BaseActivity {

    public InAndOutRecordsActivity() {
    }

    //回调接口
    public interface OnContactFragmentListener {
        //TODO 可自定义回调函数
        public void onContactFramentReloadData();

        public void onSelectedContact(Contact contact);
    }


    private InAndOutRecordsActivity m_this;
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
    private ArrayList<InOutInfo> mDataList;


    private Button    m_btn0,m_btn1,m_btn2,m_btn3,m_btn4;
    private  View     m_indexIcon;
    private  int    m_index;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreateView");
        m_this = this;
        m_index = 0;
        setContentView(R.layout.fragment_inandout);

        mDataList = new ArrayList<InOutInfo>();
        TextView common_navi_title = (TextView)findViewById(R.id.common_navi_title);
        common_navi_title.setText("出入库记录");

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
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("Contact", "onActivityCreated");
                Intent intent = new Intent(m_this,AddSuppyCompanyActivity.class);
                intent.putExtra("type",1);
                startActivity(intent);
            }
        });

        if(MainApplication.getInstance().getUserType(InAndOutRecordsActivity.this) == 2)
        {

            addBtn.setVisibility(View.VISIBLE);
        }else{
            if("1".equalsIgnoreCase(MainApplication.getInstance().getSPValue(InAndOutRecordsActivity.this,"iscanaddnewcontact")))
            {
                addBtn.setVisibility(View.VISIBLE);
            }else {

                addBtn.setVisibility(View.INVISIBLE);
            }
        }

        m_indexIcon =  findViewById(R.id.workroom_index_icon);
        rereshIndexLocation();

        m_btn0 =(Button) findViewById(R.id.workroom_fragment_btn0);
        m_btn0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m_index = 0;
                rereshIndexLocation();
                getAllCompanylist();
            }
        });
        m_btn1 =(Button) findViewById(R.id.workroom_fragment_btn1);
        m_btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m_index = 1;
                rereshIndexLocation();
                getAllCompanylist();
            }
        });
        m_btn2 =(Button)findViewById(R.id.workroom_fragment_btn2);
        m_btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m_index = 2;
                rereshIndexLocation();
                getAllCompanylist();
            }
        });
        m_btn3 =(Button)findViewById(R.id.workroom_fragment_btn3);
        m_btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m_index = 3;
                rereshIndexLocation();
                getAllCompanylist();

            }
        });

        m_btn4 =(Button)findViewById(R.id.workroom_fragment_btn4);
        m_btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m_index = 4;
                rereshIndexLocation();
                getAllCompanylist();
            }
        });



        addBtn.setVisibility(View.INVISIBLE);

        sortListView = (ListView) findViewById(R.id.id_contact_list);

        getAllCompanylist();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 1){
        }
    }



    private void rereshIndexLocation( ){


        Display display = m_this.getWindow().getWindowManager().getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        display.getMetrics(dm);
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) m_indexIcon.getLayoutParams();
        lp.leftMargin = m_index * (dm.widthPixels /5);
        lp.width = (dm.widthPixels /5);;//这里动态设置光标的长度，以便适配不同的分辨率
        m_indexIcon.setLayoutParams(lp);

    }


    private  void  getAllCompanylist(){
        showWaitView();
        Map queryAllMap = new HashMap();
        queryAllMap.put("owner",   LoginUserUtil.getTel(InAndOutRecordsActivity.this));
        queryAllMap.put("key", m_index==0?"":String.valueOf(m_index));

        HttpManager.getInstance(InAndOutRecordsActivity.this).queryAllContacts("/warehousegoodsinoutrecord/query2", queryAllMap, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {

                stopWaitingView();
                if (jsonObject.optInt("code") == 1) {

                    JSONArray arr = jsonObject.optJSONArray("ret");
                    if (arr.length() > 0) {

                        mDataList.clear();
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject obj = arr.optJSONObject(i);
                            ArrayList<InOutInfo> arrItems = new ArrayList();
                            InOutInfo info = InOutInfo.fromWithJsonObj(obj);
                            mDataList.add(info);
                        }

                        final  ArrayList<InOutInfo> list = mDataList;
                        Handler mainHandler = new Handler(Looper.getMainLooper());
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                InOutAdapter m_adapter = new InOutAdapter(m_this, list);
                                sortListView.setAdapter(m_adapter);
                                sortListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                        final int index = i;
                                        final AlertDialog.Builder normalDialog =
                                                new AlertDialog.Builder(m_this);
                                        normalDialog.setTitle("删除此操作,不可恢复!");
                                        normalDialog.setMessage("确认删除?");
                                        normalDialog.setPositiveButton("确定",
                                                new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        InOutInfo rep = mDataList.get(index);
                                                        delInfo(rep.id);
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
                                    }
                                });
                                m_adapter.notifyDataSetChanged();
                            }
                        });
                    }
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e(TAG, "contact/queryAll:onErrorResponse " + volleyError);
                stopWaitingView();
            }
        });
    }


    public void onResume() {
        super.onResume();
        getAllCompanylist();

    }
    private  void delInfo (String infoID){
        Map queryAllMap = new HashMap();
        queryAllMap.put("id",  infoID);//warehousegoodsinoutrecord
        HttpManager.getInstance(InAndOutRecordsActivity.this).queryAllContacts("/warehousegoodsinoutrecord/del", queryAllMap, new Response.Listener<JSONObject>() {
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
