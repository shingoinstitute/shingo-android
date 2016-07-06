package org.shingo.shingoapp.ui.events;

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
import org.shingo.shingoapp.middle.SEvent.SDay;
import org.shingo.shingoapp.middle.SEvent.SEvent;
import org.shingo.shingoapp.middle.SEvent.SSession;

public class EventDetailActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnTaskComplete, AgendaFragment.OnAgendaFragmentInteractionListener, SessionFragment.OnSessionListFragmentInteractionListener {

    private SEvent mEvent;
    private ProgressDialog progress;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
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
        } else if (id == R.id.nav_speaker) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

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
            SessionFragment fragment = SessionFragment.newInstance(day.getId());
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.event_detail, fragment)
                    .commit();
        }
    }

    @Override
    public void onListFragmentInteraction(SSession item) {

    }
}
