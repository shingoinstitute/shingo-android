package org.shingo.shingoeventsapp.middle.SEvent;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;
import org.shingo.shingoeventsapp.middle.SObject;

/**
 * This class holds the data
 * for Rooms at a venue.
 *
 * @author Dustin Homan
 */
public class SRoom extends SEventObject implements Comparable<SObject>,Parcelable {
    private double[] location = new double[2];
    private int floor;
    private String venueId;

    public SRoom(){}

    @Override
    public String getDetail() {
        return "";
    }

    public String getVenueId() {
        return venueId;
    }

    public void setVenueId(String venueId) {
        this.venueId = venueId;
    }

    @SuppressWarnings("unused")
    public SRoom(String id, String name, double[] location, int floor) {
        super(id, name);
        this.location = location;
        this.floor = floor;
    }

    @Override
    public void fromJSON(String json){
        super.fromJSON(json);
        try {
            JSONObject jsonRoom = new JSONObject(json);
            if(jsonRoom.has("Floor__c"))
                this.floor = jsonRoom.isNull("Floor__c") ? 0 : jsonRoom.getInt("Floor__c");
            if(jsonRoom.has("Map_X_Coordinate__c") && jsonRoom.has("Map_Y_Coordinate__c")){
                double x = jsonRoom.isNull("Map_X_Coordinate__c") ? 0 : jsonRoom.getDouble("Map_X_Coordinate__c");
                double y = jsonRoom.isNull("Map_Y_Coordinate__c") ? 0 : jsonRoom.getDouble("Map_Y_Coordinate__c");
                this.location[0] = x;
                this.location[1] = y;
            }
            if(jsonRoom.has("Associated_Venue__r")){
                this.venueId = jsonRoom.getJSONObject("Associated_Venue__r").getString("Id");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unused")
    public double[] getLocation(){
        return location;
    }

    @SuppressWarnings("unused")
    public int getFloor(){
        return floor;
    }

    @Override
    public int compareTo(@NonNull SObject a){
        if(!(a instanceof SRoom))
            throw new ClassCastException(a.getName() + " is not a SRoom");
        int floorCompare = this.floor - ((SRoom) a).floor;
        if(floorCompare == 0){
            return this.name.compareTo(a.getName());
        }

        return floorCompare;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDoubleArray(this.location);
        dest.writeInt(this.floor);
        dest.writeString(this.id);
        dest.writeString(this.name);
    }

    protected SRoom(Parcel in) {
        this.location = in.createDoubleArray();
        this.floor = in.readInt();
        this.id = in.readString();
        this.name = in.readString();
    }

    public static final Parcelable.Creator<SRoom> CREATOR = new Parcelable.Creator<SRoom>() {
        @Override
        public SRoom createFromParcel(Parcel source) {
            return new SRoom(source);
        }

        @Override
        public SRoom[] newArray(int size) {
            return new SRoom[size];
        }
    };
}
