package io.github.kfaryarok.android.firstlaunch.pages;

import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.kfaryarok.android.R;
import io.github.kfaryarok.android.alerts.AlertHelper;
import io.github.kfaryarok.android.firstlaunch.FirstLaunchActivity;
import io.github.kfaryarok.android.firstlaunch.FirstLaunchPageFragment;
import io.github.kfaryarok.android.settings.prefs.TimePreference;
import io.github.kfaryarok.android.util.LayoutUtil;
import io.github.kfaryarok.android.util.PreferenceUtil;

/**
 * Second page of the setup wizard.
 * Here the user configures the alerts - time, what to show, if to show at all.
 * TODO: Add more explanation about what alerts are
 *
 * @author tbsc on 17/11/2017
 */
public class AlertsPageFirstLaunchFragment extends FirstLaunchPageFragment {

    @BindView(R.id.cb_firstlaunch_page_alerts_toggle)
    public CheckBox toggleCheckBox;

    @BindView(R.id.tvbtn_firstlaunch_page_alerts_change)
    public TextView timePickerButtonTextView;

    @BindView(R.id.tv_firstlaunch_page_alerts_timeselected)
    public TextView selectedTimeTextView;

    @BindView(R.id.tv_firstlaunch_page_alerts_titletime)
    public TextView timeTitleTextView;

    @BindView(R.id.cb_firstlaunch_page_alerts_global)
    public CheckBox globalToggleCheckBox;

    @BindView(R.id.btn_firstlaunch_page_alerts_previous)
    public Button previousPageButton;

    @BindView(R.id.btn_firstlaunch_page_alerts_next)
    public Button nextPageButton;

    @BindView(R.id.clayout_firstlaunch_page_alerts_content)
    public ConstraintLayout contentConstraintLayout;

    @Override
    protected View onAbstractCreateView(View view) {
        ButterKnife.bind(this, view);

        flipNavigationButtons(previousPageButton, nextPageButton);

        LayoutUtil.setDirection(contentConstraintLayout, LayoutUtil.RTL);
        LayoutUtil.setDirection(toggleCheckBox, LayoutUtil.LTR);
        LayoutUtil.setDirection(globalToggleCheckBox, LayoutUtil.LTR);

        toggleCheckBox.setChecked(true);
        globalToggleCheckBox.setChecked(true);

        toggleCheckBox.setOnCheckedChangeListener((buttonView, isChecked) ->  {
            globalToggleCheckBox.setEnabled(isChecked);
            timePickerButtonTextView.setClickable(isChecked);
            if (!isChecked) {
                int disabledColor = ContextCompat.getColor(getContext(), android.R.color.darker_gray);
                selectedTimeTextView.setTextColor(disabledColor);
                timeTitleTextView.setTextColor(disabledColor);
                timePickerButtonTextView.setTextColor(disabledColor);
            } else {
                int buttonColor = ContextCompat.getColor(getContext(), android.R.color.holo_blue_dark);
                int textColor = ContextCompat.getColor(getContext(), android.R.color.primary_text_light);
                selectedTimeTextView.setTextColor(textColor);
                timeTitleTextView.setTextColor(textColor);
                timePickerButtonTextView.setTextColor(buttonColor);
            }

            PreferenceUtil.prefs(getContext()).edit()
                    .putBoolean(getString(R.string.pref_alerts_enabled_bool), isChecked)
                    .apply();

            AlertHelper.toggleAlert(getContext(), isChecked);
        });

        timePickerButtonTextView.setOnClickListener(button -> {
            TimePickerDialog dialog = new TimePickerDialog(getContext(), (timePicker, hourOfDay, minute) -> {
                SharedPreferences prefs = PreferenceUtil.prefs(getContext());
                String time = TimePreference.timeToString(hourOfDay, minute);
                prefs.edit()
                        .putString(getString(R.string.pref_alerts_time_string), time)
                        .apply();

                // let it know alert time was changed
                AlertHelper.enableAlert(getContext());

                selectedTimeTextView.setText(time);
            }, PreferenceUtil.parseAlertHour(getContext()), PreferenceUtil.parseAlertMinute(getContext()), true);

            dialog.show();
        });

        selectedTimeTextView.setText(PreferenceUtil.getAlertTimePreference(getContext()));

        globalToggleCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            PreferenceUtil.prefs(getContext()).edit()
                    .putBoolean(getString(R.string.pref_globalalerts_enabled_bool), isChecked)
                    .apply();
            AlertHelper.enableAlert(getContext());
        });

        previousPageButton.setOnClickListener((v) -> {
            FirstLaunchActivity act = (FirstLaunchActivity) getActivity();
            act.viewPager.setCurrentItem(act.viewPager.getCurrentItem() - 1);
        });
        nextPageButton.setOnClickListener((v) -> {
            FirstLaunchActivity act = (FirstLaunchActivity) getActivity();
            act.viewPager.setCurrentItem(act.viewPager.getCurrentItem() + 1);
        });

        return view;
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_first_launch_page_alerts;
    }

}
