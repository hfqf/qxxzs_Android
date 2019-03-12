package com.points.autorepar.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bigkoo.pickerview.TimePickerView;

import com.points.autorepar.R;
import com.points.autorepar.activity.contact.SelectContactActivity;
import com.points.autorepar.MainApplication;
import com.points.autorepar.lib.cjj.MaterialRefreshLayout;
import com.points.autorepar.lib.cjj.MaterialRefreshListener;
import com.points.autorepar.bean.Contact;
import com.points.autorepar.utils.DateUtil;
import com.points.autorepar.utils.LoginUserUtil;

import java.util.Calendar;
import java.util.Date;

public class QueryFragment extends Fragment {



    public interface OnFragmentInteractionListener {

        public void onFragmentInteraction(Uri uri);
        public void onQueryFragmentSharePrint(String url);
    }

    private OnFragmentInteractionListener mListener;

    private final  String   TAG = "QueryFragment";
    private WebView m_webview;
    private SearchView   m_search;
    //下拉上拉刷新控件
    private MaterialRefreshLayout materialRefreshLayout;
    private  Button    addBtn;
    private TextView   title;
    private  String    currentUrl;

    private EditText    startEditText;
    private EditText    endEditText;
    private EditText    contactEditText;
    private  Button     queryBtn;
    private  boolean    m_isStartTime;
    private TimePicker m_picker;
    private Contact    m_contact;
    public QueryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_query, container, false);
        m_webview = (WebView) view.findViewById(R.id.id_query_list);
        m_webview.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent ev) {

                ((WebView)v).requestDisallowInterceptTouchEvent(true);


                return false;
            }
        });
        WebSettings webSettings = m_webview.getSettings();
        webSettings.setDisplayZoomControls(false);
        webSettings.setBuiltInZoomControls(true); // 设置显示缩放按钮
        webSettings.setSupportZoom(true); // 支持缩放
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);

        addBtn = (Button) view.findViewById(R.id.common_navi_add);
        addBtn.setText("打印");
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mListener.onQueryFragmentSharePrint(currentUrl);

            }
        });
        title = (TextView)view.findViewById(R.id.common_navi_title);
        title.setText("统计");

        startEditText = (EditText) view.findViewById(R.id.query_input1);
        startEditText.setClickable(true);
        startEditText.setFocusableInTouchMode(false);
        startEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m_isStartTime = true;
                selectDayTime();
            }
        });
        startEditText.setText(DateUtil.getYear()+" 00:00:00");
        endEditText = (EditText)view.findViewById(R.id.query_input2);
        endEditText.setClickable(true);
        endEditText.setFocusableInTouchMode(false);
        endEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m_isStartTime = false;
                selectDayTime();
            }
        });
        endEditText.setText(DateUtil.getToday()+" 23:59:59");

        contactEditText = (EditText)view.findViewById(R.id.query_input3);
        contactEditText.setClickable(true);
        contactEditText.setFocusableInTouchMode(false);
        contactEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contactEditText.setText("");
                Intent intent = new Intent(getActivity(),SelectContactActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("flag","0");
                intent.putExtras(bundle);
                startActivityForResult(intent,1);
            }
        });

        queryBtn = (Button) view.findViewById(R.id.startQuery);
        queryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reloadDataAndRefreshView();
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
                m_contact = data.getParcelableExtra("contact");
                contactEditText.setText(m_contact.getName());
            }

        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.e("Contact", "onActivityCreated1");
        materialRefreshLayout = (MaterialRefreshLayout) getActivity().findViewById(R.id.refresh_query);
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
        Log.e("Contact", "onStart");
        reloadDataAndRefreshView();
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
        if(startEditText.getText().length() == 0){
            Toast.makeText(getActivity(),"开始时间不能为空",Toast.LENGTH_SHORT).show();
            return;
        }

        if(endEditText.getText().length() == 0){
            Toast.makeText(getActivity(),"结束时间不能为空",Toast.LENGTH_SHORT).show();
            return;
        }

        if(!DateUtil.isValidTimeSelect(startEditText.getText().toString(),endEditText.getText().toString())){
            Toast.makeText(getActivity(),"结束时间不能小于开始时间",Toast.LENGTH_SHORT).show();
            return;
        }

        Activity hander = (Activity)getActivity();
        MainApplication mainApplication = (MainApplication) hander.getApplication();
        String carCode = m_contact == null ? "" : m_contact.getCarCode();
        currentUrl = mainApplication.consts().HTTP_URL +"/repair/print?owner="+ LoginUserUtil.getTel(getActivity()) +"&carcode="+carCode+"&start="+startEditText.getText().toString()+"&end="+endEditText.getText().toString();
        m_webview.loadUrl(currentUrl);
        materialRefreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                materialRefreshLayout.finishRefresh();
                materialRefreshLayout.finishRefreshLoadMore();
            }
        }, 300);

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
                    if(m_isStartTime){
                        startEditText.setText(time);
                    }else {
                        endEditText.setText(time);
                    }
            }
        }).build();
        pvTime.setDate(Calendar.getInstance());//注：根据需求来决定是否使用该方法（一般是精确到秒的情况），此项可以在弹出选择器的时候重新设置当前时间，避免在初始化之后由于时间已经设定，导致选中时间与当前时间不匹配的问题。
        pvTime.show();

    }

}
