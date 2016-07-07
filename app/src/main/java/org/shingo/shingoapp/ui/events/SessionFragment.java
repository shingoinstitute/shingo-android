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
import org.shingo.shingoapp.middle.SEvent.SSession;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnSessionListFragmentInteractionListener}
 * interface.
 */
public class SessionFragment extends Fragment implements OnTaskComplete{

    private String ARG_ID = "";
    private String mId = "";
    private OnSessionListFragmentInteractionListener mListener;
    private List<SSession> mSessions;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private ProgressDialog progress;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public SessionFragment() {
    }

    public static SessionFragment newInstance(String id, String ARG_TYPE) {
        SessionFragment fragment = new SessionFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TYPE, id);
        args.putString("type", ARG_TYPE);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            ARG_ID = getArguments().getString("type");
            mId = getArguments().getString(ARG_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_session_list, container, false);

        mSessions = new ArrayList<>();
        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            mRecyclerView = (RecyclerView) view;
            mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
            mAdapter = new MySessionRecyclerViewAdapter(mSessions, mListener);
            mRecyclerView.setAdapter(mAdapter);
            GetAsyncData getSessionsAsync = new GetAsyncData(this);
            String[] params = {"/salesforce/events/sessions/", ARG_ID + "=" + mId};
            getSessionsAsync.execute(params);
            progress = ProgressDialog.show(getContext(), "", "Loading Sessions...");
        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnSessionListFragmentInteractionListener) {
            mListener = (OnSessionListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnSessionListFragmentInteractionListener");
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
                JSONArray jSessions = result.getJSONArray("sessions");
                for(int i = 0; i < jSessions.length(); i++){
                    SSession session = new SSession();
                    session.fromJSON(jSessions.getJSONObject(i).toString());
                    mSessions.add(session);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Collections.sort(mSessions);
        mAdapter.notifyDataSetChanged();
        progress.dismiss();
    }

    @Override
    public void onTaskError(String error) {

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
    public interface OnSessionListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(SSession item);
    }
}
