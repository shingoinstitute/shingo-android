package org.shingo.shingoapp.ui.events;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import org.json.JSONException;
import org.json.JSONObject;
import org.shingo.shingoapp.R;
import org.shingo.shingoapp.data.GetAsyncData;
import org.shingo.shingoapp.data.OnTaskComplete;
import org.shingo.shingoapp.middle.SEntity.SPerson;
import org.shingo.shingoapp.middle.SEvent.SDay;
import org.shingo.shingoapp.middle.SEvent.SEvent;
import org.shingo.shingoapp.middle.SEvent.SSession;
import org.shingo.shingoapp.ui.ModelActivity;

public class EventDetailActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnTaskComplete,
        AgendaFragment.OnAgendaFragmentInteractionListener, SessionFragment.OnSessionListFragmentInteractionListener,
        SpeakerFragment.OnSpeakerListFragmentInteractionListener {

    private SEvent mEvent;
    private ProgressDialog progress;
    private NavigationView navigationView;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        String id = getIntent().getExtras().getString("event_id");

        GetAsyncData getEventAsync = new GetAsyncData(this);
        String[] params = {"/salesforce/events/" + id};
        getEventAsync.execute(params);

        progress = ProgressDialog.show(this, "", "Loading event details...");


//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        navigationView.getMenu().getItem(0).setChecked(true);
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
        getMenuInflater().inflate(R.menu.event_detail, menu);
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
        int id = item.getItemId();

        if (id == R.id.nav_agenda) {
            AgendaFragment fragment = AgendaFragment.newInstance(mEvent.getId());
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.event_detail, fragment)
                    .commit();
        } else if (id == R.id.nav_sessions) {
            SessionFragment fragment = SessionFragment.newInstance(mEvent.getId(),"event_id");
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.event_detail, fragment)
                    .commit();
        } else if (id == R.id.nav_speakers) {
            SpeakerFragment fragment = SpeakerFragment.newInstance(mEvent.getId(),"event_id");
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.event_detail, fragment)
                    .commit();
        } else if (id == R.id.nav_login) {

        } else if (id == R.id.nav_manage) {

        } else if(id == R.id.nav_events){
            startActivity(new Intent(this, EventListActivity.class));
        } else if(id == R.id.nav_model){
            startActivity(new Intent(this, ModelActivity.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onTaskComplete() {

    }

    @Override
    public void onTaskComplete(String response) {
        try {
            JSONObject result = new JSONObject(response);
            if(result.getBoolean("success")){
                mEvent = new SEvent();
                mEvent.fromJSON(result.getJSONObject("event").toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        toolbar.setTitle(mEvent.getName());
        onNavigationItemSelected(navigationView.getMenu().findItem(R.id.nav_agenda));

        progress.dismiss();
    }

    @Override
    public void onTaskError(String error) {
        progress.dismiss();
    }

    @Override
    public void onListFragmentInteraction(SDay day) {
        if(day != null){
            SessionFragment fragment = SessionFragment.newInstance(day.getId(), "agenda_id");
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.event_detail, fragment)
                    .commit();

            navigationView.getMenu().findItem(R.id.nav_sessions).setChecked(true);
        }
    }

    @Override
    public void onListFragmentInteraction(SSession item) {
        SpeakerFragment fragment = SpeakerFragment.newInstance(item.getId(), "session_id");
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.event_detail, fragment)
                .commit();

        navigationView.getMenu().findItem(R.id.nav_speakers).setChecked(true);
    }

    @Override
    public void onListFragmentInteraction(SPerson person) {

    }
}
