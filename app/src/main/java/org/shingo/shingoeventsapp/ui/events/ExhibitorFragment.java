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
import org.shingo.shingoeventsapp.middle.SEntity.SOrganization;
import org.shingo.shingoeventsapp.ui.events.viewadapters.MySEntityRecyclerViewAdapter;
import org.shingo.shingoeventsapp.ui.interfaces.OnErrorListener;
import org.shingo.shingoeventsapp.ui.interfaces.EventInterface;

/**
 * A fragment representing a list of {@link SOrganization}.
 */
public class ExhibitorFragment extends Fragment implements OnTaskCompleteListener {

    private static final String ARG_ID = "event_id";
    private static final String CACHE_KEY = "exhibitors";
    private String mEventId = "";

    private OnErrorListener mErrorListener;
    private EventInterface mEvents;

    private RecyclerView.Adapter mAdapter;
    private ProgressBar progress;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ExhibitorFragment() {
    }

    public static ExhibitorFragment newInstance(String eventId) {
        ExhibitorFragment fragment = new ExhibitorFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ID, eventId);
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
        View view = inflater.inflate(R.layout.fragment_sentity_list, container, false);

        getActivity().setTitle("Exhibitors");

        if(mEvents.getEvent(mEventId).needsUpdated(CACHE_KEY)) {
            GetAsyncData getExhibitorsAsync = new GetAsyncData(this);
            getExhibitorsAsync.execute("/salesforce/events/exhibitors", ARG_ID + "=" + mEventId);

            progress = (ProgressBar) view.findViewById(R.id.progressBar);
        } else {
            view.findViewById(R.id.progressBar).setVisibility(View.GONE);
        }

        mAdapter = new MySEntityRecyclerViewAdapter(mEvents.getEvent(mEventId).getExhibitors());

        Context context = view.getContext();
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
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
        if(context instanceof EventInterface)
            mEvents = (EventInterface) context;
        else
            throw new RuntimeException(context.toString() + " must implement EventInterface");

        if(context instanceof OnErrorListener)
            mErrorListener = (OnErrorListener) context;
        else
            throw new RuntimeException(context.toString() + " must implement OnErrorListener");

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mErrorListener =null;
        mEvents = null;
    }

    @Override
    public void onTaskComplete(String response) {
        try {
            JSONObject result = new JSONObject(response);
            if(result.getBoolean("success")){
                if(result.has("exhibitors")){
                    JSONArray jExhibitors = result.getJSONArray("exhibitors");
                    mEvents.getEvent(mEventId).getExhibitors().clear();
                    mEvents.getEvent(mEventId).updatePullTime(CACHE_KEY);
                    for(int i = 0; i < jExhibitors.length(); i++){
                        SOrganization org = new SOrganization();
                        jExhibitors.getJSONObject(i).getJSONObject("Organization__r").put("Id", jExhibitors.getJSONObject(i).getString("Id"));
                        org.fromJSON(jExhibitors.getJSONObject(i).getJSONObject("Organization__r").toString());
                        org.type = SOrganization.SOrganizationType.Exhibitor;
                        mEvents.getEvent(mEventId).getExhibitors().add(org);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        mAdapter.notifyDataSetChanged();
        if(mEvents.getEvent(mEventId).getExhibitors().isEmpty() && getView() != null)
            getView().findViewById(R.id.empty_entity).setVisibility(View.VISIBLE);
        progress.setVisibility(View.GONE);
    }

    @Override
    public void onTaskError(String error) {
        if(mErrorListener != null)
            mErrorListener.handleError(error);
        progress.setVisibility(View.GONE);
    }
}
