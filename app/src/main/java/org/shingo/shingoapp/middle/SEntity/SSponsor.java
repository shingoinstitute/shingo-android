package org.shingo.shingoapp.middle.SEntity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;
import org.shingo.shingoapp.middle.SObject;

import java.io.IOException;
import java.net.URL;

/**
 * This class holds the data for
 * Shingo Event Sponsors.
 *
 * @author Dustin Homan
 */
public class SSponsor extends SOrganization implements Comparable<SObject> {

    private Bitmap banner;
    public SSponsorLevel level;
    public boolean is_banner_loading = false;

    public SSponsor(String id, String name, String summary, String website, String email, String phone, Bitmap image, Bitmap banner, SSponsorLevel level) {
        super(id, name, summary, website, email, phone, image, SOrganizationType.Sponsor);
        this.banner = banner;
        this.level = level;
    }

    public Bitmap getBanner(){
        return banner;
    }

    @Override
    public int compareTo(@NonNull SObject a){
        if(!(a instanceof SSponsor))
            throw new ClassCastException(a.getName() + " is not a SSponsor");
        int enumCompare = ((SSponsor) a).level.compareTo(this.level);
        if(enumCompare == 0)
            return this.name.compareTo(a.getName());
        return enumCompare;
    }

    @Override
    public void fromJSON(String json){
        super.fromJSON(json);
        try {
            JSONObject jSponsor = new JSONObject(json);
            getBannerFromURL(jSponsor.getString("Banner"));
            try {
                this.level = SSponsorLevel.valueOf(jSponsor.getString("Level"));
            } catch (IllegalArgumentException ex){
                this.level = SSponsorLevel.Friend;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void getBannerFromURL(final String urlString){
        if(!urlString.contains("http"))
            return;
        is_banner_loading = true;
        Thread thread = new Thread(){
            @Override
            public void run(){
                try {
                    URL url = new URL(urlString);
                    banner = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                    is_banner_loading = false;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }

    public enum SSponsorLevel{
        Friend,
        Supporter,
        Benefactor,
        Champion,
        President
    }
}
