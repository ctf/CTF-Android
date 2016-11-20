package com.example.ctfdemo.fragments;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;

import com.example.ctfdemo.MainActivity;
import com.example.ctfdemo.R;
import com.example.ctfdemo.auth.AccountUtil;
import com.example.ctfdemo.requests.CTFSpiceService;
import com.example.ctfdemo.requests.LogoutRequest;
import com.example.ctfdemo.tepid.Session;
import com.example.ctfdemo.utils.Preferences;
import com.gc.materialdesign.widgets.Dialog;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.util.Locale;

public class SettingsFragment extends PreferenceFragmentCompat {

    public static final String TAG = "SETTINGS_FRAGMENT", KEY_TOKEN = "TOKEN";
    private String token;
    private SpiceManager requestManager = new SpiceManager(CTFSpiceService.class);
    private Preferences mPrefs;

    public static SettingsFragment newInstance(String token) {
        SettingsFragment frag = new SettingsFragment();
        Bundle args = new Bundle();
        args.putString(KEY_TOKEN, token);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        Bundle args = getArguments();
        if (args != null) {
            token = args.getString(KEY_TOKEN);
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        requestManager.start(getActivity());
    }

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        mPrefs = new Preferences(getContext());
        getPreferenceManager().setSharedPreferencesName("CAPSULE_PREFERENCES");
        addPreferencesFromResource(R.xml.preferences);

        findPreference("pref_logout").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
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
                    getActivity().recreate();
                    //todo this brings us back to the main page, want to get back to settings page
                }
                return true;
            }
        });

        findPreference("pref_theme").setDefaultValue(mPrefs.isDarkMode());
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
                requestManager.execute(new LogoutRequest(token, session.getId()), new RequestListener<Void>() {
                    @Override
                    public void onRequestFailure(SpiceException spiceException) {
                        AccountUtil.removeAccount();
                        getActivity().finish();
                    }

                    @Override
                    public void onRequestSuccess(Void v) {
                        AccountUtil.removeAccount();
                        getActivity().finish();
                    }
                });
                return false;
            }
        });
    }

    @Override
    public void onStop() {
        if (requestManager.isStarted()) {
            requestManager.shouldStop();
        }
        super.onStop();
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
