package com.points.autorepar.utils.SpeImageLoader;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.points.autorepar.R;
import com.points.autorepar.common.Consts;

public class SpeImageLoaderUtil {
    public  static void loadImage(Context context, ImageView image, String url, Drawable placeholder, Drawable error){
        Glide.with(context)
                .load(url)
                .placeholder(placeholder)
                .error(error)
                .into(image);
    }

    public  static void loadImage(Context context, ImageView image, String url, int placeholder, int error){
        Glide.with(context)
                .load(url)
                .placeholder(placeholder)
                .error(error)
                .into(image);
    }


    public  static void loadUrlImage(Context context, ImageView image, String url){
        String _url = "";
        if(url.contains("png")){
            _url = Consts.HTTP_URL+"/file/pic/"+url;
        }else {
            _url = Consts.HTTP_URL+"/file/pic/"+url+".png";
        }
        Glide.with(context)
                .load(_url)
                .placeholder(R.drawable.appicon)
                .error(R.drawable.appicon)
                .into(image);
    }
}
