package com.points.autorepar.activity;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.points.autorepar.R;
import com.points.autorepar.dialog.PrivacyBottomDialog;
import com.points.autorepar.utils.LoginUserUtil;

public class PrivacyActivity extends BaseActivity {
    private Button agreeBtn;
    private Button disagreeBtn;
    private boolean fromSetting;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_user_privacy);
        WebView web =findViewById(R.id.webview);
        web.loadUrl("http://www.autorepairehelper.cn/noticeboard/yssm");
        web.setWebViewClient(new WebViewClient());

        RelativeLayout naviLayout =  (RelativeLayout)this.findViewById(R.id.contact_adduser_navi);
        Button  mAddBtn = (Button)naviLayout.findViewById(R.id.common_navi_add);
        mAddBtn.setVisibility(View.INVISIBLE);

        TextView  textView1 = (TextView)naviLayout.findViewById(R.id.common_navi_title);
        textView1.setText("隐私政策和用户协议");

        Button mBackBtn = (Button)naviLayout.findViewById(R.id.common_navi_back);
        mBackBtn.setVisibility(View.VISIBLE);
        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

         String first = getIntent().getStringExtra("first");
         if(first==null){
             naviLayout.setVisibility(View.GONE);
             fromSetting = false;
         }else {
             if(first.equalsIgnoreCase("0")){
                 fromSetting = true;
             }else {
                 fromSetting = false;
                 naviLayout.setVisibility(View.GONE);
             }
         }

        agreeBtn = (Button)findViewById(R.id.btn_agree);
        agreeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginUserUtil.setPrivacy(getApplication());
                finish();
            }
        });

        disagreeBtn = (Button)findViewById(R.id.btn_disagree);
        disagreeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.exit(0);
            }
        });


        if(fromSetting){
            agreeBtn.setVisibility(View.GONE);
            disagreeBtn.setVisibility(View.GONE);
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && fromSetting) {
            return true;
        }
        return false;
    }

}
