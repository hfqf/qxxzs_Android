package com.points.autorepar.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.points.autorepar.R;
import com.points.autorepar.bean.Contact;
import com.points.autorepar.bean.ContactOrderInfo;
import com.points.autorepar.sql.DBService;
import com.points.autorepar.utils.DateUtil;

import java.util.List;

/**
 * Created by points on 16/11/28.
 */
public class ContactOrderListAdapter extends BaseAdapter {
    private Context         m_context;
    private LayoutInflater  m_LInflater;
    private List            m_arrData;

    public ContactOrderListAdapter(Context context, List list) {

        this.m_context   = context;
        this.m_LInflater = LayoutInflater.from(context);
        this.m_arrData   = list;
    }

    @Override
    public int getCount(){
        return m_arrData != null ? m_arrData.size() : 0;
    }

    @Override
    public   View getView(int position, View convertView, ViewGroup parent) {

        ContactOrderInfo order = (ContactOrderInfo)m_arrData.get(position);
        ViewHolder holder = null ;
        if(convertView==null){
            convertView = this.m_LInflater.inflate(R.layout.contact_cell, null);
            holder = new ViewHolder();
            holder.mCarCode = ((TextView) convertView.findViewById(R.id.contact_cell_1));
            holder.mName = ((TextView) convertView.findViewById(R.id.contact_cell_2));
            holder.mLab3 = ((TextView) convertView.findViewById(R.id.contact_cell_3));
            holder.mLab4 = ((TextView) convertView.findViewById(R.id.contact_cell_4));
            holder.mLab5 = ((TextView) convertView.findViewById(R.id.contact_cell_5));
            holder.mLab6 = ((TextView) convertView.findViewById(R.id.contact_cell_6));
            holder.mLab7 = ((TextView) convertView.findViewById(R.id.contact_cell_7));
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder)convertView.getTag();
        }

        Log.e("ContactOrderListAdapter",order.openid);
        Contact con = DBService.queryContact(order.openid);
        if(con != null){
            holder.mCarCode.setText("客户:"+con.getName());
            holder.mName.setText("车牌:"+con.getCarCode());
            holder.mLab6.setText("车型:"+con.getCarType());
            holder.mLab3.setText("预约时间:"+order.time);
            holder.mLab4.setText("预约内容:"+order.info);
            holder.mLab5.setText("提交时间:"+ order.inserttime);
            holder.mLab7.setText(order.state.equals("0") ? "还未处理":"已经处理");
            holder.mLab7.setTextColor(this.m_context.getResources().getColor (order.state.equals("1")  ?  R.color.material_light_blue:R.color.material_red));
        }

        return convertView;
    }
    @Override
    public long getItemId(int position){
        return position;
    }

    @Override
    public  Object getItem(int position){
        Contact con = (Contact) m_arrData.get(position);
        return con;
    }


    private class ViewHolder {

        TextView mCarCode;
        TextView mName;
        TextView mLab3;
        TextView mLab4;
        TextView mLab5;
        TextView mLab6;
        TextView mLab7;


    }
}
