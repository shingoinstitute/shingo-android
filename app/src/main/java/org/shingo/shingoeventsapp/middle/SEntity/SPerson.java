package org.shingo.shingoeventsapp.middle.SEntity;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.shingo.shingoeventsapp.middle.SObject;

/**
 * This class is the data holder for
 * events people in the Shingo app.
 * People are defined as one of the following:
 * Speakers, Board Member's,
 * and Academy Members.
 *
 * @author Dustin Homan
 */
public class SPerson extends SEntity implements Comparable<SObject>,Parcelable {

    /**
     * Person's title
     */
    private String title;

    /**
     * Person's company
     */
    private String company;

    /**
     * See {@link SPersonType}
     */
    public SPersonType type;

    public SPerson(){}

    /**
     * Constructor to parse from JSON string
     * @param json JSON {@link String} representing a Person
     */
    public SPerson(String json){
        fromJSON(json);
    }

    public SPerson(String id, String name, String title, String company, SPersonType type){
        super(id,name, "", null);
        this.title = title;
        this.company = company;
        this.type = type;
    }

    /**
     * @param id Salesforce Id
     * @param name Person's name
     * @param title Person's title
     * @param company Person's company
     * @param summary A brief biography of the Person
     * @param image A profile picture of the Person
     * @param type The Person's type {@link SPersonType}
     */
    @SuppressWarnings("unused")
    public SPerson(String id, String name, String title, String company, String summary,
                   Bitmap image, SPersonType type){
        super(id, name, summary, image);
        this.title = title;
        this.company = company;
        this.type = type;
    }

    /**
     * Setter for {@link #type}
     * @param type {@link String} representing {@link SPersonType}
     */
    @Override
    protected void setTypeFromString(String type){
        this.type = SPersonType.valueOf(type.replace("\\s", ""));
    }

    /**
     * Parse a Person from a JSON string
     * @param json JSON {@link String} representing a Person
     */
    @Override
    public void fromJSON(String json) {
        super.fromJSON(json);
        try {
            JSONObject jsonPerson = new JSONObject(json);
            if(jsonPerson.has("Speaker_Title__c"))
                this.title = (jsonPerson.isNull("Speaker_Title__c") ? null : jsonPerson.getString("Speaker_Title__c"));
            if(jsonPerson.has("Organization__r"))
                this.company = jsonPerson.isNull("Organization__r") ? "" : jsonPerson.getJSONObject("Organization__r").getString("Name");
            if(jsonPerson.has("Session_Speaker_Associations__r"))
                this.type = jsonPerson.isNull("Session_Speaker_Associations__r") ? SPersonType.ConcurrentSpeaker : SPersonType.KeynoteSpeaker;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sorts Person's by their last name
     * @param a {@link SPerson}
     * @return {@link String#compareTo(String)} on the last names of the people
     * @throws ClassCastException
     */
    @Override
    public int compareTo(@NonNull SObject a) throws ClassCastException{
        if(!(a instanceof SPerson)){
            throw new ClassCastException(a.toString() + " is not a SPerson");
        }
        String[] fullName = this.name.split(" ");
        String[] aFullName = a.getName().split(" ");
        int compareLast = fullName[fullName.length - 1].compareTo(aFullName[aFullName.length - 1]);
        if(compareLast == 0){
            return fullName[0].compareTo(aFullName[0]);
        }

        return compareLast;
    }

    /**
     * Getter for {@link #title}
     * @return {@link #title}
     */
    public String getTitle(){
        return title;
    }

    /**
     * Getter for {@link #company}
     * @return {@link #company}
     */
    @SuppressWarnings("unused")
    public String getCompany(){
        return company;
    }

    /**
     * Getter for Person's Detail
     * @return {@link #title}, {@link #company}
     */
    @Override
    public String getDetail() {
        return (title != null ? title + (company != null ? ", " + company : "") : company);
    }

    /**
     * {@link Enum} describing a Person's type
     */
    public enum SPersonType {
        KeynoteSpeaker("Keynote Speaker"),
        ConcurrentSpeaker("Concurrent Speaker"),
        BoardMember("Board Member"),
        AcademyMember("Academy Member"),
        Attendee("Attendee");

        private String type;
        SPersonType(String type){
            this.type = type;
        }

        @Override
        public String toString(){
            return type;
        }
    }


    /**
     * Implementation of {@link Parcelable#describeContents()}
     * @return 0
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Implementation of {@link Parcelable#writeToParcel(Parcel, int)}
     * @param dest {@link Parcel}
     * @param flags {@link int}
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.company);
        dest.writeInt(this.type == null ? -1 : this.type.ordinal());
        dest.writeString(this.imageUrl);
        dest.writeString(this.summary);
        dest.writeString(this.id);
        dest.writeString(this.name);
    }

    /**
     * Constructor from {@link Parcel}
     * @param in {@link Parcel}
     */
    protected SPerson(Parcel in) {
        this.title = in.readString();
        this.company = in.readString();
        int tmpType = in.readInt();
        this.type = tmpType == -1 ? null : SPersonType.values()[tmpType];
        this.imageUrl = in.readString();
        this.summary = in.readString();
        this.id = in.readString();
        this.name = in.readString();
    }

    /**
     * Implementation of {@link android.os.Parcelable.Creator}
     */
    public static final Parcelable.Creator<SPerson> CREATOR = new Parcelable.Creator<SPerson>() {

        /**
         * Implementation of {@link android.os.Parcelable.Creator#createFromParcel(Parcel)}
         * @param source {@link Parcel}
         * @return A Person created from source
         */
        @Override
        public SPerson createFromParcel(Parcel source) {
            return new SPerson(source);
        }

        /**
         * Implementation of {@link android.os.Parcelable.Creator#newArray(int)}
         * @param size Size of the new Person array
         * @return a new Person array
         */
        @Override
        public SPerson[] newArray(int size) {
            return new SPerson[size];
        }
    };
}
