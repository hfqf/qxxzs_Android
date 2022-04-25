package com.points.autorepar.adapter.WorkRoomCarInfo;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * 多行显示图片组件
 */
public class WorkRoomCarInfoPicsRecycleView extends RecyclerView {
    private static int MAX_ROW = 3;
    public WorkRoomCarInfoPicsRecycleView(Context context) {
        super(context);
        setLayoutManager(new GridLayoutManager(context,MAX_ROW));
    }
}
