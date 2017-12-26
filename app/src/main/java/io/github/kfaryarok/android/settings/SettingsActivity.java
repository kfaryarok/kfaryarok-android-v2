package io.github.kfaryarok.android.settings;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import io.github.kfaryarok.android.R;
import io.github.kfaryarok.android.util.LayoutUtil;

/**
 * @author tbsc on {unknown} (copied from v1)
 */
public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        LayoutUtil.setDirection(this, LayoutUtil.RTL);
    }

}
