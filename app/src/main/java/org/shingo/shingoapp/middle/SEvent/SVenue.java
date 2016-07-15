package org.shingo.shingoapp.middle.SEvent;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.shingo.shingoapp.middle.SObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * This class holds data for
 * a Shingo Event venue.
 *
 * @author Dustin Homan
 */
public class SVenue extends SObject implements Parcelable {
    private Location location;
    private List<VenueMap> maps = new ArrayList<>();
    private List<SRoom> rooms = new ArrayList<>();
    public boolean is_loading = false;

    public SVenue(){}

    @SuppressWarnings("unused")
    public SVenue(String id, String name, double[] location, List<VenueMap> maps, List<SRoom> rooms){
        super(id, name);
        this.location = new Location(location[0], location[1]);
        this.maps = maps;
        this.rooms = rooms;
    }

    @SuppressWarnings("unused")
    public Location getLocation(){
        return location;
    }

    @SuppressWarnings("unused")
    public List<VenueMap> getMaps(){
        return maps;
    }

    @SuppressWarnings("unused")
    public List<SRoom> getRooms(){
        return rooms;
    }

    @Override
    public void fromJSON(String json){
        super.fromJSON(json);
        try {
            JSONObject jsonVenue = new JSONObject(json);
            this.location = new Location(jsonVenue.getDouble("Latitude"), jsonVenue.getDouble("Longitude"));
            JSONArray jMaps = jsonVenue.getJSONArray("Maps");
            for(int i = 0; i < jMaps.length(); i++){
                JSONObject jMap = jMaps.getJSONObject(i);
                getMap(jMap.getString("Name"), jMap.getString("Url"), jMap.getInt("Order"));
            }
            JSONArray jRooms = jsonVenue.getJSONArray(("Rooms"));
            for(int i = 0; i < jRooms.length(); i++){
                SRoom room = new SRoom();
                room.fromJSON(jRooms.getJSONObject(i).toString());
                rooms.add(room);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getMap(final String urlString, final String name, final int order){
        is_loading = true;
        Thread thread = new Thread(){
            @Override
            public void run(){
                try {
                    URL url = new URL(urlString);
                    maps.add(new VenueMap(name, BitmapFactory.decodeStream(url.openConnection().getInputStream()), order));
                    is_loading = false;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }

    public class VenueMap implements Comparable<VenueMap>{
        private String name;
        private Bitmap map;
        public int order = 0;

        public VenueMap(String name, Bitmap map, int order){
            this.name = name;
            this.map = map;
            this.order = order;
        }

        public String getName(){
            return name;
        }

        @SuppressWarnings("unused")
        public Bitmap getMap(){
            return map;
        }

        @Override
        public int compareTo(@NonNull VenueMap a) {
            int compare = this.order - a.order;
            if(compare == 0){
                return this.name.compareTo(a.getName());
            }

            return compare;
        }
    }

    public static class Location implements Parcelable {
        private double latitude;
        private double longitude;

        public Location(double latitude, double longitude){
            this.latitude = latitude;
            this.longitude = longitude;
        }

        @SuppressWarnings("unused")
        public double getLatitude(){
            return latitude;
        }

        @SuppressWarnings("unused")
        public double getLongitude(){
            return longitude;
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
    }

    protected SVenue(Parcel in) {
        this.location = in.readParcelable(Location.class.getClassLoader());
        this.maps = new ArrayList<>();
        in.readList(this.maps, VenueMap.class.getClassLoader());
        this.rooms = in.createTypedArrayList(SRoom.CREATOR);
        this.is_loading = in.readByte() != 0;
        this.id = in.readString();
        this.name = in.readString();
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
