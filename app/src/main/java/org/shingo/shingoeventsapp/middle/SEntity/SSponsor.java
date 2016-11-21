package org.shingo.shingoeventsapp.middle.SEntity;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.support.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;
import org.shingo.shingoeventsapp.middle.SObject;

/**
 * This class holds the data for
 * Shingo Event Sponsors.
 *
 * @author Dustin Homan
 */
public class SSponsor extends SOrganization implements Comparable<SObject> {

    private Bitmap banner;
    private Bitmap splash;
    private String bannerUrl;
    private String splashUrl;
    public SSponsorLevel level;

    public SSponsor(){}

    @SuppressWarnings("unused")
    public SSponsor(String id, String name, String summary, String website, String email, String phone, Bitmap image, Bitmap banner, SSponsorLevel level) {
        super(id, name, summary, website, email, phone, image, SOrganizationType.Sponsor);
        this.banner = banner;
        this.level = level;
    }

    @SuppressWarnings("unused")
    public Bitmap getBanner(){
        return banner;
    }

    @SuppressWarnings("unused")
    public void setBanner(Bitmap banner) {
        this.banner = banner;
    }

    @SuppressWarnings("unused")
    public Bitmap getSplash() {
        return splash;
    }

    @SuppressWarnings("unused")
    public void setSplash(Bitmap splash) {
        this.splash = splash;
    }

    @SuppressWarnings("unused")
    public String getSplashUrl() {
        return splashUrl;
    }

    @SuppressWarnings("unused")
    public String getBannerUrl() {
        return bannerUrl;
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
        try {
            JSONObject jSponsor = new JSONObject(json);
            super.fromJSON(jSponsor.getJSONObject("Organization__r").toString());
            if(jSponsor.has("Sponsor_Level__c"))
                this.level = jSponsor.isNull("Sponsor_Level__c") ? SSponsorLevel.Friend : SSponsorLevel.valueOf(jSponsor.getString("Sponsor_Level__c"));
            else
                this.level = SSponsorLevel.Friend;
            if(jSponsor.has("Banner_URL__c"))
                this.bannerUrl = jSponsor.isNull("Banner_URL__c") ? "https://placehold.it/350x150" : jSponsor.getString("Banner_URL__c");
            if(jSponsor.has("Splash_Screen_URL__c"))
                this.splashUrl = jSponsor.isNull("Splash_Screen_URL__c") ? "https://placehold.it/350x600" : jSponsor.getString("Splash_Screen_URL__c");
            this.type = SOrganizationType.Sponsor;
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public enum SSponsorLevel{
        Other,
        Friend,
        Supporter,
        Benefactor,
        Champion,
        President
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(this.level == null ? -1 : this.level.ordinal());
        dest.writeString(this.bannerUrl);
        dest.writeString(this.splashUrl);
        dest.writeString(this.website);
        dest.writeString(this.email);
        dest.writeString(this.phone);
        dest.writeInt(this.type == null ? -1 : this.type.ordinal());
        dest.writeString(this.imageUrl);
        dest.writeString(this.summary);
        dest.writeString(this.id);
        dest.writeString(this.name);
    }

    protected SSponsor(Parcel in) {
        super(in);
        int tmpLevel = in.readInt();
        this.level = tmpLevel == -1 ? null : SSponsorLevel.values()[tmpLevel];
        this.bannerUrl = in.readString();
        this.splashUrl = in.readString();
        this.website = in.readString();
        this.email = in.readString();
        this.phone = in.readString();
        int tmpType = in.readInt();
        this.type = tmpType == -1 ? null : SOrganizationType.values()[tmpType];
        this.imageUrl = in.readString();
        this.summary = in.readString();
        this.id = in.readString();
        this.name = in.readString();
    }

    public static final Creator<SSponsor> CREATOR = new Creator<SSponsor>() {
        @Override
        public SSponsor createFromParcel(Parcel source) {
            return new SSponsor(source);
        }

        @Override
        public SSponsor[] newArray(int size) {
            return new SSponsor[size];
        }
    };
}
