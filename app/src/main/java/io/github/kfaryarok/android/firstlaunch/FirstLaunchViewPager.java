package io.github.kfaryarok.android.firstlaunch;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.duolingo.open.rtlviewpager.RtlViewPager;

/**
 * @author tbsc on 20/11/2017
 */
public class FirstLaunchViewPager extends RtlViewPager {

    public FirstLaunchViewPager(Context context) {
        super(context);
    }

    public FirstLaunchViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    // disable swiping to different pages

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return false;
    }

}
