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
import com.points.autorepar.MainApplication;
import com.points.autorepar.sql.DBService;
import com.points.autorepar.bean.Contact;
import com.points.autorepar.bean.RepairHistory;
import com.points.autorepar.utils.SpeImageLoader.SpeImageLoaderUtil;

import java.util.List;

/**
 * Created by points on 16/12/6.
 */


public class RepairHistoryAdapter extends BaseAdapter {

    private Context m_context;
    private LayoutInflater m_LInflater;
    private List m_arrData;
    private int status;
    public  RepairHistoryAdapter(Context context,List list,int status) {

        this.m_context   = context;
        this.m_LInflater = LayoutInflater.from(context);
        this.m_arrData   = list;
        this.status = status;
    }


    @Override
    public int getCount(){
        return m_arrData != null ? m_arrData.size() : 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null ;

        RepairHistory con = (RepairHistory)m_arrData.get(position);
        if(holder==null){
            convertView = this.m_LInflater.inflate(R.layout.repairhistorynopaycelllayout, null);
            holder = new ViewHolder();
            holder.mHead = ((ImageView) convertView.findViewById(R.id.id_repair_tiped_list_cell_headurl));
            holder.mName = ((TextView) convertView.findViewById(R.id.id_repair_tiped_list_cell_name));
            holder.mIsWatingInShop = ((TextView) convertView.findViewById(R.id.id_repair_tiped_list_cell_iswaitinginshop));
            holder.mEnterTime = ((TextView) convertView.findViewById(R.id.id_repair_tiped_list_cell_entertime));
            holder.mCarInfo = ((TextView) convertView.findViewById(R.id.id_repair_tiped_list_cell_carinfo));
            holder.mState = ((TextView) convertView.findViewById(R.id.id_repair_tiped_list_cell_state));
            holder.mRepairType = ((TextView) convertView.findViewById(R.id.id_repair_tiped_list_cell_repairtype));
            holder.mTotalPrice = ((TextView) convertView.findViewById(R.id.id_repair_tiped_list_cell_totalprice));
            holder.mTotalPrice1 = ((TextView) convertView.findViewById(R.id.id_repair_tiped_list_cell_totalprice1));
            holder.mCustomerLeaveMessage = ((TextView) convertView.findViewById(R.id.id_repair_tiped_list_cell_customleavemessage));
            holder.mIndex = ((TextView) convertView.findViewById(R.id.id_repair_tiped_list_cell_index));
            convertView.setTag(holder);
            holder.mPayStat = ((TextView) convertView.findViewById(R.id.paystat));
            holder.mTotal = ((TextView) convertView.findViewById(R.id.payTotal));
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
        SpeImageLoaderUtil.loadImage(this.m_context,_holder.mHead,url,R.drawable.appicon,R.drawable.appicon);
        if(customer !=null){
            holder.mName.setText(customer.getName());
            holder.mCarInfo.setText(customer.getCarCode()+" "+customer.getCarType());
        }
        holder.mEnterTime.setText(con.entershoptime);


           // 0线下(现金) 1会员卡支付 2微信 3支付宝
            if(con.payType == null){
                holder.mPayStat.setText("现金");
            }else {
                if("0".equalsIgnoreCase(con.payType)) {
                    holder.mPayStat.setText("现金");
                }else if("1".equalsIgnoreCase(con.payType)){
                    holder.mPayStat.setText("会员卡支付");
                }else if("2".equalsIgnoreCase(con.payType)){
                    holder.mPayStat.setText("微信支付");
                }else if("3".equalsIgnoreCase(con.payType)){
                    holder.mPayStat.setText("支付宝支付");
                }
            }

            if("".equalsIgnoreCase(con.saleMoney)||"null".equalsIgnoreCase(con.saleMoney)||"0".equalsIgnoreCase(con.saleMoney))
            {
                holder.mTotalPrice1.setVisibility(View.GONE);

            }else{
                holder.mTotalPrice1.setVisibility(View.VISIBLE);
                holder.mTotalPrice1.setText("优惠：￥"+con.saleMoney);
            }

            if(con.ownnum == null){
                holder.mTotalPrice.setVisibility(View.GONE);
            }else {
                if(Integer.parseInt(con.ownnum)==0||"".equalsIgnoreCase(con.saleMoney)){
                    holder.mTotalPrice.setVisibility(View.GONE);
                }else {
                    holder.mTotalPrice.setVisibility(View.VISIBLE);
                    holder.mTotalPrice.setText("欠：￥"+con.ownnum);
                }
            }

            int money = Integer.parseInt(con.totalPrice)-Integer.parseInt(con.saleMoney);
            holder.mTotal.setText("实 ￥："+money);


        holder.mIsWatingInShop.setText(con.iswatiinginshop.equals("0") ? "不在店等":"在店等");
        holder.mRepairType.setText("服务项目:"+con.repairType);
//        holder.mTotalPrice.setText("¥ "+con.totalPrice);
        holder.mEnterTime.setText("进入门店时间:    "+con.entershoptime);
        holder.mCustomerLeaveMessage.setText((con.state.equals("2") ?"实际提车时间:    ":"预计提车时间:    ")+con.wantedcompletedtime);
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
        TextView mTotalPrice1;
        TextView mEnterTime;
        TextView mCustomerLeaveMessage;
        TextView mIndex;
        TextView mPayStat;
        TextView mTotal;
    }

}
