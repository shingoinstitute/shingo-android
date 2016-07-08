package org.shingo.shingoapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import org.shingo.shingoapp.R;
import org.shingo.shingoapp.middle.SEntity.SPerson;
import org.shingo.shingoapp.middle.SEvent.SDay;
import org.shingo.shingoapp.middle.SEvent.SEvent;
import org.shingo.shingoapp.middle.SEvent.SSession;
import org.shingo.shingoapp.ui.events.AgendaFragment;
import org.shingo.shingoapp.ui.events.EventFragment;
import org.shingo.shingoapp.ui.events.EventListActivity;
import org.shingo.shingoapp.ui.events.SessionFragment;
import org.shingo.shingoapp.ui.events.SpeakerFragment;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, EventFragment.OnEventListFragmentInteractionListener,
        AgendaFragment.OnAgendaFragmentInteractionListener, SessionFragment.OnSessionListFragmentInteractionListener,
        SpeakerFragment.OnSpeakerListFragmentInteractionListener {

    public Map<String, SEvent> mEvents = new HashMap<>();
    public String mEventId = "";
    public Date lastListPull = new Date();
    public NavigationView navigationView;

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

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
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
        // Handle navigation view item clicks here.
        if(item == null) return false;
        int id = item.getItemId();

        if (id == R.id.nav_agenda) {
            AgendaFragment fragment = AgendaFragment.newInstance(mEvents.get(mEventId).getId());
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.main_content, fragment)
                    .commit();
        } else if (id == R.id.nav_sessions) {
            SessionFragment fragment = SessionFragment.newInstance(mEvents.get(mEventId).getId(),"event_id");
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.main_content, fragment)
                    .commit();
        } else if (id == R.id.nav_speakers) {
            SpeakerFragment fragment = SpeakerFragment.newInstance(mEvents.get(mEventId).getId(),"event_id");
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.main_content, fragment)
                    .commit();
        } else if (id == R.id.nav_login) {

        } else if (id == R.id.nav_manage) {

        } else if(id == R.id.nav_events){
            navigationView.getMenu().findItem(R.id.event_menu).setVisible(false);
            EventFragment fragment = EventFragment.newInstance(1);
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.main_content, fragment)
                    .commit();
        } else if(id == R.id.nav_model){
            navigationView.getMenu().findItem(R.id.event_menu).setVisible(false);
            ModelFragment fragment = ModelFragment.newInstance();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.main_content, fragment)
                    .commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onEventListFragmentInteraction(SEvent event) {
        mEventId = event.getId();
        Log.d("Event Id set",mEventId);
        navigationView.getMenu().findItem(R.id.event_menu).setVisible(true);
        setTitle(event.getName());
        onNavigationItemSelected(navigationView.getMenu().findItem(R.id.nav_agenda));
    }

    @Override
    public void onListFragmentInteraction(SDay day) {
        SessionFragment fragment = SessionFragment.newInstance(day.getId(), "agenda_id");
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.main_content, fragment)
                .commit();
    }

    @Override
    public void onListFragmentInteraction(SSession item) {

    }

    @Override
    public void onListFragmentInteraction(SPerson person) {

    }
}
