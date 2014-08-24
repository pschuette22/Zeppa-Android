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
 * on 2014-08-20 at 15:31:25 UTC 
 * Modify at your own risk.
 */

package com.minook.zeppa.zeppaeventendpoint;

/**
 * Service definition for Zeppaeventendpoint (v1).
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
 * This service uses {@link ZeppaeventendpointRequestInitializer} to initialize global parameters via its
 * {@link Builder}.
 * </p>
 *
 * @since 1.3
 * @author Google, Inc.
 */
@SuppressWarnings("javadoc")
public class Zeppaeventendpoint extends com.google.api.client.googleapis.services.json.AbstractGoogleJsonClient {

  // Note: Leave this static initializer at the top of the file.
  static {
    com.google.api.client.util.Preconditions.checkState(
        com.google.api.client.googleapis.GoogleUtils.MAJOR_VERSION == 1 &&
        com.google.api.client.googleapis.GoogleUtils.MINOR_VERSION >= 15,
        "You are currently running with version %s of google-api-client. " +
        "You need at least version 1.15 of google-api-client to run version " +
        "1.18.0-rc of the zeppaeventendpoint library.", com.google.api.client.googleapis.GoogleUtils.VERSION);
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
  public static final String DEFAULT_SERVICE_PATH = "zeppaeventendpoint/v1/";

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
  public Zeppaeventendpoint(com.google.api.client.http.HttpTransport transport, com.google.api.client.json.JsonFactory jsonFactory,
      com.google.api.client.http.HttpRequestInitializer httpRequestInitializer) {
    this(new Builder(transport, jsonFactory, httpRequestInitializer));
  }

  /**
   * @param builder builder
   */
  Zeppaeventendpoint(Builder builder) {
    super(builder);
  }

  @Override
  protected void initialize(com.google.api.client.googleapis.services.AbstractGoogleClientRequest<?> httpClientRequest) throws java.io.IOException {
    super.initialize(httpClientRequest);
  }

  /**
   * Create a request for the method "fetchHostedEvents".
   *
   * This request holds the parameters needed by the zeppaeventendpoint server.  After setting any
   * optional parameters, call the {@link FetchHostedEvents#execute()} method to invoke the remote
   * operation.
   *
   * @param userId
   * @param minEndInMillis
   * @return the request
   */
  public FetchHostedEvents fetchHostedEvents(java.lang.Long userId, java.lang.Long minEndInMillis) throws java.io.IOException {
    FetchHostedEvents result = new FetchHostedEvents(userId, minEndInMillis);
    initialize(result);
    return result;
  }

  public class FetchHostedEvents extends ZeppaeventendpointRequest<com.minook.zeppa.zeppaeventendpoint.model.CollectionResponseZeppaEvent> {

    private static final String REST_PATH = "fetchHostedEvents/{userId}/{minEndInMillis}";

    /**
     * Create a request for the method "fetchHostedEvents".
     *
     * This request holds the parameters needed by the the zeppaeventendpoint server.  After setting
     * any optional parameters, call the {@link FetchHostedEvents#execute()} method to invoke the
     * remote operation. <p> {@link FetchHostedEvents#initialize(com.google.api.client.googleapis.serv
     * ices.AbstractGoogleClientRequest)} must be called to initialize this instance immediately after
     * invoking the constructor. </p>
     *
     * @param userId
     * @param minEndInMillis
     * @since 1.13
     */
    protected FetchHostedEvents(java.lang.Long userId, java.lang.Long minEndInMillis) {
      super(Zeppaeventendpoint.this, "POST", REST_PATH, null, com.minook.zeppa.zeppaeventendpoint.model.CollectionResponseZeppaEvent.class);
      this.userId = com.google.api.client.util.Preconditions.checkNotNull(userId, "Required parameter userId must be specified.");
      this.minEndInMillis = com.google.api.client.util.Preconditions.checkNotNull(minEndInMillis, "Required parameter minEndInMillis must be specified.");
    }

    @Override
    public FetchHostedEvents setAlt(java.lang.String alt) {
      return (FetchHostedEvents) super.setAlt(alt);
    }

    @Override
    public FetchHostedEvents setFields(java.lang.String fields) {
      return (FetchHostedEvents) super.setFields(fields);
    }

    @Override
    public FetchHostedEvents setKey(java.lang.String key) {
      return (FetchHostedEvents) super.setKey(key);
    }

    @Override
    public FetchHostedEvents setOauthToken(java.lang.String oauthToken) {
      return (FetchHostedEvents) super.setOauthToken(oauthToken);
    }

    @Override
    public FetchHostedEvents setPrettyPrint(java.lang.Boolean prettyPrint) {
      return (FetchHostedEvents) super.setPrettyPrint(prettyPrint);
    }

    @Override
    public FetchHostedEvents setQuotaUser(java.lang.String quotaUser) {
      return (FetchHostedEvents) super.setQuotaUser(quotaUser);
    }

    @Override
    public FetchHostedEvents setUserIp(java.lang.String userIp) {
      return (FetchHostedEvents) super.setUserIp(userIp);
    }

    @com.google.api.client.util.Key
    private java.lang.Long userId;

    /**

     */
    public java.lang.Long getUserId() {
      return userId;
    }

    public FetchHostedEvents setUserId(java.lang.Long userId) {
      this.userId = userId;
      return this;
    }

    @com.google.api.client.util.Key
    private java.lang.Long minEndInMillis;

    /**

     */
    public java.lang.Long getMinEndInMillis() {
      return minEndInMillis;
    }

    public FetchHostedEvents setMinEndInMillis(java.lang.Long minEndInMillis) {
      this.minEndInMillis = minEndInMillis;
      return this;
    }

    @Override
    public FetchHostedEvents set(String parameterName, Object value) {
      return (FetchHostedEvents) super.set(parameterName, value);
    }
  }

  /**
   * Create a request for the method "fetchPossibleEvents".
   *
   * This request holds the parameters needed by the zeppaeventendpoint server.  After setting any
   * optional parameters, call the {@link FetchPossibleEvents#execute()} method to invoke the remote
   * operation.
   *
   * @param userId
   * @param callTime
   * @param minEndInMillis
   * @return the request
   */
  public FetchPossibleEvents fetchPossibleEvents(java.lang.Long userId, java.lang.Long callTime, java.lang.Long minEndInMillis) throws java.io.IOException {
    FetchPossibleEvents result = new FetchPossibleEvents(userId, callTime, minEndInMillis);
    initialize(result);
    return result;
  }

  public class FetchPossibleEvents extends ZeppaeventendpointRequest<com.minook.zeppa.zeppaeventendpoint.model.CollectionResponseZeppaEvent> {

    private static final String REST_PATH = "fetchPossibleEvents/{userId}/{callTime}/{minEndInMillis}";

    /**
     * Create a request for the method "fetchPossibleEvents".
     *
     * This request holds the parameters needed by the the zeppaeventendpoint server.  After setting
     * any optional parameters, call the {@link FetchPossibleEvents#execute()} method to invoke the
     * remote operation. <p> {@link FetchPossibleEvents#initialize(com.google.api.client.googleapis.se
     * rvices.AbstractGoogleClientRequest)} must be called to initialize this instance immediately
     * after invoking the constructor. </p>
     *
     * @param userId
     * @param callTime
     * @param minEndInMillis
     * @since 1.13
     */
    protected FetchPossibleEvents(java.lang.Long userId, java.lang.Long callTime, java.lang.Long minEndInMillis) {
      super(Zeppaeventendpoint.this, "POST", REST_PATH, null, com.minook.zeppa.zeppaeventendpoint.model.CollectionResponseZeppaEvent.class);
      this.userId = com.google.api.client.util.Preconditions.checkNotNull(userId, "Required parameter userId must be specified.");
      this.callTime = com.google.api.client.util.Preconditions.checkNotNull(callTime, "Required parameter callTime must be specified.");
      this.minEndInMillis = com.google.api.client.util.Preconditions.checkNotNull(minEndInMillis, "Required parameter minEndInMillis must be specified.");
    }

    @Override
    public FetchPossibleEvents setAlt(java.lang.String alt) {
      return (FetchPossibleEvents) super.setAlt(alt);
    }

    @Override
    public FetchPossibleEvents setFields(java.lang.String fields) {
      return (FetchPossibleEvents) super.setFields(fields);
    }

    @Override
    public FetchPossibleEvents setKey(java.lang.String key) {
      return (FetchPossibleEvents) super.setKey(key);
    }

    @Override
    public FetchPossibleEvents setOauthToken(java.lang.String oauthToken) {
      return (FetchPossibleEvents) super.setOauthToken(oauthToken);
    }

    @Override
    public FetchPossibleEvents setPrettyPrint(java.lang.Boolean prettyPrint) {
      return (FetchPossibleEvents) super.setPrettyPrint(prettyPrint);
    }

    @Override
    public FetchPossibleEvents setQuotaUser(java.lang.String quotaUser) {
      return (FetchPossibleEvents) super.setQuotaUser(quotaUser);
    }

    @Override
    public FetchPossibleEvents setUserIp(java.lang.String userIp) {
      return (FetchPossibleEvents) super.setUserIp(userIp);
    }

    @com.google.api.client.util.Key
    private java.lang.Long userId;

    /**

     */
    public java.lang.Long getUserId() {
      return userId;
    }

    public FetchPossibleEvents setUserId(java.lang.Long userId) {
      this.userId = userId;
      return this;
    }

    @com.google.api.client.util.Key
    private java.lang.Long callTime;

    /**

     */
    public java.lang.Long getCallTime() {
      return callTime;
    }

    public FetchPossibleEvents setCallTime(java.lang.Long callTime) {
      this.callTime = callTime;
      return this;
    }

    @com.google.api.client.util.Key
    private java.lang.Long minEndInMillis;

    /**

     */
    public java.lang.Long getMinEndInMillis() {
      return minEndInMillis;
    }

    public FetchPossibleEvents setMinEndInMillis(java.lang.Long minEndInMillis) {
      this.minEndInMillis = minEndInMillis;
      return this;
    }

    @Override
    public FetchPossibleEvents set(String parameterName, Object value) {
      return (FetchPossibleEvents) super.set(parameterName, value);
    }
  }

  /**
   * Create a request for the method "fetchRepostsOfEvent".
   *
   * This request holds the parameters needed by the zeppaeventendpoint server.  After setting any
   * optional parameters, call the {@link FetchRepostsOfEvent#execute()} method to invoke the remote
   * operation.
   *
   * @param eventId
   * @param createdAfterMillis
   * @return the request
   */
  public FetchRepostsOfEvent fetchRepostsOfEvent(java.lang.Long eventId, java.lang.Long createdAfterMillis) throws java.io.IOException {
    FetchRepostsOfEvent result = new FetchRepostsOfEvent(eventId, createdAfterMillis);
    initialize(result);
    return result;
  }

  public class FetchRepostsOfEvent extends ZeppaeventendpointRequest<com.minook.zeppa.zeppaeventendpoint.model.CollectionResponseZeppaEvent> {

    private static final String REST_PATH = "fetchRepostsOfEvent/{eventId}/{createdAfterMillis}";

    /**
     * Create a request for the method "fetchRepostsOfEvent".
     *
     * This request holds the parameters needed by the the zeppaeventendpoint server.  After setting
     * any optional parameters, call the {@link FetchRepostsOfEvent#execute()} method to invoke the
     * remote operation. <p> {@link FetchRepostsOfEvent#initialize(com.google.api.client.googleapis.se
     * rvices.AbstractGoogleClientRequest)} must be called to initialize this instance immediately
     * after invoking the constructor. </p>
     *
     * @param eventId
     * @param createdAfterMillis
     * @since 1.13
     */
    protected FetchRepostsOfEvent(java.lang.Long eventId, java.lang.Long createdAfterMillis) {
      super(Zeppaeventendpoint.this, "POST", REST_PATH, null, com.minook.zeppa.zeppaeventendpoint.model.CollectionResponseZeppaEvent.class);
      this.eventId = com.google.api.client.util.Preconditions.checkNotNull(eventId, "Required parameter eventId must be specified.");
      this.createdAfterMillis = com.google.api.client.util.Preconditions.checkNotNull(createdAfterMillis, "Required parameter createdAfterMillis must be specified.");
    }

    @Override
    public FetchRepostsOfEvent setAlt(java.lang.String alt) {
      return (FetchRepostsOfEvent) super.setAlt(alt);
    }

    @Override
    public FetchRepostsOfEvent setFields(java.lang.String fields) {
      return (FetchRepostsOfEvent) super.setFields(fields);
    }

    @Override
    public FetchRepostsOfEvent setKey(java.lang.String key) {
      return (FetchRepostsOfEvent) super.setKey(key);
    }

    @Override
    public FetchRepostsOfEvent setOauthToken(java.lang.String oauthToken) {
      return (FetchRepostsOfEvent) super.setOauthToken(oauthToken);
    }

    @Override
    public FetchRepostsOfEvent setPrettyPrint(java.lang.Boolean prettyPrint) {
      return (FetchRepostsOfEvent) super.setPrettyPrint(prettyPrint);
    }

    @Override
    public FetchRepostsOfEvent setQuotaUser(java.lang.String quotaUser) {
      return (FetchRepostsOfEvent) super.setQuotaUser(quotaUser);
    }

    @Override
    public FetchRepostsOfEvent setUserIp(java.lang.String userIp) {
      return (FetchRepostsOfEvent) super.setUserIp(userIp);
    }

    @com.google.api.client.util.Key
    private java.lang.Long eventId;

    /**

     */
    public java.lang.Long getEventId() {
      return eventId;
    }

    public FetchRepostsOfEvent setEventId(java.lang.Long eventId) {
      this.eventId = eventId;
      return this;
    }

    @com.google.api.client.util.Key
    private java.lang.Long createdAfterMillis;

    /**

     */
    public java.lang.Long getCreatedAfterMillis() {
      return createdAfterMillis;
    }

    public FetchRepostsOfEvent setCreatedAfterMillis(java.lang.Long createdAfterMillis) {
      this.createdAfterMillis = createdAfterMillis;
      return this;
    }

    @Override
    public FetchRepostsOfEvent set(String parameterName, Object value) {
      return (FetchRepostsOfEvent) super.set(parameterName, value);
    }
  }

  /**
   * Create a request for the method "getZeppaEvent".
   *
   * This request holds the parameters needed by the zeppaeventendpoint server.  After setting any
   * optional parameters, call the {@link GetZeppaEvent#execute()} method to invoke the remote
   * operation.
   *
   * @param id
   * @return the request
   */
  public GetZeppaEvent getZeppaEvent(java.lang.Long id) throws java.io.IOException {
    GetZeppaEvent result = new GetZeppaEvent(id);
    initialize(result);
    return result;
  }

  public class GetZeppaEvent extends ZeppaeventendpointRequest<com.minook.zeppa.zeppaeventendpoint.model.ZeppaEvent> {

    private static final String REST_PATH = "zeppaevent/{id}";

    /**
     * Create a request for the method "getZeppaEvent".
     *
     * This request holds the parameters needed by the the zeppaeventendpoint server.  After setting
     * any optional parameters, call the {@link GetZeppaEvent#execute()} method to invoke the remote
     * operation. <p> {@link GetZeppaEvent#initialize(com.google.api.client.googleapis.services.Abstra
     * ctGoogleClientRequest)} must be called to initialize this instance immediately after invoking
     * the constructor. </p>
     *
     * @param id
     * @since 1.13
     */
    protected GetZeppaEvent(java.lang.Long id) {
      super(Zeppaeventendpoint.this, "GET", REST_PATH, null, com.minook.zeppa.zeppaeventendpoint.model.ZeppaEvent.class);
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
    public GetZeppaEvent setAlt(java.lang.String alt) {
      return (GetZeppaEvent) super.setAlt(alt);
    }

    @Override
    public GetZeppaEvent setFields(java.lang.String fields) {
      return (GetZeppaEvent) super.setFields(fields);
    }

    @Override
    public GetZeppaEvent setKey(java.lang.String key) {
      return (GetZeppaEvent) super.setKey(key);
    }

    @Override
    public GetZeppaEvent setOauthToken(java.lang.String oauthToken) {
      return (GetZeppaEvent) super.setOauthToken(oauthToken);
    }

    @Override
    public GetZeppaEvent setPrettyPrint(java.lang.Boolean prettyPrint) {
      return (GetZeppaEvent) super.setPrettyPrint(prettyPrint);
    }

    @Override
    public GetZeppaEvent setQuotaUser(java.lang.String quotaUser) {
      return (GetZeppaEvent) super.setQuotaUser(quotaUser);
    }

    @Override
    public GetZeppaEvent setUserIp(java.lang.String userIp) {
      return (GetZeppaEvent) super.setUserIp(userIp);
    }

    @com.google.api.client.util.Key
    private java.lang.Long id;

    /**

     */
    public java.lang.Long getId() {
      return id;
    }

    public GetZeppaEvent setId(java.lang.Long id) {
      this.id = id;
      return this;
    }

    @Override
    public GetZeppaEvent set(String parameterName, Object value) {
      return (GetZeppaEvent) super.set(parameterName, value);
    }
  }

  /**
   * Create a request for the method "insertZeppaEvent".
   *
   * This request holds the parameters needed by the zeppaeventendpoint server.  After setting any
   * optional parameters, call the {@link InsertZeppaEvent#execute()} method to invoke the remote
   * operation.
   *
   * @param content the {@link com.minook.zeppa.zeppaeventendpoint.model.ZeppaEvent}
   * @return the request
   */
  public InsertZeppaEvent insertZeppaEvent(com.minook.zeppa.zeppaeventendpoint.model.ZeppaEvent content) throws java.io.IOException {
    InsertZeppaEvent result = new InsertZeppaEvent(content);
    initialize(result);
    return result;
  }

  public class InsertZeppaEvent extends ZeppaeventendpointRequest<com.minook.zeppa.zeppaeventendpoint.model.ZeppaEvent> {

    private static final String REST_PATH = "zeppaevent";

    /**
     * Create a request for the method "insertZeppaEvent".
     *
     * This request holds the parameters needed by the the zeppaeventendpoint server.  After setting
     * any optional parameters, call the {@link InsertZeppaEvent#execute()} method to invoke the
     * remote operation. <p> {@link InsertZeppaEvent#initialize(com.google.api.client.googleapis.servi
     * ces.AbstractGoogleClientRequest)} must be called to initialize this instance immediately after
     * invoking the constructor. </p>
     *
     * @param content the {@link com.minook.zeppa.zeppaeventendpoint.model.ZeppaEvent}
     * @since 1.13
     */
    protected InsertZeppaEvent(com.minook.zeppa.zeppaeventendpoint.model.ZeppaEvent content) {
      super(Zeppaeventendpoint.this, "POST", REST_PATH, content, com.minook.zeppa.zeppaeventendpoint.model.ZeppaEvent.class);
    }

    @Override
    public InsertZeppaEvent setAlt(java.lang.String alt) {
      return (InsertZeppaEvent) super.setAlt(alt);
    }

    @Override
    public InsertZeppaEvent setFields(java.lang.String fields) {
      return (InsertZeppaEvent) super.setFields(fields);
    }

    @Override
    public InsertZeppaEvent setKey(java.lang.String key) {
      return (InsertZeppaEvent) super.setKey(key);
    }

    @Override
    public InsertZeppaEvent setOauthToken(java.lang.String oauthToken) {
      return (InsertZeppaEvent) super.setOauthToken(oauthToken);
    }

    @Override
    public InsertZeppaEvent setPrettyPrint(java.lang.Boolean prettyPrint) {
      return (InsertZeppaEvent) super.setPrettyPrint(prettyPrint);
    }

    @Override
    public InsertZeppaEvent setQuotaUser(java.lang.String quotaUser) {
      return (InsertZeppaEvent) super.setQuotaUser(quotaUser);
    }

    @Override
    public InsertZeppaEvent setUserIp(java.lang.String userIp) {
      return (InsertZeppaEvent) super.setUserIp(userIp);
    }

    @Override
    public InsertZeppaEvent set(String parameterName, Object value) {
      return (InsertZeppaEvent) super.set(parameterName, value);
    }
  }

  /**
   * Create a request for the method "listZeppaEvent".
   *
   * This request holds the parameters needed by the zeppaeventendpoint server.  After setting any
   * optional parameters, call the {@link ListZeppaEvent#execute()} method to invoke the remote
   * operation.
   *
   * @return the request
   */
  public ListZeppaEvent listZeppaEvent() throws java.io.IOException {
    ListZeppaEvent result = new ListZeppaEvent();
    initialize(result);
    return result;
  }

  public class ListZeppaEvent extends ZeppaeventendpointRequest<com.minook.zeppa.zeppaeventendpoint.model.CollectionResponseZeppaEvent> {

    private static final String REST_PATH = "zeppaevent";

    /**
     * Create a request for the method "listZeppaEvent".
     *
     * This request holds the parameters needed by the the zeppaeventendpoint server.  After setting
     * any optional parameters, call the {@link ListZeppaEvent#execute()} method to invoke the remote
     * operation. <p> {@link ListZeppaEvent#initialize(com.google.api.client.googleapis.services.Abstr
     * actGoogleClientRequest)} must be called to initialize this instance immediately after invoking
     * the constructor. </p>
     *
     * @since 1.13
     */
    protected ListZeppaEvent() {
      super(Zeppaeventendpoint.this, "GET", REST_PATH, null, com.minook.zeppa.zeppaeventendpoint.model.CollectionResponseZeppaEvent.class);
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
    public ListZeppaEvent setAlt(java.lang.String alt) {
      return (ListZeppaEvent) super.setAlt(alt);
    }

    @Override
    public ListZeppaEvent setFields(java.lang.String fields) {
      return (ListZeppaEvent) super.setFields(fields);
    }

    @Override
    public ListZeppaEvent setKey(java.lang.String key) {
      return (ListZeppaEvent) super.setKey(key);
    }

    @Override
    public ListZeppaEvent setOauthToken(java.lang.String oauthToken) {
      return (ListZeppaEvent) super.setOauthToken(oauthToken);
    }

    @Override
    public ListZeppaEvent setPrettyPrint(java.lang.Boolean prettyPrint) {
      return (ListZeppaEvent) super.setPrettyPrint(prettyPrint);
    }

    @Override
    public ListZeppaEvent setQuotaUser(java.lang.String quotaUser) {
      return (ListZeppaEvent) super.setQuotaUser(quotaUser);
    }

    @Override
    public ListZeppaEvent setUserIp(java.lang.String userIp) {
      return (ListZeppaEvent) super.setUserIp(userIp);
    }

    @com.google.api.client.util.Key
    private java.lang.String cursor;

    /**

     */
    public java.lang.String getCursor() {
      return cursor;
    }

    public ListZeppaEvent setCursor(java.lang.String cursor) {
      this.cursor = cursor;
      return this;
    }

    @com.google.api.client.util.Key
    private java.lang.Integer limit;

    /**

     */
    public java.lang.Integer getLimit() {
      return limit;
    }

    public ListZeppaEvent setLimit(java.lang.Integer limit) {
      this.limit = limit;
      return this;
    }

    @Override
    public ListZeppaEvent set(String parameterName, Object value) {
      return (ListZeppaEvent) super.set(parameterName, value);
    }
  }

  /**
   * Create a request for the method "removeZeppaEvent".
   *
   * This request holds the parameters needed by the zeppaeventendpoint server.  After setting any
   * optional parameters, call the {@link RemoveZeppaEvent#execute()} method to invoke the remote
   * operation.
   *
   * @param id
   * @return the request
   */
  public RemoveZeppaEvent removeZeppaEvent(java.lang.Long id) throws java.io.IOException {
    RemoveZeppaEvent result = new RemoveZeppaEvent(id);
    initialize(result);
    return result;
  }

  public class RemoveZeppaEvent extends ZeppaeventendpointRequest<Void> {

    private static final String REST_PATH = "zeppaevent/{id}";

    /**
     * Create a request for the method "removeZeppaEvent".
     *
     * This request holds the parameters needed by the the zeppaeventendpoint server.  After setting
     * any optional parameters, call the {@link RemoveZeppaEvent#execute()} method to invoke the
     * remote operation. <p> {@link RemoveZeppaEvent#initialize(com.google.api.client.googleapis.servi
     * ces.AbstractGoogleClientRequest)} must be called to initialize this instance immediately after
     * invoking the constructor. </p>
     *
     * @param id
     * @since 1.13
     */
    protected RemoveZeppaEvent(java.lang.Long id) {
      super(Zeppaeventendpoint.this, "DELETE", REST_PATH, null, Void.class);
      this.id = com.google.api.client.util.Preconditions.checkNotNull(id, "Required parameter id must be specified.");
    }

    @Override
    public RemoveZeppaEvent setAlt(java.lang.String alt) {
      return (RemoveZeppaEvent) super.setAlt(alt);
    }

    @Override
    public RemoveZeppaEvent setFields(java.lang.String fields) {
      return (RemoveZeppaEvent) super.setFields(fields);
    }

    @Override
    public RemoveZeppaEvent setKey(java.lang.String key) {
      return (RemoveZeppaEvent) super.setKey(key);
    }

    @Override
    public RemoveZeppaEvent setOauthToken(java.lang.String oauthToken) {
      return (RemoveZeppaEvent) super.setOauthToken(oauthToken);
    }

    @Override
    public RemoveZeppaEvent setPrettyPrint(java.lang.Boolean prettyPrint) {
      return (RemoveZeppaEvent) super.setPrettyPrint(prettyPrint);
    }

    @Override
    public RemoveZeppaEvent setQuotaUser(java.lang.String quotaUser) {
      return (RemoveZeppaEvent) super.setQuotaUser(quotaUser);
    }

    @Override
    public RemoveZeppaEvent setUserIp(java.lang.String userIp) {
      return (RemoveZeppaEvent) super.setUserIp(userIp);
    }

    @com.google.api.client.util.Key
    private java.lang.Long id;

    /**

     */
    public java.lang.Long getId() {
      return id;
    }

    public RemoveZeppaEvent setId(java.lang.Long id) {
      this.id = id;
      return this;
    }

    @Override
    public RemoveZeppaEvent set(String parameterName, Object value) {
      return (RemoveZeppaEvent) super.set(parameterName, value);
    }
  }

  /**
   * Create a request for the method "updateZeppaEvent".
   *
   * This request holds the parameters needed by the zeppaeventendpoint server.  After setting any
   * optional parameters, call the {@link UpdateZeppaEvent#execute()} method to invoke the remote
   * operation.
   *
   * @param content the {@link com.minook.zeppa.zeppaeventendpoint.model.ZeppaEvent}
   * @return the request
   */
  public UpdateZeppaEvent updateZeppaEvent(com.minook.zeppa.zeppaeventendpoint.model.ZeppaEvent content) throws java.io.IOException {
    UpdateZeppaEvent result = new UpdateZeppaEvent(content);
    initialize(result);
    return result;
  }

  public class UpdateZeppaEvent extends ZeppaeventendpointRequest<com.minook.zeppa.zeppaeventendpoint.model.ZeppaEvent> {

    private static final String REST_PATH = "zeppaevent";

    /**
     * Create a request for the method "updateZeppaEvent".
     *
     * This request holds the parameters needed by the the zeppaeventendpoint server.  After setting
     * any optional parameters, call the {@link UpdateZeppaEvent#execute()} method to invoke the
     * remote operation. <p> {@link UpdateZeppaEvent#initialize(com.google.api.client.googleapis.servi
     * ces.AbstractGoogleClientRequest)} must be called to initialize this instance immediately after
     * invoking the constructor. </p>
     *
     * @param content the {@link com.minook.zeppa.zeppaeventendpoint.model.ZeppaEvent}
     * @since 1.13
     */
    protected UpdateZeppaEvent(com.minook.zeppa.zeppaeventendpoint.model.ZeppaEvent content) {
      super(Zeppaeventendpoint.this, "PUT", REST_PATH, content, com.minook.zeppa.zeppaeventendpoint.model.ZeppaEvent.class);
    }

    @Override
    public UpdateZeppaEvent setAlt(java.lang.String alt) {
      return (UpdateZeppaEvent) super.setAlt(alt);
    }

    @Override
    public UpdateZeppaEvent setFields(java.lang.String fields) {
      return (UpdateZeppaEvent) super.setFields(fields);
    }

    @Override
    public UpdateZeppaEvent setKey(java.lang.String key) {
      return (UpdateZeppaEvent) super.setKey(key);
    }

    @Override
    public UpdateZeppaEvent setOauthToken(java.lang.String oauthToken) {
      return (UpdateZeppaEvent) super.setOauthToken(oauthToken);
    }

    @Override
    public UpdateZeppaEvent setPrettyPrint(java.lang.Boolean prettyPrint) {
      return (UpdateZeppaEvent) super.setPrettyPrint(prettyPrint);
    }

    @Override
    public UpdateZeppaEvent setQuotaUser(java.lang.String quotaUser) {
      return (UpdateZeppaEvent) super.setQuotaUser(quotaUser);
    }

    @Override
    public UpdateZeppaEvent setUserIp(java.lang.String userIp) {
      return (UpdateZeppaEvent) super.setUserIp(userIp);
    }

    @Override
    public UpdateZeppaEvent set(String parameterName, Object value) {
      return (UpdateZeppaEvent) super.set(parameterName, value);
    }
  }

  /**
   * Builder for {@link Zeppaeventendpoint}.
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

    /** Builds a new instance of {@link Zeppaeventendpoint}. */
    @Override
    public Zeppaeventendpoint build() {
      return new Zeppaeventendpoint(this);
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
     * Set the {@link ZeppaeventendpointRequestInitializer}.
     *
     * @since 1.12
     */
    public Builder setZeppaeventendpointRequestInitializer(
        ZeppaeventendpointRequestInitializer zeppaeventendpointRequestInitializer) {
      return (Builder) super.setGoogleClientRequestInitializer(zeppaeventendpointRequestInitializer);
    }

    @Override
    public Builder setGoogleClientRequestInitializer(
        com.google.api.client.googleapis.services.GoogleClientRequestInitializer googleClientRequestInitializer) {
      return (Builder) super.setGoogleClientRequestInitializer(googleClientRequestInitializer);
    }
  }
}
