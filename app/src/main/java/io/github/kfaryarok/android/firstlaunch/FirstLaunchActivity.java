package io.github.kfaryarok.android.firstlaunch;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.kfaryarok.android.R;
import io.github.kfaryarok.android.firstlaunch.pages.AlertsPageFirstLaunchFragment;
import io.github.kfaryarok.android.firstlaunch.pages.ClassPageFirstLaunchFragment;
import io.github.kfaryarok.android.firstlaunch.pages.LastPageFirstLaunchFragment;

public class FirstLaunchActivity extends FragmentActivity {

    @BindView(R.id.vp_firstlaunch)
    public ViewPager viewPager;

    public FirstLaunchPagerAdapter adapterPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_launch);

        ButterKnife.bind(this);

        adapterPager = new FirstLaunchPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapterPager);
    }

    @Override
    public void onBackPressed() {
        if (viewPager.getCurrentItem() != 0) {
            // If it isn't the first page, make the back button go one page back
            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
        }
    }

    private class FirstLaunchPagerAdapter extends FragmentStatePagerAdapter {

        FirstLaunchPageFragment[] pages = {
                new ClassPageFirstLaunchFragment(),
                new AlertsPageFirstLaunchFragment(),
                new LastPageFirstLaunchFragment()
        };

        public FirstLaunchPagerAdapter(FragmentManager fm) {
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

}
