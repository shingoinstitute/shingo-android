package org.shingo.shingoeventsapp.ui.events;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.shingo.shingoeventsapp.R;
import org.shingo.shingoeventsapp.data.GetAsyncData;
import org.shingo.shingoeventsapp.data.OnTaskCompleteListener;
import org.shingo.shingoeventsapp.middle.SEvent.SEvent;
import org.shingo.shingoeventsapp.middle.SEvent.SVenue;
import org.shingo.shingoeventsapp.ui.MainActivity;
import org.shingo.shingoeventsapp.ui.interfaces.NavigationInterface;
import org.shingo.shingoeventsapp.ui.interfaces.OnErrorListener;
import org.shingo.shingoeventsapp.ui.interfaces.EventInterface;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * A simple {@link Fragment} subclass.
 * </p>
 * Use the {@link EventDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EventDetailFragment extends Fragment implements OnTaskCompleteListener {

    private static final String ARG_ID = "event_id";
    private static final String CACHE_KEY = "event";
    private String mEventId;

    private NavigationInterface mNavigate;
    private EventInterface mEvents;
    private OnErrorListener mErrorListener;

    private ProgressDialog progress;
    private SEvent mEvent;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public EventDetailFragment() {}

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param event_id Salesforce Id of event
     * @return A new instance of fragment EventDetailFragment.
     */
    public static EventDetailFragment newInstance(String event_id) {
        EventDetailFragment fragment = new EventDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ID, event_id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            mEventId = getArguments().getString(ARG_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_event_detail, container, false);
        MainActivity mainActivity = (MainActivity) getActivity();

        mEvent = mEvents.getEvent(mEventId);

        if(mEvent == null){
            mNavigate.navigateToId(R.id.nav_events);
            return null;
        }

        if(mEvent.needsUpdated(CACHE_KEY + mEventId)) {
            GetAsyncData getEventAsync = new GetAsyncData(this);
            getEventAsync.execute("/salesforce/events/" + mEventId);
            progress = ProgressDialog.show(getContext(), "", "Loading Event...");
        } else {
            updateViews(view);
        }

        setOnClickListeners(view);

        mainActivity.setTitle(mEvent.getName());
        mainActivity.toggleNavHeader(1);
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

    private void setOnClickListeners(View view){
        view.findViewById(R.id.event_agenda).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNavigate.navigateToId(R.id.nav_agenda);
            }
        });
        view.findViewById(R.id.event_sessions).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNavigate.navigateToId(R.id.nav_sessions);
            }
        });
        view.findViewById(R.id.event_speakers).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNavigate.navigateToId(R.id.nav_speakers);
            }
        });
        view.findViewById(R.id.event_exhibitors).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mNavigate.navigateToId(R.id.nav_exhibitors);
            }
        });
        view.findViewById(R.id.event_recipients).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mNavigate.navigateToId(R.id.nav_recipients);
            }
        });
        view.findViewById(R.id.event_sponsors).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mNavigate.navigateToId(R.id.nav_sponsors);
            }
        });
        view.findViewById(R.id.event_venues).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mNavigate.navigateToId(R.id.nav_venues);
            }
        });
        view.findViewById(R.id.event_attendees).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mNavigate.navigateToId(R.id.nav_attendees);
            }
        });
    }

    @SuppressWarnings("deprecation")
    private String formatEventDates(Date start, Date end){
        int deltaYear = start.getYear() - end.getYear();
        int deltaMonth = start.getMonth() - end.getMonth();
        int deltaDay = start.getDate() - end.getDate();
        SimpleDateFormat formatter = new SimpleDateFormat("EEEE dd MMM yyyy", Locale.getDefault());
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        if(deltaYear == 0){
            if(deltaMonth == 0){
                if(deltaDay == 0){
                    return formatter.format(start);
                } else {
                    String e = formatter.format(end);
                    formatter.applyPattern("EEEE dd");
                    String s = formatter.format(start);
                    return s + " - " + e;
                }
            } else {
                String e = formatter.format(end);
                formatter.applyPattern("EEEE dd MMM");
                String s = formatter.format(start);
                return s + " - " + e;
            }
        } else {
            return formatter.format(start) + " - " + formatter.format(end);
        }
    }

    @SuppressWarnings("deprecation")
    private void updateViews(View view){
        if(view == null) return;
        ((TextView)view.findViewById(R.id.event_name)).setText(mEvent.getName());
        ((TextView)view.findViewById(R.id.event_location)).setText(mEvent.getDisplayLocation());
        ((TextView)view.findViewById(R.id.event_dates)).setText(formatEventDates(mEvent.getStart(), mEvent.getEnd()));
        ((TextView)view.findViewById(R.id.event_registration)).setText(String.format("Register: %s", mEvent.getRegistration()));
        ((TextView)view.findViewById(R.id.event_sales_text)).setText(Html.fromHtml(mEvent.getSalesText()));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnErrorListener)
            mErrorListener = (OnErrorListener) context;
        else
            throw new RuntimeException(context.toString() + " must implement EventInterface");

        if (context instanceof EventInterface)
            mEvents = (EventInterface) context;
        else
            throw new RuntimeException(context.toString() + " must implement EventInterface");

        if (context instanceof NavigationInterface)
            mNavigate = (NavigationInterface) context;
        else
            throw new RuntimeException(context.toString() + " must implement NavigationInterface");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mErrorListener = null;
        mEvents = null;
        mNavigate = null;
    }

    @Override
    public void onTaskComplete(String response) {
        try {
            JSONObject result = new JSONObject(response);
            if(result.getBoolean("success")){
                if(result.has("event")){
                    mEvent.updatePullTime(CACHE_KEY + mEventId);
                    mEvent.fromJSON(result.getJSONObject("event").toString());
                    GetAsyncData data = new GetAsyncData(new OnTaskCompleteListener() {
                        @Override
                        public void onTaskComplete(String response) {
                            try {
                                JSONObject result = new JSONObject(response);
                                Log.d("EVENT VENUE CALL", response);
                                if (result.getBoolean("success")) {
                                    mEvent.getVenues().clear();
                                    for (int i = 0; i < result.getJSONArray("venues").length(); i++) {
                                        JSONObject jsonVenue = result.getJSONArray("venues").getJSONObject(i);
                                        SVenue mVenue = new SVenue();
                                        mVenue.fromJSON(jsonVenue.toString());
                                        mEvent.getVenues().add(mVenue);
                                        mEvent.updatePullTime("venue" + mVenue.getId());
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onTaskError(String error) {

                        }
                    });
                    data.execute("/salesforce/events/venues?event_id=" + mEvent.getId());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        updateViews(getView());
        progress.dismiss();
    }

    @Override
    public void onTaskError(String error) {
        if(mErrorListener != null)
            mErrorListener.handleError(error);
        progress.dismiss();
    }
}
