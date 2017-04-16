package ca.mcgill.science.ctf.utils;

import android.app.Activity;
import android.content.Context;

import ca.allanwang.capsule.library.utils.CPrefs;
import ca.mcgill.science.ctf.R;

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

    public static void setTheme(Activity activity) {
        Preferences prefs = new Preferences(activity);
        if (prefs.isDarkMode()) activity.setTheme(R.style.AppTheme_Dark_NoActionBar);
        else activity.setTheme(R.style.AppTheme_NoActionBar);
    }

}
