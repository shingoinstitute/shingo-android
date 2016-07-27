package org.shingo.shingoeventsapp.middle.SEvent;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class holds data for
 * a Shingo Event venue.
 *
 * @author Dustin Homan
 */
public class SVenue extends SEventObject implements Parcelable {
    private Location location;
    private List<VenueMap> maps = new ArrayList<>();
    private List<SRoom> rooms = new ArrayList<>();
    private String address;
    public boolean is_loading = false;

    public SVenue(){};
    public SVenue(String json){fromJSON(json);}

    @Override
    public String getDetail() {
        return this.address;
    }

    @SuppressWarnings("unused")
    public SVenue(String id, String name, double[] location, List<VenueMap> maps, List<SRoom> rooms){
        super(id, name);
        this.location = new Location(location[0], location[1]);
        this.maps = maps;
        this.rooms = rooms;
    }

    @SuppressWarnings("unused")
    public Location getLocation(){
        return this.location;
    }

    @SuppressWarnings("unused")
    public List<VenueMap> getMaps(){
        return this.maps;
    }

    @SuppressWarnings("unused")
    public List<SRoom> getRooms(){
        return this.rooms;
    }

    @Override
    public void fromJSON(String json){
        super.fromJSON(json);
        try {
            JSONObject jsonVenue = new JSONObject(json);
            if(jsonVenue.has("Venue_Location__c") && !jsonVenue.isNull("Venue_Location__c"))
                this.location = new Location(jsonVenue.getJSONObject("Venue_Location__c").getDouble("latitude"), jsonVenue.getJSONObject("Venue_Location__c").getDouble("longitude"));
            else
                this.location = new Location(41.7493263,-111.8017771);
            if(jsonVenue.has("Maps__r") && !jsonVenue.isNull("Maps__r")) {
                JSONArray jsonMapsArray = jsonVenue.getJSONObject("Maps__r").getJSONArray("records");
                for(int i = 0; i < jsonMapsArray.length(); i++){
                    JSONObject jsonMap = jsonMapsArray.getJSONObject(i);
                    VenueMap map = new VenueMap();
                    map.setName(jsonMap.isNull("Name") ? "Map: " + i : jsonMap.getString("Name"));
                    map.setFloor(jsonMap.isNull("Floor__c") ? 0 : jsonMap.getInt("Floor__c"));
                    map.setUrl(jsonMap.isNull("URL__c") ? "https://placehold.it/1280x1280" : jsonMap.getString("URL__c"));
                    this.maps.add(map);
                }

                Collections.sort(maps);
            }
            if(jsonVenue.has("Shingo_Rooms__r") && !jsonVenue.isNull("Shingo_Rooms__r")){
                JSONArray jRooms = jsonVenue.getJSONObject("Shingo_Rooms__r").getJSONArray("records");
                rooms.clear();
                for(int i = 0; i < jRooms.length(); i++){
                    SRoom room = new SRoom();
                    room.fromJSON(jRooms.getJSONObject(i).toString());
                    room.setVenueId(this.id);
                    rooms.add(room);
                }
            }
            if(jsonVenue.has("Address__c"))
                this.address = jsonVenue.isNull("Address__c") ? "" : jsonVenue.getString("Address__c");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getAddress() {
        return address;
    }

    public static class VenueMap implements Comparable<VenueMap>,Parcelable {
        private String name;
        private Bitmap map;
        private String url;
        private int floor = 0;

        public VenueMap(){}

        public VenueMap(String url){
            this.name = url;
            this.url = url;
        }

        public VenueMap(String name, Bitmap map, int floor){
            this.name = name;
            this.map = map;
            this.floor = floor;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setFloor(int floor) {
            this.floor = floor;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public int getFloor() {
            return floor;
        }

        public String getName(){
            return this.name;
        }

        public String getUrl() {
            return this.url;
        }

        @SuppressWarnings("unused")
        public Bitmap getMap(){
            return this.map;
        }

        public void setMap(Bitmap map) { this.map = map; }

        @Override
        public int compareTo(@NonNull VenueMap a) {
            int compare = this.floor - a.floor;
            if(compare == 0){
                return this.name.compareTo(a.getName());
            }

            return compare;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.name);
            dest.writeParcelable(this.map, flags);
            dest.writeString(this.url);
            dest.writeInt(this.floor);
        }

        protected VenueMap(Parcel in) {
            this.name = in.readString();
            this.map = in.readParcelable(Bitmap.class.getClassLoader());
            this.url = in.readString();
            this.floor = in.readInt();
        }

        public static final Creator<VenueMap> CREATOR = new Creator<VenueMap>() {
            @Override
            public VenueMap createFromParcel(Parcel source) {
                return new VenueMap(source);
            }

            @Override
            public VenueMap[] newArray(int size) {
                return new VenueMap[size];
            }
        };
    }

    public static class Location implements Parcelable {
        private double latitude;
        private double longitude;

        public Location(double latitude, double longitude){
            this.latitude = latitude;
            this.longitude = longitude;
        }

        public double[] getArray(){
            return new double[]{latitude, longitude};
        }

        @SuppressWarnings("unused")
        public double getLatitude(){
            return this.latitude;
        }

        @SuppressWarnings("unused")
        public double getLongitude(){
            return this.longitude;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeDouble(this.latitude);
            dest.writeDouble(this.longitude);
        }

        protected Location(Parcel in) {
            this.latitude = in.readDouble();
            this.longitude = in.readDouble();
        }

        public static final Creator<Location> CREATOR = new Creator<Location>() {
            @Override
            public Location createFromParcel(Parcel source) {
                return new Location(source);
            }

            @Override
            public Location[] newArray(int size) {
                return new Location[size];
            }
        };
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.location, flags);
        dest.writeList(this.maps);
        dest.writeTypedList(this.rooms);
        dest.writeByte(this.is_loading ? (byte) 1 : (byte) 0);
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeString(this.address);
    }

    protected SVenue(Parcel in) {
        this.location = in.readParcelable(Location.class.getClassLoader());
        this.maps = new ArrayList<>();
        in.readList(this.maps, VenueMap.class.getClassLoader());
        this.rooms = in.createTypedArrayList(SRoom.CREATOR);
        this.is_loading = in.readByte() != 0;
        this.id = in.readString();
        this.name = in.readString();
        this.address = in.readString();
    }

    public static final Parcelable.Creator<SVenue> CREATOR = new Parcelable.Creator<SVenue>() {
        @Override
        public SVenue createFromParcel(Parcel source) {
            return new SVenue(source);
        }

        @Override
        public SVenue[] newArray(int size) {
            return new SVenue[size];
        }
    };
}
