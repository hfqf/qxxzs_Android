package com.points.autorepar.bean;

import android.content.AsyncQueryHandler;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;


import com.points.autorepar.interfaces.IContactView;
import com.points.autorepar.sql.DBService;
import com.points.autorepar.utils.PinyinUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jenly <a href="mailto:jenly1314@gmail.com">Jenly</a>
 * @since 2017/3/31
 */

public class ContactPresenter {

    private Context context;

    private IContactView iContactView;

    private AsyncQueryHandler aueryHandler;

    private Handler handler;

    private String key;

    public ContactPresenter(Context context, IContactView iContactView){
        this.context = context;
        this.iContactView = iContactView;

    }


    public void loadListContact(){

        ArrayList<Contact> arr = DBService.queryAllContact();

        List<ContactSim> list = new ArrayList<>();

        for(int i=0;i<arr.size();i++){
            Contact con = arr.get(i);
            ContactSim c = new ContactSim();
            c.setName(con.getName());
            c.setNumber(con.getTel());
            c.setSort(PinyinUtil.formatAlpha(con.getName()));
            list.add(c);
        }
        iContactView.getListContact(list);

    }


    public void showLetter(String letter){
        this.key = letter;
        if(handler == null){
            handler = new Handler(){
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    switch (msg.what){
                        case 1:
                            iContactView.showLetter(key);
                            handler.removeMessages(2);
                            handler.sendEmptyMessageDelayed(2,500);
                            break;
                        case 2:
                            iContactView.hideLetter();
                            break;
                    }
                }
            };
        }
        handler.sendEmptyMessage(1);

    }



}
