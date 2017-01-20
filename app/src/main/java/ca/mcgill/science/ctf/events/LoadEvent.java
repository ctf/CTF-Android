package ca.mcgill.science.ctf.events;

import ca.mcgill.science.ctf.enums.DataType;

/**
 * Created by Allan Wang on 26/12/2016.
 * <p>
 * POJO sent after spice request via EventBus
 */

public class LoadEvent {

    public final DataType.Single type;
    public final boolean isSuccessful;
    public final Object data;
    private boolean activityOnly = false, fragmentOnly = false;

    public LoadEvent(DataType.Single type, boolean isSuccessful, Object data) {
        this.type = type;
        this.isSuccessful = isSuccessful;
        this.data = data;
    }

    public LoadEvent activityOnly() {
        activityOnly = true;
        fragmentOnly = false;
        return this;
    }

    public LoadEvent fragmentOnly() {
        activityOnly = false;
        fragmentOnly = true;
        return this;
    }

    public boolean isActivityOnly() {
        return activityOnly;
    }

    public boolean isFragmentOnly() {
        return fragmentOnly;
    }
}
