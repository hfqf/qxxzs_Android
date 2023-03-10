package com.points.autorepar.adapter.WorkRoomCarInfo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.points.autorepar.R;
import com.points.autorepar.activity.ImgDisplayActivity;
import com.points.autorepar.dialog.SpeDialogUtil;

import java.io.Serializable;
import java.util.ArrayList;

import ImageUtil.preview.SpeImagePreviewUtil;
import RxJava.RxViewHelper;

public class WorkRommCarInfoPicsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<String> arrayList;
    private Context context;
    private WorkRoomCarInfoPicsRecycleViewModel vm;
    private WorkRommCarInfoPicsAdapterInterface delegate;
    public interface WorkRommCarInfoPicsAdapterInterface {
        public void onDelPic(String url);
    }
   public WorkRommCarInfoPicsAdapter(Context context,ArrayList<String> array,WorkRommCarInfoPicsAdapterInterface delegate){
       this.context = context;
       this.arrayList = array;
       this.delegate = delegate;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ItemHolder(LayoutInflater.from(context).inflate(R.layout.workroomcarinfo_pics_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        String url = arrayList.get(i);
        ItemHolder holder = (ItemHolder) viewHolder;
        RxViewHelper.clickWith(holder.imageView,()->{
            Log.i("onBindViewHolder",url);
            checkFullImage(i);
        });
        RxViewHelper.longClickWith(holder.imageView,()->{
            SpeDialogUtil.showDialog(context,"确认删除?",()->{
                  if(this.delegate!=null){
                    this.delegate.onDelPic(url);
                }
            });
        });
        Glide.with(context).load(url).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    private void checkFullImage(int index){
        SpeImagePreviewUtil.preivew((Activity) context,index,arrayList);
    }
}


class ItemHolder extends RecyclerView.ViewHolder{
    ImageView imageView;
    public ItemHolder(View itemView) {
        super(itemView);
        imageView = (ImageView) itemView.findViewById(R.id.item_imageview);
    }
}


