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
import org.shingo.shingoapp.middle.SEntity.SSponsor;
import org.shingo.shingoapp.ui.MainActivity;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class SponsorFragment extends Fragment implements OnTaskComplete{

    private static final String ARG_ID = "event_id";
    private static final String CACHE_KEY = "sponsors";
    private String mEventId = "";
    private OnListFragmentInteractionListener mListener;
    private RecyclerView.Adapter mAdapter;
    private MainActivity mainActivity;
    private ProgressDialog progress;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public SponsorFragment() {
    }

    public static SponsorFragment newInstance(String eventId) {
        SponsorFragment fragment = new SponsorFragment();
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
        View view = inflater.inflate(R.layout.fragment_sponsor_list, container, false);
        mainActivity = (MainActivity)getActivity();
        mainActivity.setTitle("Sponsors");

        Context context = view.getContext();
        RecyclerView recyclerView = (RecyclerView) view;
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        if(mainActivity.mEvents.get(mainActivity.mEventIndex).needsUpdated(CACHE_KEY)){
            GetAsyncData getSponsorsAsync = new GetAsyncData(this);
            String[] params = { "/salesforce/events/sponsors", ARG_ID + "=" + mEventId };
            getSponsorsAsync.execute(params);

            progress = ProgressDialog.show(getContext(), "", "Loading Sponsors...");
        }

        mAdapter = new MySponsorRecyclerViewAdapter(mainActivity.mEvents.get(mainActivity.mEventIndex).getSponsors(), mListener);
        recyclerView.setAdapter(mAdapter);

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
        throw new UnsupportedOperationException("Not implemented...");
    }

    @Override
    public void onTaskComplete(String response) {
        try {
            JSONObject result = new JSONObject(response);
            if(result.getBoolean("success")){
                if(result.has("sponsors")){
                    mainActivity.mEvents.get(mainActivity.mEventIndex).updatePullTime(CACHE_KEY);
                    mainActivity.mEvents.get(mainActivity.mEventIndex).getSponsors().clear();
                    JSONArray jSponsors = result.getJSONArray("sponsors");
                    for(int i = 0; i < jSponsors.length(); i++){
                        SSponsor sponsor = new SSponsor();
                        sponsor.fromJSON(jSponsors.getJSONObject(i).toString());
                        mainActivity.mEvents.get(mainActivity.mEventIndex).getSponsors().add(sponsor);
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
        mainActivity.handleError(error);
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
        void onListFragmentInteraction(SSponsor item);
    }
}
