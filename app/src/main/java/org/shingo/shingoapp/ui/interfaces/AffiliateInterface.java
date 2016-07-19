package org.shingo.shingoapp.ui.interfaces;

import org.shingo.shingoapp.middle.SEntity.SOrganization;

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
