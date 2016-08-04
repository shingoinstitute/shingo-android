package org.shingo.shingoeventsapp.ui;

import android.app.ProgressDialog;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.shingo.shingoeventsapp.R;
import org.shingo.shingoeventsapp.data.GetAsyncData;
import org.shingo.shingoeventsapp.data.OnTaskCompleteListener;
import org.shingo.shingoeventsapp.middle.SEntity.SOrganization;
import org.shingo.shingoeventsapp.middle.SEntity.SOrganization.*;
import org.shingo.shingoeventsapp.ui.events.viewadapters.MySEntityRecyclerViewAdapter;
import org.shingo.shingoeventsapp.ui.interfaces.*;

/**
 * A fragment representing a list of {@link SOrganization} of the type {@link SOrganizationType#Affiliate}.
 * <p>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class AffiliateFragment extends Fragment implements OnTaskCompleteListener {

//    private OnListFragmentInteractionListener mListener;
    private OnErrorListener mErrorListener;
    private CacheInterface mCache;
    private AffiliateInterface mAffiliates;

    private RecyclerView.Adapter mAdapter;
    private ProgressDialog progress;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public AffiliateFragment() {
    }

    public static AffiliateFragment newInstance() {
        return new AffiliateFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle("Licensed Affiliates");
        ((MainActivity) getActivity()).toggleNavHeader(0);

        View view = inflater.inflate(R.layout.fragment_sentity_list, container, false);

        if(mCache.needsUpdated(CacheInterface.CacheType.Affiliates)){
            GetAsyncData getAffiliatesAsync = new GetAsyncData(this);
            getAffiliatesAsync.execute("/salesforce/affiliates");

            progress = ProgressDialog.show(getContext(), "", "Loading Affiliates...");
        }

        Context context = view.getContext();
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        mAdapter = new MySEntityRecyclerViewAdapter(mAffiliates.affiliates());
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
        if (context instanceof AffiliateInterface)
            mAffiliates = (AffiliateInterface) context;
        else
            throw new RuntimeException(context.toString() + " must implement AffiliateInterface");

        if (context instanceof CacheInterface)
            mCache = (CacheInterface) context;
        else
            throw new RuntimeException(context.toString() + " must implement CacheInterface");

        if (context instanceof OnErrorListener)
            mErrorListener = (OnErrorListener) context;
        else
            throw new RuntimeException(context.toString() + " must implement OnErrorListener");

//        if (context instanceof OnListFragmentInteractionListener)
//            mListener = (OnListFragmentInteractionListener) context;
//        else
//            throw new RuntimeException(context.toString() + " must implement OnListFragmentInteractionListener");
    }

    @Override
    public void onDetach() {
        super.onDetach();
//        mListener = null;
        mCache = null;
        mAffiliates = null;
    }

    @Override
    public void onTaskComplete(String response) {
        try {
            JSONObject result = new JSONObject(response);
            if(result.getBoolean("success")){
                if(result.has("affiliates")){
                    JSONArray jAffiliates = result.getJSONArray("affiliates");
                    mCache.updateTime(CacheInterface.CacheType.Affiliates);
                    mAffiliates.clearAffiliates();
                    for(int i = 0; i < jAffiliates.length(); i++) {
                        SOrganization org = new SOrganization(jAffiliates.getJSONObject(i).toString());
                        org.type = SOrganizationType.Affiliate;
                        mAffiliates.addAffiliate(org);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mAffiliates.sortAffiliates();
        mAdapter.notifyDataSetChanged();
        progress.dismiss();
    }

    @Override
    public void onTaskError(String error) {
        mErrorListener.handleError(error);
        progress.dismiss();
    }
}
