/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
/*
 * This code was generated by https://code.google.com/p/google-apis-client-generator/
 * (build: 2015-03-26 20:30:19 UTC)
 * on 2015-04-24 at 19:53:26 UTC 
 * Modify at your own risk.
 */

package com.minook.zeppa.zeppaeventendpoint.model;

/**
 * Model definition for ZeppaEvent.
 *
 * <p> This is the Java data model class that specifies how to parse/serialize into the JSON that is
 * transmitted over HTTP when working with the zeppaeventendpoint. For a detailed explanation see:
 * <a href="http://code.google.com/p/google-http-java-client/wiki/JSON">http://code.google.com/p/google-http-java-client/wiki/JSON</a>
 * </p>
 *
 * @author Google, Inc.
 */
@SuppressWarnings("javadoc")
public final class ZeppaEvent extends com.google.api.client.json.GenericJson {

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.lang.Long created;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String description;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String displayLocation;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.lang.Long end;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String googleCalendarEventId;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String googleCalendarId;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Boolean guestsMayInvite;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.lang.Long hostId;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String iCalUID;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.lang.Long id;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.util.List<java.lang.Long> invitedUserIds;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private Key key;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String mapsLocation;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String privacy;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.lang.Long start;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.util.List<java.lang.Long> tagIds;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String title;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.lang.Long updated;

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Long getCreated() {
    return created;
  }

  /**
   * @param created created or {@code null} for none
   */
  public ZeppaEvent setCreated(java.lang.Long created) {
    this.created = created;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getDescription() {
    return description;
  }

  /**
   * @param description description or {@code null} for none
   */
  public ZeppaEvent setDescription(java.lang.String description) {
    this.description = description;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getDisplayLocation() {
    return displayLocation;
  }

  /**
   * @param displayLocation displayLocation or {@code null} for none
   */
  public ZeppaEvent setDisplayLocation(java.lang.String displayLocation) {
    this.displayLocation = displayLocation;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Long getEnd() {
    return end;
  }

  /**
   * @param end end or {@code null} for none
   */
  public ZeppaEvent setEnd(java.lang.Long end) {
    this.end = end;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getGoogleCalendarEventId() {
    return googleCalendarEventId;
  }

  /**
   * @param googleCalendarEventId googleCalendarEventId or {@code null} for none
   */
  public ZeppaEvent setGoogleCalendarEventId(java.lang.String googleCalendarEventId) {
    this.googleCalendarEventId = googleCalendarEventId;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getGoogleCalendarId() {
    return googleCalendarId;
  }

  /**
   * @param googleCalendarId googleCalendarId or {@code null} for none
   */
  public ZeppaEvent setGoogleCalendarId(java.lang.String googleCalendarId) {
    this.googleCalendarId = googleCalendarId;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Boolean getGuestsMayInvite() {
    return guestsMayInvite;
  }

  /**
   * @param guestsMayInvite guestsMayInvite or {@code null} for none
   */
  public ZeppaEvent setGuestsMayInvite(java.lang.Boolean guestsMayInvite) {
    this.guestsMayInvite = guestsMayInvite;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Long getHostId() {
    return hostId;
  }

  /**
   * @param hostId hostId or {@code null} for none
   */
  public ZeppaEvent setHostId(java.lang.Long hostId) {
    this.hostId = hostId;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getICalUID() {
    return iCalUID;
  }

  /**
   * @param iCalUID iCalUID or {@code null} for none
   */
  public ZeppaEvent setICalUID(java.lang.String iCalUID) {
    this.iCalUID = iCalUID;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Long getId() {
    return id;
  }

  /**
   * @param id id or {@code null} for none
   */
  public ZeppaEvent setId(java.lang.Long id) {
    this.id = id;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.util.List<java.lang.Long> getInvitedUserIds() {
    return invitedUserIds;
  }

  /**
   * @param invitedUserIds invitedUserIds or {@code null} for none
   */
  public ZeppaEvent setInvitedUserIds(java.util.List<java.lang.Long> invitedUserIds) {
    this.invitedUserIds = invitedUserIds;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public Key getKey() {
    return key;
  }

  /**
   * @param key key or {@code null} for none
   */
  public ZeppaEvent setKey(Key key) {
    this.key = key;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getMapsLocation() {
    return mapsLocation;
  }

  /**
   * @param mapsLocation mapsLocation or {@code null} for none
   */
  public ZeppaEvent setMapsLocation(java.lang.String mapsLocation) {
    this.mapsLocation = mapsLocation;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getPrivacy() {
    return privacy;
  }

  /**
   * @param privacy privacy or {@code null} for none
   */
  public ZeppaEvent setPrivacy(java.lang.String privacy) {
    this.privacy = privacy;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Long getStart() {
    return start;
  }

  /**
   * @param start start or {@code null} for none
   */
  public ZeppaEvent setStart(java.lang.Long start) {
    this.start = start;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.util.List<java.lang.Long> getTagIds() {
    return tagIds;
  }

  /**
   * @param tagIds tagIds or {@code null} for none
   */
  public ZeppaEvent setTagIds(java.util.List<java.lang.Long> tagIds) {
    this.tagIds = tagIds;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getTitle() {
    return title;
  }

  /**
   * @param title title or {@code null} for none
   */
  public ZeppaEvent setTitle(java.lang.String title) {
    this.title = title;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Long getUpdated() {
    return updated;
  }

  /**
   * @param updated updated or {@code null} for none
   */
  public ZeppaEvent setUpdated(java.lang.Long updated) {
    this.updated = updated;
    return this;
  }

  @Override
  public ZeppaEvent set(String fieldName, Object value) {
    return (ZeppaEvent) super.set(fieldName, value);
  }

  @Override
  public ZeppaEvent clone() {
    return (ZeppaEvent) super.clone();
  }

}
