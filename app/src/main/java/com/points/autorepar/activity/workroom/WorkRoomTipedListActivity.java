package com.points.autorepar.activity.workroom;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
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
import com.points.autorepar.adapter.RepairHistoryTipedAdapter;
import com.points.autorepar.bean.ADTReapirItemInfo;
import com.points.autorepar.bean.Contact;
import com.points.autorepar.bean.RepairHistory;
import com.points.autorepar.fragment.RepairFragment;
import com.points.autorepar.http.HttpManager;
import com.points.autorepar.lib.cjj.MaterialRefreshLayout;
import com.points.autorepar.lib.cjj.MaterialRefreshListener;
import com.points.autorepar.sql.DBService;
import com.points.autorepar.utils.LoginUserUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by points on 2018/6/30.
 */


public class WorkRoomTipedListActivity extends BaseActivity {

    private WorkRoomTipedListActivity m_this;


    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(Uri uri);
        public void onStartSelectContact();
    }

    private  final String                       TAG = "RepairFragment";
    //下拉上拉刷新控件
    private MaterialRefreshLayout materialRefreshLayout;

    //回调
    private RepairFragment.OnFragmentInteractionListener mListener;
    private ListView m_listView;
    private RepairHistoryTipedAdapter m_adapter;
    private Button m_addNewRepairHistoryBtn;
    private TextView m_title;
    private  ArrayList<RepairHistory>            m_arrData;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_repair);
        
        m_listView = (ListView)findViewById(R.id.id_repair_tiped_list);
        m_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                RepairHistory rep =  m_arrData.get(position);
                rep.m_isAddNewRep = 0;
                Intent intent = new Intent(m_this,WorkRoomEditActivity.class);
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

        m_title = (TextView)findViewById(R.id.common_navi_title);
        m_title.setText("到期提醒");
        m_addNewRepairHistoryBtn = (Button)findViewById(R.id.common_navi_add);
        m_addNewRepairHistoryBtn.setVisibility(View.INVISIBLE);
        m_addNewRepairHistoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "m_addNewRepairHistoryBtn");
                final List arr = DBService.queryAllContactName();
                if(arr == null){
                    Toast.makeText(m_this, "暂无客户,快去添加吧", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent(m_this,SelectContactActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("flag","1");
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });






        materialRefreshLayout = (MaterialRefreshLayout) findViewById(R.id.id_repair_navi_refresh_contact);
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
    }

    

    //private function

    /**
     * 删除某个维修记录
     * @param rep
     */
    private void deleteOneRepairServerAndLocalDB(RepairHistory rep){
        final RepairHistory _rep = rep;
        Map map = new HashMap();
        map.put("owner", LoginUserUtil.getTel(m_this));
        map.put("id", rep.idfromnode);
        ((BaseActivity)m_this).showWaitView();
        HttpManager.getInstance(m_this).deleteOneContact("/repair/del", map, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                ((BaseActivity)m_this).stopWaitingView();
                if(jsonObject.optInt("code") == 1){

                    reloadDataAndRefreshView();

                }
                else {
                    Toast.makeText(m_this,"删除失败",Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                ((BaseActivity)m_this).stopWaitingView();
                Toast.makeText(m_this,"删除失败",Toast.LENGTH_SHORT).show();
            }
        });

    }

    /*
     * 下拉或上拉相关函数
     */
    public   void  reloadDataAndRefreshView(){

        Map map = new HashMap();
        map.put("owner", LoginUserUtil.getTel(m_this));
        ((BaseActivity)m_this).showWaitView();
        HttpManager.getInstance(m_this)
                .queryAllTipedRepair("/repair/queryAllTiped1", map, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        ((BaseActivity)m_this).stopWaitingView();
                        Log.e(TAG,"/repair/queryAllTiped"+jsonObject.toString());
                        if(jsonObject.optInt("code") == 1){

                            m_arrData = getArrayRepair(jsonObject);
                            Handler mainHandler = new Handler(Looper.getMainLooper());
                            mainHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    m_adapter = new RepairHistoryTipedAdapter(m_this, m_arrData);
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
                        ((BaseActivity)m_this).stopWaitingView();
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


    private void  deleteRepairHistory(final RepairHistory rep) {
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(m_this);
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

    

}
