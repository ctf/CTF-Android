package ca.mcgill.science.ctf.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.StringRes;

/**
 * Created by Allan Wang on 19/03/2017.
 */

public class Utils {

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    public static String stringFormatter(Context context, @StringRes int base, boolean condition, @StringRes int yes, @StringRes int no) {
        return String.format(context.getString(base, condition ? context.getString(yes) : context.getString(no)));
    }
}
