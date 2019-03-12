package com.points.autorepar.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.points.autorepar.R;
import com.points.autorepar.platerecognizer.base.BitmapCache;

import java.util.List;

/**
 * Created by Administrator on 2016/1/18.
 */
public class ImgAdapter extends BaseAdapter {
    private Context context;
    private Activity m_activity;
    private List<String> bitmapList;
    private onItemClickLis onItemClickLis;
    public ImageLoader imageLoader;
    public RequestQueue mQueue;
    public boolean showflag;

    public BitmapCache lruImageCache;
    public void setOnItemClickLis(ImgAdapter.onItemClickLis onItemClickLis) {
        this.onItemClickLis = onItemClickLis;
    }

    public ImgAdapter(Context context, List<String> bitmapList,Boolean showflag) {
        this.context = context;
//        this.m_activity = activity;
        this.bitmapList = bitmapList;
        this.showflag = showflag;
        mQueue = Volley.newRequestQueue(context);

        lruImageCache = new BitmapCache();

        imageLoader = new ImageLoader(mQueue,lruImageCache);
    }

    public void setData(List<String> bitmapList)
    {
        this.bitmapList = bitmapList;
    }
    @Override
    public int getCount() {
        return bitmapList.size();
    }

    @Override
    public Object getItem(int position) {
        return bitmapList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_img, null);
            holder = new ViewHolder();
            holder.img_item = (ImageView) convertView.findViewById(R.id.img_item);
            holder.img_marke = (ImageView) convertView.findViewById(R.id.img_marke);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        String path = (String)bitmapList.get(position);
        boolean flag =path.startsWith("http");
        final ImageView image = holder.img_item;

        if(showflag)
        {
            holder.img_marke.setVisibility(View.VISIBLE);
        }else {
            holder.img_marke.setVisibility(View.GONE);

        }
        if(flag)
        {
                imageLoader.get(path, new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {
                    image.setImageBitmap(imageContainer.getBitmap());
                }

                @Override
                public void onErrorResponse(VolleyError volleyError) {

                }
            },200,200);
        }else{
            Bitmap bitmap = BitmapUtils.decodeSampledBitmapFromSd(path, 200, 200);
            holder.img_item.setImageBitmap(bitmap);
        }

        //回调调用
        final ViewHolder finalHolder = holder;
        holder.img_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickLis.onItemClick(finalHolder.img_marke,position);
            }
        });
        return convertView;
    }

    //接口回调
    public interface onItemClickLis {
        public void onItemClick(ImageView img_marke, int position);
    }

    public class ViewHolder {
        private ImageView img_item;
        private ImageView img_marke;
    }
}
