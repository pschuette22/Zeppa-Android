package com.minook.zeppa;

import com.appspot.zeppa_cloud_1821.zeppaclientapi.Zeppaclientapi;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.gson.GsonFactory;

/**
 * Created by Pete Schuette on 11/8/15.
 */
public class ApiClientHelper {

    private final static String API_MODULE_URL = "https://1-dot-zeppa-api-dot-zeppa-cloud-1821.appspot.com/_ah/api/";

    private HttpTransport transport;
    private GsonFactory factory;


    /**
     * Create a new instance of the ApiClientHelper class
     */
    public ApiClientHelper() {
        transport = AndroidHttp.newCompatibleTransport();
        factory = GsonFactory.getDefaultInstance();
    }

    /**
     * Build an endpoint to make HttpRequests to the App Engine Backend
     *
     * @return endpoint
     */
    public Zeppaclientapi buildClientEndpoint() {
        Zeppaclientapi.Builder builder = new Zeppaclientapi.Builder(
                transport,
                factory,
                null);
        builder = CloudEndpointUtils.updateBuilder(builder);
        // Override poorly formed url
        builder.setRootUrl(API_MODULE_URL);
        Zeppaclientapi endpoint = builder.build();

        return endpoint;
    }


}
