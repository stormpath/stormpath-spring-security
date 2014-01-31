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

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.authc.AuthenticationRequest;
import com.stormpath.sdk.authc.UsernamePasswordRequest;
import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.group.Group;
import com.stormpath.sdk.group.GroupList;
import com.stormpath.sdk.resource.ResourceException;
import com.stormpath.spring.security.util.StringUtils;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * A {@code AuthenticationProvider} implementation that uses the <a href="http://www.stormpath.com">Stormpath</a> Cloud Identity
 * Management service for authentication and authorization operations for a single Application.
 * <p/>
 * The Stormpath-registered
 * <a href="https://www.stormpath.com/docs/libraries/application-rest-url">Application's Stormpath REST URL</a>
 * must be configured as the {@code applicationRestUrl} property.
 * <h3>Authentication</h3>
 * Once your application's REST URL is configured, this provider implementation automatically executes authentication
 * attempts without any need of further configuration by interacting with the Application's
 * <a href="http://www.stormpath.com/docs/rest/api#ApplicationLoginAttempts">loginAttempts endpoint</a>.
 * <h3>Authorization</h3>
 * Stormpath Accounts and Groups can be translated to Spring Security granted authorities via the following components.  You
 * can implement implementations of these interfaces and plug them into this provider for custom translation behavior:
 * <ul>
 * <li>{@link AccountGrantedAuthorityResolver AccountGrantedAuthorityResolver}</li>
 * <li>{@link GroupGrantedAuthorityResolver GroupGrantedAuthorityResolver}</li>
 * </ul>
 * <p/>
 * This provider implementation pre-configures the {@code groupGrantedAuthorityResolver} to be a {@link DefaultGroupGrantedAuthorityResolver}
 * instance (which can also be configured).  The other interface, if used, must be implemented as it is specific to your
 * application's data model.
 * <p/>
 * When the given credentials are successfully authenticated an {@link AuthenticationTokenFactory AuthenticationTokenFactory} instance
 * is used to create an authenticated token to be returned to the provider's client. By default, the {@link UsernamePasswordAuthenticationTokenFactory}
 * is used, constructing {@code UsernamePasswordAuthenticationToken} objects. It can be easily modify by creating a new
 * <code>AuthenticationTokenFactory</code> and setting it to this provider via {@link #setAuthenticationTokenFactory(AuthenticationTokenFactory)}.
 *
 * @see AccountGrantedAuthorityResolver
 * @see GroupGrantedAuthorityResolver
 * @see AuthenticationTokenFactory
 */
public class StormpathAuthenticationProvider implements AuthenticationProvider {

    private Client client;
    private String applicationRestUrl;
    private GroupGrantedAuthorityResolver groupGrantedAuthorityResolver;
    private AccountGrantedAuthorityResolver accountGrantedAuthorityResolver;
    private AuthenticationTokenFactory authenticationTokenFactory;

    private Application application; //acquired via the client at runtime, not configurable by the StormpathAuthenticationProvider user

    public StormpathAuthenticationProvider() {
        this.groupGrantedAuthorityResolver = new DefaultGroupGrantedAuthorityResolver();
        this.authenticationTokenFactory = new UsernamePasswordAuthenticationTokenFactory();
    }

    /**
     * Returns the {@code Client} instance used to communicate with Stormpath's REST API.
     *
     * @return the {@code Client} instance used to communicate with Stormpath's REST API.
     */
    public Client getClient() {
        return client;
    }

    /**
     * Sets the {@code Client} instance used to communicate with Stormpath's REST API.
     *
     * @param client the {@code Client} instance used to communicate with Stormpath's REST API.
     */
    public void setClient(Client client) {
        this.client = client;
    }

    /**
     * Returns the Stormpath REST URL of the specific application communicating with Stormpath.
     * <p/>
     * Any application supported by Stormpath will have a
     * <a href="http://www.stormpath.com/docs/quickstart/authenticate-account">dedicated unique REST URL</a>.  The
     * Stormpath REST URL of the Spring Security-enabled application communicating with Stormpath via this Provider must be
     * configured by this property.
     *
     * @return the Stormpath REST URL of the specific application communicating with Stormpath.
     */
    public String getApplicationRestUrl() {
        return applicationRestUrl;
    }

    /**
     * Sets the Stormpath REST URL of the specific application communicating with Stormpath.
     * <p/>
     * Any application supported by Stormpath will have a
     * <a href="http://www.stormpath.com/docs/quickstart/authenticate-account">dedicated unique REST URL</a>.  The
     * Stormpath REST URL of the Spring Security-enabled application communicating with Stormpath via this Provider must be
     * configured by this property.
     *
     * @param applicationRestUrl the Stormpath REST URL of the specific application communicating with Stormpath.
     */
    public void setApplicationRestUrl(String applicationRestUrl) {
        this.applicationRestUrl = applicationRestUrl;
    }

    /**
     * Returns the {@link GroupGrantedAuthorityResolver} used to translate Stormpath Groups into Spring Security granted authorities.
     * Unless overridden via {@link #setGroupGrantedAuthorityResolver(GroupGrantedAuthorityResolver) setGroupGrantedAuthorityResolver},
     * the default instance is a {@link DefaultGroupGrantedAuthorityResolver}.
     *
     * @return the {@link GroupGrantedAuthorityResolver} used to translate Stormpath Groups into Spring Security granted authorities.
     */
    public GroupGrantedAuthorityResolver getGroupGrantedAuthorityResolver() {
        return groupGrantedAuthorityResolver;
    }

    /**
     * Sets the {@link GroupGrantedAuthorityResolver} used to translate Stormpath Groups into Spring Security granted authorities.
     * Unless overridden, the default instance is a {@link DefaultGroupGrantedAuthorityResolver}.
     *
     * @param groupGrantedAuthorityResolver the {@link GroupGrantedAuthorityResolver} used to translate Stormpath Groups into
     *                                      Spring Security granted authorities.
     */
    public void setGroupGrantedAuthorityResolver(GroupGrantedAuthorityResolver groupGrantedAuthorityResolver) {
        this.groupGrantedAuthorityResolver = groupGrantedAuthorityResolver;
    }

    /**
     * Returns the {@link AccountGrantedAuthorityResolver} used to discover a Stormpath Account's assigned permissions.  This
     * is {@code null} by default and must be configured based on your application's needs.
     *
     * @return the {@link AccountGrantedAuthorityResolver} used to discover a Stormpath Account's assigned permissions.
     */
    public AccountGrantedAuthorityResolver getAccountGrantedAuthorityResolver() {
        return accountGrantedAuthorityResolver;
    }

    /**
     * Sets the {@link AccountGrantedAuthorityResolver} used to discover a Stormpath Account's assigned permissions.  This
     * is {@code null} by default and must be configured based on your application's needs.
     *
     * @param accountGrantedAuthorityResolver the {@link AccountGrantedAuthorityResolver} used to discover a Stormpath Account's
     *                                        assigned permissions
     */
    public void setAccountGrantedAuthorityResolver(AccountGrantedAuthorityResolver accountGrantedAuthorityResolver) {
        this.accountGrantedAuthorityResolver = accountGrantedAuthorityResolver;
    }

    /**
     *
     * Returns the {@link AccountGrantedAuthorityResolver} used to discover a Stormpath Account's assigned permissions.  This
     * is {@code null} by default and must be configured based on your application's needs. Unless overridden, the default instance
     * is a {@link UsernamePasswordAuthenticationTokenFactory}.
     *
     * @return the token factory to be used when creating tokens for the successfully authenticated credentials.
     */
    public AuthenticationTokenFactory getAuthenticationTokenFactory() {
        return authenticationTokenFactory;
    }

    /**
     * Sets the {@link AuthenticationTokenFactory} used to create authenticated tokens. Unless overridden via
     * {@link #setAuthenticationTokenFactory(AuthenticationTokenFactory)} setAuthenticationTokenFactory},
     * the default instance is a {@link UsernamePasswordAuthenticationTokenFactory}.
     *
     * @param authenticationTokenFactory the token factory to be used when creating tokens for the successfully
     *                                   authenticated credentials.
     */
    public void setAuthenticationTokenFactory(AuthenticationTokenFactory authenticationTokenFactory) {
        if (authenticationTokenFactory == null) {
            throw new IllegalArgumentException("authenticationTokenFactory cannot be null.");
        }
        this.authenticationTokenFactory = authenticationTokenFactory;
    }

    private void assertState() {
        if (this.client == null) {
            throw new IllegalStateException("Stormpath SDK Client instance must be configured.");
        }
        if (this.applicationRestUrl == null) {
            throw new IllegalStateException("\n\nThis application's Stormpath REST URL must be configured.\n\n  " +
                    "You may get your application's Stormpath REST URL as shown here:\n\n " +
                    "http://www.stormpath.com/docs/application-rest-url\n\n" +
                    "Copy and paste the 'REST URL' value as the 'applicationRestUrl' property of this class.");
        }
    }

    /**
     * Performs actual authentication for the received authentication credentials using
     * <a href="http://www.stormpath.com">Stormpath</a> Cloud Identity Management service for a single application.
     *
     * @param authentication the authentication request object.
     *
     * @return a fully authenticated object including credentials.
     *
     * @throws AuthenticationException if authentication fails.
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        assertState();
        AuthenticationRequest request = createAuthenticationRequest(authentication);
        Application application = ensureApplicationReference();

        Account account;

        try {
            account = application.authenticateAccount(request).getAccount();
        } catch (ResourceException e) {
            String msg = StringUtils.clean(e.getMessage());
            if (msg == null) {
                msg = StringUtils.clean(e.getDeveloperMessage());
            }
            if (msg == null) {
                msg = "Invalid login or password.";
            }
            throw new AuthenticationServiceException(msg, e);
        } finally {
            //Clear the request data to prevent later memory access
            request.clear();
        }

//        Authentication authToken = this.authenticationTokenFactory.createAuthenticationToken(
//                authentication.getPrincipal(), authentication.getCredentials(), getGrantedAuthorities(account), account);

        Authentication authToken = this.authenticationTokenFactory.createAuthenticationToken(
                authentication.getPrincipal(), authentication.getCredentials(), getGrantedAuthorities(account), account);

        return authToken;
    }

    /**
     * Returns <code>true</code> if this <Code>AuthenticationProvider</code> supports the indicated
     * <Code>Authentication</code> object.
     *
     * @param authentication the class to validate this <Code>AuthenticationProvider</code> supports
     *
     * @return <code>true</code> if the given class is supported
     */
    @Override
    public boolean supports(Class<?> authentication) {
        return Authentication.class.isAssignableFrom(authentication);
    }

    //This is not thread safe, but the Client is, and this is only executed during initial Application
    //acquisition, so it is negligible if this executes a few times instead of just once.
    protected final Application ensureApplicationReference() {
        if (this.application == null) {
            String href = getApplicationRestUrl();
            this.application = client.getDataStore().getResource(href, Application.class);
        }
        return this.application;
    }

    protected AuthenticationRequest createAuthenticationRequest(Authentication authentication) {
        String username = (String) authentication.getPrincipal();
        String password = (String) authentication.getCredentials();
        return new UsernamePasswordRequest(username, password);
    }

    protected Collection<GrantedAuthority> getGrantedAuthorities(Account account) {

        GroupList groups = account.getGroups();

        if (groups == null) {
            return null;
        }

        Collection<GrantedAuthority> grantedAuthorities = new HashSet<GrantedAuthority>();

        for (Group group : groups) {
            Set<GrantedAuthority> groupRoles = resolveGrantedAuthorities(group);
            grantedAuthorities.addAll(groupRoles);
        }

        Set<GrantedAuthority> accountRoles = resolveGrantedAuthorities(account);
        grantedAuthorities.addAll(accountRoles);

        return grantedAuthorities;
    }

    private Set<GrantedAuthority> resolveGrantedAuthorities(Group group) {
        if (groupGrantedAuthorityResolver != null) {
            return groupGrantedAuthorityResolver.resolveGrantedAuthorities(group);
        }
        return Collections.emptySet();
    }

    private Set<GrantedAuthority> resolveGrantedAuthorities(Account account) {
        if (accountGrantedAuthorityResolver != null) {
            return accountGrantedAuthorityResolver.resolveGrantedAuthorities(account);
        }
        return Collections.emptySet();
    }

}
