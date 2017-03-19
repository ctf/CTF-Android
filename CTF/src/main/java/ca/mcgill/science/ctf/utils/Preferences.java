package ca.mcgill.science.ctf.utils;

import android.content.Context;

import ca.allanwang.capsule.library.utils.CPrefs;

/**
 * Created by Allan Wang on 2016-11-20.
 */

public class Preferences extends CPrefs {

    public Preferences(Context context) {
        super(context);
    }

    public boolean isDarkMode() {
        return getBoolean("pref_theme", false);
    }

}
