package com.points.autorepar.adapter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v13.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.points.autorepar.activity.CommonWebviewActivity;
import com.points.autorepar.activity.contact.ContactAddNewActivity;
import com.points.autorepar.activity.contact.ContactInfoEditActivity;
import com.points.autorepar.bean.Contact;
import com.points.autorepar.common.Consts;
import com.points.autorepar.lib.ocr.ui.camera.CameraActivity;
import com.points.autorepar.lib.ocr.ui.camera.FileUtil;

import java.util.List;
import java.util.Map;

/**
 * Created by points on 16/11/28.
 */
public class ContactInfoAdapter extends BaseAdapter {
    private Context         m_context;
    private LayoutInflater  m_LInflater;
    public  Contact         m_contact;
    public  boolean         m_isAddNew;
    private ContactInfoEditActivity m_infoActivity;
//    private String mKey_str;
//    private String mCarType_str;

    public ContactInfoAdapter(Context context, Contact m_contact) {
        this.m_context   = context;
        this.m_infoActivity = (ContactInfoEditActivity)context;
        this.m_LInflater = LayoutInflater.from(context);
        this.m_contact   = m_contact;
        m_isAddNew       = true;
    }


    public void refreshEditVaules(){

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
                holder.value.addTextChangedListener(watcher1);
                break;
            }

            case 1:{
                convertView = this.m_LInflater.inflate(R.layout.contact_info_cell_carnum, null);
                ImageView contact_cartype_show = ((ImageView) convertView.findViewById(R.id.contact_carnum_v));

                EditText contact_carnum = (EditText)convertView.findViewById(R.id.contact_carnum);
                contact_carnum.setText(m_contact.getCarCode());
                contact_carnum.addTextChangedListener(watcher2);
                contact_cartype_show.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        Toast.makeText(m_context, "扫描时横竖屏都可以,最后选取的截图一定只能包含车牌文字,不能含有其它不相关的数字或字母。扫描识别有一定几率失败或不能完全识别，如果没识别成功可以在详情页再稍微编辑下。", Toast.LENGTH_LONG).show();

                        Intent intent = new Intent(m_context, CameraActivity.class);
                        intent.putExtra(CameraActivity.KEY_OUTPUT_FILE_PATH,
                                FileUtil.getSaveFile(m_context).getAbsolutePath());
                        intent.putExtra(CameraActivity.KEY_CONTENT_TYPE,
                                CameraActivity.CONTENT_TYPE_GENERAL);
                        m_infoActivity.startActivityForResult(intent, 122);


                    }
                });


                break;
            }

            case 2:{
//                holder.tip.setText("车主号码:");
//                holder.value.setText(this.m_contact.getTel());
//                holder.value.addTextChangedListener(watcher3);
//                break;
                convertView = this.m_LInflater.inflate(R.layout.contact_info_cell_phone, null);
                EditText contact_add_phone = ((EditText) convertView.findViewById(R.id.contact_add_phone));
                contact_add_phone.setText(m_contact.getTel());
                contact_add_phone.addTextChangedListener(watcher3);
                ImageView callPhone = ((ImageView) convertView.findViewById(R.id.callphone));
                callPhone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

//                        Intent intent = new Intent(Intent.ACTION_CALL);
//                        Uri data = Uri.parse("tel:" + m_contact.getTel());
//                        intent.setData(data);
//                        m_context.startActivity(intent);
//                        callDirectly(m_contact.getTel());
                        onCall(m_contact.getTel());
                    }
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


//                            String weburl ="file:///android_asset/carpickerIndex.html";
                            String weburl = MainApplication.consts(ContactInfoAdapter.this.m_context).HTTP_URL + "/noticeboard/carpick2";
                            Intent intent = new Intent(m_context, CommonWebviewActivity.class);
                            intent.putExtra("url",weburl);
                            intent.putExtra("title","车型");
                            m_infoActivity.startActivityForResult(intent,1);
                        }
                    });
                    if(m_contact.getCar_key() == null || "".equalsIgnoreCase(m_contact.getCar_key()) )
                    {
                        contact_cartype_show.setVisibility(View.GONE);
                    }else {
//                        contact_cartype_show.setVisibility(View.VISIBLE);
                        contact_cartype_show.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                if ("".equalsIgnoreCase(m_contact.getCar_key())) {
                                    Toast.makeText(m_context, "请设置正确的车型", Toast.LENGTH_LONG).show();
                                    return;
                                }

                                String weburl = Consts.HTTP_URL + "/noticeboard/cardetail?carId=" + m_contact.getCar_key();
//                            String weburl ="file:///android_asset/carpickerIndex.html";
                                Intent intent = new Intent(m_context, CommonWebviewActivity.class);
                                intent.putExtra("url", weburl);
                                intent.putExtra("title", "车型");
                                ((Activity) m_context).startActivityForResult(intent, 1);
                            }
                        });
                    }
                }else{
                    mCarType.setCursorVisible(true);
                    mCarType.setFocusable(true);
                    mCarType.setFocusableInTouchMode(true);
                    mCarType.addTextChangedListener(watcher4);
                    contact_cartype_show.setVisibility(View.GONE);

                }



                mCarType.setText(m_contact.getCarType());



                break;
            }
            case 4:{
                convertView = this.m_LInflater.inflate(R.layout.contact_info_cell_carvin, null);
                ImageView contact_cartype_show = ((ImageView) convertView.findViewById(R.id.carz_v));

                EditText contact_carnum = (EditText)convertView.findViewById(R.id.contact_car_vin);
                contact_carnum.setText(m_contact.getVin());
                contact_carnum.addTextChangedListener(watcher5);
                contact_cartype_show.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        Toast.makeText(m_context, "扫描时横竖屏都可以,最后选取的截图一定只能包含车牌文字,不能含有其它不相关的数字或字母。扫描识别有一定几率失败或不能完全识别，如果没识别成功可以在详情页再稍微编辑下。", Toast.LENGTH_LONG).show();

                        Intent intent = new Intent(m_context, CameraActivity.class);
                        intent.putExtra(CameraActivity.KEY_OUTPUT_FILE_PATH,
                                FileUtil.getSaveFile(m_context).getAbsolutePath());
                        intent.putExtra(CameraActivity.KEY_CONTENT_TYPE,
                                CameraActivity.CONTENT_TYPE_GENERAL);
                        m_infoActivity.startActivityForResult(intent, 120);


                    }
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
                        m_infoActivity.selectDate(0);
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
                        m_infoActivity.selectDate(1);
                    }
                });
                break;
            }
            case 7:{
                holder.tip.setText("年审提醒提前天数:");
                holder.value.setText(this.m_contact.getTqTime1());
                holder.value.addTextChangedListener(watcher6);
                break;
            }
            case 8:{
                holder.tip.setText("商业险公司:");
                holder.value.setText(this.m_contact.getSafecompany());
                holder.value.addTextChangedListener(watcher7);
                break;
            }
            case 9:{
                holder.tip.setText("商业险到期时间:");
                holder.value.setText(this.m_contact.getSafenexttime());
                holder.value.setFocusableInTouchMode(false);
                holder.value.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        m_infoActivity.selectDate(2);
                    }
                });
                break;
            }
            case 10:{
                holder.tip.setText("商业险提前提醒时间:");
                holder.value.setText(this.m_contact.getTqTime2());
                holder.value.addTextChangedListener(watcher8);
                break;
            }
            case 11:{
                holder.tip.setText("交强险公司:");
                holder.value.setText(this.m_contact.getSafecompany3());
                holder.value.addTextChangedListener(watcher9);
                break;
            }
            case 12:{
                holder.tip.setText("交强险到期时间:");
                holder.value.setText(this.m_contact.getSafenexttime3());
                holder.value.setFocusableInTouchMode(false);
                holder.value.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        m_infoActivity.selectDate(3);
                    }
                });
                break;
            }
            case 13:{
                holder.tip.setText("交强险提前提醒时间:");
                holder.value.setText(this.m_contact.getTqTime3());
                holder.value.addTextChangedListener(watcher10);
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



    private TextWatcher watcher1 = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {


        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
            // TODO Auto-generated method stub

        }

        @Override
        public void afterTextChanged(Editable s) {
            if(s.toString().length() >0 && !s.toString().equals("0")){
                m_contact.setName(s.toString());
            }
        }
    };

    private TextWatcher watcher2 = new TextWatcher() {

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
            if(s.toString().length() >0 && !s.toString().equals("0")) {
                m_contact.setCarCode(s.toString());
            }

        }
    };

    private TextWatcher watcher3 = new TextWatcher() {

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
            if(s.toString().length() >0 && !s.toString().equals("0")) {
                m_contact.setTel(s.toString());
            }
        }
    };

    private TextWatcher watcher4 = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // TODO Auto-generated method stub
//            m_contact.setCarType(s.toString());
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
            // TODO Auto-generated method stub

        }

        @Override
        public void afterTextChanged(Editable s) {
            if(s.toString().length() >0 && !s.toString().equals("0")) {
                m_contact.setCarType(s.toString());
            }
        }
    };

    private TextWatcher watcher5 = new TextWatcher() {

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
            if(s.toString().length() >0 && !s.toString().equals("0")) {
                m_contact.setVin(s.toString());
            }
        }
    };

    private TextWatcher watcher6 = new TextWatcher() {

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
            if(s.toString().length() >0 && !s.toString().equals("0")) {
                m_contact.setTqTime1(s.toString());
            }
        }
    };

    private TextWatcher watcher7 = new TextWatcher() {

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
            if(s.toString().length() >0 && !s.toString().equals("0")) {
                m_contact.setSafecompany(s.toString());
            }
        }
    };

    private TextWatcher watcher8 = new TextWatcher() {

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
            if(s.toString().length() >0 && !s.toString().equals("0")) {
                m_contact.setTqTime2(s.toString());
            }
        }
    };

    private TextWatcher watcher9 = new TextWatcher() {

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
            if(s.toString().length() >0 && !s.toString().equals("0")) {
                m_contact.setSafecompany3(s.toString());
            }
        }
    };

    private TextWatcher watcher10 = new TextWatcher() {

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
            if(s.toString().length() >0 && !s.toString().equals("0")) {
                m_contact.setTqTime3(s.toString());
            }
        }
    };
    public void onActivityResult(int requestCode
            , int resultCode, Intent intent)
    {
        // 当requestCode、resultCode同时为0，也就是处理特定的结果
        if (requestCode == 0 && resultCode == 1)
        {
            Bundle data = intent.getExtras();
            // 取出Bundle中的数据

            m_contact.setCarType( data.getString("mCarType_str"));
            m_contact.setCar_key( data.getString("mKey_str"));
            notifyDataSetChanged();
        }
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
    //动态权限申请后处理
    public void onRequestPermissionsResult(int requestCode, String[] permissions,int[] grantResults){
        switch (requestCode) {
            case REQUEST_CODE_ASK_CALL_PHONE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted callDirectly(mobile);
                }else {
                    // Permission Denied
                    Toast.makeText(m_infoActivity,"请打开拨号权限后使用", Toast.LENGTH_SHORT) .show();
                }break;
            default:
                m_infoActivity.onRequestPermissionsResult();
        } }
}
