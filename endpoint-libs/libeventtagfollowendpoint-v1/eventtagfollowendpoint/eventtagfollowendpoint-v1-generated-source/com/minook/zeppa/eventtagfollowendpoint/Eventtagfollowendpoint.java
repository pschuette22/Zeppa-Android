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
 * on 2014-08-19 at 19:45:52 UTC 
 * Modify at your own risk.
 */

package com.minook.zeppa.eventtagfollowendpoint;

/**
 * Service definition for Eventtagfollowendpoint (v1).
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
 * This service uses {@link EventtagfollowendpointRequestInitializer} to initialize global parameters via its
 * {@link Builder}.
 * </p>
 *
 * @since 1.3
 * @author Google, Inc.
 */
@SuppressWarnings("javadoc")
public class Eventtagfollowendpoint extends com.google.api.client.googleapis.services.json.AbstractGoogleJsonClient {

  // Note: Leave this static initializer at the top of the file.
  static {
    com.google.api.client.util.Preconditions.checkState(
        com.google.api.client.googleapis.GoogleUtils.MAJOR_VERSION == 1 &&
        com.google.api.client.googleapis.GoogleUtils.MINOR_VERSION >= 15,
        "You are currently running with version %s of google-api-client. " +
        "You need at least version 1.15 of google-api-client to run version " +
        "1.18.0-rc of the eventtagfollowendpoint library.", com.google.api.client.googleapis.GoogleUtils.VERSION);
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
  public static final String DEFAULT_SERVICE_PATH = "eventtagfollowendpoint/v1/";

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
  public Eventtagfollowendpoint(com.google.api.client.http.HttpTransport transport, com.google.api.client.json.JsonFactory jsonFactory,
      com.google.api.client.http.HttpRequestInitializer httpRequestInitializer) {
    this(new Builder(transport, jsonFactory, httpRequestInitializer));
  }

  /**
   * @param builder builder
   */
  Eventtagfollowendpoint(Builder builder) {
    super(builder);
  }

  @Override
  protected void initialize(com.google.api.client.googleapis.services.AbstractGoogleClientRequest<?> httpClientRequest) throws java.io.IOException {
    super.initialize(httpClientRequest);
  }

  /**
   * An accessor for creating requests from the EventTagFollowEndpoint collection.
   *
   * <p>The typical use is:</p>
   * <pre>
   *   {@code Eventtagfollowendpoint eventtagfollowendpoint = new Eventtagfollowendpoint(...);}
   *   {@code Eventtagfollowendpoint.EventTagFollowEndpoint.List request = eventtagfollowendpoint.eventTagFollowEndpoint().list(parameters ...)}
   * </pre>
   *
   * @return the resource collection
   */
  public EventTagFollowEndpoint eventTagFollowEndpoint() {
    return new EventTagFollowEndpoint();
  }

  /**
   * The "eventTagFollowEndpoint" collection of methods.
   */
  public class EventTagFollowEndpoint {

    /**
     * Create a request for the method "eventTagFollowEndpoint.getUsersFollowingEventTag".
     *
     * This request holds the parameters needed by the eventtagfollowendpoint server.  After setting any
     * optional parameters, call the {@link GetUsersFollowingEventTag#execute()} method to invoke the
     * remote operation.
     *
     * @param tagId
     * @return the request
     */
    public GetUsersFollowingEventTag getUsersFollowingEventTag(java.lang.Long tagId) throws java.io.IOException {
      GetUsersFollowingEventTag result = new GetUsersFollowingEventTag(tagId);
      initialize(result);
      return result;
    }

    public class GetUsersFollowingEventTag extends EventtagfollowendpointRequest<com.minook.zeppa.eventtagfollowendpoint.model.CollectionResponseLong> {

      private static final String REST_PATH = "collectionresponse_long/{tagId}";

      /**
       * Create a request for the method "eventTagFollowEndpoint.getUsersFollowingEventTag".
       *
       * This request holds the parameters needed by the the eventtagfollowendpoint server.  After
       * setting any optional parameters, call the {@link GetUsersFollowingEventTag#execute()} method to
       * invoke the remote operation. <p> {@link GetUsersFollowingEventTag#initialize(com.google.api.cli
       * ent.googleapis.services.AbstractGoogleClientRequest)} must be called to initialize this
       * instance immediately after invoking the constructor. </p>
       *
       * @param tagId
       * @since 1.13
       */
      protected GetUsersFollowingEventTag(java.lang.Long tagId) {
        super(Eventtagfollowendpoint.this, "GET", REST_PATH, null, com.minook.zeppa.eventtagfollowendpoint.model.CollectionResponseLong.class);
        this.tagId = com.google.api.client.util.Preconditions.checkNotNull(tagId, "Required parameter tagId must be specified.");
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
      public GetUsersFollowingEventTag setAlt(java.lang.String alt) {
        return (GetUsersFollowingEventTag) super.setAlt(alt);
      }

      @Override
      public GetUsersFollowingEventTag setFields(java.lang.String fields) {
        return (GetUsersFollowingEventTag) super.setFields(fields);
      }

      @Override
      public GetUsersFollowingEventTag setKey(java.lang.String key) {
        return (GetUsersFollowingEventTag) super.setKey(key);
      }

      @Override
      public GetUsersFollowingEventTag setOauthToken(java.lang.String oauthToken) {
        return (GetUsersFollowingEventTag) super.setOauthToken(oauthToken);
      }

      @Override
      public GetUsersFollowingEventTag setPrettyPrint(java.lang.Boolean prettyPrint) {
        return (GetUsersFollowingEventTag) super.setPrettyPrint(prettyPrint);
      }

      @Override
      public GetUsersFollowingEventTag setQuotaUser(java.lang.String quotaUser) {
        return (GetUsersFollowingEventTag) super.setQuotaUser(quotaUser);
      }

      @Override
      public GetUsersFollowingEventTag setUserIp(java.lang.String userIp) {
        return (GetUsersFollowingEventTag) super.setUserIp(userIp);
      }

      @com.google.api.client.util.Key
      private java.lang.Long tagId;

      /**

       */
      public java.lang.Long getTagId() {
        return tagId;
      }

      public GetUsersFollowingEventTag setTagId(java.lang.Long tagId) {
        this.tagId = tagId;
        return this;
      }

      @Override
      public GetUsersFollowingEventTag set(String parameterName, Object value) {
        return (GetUsersFollowingEventTag) super.set(parameterName, value);
      }
    }

  }

  /**
   * Create a request for the method "fetchEventTagFollowForUser".
   *
   * This request holds the parameters needed by the eventtagfollowendpoint server.  After setting any
   * optional parameters, call the {@link FetchEventTagFollowForUser#execute()} method to invoke the
   * remote operation.
   *
   * @param eventTagId
   * @param zeppaUserId
   * @return the request
   */
  public FetchEventTagFollowForUser fetchEventTagFollowForUser(java.lang.Long eventTagId, java.lang.Long zeppaUserId) throws java.io.IOException {
    FetchEventTagFollowForUser result = new FetchEventTagFollowForUser(eventTagId, zeppaUserId);
    initialize(result);
    return result;
  }

  public class FetchEventTagFollowForUser extends EventtagfollowendpointRequest<com.minook.zeppa.eventtagfollowendpoint.model.EventTagFollow> {

    private static final String REST_PATH = "fetchEventTagFollowForUser/{EventTagId}/{ZeppaUserId}";

    /**
     * Create a request for the method "fetchEventTagFollowForUser".
     *
     * This request holds the parameters needed by the the eventtagfollowendpoint server.  After
     * setting any optional parameters, call the {@link FetchEventTagFollowForUser#execute()} method
     * to invoke the remote operation. <p> {@link FetchEventTagFollowForUser#initialize(com.google.api
     * .client.googleapis.services.AbstractGoogleClientRequest)} must be called to initialize this
     * instance immediately after invoking the constructor. </p>
     *
     * @param eventTagId
     * @param zeppaUserId
     * @since 1.13
     */
    protected FetchEventTagFollowForUser(java.lang.Long eventTagId, java.lang.Long zeppaUserId) {
      super(Eventtagfollowendpoint.this, "POST", REST_PATH, null, com.minook.zeppa.eventtagfollowendpoint.model.EventTagFollow.class);
      this.eventTagId = com.google.api.client.util.Preconditions.checkNotNull(eventTagId, "Required parameter eventTagId must be specified.");
      this.zeppaUserId = com.google.api.client.util.Preconditions.checkNotNull(zeppaUserId, "Required parameter zeppaUserId must be specified.");
    }

    @Override
    public FetchEventTagFollowForUser setAlt(java.lang.String alt) {
      return (FetchEventTagFollowForUser) super.setAlt(alt);
    }

    @Override
    public FetchEventTagFollowForUser setFields(java.lang.String fields) {
      return (FetchEventTagFollowForUser) super.setFields(fields);
    }

    @Override
    public FetchEventTagFollowForUser setKey(java.lang.String key) {
      return (FetchEventTagFollowForUser) super.setKey(key);
    }

    @Override
    public FetchEventTagFollowForUser setOauthToken(java.lang.String oauthToken) {
      return (FetchEventTagFollowForUser) super.setOauthToken(oauthToken);
    }

    @Override
    public FetchEventTagFollowForUser setPrettyPrint(java.lang.Boolean prettyPrint) {
      return (FetchEventTagFollowForUser) super.setPrettyPrint(prettyPrint);
    }

    @Override
    public FetchEventTagFollowForUser setQuotaUser(java.lang.String quotaUser) {
      return (FetchEventTagFollowForUser) super.setQuotaUser(quotaUser);
    }

    @Override
    public FetchEventTagFollowForUser setUserIp(java.lang.String userIp) {
      return (FetchEventTagFollowForUser) super.setUserIp(userIp);
    }

    @com.google.api.client.util.Key("EventTagId")
    private java.lang.Long eventTagId;

    /**

     */
    public java.lang.Long getEventTagId() {
      return eventTagId;
    }

    public FetchEventTagFollowForUser setEventTagId(java.lang.Long eventTagId) {
      this.eventTagId = eventTagId;
      return this;
    }

    @com.google.api.client.util.Key("ZeppaUserId")
    private java.lang.Long zeppaUserId;

    /**

     */
    public java.lang.Long getZeppaUserId() {
      return zeppaUserId;
    }

    public FetchEventTagFollowForUser setZeppaUserId(java.lang.Long zeppaUserId) {
      this.zeppaUserId = zeppaUserId;
      return this;
    }

    @Override
    public FetchEventTagFollowForUser set(String parameterName, Object value) {
      return (FetchEventTagFollowForUser) super.set(parameterName, value);
    }
  }

  /**
   * Create a request for the method "getEventTagFollow".
   *
   * This request holds the parameters needed by the eventtagfollowendpoint server.  After setting any
   * optional parameters, call the {@link GetEventTagFollow#execute()} method to invoke the remote
   * operation.
   *
   * @param id
   * @return the request
   */
  public GetEventTagFollow getEventTagFollow(java.lang.Long id) throws java.io.IOException {
    GetEventTagFollow result = new GetEventTagFollow(id);
    initialize(result);
    return result;
  }

  public class GetEventTagFollow extends EventtagfollowendpointRequest<com.minook.zeppa.eventtagfollowendpoint.model.EventTagFollow> {

    private static final String REST_PATH = "eventtagfollow/{id}";

    /**
     * Create a request for the method "getEventTagFollow".
     *
     * This request holds the parameters needed by the the eventtagfollowendpoint server.  After
     * setting any optional parameters, call the {@link GetEventTagFollow#execute()} method to invoke
     * the remote operation. <p> {@link GetEventTagFollow#initialize(com.google.api.client.googleapis.
     * services.AbstractGoogleClientRequest)} must be called to initialize this instance immediately
     * after invoking the constructor. </p>
     *
     * @param id
     * @since 1.13
     */
    protected GetEventTagFollow(java.lang.Long id) {
      super(Eventtagfollowendpoint.this, "GET", REST_PATH, null, com.minook.zeppa.eventtagfollowendpoint.model.EventTagFollow.class);
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
    public GetEventTagFollow setAlt(java.lang.String alt) {
      return (GetEventTagFollow) super.setAlt(alt);
    }

    @Override
    public GetEventTagFollow setFields(java.lang.String fields) {
      return (GetEventTagFollow) super.setFields(fields);
    }

    @Override
    public GetEventTagFollow setKey(java.lang.String key) {
      return (GetEventTagFollow) super.setKey(key);
    }

    @Override
    public GetEventTagFollow setOauthToken(java.lang.String oauthToken) {
      return (GetEventTagFollow) super.setOauthToken(oauthToken);
    }

    @Override
    public GetEventTagFollow setPrettyPrint(java.lang.Boolean prettyPrint) {
      return (GetEventTagFollow) super.setPrettyPrint(prettyPrint);
    }

    @Override
    public GetEventTagFollow setQuotaUser(java.lang.String quotaUser) {
      return (GetEventTagFollow) super.setQuotaUser(quotaUser);
    }

    @Override
    public GetEventTagFollow setUserIp(java.lang.String userIp) {
      return (GetEventTagFollow) super.setUserIp(userIp);
    }

    @com.google.api.client.util.Key
    private java.lang.Long id;

    /**

     */
    public java.lang.Long getId() {
      return id;
    }

    public GetEventTagFollow setId(java.lang.Long id) {
      this.id = id;
      return this;
    }

    @Override
    public GetEventTagFollow set(String parameterName, Object value) {
      return (GetEventTagFollow) super.set(parameterName, value);
    }
  }

  /**
   * Create a request for the method "insertEventTagFollow".
   *
   * This request holds the parameters needed by the eventtagfollowendpoint server.  After setting any
   * optional parameters, call the {@link InsertEventTagFollow#execute()} method to invoke the remote
   * operation.
   *
   * @param content the {@link com.minook.zeppa.eventtagfollowendpoint.model.EventTagFollow}
   * @return the request
   */
  public InsertEventTagFollow insertEventTagFollow(com.minook.zeppa.eventtagfollowendpoint.model.EventTagFollow content) throws java.io.IOException {
    InsertEventTagFollow result = new InsertEventTagFollow(content);
    initialize(result);
    return result;
  }

  public class InsertEventTagFollow extends EventtagfollowendpointRequest<com.minook.zeppa.eventtagfollowendpoint.model.EventTagFollow> {

    private static final String REST_PATH = "eventtagfollow";

    /**
     * Create a request for the method "insertEventTagFollow".
     *
     * This request holds the parameters needed by the the eventtagfollowendpoint server.  After
     * setting any optional parameters, call the {@link InsertEventTagFollow#execute()} method to
     * invoke the remote operation. <p> {@link InsertEventTagFollow#initialize(com.google.api.client.g
     * oogleapis.services.AbstractGoogleClientRequest)} must be called to initialize this instance
     * immediately after invoking the constructor. </p>
     *
     * @param content the {@link com.minook.zeppa.eventtagfollowendpoint.model.EventTagFollow}
     * @since 1.13
     */
    protected InsertEventTagFollow(com.minook.zeppa.eventtagfollowendpoint.model.EventTagFollow content) {
      super(Eventtagfollowendpoint.this, "POST", REST_PATH, content, com.minook.zeppa.eventtagfollowendpoint.model.EventTagFollow.class);
    }

    @Override
    public InsertEventTagFollow setAlt(java.lang.String alt) {
      return (InsertEventTagFollow) super.setAlt(alt);
    }

    @Override
    public InsertEventTagFollow setFields(java.lang.String fields) {
      return (InsertEventTagFollow) super.setFields(fields);
    }

    @Override
    public InsertEventTagFollow setKey(java.lang.String key) {
      return (InsertEventTagFollow) super.setKey(key);
    }

    @Override
    public InsertEventTagFollow setOauthToken(java.lang.String oauthToken) {
      return (InsertEventTagFollow) super.setOauthToken(oauthToken);
    }

    @Override
    public InsertEventTagFollow setPrettyPrint(java.lang.Boolean prettyPrint) {
      return (InsertEventTagFollow) super.setPrettyPrint(prettyPrint);
    }

    @Override
    public InsertEventTagFollow setQuotaUser(java.lang.String quotaUser) {
      return (InsertEventTagFollow) super.setQuotaUser(quotaUser);
    }

    @Override
    public InsertEventTagFollow setUserIp(java.lang.String userIp) {
      return (InsertEventTagFollow) super.setUserIp(userIp);
    }

    @Override
    public InsertEventTagFollow set(String parameterName, Object value) {
      return (InsertEventTagFollow) super.set(parameterName, value);
    }
  }

  /**
   * Create a request for the method "listEventTagFollow".
   *
   * This request holds the parameters needed by the eventtagfollowendpoint server.  After setting any
   * optional parameters, call the {@link ListEventTagFollow#execute()} method to invoke the remote
   * operation.
   *
   * @return the request
   */
  public ListEventTagFollow listEventTagFollow() throws java.io.IOException {
    ListEventTagFollow result = new ListEventTagFollow();
    initialize(result);
    return result;
  }

  public class ListEventTagFollow extends EventtagfollowendpointRequest<com.minook.zeppa.eventtagfollowendpoint.model.CollectionResponseEventTagFollow> {

    private static final String REST_PATH = "eventtagfollow";

    /**
     * Create a request for the method "listEventTagFollow".
     *
     * This request holds the parameters needed by the the eventtagfollowendpoint server.  After
     * setting any optional parameters, call the {@link ListEventTagFollow#execute()} method to invoke
     * the remote operation. <p> {@link ListEventTagFollow#initialize(com.google.api.client.googleapis
     * .services.AbstractGoogleClientRequest)} must be called to initialize this instance immediately
     * after invoking the constructor. </p>
     *
     * @since 1.13
     */
    protected ListEventTagFollow() {
      super(Eventtagfollowendpoint.this, "GET", REST_PATH, null, com.minook.zeppa.eventtagfollowendpoint.model.CollectionResponseEventTagFollow.class);
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
    public ListEventTagFollow setAlt(java.lang.String alt) {
      return (ListEventTagFollow) super.setAlt(alt);
    }

    @Override
    public ListEventTagFollow setFields(java.lang.String fields) {
      return (ListEventTagFollow) super.setFields(fields);
    }

    @Override
    public ListEventTagFollow setKey(java.lang.String key) {
      return (ListEventTagFollow) super.setKey(key);
    }

    @Override
    public ListEventTagFollow setOauthToken(java.lang.String oauthToken) {
      return (ListEventTagFollow) super.setOauthToken(oauthToken);
    }

    @Override
    public ListEventTagFollow setPrettyPrint(java.lang.Boolean prettyPrint) {
      return (ListEventTagFollow) super.setPrettyPrint(prettyPrint);
    }

    @Override
    public ListEventTagFollow setQuotaUser(java.lang.String quotaUser) {
      return (ListEventTagFollow) super.setQuotaUser(quotaUser);
    }

    @Override
    public ListEventTagFollow setUserIp(java.lang.String userIp) {
      return (ListEventTagFollow) super.setUserIp(userIp);
    }

    @com.google.api.client.util.Key
    private java.lang.String cursor;

    /**

     */
    public java.lang.String getCursor() {
      return cursor;
    }

    public ListEventTagFollow setCursor(java.lang.String cursor) {
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

    public ListEventTagFollow setLimit(java.lang.Integer limit) {
      this.limit = limit;
      return this;
    }

    @Override
    public ListEventTagFollow set(String parameterName, Object value) {
      return (ListEventTagFollow) super.set(parameterName, value);
    }
  }

  /**
   * Create a request for the method "removeEventTagFollow".
   *
   * This request holds the parameters needed by the eventtagfollowendpoint server.  After setting any
   * optional parameters, call the {@link RemoveEventTagFollow#execute()} method to invoke the remote
   * operation.
   *
   * @param id
   * @return the request
   */
  public RemoveEventTagFollow removeEventTagFollow(java.lang.Long id) throws java.io.IOException {
    RemoveEventTagFollow result = new RemoveEventTagFollow(id);
    initialize(result);
    return result;
  }

  public class RemoveEventTagFollow extends EventtagfollowendpointRequest<Void> {

    private static final String REST_PATH = "eventtagfollow/{id}";

    /**
     * Create a request for the method "removeEventTagFollow".
     *
     * This request holds the parameters needed by the the eventtagfollowendpoint server.  After
     * setting any optional parameters, call the {@link RemoveEventTagFollow#execute()} method to
     * invoke the remote operation. <p> {@link RemoveEventTagFollow#initialize(com.google.api.client.g
     * oogleapis.services.AbstractGoogleClientRequest)} must be called to initialize this instance
     * immediately after invoking the constructor. </p>
     *
     * @param id
     * @since 1.13
     */
    protected RemoveEventTagFollow(java.lang.Long id) {
      super(Eventtagfollowendpoint.this, "DELETE", REST_PATH, null, Void.class);
      this.id = com.google.api.client.util.Preconditions.checkNotNull(id, "Required parameter id must be specified.");
    }

    @Override
    public RemoveEventTagFollow setAlt(java.lang.String alt) {
      return (RemoveEventTagFollow) super.setAlt(alt);
    }

    @Override
    public RemoveEventTagFollow setFields(java.lang.String fields) {
      return (RemoveEventTagFollow) super.setFields(fields);
    }

    @Override
    public RemoveEventTagFollow setKey(java.lang.String key) {
      return (RemoveEventTagFollow) super.setKey(key);
    }

    @Override
    public RemoveEventTagFollow setOauthToken(java.lang.String oauthToken) {
      return (RemoveEventTagFollow) super.setOauthToken(oauthToken);
    }

    @Override
    public RemoveEventTagFollow setPrettyPrint(java.lang.Boolean prettyPrint) {
      return (RemoveEventTagFollow) super.setPrettyPrint(prettyPrint);
    }

    @Override
    public RemoveEventTagFollow setQuotaUser(java.lang.String quotaUser) {
      return (RemoveEventTagFollow) super.setQuotaUser(quotaUser);
    }

    @Override
    public RemoveEventTagFollow setUserIp(java.lang.String userIp) {
      return (RemoveEventTagFollow) super.setUserIp(userIp);
    }

    @com.google.api.client.util.Key
    private java.lang.Long id;

    /**

     */
    public java.lang.Long getId() {
      return id;
    }

    public RemoveEventTagFollow setId(java.lang.Long id) {
      this.id = id;
      return this;
    }

    @Override
    public RemoveEventTagFollow set(String parameterName, Object value) {
      return (RemoveEventTagFollow) super.set(parameterName, value);
    }
  }

  /**
   * Create a request for the method "updateEventTagFollow".
   *
   * This request holds the parameters needed by the eventtagfollowendpoint server.  After setting any
   * optional parameters, call the {@link UpdateEventTagFollow#execute()} method to invoke the remote
   * operation.
   *
   * @param content the {@link com.minook.zeppa.eventtagfollowendpoint.model.EventTagFollow}
   * @return the request
   */
  public UpdateEventTagFollow updateEventTagFollow(com.minook.zeppa.eventtagfollowendpoint.model.EventTagFollow content) throws java.io.IOException {
    UpdateEventTagFollow result = new UpdateEventTagFollow(content);
    initialize(result);
    return result;
  }

  public class UpdateEventTagFollow extends EventtagfollowendpointRequest<com.minook.zeppa.eventtagfollowendpoint.model.EventTagFollow> {

    private static final String REST_PATH = "eventtagfollow";

    /**
     * Create a request for the method "updateEventTagFollow".
     *
     * This request holds the parameters needed by the the eventtagfollowendpoint server.  After
     * setting any optional parameters, call the {@link UpdateEventTagFollow#execute()} method to
     * invoke the remote operation. <p> {@link UpdateEventTagFollow#initialize(com.google.api.client.g
     * oogleapis.services.AbstractGoogleClientRequest)} must be called to initialize this instance
     * immediately after invoking the constructor. </p>
     *
     * @param content the {@link com.minook.zeppa.eventtagfollowendpoint.model.EventTagFollow}
     * @since 1.13
     */
    protected UpdateEventTagFollow(com.minook.zeppa.eventtagfollowendpoint.model.EventTagFollow content) {
      super(Eventtagfollowendpoint.this, "PUT", REST_PATH, content, com.minook.zeppa.eventtagfollowendpoint.model.EventTagFollow.class);
    }

    @Override
    public UpdateEventTagFollow setAlt(java.lang.String alt) {
      return (UpdateEventTagFollow) super.setAlt(alt);
    }

    @Override
    public UpdateEventTagFollow setFields(java.lang.String fields) {
      return (UpdateEventTagFollow) super.setFields(fields);
    }

    @Override
    public UpdateEventTagFollow setKey(java.lang.String key) {
      return (UpdateEventTagFollow) super.setKey(key);
    }

    @Override
    public UpdateEventTagFollow setOauthToken(java.lang.String oauthToken) {
      return (UpdateEventTagFollow) super.setOauthToken(oauthToken);
    }

    @Override
    public UpdateEventTagFollow setPrettyPrint(java.lang.Boolean prettyPrint) {
      return (UpdateEventTagFollow) super.setPrettyPrint(prettyPrint);
    }

    @Override
    public UpdateEventTagFollow setQuotaUser(java.lang.String quotaUser) {
      return (UpdateEventTagFollow) super.setQuotaUser(quotaUser);
    }

    @Override
    public UpdateEventTagFollow setUserIp(java.lang.String userIp) {
      return (UpdateEventTagFollow) super.setUserIp(userIp);
    }

    @Override
    public UpdateEventTagFollow set(String parameterName, Object value) {
      return (UpdateEventTagFollow) super.set(parameterName, value);
    }
  }

  /**
   * Builder for {@link Eventtagfollowendpoint}.
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

    /** Builds a new instance of {@link Eventtagfollowendpoint}. */
    @Override
    public Eventtagfollowendpoint build() {
      return new Eventtagfollowendpoint(this);
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
     * Set the {@link EventtagfollowendpointRequestInitializer}.
     *
     * @since 1.12
     */
    public Builder setEventtagfollowendpointRequestInitializer(
        EventtagfollowendpointRequestInitializer eventtagfollowendpointRequestInitializer) {
      return (Builder) super.setGoogleClientRequestInitializer(eventtagfollowendpointRequestInitializer);
    }

    @Override
    public Builder setGoogleClientRequestInitializer(
        com.google.api.client.googleapis.services.GoogleClientRequestInitializer googleClientRequestInitializer) {
      return (Builder) super.setGoogleClientRequestInitializer(googleClientRequestInitializer);
    }
  }
}
