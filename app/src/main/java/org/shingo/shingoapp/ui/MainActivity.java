package org.shingo.shingoapp.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.shingo.shingoapp.R;
import org.shingo.shingoapp.middle.SEntity.SOrganization;
import org.shingo.shingoapp.middle.SEntity.SPerson;
import org.shingo.shingoapp.middle.SEvent.SDay;
import org.shingo.shingoapp.middle.SEvent.SEvent;
import org.shingo.shingoapp.middle.SEvent.SSession;
import org.shingo.shingoapp.ui.events.AgendaFragment;
import org.shingo.shingoapp.ui.events.EventDetailFragment;
import org.shingo.shingoapp.ui.events.EventFragment;
import org.shingo.shingoapp.ui.events.ExhibitorFragment;
import org.shingo.shingoapp.ui.events.SessionFragment;
import org.shingo.shingoapp.ui.events.SpeakerFragment;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, EventFragment.OnEventListFragmentInteractionListener,
        AgendaFragment.OnAgendaFragmentInteractionListener, SessionFragment.OnSessionListFragmentInteractionListener,
        SpeakerFragment.OnSpeakerListFragmentInteractionListener, EventDetailFragment.OnEventFragmentInteractionListener,
        ExhibitorFragment.OnListFragmentInteractionListener {

    public ArrayList<SEvent> mEvents = new ArrayList<>();
    public int mEventIndex;
    private Date lastListPull;
    public DrawerLayout drawer;
    public NavigationView navigationView;
    private static final long TIME_OUT = TimeUnit.MINUTES.toMillis(30);
    private Fragment mFragment;
    private int mToggle = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        assert drawer != null;
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        drawer.setSaveEnabled(true);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        assert navigationView != null;
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setSaveEnabled(true);

        if(savedInstanceState != null){
            mEvents = savedInstanceState.getParcelableArrayList("events");
            if(savedInstanceState.containsKey("update"))
                lastListPull = new Date(savedInstanceState.getLong("update"));
            mToggle = savedInstanceState.getInt("toggle");
            FragmentManager fragmentManager = getSupportFragmentManager();
            mFragment = fragmentManager.getFragment(savedInstanceState, "mFragment");
            fragmentManager.beginTransaction()
                    .replace(R.id.main_content, mFragment)
                    .commit();
            toggleNavHeader(mToggle);
        } else {
            onNavigationItemSelected(navigationView.getMenu().findItem(R.id.nav_home));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState){
        outState.putParcelableArrayList("events", mEvents);
        if(lastListPull != null)
            outState.putLong("update", lastListPull.getTime());
        outState.putInt("toggle", mToggle);
        super.onSaveInstanceState(outState);

        getSupportFragmentManager().putFragment(outState, "mFragment", mFragment);
    }

    @Override
    public void onBackPressed() {
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        assert drawer != null;
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            // TODO: Implement back navigation for fragments
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

        if(id == R.id.nav_home){
            toggleNavHeader(0);
            setTitle("Shingo App");
            HomeFragment fragment = HomeFragment.newInstance();
            mFragment = fragment;
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.main_content, fragment)
                    .commit();
        } else if(id == R.id.nav_detail){
            toggleNavHeader(1);
            EventDetailFragment fragment = EventDetailFragment.newInstance(mEvents.get(mEventIndex).getId());
            mFragment = fragment;
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.main_content, fragment)
                    .commit();
        } else if (id == R.id.nav_agenda) {
            AgendaFragment fragment = AgendaFragment.newInstance(mEvents.get(mEventIndex).getId());
            mFragment = fragment;
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.main_content, fragment)
                    .commit();
        } else if (id == R.id.nav_sessions) {
            SessionFragment fragment = SessionFragment.newInstance();
            mFragment = fragment;
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.main_content, fragment)
                    .commit();
        } else if (id == R.id.nav_speakers) {
            SpeakerFragment fragment = SpeakerFragment.newInstance();
            mFragment = fragment;
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.main_content, fragment)
                    .commit();
        } else if (id == R.id.nav_exhibitors){
            ExhibitorFragment fragment = ExhibitorFragment.newInstance(mEvents.get(mEventIndex).getId());
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.main_content, fragment)
                    .commit();
        } else if (id == R.id.nav_login) {

        } else if (id == R.id.nav_manage) {

        } else if(id == R.id.nav_events){
            toggleNavHeader(0);
            EventFragment fragment = EventFragment.newInstance();
            mFragment = fragment;
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.main_content, fragment)
                    .commit();
        } else if(id == R.id.nav_model){
            toggleNavHeader(0);
            ModelFragment fragment = ModelFragment.newInstance();
            mFragment = fragment;
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.main_content, fragment)
                    .commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        assert drawer != null;
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void toggleNavHeader(int type){
        mToggle = type;
        View header = navigationView.getHeaderView(0);
        switch (type){
            case 0:
                navigationView.getMenu().findItem(R.id.event_menu).setVisible(false);
                header.findViewById(R.id.nav_header).setBackground(new ColorDrawable(getResources().getColor(R.color.colorPrimaryDark)));
                ((TextView) header.findViewById(R.id.nav_header_title)).setText("Shingo Institute");
                ((TextView) header.findViewById(R.id.nav_header_detail)).setText("shingo.org");
                break;
            case 1:
                SEvent event = mEvents.get(mEventIndex);
                navigationView.getMenu().findItem(R.id.event_menu).setVisible(true);
                setTitle(event.getName());
                if(event.getBanner() == null && !event.getBannerUrl().equals("")){
                    DownloadImageTask downloadImageTask = new DownloadImageTask(header.findViewById(R.id.nav_header), event);
                    downloadImageTask.execute(event.getBannerUrl());
                } else if(mEvents.get(mEventIndex).getBanner() != null) {
                    header.findViewById(R.id.nav_header).setBackground(new BitmapDrawable(getResources(), mEvents.get(mEventIndex).getBanner()));
                }
                ((TextView)header.findViewById(R.id.nav_header_title)).setText(event.getName());
                ((TextView)header.findViewById(R.id.nav_header_detail)).setText(event.getDisplayLocation());
                break;
        }
    }

    @Override
    public void onEventListFragmentInteraction(SEvent event) {
        mEventIndex = mEvents.indexOf(event);
        onNavigationItemSelected(navigationView.getMenu().findItem(R.id.nav_detail));
    }

    @Override
    public void onListFragmentInteraction(SDay day) {
        SessionFragment fragment = SessionFragment.newInstance((ArrayList<String>) day.getSessions(), day.getId());
        mFragment = fragment;
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.main_content, fragment)
                .commit();
    }

    @Override
    public void onListFragmentInteraction(SSession item) {
        SpeakerFragment fragment = SpeakerFragment.newInstance((ArrayList<String>) item.getSpeakers(), item.getId());
        mFragment = fragment;
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.main_content, fragment)
                .commit();
    }

    @Override
    public void onListFragmentInteraction(SPerson person) {

    }

    public boolean needsUpdated(){
        if(lastListPull == null) return true;
        return lastListPull.after(new Date(lastListPull.getTime() + TIME_OUT));
    }

    public void updateListPull(){
        lastListPull = new Date();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onListFragmentInteraction(SOrganization org) {

    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        View bmImage;
        SEvent event;

        public DownloadImageTask(View bmImage, SEvent event) {
            this.bmImage = bmImage;
            this.event = event;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setBackground(new BitmapDrawable(getResources(), result));
            event.setBanner(result);
        }
    }
}
