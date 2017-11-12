package io.github.kfaryarok.android.settings;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import io.github.kfaryarok.android.R;

/**
 * Intent.EXTRA_TEXT is used to tell the activity if it's running as a first launch activity.
 *
 * @author tbsc on {unknown} (copied from v1)
 */
public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        boolean mFirstLaunchActivity = getIntent().getBooleanExtra(Intent.EXTRA_TEXT, false);

        if (mFirstLaunchActivity) {
            ActionBar ab = getSupportActionBar();

            if (ab != null) {
                // disable up button if on first launch mode
                ab.setDisplayHomeAsUpEnabled(false);
            }

            setTitle(R.string.act_firstlaunch);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }
    }

}
