package io.github.kfaryarok.android.firstlaunch;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

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
    protected void flipNavigationButtons(@Nullable ImageButton previous, @Nullable ImageButton next) {
        if (ViewCompat.getLayoutDirection(getActivity().getWindow().getDecorView()) == ViewCompat.LAYOUT_DIRECTION_RTL) {
            if (previous != null) {
                previous.setImageResource(R.drawable.ic_navigate_next_black_24dp);
            }
            if (next != null) {
                next.setImageResource(R.drawable.ic_navigate_before_black_24dp);
            }
        }
    }

    protected abstract View onAbstractCreateView(View view);

    @LayoutRes
    protected abstract int getLayout();
    
}
