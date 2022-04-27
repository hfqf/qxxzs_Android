package RxJava;

import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxCompoundButton;
import com.jakewharton.rxbinding2.widget.RxTextView;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import io.reactivex.android.schedulers.AndroidSchedulers;


public class RxViewHelper {
    public static void clickWith(View v,ClickCallback callBack) {
        if (v == null) {
            return;
        }
        RxView.clicks(v)
                .throttleFirst(2, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(o -> callBack.handleClick());
    }

    public static void longClickWith(View v,ClickCallback callBack) {
        if (v == null) {
            return;
        }
        RxView.longClicks(v)
                .throttleFirst(2, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(o -> callBack.handleClick());
    }

    public static void textChange(TextView tv, TextChangeCallback callback) {
        if (tv == null) {
            return;
        }
        RxTextView.textChanges(tv)
                .debounce(200, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(charSequence -> callback.textChanged(charSequence));
    }
}
