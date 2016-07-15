package org.shingo.shingoapp.middle.SEntity;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;
import org.shingo.shingoapp.middle.SObject;

/**
 * This class is the data holder for
 * all organizations in the Shingo app.
 * Organizations are defined as one of the following:
 * Affiliate, Exhibitor, Sponsor, or Recipient
 *
 * @author Dustin Homan
 */
public class SOrganization extends SEntity implements Comparable<SObject>, Parcelable {

    /**
     * URL to an organization's website
     */
    protected String website;

    /**
     * Organization's public contact email address
     */
    protected String email;

    /**
     * Organization's public contact phone number
     */
    protected String phone;

    /**
     * See {@link SOrganizationType}
     */
    public SOrganizationType type;

    public SOrganization(){}

    /**
     * Constructor to parse from JSON string
     * @param json JSON {@link String} representing an Organization
     */
    public SOrganization(String json){
        fromJSON(json);
    }

    /**
     * @param id Salesforce id
     * @param name Name of the organization
     * @param summary A brief summary of the organization
     * @param website The URL to the organization's website
     * @param email A public email contact of the organization
     * @param phone A public phone contact of the organization
     * @param image The logo or banner of the organization
     * @param type The organization's type {@link SOrganizationType}
     */
    public SOrganization(String id, String name, String summary, String website, String email,
                         String phone, Bitmap image, SOrganizationType type){
        super(id, name, summary, image);
        this.website = website;
        this.email = email;
        this.phone = phone;
        this.type = type;
    }

    /**
     * A method to parse an Organization from a JSON string.
     * @param json JSON string representing an Organization
     */
    @Override
    public void fromJSON(String json) {
        super.fromJSON(json);
        try {
            JSONObject jOrganization = new JSONObject(json);
            this.website = (jOrganization.getString("Website").equals("null") ? null : jOrganization.getString("Website"));
            this.email = (jOrganization.getString("Email").equals("null") ? null : jOrganization.getString("Email"));
            this.phone = (jOrganization.getString("Phone").equals("null") ? null : jOrganization.getString("Phone"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Getter for an Organization's detail
     * @return {@link #website}
     */
    @Override
    public String getDetail() {
        return this.website;
    }

    /**
     * Setter for {@link #type} from {@link String}
     * @param type {@link String} representation of Organization type
     */
    @Override
    protected void setTypeFromString(String type) {
        this.type = SOrganizationType.valueOf(type.replace("\\s",""));
    }

    /**
     * Getter for {@link #website}
     * @return {@link #website}
     */
    @SuppressWarnings("unused")
    public String getWebsite(){
        return website;
    }

    /**
     * Getter for {@link #email}
     * @return {@link #email}
     */
    @SuppressWarnings("unused")
    public String getEmail(){
        return email;
    }

    /**
     * Getter for {@link #phone}
     * @return {@link #phone}
     */
    @SuppressWarnings("unused")
    public String getPhone(){
        return phone;
    }

    /**
     * {@link Enum} describing the type of an Organization
     */
    public enum SOrganizationType{
        Affiliate,
        Exhibitor,
        Sponsor,
        None
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
        dest.writeString(this.website);
        dest.writeString(this.email);
        dest.writeString(this.phone);
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
    protected SOrganization(Parcel in) {
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

    /**
     * Implementation of {@link android.os.Parcelable.Creator}
     */
    public static final Creator<SOrganization> CREATOR = new Creator<SOrganization>() {

        /**
         * Implementation of {@link android.os.Parcelable.Creator#createFromParcel(Parcel)}
         * @param source {@link Parcel}
         * @return An Organization created from source
         */
        @Override
        public SOrganization createFromParcel(Parcel source) {
            return new SOrganization(source);
        }

        /**
         * Implementation of {@link android.os.Parcelable.Creator#newArray(int)}
         * @param size Size of the new Organization array
         * @return a new Organization array
         */
        @Override
        public SOrganization[] newArray(int size) {
            return new SOrganization[size];
        }
    };

}
