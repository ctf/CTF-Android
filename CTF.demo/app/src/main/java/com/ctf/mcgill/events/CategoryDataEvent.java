package com.ctf.mcgill.events;

import com.ctf.mcgill.enums.DataType;

/**
 * Created by Allan Wang on 27/12/2016.
 */

public class CategoryDataEvent {
    public final DataType.Category type;
    public boolean forceReload = false; //Default to lenient
    public final Object[] extras;

    public CategoryDataEvent(DataType.Category type, Object... extras) {
        this.type = type;
        this.extras = extras;
    }

    public CategoryDataEvent(DataType.Category type, boolean forceReload, Object... extras) {
        this.type = type;
        this.forceReload = forceReload;
        this.extras = extras;
    }
}
