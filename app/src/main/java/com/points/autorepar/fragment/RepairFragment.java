package com.points.autorepar.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.points.autorepar.R;
import com.points.autorepar.activity.BaseActivity;
import com.points.autorepar.activity.contact.SelectContactActivity;
import com.points.autorepar.activity.repair.RepairInfoEditActivity;
import com.points.autorepar.activity.workroom.WorkRoomEditActivity;
import com.points.autorepar.adapter.RepairHistoryAdapter;
import com.points.autorepar.adapter.RepairHistoryTipedAdapter;
import com.points.autorepar.http.HttpManager;
import com.points.autorepar.lib.cjj.MaterialRefreshLayout;
import com.points.autorepar.lib.cjj.MaterialRefreshListener;
import com.points.autorepar.sql.DBService;
import com.points.autorepar.bean.ADTReapirItemInfo;
import com.points.autorepar.bean.Contact;
import com.points.autorepar.bean.RepairHistory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.points.autorepar.lib.wheelview.*;
import com.points.autorepar.utils.LoginUserUtil;

import org.json.JSONArray;
import org.json.JSONObject;


public class RepairFragment extends Fragment {

    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(Uri uri);
        public void onStartSelectContact();
    }

    private  final String                       TAG = "RepairFragment";
    //下拉上拉刷新控件
    private MaterialRefreshLayout                materialRefreshLayout;

    //回调
    private OnFragmentInteractionListener        mListener;
    private ListView                             m_listView;
    private RepairHistoryTipedAdapter                 m_adapter;
    private Button                               m_addNewRepairHistoryBtn;
    private TextView                             m_title;
    AsycnCompletedReceiver                       asycnCompletedReceiver;
    private  ArrayList<RepairHistory>            m_arrData;

    public RepairFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_repair, container, false);

        m_listView = (ListView)view.findViewById(R.id.id_repair_tiped_list);
        m_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                RepairHistory rep =  m_arrData.get(position);
                rep.m_isAddNewRep = 0;
                Intent intent = new Intent(getActivity(),WorkRoomEditActivity.class);
                intent.putExtra(String.valueOf(R.string.key_repair_edit_para), rep);
                startActivityForResult(intent, 1);
            }
        });

        m_listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                RepairHistory rep = m_arrData.get(position);
                deleteRepairHistory(rep);
                return false;
            }
        });

        m_title = (TextView)view.findViewById(R.id.common_navi_title);
        m_title.setText("到期提醒");
        m_addNewRepairHistoryBtn = (Button)view.findViewById(R.id.common_navi_add);
        m_addNewRepairHistoryBtn.setVisibility(View.INVISIBLE);
        m_addNewRepairHistoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "m_addNewRepairHistoryBtn");
                final  List arr = DBService.queryAllContactName();
                if(arr == null){
                    Toast.makeText(getActivity(), "暂无客户,快去添加吧", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent(getActivity(),SelectContactActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("flag","1");
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });






        materialRefreshLayout = (MaterialRefreshLayout) view.findViewById(R.id.id_repair_navi_refresh_contact);
        materialRefreshLayout.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(final MaterialRefreshLayout materialRefreshLayout) {

                reloadDataAndRefreshView();

            }

            @Override
            public void onfinish() {
                Log.e(TAG, "onfinish");
            }


            @Override
            public void onRefreshLoadMore(final MaterialRefreshLayout materialRefreshLayout) {

                reloadDataAndRefreshView();

            }
        });


        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
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
        Log.e(TAG, "onStart");

        //添加注册
        IntentFilter intentFilter1 = new IntentFilter();
        intentFilter1.addAction("com.hfqf.asyncedreceiver");
        asycnCompletedReceiver = new AsycnCompletedReceiver();
        getActivity().registerReceiver(asycnCompletedReceiver, intentFilter1);
        reloadDataAndRefreshView();
    }

    @Override
    public  void  onStop() {
        super.onStop();
        getActivity().unregisterReceiver(asycnCompletedReceiver);
        Log.e(TAG, "onStop");

    }

    //private function

    /**
     * 删除某个维修记录
     * @param rep
     */
    private void deleteOneRepairServerAndLocalDB(RepairHistory rep){
        final RepairHistory _rep = rep;
        Map map = new HashMap();
        map.put("owner", LoginUserUtil.getTel(getActivity()));
        map.put("id", rep.idfromnode);
        ((BaseActivity)getActivity()).showWaitView();
        HttpManager.getInstance(getActivity()).deleteOneContact("/repair/del", map, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                ((BaseActivity)getActivity()).stopWaitingView();
                if(jsonObject.optInt("code") == 1){

                    reloadDataAndRefreshView();

                }
                else {
                    Toast.makeText(getActivity(),"删除失败",Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                ((BaseActivity)getActivity()).stopWaitingView();
                Toast.makeText(getActivity(),"删除失败",Toast.LENGTH_SHORT).show();
            }
        });

    }

    /*
     * 下拉或上拉相关函数
     */
    public   void  reloadDataAndRefreshView(){

        Map map = new HashMap();
        map.put("owner", LoginUserUtil.getTel(getActivity()));
        ((BaseActivity)getActivity()).showWaitView();
        HttpManager.getInstance(getActivity())
                   .queryAllTipedRepair("/repair/queryAllTiped1", map, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                ((BaseActivity)getActivity()).stopWaitingView();
                Log.e(TAG,"/repair/queryAllTiped"+jsonObject.toString());
                if(jsonObject.optInt("code") == 1){

                    m_arrData = getArrayRepair(jsonObject);
                    Handler mainHandler = new Handler(Looper.getMainLooper());
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            m_adapter = new RepairHistoryTipedAdapter(getActivity(), m_arrData);
                            m_listView.setAdapter(m_adapter);
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
                ((BaseActivity)getActivity()).stopWaitingView();
            }
        });

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


    private void  deleteRepairHistory(final RepairHistory rep) {
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(getActivity());
        normalDialog.setTitle("删除维修记录");
        normalDialog.setMessage("确认删除?");
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteOneRepairServerAndLocalDB(rep);
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



    //同步完数据必须强制刷新页面
    class AsycnCompletedReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("com.hfqf.asyncedreceiver")) {
                Bundle bundle = intent.getExtras();//获取数据
                if(bundle.get("type").equals("repair")){

                    Log.e(TAG, "AsycnCompletedReceiver-->repair");

                    reloadDataAndRefreshView();
                }
            }
        }
    }

}
