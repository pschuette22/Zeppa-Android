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

package com.minook.zeppa.eventtagendpoint;

/**
 * Service definition for Eventtagendpoint (v1).
 *
 * <p>
 * This is an API
 * </p>
 *
 * <p>
 * For more information about this service, see the
 * <a href="" target="_blank">API Documentation</a>
 * </p>
 *
 * <p>
 * This service uses {@link EventtagendpointRequestInitializer} to initialize global parameters via its
 * {@link Builder}.
 * </p>
 *
 * @since 1.3
 * @author Google, Inc.
 */
@SuppressWarnings("javadoc")
public class Eventtagendpoint extends com.google.api.client.googleapis.services.json.AbstractGoogleJsonClient {

  // Note: Leave this static initializer at the top of the file.
  static {
    com.google.api.client.util.Preconditions.checkState(
        com.google.api.client.googleapis.GoogleUtils.MAJOR_VERSION == 1 &&
        com.google.api.client.googleapis.GoogleUtils.MINOR_VERSION >= 15,
        "You are currently running with version %s of google-api-client. " +
        "You need at least version 1.15 of google-api-client to run version " +
        "1.18.0-rc of the eventtagendpoint library.", com.google.api.client.googleapis.GoogleUtils.VERSION);
  }

  /**
   * The default encoded root URL of the service. This is determined when the library is generated
   * and normally should not be changed.
   *
   * @since 1.7
   */
  public static final String DEFAULT_ROOT_URL = "https://zeppa-cloud-1821.appspot.com/_ah/api/";

  /**
   * The default encoded service path of the service. This is determined when the library is
   * generated and normally should not be changed.
   *
   * @since 1.7
   */
  public static final String DEFAULT_SERVICE_PATH = "eventtagendpoint/v1/";

  /**
   * The default encoded base URL of the service. This is determined when the library is generated
   * and normally should not be changed.
   */
  public static final String DEFAULT_BASE_URL = DEFAULT_ROOT_URL + DEFAULT_SERVICE_PATH;

  /**
   * Constructor.
   *
   * <p>
   * Use {@link Builder} if you need to specify any of the optional parameters.
   * </p>
   *
   * @param transport HTTP transport, which should normally be:
   *        <ul>
   *        <li>Google App Engine:
   *        {@code com.google.api.client.extensions.appengine.http.UrlFetchTransport}</li>
   *        <li>Android: {@code newCompatibleTransport} from
   *        {@code com.google.api.client.extensions.android.http.AndroidHttp}</li>
   *        <li>Java: {@link com.google.api.client.googleapis.javanet.GoogleNetHttpTransport#newTrustedTransport()}
   *        </li>
   *        </ul>
   * @param jsonFactory JSON factory, which may be:
   *        <ul>
   *        <li>Jackson: {@code com.google.api.client.json.jackson2.JacksonFactory}</li>
   *        <li>Google GSON: {@code com.google.api.client.json.gson.GsonFactory}</li>
   *        <li>Android Honeycomb or higher:
   *        {@code com.google.api.client.extensions.android.json.AndroidJsonFactory}</li>
   *        </ul>
   * @param httpRequestInitializer HTTP request initializer or {@code null} for none
   * @since 1.7
   */
  public Eventtagendpoint(com.google.api.client.http.HttpTransport transport, com.google.api.client.json.JsonFactory jsonFactory,
      com.google.api.client.http.HttpRequestInitializer httpRequestInitializer) {
    this(new Builder(transport, jsonFactory, httpRequestInitializer));
  }

  /**
   * @param builder builder
   */
  Eventtagendpoint(Builder builder) {
    super(builder);
  }

  @Override
  protected void initialize(com.google.api.client.googleapis.services.AbstractGoogleClientRequest<?> httpClientRequest) throws java.io.IOException {
    super.initialize(httpClientRequest);
  }

  /**
   * Create a request for the method "getEventTag".
   *
   * This request holds the parameters needed by the eventtagendpoint server.  After setting any
   * optional parameters, call the {@link GetEventTag#execute()} method to invoke the remote
   * operation.
   *
   * @param id
   * @return the request
   */
  public GetEventTag getEventTag(java.lang.Long id) throws java.io.IOException {
    GetEventTag result = new GetEventTag(id);
    initialize(result);
    return result;
  }

  public class GetEventTag extends EventtagendpointRequest<com.minook.zeppa.eventtagendpoint.model.EventTag> {

    private static final String REST_PATH = "eventtag/{id}";

    /**
     * Create a request for the method "getEventTag".
     *
     * This request holds the parameters needed by the the eventtagendpoint server.  After setting any
     * optional parameters, call the {@link GetEventTag#execute()} method to invoke the remote
     * operation. <p> {@link
     * GetEventTag#initialize(com.google.api.client.googleapis.services.AbstractGoogleClientRequest)}
     * must be called to initialize this instance immediately after invoking the constructor. </p>
     *
     * @param id
     * @since 1.13
     */
    protected GetEventTag(java.lang.Long id) {
      super(Eventtagendpoint.this, "GET", REST_PATH, null, com.minook.zeppa.eventtagendpoint.model.EventTag.class);
      this.id = com.google.api.client.util.Preconditions.checkNotNull(id, "Required parameter id must be specified.");
    }

    @Override
    public com.google.api.client.http.HttpResponse executeUsingHead() throws java.io.IOException {
      return super.executeUsingHead();
    }

    @Override
    public com.google.api.client.http.HttpRequest buildHttpRequestUsingHead() throws java.io.IOException {
      return super.buildHttpRequestUsingHead();
    }

    @Override
    public GetEventTag setAlt(java.lang.String alt) {
      return (GetEventTag) super.setAlt(alt);
    }

    @Override
    public GetEventTag setFields(java.lang.String fields) {
      return (GetEventTag) super.setFields(fields);
    }

    @Override
    public GetEventTag setKey(java.lang.String key) {
      return (GetEventTag) super.setKey(key);
    }

    @Override
    public GetEventTag setOauthToken(java.lang.String oauthToken) {
      return (GetEventTag) super.setOauthToken(oauthToken);
    }

    @Override
    public GetEventTag setPrettyPrint(java.lang.Boolean prettyPrint) {
      return (GetEventTag) super.setPrettyPrint(prettyPrint);
    }

    @Override
    public GetEventTag setQuotaUser(java.lang.String quotaUser) {
      return (GetEventTag) super.setQuotaUser(quotaUser);
    }

    @Override
    public GetEventTag setUserIp(java.lang.String userIp) {
      return (GetEventTag) super.setUserIp(userIp);
    }

    @com.google.api.client.util.Key
    private java.lang.Long id;

    /**

     */
    public java.lang.Long getId() {
      return id;
    }

    public GetEventTag setId(java.lang.Long id) {
      this.id = id;
      return this;
    }

    @Override
    public GetEventTag set(String parameterName, Object value) {
      return (GetEventTag) super.set(parameterName, value);
    }
  }

  /**
   * Create a request for the method "insertEventTag".
   *
   * This request holds the parameters needed by the eventtagendpoint server.  After setting any
   * optional parameters, call the {@link InsertEventTag#execute()} method to invoke the remote
   * operation.
   *
   * @param content the {@link com.minook.zeppa.eventtagendpoint.model.EventTag}
   * @return the request
   */
  public InsertEventTag insertEventTag(com.minook.zeppa.eventtagendpoint.model.EventTag content) throws java.io.IOException {
    InsertEventTag result = new InsertEventTag(content);
    initialize(result);
    return result;
  }

  public class InsertEventTag extends EventtagendpointRequest<com.minook.zeppa.eventtagendpoint.model.EventTag> {

    private static final String REST_PATH = "eventtag";

    /**
     * Create a request for the method "insertEventTag".
     *
     * This request holds the parameters needed by the the eventtagendpoint server.  After setting any
     * optional parameters, call the {@link InsertEventTag#execute()} method to invoke the remote
     * operation. <p> {@link InsertEventTag#initialize(com.google.api.client.googleapis.services.Abstr
     * actGoogleClientRequest)} must be called to initialize this instance immediately after invoking
     * the constructor. </p>
     *
     * @param content the {@link com.minook.zeppa.eventtagendpoint.model.EventTag}
     * @since 1.13
     */
    protected InsertEventTag(com.minook.zeppa.eventtagendpoint.model.EventTag content) {
      super(Eventtagendpoint.this, "POST", REST_PATH, content, com.minook.zeppa.eventtagendpoint.model.EventTag.class);
    }

    @Override
    public InsertEventTag setAlt(java.lang.String alt) {
      return (InsertEventTag) super.setAlt(alt);
    }

    @Override
    public InsertEventTag setFields(java.lang.String fields) {
      return (InsertEventTag) super.setFields(fields);
    }

    @Override
    public InsertEventTag setKey(java.lang.String key) {
      return (InsertEventTag) super.setKey(key);
    }

    @Override
    public InsertEventTag setOauthToken(java.lang.String oauthToken) {
      return (InsertEventTag) super.setOauthToken(oauthToken);
    }

    @Override
    public InsertEventTag setPrettyPrint(java.lang.Boolean prettyPrint) {
      return (InsertEventTag) super.setPrettyPrint(prettyPrint);
    }

    @Override
    public InsertEventTag setQuotaUser(java.lang.String quotaUser) {
      return (InsertEventTag) super.setQuotaUser(quotaUser);
    }

    @Override
    public InsertEventTag setUserIp(java.lang.String userIp) {
      return (InsertEventTag) super.setUserIp(userIp);
    }

    @Override
    public InsertEventTag set(String parameterName, Object value) {
      return (InsertEventTag) super.set(parameterName, value);
    }
  }

  /**
   * Create a request for the method "listEventTag".
   *
   * This request holds the parameters needed by the eventtagendpoint server.  After setting any
   * optional parameters, call the {@link ListEventTag#execute()} method to invoke the remote
   * operation.
   *
   * @return the request
   */
  public ListEventTag listEventTag() throws java.io.IOException {
    ListEventTag result = new ListEventTag();
    initialize(result);
    return result;
  }

  public class ListEventTag extends EventtagendpointRequest<com.minook.zeppa.eventtagendpoint.model.CollectionResponseEventTag> {

    private static final String REST_PATH = "eventtag";

    /**
     * Create a request for the method "listEventTag".
     *
     * This request holds the parameters needed by the the eventtagendpoint server.  After setting any
     * optional parameters, call the {@link ListEventTag#execute()} method to invoke the remote
     * operation. <p> {@link
     * ListEventTag#initialize(com.google.api.client.googleapis.services.AbstractGoogleClientRequest)}
     * must be called to initialize this instance immediately after invoking the constructor. </p>
     *
     * @since 1.13
     */
    protected ListEventTag() {
      super(Eventtagendpoint.this, "GET", REST_PATH, null, com.minook.zeppa.eventtagendpoint.model.CollectionResponseEventTag.class);
    }

    @Override
    public com.google.api.client.http.HttpResponse executeUsingHead() throws java.io.IOException {
      return super.executeUsingHead();
    }

    @Override
    public com.google.api.client.http.HttpRequest buildHttpRequestUsingHead() throws java.io.IOException {
      return super.buildHttpRequestUsingHead();
    }

    @Override
    public ListEventTag setAlt(java.lang.String alt) {
      return (ListEventTag) super.setAlt(alt);
    }

    @Override
    public ListEventTag setFields(java.lang.String fields) {
      return (ListEventTag) super.setFields(fields);
    }

    @Override
    public ListEventTag setKey(java.lang.String key) {
      return (ListEventTag) super.setKey(key);
    }

    @Override
    public ListEventTag setOauthToken(java.lang.String oauthToken) {
      return (ListEventTag) super.setOauthToken(oauthToken);
    }

    @Override
    public ListEventTag setPrettyPrint(java.lang.Boolean prettyPrint) {
      return (ListEventTag) super.setPrettyPrint(prettyPrint);
    }

    @Override
    public ListEventTag setQuotaUser(java.lang.String quotaUser) {
      return (ListEventTag) super.setQuotaUser(quotaUser);
    }

    @Override
    public ListEventTag setUserIp(java.lang.String userIp) {
      return (ListEventTag) super.setUserIp(userIp);
    }

    @com.google.api.client.util.Key
    private java.lang.String cursor;

    /**

     */
    public java.lang.String getCursor() {
      return cursor;
    }

    public ListEventTag setCursor(java.lang.String cursor) {
      this.cursor = cursor;
      return this;
    }

    @com.google.api.client.util.Key
    private java.lang.String filter;

    /**

     */
    public java.lang.String getFilter() {
      return filter;
    }

    public ListEventTag setFilter(java.lang.String filter) {
      this.filter = filter;
      return this;
    }

    @com.google.api.client.util.Key
    private java.lang.Integer limit;

    /**

     */
    public java.lang.Integer getLimit() {
      return limit;
    }

    public ListEventTag setLimit(java.lang.Integer limit) {
      this.limit = limit;
      return this;
    }

    @com.google.api.client.util.Key
    private java.lang.String ordering;

    /**

     */
    public java.lang.String getOrdering() {
      return ordering;
    }

    public ListEventTag setOrdering(java.lang.String ordering) {
      this.ordering = ordering;
      return this;
    }

    @Override
    public ListEventTag set(String parameterName, Object value) {
      return (ListEventTag) super.set(parameterName, value);
    }
  }

  /**
   * Create a request for the method "removeEventTag".
   *
   * This request holds the parameters needed by the eventtagendpoint server.  After setting any
   * optional parameters, call the {@link RemoveEventTag#execute()} method to invoke the remote
   * operation.
   *
   * @param tagId
   * @return the request
   */
  public RemoveEventTag removeEventTag(java.lang.Long tagId) throws java.io.IOException {
    RemoveEventTag result = new RemoveEventTag(tagId);
    initialize(result);
    return result;
  }

  public class RemoveEventTag extends EventtagendpointRequest<Void> {

    private static final String REST_PATH = "eventtag/{tagId}";

    /**
     * Create a request for the method "removeEventTag".
     *
     * This request holds the parameters needed by the the eventtagendpoint server.  After setting any
     * optional parameters, call the {@link RemoveEventTag#execute()} method to invoke the remote
     * operation. <p> {@link RemoveEventTag#initialize(com.google.api.client.googleapis.services.Abstr
     * actGoogleClientRequest)} must be called to initialize this instance immediately after invoking
     * the constructor. </p>
     *
     * @param tagId
     * @since 1.13
     */
    protected RemoveEventTag(java.lang.Long tagId) {
      super(Eventtagendpoint.this, "DELETE", REST_PATH, null, Void.class);
      this.tagId = com.google.api.client.util.Preconditions.checkNotNull(tagId, "Required parameter tagId must be specified.");
    }

    @Override
    public RemoveEventTag setAlt(java.lang.String alt) {
      return (RemoveEventTag) super.setAlt(alt);
    }

    @Override
    public RemoveEventTag setFields(java.lang.String fields) {
      return (RemoveEventTag) super.setFields(fields);
    }

    @Override
    public RemoveEventTag setKey(java.lang.String key) {
      return (RemoveEventTag) super.setKey(key);
    }

    @Override
    public RemoveEventTag setOauthToken(java.lang.String oauthToken) {
      return (RemoveEventTag) super.setOauthToken(oauthToken);
    }

    @Override
    public RemoveEventTag setPrettyPrint(java.lang.Boolean prettyPrint) {
      return (RemoveEventTag) super.setPrettyPrint(prettyPrint);
    }

    @Override
    public RemoveEventTag setQuotaUser(java.lang.String quotaUser) {
      return (RemoveEventTag) super.setQuotaUser(quotaUser);
    }

    @Override
    public RemoveEventTag setUserIp(java.lang.String userIp) {
      return (RemoveEventTag) super.setUserIp(userIp);
    }

    @com.google.api.client.util.Key
    private java.lang.Long tagId;

    /**

     */
    public java.lang.Long getTagId() {
      return tagId;
    }

    public RemoveEventTag setTagId(java.lang.Long tagId) {
      this.tagId = tagId;
      return this;
    }

    @Override
    public RemoveEventTag set(String parameterName, Object value) {
      return (RemoveEventTag) super.set(parameterName, value);
    }
  }

  /**
   * Builder for {@link Eventtagendpoint}.
   *
   * <p>
   * Implementation is not thread-safe.
   * </p>
   *
   * @since 1.3.0
   */
  public static final class Builder extends com.google.api.client.googleapis.services.json.AbstractGoogleJsonClient.Builder {

    /**
     * Returns an instance of a new builder.
     *
     * @param transport HTTP transport, which should normally be:
     *        <ul>
     *        <li>Google App Engine:
     *        {@code com.google.api.client.extensions.appengine.http.UrlFetchTransport}</li>
     *        <li>Android: {@code newCompatibleTransport} from
     *        {@code com.google.api.client.extensions.android.http.AndroidHttp}</li>
     *        <li>Java: {@link com.google.api.client.googleapis.javanet.GoogleNetHttpTransport#newTrustedTransport()}
     *        </li>
     *        </ul>
     * @param jsonFactory JSON factory, which may be:
     *        <ul>
     *        <li>Jackson: {@code com.google.api.client.json.jackson2.JacksonFactory}</li>
     *        <li>Google GSON: {@code com.google.api.client.json.gson.GsonFactory}</li>
     *        <li>Android Honeycomb or higher:
     *        {@code com.google.api.client.extensions.android.json.AndroidJsonFactory}</li>
     *        </ul>
     * @param httpRequestInitializer HTTP request initializer or {@code null} for none
     * @since 1.7
     */
    public Builder(com.google.api.client.http.HttpTransport transport, com.google.api.client.json.JsonFactory jsonFactory,
        com.google.api.client.http.HttpRequestInitializer httpRequestInitializer) {
      super(
          transport,
          jsonFactory,
          DEFAULT_ROOT_URL,
          DEFAULT_SERVICE_PATH,
          httpRequestInitializer,
          false);
    }

    /** Builds a new instance of {@link Eventtagendpoint}. */
    @Override
    public Eventtagendpoint build() {
      return new Eventtagendpoint(this);
    }

    @Override
    public Builder setRootUrl(String rootUrl) {
      return (Builder) super.setRootUrl(rootUrl);
    }

    @Override
    public Builder setServicePath(String servicePath) {
      return (Builder) super.setServicePath(servicePath);
    }

    @Override
    public Builder setHttpRequestInitializer(com.google.api.client.http.HttpRequestInitializer httpRequestInitializer) {
      return (Builder) super.setHttpRequestInitializer(httpRequestInitializer);
    }

    @Override
    public Builder setApplicationName(String applicationName) {
      return (Builder) super.setApplicationName(applicationName);
    }

    @Override
    public Builder setSuppressPatternChecks(boolean suppressPatternChecks) {
      return (Builder) super.setSuppressPatternChecks(suppressPatternChecks);
    }

    @Override
    public Builder setSuppressRequiredParameterChecks(boolean suppressRequiredParameterChecks) {
      return (Builder) super.setSuppressRequiredParameterChecks(suppressRequiredParameterChecks);
    }

    @Override
    public Builder setSuppressAllChecks(boolean suppressAllChecks) {
      return (Builder) super.setSuppressAllChecks(suppressAllChecks);
    }

    /**
     * Set the {@link EventtagendpointRequestInitializer}.
     *
     * @since 1.12
     */
    public Builder setEventtagendpointRequestInitializer(
        EventtagendpointRequestInitializer eventtagendpointRequestInitializer) {
      return (Builder) super.setGoogleClientRequestInitializer(eventtagendpointRequestInitializer);
    }

    @Override
    public Builder setGoogleClientRequestInitializer(
        com.google.api.client.googleapis.services.GoogleClientRequestInitializer googleClientRequestInitializer) {
      return (Builder) super.setGoogleClientRequestInitializer(googleClientRequestInitializer);
    }
  }
}
