package com.points.autorepar.dbutil.bean;

import java.util.ArrayList;

public class HFLocalDBSettingModel {
    public String dbName;
    public String dbVersion;
    public ArrayList<HFLocalDBColumnSettingModel> dbTables;
    //需要升级的sql语句数组，可直接执行
    public ArrayList<String> arrWillExecSQL;
}
