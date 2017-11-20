package io.github.kfaryarok.android.firstlaunch.pages;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.kfaryarok.android.R;
import io.github.kfaryarok.android.firstlaunch.FirstLaunchActivity;
import io.github.kfaryarok.android.firstlaunch.FirstLaunchPageFragment;
import io.github.kfaryarok.android.settings.prefs.ClassPreferenceDialogFragmentCompat;
import io.github.kfaryarok.android.util.ClassUtil;
import io.github.kfaryarok.android.util.LayoutUtil;
import io.github.kfaryarok.android.util.PreferenceUtil;

/**
 * The first page of the app's setup wizard.
 * Lets user either select a class or set the app to show all updates.
 *
 * @author tbsc on 17/11/2017
 */
public class ClassPageFirstLaunchFragment extends FirstLaunchPageFragment {

    @BindView(R.id.include_class_selector)
    public View selectorInclude;

    @BindView(R.id.np_dialog_class_num)
    public NumberPicker numPicker;

    @BindView(R.id.rg_dialog_grade)
    public RadioGroup gradePicker;

    @BindView(R.id.cb_firstlaunch_show_all_updates)
    public CheckBox showAllCheckBox;

    @BindView(R.id.btn_firstlaunch_page1_next)
    public ImageButton nextPageButton;

    @BindView(R.id.rb_dialog_grade_g)
    public RadioButton gGradeRadioButton;
    @BindView(R.id.rb_dialog_grade_h)
    public RadioButton hGradeRadioButton;
    @BindView(R.id.rb_dialog_grade_i)
    public RadioButton iGradeRadioButton;
    @BindView(R.id.rb_dialog_grade_j)
    public RadioButton jGradeRadioButton;
    @BindView(R.id.rb_dialog_grade_k)
    public RadioButton kGradeRadioButton;
    @BindView(R.id.rb_dialog_grade_l)
    public RadioButton lGradeRadioButton;

    public boolean allowNextPage = false;

    @Override
    protected View onAbstractCreateView(View view) {
        ButterKnife.bind(this, view);

        numPicker.setMinValue(1);
        numPicker.setMaxValue(11);
        numPicker.setWrapSelectorWheel(false);

        // only enable it after user puts in data
        // nextPageButton.setEnabled(false);

        LayoutUtil.setDirection(selectorInclude, LayoutUtil.RTL);
        LayoutUtil.setDirection(showAllCheckBox, LayoutUtil.RTL);

        gradePicker.setOnCheckedChangeListener(((group, checkedId) -> {
            String currentClass = PreferenceUtil.getActualStoredClassPreference(getContext());
            int classNum = ClassUtil.parseHebrewClassNumber(currentClass);

            String newGrade = ClassPreferenceDialogFragmentCompat.convertGradeRadioButtonResToString(getContext(), checkedId);

            PreferenceUtil.prefs(getContext()).edit()
                    .putString(getString(R.string.pref_class_string), newGrade + classNum)
                    .apply();

            // a grade is selected and is assumed can't be deselected (radio buttons)
            // just enable going to the next page permanently
            // nextPageButton.setEnabled(true);
            allowNextPage = true;
        }));

        numPicker.setOnValueChangedListener(((picker, oldVal, newVal) -> {
            String currentClass = PreferenceUtil.getActualStoredClassPreference(getContext());
            String grade = ClassUtil.parseHebrewGrade(currentClass);

            PreferenceUtil.prefs(getContext()).edit()
                    .putString(getString(R.string.pref_class_string), grade + newVal)
                    .apply();
        }));

        showAllCheckBox.setOnCheckedChangeListener(((buttonView, isChecked) -> {
            gGradeRadioButton.setEnabled(!isChecked);
            hGradeRadioButton.setEnabled(!isChecked);
            iGradeRadioButton.setEnabled(!isChecked);
            jGradeRadioButton.setEnabled(!isChecked);
            kGradeRadioButton.setEnabled(!isChecked);
            lGradeRadioButton.setEnabled(!isChecked);
            numPicker.setEnabled(!isChecked);

            PreferenceUtil.prefs(getContext()).edit()
                    .putBoolean(getString(R.string.pref_show_all_updates_bool), isChecked)
                    .apply();

            // if this was selected, then allow going to the next page
            // if it was deselected, then allow going to the next page ONLY if a grade is selected
            // nextPageButton.setEnabled(isChecked || gradePicker.getCheckedRadioButtonId() != -1);
            allowNextPage = isChecked || gradePicker.getCheckedRadioButtonId() != -1;
        }));

        nextPageButton.setOnClickListener((v) -> {
            FirstLaunchActivity act = (FirstLaunchActivity) getActivity();
            if (allowNextPage) {
                act.viewPager.setCurrentItem(act.viewPager.getCurrentItem() + 1);
            } else {
                act.showToast(getString(R.string.toast_firstlaunch_no_class));
            }
        });

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @LayoutRes
    @Override
    protected int getLayout() {
        return R.layout.fragment_first_launch_page1_class;
    }

}
