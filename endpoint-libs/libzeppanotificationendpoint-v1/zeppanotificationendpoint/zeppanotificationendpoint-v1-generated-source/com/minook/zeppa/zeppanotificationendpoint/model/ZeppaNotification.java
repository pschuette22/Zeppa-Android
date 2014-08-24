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
 * (build: 2014-07-22 21:53:01 UTC)
 * on 2014-08-20 at 15:31:40 UTC 
 * Modify at your own risk.
 */

package com.minook.zeppa.zeppanotificationendpoint.model;

/**
 * Model definition for ZeppaNotification.
 *
 * <p> This is the Java data model class that specifies how to parse/serialize into the JSON that is
 * transmitted over HTTP when working with the zeppanotificationendpoint. For a detailed explanation
 * see:
 * <a href="http://code.google.com/p/google-http-java-client/wiki/JSON">http://code.google.com/p/google-http-java-client/wiki/JSON</a>
 * </p>
 *
 * @author Google, Inc.
 */
@SuppressWarnings("javadoc")
public final class ZeppaNotification extends com.google.api.client.json.GenericJson {

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.lang.Long eventId;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String extraMessage;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.lang.Long fromUserId;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Boolean hasSeen;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private Key key;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Integer notificationOrdinal;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String notificationType;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.lang.Long sentDate;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.lang.Long toUserId;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String type;

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Long getEventId() {
    return eventId;
  }

  /**
   * @param eventId eventId or {@code null} for none
   */
  public ZeppaNotification setEventId(java.lang.Long eventId) {
    this.eventId = eventId;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getExtraMessage() {
    return extraMessage;
  }

  /**
   * @param extraMessage extraMessage or {@code null} for none
   */
  public ZeppaNotification setExtraMessage(java.lang.String extraMessage) {
    this.extraMessage = extraMessage;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Long getFromUserId() {
    return fromUserId;
  }

  /**
   * @param fromUserId fromUserId or {@code null} for none
   */
  public ZeppaNotification setFromUserId(java.lang.Long fromUserId) {
    this.fromUserId = fromUserId;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Boolean getHasSeen() {
    return hasSeen;
  }

  /**
   * @param hasSeen hasSeen or {@code null} for none
   */
  public ZeppaNotification setHasSeen(java.lang.Boolean hasSeen) {
    this.hasSeen = hasSeen;
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
  public ZeppaNotification setKey(Key key) {
    this.key = key;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Integer getNotificationOrdinal() {
    return notificationOrdinal;
  }

  /**
   * @param notificationOrdinal notificationOrdinal or {@code null} for none
   */
  public ZeppaNotification setNotificationOrdinal(java.lang.Integer notificationOrdinal) {
    this.notificationOrdinal = notificationOrdinal;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getNotificationType() {
    return notificationType;
  }

  /**
   * @param notificationType notificationType or {@code null} for none
   */
  public ZeppaNotification setNotificationType(java.lang.String notificationType) {
    this.notificationType = notificationType;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Long getSentDate() {
    return sentDate;
  }

  /**
   * @param sentDate sentDate or {@code null} for none
   */
  public ZeppaNotification setSentDate(java.lang.Long sentDate) {
    this.sentDate = sentDate;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Long getToUserId() {
    return toUserId;
  }

  /**
   * @param toUserId toUserId or {@code null} for none
   */
  public ZeppaNotification setToUserId(java.lang.Long toUserId) {
    this.toUserId = toUserId;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getType() {
    return type;
  }

  /**
   * @param type type or {@code null} for none
   */
  public ZeppaNotification setType(java.lang.String type) {
    this.type = type;
    return this;
  }

  @Override
  public ZeppaNotification set(String fieldName, Object value) {
    return (ZeppaNotification) super.set(fieldName, value);
  }

  @Override
  public ZeppaNotification clone() {
    return (ZeppaNotification) super.clone();
  }

}
