package org.shingo.shingoapp.ui.events;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.shingo.shingoapp.R;
import org.shingo.shingoapp.data.GetAsyncData;
import org.shingo.shingoapp.data.OnTaskComplete;
import org.shingo.shingoapp.middle.SEvent.SEvent;
import org.shingo.shingoapp.ui.MainActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnEventFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link EventDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EventDetailFragment extends Fragment implements OnTaskComplete {

    private static final String ARG_ID = "event_id";
    private String mEventId;
    private OnEventFragmentInteractionListener mListener;
    private ProgressDialog progress;
    private MainActivity mainActivity;
    private View view;

    public EventDetailFragment() {
        // Required empty public constructor
    }

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
        if (getArguments() != null) {
            mEventId = getArguments().getString(ARG_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_event_detail, container, false);
        mainActivity = (MainActivity) getActivity();
        if(mainActivity.mEvents.size() == 0){
            mainActivity.onNavigationItemSelected(mainActivity.navigationView.getMenu().findItem(R.id.nav_events));
            return null;
        }
        GetAsyncData getEventAsync = new GetAsyncData(this);
        getEventAsync.execute("/salesforce/events/" + mEventId);
        progress = ProgressDialog.show(getContext(), "", "Loading Event...");

        (view.findViewById(R.id.event_agenda)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.onNavigationItemSelected(mainActivity.navigationView.getMenu().findItem(R.id.nav_agenda));
            }
        });
        (view.findViewById(R.id.event_sessions)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.onNavigationItemSelected(mainActivity.navigationView.getMenu().findItem(R.id.nav_sessions));
            }
        });
        (view.findViewById(R.id.event_speakers)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.onNavigationItemSelected(mainActivity.navigationView.getMenu().findItem(R.id.nav_speakers));
            }
        });
        (view.findViewById(R.id.event_exhibitors)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivity.onNavigationItemSelected(mainActivity.navigationView.getMenu().findItem(R.id.nav_exhibitors));
            }
        });

        mainActivity.setTitle(mainActivity.mEvents.get(mainActivity.mEventIndex).getName());
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnEventFragmentInteractionListener) {
            mListener = (OnEventFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnEventFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onTaskComplete() {
        throw new UnsupportedOperationException("Not implemented...");
    }

    @Override
    public void onTaskComplete(String response) {
        try {
            JSONObject result = new JSONObject(response);
            if(result.getBoolean("success")){
                if(result.has("event")){
                    mainActivity.mEvents.get(mainActivity.mEventIndex).fromJSON(result.getJSONObject("event").toString());
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        SEvent event = mainActivity.mEvents.get(mainActivity.mEventIndex);
        ((TextView)view.findViewById(R.id.event_name)).setText(event.getName());
        ((TextView)view.findViewById(R.id.event_location)).setText(event.getDisplayLocation());
        ((TextView)view.findViewById(R.id.event_dates)).setText(formatEventDates(event.getStart(), event.getEnd()));
        ((TextView)view.findViewById(R.id.event_registration)).setText("Register: " + event.getRegistration());
        ((TextView)view.findViewById(R.id.event_sales_text)).setText(Html.fromHtml(event.getSalesText()));
        progress.dismiss();
    }

    public String formatEventDates(Date start, Date end){
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

    @Override
    public void onTaskError(String error) {
        // TODO: Handle error
        progress.dismiss();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnEventFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
