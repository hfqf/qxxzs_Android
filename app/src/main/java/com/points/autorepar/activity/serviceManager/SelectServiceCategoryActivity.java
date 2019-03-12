package com.points.autorepar.activity.serviceManager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.points.autorepar.MainApplication;
import com.points.autorepar.R;
import com.points.autorepar.activity.BaseActivity;
import com.points.autorepar.activity.Store.InAndOutServiceManageActivity;
import com.points.autorepar.adapter.SelectServiceHomeExpandableAdapter;
import com.points.autorepar.bean.ADTReapirItemInfo;
import com.points.autorepar.bean.ADTServiceInfo;
import com.points.autorepar.bean.ADTServiceItemInfo;
import com.points.autorepar.bean.GoodsItemInfo;
import com.points.autorepar.bean.RepairHistory;
import com.points.autorepar.bean.WorkRoomEvent;
import com.points.autorepar.http.HttpManager;
import com.points.autorepar.lib.wheelview.WheelView;
import com.points.autorepar.utils.LoginUserUtil;
import com.wdullaer.materialdatetimepicker.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;

public class SelectServiceCategoryActivity extends BaseActivity {

    private  final  String  TAG = "ServiceHomeActivity";
    private SelectServiceCategoryActivity m_this;
    ListView          m_expandableListView;

    private Button mBackBtn;
    private Button mAddBtn;
    private TextView mTitle;
    private String repid;
    private String contactid;
    private ItemAdapter adapter;
    ArrayList<ADTServiceInfo>  m_arr;
    ArrayList<ADTReapirItemInfo>  m_ItemInfo;
    ArrayList<GoodsItemInfo> arrRep =  new ArrayList();
    ArrayList<GoodsItemInfo> m_arrAll =  new ArrayList(); ;
    private RepairHistory m_currentData;
    private EditText m_searchText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getTotal();
        setContentView(R.layout.activity_servicecate_home);


        adapter = new ItemAdapter(SelectServiceCategoryActivity.this
                , new  ArrayList<GoodsItemInfo>());

        mTitle      = (TextView)findViewById(R.id.common_navi_title);
        mTitle.setText("添加配件");
        m_currentData = getIntent().getParcelableExtra("data");

//        m_ItemInfo = m_currentData.arrRepairItems;
        m_ItemInfo = new ArrayList<ADTReapirItemInfo>();
        repid  = getIntent().getStringExtra("repid");
        contactid = getIntent().getStringExtra("contactid");
        mBackBtn    = (Button)findViewById(R.id.common_navi_back);
        mBackBtn.setVisibility(View.VISIBLE);
        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Button common_navi_add1 = (Button)findViewById(R.id.common_navi_add1);
        common_navi_add1.setText("入库");
        common_navi_add1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(m_this, AddOrEditServiceCategoryActivity.class);
//                intent.putExtra("type","0");//新增
//                startActivityForResult(intent,1);
//                deleteRepid();


                Intent intent = new Intent(m_this, InAndOutServiceManageActivity.class);
                startActivity(intent);
            }
        });


        mAddBtn = (Button)findViewById(R.id.common_navi_add);
        mAddBtn.setText("确定");
        mAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(m_this, AddOrEditServiceCategoryActivity.class);
//                intent.putExtra("type","0");//新增
//                startActivityForResult(intent,1);
                deleteRepid();


            }
        });


        m_searchText = (EditText) findViewById(R.id.search_text);
        m_searchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {

                if (i == EditorInfo.IME_ACTION_SEARCH) {
                    reloadData();
                    return true;
                }
                return false;
            }
        });

        m_this = this;
        m_expandableListView = (ListView)findViewById(R.id.expand_list);
        m_expandableListView.setAdapter(adapter);
//        m_expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
//            public View getGroupView(intgroupPosition, booleanisExpanded, View convertView, ViewGroup parent)
//
//            @Override
//                                        int groupPosition, long id) {
//                // TODO Auto-generated method stub
//                return false;
//            }
//        });


        m_expandableListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4){


            }
        });




        reloadData();

    }

    private  void delService(ADTServiceInfo _info){

        Map map = new HashMap();
        map.put("id", _info.id);
        HttpManager.getInstance(this).getAllServiceTypePreviewList("/servicetoptype/del", map, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                if (jsonObject.optInt("code") == 1) {
                   reloadData();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {



            }
        });
    }

    private GoodsItemInfo selectedGood(GoodsItemInfo newGood){
        for(int i=0;i<m_arrAll.size();i++){
            GoodsItemInfo good= m_arrAll.get(i);
            if(good.id.equals(newGood.id)){
                return good;
            }
        }
        return  newGood;
    }


    private void reloadData(){
        Map map = new HashMap();
        map.put("owner", LoginUserUtil.getTel(this));
        map.put("key",   m_searchText.getText().toString());

        for(int i=0;i<m_arrAll.size();i++){
            GoodsItemInfo first= m_arrAll.get(i);
            for(int j=0;i<arrRep.size();j++){
                GoodsItemInfo second= m_arrAll.get(j);
                if(first.id.equals(second.id)){
                    first.selectnum = second.selectnum;
                    break;
                }
            }
        }

        arrRep =  new ArrayList();
        HttpManager.getInstance(this).getAllServiceTypePreviewList("/warehousegoods/query3", map, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                if (jsonObject.optInt("code") == 1) {
                    JSONArray arr = jsonObject.optJSONArray("ret");
                    if (arr.length() > 0) {
                        for (int i = 0; i < arr.length(); i++) {
                            GoodsItemInfo info = GoodsItemInfo.fromWithJsonObj(arr.optJSONObject(i));
                            GoodsItemInfo good = selectedGood(info);
                            arrRep.add(good);
                        }
                        adapter.addData(arrRep,false);
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        });
    }

    private void getTotal(){
        Map map = new HashMap();
        map.put("owner", LoginUserUtil.getTel(this));
        map.put("key",   "");
        HttpManager.getInstance(this).getAllServiceTypePreviewList("/warehousegoods/query3", map, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                if (jsonObject.optInt("code") == 1) {
                    JSONArray arr = jsonObject.optJSONArray("ret");
                    if (arr.length() > 0) {
                        for (int i = 0; i < arr.length(); i++) {
                            GoodsItemInfo info = GoodsItemInfo.fromWithJsonObj(arr.optJSONObject(i));
                            m_arrAll.add(info);
                        }
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1){
            reloadData();
        }else if(requestCode == 2){
            reloadData();
        }
    }


    private ArrayList<ADTServiceInfo> getArrayService(JSONObject ret){
        JSONArray arr = ret.optJSONArray("ret");
        ArrayList<ADTServiceInfo> arrRep =  new ArrayList();
        if (arr.length() > 0) {
            for (int i = 0; i < arr.length(); i++) {
                ADTServiceInfo serviceInfo = new ADTServiceInfo();
                JSONObject obj = arr.optJSONObject(i);
                serviceInfo.id =obj.optString("_id").replace(" ", "");
                serviceInfo.name =obj.optString("name").replace(" ", "");

                JSONArray items = obj.optJSONArray("subtype");
                ArrayList<ADTServiceItemInfo> arrItems = new ArrayList();
                if(items != null){
                    for(int j=0;j<items.length();j++){
                        JSONObject itemObj = items.optJSONObject(j);
                        ADTServiceItemInfo item = new ADTServiceItemInfo();
                        item.id =  itemObj.optString("_id");
                        item.name =  itemObj.optString("name");
                        item.price =  itemObj.optString("price");
                        item.topTypeId =  itemObj.optString("toptypeid");
                        item.workHourPay =  itemObj.optString("workhourpay");
                        item.num =  "0";
                        arrItems.add(item);
                    }
                }
                serviceInfo.arrSubTypes = arrItems;

                arrRep.add(serviceInfo);
            }
        }
        return arrRep;
    }

    private  void addRepairItems(){

//        List<Map<String,String>> list = new ArrayList<Map<String,String>>();
        Map map = new HashMap();
        ArrayList<GoodsItemInfo> data = adapter.getData();
        try{
            JSONArray str_dispatchtime = new JSONArray();
            JSONArray str_goods  = new JSONArray();
            JSONArray str_isdirectadd = new JSONArray();
            JSONArray str_repairer = new JSONArray();
            JSONArray str_state = new JSONArray();
            JSONArray str_isneeddispatch = new JSONArray();
            JSONArray str_itemtype = new JSONArray();
            JSONArray str_workhourpay= new JSONArray();
            JSONArray str_repid = new JSONArray();
            JSONArray str_num = new JSONArray();
            JSONArray str_price = new JSONArray();
            JSONArray str_contactid = new JSONArray();
            JSONArray str_service = new JSONArray();
            JSONArray str_type = new JSONArray();
            JSONArray str_name = new JSONArray();


            JSONArray list = new JSONArray();
            JSONObject selectmap = null;
        for(int i=0;i<m_currentData.arrRepairItems.size();i++)
        {
            ADTReapirItemInfo info = m_currentData.arrRepairItems.get(i);
            if("1".equalsIgnoreCase(info.itemtype))
            {
//                ADTReapirItemInfo info = new ADTReapirItemInfo();

                str_dispatchtime.put("");
                str_name.put("");
                str_goods.put("");
                str_isdirectadd.put("0");
                str_repairer.put("");
                str_state.put("0");
                str_isneeddispatch.put("1");
                str_itemtype.put("1");
                str_service.put(info.idfromnode);
                str_repid.put(repid);
                str_workhourpay.put(info.workhourpay);

                str_contactid.put(contactid);
                str_price.put(info.price);
                str_num.put(info.num);
                str_type.put(info.type);
                selectmap = new JSONObject();

                selectmap.put("dispatchtime", str_dispatchtime);
                selectmap.put("goods", str_service);
                selectmap.put("isdirectadd", str_isdirectadd);
                selectmap.put("repairer", str_repairer);
                selectmap.put("state", str_state);
                selectmap.put("isneeddispatch", str_isneeddispatch);
                selectmap.put("itemtype", str_itemtype);

                selectmap.put("workhourpay", str_workhourpay);
                selectmap.put("repid", str_repid);
                selectmap.put("num", str_num);
                selectmap.put("price", str_price);
                selectmap.put("contactid", str_contactid);
                selectmap.put("service", str_service);
                selectmap.put("type", str_type);
                selectmap.put("name", str_type);

                list.put(selectmap);
            }
        }





        for(int j=0;j<arrRep.size();j++) {
            GoodsItemInfo itemInfo = arrRep.get(j);
//            ADTServiceInfo serviceInfo =  m_arr.get(0);
//            ADTServiceItemInfo itemInfo = serviceInfo.arrSubTypes.get(j);
            if (!"0".equalsIgnoreCase(itemInfo.selectnum)) {

                ADTReapirItemInfo info = new ADTReapirItemInfo();

                str_dispatchtime.put("");
                str_name.put("");
                str_goods.put("");
                str_isdirectadd.put("0");
                str_repairer.put("");
                str_state.put("0");
                str_isneeddispatch.put("1");
                str_itemtype.put("0");
                str_workhourpay.put(itemInfo.workhourpay);
                str_service.put(itemInfo.id);
                str_repid.put(repid);

                str_contactid.put(contactid);
                str_price.put(itemInfo.saleprice);
                str_num.put(itemInfo.selectnum);
                str_type.put(itemInfo.name);
            selectmap = new JSONObject();

            selectmap.put("dispatchtime", str_dispatchtime);
            selectmap.put("goods", str_service);
            selectmap.put("isdirectadd", str_isdirectadd);
            selectmap.put("repairer", str_repairer);
            selectmap.put("state", str_state);
            selectmap.put("isneeddispatch", str_isneeddispatch);
            selectmap.put("itemtype", str_itemtype);

            selectmap.put("workhourpay", str_workhourpay);
            selectmap.put("repid", str_repid);
            selectmap.put("num", str_num);
            selectmap.put("price", str_price);
            selectmap.put("contactid", str_contactid);
            selectmap.put("service", str_service);
            selectmap.put("type", str_type);
            selectmap.put("name", str_type);

            list.put(selectmap);
            }

        }



//            String str = list.toString();

            map.put("items", list);

    }catch (Exception e )
    {
        e.printStackTrace();
    }
    String url = "";

    url = "/repairitem/additems2";






        HttpManager.getInstance(SelectServiceCategoryActivity.this)
                .queryAllTipedRepair(url, map, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        Log.e(TAG,"/repairitem/additems2"+jsonObject.toString());

                        if(jsonObject.optInt("code") == 1){

                            ArrayList<ADTReapirItemInfo>  m_ItemInfotmp = new ArrayList<ADTReapirItemInfo>();
                            JSONArray items = jsonObject.optJSONArray("ret");


                            for(int i=0 ;i<m_ItemInfo.size();i++)
                            {
                                ADTReapirItemInfo info = m_ItemInfo.get(i);
                                if("1".equalsIgnoreCase(info.itemtype))
                                {
                                    m_ItemInfotmp.add(info);
                                }


                            }

                            if(items != null) {
                                for (int j = 0; j < items.length(); j++) {
                                    JSONObject itemObj = items.optJSONObject(j);
                                    ADTReapirItemInfo info = ADTReapirItemInfo.fromWithJsonObj(itemObj);
                                    m_ItemInfotmp.add(info);
                                }
                                m_currentData.arrRepairItems = m_ItemInfotmp;
                                EventBus.getDefault().post(
                                        new WorkRoomEvent(m_currentData));
                                finish();
                            }





//                            m_currentData.arrRepairItems = m_ItemInfo;
   //                            EventBus.getDefault().post(
//                                    new WorkRoomEvent(m_currentData));
//                            finish();

                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        int i = 0;
                    }
                });

    }

    private  void deleteRepid(){
        Map map = new HashMap();
        map.put("repid", repid);


        String url = "";

        url = "/repairitem/delOneRepairAll";

        HttpManager.getInstance(SelectServiceCategoryActivity.this)
                .queryAllTipedRepair(url, map, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        Log.e(TAG,"/repair/queryAllSafeTiped"+jsonObject.toString());

                        if(jsonObject.optInt("code") == 1){


                            addRepairItems();



                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                    }
                });

    }

    public class ItemAdapter extends BaseAdapter {
        private Context context;
        private ArrayList<GoodsItemInfo> data;

        public ArrayList<GoodsItemInfo> getData()
        {
            return data;
        }

        public ItemAdapter(Context context, ArrayList<GoodsItemInfo> data) {
            this.context = context;
            this.data = data;
        }

        public void addData(ArrayList<GoodsItemInfo> list, boolean append) {
            if (list != null && list.size() > 0) {
                if (!append)
                    data.clear();
                data.addAll(list);
            }
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {

            return data.size();

        }

        @Override
        public GoodsItemInfo getItem(int arg0) {
            return data.get(arg0);
        }

        @Override
        public long getItemId(int arg0) {
            return arg0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            Holder holder;
            if (convertView == null || convertView.getTag() == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.activity_servicecate_home_item, parent, false);
                holder = new Holder();
                 holder.imageView = (ImageView) convertView.findViewById(R.id.img);
                holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
                holder.tv_price = (EditText) convertView.findViewById(R.id.tv_price);
                holder.tv_havenum = (TextView) convertView.findViewById(R.id.tv_havenum);
                holder.bt_add = (TextView) convertView.findViewById(R.id.bt_add);
                holder.bt_min = (TextView) convertView.findViewById(R.id.bt_min);
                holder.input_val = (TextView) convertView.findViewById(R.id.input_val);
                holder.tv_no = (TextView) convertView.findViewById(R.id.tv_no);



                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }
            BaseActivity activity = (BaseActivity) this.context;
            final Holder _holder =holder;
            GoodsItemInfo info = getItem(position);
            final String url = MainApplication.consts(activity).BOS_SERVER+info.picurl+".png";

//            holder.imageView.setImageResource(R.drawable.appicon);
            activity.imageLoader.get(url, new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {
                    _holder.imageView.setImageBitmap(imageContainer.getBitmap());
                }
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    _holder.imageView.setImageResource(R.drawable.appicon);
                }
            },200,200);

            holder.tv_name.setText(info.name);
            holder.tv_price.setText(info.saleprice);
            holder.tv_price.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    String originText = editable.toString();

                    data.get(position).saleprice = originText;
                    notifyDataSetChanged();
                }
            });

            holder.tv_havenum.setText("x"+info.num);
            holder.tv_no.setText("编码："+info.barcode);
            holder.input_val.setText(info.selectnum);
            holder.bt_add.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    GoodsItemInfo info = getItem(position);
                    int curNum = Integer
                            .parseInt((String) info.selectnum)+1;
                    int iNum=  Integer
                            .parseInt((String) info.num);
                    if(curNum > iNum)
                    {
                        if(iNum>0)
                        {
                            curNum = curNum-1;
                        }else{
                            curNum = 0;
                        }

                    }

                    data.get(position).selectnum = curNum+"";
                    notifyDataSetChanged();

                }
            });

            holder.bt_min.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    GoodsItemInfo info = getItem(position);
                    int curNum = Integer
                            .parseInt((String) info.selectnum)-1;
                    if(curNum <0 )
                    {
                        curNum = 0;
                    }

                    data.get(position).selectnum = curNum+"";
                    notifyDataSetChanged();
//                    ADTServiceInfo serviceInfo =  arrData.get(groupPosition);
//                    ADTServiceItemInfo itemInfo = serviceInfo.arrSubTypes.get(childPosition);
//                    // TODO Auto-generated method stub
//                    int curNum = Integer
//                            .parseInt((String) itemInfo.num)-1;
//                    if(curNum < 0)
//                    {
//                        curNum = 0;
//                    }
//
//                    arrData.get(groupPosition).arrSubTypes.get(childPosition).num = curNum+"";
//                    notifyDataSetChanged();

                }
            });


            return convertView;
        }

        class Holder {
            ImageView imageView;
            TextView tv_name;
            EditText tv_price;
            TextView tv_havenum;
            TextView bt_add;
            TextView bt_min;
            TextView input_val;
            TextView tv_no;
        }
    }
}
