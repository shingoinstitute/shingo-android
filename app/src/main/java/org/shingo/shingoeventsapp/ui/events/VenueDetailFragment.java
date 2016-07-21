package org.shingo.shingoeventsapp.ui.events;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONException;
import org.json.JSONObject;
import org.shingo.shingoeventsapp.R;
import org.shingo.shingoeventsapp.data.GetAsyncData;
import org.shingo.shingoeventsapp.data.OnTaskCompleteListener;
import org.shingo.shingoeventsapp.middle.SEvent.SVenue;
import org.shingo.shingoeventsapp.ui.interfaces.EventInterface;
import org.shingo.shingoeventsapp.ui.interfaces.OnErrorListener;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link VenueDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VenueDetailFragment extends Fragment implements OnTaskCompleteListener {

    private static final String ARG_VENUE_ID = "venue_id";
    private static final String ARG_EVENT_ID = "event_id";
    private static final String CACHE_KEY = "venue";

    private String mVenueId;
    private String mEventId;
    private SVenue mVenue;

    private OnErrorListener mErrorListener;
    private EventInterface mEvents;

    private ProgressDialog progress;


    public VenueDetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param venueId Salesforce Id
     * @return A new instance of fragment VenueDetailFragment.
     */
    public static VenueDetailFragment newInstance(String venueId, String eventId) {
        VenueDetailFragment fragment = new VenueDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_VENUE_ID, venueId);
        args.putString(ARG_EVENT_ID, eventId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mVenueId = getArguments().getString(ARG_VENUE_ID);
            mEventId = getArguments().getString(ARG_EVENT_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_venue_detail, container, false);
        int i = mEvents.getEvent(mEventId).getVenues().indexOf(new SVenue("{Id:\"" + mVenueId + "\"}"));
        mVenue = mEvents.getEvent(mEventId).getVenues().get(i);
        if(mEvents.getEvent(mEventId).needsUpdated(CACHE_KEY + mVenueId)){
            GetAsyncData getVenueAsync = new GetAsyncData(this);
            getVenueAsync.execute("/salesforce/events/venues/" + mVenueId);

            progress = ProgressDialog.show(getContext(), "", "Loading Venue...");
        }
        updateViews(view);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof EventInterface)
            mEvents = (EventInterface) context;
        else
            throw new RuntimeException(context.toString() + " must implement EventInterface");

        if (context instanceof OnErrorListener)
            mErrorListener = (OnErrorListener) context;
        else
            throw new RuntimeException(context.toString() + " must implement OnErrorListener");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mErrorListener = null;
        mEvents = null;
    }

    private void updateViews(View view){

    }

    @Override
    public void onTaskComplete(String response) {
        try {
            JSONObject result = new JSONObject(response);
            if(result.getBoolean("success")){
                if(result.has("venue")){
                    mVenue.fromJSON(result.getJSONObject("venue").toString());
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        updateViews(getView());
        progress.dismiss();
    }

    @Override
    public void onTaskError(String error) {
        mErrorListener.handleError(error);
    }
}
