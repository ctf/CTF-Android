package ca.mcgill.science.ctf.events;

import ca.mcgill.science.ctf.enums.DataType;

/**
 * Created by Allan Wang on 27/12/2016.
 */

public class SingleDataEvent {
    public final DataType.Single type;
    public boolean forceReload = false; //Default to lenient
    public final Object extra;

    public SingleDataEvent(DataType.Single type) {
        this.type = type;
        this.extra = null;
    }

    public SingleDataEvent(DataType.Single type, Object extra) {
        this.type = type;
        this.extra = extra;
    }

    public SingleDataEvent(DataType.Single type, boolean forceReload, Object extra) {
        this.type = type;
        this.forceReload = forceReload;
        this.extra = extra;
    }
}
