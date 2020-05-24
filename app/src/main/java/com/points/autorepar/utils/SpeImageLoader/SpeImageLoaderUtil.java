package com.points.autorepar.utils.SpeImageLoader;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

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
}
