package com.example.ctfdemo;

/* note to future self : don't fucking touch the v4 support lib fragment manager,
 * or the v4 support lib fragments. I fucking finally got it working nicely so that
 * all pages share the same navigation drawer. You can't use the android.app.Fragment
 * because some of the methods implemented for the recyclerview on the roominfo pages
 * won't work, and treating them as a special case with their own activity fucking lags,
 * and brings up a host of other problems, so leave it this way unless you've found
 * a better way to do it.
 */

import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.ctfdemo.auth.AccountUtil;
import com.example.ctfdemo.fragments.MainFragment;
import com.example.ctfdemo.fragments.MyAccountFragment;
import com.example.ctfdemo.fragments.ReportProblemFragment;
import com.example.ctfdemo.fragments.RoomFragment;
import com.example.ctfdemo.fragments.SettingsFragment;
import com.example.ctfdemo.requests.CTFSpiceService;
import com.example.ctfdemo.requests.TokenRequest;
import com.gc.materialdesign.widgets.Dialog;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.util.Locale;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private String username, token;
    private SpiceManager requestManager = new SpiceManager(CTFSpiceService.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AccountUtil.initAccount(this);
        setLocale();

        if (AccountUtil.isSignedIn()) {
            setContentView(R.layout.activity_main);

            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.setDrawerListener(toggle);
            toggle.syncState();
            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);

            username = AccountUtil.getUserName();

        } else {
            AccountManager.get(this).addAccount(AccountUtil.accountType, AccountUtil.tokenType, null, null, this, new AccountManagerCallback<Bundle>() {
                @Override
                public void run(AccountManagerFuture<Bundle> accountManagerFuture) {
                    // switch back to main activity after user signs in
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                }
            }, null);
        }
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
                    FragmentManager fm = getSupportFragmentManager();
                    fm.beginTransaction().replace(R.id.content_frame, MainFragment.newInstance(username, token)).commit();
                }
            });
        }

        super.onStart();
    }

    @Override
    protected void onResume() {
        setLocale();
        super.onResume();
    }

    @Override
    protected void onStop() {
        if (requestManager.isStarted()) {
            requestManager.shouldStop();
        }
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) { // no use for this atm
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
/*        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);*/
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        FragmentManager fm = getSupportFragmentManager();
        int id = item.getItemId();

        //todo use fragment.newinstance when possible, no reason for every fragment to start making token requests
        //todo null check token before passing to frags
        switch (id) {
            case R.id.dashboard:
                fm.beginTransaction().replace(R.id.content_frame, MainFragment.newInstance(username, token)).commit();
                getSupportActionBar().setTitle(R.string.dashboard);
                break;
            case R.id.room_info:
                fm.beginTransaction().replace(R.id.content_frame, RoomFragment.newInstance(token)).commit();
                getSupportActionBar().setTitle(R.string.roominfo);
                break;
            case R.id.user_info:
                fm.beginTransaction().replace(R.id.content_frame, MyAccountFragment.newInstance(username, token)).commit();
                getSupportActionBar().setTitle(R.string.userinfo);
                break;
            case R.id.settings:
                fm.beginTransaction().replace(R.id.content_frame, new SettingsFragment()).commit();
                getSupportActionBar().setTitle(R.string.settings);
                break;
            case R.id.report_problem:
                fm.beginTransaction().replace(R.id.content_frame, new ReportProblemFragment()).commit();
                getSupportActionBar().setTitle(R.string.reportproblem);
                break;
            case R.id.logout:
                showLogoutDialog();
                break;
        }

        // then close the drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    //todo kill the dialog
    private void showLogoutDialog() {
        String[] logoutMessages = getResources().getStringArray(R.array.logout_messages);
        int rand = new Random().nextInt(logoutMessages.length);
        final Dialog dialog = new Dialog(this, "", logoutMessages[rand].toUpperCase());
        dialog.setOnAcceptButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AccountUtil.removeAccount();
                finish();
                //System.exit(0);
            }
        });
        dialog.show();
    }

    private void setLocale() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String lang = preferences.getString("user_language", "en");
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
    }
}
