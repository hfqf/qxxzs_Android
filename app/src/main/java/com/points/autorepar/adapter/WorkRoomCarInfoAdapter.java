package com.points.autorepar.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.bigkoo.pickerview.TimePickerView;
import com.points.autorepar.R;
import com.points.autorepar.activity.ImgDisplayActivity;
import com.points.autorepar.activity.workroom.WorkRoomEditActivity;
import com.points.autorepar.bean.Contact;
import com.points.autorepar.bean.RepairHistory;
import com.points.autorepar.bean.WorkRoomPicBackEvent;
import com.points.autorepar.bean.WorkRoomPicEvent;
import com.points.autorepar.common.Consts;
import com.points.autorepar.http.HttpManager;
import com.points.autorepar.lib.wheelview.WheelView;
import com.points.autorepar.sql.DBService;
import com.points.autorepar.utils.DateUtil;
import com.points.autorepar.utils.LoginUserUtil;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.greenrobot.event.EventBus;

//import static com.facebook.react.bridge.UiThreadUtil.runOnUiThread;

/**
 * Created by points on 16/11/28.
 */
public class WorkRoomCarInfoAdapter extends BaseAdapter {
    private Context         m_context;
    private LayoutInflater  m_LInflater;
    public RepairHistory    m_data;
    private  final  String TAG = "WorkRoomCarInfoAdapter";
    private  WorkRoomEditActivity m_activity;
    private static String str_img1,str_img2,str_img3;
    private static String mCurrentPhotoPath;
    private static int imgnum;
    private static  int curimgnum;
    private int uploadImgCount;
    private ArrayList<String> picUrls = new ArrayList<String>();


    public WorkRoomCarInfoAdapter(WorkRoomEditActivity context, RepairHistory rep) {

        this.m_activity = context;
        this.m_context   = context;
        this.m_LInflater = LayoutInflater.from(context);
        this.m_data   = rep;

        init_Img(rep.pics);

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    public void unRegisterBus(){
        EventBus.getDefault().unregister(this);
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
            ViewHolderCellImg holder =  new ViewHolderCellImg();
            convertView = LayoutInflater.from(m_context).inflate(R.layout.cell_workroom_carinfo_img, null);
            holder.img1 = (ImageView) convertView.findViewById(R.id.img1);
            holder.img2 = (ImageView) convertView.findViewById(R.id.img2);
            holder.img3 = (ImageView) convertView.findViewById(R.id.img3);
            holder.addimg =(ImageView)convertView.findViewById(R.id.addimg);
            convertView.setTag(holder);

            if(str_img1 == null ||"".equalsIgnoreCase(str_img1)||"null".equalsIgnoreCase(str_img1))
            {
                holder.img1.setVisibility(View.INVISIBLE);

            }else{
                holder.img1.setVisibility(View.VISIBLE);
//                boolean status = str_img1.contains("http");
                String strUrl = Consts.HTTP_URL+"/file/pic/"+str_img1+".png";
//                holder.img2.setImageResource(R.drawable.addimg);
//                if(status) {
                    final ImageView imageView1 = holder.img1;


                    m_activity.imageLoader.get(strUrl, new ImageLoader.ImageListener() {
                        @Override
                        public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {
                            imageView1.setImageBitmap(imageContainer.getBitmap());
                        }

                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            imageView1.setImageResource(R.drawable.appicon);
                        }
                    },1000,1000);

                holder.img1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//
                        Intent inte = new Intent(m_context,
                                ImgDisplayActivity.class);
                        Bundle bu = new Bundle();
                        ArrayList imglist = new ArrayList();
                        if(!"".equalsIgnoreCase(str_img1)&& !"null".equalsIgnoreCase(str_img1))
                        {
                            String url= Consts.HTTP_URL+"/file/pic/" +str_img1+".png";
                            imglist.add(url);
                        }

                        if(!"".equalsIgnoreCase(str_img2)&& !"null".equalsIgnoreCase(str_img2))
                        {
                            String url= Consts.HTTP_URL+"/file/pic/" +str_img2+".png";
                            imglist.add(url);
                        }

                        if(!"".equalsIgnoreCase(str_img3)&& !"null".equalsIgnoreCase(str_img3))
                        {
                            String url= Consts.HTTP_URL+"/file/pic/" +str_img3+".png";
                            imglist.add(url);
                        }

                        bu.putSerializable("images", (Serializable) imglist);
                        inte.putExtra("bundle", bu);
                        inte.putExtra("position", 0);
                        m_context.startActivity(inte);
                    }
                });

                holder.img1.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        final AlertDialog.Builder normalDialog =
                                new AlertDialog.Builder(m_activity);
                        normalDialog.setTitle("删除此照片,不可恢复!");
                        normalDialog.setMessage("确认删除?");
                        normalDialog.setPositiveButton("确定",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {


//                                            initImg("");
                                            if(picUrls.size()>0)
                                            {
                                                curimgnum--;
                                                str_img1 = "";
                                                picUrls.remove(0);
                                                setDataPic();
                                            }

                                    }
                                });
                        normalDialog.setNegativeButton("取消",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //...To-do
                                    }
                                });
                        // 显示
                        normalDialog.show();
                        return true;
                    }
                });

//                }else{
//                    Bitmap bitmap = getLoacalBitmap(str_img1);
//                    holder.img1.setImageBitmap(bitmap);
//                }
            }

            if(str_img2 == null ||"".equalsIgnoreCase(str_img2) ||"null".equalsIgnoreCase(str_img2))
            {
                holder.img2.setVisibility(View.INVISIBLE);
            }else{
                holder.img2.setVisibility(View.VISIBLE);
//                boolean status = str_img2.contains("http");
//                holder.img3.setImageResource(R.drawable.addimg);
                String strUrl = Consts.HTTP_URL+"/file/pic/"+str_img2+".png";
//                if(status) {
                    final ImageView imageView1 = holder.img2;


                    m_activity.imageLoader.get(strUrl, new ImageLoader.ImageListener() {
                        @Override
                        public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {
                            imageView1.setImageBitmap(imageContainer.getBitmap());
                        }

                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            imageView1.setImageResource(R.drawable.appicon);
                        }
                    },1000,1000);

                holder.img2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//
                        Intent inte = new Intent(m_context,
                                ImgDisplayActivity.class);
                        Bundle bu = new Bundle();
                        ArrayList imglist = new ArrayList();
                        if(!"".equalsIgnoreCase(str_img1) && !"null".equalsIgnoreCase(str_img1))
                        {
                            String url= Consts.HTTP_URL+"/file/pic/" +str_img1+".png";
                            imglist.add(url);
                        }

                        if(!"".equalsIgnoreCase(str_img2) && !"null".equalsIgnoreCase(str_img2))
                        {
                            String url= Consts.HTTP_URL+"/file/pic/" +str_img2+".png";
                            imglist.add(url);
                        }

                        if(!"".equalsIgnoreCase(str_img3)&& !"null".equalsIgnoreCase(str_img3))
                        {
                            String url= Consts.HTTP_URL+"/file/pic/" +str_img3+".png";
                            imglist.add(url);
                        }

                        bu.putSerializable("images", (Serializable) imglist);
                        inte.putExtra("bundle", bu);
                        inte.putExtra("position", 0);
                        m_context.startActivity(inte);
                    }
                });

                holder.img2.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        final AlertDialog.Builder normalDialog =
                                new AlertDialog.Builder(m_activity);
                        normalDialog.setTitle("删除此照片,不可恢复!");
                        normalDialog.setMessage("确认删除?");
                        normalDialog.setPositiveButton("确定",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {


                                            if(picUrls.size()>0)
                                            {
                                                curimgnum--;
                                                str_img2 = "";
                                                picUrls.remove(1);
                                                setDataPic();
                                            }


                                    }
                                });
                        normalDialog.setNegativeButton("取消",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //...To-do
                                    }
                                });
                        // 显示
                        normalDialog.show();
                        return true;
                    }
                });

//
//                }else{
//                    Bitmap bitmap = getLoacalBitmap(str_img2);
//                    holder.img2.setImageBitmap(bitmap);
//                }

            }


            holder.addimg.setImageResource(R.drawable.addimg);
            if(imgnum == 3)
            {
                holder.addimg.setVisibility(View.INVISIBLE);
            }else{
                holder.addimg.setVisibility(View.VISIBLE);
            }
            holder.addimg.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            curimgnum = imgnum+1;
                            m_activity.showCamera();


                        }
            });

            if(str_img3 == null ||"".equalsIgnoreCase(str_img3)||"null".equalsIgnoreCase(str_img3))
            {
                holder.img3.setVisibility(View.INVISIBLE);
            }else{
                holder.img3.setVisibility(View.VISIBLE);

//                boolean status = str_img3.contains("http");
//                if(status) {
                    final ImageView imageView1 = holder.img3;
                String strUrl = Consts.HTTP_URL+"/file/pic/"+str_img3+".png";
                    m_activity.imageLoader.get(strUrl, new ImageLoader.ImageListener() {
                        @Override
                        public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {
                            imageView1.setImageBitmap(imageContainer.getBitmap());
                        }

                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            imageView1.setImageResource(R.drawable.appicon);
                        }
                    },1000,1000);

                holder.img3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//
                        Intent inte = new Intent(m_context,
                                ImgDisplayActivity.class);
                        Bundle bu = new Bundle();
                        ArrayList imglist = new ArrayList();
                        if(!"".equalsIgnoreCase(str_img1)&& !"null".equalsIgnoreCase(str_img1))
                        {
                            String url= Consts.HTTP_URL+"/file/pic/" +str_img1+".png";
                            imglist.add(url);
                        }

                        if(!"".equalsIgnoreCase(str_img2)&& !"null".equalsIgnoreCase(str_img2))
                        {
                            String url= Consts.HTTP_URL+"/file/pic/" +str_img2+".png";
                            imglist.add(url);
                        }

                        if(!"".equalsIgnoreCase(str_img3)&& !"null".equalsIgnoreCase(str_img3))
                        {
                            String url= Consts.HTTP_URL+"/file/pic/" +str_img3+".png";
                            imglist.add(url);
                        }

                        bu.putSerializable("images", (Serializable) imglist);
                        inte.putExtra("bundle", bu);
                        inte.putExtra("position", 0);
                        m_context.startActivity(inte);
                    }
                });

                holder.img3.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        final AlertDialog.Builder normalDialog =
                                new AlertDialog.Builder(m_activity);
                        normalDialog.setTitle("删除此照片,不可恢复!");
                        normalDialog.setMessage("确认删除?");
                        normalDialog.setPositiveButton("确定",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                            if(picUrls.size()>0)
                                            {
                                                curimgnum--;
                                                str_img3 = "";
                                                picUrls.remove(2);
                                                setDataPic();
                                            }

                                    }
                                });
                        normalDialog.setNegativeButton("取消",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //...To-do
                                    }
                                });
                        // 显示
                        normalDialog.show();
                        return true;
                    }
                });

//                }else{
//                    Bitmap bitmap = getLoacalBitmap(str_img3);
//                    holder.img3.setImageBitmap(bitmap);
//                }
            }
            return convertView;
        }
        else if( position > 0 && position < 7)
        {
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

                }
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

        }else if(position == 20)
        {


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
                    holder.value.addTextChangedListener(repairTimeWatcher);
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
                    holder.value.addTextChangedListener(kmWatcher);
                    if(m_data.totalKm == null||"".equalsIgnoreCase(m_data.totalKm) || "null".equalsIgnoreCase(m_data.totalKm)) {
                        holder.value.setText("0");
                    }
                    break;
                }
                case 10:{


                    holder.tip.setText("入店油量");
                    holder.tip.setEnabled(false);
                    holder.value.setFocusableInTouchMode(false);
                    holder.value.addTextChangedListener(oilWatcher);
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
                    holder.value.addTextChangedListener(repairTimeWatcher);
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
                    holder.value.addTextChangedListener(customerAddtionWatcher);
                    break;
                }

                case 15:{
                    holder.tip.setText("维修内容");
                    holder.value.setText(m_data.repairType);
                    holder.value.addTextChangedListener(repaorContentWatcher);
                    holder.value.setHint("必填");
                    break;
                }

                case 16:{
                    holder.tip.setText("维修备注");
                    holder.value.setText(m_data.addition);
                    holder.value.addTextChangedListener(addtionContentWatcher);
                    break;
                }
//                case 19:{
//                    holder.tip.setText("车况图片");
//                    holder.value.setText(m_data.addition);
//                    holder.value.addTextChangedListener(addtionContentWatcher);
//                    break;
//                }
                case 18:{
                    holder.tip.setText("提醒周期(下次保养)");
                    if(m_data.circle == null || "".equalsIgnoreCase(m_data.circle)) {
                        holder.value.setText("1");
                    }else{
                        holder.value.setText(m_data.circle);
                    }
                    holder.value.setHint("必填");
                    holder.value.setFocusableInTouchMode(false);
                    holder.value.addTextChangedListener(repairTimeWatcher);
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


                                            Log.e(TAG, "OK" + wv.getSeletedItem());

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
                    holder.tip.setText("下次保养里程数（KM）");
                    holder.value.setText(m_data.nexttipkm);
                    holder.value.addTextChangedListener(nextKmContentWatcher);
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

    private class ViewHolderCellImg {
        ImageView img1;
        ImageView img2;
        ImageView img3;
        ImageView addimg;
    }


    private class ViewHolderCell1 {
        TextView tip;
        EditText value;
    }

    private class ViewHolderSwiter {
        TextView tip;
        CheckBox checkbox;
    }

    private TextWatcher repaorContentWatcher = new TextWatcher() {

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

    private TextWatcher addtionContentWatcher = new TextWatcher() {

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

    private TextWatcher nextKmContentWatcher = new TextWatcher() {

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
            m_data.nexttipkm = s.toString();
        }
    };


    private TextWatcher kmWatcher = new TextWatcher() {

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
        }
    };


    private TextWatcher oilWatcher = new TextWatcher() {

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
            m_data.oilvolume = s.toString();
        }
    };


    private TextWatcher customerAddtionWatcher = new TextWatcher() {

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
            m_data.customremark = s.toString();
        }
    };


    private TextWatcher repairCircleWatcher = new TextWatcher() {

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
            m_data.circle = s.toString();
        }
    };


    private TextWatcher repairTimeWatcher = new TextWatcher() {

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
            m_data.inserttime = s.toString();
        }
    };

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


//    public void onActivityResult(int requestCode, int resultCode, Intent data)
//    {
//        // 当requestCode、resultCode同时为0，也就是处理特定的结果
//        if (requestCode == 0 && resultCode == 1)
//        {
//            // 取出Intent里的Extras数据
//            Bundle bundle = data.getExtras();
//            // 取出Bundle中的数据
//            String resultCity = bundle.getString("data");
//            // 修改city文本框的内容
////            city.setText(resultCity);
//            set_img1 = resultCity;
//        }
//    }
    private File createImageFile() {
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        File image = null;
        try {
            image = File.createTempFile(
                    generateFileName(),  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );
        } catch (IOException e) {
            e.printStackTrace();
        }

        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    public static String generateFileName() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        return imageFileName;
    }
    public void onEventMainThread(WorkRoomPicEvent event) {
        String data = event.getMsgPic();

        final String strPic = data;

        if(curimgnum == 0)
            return;
        final int num = curimgnum;
        m_activity.runOnUiThread(new Runnable() {
            public void run() {
                uploadFileToBOS(strPic,new File(strPic),num);
            }
        });

        imgnum = curimgnum;

//        curimgnum= 0;

    }
     void uploadFileToBOS(final String fileName, final File file,final int type) {

        String str[] = fileName.split("/");
        Map map = new HashMap();
        String  filename = LoginUserUtil.getTel(m_context)+"_"+System.currentTimeMillis();
        map.put("fileName", filename);
        boolean ret = file.exists();
        HttpManager.getInstance(m_context).startNormalFilePost("/file/picUpload", filename,file, map, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                String strUrl = jsonObject.optString("url");
                int ret = jsonObject.optInt("code");
                if(ret != 1)
                {
                    return ;
                }

                if(strUrl ==null || "".equalsIgnoreCase(strUrl))
                {
//                    url = strUrl;
                }else{

                        if(picUrls.size()<type)
                        {
                            picUrls.add(strUrl);
                        }else{
                            picUrls.set(type-1,strUrl);
                        }

                        if(type==1)
                        {
                            str_img1 = strUrl;
                        }else if(type==2)
                        {
                            str_img2 = strUrl;
                        }else if(type ==3)
                        {
                            str_img3 = strUrl;
                        }
                        setDataPic();
                }
//                initImg(url);

                EventBus.getDefault().post(
                        new WorkRoomPicBackEvent(mCurrentPhotoPath));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
//                Toast?.makeText(getApplicationContext(), "上传图片失败", Toast.LENGTH_SHORT).show();
                int i=0;
            }
        });

    }





    public static Bitmap getLoacalBitmap(String url) {
        try {
            FileInputStream fis = new FileInputStream(url);
            return BitmapFactory.decodeStream(fis);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Bitmap getHttpBitmap(String url) {
        URL myFileUrl = null;
        Bitmap bitmap = null;
        try {

            myFileUrl = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
            conn.setConnectTimeout(0);
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public void init_Img(String url)
    {
        if(url== null || "".equalsIgnoreCase(url))
        {
            str_img1 = "";
            str_img2 = "";
            str_img3 = "";
            imgnum = 0;
        }else{
            String[] parts = url.split(",");
            imgnum = parts.length;

            if (parts != null) {
                if (parts.length != 0) {
                    if (parts.length == 1) {
                        str_img1 = parts[0];
                        str_img2= "";
                        str_img3 = "";
                        imgnum = 1;
                        picUrls.add(str_img1);
                    }
                    if (parts.length == 2) {
                        str_img1 = parts[0];
                        str_img2 = parts[1];
                        str_img3 = "";
                        imgnum = 2;
                        picUrls.add(str_img1);
                        picUrls.add(str_img2);
                    }
                    if (parts.length == 3) {
                        str_img1 = parts[0];
                        str_img2 = parts[1];
                        str_img3 = parts[2];
                        imgnum = 3;
                        picUrls.add(str_img1);
                        picUrls.add(str_img2);
                        picUrls.add(str_img3);

                    }
                }
            }
        }
        m_data.pics = url;
        notifyDataSetChanged();
    }

    void setDataPic()
    {
        String url = "";
        str_img1 ="";
        str_img2 ="";
        str_img3 = "";
        if(picUrls.size() == 0)
        {
            url = "";
        }else {
            for (int i = 0; i < picUrls.size(); i++) {
                if (i == 0) {
                    url = picUrls.get(0);
                } else {
                    url = url + "," + picUrls.get(i);
                }

                switch (i)
                {
                    case 0:
                        str_img1 = picUrls.get(i);
                        break;
                    case 1:
                        str_img2 = picUrls.get(i);
                        break;
                    case 2:
                        str_img3 = picUrls.get(i);
                        break;
                }
            }
        }
        m_data.pics = url;
        notifyDataSetChanged();
    }

}
