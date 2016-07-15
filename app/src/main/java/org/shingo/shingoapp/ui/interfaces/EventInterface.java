package org.shingo.shingoapp.ui.interfaces;

import org.shingo.shingoapp.middle.SEvent.SEvent;

import java.util.List;

/**
 * Created by dustinehoman on 7/15/16.
 */
public interface EventInterface {
    void addEvent(SEvent event);
    SEvent get(String id);
    List<SEvent> all();
    void clear();
    void sort();
}
