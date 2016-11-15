package com.example.ctfdemo;

import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.preference.PreferenceManager;

import com.example.ctfdemo.auth.AccountUtil;
import com.example.ctfdemo.fragments.DashboardFragment;
import com.example.ctfdemo.fragments.MyAccountFragment;
import com.example.ctfdemo.fragments.ReportProblemFragment;
import com.example.ctfdemo.fragments.RoomFragment;
import com.example.ctfdemo.fragments.SettingsFragment;
import com.example.ctfdemo.requests.CTFSpiceService;
import com.example.ctfdemo.requests.TokenRequest;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.pitchedapps.capsule.library.activities.CapsuleActivityFrame;
import com.pitchedapps.capsule.library.interfaces.CDrawerItem;
import com.pitchedapps.capsule.library.item.DrawerItem;

public class MainActivity extends CapsuleActivityFrame {

    private String token;
    private SpiceManager requestManager = new SpiceManager(CTFSpiceService.class);

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        preCapsuleOnCreate(savedInstanceState); //rerouting onCreate based on what is available

        AccountUtil.initAccount(this);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String lang = preferences.getString("pref_language", "en");
        SettingsFragment.setLocale(this, lang); //todo put setlocale somewhere else? CTFApp maybe?


        if (isWifiConnected()) {
            if (AccountUtil.isSignedIn()) {
                capsuleOnCreate(savedInstanceState);
                cFab.hide(); //we don't the fab for now
                capsuleFrameOnCreate(savedInstanceState);
            } else {
                //TODO make account work
                AccountManager.get(this).addAccount(AccountUtil.accountType, AccountUtil.tokenType, null, null, this, new AccountManagerCallback<Bundle>() {
                    @Override
                    public void run(AccountManagerFuture<Bundle> accountManagerFuture) {
                        // switch back to main activity after user signs in
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                    }
                }, null);
            }
        } else {
            setContentView(R.layout.no_wifi);
        }

    }

    /**
     * Sets up account header
     * will not be added if null
     *
     * @return desired header
     */
    @Nullable
    @Override
    protected AccountHeader getAccountHeader() {
        return new AccountHeaderBuilder().withActivity(this)
                .withHeaderBackground(R.color.colorPrimary)
                .withSelectionFirstLine(s(R.string.app_name))
                .withSelectionSecondLine(BuildConfig.VERSION_NAME)
                .withProfileImagesClickable(false)
                .withResetDrawerOnProfileListClick(false)
                .addProfiles(
                        new ProfileDrawerItem().withIcon(ContextCompat.getDrawable(this, R.drawable.ctf))
                )
                .withSelectionListEnabled(false)
                .withSelectionListEnabledForSingleProfile(false)
                .build();
    }

    /**
     * Sets up array of drawer items
     *
     * @return array of drawer items
     */
    @Override
    protected CDrawerItem[] getDrawerItems() {
        return new CDrawerItem[]{ //TODO add fragments
                new DrawerItem(DashboardFragment.newInstance(token), R.string.dashboard, GoogleMaterial.Icon.gmd_dashboard, true),
                new DrawerItem(RoomFragment.newInstance(token), R.string.roominfo, GoogleMaterial.Icon.gmd_weekend, true),
                new DrawerItem(MyAccountFragment.newInstance(token), R.string.userinfo, GoogleMaterial.Icon.gmd_person, true),
                new DrawerItem(SettingsFragment.newInstance(token), R.string.settings, GoogleMaterial.Icon.gmd_settings, true), //TODO No capsule based, verify
                new DrawerItem(new ReportProblemFragment(), R.string.reportproblem, GoogleMaterial.Icon.gmd_error, true)
        };
    }

    @Override
    protected void onStart() {
        if (AccountUtil.isSignedIn()) {
            requestManager.start(this);
            requestManager.execute(new TokenRequest(AccountUtil.getAccount(), this), new RequestListener<String>() {
                @Override
                public void onRequestFailure(SpiceException spiceException) {
                    // todo wat do if we can't get token?!
                }

                @Override
                public void onRequestSuccess(String str) {
                    token = str;
                    selectDrawerItem(0); //Go to dashboard
                }
            });
        }

        super.onStart();
    }

    @Override
    protected void onResume() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String lang = preferences.getString("pref_language", "en");
        SettingsFragment.setLocale(this, lang);
        //prefs.setTheme();
        super.onResume();
    }

    @Override
    protected void onStop() {
        if (requestManager.isStarted()) {
            requestManager.shouldStop();
        }
        super.onStop();
    }

    /**
     * helper method - checks if wifi is enabled and if the phone is connected to a network
     * we'll need wifi to talk to TEPID (although we are caching the responses, they could be very stale)
     * @return true if we have wifi, false otherwise
     */
    private boolean isWifiConnected() {
        WifiManager wm = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        if (wm.isWifiEnabled()) {
            WifiInfo wi = wm.getConnectionInfo();
            if (null != wi && wi.getNetworkId() != -1) {
                return true;
            }
        }
        return false;
    }

}
