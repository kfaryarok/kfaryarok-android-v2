package io.github.kfaryarok.android.firstlaunch;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

    protected abstract View onAbstractCreateView(View view);

    @LayoutRes
    protected abstract int getLayout();
    
}
