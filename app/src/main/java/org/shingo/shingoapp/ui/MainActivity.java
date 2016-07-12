package org.shingo.shingoapp.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
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
import android.widget.ImageView;
import android.widget.TextView;

import org.shingo.shingoapp.R;
import org.shingo.shingoapp.middle.SEntity.SPerson;
import org.shingo.shingoapp.middle.SEvent.SDay;
import org.shingo.shingoapp.middle.SEvent.SEvent;
import org.shingo.shingoapp.middle.SEvent.SSession;
import org.shingo.shingoapp.ui.events.AgendaFragment;
import org.shingo.shingoapp.ui.events.EventFragment;
import org.shingo.shingoapp.ui.events.SessionFragment;
import org.shingo.shingoapp.ui.events.SpeakerFragment;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, EventFragment.OnEventListFragmentInteractionListener,
        AgendaFragment.OnAgendaFragmentInteractionListener, SessionFragment.OnSessionListFragmentInteractionListener,
        SpeakerFragment.OnSpeakerListFragmentInteractionListener {

    public List<SEvent> mEvents = new ArrayList<>();
    public int mEventIndex;
    private Date lastListPull;
    public DrawerLayout drawer;
    public NavigationView navigationView;
    private static final long TIME_OUT = TimeUnit.MINUTES.toMillis(30);

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

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        assert navigationView != null;
        navigationView.setNavigationItemSelectedListener(this);
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

        if (id == R.id.nav_agenda) {
            AgendaFragment fragment = AgendaFragment.newInstance(mEvents.get(mEventIndex).getId());
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.main_content, fragment)
                    .commit();
        } else if (id == R.id.nav_sessions) {
            SessionFragment fragment = SessionFragment.newInstance();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.main_content, fragment)
                    .commit();
        } else if (id == R.id.nav_speakers) {
            SpeakerFragment fragment = SpeakerFragment.newInstance();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.main_content, fragment)
                    .commit();
        } else if (id == R.id.nav_login) {

        } else if (id == R.id.nav_manage) {

        } else if(id == R.id.nav_events){
            navigationView.getMenu().findItem(R.id.event_menu).setVisible(false);
            try{
                drawer.findViewById(R.id.nav_header).setBackground(new ColorDrawable(getResources().getColor(R.color.colorPrimaryDark)));
                ((TextView) drawer.findViewById(R.id.nav_header_title)).setText("Shingo Institute");
                ((TextView) drawer.findViewById(R.id.nav_header_detail)).setText("shingo.org");
            } catch(NullPointerException e){
                Log.d("Expected Exception", e.getStackTrace().toString());
            }
            EventFragment fragment = EventFragment.newInstance();
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
        assert drawer != null;
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onEventListFragmentInteraction(SEvent event) {
        mEventIndex = mEvents.indexOf(event);
        navigationView.getMenu().findItem(R.id.event_menu).setVisible(true);
        setTitle(event.getName());
        if(mEvents.get(mEventIndex).getBanner() == null && !event.getBannerUrl().equals("")){
            DownloadImageTask downloadImageTask = new DownloadImageTask(drawer.findViewById(R.id.nav_header), event);
            downloadImageTask.execute(event.getBannerUrl());
        } else if(mEvents.get(mEventIndex).getBanner() != null) {
            drawer.findViewById(R.id.nav_header).setBackground(new BitmapDrawable(getResources(), mEvents.get(mEventIndex).getBanner()));
        }
        ((TextView)drawer.findViewById(R.id.nav_header_title)).setText(event.getName());
        ((TextView)drawer.findViewById(R.id.nav_header_detail)).setText(event.getDisplayLocation());
        onNavigationItemSelected(navigationView.getMenu().findItem(R.id.nav_agenda));
    }

    @Override
    public void onListFragmentInteraction(SDay day) {
        SessionFragment fragment = SessionFragment.newInstance((ArrayList<String>) day.getSessions(), day.getId());
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.main_content, fragment)
                .commit();
    }

    @Override
    public void onListFragmentInteraction(SSession item) {
        SpeakerFragment fragment = SpeakerFragment.newInstance((ArrayList<String>) item.getSpeakers(), item.getId());
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
