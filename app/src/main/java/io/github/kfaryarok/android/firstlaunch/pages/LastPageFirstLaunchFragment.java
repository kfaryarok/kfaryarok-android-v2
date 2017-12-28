package io.github.kfaryarok.android.firstlaunch.pages;

import android.view.View;
import android.widget.ImageButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.kfaryarok.android.MainActivity;
import io.github.kfaryarok.android.R;
import io.github.kfaryarok.android.firstlaunch.FirstLaunchActivity;
import io.github.kfaryarok.android.firstlaunch.FirstLaunchPageFragment;
import io.github.kfaryarok.android.util.PreferenceUtil;

/**
 * @author tbsc on 17/11/2017
 */
public class LastPageFirstLaunchFragment extends FirstLaunchPageFragment {

    @BindView(R.id.btn_firstlaunch_page3_finish)
    public ImageButton finishButton;

    @BindView(R.id.btn_firstlaunch_page3_previous)
    public ImageButton previousPageButton;

    @Override
    protected View onAbstractCreateView(View view) {
        ButterKnife.bind(this, view);

        flipNavigationButtons(previousPageButton, null);

        finishButton.setOnClickListener(v -> {
            // finished setup, exit and remember first launch wizard ran
            PreferenceUtil.prefs(getContext()).edit()
                    .putBoolean(getString(R.string.pref_launched_before_bool), true)
                    .apply();
            MainActivity.resumeFromFirstLaunch = true;
            getActivity().finish();
        });

        previousPageButton.setOnClickListener((v) -> {
            FirstLaunchActivity act = (FirstLaunchActivity) getActivity();
            act.viewPager.setCurrentItem(act.viewPager.getCurrentItem() - 1);
        });

        return view;
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_first_launch_page3;
    }

}
