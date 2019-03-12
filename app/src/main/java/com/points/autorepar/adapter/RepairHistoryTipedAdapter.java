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
import com.points.autorepar.R;
import com.points.autorepar.activity.BaseActivity;
import com.points.autorepar.bean.Contact;
import com.points.autorepar.bean.RepairHistory;
import com.points.autorepar.MainApplication;
import com.points.autorepar.sql.DBService;

import java.util.List;

/**
 * Created by points on 16/12/6.
 */


public class RepairHistoryTipedAdapter extends BaseAdapter {

    private Context m_context;
    private LayoutInflater m_LInflater;
    private List m_arrData;
    public RepairHistoryTipedAdapter(Context context, List list) {

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

        ViewHolder holder = null ;

        RepairHistory con = (RepairHistory)m_arrData.get(position);
        if(convertView==null){
            convertView = this.m_LInflater.inflate(R.layout.repairhistorycelllayout, null);
            holder = new ViewHolder();
            holder.mHead = ((ImageView) convertView.findViewById(R.id.id_repair_tiped_list_cell_headurl));
            holder.mName = ((TextView) convertView.findViewById(R.id.id_repair_tiped_list_cell_name));
            holder.mIsWatingInShop = ((TextView) convertView.findViewById(R.id.id_repair_tiped_list_cell_iswaitinginshop));
            holder.mEnterTime = ((TextView) convertView.findViewById(R.id.id_repair_tiped_list_cell_entertime));
            holder.mCarInfo = ((TextView) convertView.findViewById(R.id.id_repair_tiped_list_cell_carinfo));
            holder.mState = ((TextView) convertView.findViewById(R.id.id_repair_tiped_list_cell_state));
            holder.mRepairType = ((TextView) convertView.findViewById(R.id.id_repair_tiped_list_cell_repairtype));
            holder.mTotalPrice = ((TextView) convertView.findViewById(R.id.id_repair_tiped_list_cell_totalprice));
            holder.mCustomerLeaveMessage = ((TextView) convertView.findViewById(R.id.id_repair_tiped_list_cell_customleavemessage));
            holder.mIndex = ((TextView) convertView.findViewById(R.id.id_repair_tiped_list_cell_index));
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder)convertView.getTag();
        }

        Contact customer = DBService.queryContact(con.carCode);
        String url = "";
        BaseActivity activity = (BaseActivity) this.m_context;
        if(customer != null){
            url = MainApplication.consts(this.m_context).BOS_SERVER+customer.getHeadurl();

        }


        final ViewHolder _holder = holder;
        holder.mHead.setImageResource(R.drawable.appicon);
        activity.imageLoader.get(url, new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {
                _holder.mHead.setImageBitmap(imageContainer.getBitmap());
            }
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                _holder.mHead.setImageResource(R.drawable.appicon);
            }
        },200,200);
        if(customer !=null){
            holder.mName.setText(customer.getName());
            holder.mCarInfo.setText(customer.getCarCode()+" "+customer.getCarType());
        }
        holder.mEnterTime.setText(con.entershoptime);
        holder.mState.setText("已到期");
        holder.mState.setBackgroundColor(activity.getResources().getColor(R.color.mdtp_light_gray));
        holder.mIsWatingInShop.setText("");
        holder.mRepairType.setText("服务项目:"+con.repairType);
        holder.mTotalPrice.setText("¥ "+con.totalPrice);
        holder.mEnterTime.setText("提车时间:    "+con.wantedcompletedtime);
        holder.mCustomerLeaveMessage.setText("到期时间:    "+con.tipCircle);
        holder.mIndex.setText(String.valueOf(position+1));
        return convertView;
    }
    @Override
    public long getItemId(int position){
        return position;
    }

    @Override
    public  Object getItem(int position){
        RepairHistory con = (RepairHistory) m_arrData.get(position);
        return con;
    }


    private class ViewHolder {
        ImageView mHead;
        TextView mName;
        TextView mIsWatingInShop;
        TextView mCarInfo;
        TextView mState;
        TextView mRepairType;
        TextView mTotalPrice;
        TextView mEnterTime;
        TextView mCustomerLeaveMessage;
        TextView mIndex;
    }

}
