package org.shingo.shingoeventsapp.ui.events;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import org.shingo.shingoeventsapp.R;
import org.shingo.shingoeventsapp.middle.SEvent.SVenue;
import org.shingo.shingoeventsapp.ui.events.viewadapters.MapsPagerAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MapListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapListFragment extends Fragment {
    private static final String ARG_VENUE = "venue";

    private SVenue mVenue;
    private MapsPagerAdapter mMapsPagerAdapter;
    private ViewPager mViewPager;


    public MapListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param venue Parameter 1.
     * @return A new instance of fragment MapListFragment.
     */
    public static MapListFragment newInstance(SVenue venue) {
        MapListFragment fragment = new MapListFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_VENUE, venue);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            mVenue = getArguments().getParcelable(ARG_VENUE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_map_list, container, false);
        mMapsPagerAdapter = new MapsPagerAdapter(getActivity().getSupportFragmentManager(), mVenue.getMaps());
        mViewPager = (ViewPager) view;
        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){
            @Override
            public void onPageSelected(int position){
                mViewPager.setCurrentItem(position, true);
            }
        });
        mViewPager.setAdapter(mMapsPagerAdapter);

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
}
