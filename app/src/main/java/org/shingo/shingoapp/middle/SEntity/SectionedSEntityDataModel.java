package org.shingo.shingoapp.middle.SEntity;

import java.util.List;

/**
 * Created by dustinehoman on 7/19/16.
 */
public class SectionedSEntityDataModel {
    private String header;
    private List<? extends SEntity> items;

    public SectionedSEntityDataModel(String header, List<? extends SEntity> items){
        this.header = header + "s";
        this.items = items;
    }

    public String getHeader() {
        return header;
    }

    public List<? extends SEntity> getItems() {
        return items;
    }

    public void setItems(List<SEntity> items) {
        this.items = items;
    }
}
