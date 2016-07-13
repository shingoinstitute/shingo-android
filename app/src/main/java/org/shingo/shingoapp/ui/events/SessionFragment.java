package org.shingo.shingoapp.ui.events;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.shingo.shingoapp.R;
import org.shingo.shingoapp.data.GetAsyncData;
import org.shingo.shingoapp.data.OnTaskComplete;
import org.shingo.shingoapp.middle.SEvent.SSession;
import org.shingo.shingoapp.ui.MainActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * A fragment representing a list of {@link SSession}s.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnSessionListFragmentInteractionListener}
 * interface.
 */
public class SessionFragment extends Fragment implements OnTaskComplete{

    private static final String ARG_ID = "ids";
    private static final String CACHE_KEY = "sessions";
    private boolean isSectioned = true;
    private ArrayList<String> mIds;
    private List<SectionedSessionDataModel> data = new ArrayList<>();
    private String mAgendaId;
    private OnSessionListFragmentInteractionListener mListener;

    private RecyclerView.Adapter mAdapter;
    private ProgressDialog progress;
    private MainActivity mainActivity;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public SessionFragment() {
    }

    public static SessionFragment newInstance(){
        return new SessionFragment();
    }

    public static SessionFragment newInstance(ArrayList<String> ids, String agenda_id) {
        SessionFragment fragment = new SessionFragment();
        Bundle args = new Bundle();
        args.putString("agenda_id", agenda_id);
        args.putStringArrayList(ARG_ID, ids);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mIds = getArguments().getStringArrayList(ARG_ID);
            mAgendaId = getArguments().getString("agenda_id");
            isSectioned = false;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_session_list, container, false);
        mainActivity = (MainActivity)getActivity();
        mainActivity.setTitle("Sessions");
        if(mainActivity.mEvents.size() == 0){
            mainActivity.onNavigationItemSelected(mainActivity.navigationView.getMenu().findItem(R.id.nav_events));
            return null;
        }
        int eventIndex = mainActivity.mEventIndex;

        Context context = view.getContext();
        RecyclerView mRecyclerView = (RecyclerView) view;
        mRecyclerView.setLayoutManager(new LinearLayoutManager(context));

        if(!isSectioned && mainActivity.mEvents.get(eventIndex).hasCache(CACHE_KEY)) {
            mAdapter = new MySessionRecyclerViewAdapter(mainActivity.mEvents.get(eventIndex).getSubsetSessions(mIds), mListener);
        } else if(!isSectioned){
            mainActivity.mEvents.get(eventIndex).getSessions().clear();
            GetAsyncData getSessionsAsync = new GetAsyncData(this);
            String[] params = {"/salesforce/events/sessions/", "agenda_id=" + mAgendaId};
            getSessionsAsync.execute(params);
            progress = ProgressDialog.show(getContext(), "", "Loading Sessions...");
            mAdapter = new MySessionRecyclerViewAdapter(mainActivity.mEvents.get(eventIndex).getSessions(), mListener);
        } else if(mainActivity.mEvents.get(eventIndex).needsUpdated(CACHE_KEY)) {
            mainActivity.mEvents.get(eventIndex).getSessions().clear();
            mainActivity.mEvents.get(eventIndex).updatePullTime(CACHE_KEY);
            GetAsyncData getSessionsAsync = new GetAsyncData(this);
            String[] params = {"/salesforce/events/sessions/", "event_id=" + mainActivity.mEvents.get(eventIndex).getId()};
            getSessionsAsync.execute(params);
            progress = ProgressDialog.show(getContext(), "", "Loading Sessions...");
            mAdapter = new MySessionSectionedRecylclerViewAdapter(data, mListener);
        } else {
            sectionSessions(mainActivity.mEvents.get(eventIndex).getSessions());
            mAdapter = new MySessionSectionedRecylclerViewAdapter(data, mListener);
        }

        mRecyclerView.setAdapter(mAdapter);

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnSessionListFragmentInteractionListener) {
            mListener = (OnSessionListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnSessionListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onTaskComplete() {
        throw new UnsupportedOperationException("Not implemented!");
    }

    @Override
    public void onTaskComplete(String response) {
        try {
            JSONObject result = new JSONObject(response);
            if (result.getBoolean("success")) {
                if(result.has("sessions")) {
                    JSONArray jSessions = result.getJSONArray("sessions");
                    for (int i = 0; i < jSessions.length(); i++) {
                        SSession session = new SSession();
                        session.fromJSON(jSessions.getJSONObject(i).toString());
                        mainActivity.mEvents.get(mainActivity.mEventIndex).getSessions().add(session);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Collections.sort(mainActivity.mEvents.get(mainActivity.mEventIndex).getSessions());
        if(isSectioned) sectionSessions(mainActivity.mEvents.get(mainActivity.mEventIndex).getSessions());
        mAdapter.notifyDataSetChanged();
        progress.dismiss();
    }

    private void sectionSessions(List<SSession> sessions){
        for (int i = 0; i < sessions.size(); i++) {
            data.add(groupSessionsByDay(i, sessions));
            i += data.get(data.size() - 1).getItems().size() - 1;
        }
    }

    private SectionedSessionDataModel groupSessionsByDay(int start, List<SSession> sessions){
        List<SSession> group = new ArrayList<>();
        String day = getDayFromDate(sessions.get(start).getStart());
        for(int i = start; i < sessions.size(); i++){
            if(getDayFromDate(sessions.get(i).getStart()).equals(day))
                group.add(sessions.get(i));
            else break;
        }

        return new SectionedSessionDataModel(day, group);
    }

    private String getDayFromDate(Date date){
        SimpleDateFormat formatter = new SimpleDateFormat("EEEE");
        return formatter.format(date);
    }

    @Override
    public void onTaskError(String error) {
        progress.dismiss();
        // TODO: Create and show Error Dialog
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
    public interface OnSessionListFragmentInteractionListener {
        void onListFragmentInteraction(SSession item);
    }

    public class SectionedSessionDataModel {
        private String day;
        private List<SSession> items;

        public SectionedSessionDataModel(){}

        public SectionedSessionDataModel(String day, List<SSession> items){
            this.day = day;
            this.items = items;
        }

        public String getDay() {
            return day;
        }

        public void setDay(String day) {
            this.day = day;
        }

        public List<SSession> getItems() {
            return items;
        }

        public void setItems(List<SSession> items) {
            this.items = items;
        }
    }
}
