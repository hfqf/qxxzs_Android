package com.points.autorepar.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bigkoo.pickerview.TimePickerView;
import com.points.autorepar.R;
import com.points.autorepar.activity.contact.SelectContactActivity;
import com.points.autorepar.activity.workroom.WorkRoomEditActivity;
import com.points.autorepar.adapter.RepairHistoryAdapter;
import com.points.autorepar.bean.ADTReapirItemInfo;
import com.points.autorepar.bean.Contact;
import com.points.autorepar.bean.RepairHistory;
import com.points.autorepar.http.HttpManager;
import com.points.autorepar.lib.cjj.MaterialRefreshLayout;
import com.points.autorepar.lib.cjj.MaterialRefreshListener;
import com.points.autorepar.activity.BaseActivity;
import com.points.autorepar.lib.sortlistview.ClearEditText;
import com.points.autorepar.lib.wheelview.WheelView;
import com.points.autorepar.sql.DBService;
import com.points.autorepar.utils.DateUtil;
import com.points.autorepar.utils.LoginUserUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

public class WorkRoomFragment extends Fragment {



    public interface OnWorkRoomFragmentInteractionListener {


    }

    private OnWorkRoomFragmentInteractionListener mListener;

    private final  String   TAG = "WorkRoomFragment";
    //下拉上拉刷新控件
    private MaterialRefreshLayout materialRefreshLayout;
    private  Button    addBtn;
    private TextView   title;
    private ListView   m_list;
    private  int       m_page;
    private  boolean   m_isRefresh;


    private Button    m_btn0,m_btn1,m_btn2,m_btn3;
    private  View     m_indexIcon;
    private  int    m_index;
    private  ArrayList<RepairHistory>            m_arrData;
    private  WorkRoomFragment    m_this;
    private  EditText mClearEditText;
    private Contact m_searchContact;

    public WorkRoomFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        m_index = 0;
        m_page = 0;
        m_this = this;
        m_isRefresh = true;

    }

    private void rereshIndexLocation( ){


        Display display = getActivity().getWindow().getWindowManager().getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        display.getMetrics(dm);
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) m_indexIcon.getLayoutParams();
        lp.leftMargin = m_index * (dm.widthPixels /4);
        lp.width = (dm.widthPixels /4);;//这里动态设置光标的长度，以便适配不同的分辨率
        m_indexIcon.setLayoutParams(lp);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_workroom, container, false);

        m_list =  (ListView) view.findViewById(R.id.id_workroomlist);
        m_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                RepairHistory rep =  m_arrData.get(position);
                rep.m_isAddNewRep = 0;
                Intent intent = new Intent(getActivity(),WorkRoomEditActivity.class);
                intent.putExtra(String.valueOf(R.string.key_repair_edit_para), rep);
                startActivityForResult(intent, 1);
            }
        });

        m_indexIcon =  view.findViewById(R.id.workroom_index_icon);

        rereshIndexLocation();


        addBtn = (Button) view.findViewById(R.id.common_navi_add);
        addBtn.setText("开单");
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.e(TAG, "m_addNewRepairHistoryBtn");
                final List arr = DBService.queryAllContactName();
                if(arr == null){
                    Toast.makeText(getActivity(), "暂无客户,快去添加吧", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent(getActivity(),SelectContactActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("flag","2");
                intent.putExtras(bundle);
                startActivity(intent);

            }
        });
        title = (TextView)view.findViewById(R.id.common_navi_title);
        title.setText("工单");
        m_btn0 =(Button) view.findViewById(R.id.workroom_fragment_btn0);
        m_btn0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m_index = 0;
                m_searchContact = null;
                rereshIndexLocation();
                m_isRefresh = true;
                reloadDataAndRefreshView();
            }
        });
        m_btn1 =(Button) view.findViewById(R.id.workroom_fragment_btn1);
        m_btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m_index = 1;
                m_page = 0;
                m_searchContact = null;
                m_isRefresh = true;
                rereshIndexLocation();
                reloadDataAndRefreshView();
            }
        });
        m_btn2 =(Button) view.findViewById(R.id.workroom_fragment_btn2);
        m_btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m_index = 2;
                m_page = 0;
                m_searchContact = null;
                rereshIndexLocation();
                m_isRefresh = true;
                reloadDataAndRefreshView();
            }
        });
        m_btn3 =(Button) view.findViewById(R.id.workroom_fragment_btn3);
        m_btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m_index = 3;
                m_page = 0;
                m_searchContact = null;
                m_isRefresh = true;
                rereshIndexLocation();
                reloadDataAndRefreshView();
            }
        });


        mClearEditText = (EditText) view.findViewById(R.id.workroom_filter_edit);
        mClearEditText.setClickable(true);
        mClearEditText.setFocusableInTouchMode(false);
        mClearEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(),SelectContactActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("flag","0");
                intent.putExtras(bundle);
                startActivityForResult(intent,1);
            }
        });
        return view;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(data == null){

            }else {
                m_isRefresh = true;
                m_page = 0;
                Contact contact = data.getParcelableExtra("contact");
                m_searchContact = contact;
                reloadDataAndRefreshView();
            }

        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.e("Contact", "onActivityCreated1");
        materialRefreshLayout = (MaterialRefreshLayout) getActivity().findViewById(R.id.refresh_workroom);
        materialRefreshLayout.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(final MaterialRefreshLayout materialRefreshLayout) {
                m_searchContact = null;
                m_isRefresh = true;
                m_page = 0;
                reloadDataAndRefreshView();
            }
            @Override
            public void onfinish() {
                Log.e("Contact", "onfinish");
            }

            @Override
            public void onRefreshLoadMore(final MaterialRefreshLayout materialRefreshLayout) {
                m_page++;
                m_isRefresh = false;
                reloadDataAndRefreshView();
            }
        });
        reloadDataAndRefreshView();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnWorkRoomFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public  void  onStart() {
        super.onStart();
        Log.e("Contact", "onStart");
    }

    @Override
    public  void  onStop() {
        super.onStop();
        Log.e("Contact", "onStop");
    }

    @Override
    public  void  onResume(){
        super.onResume();
        Log.e("Contact", "onResume");
        reloadDataAndRefreshView();

    }

    @Override
    public  void  onPause() {
        super.onPause();
        Log.e("Contact", "onPause");
    }

    /*
     * 下拉或上拉相关函数
     */
    public   void  reloadDataAndRefreshView(){

        if(m_searchContact == null){
            mClearEditText.setText("");
        }else {
            mClearEditText.setText(m_searchContact.getCarCode());
        }

       final BaseActivity base =  (BaseActivity) getActivity();
        base.showWaitView();
        Map map = new HashMap();
        map.put("owner", LoginUserUtil.getTel(getActivity()));
        map.put("state", String.valueOf(m_index));
        map.put("page", String.valueOf(m_page));
        map.put("pagesize", m_searchContact == null ?"20" : "1000");
        if(m_searchContact != null){
            map.put("contactid",m_searchContact.getIdfromnode());
            map.put("carcode",m_searchContact.getCarCode());
        }
        HttpManager.getInstance(getActivity())
                .queryAllTipedRepair("/repair/queryAllWithState", map, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        Log.e(TAG,"/repair/queryAllWithState"+jsonObject.toString());

                        m_searchContact = null;
                        base.stopWaitingView();
                        if(jsonObject.optInt("code") == 1){

                                m_arrData =  getArrayRepair(jsonObject);

                            Handler mainHandler = new Handler(Looper.getMainLooper());
                            mainHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    RepairHistoryAdapter   m_adapter = new RepairHistoryAdapter(getActivity(), m_arrData,m_index);
                                    m_list.setAdapter(m_adapter);
                                    if(m_isRefresh){
                                        m_list.setSelection(0);
                                    }else {
                                        m_list.setSelection(m_list.getBottom()-1);
                                    }
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
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        m_searchContact = null;
                        base.stopWaitingView();
                    }
                });

    }

    private ArrayList<RepairHistory> getArrayRepair(JSONObject ret){
        JSONArray arr = ret.optJSONArray("ret");

        ArrayList<RepairHistory> arrRep = m_isRefresh ? new ArrayList() : m_arrData;
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
     * 选择时间
     */
    private void selectDayTime( ) {
        //时间选择器
        TimePickerView pvTime = new TimePickerView.Builder(getActivity(), new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {//选中事件回调

                    String time = DateUtil.timeFrom(date);

            }
        }).build();
        pvTime.setDate(Calendar.getInstance());//注：根据需求来决定是否使用该方法（一般是精确到秒的情况），此项可以在弹出选择器的时候重新设置当前时间，避免在初始化之后由于时间已经设定，导致选中时间与当前时间不匹配的问题。
        pvTime.show();

    }



}
