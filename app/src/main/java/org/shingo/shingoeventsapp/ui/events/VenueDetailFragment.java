package org.shingo.shingoeventsapp.ui.events;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.shingo.shingoeventsapp.R;
import org.shingo.shingoeventsapp.data.GetAsyncData;
import org.shingo.shingoeventsapp.data.OnTaskCompleteListener;
import org.shingo.shingoeventsapp.middle.SEvent.SRoom;
import org.shingo.shingoeventsapp.middle.SEvent.SVenue;
import org.shingo.shingoeventsapp.middle.SectionedDataModel;
import org.shingo.shingoeventsapp.ui.events.viewadapters.MySEventObjectRecyclerView;
import org.shingo.shingoeventsapp.ui.events.viewadapters.MySectionedSEventObjectRecyclerView;
import org.shingo.shingoeventsapp.ui.interfaces.EventInterface;
import org.shingo.shingoeventsapp.ui.interfaces.NavigationInterface;
import org.shingo.shingoeventsapp.ui.interfaces.OnErrorListener;
import org.shingo.shingoeventsapp.ui.interfaces.OnListFragmentInteractionListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
    private List<SectionedDataModel> rooms = new ArrayList<>();

    private OnListFragmentInteractionListener mListener;
    private OnErrorListener mErrorListener;
    private EventInterface mEvents;
    private NavigationInterface mNavigation;

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
        if (context instanceof NavigationInterface)
            mNavigation = (NavigationInterface) context;
        else
            throw new RuntimeException(context.toString() + " must implement NavigationInterface");

        if (context instanceof EventInterface)
            mEvents = (EventInterface) context;
        else
            throw new RuntimeException(context.toString() + " must implement EventInterface");

        if (context instanceof OnErrorListener)
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
        mErrorListener = null;
        mEvents = null;
    }

    private void updateViews(View view){
        if(view == null) return;
        ((TextView) view.findViewById(R.id.venue_name)).setText(mVenue.getName());
        ((TextView) view.findViewById(R.id.venue_address)).setText(mVenue.getAddress().replaceAll("\\n"," ").replaceAll("\\s\\s", " "));
        view.findViewById(R.id.venue_maps).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mNavigation.changeFragment(MapListFragment.newInstance(mVenue));
            }
        });
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.room_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        sectionRooms();
        recyclerView.setAdapter(new MySectionedSEventObjectRecyclerView(rooms, mListener));
    }

    private void sectionRooms(){
        Collections.sort(mVenue.getRooms());
        int maxFloor = 0;
        int minFloor = 0;
        for(SRoom r : mVenue.getRooms()){
            if(r.getFloor() > maxFloor)
                maxFloor = r.getFloor();
            if(r.getFloor() < minFloor)
                minFloor = r.getFloor();
        }

        sortRoomsByFloor(minFloor, maxFloor);
    }

    private void sortRoomsByFloor(int minFloor, int maxFloor){
        rooms.clear();
        for(int i = minFloor; i <= maxFloor; i++){
            String floorName = i < 1 ? (i == 0 ? "Ground Floor" : "B" + String.valueOf(Math.abs(i))) : String.valueOf(i);
            List<SRoom> floor = new ArrayList<>();
            for(SRoom r : mVenue.getRooms()){
                if(r.getFloor() == i)
                    floor.add(r);
            }
            rooms.add(new SectionedDataModel(floorName, floor));
        }

    }

    @Override
    public void onTaskComplete(String response) {
        try {
            JSONObject result = new JSONObject(response);
            if(result.getBoolean("success")){
                if(result.has("venue")){
                    mEvents.getEvent(mEventId).updatePullTime(CACHE_KEY + mVenueId);
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