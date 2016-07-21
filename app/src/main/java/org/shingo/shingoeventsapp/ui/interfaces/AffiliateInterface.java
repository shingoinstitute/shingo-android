package org.shingo.shingoeventsapp.ui.interfaces;

import org.shingo.shingoeventsapp.middle.SEntity.SOrganization;

import java.util.List;

/**
 * Created by dustinehoman on 7/19/16.
 */
public interface AffiliateInterface {
    void addAffiliate(SOrganization event);
    SOrganization getAffiliate(String id);
    List<SOrganization> affiliates();
    void clearAffiliates();
    void sortAffiliates();
}
