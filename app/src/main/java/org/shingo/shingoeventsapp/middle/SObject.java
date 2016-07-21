package org.shingo.shingoeventsapp.middle;

import android.support.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * This is the base class for events Shingo SF Objects.
 *
 * @author Dustin Homan
 */
public abstract class SObject implements Comparable<SObject> {

    /**
     * Salesforce Id
     */
    protected String id;

    /**
     * Display name of the object
     */
    protected String name;

    public SObject(){}

    /**
     * @param id Salesforce id
     * @param name Object's Name
     */
    public SObject(String id, String name){
        this.id = id;
        this.name = name;
    }

    /**
     * Getter for {@link #id}
     * @return {@link #id}
     */
    public String getId(){
        return id;
    }

    /**
     * Getter for {@link #name}
     * @return {@link #name}
     */
    public String getName(){
        return name;
    }

    /**
     * Default {@link SObject} behavior is to print {@link #name}
     * @return {@link #name}
     */
    @Override
    public String toString(){
        return name;
    }

    /**
     * Compares {@link #id}s of two {@link SObject}s for equality
     * @param a {@link SObject}
     * @return {@link #id#equals(Object)}
     */
    @Override
    public boolean equals(@NonNull Object a) {
        return a instanceof SObject && this.id.equals(((SObject) a).getId());

    }

    /**
     * Compares {@link #name}s of two {@link SObject}s for purposes of sorting
     * @param a {@link SObject}
     * @return {@link #name#compareTo(SObject)}
     */
    @Override
    public int compareTo(@NonNull SObject a){
        return this.name.compareTo(a.getName());
    }

    /**
     * Parses an Object from a JSON string
     * @param json JSON {@link String} representing an Object
     */
    public void fromJSON(String json){
        try {
            JSONObject jsonObject = new JSONObject(json);
            if(jsonObject.isNull("Id"))
                throw new JSONException("Null 'Id' for SObject. All SObjects require an 'Id'.");
            if(jsonObject.has("Id"))
                this.id = jsonObject.getString("Id");
            if(jsonObject.has("Name"))
                this.name = jsonObject.getString("Name");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Parses a {@link String} into a {@link Date}
     * @param dateTime {@link String} representing a date and time of format "yyyy-MM-dd'T'hh:mm:ss.SSS"
     * @return {@link Date}
     * @throws ParseException
     */
    protected Date parseDateTimeString(String dateTime) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSS", Locale.getDefault());
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        return format.parse(dateTime);
    }

    /**
     * Parses a {@link String} into a {@link Date}
     * @param date {@link String} representing a date of format "yyyy-MM-dd"
     * @return {@link Date}
     * @throws ParseException
     */
    protected Date parseDateString(String date) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        return format.parse(date);
    }

}
