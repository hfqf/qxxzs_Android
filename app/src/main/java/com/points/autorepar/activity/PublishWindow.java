package com.points.autorepar.activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.points.autorepar.R;
import com.points.autorepar.fragment.ContactFragment;


/**
 * Created by zachary on 2017/2/28.
 */

public class PublishWindow extends PopupWindow implements View.OnClickListener {
    private Context context;
    private LayoutInflater inflater;
    private View mview;
    private ImageButton imgbtn_1_1,imgbtn_1_2,imgbtn_1_3,imgbtn_2_1,imgbtn_2_2,imgbtn_2_3;
    private LinearLayout ln_1_1,ln_1_2,ln_1_3,ln_2_1,ln_2_2,ln_2_3;
    private ImageButton btn_close;
    private RelativeLayout rl_mainarea;
    private RelativeLayout rl_bottomarea;
    private Animation anim_fade_in,anim_fade_out;
    private Handler handler;

    //回调
    private PublishWindow.OnPublishWindowListener mListener;

    //回调接口
    public interface OnPublishWindowListener {
        //TODO 可自定义回调函数
        public void onPublishWindowSelectedIndex(int ViewId);
    }


    public PublishWindow(Context context){
        this.context = context;
        mListener = (PublishWindow.OnPublishWindowListener) context;
        initView();
    }

    private void initView(){
        inflater = LayoutInflater.from(context);
        mview = inflater.inflate(R.layout.publishwindow,null);
        setContentView(mview);
        this.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);

        handler = new Handler();
        //this.setBackgroundDrawable(new ColorDrawable(Color.argb(255, 255, 255, 255)));

        btn_close = (ImageButton)mview.findViewById(R.id.btn_close);
        ln_1_1 = (LinearLayout) mview.findViewById(R.id.ln_1_1);
        ln_1_2 = (LinearLayout) mview.findViewById(R.id.ln_1_2);
        ln_1_3 = (LinearLayout) mview.findViewById(R.id.ln_1_3);
        ln_2_1 = (LinearLayout) mview.findViewById(R.id.ln_2_1);
        ln_2_2 = (LinearLayout)mview.findViewById(R.id.ln_2_2);
        ln_2_3 = (LinearLayout) mview.findViewById(R.id.ln_2_3);

        rl_mainarea = (RelativeLayout)mview.findViewById(R.id.rl_mainarea);
        rl_bottomarea = (RelativeLayout)mview.findViewById(R.id.rl_bottomarea);

       // btn_close.setOnClickListener(this);
        rl_mainarea.setOnClickListener(this);
        rl_bottomarea.setOnClickListener(this); //点击此区域关闭弹窗
        imgbtn_1_1 = (ImageButton) mview.findViewById(R.id.btn_article);
        imgbtn_1_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mListener.onPublishWindowSelectedIndex(0);
                closePublishView();

            }
        });
        imgbtn_1_2 = (ImageButton)mview.findViewById(R.id.btn_photo);
        imgbtn_1_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onPublishWindowSelectedIndex(1);
                closePublishView();
            }
        });
        imgbtn_1_3 = (ImageButton)mview.findViewById(R.id.btn_news);
        imgbtn_1_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onPublishWindowSelectedIndex(2);
                closePublishView();
            }
        });

        openPublishVew();

    }

    private void openPublishVew(){
        ln_1_1.setVisibility(View.INVISIBLE);
        ln_1_2.setVisibility(View.INVISIBLE);
        ln_1_3.setVisibility(View.INVISIBLE);
        ln_2_1.setVisibility(View.INVISIBLE);
        ln_2_2.setVisibility(View.INVISIBLE);
        ln_2_3.setVisibility(View.INVISIBLE);
        rl_mainarea.startAnimation(AnimationUtils.loadAnimation(context,R.anim.anim_fade_in));
        btn_close.startAnimation(AnimationUtils.loadAnimation(context,R.anim.anim_rotate_right));
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ln_1_1.setVisibility(View.VISIBLE);
                ln_1_1.startAnimation(AnimationUtils.loadAnimation(context,R.anim.anim_translate_up));
            }
        },100);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ln_1_2.setVisibility(View.VISIBLE);
                ln_1_2.startAnimation(AnimationUtils.loadAnimation(context,R.anim.anim_translate_up));
            }
        },200);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ln_1_3.setVisibility(View.VISIBLE);
                ln_1_3.startAnimation(AnimationUtils.loadAnimation(context,R.anim.anim_translate_up));
            }
        },300);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ln_2_1.setVisibility(View.INVISIBLE);
                ln_2_1.startAnimation(AnimationUtils.loadAnimation(context,R.anim.anim_translate_up));
            }
        },400);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ln_2_2.setVisibility(View.INVISIBLE);
                ln_2_2.startAnimation(AnimationUtils.loadAnimation(context,R.anim.anim_translate_up));
            }
        },500);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ln_2_3.setVisibility(View.INVISIBLE);
                ln_2_3.startAnimation(AnimationUtils.loadAnimation(context,R.anim.anim_translate_up));
            }
        },600);



    }

    private void closePublishView(){
        btn_close.startAnimation(AnimationUtils.loadAnimation(context,R.anim.anim_rotate_left));
        rl_mainarea.startAnimation(AnimationUtils.loadAnimation(context,R.anim.anim_fade_out));
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                dismiss();
            }
        }, 600);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ln_1_1.startAnimation(AnimationUtils.loadAnimation(context,R.anim.anim_translate_down));
                ln_1_1.setVisibility(View.INVISIBLE);
            }
        },300);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
//                ln_1_2.startAnimation(AnimationUtils.loadAnimation(context,R.anim.anim_translate_down));
                ln_1_2.setVisibility(View.INVISIBLE);
            }
        },250);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
//                ln_1_3.startAnimation(AnimationUtils.loadAnimation(context,R.anim.anim_translate_down));
                ln_1_3.setVisibility(View.INVISIBLE);
            }
        },200);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ln_2_1.startAnimation(AnimationUtils.loadAnimation(context,R.anim.anim_translate_down));
                ln_2_1.setVisibility(View.INVISIBLE);
            }
        },150);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ln_2_2.startAnimation(AnimationUtils.loadAnimation(context,R.anim.anim_translate_down));
                ln_2_2.setVisibility(View.INVISIBLE);
            }
        },100);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ln_2_3.startAnimation(AnimationUtils.loadAnimation(context,R.anim.anim_translate_down));
                ln_2_3.setVisibility(View.INVISIBLE);
            }
        },50);

        mListener = null;
    }

    @Override
    public void onClick(View view) {

                closePublishView();

        }
}
