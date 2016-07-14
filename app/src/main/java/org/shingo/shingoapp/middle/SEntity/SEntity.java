package org.shingo.shingoapp.middle.SEntity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;
import org.shingo.shingoapp.middle.SObject;

import java.io.IOException;
import java.net.URL;

/**
 * Created by dustinehoman on 5/9/16.
 */
public abstract class SEntity extends SObject {
    protected Bitmap image;
    protected String imageUrl;
    protected String summary;

    public SEntity(){}

    public SEntity(String id, String name, String summary, Bitmap image){
        super(id, name);
        this.summary = summary;
        this.image = image;
    }

    public void setImage(Bitmap image) { this.image = image; }

    public String getImageUrl() { return imageUrl; }

    public Bitmap getImage(){
        return image;
    }

    public String getSummary(){
        return summary;
    }

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

    public abstract String getDetail();

    public abstract View getContent(Context context);

    protected abstract void getTypeFromString(String type);
}
