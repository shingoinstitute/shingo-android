package org.shingo.shingoeventsapp.ui.interfaces;

import android.support.v4.app.Fragment;

/**
 * Created by dustinehoman on 7/15/16.
 */
public interface NavigationInterface {
    void navigateToId(int id);
    void changeFragment(Fragment fragment);
}
