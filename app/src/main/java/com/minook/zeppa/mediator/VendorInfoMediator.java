package com.minook.zeppa.mediator;

import com.appspot.zeppa_cloud_1821.zeppaclientapi.model.Vendor;

/**
 * Created by Pete Schuette on 3/14/16.
 */
public class VendorInfoMediator extends AbstractZeppaUserMediator {

    private Vendor vendor;

    public VendorInfoMediator(Vendor vendor) {
        this.vendor = vendor;
    }

    @Override
    public String getGivenName() {
        return vendor.getCompanyName();
    }

    @Override
    public String getFamilyName() {
        return vendor.getCompanyName();
    }

    @Override
    public String getDisplayName() {
        return vendor.getCompanyName();
    }

    @Override
    protected String getImageUrl() {
        return vendor.getCompanyLogoUrl();
    }

    @Override
    public Long getUserId() {
        return vendor.getKey().getId();
    }
}
