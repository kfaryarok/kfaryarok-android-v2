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

package io.github.kfaryarok.android.updates.api;

import java.util.List;

/**
 * In true OOP-fashion, new class for containing new updates, that will be given to the adapter
 * for creating new cards.
 *
 * Most of the class works by using different constructor for different updates.
 * You have {@link #UpdateImpl(String[], String)} for when you want to create
 * an update for only some classes, and {@link #UpdateImpl(String)} for when you want
 * to create a global update.
 *
 * @author tbsc on 03/03/2017 (copied from v1)
 */
public class UpdateImpl implements Update {

    private Affected affected;
    private String summary;

    /**
     * Constructor for creating an update, that affects only one class.
     *
     * @param affectedClass Name of class that this update affects.
     *                      If, for example, I'm in E7, and this update affects
     *                      E6, I'm not one of the affected classes. But if it
     *                      affects E7, then I should see this update.
     * @param text A short text explaining the update
     */
    public UpdateImpl(String affectedClass, String text) {
        this.affected = new ClassesAffected(affectedClass);
        this.summary = text;
    }

    /**
     * Constructor for creating an update, that affects only certain classes.
     *
     * @param affectedClasses Array of classes that this update affects.
     *                        If, for example, I'm in E7, and this update affects
     *                        E6, I'm not one of the affected classes. But if it
     *                        affects E7, then I should see this update.
     * @param text A short text explaining the update
     */
    public UpdateImpl(String[] affectedClasses, String text) {
        this.affected = new ClassesAffected(affectedClasses);
        this.summary = text;
    }

    /**
     * Constructor for creating an update, that affects only certain classes.
     *
     * @param affectedClasses List of classes that this update affects.
     *                        If, for example, I'm in E7, and this update affects
     *                        E6, I'm not one of the affected classes. But if it
     *                        affects E7, then I should see this update.
     * @param text A short text explaining the update
     */
    public UpdateImpl(List<String> affectedClasses, String text) {
        this.affected = new ClassesAffected(affectedClasses.toArray(new String[0]));
        this.summary = text;
    }

    /**
     * Constructor for creating a global update.
     *
     * @param text what to display
     */
    public UpdateImpl(String text) {
        this.affected = new GlobalAffected();
        this.summary = text;
    }

    /**
     * Creates an update with no specific affection.
     *
     * @param affects who this affects
     * @param text short text to display on card
     */
    public UpdateImpl(Affected affects, String text) {
        this.affected = affects;
        this.summary = text;
    }

    @Override
    public Affected getAffected() {
        return affected;
    }

    @Override
    public String getText() {
        return summary;
    }

}
