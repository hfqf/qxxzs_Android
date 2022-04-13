package Utils;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import android.app.FragmentManager;
import android.content.Context;
import java.util.Calendar;

/**
 * @author hfqf123
 * @date 4/13/22
 * @description:
 */
public class SpeDateHelper {
    public static void  show(DatePickerDialog.OnDateSetListener listener, FragmentManager f){
        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                listener,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );

        dpd.setVersion(DatePickerDialog.Version.VERSION_2);
        dpd.show(f, "Datepickerdialog");
    }
}
