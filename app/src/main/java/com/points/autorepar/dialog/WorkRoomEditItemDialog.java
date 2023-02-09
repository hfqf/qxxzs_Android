package com.points.autorepar.dialog;
import android.content.Context;
import android.support.design.widget.BottomSheetDialog;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.points.autorepar.R;
import com.points.autorepar.bean.ADTReapirItemInfo;
import com.points.autorepar.bean.RepairHistory;
import com.points.autorepar.http.submodule.repair.HttpManagerRepairUtil;
import com.points.autorepar.lib.BluetoothPrinter.util.ToastUtil;
import com.points.autorepar.utils.NumberFilterUtil;

import org.json.JSONObject;

import RxJava.PostSucceedCallback;
import RxJava.RxViewHelper;

public class WorkRoomEditItemDialog extends BottomSheetDialog {
    private ADTReapirItemInfo item;
    private Context context;
    private Window mWindow;
    public WorkRoomEditItemDialog(Context context) {
        super(context);
        mWindow = getWindow();
    }

    public static void editItem(Context context, ADTReapirItemInfo item, PostSucceedCallback callback){
        WorkRoomEditItemDialog dialog = new WorkRoomEditItemDialog(context);
        dialog.context = context;
        dialog.item = item;
        dialog.setContentView(R.layout.activity_workroom_edit_item);
        EditText name =  dialog.findViewById(R.id.item_edit_name);
        EditText price =  dialog.findViewById(R.id.item_edit_price);
        EditText num =  dialog.findViewById(R.id.item_edit_num);
        TextView cancel =  dialog.findViewById(R.id.item_edit_cancel);
        TextView confirm =  dialog.findViewById(R.id.item_edit_confirm);
        name.setText(item.type);
        price.setText(item.price);
        num.setText(item.num);
        RxViewHelper.textChange(name,(s)->{
            item.type = s.toString();
        });
        RxViewHelper.textChange(price,(s)->{
            item.price = s.toString();
        });
        RxViewHelper.textChange(num,(s)->{
            item.num = s.toString();
        });

        RxViewHelper.clickWith(cancel,()->{
            dialog.dismiss();
        });

        RxViewHelper.clickWith(confirm,()->{
            if(!NumberFilterUtil.isInteger(item.price)){
                ToastUtil.showToast(context,"价格只能输入整数");
                return;
            }
            if(!NumberFilterUtil.isInteger(item.num)){
                ToastUtil.showToast(context,"数量只能输入整数");
                return;
            }
            HttpManagerRepairUtil.upDateRepairItem(context, item, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    ToastUtil.showToast(context,"编辑成功");
                    callback.onPostSucceed();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    ToastUtil.showToast(context,"编辑失败");
                }
            });
            dialog.dismiss();
        });

        dialog.show();
    }
}
