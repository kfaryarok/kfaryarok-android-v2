package io.github.kfaryarok.android.firstlaunch.pages;

import android.app.TimePickerDialog;
import android.content.ComponentName;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.kfaryarok.android.R;
import io.github.kfaryarok.android.alerts.AlertHelper;
import io.github.kfaryarok.android.alerts.BootReceiver;
import io.github.kfaryarok.android.firstlaunch.FirstLaunchActivity;
import io.github.kfaryarok.android.firstlaunch.FirstLaunchPageFragment;
import io.github.kfaryarok.android.settings.prefs.TimePreference;
import io.github.kfaryarok.android.util.LayoutUtil;
import io.github.kfaryarok.android.util.PreferenceUtil;

/**
 * Second page of the setup wizard.
 * Here the user configures the alerts - time, what to show, if to show at all.
 *
 * @author tbsc on 17/11/2017
 */
public class AlertsPageFirstLaunchFragment extends FirstLaunchPageFragment {

    @BindView(R.id.cb_firstlaunch_page2_alerts_toggle)
    public CheckBox toggleCheckBox;

    @BindView(R.id.btn_firstlaunch_page2_alerts_timepicker)
    public Button timePickerButton;

    @BindView(R.id.tv_firstlaunch_page2_alerts_timeselected)
    public TextView selectedTimeTextView;

    @BindView(R.id.cb_firstlaunch_page2_alerts_global)
    public CheckBox globalToggleCheckBox;

    @BindView(R.id.btn_firstlaunch_page2_previous)
    public ImageButton previousPageButton;

    @BindView(R.id.btn_firstlaunch_page2_next)
    public ImageButton nextPageButton;

    @Override
    protected View onAbstractCreateView(View view) {
        ButterKnife.bind(this, view);

        LayoutUtil.setDirection(toggleCheckBox, LayoutUtil.RTL);
        LayoutUtil.setDirection(timePickerButton, LayoutUtil.RTL);
        LayoutUtil.setDirection(globalToggleCheckBox, LayoutUtil.RTL);

        toggleCheckBox.setChecked(true);
        globalToggleCheckBox.setChecked(true);

        toggleCheckBox.setOnCheckedChangeListener((buttonView, isChecked) ->  {
            timePickerButton.setEnabled(isChecked);
            globalToggleCheckBox.setEnabled(isChecked);

            PreferenceUtil.prefs(getContext()).edit()
                    .putBoolean(getString(R.string.pref_alerts_enabled_bool), isChecked)
                    .apply();

            ComponentName receiver = new ComponentName(getContext(), BootReceiver.class);
            PackageManager pm = getContext().getPackageManager();
            if (isChecked) {
                // alerts are enabled, enable alert and boot receiver
                AlertHelper.enableAlert(getContext());
                pm.setComponentEnabledSetting(receiver,
                        PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                        PackageManager.DONT_KILL_APP);
            } else {
                // alerts are disabled, disable alert and boot receiver
                AlertHelper.disableAlert(getContext());
                pm.setComponentEnabledSetting(receiver,
                        PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                        PackageManager.DONT_KILL_APP);
            }
        });

        timePickerButton.setOnClickListener(button -> {
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

        globalToggleCheckBox.setOnCheckedChangeListener((buttonView, isChecked) ->
                PreferenceUtil.prefs(getContext()).edit()
                .putBoolean(getString(R.string.pref_globalalerts_enabled_bool), isChecked)
                .apply());

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
        return R.layout.fragment_first_launch_page2_alerts;
    }

}
