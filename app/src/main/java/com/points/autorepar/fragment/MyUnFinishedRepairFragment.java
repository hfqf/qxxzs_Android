package com.points.autorepar.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.content.*;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.*;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.points.autorepar.MainApplication;
import com.points.autorepar.R;
import com.points.autorepar.activity.EmployeeReportWebviewActivity;
import com.points.autorepar.activity.MainTabbarActivity;
import com.points.autorepar.activity.ReportWebviewActivity;
import com.points.autorepar.activity.WebActivity;
import com.points.autorepar.activity.contact.ContactListActivity;
import com.points.autorepar.activity.contact.EmployeeListActivity;
import com.points.autorepar.activity.contact.SelectContactActivity;
import com.points.autorepar.activity.repair.NoticeActivity;
import com.points.autorepar.activity.serviceManager.ServiceHomeActivity;
import com.points.autorepar.activity.workroom.EmployeeWorkRoomListActivity;
import com.points.autorepar.activity.workroom.WorkRoomListActivity;
import com.points.autorepar.bean.ADTReapirItemInfo;
import com.points.autorepar.bean.Contact;
import com.points.autorepar.bean.RepairHistory;
import com.points.autorepar.common.Consts;
import com.points.autorepar.http.HttpManager;
import com.points.autorepar.sql.DBService;
import com.points.autorepar.utils.JSONOejectUtil;
import com.points.autorepar.utils.LoginUserUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.*;



public class MyUnFinishedRepairFragment extends Fragment {

    private static final String TAG = "HomemenuFragment";
    public MyUnFinishedRepairFragmentInteractionListener listener;
    private GridView m_gridView;
    private ImageView topImageView,topImage_view;
    private SettingFragment.OnFragmentInteractionListener mListener;
    private MainTabbarActivity m_parentActivity;

    private MyUnFinishedRepairFragment.ImageAdapter adapter;
    private int i_getAllRepaiirsWithState2;//工单1 --老板
    private int i_queryDispatchedItems;//工单2 -- 员工
    private int i_queryAllTipedRepair; //到期提醒
    private  int i_queryCustomerOrders;//预约单
    private  int i_getVipUnreadCount; //Vip 会员管理
    private TextView top1,top2,top3,top4,top5;
    private  ArrayList<HashMap<String, Object>> lstImageItem;

    public WebView webView;
    public void onResume() {
        super.onResume();
        queryCustomerOrders();
        getAllRepaiirsWithState2();
        queryAllTipedRepair();
        queryContactInfo();
        getAllContactsFromServer();
//        queryDispatchedItems();

    }



    public interface MyUnFinishedRepairFragmentInteractionListener {
        void onMyUnFinishedRepairFragmentSelectedIndex(int index);
    }
    public void SetDelegate (SettingFragment.OnFragmentInteractionListener delegate){
        mListener = delegate;
        m_parentActivity = (MainTabbarActivity)delegate;
    }


    public static HomemenuFragment newInstance(String param1, String param2) {
        HomemenuFragment fragment = new HomemenuFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public MyUnFinishedRepairFragment() {
        // Required empty public constructor
    }

    public void setListener(MyUnFinishedRepairFragmentInteractionListener lister) {
        this.listener = lister;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG,"onCreateView");
        View view = inflater.inflate(R.layout.fragment_my_unfinished_repair, container, false);
        //汽修小助手门店（会员）
        m_gridView = (GridView)view.findViewById(R.id.homemenu_gridview);

        TextView m_title = (TextView)view.findViewById(R.id.common_navi_title);
        String strTitle = LoginUserUtil.getShopName(getActivity());
//        m_title.setText("汽修小助手门店（会员）");
        if("1".equalsIgnoreCase( MainApplication.getInstance().getIsVip(getActivity())))
        {
            strTitle=  strTitle+"（会员）";
        }
        m_title.setText(strTitle);

        webView = (WebView)view.findViewById(R.id.webview);

//        webView.addJavascriptInterface(this,"android");//添加js监听 这样html就能调用客户端
//        webView.setWebChromeClient(webChromeClient);
//        webView.setWebViewClient(webViewClient);

        WebSettings webSettings=webView.getSettings();
        webSettings.setJavaScriptEnabled(true);//允许使用js

        /**
         * LOAD_CACHE_ONLY: 不使用网络，只读取本地缓存数据
         * LOAD_DEFAULT: （默认）根据cache-control决定是否从网络上取数据。
         * LOAD_NO_CACHE: 不使用缓存，只从网络获取数据.
         * LOAD_CACHE_ELSE_NETWORK，只要本地有，无论是否过期，或者no-cache，都使用缓存中的数据。
         */
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);//不使用缓存，只从网络获取数据.
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        //支持屏幕缩放
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);

        topImageView = (ImageView)view.findViewById(R.id.topImage) ;
        topImage_view =(ImageView)view.findViewById(R.id.topImage_view);
        MainApplication mainApplication = (MainApplication) getActivity().getApplication();
        Button common_navi_back = (Button)view.findViewById(R.id.common_navi_back);
        common_navi_back.setVisibility(View.GONE);
        Button common_navi_add = (Button)view.findViewById(R.id.common_navi_add);
        common_navi_add.setVisibility(View.GONE);
        lstImageItem = new ArrayList<HashMap<String, Object>>();
        for (int i = 0; i < 3; i++) {
            if(i==0){
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("icon", R.drawable.home_my_gd);
                map.put("name", "施工单");
                lstImageItem.add(map);
            }else if(i==1){
               HashMap<String, Object> map = new HashMap<String, Object>();
               map.put("icon", R.drawable.home_gd);
               map.put("name", "已开工单");
               lstImageItem.add(map);
            }
            else if(i==2){
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("icon", R.drawable.home_kh);
                map.put("name", "客户管理");
                lstImageItem.add(map);
            }
        }
        i_getAllRepaiirsWithState2 =0;//工单1 --老板
        i_queryDispatchedItems=0;//工单2 -- 员工
        i_queryAllTipedRepair=0; //到期提醒
        i_queryCustomerOrders=0;//预约单
        i_getVipUnreadCount=0; //Vip 会员管理


        //生成适配器的ImageItem <====> 动态数组的元素，两者一一对应
        SimpleAdapter saImageItems = new SimpleAdapter(getActivity(), //没什么解释
                lstImageItem,//数据来源
                R.layout.activity_homemenu_item,//night_item的XML实现
                //动态数组与ImageItem对应的子项
                new String[]{"icon", "name"},

                //ImageItem的XML文件里面的一个ImageView,两个TextView ID
                new int[]{R.id.ItemImage, R.id.ItemText});

        adapter = new MyUnFinishedRepairFragment.ImageAdapter(getActivity());
        //添加并且显示
        m_gridView.setAdapter(adapter);
        //添加消息处理
        m_gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                Toast.makeText(getActivity(),String.valueOf(i), Toast.LENGTH_SHORT).show();

                if(i==0){
                    Intent intent = new Intent(getActivity(), EmployeeWorkRoomListActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("flag", "2");
                    intent.putExtras(bundle);
                    startActivity(intent);
                }else if(i==1){

                        if("1".equalsIgnoreCase(MainApplication.getInstance().getSPValue(getActivity(),"iscankaidan")))
                        {
                            Intent intent = new Intent(getActivity(), WorkRoomListActivity.class);
                            intent.putExtra("type",1);
                            startActivity(intent);
                        }else {

                            Toast.makeText(getActivity(),"您暂无此功能权限",Toast.LENGTH_LONG).show();
                        }
                }else if(i== 2){
                    Intent intent = new Intent(getActivity(), ContactListActivity.class);
                    startActivityForResult(intent,1001);
                }
                else if(i==3){
                    Intent intent = new Intent(getActivity(), ContactListActivity.class);

                    startActivityForResult(intent,1001);
                }
                else if(i==4){
                    //MyReactActivity
                }
                else if(i==6){
                    Intent intent = new Intent(getActivity(), ServiceHomeActivity.class);
                    startActivity(intent);
                }else if(i==5){
                    Intent intent = new Intent(getActivity(), NoticeActivity.class);
                    startActivity(intent);
                }else if(i==7){
                    getMenuSrc();
                }else if(i==8){

                    Intent intent = new Intent(getActivity(), EmployeeListActivity.class);
                    startActivity(intent);
                }
            }
        });
        getTopImage();
        /**
         * 动态获取权限，Android 6.0 新特性，一些保护权限，除了要在AndroidManifest中声明权限，还要使用如下代码动态获取
         */
        if (Build.VERSION.SDK_INT >= 23) {
            int REQUEST_CODE_CONTACT = 101;
            String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
            //验证是否许可权限
            for (String str : permissions) {
                if (getActivity().checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
                    //申请权限
                    getActivity().requestPermissions(permissions, REQUEST_CODE_CONTACT);
                    break;
                }
            }
        }
        top1 = (TextView) view.findViewById(R.id.top1);
        top2 = (TextView) view.findViewById(R.id.top2);
        top3 = (TextView) view.findViewById(R.id.top3);
        top4 = (TextView) view.findViewById(R.id.top4);
        top5 = (TextView) view.findViewById(R.id.top5);


        top1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setTop(1);

            }
        });
        top2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setTop(2);

            }
        });
        top3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setTop(3);

            }
        });
        top4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTop(4);


            }
        });
        top5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setTop(5);

            }
        });
        setTop(1);

        return view;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {

            Log.d(TAG,"onAttach");

        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

    }


    @Override
    public  void  onStart() {
        super.onStart();
        Log.e(TAG, "onStart");

    }



    private void getTopImage( ){

        Map map = new HashMap();
        map.put("type", "0");
//        showWaitView();
        HttpManager.getInstance(getActivity()).getTopImage("/adv/query", map, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
//                stopWaitingView();
                if(jsonObject.optInt("code") == 1){
                    try {
                        JSONArray jsonArray = jsonObject.optJSONArray("ret");
                        if(jsonArray == null || jsonArray.length() == 0)
                        {
                            topImageView.setVisibility(View.GONE);
                            topImage_view.setVisibility(View.GONE);
                        }else {
                            topImageView.setVisibility(View.VISIBLE);
                            topImage_view.setVisibility(View.VISIBLE);
//                        JSONObject obj = jsonObject.optJSONObject("ret");
                            JSONObject obj = jsonArray.getJSONObject(0);
                            String picurl = obj.optString("picurl");
                            final String title = obj.optString("title");
                            final String url = obj.optString("url");

                            m_parentActivity.imageLoader.get(picurl, new ImageLoader.ImageListener() {
                                @Override
                                public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {
                                    topImageView.setImageBitmap(imageContainer.getBitmap());
                                }

                                @Override
                                public void onErrorResponse(VolleyError volleyError) {
                                    topImageView.setImageResource(R.drawable.appicon);
                                }
                            }, 1000, 1000);

                            topImageView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    WebActivity.actionStart(getActivity(), url, title);
//                                m_parentActivity.startChangeHeadImage();
                                }
                            });
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                else {
                    Toast.makeText(getActivity(),"删除失败",Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
//                stopWaitingView();
//                Toast.makeText(getActivity(),"删除失败",Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void getMenuSrc(){

        Map map = new HashMap();
        map.put("type", "0");
        map.put("key","");
        map.put("owner", LoginUserUtil.getTel(getActivity()));

//        showWaitView();
        HttpManager.getInstance(getActivity()).getTopImage("/noticeboard/generateExcelFile", map, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
//                stopWaitingView();
                if(jsonObject.optInt("code") == 1){
                    try {
//                        JSONArray jsonArray = jsonObject.optJSONArray("ret");
                        JSONObject obj = jsonObject.optJSONObject("ret");
//                        JSONObject obj = jsonArray.getJSONObject(0);
                        final String url = obj.optString("url");

                        final String downloadurl = Consts.HTTP_URL +"/file/excel/"+url;


//                        runOnUiThread(new Runnable(){
//
//                            @Override
//                            public void run() {
//                                //更新UI
//                                downloadFile1(downloadurl,url);
//                            }
//
//                        });
// Android 4.0 之后不能在主线程中请求HTTP请求
                        new Thread(new Runnable(){
                            @Override
                            public void run() {
                                downloadFile1(downloadurl,url);
                            }
                        }).start();

//                        String url = "soft.imtt.qq.com/browser/tes/feedback.html";
//                        TbsPlus.openUrl(getActivity(), downloadurl, TbsPlus.eTBSPLUS_SCREENDIR.eTBSPLUS_SCREENDIR_SENSOR);

//                        Intent intent = new Intent(getActivity(),
//                                BrowserActivity.class);
//                        getActivity().startActivity(intent);

//                        WebActivity.actionStart(getActivity(), url,"资料导出");

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                else {
                    Toast.makeText(getActivity(),"删除失败",Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
//                stopWaitingView();
//                Toast.makeText(getActivity(),"删除失败",Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void downloadFile1(String url,String filename) {
        try {
//下载路径，如果路径无效了，可换成你的下载路径;
            String path = Environment.getExternalStorageDirectory().getAbsolutePath();
            final long startTime = System.currentTimeMillis();
            Log.i("DOWNLOAD", "startTime=" + startTime);
//下载函数
            URL myURL = new URL(url);
            URLConnection conn = myURL.openConnection();
            conn.connect();
            InputStream is = conn.getInputStream();
            int fileSize = conn.getContentLength();//根据响应获取文件大小
            if (fileSize <= 0) throw new RuntimeException("无法获知文件大小 ");
            if (is == null) throw new RuntimeException("stream is null");
            File file1 = new File(path);
            if (!file1.exists()) {
                file1.mkdirs();
            }
//把数据存入路径+文件名
            FileOutputStream fos = new FileOutputStream(path + "/" + filename);
            byte buf[] = new byte[1024];
            int downLoadFileSize = 0;
            do {
//循环读取
                int numread = is.read(buf);
                if (numread == -1) {
                    break;
                }
                fos.write(buf, 0, numread);
                downLoadFileSize += numread;
//更新进度条
            } while (true);
            is.close();
            final String downloadfile =path+"/"+filename;

//            runOnUiThread(new Runnable(){
//
//                @Override
//                public void run() {
//                    //更新UI
//
//                    Toast.makeText(getActivity(),"文件已保存，位置为"+downloadfile,Toast.LENGTH_LONG).show();
////                                Intent intent = new Intent("android.intent.action.VIEW");
////                                intent.addCategory("android.intent.category.DEFAULT");
////                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
////                                File file = new File(downloadfile);
////                                Uri uri = Uri.fromFile(file);
////                                intent.setDataAndType(uri, "application/vnd.ms-excel");
//                }
//
//            });
//
//            Log.i("DOWNLOAD", "download success");
//            Log.i("DOWNLOAD", "totalTime=" + (System.currentTimeMillis() - startTime));

        } catch (Exception ex) {
            Log.e("DOWNLOAD", "error: " + ex.getMessage(), ex);
        }
    }

    //预约单未读数
    private void queryCustomerOrders(){
        Map map = new HashMap();
        map.put("owner", LoginUserUtil.getTel(getActivity()));


        String url = "";


        url = "/contact/getOrderRepairList";

        HttpManager.getInstance(getActivity())
                .queryCustomerOrders(url, map, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        Log.e(TAG,"/repair/getOrderRepairList"+jsonObject.toString());

                        if(jsonObject.optInt("code") == 1){


                            JSONArray arr = jsonObject.optJSONArray("ret");

                            if(arr!= null && arr.length()>0)
                            {
                                i_queryCustomerOrders = arr.length();
                                adapter.notifyDataSetChanged();
                            }

                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        int i = 0;
                    }
                });
    }

    //到期提醒未读数
    private  void queryAllTipedRepair(){
        Map map = new HashMap();
        map.put("owner", LoginUserUtil.getTel(getActivity()));


        String url = "";

        url = "/repair/queryAllTiped1";

        HttpManager.getInstance(getActivity())
                .queryAllTipedRepair(url, map, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        Log.e(TAG,"/repair/queryAllTiped1"+jsonObject.toString());

                        if(jsonObject.optInt("code") == 1){


                            ArrayList<RepairHistory> m_arrData =  getArrayRepair(jsonObject,2);
                            i_queryAllTipedRepair = m_arrData.size();
                            if(m_arrData.size()>0)
                            {
                                i_queryAllTipedRepair = m_arrData.size();
//                                adapter.notifyDataSetChanged();
                            }
                            queryAllTipedRepair2();

                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        queryAllTipedRepair2();
                    }
                });

    }

    //到期提醒未读数2
    private  void queryAllTipedRepair2(){
        Map map = new HashMap();
        map.put("owner", LoginUserUtil.getTel(getActivity()));


        String url = "";

        url = "/contact/queryAllYearCheckTiped";

        HttpManager.getInstance(getActivity())
                .queryAllTipedRepair(url, map, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        Log.e(TAG,"/repair/queryAllTiped1"+jsonObject.toString());

                        if(jsonObject.optInt("code") == 1){


                            JSONArray arr = jsonObject.optJSONArray("ret");

                            if(arr!= null && arr.length()>0)
                            {
                                i_queryAllTipedRepair += arr.length();
//                                adapter.notifyDataSetChanged();
                            }
                            queryAllTipedRepair3();
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                    }
                });

    }

    //到期提醒未读数3
    private  void queryAllTipedRepair3(){
        Map map = new HashMap();
        map.put("owner", LoginUserUtil.getTel(getActivity()));


        String url = "";

        url = "/contact/queryAllSafeTiped";

        HttpManager.getInstance(getActivity())
                .queryAllTipedRepair(url, map, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        Log.e(TAG,"/repair/queryAllSafeTiped"+jsonObject.toString());

                        if(jsonObject.optInt("code") == 1){


                            JSONArray arr = jsonObject.optJSONArray("ret");

                            if(arr!= null && arr.length()>0)
                            {
                                i_queryAllTipedRepair += arr.length();
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


    // 工单未读数
    private void getAllRepaiirsWithState2(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// HH:mm:ss
//获取当前时间
        Date date = new Date(System.currentTimeMillis());
        String time = simpleDateFormat.format(date);
        Map map = new HashMap();
        map.put("owner", LoginUserUtil.getTel(getActivity()));

        map.put("page", String.valueOf(1));
        map.put("pagesize",  "1000");
        map.put("insertTime",time);
        map.put("state","0");

        String url = "";

        url = "/repair/queryAllWithState2";

        HttpManager.getInstance(getActivity())
                .queryAllTipedRepair(url, map, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        Log.e(TAG,"/repair/queryAllWithState"+jsonObject.toString());

                        if(jsonObject.optInt("code") == 1){

                            ArrayList<RepairHistory> m_arrData =  getArrayRepair(jsonObject,1);
//                            i_getAllRepaiirsWithState2 = m_arrData.size();
                            if(m_arrData.size()>0)
                            {
                                i_getAllRepaiirsWithState2 = m_arrData.size();
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

    private ArrayList<RepairHistory> getArrayRepair(JSONObject ret,int type){
        JSONArray arr = ret.optJSONArray("ret");

        ArrayList<RepairHistory> arrRep =  new ArrayList() ;
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

                repFromServer.state =obj.optString("state");
                repFromServer.customremark =obj.optString("customremark");
                repFromServer.wantedcompletedtime =obj.optString("wantedcompletedtime");
                repFromServer.iswatiinginshop =obj.optString("iswatiinginshop");
                repFromServer.entershoptime =obj.optString("entershoptime");
                repFromServer.contactid =obj.optString("contactid");

                repFromServer.payType =obj.optString("payType");
                repFromServer.pics =obj.optString("pics");
                repFromServer.ownnum =obj.optString("ownnum");
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

                if(type ==1 ) {
                    Contact con = DBService.queryContact(repFromServer.carCode);
                    if (con != null) {
                        arrRep.add(repFromServer);
                    }
                }else if(type ==2){

                    Contact con = DBService.queryContactCode(repFromServer.contactid);
                    if (con != null) {
                        arrRep.add(repFromServer);
                    }
                }


            }
        }
        return arrRep;
    }



    public class ImageAdapter extends BaseAdapter {

        private Context mContext;
        private LayoutInflater inflater;
        private class GirdTemp{
            ImageView phone_function_pic;
            TextView phone_function_name;
            TextView phone_function_pic_unread;
        }
        public ImageAdapter(Context c){
            mContext = c;
            inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        @Override
        public int getCount() {

            return lstImageItem.size();

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
            MyUnFinishedRepairFragment.ImageAdapter.GirdTemp temp;
//            if(convertView == null){
            convertView = inflater.inflate(R.layout.activity_homemenu_item, null);
            temp = new MyUnFinishedRepairFragment.ImageAdapter.GirdTemp();
            temp.phone_function_pic = (ImageView) convertView.findViewById(R.id.ItemImage);
            temp.phone_function_name = (TextView) convertView.findViewById(R.id.ItemText);
            temp.phone_function_pic_unread = (TextView) convertView.findViewById(R.id.unreadImage);
//                convertView.setTag(holder);
//            }else{
//                temp = (GirdHolder) convertView.getTag();
//            }
            HashMap<String, Object> map =lstImageItem.get(position);

            temp.phone_function_pic.setImageResource((int)map.get("icon"));
            temp.phone_function_name.setText((String)map.get("name"));

            if("工单".equalsIgnoreCase((String)map.get("name")))
            {
                if(i_getAllRepaiirsWithState2 >0)
                {
                    temp.phone_function_pic_unread.setVisibility(View.VISIBLE);
                    temp.phone_function_pic_unread.setText(i_getAllRepaiirsWithState2+"");
                }
            }

            if("预约单".equalsIgnoreCase((String)map.get("name")))
            {
                if(i_queryCustomerOrders >0)
                {
                    temp.phone_function_pic_unread.setVisibility(View.VISIBLE);
                    temp.phone_function_pic_unread.setText(i_queryCustomerOrders+"");
                }
            }

            if("到期提醒".equalsIgnoreCase((String)map.get("name")))
            {
                if(i_queryAllTipedRepair >0)
                {
                    temp.phone_function_pic_unread.setVisibility(View.VISIBLE);
                    temp.phone_function_pic_unread.setText(i_queryAllTipedRepair+"");
                }
            }

            return convertView;
        }

    }

    void queryContactInfo()
    {
        Map map = new HashMap();
        map.put("owner", LoginUserUtil.getTel(getActivity()));



        String url = "";

        url = "/contact/getTotalCount";

        HttpManager.getInstance(getActivity())
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
            queryAllMap.put("owner",   LoginUserUtil.getTel(getActivity()));

            HttpManager.getInstance(getActivity()).queryAllContacts("/contact/queryAll", queryAllMap, new Response.Listener<JSONObject>() {
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
                                    conFromServer.setSafecompany3(JSONOejectUtil.optString(obj,"safecompany3"));
                                    conFromServer.setSafenexttime3(JSONOejectUtil.optString(obj,"safenexttime3"));
                                    conFromServer.setTqTime3(JSONOejectUtil.optString(obj,"tqTime3"));

                                    DBService.addNewContact(conFromServer,db);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
//                                DBService.closeDB(db);?

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

    public void setTop(int num)
    {
        if(num !=5) {
            top1.setTextColor(getResources().getColor(R.color.mdtp_date_picker_text_disabled));
            top2.setTextColor(getResources().getColor(R.color.mdtp_date_picker_text_disabled));
            top3.setTextColor(getResources().getColor(R.color.mdtp_date_picker_text_disabled));
            top4.setTextColor(getResources().getColor(R.color.mdtp_date_picker_text_disabled));
            top5.setTextColor(getResources().getColor(R.color.mdtp_date_picker_text_disabled));
        }
        String url = "";
        switch (num)
        {
            case 1:
                top1.setTextColor(getResources().getColor(R.color.text_blue));
                url = Consts.HTTP_URL+"/repairstatistics/homeAnalysis?type=1&owner="+LoginUserUtil.getTel(getActivity());
                webView.loadUrl(url);
                break;
            case 2:
                url = Consts.HTTP_URL+"/repairstatistics/homeAnalysis?type=2&owner="+LoginUserUtil.getTel(getActivity());
                webView.loadUrl(url);
                top2.setTextColor(getResources().getColor(R.color.text_blue));
                break;
            case 3:
                url = Consts.HTTP_URL+"/repairstatistics/homeAnalysis?type=3&owner="+LoginUserUtil.getTel(getActivity());
                webView.loadUrl(url);
                top3.setTextColor(getResources().getColor(R.color.text_blue));
                break;
            case 4:
                url = Consts.HTTP_URL+"/repairstatistics/homeAnalysis?type=4&owner="+LoginUserUtil.getTel(getActivity());
                webView.loadUrl(url);
                top4.setTextColor(getResources().getColor(R.color.text_blue));
                break;
            case 5:

                url = Consts.HTTP_URL+"/repairstatistics/homeAnalysis2";

                Intent intent = new Intent(getActivity(), ReportWebviewActivity.class);
                intent.putExtra("url",url);
                intent.putExtra("title","报表");
                startActivity(intent);
//                top5.setTextColor(getResources().getColor(R.color.text_blue));
                break;

        }

    }


}
