package io.github.kfaryarok.android.settings.prefs;

import android.content.Context;
import android.text.InputFilter;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.NumberPicker;

import java.lang.reflect.Field;

import io.github.kfaryarok.android.R;

/**
 * Extension of NumberPicker for picking grades.
 * Has 6 options, each options representing a grade.
 *
 * @author tbsc on 26/12/2017
 */
public class GradePicker extends NumberPicker {

    public GradePicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFormatter(value -> convertGradePickerValueToFormattedString(getContext(), value));
        setMinValue(7);
        setMaxValue(12);
        setWrapSelectorWheel(false);
        setSelected(false);
    }

    public static String convertGradePickerValueToFormattedString(Context ctx, int gradeNum) {
        switch (gradeNum) {
            case 7:
                return ctx.getString(R.string.grade_g_formatted);
            case 8:
                return ctx.getString(R.string.grade_h_formatted);
            case 9:
                return ctx.getString(R.string.grade_i_formatted);
            case 10:
                return ctx.getString(R.string.grade_j_formatted);
            case 11:
                return ctx.getString(R.string.grade_k_formatted);
            case 12:
                return ctx.getString(R.string.grade_l_formatted);
            default:
                return null;
        }
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
    }

}
