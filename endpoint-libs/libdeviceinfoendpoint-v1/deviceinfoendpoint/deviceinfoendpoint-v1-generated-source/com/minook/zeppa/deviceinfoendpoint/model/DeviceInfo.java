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
 * on 2014-08-13 at 19:43:48 UTC 
 * Modify at your own risk.
 */

package com.minook.zeppa.deviceinfoendpoint.model;

/**
 * Model definition for DeviceInfo.
 *
 * <p> This is the Java data model class that specifies how to parse/serialize into the JSON that is
 * transmitted over HTTP when working with the deviceinfoendpoint. For a detailed explanation see:
 * <a href="http://code.google.com/p/google-http-java-client/wiki/JSON">http://code.google.com/p/google-http-java-client/wiki/JSON</a>
 * </p>
 *
 * @author Google, Inc.
 */
@SuppressWarnings("javadoc")
public final class DeviceInfo extends com.google.api.client.json.GenericJson {

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
  private java.lang.String phoneType;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String registrationId;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.lang.Long timeRegisteredInMillis;

  /**
   * @return value or {@code null} for none
   */
  public Key getKey() {
    return key;
  }

  /**
   * @param key key or {@code null} for none
   */
  public DeviceInfo setKey(Key key) {
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
  public DeviceInfo setOwnerId(java.lang.Long ownerId) {
    this.ownerId = ownerId;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getPhoneType() {
    return phoneType;
  }

  /**
   * @param phoneType phoneType or {@code null} for none
   */
  public DeviceInfo setPhoneType(java.lang.String phoneType) {
    this.phoneType = phoneType;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getRegistrationId() {
    return registrationId;
  }

  /**
   * @param registrationId registrationId or {@code null} for none
   */
  public DeviceInfo setRegistrationId(java.lang.String registrationId) {
    this.registrationId = registrationId;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Long getTimeRegisteredInMillis() {
    return timeRegisteredInMillis;
  }

  /**
   * @param timeRegisteredInMillis timeRegisteredInMillis or {@code null} for none
   */
  public DeviceInfo setTimeRegisteredInMillis(java.lang.Long timeRegisteredInMillis) {
    this.timeRegisteredInMillis = timeRegisteredInMillis;
    return this;
  }

  @Override
  public DeviceInfo set(String fieldName, Object value) {
    return (DeviceInfo) super.set(fieldName, value);
  }

  @Override
  public DeviceInfo clone() {
    return (DeviceInfo) super.clone();
  }

}