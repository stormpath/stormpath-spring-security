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
package com.stormpath.spring.security.servlet.listener;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.idsite.AuthenticationResult;
import com.stormpath.sdk.idsite.IdSiteResultListener;
import com.stormpath.sdk.idsite.LogoutResult;
import com.stormpath.sdk.idsite.RegistrationResult;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.spring.security.authc.IdSiteAccountIDField;
import com.stormpath.spring.security.authc.IdSiteAuthenticationToken;
import com.stormpath.spring.security.provider.StormpathAuthenticationProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * This listener will be notified about successful ID Site activities such as registration, authentication and logout.
 * <p/>
 * In case this listener wants to be overriden, it must be exposed in the Spring Configuration with the name `idSiteResultListener`.
 * <p/>
 * For example, if using the XML configuration, you will need to edit <code>webapp/WEB-INF/spring/root-context.xml</code> and
 * add a bean like this:
 * <p/>
 * <pre>
 *      {@literal<}!-- Notifications about IdSite-related activity will be sent to this listener -->
 *      {@literal<}bean id="idSiteResultListener" class="com.mycompany.myspringsecurityapp.idsite.listener.IdSiteListener" {@literal/>}
 * </pre>
 *
 * @since 0.4.0
 */
public class IdSiteListener implements IdSiteResultListener {
    private static final Logger logger = LoggerFactory.getLogger(IdSiteListener.class);

    protected StormpathAuthenticationProvider authenticationProvider;

    //By default, the Account's email will be used as the principal ID for a logged in user. This can be modified
    //via {@ref #setIdSitePrincipalAccountIdField(String)}. Available options are restricted to Email and Username.
    //See: {@link IdSiteAccountIDField} Enum.
    private IdSiteAccountIDField idSitePrincipalAccountIdField = IdSiteAccountIDField.EMAIL;

    public IdSiteListener(StormpathAuthenticationProvider stormpathAuthenticationProvider) {
        this.authenticationProvider = stormpathAuthenticationProvider;
    }

    @Override
    public void onRegistered(RegistrationResult result) {
        logger.debug("Successful Id Site registration for account: " + result.getAccount().getEmail());
    }

    @Override
    public void onAuthenticated(AuthenticationResult result) {
        Account account = result.getAccount();
        Authentication authentication = authenticationProvider.authenticate(new IdSiteAuthenticationToken(getIdSitePrincipalValue(account), account));
        SecurityContextHolder.clearContext();
        SecurityContextHolder.getContext().setAuthentication(authentication);
        logger.debug("Successful Id Site authentication for account: " + account.getEmail());
    }

    @Override
    public void onLogout(LogoutResult result) {
        SecurityContextHolder.clearContext();
        logger.debug("Successful Id Site logout for account: " + result.getAccount().getEmail());
    }

    /**
     * Configures the field that will be set as the principal when creating the {@link org.springframework.security.core.Authentication authentication token}
     * after a successful ID Site login.
     * <p/>
     * When users login via ID Site, we do not have access to the actual login information. Thus, we do not know whether the
     * user logged in with his username or his email. Via this field, the developer can configure whether the principal information
     * will be either the {@link com.stormpath.spring.security.authc.IdSiteAccountIDField#USERNAME account username} or the
     * {@link com.stormpath.spring.security.authc.IdSiteAccountIDField#EMAIL account email}.
     * <p/>
     * By default, the account `email` is used.
     *
     * @param idField either `username` or `email` to express the desired principal to set when constructing the
     * {@link org.springframework.security.core.Authentication authentication token} after a successful ID Site login.
     *
     * @see com.stormpath.spring.security.authc.IdSiteAccountIDField
     */
    public void setIdSitePrincipalAccountIdField(String idField) {
        Assert.notNull(idField);
        this.idSitePrincipalAccountIdField = IdSiteAccountIDField.fromName(idField);
    }

    /**
     * Returns the account field that will be used as the principal for the {@link org.springframework.security.core.Authentication authentication token}
     * after a successful ID Site login.
     *
     * @return the account field that will be used as the principal for the {@link org.springframework.security.core.Authentication authentication token}
     * after a successful ID Site login.
     */
    public String getIdSitePrincipalAccountIdField() {
        return this.idSitePrincipalAccountIdField.toString();
    }

    protected String getIdSitePrincipalValue(Account account) {
        switch (this.idSitePrincipalAccountIdField) {
            case EMAIL:
                return account.getEmail();
            case USERNAME:
                return account.getUsername();
            default:
                throw new UnsupportedOperationException("Unrecognized idSitePrincipalAccountIdField value: " + this.idSitePrincipalAccountIdField);
        }
    }

}
