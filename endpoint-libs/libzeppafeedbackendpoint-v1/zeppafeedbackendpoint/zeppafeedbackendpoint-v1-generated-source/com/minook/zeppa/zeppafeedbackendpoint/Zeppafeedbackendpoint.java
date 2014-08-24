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
 * on 2014-08-20 at 15:31:32 UTC 
 * Modify at your own risk.
 */

package com.minook.zeppa.zeppafeedbackendpoint;

/**
 * Service definition for Zeppafeedbackendpoint (v1).
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
 * This service uses {@link ZeppafeedbackendpointRequestInitializer} to initialize global parameters via its
 * {@link Builder}.
 * </p>
 *
 * @since 1.3
 * @author Google, Inc.
 */
@SuppressWarnings("javadoc")
public class Zeppafeedbackendpoint extends com.google.api.client.googleapis.services.json.AbstractGoogleJsonClient {

  // Note: Leave this static initializer at the top of the file.
  static {
    com.google.api.client.util.Preconditions.checkState(
        com.google.api.client.googleapis.GoogleUtils.MAJOR_VERSION == 1 &&
        com.google.api.client.googleapis.GoogleUtils.MINOR_VERSION >= 15,
        "You are currently running with version %s of google-api-client. " +
        "You need at least version 1.15 of google-api-client to run version " +
        "1.18.0-rc of the zeppafeedbackendpoint library.", com.google.api.client.googleapis.GoogleUtils.VERSION);
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
  public static final String DEFAULT_SERVICE_PATH = "zeppafeedbackendpoint/v1/";

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
  public Zeppafeedbackendpoint(com.google.api.client.http.HttpTransport transport, com.google.api.client.json.JsonFactory jsonFactory,
      com.google.api.client.http.HttpRequestInitializer httpRequestInitializer) {
    this(new Builder(transport, jsonFactory, httpRequestInitializer));
  }

  /**
   * @param builder builder
   */
  Zeppafeedbackendpoint(Builder builder) {
    super(builder);
  }

  @Override
  protected void initialize(com.google.api.client.googleapis.services.AbstractGoogleClientRequest<?> httpClientRequest) throws java.io.IOException {
    super.initialize(httpClientRequest);
  }

  /**
   * Create a request for the method "getZeppaFeedback".
   *
   * This request holds the parameters needed by the zeppafeedbackendpoint server.  After setting any
   * optional parameters, call the {@link GetZeppaFeedback#execute()} method to invoke the remote
   * operation.
   *
   * @param id
   * @return the request
   */
  public GetZeppaFeedback getZeppaFeedback(java.lang.Long id) throws java.io.IOException {
    GetZeppaFeedback result = new GetZeppaFeedback(id);
    initialize(result);
    return result;
  }

  public class GetZeppaFeedback extends ZeppafeedbackendpointRequest<com.minook.zeppa.zeppafeedbackendpoint.model.ZeppaFeedback> {

    private static final String REST_PATH = "zeppafeedback/{id}";

    /**
     * Create a request for the method "getZeppaFeedback".
     *
     * This request holds the parameters needed by the the zeppafeedbackendpoint server.  After
     * setting any optional parameters, call the {@link GetZeppaFeedback#execute()} method to invoke
     * the remote operation. <p> {@link GetZeppaFeedback#initialize(com.google.api.client.googleapis.s
     * ervices.AbstractGoogleClientRequest)} must be called to initialize this instance immediately
     * after invoking the constructor. </p>
     *
     * @param id
     * @since 1.13
     */
    protected GetZeppaFeedback(java.lang.Long id) {
      super(Zeppafeedbackendpoint.this, "GET", REST_PATH, null, com.minook.zeppa.zeppafeedbackendpoint.model.ZeppaFeedback.class);
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
    public GetZeppaFeedback setAlt(java.lang.String alt) {
      return (GetZeppaFeedback) super.setAlt(alt);
    }

    @Override
    public GetZeppaFeedback setFields(java.lang.String fields) {
      return (GetZeppaFeedback) super.setFields(fields);
    }

    @Override
    public GetZeppaFeedback setKey(java.lang.String key) {
      return (GetZeppaFeedback) super.setKey(key);
    }

    @Override
    public GetZeppaFeedback setOauthToken(java.lang.String oauthToken) {
      return (GetZeppaFeedback) super.setOauthToken(oauthToken);
    }

    @Override
    public GetZeppaFeedback setPrettyPrint(java.lang.Boolean prettyPrint) {
      return (GetZeppaFeedback) super.setPrettyPrint(prettyPrint);
    }

    @Override
    public GetZeppaFeedback setQuotaUser(java.lang.String quotaUser) {
      return (GetZeppaFeedback) super.setQuotaUser(quotaUser);
    }

    @Override
    public GetZeppaFeedback setUserIp(java.lang.String userIp) {
      return (GetZeppaFeedback) super.setUserIp(userIp);
    }

    @com.google.api.client.util.Key
    private java.lang.Long id;

    /**

     */
    public java.lang.Long getId() {
      return id;
    }

    public GetZeppaFeedback setId(java.lang.Long id) {
      this.id = id;
      return this;
    }

    @Override
    public GetZeppaFeedback set(String parameterName, Object value) {
      return (GetZeppaFeedback) super.set(parameterName, value);
    }
  }

  /**
   * Create a request for the method "insertZeppaFeedback".
   *
   * This request holds the parameters needed by the zeppafeedbackendpoint server.  After setting any
   * optional parameters, call the {@link InsertZeppaFeedback#execute()} method to invoke the remote
   * operation.
   *
   * @param content the {@link com.minook.zeppa.zeppafeedbackendpoint.model.ZeppaFeedback}
   * @return the request
   */
  public InsertZeppaFeedback insertZeppaFeedback(com.minook.zeppa.zeppafeedbackendpoint.model.ZeppaFeedback content) throws java.io.IOException {
    InsertZeppaFeedback result = new InsertZeppaFeedback(content);
    initialize(result);
    return result;
  }

  public class InsertZeppaFeedback extends ZeppafeedbackendpointRequest<com.minook.zeppa.zeppafeedbackendpoint.model.ZeppaFeedback> {

    private static final String REST_PATH = "zeppafeedback";

    /**
     * Create a request for the method "insertZeppaFeedback".
     *
     * This request holds the parameters needed by the the zeppafeedbackendpoint server.  After
     * setting any optional parameters, call the {@link InsertZeppaFeedback#execute()} method to
     * invoke the remote operation. <p> {@link InsertZeppaFeedback#initialize(com.google.api.client.go
     * ogleapis.services.AbstractGoogleClientRequest)} must be called to initialize this instance
     * immediately after invoking the constructor. </p>
     *
     * @param content the {@link com.minook.zeppa.zeppafeedbackendpoint.model.ZeppaFeedback}
     * @since 1.13
     */
    protected InsertZeppaFeedback(com.minook.zeppa.zeppafeedbackendpoint.model.ZeppaFeedback content) {
      super(Zeppafeedbackendpoint.this, "POST", REST_PATH, content, com.minook.zeppa.zeppafeedbackendpoint.model.ZeppaFeedback.class);
    }

    @Override
    public InsertZeppaFeedback setAlt(java.lang.String alt) {
      return (InsertZeppaFeedback) super.setAlt(alt);
    }

    @Override
    public InsertZeppaFeedback setFields(java.lang.String fields) {
      return (InsertZeppaFeedback) super.setFields(fields);
    }

    @Override
    public InsertZeppaFeedback setKey(java.lang.String key) {
      return (InsertZeppaFeedback) super.setKey(key);
    }

    @Override
    public InsertZeppaFeedback setOauthToken(java.lang.String oauthToken) {
      return (InsertZeppaFeedback) super.setOauthToken(oauthToken);
    }

    @Override
    public InsertZeppaFeedback setPrettyPrint(java.lang.Boolean prettyPrint) {
      return (InsertZeppaFeedback) super.setPrettyPrint(prettyPrint);
    }

    @Override
    public InsertZeppaFeedback setQuotaUser(java.lang.String quotaUser) {
      return (InsertZeppaFeedback) super.setQuotaUser(quotaUser);
    }

    @Override
    public InsertZeppaFeedback setUserIp(java.lang.String userIp) {
      return (InsertZeppaFeedback) super.setUserIp(userIp);
    }

    @Override
    public InsertZeppaFeedback set(String parameterName, Object value) {
      return (InsertZeppaFeedback) super.set(parameterName, value);
    }
  }

  /**
   * Create a request for the method "listZeppaFeedback".
   *
   * This request holds the parameters needed by the zeppafeedbackendpoint server.  After setting any
   * optional parameters, call the {@link ListZeppaFeedback#execute()} method to invoke the remote
   * operation.
   *
   * @return the request
   */
  public ListZeppaFeedback listZeppaFeedback() throws java.io.IOException {
    ListZeppaFeedback result = new ListZeppaFeedback();
    initialize(result);
    return result;
  }

  public class ListZeppaFeedback extends ZeppafeedbackendpointRequest<com.minook.zeppa.zeppafeedbackendpoint.model.CollectionResponseZeppaFeedback> {

    private static final String REST_PATH = "zeppafeedback";

    /**
     * Create a request for the method "listZeppaFeedback".
     *
     * This request holds the parameters needed by the the zeppafeedbackendpoint server.  After
     * setting any optional parameters, call the {@link ListZeppaFeedback#execute()} method to invoke
     * the remote operation. <p> {@link ListZeppaFeedback#initialize(com.google.api.client.googleapis.
     * services.AbstractGoogleClientRequest)} must be called to initialize this instance immediately
     * after invoking the constructor. </p>
     *
     * @since 1.13
     */
    protected ListZeppaFeedback() {
      super(Zeppafeedbackendpoint.this, "GET", REST_PATH, null, com.minook.zeppa.zeppafeedbackendpoint.model.CollectionResponseZeppaFeedback.class);
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
    public ListZeppaFeedback setAlt(java.lang.String alt) {
      return (ListZeppaFeedback) super.setAlt(alt);
    }

    @Override
    public ListZeppaFeedback setFields(java.lang.String fields) {
      return (ListZeppaFeedback) super.setFields(fields);
    }

    @Override
    public ListZeppaFeedback setKey(java.lang.String key) {
      return (ListZeppaFeedback) super.setKey(key);
    }

    @Override
    public ListZeppaFeedback setOauthToken(java.lang.String oauthToken) {
      return (ListZeppaFeedback) super.setOauthToken(oauthToken);
    }

    @Override
    public ListZeppaFeedback setPrettyPrint(java.lang.Boolean prettyPrint) {
      return (ListZeppaFeedback) super.setPrettyPrint(prettyPrint);
    }

    @Override
    public ListZeppaFeedback setQuotaUser(java.lang.String quotaUser) {
      return (ListZeppaFeedback) super.setQuotaUser(quotaUser);
    }

    @Override
    public ListZeppaFeedback setUserIp(java.lang.String userIp) {
      return (ListZeppaFeedback) super.setUserIp(userIp);
    }

    @com.google.api.client.util.Key
    private java.lang.String cursor;

    /**

     */
    public java.lang.String getCursor() {
      return cursor;
    }

    public ListZeppaFeedback setCursor(java.lang.String cursor) {
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

    public ListZeppaFeedback setLimit(java.lang.Integer limit) {
      this.limit = limit;
      return this;
    }

    @Override
    public ListZeppaFeedback set(String parameterName, Object value) {
      return (ListZeppaFeedback) super.set(parameterName, value);
    }
  }

  /**
   * Create a request for the method "removeZeppaFeedback".
   *
   * This request holds the parameters needed by the zeppafeedbackendpoint server.  After setting any
   * optional parameters, call the {@link RemoveZeppaFeedback#execute()} method to invoke the remote
   * operation.
   *
   * @param id
   * @return the request
   */
  public RemoveZeppaFeedback removeZeppaFeedback(java.lang.Long id) throws java.io.IOException {
    RemoveZeppaFeedback result = new RemoveZeppaFeedback(id);
    initialize(result);
    return result;
  }

  public class RemoveZeppaFeedback extends ZeppafeedbackendpointRequest<Void> {

    private static final String REST_PATH = "zeppafeedback/{id}";

    /**
     * Create a request for the method "removeZeppaFeedback".
     *
     * This request holds the parameters needed by the the zeppafeedbackendpoint server.  After
     * setting any optional parameters, call the {@link RemoveZeppaFeedback#execute()} method to
     * invoke the remote operation. <p> {@link RemoveZeppaFeedback#initialize(com.google.api.client.go
     * ogleapis.services.AbstractGoogleClientRequest)} must be called to initialize this instance
     * immediately after invoking the constructor. </p>
     *
     * @param id
     * @since 1.13
     */
    protected RemoveZeppaFeedback(java.lang.Long id) {
      super(Zeppafeedbackendpoint.this, "DELETE", REST_PATH, null, Void.class);
      this.id = com.google.api.client.util.Preconditions.checkNotNull(id, "Required parameter id must be specified.");
    }

    @Override
    public RemoveZeppaFeedback setAlt(java.lang.String alt) {
      return (RemoveZeppaFeedback) super.setAlt(alt);
    }

    @Override
    public RemoveZeppaFeedback setFields(java.lang.String fields) {
      return (RemoveZeppaFeedback) super.setFields(fields);
    }

    @Override
    public RemoveZeppaFeedback setKey(java.lang.String key) {
      return (RemoveZeppaFeedback) super.setKey(key);
    }

    @Override
    public RemoveZeppaFeedback setOauthToken(java.lang.String oauthToken) {
      return (RemoveZeppaFeedback) super.setOauthToken(oauthToken);
    }

    @Override
    public RemoveZeppaFeedback setPrettyPrint(java.lang.Boolean prettyPrint) {
      return (RemoveZeppaFeedback) super.setPrettyPrint(prettyPrint);
    }

    @Override
    public RemoveZeppaFeedback setQuotaUser(java.lang.String quotaUser) {
      return (RemoveZeppaFeedback) super.setQuotaUser(quotaUser);
    }

    @Override
    public RemoveZeppaFeedback setUserIp(java.lang.String userIp) {
      return (RemoveZeppaFeedback) super.setUserIp(userIp);
    }

    @com.google.api.client.util.Key
    private java.lang.Long id;

    /**

     */
    public java.lang.Long getId() {
      return id;
    }

    public RemoveZeppaFeedback setId(java.lang.Long id) {
      this.id = id;
      return this;
    }

    @Override
    public RemoveZeppaFeedback set(String parameterName, Object value) {
      return (RemoveZeppaFeedback) super.set(parameterName, value);
    }
  }

  /**
   * Create a request for the method "updateZeppaFeedback".
   *
   * This request holds the parameters needed by the zeppafeedbackendpoint server.  After setting any
   * optional parameters, call the {@link UpdateZeppaFeedback#execute()} method to invoke the remote
   * operation.
   *
   * @param content the {@link com.minook.zeppa.zeppafeedbackendpoint.model.ZeppaFeedback}
   * @return the request
   */
  public UpdateZeppaFeedback updateZeppaFeedback(com.minook.zeppa.zeppafeedbackendpoint.model.ZeppaFeedback content) throws java.io.IOException {
    UpdateZeppaFeedback result = new UpdateZeppaFeedback(content);
    initialize(result);
    return result;
  }

  public class UpdateZeppaFeedback extends ZeppafeedbackendpointRequest<com.minook.zeppa.zeppafeedbackendpoint.model.ZeppaFeedback> {

    private static final String REST_PATH = "zeppafeedback";

    /**
     * Create a request for the method "updateZeppaFeedback".
     *
     * This request holds the parameters needed by the the zeppafeedbackendpoint server.  After
     * setting any optional parameters, call the {@link UpdateZeppaFeedback#execute()} method to
     * invoke the remote operation. <p> {@link UpdateZeppaFeedback#initialize(com.google.api.client.go
     * ogleapis.services.AbstractGoogleClientRequest)} must be called to initialize this instance
     * immediately after invoking the constructor. </p>
     *
     * @param content the {@link com.minook.zeppa.zeppafeedbackendpoint.model.ZeppaFeedback}
     * @since 1.13
     */
    protected UpdateZeppaFeedback(com.minook.zeppa.zeppafeedbackendpoint.model.ZeppaFeedback content) {
      super(Zeppafeedbackendpoint.this, "PUT", REST_PATH, content, com.minook.zeppa.zeppafeedbackendpoint.model.ZeppaFeedback.class);
    }

    @Override
    public UpdateZeppaFeedback setAlt(java.lang.String alt) {
      return (UpdateZeppaFeedback) super.setAlt(alt);
    }

    @Override
    public UpdateZeppaFeedback setFields(java.lang.String fields) {
      return (UpdateZeppaFeedback) super.setFields(fields);
    }

    @Override
    public UpdateZeppaFeedback setKey(java.lang.String key) {
      return (UpdateZeppaFeedback) super.setKey(key);
    }

    @Override
    public UpdateZeppaFeedback setOauthToken(java.lang.String oauthToken) {
      return (UpdateZeppaFeedback) super.setOauthToken(oauthToken);
    }

    @Override
    public UpdateZeppaFeedback setPrettyPrint(java.lang.Boolean prettyPrint) {
      return (UpdateZeppaFeedback) super.setPrettyPrint(prettyPrint);
    }

    @Override
    public UpdateZeppaFeedback setQuotaUser(java.lang.String quotaUser) {
      return (UpdateZeppaFeedback) super.setQuotaUser(quotaUser);
    }

    @Override
    public UpdateZeppaFeedback setUserIp(java.lang.String userIp) {
      return (UpdateZeppaFeedback) super.setUserIp(userIp);
    }

    @Override
    public UpdateZeppaFeedback set(String parameterName, Object value) {
      return (UpdateZeppaFeedback) super.set(parameterName, value);
    }
  }

  /**
   * Builder for {@link Zeppafeedbackendpoint}.
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

    /** Builds a new instance of {@link Zeppafeedbackendpoint}. */
    @Override
    public Zeppafeedbackendpoint build() {
      return new Zeppafeedbackendpoint(this);
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
     * Set the {@link ZeppafeedbackendpointRequestInitializer}.
     *
     * @since 1.12
     */
    public Builder setZeppafeedbackendpointRequestInitializer(
        ZeppafeedbackendpointRequestInitializer zeppafeedbackendpointRequestInitializer) {
      return (Builder) super.setGoogleClientRequestInitializer(zeppafeedbackendpointRequestInitializer);
    }

    @Override
    public Builder setGoogleClientRequestInitializer(
        com.google.api.client.googleapis.services.GoogleClientRequestInitializer googleClientRequestInitializer) {
      return (Builder) super.setGoogleClientRequestInitializer(googleClientRequestInitializer);
    }
  }
}
