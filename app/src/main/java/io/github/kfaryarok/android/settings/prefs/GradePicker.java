package io.github.kfaryarok.android.settings.prefs;

import android.content.Context;
import android.text.InputFilter;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.NumberPicker;

import java.lang.reflect.Field;

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
            Field f = NumberPicker.class.getDeclaredField("mInputText");
            f.setAccessible(true);
            EditText inputText = (EditText) f.get(picker);
            inputText.setFilters(new InputFilter[0]);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        // doesn't work
//        try {
//            Method method = picker.getClass().getDeclaredMethod("changeValueByOne", boolean.class);
//            method.setAccessible(true);
//            method.invoke(picker, true);
//        } catch (NoSuchMethodException e) {
//            e.printStackTrace();
//        } catch (IllegalArgumentException e) {
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        } catch (InvocationTargetException e) {
//            e.printStackTrace();
//        }
    }

}
