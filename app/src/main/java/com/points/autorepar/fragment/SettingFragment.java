package com.points.autorepar.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.baidubce.BceClientException;
import com.baidubce.BceServiceException;
import com.baidubce.auth.DefaultBceCredentials;
import com.baidubce.services.bos.BosClient;
import com.baidubce.services.bos.BosClientConfiguration;
import com.baidubce.services.bos.model.PutObjectResponse;
import com.points.autorepar.MainApplication;
import com.points.autorepar.R;
import com.points.autorepar.activity.EditUserInfoActivity;
import com.points.autorepar.activity.LoginActivity;
import com.points.autorepar.activity.MainTabbarActivity;
import com.points.autorepar.activity.ResetPwdActivity;
import com.points.autorepar.bean.ADTReapirItemInfo;
import com.points.autorepar.bean.Contact;
import com.points.autorepar.common.Consts;
import com.points.autorepar.http.HttpManager;
import com.points.autorepar.lib.wheelview.WheelView;
import com.points.autorepar.utils.DateUtil;
import com.points.autorepar.utils.FileUtils;
import com.points.autorepar.utils.LoginUserUtil;
import com.points.autorepar.utils.ScreenUtil;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.File;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class SettingFragment extends Fragment {
    private static final String key_sp  = "points";

    private static final String TAG = "SettingFragment";

    private OnFragmentInteractionListener mListener;

    private Context m_context;
    private WebView web;
    private Button m_addNewRepairHistoryBtn;
    private TextView m_title;
    private TextView mLogoutBtn;
    private ImageView m_head;
    private  Uri  outputFileUri;

    private TextView mShareBtn;
    private TextView mResetPwdBtn;
    private MainTabbarActivity m_parentActivity;

    private  TextView mNameTextView;
    private  TextView mShopNameTextView;
    private  TextView mAddressTextView;
    private  TextView mPriceTextView;
    private  TextView mNumTextView;
    private TextView vip_tex;
    private TextView set_ad;
    private  TextView set_time;
    private ImageButton m_editBtn;
    private Switch m_switcher1;
    private Switch m_switcher2;
    private Switch m_switcher3;
    public void SetDelegate (OnFragmentInteractionListener delegate){
        mListener = delegate;
        m_parentActivity = (MainTabbarActivity)delegate;
    }


    public static SettingFragment newInstance(String param1, String param2) {
        SettingFragment fragment = new SettingFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public SettingFragment() {
        // Required empty public constructor
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
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        web = (WebView)view.findViewById(R.id.id_setting_webview);
        MainApplication mainApplication = (MainApplication) getActivity().getApplication();

        web.loadUrl (mainApplication.consts().HTTP_URL+"/noticeboard/android");

        m_addNewRepairHistoryBtn = (Button)view.findViewById(R.id.common_navi_add);
        m_addNewRepairHistoryBtn.setText("编辑");
        m_addNewRepairHistoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(),EditUserInfoActivity.class);
                startActivity(intent);
            }
        });

        if(MainApplication.getInstance().getUserType(getActivity())== 1 || MainApplication.getInstance().getUserType(getActivity())== 3)
        {
            m_addNewRepairHistoryBtn.setVisibility(View.INVISIBLE);
        }
        m_title = (TextView)view.findViewById(R.id.common_navi_title);
        m_title.setText("我的");
        vip_tex = (TextView)view.findViewById(R.id.vip_text);

        if(MainApplication.getInstance().getUserType(getActivity()) == 1 || MainApplication.getInstance().getUserType(getActivity())== 3)
        {
            vip_tex.setVisibility(View.GONE);

        }else{
            vip_tex.setVisibility(View.VISIBLE);
        }
        vip_tex.setText( MainApplication.getInstance().getEndDate(getActivity()));


        m_head = (ImageView) view.findViewById(R.id.set_head);
        Button common_navi_back = (Button)view.findViewById(R.id.common_navi_back);
        common_navi_back.setVisibility(View.INVISIBLE);

        mNameTextView = (TextView) view.findViewById(R.id.set_name);
        mShopNameTextView = (TextView) view.findViewById(R.id.set_shopname);
        mAddressTextView = (TextView) view.findViewById(R.id.set_address);

        set_ad = (TextView) view.findViewById(R.id.set_ad);
        set_time = (TextView) view.findViewById(R.id.set_time);

        m_switcher1 = (Switch)view.findViewById(R.id.setting_switch1);
        m_switcher2= (Switch)view.findViewById(R.id.setting_send_switch1);
        m_switcher3 = (Switch)view.findViewById(R.id.set_changecar_item_switch);

        String key_isdirectadditem=  getActivity().getApplicationContext().getResources().getString(R.string.key_loginer_isdirectadditem);
        SharedPreferences sp = getActivity().getSharedPreferences(key_sp, Context.MODE_PRIVATE);
        String isdirectadditem = sp.getString(key_isdirectadditem, null);



        if("0".equalsIgnoreCase(isdirectadditem))
        {
            m_switcher1.setChecked(false);
        }else{
            m_switcher1.setChecked(true);

        }

        m_switcher1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String flag = "0";
                if (isChecked){
                    flag = "1";
                }else {
                    flag = "0";
                }
                updateUserAddItemSet(flag);
            }
        });


       String key_isneeddispatch =  getActivity().getApplicationContext().getResources().getString(R.string.key_loginer_isneeddispatch);
        sp = getActivity().getSharedPreferences(key_sp, Context.MODE_PRIVATE);
        String isneeddispatch = sp.getString(key_isneeddispatch, null);



        String isneeddirectaddcartype = MainApplication.getInstance().getisneeddirectaddcartype(getActivity());
        if("0".equalsIgnoreCase(isneeddirectaddcartype))
        {
            m_switcher3.setChecked(false);
        }else{
            m_switcher3.setChecked(true);

        }
       if("0".equalsIgnoreCase(isneeddispatch))
       {
           m_switcher2.setChecked(false);
       }else{
           m_switcher2.setChecked(true);

       }

        m_switcher3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String flag = "0";
                if (isChecked){
                    flag = "1";
                }else {
                    flag = "0";
                }
                updateAddCarTyoeSet(flag);
            }
        });

        m_switcher2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String flag = "0";
                if (isChecked){
                    flag = "1";
                }else {
                    flag = "0";
                }
                updateDispatchSet(flag);
            }
        });


         TextView set_input_price = (TextView) view.findViewById(R.id.set_input_price);
         View set_inut_num_view = (View)view.findViewById(R.id.set_inut_num_view);
        View set_inut_num_view1 = (View)view.findViewById(R.id.set_inut_num_view1);
         TextView today_set_input_price = (TextView)view.findViewById(R.id.today_set_input_price);
         LinearLayout set_send_item_setting_ln = (LinearLayout) view.findViewById(R.id.set_send_item_setting_ln);
        LinearLayout set_rep_item_setting_ln = (LinearLayout) view.findViewById(R.id.set_rep_item_setting_ln);

        LinearLayout set_changecar_item_setting_ln = (LinearLayout)view.findViewById(R.id.set_changecar_item_setting_ln);
        mPriceTextView = (TextView) view.findViewById(R.id.set_input_price);
        mPriceTextView.setText(LoginUserUtil.getTodayTotalInput(getActivity()));
        mNumTextView = (TextView) view.findViewById(R.id.set_inut_num);
        mNumTextView.setText(LoginUserUtil.getTodayTotalNum(getActivity()));

        if(MainApplication.getInstance().getUserType(getActivity()) != 3 && LoginUserUtil.isEmployeeLogined(getActivity()))
        {
            set_input_price.setVisibility(View.GONE);
            today_set_input_price.setVisibility(View.GONE);
            mPriceTextView.setVisibility(View.GONE);
            mNumTextView.setVisibility(View.GONE);
            set_inut_num_view.setVisibility(View.GONE);
            set_inut_num_view1.setVisibility(View.GONE);
            set_send_item_setting_ln.setVisibility(View.GONE);
            set_rep_item_setting_ln.setVisibility(View.GONE);
            set_changecar_item_setting_ln.setVisibility(View.GONE);

        }else{
            today_set_input_price.setVisibility(View.GONE);
            set_input_price.setVisibility(View.GONE);
            mPriceTextView.setVisibility(View.GONE);
            mNumTextView.setVisibility(View.GONE);
            set_inut_num_view.setVisibility(View.GONE);
            set_inut_num_view1.setVisibility(View.GONE);
            set_send_item_setting_ln.setVisibility(View.VISIBLE);
            set_rep_item_setting_ln.setVisibility(View.VISIBLE);
        }

        if(m_parentActivity != null){
            if(m_parentActivity.imageLoader != null){
                m_parentActivity.imageLoader.get(LoginUserUtil.gethHeadUrl(getActivity()), new ImageLoader.ImageListener() {
                    @Override
                    public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {
                        m_head.setImageBitmap(imageContainer.getBitmap());
                    }

                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        m_head.setImageResource(R.drawable.appicon);
                    }
                },1000,1000);
            }
        }


        m_head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                m_parentActivity.startChangeHeadImage();
            }
        });

        mLogoutBtn = (TextView) view.findViewById(R.id.setting_logout);
        mLogoutBtn.setClickable(true);
        mLogoutBtn.setFocusableInTouchMode(false);
        mLogoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLogoutBtnClicked();
            }
        });

        mShareBtn = (TextView) view.findViewById(R.id.set_share);
        mShareBtn.setClickable(true);
        mShareBtn.setFocusableInTouchMode(false);
        mShareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onShareXZS();
            }
        });

        mResetPwdBtn = (TextView) view.findViewById(R.id.set_resetpwd);
        mResetPwdBtn.setClickable(true);
        mResetPwdBtn.setFocusableInTouchMode(false);
        mResetPwdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),ResetPwdActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            this.m_context = activity;
            Log.d(TAG,"onAttach");

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

    public interface OnFragmentInteractionListener {

        public void onFragmentInteraction(Uri uri);

        public void onLogout();

        public void onShareXZS();
    }



    @Override
    public  void  onStart() {
        super.onStart();
        Log.e(TAG, "onStart");


        mNameTextView.setText("用户名: "+LoginUserUtil.getName(getActivity()));
        mShopNameTextView.setText("门店名: "+LoginUserUtil.getShopName(getActivity()));
        mAddressTextView.setText("门店地址: "+LoginUserUtil.getAddress(getActivity()));

        String ad= MainApplication.getInstance().getShopAD(getActivity());
        String start= MainApplication.getInstance().getworkstarttime(getActivity());
        String end= MainApplication.getInstance().getworkendtime(getActivity());
        set_ad.setText("广告宣传语："+ad);
        set_time.setText("营业时间："+start+"-"+end);
        mPriceTextView.setText(LoginUserUtil.getTodayTotalInput(getActivity()));
        mNumTextView.setText(LoginUserUtil.getTodayTotalNum(getActivity()));


        Map map = new HashMap();
        map.put("day", DateUtil.getToday());
        map.put("owner", LoginUserUtil.getTel(getActivity()));
        HttpManager.getInstance(getActivity()).queryTodayServiceInfo("/repair/getTodayBills", map, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {

                Log.e(TAG,jsonObject.toString());
                String ret = jsonObject.optString("code");
                if(ret == "1"){
                    mPriceTextView.setText("今日收入:¥"+jsonObject.optJSONObject("ret").optString("totalprice"));
                    mNumTextView.setText("今日开单:"+jsonObject.optJSONObject("ret").optString("totalRepCount"));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        });
    }


    public void  refreshWebView(){
        if(web != null) {
//            web.loadUrl("http://autorepairhelper.duapp.com/noticeboard/android");
        }

    }

    public void  onLogoutBtnClicked(){
        if(mListener != null){
            mListener.onLogout();
        }
    }


   public void refreshHead(String url){
       m_parentActivity.imageLoader.get(url, new ImageLoader.ImageListener() {
           @Override
           public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {
               m_head.setImageBitmap(imageContainer.getBitmap());
           }

           @Override
           public void onErrorResponse(VolleyError volleyError) {

           }
       },200,200);
   }

    public void updateDispatchSet(final String type){



        Map cv = new HashMap();
        cv.put("isneeddispatch", type);
        cv.put("tel", LoginUserUtil.getTel(getActivity()));

        HttpManager.getInstance(m_context).updateOneRepair("/users/updateUserDispatchSet", cv, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {


                if(jsonObject.optInt("code") == 1){
                    String key_isneeddispatch =  getActivity().getApplicationContext().getResources().getString(R.string.key_loginer_isneeddispatch);

                    SharedPreferences.Editor editor18= getActivity().getSharedPreferences(key_sp, Context.MODE_PRIVATE).edit();
                    editor18.putString(key_isneeddispatch, type);
                    editor18.commit();

                    Toast.makeText(m_context,"更新成功",Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(m_context,"更新失败",Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {


                Toast.makeText(m_context,"更新失败",Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void updateUserAddItemSet(final String type)
    {
        Map cv = new HashMap();
        cv.put("isdirectadditem", type);
        cv.put("tel", LoginUserUtil.getTel(getActivity()));

        HttpManager.getInstance(m_context).updateOneRepair("/users/updateUserAddItemSet", cv, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {


                if(jsonObject.optInt("code") == 1){
                    String key_isdirectadditem=  getActivity().getApplicationContext().getResources().getString(R.string.key_loginer_isdirectadditem);
                    SharedPreferences.Editor editor18 = getActivity().getSharedPreferences(key_sp, Context.MODE_PRIVATE).edit();

                    editor18.putString(key_isdirectadditem, type);
                    editor18.commit();

                    Toast.makeText(m_context,"更新成功",Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(m_context,"更新失败",Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {


                Toast.makeText(m_context,"更新失败",Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void updateAddCarTyoeSet(final String type){



        Map cv = new HashMap();
        cv.put("isneeddirectaddcartype", type);
        cv.put("tel", LoginUserUtil.getTel(getActivity()));

        HttpManager.getInstance(m_context).updateOneRepair("/users/updateAddCarTyoeSet", cv, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {


                if(jsonObject.optInt("code") == 1){
                   MainApplication.getInstance().setisneeddirectaddcartype(getActivity(),type);

                    Toast.makeText(m_context,"更新成功",Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(m_context,"更新失败",Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {


                Toast.makeText(m_context,"更新失败",Toast.LENGTH_SHORT).show();
            }
        });
    }

}
