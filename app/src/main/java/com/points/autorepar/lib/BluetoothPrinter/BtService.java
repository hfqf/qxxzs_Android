package com.points.autorepar.lib.BluetoothPrinter;

import android.app.IntentService;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;


import com.points.autorepar.R;
import com.points.autorepar.bean.RepairHistory;
import com.points.autorepar.lib.BluetoothPrinter.print.GPrinterCommand;
import com.points.autorepar.lib.BluetoothPrinter.print.PrintPic;
import com.points.autorepar.lib.BluetoothPrinter.print.PrintQueue;
import com.points.autorepar.lib.BluetoothPrinter.print.PrintUtil;
import com.points.autorepar.lib.BluetoothPrinter.printutil.PrintOrderDataMaker;
import com.points.autorepar.lib.BluetoothPrinter.printutil.PrinterWriter;
import com.points.autorepar.lib.BluetoothPrinter.printutil.PrinterWriter58mm;

import java.io.BufferedInputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * Created by liuguirong on 8/1/17.
 * <p/>
 * print ticket service
 */
public class BtService extends IntentService {

    public BtService() {
        super("BtService");
    }
    private RepairHistory m_currentData;
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public BtService(String name) {

        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        if (intent == null || intent.getAction() == null) {
            return;
        }

        m_currentData = intent.getParcelableExtra(String.valueOf(R.string.key_repair_edit_para));

        if (intent.getAction().equals(PrintUtil.ACTION_PRINT_TEST)) {
            printTest();
        } else if (intent.getAction().equals(PrintUtil.ACTION_PRINT_TEST_TWO)) {
            printTesttwo(3);
        }else if (intent.getAction().equals(PrintUtil.ACTION_PRINT_BITMAP)) {
            printBitmapTest();
        }else if(intent.getAction().equals(PrintUtil.ACTION_PRINT_PAGE))
        {
            printPage();
        }

    }

    private void printTest() {
            PrintOrderDataMaker printOrderDataMaker = new PrintOrderDataMaker(this,"", PrinterWriter58mm.TYPE_58, PrinterWriter.HEIGHT_PARTING_DEFAULT);
            ArrayList<byte[]> printData = (ArrayList<byte[]>) printOrderDataMaker.getPrintData(PrinterWriter58mm.TYPE_58);
            PrintQueue.getQueue(getApplicationContext()).add(printData);

    }

    private void printPage(){
        PrintOrderDataMaker printOrderDataMaker = new PrintOrderDataMaker(this,"", PrinterWriter58mm.TYPE_58, PrinterWriter.HEIGHT_PARTING_DEFAULT);
        ArrayList<byte[]> printData = (ArrayList<byte[]>) printOrderDataMaker.getPrintPageData(PrinterWriter58mm.TYPE_58,m_currentData);
        PrintQueue.getQueue(getApplicationContext()).add(printData);
    }

    /**
     * ????????????
     * @param num
     */
  private void printTesttwo(int num) {
        try {
            ArrayList<byte[]> bytes = new ArrayList<byte[]>();
            for (int i = 0; i < num; i++) {
                String message = "??????????????????\n??????????????????\n??????????????????\n\n";
                bytes.add(GPrinterCommand.reset);
                bytes.add(message.getBytes("gbk"));
                bytes.add(GPrinterCommand
                        .print);
                bytes.add(GPrinterCommand.print);
                bytes.add(GPrinterCommand.print);
            }
            PrintQueue.getQueue(getApplicationContext()).add(bytes);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void print(byte[] byteArrayExtra) {
        if (null == byteArrayExtra || byteArrayExtra.length <= 0) {
            return;
        }
        PrintQueue.getQueue(getApplicationContext()).add(byteArrayExtra);
    }

    private void printBitmapTest() {
        BufferedInputStream bis;
        try {
            bis = new BufferedInputStream(getAssets().open(
                    "qxxzs_czb.bmp"));
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        Bitmap bitmap = BitmapFactory.decodeStream(bis);
        PrintPic printPic = PrintPic.getInstance();
        printPic.init(bitmap);
        if (null != bitmap) {
            if (bitmap.isRecycled()) {
                bitmap = null;
            } else {
                bitmap.recycle();
                bitmap = null;
            }
        }
        byte[] bytes = printPic.printDraw();
        ArrayList<byte[]> printBytes = new ArrayList<byte[]>();
        printBytes.add(GPrinterCommand.reset);
        printBytes.add(GPrinterCommand.print);
        printBytes.add(bytes);
        Log.e("BtService", "image bytes size is :" + bytes.length);
        printBytes.add(GPrinterCommand.print);
        PrintQueue.getQueue(getApplicationContext()).add(bytes);
    }
//
//    private void printPainting() {
//        byte[] bytes = PrintPic.getInstance().printDraw();
//        ArrayList<byte[]> printBytes = new ArrayList<byte[]>();
//        printBytes.add(GPrinterCommand.reset);
//        printBytes.add(GPrinterCommand.print);
//        printBytes.add(bytes);
//        Log.e("BtService", "image bytes size is :" + bytes.length);
//        printBytes.add(GPrinterCommand.print);
//        PrintQueue.getQueue(getApplicationContext()).add(bytes);
//    }
}