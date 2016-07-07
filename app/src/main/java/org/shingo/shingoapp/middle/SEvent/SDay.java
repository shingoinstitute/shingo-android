package org.shingo.shingoapp.middle.SEvent;

import android.support.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.shingo.shingoapp.middle.SObject;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

/**
 * This class holds data fo
 * Shingo Event days.
 *
 * @author Dustin Homan
 */
public class SDay extends SObject implements Comparable<SObject> {
    private List<SSession> sessions;
    private Date date;

    public SDay(){}
    public SDay(String id, String name, Date date, List<SSession> sessions){
        super(id, name);
        this.date = date;
        this.sessions = sessions;
    }

    public List<SSession> getSessions(){
        return sessions;
    }

    public Date getDate(){
        return date;
    }

    @Override
    public void fromJSON(String json){
        super.fromJSON(json);
        try {
            JSONObject jsonDay = new JSONObject(json);
            this.name = jsonDay.getString("Display_Name__c");
            this.date = formatDateString(jsonDay.getString("Agenda_Date__c"));
//            JSONArray jSessions = jsonDay.getJSONArray("Sessions");
//            for(int i = 0; i < jSessions.length(); i++){
//                SSession session = new SSession();
//                session.fromJSON(jSessions.getJSONObject(i).toString());
//                sessions.add(session);
//            }
        } catch (JSONException | ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int compareTo(@NonNull SObject a){
        if(!(a instanceof SDay))
            throw new ClassCastException(a.getName() + " is not a SDay");
        if(((SDay) a).getDate() == null){
            return 0;
        }
        int compareDate = this.date.compareTo(((SDay) a).getDate());
        if(compareDate == 0)
            return this.name.compareTo(a.getName());

        return compareDate;
    }

}
