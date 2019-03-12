package com.points.autorepar.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.points.autorepar.R;
import com.points.autorepar.bean.ADTServiceInfo;
import com.points.autorepar.bean.ADTServiceItemInfo;

import java.util.ArrayList;
import java.util.List;

public class SelectServiceHomeExpandableAdapter extends BaseExpandableListAdapter {

    private List<String> groupArray;
    private List<List<String>> childArray;

    ArrayList<ADTServiceInfo> arrData;
    private Context mContext;

    public ServiceHomeExpandableAdapterListen listener;

    public interface ServiceHomeExpandableAdapterListen{
        void onSelectedGroup(ADTServiceInfo serviceInfo);
    }

    public SelectServiceHomeExpandableAdapter(Context context, ArrayList<ADTServiceInfo> arr){
        mContext = context;
        this.arrData = arr;
    }

    @Override
    public int getGroupCount() {
        return arrData.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        ADTServiceInfo serviceInfo =  arrData.get(groupPosition);
        return serviceInfo.arrSubTypes.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        ADTServiceInfo serviceInfo =  arrData.get(groupPosition);

        return serviceInfo;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        ADTServiceInfo serviceInfo =  arrData.get(groupPosition);
        return serviceInfo.arrSubTypes.get(childPosition);
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





        ADTServiceInfo serviceInfo =  arrData.get(groupPosition);

        holder.groupName.setText(serviceInfo.name+"("+serviceInfo.arrSubTypes.size()+")");

        return view;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        View view = convertView;
        ChildHolder holder = null;
        if(view == null){
            holder = new ChildHolder();
            view = LayoutInflater.from(mContext).inflate(R.layout.activity_select_service_home_item_cell, null);
            holder.childName = (TextView)view.findViewById(R.id.tv_child_name);
            holder.add_bt = (TextView)view.findViewById(R.id.bt_add);
            holder.min_bt = (TextView)view.findViewById(R.id.bt_min);
            holder.input_val =  (TextView)view.findViewById(R.id.input_val);
            view.setTag(holder);
        }else{
            holder = (ChildHolder)view.getTag();
        }

//        if(childPosition == 0){
//            holder.divider.setVisibility(View.GONE);
//        }



        ADTServiceInfo serviceInfo =  arrData.get(groupPosition);
        ADTServiceItemInfo itemInfo = serviceInfo.arrSubTypes.get(childPosition);
        holder.childName.setText(itemInfo.name+"(服务:¥"+itemInfo.price+" 工时:¥"+itemInfo.workHourPay+")");
        holder.input_val.setText(itemInfo.num);


        holder.add_bt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ADTServiceInfo serviceInfo =  arrData.get(groupPosition);
                ADTServiceItemInfo itemInfo = serviceInfo.arrSubTypes.get(childPosition);
                // TODO Auto-generated method stub
                int curNum = Integer
                        .parseInt((String) itemInfo.num)+1;
                if(curNum > 999)
                {
                    curNum = 999;
                }
                arrData.get(groupPosition).arrSubTypes.get(childPosition).num = curNum+"";
                notifyDataSetChanged();

            }
        });

        holder.min_bt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                ADTServiceInfo serviceInfo =  arrData.get(groupPosition);
                ADTServiceItemInfo itemInfo = serviceInfo.arrSubTypes.get(childPosition);
                // TODO Auto-generated method stub
                int curNum = Integer
                        .parseInt((String) itemInfo.num)-1;
                if(curNum < 0)
                {
                    curNum = 0;
                }

                arrData.get(groupPosition).arrSubTypes.get(childPosition).num = curNum+"";
                notifyDataSetChanged();

            }
        });
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
        public TextView input_val;
        public TextView add_bt;
        public TextView min_bt;
//        public ImageView sound;
//        public ImageView divider;
    }
}