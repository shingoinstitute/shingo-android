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
import org.shingo.shingoapp.data.OnTaskCompleteListener;
import org.shingo.shingoapp.middle.SEntity.SSponsor;
import org.shingo.shingoapp.ui.events.viewadapters.MySponsorSectionedRecyclerViewAdapter;
import org.shingo.shingoapp.ui.interfaces.OnErrorListener;
import org.shingo.shingoapp.ui.interfaces.EventInterface;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of {@link SSponsor}s.
 */
public class SponsorFragment extends Fragment implements OnTaskCompleteListener {

    private static final String ARG_ID = "event_id";
    private static final String CACHE_KEY = "sponsors";
    private String mEventId = "";
    private List<SectionedSponsorDataModel> data = new ArrayList<>();

    private OnErrorListener mErrorListener;
    private EventInterface mEvents;

    private RecyclerView.Adapter mAdapter;
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
        getActivity().setTitle("Sponsors");

        Context context = view.getContext();
        RecyclerView recyclerView = (RecyclerView) view;
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        if(mEvents.get(mEventId).needsUpdated(CACHE_KEY)){
            GetAsyncData getSponsorsAsync = new GetAsyncData(this);
            getSponsorsAsync.execute("/salesforce/events/sponsors", ARG_ID + "=" + mEventId);

            progress = ProgressDialog.show(getContext(), "", "Loading Sponsors...");
        }
        sectionSponsors(mEvents.get(mEventId).getSponsors());
        mAdapter = new MySponsorSectionedRecyclerViewAdapter(data);
        recyclerView.setAdapter(mAdapter);

        return view;
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
                if(result.has("sponsors")){
                    mEvents.get(mEventId).getSponsors().clear();
                    mEvents.get(mEventId).updatePullTime(CACHE_KEY);
                    JSONArray jSponsors = result.getJSONArray("sponsors");
                    for(int i = 0; i < jSponsors.length(); i++){
                        SSponsor sponsor = new SSponsor();
                        sponsor.fromJSON(jSponsors.getJSONObject(i).toString());
                        mEvents.get(mEventId).getSponsors().add(sponsor);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        sectionSponsors(mEvents.get(mEventId).getSponsors());

        mAdapter.notifyDataSetChanged();

        progress.dismiss();
    }

    @Override
    public void onTaskError(String error) {
        mErrorListener.handleError(error);
        progress.dismiss();
    }

    private void sectionSponsors(List<SSponsor> sponsors){
        data.clear();
        for(SSponsor.SSponsorLevel type : SSponsor.SSponsorLevel.values()){
            data.add(groupSponsorsByLevel(type, sponsors));
        }
    }

    private SectionedSponsorDataModel groupSponsorsByLevel(SSponsor.SSponsorLevel level, List<SSponsor> sponsors){
        List<SSponsor> group = new ArrayList<>();
        for(SSponsor p : sponsors){
            if(p.level == level)
                group.add(p);
            else if(level == SSponsor.SSponsorLevel.Friend && p.level == null)
                group.add(p);
        }

        return new SectionedSponsorDataModel(level.toString(), group);
    }

    public class SectionedSponsorDataModel {
        private String header;
        private List<SSponsor> items;

        public SectionedSponsorDataModel(String header, List<SSponsor> items){
            this.header = header + "s";
            this.items = items;
        }

        public String getHeader() {
            return header;
        }

        public List<SSponsor> getItems() {
            return items;
        }

        public void setItems(List<SSponsor> items) {
            this.items = items;
        }
    }
}
