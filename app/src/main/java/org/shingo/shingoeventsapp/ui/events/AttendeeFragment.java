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

import org.shingo.shingoeventsapp.R;
import org.shingo.shingoeventsapp.ui.events.viewadapters.MySEntityRecyclerViewAdapter;
import org.shingo.shingoeventsapp.ui.interfaces.EventInterface;

import java.util.Collections;

/**
 * A fragment representing a list of Items.
 * <p>
 * Activities containing this fragment MUST implement the {@link EventInterface}
 * interface.
 */
public class AttendeeFragment extends Fragment {

    private static final String ARG_ID = "event_id";
    private String mEventId = "";
    private EventInterface mEvents;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public AttendeeFragment() {
    }

    @SuppressWarnings("unused")
    public static AttendeeFragment newInstance(String eventId) {
        AttendeeFragment fragment = new AttendeeFragment();
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
        getActivity().setTitle("Attendees");
        View view = inflater.inflate(R.layout.fragment_sentity_list, container, false);

        Context context = view.getContext();
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        Collections.sort(mEvents.getEvent(mEventId).getAttendees());
        recyclerView.setAdapter(new MySEntityRecyclerViewAdapter(mEvents.getEvent(mEventId).getAttendees()));
        view.findViewById(R.id.progressBar).setVisibility(View.GONE);
        if(mEvents.getEvent(mEventId).getAttendees().size() == 0)
            view.findViewById(R.id.empty_entity).setVisibility(View.VISIBLE);

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
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mEvents = null;
    }
}
