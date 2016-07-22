package org.shingo.shingoeventsapp.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

import com.appsee.Appsee;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;
import org.shingo.shingoeventsapp.R;
import org.shingo.shingoeventsapp.middle.SEntity.SOrganization;
import org.shingo.shingoeventsapp.middle.SEvent.*;
import org.shingo.shingoeventsapp.middle.SObject;
import org.shingo.shingoeventsapp.ui.events.*;
import org.shingo.shingoeventsapp.ui.interfaces.*;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnListFragmentInteractionListener,
        NavigationInterface, EventInterface, OnErrorListener, CacheInterface, AffiliateInterface {

    private static final long TIME_OUT = TimeUnit.MINUTES.toMillis(30);
    private Date lastEventPull;
    private Date lastAffiliatePull;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private Fragment mFragment;
    private int mToggle = 0;
    private ArrayList<SEvent> mEvents = new ArrayList<>();
    private ArrayList<SOrganization> mAffiliates = new ArrayList<>();
    private SEvent mEvent;

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        Appsee.start(getString(R.string.com_appsee_apikey));
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
                lastEventPull = new Date(savedInstanceState.getLong("lastListPull"));
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
        if(lastEventPull != null)
            outState.putLong("lastListPull", lastEventPull.getTime());
        if(mEvent != null)
            outState.putParcelable("mEvent", mEvent);

        super.onSaveInstanceState(outState);

        if(getSupportFragmentManager().findFragmentById(mFragment.getId()) != null)
            getSupportFragmentManager().putFragment(outState, "mFragment", mFragment);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public void onBackPressed() {
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        assert drawer != null;
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if(Objects.equals(mFragment.getClass().getName(), HomeFragment.class.getName())){
            this.finishAffinity();
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
            lastAffiliatePull = null;
            lastEventPull = null;
            for(SEvent e : mEvents){
                e.refresh();
            }
            replaceFragment(mFragment);
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
            case R.id.nav_venues:
                replaceFragment(VenueFragment.newInstance(eventId));
                break;
            case R.id.nav_events:
                replaceFragment(EventFragment.newInstance());
                break;
            case R.id.nav_affiliates:
                replaceFragment(AffiliateFragment.newInstance());
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
        final boolean disconnected = !isNetworkAvailable();
        if(disconnected)
            error = "This part of the application requires access to the internet!\n\nPlease turn on your wi-fi or mobile data.";
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(error)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        navigateToId(R.id.nav_home);
                    }
                }).setTitle("Error")
                .setIcon(R.drawable.ic_error);
        builder.create().show();
    }

    private boolean isNetworkAvailable(){
        NetworkInfo info = ((ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        return info != null && info.isConnected();
    }

    private void replaceFragment(Fragment fragment){
        mFragment = fragment;
        if(getSupportFragmentManager().findFragmentByTag(fragment.getClass().getName()) != null)
            getSupportFragmentManager().popBackStackImmediate(fragment.getClass().getName(),FragmentManager.POP_BACK_STACK_INCLUSIVE);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_content, fragment, fragment.getClass().getName())
                .addToBackStack(fragment.getClass().getName())
                .commit();
    }

    @SuppressWarnings("deprecation")
    public void toggleNavHeader(int type){
        mToggle = type;
        if(navigationView == null) return;
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
    public void navigateToId(int id) {
        onNavigationItemSelected(navigationView.getMenu().findItem(id));
    }

    @Override
    public void changeFragment(Fragment fragment){
        replaceFragment(fragment);
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
    public void addAffiliate(SOrganization event) {
        mAffiliates.add(event);
    }

    @Override
    public SEvent getEvent(String id) {
        for(SEvent e : mEvents){
            if(e.getId().equals(id))
                return e;
        }

        throw new RuntimeException("Event not found for id=" + id);
    }

    @Override
    public SOrganization getAffiliate(String id){
        for(SOrganization o : mAffiliates){
            if(o.getId().equals(id))
                return o;
        }

        throw new RuntimeException("Affiliate not found for id=" + id);
    }

    @Override
    public List<SOrganization> affiliates() {
        return mAffiliates;
    }

    @Override
    public void clearAffiliates() {
        mAffiliates.clear();
    }

    @Override
    public void sortAffiliates() {
        Collections.sort(mAffiliates);
    }

    @Override
    public List<SEvent> events() {
        return mEvents;
    }

    @Override
    public void clearEvents() {
        mEvents.clear();
    }

    @Override
    public void sortEvents() {
        Collections.sort(mEvents);
    }

    @Override
    public boolean needsUpdated(CacheType type) {
        switch (type){
            case Events:
                return lastEventPull == null || lastEventPull.after(new Date(lastEventPull.getTime() + TIME_OUT));
            case Affiliates:
                return lastAffiliatePull == null || lastAffiliatePull.after(new Date(lastAffiliatePull.getTime() + TIME_OUT));
            default:
                return false;
        }
    }

    @Override
    public void updateTime(CacheType type) {
        switch (type){
            case Events:
                lastEventPull = new Date();
                break;
            case Affiliates:
                lastAffiliatePull = new Date();
                break;
        }
    }

    @Override
    public void onListFragmentInteraction(SObject item) {
        if(item instanceof SVenue){
            replaceFragment(VenueDetailFragment.newInstance(item.getId(), mEvent.getId()));
        } else if(item instanceof SEvent){
            mEvent = mEvents.get(mEvents.indexOf(item));
            onNavigationItemSelected(navigationView.getMenu().findItem(R.id.nav_detail));
        } else if(item instanceof SDay){
            SDay day = (SDay) item;
            replaceFragment(SessionFragment.newInstance((ArrayList<String>) day.getSessions(), day.getId(), mEvent.getId()));
        } else if(item instanceof SSession){
            SSession session = (SSession) item;
            replaceFragment(SpeakerFragment.newInstance((ArrayList<String>) session.getSpeakers(), session.getId(), mEvent.getId()));
        }
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
