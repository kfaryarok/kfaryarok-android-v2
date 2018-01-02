package io.github.kfaryarok.android.firstlaunch.pages;

import android.support.annotation.LayoutRes;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.NumberPicker;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.kfaryarok.android.R;
import io.github.kfaryarok.android.firstlaunch.FirstLaunchActivity;
import io.github.kfaryarok.android.firstlaunch.FirstLaunchPageFragment;
import io.github.kfaryarok.android.settings.prefs.ClassPreferenceDialogFragmentCompat;
import io.github.kfaryarok.android.settings.prefs.GradePicker;
import io.github.kfaryarok.android.util.ClassUtil;
import io.github.kfaryarok.android.util.LayoutUtil;
import io.github.kfaryarok.android.util.PreferenceUtil;

/**
 * The first page of the app's setup wizard.
 * Lets user either select a class or set the app to show all updates.
 *
 * TODO: Explain a bit more about the class selection and what it means
 *
 * @author tbsc on 17/11/2017
 */
public class ClassPageFirstLaunchFragment extends FirstLaunchPageFragment {

    @BindView(R.id.include_firstlaunch_class_selector)
    public View selectorInclude;

    @BindView(R.id.np_dialog_class_num)
    public NumberPicker numPicker;

    @BindView(R.id.cb_firstlaunch_page_class_showallupdates)
    public CheckBox showAllCheckBox;

    @BindView(R.id.btn_firstlaunch_page_class_next)
    public Button nextPageButton;

    @BindView(R.id.gp_dialog_grade)
    public GradePicker gradePicker;

    @Override
    protected View onAbstractCreateView(View view) {
        ButterKnife.bind(this, view);

        flipNavigationButtons(null, nextPageButton);

        numPicker.setMinValue(1);
        numPicker.setMaxValue(11);
        numPicker.setWrapSelectorWheel(false);
        numPicker.setSelected(true);

        LayoutUtil.setDirection(selectorInclude, LayoutUtil.RTL);
        LayoutUtil.setDirection(showAllCheckBox, LayoutUtil.RTL);

        GradePicker.fixFormatting(gradePicker);

        gradePicker.setOnValueChangedListener((picker, oldVal, newVal) -> {
            String currentClass = PreferenceUtil.getActualStoredClassPreference(getContext());
            int classNum = ClassUtil.parseHebrewClassNumber(currentClass);

            String newGrade = ClassPreferenceDialogFragmentCompat.convertGradePickerValueToString(getContext(), newVal);

            PreferenceUtil.prefs(getContext()).edit()
                    .putString(getString(R.string.pref_class_string), newGrade + classNum)
                    .apply();
        });

        numPicker.setOnValueChangedListener(((picker, oldVal, newVal) -> {
            String currentClass = PreferenceUtil.getActualStoredClassPreference(getContext());
            String grade = ClassUtil.parseHebrewGrade(currentClass);

            PreferenceUtil.prefs(getContext()).edit()
                    .putString(getString(R.string.pref_class_string), grade + newVal)
                    .apply();
        }));

        showAllCheckBox.setOnCheckedChangeListener(((buttonView, isChecked) -> {
            gradePicker.setEnabled(!isChecked);
            numPicker.setEnabled(!isChecked);

            PreferenceUtil.prefs(getContext()).edit()
                    .putBoolean(getString(R.string.pref_show_all_updates_bool), isChecked)
                    .apply();
        }));

        nextPageButton.setOnClickListener((v) -> {
            FirstLaunchActivity act = (FirstLaunchActivity) getActivity();
            act.viewPager.setCurrentItem(act.viewPager.getCurrentItem() + 1);

            // seems very complex at first and a bit unnecessary, but I'll explain why it's there
            // firstly, it sees if there's a class stored in preferences
            // then it checks if maybe the user set it to show all updates
            // then it checks if the selected grade is 7 (ז)
            // and lastly it checks if the selected class number is 1
            // this is needed because the user might be from ז1 and therefore won't change the class
            // the problem is that then the class never gets saved to the preferences
            // this checks if that happens, and if it does, it saves to prefs
            if (PreferenceUtil.getActualStoredClassPreference(getContext()).equals(getString(R.string.pref_class_string_def))
                    && !PreferenceUtil.getShowAllUpdatesPreference(getContext())
                    && gradePicker.getValue() == ClassPreferenceDialogFragmentCompat.convertGradeStringToGradePickerValue("ז")
                    && numPicker.getValue() == 1) {
                PreferenceUtil.prefs(getContext()).edit()
                        .putString(getString(R.string.pref_class_string),
                                ClassPreferenceDialogFragmentCompat
                                        .convertGradePickerValueToString(getContext(), gradePicker.getValue())
                                        + numPicker.getValue())
                        .apply();
            }
        });

        return view;
    }

    @LayoutRes
    @Override
    protected int getLayout() {
        return R.layout.fragment_first_launch_page_class;
    }

}
