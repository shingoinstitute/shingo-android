package org.shingo.shingoeventsapp.middle;

import java.util.List;

/**
 * Created by dustinehoman on 7/19/16.
 */
public class SectionedDataModel {
    private String header;
    private List<? extends SObject> items;

    public SectionedDataModel(String header, List<? extends SObject> items){
        this.header = header;
        this.items = items;
    }

    public String getHeader() {
        return header;
    }

    public List<? extends SObject> getItems() {
        return items;
    }

    public void setItems(List<SObject> items) {
        this.items = items;
    }
}
