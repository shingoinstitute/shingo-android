package org.shingo.shingoapp.middle.SEntity;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.shingo.shingoapp.middle.SObject;
import org.shingo.shingoapp.R;

/**
 * This class is the data holder for
 * all people in the Shingo app.
 * People are defined as one of the following:
 * Speakers, Board Member's,
 * and Academy Members.
 *
 * @author Dustin Homan
 */
public class SPerson extends SEntity implements Comparable<SObject>,Parcelable {

    private String title;
    private String company;
    public SPersonType type;

    public SPerson(){}

    public SPerson(String id, String name, String title, String company, String summary,
                   Bitmap image, SPersonType type){
        super(id, name, summary, image);
        this.title = title;
        this.company = company;
        this.type = type;
    }

    public SPerson(String id) {
        this.id = id;
    }

    @Override
    protected void getTypeFromString(String type){
        switch (type){
            case "Keynote Speaker":
            case "KeynoteSpeaker":
                this.type = SPersonType.KeynoteSpeaker;
            case "Board Member":
            case "BoardMember":
                this.type = SPersonType.BoardMember;
            case "Academy Member":
            case "AcademyMember":
                this.type = SPersonType.AcademyMember;
            case "Concurrent Speaker":
            case "ConcurrentSpeaker":
            default:
                this.type = SPersonType.ConcurrentSpeaker;
        }
    }

    @Override
    public void fromJSON(String json) {
        super.fromJSON(json);
        try {
            JSONObject jPerson = new JSONObject(json);
            title = (jPerson.isNull("Speaker_Title__c") ? null : jPerson.getString("Speaker_Title__c"));
            if(!jPerson.isNull("Organization__r"))
                company = jPerson.getJSONObject("Organization__r").getString("Name");
            else
                company = "";
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int compareTo(@NonNull SObject a) throws ClassCastException{
        if(!(a instanceof SPerson)){
            throw new ClassCastException(a.toString() + " is not a SPerson");
        }
        String[] fullName = this.name.split(" ");
        String[] aFullName = a.getName().split(" ");
        int compareLast = fullName[fullName.length - 1].compareTo(aFullName[aFullName.length - 1]);
        if(compareLast == 0){
            return fullName[0].compareTo(aFullName[0]);
        }

        return compareLast;
    }

    public String getTitle(){
        return title;
    }

    public String getCompany(){
        return company;
    }

    @Override
    public String getDetail() {
        return (title != null ? title + (company != null ? ", " + company : "") : company);
    }

    @Override
    public View getContent(Context context) {
        View contentView = View.inflate(context, R.layout.sperson_content_view, null);
        ((TextView) contentView).setText(Html.fromHtml(summary));
        return contentView;
    }

    public enum SPersonType {
        KeynoteSpeaker("Keynote Speaker"),
        ConcurrentSpeaker("Concurrent Speaker"),
        BoardMember("Board Member"),
        AcademyMember("Academy Member");

        private String type;
        SPersonType(String type){
            this.type = type;
        }

        @Override
        public String toString(){
            return type;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.company);
        dest.writeInt(this.type == null ? -1 : this.type.ordinal());
        dest.writeString(this.imageUrl);
        dest.writeString(this.summary);
        dest.writeString(this.id);
        dest.writeString(this.name);
    }

    protected SPerson(Parcel in) {
        this.title = in.readString();
        this.company = in.readString();
        int tmpType = in.readInt();
        this.type = tmpType == -1 ? null : SPersonType.values()[tmpType];
        this.imageUrl = in.readString();
        this.summary = in.readString();
        this.id = in.readString();
        this.name = in.readString();
    }

    public static final Parcelable.Creator<SPerson> CREATOR = new Parcelable.Creator<SPerson>() {
        @Override
        public SPerson createFromParcel(Parcel source) {
            return new SPerson(source);
        }

        @Override
        public SPerson[] newArray(int size) {
            return new SPerson[size];
        }
    };
}
