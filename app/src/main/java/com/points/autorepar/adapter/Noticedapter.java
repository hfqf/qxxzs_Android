package com.points.autorepar.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.points.autorepar.MainApplication;
import com.points.autorepar.R;
import com.points.autorepar.activity.BaseActivity;
import com.points.autorepar.activity.contact.ContactInfoEditActivity;
import com.points.autorepar.activity.repair.NoticeActivity;
import com.points.autorepar.activity.repair.RemindActivity;
import com.points.autorepar.activity.workroom.WorkRoomEditActivity;
import com.points.autorepar.bean.Contact;
import com.points.autorepar.bean.RemindInfo;
import com.points.autorepar.bean.RepairHistory;
import com.points.autorepar.lib.sortlistview.SortAdapter;
import com.points.autorepar.lib.wheelview.WheelView;
import com.points.autorepar.sql.DBService;

import java.util.ArrayList;
import java.util.Arrays;


/**
 * Created by necer on 2017/6/7.
 */

public class Noticedapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private Context context;
    private NoticeActivity m_activity;
    //    private
    private ArrayList<RepairHistory> m_data1;
    private ArrayList<Contact> m_data2;
    private ArrayList<Contact> m_data3;

    private int  m_data1_showType;
    private int  m_data2_showType;
    private int  m_data3_showType;
    public Noticedapter(Context context,NoticeActivity activity) {
        this.context = context;
        this.m_activity = activity;
        m_data1_showType =0;
        m_data2_showType =0;
        m_data3_showType =0;
    }



    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == 0)
        {
            return new Noticedapter.MyViewTopHolder(LayoutInflater.from(context).inflate(R.layout.activity_remind_itemtop, parent, false));
        }else if(viewType ==1) {
            return new Noticedapter.MyViewHolder1(LayoutInflater.from(context).inflate(R.layout.repairhistorynopaycelllayout, parent, false));
        }else if(viewType == 2 || viewType==3){
            return new Noticedapter.MyViewHolder2(LayoutInflater.from(context).inflate(R.layout.item, parent, false));

        }else{
            return new Noticedapter.MyViewTopHolder(LayoutInflater.from(context).inflate(R.layout.activity_remind_itemtop, parent, false));
        }
    }
//
//    @Override
//    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
//
//    }


    public void setData1(ArrayList<RepairHistory> data)
    {
        this.m_data1 = data;
    }
    public void setData2(ArrayList<Contact> data)
    {
        this.m_data2 = data;
    }

    public void setData3(ArrayList<Contact> data)
    {
        this.m_data3 = data;
    }

    @Override
    public int getItemViewType(int position) {
        int listnum = 0;
        if(this.m_data1==null)
        {
            listnum =0;
        }else{
            if(m_data1_showType == 0) {
                listnum = this.m_data1.size();
            }else{
                listnum =0;
            }
        }
        int listnum2 = 0;
        if(this.m_data2==null)
        {
            listnum2 =0;
        }else{

            if(m_data2_showType == 0) {
                listnum2 = this.m_data2.size();
            }else{
                listnum2 =0;
            }
        }
        int listnum3 = 0;
        if(this.m_data3==null)
        {
            listnum3 =0;
        }else{
            if(m_data3_showType == 0) {
                listnum3 = this.m_data3.size();
            }else{
                listnum3 =0;
            }
        }
        if(position == 0 || position ==( listnum+1) || position == (listnum+listnum2+2))
        {
            return 0;
        }else if(position<=listnum+1)
        {
            return 1;
        }else if(position <= (listnum+listnum2+2))
        {
            return 2;
        }else if(position <= (listnum+listnum2+listnum3+3))
        {
            return 3;
        }else{
            return 0;
        }


    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder hd, final int position) {
        int listnum = 0;
        int listnum_tmp = 0;
        int listnum_tmp2 = 0;
        int listnum_tmp3 = 0;
        if(this.m_data1==null)
        {
            listnum =0;
        }else{
            if(m_data1_showType == 0) {
                listnum = this.m_data1.size();
            }else{
                listnum =0;
            }
            listnum_tmp = this.m_data1.size();
        }
        int listnum2 = 0;
        if(this.m_data2==null)
        {
            listnum2 =0;
        }else{

            if(m_data2_showType == 0) {
                listnum2 = this.m_data2.size();
            }else{
                listnum2 =0;
            }
            listnum_tmp2 = this.m_data2.size();
        }
        int listnum3 = 0;
        if(this.m_data3==null)
        {
            listnum3 =0;
        }else{
            if(m_data3_showType == 0) {
                listnum3 = this.m_data3.size();
            }else{
                listnum3 =0;
            }
            listnum_tmp3 = this.m_data3.size();
        }


        if(position == 0 || position ==( listnum+1) || position == (listnum+listnum2+2))
        {
            MyViewTopHolder holder = (MyViewTopHolder) hd;
            if(position== 0) {
                holder.title.setText( "到期工单(" + listnum_tmp + ")");
                holder.lin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(m_data1_showType == 1)
                        {
                            m_data1_showType = 0;
                        }else{
                            m_data1_showType = 1;
                        }
                        notifyDataSetChanged();

                    }
                });
            }else if(position ==( listnum+1)){

                holder.title.setText( "到期年审(" + listnum_tmp2 + ")");
                holder.lin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(m_data2_showType == 1)
                        {
                            m_data2_showType = 0;
                        }else{
                            m_data2_showType = 1;
                        }
                        notifyDataSetChanged();

                    }
                });
            }else if(position ==( listnum+listnum2+2)){

                holder.title.setText( "到期保险(" + listnum_tmp3 + ")");
                holder.lin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(m_data3_showType == 1)
                        {
                            m_data3_showType = 0;
                        }else{
                            m_data3_showType = 1;
                        }
                        notifyDataSetChanged();

                    }
                });
            }
        }else if(position<=listnum+1)
        {
            MyViewHolder1 holder = (MyViewHolder1)hd;
            RepairHistory con = (RepairHistory)this.m_data1.get(position-1);

            final RepairHistory _con = con;
            holder.id_repair_tiped_list_cell.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(m_activity, WorkRoomEditActivity.class);
                    intent.putExtra(String.valueOf(R.string.key_repair_edit_para), _con);
                    m_activity.startActivityForResult(intent, 1);

                }
            });
            Contact customer = DBService.queryContact(con.carCode);
            String url = "";
            BaseActivity activity = (BaseActivity) this.context;
            if(customer != null){
                url = MainApplication.consts(this.context).BOS_SERVER+customer.getHeadurl();

            }

            final MyViewHolder1 _holder = holder;
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


                if("1".equalsIgnoreCase(con.payType)) {
                    holder.mPayStat.setText("会员卡");
                }else{
                    holder.mPayStat.setText("线下支付");
                }
                if("".equalsIgnoreCase(con.ownnum) || "null".equalsIgnoreCase(con.ownnum) || "0".equalsIgnoreCase(con.ownnum)) {
                    holder.mTotalPrice.setVisibility(View.GONE);
                }else{
                    holder.mTotalPrice.setVisibility(View.VISIBLE);
                    holder.mTotalPrice.setText("欠：" + con.ownnum);
                }
            if (con.state.equals("0")) {
                holder.mState.setText("维修中");
                holder.mState.setBackgroundColor(activity.getResources().getColor(R.color.material_light_blue));
            } else if (con.state.equals("1")) {
                holder.mState.setText("已修完");
                holder.mState.setBackgroundColor(activity.getResources().getColor(R.color.material_blue));
            } else if (con.state.equals("2")) {
                holder.mState.setText("已提车");
                holder.mState.setBackgroundColor(activity.getResources().getColor(R.color.material_green));
            } else if (con.state.equals("3")) {
                holder.mState.setText("已取消");
                holder.mState.setBackgroundColor(activity.getResources().getColor(R.color.material_green));
            }
                holder.mTotal.setText("总："+con.totalPrice);



            holder.mIsWatingInShop.setText(con.iswatiinginshop.equals("0") ? "不在店等":"在店等");
            holder.mRepairType.setText("服务项目:"+con.repairType);
//        holder.mTotalPrice.setText("¥ "+con.totalPrice);
            holder.mEnterTime.setText("进入门店时间:    "+con.entershoptime);
            holder.mCustomerLeaveMessage.setText((con.state.equals("2") ?"实际提车时间:    ":"预计提车时间:    ")+con.wantedcompletedtime);
            holder.mIndex.setText(String.valueOf(position+1));
            holder.mIndex.setVisibility(View.INVISIBLE);

        }
        else if(position <= (listnum+listnum2+2))
        {
            MyViewHolder2 viewHolder = (MyViewHolder2)hd;
            Contact con =  this.m_data2.get(position-listnum-2);
            viewHolder.tvLetter.setVisibility(View.GONE);
            viewHolder.m_tel.setText(con.getTel());
            viewHolder.m_carType.setText(con.getCarType());
            viewHolder.m_isBind.setText("");
            viewHolder.m_carCode.setText(con.getCarCode());
            final MyViewHolder2 _viewHolder = viewHolder;
            final String url = MainApplication.consts(this.context).BOS_SERVER+con.getHeadurl();
            final BaseActivity activity = (BaseActivity) this.context;

            Handler mainHandler = new Handler(Looper.getMainLooper());
            mainHandler.post(new Runnable() {
                @Override
                public void run() {

                    activity.imageLoader.get(url, new ImageLoader.ImageListener() {
                        @Override
                        public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {
                            _viewHolder.m_head.setImageBitmap(imageContainer.getBitmap());
                        }
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            _viewHolder.m_head.setImageResource(R.drawable.appicon);
                        }
                    },200,200);
                }
            });
        }

        else if(position <= (listnum+listnum2+listnum3+3))
        {
            MyViewHolder2 viewHolder = (MyViewHolder2)hd;
            Contact con =  this.m_data3.get(position-listnum-listnum2-3);
            viewHolder.tvLetter.setVisibility(View.GONE);
            viewHolder.m_tel.setText(con.getTel());
            viewHolder.m_carType.setText(con.getCarType());
            viewHolder.m_isBind.setText("");
            viewHolder.m_carCode.setText(con.getCarCode());

            final MyViewHolder2 _viewHolder = viewHolder;
            final String url = MainApplication.consts(this.context).BOS_SERVER+con.getHeadurl();
            final BaseActivity activity = (BaseActivity) this.context;

            Handler mainHandler = new Handler(Looper.getMainLooper());
            mainHandler.post(new Runnable() {
                @Override
                public void run() {

                    activity.imageLoader.get(url, new ImageLoader.ImageListener() {
                        @Override
                        public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {
                            _viewHolder.m_head.setImageBitmap(imageContainer.getBitmap());
                        }
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            _viewHolder.m_head.setImageResource(R.drawable.appicon);
                        }
                    },200,200);
                }
            });

        }else{

        }


    }

    @Override
    public int getItemCount() {
        int count =0;
        int listnum = 0;
        if(this.m_data1==null)
        {
            listnum =0;
        }else{
            if(m_data1_showType == 0) {
                listnum = this.m_data1.size();
            }else{
                listnum =0;
            }
        }
        int listnum2 = 0;
        if(this.m_data2==null)
        {
            listnum2 =0;
        }else{

            if(m_data2_showType == 0) {
                listnum2 = this.m_data2.size();
            }else{
                listnum2 =0;
            }
        }
        int listnum3 = 0;
        if(this.m_data3==null)
        {
            listnum3 =0;
        }else{
            if(m_data3_showType == 0) {
                listnum3 = this.m_data3.size();
            }else{
                listnum3 =0;
            }
        }
        count = listnum+listnum2+listnum3+3;

        return count;
    }
    class MyViewTopHolder extends RecyclerView.ViewHolder{
        TextView title;
        LinearLayout lin;
        public MyViewTopHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.name);
            lin = (LinearLayout) itemView.findViewById(R.id.lin);
        }
    }
    class MyViewHolder1 extends RecyclerView.ViewHolder {
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
        TextView mPayStat;
        TextView mTotal;
        LinearLayout id_repair_tiped_list_cell;
        public MyViewHolder1(View convertView) {
            super(convertView);
            id_repair_tiped_list_cell = (LinearLayout)convertView.findViewById(R.id.id_repair_tiped_list_cell);
            mHead = ((ImageView) convertView.findViewById(R.id.id_repair_tiped_list_cell_headurl));
            mName = ((TextView) convertView.findViewById(R.id.id_repair_tiped_list_cell_name));
            mIsWatingInShop = ((TextView) convertView.findViewById(R.id.id_repair_tiped_list_cell_iswaitinginshop));
            mEnterTime = ((TextView) convertView.findViewById(R.id.id_repair_tiped_list_cell_entertime));
            mCarInfo = ((TextView) convertView.findViewById(R.id.id_repair_tiped_list_cell_carinfo));
            mState = ((TextView) convertView.findViewById(R.id.id_repair_tiped_list_cell_state));
            mRepairType = ((TextView) convertView.findViewById(R.id.id_repair_tiped_list_cell_repairtype));
            mTotalPrice = ((TextView) convertView.findViewById(R.id.id_repair_tiped_list_cell_totalprice));
            mCustomerLeaveMessage = ((TextView) convertView.findViewById(R.id.id_repair_tiped_list_cell_customleavemessage));
            mIndex = ((TextView) convertView.findViewById(R.id.id_repair_tiped_list_cell_index));
            mPayStat = ((TextView) convertView.findViewById(R.id.paystat));
            mTotal = ((TextView) convertView.findViewById(R.id.payTotal));
        }
    }
    class MyViewHolder2 extends RecyclerView.ViewHolder {
        TextView tvLetter;
        TextView tvTitle;
        ImageView m_head;
        TextView  m_tel;
        TextView  m_carType;
        TextView  m_carCode;
        TextView m_isBind;

        public MyViewHolder2(View view) {
            super(view);
            tvTitle = (TextView) view.findViewById(R.id.title);
            tvLetter = (TextView) view.findViewById(R.id.catalog);
            m_head = (ImageView) view.findViewById(R.id.contact_list_cell_show_content_head);
            m_tel = (TextView) view.findViewById(R.id.contact_list_cell_show_content_tel);
            m_carType = (TextView) view.findViewById(R.id.contact_list_cell_show_content_cartype);
            m_carCode = (TextView) view.findViewById(R.id.contact_list_cell_show_carcode);
            m_isBind = (TextView) view.findViewById(R.id.contact_list_cell_show_content_isbind);
        }
    }

    final static class ViewHolder {

    }

}




