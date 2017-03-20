package org.shingo.shingoeventsapp.ui.interfaces;

import android.graphics.Bitmap;

import org.shingo.shingoeventsapp.middle.SEvent.SEvent;

import java.util.List;

/**
 * Created by dustinehoman on 7/15/16.
 */
public interface EventInterface {
    void addEvent(SEvent event);
    SEvent getEvent(String id);
    List<SEvent> events();
    void clearEvents();
    void sortEvents();
    void addAd(String id, int type, Bitmap ad);
}
