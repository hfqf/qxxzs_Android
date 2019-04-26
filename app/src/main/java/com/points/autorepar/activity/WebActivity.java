package com.points.autorepar.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Window;
import android.view.WindowManager;

import com.points.autorepar.MainApplication;
import com.points.autorepar.base.BaseWebActivity;

/**
 * Created by cenxiaozhong on 2017/5/22.
 *  <p>
 *
 */

public class WebActivity extends BaseWebActivity {

    private static String url = "";
    private static String title = "";
    public static void actionStart(Context context, String weburl,String Title) {
        url = weburl;
        title = Title;
        Intent intent = new Intent(context, WebActivity.class);
        context.startActivity(intent);
    }


    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public void setUrl(String url) {
         super.setUrl(url);
    }

    @Override
    public String getTitleString() {
        return title;
    }

    @Override
    public void setTitleString(String Title) {
        super.setUrl(Title);
    }


    @Override
    protected void onStart() {

        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onCreate (@Nullable Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
        Window window = getWindow();
//设置透明状态栏,这样才能让 ContentView 向上
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

//需要设置这个 flag 才能调用 setStatusBarColor 来设置状态栏颜色
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        MainApplication mainApplication = (MainApplication) getApplication();
//             String url = getIntent().getStringExtra("url");
//             String title = getIntent().getStringExtra("title");
        setUrl(getUrl());
        setTitleString(getTitleString());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
