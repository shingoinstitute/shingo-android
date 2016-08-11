package org.shingo.shingoeventsapp.ui.events.viewadapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;
import android.view.ViewGroup;

import org.shingo.shingoeventsapp.middle.SEvent.SVenue;
import org.shingo.shingoeventsapp.ui.events.MapFragment;

import java.util.List;

/**
 * Created by dustinehoman on 7/22/16.
 */


public class MapsPagerAdapter extends FragmentStatePagerAdapter {

    private List<SVenue.VenueMap> mMaps;

    public MapsPagerAdapter(FragmentManager fm, List<SVenue.VenueMap> maps){
        super(fm);
        mMaps = maps;
    }

    @Override
    public Fragment getItem(int position) {
        Log.d(getClass().getName(), "GetItem(" + position + ")");
        return MapFragment.newInstance(mMaps.get(position).getUrl());
    }

    @Override
    public int getCount() {
        return mMaps.size();
    }

    @Override
    public CharSequence getPageTitle(int position){
        return mMaps.get(position).getName();
    }
}
