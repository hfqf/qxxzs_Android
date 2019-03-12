package com.points.autorepar.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.points.autorepar.MainApplication;
import com.points.autorepar.R;
import com.points.autorepar.activity.Store.InAndOutRecordsActivity;
import com.points.autorepar.activity.Store.InAndOutServiceManageActivity;
import com.points.autorepar.activity.Store.PurchaseRejectListActivity;
import com.points.autorepar.activity.Store.ServiceManageActivity;
import com.points.autorepar.activity.Store.SuppyCompanyListActivity;
import com.points.autorepar.activity.Store.TotalPurchaseListActivity;
import com.points.autorepar.activity.Store.WarnPurchaseListActivity;
import com.points.autorepar.http.HttpManager;
import com.points.autorepar.utils.LoginUserUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class StoreActivity extends Activity  {


    private  int type = 1;
    private int i_getWarnCount = 0;
    private Integer[] mFunctionPics2 = {R.drawable.warehouse_zl,R.drawable.warehouse_cg,
            R.drawable.warehouse_crk,R.drawable.warehouse_th,
            R.drawable.warehouse_yj};
    private Integer[] mFunctionPics1 = {R.drawable.warehouse_gys,R.drawable.warehouse_spgl};
    //存放各功能的名称
    private String[] mFunctionName2 = {"库存总览",
            "采购/入库","出入库记录","库存退货","库存预警"};
    private String[] mFunctionName1 = {"供应商管理",
            "配件分类设置"};
    private GridView girdview1;
    private ImageAdapter1 adapter1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        TextView m_title = (TextView)findViewById(R.id.common_navi_title);
        m_title.setText("统计");
        type = getIntent().getIntExtra("type",1);
        Window window = getWindow();
//设置透明状态栏,这样才能让 ContentView 向上
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

//需要设置这个 flag 才能调用 setStatusBarColor 来设置状态栏颜色
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        Button common_navi_back = (Button)findViewById(R.id.common_navi_back);
        common_navi_back.setVisibility(View.GONE);
        Button common_navi_add = (Button)findViewById(R.id.common_navi_add);
        common_navi_add.setVisibility(View.GONE);
        MainApplication mainApplication = (MainApplication) getApplication();
        GridView girdview = (GridView) findViewById(R.id.gridview);
        girdview.setAdapter(new ImageAdapter(this));
        girdview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
//                Toast.makeText(StoreActivity.this, "图片ID："+mFunctionPics[position],Toast.LENGTH_LONG).show();
                String weburl = "";
                String title = "统计";
                switch(position)
                {
                    case 0:
                        Intent intent = new Intent(StoreActivity.this, SuppyCompanyListActivity.class);
                        startActivity(intent);

                        break;
                    case 1:
                        Intent intent1 = new Intent(StoreActivity.this, ServiceManageActivity.class);
                        startActivity(intent1);

                        break;
                    case 2:
                        if(type == 1) {
                            weburl = "https://www.autorepairehelper.cn/repairstatistics/saledgoods";
                        }else{
                            weburl = "https://www.autorepairehelper.cn/repairstatistics/own";
                        }

                        break;
                }

//                   WebActivity.actionStart(StoreActivity.this, weburl,title);

            }
        });

         girdview1 = (GridView) findViewById(R.id.gridview1);
        adapter1 = new ImageAdapter1(this);
        girdview1.setAdapter(adapter1);
        girdview1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
//                Toast.makeText(StoreActivity.this, "图片ID："+mFunctionPics[position],Toast.LENGTH_LONG).show();
                String weburl = "";
                String title = "统计";
                switch(position)
                {
                    case 0:
                        Intent intent = new Intent(StoreActivity.this, TotalPurchaseListActivity.class);

                        startActivity(intent);

                        break;
                    case 1:
                        Intent intent1 = new Intent(StoreActivity.this, InAndOutServiceManageActivity.class);

                        startActivity(intent1);

                        break;
                    case 2:
                        Intent intent2 = new Intent(StoreActivity.this, InAndOutRecordsActivity.class);

                        startActivity(intent2);

                        break;
                    case 3:
                        Intent intent3 = new Intent(StoreActivity.this, PurchaseRejectListActivity.class);
                        startActivity(intent3);
                        break;
                    case 4:
                        Intent intent4 = new Intent(StoreActivity.this, WarnPurchaseListActivity.class);

                        startActivity(intent4);
                        break;

                }

            }
        });


    }

    public void onResume() {
        super.onResume();
        queryWarn();

    }


    public class ImageAdapter extends BaseAdapter {

        private Context mContext;
        private LayoutInflater inflater;
        private class GirdTemp{
            ImageView phone_function_pic;
            TextView phone_function_name;
        }
        public ImageAdapter(Context c){
            mContext = c;
            inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        @Override
        public int getCount() {

                return mFunctionPics1.length;

        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            GirdTemp temp;
//            if(convertView == null){
                convertView = inflater.inflate(R.layout.activity_report_itme, null);
                temp = new GirdTemp();
                temp.phone_function_pic = (ImageView) convertView.findViewById(R.id.function_view);
                temp.phone_function_name = (TextView) convertView.findViewById(R.id.function_name);

                temp.phone_function_pic.setImageResource(mFunctionPics1[position]);
                temp.phone_function_name.setText(mFunctionName1[position]);

            return convertView;
        }

    }


    public class ImageAdapter1 extends BaseAdapter {

        private Context mContext;
        private LayoutInflater inflater;
        private class GirdTemp{
            ImageView phone_function_pic;
            TextView phone_function_name;
            TextView phone_function_pic_unread;
        }
        public ImageAdapter1(Context c){
            mContext = c;
            inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        @Override
        public int getCount() {

                return mFunctionPics2.length;

        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            GirdTemp temp;
//            if(convertView == null){
            convertView = inflater.inflate(R.layout.activity_report_itme, null);
            temp = new GirdTemp();
            temp.phone_function_pic = (ImageView) convertView.findViewById(R.id.function_view);
            temp.phone_function_name = (TextView) convertView.findViewById(R.id.function_name);
            temp.phone_function_pic_unread = (TextView) convertView.findViewById(R.id.unreadImage);
//                convertView.setTag(holder);
//            }else{
//                temp = (GirdHolder) convertView.getTag();
//            }

                temp.phone_function_pic.setImageResource(mFunctionPics2[position]);
                temp.phone_function_name.setText(mFunctionName2[position]);
            if("库存预警".equalsIgnoreCase((String)mFunctionName2[position]))
            {
                if(i_getWarnCount >0)
                {
                    temp.phone_function_pic_unread.setVisibility(View.VISIBLE);
                    temp.phone_function_pic_unread.setText(i_getWarnCount+"");
                }
            }
            return convertView;
        }

    }

    //到期提醒未读数
    private  void queryWarn(){
        Map queryAllMap = new HashMap();
        queryAllMap.put("owner",   LoginUserUtil.getTel(StoreActivity.this));

        HttpManager.getInstance(StoreActivity.this).queryAllContacts("/warehousegoods/querywaring", queryAllMap, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {


                if (jsonObject.optInt("code") == 1) {

                    JSONArray arr = jsonObject.optJSONArray("ret");
                    if (arr.length() > 0) {
                        i_getWarnCount = arr.length();

                    }else{
                        i_getWarnCount = 0;
                    }

                    adapter1.notifyDataSetChanged();


                }



            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
//                Log.e(TAG, "contact/queryAll:onErrorResponse " + volleyError);

            }
        });

    }



}
