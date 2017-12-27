package io.github.kfaryarok.android.settings.prefs;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.widget.NumberPicker;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Extension of NumberPicker for picking grades.
 * Has 6 options, each options representing a grade.
 * TODO: When an activity shows with this view, it's selected and it causes the selected value to not show. Fix it
 *
 * @author tbsc on 26/12/2017
 */
public class GradePicker extends NumberPicker {

    public GradePicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFormatter(value -> ClassPreferenceDialogFragmentCompat.convertGradePickerValueToString(getContext(), value));
        setMinValue(7);
        setMaxValue(12);
        setWrapSelectorWheel(false);
        setSelected(false);
    }

    /**
     * Must be called where ever GradePicker is used, for fixing the formatting of the first value.
     */
    public static void fixFormatting(GradePicker picker) {
        try {
            Method method = picker.getClass().getDeclaredMethod("changeValueByOne", boolean.class);
            method.setAccessible(true);
            method.invoke(picker, true);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

}
