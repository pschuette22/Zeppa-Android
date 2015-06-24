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
 * on 2015-06-20 at 23:06:14 UTC 
 * Modify at your own risk.
 */

package com.minook.zeppa.zeppauserinfoendpoint.model;

/**
 * Model definition for ZeppaUserInfo.
 *
 * <p> This is the Java data model class that specifies how to parse/serialize into the JSON that is
 * transmitted over HTTP when working with the zeppauserinfoendpoint. For a detailed explanation
 * see:
 * <a href="http://code.google.com/p/google-http-java-client/wiki/JSON">http://code.google.com/p/google-http-java-client/wiki/JSON</a>
 * </p>
 *
 * @author Google, Inc.
 */
@SuppressWarnings("javadoc")
public final class ZeppaUserInfo extends com.google.api.client.json.GenericJson {

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.lang.Long created;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String familyName;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String givenName;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String googleAccountEmail;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.lang.Long id;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String imageUrl;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private Key key;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String primaryUnformattedNumber;

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
  public ZeppaUserInfo setCreated(java.lang.Long created) {
    this.created = created;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getFamilyName() {
    return familyName;
  }

  /**
   * @param familyName familyName or {@code null} for none
   */
  public ZeppaUserInfo setFamilyName(java.lang.String familyName) {
    this.familyName = familyName;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getGivenName() {
    return givenName;
  }

  /**
   * @param givenName givenName or {@code null} for none
   */
  public ZeppaUserInfo setGivenName(java.lang.String givenName) {
    this.givenName = givenName;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getGoogleAccountEmail() {
    return googleAccountEmail;
  }

  /**
   * @param googleAccountEmail googleAccountEmail or {@code null} for none
   */
  public ZeppaUserInfo setGoogleAccountEmail(java.lang.String googleAccountEmail) {
    this.googleAccountEmail = googleAccountEmail;
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
  public ZeppaUserInfo setId(java.lang.Long id) {
    this.id = id;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getImageUrl() {
    return imageUrl;
  }

  /**
   * @param imageUrl imageUrl or {@code null} for none
   */
  public ZeppaUserInfo setImageUrl(java.lang.String imageUrl) {
    this.imageUrl = imageUrl;
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
  public ZeppaUserInfo setKey(Key key) {
    this.key = key;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getPrimaryUnformattedNumber() {
    return primaryUnformattedNumber;
  }

  /**
   * @param primaryUnformattedNumber primaryUnformattedNumber or {@code null} for none
   */
  public ZeppaUserInfo setPrimaryUnformattedNumber(java.lang.String primaryUnformattedNumber) {
    this.primaryUnformattedNumber = primaryUnformattedNumber;
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
  public ZeppaUserInfo setUpdated(java.lang.Long updated) {
    this.updated = updated;
    return this;
  }

  @Override
  public ZeppaUserInfo set(String fieldName, Object value) {
    return (ZeppaUserInfo) super.set(fieldName, value);
  }

  @Override
  public ZeppaUserInfo clone() {
    return (ZeppaUserInfo) super.clone();
  }

}
