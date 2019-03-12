package com.points.autorepar.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.points.autorepar.MainApplication;
import com.points.autorepar.R;
import com.points.autorepar.activity.BaseActivity;
import com.points.autorepar.bean.CompanyItemInfo;
import com.points.autorepar.bean.Contact;
import com.points.autorepar.bean.RepairHistory;
import com.points.autorepar.lib.sortlistview.SortAdapter;
import com.points.autorepar.sql.DBService;

import java.util.List;

/**
 * Created by points on 16/12/6.
 */


public class CompanyListAdapter extends BaseAdapter {

    private Context m_context;
    private LayoutInflater m_LInflater;
    private List m_arrData;
    public CompanyListAdapter(Context context, List list) {

        this.m_context   = context;
        this.m_LInflater = LayoutInflater.from(context);
        this.m_arrData   = list;
    }


    @Override
    public int getCount(){
        return m_arrData != null ? m_arrData.size() : 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder = null ;

        CompanyItemInfo con = (CompanyItemInfo)m_arrData.get(position);
        if(viewHolder==null) {
            viewHolder = new ViewHolder();
            convertView = this.m_LInflater.inflate(R.layout.companyitem, null);
            viewHolder.title = (TextView) convertView.findViewById(R.id.title);
            viewHolder.name = (TextView) convertView.findViewById(R.id.name);
            viewHolder.tel = (TextView) convertView.findViewById(R.id.tel);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }


        CompanyItemInfo info = (CompanyItemInfo)getItem(position);
        viewHolder.title.setText(info.suppliercompanyname);
        viewHolder.name.setText(info.managername);
        viewHolder.tel.setText(info.tel);

        return convertView;
    }
    @Override
    public long getItemId(int position){
        return position;
    }

    @Override
    public  Object getItem(int position){
        CompanyItemInfo con = (CompanyItemInfo) m_arrData.get(position);
        return con;
    }


    private class ViewHolder {

        TextView title;
        TextView name;
        TextView tel;
    }

}
