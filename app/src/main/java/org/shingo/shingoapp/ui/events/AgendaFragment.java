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
import org.shingo.shingoapp.middle.SEvent.SDay;
import org.shingo.shingoapp.ui.MainActivity;

import java.util.Collections;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnAgendaFragmentInteractionListener}
 * interface.
 */
public class AgendaFragment extends Fragment implements OnTaskComplete {

    private static final String ARG_EVENT_ID = "event_id";
    private String mEventId;
    private OnAgendaFragmentInteractionListener mListener;
    private RecyclerView.Adapter mAdapter;
    private ProgressDialog progress;

    private MainActivity mainActivity;

    private final String CACHE_KEY = "agenda";

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
        mainActivity = (MainActivity) getActivity();

        if (getArguments() != null) {
            mEventId = getArguments().getString(ARG_EVENT_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_agenda_list, container, false);

        Context context = view.getContext();
        RecyclerView mRecyclerView = (RecyclerView) view;
        mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        mainActivity.setTitle("Agenda");
        if(mainActivity.mEvents.size() == 0){
            mainActivity.onNavigationItemSelected(mainActivity.navigationView.getMenu().findItem(R.id.nav_events));
            return null;
        }
        if(mainActivity.mEvents.get(mainActivity.mEventIndex).needsUpdated(CACHE_KEY)) {
            mainActivity.mEvents.get(mainActivity.mEventIndex).updatePullTime(CACHE_KEY);
            GetAsyncData getDaysAsync = new GetAsyncData(this);
            String[] params = {"/salesforce/events/days", "event_id=" + mEventId};
            getDaysAsync.execute(params);

            progress = ProgressDialog.show(getContext(), "", "Loading agenda...");
        }

        mAdapter = new MyAgendaRecyclerViewAdapter(mainActivity.mEvents.get(mainActivity.mEventIndex).getAgenda(), mListener);
        mRecyclerView.setAdapter(mAdapter);

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnAgendaFragmentInteractionListener) {
            mListener = (OnAgendaFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnAgendaFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onTaskComplete() {
        throw new UnsupportedOperationException("This callback is not implemented...");
    }

    @Override
    public void onTaskComplete(String response) {
        try {
            JSONObject result = new JSONObject(response);
            if(result.getBoolean("success")){
                JSONArray jDays = result.getJSONArray("days");
                for(int i = 0; i < jDays.length(); i++){
                    SDay day = new SDay();
                    day.fromJSON(jDays.getJSONObject(i).toString());
                    mainActivity.mEvents.get(mainActivity.mEventIndex).getAgenda().add(day);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Collections.sort(mainActivity.mEvents.get(mainActivity.mEventIndex).getAgenda());
        mAdapter.notifyDataSetChanged();
        progress.dismiss();
    }

    @Override
    public void onTaskError(String error) {
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
    public interface OnAgendaFragmentInteractionListener {
        void onListFragmentInteraction(SDay day);
    }
}
