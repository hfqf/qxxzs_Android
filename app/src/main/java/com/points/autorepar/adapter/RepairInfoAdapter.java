package com.points.autorepar.adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.points.autorepar.R;
import com.points.autorepar.activity.repair.RepairInfoEditActivity;
import com.points.autorepar.bean.ADTReapirItemInfo;
import com.points.autorepar.bean.RepairHistory;

/**
 * Created by points on 16/11/28.
 */
public class RepairInfoAdapter extends BaseAdapter {
    private Context         m_context;
    private LayoutInflater  m_LInflater;
    public RepairHistory    m_data;
    private RepairInfoEditActivity m_parent;
    private  final  String TAG = "RepairInfoAdapter";


    public RepairInfoAdapter(Context context, RepairHistory rep) {

        this.m_context   = context;
        this.m_LInflater = LayoutInflater.from(context);
        this.m_data   = rep;
        this.m_parent = (RepairInfoEditActivity) context;

    }

    @Override
    public int getCount(){
        return 7+this.m_data.arrRepairItems.size();
}

    @Override
    public   View getView(int position, View convertView, ViewGroup parent) {


        if(position == 0 || position == 1|| position == 2) {
            ViewHolderNormalInput holder = null ;
            View vi = this.m_LInflater.inflate(R.layout.repair_info_cell_small_edit, null);

            if(convertView==null || vi.getClass() != convertView.getClass()){
                convertView = this.m_LInflater.inflate(R.layout.repair_info_cell_small_edit, null);
                holder = new ViewHolderNormalInput();
                holder.tip = ((TextView) convertView.findViewById(R.id.repair_info_cell_tip));
                holder.value = ((EditText) convertView.findViewById(R.id.repair_info_cell_content));
                convertView.setTag(holder);
            }else {

                if("ViewHolderNormalInput" == convertView.getTag().getClass().toString()){
                    holder = (ViewHolderNormalInput)convertView.getTag();
                }
                else {
                    convertView = this.m_LInflater.inflate(R.layout.repair_info_cell_small_edit, null);
                    holder = new ViewHolderNormalInput();
                    holder.tip = ((TextView) convertView.findViewById(R.id.repair_info_cell_tip));
                    holder.value = ((EditText) convertView.findViewById(R.id.repair_info_cell_content));
                    convertView.setTag(holder);
                }
            }

            switch (position) {
                case 0:{
                    holder.tip.setText("公里数(KM):");
                    holder.value.setText(m_data.totalKm);
                    holder.value.addTextChangedListener(watcherKm);

                    break;
                }

                case 1:{
                    holder.value.removeTextChangedListener(watcherKm);
                    holder.tip.setText("维修日期:");
                    holder.value.setText(this.m_data.repairTime);
                    holder.value.setFocusableInTouchMode(false);
                    holder.value.addTextChangedListener(watcherRepairTime);
                    holder.value.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            m_parent.selectDate();

                        }
                    });
                    break;
                }

                case 2:{
                    holder.value.removeTextChangedListener(watcherKm);
                    holder.tip.setText("提醒周期:");
                    holder.value.setText(this.m_data.circle);
                    holder.value.setClickable(true);
                    holder.value.setFocusableInTouchMode(false);
                    holder.value.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            m_parent.selectTipCircle();
                        }
                    });


                    break;
                }
                default:
                {
                    break;
                }
            }
        }
        else if(position == 3 || position == 4)
        {
            ViewHolderMutilInput holder = null ;
            View vi = this.m_LInflater.inflate(R.layout.repair_info_cell_large_edit, null);

            if(convertView==null || vi.getClass() != convertView.getClass()) {
                convertView = this.m_LInflater.inflate(R.layout.repair_info_cell_large_edit, null);
                holder = new ViewHolderMutilInput();
                holder.tip = ((TextView) convertView.findViewById(R.id.repair_info_cell_tip));
                holder.value = ((EditText) convertView.findViewById(R.id.repair_info_cell_content));
                convertView.setTag(holder);
            }else {
                if("ViewHolderMutilInput" == convertView.getTag().getClass().toString()){
                    holder = (ViewHolderMutilInput)convertView.getTag();
                }
                else {
                    convertView = this.m_LInflater.inflate(R.layout.repair_info_cell_large_edit, null);
                    holder = new ViewHolderMutilInput();
                    holder.tip = ((TextView) convertView.findViewById(R.id.repair_info_cell_tip));
                    holder.value = ((EditText) convertView.findViewById(R.id.repair_info_cell_content));
                    convertView.setTag(holder);
                }
            }

            switch (position) {
                case 3:{
                    holder.tip.setText("保养项目:");
                    holder.value.setText(this.m_data.repairType);
                    holder.value.addTextChangedListener(watcherRepairType);
                    break;
                }

                case 4:{
                    holder.tip.setText("备注:");
                    holder.value.setText(this.m_data.addition.length() == 0 ? "暂无":this.m_data.addition);
                    holder.value.addTextChangedListener(watcherAddtion);
                    break;
                }

                default:
                {
                    break;
                }
            }
        }
        else if (position == 5){
            ViewHolderSwiter holder = null ;
            View vi = this.m_LInflater.inflate(R.layout.repair_info_cell_checkbox, null);
            if(convertView==null ||vi.getClass() != convertView.getClass()){
                convertView = this.m_LInflater.inflate(R.layout.repair_info_cell_checkbox, null);
                holder = new ViewHolderSwiter();
                holder.tip = ((TextView) convertView.findViewById(R.id.repair_info_cell_tip));
                holder.checkbox = ((CheckBox) convertView.findViewById(R.id.id_repair_add_closetip_checkox));
                convertView.setTag(holder);
            }else {
                if("ViewHolderSwiter" == convertView.getTag().getClass().toString()){
                    holder = (ViewHolderSwiter)convertView.getTag();
                }
                else {
                    convertView = this.m_LInflater.inflate(R.layout.repair_info_cell_checkbox, null);
                    holder = new ViewHolderSwiter();
                    holder.tip = ((TextView) convertView.findViewById(R.id.repair_info_cell_tip));
                    holder.checkbox = ((CheckBox) convertView.findViewById(R.id.id_repair_add_closetip_checkox));
                    convertView.setTag(holder);
                }
                holder = (ViewHolderSwiter)convertView.getTag();
            }

            boolean isClose = this.m_data.isClose.equals("1");
            holder.checkbox.setChecked(isClose);
            holder.checkbox.setClickable(true);
            holder.checkbox.setFocusableInTouchMode(false);
            holder.checkbox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    m_parent.changeCloseTip();
                }
            });

        }
        else if (position == 6){
            ViewHolderAddItem holder6 = null ;
            View vi = this.m_LInflater.inflate(R.layout.repair_info_item_add, null);
            if(convertView==null || vi.getClass() != convertView.getClass()){
                convertView = this.m_LInflater.inflate(R.layout.repair_info_item_add, null);
                holder6 = new ViewHolderAddItem();
                holder6.tip = ((TextView) convertView.findViewById(R.id.repair_info_cell_tip));
                holder6.value1 = ((EditText) convertView.findViewById(R.id.repair_info_cell_content1));
                holder6.value2 = ((EditText) convertView.findViewById(R.id.repair_info_cell_content2));
                holder6.value3 = ((EditText) convertView.findViewById(R.id.repair_info_cell_content3));
                holder6.addBtn = ((Button) convertView.findViewById(R.id.repair_info_cell_content4));
                convertView.setTag(holder6);
            }else {
                if("ViewHolderAddItem" == convertView.getTag().getClass().toString()){
                    holder6 = (ViewHolderAddItem)convertView.getTag();
                }
                else {
                    convertView = this.m_LInflater.inflate(R.layout.repair_info_item_add, null);
                    holder6 = new ViewHolderAddItem();
                    holder6.tip = ((TextView) convertView.findViewById(R.id.repair_info_cell_tip));
                    holder6.value1 = ((EditText) convertView.findViewById(R.id.repair_info_cell_content1));
                    holder6.value2 = ((EditText) convertView.findViewById(R.id.repair_info_cell_content2));
                    holder6.value3 = ((EditText) convertView.findViewById(R.id.repair_info_cell_content3));
                    holder6.addBtn = ((Button) convertView.findViewById(R.id.repair_info_cell_content4));
                    convertView.setTag(holder6);
                }
            }

            holder6.addBtn.setClickable(true);
            holder6.addBtn.setFocusableInTouchMode(false);

            final ViewHolderAddItem _holder6 = holder6 ;
            holder6.addBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    ADTReapirItemInfo       m_addRepItem = new ADTReapirItemInfo();
                    m_addRepItem.type =  _holder6.value1.getText().toString();
                    m_addRepItem.price = _holder6.value2.getText().toString();
                    m_addRepItem.num = _holder6.value3.getText().toString();
                    m_parent.addNewRepairItem(m_addRepItem);
                }
            });
        }

        else{

           final   ADTReapirItemInfo info = this.m_data.arrRepairItems.get(position-7);
            final  int index =  position-7;
            ViewHolderItemCell holder = null ;
            View vi = this.m_LInflater.inflate(R.layout.repair_info_item_cell, null);

            if(convertView==null || vi.getClass() != convertView.getClass()){
                convertView = this.m_LInflater.inflate(R.layout.repair_info_item_cell, null);
                holder = new ViewHolderItemCell();
                holder.index =  ((TextView) convertView.findViewById(R.id.repair_info_item_index));
                holder.tip1 = ((TextView) convertView.findViewById(R.id.repair_info_item_cell_tip1));
                holder.tip1 = ((TextView) convertView.findViewById(R.id.repair_info_item_cell_tip2));
                holder.tip1 = ((TextView) convertView.findViewById(R.id.repair_info_item_cell_tip3));

                holder.value1 = ((EditText) convertView.findViewById(R.id.repair_info_cell_content1));
                holder.value2 = ((EditText) convertView.findViewById(R.id.repair_info_cell_content2));
                holder.value3 = ((EditText) convertView.findViewById(R.id.repair_info_cell_content3));
                holder.delBtn = ((Button) convertView.findViewById(R.id.repair_info_cell_content4));
                convertView.setTag(holder);
            }
            else {

                if("ViewHolderItemCell" == convertView.getTag().getClass().toString()){
                    holder = (ViewHolderItemCell)convertView.getTag();
                }
                else {
                    convertView = this.m_LInflater.inflate(R.layout.repair_info_item_cell, null);
                    holder = new ViewHolderItemCell();
                    holder.index =  ((TextView) convertView.findViewById(R.id.repair_info_item_index));
                    holder.tip1 = ((TextView) convertView.findViewById(R.id.repair_info_item_cell_tip1));
                    holder.tip1 = ((TextView) convertView.findViewById(R.id.repair_info_item_cell_tip2));
                    holder.tip1 = ((TextView) convertView.findViewById(R.id.repair_info_item_cell_tip3));

                    holder.value1 = ((EditText) convertView.findViewById(R.id.repair_info_cell_content1));
                    holder.value2 = ((EditText) convertView.findViewById(R.id.repair_info_cell_content2));
                    holder.value3 = ((EditText) convertView.findViewById(R.id.repair_info_cell_content3));
                    holder.delBtn = ((Button) convertView.findViewById(R.id.repair_info_cell_content4));
                    convertView.setTag(holder);
                }
                holder = (ViewHolderItemCell)convertView.getTag();
            }
            holder.index.setText(String.valueOf(position-6));
            holder.value1.setText(info.type);
            holder.value2.setText(info.price);
            holder.value3.setText(info.num);

            holder.delBtn.setClickable(true);
            holder.delBtn.setFocusableInTouchMode(false);
            holder.delBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    m_parent.delRepairItem(info,index);
                }
            });
        }



        return convertView;
    }
    @Override
    public long getItemId(int position){
        return position;
    }

    @Override
    public  Object getItem(int position){
        return this.m_data;
    }

    private class ViewHolderNormalInput {
        TextView tip;
        EditText value;
    }

    private class ViewHolderMutilInput {
        TextView tip;
        EditText value;
    }

    private class ViewHolderAddItem {
        TextView tip;
        EditText value1;
        EditText value2;
        EditText value3;
        Button   addBtn;
    }

    private class ViewHolderItemCell {
        TextView  index;
        TextView  tip1;
        EditText  value1;
        TextView  tip2;
        EditText  value2;
        TextView  tip3;
        EditText  value3;
        Button    delBtn;
    }

    private class ViewHolderSwiter {
        TextView tip;
        CheckBox checkbox;
    }

    private TextWatcher watcherKm = new TextWatcher( ) {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // TODO Auto-generated method stub

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
            // TODO Auto-generated method stub
        }

        @Override
        public void afterTextChanged(Editable s) {
            m_data.totalKm = s.toString();
            Log.e(TAG,"afterTextChanged"+m_data.totalKm);
        }
    };

    private TextWatcher watcherRepairTime = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // TODO Auto-generated method stub

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
            // TODO Auto-generated method stub

        }

        @Override
        public void afterTextChanged(Editable s) {
            m_data.repairTime = s.toString();
        }
    };

    private TextWatcher watcherTipCircle = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // TODO Auto-generated method stub

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
            // TODO Auto-generated method stub

        }

        @Override
        public void afterTextChanged(Editable s) {
            m_data.tipCircle = s.toString();

        }
    };

    private TextWatcher watcherRepairType = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // TODO Auto-generated method stub

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
            // TODO Auto-generated method stub

        }

        @Override
        public void afterTextChanged(Editable s) {
            m_data.repairType = s.toString();

        }
    };

    private TextWatcher watcherAddtion = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // TODO Auto-generated method stub

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
            // TODO Auto-generated method stub

        }

        @Override
        public void afterTextChanged(Editable s) {
            m_data.addition = s.toString();

        }
    };
}
