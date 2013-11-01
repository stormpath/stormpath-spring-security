/*
 * Copyright 2013 Stormpath, Inc.
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

package com.stormpath.spring.security.provider;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * Interface to be implemented as a factory for {@code Authentication} tokens.
 *
 * @see UsernamePasswordAuthenticationTokenFactory
 */

public interface AuthenticationTokenFactory {

    /**
     * Constructs and returns an <code>Authentication</code> token containing the given credentials.
     *
     * @param principal The identity of the principal. In the case of an authentication with username and
     *                  password, this would be the username.
     * @param credentials The credentials that prove the principal is correct. This is usually a password, but could be anything
     *                    relevant to the <code>AuthenticationManager</code>
     * @param authorities the authorities that the principal has been granted
     * @return a <code>Authentication</code> object including credentials
     *
     */
    public Authentication createAuthenticationToken(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities);

}
