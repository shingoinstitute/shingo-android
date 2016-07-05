package org.shingo.shingoapp.middle.SEvent;

import android.support.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.shingo.shingoapp.middle.SEntity.SPerson;
import org.shingo.shingoapp.middle.SObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * This class holds data for
 * a Shingo Event.
 *
 * @author Dustin Homan
 */
public class SEvent extends SObject implements Comparable<SObject> {
    private Date start;
    private Date end;
    private String registration;
    private SVenue venue;
    private String displayLocation;
    private String city;
    private String country;
    private String primaryColor;
    private List<SDay> agenda = new ArrayList<>();

    public SEvent(){}

    public SEvent(String id, String name, Date start, Date end, String registration,
                  SVenue venue, String displayLocation, String city, String country, String primaryColor, List<SDay> agenda){
        super(id, name);
        this.start = start;
        this.end = end;
        this.registration = registration;
        this.venue = venue;
        this.displayLocation = displayLocation;
        this.city = city;
        this.country = country;
        this.primaryColor = primaryColor;
        this.agenda = agenda;
    }

    public Date getStart(){
        return start;
    }

    public Date getEnd(){
        return end;
    }

    public String getRegistration(){
        return registration;
    }

    public SVenue getVenue(){
        return venue;
    }

    public List<SDay> getAgenda(){
        return agenda;
    }

    public List<SSession> getSessions(){
        List<SSession> sessions = new ArrayList<>();
        for(SDay day : agenda){
            sessions.addAll(day.getSessions());
        }

        Collections.sort(sessions);

        return sessions;
    }

    public List<SPerson> getSpeakers(){
        List<SPerson> speakers = new ArrayList<>();
        for(SDay day : agenda){
            for(SSession session : day.getSessions()){
                speakers.addAll(session.getSpeakers());
            }
        }

        Collections.sort(speakers);

        return speakers;
    }

    @Override
    public int compareTo(@NonNull SObject a){
        if(!(a instanceof SEvent))
            throw new ClassCastException(a.getName() + " is not a SEvent");

        int compareStart = this.start.compareTo(((SEvent) a).getStart());
        if(compareStart == 0){
            int compareEnd = this.end.compareTo(((SEvent) a).getEnd());
            if(compareEnd == 0)
                return this.name.compareTo(a.getName());
        }

        return compareStart;
    }

    @Override
    public void fromJSON(String json){
        super.fromJSON(json);
        try {
            JSONObject jsonEvent = new JSONObject(json);
            this.start = formatDateString(jsonEvent.getString("Start_Date__c"));
            this.end = formatDateString(jsonEvent.getString("End_Date__c"));
            this.registration = (jsonEvent.getString("Registration_Link__c").equals("null") ? "http://events.shingo.org" : jsonEvent.getString("Registration_Link__c"));
            this.venue = new SVenue();
            this.displayLocation = (jsonEvent.getString("Display_Location__c").equals("null") ? "" : jsonEvent.getString("Display_Location__c"));
            this.city = (jsonEvent.getString("Host_City__c").equals("null") ? "" : jsonEvent.getString("Host_City__c"));
            this.country = (jsonEvent.getString("Host_Country__c").equals("null") ? "" : jsonEvent.getString("Host_Country__c"));
            this.primaryColor = (jsonEvent.getString("Primary_Color__c").equals("null") ? "#640921" : jsonEvent.getString("Primary_Color__c"));
            JSONArray jDays = jsonEvent.getJSONObject("Shingo_Day_Agendas__r").getJSONArray("records");
            for(int i = 0; i < jDays.length(); i++){
                SDay day = new SDay();
                day.fromJSON(jDays.getJSONObject(i).toString());
                agenda.add(day);
            }

            Collections.sort(agenda);
        } catch (JSONException | ParseException e) {
            e.printStackTrace();
        }
    }
}
