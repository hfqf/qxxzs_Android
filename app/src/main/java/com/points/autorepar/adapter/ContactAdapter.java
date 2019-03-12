package com.points.autorepar.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.points.autorepar.R;
import com.points.autorepar.bean.Contact;

import java.util.List;

/**
 * Created by points on 16/11/28.
 */
public class ContactAdapter extends BaseAdapter {
    private Context         m_context;
    private LayoutInflater  m_LInflater;
    private List            m_arrData;

    public  ContactAdapter(Context context,List list) {

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

        Contact con = (Contact)m_arrData.get(position);
        ViewHolder holder = null ;
        if(convertView==null){
            convertView = this.m_LInflater.inflate(R.layout.contact_cell, null);
            holder = new ViewHolder();
//            holder.mCarCode = ((TextView) convertView.findViewById(R.id.contact_cell_carcode));
//            holder.mName = ((TextView) convertView.findViewById(R.id.contact_cell_name));
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder)convertView.getTag();
        }
        holder.mCarCode.setText(con.getCarCode());
        holder.mName.setText(con.getName());
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
        TextView mTel;
        TextView mCarType;


    }
}
