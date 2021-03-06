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
import org.shingo.shingoeventsapp.middle.SEntity.SRecipient;
import org.shingo.shingoeventsapp.middle.SectionedDataModel;
import org.shingo.shingoeventsapp.ui.events.viewadapters.MySectionedSEntityRecyclerViewAdapter;
import org.shingo.shingoeventsapp.ui.interfaces.OnErrorListener;
import org.shingo.shingoeventsapp.ui.interfaces.EventInterface;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of {@link SRecipient}s.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnErrorListener}
 * interface and the {@link EventInterface}.
 */
public class RecipientFragment extends Fragment implements OnTaskCompleteListener {

    private static final String ARG_ID = "event_id";
    private static final String CACHE_KEY = "recipients";
    private String mEventId = "";
    private List<SectionedDataModel> data = new ArrayList<>();

    private OnErrorListener mErrorListener;
    private EventInterface mEvents;

    private RecyclerView.Adapter mAdapter;
    private ProgressBar progress;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RecipientFragment() {
    }

    public static RecipientFragment newInstance(String eventId) {
        RecipientFragment fragment = new RecipientFragment();
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
        getActivity().setTitle("Recipients");

        if(mEvents.getEvent(mEventId).needsUpdated(CACHE_KEY)){
            GetAsyncData getRecipientsAsync = new GetAsyncData(this);
            getRecipientsAsync.execute("/salesforce/events/recipients", ARG_ID + "=" + mEventId);

            progress = (ProgressBar) view.findViewById(R.id.progressBar);
        } else {
            view.findViewById(R.id.progressBar).setVisibility(View.GONE);
        }

        Context context = view.getContext();
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        sectionRecipients(mEvents.getEvent(mEventId).getRecipients());
        mAdapter = new MySectionedSEntityRecyclerViewAdapter(data);
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
                if(result.has("recipients")){
                    JSONArray jRecipients = result.getJSONArray("recipients");
                    mEvents.getEvent(mEventId).getRecipients().clear();
                    mEvents.getEvent(mEventId).updatePullTime(CACHE_KEY);
                    for(int i = 0; i < jRecipients.length(); i++){
                        SRecipient recipient = new SRecipient();
                        recipient.fromJSON(jRecipients.getJSONObject(i).toString());
                        mEvents.getEvent(mEventId).getRecipients().add(recipient);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        sectionRecipients(mEvents.getEvent(mEventId).getRecipients());

        mAdapter.notifyDataSetChanged();
        if(mEvents.getEvent(mEventId).getRecipients().isEmpty() && getView() != null)
            getView().findViewById(R.id.empty_entity).setVisibility(View.VISIBLE);
        progress.setVisibility(View.GONE);
    }

    @Override
    public void onTaskError(String error) {
        if(mErrorListener != null)
            mErrorListener.handleError(error);
        progress.setVisibility(View.GONE);
    }

    private void sectionRecipients(List<SRecipient> sponsors){
        data.clear();
        for(SRecipient.SRecipientAward type : SRecipient.SRecipientAward.values()){
            data.add(groupRecipientsByLevel(type, sponsors));
        }
    }

    private SectionedDataModel groupRecipientsByLevel(SRecipient.SRecipientAward award, List<SRecipient> sponsors){
        List<SRecipient> group = new ArrayList<>();
        for(SRecipient p : sponsors){
            if(p.award == award)
                group.add(p);
            else if(award == SRecipient.SRecipientAward.ResearchAward && p.award == null)
                group.add(p);
        }

        return new SectionedDataModel(award.toString() + " Recipient", group);
    }
}
