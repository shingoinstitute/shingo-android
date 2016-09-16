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
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.shingo.shingoeventsapp.R;
import org.shingo.shingoeventsapp.data.GetAsyncData;
import org.shingo.shingoeventsapp.data.OnTaskCompleteListener;
import org.shingo.shingoeventsapp.middle.SEvent.SEvent;
import org.shingo.shingoeventsapp.middle.SEvent.SSession;
import org.shingo.shingoeventsapp.middle.SectionedDataModel;
import org.shingo.shingoeventsapp.ui.interfaces.OnErrorListener;
import org.shingo.shingoeventsapp.ui.events.viewadapters.MySessionRecyclerViewAdapter;
import org.shingo.shingoeventsapp.ui.events.viewadapters.MySectionedSessionRecyclerViewAdapter;
import org.shingo.shingoeventsapp.ui.interfaces.EventInterface;
import org.shingo.shingoeventsapp.ui.interfaces.OnListFragmentInteractionListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * A fragment representing a list of {@link SSession}s.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class SessionFragment extends Fragment implements OnTaskCompleteListener {

    private static final String ARG_SESSION_IDS = "session_ids";
    private static final String ARG_EVENT_ID = "event_id";
    private static final String ARG_AGENDA_ID = "agenda_id";
    private static final String CACHE_KEY = "sessions";
    private boolean isSectioned = true;
    private ArrayList<String> mSessionIds;
    private String mEventId;
    private String mAgendaId;
    private List<SectionedDataModel> data = new ArrayList<>();

    private OnListFragmentInteractionListener mListener;
    private OnErrorListener mErrorListener;
    private EventInterface mEvents;

    private RecyclerView.Adapter mAdapter;
    private ProgressBar progress;
    private ProgressDialog progressDialog;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public SessionFragment() {
    }

    public static SessionFragment newInstance(String eventId){
        SessionFragment fragment = new SessionFragment();
        Bundle args = new Bundle();
        args.putString(ARG_EVENT_ID, eventId);
        fragment.setArguments(args);
        return fragment;
    }

    public static SessionFragment newInstance(ArrayList<String> ids, String agendaId, String eventId) {
        SessionFragment fragment = new SessionFragment();
        Bundle args = new Bundle();
        args.putString(ARG_AGENDA_ID, agendaId);
        args.putString(ARG_EVENT_ID, eventId);
        args.putStringArrayList(ARG_SESSION_IDS, ids);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            if(getArguments().containsKey(ARG_SESSION_IDS))
                mSessionIds = getArguments().getStringArrayList(ARG_SESSION_IDS);
            if(getArguments().containsKey(ARG_AGENDA_ID))
                mAgendaId = getArguments().getString(ARG_AGENDA_ID);
            mEventId = getArguments().getString(ARG_EVENT_ID);
            isSectioned = !getArguments().containsKey(ARG_SESSION_IDS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_seventobject_list, container, false);
        getActivity().setTitle("Sessions");

        SEvent event = mEvents.getEvent(mEventId);

        if(!isSectioned && event.hasCache(CACHE_KEY)) {
            getActivity().setTitle(getTitle(event.getSessions(mSessionIds).get(0)));
            mAdapter = new MySessionRecyclerViewAdapter(event.getSessions(mSessionIds), mListener);
        } else if(event.needsUpdated(CACHE_KEY)) {
            GetAsyncData getSessionsAsync = new GetAsyncData(this);
            getSessionsAsync.execute("/salesforce/events/sessions/", (mAgendaId == null ? ARG_EVENT_ID + "=" + mEventId : ARG_AGENDA_ID + "=" + mAgendaId));
            mAdapter = isSectioned ? new MySectionedSessionRecyclerViewAdapter(data, mListener) : new MySessionRecyclerViewAdapter(event.getSessions(), mListener);
            if(view.findViewById(R.id.progressBar) != null){
                progress = (ProgressBar) view.findViewById(R.id.progressBar);
                progress.setVisibility(View.VISIBLE);
            } else {
                progressDialog = ProgressDialog.show(getContext(), "", "Loading Sessions...");
            }
        } else {
            sectionSessions(event.getSessions());
            mAdapter = new MySectionedSessionRecyclerViewAdapter(data, mListener);
        }

        Context context = view.getContext();
        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        mRecyclerView.setAdapter(mAdapter);

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
        mErrorListener = null;
        mEvents = null;
    }

    @Override
    public void onTaskComplete(String response) {
        try {
            JSONObject result = new JSONObject(response);
            if (result.getBoolean("success")) {
                mEvents.getEvent(mEventId).getSessions().clear();
                if(mAgendaId == null)
                    mEvents.getEvent(mEventId).updatePullTime(CACHE_KEY);
                if(result.has("sessions")) {
                    JSONArray jSessions = result.getJSONArray("sessions");
                    for (int i = 0; i < jSessions.length(); i++) {
                        SSession session = new SSession();
                        session.fromJSON(jSessions.getJSONObject(i).toString());
                        mEvents.getEvent(mEventId).getSessions().add(session);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Collections.sort(mEvents.getEvent(mEventId).getSessions());

        if(isSectioned)
            sectionSessions(mEvents.getEvent(mEventId).getSessions());
        else
            getActivity().setTitle(getTitle(mEvents.getEvent(mEventId).getSessions().get(0)));

        mAdapter.notifyDataSetChanged();
        if(progressDialog != null)
            progressDialog.dismiss();
        else
            progress.setVisibility(View.GONE);
    }

    @Override
    public void onTaskError(String error) {
        if(mErrorListener != null)
            mErrorListener.handleError(error);
        if(progressDialog != null)
            progressDialog.dismiss();
        else
            progress.setVisibility(View.GONE);
    }

    private void sectionSessions(List<SSession> sessions){
        data.clear();
        for (int i = 0; i < sessions.size(); i++) {
            data.add(groupSessionsByDay(i, sessions));
            i += data.get(data.size() - 1).getItems().size() - 1;
        }
    }

    /**
     * Group {@link SSession}s by day of the week
     * @param start start index
     * @param sessions Sorted {@link List<SSession>}
     * @return {@link SectionedDataModel}
     */
    private SectionedDataModel groupSessionsByDay(int start, List<SSession> sessions){
        List<SSession> group = new ArrayList<>();
        String day = getDayFromDate(sessions.get(start).getStart());
        for(int i = start; i < sessions.size(); i++){
            if(getDayFromDate(sessions.get(i).getStart()).equals(day))
                group.add(sessions.get(i));
            else break;
        }

        return new SectionedDataModel(day, group);
    }

    private String getDayFromDate(Date date){
        SimpleDateFormat formatter = new SimpleDateFormat("EEEE", Locale.getDefault());
        return formatter.format(date);
    }

    private String getTitle(SSession session){
        if(session == null) return "Sessions";
        return getDayFromDate(session.getStart()) + " Sessions";
    }
}
