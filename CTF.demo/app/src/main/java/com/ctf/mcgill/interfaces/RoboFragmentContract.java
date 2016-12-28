package com.ctf.mcgill.interfaces;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.ctf.mcgill.enums.DataType;
import com.ctf.mcgill.events.LoadEvent;

/**
 * Created by Allan Wang on 26/12/2016.
 * <p>
 * Methods guaranteed to exist for fragments requiring spicemanager
 */

public interface RoboFragmentContract {

    /**
     * Get all potential data from RequestActivity
     * @param args getArguments(), may be null
     */
    void getArgs(@Nullable Bundle args);

    //Be sure to also create a static newInstance as a way to put send the data

    /**
     * Returns unique DataType Category containing all the appropriate requests
     * @return enum
     */
    DataType.Category getDataCategory();

    /**
     * Posts an event requesting the data for the current DataType Category
     */
    void requestData();

    /**
     * Eventbus call when event is sent
     * Be sure to add @Subscribe annotation to the submost class
     * This method should save all data, then call updateContent on that event.type
     * @param event loading event sent
     */
    void onLoadEvent(LoadEvent event);

    /**
     * Update a specified content given that the data retrieval has happened
     * However, the data may still be null or invalid, so check for that too
     * @param types types to update
     */
    void updateContent(DataType.Single... types);

}