package com.ctf.mcgill.events;

import com.ctf.mcgill.enums.DataType;

/**
 * Created by Allan Wang on 27/12/2016.
 */
public class CategoryDataEvent {
    public final DataType.Category type;
    public boolean forceReload = false; //Default to lenient
    public final Object extra;

    public CategoryDataEvent(DataType.Category type) {
        this.type = type;
        this.extra = null;
    }

    public CategoryDataEvent(DataType.Category type, Object extra) {
        this.type = type;
        this.extra = extra;
    }

    public CategoryDataEvent(DataType.Category type, boolean forceReload, Object extra) {
        this.type = type;
        this.forceReload = forceReload;
        this.extra = extra;
    }
}
