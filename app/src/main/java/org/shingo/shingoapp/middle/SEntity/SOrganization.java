package org.shingo.shingoapp.middle.SEntity;

import android.content.Context;
import android.graphics.Bitmap;
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
public class SOrganization extends SEntity implements Comparable<SObject> {

    protected String website;
    protected String email;
    protected String phone;
    public SOrganizationType type;

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
}
