package io.github.kfaryarok.android.updates;

import android.content.Context;
import android.text.format.DateFormat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.Scanner;

/**
 * Class for working with the app's update cache.
 * Used to reduce update server traffic.
 *
 * @author tbsc on 09/11/2017 (content copied from v1 UpdateHelper)
 */
public class UpdateCache {

    /**
     * Retrieves the last downloaded JSON file from the app's cache directory.
     * @param ctx Used to access the cache directory
     * @return Cached updates
     * @throws FileNotFoundException If file wasn't found, meaning no cache is available
     */
    public static String getUpdatesCache(Context ctx) throws FileNotFoundException {
        File appDir = ctx.getCacheDir();
        File lastSynced = new File(appDir, "update.json");
        return new Scanner(lastSynced).useDelimiter("\\Z").next();
    }

    /**
     * Caches the given JSON string data to a file in the app's cache directory.
     * @param ctx Used to access the cache directory
     * @param data What to cache
     * @throws IOException if file creation/writing fails
     */
    public static void setUpdatesCache(Context ctx, String data) throws IOException {
        File appDir = ctx.getCacheDir();
        File lastSynced = new File(appDir, "update.json");
        lastSynced.createNewFile();
        FileWriter writer = new FileWriter(lastSynced);
        writer.write(data);
        writer.close();
    }

    /**
     * Checks when were the updates cached last time.
     * @param ctx Used to get cache directory
     * @return {@link Date} object of when it happened
     */
    public static Date getWhenLastCached(Context ctx) {
        File appDir = ctx.getCacheDir();
        File lastSynced = new File(appDir, "update.json");
        return new Date(lastSynced.lastModified());
    }

    /**
     * Sees when cache was last updated and formats it, in "HH:mm:ss dd/MM/yyyy" format.
     * If cache was never updated, it returns "אף פעם".
     * @param ctx Used to see when cache was last updated as a {@link Date}
     * @return formatted string of the date returned by {@link #getWhenLastCached(Context)}
     */
    public static String getWhenLastCachedFormatted(Context ctx) {
        Date lastUpdated = getWhenLastCached(ctx);
        if (lastUpdated.equals(new Date(0))) {
            return "אף פעם";
        } else {
            return (String) DateFormat.format("kk:mm:ss dd/MM/yyyy", lastUpdated);
        }
    }

    /**
     * Checks if there's a cached file.
     * @param ctx Used to access the cache directory
     * @return Does update.json exist in the cache directory
     */
    public static boolean isCached(Context ctx) {
        File appDir = ctx.getCacheDir();
        File lastSynced = new File(appDir, "update.json");
        return lastSynced.exists();
    }

    /**
     * Returns true if cache exists and its last modified time is more than 3 hours from now.
     * @param ctx Used to check last modified
     * @return Does cache exist and is it older than 3 hours
     */
    public static boolean isCacheOlderThan3Hours(Context ctx) {
        long threeHoursInMillis = 10800000;
        return isCached(ctx) &&
                System.currentTimeMillis() - getWhenLastCached(ctx).getTime() > threeHoursInMillis;
    }

    /**
     * Returns true if cache exists and its last modified time is more than 1 hour from now.
     * @param ctx Used to check last modified
     * @return Does cache exist and is it older than 1 hour
     */
    public static boolean isCacheOlderThan1Hour(Context ctx) {
        long oneHourInMillis = 3600000;
        return isCached(ctx) &&
                System.currentTimeMillis() - getWhenLastCached(ctx).getTime() > oneHourInMillis;
    }

    /**
     * If exists, deletes update.json in the cache directory
     * @param ctx Used to access the cache directory
     */
    public static void deleteCache(Context ctx) {
        File appDir = ctx.getCacheDir();
        File lastSynced = new File(appDir, "update.json");
        if (lastSynced.exists()) {
            lastSynced.delete();
        }
    }

}
