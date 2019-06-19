package com.points.autorepar.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.points.autorepar.R;
import com.points.autorepar.activity.workroom.WorkRoomEditActivity;
import com.points.autorepar.adapter.WorkRoomCarInfoAdapter;
import com.points.autorepar.adapter.WorkRoomRepairItemsAdapter;
import com.points.autorepar.bean.RepairHistory;
import com.points.autorepar.bean.WorkRoomPicBackEvent;
import com.points.autorepar.bean.WorkRoomPicEvent;

import de.greenrobot.event.EventBus;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link WorkRoomCarInfoFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link WorkRoomCarInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WorkRoomCarInfoFragment extends Fragment {


    private RepairHistory                    m_currentData;

    private OnWorkRoomCarInfoFragmentInteractionListener   mListener;

    private ListView                         m_listView;
    private WorkRoomCarInfoAdapter           m_adapter;

    private  String                          TAG  = "WorkRoomCarInfoFragment";
    private   WorkRoomEditActivity                m_activityer;

    public interface OnWorkRoomCarInfoFragmentInteractionListener {
        // TODO: Update argument type and name
        void onOnWorkRoomCarInfoFragmentInteraction(Uri uri);
    }

    public WorkRoomCarInfoFragment() {
        // Required empty public constructor
    }

    public static WorkRoomCarInfoFragment newInstance(WorkRoomEditActivity activityer, RepairHistory rep) {
        WorkRoomCarInfoFragment fragment = new WorkRoomCarInfoFragment();
        fragment.m_activityer = activityer;
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
            m_adapter = new WorkRoomCarInfoAdapter(m_activityer,m_currentData);
        }


        if (!EventBus.getDefault().isRegistered(this)) {

            EventBus.getDefault().register(this);

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        m_adapter.unRegisterBus();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_work_room_car_info, container, false);
        m_listView = (ListView) view.findViewById(R.id.id_workroom_carinfo_list);
        m_listView.setAdapter(m_adapter);

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onOnWorkRoomCarInfoFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnWorkRoomCarInfoFragmentInteractionListener) {
            mListener = (OnWorkRoomCarInfoFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
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
        reloadDataAndRefreshView();
    }


    private  void reloadDataAndRefreshView(){
        if(m_currentData != null){
            if(m_adapter != null){
                m_adapter.m_data = m_currentData;
                m_adapter.notifyDataSetChanged();
            }
        }
    }

    public RepairHistory getCurrentRepair(){
        if(m_adapter == null){
            return null;
        }
        return m_adapter.m_data;
    }
    public void onEventMainThread(WorkRoomPicBackEvent event) {

        new Handler().postDelayed(new Runnable(){
            public void run() {
                if(m_listView !=null)
                {
                    m_listView.setSelection(19);
                }
            }
        }, 100);



    }


}
