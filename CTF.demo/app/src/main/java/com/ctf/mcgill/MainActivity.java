package com.ctf.mcgill;

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
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;

import com.ctf.mcgill.BuildConfig;
import com.ctf.mcgill.R;
import com.ctf.mcgill.auth.AccountUtil;
import com.ctf.mcgill.fragments.DashboardFragment;
import com.ctf.mcgill.fragments.MyAccountFragment;
import com.ctf.mcgill.fragments.ReportProblemFragment;
import com.ctf.mcgill.fragments.RoomFragment;
import com.ctf.mcgill.fragments.SettingsFragment;
import com.ctf.mcgill.requests.CTFSpiceService;
import com.ctf.mcgill.requests.TokenRequest;
import com.ctf.mcgill.utils.Preferences;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.pitchedapps.capsule.library.activities.CapsuleActivityFrame;
import com.pitchedapps.capsule.library.changelog.ChangelogDialog;
import com.pitchedapps.capsule.library.event.SnackbarEvent;
import com.pitchedapps.capsule.library.interfaces.CDrawerItem;
import com.pitchedapps.capsule.library.item.DrawerItem;

public class MainActivity extends CapsuleActivityFrame {

    private String token;

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        Preferences prefs = new Preferences(this);
        if (prefs.isDarkMode()) setTheme(R.style.AppTheme_Dark_NoActionBar);

        preCapsuleOnCreate(savedInstanceState);
        AccountUtil.initAccount(this);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String lang = preferences.getString("pref_language", "en");
        SettingsFragment.setLocale(this, lang); //todo put setlocale somewhere else? CTFApp maybe?
        final SpiceManager requestManager = new SpiceManager(CTFSpiceService.class);
        if (isWifiConnected()) {
            capsuleOnCreate(savedInstanceState);
            capsuleFrameOnCreate(savedInstanceState);
            cFab.hide(); //we don't the fab for now
            cCoordinatorLayout.setScrollAllowed(false); //scrolling is currently not being used
            if (AccountUtil.isSignedIn()) {
                requestManager.start(this);
                requestManager.execute(new TokenRequest(AccountUtil.getAccount(), this), new RequestListener<String>() {
                    @Override
                    public void onRequestFailure(SpiceException spiceException) {
                        // todo wat do if we can't get token?!
                        //should go back to sign in page - Allan
                        snackbar(new SnackbarEvent("Token request failed"));
                        requestManager.shouldStop();
                    }

                    @Override
                    public void onRequestSuccess(String str) {
                        token = str;
                        selectDrawerItem(getLastDrawerPosition()); //Go to dashboard by default
                        requestManager.shouldStop();
                    }
                });
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
                new DrawerItem(R.string.dashboard, GoogleMaterial.Icon.gmd_dashboard, true) {
                    @Nullable
                    @Override
                    public Fragment getFragment() {
                        return DashboardFragment.newInstance(token);
                    }
                },
                new DrawerItem(R.string.roominfo, GoogleMaterial.Icon.gmd_weekend, true) {
                    @Nullable
                    @Override
                    public Fragment getFragment() {
                        return RoomFragment.newInstance(token);
                    }
                },
                new DrawerItem(R.string.userinfo, GoogleMaterial.Icon.gmd_person, true) {
                    @Nullable
                    @Override
                    public Fragment getFragment() {
                        return MyAccountFragment.newInstance(token);
                    }
                },
                new DrawerItem(R.string.settings, GoogleMaterial.Icon.gmd_settings, true) {
                    @Nullable
                    @Override
                    public Fragment getFragment() {
                        return SettingsFragment.newInstance(token);
                    }
                }, //TODO Not capsule based, verify
                new DrawerItem(R.string.reportproblem, GoogleMaterial.Icon.gmd_error, true) {
                    @Nullable
                    @Override
                    public Fragment getFragment() {
                        return new ReportProblemFragment();
                    }
                }
        };
    }

    @Override
    protected void onResume() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String lang = preferences.getString("pref_language", "en");
        SettingsFragment.setLocale(this, lang);
        //prefs.setTheme();
        super.onResume();
    }

    /**
     * helper method - checks if wifi is enabled and if the phone is connected to a network
     * we'll need wifi to talk to TEPID (although we are caching the responses, they could be very stale)
     *
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_changelog) {
            ChangelogDialog.show(this, R.xml.changelog);
            return true;
        } else if (id == R.id.action_settings) {
            selectDrawerItem(3); //TODO do not hardcode number
        }

        return super.onOptionsItemSelected(item);
    }

}
