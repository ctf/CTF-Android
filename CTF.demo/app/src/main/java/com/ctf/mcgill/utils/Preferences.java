package com.ctf.mcgill.utils;

import android.content.Context;

import com.pitchedapps.capsule.library.utils.CPrefs;

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
