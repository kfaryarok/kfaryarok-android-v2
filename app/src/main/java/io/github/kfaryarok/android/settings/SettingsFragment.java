package io.github.kfaryarok.android.settings;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v4.app.DialogFragment;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.EditTextPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.widget.Toast;

import java.net.MalformedURLException;
import java.net.URL;

import io.github.kfaryarok.android.R;
import io.github.kfaryarok.android.alerts.AlertHelper;
import io.github.kfaryarok.android.settings.prefs.ClassPreference;
import io.github.kfaryarok.android.settings.prefs.ClassPreferenceDialogFragmentCompat;
import io.github.kfaryarok.android.settings.prefs.TimePreference;
import io.github.kfaryarok.android.updates.UpdateCache;
import io.github.kfaryarok.android.updates.UpdateHelper;
import io.github.kfaryarok.android.util.PreferenceUtil;

/**
 * @author tbsc on 12/11/2017 (recreated from v1)
 */
public class SettingsFragment extends PreferenceFragmentCompat {

    private CheckBoxPreference alertsCb;
    private TimePreference timeAlertTp;
    private CheckBoxPreference globalAlertsCb;
    private ClassPreference classCd;
    private EditTextPreference updateServerEtp;
    private Preference resetAppBp;
    private CheckBoxPreference showAllCb;

    private Toast toast;

    @SuppressLint("ApplySharedPref")
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        // add all preferences from the XML
        addPreferencesFromResource(R.xml.pref_kfaryarok);
        // add the menu bar
        setHasOptionsMenu(true);

        // initiate everything
        alertsCb = (CheckBoxPreference) findPreference(getString(R.string.pref_alerts_enabled_bool));
        timeAlertTp = (TimePreference) findPreference(getString(R.string.pref_alerts_time_string));
        globalAlertsCb = (CheckBoxPreference) findPreference(getString(R.string.pref_globalalerts_enabled_bool));
        classCd = (ClassPreference) findPreference(getString(R.string.pref_class_string));
        updateServerEtp = (EditTextPreference) findPreference(getString(R.string.pref_updateserver_string));
        resetAppBp = findPreference(getString(R.string.pref_reset_bool));
        showAllCb = (CheckBoxPreference) findPreference(getString(R.string.pref_show_all_updates_bool));

        // depending on whether alerts are already enabled, decided if alert preferences should be enabled
        boolean alertsEnabled = PreferenceUtil.getAlertEnabledPreference(getContext());
        timeAlertTp.setEnabled(alertsEnabled);
        globalAlertsCb.setEnabled(alertsEnabled);
        classCd.setEnabled(!PreferenceUtil.getShowAllUpdatesPreference(getContext()));

        alertsCb.setOnPreferenceChangeListener((preference, newValue) -> {
            boolean newBool = (boolean) newValue;

            AlertHelper.toggleAlert(getContext(), newBool);

            timeAlertTp.setEnabled(newBool);
            globalAlertsCb.setEnabled(newBool);

            return true;
        });

        timeAlertTp.setOnPreferenceChangeListener((preference, newValue) -> {
            // Update alert by re-enabling it
            AlertHelper.enableAlert(getContext());

            return true;
        });

        globalAlertsCb.setOnPreferenceChangeListener((preference, newValue) -> {
            // Update alert by re-enabling it
            AlertHelper.enableAlert(getContext());

            return true;
        });

        // change class preference to show current selected class in its summary
        classCd.setSummary(PreferenceUtil.getActualStoredClassPreference(getContext()));

        // set advanced settings prefscreen category's visibility based on prefs
        PreferenceCategory prefCategoryAdvanced = (PreferenceCategory) findPreference(getString(R.string.settings_advanced_category));
        boolean devModeActive = PreferenceUtil.getDeveloperModePreference(getContext());
        prefCategoryAdvanced.setVisible(devModeActive);
        if (!devModeActive) {
            prefCategoryAdvanced.removeAll();
        }

        resetAppBp.setOnPreferenceClickListener(preference -> {
            // clear prefs
            // because app quits immediately, we need to clear prefs immediately
            PreferenceUtil.prefs(getContext()).edit().clear().commit();
            // relaunch app
            System.exit(0);
            return true;
        });

        updateServerEtp.setOnPreferenceChangeListener((preference, newValue) -> {
            String server = (String) newValue;

            if ("".equals(server)) {
                server = UpdateHelper.DEFAULT_UPDATE_URL;
                PreferenceUtil.prefs(getContext()).edit()
                        .putString(getString(R.string.pref_updateserver_string), getString(R.string.pref_updateserver_string_def))
                        .commit();
                if (toast != null) {
                    toast.cancel();
                }
                toast = Toast.makeText(getContext(), getString(R.string.toast_devmode_defaultserver_revert), Toast.LENGTH_LONG);
                toast.show();
            } else {
                // check if it's a valid url
                try {
                    // use URL to see if the url is valid
                    new URL(server);
                } catch (MalformedURLException e) {
                    // invalid
                    if (toast != null) {
                        toast.cancel();
                    }
                    toast = Toast.makeText(getContext(), getString(R.string.toast_devmode_invalid_server), Toast.LENGTH_LONG);
                    toast.show();
                    return false;
                }
            }

            updateServerEtp.setSummary(server);

            // if changing server, than delete cache so new data from new server is fetched
            UpdateCache.deleteCache(getContext());

            return true;
        });

        showAllCb.setOnPreferenceChangeListener(((preference, newValue) -> {
            classCd.setEnabled(!(boolean) newValue);
            return true;
        }));
    }

    @Override
    public void onDisplayPreferenceDialog(final Preference preference) {
        Dialog dialog = null;
        DialogFragment dialogFragment = null;
        if (preference instanceof TimePreference) {
            dialog = new TimePickerDialog(getContext(), (view, hourOfDay, minute) -> {
                SharedPreferences prefs = PreferenceUtil.prefs(getContext());
                String time = TimePreference.timeToString(hourOfDay, minute);
                prefs.edit()
                        .putString(getString(R.string.pref_alerts_time_string), time)
                        .apply();
                preference.setSummary(time);
                // let it know alert time was changed
                AlertHelper.enableAlert(getContext());
            }, PreferenceUtil.parseAlertHour(getContext()), PreferenceUtil.parseAlertMinute(getContext()), true);
        } else if (preference instanceof ClassPreference) {
            dialogFragment = new ClassPreferenceDialogFragmentCompat();
            Bundle bundle = new Bundle(1);
            bundle.putString("key", preference.getKey());
            dialogFragment.setArguments(bundle);
        }

        if (dialogFragment != null) {
            dialogFragment.setTargetFragment(this, 0);
            dialogFragment.show(this.getFragmentManager(), "android.support.v7.preference.PreferenceFragment.DIALOG");
        } else if (dialog != null) {
            dialog.show();
        } else {
            super.onDisplayPreferenceDialog(preference);
        }
    }

    private void showToast(@StringRes int resId) {
        showToast(getString(resId));
    }

    private void showToast(String text) {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(getContext(), text, Toast.LENGTH_LONG);
        toast.show();
    }

}
