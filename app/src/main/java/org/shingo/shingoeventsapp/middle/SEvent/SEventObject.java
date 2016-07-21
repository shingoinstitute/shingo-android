package org.shingo.shingoeventsapp.middle.SEvent;

import org.shingo.shingoeventsapp.middle.SObject;

/**
 * Created by dustinehoman on 7/20/16.
 */
public abstract class SEventObject extends SObject {
    SEventObject(){super();}
    SEventObject(String id, String name){super(id,name);}
    public abstract String getDetail();
}
