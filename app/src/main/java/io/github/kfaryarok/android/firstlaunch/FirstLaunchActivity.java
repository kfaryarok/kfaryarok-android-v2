package io.github.kfaryarok.android.firstlaunch;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.kfaryarok.android.R;
import io.github.kfaryarok.android.firstlaunch.pages.AlertsPageFirstLaunchFragment;
import io.github.kfaryarok.android.firstlaunch.pages.ClassPageFirstLaunchFragment;
import io.github.kfaryarok.android.firstlaunch.pages.LastPageFirstLaunchFragment;

public class FirstLaunchActivity extends FragmentActivity {

    @BindView(R.id.vp_firstlaunch)
    public FirstLaunchViewPager viewPager;

    public PagerAdapter adapterPager;

    private Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_launch);

        ButterKnife.bind(this);

        // LayoutUtil.setDirection(this, LayoutUtil.LTR);

        adapterPager = new PagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapterPager);
    }

    @Override
    public void onBackPressed() {
        if (viewPager.getCurrentItem() != 0) {
            // If it isn't the first page, make the back button go one page back
            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
        } else {
            // if at the first page, exit app on pressing back button
            Intent homeIntent = new Intent(Intent.ACTION_MAIN);
            homeIntent.addCategory(Intent.CATEGORY_HOME);
            homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(homeIntent);
        }
    }

    private class PagerAdapter extends FragmentStatePagerAdapter {

        FirstLaunchPageFragment[] pages = {
                new ClassPageFirstLaunchFragment(),
                new AlertsPageFirstLaunchFragment(),
                new LastPageFirstLaunchFragment()
        };

        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return pages[position];
        }

        @Override
        public int getCount() {
            return 3;
        }

    }

    public void showToast(@StringRes int resId) {
        showToast(getString(resId));
    }

    public void showToast(String text) {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(this, text, Toast.LENGTH_LONG);
        toast.show();
    }

}
