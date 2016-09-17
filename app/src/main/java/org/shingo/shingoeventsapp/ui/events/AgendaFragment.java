package org.shingo.shingoeventsapp.ui.events;

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
import org.json.JSONObject;
import org.shingo.shingoeventsapp.R;
import org.shingo.shingoeventsapp.data.GetAsyncData;
import org.shingo.shingoeventsapp.data.OnTaskCompleteListener;
import org.shingo.shingoeventsapp.middle.SEvent.SDay;
import org.shingo.shingoeventsapp.ui.events.viewadapters.MySEventObjectRecyclerView;
import org.shingo.shingoeventsapp.ui.interfaces.NavigationInterface;
import org.shingo.shingoeventsapp.ui.interfaces.OnErrorListener;
import org.shingo.shingoeventsapp.ui.interfaces.EventInterface;
import org.shingo.shingoeventsapp.ui.interfaces.OnListFragmentInteractionListener;

import java.util.Collections;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class AgendaFragment extends Fragment implements OnTaskCompleteListener {

    private static final String ARG_EVENT_ID = "event_id";
    private static final String CACHE_KEY = "agenda";
    private String mEventId;

    private OnListFragmentInteractionListener mListener;
    private OnErrorListener mErrorListener;
    private NavigationInterface mNavigate;
    private EventInterface mEvents;

    private RecyclerView.Adapter mAdapter;
    private ProgressBar progress;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public AgendaFragment() {
    }

    public static AgendaFragment newInstance(String mEventId) {
        AgendaFragment fragment = new AgendaFragment();
        Bundle args = new Bundle();
        args.putString(ARG_EVENT_ID, mEventId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            mEventId = getArguments().getString(ARG_EVENT_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_seventobject_list, container, false);

        getActivity().setTitle("Agenda");

        if(mEvents.getEvent(mEventId).needsUpdated(CACHE_KEY)) {
            GetAsyncData getDaysAsync = new GetAsyncData(this);
            String[] params = {"/salesforce/events/days", "event_id=" + mEventId};
            getDaysAsync.execute(params);

            progress = (ProgressBar) view.findViewById(R.id.progressBar);
        } else {
            view.findViewById(R.id.progressBar).setVisibility(View.GONE);
        }

        Context context = view.getContext();
        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        mAdapter = new MySEventObjectRecyclerView(mEvents.getEvent(mEventId).getAgenda(), mListener);
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
        if (context instanceof EventInterface)
            mEvents = (EventInterface) context;
        else
            throw new RuntimeException(context.toString() + " must implement EventInterface");

        if (context instanceof NavigationInterface)
            mNavigate = (NavigationInterface) context;
        else
            throw new RuntimeException(context.toString() + " must implement NavigationInterface");

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
        mListener = null;
        mErrorListener = null;
        mEvents = null;
        mNavigate = null;
    }

    @Override
    public void onTaskComplete(String response) {
        try {
            JSONObject result = new JSONObject(response);
            if(result.getBoolean("success")){
                mEvents.getEvent(mEventId).updatePullTime(CACHE_KEY);
                mEvents.getEvent(mEventId).getAgenda().clear();
                JSONArray jDays = result.getJSONArray("days");
                for(int i = 0; i < jDays.length(); i++){
                    SDay day = new SDay();
                    day.fromJSON(jDays.getJSONObject(i).toString());
                    mEvents.getEvent(mEventId).getAgenda().add(day);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Collections.sort(mEvents.getEvent(mEventId).getAgenda());
        mAdapter.notifyDataSetChanged();
        if(mEvents.getEvent(mEventId).getAgenda().size() == 0 && getView() != null)
            getView().findViewById(R.id.empty_entity);
        progress.setVisibility(View.GONE);
    }

    @Override
    public void onTaskError(String error) {
        if(mErrorListener != null)
            mErrorListener.handleError(error);
        progress.setVisibility(View.GONE);
    }
}
