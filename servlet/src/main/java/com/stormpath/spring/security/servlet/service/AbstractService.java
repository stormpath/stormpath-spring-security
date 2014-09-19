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
package com.stormpath.spring.security.servlet.service;

import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.client.Client;
import com.stormpath.spring.security.provider.StormpathAuthenticationProvider;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Abstract class that should be overriden by any class that integrates this app with Stormpath's Backend. This
 * class provides easy access to the {@link com.stormpath.spring.security.provider.StormpathAuthenticationProvider
 * StormpathAuthenticationProvider}, the {@link com.stormpath.sdk.client.Client Stormpath Client instance} and the configured
 * Stormpath {@link com.stormpath.sdk.application.Application application} this Spring Security app interacts with.
 *
 * @since 0.3.0
 */
public class AbstractService {

    @Autowired
    private StormpathAuthenticationProvider authenticationProvider;

    private static Application application;

    /**
     * Returns the {@link com.stormpath.spring.security.provider.StormpathAuthenticationProvider Stormpath Spring Security
     * Plugin Application Provider}.
     *
     * @return the the {@link com.stormpath.spring.security.provider.StormpathAuthenticationProvider Stormpath Spring Security
     * Plugin Application Provider}.
     */
    protected StormpathAuthenticationProvider getAuthenticationProvider() {
        return authenticationProvider;
    }

    /**
     * Returns the {@link com.stormpath.sdk.client.Client Stormpath Client instance} this Spring Security App is using to
     * interact with the Stormpath Backend.
     *
     * @return the {@link com.stormpath.sdk.client.Client Stormpath Client instance} this Spring Security App is using to
     * interact with the Stormpath Backend.
     */
    protected Client getStormpathClient() {
        return getAuthenticationProvider().getClient();
    }

    /**
     * Returns the {@link com.stormpath.sdk.application.Application Stormpath Application instance} this Spring Security
     * App is configured to work with.
     * <p/>
     * The Application instance is static and thus shared among every sub-class of this class.
     *
     * @return the {@link com.stormpath.sdk.application.Application Stormpath Application instance} this Spring Security
     * App is configured to work with.
     */
    protected Application getStormpathApplication() {
        if (this.application == null) {
            this.application = getStormpathClient().getResource(getAuthenticationProvider().getApplicationRestUrl(), Application.class);
        }
        return this.application;
    }

}
