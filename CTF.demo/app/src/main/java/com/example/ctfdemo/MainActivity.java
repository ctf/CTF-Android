package com.example.ctfdemo;

/* note to future self : don't fucking touch the v4 support lib fragment manager,
 * or the v4 support lib fragments. I fucking finally got it working nicely so that
 * all pages share the same navigation drawer. You can't use the android.app.Fragment
 * because some of the methods implemented for the recyclerview on the roominfo pages
 * won't work, and treating them as a special case with their own activity fucking lags,
 * and brings up a host of other bugs, so leave it this way unless you've found a better way to do it.
 */

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.fragments.MainFragment;
import com.example.fragments.ReportProblemFragment;
import com.example.fragments.SettingsFragment;
import com.example.fragments.UserInfoFragment;
import com.example.fragments.RoomInfoFragment;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().replace(R.id.content_frame, new MainFragment()).commit();
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        FragmentManager fm = getSupportFragmentManager();

        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.dashboard) {
            fm.beginTransaction().replace(R.id.content_frame, new MainFragment()).commit();
        } else if (id == R.id.room_info) {
            fm.beginTransaction().replace(R.id.content_frame, new RoomInfoFragment()).commit();
        } else if (id == R.id.user_info) {
            fm.beginTransaction().replace(R.id.content_frame, new UserInfoFragment()).commit();
        } else if (id == R.id.settings) {
            fm.beginTransaction().replace(R.id.content_frame, new SettingsFragment()).commit();
        } else if (id == R.id.report_problem) {
            Intent myIntent = new Intent(MainActivity.this, SendError.class);
            MainActivity.this.startActivity(myIntent);
            //fm.beginTransaction().replace(R.id.content_frame, new ReportProblemFragment()).commit();
            //Launching the activity this way allows it to actually work. (When I launch it your way it acts like the java file does not exist)
            //However, this makes the nav drawer non-existent.
            //Please fix.
            //Thanks, Z
        } else if (id == R.id.logout) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
