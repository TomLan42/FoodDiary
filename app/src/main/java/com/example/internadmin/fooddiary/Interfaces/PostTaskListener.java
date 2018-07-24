package com.example.internadmin.fooddiary.Interfaces;

/**
 * Interface used by DownloadDishIDTask and ImageUploadTask.
 * For returning the task results for further processing.
 * @param <Bundle>
 */

public interface PostTaskListener<Bundle> {
    void onPostTask(Bundle result);
}