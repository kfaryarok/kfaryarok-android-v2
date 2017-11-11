/*
 * This file is part of kfaryarok-android.
 *
 * kfaryarok-android is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * kfaryarok-android is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with kfaryarok-android.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.kfaryarok.android.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import io.github.kfaryarok.android.R;

/**
 * Utility class for getting values of various preferences.
 *
 * @author tbsc on 10/03/2017 (copied from v1)
 */
public class PreferenceUtil {

    private static SharedPreferences prefs;

    public static SharedPreferences getSharedPreferences(Context ctx) {
        if (prefs == null) prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        return prefs;
    }

    public static String getClassPreference(Context ctx) {
        if (prefs == null) prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        return prefs.getString(ctx.getString(R.string.pref_class_string), ctx.getString(R.string.pref_class_string_def));
    }

    public static boolean getAlertEnabledPreference(Context ctx) {
        if (prefs == null) prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        return prefs.getBoolean(ctx.getString(R.string.pref_alerts_enabled_bool), Boolean.parseBoolean(ctx.getString(R.string.pref_alerts_enabled_bool_def)));
    }

    public static String getAlertTimePreference(Context ctx) {
        if (prefs == null) prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        return prefs.getString(ctx.getString(R.string.pref_alerts_time_string), ctx.getString(R.string.pref_alerts_time_string_def));
    }

    public static boolean getGlobalAlertsPreference(Context ctx) {
        if (prefs == null) prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        return prefs.getBoolean(ctx.getString(R.string.pref_globalalerts_enabled_bool), Boolean.parseBoolean(ctx.getString(R.string.pref_globalalerts_enabled_bool_def)));
    }

    public static boolean getLaunchedBeforePreference(Context ctx) {
        if (prefs == null) prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        return prefs.getBoolean(ctx.getString(R.string.pref_launched_before_bool), Boolean.parseBoolean(ctx.getString(R.string.pref_launched_before_bool_def)));
    }

    public static boolean getDeveloperModePreference(Context ctx) {
        if (prefs == null) prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        return prefs.getBoolean(ctx.getString(R.string.pref_advanced_mode_bool), Boolean.parseBoolean(ctx.getString(R.string.pref_advanced_mode_bool_def)));
    }

//    public static String getUpdateServerPreference(Context ctx) {
//        if (prefs == null) prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
//        String server = prefs.getString(ctx.getString(R.string.pref_updateserver_string), UpdateFetcher.DEFAULT_UPDATE_URL);
//        return "".equals(server) ? UpdateFetcher.DEFAULT_UPDATE_URL : server;
//    }

}
