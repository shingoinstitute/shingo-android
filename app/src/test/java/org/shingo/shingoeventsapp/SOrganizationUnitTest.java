package org.shingo.shingoeventsapp;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.shingo.shingoeventsapp.middle.SEntity.SOrganization;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class SOrganizationUnitTest {

    String[] json;

    @Before
    public void initialize() {
        json = new String[]{"{\"Name\": \"A Test\", \"Id\": \"abc\", \"Website\": \"example.com\", \"Email\": \"dude@example.com\", \"Phone\": \"555-555-5555\", \"Type__c\": \"None\", \"Page_Path__c\": \"/stuff/\", \"Summary__c\": \"My Test Summary\", \"Logo__c\": \"http://placehold.it/300x300\"}", "{\"Name\": \"B Test\", \"Id\": \"def\", \"Website\": \"example.com\", \"Email\": \"dude@example.com\", \"Phone\": \"555-555-5555\", \"Type__c\": \"Affiliate\", \"Page_Path__c\": \"/stuff/\", \"Summary__c\": \"My Test Summary\", \"Logo__c\": \"http://placehold.it/300x300\"}", "{\"Name\": \"C Test\", \"Id\": \"ghi\", \"Website\": \"example.com\", \"Email\": \"dude@example.com\", \"Phone\": \"555-555-5555\", \"Type__c\": \"Exhibitor\", \"Page_Path__c\": \"/stuff/\", \"Summary__c\": \"My Test Summary\", \"Logo__c\": \"http://placehold.it/300x300\"}", "{\"Name\": \"D Test\", \"Id\": \"jkl\", \"Website\": \"example.com\", \"Email\": \"dude@example.com\", \"Phone\": \"555-555-5555\", \"Type__c\": \"Sponsor\", \"Page_Path__c\": \"/stuff/\", \"Summary__c\": \"My Test Summary\", \"Logo__c\": \"http://placehold.it/300x300\"}"};
    }

    @Test
    public void fromJson_isCorrect() throws Exception {
        for(String j : json) {
            SOrganization testOrg = new SOrganization();
            testOrg.fromJSON(j);
            JSONObject expected = new JSONObject(j);
            assertEquals("Name is not parsed", expected.getString("Name"), testOrg.getName());
            assertEquals("Id is not parsed", expected.getString("Id"), testOrg.getId());
            assertEquals("Website is not parsed", expected.getString("Website"), testOrg.getWebsite());
            assertEquals("Email is not parsed", expected.getString("Email"), testOrg.getEmail());
            assertEquals("Phone is not parsed", expected.getString("Phone"), testOrg.getPhone());
            assertEquals("Type__c is not parsed", expected.getString("Type__c"), testOrg.type.toString());
            assertEquals("Page_Path__c is not parsed", expected.getString("Page_Path__c"), testOrg.getShingoPath());
            assertEquals("Summary__c is not parsed", expected.getString("Summary__c"), testOrg.getSummary());
            assertEquals("Logo__c is not parsed", expected.getString("Logo__c"), testOrg.getImageUrl());
        }
    }

    @Test
    public void getDetail_isCorrect() throws Exception {
        SOrganization testOrgNone = new SOrganization(json[0]);
        assertEquals("getDetail is not correct for OrgType None", testOrgNone.getWebsite(), testOrgNone.getDetail());

        SOrganization testOrgAff = new SOrganization(json[1]);
        assertEquals("getDetail is not correct for OrgType Affiliate", "http://shingo.org" + testOrgAff.getShingoPath(), testOrgAff.getDetail());

        for(int i = 2; i < json.length; i++){
            SOrganization testOrg = new SOrganization(json[i]);
            assertEquals("getDetail is not correct for OrgType" + testOrg.type.toString(), testOrg.getEmail(), testOrg.getDetail());
        }

    }

    @Test
    public void compareTo_isCorrect() throws  Exception {
        SOrganization a = new SOrganization(json[0]);
        SOrganization b = new SOrganization(json[1]);
        assertEquals("a is not equal to a", 0, a.compareTo(a));
        assertEquals("a is not less than b", -1, a.compareTo(b));
        assertEquals("b is not greater than a", 1, b.compareTo(a));
    }

    @Test
    public void equals_isCorrect() throws Exception {
        SOrganization a = new SOrganization(json[0]);
        SOrganization b = new SOrganization(json[1]);
        SOrganization c = new SOrganization(json[0]);
        assertTrue("a is not equal to c", a.equals(c));
        assertFalse("a is equal to b", a.equals(b));
    }
}