package org.shingo.shingoeventsapp.ui.interfaces;


/**
 * Created by dustinehoman on 7/19/16.
 */
public interface CacheInterface {
    boolean needsUpdated(CacheType type);
    void updateTime(CacheType type);
    enum CacheType{
        Events,
        Affiliates
    }
}
