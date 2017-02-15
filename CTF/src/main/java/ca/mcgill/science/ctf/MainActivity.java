package ca.mcgill.science.ctf;

import android.Manifest;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.core.CrashlyticsCore;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.pitchedapps.capsule.library.changelog.ChangelogDialog;
import com.pitchedapps.capsule.library.event.SnackbarEvent;
import com.pitchedapps.capsule.library.interfaces.CDrawerItem;
import com.pitchedapps.capsule.library.item.DrawerItem;
import com.pitchedapps.capsule.library.logging.CallbackLogTree;
import com.pitchedapps.capsule.library.permissions.CPermissionCallback;

import ca.mcgill.science.ctf.auth.AccountUtil;
import ca.mcgill.science.ctf.fragments.DashboardFragment;
import ca.mcgill.science.ctf.fragments.MyAccountFragment;
import ca.mcgill.science.ctf.fragments.ReportProblemFragment;
import ca.mcgill.science.ctf.fragments.RoomFragment;
import ca.mcgill.science.ctf.fragments.SettingsFragment;
import ca.mcgill.science.ctf.requests.CTFSpiceService;
import ca.mcgill.science.ctf.requests.TokenRequest;
import ca.mcgill.science.ctf.utils.Preferences;
import io.fabric.sdk.android.Fabric;
import timber.log.Timber;

public class MainActivity extends RequestActivity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        if (!BuildConfig.DEBUG) {
            disableCLog(); //Make things cleaner; don't log unless specified
            //Add crashlytics
            CrashlyticsCore core = new CrashlyticsCore.Builder().build();
            Fabric.with(this, new Crashlytics.Builder().core(core).build(), new Answers(), new Crashlytics());
            Timber.plant(new CallbackLogTree(new CallbackLogTree.Callback() {
                @Override
                public void log(int priority, String tag, String message, Throwable t) {
                    if (priority == Log.ERROR) {
                        if (t == null) {
                            Crashlytics.logException(new Exception(message));
                        } else {
                            Crashlytics.logException(t);
                        }
                    } else {
                        Crashlytics.log(priority, tag, message);
                    }
                }
            }));
        }
        super.onCreate(savedInstanceState);
        Preferences prefs = new Preferences(this);
        if (prefs.isDarkMode()) setTheme(R.style.AppTheme_Dark_NoActionBar);

        preCapsuleOnCreate(savedInstanceState);
//        checkAccountPermission(new CPermissionCallback() {
//            @Override
//            public void onResult(PermissionResult result) {
//                if (result.isAllGranted()) {
        AccountUtil.initAccount(MainActivity.this);
//                } else {
//                    //Stop app?
//                }
//            }
//        });

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String lang = preferences.getString("pref_language", "en");
        SettingsFragment.Companion.setLocale(this, lang); //todo put setlocale somewhere else? CTFApp maybe?
        final SpiceManager requestManager = new SpiceManager(CTFSpiceService.class);
        if (isWifiConnected()) {
            capsuleOnCreate(savedInstanceState);
            capsuleFrameOnCreate(savedInstanceState);
            cFab.hide(); //we don't use the fab for now
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
                        mToken = str;
                        selectDrawerItem(getLastDrawerPosition()); //Go to dashboard by default TODO fix commitAllowingStateLoss ->   java.lang.IllegalStateException: Can not perform this action after onSaveInstanceState
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

    //TODO use this?
    public void checkAccountPermission(@NonNull CPermissionCallback callback) {
        requestPermission(callback, 42, Manifest.permission.GET_ACCOUNTS);
    }

    /**
     * Sets up account header
     * will not be added if null
     *
     * @return desired header
     */
    @Nullable
    @Override
    protected AccountHeaderBuilder getAccountHeaderBuilder() {
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
                .withSelectionListEnabledForSingleProfile(false);
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
                        return DashboardFragment.newInstance(rQuota, rPrintJobArray, rRoomInfoList);
                    }
                },
                new DrawerItem(R.string.roominfo, GoogleMaterial.Icon.gmd_weekend, true) {
                    @Nullable
                    @Override
                    public Fragment getFragment() {
                        return RoomFragment.newInstance(rDestinationMap, rRoomJobsMap);
                    }
                },
                new DrawerItem(R.string.userinfo, GoogleMaterial.Icon.gmd_person, true) {
                    @Nullable
                    @Override
                    public Fragment getFragment() {
                        return MyAccountFragment.newInstance(rQuota, rPrintJobArray, rNickname, false);
                    }
                },
                new DrawerItem(R.string.settings, GoogleMaterial.Icon.gmd_settings, true) {
                    @Nullable
                    @Override
                    public Fragment getFragment() {
                        return SettingsFragment.Companion.newInstance(mToken);
                    }
                },
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
        SettingsFragment.Companion.setLocale(this, lang);
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
        switch (item.getItemId()) {
            case R.id.action_changelog:
                ChangelogDialog.show(this, R.xml.changelog);
                return true;
            case R.id.action_settings:
                selectDrawerItemFromId(R.string.settings); //Switch to settings; note that CDrawer for settings has this as the titleId
                break;
        }

        return super.onOptionsItemSelected(item);
    }

}
