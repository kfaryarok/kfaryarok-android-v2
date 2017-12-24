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

package io.github.kfaryarok.android.updates;

import android.content.Context;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;

import io.github.kfaryarok.android.R;
import io.github.kfaryarok.android.updates.api.ClassesAffected;
import io.github.kfaryarok.android.updates.api.Update;
import io.github.kfaryarok.android.util.NetworkUtil;
import io.github.kfaryarok.android.util.PreferenceUtil;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Various utility methods for working with updates.
 *
 * @author tbsc on 16/03/2017 (some parts of the file copied from v1)
 */
public class UpdateHelper {

    public static String DEFAULT_UPDATE_URL = "https://tbscdev.xyz/update.json";

    /**
     * Does everything needed to get the updates, fetching JSON from server or cache, parsing, and filtering.
     */
    public static void getUpdatesReactively(Context ctx, boolean forceFetch,
                                            Consumer<? super Update> onNext, Consumer<? super Throwable> onError,
                                            Action onComplete, Consumer<? super Disposable> onSubscribe) {
        decideOnSource(ctx, forceFetch)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(AndroidSchedulers.mainThread())
                // parse updates from fetched data
                .flatMap(data -> Observable.fromArray(UpdateParser.parseUpdates(data)))
                // filter out irrelevant stuff
                .filter(update -> update.getAffected().affects(PreferenceUtil.getClassPreference(ctx)))
                .subscribe(onNext, onError, onComplete, onSubscribe);
    }

    private static Observable<String> decideOnSource(Context ctx, boolean forceFetch) {
        if (forceFetch) {
            return networkSource(ctx, true);
        }

        // if cache is newer than an hour, use it
        if (!UpdateCache.isCacheOlderThan1Hour(ctx)) {
            try {
                // try using cache
                return cacheSource(ctx);
            } catch (FileNotFoundException cacheException) {
                // didn't work - no cache for some reason although it should be there
                // use network
                return networkSource(ctx, true);
            }
        } else {
            // cache is older than an hour, use network
            return networkSource(ctx, true);
        }
    }

    /**
     * Provides an observable that will provide data from the cache.
     * @param ctx Used to retrieve cache
     * @return Observable cache
     * @throws FileNotFoundException No cache exists
     */
    private static Observable<String> cacheSource(Context ctx) throws FileNotFoundException {
        return Observable.just(UpdateCache.getUpdatesCache(ctx));
    }

    /**
     * Provides an observable that will provide data from the network.
     * @param ctx Used to save to cache and get error messages
     * @param saveToCache Whether or not to save the downloaded data to the cache
     * @return Observable containing newly fetched data
     */
    private static Observable<String> networkSource(Context ctx, boolean saveToCache) {
        return Observable.just(PreferenceUtil.getUpdateServerPreference(ctx))
                .observeOn(Schedulers.io()) // fetch on IO thread
                .map(NetworkUtil::downloadUsingInputStreamReader)
                // if we got nothing from the fetcher, return fake data detailing an error fetching
                .filter(s -> s != null) // not going to use API 24+ methods, ignoring warning
                .defaultIfEmpty(UpdateParser.getSingleGlobalUpdateJSONString(ctx.getString(R.string.error_update_text)))
                .map(data -> {
                    // "fake" mapping - doing something with each value but returning the same value
                    // this saves to cache but doesn't change the data
                    if (saveToCache) {
                        // don't save fake data to cache
                        if (!UpdateParser.getSingleGlobalUpdateJSONString(ctx.getString(R.string.error_update_text)).equals(data)) {
                            // save data to cache
                            UpdateCache.setUpdatesCache(ctx, data);
                        }
                    }
                    return data;
                });
    }

    /**
     * Creates a string of the classes in the array, with the user's current class first and
     * other classes afterwards, comma-separated.
     *
     * I'll try to explain how it works, because it's not simple at first glance.
     * 1. If the array has the user's class, add it.
     * 2. If there is more than one class, add a comma for the next classes
     * 3. Loop through the classes
     * 4. Continue to next class if it's the user's class (because it's always there)
     * 5. Append the class
     * 6. Append comma, unless it's the last class
     *
     * @param classes The array of classes to format
     * @param userClass To know which class to put first. If the classes array doesn't have it, then it will not be reordered.
     * @return Class array in string form, comma-separated.
     */
    public static String formatClassString(String[] classes, String userClass) {
        if (classes == null || classes.length == 0) {
            // if anything is invalid just return null
            return null;
        }

        // string builder for creating the class string
        StringBuilder classBuilder = new StringBuilder();

        // convert to array list so to remove the user's class from the list
        ArrayList<String> classList = new ArrayList<>();
        Collections.addAll(classList, classes);

        boolean firstElementRemoved = false;

        if (classList.contains(userClass)) {
            // don't assume about user class, first check if it's even there
            classBuilder.append(userClass);
            classList.remove(userClass);
            firstElementRemoved = true;
        }

        if (!classList.isEmpty()) {
            if (firstElementRemoved) {
                // there are more classes, put a comma
                classBuilder.append(", ");
            }

            for (int i = 0; i < classList.size(); i++) {
                // put the class
                String clazz = classList.get(i);
                classBuilder.append(clazz);

                // if not the last class, put a comma
                if (i < classList.size() - 1) {
                    classBuilder.append(", ");
                }
            }
        }

        return classBuilder.toString();
    }

    /**
     * Takes the given update, and from the affected classes/"globality" and the text of the update,
     * format a string from it, showing its information.
     * @param update The update to format
     * @param ctx Context, used to get preference values and strings
     * @return Formatted string detailing the update
     */
    public static String formatUpdate(Update update, Context ctx) {
        return formatUpdate(update, ctx.getString(R.string.global_update), PreferenceUtil.getClassPreference(ctx));
    }

    /**
     * Separated the actual work from the main method to make it easier for me to create a unit
     * test for this.
     * @param update The update object
     * @param globalUpdateString What text to show if it's global
     * @param userClass The user's class
     * @return Formatted string detailing the update
     */
    public static String formatUpdate(Update update, String globalUpdateString, String userClass) {
        if (update == null) {
            // your daily null check!
            return null;
        }

        String affects = "";

        if (update.getAffected().isGlobal()) {
            // global update; set affects string to global_update
            affects = globalUpdateString;
        } else if (update.getAffected() instanceof ClassesAffected) {
            // normal update; get formatted class string
            ClassesAffected affected = (ClassesAffected) update.getAffected();
            affects = formatClassString(affected.getClassesAffected(), userClass);
        }

        // return formatted string like in this example (in english): I7, K5: blah blah
        return affects + ": " + update.getText();
    }

}
