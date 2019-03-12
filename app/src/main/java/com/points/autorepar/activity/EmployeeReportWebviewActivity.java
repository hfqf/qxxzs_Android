package com.points.autorepar.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.points.autorepar.MainApplication;
import com.points.autorepar.R;
import com.points.autorepar.utils.LoginUserUtil;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * Created by cenxiaozhong on 2017/5/22.
 *  <p>
 *
 */

public class EmployeeReportWebviewActivity extends Activity implements View.OnClickListener,DatePickerDialog.OnDateSetListener {

    private WebView webView;
    private ProgressBar progressBar;
    private TextView left_month,right_month;
    private Button bt_start;
    private String url ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview );

        progressBar= (ProgressBar)findViewById(R.id.progressbar);//进度条
        left_month= (TextView)findViewById(R.id.left_month);//进度条
        right_month= (TextView)findViewById(R.id.right_month);//进度条
        bt_start = (Button)findViewById(R.id.start);
        left_month.setOnClickListener(this);
        bt_start.setOnClickListener(this);
        right_month.setOnClickListener(this);

        MainApplication mainApplication = (MainApplication) getApplication();
        Window window = getWindow();
//设置透明状态栏,这样才能让 ContentView 向上
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

//需要设置这个 flag 才能调用 setStatusBarColor 来设置状态栏颜色
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        Button common_navi_back = (Button)findViewById(R.id.common_navi_back);
        common_navi_back.setVisibility(View.GONE);
        Button common_navi_add = (Button)findViewById(R.id.common_navi_add);
        common_navi_add.setVisibility(View.GONE);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");// HH:mm:ss
        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy-MM-");
        Date date = new Date(System.currentTimeMillis());
        String startTime = simpleDateFormat1.format(date)+"01";

        left_month.setText(startTime);
        right_month.setText(simpleDateFormat.format(date));


        webView = (WebView) findViewById(R.id.webview);
         url = getIntent().getStringExtra("url");
        String titleString = getIntent().getStringExtra("title");
        TextView m_title = (TextView)findViewById(R.id.common_navi_title);
        m_title.setText(titleString);

//        webView.loadUrl("file:///android_asset/test.html");//加载asset文件夹下html
//        webView.loadUrl(url);//加载url

        //使用webview显示html代码
//        webView.loadDataWithBaseURL(null,"<html><head><title> 欢迎您 </title></head>" +
//                "<body><h2>使用webview显示 html代码</h2></body></html>", "text/html" , "utf-8", null);

        webView.addJavascriptInterface(this,"android");//添加js监听 这样html就能调用客户端
        webView.setWebChromeClient(webChromeClient);
        webView.setWebViewClient(webViewClient);

        WebSettings webSettings=webView.getSettings();
        webSettings.setJavaScriptEnabled(true);//允许使用js

        /**
         * LOAD_CACHE_ONLY: 不使用网络，只读取本地缓存数据
         * LOAD_DEFAULT: （默认）根据cache-control决定是否从网络上取数据。
         * LOAD_NO_CACHE: 不使用缓存，只从网络获取数据.
         * LOAD_CACHE_ELSE_NETWORK，只要本地有，无论是否过期，或者no-cache，都使用缓存中的数据。
         */
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);//不使用缓存，只从网络获取数据.

        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);

        String weburl = url+"?start="+left_month.getText().toString()+"&end="+right_month.getText().toString()
                +"&id="+ LoginUserUtil.getUserId(EmployeeReportWebviewActivity.this);
        webView.loadUrl(weburl);
        //支持屏幕缩放

        //不显示webview缩放按钮
//        webSettings.setDisplayZoomControls(false);
    }

    //WebViewClient主要帮助WebView处理各种通知、请求事件
    private WebViewClient webViewClient=new WebViewClient(){
        @Override
        public void onPageFinished(WebView view, String url) {//页面加载完成
            progressBar.setVisibility(View.GONE);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {//页面开始加载
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.i("ansen","拦截url:"+url);
            if(url.equals("http://www.google.com/")){
                Toast.makeText(EmployeeReportWebviewActivity.this,"国内不能访问google,拦截该url",Toast.LENGTH_LONG).show();
                return true;//表示我已经处理过了
            }
            return super.shouldOverrideUrlLoading(view, url);
        }

    };

    //WebChromeClient主要辅助WebView处理Javascript的对话框、网站图标、网站title、加载进度等
    private WebChromeClient webChromeClient=new WebChromeClient(){
        //不支持js的alert弹窗，需要自己监听然后通过dialog弹窗
        @Override
        public boolean onJsAlert(WebView webView, String url, String message, JsResult result) {
//            AlertDialog.Builder localBuilder = new AlertDialog.Builder(webView.getContext());
//            localBuilder.setMessage(message).setPositiveButton("确定",null);
//            localBuilder.setCancelable(false);
//            localBuilder.create().show();
//
//            //注意:
//            //必须要这一句代码:result.confirm()表示:
//            //处理结果为确定状态同时唤醒WebCore线程
//            //否则不能继续点击按钮
//            result.confirm();
            return true;
        }

        //获取网页标题
        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            Log.i("ansen","网页标题:"+title);
        }

        //加载进度回调
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            progressBar.setProgress(newProgress);
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.i("ansen","是否有上一个页面:"+webView.canGoBack());
        if (webView.canGoBack() && keyCode == KeyEvent.KEYCODE_BACK){//点击返回按钮的时候判断有没有上一页
            webView.goBack(); // goBack()表示返回webView的上一页面
            return true;
        }
        return super.onKeyDown(keyCode,event);
    }

    /**
     * JS调用android的方法
     * @param str
     * @return
     */
    @JavascriptInterface //仍然必不可少
    public void  getClient(String str){
        Log.i("ansen","html调用客户端:"+str);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //释放资源
        webView.destroy();
        webView=null;
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.left_month:
                m_selectTimeType = 1;
                selectDate();
                break;
            case R.id.right_month:
                m_selectTimeType = 2;
                selectDate();
                break;
            case R.id.start:
                String weburl = url+"?start="+left_month.getText().toString()+"&end="+right_month.getText().toString()
                        +"&id="+ LoginUserUtil.getUserId(EmployeeReportWebviewActivity.this);
                webView.loadUrl(weburl);
                break;
        }

    }

    public void selectDate() {
        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                EmployeeReportWebviewActivity.this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );

        dpd.setVersion(DatePickerDialog.Version.VERSION_2);
        dpd.show(getFragmentManager(), "Datepickerdialog");
    }

    private int m_selectTimeType = 0;
    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        int month = monthOfYear + 1;
        String date = year + (month > 9 ? ("-"+month) : ("-0"+month)) + ( dayOfMonth > 9 ?  ("-"+dayOfMonth) : ("-0"+dayOfMonth));
        if(m_selectTimeType == 1){
            left_month.setText(date);
        }else if(m_selectTimeType == 2){
            right_month.setText(date);
        }
    }


}
