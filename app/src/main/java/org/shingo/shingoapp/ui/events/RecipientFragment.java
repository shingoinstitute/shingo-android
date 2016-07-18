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
import org.shingo.shingoapp.middle.SEntity.SRecipient;
import org.shingo.shingoapp.ui.events.viewadapters.MyRecipientSectionedRecyclerViewAdapter;
import org.shingo.shingoapp.ui.interfaces.OnErrorListener;
import org.shingo.shingoapp.ui.interfaces.EventInterface;

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
    private List<SectionedRecipientDataModel> data = new ArrayList<>();

    private OnErrorListener mErrorListener;
    private EventInterface mEvents;

    private RecyclerView.Adapter mAdapter;
    private ProgressDialog progress;

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

        if (getArguments() != null) {
            mEventId = getArguments().getString(ARG_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipients_list, container, false);
        getActivity().setTitle("Recipients");

        if(mEvents.get(mEventId).needsUpdated(CACHE_KEY)){
            GetAsyncData getRecipientsAsync = new GetAsyncData(this);
            String[] params = {"/salesforce/events/recipients", ARG_ID + "=" + mEventId};
            getRecipientsAsync.execute(params);

            progress = ProgressDialog.show(getContext(), "", "Loading Recipients");
        }

        Context context = view.getContext();
        RecyclerView recyclerView = (RecyclerView) view;
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        sectionRecipients(mEvents.get(mEventId).getRecipients());
        mAdapter = new MyRecipientSectionedRecyclerViewAdapter(data);
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
                if(result.has("recipients")){
                    JSONArray jRecipients = result.getJSONArray("recipients");
                    mEvents.get(mEventId).getRecipients().clear();
                    mEvents.get(mEventId).updatePullTime(CACHE_KEY);
                    for(int i = 0; i < jRecipients.length(); i++){
                        SRecipient recipient = new SRecipient();
                        recipient.fromJSON(jRecipients.getJSONObject(i).toString());
                        mEvents.get(mEventId).getRecipients().add(recipient);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        sectionRecipients(mEvents.get(mEventId).getRecipients());

        mAdapter.notifyDataSetChanged();
        progress.dismiss();
    }

    @Override
    public void onTaskError(String error) {
        mErrorListener.handleError(error);
        progress.dismiss();
    }

    private void sectionRecipients(List<SRecipient> sponsors){
        data.clear();
        for(SRecipient.SRecipientAward type : SRecipient.SRecipientAward.values()){
            data.add(groupRecipientsByLevel(type, sponsors));
        }
    }

    private SectionedRecipientDataModel groupRecipientsByLevel(SRecipient.SRecipientAward award, List<SRecipient> sponsors){
        List<SRecipient> group = new ArrayList<>();
        for(SRecipient p : sponsors){
            if(p.award == award)
                group.add(p);
            else if(award == SRecipient.SRecipientAward.ResearchAward && p.award == null)
                group.add(p);
        }

        return new SectionedRecipientDataModel(award.toString(), group);
    }

    public class SectionedRecipientDataModel {
        private String header;
        private List<SRecipient> items;

        public SectionedRecipientDataModel(String header, List<SRecipient> items){
            this.header = header + "s";
            this.items = items;
        }

        public String getHeader() {
            return header;
        }

        public List<SRecipient> getItems() {
            return items;
        }

        public void setItems(List<SRecipient> items) {
            this.items = items;
        }
    }
}
