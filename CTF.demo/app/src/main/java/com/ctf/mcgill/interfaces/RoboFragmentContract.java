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
     * Returns unique DataType Category containing all the appropriate requests
     * @return enum
     */
    DataType.Category getDataCategory();

    /**
     * Eventbus call when event is sent
     * Be sure to add @Subscribe annotation to the submost class
     * @param event loading event sent
     */
    void onLoadEvent(LoadEvent event);

    /**
     * Updates the recyclerview and sends a new spice request
     */
    void updateData();

    /**
     * Get all potential data from RequestActivity
     * @param args getArguments(), may be null
     */
    void getArgs(@Nullable Bundle args);

    //Be sure to also create a static newInstance as a way to put send the data
}
