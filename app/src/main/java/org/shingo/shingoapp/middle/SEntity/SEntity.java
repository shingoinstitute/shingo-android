package org.shingo.shingoapp.middle.SEntity;

import android.graphics.Bitmap;

import org.json.JSONException;
import org.json.JSONObject;
import org.shingo.shingoapp.middle.SEvent.SEvent;
import org.shingo.shingoapp.middle.SObject;

/**
 * This class is the data holder for
 * all entities in the Shingo app.
 * Entities can be {@link SOrganization}s,
 * {@link SPerson}s, {@link SRecipient}s, or
 * {@link SSponsor}s.
 *
 * @author Dustin Homan
 */
public abstract class SEntity extends SObject {

    /**
     * Typically a logo, profile picture, or banner.
     */
    protected Bitmap image;

    /**
     * URL to download {@link #image}
     */
    protected String imageUrl;

    /**
     * A brief description of the entity.
     */
    protected String summary;

    public SEntity(){}

    /**
     * Constructor to be used by child classes
     * @param id Salesforce Id
     * @param name Entity name
     * @param summary A brief description
     * @param image A logo, profile picture, or banner
     */
    public SEntity(String id, String name, String summary, Bitmap image){
        super(id, name);
        this.summary = summary;
        this.image = image;
    }

    /**
     * Getter for {@link #image}
     * @return {@link #image}
     */
    public Bitmap getImage(){
        return image;
    }

    /**
     * Setter for {@link #image}
     * @param image {@link Bitmap}
     */
    public void setImage(Bitmap image) { this.image = image; }

    /**
     * Getter for {@link #imageUrl}
     * @return {@link #imageUrl}
     */
    public String getImageUrl() { return imageUrl; }

    /**
     * Getter for {@link #summary}
     * @return {@link #summary}
     */
    public String getSummary(){
        return summary;
    }

    /**
     * Used to parse an Entity from a JSON String
     * @param json JSON {@link String} representing an Entity
     */
    @Override
    public void fromJSON(String json){
        super.fromJSON(json);
        try {
            JSONObject jsonEntity = new JSONObject(json);
            if(jsonEntity.has("List_of_Photos__c"))
                this.imageUrl = jsonEntity.isNull("List_of_Photos__c") ? "http://res.cloudinary.com/shingo/image/upload/c_fill,g_center,h_300,w_300/v1414874243/silhouette_vzugec.png" : jsonEntity.getString("List_of_Photos__c").split("\\^")[0];
            else if(jsonEntity.has("Picture_URL__c"))
                this.imageUrl = jsonEntity.isNull("Picture_URL__c") ? "http://res.cloudinary.com/shingo/image/upload/c_fill,g_center,h_300,w_300/v1414874243/silhouette_vzugec.png" : jsonEntity.getString("Picture_URL__c");
            else if(jsonEntity.has("Logo__c"))
                this.imageUrl = jsonEntity.isNull("Logo__c") ? "https://placehold.it/350x350" : jsonEntity.getString("Logo__c");

            if(jsonEntity.has("Summary__c"))
                this.summary = (jsonEntity.isNull("Summary__c") ? "Summary coming soon!" : jsonEntity.getString("Summary__c"));
            else if(jsonEntity.has("App_Abstract__c"))
                this.summary = jsonEntity.isNull("App_Abstract__c") ? "Summary coming soon!" : jsonEntity.getString("App_Abstract__c");
            else if(jsonEntity.has("Speaker_Biography__c"))
                this.summary = (jsonEntity.isNull("Speaker_Biography__c") ? "Bio coming soon!" : jsonEntity.getString("Speaker_Biography__c"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Abstract method to get child classes detail
     * @return {@link String}
     */
    public abstract String getDetail();

    /**
     * Abstract Setter for child classes {@link Enum} type
     * @param type {@link String} representing {@link Enum}
     */
    protected abstract void setTypeFromString(String type);
}
