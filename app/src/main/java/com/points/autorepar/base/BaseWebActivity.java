package com.points.autorepar.base;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.just.agentweb.AgentWeb;
import com.just.agentweb.DefaultWebClient;
import com.points.autorepar.MainApplication;
import com.points.autorepar.R;

/**
 * Created by cenxiaozhong on 2017/5/26.
 * <p>
 * source code  https://github.com/Justson/AgentWeb
 */

public class BaseWebActivity extends AppCompatActivity {


    protected AgentWeb mAgentWeb;
    private LinearLayout mLinearLayout;
//    private Toolbar mToolbar;
    private TextView mTitleTextView;
    private AlertDialog mAlertDialog;
    private String url;
    private String title;
    private Button common_navi_add;
    private Button common_back;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_web);

        MainApplication mainApplication = (MainApplication) getApplication();
        mLinearLayout = (LinearLayout) this.findViewById(R.id.container);

        mTitleTextView = (TextView) this.findViewById(R.id.common_navi_title);
        common_navi_add = (Button)this.findViewById(R.id.common_navi_add);
        common_navi_add.setVisibility(View.INVISIBLE);

        common_back =  (Button)findViewById(R.id.common_navi_back);
        common_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        long p = System.currentTimeMillis();
        String m_title = getTitleString();
        mTitleTextView.setText(m_title);
        mAgentWeb =
                AgentWeb.with(this)
                .setAgentWebParent(mLinearLayout, new LinearLayout.LayoutParams(-1, -1))
                .useDefaultIndicator()
                .setWebChromeClient(mWebChromeClient)
                .setWebViewClient(mWebViewClient)
                .setMainFrameErrorView(R.layout.agentweb_error_page, -1)
                .setSecurityType(AgentWeb.SecurityType.STRICT_CHECK)
                .setWebLayout(new WebLayout(this))
                .setOpenOtherPageWays(DefaultWebClient.OpenOtherPageWays.ASK)//??????????????????????????????????????????????????????????????????
                .interceptUnkownUrl() //??????????????????????????????Scheme
                .createAgentWeb()
                .ready()
                .go(getUrl());

        //mAgentWeb.getUrlLoader().loadUrl(getUrl());
        long n = System.currentTimeMillis();
        Log.i("Info", "init used time:" + (n - p));
    }

    private WebViewClient mWebViewClient = new WebViewClient() {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            return super.shouldOverrideUrlLoading(view, request);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            //do you  work
            Log.i("Info", "BaseWebActivity onPageStarted");
        }
    };
    private WebChromeClient mWebChromeClient = new WebChromeClient() {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            //do you work
//            Log.i("Info","onProgress:"+newProgress);
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
//            if (mTitleTextView != null) {
//                mTitleTextView.setText(title);
//            }
        }
    };

    public String getUrl() {

//        return "https://m.jd.com/";
//        return "file:///android_asset/index.html";
        return url;
    }

    public void setTitleString (String Title){
        this.title = Title;
    }

    public String getTitleString() {

//        return "https://m.jd.com/";
//        return "file:///android_asset/index.html";
        return this.title;
    }

    public void setUrl (String url){
        this.url = url;
    }

    private void showDialog() {

        if (mAlertDialog == null) {
            mAlertDialog = new AlertDialog.Builder(this)
                    .setMessage("???????????????????????????????")
                    .setNegativeButton("?????????", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (mAlertDialog != null) {
                                mAlertDialog.dismiss();
                            }
                        }
                    })//
                    .setPositiveButton("??????", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            if (mAlertDialog != null) {
                                mAlertDialog.dismiss();
                            }
                            BaseWebActivity.this.finish();
                        }
                    }).create();
        }
        mAlertDialog.show();

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (mAgentWeb.handleKeyEvent(keyCode, event)) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onPause() {
        mAgentWeb.getWebLifeCycle().onPause();
        super.onPause();

    }

    @Override
    protected void onResume() {
        mAgentWeb.getWebLifeCycle().onResume();
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.i("Info", "onResult:" + requestCode + " onResult:" + resultCode);
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //mAgentWeb.destroy();
        mAgentWeb.getWebLifeCycle().onDestroy();
    }
}
