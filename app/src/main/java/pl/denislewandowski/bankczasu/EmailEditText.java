package pl.denislewandowski.bankczasu;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

public class EmailEditText extends android.support.v7.widget.AppCompatEditText {
    public EmailEditText(Context context) {
        super(context);
    }

    public EmailEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EmailEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public int getAutofillType() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return View.AUTOFILL_TYPE_NONE;
        } else {
            return super.getAutofillType();
        }
    }
}
