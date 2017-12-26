package io.github.kfaryarok.android.util;

import android.app.Activity;
import android.support.v4.view.ViewCompat;
import android.view.View;

/**
 * Utility methods for working with layouts.
 *
 * @author tbsc on 17/11/2017
 */
public class LayoutUtil {

    public static final int RTL = ViewCompat.LAYOUT_DIRECTION_RTL;
    public static final int LTR = ViewCompat.LAYOUT_DIRECTION_LTR;

    public static void setDirection(View view, int direction) {
        ViewCompat.setLayoutDirection(view, direction);
    }

    public static void setDirection(Activity act, int direction) {
        ViewCompat.setLayoutDirection(act.getWindow().getDecorView(), direction);
    }

}
