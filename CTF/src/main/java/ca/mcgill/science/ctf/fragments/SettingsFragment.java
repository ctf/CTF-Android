package ca.mcgill.science.ctf.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;

import org.greenrobot.eventbus.EventBus;

import java.util.Locale;

import ca.allanwang.capsule.library.event.CFabEvent;
import ca.allanwang.capsule.library.interfaces.CFragmentCore;
import ca.mcgill.science.ctf.MainActivity;
import ca.mcgill.science.ctf.R;
import ca.mcgill.science.ctf.api.ITEPID;
import ca.mcgill.science.ctf.auth.AccountUtil;
import ca.mcgill.science.ctf.fragments.base.BaseFragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Allan Wang on 18/03/2017.
 */

public class SettingsFragment extends PreferenceFragmentCompat implements CFragmentCore{

    private ITEPID mAPI;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAPI = BaseFragment.getAPI(this);
    }

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        getPreferenceManager().setSharedPreferencesName("CAPSULE_PREFERENCES");
        addPreferencesFromResource(R.xml.preferences);

        findPreference("pref_language").setOnPreferenceChangeListener((preference, o) -> {
            if (o instanceof String) {
                setLocale(getActivity(), (String) o);
                ((MainActivity) getActivity()).reload();
                //todo this brings us back to the main page, want to get back to settings page (fixed 2016/12/28)
            }
            return true;
        });

        findPreference("pref_theme").setOnPreferenceClickListener(preference -> {
            ((MainActivity) getActivity()).reload();
            return false;
        });

        findPreference("pref_about").setOnPreferenceClickListener(preference -> {
            //TODO add about
//                Dialog dialog = new Dialog(getActivity(), "About", "...");
//                dialog.show();
            return true;
        });

        findPreference("pref_logout").setOnPreferenceClickListener(preference -> {
            mAPI.removeSession(AccountUtil.getSession().get_id()).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    logout();
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    logout(); //TODO notify user and don't actually logout?
                }
            });
            return false;
        });
    }

    private void logout() {
        ((MainActivity) getActivity()).removeAccount();
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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        EventBus.getDefault().post(new CFabEvent(false));
    }

    @Override
    public int getTitleId() {
        return R.string.settings;
    }
}