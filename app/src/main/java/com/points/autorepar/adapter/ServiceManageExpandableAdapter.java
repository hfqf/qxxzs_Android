package com.points.autorepar.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.points.autorepar.R;
import com.points.autorepar.bean.ADTServiceInfo;
import com.points.autorepar.bean.ADTServiceItemInfo;
import com.points.autorepar.bean.ServiceManageInfo;
import com.points.autorepar.bean.ServiceManageSubInfo;

import java.util.ArrayList;
import java.util.List;

public class ServiceManageExpandableAdapter extends BaseExpandableListAdapter {

    private List<String> groupArray;
    private List<List<String>> childArray;

    ArrayList<ServiceManageInfo> arrData;
    private Context mContext;

    public ServiceHomeExpandableAdapterListen listener;

    public interface ServiceHomeExpandableAdapterListen{
        void onSelectedGroup(ServiceManageInfo serviceInfo);
    }

    public ServiceManageExpandableAdapter(Context context, ArrayList<ServiceManageInfo> arr){
        mContext = context;
        this.arrData = arr;
    }

    @Override
    public int getGroupCount() {
        return arrData.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        ServiceManageInfo serviceInfo =  arrData.get(groupPosition);
        return serviceInfo.subtypelist.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        ServiceManageInfo serviceInfo =  arrData.get(groupPosition);

        return serviceInfo;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        ServiceManageInfo serviceInfo =  arrData.get(groupPosition);
        return serviceInfo.subtypelist.get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        View view = convertView;
        GroupHolder holder = null;
        if(view == null){
            holder = new GroupHolder();
            view = LayoutInflater.from(mContext).inflate(R.layout.activity_serice_home_group_cell, null);
            holder.groupName = (TextView)view.findViewById(R.id.tv_group_name);
            holder.arrow = (ImageView) view.findViewById(R.id.iv_arrow);
            view.setTag(holder);
        }else{
            holder = (GroupHolder)view.getTag();
        }

        //判断是否已经打开列表
        if(isExpanded){
            holder.arrow.setBackgroundResource(R.drawable.right_arrow);
        }else{
            holder.arrow.setBackgroundResource(R.drawable.dowm_arrow);
        }





        ServiceManageInfo serviceInfo =  arrData.get(groupPosition);

        holder.groupName.setText(serviceInfo.name+"("+serviceInfo.subtypelist.size()+")");

        return view;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        View view = convertView;
        ChildHolder holder = null;
        if(view == null){
            holder = new ChildHolder();
            view = LayoutInflater.from(mContext).inflate(R.layout.activity_service_home_item_cell, null);
            holder.childName = (TextView)view.findViewById(R.id.tv_child_name);
//            holder.sound = (ImageView)view.findViewById(R.id.iv_sound);
//            holder.divider = (ImageView)view.findViewById(R.id.iv_divider);
            view.setTag(holder);
        }else{
            holder = (ChildHolder)view.getTag();
        }

        if(childPosition == 0){
//            holder.divider.setVisibility(View.GONE);
        }



        ServiceManageInfo serviceInfo =  arrData.get(groupPosition);
        ServiceManageSubInfo itemInfo = serviceInfo.subtypelist.get(childPosition);
//        holder.childName.setText(itemInfo.name+"(服务:¥"+itemInfo.price+" 工时:¥"+itemInfo.workHourPay+")");
        holder.childName.setText(itemInfo.name);
        return view;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    class GroupHolder{
        public TextView groupName;
        public ImageView arrow;
    }

    class ChildHolder{
        public TextView childName;
        public ImageView sound;
        public ImageView divider;
    }
}