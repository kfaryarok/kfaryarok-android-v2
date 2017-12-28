package io.github.kfaryarok.android.firstlaunch;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import io.github.kfaryarok.android.R;

/**
 * @author tbsc on 17/11/2017
 */
public abstract class FirstLaunchPageFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    @NonNull
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(getLayout(), container, false);
        return onAbstractCreateView(view);
    }

    /**
     * Checks if the layout direction is set to RTL, and if so, it does the following:
     * - The image of the previous button is changed to the next's image
     * - The image of the next button is changed to the previous' image
     * Needs to be called by each implementation of this class in {@link #onAbstractCreateView(View)}
     * If one of the buttons don't exist, put in null.
     * @param previous The previous page button, if there is one
     * @param next The next page button, if there is one
     */
    protected void flipNavigationButtons(@Nullable Button previous, @Nullable Button next) {
        if (ViewCompat.getLayoutDirection(getActivity().getWindow().getDecorView()) == ViewCompat.LAYOUT_DIRECTION_RTL) {
            if (previous != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    previous.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_navigate_next_black_24dp, 0, 0, 0);
                } else {
                    previous.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_navigate_next_black_24dp, 0, 0, 0);
                }
            }
            if (next != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    next.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_navigate_before_black_24dp, 0);
                } else {
                    next.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_navigate_before_black_24dp, 0);
                }
            }
        }
    }

    protected abstract View onAbstractCreateView(View view);

    @LayoutRes
    protected abstract int getLayout();
    
}
