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
import org.shingo.shingoapp.middle.SEvent.SEvent;
import org.shingo.shingoapp.ui.MainActivity;
import org.shingo.shingoapp.ui.interfaces.CacheInterface;
import org.shingo.shingoapp.ui.interfaces.OnErrorListener;
import org.shingo.shingoapp.ui.events.viewadapters.MyEventRecyclerViewAdapter;
import org.shingo.shingoapp.ui.interfaces.EventInterface;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnEventFragmentInteractionListener}
 * interface.
 */
public class EventFragment extends Fragment implements OnTaskCompleteListener {

    private OnEventFragmentInteractionListener mListener;
    private OnErrorListener mErrorListener;
    private EventInterface mEvents;
    private CacheInterface mCache;
    private RecyclerView.Adapter mAdapter;
    private ProgressDialog progress;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public EventFragment() {
    }

    public static EventFragment newInstance() {
        return new EventFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle("Upcoming Events");
        ((MainActivity) getActivity()).toggleNavHeader(0);

        View view = inflater.inflate(R.layout.fragment_event_list, container, false);

        if(mCache.needsUpdated(CacheInterface.CacheType.Events)) {
            GetAsyncData getEventsAsync = new GetAsyncData(this);
            getEventsAsync.execute("/salesforce/events");

            progress = ProgressDialog.show(getContext(), "", "Loading Events", true);
        }

        Context context = view.getContext();
        RecyclerView recyclerView = (RecyclerView) view;
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        mAdapter = new MyEventRecyclerViewAdapter(mEvents.events(), mListener);
        recyclerView.setAdapter(mAdapter);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof CacheInterface)
            mCache = (CacheInterface) context;
        else
            throw new RuntimeException(context.toString() + " must implement CacheInterface");

        if(context instanceof EventInterface)
            mEvents = (EventInterface) context;
        else
            throw new RuntimeException(context.toString() + " must implement EventInterface");

        if(context instanceof OnErrorListener)
            mErrorListener = (OnErrorListener) context;
        else
            throw new RuntimeException(context.toString() + " must implement OnErrorListener");

        if (context instanceof OnEventFragmentInteractionListener)
            mListener = (OnEventFragmentInteractionListener) context;
        else
            throw new RuntimeException(context.toString() + " must implement OnEventFragmentInteractionListener");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        mErrorListener =null;
        mEvents = null;
    }

    @Override
    public void onTaskComplete(String response) {
        try {
            JSONObject result = new JSONObject(response);
            if(result.getBoolean("success")){
                JSONArray jEvents = result.getJSONArray("events");
                mEvents.clearEvents();
                mCache.updateTime(CacheInterface.CacheType.Events);
                for(int i = 0; i < jEvents.length(); i++){
                    SEvent event = new SEvent();
                    event.fromJSON(jEvents.getJSONObject(i).toString());
                    mEvents.addEvent(event);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mEvents.sortEvents();
        mAdapter.notifyDataSetChanged();
        //TODO: Display empty message if data set is empty

        progress.dismiss();
    }

    @Override
    public void onTaskError(String error) {
        mErrorListener.handleError(error);
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
    public interface OnEventFragmentInteractionListener {
        void onListFragmentInteraction(SEvent event);
    }
}
