package org.shingo.shingoeventsapp.middle.SEvent;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.shingo.shingoeventsapp.middle.SObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * This class holds data fo
 * Shingo Event days.
 *
 * @author Dustin Homan
 */
public class SDay extends SEventObject implements Comparable<SObject>,Parcelable {
    private List<String> sessions = new ArrayList<>();
    private Date date;

    public SDay(){}

    @Override
    public String getDetail() {
        SimpleDateFormat format = new SimpleDateFormat("d MMMM", Locale.getDefault());
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        return format.format(date);
    }

    @SuppressWarnings("unused")
    public SDay(String id, String name, Date date, List<String> sessions){
        super(id, name);
        this.date = date;
        this.sessions = sessions;
    }

    public List<String> getSessions(){
        return sessions;
    }

    private Date getDate(){
        return date;
    }

    @Override
    public void fromJSON(String json){
        super.fromJSON(json);
        try {
            JSONObject jsonDay = new JSONObject(json);
            if(jsonDay.has("Display_Name__c"))
                this.name = jsonDay.isNull("Display_Name__c") ? "" : jsonDay.getString("Display_Name__c");
            if(jsonDay.has("Agenda_Date__c"))
                this.date = parseDateString(jsonDay.getString("Agenda_Date__c"));
            if(jsonDay.has("Shingo_Sessions__r") && !jsonDay.isNull("Shingo_Sessions__r")) {
                JSONArray jSessions = jsonDay.getJSONObject("Shingo_Sessions__r").getJSONArray("records");
                for (int i = 0; i < jSessions.length(); i++)
                    sessions.add(jSessions.getJSONObject(i).getString("Id"));
            }
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringList(this.sessions);
        dest.writeLong(this.date != null ? this.date.getTime() : -1);
        dest.writeString(this.id);
        dest.writeString(this.name);
    }

    private SDay(Parcel in) {
        this.sessions = in.createStringArrayList();
        long tmpDate = in.readLong();
        this.date = tmpDate == -1 ? null : new Date(tmpDate);
        this.id = in.readString();
        this.name = in.readString();
    }

    public static final Parcelable.Creator<SDay> CREATOR = new Parcelable.Creator<SDay>() {
        @Override
        public SDay createFromParcel(Parcel source) {
            return new SDay(source);
        }

        @Override
        public SDay[] newArray(int size) {
            return new SDay[size];
        }
    };
}
