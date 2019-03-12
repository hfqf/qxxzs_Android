package com.points.autorepar.bean;

/**
 * Created by points on 16/11/25.
 */



public class WorkRoomEvent {



    private RepairHistory mMsg;
    public WorkRoomEvent(RepairHistory msg) {
        // TODO Auto-generated constructor stub
        mMsg = msg;
    }
    public RepairHistory getMsg(){
        return mMsg;
    }


}
