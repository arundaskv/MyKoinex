package wallet.mycoin.model;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.EditText;

/**
 * Created by Arun.Das on 27-12-2017.
 */

public class CustomEditText extends android.support.v7.widget.AppCompatEditText {
    public CustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if ((event.getKeyCode() == KeyEvent.KEYCODE_BACK)) {
            clearFocus();
            return true;  // So it is not propagated.
        }
        return false;
    }
}
