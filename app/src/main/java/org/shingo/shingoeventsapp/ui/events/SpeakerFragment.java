package org.shingo.shingoeventsapp.ui.events;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.shingo.shingoeventsapp.R;
import org.shingo.shingoeventsapp.data.GetAsyncData;
import org.shingo.shingoeventsapp.data.OnTaskCompleteListener;
import org.shingo.shingoeventsapp.middle.SEntity.SPerson;
import org.shingo.shingoeventsapp.middle.SectionedDataModel;
import org.shingo.shingoeventsapp.middle.SEvent.SEvent;
import org.shingo.shingoeventsapp.ui.events.viewadapters.MySEntityRecyclerViewAdapter;
import org.shingo.shingoeventsapp.ui.events.viewadapters.MySectionedSEntityRecyclerViewAdapter;
import org.shingo.shingoeventsapp.ui.interfaces.OnErrorListener;
import org.shingo.shingoeventsapp.ui.interfaces.EventInterface;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A fragment representing a list of {@link SPerson}s (Speakers).
 */
public class SpeakerFragment extends Fragment implements OnTaskCompleteListener {

    private static final String ARG_SPEAKER_IDS = "speaker_ids";
    private static final String ARG_EVENT_ID = "event_id";
    private static final String ARG_SESSION_ID = "session_id";
    private static final String CACHE_KEY = "speakers";
    private ArrayList<String> mSpeakerIds;
    private String mEventId;
    private String mSessionId;
    private boolean isSectioned = true;
    private List<SectionedDataModel> data = new ArrayList<>();

    private OnErrorListener mErrorListener;
    private EventInterface mEvents;

    private RecyclerView.Adapter mAdapter;
    private ProgressDialog progress;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public SpeakerFragment() {
    }

    public static SpeakerFragment newInstance(String eventId){
        SpeakerFragment fragment = new SpeakerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_EVENT_ID, eventId);
        fragment.setArguments(args);
        return fragment;
    }

    public static SpeakerFragment newInstance(ArrayList<String> ids, String sessionId, String eventId) {
        SpeakerFragment fragment = new SpeakerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_SESSION_ID, sessionId);
        args.putString(ARG_EVENT_ID, eventId);
        args.putStringArrayList(ARG_SPEAKER_IDS, ids);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            if(getArguments().containsKey(ARG_SPEAKER_IDS))
                mSpeakerIds = getArguments().getStringArrayList(ARG_SPEAKER_IDS);
            if(getArguments().containsKey(ARG_SESSION_ID))
                mSessionId = getArguments().getString(ARG_SESSION_ID);
            mEventId = getArguments().getString(ARG_EVENT_ID);
            isSectioned = mSessionId == null;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle("Speakers");

        View view = inflater.inflate(R.layout.fragment_sentity_list, container, false);

        SEvent event = mEvents.getEvent(mEventId);
        if(mSpeakerIds != null && event.hasCache(CACHE_KEY)){
            mAdapter = new MySEntityRecyclerViewAdapter(event.getSpeakers(mSpeakerIds));
        } else if(event.needsUpdated(CACHE_KEY)){
            GetAsyncData getSpeakersAsync = new GetAsyncData(this);
            getSpeakersAsync.execute("/salesforce/events/speakers", mSessionId == null ? ARG_EVENT_ID + "=" +mEventId : ARG_SESSION_ID + "=" + mSessionId);
            mAdapter = isSectioned ? new MySectionedSEntityRecyclerViewAdapter(data) : new MySEntityRecyclerViewAdapter(event.getSpeakers());

            progress = ProgressDialog.show(getContext(), "", "Loading Speakers...");
        } else {
            sectionSpeakers(event.getSpeakers());
            mAdapter = new MySectionedSEntityRecyclerViewAdapter(data);
        }

        Context context = view.getContext();
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(mAdapter);

        if(mAdapter.getItemCount() == 0)
            (view.findViewById(R.id.empty_entity)).setVisibility(View.VISIBLE);
        else
            (view.findViewById(R.id.empty_entity)).setVisibility(View.GONE);

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

    @Override
    public void onTaskComplete(String response) {
        try {
            JSONObject result = new JSONObject(response);
            if(result.getBoolean("success")){
                mEvents.getEvent(mEventId).getSpeakers().clear();
                if(mSessionId == null)
                    mEvents.getEvent(mEventId).updatePullTime(CACHE_KEY);
                JSONArray jSpeakers = result.getJSONArray("speakers");
                for(int i = 0; i < jSpeakers.length(); i++){
                    SPerson speaker = new SPerson();
                    speaker.fromJSON(jSpeakers.getJSONObject(i).toString());
                    mEvents.getEvent(mEventId).getSpeakers().add(speaker);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Collections.sort(mEvents.getEvent(mEventId).getSpeakers());

        if(isSectioned)
            sectionSpeakers(mEvents.getEvent(mEventId).getSpeakers());

        mAdapter.notifyDataSetChanged();
        progress.dismiss();
    }

    @Override
    public void onTaskError(String error) {
        mErrorListener.handleError(error);
        progress.dismiss();
    }

    private void sectionSpeakers(List<SPerson> speakers){
        data.clear();
        for(SPerson.SPersonType type : SPerson.SPersonType.values()){
            data.add(groupSpeakersByType(type, speakers));
        }
    }

    private SectionedDataModel groupSpeakersByType(SPerson.SPersonType type, List<SPerson> speakers){
        List<SPerson> group = new ArrayList<>();
        for(SPerson p : speakers){
            if(p.type == type)
                group.add(p);
            else if(type == SPerson.SPersonType.ConcurrentSpeaker && p.type == null)
                group.add(p);
        }

        return new SectionedDataModel(type.toString(), group);
    }
}
