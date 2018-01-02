/*
 * This file is part of kfaryarok-android.
 *
 * kfaryarok-android is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * kfaryarok-android is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with kfaryarok-android.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.kfaryarok.android.settings.prefs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.preference.DialogPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceDialogFragmentCompat;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.Toast;

import io.github.kfaryarok.android.R;
import io.github.kfaryarok.android.util.ClassUtil;
import io.github.kfaryarok.android.util.LayoutUtil;

/**
 * Fragment for the TimePreference to show when clicked, and to have control of it.
 *
 * @author tbsc on 10/03/2017 (copied from v1)
 */
public class ClassPreferenceDialogFragmentCompat extends PreferenceDialogFragmentCompat implements DialogPreference.TargetFragment {

    GradePicker gradePicker;
    NumberPicker classNumPicker;
    LinearLayout selectorLinearLayout;

    private Toast toast;

    @Override
    protected View onCreateDialogView(Context context) {
        return super.onCreateDialogView(context);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        if (dialog.getWindow() != null) {
            LayoutUtil.setDirection(dialog.getWindow().getDecorView(), LayoutUtil.RTL);
        }
        return dialog;
    }

    @Override
    protected void onBindDialogView(View v) {
        super.onBindDialogView(v);

        gradePicker = v.findViewById(R.id.gp_dialog_grade);
        classNumPicker = v.findViewById(R.id.np_dialog_class_num);
        selectorLinearLayout = v.findViewById(R.id.ll_dialog_class_selectors);
        final ClassPreference pref = (ClassPreference) getPreference();

        GradePicker.fixFormatting(gradePicker);

        gradePicker.setValue(convertGradeStringToGradePickerValue(pref.grade));
        gradePicker.setOnValueChangedListener(
                (picker, oldVal, newVal) ->
                        classNumPicker.setMaxValue(ClassUtil.getClassesInHebrewGrade(convertGradePickerValueToString(getContext(), newVal))));

        classNumPicker.setMinValue(1);
        classNumPicker.setMaxValue(ClassUtil.getClassesInHebrewGrade(pref.grade));
        classNumPicker.setWrapSelectorWheel(false);
        classNumPicker.setValue(pref.classNum);
    }

    @Override
    public void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            ClassPreference pref = (ClassPreference) getPreference();
            String grade = convertGradePickerValueToString(getContext(), gradePicker.getValue());
            int classNum = classNumPicker.getValue();
            pref.setClass(grade, classNum);
            pref.setSummary(grade + classNum);
        }
    }

    @Override
    public Preference findPreference(CharSequence charSequence) {
        return getPreference();
    }

    public static int convertGradeStringToGradePickerValue(String grade) {
        if (grade == null || grade.length() == 0) {
            return -1;
        }

        if (!ClassUtil.isValidHebrewGrade(grade)) {
            return -1;
        }

        switch (grade) {
            case "ז":
                return 7;
            case "ח":
                return 8;
            case "ט":
                return 9;
            case "י":
                return 10;
            case "יא":
                return 11;
            case "יב":
                return 12;
            default:
                return -1;
        }
    }

    @Nullable
    public static String convertGradePickerValueToString(Context ctx, int gradeNum) {
        switch (gradeNum) {
            case 7:
                return ctx.getString(R.string.grade_g);
            case 8:
                return ctx.getString(R.string.grade_h);
            case 9:
                return ctx.getString(R.string.grade_i);
            case 10:
                return ctx.getString(R.string.grade_j);
            case 11:
                return ctx.getString(R.string.grade_k);
            case 12:
                return ctx.getString(R.string.grade_l);
            default:
                return null;
        }
    }

}
