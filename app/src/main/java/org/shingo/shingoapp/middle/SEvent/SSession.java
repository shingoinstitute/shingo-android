package org.shingo.shingoapp.middle.SEvent;

import android.support.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.shingo.shingoapp.middle.SEntity.SPerson;
import org.shingo.shingoapp.middle.SObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * This class holds data for
 * Shingo Event Sessions.
 *
 * @author Dustin Homan
 */
public class SSession extends SObject implements Comparable<SObject> {
    private String summary;
    private SRoom room;
    private Date start;
    private Date end;
    private List<SPerson> speakers = new ArrayList<>();
    public SSessionType type;

    public SSession(){}

    public SSession(String id, String name, String summary, SRoom room, Date start, Date end,
                    List<SPerson> speakers, SSessionType type){
        super(id, name);
        this.summary = summary;
        this.room = room;
        this.start = start;
        this.end = end;
        this.speakers = speakers;
        this.type = type;
    }

    @Override
    public void fromJSON(String json){
        super.fromJSON(json);
        try {
            JSONObject jsonSession = new JSONObject(json);
            this.name = (jsonSession.getString("Session_Display_Name__c").equals("null") ? "Session" : jsonSession.getString("Session_Display_Name__c"));
            if(jsonSession.has("Summary__c")) {
                this.summary = jsonSession.getString("Summary__c").equals("null") ? "Summary coming soon!" : jsonSession.getString("Summary__c");
            }
            if(jsonSession.has("Room__r")) {
                this.room = new SRoom();
                this.room.fromJSON(jsonSession.getJSONObject("Room__r").toString());
            }
            this.start = formatDateTimeString(jsonSession.getString("Start_Date_Time__c"));
            this.end = formatDateTimeString(jsonSession.getString("End_Date_Time__c"));
//            JSONArray jSpeakers = jsonSession.getJSONArray("Speakers");
//            for(int i = 0; i < jSpeakers.length(); i++){
//                SPerson speaker = new SPerson();
//                speaker.fromJSON(jSpeakers.getJSONObject(i).toString());
//                speakers.add(speaker);
//            }
            this.type = (jsonSession.getString("Session_Type__c").equals("null") ? SSessionType.Concurrent : SSessionType.valueOf(jsonSession.getString("Session_Type__c").replaceAll("\\s", "")));
        } catch (JSONException | ParseException e) {
            e.printStackTrace();
        }
    }

    public String getSummary(){
        return summary;
    }

    public SRoom getRoom(){
        return room;
    }

    public Date getStart(){
        return start;
    }

    public Date getEnd(){
        return end;
    }

    public List<SPerson> getSpeakers(){
        return speakers;
    }

    @Override
    public int compareTo(@NonNull SObject a){
        if(!(a instanceof SSession))
            throw new ClassCastException(a.getName() + " is not a SSession");
        int compareStart = this.start.compareTo(((SSession) a).getStart());

        if(compareStart == 0){
            int compareEnd = this.end.compareTo(((SSession) a).getEnd());
            if(compareEnd == 0){
                int compareType = this.type.compareTo(((SSession) a).type);
                if(compareType == 0)
                    return this.name.compareTo(a.getName());
                return compareType;
            }
            return compareEnd;
        }

        return compareStart;
    }

    public String getTimeString() {
        String timeString = "";
        DateFormat formatter = SimpleDateFormat.getTimeInstance();
        timeString += formatter.format(start) + " - " + formatter.format(end);
        return timeString;
    }

    public enum SSessionType{
        Keynote("Keynote"),
        Concurrent("Concurrent"),
        Offsite("Off-site"),
        Tour("Tour"),
        Meal("Meal"),
        HalfDayWorkshop("Half Day Workshop"),
        FullDayWorkshop("Full Day Workshop"),
        Social("Social");

        private String type;
        SSessionType(String type){
            this.type = type;
        }

        @Override
        public String toString(){
            return type;
        }
    }
}
