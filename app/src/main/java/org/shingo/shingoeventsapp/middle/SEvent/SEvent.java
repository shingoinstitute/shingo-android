package org.shingo.shingoeventsapp.middle.SEvent;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.shingo.shingoeventsapp.middle.SEntity.SOrganization;
import org.shingo.shingoeventsapp.middle.SEntity.SPerson;
import org.shingo.shingoeventsapp.middle.SEntity.SRecipient;
import org.shingo.shingoeventsapp.middle.SEntity.SSponsor;
import org.shingo.shingoeventsapp.middle.SObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * This class holds data for
 * a Shingo Event.
 *
 * @author Dustin Homan
 */
public class SEvent extends SEventObject implements Comparable<SObject>,Parcelable {
    private Date start;
    private Date end;
    private String registration;
    private String displayLocation;
    private String city;
    private String country;
    private String primaryColor;
    private String bannerUrl;
    private String salesText;
    private Bitmap banner;
    private Random random = new Random();
    private List<Bitmap> bannerAds = new ArrayList<>();
    private List<Bitmap> splashAds = new ArrayList<>();
    private List<SDay> agenda = new ArrayList<>();
    private List<SPerson> speakers = new ArrayList<>();
    private List<SPerson> attendees = new ArrayList<>();
    private List<SSession> sessions = new ArrayList<>();
    private List<SOrganization> exhibitors = new ArrayList<>();
    private List<SSponsor> sponsors = new ArrayList<>();
    private List<SRecipient> recipients = new ArrayList<>();
    private List<SVenue> venues = new ArrayList<>();
    private static final long TIME_OUT = TimeUnit.MINUTES.toMillis(15);
    private Map<String, Date> lastDataPull = new HashMap<>();

    public SEvent(){}

    public SEvent(String id, String name, Date start, Date end, String registration,
                  String displayLocation, String city, String country, String primaryColor,
                  List<SDay> agenda, List<SPerson> speakers){
        super(id, name);
        this.start = start;
        this.end = end;
        this.registration = registration;
        this.displayLocation = displayLocation;
        this.city = city;
        this.country = country;
        this.primaryColor = primaryColor;
        this.agenda = agenda;
        this.speakers = speakers;
    }

    public Date getStart(){
        return start;
    }

    public Date getEnd(){
        return end;
    }

    public String getRegistration(){
        return registration;
    }

    public String getDisplayLocation() {
        return displayLocation;
    }

    @SuppressWarnings("unused")
    public String getCity() {
        return city;
    }

    @SuppressWarnings("unused")
    public String getPrimaryColor() {
        return primaryColor;
    }

    @SuppressWarnings("unused")
    public String getCountry() {
        return country;
    }

    public String getBannerUrl() {
        return bannerUrl;
    }

    public String getSalesText() {
        return salesText;
    }

    public Bitmap getBanner() {
        return banner;
    }

    public void setBanner(Bitmap banner) {
        this.banner = banner;
    }

    public List<SDay> getAgenda(){
        return agenda;
    }

    public List<SSession> getSessions(){
        return sessions;
    }

    public List<SSession> getSessions(List<String> ids){
        if(ids.size() == 0) return new ArrayList<>();
        int start = sessions.size() - 1;
        int end = 0;
        for(String s : ids){
            int i = sessions.indexOf(new SSession(s));
            start = i < start ? i : start;
            end = i > end ? i : end;
        }

        return sessions.subList(start, end + 1);
    }

    public List<SPerson> getSpeakers(){
        return speakers;
    }

    public List<SPerson> getSpeakers(List<String> ids){
        if(ids.size() == 0) return new ArrayList<>();
        int start = speakers.size() - 1;
        int end = 0;
        for(String s : ids){
            int i = speakers.indexOf(new SPerson("{Id:\"" + s +"\"}"));
            start = i < start ? i : start;
            end = i > end ? i : end;
        }

        return speakers.subList(start, end + 1);
    }

    public List<SPerson> getAttendees() {
        return attendees;
    }

    public List<SOrganization> getExhibitors() {
        return exhibitors;
    }

    public List<SSponsor> getSponsors() {
        return sponsors;
    }

    public List<SRecipient> getRecipients() {
        return recipients;
    }

    public List<SVenue> getVenues() {
        return venues;
    }

    public List<Bitmap> getBannerAds() {
        return bannerAds;
    }

    public void addBannerAd(Bitmap bannerAd) {
        this.bannerAds.add(bannerAd);
    }

    public List<Bitmap> getSplashAds() {
        return splashAds;
    }

    public Bitmap getSplashAd(){
        return splashAds.get(random.nextInt(splashAds.size()));
    }

    public void addSplashAd(Bitmap splashAd) {
        this.splashAds.add(splashAd);
    }

    @Override
    public int compareTo(@NonNull SObject a){
        if(!(a instanceof SEvent))
            throw new ClassCastException(a.getName() + " is not a SEvent");

        int compareStart = this.start.compareTo(((SEvent) a).getStart());
        if(compareStart == 0){
            int compareEnd = this.end.compareTo(((SEvent) a).getEnd());
            if(compareEnd == 0)
                return this.name.compareTo(a.getName());
        }

        return compareStart;
    }

    @Override
    public void fromJSON(String json){
        super.fromJSON(json);
        try {
            JSONObject jsonEvent = new JSONObject(json);
            if(jsonEvent.has("Start_Date__c"))
                this.start = parseDateString(jsonEvent.getString("Start_Date__c"));
            if(jsonEvent.has("End_Date__c"))
                this.end = parseDateString(jsonEvent.getString("End_Date__c"));
            if(jsonEvent.has("Registration_Link__c"))
                this.registration = (jsonEvent.isNull("Registration_Link__c") ? "http://events.shingo.org" : jsonEvent.getString("Registration_Link__c"));
            if(jsonEvent.has("Display_Location__c"))
                this.displayLocation = (jsonEvent.isNull("Display_Location__c") ? "" : jsonEvent.getString("Display_Location__c"));
            if(jsonEvent.has("Host_City__c"))
                this.city = (jsonEvent.isNull("Host_City__c") ? "" : jsonEvent.getString("Host_City__c"));
            if(jsonEvent.has("Host_Country__c"))
                this.country = (jsonEvent.isNull("Host_Country__c") ? "" : jsonEvent.getString("Host_Country__c"));
            if(jsonEvent.has("Primary_Color__c"))
                this.primaryColor = (jsonEvent.getString("Primary_Color__c").equals("null") ? "#640921" : jsonEvent.getString("Primary_Color__c"));
            if(jsonEvent.has("Shingo_Day_Agendas__r")) {
                agenda.clear();
                JSONArray jDays = jsonEvent.getJSONObject("Shingo_Day_Agendas__r").getJSONArray("records");
                for (int i = 0; i < jDays.length(); i++) {
                    SDay day = new SDay();
                    day.fromJSON(jDays.getJSONObject(i).toString());
                    agenda.add(day);
                }
            }
            if(jsonEvent.has("Sales_Text__c"))
                this.salesText = jsonEvent.isNull("Sales_Text__c") ? "" : jsonEvent.getString("Sales_Text__c");
            if(jsonEvent.has("Banner_URL__c"))
                this.bannerUrl = jsonEvent.isNull("Banner_URL__c") ? "" : jsonEvent.getString("Banner_URL__c");
            if(jsonEvent.has("Shingo_Attendees__r") && !jsonEvent.isNull("Shingo_Attendees__r")){
                attendees.clear();
                JSONArray jsonAttendees = jsonEvent.getJSONObject("Shingo_Attendees__r").getJSONArray("records");
                for(int i = 0; i < jsonAttendees.length(); i++){
                    JSONObject jsonAttendee = jsonAttendees.getJSONObject(i);
                    SPerson person = new SPerson(jsonAttendee.getJSONObject("Contact__r").getString("Id"),
                            (jsonAttendee.isNull("Badge_Name__c") ? jsonAttendee.getJSONObject("Contact__r").getString("Name") : jsonAttendee.getString("Badge_Name__c")),
                            (jsonAttendee.isNull("Badge_Title__c") ? jsonAttendee.getJSONObject("Contact__r").getString("Title") : jsonAttendee.getString("Badge_Title__c")),
                            (jsonAttendee.getJSONObject("Contact__r").isNull("Account") ? "" : jsonAttendee.getJSONObject("Contact__r").getJSONObject("Account").getString("Name")), SPerson.SPersonType.Attendee);
                    attendees.add(person);
                }
            }

        } catch (JSONException | ParseException e) {
            e.printStackTrace();
        }
    }

    public boolean needsUpdated(String data){
        if(!lastDataPull.containsKey(data)) return true;
        Date now = new Date();
        return now.after(new Date(lastDataPull.get(data).getTime() + TIME_OUT));
    }

    public void updatePullTime(String data){
        if(lastDataPull.containsKey(data)){
            lastDataPull.get(data).setTime(new Date().getTime());
        } else {
            lastDataPull.put(data, new Date());
        }
    }

    public boolean hasCache(String data){
        return lastDataPull.containsKey(data);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.start != null ? this.start.getTime() : -1);
        dest.writeLong(this.end != null ? this.end.getTime() : -1);
        dest.writeString(this.registration);
        dest.writeString(this.displayLocation);
        dest.writeString(this.city);
        dest.writeString(this.country);
        dest.writeString(this.primaryColor);
        dest.writeString(this.bannerUrl);
        dest.writeString(this.salesText);
        dest.writeTypedList(this.agenda);
        dest.writeTypedList(this.speakers);
        dest.writeTypedList(this.sessions);
        dest.writeTypedList(this.exhibitors);
        dest.writeTypedList(this.sponsors);
        dest.writeInt(this.lastDataPull.size());
        for (Map.Entry<String, Date> entry : this.lastDataPull.entrySet()) {
            dest.writeString(entry.getKey());
            dest.writeLong(entry.getValue() != null ? entry.getValue().getTime() : -1);
        }
        dest.writeString(this.id);
        dest.writeString(this.name);
    }

    protected SEvent(Parcel in) {
        long tmpStart = in.readLong();
        this.start = tmpStart == -1 ? null : new Date(tmpStart);
        long tmpEnd = in.readLong();
        this.end = tmpEnd == -1 ? null : new Date(tmpEnd);
        this.registration = in.readString();
        this.displayLocation = in.readString();
        this.city = in.readString();
        this.country = in.readString();
        this.primaryColor = in.readString();
        this.bannerUrl = in.readString();
        this.salesText = in.readString();
        this.agenda = in.createTypedArrayList(SDay.CREATOR);
        this.speakers = in.createTypedArrayList(SPerson.CREATOR);
        this.sessions = in.createTypedArrayList(SSession.CREATOR);
        this.exhibitors = in.createTypedArrayList(SOrganization.CREATOR);
        this.sponsors = in.createTypedArrayList(SSponsor.CREATOR);
        int lastDataPullSize = in.readInt();
        this.lastDataPull = new HashMap<>(lastDataPullSize);
        for (int i = 0; i < lastDataPullSize; i++) {
            String key = in.readString();
            long tmpValue = in.readLong();
            Date value = tmpValue == -1 ? null : new Date(tmpValue);
            this.lastDataPull.put(key, value);
        }
        this.id = in.readString();
        this.name = in.readString();
    }

    public static final Parcelable.Creator<SEvent> CREATOR = new Parcelable.Creator<SEvent>() {
        @Override
        public SEvent createFromParcel(Parcel source) {
            return new SEvent(source);
        }

        @Override
        public SEvent[] newArray(int size) {
            return new SEvent[size];
        }
    };

    public void refresh() {
        lastDataPull.clear();
    }

    @Override
    public String getDetail() {
        return displayLocation;
    }
}
