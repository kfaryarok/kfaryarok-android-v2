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

package io.github.kfaryarok.android.alerts;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.github.kfaryarok.android.MainActivity;
import io.github.kfaryarok.android.R;
import io.github.kfaryarok.android.settings.prefs.TimePreference;
import io.github.kfaryarok.android.updates.UpdateCache;
import io.github.kfaryarok.android.updates.UpdateHelper;
import io.github.kfaryarok.android.updates.api.Update;
import io.github.kfaryarok.android.util.PreferenceUtil;
import io.reactivex.internal.functions.Functions;

/**
 * This class is used to configure update alerts based on preferences and other things;
 * you do not need to give it any values.
 *
 * @author tbsc on 11/03/2017 (copied from v1)
 */
public class AlertHelper {

    /**
     * Starting with API 26, notifications need to be assigned a channel
     */
    public static final String NOTIFICATION_CHANNEL = "kfar_yarok_01";
    public static final int NOTIFICATION_ALERT = 1;

    private static AlarmManager alarmManager;
    private static PendingIntent pendingAlertReceiver;

    /**
     * Enables an alarm using preferences and default values.
     * @param ctx Context, used to create a pending intent for receiving alarm and setting the alarm
     */
    public static void enableAlert(Context ctx) {
        initiateFields(ctx);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            registerChannel(ctx);
        }

        // Tell the system that the receiver is enabled,
        // and should stay enabled even after reboots.
        ComponentName receiver = new ComponentName(ctx, BootReceiver.class);
        PackageManager pm = ctx.getPackageManager();
        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);

        String alertTime = PreferenceUtil.getAlertTimePreference(ctx);
        int alertHour = TimePreference.parseHour(alertTime);
        int alertMinute = TimePreference.parseMinute(alertTime);

        // create calendar instance for calculating alert time
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());

        // set alert time fields
        calendar.set(Calendar.HOUR_OF_DAY, alertHour);
        calendar.set(Calendar.MINUTE, alertMinute);
        calendar.set(Calendar.SECOND, 0);

        // if alert hour is before current time, push it forward by a day
        Calendar now = Calendar.getInstance();
        now.setTime(new Date());
        if (calendar.before(now)) {
            calendar.add(Calendar.DATE, 1);
        }

        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingAlertReceiver);
    }

    /**
     * Cancels the current alert. If the alert wasn't set with {@link #createIntent(Context)}, then
     * it won't be able to cancel the alert.
     */
    public static void disableAlert(Context ctx) {
        initiateFields(ctx);

        // Tell the system that the receiver is disabled,
        // and should no longer stay enabled after reboots.
        ComponentName receiver = new ComponentName(ctx, BootReceiver.class);
        PackageManager pm = ctx.getPackageManager();
        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);

        alarmManager.cancel(pendingAlertReceiver);
    }

    /**
     * Initiates the alarm manager field and the pending intent field, in case they're null.
     * @param ctx Used to get the alarm manager instance and to create the pending intent
     */
    private static void initiateFields(Context ctx) {
        if (alarmManager == null) {
            // cache alarm manager if null
            alarmManager = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        }
        if (pendingAlertReceiver == null) {
            // init pendingintent if null
            pendingAlertReceiver = createIntent(ctx);
        }
    }

    /**
     * Small utility method for easily toggling the alert state
     * @param ctx Application context
     * @param state What should happen to the alert state - true for enabled, false for disabled
     */
    public static void toggleAlert(Context ctx, boolean state) {
        if (state) {
            enableAlert(ctx);
        } else {
            disableAlert(ctx);
        }
    }

    /**
     * Registers the app's notification channel to the system.
     * @param ctx Used to access system
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private static void registerChannel(Context ctx) {
        NotificationManager mNotifManager =
                (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationChannel mChannel = new NotificationChannel(NOTIFICATION_CHANNEL,
                ctx.getString(R.string.notif_channel_name), NotificationManager.IMPORTANCE_DEFAULT);

        mChannel.setDescription(ctx.getString(R.string.notif_channel_desc));
        mChannel.setLightColor(Color.GREEN);
        mChannel.enableVibration(true);

        mNotifManager.createNotificationChannel(mChannel);
    }

    /**
     * Creates an instance of the pending intent that is used to fire the receiver after
     * the alarm was called.
     * @param ctx For creating the intent
     * @return A pending intent that calls {@link AlertReceiver}
     */
    private static PendingIntent createIntent(Context ctx) {
        return PendingIntent.getBroadcast(ctx, 0, new Intent(ctx, AlertReceiver.class), PendingIntent.FLAG_UPDATE_CURRENT);
    }

    /**
     * Creates a notification containing updates.
     * This method fetches updates, parses them, filters them (based on prefs) and formats them
     * into a notification, with updates line-separated.
     * @param context Used for getting preferences and strings
     * @param show Should it be shown to the user
     */
    public static void showNotification(Context context, boolean show) {
        List<Update> updates = new ArrayList<>();
        UpdateHelper.getUpdatesReactively(context, false, updates::add, Functions.emptyConsumer(), () -> {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL);
            builder.setContentTitle(context.getString(R.string.alert_updates_title) + " (" + UpdateCache.getWhenLastCachedFormatted(context) + ")")
                    .setSmallIcon(R.mipmap.ic_launcher);

            NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

            for (Update update : updates) {
                if (!PreferenceUtil.getGlobalAlertsPreference(context)) {
                    // if set to not show global updates
                    if (update.getAffected().isGlobal()) {
                        // and it's a global update, skip
                        continue;
                    }
                }
                // add update as line to the notification
                inboxStyle.addLine(UpdateHelper.formatUpdate(update, context));
            }

            if (updates.size() == 0) {
                inboxStyle.addLine("אין הודעות");
            }

            // give style to builder
            inboxStyle.setBigContentTitle("הודעות" + " (" + UpdateCache.getWhenLastCachedFormatted(context) + "):");
            builder.setStyle(inboxStyle);

            // create explicit intent to main activity
            Intent mainActivity = new Intent(context, MainActivity.class);
            // create a stack for telling android what to launch
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);

            // tell it to go to main activity
            stackBuilder.addParentStack(MainActivity.class);
            stackBuilder.addNextIntent(mainActivity);

            // get the pending intent
            PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            // give it to the notification builder
            builder.setContentIntent(pendingIntent);

            // cancel notif when clicked
            builder.setAutoCancel(true);

            // bob the build-it
            Notification notif = builder.build();

            if (show) {
                // show notification
                NotificationManager notifManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                notifManager.notify(AlertHelper.NOTIFICATION_ALERT, notif);
            }
        }, Functions.emptyConsumer());
    }

}
