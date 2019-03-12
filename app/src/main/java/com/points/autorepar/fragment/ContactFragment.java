package com.points.autorepar.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import com.points.autorepar.activity.contact.ContactAddNewActivity;
import com.points.autorepar.R;
import com.points.autorepar.activity.contact.ContactInfoEditActivity;
import com.points.autorepar.activity.contact.ContactOrderActivity;
import com.points.autorepar.adapter.ContactAdapter;
import com.points.autorepar.adapter.ContactOrderListAdapter;
import com.points.autorepar.adapter.ContactSimAdapter;
import com.points.autorepar.adapter.RepairHistoryAdapter;
import com.points.autorepar.bean.ContactOrderInfo;
import com.points.autorepar.bean.ContactPresenter;
import com.points.autorepar.bean.ContactSim;
import com.points.autorepar.http.HttpManager;
import com.points.autorepar.interfaces.IContactView;
import com.points.autorepar.lib.cjj.MaterialRefreshLayout;
import com.points.autorepar.lib.cjj.MaterialRefreshListener;
import com.points.autorepar.lib.slidebar.SlideBar;
import com.points.autorepar.lib.sortlistview.CharacterParser;
import com.points.autorepar.lib.sortlistview.ClearEditText;
import com.points.autorepar.lib.sortlistview.PinyinComparator;
import com.points.autorepar.lib.sortlistview.SideBar;
import com.points.autorepar.lib.sortlistview.SortAdapter;
import com.points.autorepar.lib.sortlistview.SortModel;
import com.points.autorepar.sql.DBService;
import com.points.autorepar.bean.Contact;
import com.points.autorepar.utils.HttpWatingUtil;
import com.points.autorepar.utils.LoggerUtil;
import com.points.autorepar.utils.LoginUserUtil;
import com.points.autorepar.utils.PinyinUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ContactFragment extends Fragment {

    //回调接口
    public interface OnContactFragmentListener {
        //TODO 可自定义回调函数
        public void onContactFramentReloadData();

        public void onSelectedContact(Contact contact);
    }


    private static final String TAG = "ContactFragment";
    //下拉上拉刷新控件
    private MaterialRefreshLayout materialRefreshLayout;

    //回调
    private OnContactFragmentListener mListener;



    private ListView         sortListView;
    private SideBar          slideBar;
    private TextView         dialog;
    private SortAdapter      adapter;
    private ClearEditText    mClearEditText;
    private CharacterParser  characterParser;
    private List<SortModel>  SourceDateList;
    private PinyinComparator pinyinComparator;
    private boolean          m_isSelectContact;
    private Button           addBtn;
    private Button           backBtn;
    List<SortModel>          filterDateList;
    private TextView         m_numLab;


    public static ContactFragment newInstance(String param1) {
        ContactFragment fragment = new ContactFragment();
        Bundle args = new Bundle();
        args.putString(TAG, param1);
        fragment.setArguments(args);
        return fragment;
    }

    public ContactFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.e(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            m_isSelectContact = getArguments().getString(TAG).equals("1");
        }

    }

    public void showQueryViewStyles(){
        addBtn.setText("新增");
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                getActivity().finish();
//                if("2".equalsIgnoreCase(m_selectType))
//                {
                    Intent intent = new Intent(getActivity(),ContactAddNewActivity.class);
                    intent.putExtra("inittype",1);
                    startActivity(intent);
//                }
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        Log.e(TAG, "onCreateView");
        View view = inflater.inflate(R.layout.fragment_contact, container, false);


        characterParser = CharacterParser.getInstance();
        pinyinComparator = new PinyinComparator();
        slideBar = (SideBar) view.findViewById(R.id.sidrbar);
        dialog = (TextView) view.findViewById(R.id.dialog);
        slideBar.setTextView(dialog);

        slideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {

            @Override
            public void onTouchingLetterChanged(String s) {
                int position = adapter.getPositionForSection(s.charAt(0));
                if(position != -1){
                    sortListView.setSelection(position);
                }

            }
        });


        m_numLab = (TextView)view.findViewById(R.id.common_navi_num);


        backBtn = (Button) view.findViewById(R.id.common_navi_back);
        backBtn.setVisibility(View.INVISIBLE);
        backBtn.setText("预约");
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), ContactOrderActivity.class);
                startActivity(intent);

            }
        });

        addBtn = (Button) view.findViewById(R.id.common_navi_add);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("Contact", "onActivityCreated");
                Intent intent = new Intent(getActivity(),ContactAddNewActivity.class);
                startActivity(intent);
            }
        });

        if(m_isSelectContact){
            showQueryViewStyles();
        }

        sortListView = (ListView)view.findViewById(R.id.id_contact_list);
        sortListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SortModel data = null;
                if(mClearEditText.getText().toString().length() == 0){
                     data = (SortModel) SourceDateList.get(position);
                }else {
                     data = (SortModel) filterDateList.get(position);
                }
                if(m_isSelectContact){
                    mListener.onSelectedContact(data.contact);
                }else {
                    mClearEditText.setText("");
                    Intent intent = new  Intent(getActivity(),ContactInfoEditActivity.class);
                    intent.putExtra(String.valueOf(R.string.key_contact_edit_para), data.contact);
                    startActivity(intent);
                }
                Log.e(TAG,"onItemClick");
            }
        });


        mClearEditText = (ClearEditText) view.findViewById(R.id.filter_edit);

        mClearEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterData(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });



        return view;
    }


    /**
     * @param
     * @return
     */
    private List<SortModel> filledData( ){
        ArrayList<Contact> arr = DBService.queryAllContact();
        List<SortModel> mSortList = new ArrayList<SortModel>();

        for(int i=0; i<arr.size(); i++){
            SortModel sortModel = new SortModel();
            Contact con =  arr.get(i);
            sortModel.setName(con.getName());
            sortModel.contact = con;
            String pinyin = characterParser.getSelling(con.getName());
            String sortString = "";
            if(pinyin.length() > 0){
                 sortString = pinyin.substring(0, 1).toUpperCase();
            }


            if(sortString.matches("[A-Z]")){
                sortModel.setSortLetters(sortString.toUpperCase());
            }else{
                sortModel.setSortLetters("#");
            }

            mSortList.add(sortModel);
        }
        return mSortList;

    }

    /**
     * @param filterStr
     */
    private void filterData(String filterStr){
        filterDateList = new ArrayList<SortModel>();

        if(TextUtils.isEmpty(filterStr)){
            filterDateList = SourceDateList;
        }else{
            filterDateList.clear();
            for(SortModel sortModel : SourceDateList){
                Contact carcode = sortModel.contact;
                String name = sortModel.getName();
                if(name.indexOf(filterStr.toString()) != -1||carcode.getTel().indexOf(filterStr.toString()) != -1  ||carcode.getCarCode().indexOf(filterStr.toString()) != -1  || characterParser.getSelling(name).startsWith(filterStr.toString())){
                    filterDateList.add(sortModel);
                }
            }
        }
    if(filterDateList !=null){
      Collections.sort(filterDateList, pinyinComparator);

     adapter.updateListView(filterDateList);
    }

    }


    @Override
    public void onAttach(Activity activity) {
        Log.e("Contact", "onAttach");
        super.onAttach(activity);
        try {
            mListener = (OnContactFragmentListener) activity;


        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        Log.e("Contact", "onDetach");
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.e("Contact", "onActivityCreated1");


        materialRefreshLayout = (MaterialRefreshLayout) getActivity().findViewById(R.id.refresh_contact2);
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

    //private function

    /*
     * 下拉或上拉相关函数
     */
    public   void  reloadDataAndRefreshView() {

        Handler mainHandler = new Handler(Looper.getMainLooper());
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                SourceDateList = filledData();
                Collections.sort(SourceDateList, pinyinComparator);
                adapter = new SortAdapter(getActivity(), SourceDateList);
                sortListView.setAdapter(adapter);
                materialRefreshLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        materialRefreshLayout.finishRefresh();
                        materialRefreshLayout.finishRefreshLoadMore();
                    }
                }, 100);
            }
        });

        m_numLab.setVisibility(View.INVISIBLE);
//        Map map = new HashMap();
//        map.put("owner", LoginUserUtil.getTel(getActivity()).toString());
//        HttpManager.getInstance(getActivity()).getCustomerOrdersList("/contact/getOrderRepairList", map,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//
//                        JSONArray _arr = response.optJSONArray("ret");
//
//                        int _count = 0;
//                        if (response.optInt("code") == 1) {
//                            for (int i = 0; i < _arr.length(); i++) {
//                                ContactOrderInfo con = new ContactOrderInfo();
//                                JSONObject obj = _arr.optJSONObject(i);
//                                con.openid = obj.optString("openid");
//                                con.time = obj.optString("time");
//                                con.info = obj.optString("info");
//                                con.state = obj.optString("state");
//                                if (con.state.equals("0")) {
//                                    _count++;
//                                }
//                            }
//                        }
//                        m_numLab.setVisibility(_count == 0 ? View.INVISIBLE : View.VISIBLE);
//                        m_numLab.setText(String.valueOf(_count));
//                    }
//                },
//
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        m_numLab.setVisibility(View.INVISIBLE);
//                    }
//                });

    }


}
