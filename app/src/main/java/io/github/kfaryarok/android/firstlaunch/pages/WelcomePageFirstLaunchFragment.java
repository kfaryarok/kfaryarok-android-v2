package io.github.kfaryarok.android.firstlaunch.pages;

import android.support.annotation.LayoutRes;
import android.view.View;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.kfaryarok.android.R;
import io.github.kfaryarok.android.firstlaunch.FirstLaunchActivity;
import io.github.kfaryarok.android.firstlaunch.FirstLaunchPageFragment;

/**
 * What users will first see when opening the app for the first time.
 * Shows the school's logo, and some text.
 *
 * @author tbsc on 28/12/2017
 */
public class WelcomePageFirstLaunchFragment extends FirstLaunchPageFragment {

    @BindView(R.id.btn_firstlaunch_page_welcome_next)
    public Button nextPageButton;

    @Override
    protected View onAbstractCreateView(View view) {
        ButterKnife.bind(this, view);

        flipNavigationButtons(null, nextPageButton);

        nextPageButton.setOnClickListener((v) -> {
            FirstLaunchActivity act = (FirstLaunchActivity) getActivity();
            act.viewPager.setCurrentItem(act.viewPager.getCurrentItem() + 1);
        });

        return view;
    }

    @LayoutRes
    @Override
    protected int getLayout() {
        return R.layout.fragment_first_launch_page_welcome;
    }

}
