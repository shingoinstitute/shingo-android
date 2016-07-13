package org.shingo.shingoapp.middle.SEntity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeParcelable(this.banner, flags);
        dest.writeInt(this.level == null ? -1 : this.level.ordinal());
        dest.writeByte(this.is_banner_loading ? (byte) 1 : (byte) 0);
        dest.writeString(this.website);
        dest.writeString(this.email);
        dest.writeString(this.phone);
        dest.writeInt(this.type == null ? -1 : this.type.ordinal());
        dest.writeParcelable(this.image, flags);
        dest.writeString(this.imageUrl);
        dest.writeString(this.summary);
        dest.writeString(this.id);
        dest.writeString(this.name);
    }

    protected SSponsor(Parcel in) {
        super(in);
        this.banner = in.readParcelable(Bitmap.class.getClassLoader());
        int tmpLevel = in.readInt();
        this.level = tmpLevel == -1 ? null : SSponsorLevel.values()[tmpLevel];
        this.is_banner_loading = in.readByte() != 0;
        this.website = in.readString();
        this.email = in.readString();
        this.phone = in.readString();
        int tmpType = in.readInt();
        this.type = tmpType == -1 ? null : SOrganizationType.values()[tmpType];
        this.image = in.readParcelable(Bitmap.class.getClassLoader());
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
