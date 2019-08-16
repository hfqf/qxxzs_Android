package com.points.autorepar.adapter;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.points.autorepar.R;
import com.points.autorepar.activity.workroom.WorkRoomEditActivity;
import com.points.autorepar.bean.Contact;
import com.points.autorepar.bean.RepairHistory;
import com.points.autorepar.sql.DBService;

import java.util.ArrayList;
import java.util.List;


import com.points.autorepar.fragment.WorkRoomCarInfoFragment;
import com.points.autorepar.fragment.WorkRoomRepairItemsFragment;
/**
 * Created by points on 16/12/6.
 */


public class WorkRoomEditInfoFragmentPagerAdapter extends FragmentPagerAdapter {

    private FragmentManager fragmetnmanager;  //创建FragmentManager
    private List<Fragment> listfragment; //创建一个List<Fragment>

    WorkRoomCarInfoFragment fragment1;
    WorkRoomRepairItemsFragment fragment2;

    public WorkRoomEditInfoFragmentPagerAdapter(WorkRoomEditActivity activityer, FragmentManager fm, RepairHistory rep) {
        super(fm);
        this.fragmetnmanager=fm;
        listfragment=new ArrayList<Fragment>();
        fragment1 =  WorkRoomCarInfoFragment.newInstance(rep);
        fragment2 =  WorkRoomRepairItemsFragment.newInstance(rep);
        listfragment.add(fragment1);
        listfragment.add(fragment2);
        this.listfragment=listfragment;
    }

    @Override
    public Fragment getItem(int arg0) {
        // TODO Auto-generated method stub
        return listfragment.get(arg0); //返回第几个fragment
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return listfragment.size(); //总共有多少个fragment
    }


    public RepairHistory getCurrentRepariData(){

        RepairHistory _data1 = fragment1.getCurrentRepair();
        if(_data1 == null){
            return null;
        }
        RepairHistory _data2 = fragment2.getCurrentRepair();
         _data1.arrRepairItems = _data2.arrRepairItems;
         return _data1;
    }


}
