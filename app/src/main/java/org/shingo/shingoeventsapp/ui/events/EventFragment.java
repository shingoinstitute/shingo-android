package org.shingo.shingoeventsapp.ui.events;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONObject;
import org.shingo.shingoeventsapp.R;
import org.shingo.shingoeventsapp.data.GetAsyncData;
import org.shingo.shingoeventsapp.data.OnTaskCompleteListener;
import org.shingo.shingoeventsapp.middle.SEntity.SEntity;
import org.shingo.shingoeventsapp.middle.SEvent.SEvent;
import org.shingo.shingoeventsapp.middle.SObject;
import org.shingo.shingoeventsapp.ui.MainActivity;
import org.shingo.shingoeventsapp.ui.events.viewadapters.MySEventObjectRecyclerView;
import org.shingo.shingoeventsapp.ui.interfaces.CacheInterface;
import org.shingo.shingoeventsapp.ui.interfaces.OnErrorListener;
import org.shingo.shingoeventsapp.ui.interfaces.EventInterface;
import org.shingo.shingoeventsapp.ui.interfaces.OnListFragmentInteractionListener;

import java.io.InputStream;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class EventFragment extends Fragment implements OnTaskCompleteListener {

    private OnListFragmentInteractionListener mListener;
    private OnErrorListener mErrorListener;
    private EventInterface mEvents;
    private CacheInterface mCache;
    private RecyclerView.Adapter mAdapter;
    private ProgressBar progress;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public EventFragment() {
    }

    public static EventFragment newInstance() {
        return new EventFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle("Upcoming Events");
        ((MainActivity) getActivity()).toggleNavHeader(0);

        View view = inflater.inflate(R.layout.fragment_seventobject_list, container, false);

        if(mCache.needsUpdated(CacheInterface.CacheType.Events)) {
            GetAsyncData getEventsAsync = new GetAsyncData(this);
            getEventsAsync.execute("/salesforce/events?publish_to_web=true");
            progress = (ProgressBar) view.findViewById(R.id.progressBar);
            progress.setVisibility(View.VISIBLE);
        } else {
            view.findViewById(R.id.progressBar).setVisibility(View.GONE);
        }

        Context context = view.getContext();
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        mAdapter = new MySEventObjectRecyclerView(mEvents.events(), mListener);
        recyclerView.setAdapter(mAdapter);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Do something that differs the Activity's menu here
        super.onCreateOptionsMenu(menu, inflater);
        menu.findItem(R.id.action_settings).setVisible(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return getActivity().onOptionsItemSelected(item);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof CacheInterface)
            mCache = (CacheInterface) context;
        else
            throw new RuntimeException(context.toString() + " must implement CacheInterface");

        if(context instanceof EventInterface)
            mEvents = (EventInterface) context;
        else
            throw new RuntimeException(context.toString() + " must implement EventInterface");

        if(context instanceof OnErrorListener)
            mErrorListener = (OnErrorListener) context;
        else
            throw new RuntimeException(context.toString() + " must implement OnErrorListener");

        if (context instanceof OnListFragmentInteractionListener)
            mListener = (OnListFragmentInteractionListener) context;
        else
            throw new RuntimeException(context.toString() + " must implement OnListFragmentInteractionListener");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        mErrorListener =null;
        mEvents = null;
    }

    @Override
    public void onTaskComplete(String response) {
        try {
            JSONObject result = new JSONObject(response);
            if(result.getBoolean("success")){
                JSONArray jsonEvents = result.getJSONArray("events");
                mEvents.clearEvents();
                mCache.updateTime(CacheInterface.CacheType.Events);
                Integer count = 0;
                for(int i = 0; i < jsonEvents.length(); i++){
                    count += 1;
                    SEvent event = new SEvent();
                    event.fromJSON(jsonEvents.getJSONObject(i).toString());
                    mEvents.addEvent(event);
                    final String id = event.getId();
                    GetAsyncData ads = new GetAsyncData(new OnTaskCompleteListener() {
                        @Override
                        public void onTaskComplete(String response) {
                            try {
                                JSONObject result = new JSONObject(response);
                                Log.d("EVENT ADS CALL", response);
                                if (result.getBoolean("success")) {
                                    JSONArray array =result.getJSONArray("sponsors");
                                    for(int i = 0; i < result.getInt("total_size"); i++){
                                        JSONObject sponsor = array.getJSONObject(i);
                                        if(!sponsor.isNull("Sponsor_Ads__r")){
                                            JSONArray ads = sponsor.getJSONObject("Sponsor_Ads__r").getJSONArray("records");
                                            for(int j = 0; j < ads.length(); j++){
                                                JSONObject ad = ads.getJSONObject(j);
                                                int type = ad.getString("Ad_Type__c").equals("Splash Screen Ad") ? 0 : 1;
                                                DownloadImageTask imageTask = new DownloadImageTask(id, type);
                                                imageTask.execute(ad.getString("Image_URL__c"));
                                            }
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onTaskError(String error) {
                            if(mErrorListener != null)
                                mErrorListener.handleError(error);
                            progress.setVisibility(View.GONE);
                        }

                        class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

                            private String id;
                            private int type;
                            public DownloadImageTask(String id, int type) {
                                this.id = id;
                                this.type = type;
                            }

                            protected Bitmap doInBackground(String... urls) {
                                String url = urls[0];
                                Bitmap bitmap = null;
                                try {
                                    Log.d("SPONSOR_ADS", "Getting ad from url: " + url);
                                    InputStream in = new java.net.URL(url).openStream();
                                    bitmap = BitmapFactory.decodeStream(in);
                                } catch (Exception e) {
                                    Log.e("Error", "Exception Message: " + e.getLocalizedMessage());
                                    e.printStackTrace();
                                }
                                return bitmap;
                            }

                            protected void onPostExecute(Bitmap bitmap) {
                                Log.d("SPONSOR_ADS", "Setting sponsor ad");
                                Log.d("SPONSOR_ADS", "Bitmap is " + (bitmap == null ? "null" : "not null"));
                                mEvents.addAd(id, type, bitmap);
                                mEvents.sortEvents();
                                mAdapter.notifyDataSetChanged();
                                if(mEvents.events().size() == 0 && getView() != null)
                                    getView().findViewById(R.id.empty_entity).setVisibility(View.VISIBLE);

                                progress.setVisibility(View.GONE);
                            }
                        }
                    });

                    ads.execute("/salesforce/events/sponsors?force_refresh=true&event_id=" + id);
                }
            }

            mEvents.sortEvents();
            mAdapter.notifyDataSetChanged();
            if(mEvents.events().size() == 0 && getView() != null)
                getView().findViewById(R.id.empty_entity).setVisibility(View.VISIBLE);

            progress.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onTaskError(String error) {
        if(mErrorListener != null)
            mErrorListener.handleError(error);
        progress.setVisibility(View.GONE);
    }
}
