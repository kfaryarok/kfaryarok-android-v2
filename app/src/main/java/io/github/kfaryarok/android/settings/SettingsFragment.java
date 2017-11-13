package io.github.kfaryarok.android.settings;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import io.github.kfaryarok.android.R;
import io.github.kfaryarok.android.util.PreferenceUtil;

/**
 * @author tbsc on 12/11/2017 (recreated from v1)
 */
public class SettingsFragment extends PreferenceFragmentCompat {

    /**
     * This fragment is used to also let user configure the app on the first launch.
     * Setting this to true causes the activity to have a tick button in the menu bar.
     */
    private boolean firstLaunch = false;

    private Toast toast;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.pref_kfaryarok);
        setHasOptionsMenu(true);

        firstLaunch = getActivity().getIntent().getBooleanExtra(Intent.EXTRA_TEXT, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (firstLaunch) {
            // first launch, so put the first launch menu
            inflater.inflate(R.menu.first_launch, menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_firstlaunch_accept) {
            // if there's a class stored in prefs already
            if (!"".equals(PreferenceUtil.getClassPreference(getContext()))) {
                // allow continuing
                getActivity().finish();
                // mark in preferences that first launch just finished
                getPreferenceManager().getSharedPreferences().edit().putBoolean(getString(R.string.pref_launched_before_bool), true).apply();
                // tell main activity that first launched just finished so recreate main activity TODO
                // MainActivity.resumeFromFirstLaunch = true;
            } else {
                // else notify user
                showToast(R.string.toast_firstlaunch_no_class);
            }
        }

        return super.onOptionsItemSelected(item);
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
