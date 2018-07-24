package com.example.internadmin.fooddiary.Interfaces;

/**
 * Interface used by DishID, in the event an activity
 * using the DishID model wants to know when the DishID
 * information has been properly populated.
 *
 * If dataAdded is true, it means that the DishID
 * was properly populated.
 *
 * If false, the DishID was not properly populated,
 * and error handling routines should be done.
 */

public interface DishIDPopulatedListener {
    void onPopulated(boolean dataAdded);
}