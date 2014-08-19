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

package com.minook.zeppa.zeppausertouserrelationshipendpoint;

/**
 * Service definition for Zeppausertouserrelationshipendpoint (v1).
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
 * This service uses {@link ZeppausertouserrelationshipendpointRequestInitializer} to initialize global parameters via its
 * {@link Builder}.
 * </p>
 *
 * @since 1.3
 * @author Google, Inc.
 */
@SuppressWarnings("javadoc")
public class Zeppausertouserrelationshipendpoint extends com.google.api.client.googleapis.services.json.AbstractGoogleJsonClient {

  // Note: Leave this static initializer at the top of the file.
  static {
    com.google.api.client.util.Preconditions.checkState(
        com.google.api.client.googleapis.GoogleUtils.MAJOR_VERSION == 1 &&
        com.google.api.client.googleapis.GoogleUtils.MINOR_VERSION >= 15,
        "You are currently running with version %s of google-api-client. " +
        "You need at least version 1.15 of google-api-client to run version " +
        "1.18.0-rc of the zeppausertouserrelationshipendpoint library.", com.google.api.client.googleapis.GoogleUtils.VERSION);
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
  public static final String DEFAULT_SERVICE_PATH = "zeppausertouserrelationshipendpoint/v1/";

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
  public Zeppausertouserrelationshipendpoint(com.google.api.client.http.HttpTransport transport, com.google.api.client.json.JsonFactory jsonFactory,
      com.google.api.client.http.HttpRequestInitializer httpRequestInitializer) {
    this(new Builder(transport, jsonFactory, httpRequestInitializer));
  }

  /**
   * @param builder builder
   */
  Zeppausertouserrelationshipendpoint(Builder builder) {
    super(builder);
  }

  @Override
  protected void initialize(com.google.api.client.googleapis.services.AbstractGoogleClientRequest<?> httpClientRequest) throws java.io.IOException {
    super.initialize(httpClientRequest);
  }

  /**
   * Create a request for the method "fetchMutualFriendRelationshipList".
   *
   * This request holds the parameters needed by the zeppausertouserrelationshipendpoint server.
   * After setting any optional parameters, call the {@link
   * FetchMutualFriendRelationshipList#execute()} method to invoke the remote operation.
   *
   * @param callingUserId
   * @param requestUserId
   * @param lastCreationTimeMillis
   * @return the request
   */
  public FetchMutualFriendRelationshipList fetchMutualFriendRelationshipList(java.lang.Long callingUserId, java.lang.Long requestUserId, java.lang.Long lastCreationTimeMillis) throws java.io.IOException {
    FetchMutualFriendRelationshipList result = new FetchMutualFriendRelationshipList(callingUserId, requestUserId, lastCreationTimeMillis);
    initialize(result);
    return result;
  }

  public class FetchMutualFriendRelationshipList extends ZeppausertouserrelationshipendpointRequest<com.minook.zeppa.zeppausertouserrelationshipendpoint.model.CollectionResponseZeppaUserToUserRelationship> {

    private static final String REST_PATH = "fetchMutualFriendRelationshipList/{callingUserId}/{requestUserId}/{lastCreationTimeMillis}";

    /**
     * Create a request for the method "fetchMutualFriendRelationshipList".
     *
     * This request holds the parameters needed by the the zeppausertouserrelationshipendpoint server.
     * After setting any optional parameters, call the {@link
     * FetchMutualFriendRelationshipList#execute()} method to invoke the remote operation. <p> {@link
     * FetchMutualFriendRelationshipList#initialize(com.google.api.client.googleapis.services.Abstract
     * GoogleClientRequest)} must be called to initialize this instance immediately after invoking the
     * constructor. </p>
     *
     * @param callingUserId
     * @param requestUserId
     * @param lastCreationTimeMillis
     * @since 1.13
     */
    protected FetchMutualFriendRelationshipList(java.lang.Long callingUserId, java.lang.Long requestUserId, java.lang.Long lastCreationTimeMillis) {
      super(Zeppausertouserrelationshipendpoint.this, "POST", REST_PATH, null, com.minook.zeppa.zeppausertouserrelationshipendpoint.model.CollectionResponseZeppaUserToUserRelationship.class);
      this.callingUserId = com.google.api.client.util.Preconditions.checkNotNull(callingUserId, "Required parameter callingUserId must be specified.");
      this.requestUserId = com.google.api.client.util.Preconditions.checkNotNull(requestUserId, "Required parameter requestUserId must be specified.");
      this.lastCreationTimeMillis = com.google.api.client.util.Preconditions.checkNotNull(lastCreationTimeMillis, "Required parameter lastCreationTimeMillis must be specified.");
    }

    @Override
    public FetchMutualFriendRelationshipList setAlt(java.lang.String alt) {
      return (FetchMutualFriendRelationshipList) super.setAlt(alt);
    }

    @Override
    public FetchMutualFriendRelationshipList setFields(java.lang.String fields) {
      return (FetchMutualFriendRelationshipList) super.setFields(fields);
    }

    @Override
    public FetchMutualFriendRelationshipList setKey(java.lang.String key) {
      return (FetchMutualFriendRelationshipList) super.setKey(key);
    }

    @Override
    public FetchMutualFriendRelationshipList setOauthToken(java.lang.String oauthToken) {
      return (FetchMutualFriendRelationshipList) super.setOauthToken(oauthToken);
    }

    @Override
    public FetchMutualFriendRelationshipList setPrettyPrint(java.lang.Boolean prettyPrint) {
      return (FetchMutualFriendRelationshipList) super.setPrettyPrint(prettyPrint);
    }

    @Override
    public FetchMutualFriendRelationshipList setQuotaUser(java.lang.String quotaUser) {
      return (FetchMutualFriendRelationshipList) super.setQuotaUser(quotaUser);
    }

    @Override
    public FetchMutualFriendRelationshipList setUserIp(java.lang.String userIp) {
      return (FetchMutualFriendRelationshipList) super.setUserIp(userIp);
    }

    @com.google.api.client.util.Key
    private java.lang.Long callingUserId;

    /**

     */
    public java.lang.Long getCallingUserId() {
      return callingUserId;
    }

    public FetchMutualFriendRelationshipList setCallingUserId(java.lang.Long callingUserId) {
      this.callingUserId = callingUserId;
      return this;
    }

    @com.google.api.client.util.Key
    private java.lang.Long requestUserId;

    /**

     */
    public java.lang.Long getRequestUserId() {
      return requestUserId;
    }

    public FetchMutualFriendRelationshipList setRequestUserId(java.lang.Long requestUserId) {
      this.requestUserId = requestUserId;
      return this;
    }

    @com.google.api.client.util.Key
    private java.lang.Long lastCreationTimeMillis;

    /**

     */
    public java.lang.Long getLastCreationTimeMillis() {
      return lastCreationTimeMillis;
    }

    public FetchMutualFriendRelationshipList setLastCreationTimeMillis(java.lang.Long lastCreationTimeMillis) {
      this.lastCreationTimeMillis = lastCreationTimeMillis;
      return this;
    }

    @Override
    public FetchMutualFriendRelationshipList set(String parameterName, Object value) {
      return (FetchMutualFriendRelationshipList) super.set(parameterName, value);
    }
  }

  /**
   * Create a request for the method "fetchUserRelationshipList".
   *
   * This request holds the parameters needed by the zeppausertouserrelationshipendpoint server.
   * After setting any optional parameters, call the {@link FetchUserRelationshipList#execute()}
   * method to invoke the remote operation.
   *
   * @param userId
   * @param minCreationTime
   * @return the request
   */
  public FetchUserRelationshipList fetchUserRelationshipList(java.lang.Long userId, java.lang.Long minCreationTime) throws java.io.IOException {
    FetchUserRelationshipList result = new FetchUserRelationshipList(userId, minCreationTime);
    initialize(result);
    return result;
  }

  public class FetchUserRelationshipList extends ZeppausertouserrelationshipendpointRequest<com.minook.zeppa.zeppausertouserrelationshipendpoint.model.CollectionResponseZeppaUserToUserRelationship> {

    private static final String REST_PATH = "fetchUserRelationshipList/{userId}/{minCreationTime}";

    /**
     * Create a request for the method "fetchUserRelationshipList".
     *
     * This request holds the parameters needed by the the zeppausertouserrelationshipendpoint server.
     * After setting any optional parameters, call the {@link FetchUserRelationshipList#execute()}
     * method to invoke the remote operation. <p> {@link FetchUserRelationshipList#initialize(com.goog
     * le.api.client.googleapis.services.AbstractGoogleClientRequest)} must be called to initialize
     * this instance immediately after invoking the constructor. </p>
     *
     * @param userId
     * @param minCreationTime
     * @since 1.13
     */
    protected FetchUserRelationshipList(java.lang.Long userId, java.lang.Long minCreationTime) {
      super(Zeppausertouserrelationshipendpoint.this, "POST", REST_PATH, null, com.minook.zeppa.zeppausertouserrelationshipendpoint.model.CollectionResponseZeppaUserToUserRelationship.class);
      this.userId = com.google.api.client.util.Preconditions.checkNotNull(userId, "Required parameter userId must be specified.");
      this.minCreationTime = com.google.api.client.util.Preconditions.checkNotNull(minCreationTime, "Required parameter minCreationTime must be specified.");
    }

    @Override
    public FetchUserRelationshipList setAlt(java.lang.String alt) {
      return (FetchUserRelationshipList) super.setAlt(alt);
    }

    @Override
    public FetchUserRelationshipList setFields(java.lang.String fields) {
      return (FetchUserRelationshipList) super.setFields(fields);
    }

    @Override
    public FetchUserRelationshipList setKey(java.lang.String key) {
      return (FetchUserRelationshipList) super.setKey(key);
    }

    @Override
    public FetchUserRelationshipList setOauthToken(java.lang.String oauthToken) {
      return (FetchUserRelationshipList) super.setOauthToken(oauthToken);
    }

    @Override
    public FetchUserRelationshipList setPrettyPrint(java.lang.Boolean prettyPrint) {
      return (FetchUserRelationshipList) super.setPrettyPrint(prettyPrint);
    }

    @Override
    public FetchUserRelationshipList setQuotaUser(java.lang.String quotaUser) {
      return (FetchUserRelationshipList) super.setQuotaUser(quotaUser);
    }

    @Override
    public FetchUserRelationshipList setUserIp(java.lang.String userIp) {
      return (FetchUserRelationshipList) super.setUserIp(userIp);
    }

    @com.google.api.client.util.Key
    private java.lang.Long userId;

    /**

     */
    public java.lang.Long getUserId() {
      return userId;
    }

    public FetchUserRelationshipList setUserId(java.lang.Long userId) {
      this.userId = userId;
      return this;
    }

    @com.google.api.client.util.Key
    private java.lang.Long minCreationTime;

    /**

     */
    public java.lang.Long getMinCreationTime() {
      return minCreationTime;
    }

    public FetchUserRelationshipList setMinCreationTime(java.lang.Long minCreationTime) {
      this.minCreationTime = minCreationTime;
      return this;
    }

    @Override
    public FetchUserRelationshipList set(String parameterName, Object value) {
      return (FetchUserRelationshipList) super.set(parameterName, value);
    }
  }

  /**
   * Create a request for the method "getZeppaUserToUserRelationship".
   *
   * This request holds the parameters needed by the zeppausertouserrelationshipendpoint server.
   * After setting any optional parameters, call the {@link GetZeppaUserToUserRelationship#execute()}
   * method to invoke the remote operation.
   *
   * @param id
   * @return the request
   */
  public GetZeppaUserToUserRelationship getZeppaUserToUserRelationship(java.lang.Long id) throws java.io.IOException {
    GetZeppaUserToUserRelationship result = new GetZeppaUserToUserRelationship(id);
    initialize(result);
    return result;
  }

  public class GetZeppaUserToUserRelationship extends ZeppausertouserrelationshipendpointRequest<com.minook.zeppa.zeppausertouserrelationshipendpoint.model.ZeppaUserToUserRelationship> {

    private static final String REST_PATH = "zeppausertouserrelationship/{id}";

    /**
     * Create a request for the method "getZeppaUserToUserRelationship".
     *
     * This request holds the parameters needed by the the zeppausertouserrelationshipendpoint server.
     * After setting any optional parameters, call the {@link
     * GetZeppaUserToUserRelationship#execute()} method to invoke the remote operation. <p> {@link Get
     * ZeppaUserToUserRelationship#initialize(com.google.api.client.googleapis.services.AbstractGoogle
     * ClientRequest)} must be called to initialize this instance immediately after invoking the
     * constructor. </p>
     *
     * @param id
     * @since 1.13
     */
    protected GetZeppaUserToUserRelationship(java.lang.Long id) {
      super(Zeppausertouserrelationshipendpoint.this, "GET", REST_PATH, null, com.minook.zeppa.zeppausertouserrelationshipendpoint.model.ZeppaUserToUserRelationship.class);
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
    public GetZeppaUserToUserRelationship setAlt(java.lang.String alt) {
      return (GetZeppaUserToUserRelationship) super.setAlt(alt);
    }

    @Override
    public GetZeppaUserToUserRelationship setFields(java.lang.String fields) {
      return (GetZeppaUserToUserRelationship) super.setFields(fields);
    }

    @Override
    public GetZeppaUserToUserRelationship setKey(java.lang.String key) {
      return (GetZeppaUserToUserRelationship) super.setKey(key);
    }

    @Override
    public GetZeppaUserToUserRelationship setOauthToken(java.lang.String oauthToken) {
      return (GetZeppaUserToUserRelationship) super.setOauthToken(oauthToken);
    }

    @Override
    public GetZeppaUserToUserRelationship setPrettyPrint(java.lang.Boolean prettyPrint) {
      return (GetZeppaUserToUserRelationship) super.setPrettyPrint(prettyPrint);
    }

    @Override
    public GetZeppaUserToUserRelationship setQuotaUser(java.lang.String quotaUser) {
      return (GetZeppaUserToUserRelationship) super.setQuotaUser(quotaUser);
    }

    @Override
    public GetZeppaUserToUserRelationship setUserIp(java.lang.String userIp) {
      return (GetZeppaUserToUserRelationship) super.setUserIp(userIp);
    }

    @com.google.api.client.util.Key
    private java.lang.Long id;

    /**

     */
    public java.lang.Long getId() {
      return id;
    }

    public GetZeppaUserToUserRelationship setId(java.lang.Long id) {
      this.id = id;
      return this;
    }

    @Override
    public GetZeppaUserToUserRelationship set(String parameterName, Object value) {
      return (GetZeppaUserToUserRelationship) super.set(parameterName, value);
    }
  }

  /**
   * Create a request for the method "insertZeppaUserToUserRelationship".
   *
   * This request holds the parameters needed by the zeppausertouserrelationshipendpoint server.
   * After setting any optional parameters, call the {@link
   * InsertZeppaUserToUserRelationship#execute()} method to invoke the remote operation.
   *
   * @param content the {@link com.minook.zeppa.zeppausertouserrelationshipendpoint.model.ZeppaUserToUserRelationship}
   * @return the request
   */
  public InsertZeppaUserToUserRelationship insertZeppaUserToUserRelationship(com.minook.zeppa.zeppausertouserrelationshipendpoint.model.ZeppaUserToUserRelationship content) throws java.io.IOException {
    InsertZeppaUserToUserRelationship result = new InsertZeppaUserToUserRelationship(content);
    initialize(result);
    return result;
  }

  public class InsertZeppaUserToUserRelationship extends ZeppausertouserrelationshipendpointRequest<com.minook.zeppa.zeppausertouserrelationshipendpoint.model.ZeppaUserToUserRelationship> {

    private static final String REST_PATH = "zeppausertouserrelationship";

    /**
     * Create a request for the method "insertZeppaUserToUserRelationship".
     *
     * This request holds the parameters needed by the the zeppausertouserrelationshipendpoint server.
     * After setting any optional parameters, call the {@link
     * InsertZeppaUserToUserRelationship#execute()} method to invoke the remote operation. <p> {@link
     * InsertZeppaUserToUserRelationship#initialize(com.google.api.client.googleapis.services.Abstract
     * GoogleClientRequest)} must be called to initialize this instance immediately after invoking the
     * constructor. </p>
     *
     * @param content the {@link com.minook.zeppa.zeppausertouserrelationshipendpoint.model.ZeppaUserToUserRelationship}
     * @since 1.13
     */
    protected InsertZeppaUserToUserRelationship(com.minook.zeppa.zeppausertouserrelationshipendpoint.model.ZeppaUserToUserRelationship content) {
      super(Zeppausertouserrelationshipendpoint.this, "POST", REST_PATH, content, com.minook.zeppa.zeppausertouserrelationshipendpoint.model.ZeppaUserToUserRelationship.class);
    }

    @Override
    public InsertZeppaUserToUserRelationship setAlt(java.lang.String alt) {
      return (InsertZeppaUserToUserRelationship) super.setAlt(alt);
    }

    @Override
    public InsertZeppaUserToUserRelationship setFields(java.lang.String fields) {
      return (InsertZeppaUserToUserRelationship) super.setFields(fields);
    }

    @Override
    public InsertZeppaUserToUserRelationship setKey(java.lang.String key) {
      return (InsertZeppaUserToUserRelationship) super.setKey(key);
    }

    @Override
    public InsertZeppaUserToUserRelationship setOauthToken(java.lang.String oauthToken) {
      return (InsertZeppaUserToUserRelationship) super.setOauthToken(oauthToken);
    }

    @Override
    public InsertZeppaUserToUserRelationship setPrettyPrint(java.lang.Boolean prettyPrint) {
      return (InsertZeppaUserToUserRelationship) super.setPrettyPrint(prettyPrint);
    }

    @Override
    public InsertZeppaUserToUserRelationship setQuotaUser(java.lang.String quotaUser) {
      return (InsertZeppaUserToUserRelationship) super.setQuotaUser(quotaUser);
    }

    @Override
    public InsertZeppaUserToUserRelationship setUserIp(java.lang.String userIp) {
      return (InsertZeppaUserToUserRelationship) super.setUserIp(userIp);
    }

    @Override
    public InsertZeppaUserToUserRelationship set(String parameterName, Object value) {
      return (InsertZeppaUserToUserRelationship) super.set(parameterName, value);
    }
  }

  /**
   * Create a request for the method "listZeppaUserToUserRelationship".
   *
   * This request holds the parameters needed by the zeppausertouserrelationshipendpoint server.
   * After setting any optional parameters, call the {@link ListZeppaUserToUserRelationship#execute()}
   * method to invoke the remote operation.
   *
   * @return the request
   */
  public ListZeppaUserToUserRelationship listZeppaUserToUserRelationship() throws java.io.IOException {
    ListZeppaUserToUserRelationship result = new ListZeppaUserToUserRelationship();
    initialize(result);
    return result;
  }

  public class ListZeppaUserToUserRelationship extends ZeppausertouserrelationshipendpointRequest<com.minook.zeppa.zeppausertouserrelationshipendpoint.model.CollectionResponseZeppaUserToUserRelationship> {

    private static final String REST_PATH = "zeppausertouserrelationship";

    /**
     * Create a request for the method "listZeppaUserToUserRelationship".
     *
     * This request holds the parameters needed by the the zeppausertouserrelationshipendpoint server.
     * After setting any optional parameters, call the {@link
     * ListZeppaUserToUserRelationship#execute()} method to invoke the remote operation. <p> {@link Li
     * stZeppaUserToUserRelationship#initialize(com.google.api.client.googleapis.services.AbstractGoog
     * leClientRequest)} must be called to initialize this instance immediately after invoking the
     * constructor. </p>
     *
     * @since 1.13
     */
    protected ListZeppaUserToUserRelationship() {
      super(Zeppausertouserrelationshipendpoint.this, "GET", REST_PATH, null, com.minook.zeppa.zeppausertouserrelationshipendpoint.model.CollectionResponseZeppaUserToUserRelationship.class);
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
    public ListZeppaUserToUserRelationship setAlt(java.lang.String alt) {
      return (ListZeppaUserToUserRelationship) super.setAlt(alt);
    }

    @Override
    public ListZeppaUserToUserRelationship setFields(java.lang.String fields) {
      return (ListZeppaUserToUserRelationship) super.setFields(fields);
    }

    @Override
    public ListZeppaUserToUserRelationship setKey(java.lang.String key) {
      return (ListZeppaUserToUserRelationship) super.setKey(key);
    }

    @Override
    public ListZeppaUserToUserRelationship setOauthToken(java.lang.String oauthToken) {
      return (ListZeppaUserToUserRelationship) super.setOauthToken(oauthToken);
    }

    @Override
    public ListZeppaUserToUserRelationship setPrettyPrint(java.lang.Boolean prettyPrint) {
      return (ListZeppaUserToUserRelationship) super.setPrettyPrint(prettyPrint);
    }

    @Override
    public ListZeppaUserToUserRelationship setQuotaUser(java.lang.String quotaUser) {
      return (ListZeppaUserToUserRelationship) super.setQuotaUser(quotaUser);
    }

    @Override
    public ListZeppaUserToUserRelationship setUserIp(java.lang.String userIp) {
      return (ListZeppaUserToUserRelationship) super.setUserIp(userIp);
    }

    @com.google.api.client.util.Key
    private java.lang.String cursor;

    /**

     */
    public java.lang.String getCursor() {
      return cursor;
    }

    public ListZeppaUserToUserRelationship setCursor(java.lang.String cursor) {
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

    public ListZeppaUserToUserRelationship setLimit(java.lang.Integer limit) {
      this.limit = limit;
      return this;
    }

    @Override
    public ListZeppaUserToUserRelationship set(String parameterName, Object value) {
      return (ListZeppaUserToUserRelationship) super.set(parameterName, value);
    }
  }

  /**
   * Create a request for the method "removeZeppaUserToUserRelationship".
   *
   * This request holds the parameters needed by the zeppausertouserrelationshipendpoint server.
   * After setting any optional parameters, call the {@link
   * RemoveZeppaUserToUserRelationship#execute()} method to invoke the remote operation.
   *
   * @param id
   * @return the request
   */
  public RemoveZeppaUserToUserRelationship removeZeppaUserToUserRelationship(java.lang.Long id) throws java.io.IOException {
    RemoveZeppaUserToUserRelationship result = new RemoveZeppaUserToUserRelationship(id);
    initialize(result);
    return result;
  }

  public class RemoveZeppaUserToUserRelationship extends ZeppausertouserrelationshipendpointRequest<Void> {

    private static final String REST_PATH = "zeppausertouserrelationship/{id}";

    /**
     * Create a request for the method "removeZeppaUserToUserRelationship".
     *
     * This request holds the parameters needed by the the zeppausertouserrelationshipendpoint server.
     * After setting any optional parameters, call the {@link
     * RemoveZeppaUserToUserRelationship#execute()} method to invoke the remote operation. <p> {@link
     * RemoveZeppaUserToUserRelationship#initialize(com.google.api.client.googleapis.services.Abstract
     * GoogleClientRequest)} must be called to initialize this instance immediately after invoking the
     * constructor. </p>
     *
     * @param id
     * @since 1.13
     */
    protected RemoveZeppaUserToUserRelationship(java.lang.Long id) {
      super(Zeppausertouserrelationshipendpoint.this, "DELETE", REST_PATH, null, Void.class);
      this.id = com.google.api.client.util.Preconditions.checkNotNull(id, "Required parameter id must be specified.");
    }

    @Override
    public RemoveZeppaUserToUserRelationship setAlt(java.lang.String alt) {
      return (RemoveZeppaUserToUserRelationship) super.setAlt(alt);
    }

    @Override
    public RemoveZeppaUserToUserRelationship setFields(java.lang.String fields) {
      return (RemoveZeppaUserToUserRelationship) super.setFields(fields);
    }

    @Override
    public RemoveZeppaUserToUserRelationship setKey(java.lang.String key) {
      return (RemoveZeppaUserToUserRelationship) super.setKey(key);
    }

    @Override
    public RemoveZeppaUserToUserRelationship setOauthToken(java.lang.String oauthToken) {
      return (RemoveZeppaUserToUserRelationship) super.setOauthToken(oauthToken);
    }

    @Override
    public RemoveZeppaUserToUserRelationship setPrettyPrint(java.lang.Boolean prettyPrint) {
      return (RemoveZeppaUserToUserRelationship) super.setPrettyPrint(prettyPrint);
    }

    @Override
    public RemoveZeppaUserToUserRelationship setQuotaUser(java.lang.String quotaUser) {
      return (RemoveZeppaUserToUserRelationship) super.setQuotaUser(quotaUser);
    }

    @Override
    public RemoveZeppaUserToUserRelationship setUserIp(java.lang.String userIp) {
      return (RemoveZeppaUserToUserRelationship) super.setUserIp(userIp);
    }

    @com.google.api.client.util.Key
    private java.lang.Long id;

    /**

     */
    public java.lang.Long getId() {
      return id;
    }

    public RemoveZeppaUserToUserRelationship setId(java.lang.Long id) {
      this.id = id;
      return this;
    }

    @Override
    public RemoveZeppaUserToUserRelationship set(String parameterName, Object value) {
      return (RemoveZeppaUserToUserRelationship) super.set(parameterName, value);
    }
  }

  /**
   * Create a request for the method "updateZeppaUserToUserRelationship".
   *
   * This request holds the parameters needed by the zeppausertouserrelationshipendpoint server.
   * After setting any optional parameters, call the {@link
   * UpdateZeppaUserToUserRelationship#execute()} method to invoke the remote operation.
   *
   * @param content the {@link com.minook.zeppa.zeppausertouserrelationshipendpoint.model.ZeppaUserToUserRelationship}
   * @return the request
   */
  public UpdateZeppaUserToUserRelationship updateZeppaUserToUserRelationship(com.minook.zeppa.zeppausertouserrelationshipendpoint.model.ZeppaUserToUserRelationship content) throws java.io.IOException {
    UpdateZeppaUserToUserRelationship result = new UpdateZeppaUserToUserRelationship(content);
    initialize(result);
    return result;
  }

  public class UpdateZeppaUserToUserRelationship extends ZeppausertouserrelationshipendpointRequest<com.minook.zeppa.zeppausertouserrelationshipendpoint.model.ZeppaUserToUserRelationship> {

    private static final String REST_PATH = "zeppausertouserrelationship";

    /**
     * Create a request for the method "updateZeppaUserToUserRelationship".
     *
     * This request holds the parameters needed by the the zeppausertouserrelationshipendpoint server.
     * After setting any optional parameters, call the {@link
     * UpdateZeppaUserToUserRelationship#execute()} method to invoke the remote operation. <p> {@link
     * UpdateZeppaUserToUserRelationship#initialize(com.google.api.client.googleapis.services.Abstract
     * GoogleClientRequest)} must be called to initialize this instance immediately after invoking the
     * constructor. </p>
     *
     * @param content the {@link com.minook.zeppa.zeppausertouserrelationshipendpoint.model.ZeppaUserToUserRelationship}
     * @since 1.13
     */
    protected UpdateZeppaUserToUserRelationship(com.minook.zeppa.zeppausertouserrelationshipendpoint.model.ZeppaUserToUserRelationship content) {
      super(Zeppausertouserrelationshipendpoint.this, "PUT", REST_PATH, content, com.minook.zeppa.zeppausertouserrelationshipendpoint.model.ZeppaUserToUserRelationship.class);
    }

    @Override
    public UpdateZeppaUserToUserRelationship setAlt(java.lang.String alt) {
      return (UpdateZeppaUserToUserRelationship) super.setAlt(alt);
    }

    @Override
    public UpdateZeppaUserToUserRelationship setFields(java.lang.String fields) {
      return (UpdateZeppaUserToUserRelationship) super.setFields(fields);
    }

    @Override
    public UpdateZeppaUserToUserRelationship setKey(java.lang.String key) {
      return (UpdateZeppaUserToUserRelationship) super.setKey(key);
    }

    @Override
    public UpdateZeppaUserToUserRelationship setOauthToken(java.lang.String oauthToken) {
      return (UpdateZeppaUserToUserRelationship) super.setOauthToken(oauthToken);
    }

    @Override
    public UpdateZeppaUserToUserRelationship setPrettyPrint(java.lang.Boolean prettyPrint) {
      return (UpdateZeppaUserToUserRelationship) super.setPrettyPrint(prettyPrint);
    }

    @Override
    public UpdateZeppaUserToUserRelationship setQuotaUser(java.lang.String quotaUser) {
      return (UpdateZeppaUserToUserRelationship) super.setQuotaUser(quotaUser);
    }

    @Override
    public UpdateZeppaUserToUserRelationship setUserIp(java.lang.String userIp) {
      return (UpdateZeppaUserToUserRelationship) super.setUserIp(userIp);
    }

    @Override
    public UpdateZeppaUserToUserRelationship set(String parameterName, Object value) {
      return (UpdateZeppaUserToUserRelationship) super.set(parameterName, value);
    }
  }

  /**
   * Builder for {@link Zeppausertouserrelationshipendpoint}.
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

    /** Builds a new instance of {@link Zeppausertouserrelationshipendpoint}. */
    @Override
    public Zeppausertouserrelationshipendpoint build() {
      return new Zeppausertouserrelationshipendpoint(this);
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
     * Set the {@link ZeppausertouserrelationshipendpointRequestInitializer}.
     *
     * @since 1.12
     */
    public Builder setZeppausertouserrelationshipendpointRequestInitializer(
        ZeppausertouserrelationshipendpointRequestInitializer zeppausertouserrelationshipendpointRequestInitializer) {
      return (Builder) super.setGoogleClientRequestInitializer(zeppausertouserrelationshipendpointRequestInitializer);
    }

    @Override
    public Builder setGoogleClientRequestInitializer(
        com.google.api.client.googleapis.services.GoogleClientRequestInitializer googleClientRequestInitializer) {
      return (Builder) super.setGoogleClientRequestInitializer(googleClientRequestInitializer);
    }
  }
}
