package com.points.autorepar.adapter.WorkRoomCarInfo;

import android.content.Context;
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
import com.points.autorepar.dialog.SpeDialogUtil;

import java.util.ArrayList;

import RxJava.RxViewHelper;

public class WorkRommCarInfoPicsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<String> arrayList;
    private Context context;
    private WorkRoomCarInfoPicsRecycleViewModel vm;
   public WorkRommCarInfoPicsAdapter(Context context,ArrayList<String> array){
       this.context = context;
       this.arrayList = array;
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
        });
        RxViewHelper.longClickWith(holder.imageView,()->{
            SpeDialogUtil.showDialog(context,"确认删除?",()->{

            });
        });
        Glide.with(context).load(url).into(holder.imageView).onLoadFailed(context.getResources().getDrawable(R.drawable.appicon));;
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }
}


class ItemHolder extends RecyclerView.ViewHolder{
    ImageView imageView;
    public ItemHolder(View itemView) {
        super(itemView);
        imageView = (ImageView) itemView.findViewById(R.id.item_imageview);
    }
}
