package com.points.autorepar.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bigkoo.pickerview.TimePickerView;
import com.points.autorepar.R;
import com.points.autorepar.activity.BaseActivity;
import com.points.autorepar.activity.ImgDisplayActivity;
import com.points.autorepar.activity.workroom.WorkRoomEditActivity;
import com.points.autorepar.activity.workroom.WorkRoomEditActivityViewModel;
import com.points.autorepar.adapter.WorkRoomCarInfo.WorkRommCarInfoPicsAdapter;
import com.points.autorepar.bean.Contact;
import com.points.autorepar.bean.RepairHistory;
import com.points.autorepar.common.Consts;
import com.points.autorepar.lib.wheelview.WheelView;
import com.points.autorepar.sql.DBService;
import com.points.autorepar.utils.DateUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import ImageUtil.preview.SpeImagePreviewUtil;
import RxJava.RxViewHelper;

/**
 * Created by points on 16/11/28.®
 */
public class WorkRoomCarInfoAdapter extends BaseAdapter {
    private Context         m_context;
    private LayoutInflater  m_LInflater;
    public RepairHistory    m_data;
    private  final  String TAG = "WorkRoomCarInfoAdapter";
    private  WorkRoomEditActivity m_activity;
    public WorkRoomCarInfoAdapter(Context context, RepairHistory rep) {
        this.m_activity = (WorkRoomEditActivity)context;
        this.m_context   = context;
        this.m_LInflater = LayoutInflater.from(context);
        this.m_data   = rep;
    }

    public void unRegisterBus(){

    }

    @Override
    public int getCount(){
        return 7+6+2+2+2+1+2;
    }

    @Override
    public   View getView(int position, View convertView, ViewGroup parent) {
        if(position == 0 || position == 7|| position == 14 || position == 17)  {
            ViewHolderCellTop holder = null ;
                convertView = this.m_LInflater.inflate(R.layout.cell_top_workroom_carinfo, null);
                holder = new ViewHolderCellTop();
                holder.tip = ((TextView) convertView.findViewById(R.id.cell_top_workroom_carinfo_tip));
                convertView.setTag(holder);
            switch (position){
                case 0:{
                    holder.tip.setText("车辆信息");
                    break;
                }
                case 7:{
                    holder.tip.setText("接车事项");
                    break;
                }
                case 14:{
                    holder.tip.setText("检车问题");
                    break;
                }
                case 17:{
                    holder.tip.setText("提醒事项");
                    break;
                }
            }
            return convertView;
        } else if( position ==20) {
            ViewHolderRecycleView holder =  new ViewHolderRecycleView();
            convertView = LayoutInflater.from(m_context).inflate(R.layout.workrommcarinfo_picsrecycleview_layout, null);
            holder.cycView =  convertView.findViewById(R.id.recycler_view);
            holder.uploadImg = (ImageView) convertView.findViewById(R.id.addimg);
            RxViewHelper.clickWith(holder.uploadImg,()->{
                SpeImagePreviewUtil.selectMutiImage((Activity) m_context,15-m_data.getArrCarInfoPics().size());
            });
            holder.cycView.setLayoutManager(new GridLayoutManager(m_context,3));
            WorkRommCarInfoPicsAdapter adapter = new WorkRommCarInfoPicsAdapter(m_context,m_data.getArrCarInfoPics());
            holder.cycView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            return convertView;
        }
        else if( position > 0 && position < 7) {
            ViewHolderCell0 holder = null ;
            convertView = this.m_LInflater.inflate(R.layout.cell_workroom_carinfo_0, null);
            holder = new ViewHolderCell0();
            holder.tip = ((TextView) convertView.findViewById(R.id.cell_workroom_carinfo_0_tip));
            holder.value = ((TextView) convertView.findViewById(R.id.cell_workroom_carinfo_0_content));
            holder.btn = ((ImageButton) convertView.findViewById(R.id.cell_workroom_carinfo_0_btn));
            convertView.setTag(holder);
            Contact contact = DBService.queryContact(m_data.carCode);
            holder.btn.setVisibility(View.INVISIBLE);
            if(contact != null) {
                switch (position) {
                    case 1: {
                        holder.tip.setText("客户");
                        holder.value.setText(contact.getName());
                        break;
                    }
                    case 2: {
                        holder.tip.setText("车牌");
                        holder.value.setText(contact.getCarCode());
                        break;
                    }
                    case 3: {
                        final String tel =  contact.getTel();
                        holder.tip.setText("号码");
                        holder.value.setText(contact.getTel());
                        holder.btn.setVisibility(View.VISIBLE);
                        holder.btn.setBackgroundResource(R.drawable.phone);
                        holder.btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(Intent.ACTION_DIAL,Uri.parse("tel:"+tel));
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                m_activity.startActivity(intent);
                            }
                        });
                        break;
                    }
                    case 4: {
                        holder.tip.setText("车型");
                        holder.value.setText(contact.getCarType());
                        break;
                    }
                    case 5: {
                        holder.tip.setText("车架号");
                        holder.value.setText(contact.getVin() == null ? "暂无" : contact.getVin());
                        break;
                    }
                    case 6: {
                        holder.tip.setText("车辆注册时间");
                        holder.value.setText(contact.getCarregistertime());
                        break;
                    }
                }convertView.setTag(holder);
            }
            return convertView;
        }
        if(position == 11){
            ViewHolderSwiter holder = null ;
            convertView = this.m_LInflater.inflate(R.layout.repair_info_cell_checkbox, null);
            holder = new ViewHolderSwiter();
            holder.tip = ((TextView) convertView.findViewById(R.id.repair_info_cell_tip));
            holder.checkbox = ((CheckBox) convertView.findViewById(R.id.id_repair_add_closetip_checkox));
            convertView.setTag(holder);
            holder.tip.setText("是否在店等");
            if(m_data != null) {
                boolean isClose = m_data.iswatiinginshop == null ? false : m_data.iswatiinginshop.equals("1");
                if(m_data.state!=null) {
                    holder.checkbox.setEnabled(!m_data.state.endsWith("2"));
                }else{
                    holder.checkbox.setEnabled(false);
                }
                holder.checkbox.setChecked(isClose);
                holder.checkbox.setClickable(true);
                holder.checkbox.setFocusableInTouchMode(false);
                holder.checkbox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean isClose = m_data.iswatiinginshop == null ? false : m_data.iswatiinginshop.equals("1");
                        m_data.iswatiinginshop = isClose ? "0" : "1";
                        notifyDataSetChanged();
                    }
                });
            }
            return convertView;
        }else if(position == 20) {
            ViewHolderSwiter holder = null ;
            convertView = this.m_LInflater.inflate(R.layout.repair_info_cell_checkbox, null);
            holder = new ViewHolderSwiter();
            holder.tip = ((TextView) convertView.findViewById(R.id.repair_info_cell_tip));
            holder.checkbox = ((CheckBox) convertView.findViewById(R.id.id_repair_add_closetip_checkox));
            convertView.setTag(holder);
            holder.checkbox.setEnabled(!m_data.state.endsWith("2"));
            boolean isClose = this.m_data.isClose.equals("1");
            holder.checkbox.setChecked(isClose);
            holder.checkbox.setClickable(true);
            holder.checkbox.setFocusableInTouchMode(false);
            holder.checkbox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    m_data.isClose =  m_data.isClose.equals("1") ? "0" : "1";
                    m_data.isreaded =  m_data.isClose.equals("1") ? "0" : "1";
                    notifyDataSetChanged();
                }
            });
            return convertView;
        }
        else if(position < 21 && position != 11)
        {
                ViewHolderCell1 holder = null ;
                convertView = this.m_LInflater.inflate(R.layout.cell_workroom_carinfo_1, null);
                holder = new ViewHolderCell1();
                holder.tip = (TextView) convertView.findViewById(R.id.cell_workroom_carinfo_1_tip);
                holder.value = ((EditText) convertView.findViewById(R.id.cell_workroom_carinfo_1_content));
                holder.value.setSelected(true);
                convertView.setTag(holder);
            switch (position){
                case 8:{
                    holder.tip.setText("入店时间");
                    holder.value.setText(m_data.entershoptime);
                    holder.value.setFocusableInTouchMode(false);
                    RxViewHelper.textChange(holder.value,(s)->{
                        if(s.toString().length() >0) {
                            m_data.inserttime = s.toString();
                        }
                    });
                    holder.value.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            getCurrentTime(0);
                        }
                    });
                    holder.value.setHint("必填");
                    break;
                }
                case 9:{
                    holder.tip.setText("入店里程(KM)");
                    holder.value.setText(m_data.totalKm);
                    holder.value.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
                    RxViewHelper.textChange(holder.value,(s)->{
                        if(s.toString().length() >0) {
                            m_data.totalKm = s.toString();
                        }
                    });
                    if(m_data.totalKm == null||"".equalsIgnoreCase(m_data.totalKm) || "null".equalsIgnoreCase(m_data.totalKm)) {
                        holder.value.setText("0");
                    }
                    break;
                }
                case 10:{
                    holder.tip.setText("入店油量");
                    holder.tip.setEnabled(false);
                    holder.value.setFocusableInTouchMode(false);
                    RxViewHelper.textChange(holder.value,(s)->{
                        if(s.toString().length() >0) {
                            m_data.oilvolume = s.toString();
                        }
                    });
                    if(m_data.oilvolume == null||"".equalsIgnoreCase(m_data.oilvolume) || "null".equalsIgnoreCase(m_data.oilvolume)) {
                        holder.value.setText("");
                    }else{
                        holder.value.setText(m_data.oilvolume);
                    }

                    holder.value.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String[] arr = m_activity.getResources().getStringArray(R.array.oil_circle);
                            View outerView = LayoutInflater.from(m_activity).inflate(R.layout.wheel_view, null);
                            final WheelView wv = (WheelView) outerView.findViewById(R.id.wheel_view_wv);
                            wv.setItems(Arrays.asList(arr));
                            wv.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
                                @Override
                                public void onSelected(int selectedIndex, String item) {
                                    Log.e(TAG, "[Dialog]selectedIndex: " + selectedIndex + ", item: " + item);
                                }
                            });
                            new AlertDialog.Builder(m_activity)
                                    .setTitle("选择车辆油量")
                                    .setView(outerView)
                                    .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Log.e(TAG, "OK" + wv.getSeletedItem());
                                            m_data.oilvolume = wv.getSeletedItem();
                                            notifyDataSetChanged();
                                        }
                                    })
                                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Log.e(TAG, "onCancel");
                                        }
                                    })
                                    .show();
                        }
                    });
                    break;
                }
                case 12:{
                    if(m_data.state ==null)
                    {
                        m_data.state = "1";
                    }
                    holder.tip.setText(m_data.state.equals("2")?"实际提车时间" : "预计提车时间");
                    holder.value.setText(m_data.wantedcompletedtime);
                    holder.value.setFocusableInTouchMode(false);
                    RxViewHelper.textChange(holder.value,(s)->{
                        if(s.toString().length() >0) {
                            m_data.inserttime = s.toString();
                        }
                    });
                    holder.value.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            getCurrentTime(1);
                        }
                    });
                    holder.value.setHint(m_data.entershoptime);
                    break;
                }
                case 13:{
                    holder.tip.setText("客户备注");
                    holder.value.setText(m_data.customremark);
                    RxViewHelper.textChange(holder.value,(s)->{
                        m_data.customremark = s.toString();
                    });
                    break;
                }

                case 15:{
                    holder.tip.setText("维修内容");
                    holder.value.setText(m_data.repairType);
                    RxViewHelper.textChange(holder.value,(s)->{
                        if(s.toString().length() >0) {
                            m_data.repairType = s.toString();
                        }
                    });
                    holder.value.setHint("必填");
                    break;
                }

                case 16:{
                    holder.tip.setText("维修备注");
                    holder.value.setText(m_data.addition);
                    RxViewHelper.textChange(holder.value,(s)->{
                        if(s.toString().length() >0) {
                            m_data.addition = s.toString();
                        }
                    });
                    break;
                }
                case 18:{
                    holder.tip.setText("提醒周期(下次保养)");
                    if(m_data.circle == null || "".equalsIgnoreCase(m_data.circle)) {
                        holder.value.setText("1");
                    }else{
                        holder.value.setText(m_data.circle);
                    }
                    holder.value.setHint("必填");
                    holder.value.setFocusableInTouchMode(false);
                    RxViewHelper.textChange(holder.value,(s)->{
                        if(s.toString().length() >0) {
                            m_data.inserttime = s.toString();
                        }
                    });
                    holder.value.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String[] arr = m_activity.getResources().getStringArray(R.array.tip_circle);
                            View outerView = LayoutInflater.from(m_activity).inflate(R.layout.wheel_view, null);
                            final WheelView wv = (WheelView) outerView.findViewById(R.id.wheel_view_wv);
                            wv.setItems(Arrays.asList(arr));
                            wv.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
                                @Override
                                public void onSelected(int selectedIndex, String item) {
                                    Log.e(TAG, "[Dialog]selectedIndex: " + selectedIndex + ", item: " + item);
                                }
                            });
                            new AlertDialog.Builder(m_activity)
                                    .setTitle("选择提醒周期(天)")
                                    .setView(outerView)
                                    .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            m_data.circle = wv.getSeletedItem();
                                            notifyDataSetChanged();
                                        }
                                    })
                                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Log.e(TAG, "onCancel");
                                        }
                                    })
                                    .show();
                        }
                    });
                    break;
                }
                case 19:{
                    holder.tip.setText("下次保养里程(KM)");
                    holder.value.setText(m_data.nexttipkm);
                    RxViewHelper.textChange(holder.value,(s)->{
                        if(s.toString().length() >0) {
                            m_data.nexttipkm = s.toString();
                        }
                    });
                    break;
                }
            }
            if(m_data.state != null) {
                if (m_data.state.equals("2")) {
                    holder.value.setEnabled(false);
                } else {
                    holder.value.setEnabled(true);
                }
            }else{
                holder.value.setEnabled(true);
            }
            return convertView;

        }else{

            ViewHolderSwiter holder = null ;
            convertView = this.m_LInflater.inflate(R.layout.repair_info_cell_checkbox, null);
            holder = new ViewHolderSwiter();
            holder.tip = ((TextView) convertView.findViewById(R.id.repair_info_cell_tip));
            holder.checkbox = ((CheckBox) convertView.findViewById(R.id.id_repair_add_closetip_checkox));
            convertView.setTag(holder);
            holder.checkbox.setEnabled(!m_data.state.endsWith("2"));

            boolean isClose = this.m_data.isClose.equals("1");
            holder.checkbox.setChecked(isClose);
            holder.checkbox.setClickable(true);
            holder.checkbox.setFocusableInTouchMode(false);
            holder.checkbox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    m_data.isClose =  m_data.isClose.equals("1") ? "0" : "1";
                    m_data.isreaded =  m_data.isClose.equals("1") ? "0" : "1";
                    notifyDataSetChanged();
                }
            });

            return convertView;

        }

    }
    @Override
    public long getItemId(int position){
        return position;
    }

    @Override
    public  Object getItem(int position){
        return this.m_data;
    }



    private class ViewHolderCellTop {
        TextView tip;
    }

    private class ViewHolderCell0 {
        TextView tip;
        TextView value;
        ImageButton btn;
    }

    private class ViewHolderRecycleView {
        RecyclerView cycView;
        ImageView uploadImg;
    }


    private class ViewHolderCell1 {
        TextView tip;
        EditText value;
    }

    private class ViewHolderSwiter {
        TextView tip;
        CheckBox checkbox;
    }

    /*
     *获取时间,0是进店时间 1是期望完成时间
     */

    private void getCurrentTime(final int flag){
        //时间选择器
        TimePickerView pvTime = new TimePickerView.Builder(m_activity, new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {//选中事件回调

                String time = DateUtil.timeFrom(date);
                if(flag == 0){
                    m_data.entershoptime = time;
                    m_data.repairTime = time.substring(0,10);
                }else {
                    m_data.wantedcompletedtime = time;
                }
                notifyDataSetChanged();
            }
        }).build();
        pvTime.setDate(Calendar.getInstance());//注：根据需求来决定是否使用该方法（一般是精确到秒的情况），此项可以在弹出选择器的时候重新设置当前时间，避免在初始化之后由于时间已经设定，导致选中时间与当前时间不匹配的问题。
        pvTime.show();
    }




}
