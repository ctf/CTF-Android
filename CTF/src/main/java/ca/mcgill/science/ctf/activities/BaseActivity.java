package ca.mcgill.science.ctf.activities;

import ca.allanwang.capsule.library.activities.CapsuleActivityFrame;
import ca.mcgill.science.ctf.BuildConfig;

/**
 * Created by Allan Wang on 2017-03-25.
 */

public abstract class BaseActivity extends CapsuleActivityFrame {

    public boolean isDebug() {
        return BuildConfig.DEBUG || BuildConfig.BUILD_TYPE.equals("releaseTest");
    }

    public void postEventDebug(Object event) {
        if (isDebug()) postEvent(event);
    }
}
