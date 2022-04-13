package com.points.autorepar.adapter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.v13.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.points.autorepar.MainApplication;
import com.points.autorepar.R;
import com.points.autorepar.activity.BaseActivity;
import com.points.autorepar.activity.CommonWebviewActivity;
import com.points.autorepar.activity.contact.ContactInfoEditActivity;
import com.points.autorepar.bean.Contact;
import com.points.autorepar.bean.UpdateCarcodeEvent;
import com.points.autorepar.common.Consts;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import RxJava.RxViewHelper;
import de.greenrobot.event.EventBus;

/**
 * Created by points on 16/11/28.
 */
public class ContactInfoAdapter extends BaseAdapter {
    private Context         m_context;
    private LayoutInflater  m_LInflater;
    public  Contact         m_contact;
    public  boolean         m_isAddNew;
    private ContactInfoEditActivity m_infoActivity;

    public ContactInfoAdapter(Context context, Contact m_contact) {
        this.m_context   = context;
        this.m_infoActivity = (ContactInfoEditActivity)context;
        this.m_LInflater = LayoutInflater.from(context);
        this.m_contact   = m_contact;
        m_isAddNew       = true;
    }

    @Override
    public int getCount(){
        return 14;
    }

    @Override
    public   View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null ;
            convertView = this.m_LInflater.inflate(R.layout.contact_info_cell, null);
            holder = new ViewHolder();
            holder.tip = ((TextView) convertView.findViewById(R.id.contact_info_cell_tip));
            holder.value = ((EditText) convertView.findViewById(R.id.contact_info_cell_content));
            convertView.setTag(holder);

        switch (position) {
            case 0:{
                holder.tip.setText("车主名:");
                holder.value.setText(this.m_contact.getName());
                RxViewHelper.textChange(holder.value,(s)->{
                    if(s.toString().length() >0 && !s.toString().equals("0")) {
                        m_contact.setName(s.toString());
                    }
                });
                break;
            }

            case 1:{
                convertView = this.m_LInflater.inflate(R.layout.contact_info_cell_carnum, null);
                ImageView contact_cartype_show = ((ImageView) convertView.findViewById(R.id.contact_carnum_v));
                EditText contact_carnum = (EditText)convertView.findViewById(R.id.contact_carnum);
                contact_carnum.setText(m_contact.getCarCode());
                RxViewHelper.textChange(contact_carnum,(s)->{
                            if(s.toString().length() >0 && !s.toString().equals("0")) {
                                m_contact.setCarCode(s.toString());
                            }
                });
                RxViewHelper.clickWith(contact_cartype_show,()->{
                    EventBus.getDefault().post(new UpdateCarcodeEvent(""));
                });
                break;
            }

            case 2:{
                convertView = this.m_LInflater.inflate(R.layout.contact_info_cell_phone, null);
                EditText contact_add_phone = ((EditText) convertView.findViewById(R.id.contact_add_phone));
                contact_add_phone.setText(m_contact.getTel());
                RxViewHelper.textChange(contact_add_phone,(s)->{
                    if(s.toString().length() >0 && !s.toString().equals("0")) {
                        m_contact.setTel(s.toString());
                    }
                });

                ImageView callPhone = ((ImageView) convertView.findViewById(R.id.callphone));
                RxViewHelper.clickWith(callPhone,()->{
                    onCall(m_contact.getTel());
                });
                break;
            }

            case 3:{
                convertView = this.m_LInflater.inflate(R.layout.contact_info_cell_cartype, null);
                EditText mCarType = ((EditText) convertView.findViewById(R.id.contact_add_cartype));
                Button contact_cartype_show = ((Button) convertView.findViewById(R.id.contact_cartype_show));
                String isneeddirectaddcartype = MainApplication.getInstance().getisneeddirectaddcartype(m_context);
                if("0".equalsIgnoreCase(isneeddirectaddcartype))
                {
                    mCarType.setCursorVisible(false);
                    mCarType.setFocusable(false);
                    mCarType.setFocusableInTouchMode(false);
                    mCarType.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String weburl = MainApplication.consts(ContactInfoAdapter.this.m_context).HTTP_URL + "/noticeboard/carpick2";
                            Intent intent = new Intent(m_context, CommonWebviewActivity.class);
                            intent.putExtra("url",weburl);
                            intent.putExtra("title","车型");
                            m_infoActivity.startActivityForResult(intent,1);
                        }
                    });
                    if(m_contact.getCar_key() == null || "".equalsIgnoreCase(m_contact.getCar_key())){
                        contact_cartype_show.setVisibility(View.GONE);
                    }else {
                        RxViewHelper.clickWith(contact_cartype_show,()->{
                            if ("".equalsIgnoreCase(m_contact.getCar_key())) {
                                Toast.makeText(m_context, "请设置正确的车型", Toast.LENGTH_LONG).show();
                                return;
                            }
                            String weburl = Consts.HTTP_URL + "/noticeboard/cardetail?carId=" + m_contact.getCar_key();
                            Intent intent = new Intent(m_context, CommonWebviewActivity.class);
                            intent.putExtra("url", weburl);
                            intent.putExtra("title", "车型");
                            ((Activity) m_context).startActivityForResult(intent, 1);
                        });
                    }
                }else{
                    mCarType.setCursorVisible(true);
                    mCarType.setFocusable(true);
                    mCarType.setFocusableInTouchMode(true);
                    RxViewHelper.textChange(mCarType,(s)->{
                        if(s.toString().length() >0 && !s.toString().equals("0")) {
                            m_contact.setCarType(s.toString());
                        }
                    });
                    contact_cartype_show.setVisibility(View.GONE);
                }
                mCarType.setText(m_contact.getCarType());
                break;
            }
            case 4:{
                convertView = this.m_LInflater.inflate(R.layout.contact_info_cell_carvin, null);
                ImageView contact_cartype_show = ((ImageView) convertView.findViewById(R.id.carz_v));
                final   EditText contact_carnum = (EditText)convertView.findViewById(R.id.contact_car_vin);
                contact_carnum.setText(m_contact.getVin());

                RxViewHelper.textChange(contact_carnum,(s)->{
                    if(s.toString().length() >0 && !s.toString().equals("0")) {
                        m_contact.setVin(s.toString());
                    }
                });

                RxViewHelper.clickWith(contact_cartype_show,()->{
                    m_infoActivity.startSelectVinPicToUpload(2, new BaseActivity.speUploadVinListener() {
                        @Override
                        public void onUploadVinPicSucceed(String vin) {
                            if(vin.length() == 17){
                                contact_carnum.setText(vin);
                            }
                        }
                    });
                });
                break;
            }
            case 5:{
                holder.tip.setText("车辆注册时间:");
                holder.value.setText(this.m_contact.getCarregistertime());
                holder.value.setFocusableInTouchMode(false);
                holder.value.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        m_infoActivity.selectDate(new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                                int month = monthOfYear + 1;
                                String date = year + (month > 9 ? ("-"+month) : ("-0"+month)) + ( dayOfMonth > 9 ?  ("-"+dayOfMonth) : ("-0"+dayOfMonth));
                                m_contact.setCarregistertime(date);
                                notifyDataSetChanged();
                            }
                        });
                    }
                });
                break;
            }
            case 6:{
                holder.tip.setText("下次年审时间:");
                holder.value.setText(this.m_contact.getYearchecknexttime());
                holder.value.setFocusableInTouchMode(false);
                holder.value.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        m_infoActivity.selectDate(new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                                int month = monthOfYear + 1;
                                String date = year + (month > 9 ? ("-"+month) : ("-0"+month)) + ( dayOfMonth > 9 ?  ("-"+dayOfMonth) : ("-0"+dayOfMonth));
                                m_contact.setYearchecknexttime(date);
                                notifyDataSetChanged();
                            }
                        });
                    }
                });
                break;
            }
            case 7:{
                holder.tip.setText("年审提醒提前天数:");
                holder.value.setText(this.m_contact.getTqTime1());
                RxViewHelper.textChange(holder.value,(s)->{
                    if(s.toString().length() >0 && !s.toString().equals("0")) {
                        m_contact.setTqTime1(s.toString());
                    }
                });
                break;
            }
            case 8:{
                holder.tip.setText("商业险公司:");
                holder.value.setText(this.m_contact.getSafecompany());
                RxViewHelper.textChange(holder.value,(s)->{
                    if(s.toString().length() >0 && !s.toString().equals("0")) {
                        m_contact.setSafecompany(s.toString());
                    }
                });
                break;
            }
            case 9:{
                holder.tip.setText("商业险到期时间:");
                holder.value.setText(this.m_contact.getSafenexttime());
                holder.value.setFocusableInTouchMode(false);
                holder.value.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        m_infoActivity.selectDate(new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                                int month = monthOfYear + 1;
                                String date = year + (month > 9 ? ("-"+month) : ("-0"+month)) + ( dayOfMonth > 9 ?  ("-"+dayOfMonth) : ("-0"+dayOfMonth));
                                m_contact.setSafenexttime(date);
                                notifyDataSetChanged();
                            }
                        });
                    }
                });
                break;
            }
            case 10:{
                holder.tip.setText("商业险提前提醒时间:");
                holder.value.setText(this.m_contact.getTqTime2());
                RxViewHelper.textChange(holder.value,(s)->{
                    if(s.toString().length() >0 && !s.toString().equals("0")) {
                        m_contact.setTqTime2(s.toString());
                    }
                });
                break;
            }
            case 11:{
                holder.tip.setText("交强险公司:");
                holder.value.setText(this.m_contact.getSafecompany3());
                RxViewHelper.textChange(holder.value,(s)->{
                    if(s.toString().length() >0 && !s.toString().equals("0")) {
                        m_contact.setSafecompany3(s.toString());
                    }
                });
                break;
            }
            case 12:{
                holder.tip.setText("交强险到期时间:");
                holder.value.setText(this.m_contact.getSafenexttime3());
                holder.value.setFocusableInTouchMode(false);
                RxViewHelper.clickWith(holder.value,()->{
                    m_infoActivity.selectDate(new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                            int month = monthOfYear + 1;
                            String date = year + (month > 9 ? ("-"+month) : ("-0"+month)) + ( dayOfMonth > 9 ?  ("-"+dayOfMonth) : ("-0"+dayOfMonth));
                            m_contact.setSafenexttime3(date);
                            notifyDataSetChanged();
                        }
                    });
                });
                break;
            }
            case 13:{
                holder.tip.setText("交强险提前提醒时间:");
                holder.value.setText(this.m_contact.getTqTime3());
                RxViewHelper.textChange(holder.value,(s)->{
                    if(s.toString().length() >0 && !s.toString().equals("0")) {
                        m_contact.setTqTime3(s.toString());
                    }
                });
                break;
            }
            default:
            {
                break;
            }
        }

        return convertView;
    }
    @Override
    public long getItemId(int position){
        return position;
    }

    @Override
    public  Object getItem(int position){
        return this.m_contact;
    }


    private class ViewHolder {
        TextView tip;
        EditText value;
    }

    private void callDirectly(String mobile) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.CALL");
        intent.setData(Uri.parse("tel:" + mobile));
        m_context.startActivity(intent);
    }

    final public static int REQUEST_CODE_ASK_CALL_PHONE=123;
    public void onCall(String mobile) {
        if (Build.VERSION.SDK_INT >= 23) {
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(m_infoActivity,
                    Manifest.permission.CALL_PHONE);
            if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(m_infoActivity, new String[] {
                        Manifest.permission.CALL_PHONE
                }, REQUEST_CODE_ASK_CALL_PHONE);
                return;
            } else {
                // 上面已经写好的拨号方法
                 callDirectly(mobile);
            }
        } else {
            // 上面已经写好的拨号方法
             callDirectly(mobile);
        }

    }
}
