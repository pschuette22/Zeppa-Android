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
 * on 2015-06-20 at 23:05:48 UTC 
 * Modify at your own risk.
 */

package com.minook.zeppa.zeppaeventtouserrelationshipendpoint.model;

/**
 * Model definition for ZeppaEventToUserRelationship.
 *
 * <p> This is the Java data model class that specifies how to parse/serialize into the JSON that is
 * transmitted over HTTP when working with the zeppaeventtouserrelationshipendpoint. For a detailed
 * explanation see:
 * <a href="http://code.google.com/p/google-http-java-client/wiki/JSON">http://code.google.com/p/google-http-java-client/wiki/JSON</a>
 * </p>
 *
 * @author Google, Inc.
 */
@SuppressWarnings("javadoc")
public final class ZeppaEventToUserRelationship extends com.google.api.client.json.GenericJson {

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.lang.Long created;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.lang.Long eventHostId;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.lang.Long eventId;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.lang.Long expires;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.lang.Long id;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.lang.Long invitedByUserId;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Boolean isAttending;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Boolean isRecommended;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Boolean isWatching;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private Key key;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.lang.Long updated;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.lang.Long userId;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Boolean wasInvited;

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Long getCreated() {
    return created;
  }

  /**
   * @param created created or {@code null} for none
   */
  public ZeppaEventToUserRelationship setCreated(java.lang.Long created) {
    this.created = created;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Long getEventHostId() {
    return eventHostId;
  }

  /**
   * @param eventHostId eventHostId or {@code null} for none
   */
  public ZeppaEventToUserRelationship setEventHostId(java.lang.Long eventHostId) {
    this.eventHostId = eventHostId;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Long getEventId() {
    return eventId;
  }

  /**
   * @param eventId eventId or {@code null} for none
   */
  public ZeppaEventToUserRelationship setEventId(java.lang.Long eventId) {
    this.eventId = eventId;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Long getExpires() {
    return expires;
  }

  /**
   * @param expires expires or {@code null} for none
   */
  public ZeppaEventToUserRelationship setExpires(java.lang.Long expires) {
    this.expires = expires;
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
  public ZeppaEventToUserRelationship setId(java.lang.Long id) {
    this.id = id;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Long getInvitedByUserId() {
    return invitedByUserId;
  }

  /**
   * @param invitedByUserId invitedByUserId or {@code null} for none
   */
  public ZeppaEventToUserRelationship setInvitedByUserId(java.lang.Long invitedByUserId) {
    this.invitedByUserId = invitedByUserId;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Boolean getIsAttending() {
    return isAttending;
  }

  /**
   * @param isAttending isAttending or {@code null} for none
   */
  public ZeppaEventToUserRelationship setIsAttending(java.lang.Boolean isAttending) {
    this.isAttending = isAttending;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Boolean getIsRecommended() {
    return isRecommended;
  }

  /**
   * @param isRecommended isRecommended or {@code null} for none
   */
  public ZeppaEventToUserRelationship setIsRecommended(java.lang.Boolean isRecommended) {
    this.isRecommended = isRecommended;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Boolean getIsWatching() {
    return isWatching;
  }

  /**
   * @param isWatching isWatching or {@code null} for none
   */
  public ZeppaEventToUserRelationship setIsWatching(java.lang.Boolean isWatching) {
    this.isWatching = isWatching;
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
  public ZeppaEventToUserRelationship setKey(Key key) {
    this.key = key;
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
  public ZeppaEventToUserRelationship setUpdated(java.lang.Long updated) {
    this.updated = updated;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Long getUserId() {
    return userId;
  }

  /**
   * @param userId userId or {@code null} for none
   */
  public ZeppaEventToUserRelationship setUserId(java.lang.Long userId) {
    this.userId = userId;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Boolean getWasInvited() {
    return wasInvited;
  }

  /**
   * @param wasInvited wasInvited or {@code null} for none
   */
  public ZeppaEventToUserRelationship setWasInvited(java.lang.Boolean wasInvited) {
    this.wasInvited = wasInvited;
    return this;
  }

  @Override
  public ZeppaEventToUserRelationship set(String fieldName, Object value) {
    return (ZeppaEventToUserRelationship) super.set(fieldName, value);
  }

  @Override
  public ZeppaEventToUserRelationship clone() {
    return (ZeppaEventToUserRelationship) super.clone();
  }

}
