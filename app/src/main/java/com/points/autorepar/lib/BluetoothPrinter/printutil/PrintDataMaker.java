package com.points.autorepar.lib.BluetoothPrinter.printutil;

import com.points.autorepar.bean.RepairHistory;

import java.util.List;

/**
 * Print
 * * Created by liugruirong on 2017/8/3.
 */

public interface PrintDataMaker {
    List<byte[]> getPrintData(int type);
    List<byte[]> getPrintPageData(int type,RepairHistory m_currentData);
}
