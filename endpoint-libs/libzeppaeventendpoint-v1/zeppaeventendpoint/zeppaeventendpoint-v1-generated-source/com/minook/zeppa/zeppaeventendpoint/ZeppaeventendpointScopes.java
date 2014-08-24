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
 * Available OAuth 2.0 scopes for use with the zeppaeventendpoint.
 *
 * @since 1.4
 */
public class ZeppaeventendpointScopes {

  /** Manage your calendars. */
  public static final String CALENDAR = "https://www.googleapis.com/auth/calendar";

  /** Send your photos and videos to Google+. */
  public static final String PLUS_MEDIA_UPLOAD = "https://www.googleapis.com/auth/plus.media.upload";

  /** View your email address. */
  public static final String USERINFO_EMAIL = "https://www.googleapis.com/auth/userinfo.email";

  /** View basic information about your account. */
  public static final String USERINFO_PROFILE = "https://www.googleapis.com/auth/userinfo.profile";

  /**
   * Returns an unmodifiable set that contains all scopes declared by this class.
   *
   * @since 1.16
   */
  public static java.util.Set<String> all() {
    java.util.Set<String> set = new java.util.HashSet<String>();
    set.add(CALENDAR);
    set.add(PLUS_MEDIA_UPLOAD);
    set.add(USERINFO_EMAIL);
    set.add(USERINFO_PROFILE);
    return java.util.Collections.unmodifiableSet(set);
  }

  private ZeppaeventendpointScopes() {
  }
}
