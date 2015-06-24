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
 * on 2015-06-20 at 23:06:55 UTC 
 * Modify at your own risk.
 */

package com.minook.zeppa.zeppafeedbackendpoint.model;

/**
 * Model definition for ZeppaFeedback.
 *
 * <p> This is the Java data model class that specifies how to parse/serialize into the JSON that is
 * transmitted over HTTP when working with the zeppafeedbackendpoint. For a detailed explanation
 * see:
 * <a href="http://code.google.com/p/google-http-java-client/wiki/JSON">http://code.google.com/p/google-http-java-client/wiki/JSON</a>
 * </p>
 *
 * @author Google, Inc.
 */
@SuppressWarnings("javadoc")
public final class ZeppaFeedback extends com.google.api.client.json.GenericJson {

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.lang.Long created;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String deviceType;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String feedback;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.lang.Long id;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private Key key;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Double rating;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String releaseCode;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String subject;

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
   * @return value or {@code null} for none
   */
  public java.lang.Long getCreated() {
    return created;
  }

  /**
   * @param created created or {@code null} for none
   */
  public ZeppaFeedback setCreated(java.lang.Long created) {
    this.created = created;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getDeviceType() {
    return deviceType;
  }

  /**
   * @param deviceType deviceType or {@code null} for none
   */
  public ZeppaFeedback setDeviceType(java.lang.String deviceType) {
    this.deviceType = deviceType;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getFeedback() {
    return feedback;
  }

  /**
   * @param feedback feedback or {@code null} for none
   */
  public ZeppaFeedback setFeedback(java.lang.String feedback) {
    this.feedback = feedback;
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
  public ZeppaFeedback setId(java.lang.Long id) {
    this.id = id;
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
  public ZeppaFeedback setKey(Key key) {
    this.key = key;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Double getRating() {
    return rating;
  }

  /**
   * @param rating rating or {@code null} for none
   */
  public ZeppaFeedback setRating(java.lang.Double rating) {
    this.rating = rating;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getReleaseCode() {
    return releaseCode;
  }

  /**
   * @param releaseCode releaseCode or {@code null} for none
   */
  public ZeppaFeedback setReleaseCode(java.lang.String releaseCode) {
    this.releaseCode = releaseCode;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getSubject() {
    return subject;
  }

  /**
   * @param subject subject or {@code null} for none
   */
  public ZeppaFeedback setSubject(java.lang.String subject) {
    this.subject = subject;
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
  public ZeppaFeedback setUpdated(java.lang.Long updated) {
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
  public ZeppaFeedback setUserId(java.lang.Long userId) {
    this.userId = userId;
    return this;
  }

  @Override
  public ZeppaFeedback set(String fieldName, Object value) {
    return (ZeppaFeedback) super.set(fieldName, value);
  }

  @Override
  public ZeppaFeedback clone() {
    return (ZeppaFeedback) super.clone();
  }

}
