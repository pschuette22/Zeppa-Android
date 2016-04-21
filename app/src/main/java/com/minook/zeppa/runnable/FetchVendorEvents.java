package com.minook.zeppa.runnable;

import com.appspot.zeppa_cloud_1821.zeppaclientapi.Zeppaclientapi;
import com.appspot.zeppa_cloud_1821.zeppaclientapi.model.CollectionResponseVendorEvent;
import com.appspot.zeppa_cloud_1821.zeppaclientapi.model.VendorEvent;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.minook.zeppa.ApiClientHelper;
import com.minook.zeppa.ZeppaApplication;
import com.minook.zeppa.mediator.VendorEventMediator;
import com.minook.zeppa.mediator.VendorInfoMediator;
import com.minook.zeppa.singleton.ZeppaEventSingleton;

import java.io.IOException;
import java.util.Iterator;

/**
 * Created by Pete Schuette on 3/8/16.
 *
 * Runnable task to fetch some vendor events
 */
public class FetchVendorEvents extends VendorEventRunnable {

    // Filter the fetched entities
    private String filter;
    // Cursor from last fetch
    private String cursor;

    // Hang onto exceptions if one is thrown
    private Exception e;

    public FetchVendorEvents(ZeppaApplication application, GoogleAccountCredential credential, String filter, String cursor) {
        super(application, credential);
        this.filter = filter;
        this.cursor = cursor;
    }


    @Override
    public void run() {


        ApiClientHelper helper = new ApiClientHelper();
        Zeppaclientapi api = helper.buildClientEndpoint();

        try {
            // initialize the query and relevant fields
            Zeppaclientapi.ListVendorEvent listVendorEvent = api.listVendorEvent(credential.getToken());
            listVendorEvent.setFilter(filter);
            listVendorEvent.setCursor(cursor);
            listVendorEvent.setLimit(20); // Fetch up to 20 events by default
            listVendorEvent.setOrdering("end asc");

            // Execute that query!
            CollectionResponseVendorEvent result = listVendorEvent.execute();

            // Safely check that the result was nice and contains results.
            if(result!= null && result.getItems() != null && result.size()>0){

                // Iterate the returned objects and return the result
                Iterator<VendorEvent> vendorEventIterator = result.getItems().iterator();
                while(vendorEventIterator.hasNext()){
                    VendorEvent e = vendorEventIterator.next();
                    VendorInfoMediator vendorInfo = getOrFetchVendorInfo(e.getHostId());

                    if(vendorInfo!= null) {
                        VendorEventMediator mediator = new VendorEventMediator(e);

                        ZeppaEventSingleton.getInstance().addMediator(mediator);
                    }
                }


                // let the app know we returned some good stuff
                try {
                    application.getCurrentActivity().runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            ZeppaEventSingleton.getInstance().notifyObservers();
                        }

                    });
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }

            }





        } catch (IOException | GoogleAuthException e) {
            // Catch that error!
            e.printStackTrace();
            this.e = e;
        }



    }


}
