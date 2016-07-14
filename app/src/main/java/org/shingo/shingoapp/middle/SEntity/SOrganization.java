package org.shingo.shingoapp.middle.SEntity;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.shingo.shingoapp.middle.SObject;
import org.shingo.shingoapp.R;

/**
 * This class holds the data
 * for Shingo Organizations.
 *
 * @author Dustin Homan
 */
public class SOrganization extends SEntity implements Comparable<SObject>,Parcelable {

    protected String website;
    protected String email;
    protected String phone;
    public SOrganizationType type;

    public SOrganization(){}

    public SOrganization(String id, String name, String summary, String website, String email,
                         String phone, Bitmap image, SOrganizationType type){
        super(id, name, summary, image);
        this.website = website;
        this.email = email;
        this.phone = phone;
        this.type = type;
    }

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

    @Override
    public String getDetail() {
        return this.website;
    }

    @Override
    public View getContent(Context context) {
        View contentView = View.inflate(context, R.layout.sorganization_content_view, null);
        ((TextView)contentView.findViewById(R.id.sorganization_website)).setText(website);
        ((TextView)contentView.findViewById(R.id.sorganization_email)).setText(email);
        ((TextView)contentView.findViewById(R.id.sorganization_summary)).setText(Html.fromHtml(summary));

        return contentView;
    }

    @Override
    protected void getTypeFromString(String type) {
        switch (type){
            case "Affiliate":
                this.type = SOrganizationType.Affiliate;
                break;
            case "Exhibitor":
                this.type = SOrganizationType.Exhibitor;
                break;
            case "Sponsor":
                this.type = SOrganizationType.Sponsor;
                break;
            default:
                this.type = SOrganizationType.None;
        }
    }

    public String getWebsite(){
        return website;
    }

    public String getEmail(){
        return email;
    }

    public String getPhone(){
        return phone;
    }

    public enum SOrganizationType{
        Affiliate,
        Exhibitor,
        Sponsor,
        None
    }

    @Override
    public int describeContents() {
        return 0;
    }

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

    public static final Creator<SOrganization> CREATOR = new Creator<SOrganization>() {
        @Override
        public SOrganization createFromParcel(Parcel source) {
            return new SOrganization(source);
        }

        @Override
        public SOrganization[] newArray(int size) {
            return new SOrganization[size];
        }
    };

}
