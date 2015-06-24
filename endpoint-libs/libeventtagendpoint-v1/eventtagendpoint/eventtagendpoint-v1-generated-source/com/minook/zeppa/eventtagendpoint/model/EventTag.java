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
 * on 2015-06-20 at 23:06:31 UTC 
 * Modify at your own risk.
 */

package com.minook.zeppa.eventtagendpoint.model;

/**
 * Model definition for EventTag.
 *
 * <p> This is the Java data model class that specifies how to parse/serialize into the JSON that is
 * transmitted over HTTP when working with the eventtagendpoint. For a detailed explanation see:
 * <a href="http://code.google.com/p/google-http-java-client/wiki/JSON">http://code.google.com/p/google-http-java-client/wiki/JSON</a>
 * </p>
 *
 * @author Google, Inc.
 */
@SuppressWarnings("javadoc")
public final class EventTag extends com.google.api.client.json.GenericJson {

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.lang.Long created;

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
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.lang.Long ownerId;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String tagText;

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
  public EventTag setCreated(java.lang.Long created) {
    this.created = created;
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
  public EventTag setId(java.lang.Long id) {
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
  public EventTag setKey(Key key) {
    this.key = key;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Long getOwnerId() {
    return ownerId;
  }

  /**
   * @param ownerId ownerId or {@code null} for none
   */
  public EventTag setOwnerId(java.lang.Long ownerId) {
    this.ownerId = ownerId;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getTagText() {
    return tagText;
  }

  /**
   * @param tagText tagText or {@code null} for none
   */
  public EventTag setTagText(java.lang.String tagText) {
    this.tagText = tagText;
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
  public EventTag setUpdated(java.lang.Long updated) {
    this.updated = updated;
    return this;
  }

  @Override
  public EventTag set(String fieldName, Object value) {
    return (EventTag) super.set(fieldName, value);
  }

  @Override
  public EventTag clone() {
    return (EventTag) super.clone();
  }

}
