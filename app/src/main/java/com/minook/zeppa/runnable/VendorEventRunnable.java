package com.minook.zeppa.runnable;

import com.appspot.zeppa_cloud_1821.zeppaclientapi.Zeppaclientapi;
import com.appspot.zeppa_cloud_1821.zeppaclientapi.model.Vendor;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.minook.zeppa.ApiClientHelper;
import com.minook.zeppa.ZeppaApplication;
import com.minook.zeppa.mediator.VendorInfoMediator;
import com.minook.zeppa.singleton.ZeppaUserSingleton;

import java.io.IOException;

/**
 * Created by DrunkWithFunk21 on 3/15/16.
 */
public abstract class VendorEventRunnable extends BaseRunnable {

    public VendorEventRunnable(ZeppaApplication application, GoogleAccountCredential credential) {
        super(application, credential);
    }


    /**
     * Get the vendor by id or fetch it from the backend
     * @param vendorId
     */
    protected VendorInfoMediator getOrFetchVendorInfo(Long vendorId){

        if(vendorId.longValue()<0){
            return null;
        }

        VendorInfoMediator vendorInfo = ZeppaUserSingleton.getInstance().getVendorById(vendorId.longValue());

        if(vendorInfo == null) {

            ApiClientHelper helper = new ApiClientHelper();
            Zeppaclientapi api = helper.buildClientEndpoint();


            try {
                Zeppaclientapi.GetVendor getVendor = api.getVendor(vendorId, credential.getToken());
                Vendor vendor = getVendor.execute();

                if(vendor!= null) {
                    vendorInfo = new VendorInfoMediator(vendor);
                    ZeppaUserSingleton.getInstance().addVendorInfoMediator(vendorInfo);
                }

            } catch (IOException | GoogleAuthException e) {
                e.printStackTrace();
            }


        }

        return vendorInfo;
    }


}
