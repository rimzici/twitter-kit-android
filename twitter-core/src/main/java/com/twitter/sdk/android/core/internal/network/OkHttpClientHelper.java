/*
 * Copyright (C) 2015 Twitter, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.twitter.sdk.android.core.internal.network;

import com.twitter.sdk.android.core.GuestSessionProvider;
import com.twitter.sdk.android.core.Session;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterAuthToken;

import okhttp3.OkHttpClient;

public class OkHttpClientHelper {
    public static OkHttpClient getOkHttpClient(GuestSessionProvider guestSessionProvider) {
        return addGuestAuth(new OkHttpClient.Builder(), guestSessionProvider).build();
    }

    public static OkHttpClient getOkHttpClient(Session<? extends TwitterAuthToken> session,
            TwitterAuthConfig authConfig) {
        if (session == null) {
            throw new IllegalArgumentException("Session must not be null.");
        }

        return addSessionAuth(new OkHttpClient.Builder(), session, authConfig).build();
    }

    public static OkHttpClient getCustomOkHttpClient(OkHttpClient httpClient,
            GuestSessionProvider guestSessionProvider) {
        if (httpClient == null) {
            throw new IllegalArgumentException("HttpClient must not be null.");
        }

        return addGuestAuth(httpClient.newBuilder(), guestSessionProvider)
                .build();
    }

    public static OkHttpClient getCustomOkHttpClient(
            OkHttpClient httpClient,
            Session<? extends TwitterAuthToken> session,
            TwitterAuthConfig authConfig) {
        if (session == null) {
            throw new IllegalArgumentException("Session must not be null.");
        }

        if (httpClient == null) {
            throw new IllegalArgumentException("HttpClient must not be null.");
        }

        return addSessionAuth(httpClient.newBuilder(), session, authConfig)
                .build();
    }

    static OkHttpClient.Builder addGuestAuth(OkHttpClient.Builder builder,
                                             GuestSessionProvider guestSessionProvider) {
        return builder
                .authenticator(new GuestAuthenticator(guestSessionProvider))
                .addInterceptor(new GuestAuthInterceptor(guestSessionProvider))
                .addNetworkInterceptor(new GuestAuthNetworkInterceptor());
    }

    static OkHttpClient.Builder addSessionAuth(OkHttpClient.Builder builder,
                                               Session<? extends TwitterAuthToken> session,
                                               TwitterAuthConfig authConfig) {
        return builder
                .addInterceptor(new OAuth1aInterceptor(session, authConfig));
    }
}
