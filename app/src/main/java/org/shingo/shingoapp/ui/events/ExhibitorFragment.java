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
import org.shingo.shingoapp.middle.SEntity.SOrganization;
import org.shingo.shingoapp.ui.MainActivity;

/**
 * A fragment representing a list of {@link SOrganization}.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class ExhibitorFragment extends Fragment implements OnTaskComplete {

    private static final String ARG_ID = "event_id";
    private static final String CACHE_KEY = "exhibitors";
    private String mEventId = "";
    private OnListFragmentInteractionListener mListener;
    private MainActivity mainActivity;
    private ProgressDialog progress;
    private RecyclerView.Adapter mAdapter;

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

        if (getArguments() != null) {
            mEventId = getArguments().getString(ARG_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_exhibitor_list, container, false);

        // Set the adapter
        Context context = view.getContext();
        RecyclerView recyclerView = (RecyclerView) view;
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        mainActivity = (MainActivity) getActivity();
        mAdapter = new MyExhibitorRecyclerViewAdapter(mainActivity.mEvents.get(mainActivity.mEventIndex).getExhibitors(), mListener);
        if(mainActivity.mEvents.get(mainActivity.mEventIndex).needsUpdated(CACHE_KEY)){
            GetAsyncData getExhibitorsAsync = new GetAsyncData(this);
            String[] params = {"/salesforce/events/exhibitors", ARG_ID + "=" + mEventId};
            getExhibitorsAsync.execute(params);
            progress = ProgressDialog.show(getContext(), "", "Loading Exhibitors");
        } else {
            recyclerView.setAdapter(mAdapter);
        }

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onTaskComplete() {
        throw new UnsupportedOperationException("Not implemented..");
    }

    @Override
    public void onTaskComplete(String response) {
        try {
            JSONObject result = new JSONObject(response);
            if(result.getBoolean("success")){
                if(result.has("exhibitors")){
                    JSONArray jExhibitors = result.getJSONArray("exhibitors");
                    mainActivity.mEvents.get(mainActivity.mEventIndex).getExhibitors().clear();
                    mainActivity.mEvents.get(mainActivity.mEventIndex).updatePullTime(CACHE_KEY);
                    for(int i = 0; i < jExhibitors.length(); i++){
                        SOrganization org = new SOrganization();
                        org.fromJSON(jExhibitors.getJSONObject(i).getJSONObject("Organization__r").toString());
                        org.type = SOrganization.SOrganizationType.Exhibitor;
                        mainActivity.mEvents.get(mainActivity.mEventIndex).getExhibitors().add(org);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mAdapter.notifyDataSetChanged();
        progress.dismiss();
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
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(SOrganization org);
    }
}
