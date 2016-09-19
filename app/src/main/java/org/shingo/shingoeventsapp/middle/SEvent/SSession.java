package org.shingo.shingoeventsapp.middle.SEvent;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.shingo.shingoeventsapp.middle.SEntity.SPerson;
import org.shingo.shingoeventsapp.middle.SObject;

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
public class SSession extends SEventObject implements Comparable<SObject>,Parcelable {
    private String summary;
    private SRoom room;
    private Date start;
    private Date end;
    /**
        A list of Salesforce Ids for {@link SPerson}s
     */
    private List<String> speakers = new ArrayList<>();
    public SSessionType type;

    public SSession(){}

    @Override
    public String getDetail() {
        return getTimeString();
    }

    public SSession(String id){
        this.id = id;
    }

    @SuppressWarnings("unused")
    public SSession(String id, String name, String summary, SRoom room, Date start, Date end,
                    List<String> speakers, SSessionType type){
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
            this.name = (jsonSession.isNull("Session_Display_Name__c") ? "Session" : jsonSession.getString("Session_Display_Name__c"));
            if(jsonSession.has("Summary__c"))
                this.summary = jsonSession.isNull("Summary__c") ? "Summary coming soon!" : jsonSession.getString("Summary__c");
            if(jsonSession.has("Room__r") && !jsonSession.isNull("Room__r")) {
                this.room = new SRoom();
                this.room.fromJSON(jsonSession.getJSONObject("Room__r").toString());
            }
            if(jsonSession.has("Start_Date_Time__c"))
                this.start = parseDateTimeString(jsonSession.getString("Start_Date_Time__c"));
            if(jsonSession.has("End_Date_Time__c"))
                this.end = parseDateTimeString(jsonSession.getString("End_Date_Time__c"));
            if(jsonSession.has("Session_Speaker_Associations__r") && !jsonSession.isNull("Session_Speaker_Associations__r")){
                JSONArray jSpeakers = jsonSession.getJSONObject("Session_Speaker_Associations__r").getJSONArray("records");
                for(int i = 0; i < jSpeakers.length(); i++)
                    speakers.add(jSpeakers.getJSONObject(i).getJSONObject("Speaker__r").getString("Id"));
            }
            if(jsonSession.has("Session_Type__c"))
                this.type = (jsonSession.isNull("Session_Type__c") ? SSessionType.Concurrent : SSessionType.valueOf(jsonSession.getString("Session_Type__c").replaceAll("\\s", "").replaceAll("-", "")));
        } catch (JSONException | ParseException e) {
            e.printStackTrace();
        }
    }

    public String getSummary(){
        return summary;
    }

    @SuppressWarnings("unused")
    public SRoom getRoom(){
        return room;
    }

    public Date getStart(){
        return start;
    }

    public Date getEnd(){
        return end;
    }

    public List<String> getSpeakers(){
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
        DateFormat formatter = SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT);
        formatter.setTimeZone(TimeZone.getTimeZone("America/New_York"));
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
        Social("Social"),
        Break("Break");

        private String type;
        SSessionType(String type){
            this.type = type;
        }

        @Override
        public String toString(){
            return type;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.summary);
        dest.writeParcelable(this.room, flags);
        dest.writeLong(this.start != null ? this.start.getTime() : -1);
        dest.writeLong(this.end != null ? this.end.getTime() : -1);
        dest.writeStringList(this.speakers);
        dest.writeInt(this.type == null ? -1 : this.type.ordinal());
        dest.writeString(this.id);
        dest.writeString(this.name);
    }

    protected SSession(Parcel in) {
        this.summary = in.readString();
        this.room = in.readParcelable(SRoom.class.getClassLoader());
        long tmpStart = in.readLong();
        this.start = tmpStart == -1 ? null : new Date(tmpStart);
        long tmpEnd = in.readLong();
        this.end = tmpEnd == -1 ? null : new Date(tmpEnd);
        this.speakers = in.createStringArrayList();
        int tmpType = in.readInt();
        this.type = tmpType == -1 ? null : SSessionType.values()[tmpType];
        this.id = in.readString();
        this.name = in.readString();
    }

    public static final Parcelable.Creator<SSession> CREATOR = new Parcelable.Creator<SSession>() {
        @Override
        public SSession createFromParcel(Parcel source) {
            return new SSession(source);
        }

        @Override
        public SSession[] newArray(int size) {
            return new SSession[size];
        }
    };
}
