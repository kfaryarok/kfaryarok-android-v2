package io.github.kfaryarok.android.settings.prefs;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.widget.NumberPicker;

/**
 * Extension of NumberPicker for picking grades.
 * Has 6 options, each options representing a grade.
 *
 * @author tbsc on 26/12/2017
 */
public class GradePicker extends NumberPicker {

    public GradePicker(Context context) {
        super(context);
    }

    public GradePicker(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GradePicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public GradePicker(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

}
