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

    private String ARG_ID = "";
    private String mId = "";
    private OnSpeakerListFragmentInteractionListener mListener;
    private List<SPerson> mSpeakers;
    private RecyclerView.Adapter mAdapter;
    private ProgressDialog progress;
    private View view;
    private MainActivity mainActivity;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public SpeakerFragment() {
    }

    public static SpeakerFragment newInstance(String id, String type) {
        SpeakerFragment fragment = new SpeakerFragment();
        Bundle args = new Bundle();
        args.putString("type", type);
        args.putString(type, id);
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
        view = inflater.inflate(R.layout.fragment_speaker_list, container, false);
        mainActivity = (MainActivity)getActivity();
        mainActivity.setTitle("Speakers");

        Context context = view.getContext();
        mSpeakers = new ArrayList<>();
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        mAdapter = new MySpeakerRecyclerViewAdapter(mSpeakers, mListener);
        recyclerView.setAdapter(mAdapter);

        GetAsyncData getSpeakersAsync = new GetAsyncData(this);
        String[] params = {"/salesforce/events/speakers", ARG_ID + "=" + mId};
        getSpeakersAsync.execute(params);

        progress = ProgressDialog.show(getContext(), "", "Loading Speakers...");

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
                    mSpeakers.add(speaker);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Collections.sort(mSpeakers);
        mAdapter.notifyDataSetChanged();

        if(mSpeakers.size() == 0){
            (view.findViewById(R.id.empty_speakers)).setVisibility(View.VISIBLE);
        }

        progress.dismiss();
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
}
