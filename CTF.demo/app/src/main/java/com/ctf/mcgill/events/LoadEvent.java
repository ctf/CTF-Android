package com.ctf.mcgill.events;

import com.ctf.mcgill.enums.DataType;

/**
 * Created by Allan Wang on 26/12/2016.
 *
 * POJO sent after spice request via EventBus
 */

public class LoadEvent {

    public final DataType.Single type;
    public final boolean isSuccessful;
    public final Object data;

    public LoadEvent(DataType.Single type, boolean isSuccessful, Object data) {
        this.type = type;
        this.isSuccessful = isSuccessful;
        this.data = data;
    }
}
