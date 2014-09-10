/*
 * Copyright 2014 Stormpath, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.stormpath.spring.security.authc;

import com.stormpath.sdk.http.HttpRequest;
import com.stormpath.sdk.idsite.IdSiteResultListener;
import com.stormpath.sdk.lang.Assert;
import org.springframework.security.authentication.AbstractAuthenticationToken;

import java.util.Collections;

/**
 * This {@link org.springframework.security.core.Authentication Authentication} sub-class is specific to ID Site. It is
 * used to hold the required information to allow a Spring Security application to login via its ID Site portal.
 * <p/>
 * ID Site does not actually require any login information before it gets displayed. However we are wrapping the ID Site information
 * ({@link com.stormpath.sdk.idsite.IdSiteResultListener IdSiteResultListener} and {@link HttpRequest})
 * in the {@link org.springframework.security.core.Authentication Authentication} interface in order to allow our
 * {@link com.stormpath.spring.security.provider.StormpathAuthenticationProvider StormpathAuthenticationProvider} to seamlessly provide an authentication mechanism
 * that can homogeneously receive both form-based or ID Site authentication requests.
 *
 * @since 0.4.0
 */
public class IdSiteAuthenticationToken extends AbstractAuthenticationToken {

    private final HttpRequest request;
    private final IdSiteResultListener idSiteResultListener;

    public IdSiteAuthenticationToken(HttpRequest request, IdSiteResultListener listener) {
        super(Collections.EMPTY_LIST);
        Assert.notNull(request);
        this.request = request;
        this.idSiteResultListener = listener;
    }

    /**
     * Returns the {@link com.stormpath.sdk.idsite.IdSiteResultListener IdSiteResultListener} that will be
     * notified after the ID Site action has been executed: login, registration or logout.
     *
     * @return the {@link com.stormpath.sdk.idsite.IdSiteResultListener IdSiteResultListener} that will be
     * notified after the ID Site action has been executed: login, registration or logout.
     */
    @Override
    public Object getPrincipal() {
        return this.idSiteResultListener;
    }

    /**
     * Returns the {@link com.stormpath.sdk.http.HttpRequest HttpRequest} that will be used to display the actual ID Site screen.
     *
     * @return the {@link com.stormpath.sdk.http.HttpRequest HttpRequest} that will be used to display the actual ID Site screen.
     *
     */
    @Override
    public Object getCredentials() {
        return this.request;
    }

}
