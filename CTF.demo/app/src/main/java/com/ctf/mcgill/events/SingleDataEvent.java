package com.ctf.mcgill.events;

import com.ctf.mcgill.enums.DataType;

/**
 * Created by Allan Wang on 27/12/2016.
 */

public class SingleDataEvent {
    public final DataType.Single type;
    public boolean forceReload = false; //Default to lenient
    public final Object[] extras;

    public SingleDataEvent(DataType.Single type, Object... extras) {
        this.type = type;
        this.extras = extras;
    }

    public SingleDataEvent(DataType.Single type, boolean forceReload, Object... extras) {
        this.type = type;
        this.forceReload = forceReload;
        this.extras = extras;
    }
}
