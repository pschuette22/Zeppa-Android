package com.minook.zeppa.mediator;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;

import com.appspot.zeppa_cloud_1821.zeppaclientapi.model.VendorEvent;
import com.appspot.zeppa_cloud_1821.zeppaclientapi.model.VendorEventRelationship;
import com.appspot.zeppa_cloud_1821.zeppaclientapi.model.ZeppaEventToUserRelationship;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.minook.zeppa.ZeppaApplication;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pete Schuette on 3/14/16.
 */
public class VendorEventMediator extends AbstractZeppaEventMediator {

    private VendorEvent event;
    private VendorEventRelationship myRelationship;
    private List<VendorEventRelationship> relationships;

    public VendorEventMediator(VendorEvent event) {
        super(null);
        this.event = event;
    }

    @Override
    public void launchIntoEventView(Context context) {
        // Launch event view
    }

    @Override
    public View convertQuickActionBar(Context context, View barView) {

        return null;
    }

    @Override
    public boolean isHostedByCurrentUser() {
        return false;
    }

    @Override
    public boolean isAgendaEvent() {
        if(myRelationship!= null) {
            return myRelationship.getJoined() || myRelationship.getWatched();
        } else {
            return false;
        }
    }

    @Override
    protected void setHostInfo(View view) {
        // Display the info for the
    }

    @Override
    public void setConflictIndicator(Context context, ImageView image) {

    }

    @Override
    public Intent getToEventViewIntent(Context context) {
        return null;
    }


    @Override
    public Long getEventId() {
        return event.getId();
    }

    @Override
    public String getCalendarEventId() {
        return event.getGoogleCalendarEventId();
    }

    @Override
    public Long getHostId() {
        return event.getHostId();
    }

    @Override
    public Long getEndInMillis() {
        return event.getEnd();
    }

    @Override
    public Long getStartInMillis() {
        return event.getStart();
    }

    @Override
    public ConflictStatus getConflictStatus() {
        return super.getConflictStatus();
    }

    @Override
    protected AbstractZeppaUserMediator getHostMediator() {
        // Return the vendor mediator
        return null;
    }

    @Override
    public List<Long> getTagIds() {
        return event.getTagIds();
    }

    @Override
    public List<ZeppaEventToUserRelationship> getEventRelationships() {
        return null;
    }

    @Override
    public void setEventRelationships(List<ZeppaEventToUserRelationship> relationships) {
        // TODO: update mediators to account for vendor event relationships
    }

    @Override
    public void errorLoadingRelationships() {
        super.errorLoadingRelationships();
    }

    @Override
    public List<Long> getAttendingUserIds() {
        // Iterate through the relationships and add attending users
        List<Long> attendingUserIds = new ArrayList<Long>();
        return attendingUserIds;
    }

    @Override
    public String getTitle() {
        return event.getTitle();
    }

    @Override
    public String getDescription() {
        return event.getDescription();
    }

    @Override
    public String getDisplayLocation() {
        return event.getDisplayLocation();
    }

    @Override
    public String getMapsLocation() {
        return event.getMapsLocation();
    }


    @Override
    public boolean doesMatchEventId(long eventId) {
        return super.doesMatchEventId(eventId);
    }


    @Override
    public boolean isPrivateEvent() {
        return false;
    }

    @Override
    public boolean guestsMayInvite() {
        return true;
    }


    @Override
    public void viewInCalendarApplication(Context context) {
        super.viewInCalendarApplication(context);
    }

    @Override
    public void loadEventRelationships(ZeppaApplication application, GoogleAccountCredential credential, OnRelationshipsLoadedListener listener) {
        // TODO: start a fetch task to load list of Vendor Event Relationships
    }


}
