package org.shingo.shingoapp.ui.interfaces;

import org.shingo.shingoapp.middle.SEvent.SEvent;

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
}
