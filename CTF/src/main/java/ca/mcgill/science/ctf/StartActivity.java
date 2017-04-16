package ca.mcgill.science.ctf;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;

import ca.allanwang.capsule.library.logging.CLog;
import ca.allanwang.capsule.library.permissions.CPermissionCallback;
import ca.mcgill.science.ctf.api.TEPIDAPI;
import ca.mcgill.science.ctf.auth.AccountUtil;
import ca.mcgill.science.ctf.fragments.SettingsFragment;
import ca.mcgill.science.ctf.utils.Preferences;
import ca.mcgill.science.ctf.utils.Utils;

/**
 * Created by Allan Wang on 2017-04-16.
 */

public class StartActivity extends AppCompatActivity {

    public static final String EXTRA_TOKEN = "TEPID_TOKEN";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        checkAccountPermission(new CPermissionCallback() {
//            @Override
//            public void onResult(PermissionResult result) {
//                if (result.isAllGranted()) {
        AccountUtil.initAccount(this);
//                } else {
//                    //Stop app?
//                }
//            }
//        });
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String lang = preferences.getString("pref_language", "en");
        SettingsFragment.setLocale(this, lang); //todo put setlocale somewhere else? CTFApp maybe?

        if (AccountUtil.isSignedIn()) {
            AccountUtil.requestToken(this, new AccountUtil.TokenRequestCallback() {
                @Override
                public void onReceived(@NonNull String token) {
                    //initialize TEPID API
                    TEPIDAPI.Companion.setInstance(token, StartActivity.this); //to be sure, set api instance here
                    CLog.d("Token received %s", token);
                    launchMainActivity(token);
                }

                @Override
                public void onFailed() {
                    requestAccount();
                }
            });
        } else if (Utils.isNetworkAvailable(this))
            requestAccount();
        else
            setContentView(R.layout.no_wifi);
    }

    private void launchMainActivity(String token) {
        Intent intent = new Intent(getBaseContext(), MainActivity.class);
        intent.putExtra(EXTRA_TOKEN, token);
        startActivity(intent);
    }

    private void requestAccount() {
        AccountUtil.requestAccount(this, future -> {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        });
    }

    //TODO use this?
//    public void checkAccountPermission(@NonNull CPermissionCallback callback) {
//        requestPermission(callback, 42, Manifest.permission.GET_ACCOUNTS);
//    }
}
