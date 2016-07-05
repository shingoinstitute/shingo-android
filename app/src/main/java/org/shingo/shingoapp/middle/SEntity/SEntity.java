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
    protected String summary;

    public boolean is_loading = false;

    public SEntity(){}

    public SEntity(String id, String name, String summary, Bitmap image){
        super(id, name);
        this.summary = summary;
        this.image = image;
    }

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
            summary = (jsonEntity.getString("Summary").equals("null") ? null : jsonEntity.getString("Summary"));
            getImageFromURL(jsonEntity.getString("Image").equals("null") ?
                    "http://res.cloudinary.com/shingo/image/upload/c_fill,g_center,h_300,w_300/v1414874243/silhouette_vzugec.png"
                    : jsonEntity.getString("Image"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public abstract String getDetail();

    public abstract View getContent(Context context);

    protected void getImageFromURL(final String urlString){
        is_loading = true;
        Thread thread = new Thread(){
            @Override
            public void run(){
                try {
                    URL url = new URL(urlString);
                    image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                    is_loading = false;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }

    protected abstract void getTypeFromString(String type);
}
