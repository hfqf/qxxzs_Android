package com.points.autorepar.fragment;

import android.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.points.autorepar.R;
import com.points.autorepar.activity.workroom.WorkRoomEditActivity;
import com.points.autorepar.adapter.WorkRoomCarInfoAdapter;
import com.points.autorepar.adapter.WorkRoomRepairItemsAdapter;
import com.points.autorepar.bean.RefEvent;
import com.points.autorepar.bean.RepairHistory;
import com.points.autorepar.bean.WorkRoomEvent;
import java.util.List;

import de.greenrobot.event.EventBus;


public class WorkRoomRepairItemsFragment extends Fragment {

    private RepairHistory m_currentData;

    private OnWorkRoomRepairItemsFragmentInteractionListener mListener;

    private ListView     m_listView;
    private WorkRoomRepairItemsAdapter  m_adapter;
    private  String                          TAG  = "WorkRoomRepairItemsFragment";
    private   WorkRoomEditActivity      m_activityeer;

    public interface OnWorkRoomRepairItemsFragmentInteractionListener {
        void onWorkRoomRepairItemsFragmentInteraction(Uri uri);
    }

    public WorkRoomRepairItemsFragment() {
        // Required empty public constructor
    }


    public static WorkRoomRepairItemsFragment newInstance(WorkRoomEditActivity activityer, RepairHistory rep) {
        WorkRoomRepairItemsFragment fragment = new WorkRoomRepairItemsFragment();
        fragment.m_activityeer = activityer;
        Bundle args = new Bundle();
        args.putParcelable("data",rep);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            m_currentData = getArguments().getParcelable("data");
        }
        if (!EventBus.getDefault().isRegistered(this)) {

            EventBus.getDefault().register(this);

        }
    }
    @Override
    public  void  onResume(){
        super.onResume();
        Log.e("Contact", "onResume");
        m_adapter.notifyDataSetChanged();

    }

    @Override
    public void onDestroy() {

        super.onDestroy();
        EventBus.getDefault().unregister(this);


    }


    public void onEventMainThread(WorkRoomEvent event) {
        RepairHistory data = event.getMsg();
        m_currentData = data;


    }

    public void onEventMainThread(RefEvent event) {
        m_adapter.notifyDataSetChanged();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_work_room_repair_items, container, false);
        m_listView = (ListView) view.findViewById(R.id.id_workroom_items_list);

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onWorkRoomRepairItemsFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnWorkRoomRepairItemsFragmentInteractionListener) {
            mListener = (OnWorkRoomRepairItemsFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public  void  onStart() {
        super.onStart();
        Log.e(TAG, "onStart");
        reloadDataAndRefreshView();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    private  void reloadDataAndRefreshView(){
        m_adapter = new WorkRoomRepairItemsAdapter(m_activityeer,m_currentData);
        m_listView.setAdapter(m_adapter);
    }

    public RepairHistory getCurrentRepair(){
        return m_adapter.m_data;
    }
}
