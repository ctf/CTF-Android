package ca.mcgill.science.ctf.fragments;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;

import com.gc.materialdesign.widgets.Dialog;

import java.util.Locale;

import ca.mcgill.science.ctf.MainActivity;
import ca.mcgill.science.ctf.R;
import ca.mcgill.science.ctf.api.Session;
import ca.mcgill.science.ctf.api.TEPIDAPI;
import ca.mcgill.science.ctf.auth.AccountUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Allan Wang on 18/03/2017.
 */

public class SettingsFragment extends PreferenceFragmentCompat {

    private TEPIDAPI mAPI;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAPI = BaseFragment.getAPI(this);
    }

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        getPreferenceManager().setSharedPreferencesName("CAPSULE_PREFERENCES");
        addPreferencesFromResource(R.xml.preferences);

        findPreference("pref_logout").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
            @Override
            public boolean onPreferenceClick(Preference preference) {
                AccountUtil.removeAccount();
                getActivity().finish();
                return true;
            }
        });

        findPreference("pref_language").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                if (o instanceof String) {
                    setLocale(getActivity(), (String) o);
                    ((MainActivity) getActivity()).reload();
                    //todo this brings us back to the main page, want to get back to settings page (fixed 2016/12/28)
                }
                return true;
            }
        });

        findPreference("pref_theme").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                ((MainActivity) getActivity()).reload();
                return false;
            }
        });

        findPreference("pref_about").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Dialog dialog = new Dialog(getActivity(), "About", "...");
                dialog.show();
                return true;
            }
        });

        findPreference("pref_logout").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Session session = AccountUtil.getSession();
                mAPI.removeSession(AccountUtil.getSession().get_id()).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1)
                            AccountUtil.removeAccount();
                        getActivity().finish();
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1)
                            AccountUtil.removeAccount();
                        getActivity().finish();
                    }
                });
                return false;
            }
        });
    }

    /**
     * helper method to change the system locale from one supported language to another
     *
     * @param activity a context
     * @param lang     ISO 639-1 code (e.g., "en", "fr", "zh", etc.)
     */
    public static void setLocale(Activity activity, String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.setLocale(locale);
        activity.getResources().updateConfiguration(config, activity.getResources().getDisplayMetrics());
    }

}