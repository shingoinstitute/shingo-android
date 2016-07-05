package org.shingo.shingoapp.middle.SEvent;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
public class SVenue extends SObject {
    private Location location;
    private List<VenueMap> maps = new ArrayList<>();
    private List<SRoom> rooms = new ArrayList<>();
    public boolean is_loading = false;

    public SVenue(){}

    public SVenue(String id, String name, double[] location, List<VenueMap> maps, List<SRoom> rooms){
        super(id, name);
        this.location = new Location(location[0], location[1]);
        this.maps = maps;
        this.rooms = rooms;
    }

    public Location getLocation(){
        return location;
    }

    public List<VenueMap> getMaps(){
        return maps;
    }

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

    public class Location{
        private double latitude;
        private double longitude;

        public Location(double latitude, double longitude){
            this.latitude = latitude;
            this.longitude = longitude;
        }

        public double getLatitude(){
            return latitude;
        }

        public double getLongitude(){
            return longitude;
        }
    }
}
