package org.shingo.shingoapp.ui;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
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
import org.shingo.shingoapp.middle.SEvent.SDay;
import org.shingo.shingoapp.middle.SEvent.SEvent;
import org.shingo.shingoapp.middle.SEvent.SSession;
import org.shingo.shingoapp.ui.events.AgendaFragment;
import org.shingo.shingoapp.ui.events.EventDetailFragment;
import org.shingo.shingoapp.ui.events.EventFragment;
import org.shingo.shingoapp.ui.interfaces.EventInterface;
import org.shingo.shingoapp.ui.events.ExhibitorFragment;
import org.shingo.shingoapp.ui.events.RecipientFragment;
import org.shingo.shingoapp.ui.events.SessionFragment;
import org.shingo.shingoapp.ui.events.SpeakerFragment;
import org.shingo.shingoapp.ui.events.SponsorFragment;
import org.shingo.shingoapp.ui.interfaces.NavigationInterface;
import org.shingo.shingoapp.ui.interfaces.OnErrorListener;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, EventFragment.OnEventFragmentInteractionListener,
        AgendaFragment.OnAgendaFragmentInteractionListener, SessionFragment.OnListFragmentInteractionListener,
        NavigationInterface, EventInterface, OnErrorListener {

    private static final long TIME_OUT = TimeUnit.MINUTES.toMillis(30);
    private Date lastListPull;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private Fragment mFragment;
    private int mToggle = 0;
    private ArrayList<SEvent> mEvents = new ArrayList<>();
    private SEvent mEvent;

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                if(getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    String tag = getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getName();
                    mFragment = getSupportFragmentManager().findFragmentByTag(tag);
                }
            }
        });

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
            mEvents = savedInstanceState.getParcelableArrayList("mEvents");
            if(savedInstanceState.containsKey("update"))
                lastListPull = new Date(savedInstanceState.getLong("lastListPull"));
            mEvent = savedInstanceState.getParcelable("mEvent");
            mFragment = getSupportFragmentManager().getFragment(savedInstanceState, "mFragment");
            replaceFragment(mFragment);
        } else {
            onNavigationItemSelected(navigationView.getMenu().findItem(R.id.nav_home));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState){
        outState.putParcelableArrayList("mEvents", mEvents);
        outState.putInt("toggle", mToggle);
        if(lastListPull != null)
            outState.putLong("lastListPull", lastListPull.getTime());
        if(mEvent != null)
            outState.putParcelable("mEvent", mEvent);

        super.onSaveInstanceState(outState);

        getSupportFragmentManager().putFragment(outState, "mFragment", mFragment);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public void onBackPressed() {
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        assert drawer != null;
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
        String eventId = "";
        if(mEvent != null)
            eventId = mEvent.getId();

        switch (id) {
            case R.id.nav_detail:
                replaceFragment(EventDetailFragment.newInstance(eventId));
                break;
            case R.id.nav_agenda:
                replaceFragment(AgendaFragment.newInstance(eventId));
                break;
            case R.id.nav_sessions:
                replaceFragment(SessionFragment.newInstance(eventId));
                break;
            case R.id.nav_speakers:
                replaceFragment(SpeakerFragment.newInstance(eventId));
                break;
            case R.id.nav_exhibitors:
                replaceFragment(ExhibitorFragment.newInstance(eventId));
                break;
            case R.id.nav_recipients:
                replaceFragment(RecipientFragment.newInstance(eventId));
                break;
            case R.id.nav_sponsors:
                replaceFragment(SponsorFragment.newInstance(eventId));
                break;
            case R.id.nav_events:
                replaceFragment(EventFragment.newInstance());
                break;
            case R.id.nav_model:
                replaceFragment(ModelFragment.newInstance());
                break;
            case R.id.nav_home:
            default:
                replaceFragment(HomeFragment.newInstance());
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void handleError(String error){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(error)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).setTitle("Error")
                .setIcon(R.drawable.ic_error);
        builder.create().show();
    }

    private void replaceFragment(Fragment fragment){
        mFragment = fragment;
        if(getSupportFragmentManager().findFragmentByTag(fragment.getClass().getName()) != null)
            getSupportFragmentManager().popBackStack(fragment.getClass().getName(),FragmentManager.POP_BACK_STACK_INCLUSIVE);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_content, fragment, fragment.getClass().getName())
                .addToBackStack(fragment.getClass().getName())
                .commit();
    }

    @SuppressWarnings("deprecation")
    public void toggleNavHeader(int type){
        mToggle = type;
        View header = navigationView.getHeaderView(0);
        switch (type){
            case 0:
                navigationView.getMenu().findItem(R.id.event_menu).setVisible(false);
                header.findViewById(R.id.nav_header).setBackground(new ColorDrawable(getResources().getColor(R.color.colorPrimaryDark)));
                ((TextView) header.findViewById(R.id.nav_header_title)).setText(getString(R.string.name));
                ((TextView) header.findViewById(R.id.nav_header_detail)).setText(getString(R.string.web_addr));
                break;
            case 1:
                if(mEvents.size() == 0) break;
                navigationView.getMenu().findItem(R.id.event_menu).setVisible(true);
                setTitle(mEvent.getName());
                if(mEvent.getBanner() == null && !mEvent.getBannerUrl().equals("")){
                    DownloadImageTask downloadImageTask = new DownloadImageTask(header.findViewById(R.id.nav_header), mEvent);
                    downloadImageTask.execute(mEvent.getBannerUrl());
                } else if(mEvent.getBanner() != null) {
                    header.findViewById(R.id.nav_header).setBackground(new BitmapDrawable(getResources(), mEvent.getBanner()));
                }
                ((TextView)header.findViewById(R.id.nav_header_title)).setText(mEvent.getName());
                ((TextView)header.findViewById(R.id.nav_header_detail)).setText(mEvent.getDisplayLocation());
                break;
        }
    }

    @Override
    public void onListFragmentInteraction(SEvent event) {
        mEvent = mEvents.get(mEvents.indexOf(event));
        onNavigationItemSelected(navigationView.getMenu().findItem(R.id.nav_detail));
    }

    @Override
    public void onListFragmentInteraction(SDay day) {
        replaceFragment(SessionFragment.newInstance((ArrayList<String>) day.getSessions(), day.getId(), mEvent.getId()));
    }

    @Override
    public void onListFragmentInteraction(SSession item) {
        replaceFragment(SpeakerFragment.newInstance((ArrayList<String>) item.getSpeakers(), item.getId(), mEvent.getId()));
    }

    public boolean needsUpdated() {
        return lastListPull == null || lastListPull.after(new Date(lastListPull.getTime() + TIME_OUT));
    }

    public void updateListPull(){
        lastListPull = new Date();
    }

    @Override
    public void navigateToId(int id) {
        onNavigationItemSelected(navigationView.getMenu().findItem(id));
    }

    @Override
    public void addEvent(SEvent event) {
        int i = mEvents.indexOf(event);
        if(i < 0)
            mEvents.add(event);
        else
            mEvents.set(i, event);
    }

    @Override
    public SEvent get(String id) {
        for(SEvent e : mEvents){
            if(e.getId().equals(id))
                return e;
        }

        throw new RuntimeException("Event not found for id=" + id);
    }

    @Override
    public List<SEvent> all() {
        return mEvents;
    }

    @Override
    public void clear() {
        mEvents.clear();
    }

    @Override
    public void sort() {
        Collections.sort(mEvents);
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        View view;
        SEvent event;

        public DownloadImageTask(View view, SEvent event) {
            this.view = view;
            this.event = event;
        }

        protected Bitmap doInBackground(String... urls) {
            String url = urls[0];
            Bitmap bitmap = null;
            try {
                InputStream in = new java.net.URL(url).openStream();
                bitmap = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return bitmap;
        }

        protected void onPostExecute(Bitmap result) {
            view.setBackground(new BitmapDrawable(getResources(), result));
            event.setBanner(result);
        }
    }
}
