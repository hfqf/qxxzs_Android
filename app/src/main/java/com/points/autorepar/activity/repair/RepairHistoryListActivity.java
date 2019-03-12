package com.points.autorepar.activity.repair;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.points.autorepar.R;
import com.points.autorepar.activity.workroom.WorkRoomEditActivity;
import com.points.autorepar.MainApplication;
import com.points.autorepar.activity.BaseActivity;
import com.points.autorepar.adapter.RepairHistoryAdapter;
import com.points.autorepar.bean.RepairHistory;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;

/**
 *desc 查询某个客户的所有维修记录
 *class RepairHistoryListActivity.java
 *author Points
 *email hfqf123@126.com
 *created 16/12/6 下午2:32
 */

public class RepairHistoryListActivity extends BaseActivity {

    private ArrayList<RepairHistory> m_currentData;
    private ListView                 m_listView;
    private RepairHistoryAdapter     m_adapter;
    private MainApplication MainApplication;
    private  RepairHistoryListActivity m_this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        m_this = this;
        setContentView(R.layout.activity_repair_history_list);

        Button mAddBtn = (Button)findViewById(R.id.common_navi_add);
        mAddBtn.setVisibility(View.INVISIBLE);
        mAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });


        TextView  mTitle      = (TextView)findViewById(R.id.common_navi_title);
        mTitle.setText("全部记录");

        Button mBackBtn = (Button)findViewById(R.id.common_navi_back);
        mBackBtn.setVisibility(View.VISIBLE);
        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        MainApplication = (MainApplication) getApplication();
        m_currentData = getIntent().getParcelableArrayListExtra(String.valueOf(R.string.key_parcel_allhistory));
        m_listView = (ListView)findViewById(R.id.id_repair_listview);
        m_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                RepairHistory rep = m_currentData.get(position);

                rep.m_isAddNewRep = 0;
                Intent intent = new Intent(m_this,WorkRoomEditActivity.class);
                intent.putExtra(String.valueOf(R.string.key_repair_edit_para), rep);
                startActivityForResult(intent, 1);

            }
        });

        m_adapter = new RepairHistoryAdapter(m_this,m_currentData,0);
        m_listView.setAdapter(m_adapter);
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
