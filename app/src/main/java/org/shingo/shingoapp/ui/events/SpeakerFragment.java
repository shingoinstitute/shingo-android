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
import org.shingo.shingoapp.middle.SEntity.SPerson;
import org.shingo.shingoapp.middle.SEvent.SSession;
import org.shingo.shingoapp.ui.MainActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnSpeakerListFragmentInteractionListener}
 * interface.
 */
public class SpeakerFragment extends Fragment implements OnTaskComplete {

    private static final String ARG_ID = "ids";
    private static final String CACHE_KEY = "speakers";
    private String mSessionId;
    private ArrayList<String> mIds;
    private OnSpeakerListFragmentInteractionListener mListener;
    private RecyclerView.Adapter mAdapter;
    private ProgressDialog progress;
    private View view;
    private MainActivity mainActivity;
    private boolean isSectioned = true;
    private List<SectionedSpeakerDataModel> data = new ArrayList<>();

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public SpeakerFragment() {
    }

    public static SpeakerFragment newInstance(){ return new SpeakerFragment(); }

    public static SpeakerFragment newInstance(ArrayList<String> ids, String session_id) {
        SpeakerFragment fragment = new SpeakerFragment();
        Bundle args = new Bundle();
        args.putString("session_id", session_id);
        args.putStringArrayList(ARG_ID, ids);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mIds = getArguments().getStringArrayList(ARG_ID);
            mSessionId = getArguments().getString("session_id");
            isSectioned = false;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_speaker_list, container, false);
        mainActivity = (MainActivity)getActivity();
        mainActivity.setTitle("Speakers");
        int eventIndex = mainActivity.mEventIndex;
        if(mainActivity.mEvents.size() == 0){
            mainActivity.onNavigationItemSelected(mainActivity.navigationView.getMenu().findItem(R.id.nav_events));
            return null;
        }
        Context context = view.getContext();
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        if(mIds != null && mainActivity.mEvents.get(eventIndex).hasCache(CACHE_KEY)){
            mAdapter = new MySpeakerRecyclerViewAdapter(mainActivity.mEvents.get(eventIndex).getSubsetSpeakers(mIds), mListener);
        } else if(mIds != null){
            mainActivity.mEvents.get(eventIndex).getSpeakers().clear();
            GetAsyncData getSpeakersAsync = new GetAsyncData(this);
            String[] params = {"/salesforce/events/speakers", "session_id=" + mSessionId};
            getSpeakersAsync.execute(params);
            mAdapter = new MySpeakerRecyclerViewAdapter(mainActivity.mEvents.get(mainActivity.mEventIndex).getSpeakers(), mListener);
            progress = ProgressDialog.show(getContext(), "", "Loading Speakers...");
        } else if(mainActivity.mEvents.get(eventIndex).needsUpdated(CACHE_KEY)){
            mainActivity.mEvents.get(eventIndex).getSpeakers().clear();
            mainActivity.mEvents.get(eventIndex).updatePullTime(CACHE_KEY);
            GetAsyncData getSpeakersAsync = new GetAsyncData(this);
            String[] params = {"/salesforce/events/speakers", "event_id=" + mainActivity.mEvents.get(eventIndex).getId()};
            getSpeakersAsync.execute(params);
            mAdapter = new MySpeakerSectionedRecyclerViewAdapter(data, mListener);
            progress = ProgressDialog.show(getContext(), "", "Loading Speakers...");
        } else {
            sectionSpeakers(mainActivity.mEvents.get(eventIndex).getSpeakers());
            mAdapter = new MySpeakerSectionedRecyclerViewAdapter(data, mListener);
        }

        recyclerView.setAdapter(mAdapter);

        if(mAdapter.getItemCount() == 0){
            (view.findViewById(R.id.empty_speakers)).setVisibility(View.VISIBLE);
        } else {
            (view.findViewById(R.id.empty_speakers)).setVisibility(View.GONE);
        }

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnSpeakerListFragmentInteractionListener) {
            mListener = (OnSpeakerListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnSpeakerListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onTaskComplete() {

    }

    @Override
    public void onTaskComplete(String response) {
        try {
            JSONObject result = new JSONObject(response);
            if(result.getBoolean("success")){
                JSONArray jSpeakers = result.getJSONArray("speakers");
                for(int i = 0; i < jSpeakers.length(); i++){
                    SPerson speaker = new SPerson();
                    speaker.fromJSON(jSpeakers.getJSONObject(i).toString());
                    mainActivity.mEvents.get(mainActivity.mEventIndex).getSpeakers().add(speaker);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Collections.sort(mainActivity.mEvents.get(mainActivity.mEventIndex).getSpeakers());

        if(isSectioned){
            sectionSpeakers(mainActivity.mEvents.get(mainActivity.mEventIndex).getSpeakers());
        }

        if(mAdapter.getItemCount() == 0){
            (view.findViewById(R.id.empty_speakers)).setVisibility(View.VISIBLE);
        } else {
            (view.findViewById(R.id.empty_speakers)).setVisibility(View.GONE);
        }

        mAdapter.notifyDataSetChanged();
        progress.dismiss();
    }

    private void sectionSpeakers(List<SPerson> speakers){
        for(SPerson.SPersonType type : SPerson.SPersonType.values()){
            data.add(groupSpeakersByType(type, speakers));
        }
    }

    private SectionedSpeakerDataModel groupSpeakersByType(SPerson.SPersonType type, List<SPerson> speakers){
        List<SPerson> group = new ArrayList<>();
        for(SPerson p : speakers){
            if(p.type == type)
                group.add(p);
            else if(type == SPerson.SPersonType.ConcurrentSpeaker && p.type == null)
                group.add(p);
        }

        return new SectionedSpeakerDataModel(type.toString(), group);
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
    public interface OnSpeakerListFragmentInteractionListener {
        void onListFragmentInteraction(SPerson person);
    }

    public class SectionedSpeakerDataModel{
        private String header;
        private List<SPerson> items;

        public SectionedSpeakerDataModel(){}

        public SectionedSpeakerDataModel(String header, List<SPerson> items){
            this.header = header + "s";
            this.items = items;
        }

        public String getHeader() {
            return header;
        }

        public void setHeader(String day) {
            this.header = day;
        }

        public List<SPerson> getItems() {
            return items;
        }

        public void setItems(List<SPerson> items) {
            this.items = items;
        }
    }
}
