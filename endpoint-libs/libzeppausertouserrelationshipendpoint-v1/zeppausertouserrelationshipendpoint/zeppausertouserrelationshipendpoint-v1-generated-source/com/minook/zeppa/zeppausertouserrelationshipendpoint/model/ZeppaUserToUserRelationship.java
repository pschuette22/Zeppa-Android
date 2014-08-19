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
 * on 2014-08-19 at 19:45:41 UTC 
 * Modify at your own risk.
 */

package com.minook.zeppa.zeppausertouserrelationshipendpoint.model;

/**
 * Model definition for ZeppaUserToUserRelationship.
 *
 * <p> This is the Java data model class that specifies how to parse/serialize into the JSON that is
 * transmitted over HTTP when working with the zeppausertouserrelationshipendpoint. For a detailed
 * explanation see:
 * <a href="http://code.google.com/p/google-http-java-client/wiki/JSON">http://code.google.com/p/google-http-java-client/wiki/JSON</a>
 * </p>
 *
 * @author Google, Inc.
 */
@SuppressWarnings("javadoc")
public final class ZeppaUserToUserRelationship extends com.google.api.client.json.GenericJson {

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.lang.Long connectionRequesterId;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private Key key;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Integer relationshipType;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.lang.Long timeCreatedInMillis;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.lang.Long timeUpdatedInMillis;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.util.List<java.lang.Long> userIds;

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Long getConnectionRequesterId() {
    return connectionRequesterId;
  }

  /**
   * @param connectionRequesterId connectionRequesterId or {@code null} for none
   */
  public ZeppaUserToUserRelationship setConnectionRequesterId(java.lang.Long connectionRequesterId) {
    this.connectionRequesterId = connectionRequesterId;
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
  public ZeppaUserToUserRelationship setKey(Key key) {
    this.key = key;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Integer getRelationshipType() {
    return relationshipType;
  }

  /**
   * @param relationshipType relationshipType or {@code null} for none
   */
  public ZeppaUserToUserRelationship setRelationshipType(java.lang.Integer relationshipType) {
    this.relationshipType = relationshipType;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Long getTimeCreatedInMillis() {
    return timeCreatedInMillis;
  }

  /**
   * @param timeCreatedInMillis timeCreatedInMillis or {@code null} for none
   */
  public ZeppaUserToUserRelationship setTimeCreatedInMillis(java.lang.Long timeCreatedInMillis) {
    this.timeCreatedInMillis = timeCreatedInMillis;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Long getTimeUpdatedInMillis() {
    return timeUpdatedInMillis;
  }

  /**
   * @param timeUpdatedInMillis timeUpdatedInMillis or {@code null} for none
   */
  public ZeppaUserToUserRelationship setTimeUpdatedInMillis(java.lang.Long timeUpdatedInMillis) {
    this.timeUpdatedInMillis = timeUpdatedInMillis;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.util.List<java.lang.Long> getUserIds() {
    return userIds;
  }

  /**
   * @param userIds userIds or {@code null} for none
   */
  public ZeppaUserToUserRelationship setUserIds(java.util.List<java.lang.Long> userIds) {
    this.userIds = userIds;
    return this;
  }

  @Override
  public ZeppaUserToUserRelationship set(String fieldName, Object value) {
    return (ZeppaUserToUserRelationship) super.set(fieldName, value);
  }

  @Override
  public ZeppaUserToUserRelationship clone() {
    return (ZeppaUserToUserRelationship) super.clone();
  }

}
